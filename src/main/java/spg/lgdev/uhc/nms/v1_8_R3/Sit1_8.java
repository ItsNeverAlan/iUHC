package spg.lgdev.uhc.nms.v1_8_R3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.nms.common.SitHandler;
import spg.lgdev.uhc.util.FastUUID;
import net.minecraft.server.v1_8_R3.EntityPig;
import net.minecraft.server.v1_8_R3.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;

public class Sit1_8 implements SitHandler {

	public static Map<String, Integer> horses = new HashMap<>();

	@Override
	public void spawn(final Player p, final Location loc) {
		final Location l = p.getLocation();
		final EntityPig horse = new EntityPig(((CraftWorld) l.getWorld()).getHandle());

		horse.setLocation(l.getX(), l.getY(), l.getZ(), 0, 0);
		horse.setInvisible(true);

		final PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(horse);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);

		horses.put(FastUUID.toString(p.getUniqueId()), horse.getId());

		final PacketPlayOutAttachEntity sit = new PacketPlayOutAttachEntity(0,
				((CraftPlayer) p).getHandle(), horse);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(sit);
	}

	@Override
	public void removeHorses(final Player p) {

		final String uuid = FastUUID.toString(p.getUniqueId());

		if (horses.containsKey(uuid)) {
			final PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(horses.get(uuid));
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
			horses.remove(uuid);
		}
	}

	@Override
	public boolean isSet(final UUID u) {
		return horses.containsKey(FastUUID.toString(u));
	}

}