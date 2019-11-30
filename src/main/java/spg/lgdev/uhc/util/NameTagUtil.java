package spg.lgdev.uhc.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.handler.Library;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.manager.TeamManager;
import spg.lgdev.uhc.player.PlayerProfile;
import spg.lgdev.uhc.player.TeamProfile;
import spg.lgdev.uhc.util.reflection.ReflectionUtils;

public class NameTagUtil extends ReflectionUtils {

	private NameTagUtil() {}

	private static final String PREFIX = "nt_team_";
	private static final String[] COLORS = new String[]{
			CachedConfig.TEAMMATE_TAG,
			CachedConfig.ENEMY_TAG,
			CachedConfig.STAFF_TAG
	};

	private static String getTeamName(String team) {
		return PREFIX + team;
	}

	public static void setup(Player player) {
		Scoreboard scoreboard = player.getScoreboard();

		if (scoreboard.equals(Bukkit.getServer().getScoreboardManager().getMainScoreboard())) {
			scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
		}

		for (String color : COLORS) {
			String teamName = getTeamName(color);
			Team team = scoreboard.getTeam(teamName);

			if (team == null) {
				team = scoreboard.registerNewTeam(teamName);
			}

			team.setPrefix(color);

			Iterator<String> entryIterator = team.getEntries().iterator();

			while (entryIterator.hasNext()) {
				entryIterator.remove();
			}
		}

		Objective objective = scoreboard.registerNewObjective("health", "health");
		objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
		objective.setDisplayName("§c" + StringEscapeUtils.unescapeJava("\u2764"));

		objective = scoreboard.registerNewObjective("health_tab", "health");
		objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);

		player.setScoreboard(scoreboard);
	}

	public static void send(final List<Player> targets, final String prefix, final List<Player> players) {

		for (final Player player : players) {

			Team team = player.getScoreboard().getTeam(getTeamName(prefix));

			if (team == null) {
				team = player.getScoreboard().registerNewTeam(getTeamName(prefix));

				team.setPrefix(prefix);
			}

			for (final Player target : targets) {

				clearFromTeams(player.getScoreboard(), target);

				if (!team.hasEntry(target.getName())) {
					team.addEntry(target.getName());
				}
			}
		}
	}

	public static void clearFromTeams(final Player player, final Player target) {
		clearFromTeams(player.getScoreboard(), target);
	}

	public static void clearFromTeams(Scoreboard scoreboard, final Player target) {
		for (final String prefix : COLORS) {
			Team team = scoreboard.getTeam(prefix);

			if (team == null) {
				continue;
			}

			team.removeEntry(target.getName());
		}
	}

	public static void clearFromEveryone(final Player player) {
		Bukkit.getScheduler().runTaskAsynchronously(iUHC.getInstance(), () -> {
			for (final Player player1 : Bukkit.getOnlinePlayers()) {
				clearFromTeams(player1, player);
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
