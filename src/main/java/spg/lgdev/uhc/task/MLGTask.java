package spg.lgdev.uhc.task;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.util.cuboid.Cuboid;

public class MLGTask extends Task {

	private int time = 5;
	private final int level;
	private final Player p;

	public MLGTask(final Player p, final int level) {
		super(false, 0l, 20l);
		this.p = p;
		this.level = level;
	}

	@Override
	public void run() {
		if (time == 0) {

			final Location max = p.getLocation().add(30.0, 20.0, 30.0);
			final Location min = p.getLocation().add(-30.0, -20.0, -30.0);

			for (final Block b : new Cuboid(max, min)) {

				if (b.getType().equals(Material.STATIONARY_WATER)) {

					b.setType(Material.AIR);

				}

			}

			p.teleport(p.getLocation().add(0.0, level, 0.0));
			cancel();

		}

		CachedConfig.SOUND_COUNTDOWN.playSoundToEveryone();
		p.sendMessage(Lang.getMsg(p, "MLG.countdown").replaceAll("<time>", time + ""));
		time--;
	}

}
