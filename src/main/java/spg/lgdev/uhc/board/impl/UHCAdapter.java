package spg.lgdev.uhc.board.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import spg.lgdev.uhc.iUHC;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import spg.lgdev.uhc.board.Board;
import spg.lgdev.uhc.board.BoardAdapter;
import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.manager.TeamManager;
import spg.lgdev.uhc.player.PlayerProfile;
import spg.lgdev.uhc.scenario.Scenarios;

public class UHCAdapter implements BoardAdapter {

	private final iUHC plugin;
	private final String scenarios;

	public UHCAdapter(final iUHC plugin) {
		this.plugin = plugin;
		this.scenarios = plugin.getFileManager().getScoreboards().getString("UHC-Scoreboard.scenario-format");
	}

	@Override
	public String getTitle(final Player player) {
		return UHCGame.getInstance().getScoreboardTitle();
	}

	@Override
	public List<String> getScoreboard(final Player p, final Board board) {

		List<String> linesOld;
		final PlayerProfile profile = plugin.getProfileManager().getProfile(p.getUniqueId());

		if (GameStatus.is(GameStatus.LOADING)) {
			linesOld = Lang.getInstance().getSidebarList(p, "chunkLoading");
		} else if (GameStatus.is(GameStatus.WAITING)) {
			linesOld = Lang.getInstance().getSidebarList(p, "lobby");
		} else if (GameStatus.is(GameStatus.TELEPORT)) {
			linesOld = Lang.getInstance().getSidebarList(p, "teleport");
		} else if (GameStatus.is(GameStatus.PVE) || GameStatus.is(GameStatus.PVP)) {
			if (!profile.isPlayerAlive()) {
				linesOld = Lang.getInstance().getSidebarList(p, "spectator");
			} else if (TeamManager.getInstance().isTeamsEnabled()) {
				linesOld = Lang.getInstance().getSidebarList(p, "team-ingame");
			} else {
				linesOld = Lang.getInstance().getSidebarList(p, "solo-ingame");
			}
		} else if (GameStatus.is(GameStatus.DEATHMATCH)) {
			if (!profile.isPlayerAlive()) {
				linesOld = Lang.getInstance().getSidebarList(p, "deathmatch-spectator");
			} else if (TeamManager.getInstance().isTeamsEnabled()) {
				linesOld = Lang.getInstance().getSidebarList(p, "team-deathmatch");
			} else {
				linesOld = Lang.getInstance().getSidebarList(p, "solo-deathmatch");
			}
		} else {
			linesOld = Lang.getInstance().getSidebarList(p, "Finish");
		}

		final List<String> lines = new LinkedList<>();

		if (UHCGame.getInstance().isMod(p.getUniqueId())) {
			Lang.getInstance().getSidebarList(p, "Staff-formats").forEach(s -> lines.add(plugin.getSidebarManager().getPlaceholders().doSidebarPlaceholders2(p, s)));
		}

		for (String s : linesOld) {

			s = plugin.getSidebarManager().getPlaceholders().doSidebarPlaceholders(p, s);

			if (s.contains("[display=false]") || s.contains("[display=!true]")) {
				continue;
			}

			if (s.equals("<NoClean-Format>")) {

				if (plugin.getProfileManager().isNoClean(p)) {
					lines.add(plugin.getSidebarManager().getPlaceholders().doSidebarPlaceholders(p, Lang.getInstance().getSidebarLine(p, "noClean-format")));
				}
				continue;

			} else if (s.equals("<deathmatch-Format>")) {

				if (UHCGame.getInstance().isDeathmatchCountdowning()) {
					lines.add(plugin.getSidebarManager().getPlaceholders().doSidebarPlaceholders(p, Lang.getInstance().getSidebarLine(p, "deathMatch-Format")));
				}
				continue;

			} else if (s.equals("<winners>")) {

				for (final UUID u : UHCGame.getInstance().getWinners()) {
					Lang.getInstance().getSidebarList(p, "Winner-formats").forEach(line -> lines.add(plugin.getSidebarManager().getPlaceholders().doSidebarPlaceholders3(u, line)));
				}
				continue;

			} else if (s.equals("<scenarios>")) {

				if (!Scenarios.getScenariosList().isEmpty()) {
					new ArrayList<>(Scenarios.getScenariosList()).forEach(scen -> lines.add(plugin.getSidebarManager().getPlaceholders().doSidebarPlaceholders(p, scenarios.replaceAll("<Scenario>", scen))));
				} else {
					lines.add(plugin.getSidebarManager().getPlaceholders().doSidebarPlaceholders(p, scenarios.replaceAll("<Scenario>", "null")));
				}
				continue;

			}

			lines.add(s);
			continue;
		}

		return lines;
	}

	@Override
	public long getInterval() {
		return CachedConfig.PerformanceMode ? 10L : 2L;
	}

	@Override
	public void onScoreboardCreate(final Player player, final Scoreboard board) {}

	@Override
	public void preLoop() {}

}
