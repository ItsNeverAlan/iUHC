package spg.lgdev.uhc.nms.common;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public interface IFastBlockSet {

	public default void setBlockFast(final Block block, final Material material, final boolean applyPhysics) {
		setBlockFast(block.getLocation(), material, applyPhysics);
	}

	public default void setBlockFast(final Location location, final Material material, final boolean applyPhysics) {
		setBlockFast(location, material.getId(), 0, applyPhysics);
	}

	public default void setBlockFast(final Location location, final int material, final boolean applyPhysics) {
		setBlockFast(location, material, 0, applyPhysics);
	}

	public default void setBlockFast(final Location location, final int blockId, final int data, final boolean applyPhysics) {
		setBlockFast(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), blockId, (byte) 0, applyPhysics);
	}

	void setBlockFast(World world, int x, int y, int z, int blockId, byte data, boolean applyPhysics);
}
