package spg.lgdev.uhc.handler.movement;

import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.border.BorderRadius;
import spg.lgdev.handler.MovementHandler;

public class CustomMovementHandler implements MovementHandler {

	@Override
	public void handleUpdateLocation(final Player player, final Location from, final Location to, PacketPlayInFlying packetPlayInFlying) {
        if (to.getBlockX() == from.getBlockX() && to.getBlockY() == from.getBlockY()
                && to.getBlockZ() == from.getBlockZ())
            return;
		BorderRadius.checkPlayer(player, null, false);
	}

    @Override
    public void handleUpdateRotation(Player player, Location location, Location location1, PacketPlayInFlying packetPlayInFlying) {
        
    }
}
