package spg.lgdev.uhc.util.bossbar;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.md_5.bungee.api.chat.BaseComponent;

public class BossBarAPI implements Listener {

	protected static final Map<UUID, BossBar> barMap = new ConcurrentHashMap<>();

	public static BossBar addBar(Player player, BaseComponent component, float progress) {
		setMessage(player, component.toLegacyText(), progress * 100);
		return getBossBar(player);
	}

	public static BossBar getBossBars(Player player) {
		if (!barMap.containsKey(player.getUniqueId())) {
			return null;
		}
		return barMap.get(player.getUniqueId());
	}

	protected static void removeBarForPlayer(Player player, BossBar bossBar) {
		if (bossBar != null)
			bossBar.setVisible(false);
		barMap.remove(player.getUniqueId());
	}

	public static void removeAllBars(Player player) {
		removeBarForPlayer(player, getBossBars(player));
	}

	public static void setMessage(Player player, String message) {
		setMessage(player, message, 100);
	}

	public static void setMessage(Player player, String message, float percentage) {
		setMessage(player, message, percentage, 0);
	}

	public static void setMessage(Player player, String message, float percentage, int timeout) {
		setMessage(player, message, percentage, timeout, 100);
	}

	public static void setMessage(Player player, String message, float percentage, int timeout, float minHealth) {
		if (!barMap.containsKey(player.getUniqueId())) {
			barMap.put(player.getUniqueId(), new EntityBossBar(player, message, percentage, timeout, 0));
			return;
		}
		BossBar bar = barMap.get(player.getUniqueId());

		if(!bar.getWorld().getName().equals(player.getWorld().getName())) {
			removeAllBars(player);
			bar = barMap.put(player.getUniqueId(), new EntityBossBar(player, message, percentage, timeout, 0));
		}

		if (!bar.getMessage().equals(message)) {
			bar.setMessage(message);
		}
		if (bar.getHealth() != percentage * 300) {
			bar.setHealth(percentage);
		}
		if (!bar.isVisible()) {
			bar.setVisible(true);
		}
		bar.updateMovement();
	}

	public static boolean hasBar(@Nonnull Player player) {
		return barMap.containsKey(player.getUniqueId());
	}

	public static void removeBar(@Nonnull Player player) {
		final BossBar bar = getBossBar(player);
		if (bar != null) { bar.setVisible(false); }
		removeAllBars(player);
	}

	@Nullable
	public static BossBar getBossBar(@Nonnull Player player) {
		if (player == null) { return null; }
		return hasBar(player) ? barMap.get(player.getUniqueId()) : null;
	}

	public static Collection<BossBar> getBossBars() {
		return barMap.values();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent e) {
		if (!hasBar(e.getPlayer())) {
			return;
		}
		BossBarAPI.removeBar(e.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onKick(PlayerKickEvent e) {
		if (!hasBar(e.getPlayer())) {
			return;
		}
		BossBarAPI.removeBar(e.getPlayer());
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent e) {
		if (!hasBar(e.getPlayer())) {
			return;
		}
		BossBarAPI.removeBar(e.getPlayer());
	}

}
