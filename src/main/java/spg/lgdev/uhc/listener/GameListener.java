package spg.lgdev.uhc.listener;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import spg.lgdev.uhc.api.events.BorderGeneratedEvent;
import spg.lgdev.uhc.api.events.GameFinishedEvent;
import spg.lgdev.uhc.api.events.GameStatusChangedEvent;
import spg.lgdev.uhc.api.events.UHCPlayerDeathEvent;
import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.enums.BarType;
import spg.lgdev.uhc.handler.game.UHCGame;

public class GameListener implements Listener {

	@EventHandler
	public void onGameEnded(final GameFinishedEvent event) {
		UHCGame.getInstance().setCurrentBarType(BarType.Winner);
		if (CachedConfig.RESTART_GAMEFINISH) {
			Bukkit.getScheduler().runTaskLater(iUHC.getInstance(), () -> {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
			}, 20 * 10L);
		}
	}

	@EventHandler
	public void onBorderGenerated(final BorderGeneratedEvent event) {

		iUHC.getInstance().getBarrierManager().setPause(false);

	}

	@EventHandler
	public void onGameStatusChanged(final GameStatusChangedEvent event) {

		UHCGame.getInstance().updateMotd();

	}

	@EventHandler
	public void onUHCPlayerDeath(final UHCPlayerDeathEvent event) {
		final int count = iUHC.getInstance().getPlayingFast();
		if (!event.isCancelled() && count < 11 && count > 1) {

		}
	}

}
