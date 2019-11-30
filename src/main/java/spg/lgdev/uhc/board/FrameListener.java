package spg.lgdev.uhc.board;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import spg.lgdev.uhc.config.CachedConfig;

public class FrameListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (CachedConfig.BoardEnabled) {
			Frame.getInstance().getBoards().put(event.getPlayer().getUniqueId(), new FrameBoard(event.getPlayer()));
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (CachedConfig.BoardEnabled) {
			Frame.getInstance().getBoards().remove(event.getPlayer().getUniqueId());
		}
	}

}
