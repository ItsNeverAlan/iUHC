package spg.lgdev.uhc.handler.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.nms.packetlistener.UHCPacketListener;
import spg.lgdev.uhc.util.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import lombok.Getter;
import lombok.Setter;
import spg.lgdev.uhc.api.events.GameFinishedEvent;
import spg.lgdev.uhc.api.events.GameScatteringEvent;
import spg.lgdev.uhc.api.events.GameStartedEvent;
import spg.lgdev.uhc.border.BorderBuilder;
import spg.lgdev.uhc.border.BorderRadius;
import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.config.Configuration;
import spg.lgdev.uhc.enums.BarType;
import spg.lgdev.uhc.enums.Customers;
import spg.lgdev.uhc.enums.DMArenaType;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.enums.PlayerStatus;
import spg.lgdev.uhc.gui.gameconfig.AntiCraftingGUI;
import spg.lgdev.uhc.gui.gameconfig.PreConfigGUI;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.Library;
import spg.lgdev.uhc.handler.impl.CombatTimer;
import spg.lgdev.uhc.handler.impl.DisconnectTimer;
import spg.lgdev.uhc.handler.impl.NocleanTimer;
import spg.lgdev.uhc.manager.ArenaManager;
import spg.lgdev.uhc.manager.InventoryManager;
import spg.lgdev.uhc.manager.TeamManager;
import spg.lgdev.uhc.nms.NMSHandler;
import spg.lgdev.uhc.player.DataManager;
import spg.lgdev.uhc.player.PlayerProfile;
import spg.lgdev.uhc.player.TeamProfile;
import spg.lgdev.uhc.scenario.Scenarios;
import spg.lgdev.uhc.task.BarTask;
import spg.lgdev.uhc.task.DeathmatchTask;
import spg.lgdev.uhc.task.FireworkTask;
import spg.lgdev.uhc.task.GameTask;
import spg.lgdev.uhc.task.InviteCleanupTask;
import spg.lgdev.uhc.task.LobbyTask;
import spg.lgdev.uhc.task.ReleaseTask;
import spg.lgdev.uhc.task.TimebombTask;
import net.development.mitw.Mitw;
import net.development.mitw.uuid.UUIDCache;

@Getter
@Setter
public class UHCGame {

	@Getter
	private static UHCGame instance;

	private final iUHC plugin;

	private Map<PotionEffect, String> effectMap = new HashMap<>();

	private int maxIngamePlayers;
	private int maxplayers;
	private int appleRate;
	private int goldenAppleRate;
	private int pvpTime;
	private int timebombTimer;
	private int finalHealTime;
	private int borderRadius;
	private int firstBorder;
	private boolean worldCreating;
	private boolean backpack;
	private boolean goldenHead;
	private boolean practiceEnabled;
	private boolean allowRescatter;
	private boolean broadcastingRules;
	private boolean deathmatchCountdowning;
	private boolean lobbyCountdowning;
	private boolean BorderShrinking;
	private boolean deathmatch;
	private boolean deathmatchStarted;

	public int finalHealCountdowns = 0;
	public int pvpCountdowns = 0;
	public int gameCountdowns = 0;
	public int deathmatchCountdowns = 0;
	public int lobbyCountdowns = 0;
	public int borderCountdowns = 0;

	private final List<UUID> winners = new ArrayList<>();
	private final List<UUID> practicePlayers = new ArrayList<>();
	private final List<UUID> whitelist = new ArrayList<>();
	private final List<UUID> hosts = new ArrayList<>();
	private final List<UUID> mods = new ArrayList<>();
	private final List<String> offlineKicks = new ArrayList<>();
	private final List<String> offlineRespawns = new ArrayList<>();
	private final List<String> playerWhitelists = new ArrayList<>();
	private final DMArenaType deathmatchArenaType;
	private int teleported;
	private int totalTeleport;
	private int speed;
	private int streght;
	private boolean shears;
	private boolean nether;
	private boolean deathKick;
	private boolean enderpearl;
	private boolean finalheal;
	private boolean finalhealed;
	private boolean pvp;
	private boolean hasBorder;
	private boolean whitelisted;
	private boolean openChat;
	private boolean firstBorderDone;

	private String hostName;
	private CustomLocation spawnPoint;
	private BarType currentBarType = null;
	private ItemStack[] flowerPowerDrops;
	private TimebombTask timebombTask;

	public UHCGame(final iUHC plugin) {

		instance = this;
		this.plugin = plugin;

		final Configuration config = plugin.getFileManager().getConfig();

		setMaxplayers(config.getInt("gameConfig.playerLimit"));
		setBorderRadius(plugin.getFileManager().getCache().getInt("border"));
		setPvpTime(config.getInt("gameConfig.tasks.pvpTimer"));
		setDeathmatch(CachedConfig.MatchEnabled);
		setFirstBorder(config.getInt("gameConfig.tasks.deathmatchTimer"));

		finalHealTime = 600;
		appleRate = 3;
		BorderShrinking = false;
		goldenAppleRate = 1;
		maxIngamePlayers = 0;
		setBackpack(false);
		goldenHead = true;
		deathmatchCountdowning = false;
		lobbyCountdowning = false;
		setTeleported(0);
		setTotalTeleport(0);
		openChat = true;
		deathKick = false;
		shears = true;
		setFinalheal(true);
		setNether(false);
		setSpeed(1);
		setStreght(0);
		setEnderpearl(false);
		setHasBorder(true);
		allowRescatter = true;
		if (CachedConfig.IsGenerated()) {
			practiceEnabled = CachedConfig.PRACTICE;
		} else {
			practiceEnabled = false;
		}
		broadcastingRules = false;
		worldCreating = false;
		hostName = "";
		spawnPoint = CustomLocation.stringToLocation(plugin.getFileManager().getCache().getString("Locations.spawn"));
		setTimebombTimer(config.getInt("scenarios.config.Timebomb-Timer"));
		effectMap.put(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 999999, 2), "SuperHeroList.FIRE_RESISTANCE");
		effectMap.put(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 2), "SuperHeroList.INVISIBILITY");
		effectMap.put(new PotionEffect(PotionEffectType.FAST_DIGGING, 999999, 1), "SuperHeroList.FAST_DIGGING");
		effectMap.put(new PotionEffect(PotionEffectType.SLOW_DIGGING, 999999, 0), "SuperHeroList.SLOW_DIGGING");
		effectMap.put(new PotionEffect(PotionEffectType.JUMP, 999999, 1), "SuperHeroList.JUMP");
		effectMap.put(new PotionEffect(PotionEffectType.WATER_BREATHING, 999999, 2), "SuperHeroList.WATER_BREATHING");
		effectMap.put(new PotionEffect(PotionEffectType.NIGHT_VISION, 999999, 2), "SuperHeroList.NIGHT_VISION");
		effectMap.put(new PotionEffect(PotionEffectType.WATER_BREATHING, 999999, 2), "SuperHeroList.WATER_BREATHING");
		effectMap.put(new PotionEffect(PotionEffectType.SPEED, 999999, 1), "SuperHeroList.SPEED");
		effectMap.put(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 999999, 1), "SuperHeroList.INCREASE_DAMAGE");
		effectMap.put(new PotionEffect(PotionEffectType.SATURATION, 999999, 1), "SuperHeroList.SATURATION");
		effectMap.put(new PotionEffect(PotionEffectType.SLOW, 999999, 0), "SuperHeroList.SLOW");

		deathmatchArenaType = DMArenaType.valueOf(CachedConfig.MatchArenaType.toUpperCase());
		setupCustomRecipe();
	}

	private void setupCustomRecipe() {

		if (Customers.getCurrentCustomer() == Customers.MitwOffical)
			return;

		final ShapelessRecipe recipe = new ShapelessRecipe(new ItemStack(Material.BOOK));
		recipe.addIngredient(Material.ENCHANTED_BOOK);
		recipe.addIngredient(Material.ENCHANTED_BOOK);
		Bukkit.addRecipe(recipe);

	}

	public int getBorderTime(final int number) {
		return ((firstBorder - getPvpTime()) + number * 5) * 60;
	}

	public int setBorderRadius(final int borderRadius) {
		if (CachedConfig.WorldBorder) {
			NMSHandler.getInstance().getNMSControl().setWorldBorder1_8(Bukkit.getWorld("UHCArena"), borderRadius);
		}

		this.borderRadius = borderRadius;
		return borderRadius;
	}

	public int setDeathmatchBorder(final int i) {
		if (CachedConfig.MatchBorderEnabled) {
			if (deathmatchArenaType == DMArenaType.FLAT) {
				new BorderBuilder(Bukkit.getWorld("UHCArena_deathmatch"), i, 4).start(true);
			} else if (deathmatchArenaType == DMArenaType.NORMAL) {
				new BorderBuilder(Bukkit.getWorld("UHCArena"), i, 4).start(true);
			}
		}
		if (CachedConfig.MatchWorldBorderEnabled) {
			if (deathmatchArenaType == DMArenaType.FLAT || deathmatchArenaType == DMArenaType.CUSTOM) {
				NMSHandler.getInstance().getNMSControl().setWorldBorder1_8(Bukkit.getWorld("UHCArena_deathmatch"), i);
			} else if (deathmatchArenaType == DMArenaType.NORMAL) {
				NMSHandler.getInstance().getNMSControl().setWorldBorder1_8(Bukkit.getWorld("UHCArena"), i);
			}
		}
		this.setHasBorder(false);
		borderRadius = i;
		return borderRadius;
	}

	public boolean insideBorder(final Location loc) {
		final String name = loc.getWorld().getName();
		if (!name.contains("UHCArena"))
			return true;
		final int size = getBorder(name);
		return !(loc.getX() < -size || loc.getX() > size || loc.getZ() < -size || loc.getZ() > size);
	}

	public boolean insideBorder(final double x, final double z, final String name) {
		if (!name.contains("UHCArena"))
			return true;
		final int size = getBorder(name);
		return !(x < -size || x > size || z < -size || z > size);
	}

	public int getBorder(final String worldName) {
		switch (worldName) {
		case "UHCArena":
			return getBorderRadius();
		case "UHCArena_nether":
			return CachedConfig.NetherBorderSize;
		case "UHCArena_practice":
			return 150;
		case "UHCArena_deathmatch":
			return CachedConfig.MatchBorderSize;
		}
		return 0;
	}

	public boolean isMod(final UUID p) {
		return mods.contains(p);
	}

	public boolean isHost(final UUID p) {
		return hosts.contains(p);
	}

	public Location getNetherLocationSmart(Location location, final boolean enterNether) {
		final double x = enterNether ? location.getX() / 8 : location.getX() * 8;
		final double z = enterNether ? location.getZ() / 8 : location.getZ() * 8;
		final double y = location.getY();
		location.setX(Math.round(x) + 0.5D);
		location.setY(y);
		location.setZ(Math.round(z) + 0.5D);
		location.setWorld(Bukkit.getWorld("UHCArena" + (enterNether ? "_nether" : "")));

		if (!insideBorder(location)) {
			location = BorderRadius.correctedPosition(location, true, 10);
		}

		return location;
	}

	public List<Player> getOnlinePlayers() {
		return plugin.getServer().getOnlinePlayers().stream()
				.filter(plugin.getProfileManager()::isAlive).collect(Collectors.toList());
	}

	public List<PlayerProfile> getOnlinePlayerProfiles() {
		return plugin.getServer().getOnlinePlayers().stream()
				.map(player -> plugin.getProfileManager().getProfile(player.getUniqueId()))
				.filter(PlayerProfile::isOnline)
				.filter(PlayerProfile::isPlayerAlive)
				.collect(Collectors.toList());
	}

	public List<UUID> getPlayersUUID() {
		return plugin.getServer().getOnlinePlayers().stream()
				.filter(plugin.getProfileManager()::isAlive).map(Player::getUniqueId).collect(Collectors.toList());
	}

	public List<Player> getOnlineSpectators() {
		return plugin.getServer().getOnlinePlayers().stream()
				.filter(player -> plugin.getProfileManager().getProfile(player.getUniqueId()).isSpectator())
				.collect(Collectors.toList());
	}

	public void lobby(final Player p, final boolean teleport) {

		clear(p, GameMode.ADVENTURE);

		if (CachedConfig.HIDEALL) {
			Utils.hideAllPlayers(p);
		}

		plugin.getItemManager().setSpawnItems(p);
		CachedConfig.SOUND_JOIN.playSound(p);

		if (teleport) {
			if (spawnPoint == null) {
				p.sendMessage("§ccant find the location §6spawn§c !");
			} else {
				p.teleport(spawnPoint.toBukkitLocation());
			}
		}
	}

	public void clear(final Player player, final GameMode gameMode) {
		player.setHealth(20.0D);
		player.setFoodLevel(20);
		player.setSaturation(12.8F);
		player.setMaximumNoDamageTicks(20);
		player.setFireTicks(0);
		player.setFallDistance(0.0F);
		player.setLevel(0);
		player.setExp(0.0F);
		player.setWalkSpeed(0.2F);
		player.getInventory().setHeldItemSlot(0);
		player.setAllowFlight(false);
		player.setCanPickupItems(true);
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.closeInventory();
		player.setGameMode(gameMode);
		player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
		((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) 0);
		player.updateInventory();
	}

	public void sendToBungeeLobby(final Player p) {
		final String server = CachedConfig.HUBS.get(iUHC.getRandom().nextInt(CachedConfig.HUBS.size()) - 1);
		final ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(server);
		p.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
	}

	public void setSpawnByPlayer(final Player p) {
		final CustomLocation uhcLocation = CustomLocation.fromBukkitLocation(p.getLocation());
		final String loc = CustomLocation.locationToString(uhcLocation);

		p.sendMessage("§6Spawn §apoint has been set and saved!");

		plugin.getFileManager().getCache().set("Locations.spawn", loc);
		plugin.getFileManager().saveCache();
		setSpawnPoint(uhcLocation);
	}

	public void setOpenChat(final boolean openChat) {
		this.openChat = openChat;
		if (openChat) {
			Lang.getInstance().broadCast("Chat.Chat-Enable");
		} else {
			Lang.getInstance().broadCast("Chat.Chat-Disable");
		}
	}

	public void setWhitelisted(final boolean whitelisted) {
		if (this.whitelisted == whitelisted)
			return;
		this.whitelisted = whitelisted;
		updateMotd();
	}

	public void setupFlowerPowerDrops() {
		flowerPowerDrops = new ItemStack[] {
				new ItemStack(Material.DIAMOND, 5),
				new ItemStack(Material.DIAMOND, 10),
				new ItemStack(Material.SUGAR, 3),
				new ItemStack(Material.DIAMOND, 1),
				new ItemStack(Material.SUGAR, 5),
				new ItemStack(Material.SUGAR, 1),
				new ItemStack(Material.DIAMOND_SWORD, 1),
				new ItemStack(Material.DIAMOND_BLOCK, 5),
				new ItemStack(Material.WOOD, 20),
				new ItemStack(Material.DIAMOND_HELMET, 1),
				new ItemStack(Material.SUGAR, 2),
				new ItemStack(Material.WOOD, 10),
				new ItemStack(Material.DIAMOND_CHESTPLATE, 1),
				new ItemStack(Material.WOOL, 3),
				new ItemStack(Material.SPIDER_EYE, 1),
				new ItemStack(Material.DIAMOND_LEGGINGS, 1),
				new ItemStack(Material.WOOD, 3),
				new ItemStack(Material.GLASS_BOTTLE, 5),
				new ItemStack(Material.DIAMOND_BOOTS, 1), new ItemStack(Material.WOOD, 5), new ItemStack(Material.EXP_BOTTLE, 20), new ItemStack(Material.EXP_BOTTLE, 30), new ItemStack(Material.SPIDER_EYE, 2), new ItemStack(Material.WOOL, 15),
				new ItemStack(Material.EXP_BOTTLE, 10), new ItemStack(Material.COOKED_BEEF, 20), new ItemStack(Material.COOKED_BEEF, 10), new ItemStack(Material.GOLDEN_APPLE, 5), new ItemStack(Material.WOOL, 30), new ItemStack(Material.WEB, 2), new ItemStack(Material.COOKED_BEEF, 30), new ItemStack(Material.COOKED_BEEF, 5), new ItemStack(Material.BLAZE_ROD, 2), new ItemStack(Material.FISHING_ROD, 1), new ItemStack(Material.GOLDEN_APPLE, 3), new ItemStack(Material.BLAZE_ROD, 1), new ItemStack(Material.COBBLESTONE, 20), new ItemStack(Material.DIAMOND_BLOCK, 2), new ItemStack(Material.WEB, 3), new ItemStack(Material.MOB_SPAWNER, 32), new ItemStack(Material.MOB_SPAWNER, 16), new ItemStack(Material.SPIDER_EYE, 5), new ItemStack(Material.GOLD_BLOCK, 1),
				new ItemStack(Material.GOLDEN_APPLE, 1), new ItemStack(Material.COBBLESTONE, 10), new ItemStack(Material.BLAZE_ROD, 3), new ItemStack(Material.YELLOW_FLOWER, 10), new ItemStack(Material.GOLD_INGOT, 10), new ItemStack(Material.GOLD_BLOCK, 10), new ItemStack(Material.COBBLESTONE, 5), new ItemStack(Material.GOLD_INGOT, 5),
				new ItemStack(Material.FISHING_ROD, 1), new ItemStack(Material.GOLD_INGOT, 5), new ItemStack(Material.GOLD_BLOCK, 5), new ItemStack(Material.NETHER_WARTS, 2), new ItemStack(Material.APPLE, 3), new ItemStack(Material.IRON_INGOT, 10), new ItemStack(Material.IRON_INGOT, 20), new ItemStack(Material.IRON_INGOT, 25), new ItemStack(Material.APPLE, 5), new ItemStack(Material.NETHER_WARTS, 3), new ItemStack(Material.ENDER_PEARL, 3), new ItemStack(Material.ENDER_PEARL, 2), new ItemStack(Material.APPLE, 1), new ItemStack(Material.NETHER_WARTS, 5), new ItemStack(Material.ENDER_PEARL, 1), new ItemStack(Material.ENDER_PEARL, 2),
				new ItemStack(Material.BOOK, 3), new ItemStack(Material.BOOK, 5), new ItemStack(Material.BOOK, 10), new ItemStack(Material.BOOK, 1),
				new ItemStack(Material.ENDER_CHEST, 2), new ItemStack(Material.ENDER_CHEST, 1), new ItemStack(Material.ENCHANTMENT_TABLE, 1), new ItemStack(Material.ENCHANTMENT_TABLE, 1), new ItemStack(Material.IRON_INGOT, 5),
		};
	}

	public void startScattering() {

		GameStatus.set(GameStatus.TELEPORT);

		UHCPacketListener.unregisterSoundPacket();

		Bukkit.getPluginManager().callEvent(new GameScatteringEvent());

		KitsHandler.getInstance().setKitInToItemStack();

		setWhitelisted(true);
		if (TeamManager.getInstance().isTeamsEnabled()) {
			TeamManager.getInstance().autoPlace();
			TeamManager.getInstance().disableAllUnusedTeams();
		}

		setOpenChat(false);
		setTotalTeleport(plugin.getPlayingFast());

		NMSHandler.getInstance().getNMSControl().setAllowedFly(true);
		CachedConfig.startCommands.forEach(s -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s));

		new ReleaseTask();

		PlayerUtil.broadcastAction(p -> {
			for (final String msg : Lang.getInstance().getMessageList(p, "Scatter.Start")) {
				p.sendMessage(Lang.getInstance().getMessage(p, "game-prefix") + msg);
			}
			plugin.getProfileManager().getProfile(p.getUniqueId()).setStatus(PlayerStatus.SCATTERING);
			CachedConfig.SOUND_SCATTER.playSound(p);
			return null;
		});
	}

	public void startGame() {

		plugin.setNocleanTimer(new NocleanTimer());
		Mitw.getInstance().getTimerManager().registerTimer(plugin.getNocleanTimer());
		plugin.setCombatTimer(new CombatTimer());
		Mitw.getInstance().getTimerManager().registerTimer(plugin.getCombatTimer());
		plugin.getServer().getPluginManager().registerEvents(plugin.getCombatTimer(), plugin);
		plugin.setDisconnectTimer(new DisconnectTimer());
		Mitw.getInstance().getTimerManager().registerTimer(plugin.getDisconnectTimer());

		GameStatus.set(GameStatus.PVE);

		NameTagUtil.updateAllTags();
		InviteCleanupTask.cancelTask();
		ScatterAllNotInArena();

		plugin.getProfileManager().setNoLateScatter();

		Bukkit.getWorld("UHCArena").setGameRuleValue("doMobSpawning", "false");
		NMSHandler.getInstance().getNMSControl().setAllowedFly(false);
		this.maxIngamePlayers = plugin.getPlayingFast();

		setPvp(false);
		CachedConfig.setIsNotGenerated();

		if(Scenarios.FlowerPower.isOn()) {
			setupFlowerPowerDrops();
		}

		Bukkit.getPluginManager().callEvent(new GameStartedEvent());

		AntiCraftingGUI.getInstance().removeRecipes();
		new GameTask();
		timebombTask = new TimebombTask();

		if (CachedConfig.BOSSBAR) {
			setCurrentBarType(BarType.FinalHeal);
			new BarTask();
		}
	}

	private String[] listString = null;
	private PotionEffect[] listEffect = null;

	public void clearPlayers() {

		PlayerUtil.broadcastAction(p -> {
			CachedConfig.SOUND_GAMESTART.playSound(p);
			p.sendMessage(Lang.getMsg(p, "game-prefix") + Lang.getMsg(p, "BroadCast.GameStarted"));

			final PlayerProfile profile = plugin.getProfileManager().getProfile(p.getUniqueId());

			if (!profile.isPlayerAlive())
				return null;

			NMSHandler.getInstance().getNMSControl().clearScatterEffects(p);

			if (!getWhitelist().contains(p.getUniqueId())) {
				getWhitelist().add(p.getUniqueId());
			}

			if (profile.getStatus() == PlayerStatus.SCATTERED) {
				profile.setStatus(PlayerStatus.PLAYING);
			} else {
				ScatterInGame(p, true);
			}

			this.getOnlinePlayers().forEach(p::showPlayer);

			if (Scenarios.SuperHeroes.isOn()) {
				if (listString == null || listEffect == null) {
					listString = effectMap.values().toArray(new String[effectMap.size()]);
					listEffect = effectMap.keySet().toArray(new PotionEffect[effectMap.size()]);
				}
				final int rand = iUHC.getRandom().nextInt(effectMap.size() - 1);
				listEffect[rand].apply(p);
				plugin.getProfileManager().getProfile(p.getUniqueId()).setSuperHeroID((short) rand);
				p.sendMessage(StringUtil.replace(Lang.getMsg(p, "SuperHero-Receive"), "<effect>", Lang.getMsg(p, listString[rand])));
			}

			return null;
		});
	}

	public void deathmatchStart() {
		deathmatchStarted = true;
		GameStatus.set(GameStatus.DEATHMATCH);
		setDeathmatchBorder(CachedConfig.MatchBorderSize);
		final World world = Bukkit.getWorld("UHCArena_deathmatch");
		final Location center = new Location(world, 0, 10, 0);
		for (final Player p : this.plugin.getServer().getOnlinePlayers()) {
			if (Library.getPlayerData(p).isPlayerAlive()) {
				p.teleport(ArenaManager.getInstance().getScatterLocation(world));
			} else {
				p.teleport(center);
			}
		}
		getWhitelist().clear();
		Bukkit.getScheduler().runTaskLater(plugin, () -> Lang.getInstance().broadCastWithSound("BroadCast.DeathMatch", CachedConfig.SOUND_DEATHMATCH), 2L);
	}

	public void checkWin() {

		if (GameStatus.is(GameStatus.FINISH) || plugin.development)
			return;

		if (!TeamManager.getInstance().isTeamsEnabled()) {

			if (plugin.getPlayingFast() == 1) {

				final PlayerProfile sp = plugin.getProfileManager().getLastPlaying();
				final Player winner = sp.getPlayer();
				getWinners().add(winner.getUniqueId());
				InventoryManager.instance.createSnapshot(winner);
				sp.addWins();

				final int ratingChanged = iUHC.getRandom().nextInt(40, 55);
				sp.setElo(sp.getElo() + ratingChanged);
				winner.sendMessage(Lang.getMsg(winner, "eloChanged-Win").replaceAll("<elo>", sp.getElo() + "").replaceAll("<eloChanged>", ratingChanged + ""));

				final String killedString = sp.getKilled().stream().map(UUIDCache::getName).collect(Collectors.joining(", "));

				for (final Player p : plugin.getServer().getOnlinePlayers()) {

					for (final String s : Lang.getInstance().getMessageList(p, "WinnerMessage-SOLO")) {
						p.sendMessage(new CStringBuffer(s).replaceAll("<player>", winner.getName()).replaceAll("<ListKilled>", killedString).toString());
					}

					CachedConfig.SOUND_GAME_END.playSound(p);
				}

				if (CachedConfig.SQL) {
					DataManager.getInstance().saveAllDatas();
				}

				GameStatus.set(GameStatus.FINISH);
				new FireworkTask(winner);
				Bukkit.getPluginManager().callEvent(new GameFinishedEvent(getWinners()));

				for (UUID uuid : getHosts()) {
					Player host = Bukkit.getPlayer(uuid);
					if (host != null) {
						plugin.getItemManager().setSpectatorItems(host);
					}
				}

			} else if (!GameStatus.is(GameStatus.DEATHMATCH) && deathmatch
					&& plugin.getPlayingFast() <= CachedConfig.MatchStartIn && !deathmatchCountdowning) {

				new DeathmatchTask(false);

			}

		} else {

			if (TeamManager.getInstance().getTeamsAlive() == 1) {

				final TeamProfile team = TeamManager.getInstance().getLastTeam();
				final String string = team.getPlayers().stream().map(UUIDCache::getName).collect(Collectors.joining(", "));

				GameStatus.set(GameStatus.FINISH);

				final int ratingChanged = iUHC.getRandom().nextInt(40, 55);

				List<Player> players = new ArrayList<>();

				for (final UUID uuid : team.getPlayers()) {
					final Player player = Bukkit.getPlayer(uuid);

					final PlayerProfile profile = plugin.getProfileManager().getProfile(uuid);
					profile.addWins();
					profile.setElo(profile.getElo() + ratingChanged);

					this.winners.add(uuid);

					if (player != null && profile.isPlayerAlive()) {
						player.sendMessage(Lang.getMsg(player, "eloChanged-Win")
								.replaceAll("<elo>", profile.getElo() + "")
								.replaceAll("<eloChanged>", ratingChanged + ""));
						players.add(player);
					}
				}

				new FireworkTask(players);

				if (CachedConfig.SQL) {
					DataManager.getInstance().saveAllDatas();
				}

				for (final Player player : plugin.getServer().getOnlinePlayers()) {

					for (final String s : Lang.getInstance().getMessageList(player, "WinnerMessage-TEAM")) {
						player.sendMessage(new CStringBuffer(s).replaceAll("<TeamName>", team.getTeamName()).replaceAll("<TeamMembers>", string).toString());
					}

					CachedConfig.SOUND_GAME_END.playSound(player);
				}

				Bukkit.getPluginManager().callEvent(new GameFinishedEvent(getWinners()));

			} else if (!GameStatus.is(GameStatus.DEATHMATCH) && deathmatch
					&& plugin.getPlayingFast() <= CachedConfig.MatchStartIn && !deathmatchCountdowning) {
				new DeathmatchTask(false);
			}
		}
	}

	public String getFormattedTime() {
		return Utils.formatTimeHours(gameCountdowns);
	}

	public void startLobby() {
		if (GameStatus.notStarted() && !isLobbyCountdowning() && !GameStatus.is(GameStatus.TELEPORT)) {
			plugin.log(false, "[DEBUG] Started lobby timer.");
			new LobbyTask();
		}
	}

	public void healAll() {
		Bukkit.getOnlinePlayers().stream()
		.filter(plugin.getProfileManager()::isAlive)
		.forEach(player -> player.setHealth(player.getMaxHealth()));
	}

	public String getGameType() {
		return TeamManager.getInstance().isTeamsEnabled() ? "To" + TeamManager.getInstance().getMaxSize() : "FFA";
	}

	public void ScatterInGame(final Player p, final boolean teleport) {

		if (plugin.development) {
			plugin.log(true, "the player " + p.getName() + " has been scatter in game");
		}

		if (TeamManager.getInstance().isTeamsEnabled()) {

			final TeamProfile team = Library.getTeam(p);

			if (team == null) {
				if (TeamManager.getInstance().getTeamSize() <= 2) {
					TeamManager.getInstance().createTeamBypass(p);
				} else {
					TeamManager.getInstance().createTeam(p);
				}
			}

		}

		this.getOnlinePlayers().forEach(p::showPlayer);

		final PlayerProfile profile = plugin.getProfileManager().getDebuggedProfile(p);
		profile.setPlayerAlive(true);

		if (teleport) {
			ArenaManager.getInstance().scatter(p);
		}
		profile.setStatus(PlayerStatus.PLAYING);

		if (Scenarios.SuperHeroes.isOn()) {

			final int rand = iUHC.getRandom().nextInt(effectMap.size());
			final String name = new ArrayList<>(effectMap.values()).get(rand);
			final PotionEffect effect = new ArrayList<>(effectMap.keySet()).get(rand);

			effect.apply(p);
			p.sendMessage(Lang.getMsg(p, "SuperHero-Receive").replaceAll("<effect>", Lang.getMsg(p, name)));

		}
	}

	public void ScatterAllNotInArena() {
		Bukkit.getWorlds().stream()
		.filter(w -> !w.getName().contains("UHCArena") || w.getName().contains("_practice"))
		.filter(w -> !w.getPlayers().isEmpty())
		.map(World::getPlayers)
		.forEach(players -> players.stream()
				.filter(player -> plugin.getProfileManager()
						.getProfile(player.getUniqueId()).isPlayerAlive())
				.forEach(player -> ScatterInGame(player, true)));
	}

	public void clearMap(final Player p) {

		try {
			plugin.getProfileManager().getDebuggedProfile(p).setPlayerAlive(false);
			plugin.getBarrierManager().remove(p.getUniqueId());
			getPracticePlayers().remove(p.getUniqueId());

			PreConfigGUI.getEditingNames().remove(p.getUniqueId());

			if (GameStatus.started()) {
				if (plugin.getCombatTimer().isCooldown(p.getUniqueId())) {
					plugin.getCombatTimer().clearCooldown(p.getUniqueId());
				}
				if (plugin.getCombatTimer().isCooldown(p.getUniqueId())) {
					plugin.getCombatTimer().clearCooldown(p.getUniqueId());
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
			System.out.println("[ERROR] an error has been exported when clearing player '" + p.getName() + "' data");
		}
	}

	public void setScoreboardTitle(final String title) {
		CachedConfig.title = title;
	}

	public String getScoreboardTitle() {
		return CachedConfig.title;
	}

	public void setCombatTag(final Player p, final String tar) {
		if (!plugin.getCombatTimer().isCooldown(p)) {
			p.sendMessage(Lang.getMsg(p, "combat-prefix") + Lang.getMsg(p, "CombatTag.InCombat").replaceAll("<player>", tar));
		}
		plugin.getCombatTimer().setCooldown(p, p.getUniqueId());
	}

	public void removeCombatTag(final Player p) {
		if (plugin.getCombatTimer().isCooldown(p)) {
			p.sendMessage(Lang.getMsg(p, "combat-prefix") + Lang.getMsg(p, "CombatTag.NoLongerCombat"));
		}
	}

	public boolean containBlockedCMD(final Player p, String cmd) {

		cmd = cmd.toUpperCase();

		for (final String str2 : CachedConfig.BLOCKED_CMDS) {
			if (str2.toUpperCase().contains(cmd))
				return true;
		}

		if (GameStatus.started() && plugin.getCombatTimer().isCooldown(p)) {
			for (final String str2 : CachedConfig.COMBAT_BLOCKED_CMDS)
				if (str2.toUpperCase().contains(cmd))
					return true;
		}

		if (plugin.getProfileManager().isFrozen(p)) {
			for (final String str2 : CachedConfig.FROZEN_BLOCKED_CMDS)
				if (str2.toUpperCase().contains(cmd))
					return true;
		}


		return false;
	}

	public void updateMotd() {

		String motd = "";
		switch (GameStatus.get()) {
		case LOADING:
			motd = CachedConfig.MOTD_INSETUP;
			break;
		case WAITING:
			if(isWhitelisted()) {
				motd = CachedConfig.MOTD_WHITELISTED;
				break;
			}
			motd = CachedConfig.MOTD_LOBBY;
			break;
		case TELEPORT:
			motd = CachedConfig.MOTD_STARTING;
			break;
		case PVE:
		case PVP:
		case DEATHMATCH:
			motd = CachedConfig.MOTD_INGAME;
			break;
		case FINISH:
			motd = CachedConfig.MOTD_FINISHING;
			break;
		}
		NMSHandler.getInstance().getNMSControl().setMotd(motd);
	}

	public void memoryFree() {
		listString = null;
		listEffect = null;
	}

}
