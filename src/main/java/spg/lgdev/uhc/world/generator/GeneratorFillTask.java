package spg.lgdev.uhc.world.generator;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.api.events.GeneratorTaskCompleteEvent;
import spg.lgdev.uhc.world.CoordXZ;


public class GeneratorFillTask implements Runnable
{
	private transient Server server = null;
	private transient World world = null;
	private transient GeneratorData border = null;
	private transient GeneratorFileData worldData = null;
	private transient boolean readyToGo = false;
	private transient boolean paused = false;
	private transient boolean pausedForMemory = false;
	private transient int taskID = -1;
	private transient Player notifyPlayer = null;
	private transient int chunksPerRun = 1;
	private transient boolean continueNotice = false;
	private transient boolean forceLoad = false;

	private transient int fillDistance = 208;
	private transient int tickFrequency = 1;
	private transient int refX = 0, lastLegX = 0;
	private transient int refZ = 0, lastLegZ = 0;
	private transient int refLength = -1;
	private transient int refTotal = 0, lastLegTotal = 0;

	private transient int x = 0;
	private transient int z = 0;
	private transient boolean isZLeg = false;
	private transient boolean isNeg = false;
	private transient int length = -1;
	private transient int current = 0;
	private transient boolean insideBorder = true;
	private final List<CoordXZ> storedChunks = new LinkedList<>();
	private final Set<CoordXZ> originalChunks = new HashSet<>();
	private transient CoordXZ lastChunk = new CoordXZ(0, 0);

	private transient long lastReport = GeneratorManager.Now();
	private transient long lastAutosave = GeneratorManager.Now();
	private transient int reportTarget = 0;
	private transient int reportTotal = 0;
	private transient int reportNum = 0;


	public GeneratorFillTask(final Server theServer, final Player player, final String worldName, final int fillDistance, final int chunksPerRun, final int tickFrequency, final boolean forceLoad)
	{
		this.server = theServer;
		this.notifyPlayer = player;
		this.fillDistance = fillDistance;
		this.tickFrequency = tickFrequency;
		this.chunksPerRun = chunksPerRun;
		this.forceLoad = forceLoad;

		this.world = server.getWorld(worldName);
		if (this.world == null)
		{
			if (worldName.isEmpty()) {
				sendMessage("You must specify a world!");
			} else {
				sendMessage("World \"" + worldName + "\" not found!");
			}
			this.stop();
			return;
		}

		this.border = (GeneratorManager.Border(worldName) == null) ? null : ((GeneratorData)GeneratorManager.Border(worldName)).copy();
		if (this.border == null)
		{
			sendMessage("No border found for world \"" + worldName + "\"!");
			this.stop();
			return;
		}

		worldData = GeneratorFileData.create(world, notifyPlayer);
		if (worldData == null)
		{
			this.stop();
			return;
		}

		this.border.setRadiusX(border.getRadiusX() + fillDistance);
		this.border.setRadiusZ(border.getRadiusZ() + fillDistance);
		this.x = CoordXZ.blockToChunk((int) border.getX());
		this.z = CoordXZ.blockToChunk((int) border.getZ());

		final int chunkWidthX = (int) Math.ceil((double)((border.getRadiusX() + 16) * 2) / 16);
		final int chunkWidthZ = (int) Math.ceil((double)((border.getRadiusZ() + 16) * 2) / 16);
		final int biggerWidth = (chunkWidthX > chunkWidthZ) ? chunkWidthX : chunkWidthZ;
		this.reportTarget = (biggerWidth * biggerWidth) + biggerWidth + 1;

		final Chunk[] originals = world.getLoadedChunks();
		for (final Chunk original : originals)
		{
			originalChunks.add(new CoordXZ(original.getX(), original.getZ()));
		}

		this.readyToGo = true;
	}

	public GeneratorFillTask(final Server theServer, final Player player, final String worldName, final int fillDistance, final int chunksPerRun, final int tickFrequency)
	{
		this(theServer, player, worldName, fillDistance, chunksPerRun, tickFrequency, false);
	}

	public void setTaskID(final int ID)
	{
		if (ID == -1) {
			this.stop();
		}
		this.taskID = ID;
	}


	@Override
	public void run()
	{
		if (continueNotice)
		{
			continueNotice = false;
			sendMessage("World map generation task automatically continuing.");
			sendMessage("Reminder: you can cancel at any time with \"wb fill cancel\", or pause/unpause with \"wb fill pause\".");
		}

		if (pausedForMemory)
		{
			if (GeneratorManager.AvailableMemoryTooLow())
				return;

			pausedForMemory = false;
			readyToGo = true;
			sendMessage("Available memory is sufficient, automatically continuing.");
		}

		if (server == null || !readyToGo || paused)
			return;

		readyToGo = false;
		final long loopStartTime = GeneratorManager.Now();

		for (int loop = 0; loop < chunksPerRun; loop++)
		{

			if (paused || pausedForMemory)
				return;

			final long now = GeneratorManager.Now();

			if (now > lastReport + 5000) {
				reportProgress();
			}

			if (now > loopStartTime + 45)
			{
				readyToGo = true;
				return;
			}

			while (!border.insideBorder(CoordXZ.chunkToBlock(x) + 8, CoordXZ.chunkToBlock(z) + 8))
			{
				if (!moveToNext())
					return;
			}
			insideBorder = true;

			if (!forceLoad)
			{
				while (worldData.isChunkFullyGenerated(x, z))
				{
					insideBorder = true;
					if (!moveToNext())
						return;
				}
			}

			world.loadChunk(x, z, true);
			worldData.chunkExistsNow(x, z);

			final int popX = !isZLeg ? x : (x + (isNeg ? -1 : 1));
			final int popZ = isZLeg ? z : (z + (!isNeg ? -1 : 1));
			world.loadChunk(popX, popZ, false);

			if (!storedChunks.contains(lastChunk) && !originalChunks.contains(lastChunk))
			{
				world.loadChunk(lastChunk.x, lastChunk.z, false);
				storedChunks.add(new CoordXZ(lastChunk.x, lastChunk.z));
			}

			storedChunks.add(new CoordXZ(popX, popZ));
			storedChunks.add(new CoordXZ(x, z));

			while (storedChunks.size() > 8)
			{
				final CoordXZ coord = storedChunks.remove(0);
				if (!originalChunks.contains(coord)) {
					world.unloadChunkRequest(coord.x, coord.z);
				}
			}

			if (!moveToNext())
				return;
		}

		readyToGo = true;
	}

	public boolean moveToNext()
	{
		if (paused || pausedForMemory)
			return false;

		reportNum++;

		if (!isNeg && current == 0 && length > 3)
		{
			if (!isZLeg)
			{
				lastLegX = x;
				lastLegZ = z;
				lastLegTotal = reportTotal + reportNum;
			} else {
				refX = lastLegX;
				refZ = lastLegZ;
				refTotal = lastLegTotal;
				refLength = length - 1;
			}
		}

		if (current < length) {
			current++;
		} else
		{
			current = 0;
			isZLeg ^= true;
			if (isZLeg)
			{
				isNeg ^= true;
				length++;
			}
		}

		lastChunk.x = x;
		lastChunk.z = z;

		if (isZLeg) {
			z += (isNeg) ? -1 : 1;
		} else {
			x += (isNeg) ? -1 : 1;
		}

		if (isZLeg && isNeg && current == 0)
		{
			if (!insideBorder)
			{
				finish();
				return false;
			}
			else {
				insideBorder = false;
			}
		}
		return true;

	}

	public void finish()
	{
		this.paused = true;
		reportProgress();
		world.save();
		sendMessage("task successfully completed!");
		this.stop();

		Bukkit.getServer().getPluginManager().callEvent(new GeneratorTaskCompleteEvent(this.world.getName(), this.border));

	}

	public void cancel()
	{
		this.stop();
	}

	private void stop()
	{
		if (server == null)
			return;

		readyToGo = false;
		if (taskID != -1) {
			server.getScheduler().cancelTask(taskID);
		}
		server = null;

		while (!storedChunks.isEmpty())
		{
			final CoordXZ coord = storedChunks.remove(0);
			if (!originalChunks.contains(coord)) {
				world.unloadChunkRequest(coord.x, coord.z);
			}
		}
	}

	public boolean valid()
	{
		return this.server != null;
	}

	public void pause()
	{
		if (this.pausedForMemory) {
			pause(false);
		} else {
			pause(!this.paused);
		}
	}
	public void pause(final boolean pause)
	{
		if (this.pausedForMemory && !pause) {
			this.pausedForMemory = false;
		} else {
			this.paused = pause;
		}
		if (this.paused)
		{
			GeneratorManager.StoreFillTask();
			reportProgress();
		} else {
			GeneratorManager.UnStoreFillTask();
		}
	}
	public boolean isPaused()
	{
		return this.paused || this.pausedForMemory;
	}

	private void reportProgress()
	{
		lastReport = GeneratorManager.Now();
		double perc = ((double)(reportTotal + reportNum) / (double)reportTarget) * 100;
		if (perc > 100) {
			perc = 100;
		}
		sendMessage(reportNum + " more chunks processed (" + (reportTotal + reportNum) + " total, ~" + GeneratorManager.coord.format(perc) + "%" + ")");
		reportTotal += reportNum;
		reportNum = 0;

		if (lastAutosave + 30000 < lastReport)
		{
			lastAutosave = lastReport;
			sendMessage("Saving the world to disk, just to be on the safe side.");
			world.save();
		}
	}

	private void sendMessage(String text)
	{
		final int availMem = GeneratorManager.AvailableMemory();

		GeneratorManager.log("[Fill] " + text + " (free mem: " + availMem + " MB)");
		if (notifyPlayer != null) {
			notifyPlayer.sendMessage("[Fill] " + text);
		}

		if (availMem < 200)
		{
			pausedForMemory = true;
			GeneratorManager.StoreFillTask();
			text = "Available memory is very low, task is pausing. A cleanup will be attempted now, and the task will automatically continue if/when sufficient memory is freed up.\n Alternatively, if you restart the server, this task will automatically continue once the server is back up.";
			GeneratorManager.log("[Fill] " + text);
			if (notifyPlayer != null) {
				notifyPlayer.sendMessage("[Fill] " + text);
			}
			System.gc();
		}
	}

	public void continueProgress(final int x, final int z, final int length, final int totalDone)
	{
		this.x = x;
		this.z = z;
		this.length = length;
		this.reportTotal = totalDone;
		this.continueNotice = true;
	}
	public int refX()
	{
		return refX;
	}
	public int refZ()
	{
		return refZ;
	}
	public int refLength()
	{
		return refLength;
	}
	public int refTotal()
	{
		return refTotal;
	}
	public int refFillDistance()
	{
		return fillDistance;
	}
	public int refTickFrequency()
	{
		return tickFrequency;
	}
	public int refChunksPerRun()
	{
		return chunksPerRun;
	}
	public String refWorld()
	{
		return world.getName();
	}
	public boolean refForceLoad()
	{
		return forceLoad;
	}

	public double getPercentageCompleted() {
		return ((double) (reportTotal + reportNum) / (double) reportTarget) * 100;
	}

	public int getChunksCompleted() {
		return reportTotal;
	}

	public int getChunksTotal() {
		return reportTarget;
	}
}
