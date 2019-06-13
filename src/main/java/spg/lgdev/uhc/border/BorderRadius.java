package spg.lgdev.uhc.border;

import java.util.Arrays;
import java.util.LinkedHashSet;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.util.Utils;
import spg.lgdev.uhc.world.CoordXZ;


public class BorderRadius {

	public static final LinkedHashSet<Integer> safeOpenBlocks = new LinkedHashSet<>(Arrays.asList(0, 6, 8, 9, 27, 28, 30, 31, 32, 37, 38, 39, 40, 50, 55, 59, 63, 64, 65, 66, 68, 69, 70, 71, 72, 75, 76, 77,
			78, 83, 90, 93, 94, 96, 104, 105, 106, 115, 131, 132, 141, 142, 149, 150, 157, 171));

	public static final LinkedHashSet<Integer> painfulBlocks = new LinkedHashSet<>(
			Arrays.asList(10, 11, 51, 81, 119));
	private static final int limBot = 0;

	public static Location checkPlayer(final Player player, final Location targetLoc, final boolean returnLocationOnly) {
		if (player == null || !player.isOnline())
			return null;

		final Location loc = (targetLoc == null) ? player.getLocation().clone() : targetLoc;
		if (loc == null)
			return null;

		final World world = loc.getWorld();

		if (world == null)
			return null;

		if (UHCGame.getInstance().insideBorder(loc))
			return null;

		final Location newLoc = newLocation(player, loc);

		if (player.isInsideVehicle()) {
			final Entity ride = player.getVehicle();
			player.leaveVehicle();
			if (ride != null) {
				final double vertOffset = (ride instanceof LivingEntity) ? 0 : ride.getLocation().getY() - loc.getY();
				final Location rideLoc = newLoc.clone();
				rideLoc.setY(newLoc.getY() + vertOffset);

				ride.setVelocity(new Vector(0, 0, 0));
				ride.teleport(rideLoc, TeleportCause.PLUGIN);
			}
		}

		if (player.getPassenger() != null) {
			final Entity rider = player.getPassenger();
			player.eject();
			rider.teleport(newLoc, TeleportCause.PLUGIN);
			player.sendMessage("Your passenger has been ejected.");
		}

		if (!returnLocationOnly) {
			Utils.teleport(player, newLoc);
		}

		if (returnLocationOnly)
			return newLoc;

		return null;
	}

	private static Location newLocation(final Player player, final Location loc) {

		final Location newLoc = correctedPosition(loc, player.isFlying(), CachedConfig.BORDER_KNOCKBACK);

		player.sendMessage(Lang.getMsg(player, "border-prefix") + Lang.getMsg(player, "ReachBorder"));

		return newLoc;
	}

	public static Location correctedPosition(final Location loc, final boolean flying, final double knockback) {

		final int size = UHCGame.getInstance().getBorder(loc.getWorld().getName());

		double xLoc = loc.getX();
		double zLoc = loc.getZ();
		double yLoc = loc.getY();

		if (xLoc <= -size) {
			xLoc = -size + knockback;
		} else if (xLoc >= size) {
			xLoc = size - knockback;
		}
		if (zLoc <= -size) {
			zLoc = -size + knockback;
		} else if (zLoc >= size) {
			zLoc = size - knockback;
		}

		final int ixLoc = Location.locToBlock(xLoc);
		final int izLoc = Location.locToBlock(zLoc);

		final Chunk tChunk = loc.getWorld().getChunkAt(CoordXZ.blockToChunk(ixLoc), CoordXZ.blockToChunk(izLoc));
		if (!tChunk.isLoaded()) {
			tChunk.load();
		}

		yLoc = getSafeY(loc.getWorld(), ixLoc, Location.locToBlock(yLoc), izLoc, flying);
		if (yLoc == -1)
			return new Location(loc.getWorld(), Math.floor(xLoc) + 0.5, loc.getWorld().getHighestBlockYAt(ixLoc, izLoc), Math.floor(zLoc) + 0.5, loc.getYaw(),
					loc.getPitch());

		return new Location(loc.getWorld(), Math.floor(xLoc) + 0.5, yLoc, Math.floor(zLoc) + 0.5, loc.getYaw(),
				loc.getPitch());
	}

	private static boolean isSafeSpot(final World world, final int X, final int Y, final int Z, final boolean flying) {
		final boolean safe = safeOpenBlocks.contains(world.getBlockTypeIdAt(X, Y, Z))
				&& safeOpenBlocks.contains(world.getBlockTypeIdAt(X, Y + 1, Z));
		if (!safe || flying)
			return safe;

		final Integer below = world.getBlockTypeIdAt(X, Y - 1, Z);
		return (safe && (!safeOpenBlocks.contains(below) || below == 8 || below == 9)
				&& !painfulBlocks.contains(below));
	}

	private static double getSafeY(final World world, final int X, int Y, final int Z, final boolean flying) {
		final boolean isNether = world.getEnvironment() == World.Environment.NETHER;
		int limTop = isNether ? 125 : world.getMaxHeight() - 2;
		final int highestBlockBoundary = Math.min(world.getHighestBlockYAt(X, Z) + 1, limTop);
		if (flying && Y > limTop && !isNether)
			return Y;

		if (Y > limTop) {
			if (isNether) {
				Y = limTop;
			} else {
				if (flying) {
					Y = limTop;
				} else {
					Y = highestBlockBoundary;
				}
			}
		}
		if (Y < limBot) {
			Y = limBot;
		}

		if (!isNether && !flying) {
			limTop = highestBlockBoundary;
		}

		for (int y1 = Y, y2 = Y; (y1 > limBot) || (y2 < limTop); y1--, y2++) {
			if (y1 > limBot) {
				if (isSafeSpot(world, X, y1, Z, flying))
					return y1;
			}

			if (y2 < limTop && y2 != y1) {
				if (isSafeSpot(world, X, y2, Z, flying))
					return y2;
			}
		}

		return -1.0;
	}
}
