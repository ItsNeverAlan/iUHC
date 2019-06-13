package spg.lgdev.uhc.api.events;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UHCPlayerDeathEvent extends Event implements Cancellable {

    private static final HandlerList handlerlist = new HandlerList();

    private OfflinePlayer player;
    private boolean cancelled;

    public UHCPlayerDeathEvent(OfflinePlayer player) {
        this.player = player;
        this.cancelled = false;
    }

    public static HandlerList getHandlerList() {
        return handlerlist;
    }

    public HandlerList getHandlers() {
        return handlerlist;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
