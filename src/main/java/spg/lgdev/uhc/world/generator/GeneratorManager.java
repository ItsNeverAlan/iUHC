package spg.lgdev.uhc.world.generator;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import spg.lgdev.uhc.iUHC;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;


public class GeneratorManager
{
	// private stuff used within this class
	private static iUHC plugin = iUHC.getInstance();
	private static Logger wbLog = plugin.getLogger();
	public static DecimalFormat coord = new DecimalFormat("0.0");
	private static int borderTask = -1;
	public static GeneratorFillTask fillTask;
	private static Runtime rt = Runtime.getRuntime();

	private static Map<String, GeneratorData> borders = Collections.synchronizedMap(new LinkedHashMap<String, GeneratorData>());
	private static Set<String> bypassPlayers = Collections.synchronizedSet(new LinkedHashSet<String>());
	private static String message;		// raw message without color code formatting
	private static String messageFmt;	// message with color code formatting ("&" changed to funky sort-of-double-dollar-sign for legitimate color/formatting codes)
	private static String messageClean;	// message cleaned of formatting codes
	private static boolean DEBUG = false;
	private static double knockBack = 3.0;
	private static int timerTicks = 4;
	private static boolean whooshEffect = false;
	private static boolean portalRedirection = true;
	private static boolean dynmapEnable = true;
	private static String dynmapMessage;
	private static int remountDelayTicks = 0;
	private static boolean killPlayer = false;
	private static boolean denyEnderpearl = false;
	private static int fillAutosaveFrequency = 30;
	private static int fillMemoryTolerance = 500;

	public static long Now()
	{
		return System.currentTimeMillis();
	}


	public static void setBorder(final String world, final GeneratorData border)
	{
		borders.put(world, border);
		log("Border set. " + BorderDescription(world));
		//doNotsave(true);
	}

	public static void setBorder(final String world, final int radiusX, final int radiusZ, final double x, final double z)
	{
		final GeneratorData old = (GeneratorData)Border(world);
		final boolean oldWrap = (old != null) && old.getWrapping();
		setBorder(world, new GeneratorData(x, z, radiusX, radiusZ, oldWrap));
	}


	// backwards-compatible methods from before elliptical/rectangular shapes were supported
	public static void setBorder(final String world, final int radius, final double x, final double z, final Boolean shapeRound)
	{
		setBorder(world, new GeneratorData(x, z, radius, radius, shapeRound));
	}
	public static void setBorder(final String world, final int radius, final double x, final double z)
	{
		setBorder(world, radius, radius, x, z);
	}


	// set border based on corner coordinates
	public static void setBorderCorners(final String world, final double x1, final double z1, final double x2, final double z2, final boolean wrap)
	{
		final double radiusX = Math.abs(x1 - x2) / 2;
		final double radiusZ = Math.abs(z1 - z2) / 2;
		final double x = ((x1 < x2) ? x1 : x2) + radiusX;
		final double z = ((z1 < z2) ? z1 : z2) + radiusZ;
		setBorder(world, new GeneratorData(x, z, (int)Math.round(radiusX), (int)Math.round(radiusZ), wrap));
	}
	public static void setBorderCorners(final String world, final double x1, final double z1, final double x2, final double z2)
	{
		final GeneratorData old = (GeneratorData)Border(world);
		final boolean oldWrap = (old != null) && old.getWrapping();
		setBorderCorners(world, x1, z1, x2, z2, oldWrap);
	}


	public static void removeBorder(final String world)
	{
		borders.remove(world);
		log("Removed border for world \"" + world + "\".");
		//doNotsave(true);
	}

	public static void removeAllBorders()
	{
		borders.clear();
		log("Removed all borders for all worlds.");
		//doNotsave(true);
	}

	public static String BorderDescription(final String world)
	{
		final GeneratorData border = borders.get(world);
		if (border == null)
			return "No border was found for the world \"" + world + "\".";
		else
			return "World \"" + world + "\" has border " + border.toString();
	}

	public static Set<String> BorderDescriptions()
	{
		final Set<String> output = new HashSet<>();

		for (final String worldName : borders.keySet())
		{
			output.add(BorderDescription(worldName));
		}

		return output;
	}

	public static Object Border(final String world)
	{
		return borders.get(world);
	}

	public static Map<String, GeneratorData> getBorders()
	{
		return new LinkedHashMap<>(borders);
	}

	public static void setMessage(final String msg)
	{
		updateMessage(msg);
		log("Border message is now set to: " + MessageRaw());
		//doNotsave(true);
	}

	public static void updateMessage(final String msg)
	{
		message = msg;
		messageFmt = replaceAmpColors(msg);
		messageClean = stripAmpColors(msg);
	}

	public static String Message()
	{
		return messageFmt;
	}
	public static String MessageRaw()
	{
		return message;
	}
	public static String MessageClean()
	{
		return messageClean;
	}

	public static String ShapeName(final boolean round)
	{
		return round ? "elliptic/round" : "rectangular/square";
	}

	public static void setDebug(final boolean debugMode)
	{
		DEBUG = debugMode;
		log("Debug mode " + (DEBUG ? "enabled" : "disabled") + ".");
		//doNotsave(true);

	}

	public static boolean Debug()
	{
		return DEBUG;
	}

	public static void setWhooshEffect(final boolean enable)
	{
		whooshEffect = enable;
		log("\"Whoosh\" knockback effect " + (enable ? "enabled" : "disabled") + ".");
		//doNotsave(true);
	}

	public static boolean whooshEffect()
	{
		return whooshEffect;
	}

	public static void showWhooshEffect(final Location loc)
	{
		if (!whooshEffect())
			return;

		final World world = loc.getWorld();
		world.playEffect(loc, Effect.ENDER_SIGNAL, 0);
		world.playEffect(loc, Effect.ENDER_SIGNAL, 0);
		world.playEffect(loc, Effect.SMOKE, 4);
		world.playEffect(loc, Effect.SMOKE, 4);
		world.playEffect(loc, Effect.SMOKE, 4);
		world.playEffect(loc, Effect.GHAST_SHOOT, 0);
	}

	public static boolean getIfPlayerKill()
	{
		return killPlayer;
	}

	public static boolean getDenyEnderpearl()
	{
		return denyEnderpearl;
	}

	public static void setDenyEnderpearl(final boolean enable)
	{
		denyEnderpearl = enable;
		log("Direct cancellation of ender pearls thrown past the border " + (enable ? "enabled" : "disabled") + ".");
		//doNotsave(true);
	}

	public static void setPortalRedirection(final boolean enable)
	{
		portalRedirection = enable;
		log("Portal redirection " + (enable ? "enabled" : "disabled") + ".");
		//doNotsave(true);
	}

	public static boolean portalRedirection()
	{
		return portalRedirection;
	}

	public static void setKnockBack(final double numBlocks)
	{
		knockBack = numBlocks;
		log("Knockback set to " + knockBack + " blocks inside the border.");
		//doNotsave(true);
	}

	public static double KnockBack()
	{
		return knockBack;
	}

	public static int TimerTicks()
	{
		return timerTicks;
	}

	public static void setRemountTicks(final int ticks)
	{
		remountDelayTicks = ticks;
		if (remountDelayTicks == 0) {
			log("Remount delay set to 0. Players will be left dismounted when knocked back from the border while on a vehicle.");
		} else {
			log("Remount delay set to " + remountDelayTicks + " tick(s). That is roughly " + (remountDelayTicks * 50) + "ms / " + ((remountDelayTicks * 50.0) / 1000.0) + " seconds.");
		}
		if (ticks < 10)
		{
			logWarn("setting the remount delay to less than 10 (and greater than 0) is not recommended. This can lead to nasty client glitches.");
			//doNotsave(true);
		}
	}

	public static int RemountTicks()
	{
		return remountDelayTicks;
	}

	public static void setFillAutosaveFrequency(final int seconds)
	{
		fillAutosaveFrequency = seconds;
		if (fillAutosaveFrequency == 0) {
			log("World autosave frequency during Fill process set to 0, disabling it. Note that much progress can be lost this way if there is a bug or crash in the world generation process from Bukkit or any world generation plugin you use.");
		}
		else {
			log("World autosave frequency during Fill process set to " + fillAutosaveFrequency + " seconds (rounded to a multiple of 5). New chunks generated by the Fill process will be forcibly saved to disk this often to prevent loss of progress due to bugs or crashes in the world generation process.");
			//doNotsave(true);
		}
	}

	public static int FillAutosaveFrequency()
	{
		return fillAutosaveFrequency;
	}


	public static void setDynmapBorderEnabled(final boolean enable)
	{
		dynmapEnable = enable;
		log("DynMap border display is now " + (enable ? "enabled" : "disabled") + ".");
		//doNotsave(true);
	}

	public static boolean DynmapBorderEnabled()
	{
		return dynmapEnable;
	}

	public static void setDynmapMessage(final String msg)
	{
		dynmapMessage = msg;
		log("DynMap border label is now set to: " + msg);
		//doNotsave(true);
	}

	public static String DynmapMessage()
	{
		return dynmapMessage;
	}

	public static void setPlayerBypass(final String player, final boolean bypass)
	{
		if (bypass) {
			bypassPlayers.add(player.toLowerCase());
		} else {
			bypassPlayers.remove(player.toLowerCase());
		}
	}

	public static boolean isPlayerBypassing(final String player)
	{
		return bypassPlayers.contains(player.toLowerCase());
	}

	public static void togglePlayerBypass(final String player)
	{
		setPlayerBypass(player, !isPlayerBypassing(player));
	}

	public static String getPlayerBypassList()
	{
		if (bypassPlayers.isEmpty())
			return "<none>";
		final String newString = bypassPlayers.toString();
		return newString.substring(1, newString.length() - 1);
	}



	public static boolean isBorderTimerRunning()
	{
		if (borderTask == -1) return false;
		return (plugin.getServer().getScheduler().isQueued(borderTask) || plugin.getServer().getScheduler().isCurrentlyRunning(borderTask));
	}

	public static void StopFillTask()
	{
		if (fillTask != null && fillTask.valid()) {
			fillTask.cancel();
		}
	}

	public static void StoreFillTask()
	{
		//doNotsave(false, true);
	}
	public static void UnStoreFillTask()
	{
		//doNotsave(false);
	}

	public static void RestoreFillTask(final String world, final int fillDistance, final int chunksPerRun, final int tickFrequency, final int x, final int z, final int length, final int total, final boolean forceLoad)
	{
		fillTask = new GeneratorFillTask(plugin.getServer(), null, world, fillDistance, chunksPerRun, tickFrequency, forceLoad);
		if (fillTask.valid())
		{
			fillTask.continueProgress(x, z, length, total);
			final int task = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, fillTask, 20, tickFrequency);
			fillTask.setTaskID(task);
		}
	}
	// for backwards compatibility
	public static void RestoreFillTask(final String world, final int fillDistance, final int chunksPerRun, final int tickFrequency, final int x, final int z, final int length, final int total)
	{
		RestoreFillTask(world, fillDistance, chunksPerRun, tickFrequency, x, z, length, total, false);
	}


	public static int AvailableMemory()
	{
		return (int)((rt.maxMemory() - rt.totalMemory() + rt.freeMemory()) / 1048576);  // 1024*1024 = 1048576 (bytes in 1 MB)
	}

	public static boolean AvailableMemoryTooLow()
	{
		return AvailableMemory() < fillMemoryTolerance;
	}


	public static boolean HasPermission(final Player player, final String request)
	{
		return HasPermission(player, request, true);
	}
	public static boolean HasPermission(final Player player, final String request, final boolean notify)
	{
		if (player == null)				// console, always permitted
			return true;

		if (player.hasPermission("worldborder." + request))	// built-in Bukkit superperms
			return true;

		if (notify) {
			player.sendMessage("You do not have sufficient permissions.");
		}

		return false;
	}


	public static String replaceAmpColors (final String message)
	{
		return ChatColor.translateAlternateColorCodes('&', message);
	}
	// adapted from code posted by Sleaker
	public static String stripAmpColors (final String message)
	{
		return message.replaceAll("(?i)&([a-fk-or0-9])", "");
	}


	public static void log(final Level lvl, final String text)
	{
		wbLog.log(lvl, text);
	}
	public static void log(final String text)
	{
		log(Level.INFO, text);
	}
	public static void logWarn(final String text)
	{
		log(Level.WARNING, text);
	}
	public static void logConfig(final String text)
	{
		log(Level.INFO, "[CONFIG] " + text);
	}
}
