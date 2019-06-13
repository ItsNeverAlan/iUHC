package spg.lgdev.uhc.visualise;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import spg.lgdev.uhc.util.memory.MemoryLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.material.MaterialData;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.handler.game.UHCGame;
import net.minecraft.server.v1_8_R3.BlockPosition;
import org.bukkit.scheduler.BukkitRunnable;

public class VisualiseHandler implements Runnable, Listener {

	private final VisualBlockData visualBlockData;
	private final iUHC plugin;
	private final Queue<VisualTask> visualTasks = new ConcurrentLinkedQueue<>();
	private final Table<UUID, BlockPosition, VisualBlock> table = HashBasedTable.create();

	@Getter
	@Setter
	private boolean pause;

	private AsyncServerWarpTimer timer;

	public VisualiseHandler(final iUHC plugin) {
		this.plugin = plugin;
		this.visualBlockData = new VisualBlockData(plugin.getBorderStyle().getVisualMaterial(),
				plugin.getBorderStyle().getVisualData());
		Bukkit.getPluginManager().registerEvents(this, plugin);
		Bukkit.getScheduler().runTaskTimer(plugin, this, 1L, 1L);
		timer = new AsyncServerWarpTimer(this);
		timer.runTaskTimerAsynchronously(plugin, 4L, 4L);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		timer.removePlayer(event.getPlayer());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		timer.addPlayer(player);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerTeleport(PlayerTeleportEvent event){
		Player player = event.getPlayer();
		Location to = event.getTo();
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			MemoryLocation memoryBlockLocation = timer.previous.get(player);
			if(memoryBlockLocation != null && (to.getBlockX() != memoryBlockLocation.getX() || to.getBlockY() != memoryBlockLocation.getY() || to.getBlockZ() != memoryBlockLocation.getZ())){
				timer.previous.put(player, MemoryLocation.copyOf(to));
				handlePositionChanged(player, to.getWorld(), to.getBlockX(), to.getBlockY(), to.getBlockZ());
			}
		});
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event){
		plugin.getBarrierManager().clearAll(event.getPlayer(), false);
	}

	private final int VIEW_DISTANCE = 8;

	private enum Side {
		NORTH(0, -1),
		EAST(1, 0),
		SOUTH(0, 1),
		WEST(-1, 0),
		;

		private int modX;
		private int modZ;

		Side(int modX, int modZ) {
			this.modX = modX;
			this.modZ = modZ;
		}
	}

	public void handlePositionChanged(final Player player, World world, int px, int py, int pz) {
		if (pause)
			return;

		if (!world.getName().contains("UHCArena"))
			return;

		final int size = UHCGame.getInstance().getBorder(world.getName());

		List<BlockPosition> blockPositions = new ArrayList<>();

		Location position = player.getLocation();

		for (Side side : Side.values()) {
			double d;
			double pos;
			double max;
			if (side.modX != 0) {
				pos = position.getZ();
				max = size * side.modX;
				d = Math.abs(size * side.modX - position.getX());
			} else {
				pos = position.getX();
				max = size * side.modZ;
				d = Math.abs(size * side.modZ - position.getZ());
			}
			if (d >= VIEW_DISTANCE) continue;

			byte hloc = side.modX != 0 ? (byte)0 : 1;

			for (int y = -7; y < 8; y++) {
				for (int h = -9; h < 10; h++) {
					if (Math.abs(pos + h) > size) {
						continue;
					}
					blockPositions.add(new BlockPosition(hloc == 0 ? max : pos + h, position.getY() + y, hloc == 1 ? max : pos + h));
				}
			}
		}

		if (player.isOnline()) {
			visualTasks.removeIf(visualTask -> visualTask.getPlayer() == player);
			visualTasks.add(new VisualTask(player, blockPositions));
		}
	}

	public boolean contains(final UUID uuid) {
		return table.containsRow(uuid);
	}

	public void remove(final UUID uuid) {
		table.row(uuid).clear();
	}

	public void clearAll(final Player player, final boolean send) {
		table.rowMap().remove(player.getUniqueId());
		((CraftPlayer) player).getHandle().clearFakeBlocks(send);
	}

	public Map<BlockPosition, MaterialData> addVisualType(final Player player,
			final Collection<BlockPosition> locations, final boolean send) {
		final Map<BlockPosition, MaterialData> sendToClient = new HashMap<>();
		locations.removeIf(blockPosition -> {
			final World world = player.getWorld();
			final Block block = world.getBlockAt(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
			final Material material = block.getType();
			return material != Material.AIR;
		});
		synchronized (table) {
			final Iterator<BlockPosition> iterator = locations.iterator();
			while (iterator.hasNext()) {
				final BlockPosition blockPosition = iterator.next();
				sendToClient.put(blockPosition, visualBlockData);
				table.put(player.getUniqueId(), blockPosition, new VisualBlock(visualBlockData, blockPosition));
			}
		}
		((CraftPlayer) player).getHandle().setFakeBlocks(sendToClient, Collections.emptyList(), send);
		return sendToClient;
	}

	public Map<BlockPosition, MaterialData> setVisualType(final Player player,
			final Collection<BlockPosition> locations, final boolean send) {
		final Map<BlockPosition, MaterialData> sendToClient = new HashMap<>();
		final List<BlockPosition> removeFromClient = new ArrayList<>();
		locations.removeIf(blockPosition -> {
			final World world = player.getWorld();
			final Block block = world.getBlockAt(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
			final Material material = block.getType();
			return material != Material.AIR;
		});
		synchronized (table) {
			final Map<BlockPosition, VisualBlock> currentBlocks = table.row(player.getUniqueId());
			for (final Map.Entry<BlockPosition, VisualBlock> entry : new ArrayList<>(currentBlocks.entrySet())) {
				final BlockPosition blockPosition = entry.getKey();
				if (!locations.remove(blockPosition)) {
					removeFromClient.add(blockPosition);
					currentBlocks.remove(blockPosition);
				}
			}
			final Iterator<BlockPosition> iterator = locations.iterator();
			while (iterator.hasNext()) {
				final BlockPosition blockPosition = iterator.next();
				sendToClient.put(blockPosition, visualBlockData);
				table.put(player.getUniqueId(), blockPosition, new VisualBlock(visualBlockData, blockPosition));
			}
		}
		((CraftPlayer) player).getHandle().setFakeBlocks(sendToClient, removeFromClient, send);
		return sendToClient;
	}

	@Override
	public void run() {
		VisualTask visualTask;
		while ((visualTask = visualTasks.poll()) != null){
			if (visualTask.getPlayer().isOnline()) {
				this.setVisualType(visualTask.getPlayer(), visualTask.getBlockPositions(), true);
			}
		}
	}

	@AllArgsConstructor
	@Getter
	private class VisualTask {

		private final Player player;
		private final List<BlockPosition> blockPositions;

	}

	@Getter
	@Setter
	public static final class AsyncServerWarpTimer extends BukkitRunnable {
		private VisualiseHandler listener;
		protected ConcurrentMap<Player, MemoryLocation> previous = new ConcurrentHashMap<>();

		public AsyncServerWarpTimer(VisualiseHandler listener) {
			this.listener = listener;
		}

		public void addPlayer(Player player) {
			previous.putIfAbsent(player, MemoryLocation.copyOf(player.getLocation()));
			listener.handlePositionChanged(player, player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
		}

		public void removePlayer(Player player) {
			previous.remove(player);
		}

		public void run() {
			for (Map.Entry<Player, MemoryLocation> playerBlockVectorEntry : new HashSet<>(previous.entrySet())) {
				Player player = playerBlockVectorEntry.getKey();
				if (!player.isOnline()) {
					continue;
				}
				if (playerBlockVectorEntry.getValue() == null) {
					continue;
				}
				MemoryLocation from = playerBlockVectorEntry.getValue();
				MemoryLocation to = MemoryLocation.copyOf(player.getLocation());
				if (!from.equals(to)) {
					listener.handlePositionChanged(player, player.getWorld(), to.getX(), to.getY(), to.getZ());
					this.previous.replace(player, to);
				}
			}
		}
	}

}
