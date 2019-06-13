package spg.lgdev.uhc.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import spg.lgdev.uhc.api.events.GeneratorTaskCompleteEvent;
import spg.lgdev.uhc.border.BorderBuilder;
import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.enums.DMArenaType;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.util.Utils;
import spg.lgdev.uhc.world.generator.GeneratorManager;

public class ChunkManager implements org.bukkit.event.Listener {

	private final String mainArena;
	private String currentLoading;
	private boolean loading;

	private Iterator<String> haveToLoads;

	public ChunkManager(final String arenaName) {

		this.mainArena = arenaName;

	}

	public void load(final iUHC plugin, final String arenaName) {

		final List<String> list = new ArrayList<>();

		list.add(arenaName);
		list.add(arenaName + "_nether");

		Bukkit.createWorld(new WorldCreator(arenaName + "_nether").environment(World.Environment.NETHER));
		if (plugin.multiverse) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
					"multiverse-core:mvimport " + arenaName + "_nether nether");
		}

		if (CachedConfig.PRACTICE) {

			if (!Utils.fileExists(arenaName + "_practice")) {
				list.add(arenaName + "_practice");
			}

			Bukkit.createWorld(new WorldCreator(arenaName + "_practice").environment(World.Environment.NORMAL));
			if (plugin.multiverse) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
						"multiverse-core:mvimport " + arenaName + "_practice normal");
			}

		}

		if (CachedConfig.MatchEnabled && !(UHCGame.getInstance().getDeathmatchArenaType() == DMArenaType.NORMAL)) {

			final WorldCreator creator = new WorldCreator(arenaName + "_deathmatch").environment(World.Environment.NORMAL);
			if (UHCGame.getInstance().getDeathmatchArenaType() == DMArenaType.FLAT) {
				creator.type(WorldType.FLAT);
			}

			Bukkit.createWorld(creator);
			if (plugin.multiverse) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
						"multiverse-core:mvimport " + arenaName + "_deathmatch normal");
			}

		}

		haveToLoads = list.iterator();
		currentLoading = "null";

	}

	public void continueLoading(final iUHC plugin, final String currentLoading) {

		final List<String> list = new ArrayList<>();

		list.add(mainArena);
		list.add(mainArena + "_nether");

		if (!Utils.fileExists(mainArena + "_practice") && CachedConfig.PRACTICE) {
			list.add(mainArena + "_practice");
		}
		if (currentLoading.contains("_nether")) {
			list.remove(mainArena);
		} else if (currentLoading.contains("_practice")) {

			list.remove(mainArena);
			list.remove(mainArena + "_nether");
			if (!list.contains(mainArena + "_practice")) {
				list.add(mainArena + "_practice");
			}

		}

		haveToLoads = list.iterator();

	}

	public void loadArena(final iUHC plugin, final String worldName, final int size) {

		CachedConfig.setIsGenerating();

		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "uhc:wb " + worldName + " set " + size + " " + size + " 0 0");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
				"uhc:wb " + worldName + " fill " + CachedConfig.LoadFrequency + " " + CachedConfig.LoadPadding + " false");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "uhc:wb fill confirm");

		setLoading(true);
		return;
	}

	public void moveToNext(final iUHC plugin) {

		Bukkit.getScheduler().runTaskLater(plugin, () -> {

			if (!haveToLoads.hasNext() || !CachedConfig.isLoadChunk) {

				finish(plugin);
				return;

			}

			currentLoading = haveToLoads.next();

			loadArena(plugin, currentLoading, UHCGame.getInstance().getBorder(currentLoading));

			CachedConfig.setWorldIsInGen(currentLoading);

		}, 60l);

	}

	public void finish(final iUHC plugin) {

		setLoading(false);

		if (CachedConfig.LoadFinishRestart && !CachedConfig.IsGenerated()) {

			CachedConfig.setIsGenerated();
			CachedConfig.setIsNotGenerating();
			new BorderBuilder(Bukkit.getWorld("UHCArena"), UHCGame.getInstance().getBorderRadius(), 4).force().start(true);

			plugin.log(true, "&eChunk load done! restart in 10 seconds!");
			Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), CachedConfig.RestartCMD)), 200L);

			return;

		} else if (!CachedConfig.LoadFinishRestart) {

			CachedConfig.setIsGenerated();
			CachedConfig.setIsNotGenerating();
			new BorderBuilder(Bukkit.getWorld("UHCArena"), UHCGame.getInstance().getBorderRadius(), 4).force().start(true);

		}

		GameStatus.set(GameStatus.WAITING);

		System.out.println(" ");
		System.out.println(" ");
		System.out.println(" ");

		plugin.log(true, "&eWorld Creator jobs is now done!");

		if (!UHCGame.getInstance().isPracticeEnabled()) {
			plugin.getWorldCreator().unloadWorld(Bukkit.getWorld("UHCArena_practice"));
		}

		InventoryManager.instance.editor(InventoryManager.instance.getEditor());
		ArenaManager.getInstance().loadScatterPoints();

		setupWorld();

	}

	private void setupWorld() {

		final World uhc = Bukkit.getWorld("UHCArena");
		final World uhcNether = Bukkit.getWorld("UHCArena_nether");
		final World uhc_death = Bukkit.getWorld("UHCArena_deathmatch");
		final World practice = Bukkit.getWorld("UHCArena_practice");

		if (practice != null) {
			practice.setPVP(true);
			practice.setTime(0L);
			practice.setDifficulty(Difficulty.EASY);
			practice.setGameRuleValue("doDaylightCycle", "false");
			practice.setSpawnLocation(0, 100, 0);
			practice.setAnimalSpawnLimit(0);
			practice.setMonsterSpawnLimit(0);
			practice.setGameRuleValue("naturalRegeneration", "false");

			practice.getEntities().stream().filter(entity -> !(entity instanceof Player)).forEach(Entity::remove);
		}

		if (uhc != null) {
			uhc.setPVP(true);
			uhc.setTime(0L);
			uhc.setDifficulty(Difficulty.HARD);
			uhc.setGameRuleValue("doDaylightCycle", "false");
			uhc.setSpawnLocation(0, 100, 0);
			uhc.setGameRuleValue("naturalRegeneration", "false");
		}

		if (uhc_death != null) {
			uhc_death.setMonsterSpawnLimit(0);
			uhc_death.setAnimalSpawnLimit(0);
			uhc_death.setAmbientSpawnLimit(0);
			uhc_death.setTicksPerAnimalSpawns(0);
			uhc_death.setTicksPerMonsterSpawns(0);
			uhc_death.setTime(0);
			uhc_death.setPVP(true);
			uhc_death.setDifficulty(Difficulty.HARD);
			uhc_death.setGameRuleValue("doDaylightCycle", "false");
			uhc_death.setGameRuleValue("naturalRegeneration", "false");
		}

		if (uhcNether != null) {
			uhcNether.setPVP(true);
			uhcNether.setDifficulty(Difficulty.HARD);
			uhcNether.setGameRuleValue("naturalRegeneration", "false");
		}

	}

	public double getPercentageCompleted() {
		return GeneratorManager.fillTask.getPercentageCompleted();
	}

	public double getChunkTotal() {
		return GeneratorManager.fillTask.getChunksTotal();
	}

	public double getChunkCompleted() {
		return GeneratorManager.fillTask.getChunksCompleted();
	}

	@EventHandler
	public void onWorldFillFinished(final GeneratorTaskCompleteEvent e) {
		if (!e.getWorldName().contains("UHCArena"))
			return;
		moveToNext(iUHC.getInstance());
		return;
	}

	public boolean isLoading() {
		return loading;
	}

	public void setLoading(final boolean loading) {
		this.loading = loading;
	}

}
