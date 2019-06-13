package spg.lgdev.uhc.enums;

import spg.lgdev.uhc.api.events.GameStatusChangedEvent;
import org.bukkit.Bukkit;

public enum GameStatus {
    LOADING(false),
    WAITING(false),
    TELEPORT(false),
    PVE(true),
    PVP(true),
    DEATHMATCH(true),
    FINISH(true);

    private static GameStatus currentGameStatus;
    private boolean currentStarted;

    GameStatus(boolean started) {
        this.currentStarted = started;
    }

    public static void set(GameStatus gameStatus) {
        if (currentGameStatus == gameStatus) {
            return;
        }
        currentGameStatus = gameStatus;
        Bukkit.getPluginManager().callEvent(new GameStatusChangedEvent(gameStatus));
    }

    public static boolean is(GameStatus b) {
        return currentGameStatus == b;
    }

    public static GameStatus get() {
        return currentGameStatus;
    }

    public static boolean started() {
        return currentGameStatus.isStarted();
    }

    public static boolean notStarted() {
        return !currentGameStatus.isStarted();
    }

    public boolean isStarted() {
        return currentStarted;
    }

}

