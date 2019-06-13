package spg.lgdev.uhc.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.NumberConversions;

public class CustomLocation {

	private String world;

	private double x;
	private double y;
	private double z;

	private float yaw;
	private float pitch;

	public CustomLocation(final double x, final double y, final double z) {
		this(x, y, z, 0.0F, 0.0F);
	}

	public CustomLocation(final String world, final double x, final double y, final double z) {
		this(world, x, y, z, 0.0F, 0.0F);
	}

	public CustomLocation(final double x, final double y, final double z, final float yaw, final float pitch) {
		this(Bukkit.getWorlds().get(0).getName(), x, y, z, yaw, pitch);
	}

	public CustomLocation(final String world, final double x, final double y, final double z, final float yaw, final float pitch) {

		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;

	}

	public static CustomLocation fromBukkitLocation(final Location location) {
		return new CustomLocation(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(),
				location.getYaw(), location.getPitch());
	}

	public static CustomLocation stringToLocation(final String string) {
		if (string == null) return null;

		final String[] split = string.split(",");

		final String world = split[0];
		final double x = Double.parseDouble(split[1]);
		final double y = Double.parseDouble(split[2]);
		final double z = Double.parseDouble(split[3]);

		final float yaw = Float.parseFloat(split[4]);
		final float pitch = Float.parseFloat(split[5]);

		return new CustomLocation(world, x, y, z, yaw, pitch);
	}

	public static String locationToString(final CustomLocation loc) {
		return String.valueOf(String.valueOf(loc.getWorld() + "," + loc.getBlockX() + ","
				+ loc.getBlockY() + "," + loc.getBlockZ() + "," + loc.getYaw() + "," + loc.getPitch()));
	}

	public static int locToBlock(final double loc) {
		return NumberConversions.floor(loc);
	}

	public Location toBukkitLocation() {
		return new Location(this.toBukkitWorld(), this.x, this.y, this.z, this.yaw, this.pitch);
	}

	public double getGroundDistanceTo(final CustomLocation location) {
		return Math.sqrt(Math.pow(this.x - location.x, 2) + Math.pow(this.z - location.z, 2));
	}

	public double getGroundDistanceTo(final Location location) {
		return Math.sqrt(Math.pow(this.x - location.getX(), 2) + Math.pow(this.z - location.getZ(), 2));
	}

	public double getDistanceTo(final CustomLocation location) {
		return Math.sqrt(
				Math.pow(this.x - location.x, 2) + Math.pow(this.y - location.y, 2) + Math.pow(this.z - location.z, 2));
	}

	public World toBukkitWorld() {
		return Bukkit.getServer().getWorld(this.world);
	}

	public Block getBlock() {
		return toBukkitWorld().getBlockAt(getBlockX(), getBlockY(), getBlockZ());
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof CustomLocation))
			return false;

		final CustomLocation location = (CustomLocation) obj;
		return location.x == this.x && location.y == this.y && location.z == this.z && location.pitch == this.pitch
				&& location.yaw == this.yaw;
	}

	@Override
	public String toString() {
		return locationToString(this);
	}

	public String getWorld() {
		return world;
	}

	public void setWorld(final String world) {
		this.world = world;
	}

	public double getX() {
		return x;
	}

	public void setX(final double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(final double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(final double z) {
		this.z = z;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(final float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(final float pitch) {
		this.pitch = pitch;
	}

	public int getBlockX() {
		return locToBlock(x);
	}

	public int getBlockY() {
		return locToBlock(y);
	}

	public int getBlockZ() {
		return locToBlock(z);
	}

}
