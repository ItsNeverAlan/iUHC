package spg.lgdev.uhc.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.nms.NMSHandler;


public class SitRemoverTask extends Task {

	private final Iterator<UUID> removes;
	private int REMOVE_PRE_TICK = 0;

	public SitRemoverTask() {

		super(!CachedConfig.LESS_CPU_USAGE, 0l, 1l);
		removes = new ArrayList<>(game.getPlayersUUID()).iterator();
		REMOVE_PRE_TICK = (int) Math.ceil((double) game.getPlayersUUID().size() / (double) 20);

	}

	@Override
	public void run() {

		if (!removes.hasNext()) {

			cancel();
			return;

		}

		Player p;

		for (int i = 0; i < REMOVE_PRE_TICK; i++) {

			if (removes.hasNext()) {

				p = Bukkit.getPlayer(removes.next());
				if (p == null) {
					continue;
				}

				NMSHandler.getInstance().getSit().removeHorses(p);

			} else {

				cancel();
				return;

			}

		}
	}

}
