package spg.lgdev.uhc.task;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import spg.lgdev.uhc.scenario.Timebomb;

public class TimebombTask extends Task {

	private final Set<Timebomb> timebombs = new HashSet<>();

	public TimebombTask() {
		super(true, 5, 5);
	}

	public void add(final Timebomb timebomb) {
		this.timebombs.add(timebomb);
	}

	public void remove(final Timebomb timebomb) {
		this.timebombs.remove(timebomb);
	}

	@Override
	public void run() {
		final Iterator<Timebomb> iterator = new HashSet<>(timebombs).iterator();
		while (iterator.hasNext()) {
			final Timebomb timebomb = iterator.next();
			timebomb.spawnHologram();
			if (timebomb.canStep()) {
				timebomb.run();
				timebomb.step();
			}
		}
	}

}
