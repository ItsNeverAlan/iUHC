package spg.lgdev.uhc.world;

import java.io.File;
import java.util.Objects;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.enums.DMArenaType;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.task.CenterClearTask;
import spg.lgdev.uhc.util.UHCSound;
import net.md_5.bungee.api.ChatColor;

public class UHCWorldCreator {

	private final iUHC plugin;

	public UHCWorldCreator(final iUHC plugin) {
		this.plugin = plugin;
	}

	public void prepareToWorlds() {
		UHCGame.getInstance().setWorldCreating(true);
		if (CachedConfig.IsGenerated() || CachedConfig.IsGenerating()) {
			plugin.getWorldCreator().importWorlds("UHCArena");
		} else if (CachedConfig.BiomeChecker) {
			createCenterWorld("UHCArena");
		} else {
			createWorld("UHCArena");
		}
	}

	public void unloadUselessWorlds() {
		if (!UHCGame.getInstance().isNether() && Bukkit.getWorld("UHCArena_nether") != null) {
			unloadWorld(Bukkit.getWorld("UHCArena_nether"));
		}
		if (!CachedConfig.MatchEnabled && Bukkit.getWorld("UHCArena_deathmatch") != null) {
			unloadWorld(Bukkit.getWorld("UHCArena_deathmatch"));
		}
		if (GameStatus.started() && Bukkit.getWorld("UHCArena_practice") != null) {
			unloadWorld(Bukkit.getWorld("UHCArena_practice"));
		}
		for (final World world : Bukkit.getWorlds()) {
			if (!world.getName().contains("UHCArena")) {
				unloadWorld(world);
			}
		}
	}

	private void createWorld(final String worldName) {
		plugin.clearArena();
		final World uhc = Bukkit.createWorld(new WorldCreator(worldName).environment(World.Environment.NORMAL));

		Bukkit.broadcastMessage("§7[§6§lUltimate§f§lUHC§7]§f Find a good seed!: " + uhc.getSeed());
		Bukkit.broadcastMessage(ChatColor.GREEN + "The arena worlds has been created!");
		UHCSound.LEVEL_UP.playSoundToEveryone();
		UHCGame.getInstance().setWorldCreating(false);
	}

	private void createCenterWorld(final String worldName) {

		plugin.clearArena();
		plugin.log(true, "&aStart to create the arena world");

		new CenterClearTask(plugin, this, worldName).runTaskTimer(plugin, 40L, 40L);

	}

	public void importWorlds(final String worldName) {

		Bukkit.createWorld(new WorldCreator(worldName));
		Bukkit.createWorld(new WorldCreator(worldName + "_nether").environment(Environment.NETHER));

		if (CachedConfig.MatchEnabled && !(UHCGame.getInstance().getDeathmatchArenaType() == DMArenaType.NORMAL)) {

			final WorldCreator creator = new WorldCreator(worldName + "_deathmatch");

			if (UHCGame.getInstance().getDeathmatchArenaType() == DMArenaType.FLAT) {
				creator.type(WorldType.FLAT);
			}

			Bukkit.createWorld(creator);

		}

		if (UHCGame.getInstance().isPracticeEnabled()) {
			Bukkit.createWorld(new WorldCreator(worldName + "_practice").environment(Environment.NORMAL));
		}

		if (CachedConfig.IsGenerated()) {
			plugin.getChunkManager().finish(plugin);
		}
	}

	public void unloadWorld(final World world) {
		if (world != null) {
			for (final Player p : world.getPlayers()) {
				p.teleport(UHCGame.getInstance().getSpawnPoint().toBukkitLocation());
			}
			Bukkit.getServer().unloadWorld(world, false);
		}
	}

	public void deleteWorld(final String world) {
		final File filePath = new File(Bukkit.getWorldContainer(), world);
		deleteFiles(filePath);
	}

	private boolean deleteFiles(final File path) {
		if (path.exists()) {
			final File[] files = path.listFiles();
			File[] arrayOfFile1;
			final int j = (Objects.requireNonNull(arrayOfFile1 = files)).length;
			for (int i = 0; i < j; i++) {
				final File file = arrayOfFile1[i];
				if (file.isDirectory()) {
					deleteFiles(file);
				} else {
					file.delete();
				}
			}
		}
		return path.delete();
	}

}
