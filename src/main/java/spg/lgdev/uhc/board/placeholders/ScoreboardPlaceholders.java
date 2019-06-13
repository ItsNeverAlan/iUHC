package spg.lgdev.uhc.board.placeholders;

import java.util.UUID;

import spg.lgdev.uhc.iUHC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.handler.impl.CombatTimer;
import spg.lgdev.uhc.handler.impl.NocleanTimer;
import spg.lgdev.uhc.manager.TeamManager;
import spg.lgdev.uhc.player.PlayerProfile;
import spg.lgdev.uhc.scenario.Scenarios;
import spg.lgdev.uhc.task.ReleaseTask;
import spg.lgdev.uhc.util.RuntimeUtil;
import spg.lgdev.uhc.util.StringUtil;
import spg.lgdev.uhc.util.Utils;
import net.development.mitw.Mitw;

public class ScoreboardPlaceholders {

	private final iUHC plugin;

	private final String untilShrink;

	public ScoreboardPlaceholders(final iUHC plugin) {

		this.plugin = plugin;

		untilShrink = plugin.getFileManager().getScoreboards().getString("UHC-Scoreboard.border-format", "&7| &c<BorderLeft>");
	}

	public String doSidebarPlaceholders(final Player p, String line) {
		final PlayerProfile profile = plugin.getProfileManager().getDebuggedProfile(p);

		if (line.contains("<is_countdown>")) {
			line = StringUtil.replace(line, "<is_countdown>", UHCGame.getInstance().isLobbyCountdowning());
		}

		if (line.contains("<is_noClean>")) {
			line = StringUtil.replace(line, "<is_noClean>", profile.isNoClean());
		}

		if (line.contains("<is_combatTag>")) {
			if (plugin.getCombatTimer() != null) {
				line = StringUtil.replace(line, "<is_combatTag>", plugin.getCombatTimer().isCooldown(p));
			} else {
				line = StringUtil.replace(line, "<is_combatTag>", "false");
			}
		}

		if (line.contains("<is_scenarios>")) {
			if (Scenarios.getScenariosList().isEmpty()) {
				line = StringUtil.replace(line, "<is_scenarios>", "None");
			} else {
				line = StringUtil.replace(line, "<is_scenarios>", "");
			}
		}

		if (GameStatus.is(GameStatus.LOADING)) {

			if (line.contains("<hostName>")) {
				line = StringUtil.replace(line, "<hostName>", UHCGame.getInstance().getHostName());
			}

			if (line.contains("<chunkLoaded>")) {
				if (plugin.getChunkManager().isLoading()) {
					line = StringUtil.replace(line, "<chunkLoaded>", plugin.getChunkManager().getChunkCompleted() + "");
				} else {
					line = StringUtil.replace(line, "<chunkLoaded>", "0");
				}
			}

			if (line.contains("<chunkTotal>")) {
				if (plugin.getChunkManager().isLoading()) {
					line = StringUtil.replace(line, "<chunkTotal>", plugin.getChunkManager().getChunkTotal() + "");
				} else {
					line = StringUtil.replace(line, "<chunkTotal>", "0");
				}
			}

			if (line.contains("<Percent>")) {
				if (plugin.getChunkManager().isLoading()) {
					line = StringUtil.replace(line, "<Percent>", CachedConfig.FORMAT.format(plugin.getChunkManager().getPercentageCompleted()) + "%");
				} else {
					line = StringUtil.replace(line, "<Percent>", "0.0%");
				}
			}

			if (line.contains("<TPS>")) {
				line = StringUtil.replace(line, "<TPS>", CachedConfig.FORMAT.format(RuntimeUtil.getTPS(0)));
			}
			if (line.contains("<freeMemory>")) {
				line = StringUtil.replace(line, "<freeMemory>", (int) ((Runtime.getRuntime().maxMemory()
						- Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory()) / 1048576) + "MB");
			}
			if (line.contains("<type>")) {
				line = StringUtil.replace(line, "<type>", UHCGame.getInstance().getGameType());
			}
			if (line.contains("<players>")) {
				line = StringUtil.replace(line, "<players>", "" + plugin.getPlaying());
			}
			if (line.contains("<max>")) {
				line = StringUtil.replace(line, "<max>", "" + UHCGame.getInstance().getMaxplayers());
			}
		} else if (GameStatus.is(GameStatus.WAITING)) {
			if (line.contains("<hostName>")) {
				line = StringUtil.replace(line, "<hostName>", UHCGame.getInstance().getHostName());
			}
			if (line.contains("<type>")) {
				line = StringUtil.replace(line, "<type>", UHCGame.getInstance().getGameType());
			}
			if (line.contains("<players>")) {
				line = StringUtil.replace(line, "<players>", "" + plugin.getPlaying());
			}
			if (line.contains("<max>")) {
				line = StringUtil.replace(line, "<max>", "" + UHCGame.getInstance().getMaxplayers());
			}
			if (line.contains("<spectators>")) {
				line = StringUtil.replace(line, "<spectators>", "" + plugin.getSpectators());
			}
			if (line.contains("<start-time>")) {
				line = StringUtil.replace(line, "<start-time>", Utils.formatTimeHours(UHCGame.getInstance().lobbyCountdowns));
			}
		} else if (GameStatus.is(GameStatus.TELEPORT)) {
			if (line.contains("<hostName>")) {
				line = StringUtil.replace(line, "<hostName>", UHCGame.getInstance().getHostName());
			}
			if (line.contains("<type>")) {
				line = StringUtil.replace(line, "<type>", UHCGame.getInstance().getGameType());
			}
			if (line.contains("<players>")) {
				line = StringUtil.replace(line, "<players>", "" + plugin.getPlaying());
			}
			if (line.contains("<max>")) {
				line = StringUtil.replace(line, "<max>", "" + UHCGame.getInstance().getMaxplayers());
			}
			if (line.contains("<spectators>")) {
				line = StringUtil.replace(line, "<spectators>", "" + plugin.getSpectators());
			}
			if (line.contains("<teleport>")) {
				line = StringUtil.replace(line, "<teleport>", "" + UHCGame.getInstance().getTeleported());
			}
			if (line.contains("<total>")) {
				line = StringUtil.replace(line, "<total>", "" + UHCGame.getInstance().getTotalTeleport());
			}
			if (line.contains("<teleportLeft>")) {
				line = StringUtil.replace(line, "<teleportLeft>", String.valueOf(ReleaseTask.NEED_TELEPORT_PLAYERS.size()));
			}
			if (line.contains("<timeLeft>")) {
				if (ReleaseTask.SCATTERING) {
					line = StringUtil.replace(line, "<timeLeft>", "Scattering...");
				} else {
					line = StringUtil.replace(line, "<timeLeft>", ReleaseTask.RELEASE_TIME + "");
				}
			}
		} else {
			if (line.contains("<hostName>")) {
				line = StringUtil.replace(line, "<hostName>", UHCGame.getInstance().getHostName());
			}
			if (line.contains("<type>")) {
				line = StringUtil.replace(line, "<type>", UHCGame.getInstance().getGameType());
			}
			if (line.contains("<players>")) {
				line = StringUtil.replace(line, "<players>", "" + plugin.getPlaying());
			}
			if (line.contains("<max>")) {
				line = StringUtil.replace(line, "<max>", "" + UHCGame.getInstance().getMaxplayers());
			}
			if (line.contains("<spectators>")) {
				line = StringUtil.replace(line, "<spectators>", "" + plugin.getSpectators());
			}
			if (line.contains("<maxplayers>")) {
				line = StringUtil.replace(line, "<maxplayers>", UHCGame.getInstance().getMaxIngamePlayers());
			}
			if (line.contains("<Format>")) {
				if (UHCGame.getInstance().isBorderShrinking()) {
					line = StringUtil.replace(line, "<Format>",
							StringUtil.replace(untilShrink, "<BorderLeft>", Utils.formatTimes(UHCGame.getInstance().borderCountdowns)));
				} else {
					line = StringUtil.replace(line, "<Format>", "");
				}
			}
			if (line.contains("<gameTimer>")) {
				line = StringUtil.replace(line, "<gameTimer>", UHCGame.getInstance().getFormattedTime());
			}
			if (line.contains("<kills>")) {
				line = StringUtil.replace(line, "<kills>", profile.getKills() + "");
			}
			if (line.contains("<teamsAlive>")) {
				line = StringUtil.replace(line, "<teamsAlive>", "" + TeamManager.getInstance().getTeamsAlive());
			}
			if (line.contains("<teamKills>")) {
				line = StringUtil.replace(line, "<teamKills>", plugin.getProfileManager().getTeamKills(p));
			}
			if (line.contains("<border>")) {
				line = StringUtil.replace(line, "<border>", UHCGame.getInstance().getBorderRadius());
			}
			if (line.contains("<deathmatchTimer>")) {
				line = StringUtil.replace(line, "<deathmatchTimer>", Utils.formatTimeHours(UHCGame.getInstance().deathmatchCountdowns));
			}
			if (line.contains("<noCleanTimer>")) {
				line = StringUtil.replace(line, "<noCleanTimer>", CachedConfig.FORMAT.format((float) Mitw.getInstance().getTimerManager().getTimer(NocleanTimer.class).getRemaining(p) / 1000));
			}
			if (line.contains("<combatTagTimer>")) {
				line = StringUtil.replace(line, "<combatTagTimer>", CachedConfig.FORMAT.format((float) Mitw.getInstance().getTimerManager().getTimer(CombatTimer.class).getRemaining(p) / 1000));
			}
		}
		line = StringUtil.replace(line, "[display=true]", "");
		line = StringUtil.replace(line, "[display=!false]", "");
		return StringUtil.cc(line);
	}

	public String doSidebarPlaceholders2(final Player p, String line) {
		if (line.contains("<tps>")) {
			line = StringUtil.replace(line, "<tps>", ChatColor.GREEN + RuntimeUtil.getTPSFormat().format(RuntimeUtil.getTPS(0)));
		}
		if (line.contains("<vanish>")) {
			line = StringUtil.replace(line, "<vanish>", plugin.getProfileManager().getProfile(p.getUniqueId()).isVanish() ? ChatColor.GREEN + "True" : ChatColor.RED + "False");
		}
		if (line.contains("<chat>")) {
			line = StringUtil.replace(line, "<chat>", UHCGame.getInstance().isOpenChat() ? ChatColor.GREEN + "True" : ChatColor.RED + "False");
		}
		line = StringUtil.replace(line, "[display=true]", "");
		line = StringUtil.replace(line, "[display=!false]", "");
		return StringUtil.cc(line);
	}

	public String doSidebarPlaceholders3(final UUID uuid, String line) {
		final PlayerProfile profile = plugin.getProfileManager().getProfile(uuid);
		if (line.contains("<winner>")) {
			line = StringUtil.replace(line, "<winner>", profile.getName());
		}
		if (line.contains("<kills>")) {
			line = StringUtil.replace(line, "<kills>", profile.getKills());
		}
		line = StringUtil.replace(line, "[display=true]", "");
		line = StringUtil.replace(line, "[display=!false]", "");
		return StringUtil.cc(line);
	}

}
