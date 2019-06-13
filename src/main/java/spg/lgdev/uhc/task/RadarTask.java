package spg.lgdev.uhc.task;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.handler.Lang;

public class RadarTask extends Task {

	public RadarTask() {
		super(!CachedConfig.LESS_CPU_USAGE, 20L, 20L);
	}

	@Override
	public void run() {

		if (GameStatus.is(GameStatus.FINISH)) {
			cancel();
			return;
		}

		for (final Player p : game.getOnlinePlayers()) {
			if (!p.getNearbyEntities(30D, 30D, 30D).isEmpty())
				return;
			if (!p.getNearbyEntities(50D, 10D, 50D).isEmpty()) {
				for (final Entity e : p.getNearbyEntities(50D, 50D, 50D)) {
					if (e instanceof Player) {
						p.sendMessage(Lang.getMsg(p, "Radar.50"));
					}
				}
				return;
			} else if (!p.getNearbyEntities(100D, 10D, 100D).isEmpty()) {
				for (final Entity e : p.getNearbyEntities(100D, 50D, 100D)) {
					if (e instanceof Player) {
						p.sendMessage(Lang.getMsg(p, "Radar.100"));
					}
				}
				return;
			} else if (!p.getNearbyEntities(150D, 10D, 150D).isEmpty()) {
				loop:
					for (final Entity e : p.getNearbyEntities(150D, 50D, 150D)) {
						if (e instanceof Player) {
							p.sendMessage(Lang.getMsg(p, "Radar.150"));
							continue loop;
						}
					}
			return;
			} else if (!p.getNearbyEntities(200D, 10D, 200D).isEmpty()) {
				for (final Entity e : p.getNearbyEntities(200D, 50D, 200D)) {
					if (e instanceof Player) {
						p.sendMessage(Lang.getMsg(p, "Radar.200"));
					}
				}
				return;
			} else if (!p.getNearbyEntities(250D, 10D, 250D).isEmpty()) {
				for (final Entity e : p.getNearbyEntities(250D, 50D, 250D)) {
					if (e instanceof Player) {
						p.sendMessage(Lang.getMsg(p, "Radar.250"));
					}
				}
			}
		}

	}

}
