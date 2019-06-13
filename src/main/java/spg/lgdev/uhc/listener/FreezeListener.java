package spg.lgdev.uhc.listener;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.nms.NMSHandler;
import spg.lgdev.uhc.player.PlayerProfile;
import spg.lgdev.uhc.util.StringUtil;
import spg.lgdev.uhc.util.json.FancyMessage;

public class FreezeListener implements org.bukkit.event.Listener {

	private final iUHC plugin;

	public FreezeListener(final iUHC plugin) {
		this.plugin = plugin;
	}

	public static void freezerPlayer(final Player p, final Player staff) {
		final PlayerProfile profile = iUHC.getInstance().getProfileManager().getProfile(p.getUniqueId());
		if (!profile.isFrozen()) {
			profile.setFrozen(true);
			p.setSprinting(false);
			NMSHandler.getInstance().getNMSControl().setScatterEffects(p);
			for (final String msg : Lang.getInstance().getMessageList(p, "Freeze.FrozenPlayer")) {
				p.sendMessage(msg);
			}
			for (final UUID uuid : UHCGame.getInstance().getMods()) {
				final Player mods = Bukkit.getPlayer(uuid);
				if (mods == null) {
					continue;
				}
				mods.sendMessage(Lang.getMsg(mods, "Freeze.Notify-Frozen-aPlayer").replace("<player>", p.getName()).replace("<staff>", staff.getName()));
			}
		} else {
			NMSHandler.getInstance().getNMSControl().clearScatterEffects(p);
			profile.setFrozen(false);
			for (final String msg : Lang.getInstance().getMessageList(p, "Freeze.UnFrozenPlayer")) {
				p.sendMessage(msg);
			}
			p.closeInventory();
		}
	}

	public String Colored(final String s) {
		return StringUtil.cc(s);
	}

	@EventHandler
	public void freezerDamage(final EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			final Player damaged = (Player) e.getEntity();
			final Player damager = (Player) e.getDamager();
			if (plugin.getProfileManager().isFrozen(damager)) {
				e.setCancelled(true);
				damager.sendMessage(Colored("&cThat player is getFrozens(), you can not hit him!"));
			}
			if (plugin.getProfileManager().isFrozen(damaged)) {
				e.setCancelled(true);
				damaged.sendMessage(Colored("&cYou are getFrozens(), you can not hurt anyone!"));
			}
		}
	}

	@EventHandler
	public void freezerDamage2(final EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			final Player p = (Player) e.getEntity();
			if (plugin.getProfileManager().isFrozen(p)) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPickupItem(final PlayerPickupItemEvent e) {
		final Player p = e.getPlayer();
		if (plugin.getProfileManager().isFrozen(p)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onDropItem(final PlayerDropItemEvent e) {
		final Player p = e.getPlayer();
		if (plugin.getProfileManager().isFrozen(p)) {
			e.setCancelled(true);
		}

	}

	@EventHandler
	public void onInteract(final PlayerInteractEvent e) {
		final Player p = e.getPlayer();
		if (plugin.getProfileManager().isFrozen(p)) {
			e.setCancelled(true);
		}

	}

	@EventHandler
	public void onQuit(final PlayerQuitEvent e) {
		final Player p = e.getPlayer();
		if (plugin.getProfileManager().isFrozen(p)) {
			p.setWalkSpeed((float) 0.2);
			p.removePotionEffect(PotionEffectType.JUMP);
			plugin.getProfileManager().getProfile(p.getUniqueId()).setFrozen(false);
			if (plugin.getFileManager().getConfig().getBoolean("freezer.autoban-on-disconnect")) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), iUHC.getInstance().getFileManager().getConfig().getString("freezer.BanCmd").replace("<player>", p.getName()));
			} else {
				Player p1;
				for (final UUID uuid : UHCGame.getInstance().getMods()) {
					p1 = Bukkit.getPlayer(uuid);
					if (p1 == null) {
						continue;
					}
					p1.sendMessage(Lang.getMsg(p1, "Freeze.Notify-Disconnect-onFreeze").replaceAll("<player>", p.getName()));
					final FancyMessage fancyMessage = new FancyMessage(Lang.getMsg(p1, "Freeze.ClickMe-ToBan").replaceAll("<player>", p.getName()));
					fancyMessage.tooltip(Colored(Lang.getMsg(p1, "Freeze.ClickMe-ToBan").replaceAll("<player>", p.getName())));
					fancyMessage.command("/" + iUHC.getInstance().getFileManager().getConfig().getString("freezer.BanCmd").replace("<player>", p.getName()));
					fancyMessage.send(p1);
				}
			}
		}
	}

	@EventHandler
	public void onBreak(final BlockBreakEvent e) {
		final Player p = e.getPlayer();
		if (plugin.getProfileManager().isFrozen(p)) {
			e.setCancelled(true);
		}

	}

	@EventHandler
	public void onPlace(final BlockPlaceEvent e) {
		final Player p = e.getPlayer();
		if (plugin.getProfileManager().isFrozen(p)) {
			e.setCancelled(true);
		}

	}

}
