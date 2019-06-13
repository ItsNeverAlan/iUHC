package spg.lgdev.uhc.task;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import spg.lgdev.uhc.manager.TeamManager;

public class InviteCleanupTask implements Runnable {

	private static int runnableID;

	public InviteCleanupTask(final Plugin plugin) {
		runnableID = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, 20 * 5L, 20 * 5L).getTaskId();
	}

	@Override
	public void run() {

		if (!TeamManager.getInstance().isTeamsEnabled())
			return;

		TeamManager.getInstance().getTeams().forEach(team -> team.getInvited().entrySet().removeIf(
				entry -> System.currentTimeMillis() - entry.getValue() > 30_000));
	}

	public static void cancelTask() {
		Bukkit.getScheduler().cancelTask(runnableID);
		runnableID = 0;

		TeamManager.getInstance().getTeams().forEach(team -> team.getInvited().entrySet().removeIf(
				entry -> System.currentTimeMillis() - entry.getValue() > 30_000));
	}

}
