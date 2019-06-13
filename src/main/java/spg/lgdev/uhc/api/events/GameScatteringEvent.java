package spg.lgdev.uhc.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import spg.lgdev.uhc.handler.game.UHCGame;

import java.util.List;
import java.util.UUID;

public class GameScatteringEvent extends Event {

    private static final HandlerList handlerlist = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlerlist;
    }

    public List<UUID> getPlayers() {
        return UHCGame.getInstance().getPlayersUUID();
    }

    public HandlerList getHandlers() {
        return handlerlist;
    }

}
