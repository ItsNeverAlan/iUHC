package spg.lgdev.uhc.task;

import spg.lgdev.uhc.util.CStringBuffer;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.enums.BarType;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.nms.NMSHandler;
import spg.lgdev.uhc.scenario.Scenarios;
import spg.lgdev.uhc.util.Utils;

public class GameTask extends Task {

	public GameTask() {
		super (!CachedConfig.LESS_CPU_USAGE, 0, 20);

		game.finalHealCountdowns = game.getFinalHealTime();

		game.pvpCountdowns = game.getPvpTime()
				- game.getFinalHealTime();

		game.borderCountdowns = game.getFirstBorder() - game.getPvpTime();
	}

	@Override
	public void run() {

		if (GameStatus.is(GameStatus.FINISH)) {
			cancel();
			return;
		}

		if (game.gameCountdowns == 15) {

			game.healAll();
			game.memoryFree();
			Lang.getInstance().broadCastWithSound("BroadCast.FirstHeal", CachedConfig.SOUND_FIRSTHEAL);
		}

		if (game.finalHealCountdowns != 0) {

			--game.finalHealCountdowns;
			final int timer = game.finalHealCountdowns;

			if (timer == 2400 || timer == 2100 || timer == 1800 || timer == 1500 || timer == 1200
					|| timer == 900 || timer == 600 || timer == 300 || timer == 120 || timer == 60
					|| timer == 30 || timer == 15 || timer <= 10 && timer > 0) {

				for (final Player p : plugin.getServer().getOnlinePlayers()) {
					p.sendMessage(new CStringBuffer(Lang.getMsg(p, "Timer-CountDown.FinalHeal"))
							.replaceAll("<timer>", Utils.formatTimes2(timer - 1))
							.replaceAll("<timerFormat>", Utils.getTimeFormat(timer - 1, p)).toString());
					CachedConfig.SOUND_COUNTDOWN.playSound(p);
				}

			} else if (timer == 0) {

				handleFinalhealCountdownEnded();

			}

		} else if (!game.isFinalhealed()) {

			handleFinalhealCountdownEnded();

		}

		if (game.isFinalhealed() && game.pvpCountdowns != 0) {

			--game.pvpCountdowns;
			final int timer = game.pvpCountdowns;

			if (timer == 2400 || timer == 2100 || timer == 1800 || timer == 1500 || timer == 1200
					|| timer == 900 || timer == 600 || timer == 300 || timer == 120 || timer == 60
					|| timer == 30 || timer == 15 || timer <= 10 && timer > 0) {

				for (final Player p : plugin.getServer().getOnlinePlayers()) {
					p.sendMessage(new CStringBuffer(Lang.getMsg(p, "Timer-CountDown.PvPTimer"))
							.replaceAll("<timer>", Utils.formatTimes2(timer - 1))
							.replaceAll("<timerFormat>", Utils.getTimeFormat(timer - 1, p)).toString());
					CachedConfig.SOUND_COUNTDOWN.playSound(p);
				}

			} else if (timer == 0) {

				handlePvpCountdownEnded();

			}

		} else if (game.isFinalhealed() && !game.isPvp()) {

			handlePvpCountdownEnded();

		}

		game.gameCountdowns++;

	}

	private void handlePvpCountdownEnded() {

		for (final Player p : plugin.getServer().getOnlinePlayers()) {
			p.sendMessage(Lang.getMsg(p, "game-prefix") + Lang.getMsg(p, "BroadCast.PvPEnabled"));
			CachedConfig.SOUND_PVP.playSound(p);
		}
		GameStatus.set(GameStatus.PVP);
		game.setPvp(true);
		if (Scenarios.Radar.isOn()) {
			new RadarTask();
		}
		new BorderTask();
		game.setCurrentBarType(BarType.Border);

	}

	private void handleFinalhealCountdownEnded() {

		for (final Player player :  plugin.getServer().getOnlinePlayers()) {
			player.sendMessage(Lang.getMsg(player, "game-prefix") + Lang.getMsg(player, "BroadCast.FinalHeal"));
			CachedConfig.SOUND_FINALHEAL.playSound(player);
		}
		game.healAll();
		game.setCurrentBarType(BarType.PvP);
		game.setFinalhealed(true);

	}

}
