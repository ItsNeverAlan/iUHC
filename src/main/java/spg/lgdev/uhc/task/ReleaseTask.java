package spg.lgdev.uhc.task;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import spg.lgdev.uhc.util.CStringBuffer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.enums.PlayerStatus;
import spg.lgdev.uhc.enums.ServerVersion;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.manager.ArenaManager;
import spg.lgdev.uhc.manager.PracticeManager;
import spg.lgdev.uhc.nms.NMSHandler;
import spg.lgdev.uhc.player.PlayerProfile;
import spg.lgdev.uhc.util.Utils;

public class ReleaseTask extends Task {

	public static List<UUID> NEED_TELEPORT_PLAYERS = Lists.newArrayList();
	public static boolean FINISHED_TELEPORT;
	public static int RELEASE_TIME;
	/**
	 * Unlimited scatter time value
	 */
	public static boolean SCATTERING;
	private int PRE_TELEPORT_PRE_SECOND = 5;

	public ReleaseTask() {

		super(false, 40, 20);

		RELEASE_TIME = plugin.getFileManager().getConfig().getInt("gameConfig.tasks.releaseTimer");
		NEED_TELEPORT_PLAYERS = new ArrayList<>();

		int count = 0;
		for (final Player player : plugin.getServer().getOnlinePlayers()) {
			if (plugin.getProfileManager().getProfile(player.getUniqueId()).isPlayerAlive()) {
				NEED_TELEPORT_PLAYERS.add(player.getUniqueId());
				count++;
			}
		}

		if (CachedConfig.UNLIMIT_TP_TIME) {

			SCATTERING = true;
			PRE_TELEPORT_PRE_SECOND = (int) Math.ceil((double) count / (double) 50);

		} else {

			SCATTERING = false;
			PRE_TELEPORT_PRE_SECOND = (int) Math.ceil((double) count / (double) (RELEASE_TIME - 5));

		}

	}

	@Override
	public void run() {

		if (!FINISHED_TELEPORT) {

			for (int i = 0; i < PRE_TELEPORT_PRE_SECOND; i++) {

				if (!NEED_TELEPORT_PLAYERS.isEmpty()) {

					final Player player = Bukkit.getPlayer(NEED_TELEPORT_PLAYERS.remove(0));
					if (player == null) {
						continue;
					}

					final PlayerProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
					if (!profile.isPlayerAlive() || profile.isSpectator() || UHCGame.getInstance().isMod(player.getUniqueId())) {
						continue;
					}

					teleportPlayer(player, profile);

				} else {

					FINISHED_TELEPORT = true;
					SCATTERING = false;

					Lang.getInstance().broadCastList("Scatter.Finish");

					PracticeManager.disable();
					ArenaManager.getInstance().memoryFree();
					plugin.getWorldCreator().unloadUselessWorlds();

					break;

				}

			}

			if (SCATTERING)
				return;

		}

		RELEASE_TIME--;

		if (RELEASE_TIME % 5 == 0 && RELEASE_TIME >= 5 || RELEASE_TIME < 5 && RELEASE_TIME > 0) {

			if (RELEASE_TIME == 1) {
				if(ServerVersion.isUnder(ServerVersion.get(), ServerVersion.v1_9_R1)) {
					new SitRemoverTask();
				}
				Bukkit.getScheduler().runTaskLater(plugin, game::clearPlayers, 18L);
			}

			for (final Player p : plugin.getServer().getOnlinePlayers()) {
				p.sendMessage(new CStringBuffer(Lang.getMsg(p, "Timer-CountDown.StartTimer"))
						.replaceAll("<timer>", Utils.formatTimes2(RELEASE_TIME - 1))
						.replaceAll("<timerFormat>", Utils.getTimeFormat(RELEASE_TIME - 1, p)).toString());
				CachedConfig.SOUND_COUNTDOWN.playSound(p);
			}

		} else if (RELEASE_TIME <= 0) {

			game.startGame();
			cancel();
			return;

		}

	}

	public void teleportPlayer(final Player player, final PlayerProfile profile) {

		if (player != null) {
			ArenaManager.getInstance().scatter(player);

			game.setTeleported(game.getTeleported() + 1);

			profile.setStatus(PlayerStatus.SCATTERED);
			NMSHandler.getInstance().getSit().spawn(player, player.getLocation());
		}
	}

}
