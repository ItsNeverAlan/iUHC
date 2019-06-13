package spg.lgdev.uhc.util.bossbar;

import java.util.UUID;

import org.bukkit.Location;

import spg.lgdev.uhc.util.MathUtil;
import spg.lgdev.uhc.util.reflection.NMSClass;
import spg.lgdev.uhc.util.reflection.minecraft.DataWatcher;
import spg.lgdev.uhc.util.reflection.minecraft.Minecraft;
import spg.lgdev.uhc.util.reflection.minecraft.DataWatcher.V1_9;
import spg.lgdev.uhc.util.reflection.util.AccessUtil;

public abstract class ClassBuilder {

	public static Object buildWitherSpawnPacket(final int id, final UUID uuid/*UUID*/, final Location loc, final Object dataWatcher) throws Exception {
		final Object packet = NMSClass.PacketPlayOutSpawnEntityLiving.newInstance();

		try {

			AccessUtil.setAccessible(NMSClass.PacketPlayOutSpawnEntityLiving.getDeclaredField("a")).set(packet, id);
			AccessUtil.setAccessible(NMSClass.PacketPlayOutSpawnEntityLiving.getDeclaredField("b")).set(packet, 64);// TODO: Find correct entity type id
			AccessUtil.setAccessible(NMSClass.PacketPlayOutSpawnEntityLiving.getDeclaredField("c")).set(packet, (int) loc.getX());
			AccessUtil.setAccessible(NMSClass.PacketPlayOutSpawnEntityLiving.getDeclaredField("d")).set(packet, MathUtil.floor(loc.getY() * 32D));
			AccessUtil.setAccessible(NMSClass.PacketPlayOutSpawnEntityLiving.getDeclaredField("e")).set(packet, (int) loc.getZ());

			AccessUtil.setAccessible(NMSClass.PacketPlayOutSpawnEntityLiving.getDeclaredField("i")).set(packet, (byte) MathUtil.d(loc.getYaw() * 256F / 360F));
			AccessUtil.setAccessible(NMSClass.PacketPlayOutSpawnEntityLiving.getDeclaredField("j")).set(packet, (byte) MathUtil.d(loc.getPitch() * 256F / 360F));
			AccessUtil.setAccessible(NMSClass.PacketPlayOutSpawnEntityLiving.getDeclaredField("k")).set(packet, (byte) MathUtil.d(loc.getPitch() * 256F / 360F));
			AccessUtil.setAccessible(NMSClass.PacketPlayOutSpawnEntityLiving.getDeclaredField("l")).set(packet, dataWatcher);

		} catch (final Exception n) {

			try {

				AccessUtil.setAccessible(NMSClass.PacketPlayOutSpawnEntityLiving.getDeclaredField("id")).set(packet, id);
				AccessUtil.setAccessible(NMSClass.PacketPlayOutSpawnEntityLiving.getDeclaredField("type")).set(packet, 64);// TODO: Find correct entity type id
				AccessUtil.setAccessible(NMSClass.PacketPlayOutSpawnEntityLiving.getDeclaredField("x")).set(packet, (int) loc.getX());
				AccessUtil.setAccessible(NMSClass.PacketPlayOutSpawnEntityLiving.getDeclaredField("y")).set(packet, MathUtil.floor(loc.getY() * 32D));
				AccessUtil.setAccessible(NMSClass.PacketPlayOutSpawnEntityLiving.getDeclaredField("z")).set(packet, (int) loc.getZ());

				AccessUtil.setAccessible(NMSClass.PacketPlayOutSpawnEntityLiving.getDeclaredField("yaw")).set(packet, (byte) MathUtil.d(loc.getYaw() * 256F / 360F));
				AccessUtil.setAccessible(NMSClass.PacketPlayOutSpawnEntityLiving.getDeclaredField("pitch")).set(packet, (byte) MathUtil.d(loc.getPitch() * 256F / 360F));
				AccessUtil.setAccessible(NMSClass.PacketPlayOutSpawnEntityLiving.getDeclaredField("headYaw")).set(packet, (byte) MathUtil.d(loc.getPitch() * 256F / 360F));
				AccessUtil.setAccessible(NMSClass.PacketPlayOutSpawnEntityLiving.getDeclaredField("l")).set(packet, dataWatcher);

			} catch (final Exception e) {
				e.printStackTrace();
			}

		}

		return packet;
	}

	public static Object buildNameMetadataPacket(final int id, final Object dataWatcher, final int nameIndex, final int visibilityIndex, final String name) throws Exception {
		DataWatcher.setValue(dataWatcher, nameIndex, V1_9.ValueType.ENTITY_NAME, name != null ? name : "");
		DataWatcher.setValue(dataWatcher, visibilityIndex, V1_9.ValueType.ENTITY_NAME_VISIBLE, Minecraft.VERSION.olderThan(Minecraft.Version.v1_9_R1) ? (byte) (name != null && !name.isEmpty() ? 1 : 0) : (name != null && !name.isEmpty()));//Byte < 1.9, Boolean >= 1.9
		final Object metaPacket = NMSClass.PacketPlayOutEntityMetadata.getConstructor(int.class, NMSClass.DataWatcher, boolean.class).newInstance(id, dataWatcher, true);

		return metaPacket;
	}

	public static Object updateEntityLocation(final Object entity, final Location loc) throws Exception {
		NMSClass.Entity.getDeclaredField("locX").set(entity, loc.getX());
		NMSClass.Entity.getDeclaredField("locY").set(entity, loc.getY());
		NMSClass.Entity.getDeclaredField("locZ").set(entity, loc.getZ());
		return entity;
	}

	//	public static Object buildDataWatcher(@Nullable Object entity) throws Exception {
	//		Object dataWatcher = NMSClass.DataWatcher.getConstructor(NMSClass.Entity).newInstance(entity);
	//		return dataWatcher;
	//	}
	//
	//	public static Object buildWatchableObject(int index, Object value) throws Exception {
	//		return buildWatchableObject(getDataWatcherValueType(value), index, value);
	//	}
	//
	//	public static Object buildWatchableObject(int type, int index, Object value) throws Exception {
	//		return NMSClass.WatchableObject.getConstructor(int.class, int.class, Object.class).newInstance(type, index, value);
	//	}
	//
	//	public static Object setDataWatcherValue(Object dataWatcher, int index, Object value) throws Exception {
	//		Object type = getDataWatcherValueType(value);
	//
	//		Object map = AccessUtil.setAccessible(NMSClass.DataWatcher.getDeclaredField("dataValues")).get(dataWatcher);
	//		NMUClass.gnu_trove_map_hash_TIntObjectHashMap.getDeclaredMethod("put", int.class, Object.class).invoke(map, index, buildWatchableObject(type, index, value));
	//
	//		return dataWatcher;
	//	}
	//
	//	public static Object getDataWatcherValue(Object dataWatcher, int index) throws Exception {
	//		Object map = AccessUtil.setAccessible(NMSClass.DataWatcher.getDeclaredField("dataValues")).get(dataWatcher);
	//		Object value = NMUClass.gnu_trove_map_hash_TIntObjectHashMap.getDeclaredMethod("get", int.class).invoke(map, index);
	//
	//		return value;
	//	}
	//
	//	public static int getWatchableObjectIndex(Object object) throws Exception {
	//		int index = AccessUtil.setAccessible(NMSClass.WatchableObject.getDeclaredField("b")).getInt(object);
	//		return index;
	//	}
	//
	//	public static int getWatchableObjectType(Object object) throws Exception {
	//		int type = AccessUtil.setAccessible(NMSClass.WatchableObject.getDeclaredField("a")).getInt(object);
	//		return type;
	//	}
	//
	//	public static Object getWatchableObjectValue(Object object) throws Exception {
	//		Object value = AccessUtil.setAccessible(NMSClass.WatchableObject.getDeclaredField("c")).get(object);
	//		return value;
	//	}
	//
	//	public static Object getDataWatcherValueType(Object value) {
	//		int type = 0;
	//		if (value instanceof Number) {
	//			if (value instanceof Byte) {
	//				type = 0;
	//			} else if (value instanceof Short) {
	//				type = 1;
	//			} else if (value instanceof Integer) {
	//				type = 2;
	//			} else if (value instanceof Float) {
	//				type = 3;
	//			}
	//		} else if (value instanceof String) {
	//			type = 4;
	//		} else if (value != null && value.getClass().equals(NMSClass.ItemStack)) {
	//			type = 5;
	//		} else if (value != null && (value.getClass().equals(NMSClass.ChunkCoordinates) || value.getClass().equals(NMSClass.BlockPosition))) {
	//			type = 6;
	//		} else if (value != null && value.getClass().equals(NMSClass.Vector3f)) {
	//			type = 7;
	//		}
	//
	//		return type;
	//	}

	public static Object buildArmorStandSpawnPacket(final Object armorStand) throws Exception {
		final Object spawnPacket = NMSClass.PacketPlayOutSpawnEntityLiving.getConstructor(NMSClass.EntityLiving).newInstance(armorStand);
		AccessUtil.setAccessible(NMSClass.PacketPlayOutSpawnEntityLiving.getDeclaredField("b")).setInt(spawnPacket, 30);

		return spawnPacket;
	}

	public static Object buildTeleportPacket(final int id, final Location loc, final boolean onGround, final boolean heightCorrection) throws Exception {
		final Object packet = NMSClass.PacketPlayOutEntityTeleport.newInstance();
		AccessUtil.setAccessible(NMSClass.PacketPlayOutEntityTeleport.getDeclaredField("a")).set(packet, id);
		AccessUtil.setAccessible(NMSClass.PacketPlayOutEntityTeleport.getDeclaredField("b")).set(packet, (int) (loc.getX() * 32D));
		AccessUtil.setAccessible(NMSClass.PacketPlayOutEntityTeleport.getDeclaredField("c")).set(packet, (int) (loc.getY() * 32D));
		AccessUtil.setAccessible(NMSClass.PacketPlayOutEntityTeleport.getDeclaredField("d")).set(packet, (int) (loc.getZ() * 32D));
		AccessUtil.setAccessible(NMSClass.PacketPlayOutEntityTeleport.getDeclaredField("e")).set(packet, (byte) (int) (loc.getYaw() * 256F / 360F));
		AccessUtil.setAccessible(NMSClass.PacketPlayOutEntityTeleport.getDeclaredField("f")).set(packet, (byte) (int) (loc.getPitch() * 256F / 360F));

		return packet;
	}
}
