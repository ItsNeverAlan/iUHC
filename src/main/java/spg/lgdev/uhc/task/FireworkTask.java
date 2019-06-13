package spg.lgdev.uhc.task;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.enums.ServerVersion;

import java.util.Collection;
import java.util.Collections;


public class FireworkTask extends Task {

	private final Collection<Player> players;

	private int time = 0;
	private final int max = CachedConfig.PerformanceMode ? 2 : 10;
	private final Builder builder = FireworkEffect.builder();

	public FireworkTask(final Player player) {
		this(Collections.singleton(player));
	}

	public FireworkTask(Collection<Player> players) {
		super(false, 20, 20);
		this.players = players;
		if (!ServerVersion.is(ServerVersion.v1_7_R4) && !ServerVersion.is(ServerVersion.v1_8_R3)) {
			cancel();
			return;
		}
		plugin.log(false, " [DEBUG] Started fire work timer.");
	}

	@Override
	public void run() {
		if (time > max) {
			this.cancel();
			return;
		}
		for (Player player : players) {
			if (!player.isOnline()) {
				continue;
			}
			this.spawnFireworks(player.getLocation());
		}
		time++;
	}

	private void spawnFireworks(final Location location) {
		final Firework fw = location.getWorld().spawn(location, Firework.class);
		final FireworkMeta fwm = fw.getFireworkMeta();

		fwm.addEffect(builder.flicker(true).withColor(Color.ORANGE).build());
		fwm.addEffect(builder.trail(true).build());
		fwm.addEffect(builder.withFade(Color.AQUA).build());
		fwm.setPower(1);
		fw.setFireworkMeta(fwm);
	}

}
