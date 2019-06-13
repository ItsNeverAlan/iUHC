package spg.lgdev.uhc.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import spg.lgdev.uhc.board.Board;
import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.handler.Library;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.manager.TeamManager;
import spg.lgdev.uhc.player.PlayerProfile;
import spg.lgdev.uhc.player.TeamProfile;
import spg.lgdev.uhc.util.reflection.ReflectionUtils;

public class NameTagUtil extends ReflectionUtils {

	private NameTagUtil() {}

	public static void send(final List<Player> targets, final String prefix, final List<Player> players) {

		for (final Player player : players) {

			final Board board = iUHC.getInstance().getSidebarManager().getPlayerBoards().get(player.getUniqueId());

			final Team team = board.getPrefixs().get(prefix);

			for (final Player target : targets) {

				clearFromTeams(board, target);

				if (!team.hasEntry(target.getName())) {
					team.addEntry(target.getName());
				}
			}
		}
	}

	public static void clearFromTeams(final Player player, final Player target) {
		final Board board = iUHC.getInstance().getSidebarManager().getPlayerBoards().get(target.getUniqueId());
		clearFromTeams(board, target);
	}

	public static void clearFromTeams(final Board board, final Player target) {
		for (final Team team : board.getPrefixs().values()) {
			team.removeEntry(target.getName());
		}
	}

	public static void clearFromEveryone(final Player player) {
		Bukkit.getScheduler().runTaskAsynchronously(iUHC.getInstance(), () -> {
			for (final Board board : iUHC.getInstance().getSidebarManager().getPlayerBoards().values()) {
				clearFromTeams(board, player);
			}
		});
	}

	public static void updateTags(final Player p) {

		final PlayerProfile profile = Library.getPlayerData(p.getUniqueId());

		final TeamManager manager = TeamManager.getInstance();
		final TeamProfile ownTeam = profile.getTeam();

		final List<Player> staffs = new ArrayList<>();
		final List<Player> enemy = new ArrayList<>();
		final List<Player> members = new ArrayList<>();

		for (final Player pl : iUHC.getInstance().getServer().getOnlinePlayers()) {

			if (UHCGame.getInstance().isMod(pl.getUniqueId())) {
				staffs.add(pl);
				continue;
			}

			if (pl == p) {
				members.add(p);
				continue;
			}

			if (manager.isTeamsEnabled()) {
				final TeamProfile currentTeam = Library.getTeam(pl);
				if (ownTeam != null && currentTeam != null) {
					if (ownTeam.equals(currentTeam)) {
						members.add(pl);
					} else {
						enemy.add(pl);
					}
				} else {
					enemy.add(pl);
				}
				continue;
			} else {
				enemy.add(pl);
				continue;
			}

		}

		send(members, CachedConfig.TEAMMATE_TAG, Arrays.asList(p));
		send(enemy, CachedConfig.ENEMY_TAG, Arrays.asList(p));
		send(staffs, CachedConfig.STAFF_TAG, Arrays.asList(p));

	}

	public static void updateTags(final Player p, final Player target) {
		final TeamManager manager = TeamManager.getInstance();

		if (UHCGame.getInstance().isMod(target.getUniqueId())) {
			send(Arrays.asList(target), CachedConfig.STAFF_TAG, Arrays.asList(p));
			return;
		}

		if (p.equals(target)) {
			send(Arrays.asList(target), CachedConfig.TEAMMATE_TAG, Arrays.asList(p));
			return;
		}

		final TeamProfile team = Library.getTeam(p);

		if (manager.isTeamsEnabled()) {
			if (team != null
					&& Library.getTeam(target) != null
					&& team.getPlayers().contains(target.getUniqueId())) {
				send(Arrays.asList(target), CachedConfig.TEAMMATE_TAG, Arrays.asList(p));
			} else {
				send(Arrays.asList(target), CachedConfig.ENEMY_TAG, Arrays.asList(p));
			}
			return;
		} else {
			send(Arrays.asList(target), CachedConfig.ENEMY_TAG, Arrays.asList(p));
			return;
		}
	}

	public static void updateAllTags() {
		Bukkit.getScheduler().runTaskAsynchronously(iUHC.getInstance(), () -> iUHC.getInstance().getServer().getOnlinePlayers().forEach(NameTagUtil::updateTags));
	}

	public static void updateAllTags(final Player target) {
		Bukkit.getScheduler().runTaskAsynchronously(iUHC.getInstance(), () -> iUHC.getInstance().getServer().getOnlinePlayers().forEach(p -> updateTags(p, target)));
	}

}
