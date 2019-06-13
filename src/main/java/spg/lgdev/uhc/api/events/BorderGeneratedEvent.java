package spg.lgdev.uhc.api.events;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;

@Getter
public class BorderGeneratedEvent extends Event {

	private static final HandlerList handlerlist = new HandlerList();
	private final String worldName;
	private final int radius;

	public BorderGeneratedEvent(final String worldName, final World world, final int radius) {
		this.worldName = worldName;
		this.radius = radius;
	}

	public static HandlerList getHandlerList() {
		return handlerlist;
	}

	@Override
	public HandlerList getHandlers() {
		return handlerlist;
	}

}
