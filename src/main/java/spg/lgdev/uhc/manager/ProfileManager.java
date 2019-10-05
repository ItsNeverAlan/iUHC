package spg.lgdev.uhc.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.Getter;
import spg.lgdev.uhc.command.player.MLGCommand;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.enums.PlayerStatus;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.Library;
import spg.lgdev.uhc.handler.Permissions;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.player.PlayerData;
import spg.lgdev.uhc.player.PlayerOptions;
import spg.lgdev.uhc.player.PlayerProfile;
import spg.lgdev.uhc.player.TeamProfile;
import spg.lgdev.uhc.player.rating.RatingChangeReason;
import spg.lgdev.uhc.player.rating.RatingHistory;
import spg.lgdev.uhc.util.StringUtil;
import spg.lgdev.uhc.util.NameTagUtil;

public class ProfileManager {

	private final iUHC plugin;
	private final UHCGame game = UHCGame.getInstance();

	public ProfileManager(final iUHC plugin) {
		this.plugin = plugin;
	}

	@Getter
	private final Map<UUID, PlayerProfile> profiles = new HashMap<>();

	public void setSpectator(final Player player, final boolean lateHide) {

		game.getMods().remove(player.getUniqueId());

		MLGCommand.InMLG.remove(player.getUniqueId());

		final PlayerProfile profile = getProfile(player.getUniqueId());
		final PlayerOptions core = profile.getOptions();

		profile.setPlayerAlive(false);

		core.setFly(true);
		core.setSpeedLevel("normal");
		core.setNightVision(false);
		core.setHideSpectators(false);

		game.clear(player, GameMode.SURVIVAL);

		final Runnable runnable = () -> {

			game.getPlayersUUID().forEach(u -> {

				final Player p = Bukkit.getPlayer(u);
				if (p != null) {
					p.hidePlayer(player);
				}

			});

			game.getOnlineSpectators().forEach(spec -> {
				spec.hidePlayer(player);
			});

			for (final UUID uuids : game.getMods()) {
				final Player mods = Bukkit.getPlayer(uuids);
				if (mods == null) {
					continue;
				}
				mods.hidePlayer(player);
				if (!getProfile(uuids).getOptions().isHideSpectators()) {
					mods.showPlayer(player);
				} else {
					mods.hidePlayer(player);
				}
			}

			player.setHealth(20.0D);
			((CraftPlayer) player).getHandle().getDataWatcher().watch(6, 20.0F);
			player.setAllowFlight(true);
			player.setFlying(true);
		};

		if (lateHide) {

			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, runnable, 20L);

		} else {

			runnable.run();

		}

		player.sendMessage("  ");
		player.sendMessage("§7Now you are spectator");
		player.sendMessage("");
		player.sendMessage("§ffor teleporting more fast, use: §e/tele <player>§f.");
		player.sendMessage(" ");

		plugin.getItemManager().setSpectatorItems(player);

		if (game.isDeathKick() && !player.hasPermission(Permissions.DEATHKICK_BYPASS)) {

			Bukkit.getScheduler().runTaskLater(plugin, () -> {

				if (player == null
						|| !player.isOnline()
						|| !getProfile(player.getUniqueId()).isSpectator())
					return;

				player.kickPlayer(Lang.getMsg(player, "DeathKick.KickMessage").replaceAll("<Player>", player.getName()));
				for (final Player player2 : plugin.getServer().getOnlinePlayers()) {
					player2.sendMessage(Lang.getMsg(player2, "DeathKick.KickBroadcast").replaceAll("<Player>", player.getName()));
				}
				return;

			}, 600l);

		}

		NameTagUtil.updateAllTags(player);

	}

	public void lateScatter(final Player player) {

		final PlayerProfile profile = getProfile(player.getUniqueId());
		profile.setPlayerAlive(true);
		profile.setLoggerDied(false);
		profile.setStatus(PlayerStatus.PLAYING);

		UHCGame.getInstance().clear(player, GameMode.SURVIVAL);

		player.getActivePotionEffects().stream().forEach(potionEffect -> {

			player.removePotionEffect(potionEffect.getType());

		});

		if (!game.getWhitelist().contains(player.getUniqueId())) {
			game.getWhitelist().add(player.getUniqueId());
		}

		game.getMods().remove(player.getUniqueId());

		for (final Player player2 : game.getOnlinePlayers()) {

			if (player2 != player) {
				player2.showPlayer(player);
				player.showPlayer(player2);
			}

		}

		for (final Player player2 : game.getOnlineSpectators()) {

			player.hidePlayer(player2);
			player2.showPlayer(player);

		}

		Player p;
		for (final UUID uuid : game.getMods()) {

			p = Bukkit.getPlayer(uuid);
			if (p == null) {
				continue;
			}
			player.hidePlayer(p);
			p.showPlayer(player);

		}

		new PlayerData().restore(player, false);
		player.updateInventory();

		player.sendMessage("         ");
		player.sendMessage("    ");
		player.sendMessage("§aLate scatter seccuss!");

		NameTagUtil.updateAllTags(player);

	}

	public void setRespawn(final Player player, final CommandSender sender) {

		final PlayerProfile profile = getProfile(player.getUniqueId());

		if (profile.getData() == null) {
			if (sender != null) {
				sender.sendMessage("§cdidnt find respawn data on this player!");
			}
			return;
		}

		profile.setPlayerAlive(true);
		profile.setLoggerDied(false);
		for (final RatingHistory ratingHistory : new ArrayList<>(profile.getRatingHistories())) {
			if (ratingHistory.getReason() == RatingChangeReason.DEATH) {
				profile.setElo(Math.abs(ratingHistory.getRatingChanged()));
				profile.removeRatingHistory(ratingHistory);
				player.sendMessage("§Elo: " + profile.getElo() + " §a+" + ratingHistory.getRatingChanged());
			}
		}

		UHCGame.getInstance().clear(player, GameMode.SURVIVAL);

		player.getActivePotionEffects().stream().forEach(potionEffect -> {

			player.removePotionEffect(potionEffect.getType());

		});

		if (!game.getWhitelist().contains(player.getUniqueId())) {
			game.getWhitelist().add(player.getUniqueId());
		}

		game.getMods().remove(player.getUniqueId());

		for (final Player player2 : game.getOnlinePlayers()) {

			if (player2 != player) {
				player2.showPlayer(player);
				player.showPlayer(player2);
			}

		}

		for (final Player player2 : game.getOnlineSpectators()) {

			player.hidePlayer(player2);
			player2.showPlayer(player);

		}

		Player p;
		for (final UUID uuid : game.getMods()) {

			p = Bukkit.getPlayer(uuid);
			if (p == null) {
				continue;
			}
			player.hidePlayer(p);
			p.showPlayer(player);

		}

		profile.getData().restore(player, false);
		player.updateInventory();

		player.sendMessage("         ");
		player.sendMessage("    ");
		player.sendMessage(Lang.getMsg(player, "prefix") + Lang.getMsg(player, "Respawn"));

		NameTagUtil.updateAllTags(player);

	}

	public void setMod(final Player player) {

		final PlayerProfile profile = getProfile(player.getUniqueId());
		profile.setPlayerAlive(false);

		final PlayerOptions core = profile.getOptions();

		core.setNotifyDiamond(true);
		core.setNotifyGold(false);
		core.setNotifySpawner(false);

		core.setHideSpectators(false);

		game.clear(player, GameMode.CREATIVE);

		player.setAllowFlight(true);
		player.setFlying(true);

		if (!game.isMod(player.getUniqueId())) {
			game.getMods().add(player.getUniqueId());
		}

		for (final Player player2 : game.getOnlinePlayers()) {

			player2.hidePlayer(player);

		}

		for (final Player player2 : game.getOnlineSpectators()) {

			if (core.isHideSpectators()) {
				player.hidePlayer(player2);
			} else {
				player.showPlayer(player2);
			}

			player2.hidePlayer(player);

		}

		Player player2;

		for (final UUID uuid : game.getMods()) {

			player2 = Bukkit.getPlayer(uuid);
			if (player2 == null || player2 == player) {
				continue;
			}

			if (core.isHideStaffs()) {
				player.hidePlayer(player2);
			} else {
				player.showPlayer(player2);
			}

			if (getProfile(player2.getUniqueId()).getOptions().isHideStaffs()) {
				player2.hidePlayer(player);
			} else {
				player2.showPlayer(player);
			}

		}

		for (final String m : Lang.getInstance().getMessageList(player, "Staff.SetStaff")) {
			player.sendMessage(m);
		}

		plugin.getItemManager().setSpectatorItems(player);
		NameTagUtil.updateAllTags(player);

	}

	public PlayerProfile getLastPlaying() {
		return profiles.values().stream().filter(profile -> profile.isOnline() && profile.isPlayerAlive()).findFirst().orElse(null);
	}

	public boolean isAlive(final Player player) {
		return getProfile(player.getUniqueId()).isPlayerAlive();
	}

	public boolean isFrozen(final Player player) {
		return getProfile(player.getUniqueId()).isFrozen();
	}

	public void setHost(final Player target) {

		if (!game.isHost(target.getUniqueId())) {
			game.getHosts().add(target.getUniqueId());
		}
		UHCGame.getInstance().setHostName(target.getName());

		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			if (game.isHost(target.getUniqueId()) && GameStatus.notStarted() && !GameStatus.is(GameStatus.TELEPORT)
					&& !UHCGame.getInstance().getPracticePlayers().contains(target.getUniqueId())) {
				iUHC.getInstance().getItemManager().setSpawnItems(target);
			}
		}, 1L);
	}

	public void joinSet(final Player p) {

		if (game.getMods().contains(p.getUniqueId())) {
			setMod(p);
		}

		if (p.hasPermission(Permissions.ADMIN)
				&& game.getHosts().size() == 0
				&& !game.isHost(p.getUniqueId())) {
			setHost(p);
			game.setHostName(p.getName());
		}
	}

	public void setStatusToAll(final PlayerStatus playerStatus) {
		Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).map(this::getProfile)
		.forEach(profile -> profile.setStatus(playerStatus));
	}

	public PlayerProfile getDebuggedProfile(final Player p) {
		if (!doesProfileExsists(p.getUniqueId())) {
			createProfile(p);
		}
		return getProfile(p.getUniqueId());
	}

	public PlayerProfile getProfile(final UUID uUID) {
		return this.profiles.get(uUID);
	}

	public boolean doesProfileExsists(final UUID uUID) {
		return this.profiles.containsKey(uUID);
	}

	public void createProfile(final Player p) {
		this.profiles.put(p.getUniqueId(), new PlayerProfile(p.getUniqueId(), p.getName()));
	}

	public boolean isNoClean(final Player p) {
		return getProfile(p.getUniqueId()).isNoClean();
	}

	public Set<PlayerProfile> getProfileSet() {
		return new HashSet<>(profiles.values());
	}

	public Set<PlayerProfile> profileSet(final Set<UUID> uuids) {
		return uuids.stream().map(this::getProfile).collect(Collectors.toSet());
	}

	public void broadRules() {

		game.setBroadcastingRules(true);
		final List<String> lines = plugin.getFileManager().getConfig().getStringList("BroadRules.RuleList");

		new BukkitRunnable() {
			private int i = 0;

			@Override
			public void run() {

				if (i == lines.size() - 1) {

					game.setBroadcastingRules(false);
					cancel();

				} else {

					Bukkit.broadcastMessage(StringUtil.cc(lines.get(i)));
					++i;

				}

			}

		}.runTaskTimer(plugin, 0L, 5 * 20L);

	}

	public String getTeamKills(final Player p) {

		final TeamProfile team = Library.getTeam(p);
		return team == null ? "0" : team.getKills() + "";

	}

	public void setNoLateScatter() {
		for (final PlayerProfile profile : getProfileSet()) {
			if (profile.isOnline()) {
				profile.setLateScatter(false);
			}
		}
	}

}
