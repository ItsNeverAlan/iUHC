package spg.lgdev.uhc.manager;

import java.util.UUID;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.game.KitsHandler;
import spg.lgdev.uhc.handler.game.UHCGame;
import net.development.mitw.utils.FastRandom;

public class PracticeManager {

	private static UHCGame game = UHCGame.getInstance();

	public static boolean teleport(final Player p) {
		try {
			final FastRandom rand = iUHC.getRandom();
			final World w = Bukkit.getWorld("UHCArena_practice");
			final int x = rand.nextInt(-140, 140);
			final int z = rand.nextInt(-140, 140);
			final int y = w.getHighestBlockYAt(x, z);
			final Location loc = new Location(w, x, y, z).add(0, 3, 0);
			p.teleport(loc);
			return true;
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void join(final Player p) {
		p.getInventory().clear();
		if (!game.getPracticePlayers().contains(p.getUniqueId())) {
			game.getPracticePlayers().add(p.getUniqueId());
		}
		p.setMaximumNoDamageTicks(20);
		Bukkit.getScheduler().runTaskLater(iUHC.getInstance(), () -> {

			KitsHandler.getInstance().giveKit(p, "PRACTICE");
			teleport(p);

			p.sendMessage(Lang.getMsg(p, "Practice.Join"));

			CachedConfig.SOUND_PRACTICE.playSound(p);

		}, 10L);
		if (CachedConfig.HIDEALL) {
			for (final UUID u : game.getPracticePlayers()) {
				final Player p1 = Bukkit.getPlayer(u);
				if (p1 != null && p != p1) {
					p.showPlayer(p1);
					p1.showPlayer(p);
				}
			}
		}
	}

	public static void quit(final Player p, final boolean spawn) {

		game.getPracticePlayers().remove(p.getUniqueId());

		p.getInventory().clear();
		p.getInventory().setArmorContents(null);

		p.setHealth(p.getMaxHealth());
		if (spawn) {

			iUHC.getInstance().getItemManager().setSpawnItems(p);
			p.teleport(game.getSpawnPoint().toBukkitLocation());

			if (CachedConfig.HIDEALL) {
				for (final UUID u : game.getPracticePlayers()) {
					final Player p1 = Bukkit.getPlayer(u);
					if (p1 != null && p != p1) {
						p.hidePlayer(p1);
						p1.hidePlayer(p);
					}
				}
			}

		}
		p.updateInventory();
		p.setMaximumNoDamageTicks(20);
	}

	public static void disable() {
		game.setPracticeEnabled(false);
	}

	public static void respawn(final Player p) {
		p.getInventory().clear();
		Bukkit.getScheduler().runTaskLater(iUHC.getInstance(), () -> {
			KitsHandler.getInstance().giveKit(p, "PRACTICE");
			teleport(p);
			CachedConfig.SOUND_PRACTICE.playSound(p);
			p.setMaximumNoDamageTicks(20);
			p.setFlying(false);
			p.setAllowFlight(false);
		}, 10L);
	}

}
