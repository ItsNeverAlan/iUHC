package spg.lgdev.uhc.util;

import java.util.Collection;
import java.util.function.Function;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableList;

public class PlayerUtil {

	public static void broadcastAction(final Function<Player, Object> action) {

		final ImmutableList<Player> players = ImmutableList.copyOf(iUHC.getInstance().getServer().getOnlinePlayers());
		final int size = players.size();
		final int diff = (int) Math.ceil(players.size() / 20D);

		for (int i = 0, j = 0; i < size; i += diff) {

			if (i >= size)
				return;

			// Some shit for the task
			final int start = i;
			final int end = i + diff;
			Bukkit.getServer().getScheduler().runTaskLater(iUHC.getInstance(), () -> {
				for (int i1 = start; i1 < end; ++i1) {
					// Overshot
					if (i1 >= players.size())
						return;

					action.apply(players.get(i1));
				}
			}, ++j);

		}
	}

	public static void broadcastAction(final Function<Player, Object> action, final Runnable runnable) {

		final ImmutableList<Player> players = ImmutableList.copyOf(iUHC.getInstance().getServer().getOnlinePlayers());
		final int size = players.size();
		final int diff = (int) Math.ceil(players.size() / 20D);

		for (int i = 0, j = 0; i < size; i += diff) {

			if (i >= size)
				return;

			// Some shit for the task
			final int start = i;
			final int end = i + diff;
			Bukkit.getServer().getScheduler().runTaskLater(iUHC.getInstance(), () -> {
				for (int i1 = start; i1 < end; ++i1) {
					// Overshot
					if (i1 >= players.size()) {
						runnable.run();
						return;
					}

					action.apply(players.get(i1));
				}
			}, ++j);

		}
	}

	public static void broadcastAction(final Function<Object, Object> action, final Collection<?> list) {

		final ImmutableList<?> players = ImmutableList.copyOf(list);
		final int size = players.size();
		final int diff = (int) Math.ceil(players.size() / 20D);

		for (int i = 0, j = 0; i < size; i += diff) {

			if (i >= size)
				return;

			// Some shit for the task
			final int start = i;
			final int end = i + diff;
			Bukkit.getServer().getScheduler().runTaskLater(iUHC.getInstance(), () -> {
				for (int i1 = start; i1 < end; ++i1) {
					// Overshot
					if (i1 >= players.size())
						return;

					action.apply(players.get(i1));
				}
			}, ++j);

		}
	}

}
