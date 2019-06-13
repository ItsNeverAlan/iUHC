package spg.lgdev.uhc.api.events;

import lombok.Getter;
import spg.lgdev.uhc.enums.GameStatus;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStatusChangedEvent extends Event {

    private static final HandlerList handlerlist = new HandlerList();
    @Getter
    private final GameStatus gameStatus;

    public GameStatusChangedEvent(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public static HandlerList getHandlerList() {
        return handlerlist;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerlist;
    }

}
