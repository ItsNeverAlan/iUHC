package spg.lgdev.uhc.task;

import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import spg.lgdev.uhc.iUHC;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.board.Board;
import spg.lgdev.uhc.nms.NMSHandler;

public class HealthTask extends Thread {

	@Override
	public void run() {

		final iUHC plugin = iUHC.getInstance();

		while (true) {

			try {
				final Map<String, Integer> healths = getAllHealths();
				new HashSet<>(plugin.getSidebarManager().getPlayerBoards().values()).stream()
				.map(Board::getTabObjective).forEach(objective -> healths.keySet()
						.forEach(name -> objective.getScore(name).setScore(healths.get(name))));
			} catch (final Exception e) {
				e.printStackTrace();
			}

			try {
				Thread.sleep(1000L);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	private Map<String, Integer> getAllHealths() {
		return iUHC.getInstance().getServer().getOnlinePlayers().stream()
				.collect(Collectors.toMap(Player::getName,
						p -> (int) Math
						.ceil(p.getHealth() + NMSHandler.getInstance().getNMSControl().getAbsorptionHearts(p)),
						(a, b) -> a));
	}

}
