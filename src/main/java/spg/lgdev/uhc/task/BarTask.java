package spg.lgdev.uhc.task;

import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.enums.BarType;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.util.Utils;
import spg.lgdev.uhc.util.bossbar.BossBarAPI;
import net.development.mitw.uuid.UUIDCache;

public class BarTask extends Task {

	private boolean destoryed = false;

	public BarTask() {
		super(!CachedConfig.LESS_CPU_USAGE, 0L, CachedConfig.PerformanceMode ? 100L : 20L);
	}

	@Override
	public void run() {
		final BarType barType = game.getCurrentBarType();
		final String message = "bossbar." + barType.name().toLowerCase();
		String submessage = "";
		float percent = 1.0f;
		if (barType == BarType.FinalHeal) {
			submessage = Utils.formatTimeHours(game.finalHealCountdowns);
			percent = (float) game.finalHealCountdowns / game.getFinalHealTime();
		} else if (barType == BarType.PvP) {
			submessage = Utils.formatTimeHours(game.pvpCountdowns);
			percent = (float) game.pvpCountdowns / (game.getPvpTime() - game.getFinalHealTime());
		} else if (barType == BarType.Border) {
			if (!UHCGame.getInstance().isHasBorder()) {
				if (!destoryed) {
					destoryed = true;
					plugin.getServer().getOnlinePlayers().stream().filter(BossBarAPI::hasBar)
					.forEach(BossBarAPI::removeAllBars);
				}
				return;
			}
			submessage = Utils.formatTimeHours(game.borderCountdowns - 1);
			percent = (float) game.borderCountdowns
					/ (game.isFirstBorderDone() ? 300 : game.getFirstBorder() - game.getPvpTime());
		} else {
			submessage = game.getWinners().stream().map(UUIDCache::getName).collect(Collectors.joining(", "));
		}
		destoryed = false;
		for (final Player player : plugin.getServer().getOnlinePlayers()) {
			BossBarAPI.setMessage(player, Lang.getMsg(player, message) + submessage, percent);
		}
	}

}
