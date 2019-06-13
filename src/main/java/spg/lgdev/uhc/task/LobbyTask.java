package spg.lgdev.uhc.task;

import spg.lgdev.uhc.util.CStringBuffer;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.util.Utils;

public class LobbyTask extends Task {

	public LobbyTask() {
		super(false, 20, 20);
		game.setLobbyCountdowning(true);
		game.lobbyCountdowns = plugin.getFileManager().getConfig().getInt("gameConfig.tasks.lobbyTimer");
	}

	@Override
	public void run() {

		if (game.lobbyCountdowns - 1 == 0) {

			game.setLobbyCountdowning(false);

			game.startScattering();

			cancel();

		} else {

			--game.lobbyCountdowns;

			if (game.lobbyCountdowns % 5 == 0 && game.lobbyCountdowns > 5) {

				for (final Player p : plugin.getServer().getOnlinePlayers()) {
					p.sendMessage(new CStringBuffer(Lang.getMsg(p, "Timer-CountDown.LobbyTimer"))
							.replaceAll("<timer>", Utils.formatTimes2(game.lobbyCountdowns - 1))
							.replaceAll("<timerFormat>", Utils.getTimeFormat(game.lobbyCountdowns - 1, p)).toString());
					CachedConfig.SOUND_COUNTDOWN.playSound(p);
				}

			} else if (game.lobbyCountdowns >= 1 && game.lobbyCountdowns <= 5) {

				for (final Player p : plugin.getServer().getOnlinePlayers()) {

					p.sendMessage(new CStringBuffer(Lang.getMsg(p, "Timer-CountDown.LobbyTimer"))
							.replaceAll("<timer>", Utils.formatTimes2(game.lobbyCountdowns - 1))
							.replaceAll("<timerFormat>", Utils.getTimeFormat(game.lobbyCountdowns - 1, p)).toString());
					CachedConfig.SOUND_COUNTDOWN.playSound(p);
				}

			}

		}

	}

}
