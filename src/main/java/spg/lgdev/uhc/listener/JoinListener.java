package spg.lgdev.uhc.listener;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerInitialSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerJoinedEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import spg.lgdev.uhc.board.Board;
import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.enums.PlayerStatus;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.Permissions;
import spg.lgdev.uhc.handler.game.Loggers;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.manager.ProfileManager;
import spg.lgdev.uhc.manager.TeamManager;
import spg.lgdev.uhc.player.PlayerProfile;
import spg.lgdev.uhc.scenario.Scenarios;
import spg.lgdev.uhc.util.FastUUID;
import spg.lgdev.uhc.util.StringUtil;
import spg.lgdev.uhc.util.NameTagUtil;

public class JoinListener implements org.bukkit.event.Listener {

	private final iUHC plugin;
	private final ProfileManager pm;
	private final UHCGame game;

	public JoinListener(final iUHC pl) {
		this.plugin = pl;
		this.pm = iUHC.getInstance().getProfileManager();
		this.game = UHCGame.getInstance();
	}

	public String Colored(final String string) {
		return StringUtil.cc(string);
	}

	public void hideSpectators(final Player p, final boolean alive) {

		if (GameStatus.started()) {
			for (final Player player : game.getOnlinePlayers()) {
				p.showPlayer(player);
				if (alive) {
					player.showPlayer(p);
				}
			}
		}

		plugin.getNotInGame().stream().filter(spec -> spec != null && !spec.equals(p)).forEach(p::hidePlayer);
	}

	@EventHandler
	public void onAsyncPlayerPreLogin(final AsyncPlayerPreLoginEvent e) {
		if (GameStatus.is(GameStatus.TELEPORT)) {
			e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, CachedConfig.LOGIN_INGAME);
			return;
		}
	}

	@EventHandler
	public void onPlayerLogin(final PlayerLoginEvent e) {
		final Player p = e.getPlayer();

		if (GameStatus.is(GameStatus.LOADING)) {
			if (!p.hasPermission(Permissions.ADMIN)) {
				e.disallow(PlayerLoginEvent.Result.KICK_OTHER, CachedConfig.LOGIN_LOADING);
				return;
			}
		}

		if (plugin.getPlaying() > game.getMaxplayers() && !p.hasPermission(Permissions.FULL_BYPASS)
				&& !p.hasPermission(Permissions.ADMIN)) {
			e.disallow(PlayerLoginEvent.Result.KICK_OTHER, CachedConfig.LOGIN_FULL);
			return;
		}

		if (GameStatus.is(GameStatus.WAITING)) {
			if (game.isWhitelisted() && !game.getWhitelist().contains(p.getUniqueId())
					&& !game.getPlayerWhitelists().contains(p.getName()) && !p.hasPermission(Permissions.ADMIN)
					&& !p.hasPermission(Permissions.WHITELIST_BYPASS)) {
				e.disallow(PlayerLoginEvent.Result.KICK_OTHER, CachedConfig.LOGIN_WHITELIST);
				return;
			}
		}

		if (GameStatus.is(GameStatus.TELEPORT)) {
			e.disallow(PlayerLoginEvent.Result.KICK_OTHER, CachedConfig.LOGIN_INGAME);
			return;
		}

		if (GameStatus.started()) {
			if (GameStatus.is(GameStatus.DEATHMATCH) && !game.getPlayerWhitelists().contains(p.getName())
					&& !p.hasPermission(Permissions.ADMIN)
					&& !p.hasPermission(Permissions.getSpectate(game.getBorderRadius()))) {
				e.disallow(PlayerLoginEvent.Result.KICK_OTHER, CachedConfig.LOGIN_INGAME);
				return;

			} else if (GameStatus.is(GameStatus.FINISH) && !game.getPlayerWhitelists().contains(p.getName())
					&& !p.hasPermission(Permissions.ADMIN)
					&& !p.hasPermission(Permissions.getSpectate(game.getBorderRadius()))) {

				e.disallow(PlayerLoginEvent.Result.KICK_OTHER, CachedConfig.LOGIN_FINISH);
				return;

			} else if (!game.getPlayerWhitelists().contains(p.getName())
					&& !game.getWhitelist().contains(p.getUniqueId())
					&& !p.hasPermission(Permissions.getSpectate(game.getBorderRadius()))) {
				e.disallow(PlayerLoginEvent.Result.KICK_OTHER, CachedConfig.LOGIN_INGAME);
				return;
			}
		}

		e.allow();

	}

	@EventHandler
	public void onPlayerInitialSpawn(final PlayerInitialSpawnEvent event) {

		if (GameStatus.notStarted()) {

			event.setSpawnLocation(game.getSpawnPoint().toBukkitLocation());

		} else {

			event.setSpawnLocation(new Location(Bukkit.getWorld("UHCArena" + (game.isDeathmatchStarted() ? "_deathmatch" : "")), 0, 100, 0));

		}

	}

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
		event.setJoinMessage(null);
	}

	@EventHandler
	public void onPlayerJoined(final PlayerJoinedEvent e) {

		final Player p = e.getPlayer();
		final PlayerProfile profile = pm.getDebuggedProfile(p);

		profile.setName(p.getName());
		profile.setOnline(true);

		pm.joinSet(p);
		plugin.getSidebarManager().getPlayerBoards().put(p.getUniqueId(), new Board(plugin, p, plugin.getSidebarManager().getAdapter()));

		p.setWalkSpeed(0.2f);

		if (GameStatus.notStarted()) {

			game.lobby(e.getPlayer(), true);
			profile.setPlayerAlive(true);
			for (final String msg : Lang.getInstance().getMessageList(p, "join-messages")) {
				p.sendMessage(StringUtil.replace(msg, new StringUtil.ReplaceValue("<online>", "" + plugin.getPlaying()),
						new StringUtil.ReplaceValue("<max>", game.getMaxplayers() + ""),
						new StringUtil.ReplaceValue("<type>", game.getGameType()),
						new StringUtil.ReplaceValue("<scenarios>", Scenarios.getScenariosString())));
			}

		} else {

			if (plugin.getDisconnectTimer().isCooldown(p.getUniqueId())) {
				plugin.getDisconnectTimer().clearCooldown(p.getUniqueId());
			}

			if (!GameStatus.is(GameStatus.FINISH)) {

				if (TeamManager.getInstance().isTeamsEnabled() && profile.getTeam() == null) {
					TeamManager.getInstance().createTeam(p);
				}

				if (game.getWhitelist().contains(p.getUniqueId()) && !game.isMod(p.getUniqueId())) {

					if (game.getOfflineRespawns().contains(FastUUID.toString(p.getUniqueId()))) {
						pm.setRespawn(e.getPlayer(), null);
						game.getOfflineRespawns().remove(FastUUID.toString(p.getUniqueId()));
					}

					profile.setPlayerAlive(true);

					if (profile.isLoggerDied()) {

						pm.setSpectator(p, false);
						profile.setLoggerDied(false);
						game.getWhitelist().remove(p.getUniqueId());

					} else {

						if (profile.getData() != null) {
							profile.getData().restore(e.getPlayer(), !Loggers.getInstance().removeEntity(e.getPlayer()));
						} else {
							System.out.println("playerdata for " + p.getName() + " doesn't exists");
						}

						if (profile.getStatus() == PlayerStatus.SCATTERING || profile.getStatus() == PlayerStatus.LOBBY) {
							game.ScatterInGame(e.getPlayer(), true);
						}

					}

				} else {

					if (!game.isMod(p.getUniqueId())) {
						pm.setSpectator(p, false);
					}

					if (game.getOfflineKicks().contains(p.getName())) {
						p.sendMessage(Lang.getMsg(p, "LeaveTooLong"));
					}

					p.teleport(new Location(Bukkit.getWorld("UHCArena"), 0, 100, 0));
				}

				if (CachedConfig.BoardEnabled) {
					NameTagUtil.updateTags(p);
					NameTagUtil.updateAllTags(p);
				}

			} else {

				if (!game.isMod(p.getUniqueId())) {
					p.teleport(new Location(Bukkit.getWorld("UHCArena"), 0, 100, 0));
				}

			}

		}

		this.hideSpectators(e.getPlayer(), profile.isPlayerAlive());

	}

}
