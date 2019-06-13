package spg.lgdev.uhc.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;
import java.util.UUID;

public class GameFinishedEvent extends Event {

    private static final HandlerList handlerlist = new HandlerList();
    private List<UUID> winners;

    public GameFinishedEvent(List<UUID> winner) {
        this.winners = winner;
    }

    public static HandlerList getHandlerList() {
        return handlerlist;
    }

    public List<UUID> getWinners() {
        return winners;
    }

    public HandlerList getHandlers() {
        return handlerlist;
    }

}
