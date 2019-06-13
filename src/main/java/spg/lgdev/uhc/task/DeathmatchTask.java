package spg.lgdev.uhc.task;

import spg.lgdev.uhc.util.CStringBuffer;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.util.StringUtil;
import spg.lgdev.uhc.util.Utils;

public class DeathmatchTask extends Task {

	public DeathmatchTask(final boolean force) {
		super(false, 0L, 20L);
		game.deathmatchCountdowns = force ? 5 : CachedConfig.MatchCountDown;
		game.setDeathmatchCountdowning(true);

		if (Bukkit.getWorld("UHCArena_deathmatch") == null) {

			if (CachedConfig.LAG_BROADCAST) {
				Bukkit.broadcastMessage(StringUtil.cc("&7&m---------------------"));
				Bukkit.broadcastMessage(" ");
				Bukkit.broadcastMessage(StringUtil.cc("&4&lLAG &c&lWARNING"));
				Bukkit.broadcastMessage(StringUtil.cc("&6&lLAG &4&lWARNING"));
				Bukkit.broadcastMessage(StringUtil.cc("&c&lLAG &6&lWARNING"));
				Bukkit.broadcastMessage(" ");
				Bukkit.broadcastMessage(StringUtil.cc("&7Reason: &f&lLoad deathmatch worlds"));
				Bukkit.broadcastMessage(StringUtil.cc("&7&m---------------------"));
			}

			final World world = Bukkit.createWorld(new WorldCreator("UHCArena_deathmatch").type(WorldType.FLAT));
			world.setMonsterSpawnLimit(0);
			world.setAnimalSpawnLimit(0);
			world.setAmbientSpawnLimit(0);
			world.setTicksPerAnimalSpawns(0);
			world.setTicksPerMonsterSpawns(0);
			world.setTime(0);
			world.setPVP(true);
			world.setDifficulty(Difficulty.HARD);
			world.setGameRuleValue("doDaylightCycle", "false");
			world.setGameRuleValue("naturalRegeneration", "false");
		}
	}

	@Override
	public void run() {

		if (GameStatus.is(GameStatus.FINISH)) {

			cancel();
			return;

		}

		if (game.deathmatchCountdowns == 0) {

			UHCGame.getInstance().deathmatchStart();
			game.setDeathmatchCountdowning(false);
			cancel();
			return;

		} else {

			if (game.deathmatchCountdowns == 2400 || game.deathmatchCountdowns == 2100 || game.deathmatchCountdowns == 1800 || game.deathmatchCountdowns == 1500 || game.deathmatchCountdowns == 1200
					|| game.deathmatchCountdowns == 900 || game.deathmatchCountdowns == 600 || game.deathmatchCountdowns == 300 || game.deathmatchCountdowns == 120 || game.deathmatchCountdowns == 60
					|| game.deathmatchCountdowns == 30 || game.deathmatchCountdowns == 15 || game.deathmatchCountdowns <= 10 && game.deathmatchCountdowns > 0) {

				for (final Player p : plugin.getServer().getOnlinePlayers()) {
					p.sendMessage(new CStringBuffer(Lang.getMsg(p, "Timer-CountDown.DeathMatchTimer"))
							.replaceAll("<timer>", Utils.formatTimes2(game.deathmatchCountdowns - 1))
							.replaceAll("<timerFormat>", Utils.getTimeFormat(game.deathmatchCountdowns - 1, p)).toString());
					CachedConfig.SOUND_COUNTDOWN.playSound(p);
				}

			}

			game.deathmatchCountdowns--;

		}

	}

}
