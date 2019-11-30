package spg.lgdev.uhc.nms.v1_8_R3;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.scheduler.BukkitRunnable;
import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.google.common.base.Predicate;

import spg.lgdev.uhc.nms.common.NMSControl;
import spg.lgdev.uhc.util.reflection.ReflectionUtils;
import spg.lgdev.uhc.util.signgui.SignUpdate;

public class NMSControl1_8 implements NMSControl {

	private Method methodAdd = null;
	private Method methodRemove = null;
	private MobEffect slow;
	private MobEffect jump;

	public NMSControl1_8() {
		try {
			methodAdd = ReflectionUtils.makeMethod(EntityPlayer.class, "a", MobEffect.class);
			slow = new MobEffect(MobEffectList.SLOWER_MOVEMENT.id, 9999, 255, true, true);
			jump = new MobEffect(MobEffectList.JUMP.id, 9999, 255, true, true);

			methodRemove = ReflectionUtils.makeMethod(EntityPlayer.class, "b", MobEffect.class);
			slow = new MobEffect(MobEffectList.SLOWER_MOVEMENT.id, 9999, 255, true, true);
			jump = new MobEffect(MobEffectList.JUMP.id, 9999, 255, true, true);
		} catch (final Throwable e) {
			throw new IllegalAccessError("failed to load nms");
		}
	}

	@Override
	public void setAllowedFly(final Boolean bl) {
		((CraftServer) iUHC.getInstance().getServer()).getHandle().getServer().setAllowFlight(bl);
	}

	@Override
	public void setWorldBorder1_8(final World w, final int size) {
		final WorldBorder border = w.getWorldBorder();
		border.setCenter(0, 0);
		border.setSize(size);
	}

	@Override
	public float getAbsorptionHearts(final Player p) {
		return ((CraftPlayer) p).getHandle().getAbsorptionHearts();
	}

	@Override
	public void setAbsorptionHearts(final Player p, final float hearts) {
		((CraftPlayer) p).getHandle().setAbsorptionHearts(hearts);
	}

	@Override
	public void pickup(final Player p, final ItemStack itemStack) {
		if (itemStack == null || p == null)
			return;
		final EntityPlayer entityPlayer = ((CraftPlayer) p).getHandle();
		final net.minecraft.server.v1_8_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
		if (entityPlayer.inventory.pickup(nmsItemStack)) {
			entityPlayer.defaultContainer.b();
			return;
		}
		EntityItem entityItem = entityPlayer.drop(nmsItemStack, false);
		if (entityItem != null) {
			entityItem.q();
			entityItem.b(entityPlayer.getName());
		}
	}

	@Override
	public void setScatterEffects(final Player p) {
		ReflectionUtils.callMethod(methodAdd, ((CraftPlayer) p).getHandle(), slow);
		ReflectionUtils.callMethod(methodAdd, ((CraftPlayer) p).getHandle(), jump);
	}

	@Override
	public void clearScatterEffects(final Player p) {
		ReflectionUtils.callMethod(methodRemove, ((CraftPlayer) p).getHandle(), slow);
		ReflectionUtils.callMethod(methodRemove, ((CraftPlayer) p).getHandle(), jump);
	}

	public void broadcast(final Packet<?> packet) {

		for (final Object o : Bukkit.getOnlinePlayers()) {
			final EntityPlayer entityplayer = ((CraftPlayer) o).getHandle();

			entityplayer.playerConnection.sendPacket(packet);
		}

	}

	@Override
	public void sendPacket(final Player player, final Object packet) {
		if (!(packet instanceof Packet))
			return;
		((CraftPlayer) player).getHandle().playerConnection.sendPacket((Packet<?>) packet);
	}

	@Override
	public void openSignEditor(final Player p, final Location l) {
		final BlockPosition bp = new BlockPosition(((CraftPlayer) p).getHandle());
		final PacketPlayOutOpenSignEditor packet = new PacketPlayOutOpenSignEditor(bp);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}

	@Override
	public SignUpdate getSignUpdate(final Object packet) {
		final PacketPlayInUpdateSign signPacket = (PacketPlayInUpdateSign) packet;
		final BlockPosition bp = signPacket.a();
		final String[] text = new String[4];
		for (int i = 0; i < signPacket.b().length; i++) {
			final IChatBaseComponent chat = signPacket.b()[i];
			text[i] = chat.getText();
		}
		return new SignUpdate(new Location(Bukkit.getWorlds().get(0), bp.getX(), bp.getY(), bp.getZ()), text);
	}

	@Override
	public void clearTab(final Player player) {
	}

	@Override
	public void setMotd(final String motd) {
		MinecraftServer.getServer().setMotd(motd);
	}

	@Override
	public void fastSync(final Runnable runnable) {
		MinecraftServer.getServer().postToMainThread(runnable);
	}

	@Override
	public int getPing(final Player player) {
		return ((CraftPlayer)player).getHandle().ping;
	}

	@Override
	public boolean canFall(final MaterialData materialData) {
		return Block.getById(materialData.getItemTypeId() << 4 | materialData.getData()) instanceof BlockFalling;
	}

	@Override
	public boolean isLiquid(final MaterialData materialData) {
		return Block.getById(materialData.getItemTypeId() << 4 | materialData.getData()).getMaterial().isLiquid();
	}

	Predicate<IBlockData> blockPredicate = BlockPredicate.a(Blocks.STONE);

	@Override
	public void placeOre(final int x, final int y, final int z, final World world, final Material material) {
		final BlockPosition blockPosition = new BlockPosition(x, y, z);
		if (blockPredicate.apply(((CraftWorld)world).getHandle().getType(blockPosition))) {
			final IBlockData ibd = net.minecraft.server.v1_8_R3.Block.getByCombinedId(material.getId());
			((CraftWorld)world).getHandle().setTypeAndData(blockPosition, ibd, 2);
		}
	}

	@Override
	public void showDyingNPC(Player player) {
		Location loc = player.getLocation();
		final List<Player> players = new ArrayList<>();
		for (Player other : Bukkit.getOnlinePlayers()) {
			if (other != player && other.getWorld() == player.getWorld() && other.getLocation().distanceSquared(loc) < 32 * 32) {
				players.add(other);
			}
		}
		EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
		PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn(entityPlayer);
		int i = Entity.getEntityCount() + 1;
		Entity.setEntityCount(i);
		net.development.mitw.utils.reflection.ReflectionUtils.setField(packet, "a", i);
		PacketPlayOutEntityStatus statusPacket = new PacketPlayOutEntityStatus();
		net.development.mitw.utils.reflection.ReflectionUtils.setField(statusPacket, "a", i);
		net.development.mitw.utils.reflection.ReflectionUtils.setField(statusPacket, "b", (byte) 3);
		PacketPlayOutEntityDestroy destoryPacket = new PacketPlayOutEntityDestroy(i);

		players.forEach(other -> {
			((CraftPlayer) other).getHandle().playerConnection.sendPacket(packet);
			((CraftPlayer) other).getHandle().playerConnection.sendPacket(statusPacket);
		});

		new BukkitRunnable() {
			@Override
			public void run() {
				players.forEach(other -> {
					((CraftPlayer) other).getHandle().playerConnection.sendPacket(destoryPacket);
				});
			}
		}.runTaskLater(iUHC.getInstance(), 20L);
	}
}
