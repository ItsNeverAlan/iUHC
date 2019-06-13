package spg.lgdev.uhc.task;

import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.handler.game.UHCGame;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public abstract class Task implements Runnable {

    protected static iUHC plugin;
    protected static UHCGame game;

    static {

        plugin = iUHC.getInstance();
        game = UHCGame.getInstance();

    }

    private int taskId = -1;

    public Task(boolean async, long delay, long timer) {
        start(async, delay, timer);
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(taskId);
        taskId = -1;
    }

    public void changePeriod(boolean async, int timer) {
        this.cancel();
        start(async, 0, timer);
    }

    public void start(boolean async, long delay, long timer) {
        if (async) setupId(Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, delay, timer));
        else setupId(Bukkit.getScheduler().runTaskTimer(plugin, this, delay, timer));
    }

    private BukkitTask setupId(BukkitTask task) {
        this.taskId = task.getTaskId();
        return task;
    }

}
