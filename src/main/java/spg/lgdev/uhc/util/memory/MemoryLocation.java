package spg.lgdev.uhc.util.memory;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import lombok.Getter;
import lombok.Setter;
import spg.lgdev.uhc.util.CustomLocation;

@Getter
@Setter
public class MemoryLocation {

	private String world;
	private int x;
	private int y;
	private int z;

	public MemoryLocation(final String world, final int x, final int y, final int z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public CustomLocation toCustomLocation() {
		return new CustomLocation(world, x, y, z);
	}

	public Location toBukkitLocation() {
		return new Location(Bukkit.getWorld(world), x, y, z);
	}

	public static MemoryLocation copyOf(final Location location) {
		return new MemoryLocation(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

}
