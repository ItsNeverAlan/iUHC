package spg.lgdev.uhc.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import spg.lgdev.uhc.world.generator.GeneratorData;

public class GeneratorTaskCompleteEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private String worldName;
    private GeneratorData borderData;

    public GeneratorTaskCompleteEvent(String worldName, GeneratorData borderData) {
        this.worldName = worldName;
        this.borderData = borderData;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public String getWorldName() {
        return worldName;
    }

    public GeneratorData getBorderData() {
        return borderData;
    }
}
