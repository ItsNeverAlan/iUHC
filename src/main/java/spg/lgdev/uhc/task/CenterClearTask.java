package spg.lgdev.uhc.task;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.util.UHCSound;
import spg.lgdev.uhc.util.cuboid.Cuboid;
import spg.lgdev.uhc.world.Biomes;
import spg.lgdev.uhc.world.Biomes.BIOME_THRESHOLD;
import spg.lgdev.uhc.world.UHCWorldCreator;
import net.md_5.bungee.api.ChatColor;

public class CenterClearTask extends BukkitRunnable {

	private int generateTimes = 0;

	private int liquids = 0;
	private int biomesLimited = 0;

	private final int maxLiquids = CachedConfig.BiomeLava;
	private final int maxBiomesLimited = CachedConfig.BiomeDisAllowed;
	private final int maxLocationY = CachedConfig.MaxHigher;
	private final int maxTrees = CachedConfig.BiomeTree;

	private boolean badWorld = false;
	private boolean riversInCenter = false;

	private int locationY = 0;
	private int trees = 0;

	private final String worldName;
	private final iUHC plugin;
	private final UHCWorldCreator worldCreator;

	public CenterClearTask(final iUHC plugin, final UHCWorldCreator worldCreator, final String worldName) {
		this.worldName = worldName;
		this.plugin = plugin;
		this.worldCreator = worldCreator;
	}

	@Override
	public void run() {

		generateTimes++;

		if (generateTimes > 1) {

			final World w1 = Bukkit.getWorld("UHCArena");

			if (plugin.multiverse) {

				Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "multiverse-core:mvdelete UHCArena");
				Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "multiverse-core:mv confirm");

			}

			worldCreator.unloadWorld(w1);

		}

		if (generateTimes > 200) {

			Bukkit.broadcastMessage(ChatColor.RED + "Tried 200 times but still not found good seed, using safe list of seeds.");
			this.cancel();
			return;

		}

		Bukkit.getScheduler().runTaskLater(plugin, () -> {

			worldCreator.deleteWorld("UHCArena");

			liquids = 0;
			biomesLimited = 0;
			locationY = 0;
			trees = 0;

			riversInCenter = false;
			badWorld = false;

			final WorldCreator worldCreator = new WorldCreator(worldName).environment(World.Environment.NORMAL).type(WorldType.NORMAL);
			final World world = Bukkit.createWorld(worldCreator);

			for (int i = -100; i <= 100; ++i) {
				for (int j = -100; j <= 100; j++) {

					final Block block = world.getHighestBlockAt(i, j).getLocation().add(0.0, -1.0, 0.0).getBlock();

					if (block.getY() > 78) {
						locationY = block.getY();
					}

					final BIOME_THRESHOLD biomeThreshold = Biomes.isValidBiome(block);

					if (biomeThreshold == BIOME_THRESHOLD.LIMITED) {
						biomesLimited++;
					} else if (biomeThreshold == BIOME_THRESHOLD.DISALLOWED) {
						badWorld = true;
					}

				}
			}

			for (int i = -CachedConfig.RiverInsideCenter; i <= CachedConfig.RiverInsideCenter; ++i) {

				for (int j = -CachedConfig.RiverInsideCenter; j <= CachedConfig.RiverInsideCenter; j++) {

					if (world.getHighestBlockAt(i, j).getBiome().equals(Biome.RIVER)) {
						riversInCenter = true;
						break;
					}

				}

			}

			for (final Block b : new Cuboid(new Location(world, 100, 81, 100), new Location(world, -100, 55, -100))) {
				final Material material = b.getType();
				if (material == Material.LOG || material == Material.LOG_2) {
					trees++;
				} else if (material == Material.WATER || material == Material.STATIONARY_WATER
						|| material == Material.LAVA || material == Material.STATIONARY_LAVA) {
					liquids++;
				}
			}

			if (riversInCenter) {

				plugin.log(true, "&cRiver Biome inside 50x50!");
				Bukkit.broadcastMessage(ChatColor.RED + "automatic recreating the world! (River Biome inside the center 50x50!)");
				return;

			}

			if (liquids >= maxLiquids) {

				plugin.log(true, "&cToo many waters/lavas!");
				Bukkit.broadcastMessage("§cautomatic recreating the world! (Too many waters/lavas inside center!) §7(§4" + liquids + "§7)");

				return;

			}

			if (biomesLimited > maxBiomesLimited || badWorld) {

				plugin.log(true, "&cDisallowed Biomes!");
				Bukkit.broadcastMessage("§cautomatic recreating the world! (Disallowed Biomes inside center!) §7(§4" + biomesLimited + "§7)");

				return;

			}

			if (trees >= maxTrees) {

				plugin.log(true, "&cToo many trees!");
				Bukkit.broadcastMessage("§cautomatic recreating the world! (Too many trees inside center) §7(§4" + trees + "§7)");
				return;

			}

			if (locationY >= maxLocationY) {

				plugin.log(true, "&cToo higher of plains!");
				Bukkit.broadcastMessage("§cautomatic recreating the world! (The many trees inside center!) §7(§4" + locationY + "§7)");
				return;

			}

			Bukkit.broadcastMessage("§7[§6§lUHC§7]§e Find a good seed!: §6" + world.getSeed());
			Bukkit.broadcastMessage(ChatColor.GREEN + "The arena worlds has been created!");
			UHCSound.LEVEL_UP.playSoundToEveryone();
			UHCGame.getInstance().setWorldCreating(false);
			this.cancel();

		}, 20L);

	}
}
