package spg.lgdev.uhc.api.events;

import lombok.Getter;
import spg.lgdev.uhc.player.PlayerProfile;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UHCProfileCreatedEvent extends Event {

    private static final HandlerList handlerlist = new HandlerList();

    @Getter
    private PlayerProfile profile;

    public UHCProfileCreatedEvent(PlayerProfile profile) {
        this.profile = profile;
    }

    public static HandlerList getHandlerList() {
        return handlerlist;
    }

    public HandlerList getHandlers() {
        return handlerlist;
    }

}
