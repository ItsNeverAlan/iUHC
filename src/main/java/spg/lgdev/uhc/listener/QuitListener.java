package spg.lgdev.uhc.listener;

import spg.lgdev.uhc.iUHC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.handler.game.Loggers;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.player.PlayerProfile;

public class QuitListener implements org.bukkit.event.Listener {

	private final iUHC plugin;

	public QuitListener(final iUHC plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onQuitPlayer(final PlayerQuitEvent e) {

		final Player p = e.getPlayer();

		e.setQuitMessage(null);
		final PlayerProfile profile = plugin.getProfileManager().getProfile(p.getUniqueId());
		profile.setOnline(false);

		plugin.getSidebarManager().getPlayerBoards().remove(p.getUniqueId());

		if (GameStatus.notStarted()) {

			profile.setPlayerAlive(false);
			UHCGame.getInstance().getWhitelist().remove(p.getUniqueId());

		} else if (profile.isPlayerAlive()) {

			profile.setPlayerAlive(false);
			UHCGame.getInstance().checkWin();

			if (GameStatus.is(GameStatus.DEATHMATCH))
				return;

			profile.saveData(p);

			plugin.getDisconnectTimer().setCooldown(p, p.getUniqueId());

			if (!UHCGame.getInstance().getWhitelist().contains(p.getUniqueId())) {
				UHCGame.getInstance().getWhitelist().add(p.getUniqueId());
			}

			if (!GameStatus.is(GameStatus.PVE)) {
				Loggers.getInstance().spawnLogger(p);
			}

		} else {
			UHCGame.getInstance().getWhitelist().remove(p.getUniqueId());
		}

		UHCGame.getInstance().clearMap(p);

	}

}
