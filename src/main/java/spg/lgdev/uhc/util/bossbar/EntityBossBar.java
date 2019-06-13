package spg.lgdev.uhc.util.bossbar;

import static spg.lgdev.uhc.util.reflection.minecraft.DataWatcher.V1_9.ValueType.ENTITY_FLAG;
import static spg.lgdev.uhc.util.reflection.minecraft.DataWatcher.V1_9.ValueType.ENTITY_LIVING_HEALTH;
import static spg.lgdev.uhc.util.reflection.minecraft.DataWatcher.V1_9.ValueType.ENTITY_NAME;
import static spg.lgdev.uhc.util.reflection.minecraft.DataWatcher.V1_9.ValueType.ENTITY_NAME_VISIBLE;
import static spg.lgdev.uhc.util.reflection.minecraft.DataWatcher.V1_9.ValueType.ENTITY_WIHER_b;
import static spg.lgdev.uhc.util.reflection.minecraft.DataWatcher.V1_9.ValueType.ENTITY_WITHER_a;
import static spg.lgdev.uhc.util.reflection.minecraft.DataWatcher.V1_9.ValueType.ENTITY_WITHER_bw;
import static spg.lgdev.uhc.util.reflection.minecraft.DataWatcher.V1_9.ValueType.ENTITY_WITHER_c;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import lombok.Getter;
import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.nms.NMSHandler;
import spg.lgdev.uhc.util.reflection.NMSClass;
import spg.lgdev.uhc.util.reflection.minecraft.DataWatcher;

public class EntityBossBar implements BossBar {

	protected static int ENTITY_DISTANCE = 32;

	protected final int ID;
	protected final UUID uuid;

	protected final Player receiver;
	protected String message;
	protected float health;
	protected float healthMinus;
	protected float minHealth = 1;

	protected Location location;

	@Getter
	protected World world;
	protected boolean visible = false;
	protected Object dataWatcher;

	protected EntityBossBar(final Player player, final String message, final float percentage, final int timeout, final float minHealth) {
		this.ID = iUHC.getRandom().nextInt();
		this.uuid = UUID.randomUUID();

		this.receiver = player;
		this.message = message;
		this.health = percentage * this.getMaxHealth();
		this.minHealth = minHealth;
		this.world = player.getWorld();
		this.location = this.makeLocation(player.getLocation());

	}

	protected Location makeLocation(final Location base) {
		return base.getDirection().multiply(ENTITY_DISTANCE).add(base.toVector()).toLocation(this.world);
	}

	@Override
	public Player getReceiver() {
		return receiver;
	}

	@Override
	public float getMaxHealth() {
		return 300;
	}

	@Override
	public void setHealth(final float percentage) {
		this.health = percentage * this.getMaxHealth();
		if (this.health <= this.minHealth) {
			BossBarAPI.removeBar(this.receiver);
		} else {
			this.sendMetadata();
		}
	}

	@Override
	public float getHealth() {
		return health;
	}

	@Override
	public void setMessage(final String message) {
		this.message = message;
		if (this.isVisible()) {
			this.sendMetadata();
		}
	}

	@Override
	public Collection<? extends Player> getPlayers() {
		return Collections.singletonList(getReceiver());
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public Location getLocation() {
		return this.location;
	}

	@Override
	public void setVisible(final boolean flag) {
		if (flag == this.visible)
			return;
		if (flag) {
			this.spawn();
		} else {
			this.destroy();
		}
	}

	@Override
	public boolean isVisible() {
		return this.visible;
	}

	@Override
	public void setProgress(final float progress) {
		setHealth(progress * 100);
	}

	@Override
	public float getProgress() {
		return getHealth() / 100;
	}

	@Override
	public void updateMovement() {
		if (!this.visible)
			return;
		this.location = this.makeLocation(this.receiver.getLocation());
		try {
			final Object packet = ClassBuilder.buildTeleportPacket(this.ID, this.getLocation(), false, false);
			NMSHandler.getInstance().getNMSControl().sendPacket(receiver, packet);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	protected void updateDataWatcher() {
		if (this.dataWatcher == null) {
			try {
				this.dataWatcher = DataWatcher.newDataWatcher(null);
				DataWatcher.setValue(this.dataWatcher, 17, ENTITY_WITHER_a, new Integer(0));
				DataWatcher.setValue(this.dataWatcher, 18, ENTITY_WIHER_b, new Integer(0));
				DataWatcher.setValue(this.dataWatcher, 19, ENTITY_WITHER_c, new Integer(0));

				DataWatcher.setValue(this.dataWatcher, 20, ENTITY_WITHER_bw, new Integer(1000));// Invulnerable time
				// (1000 = very small)
				DataWatcher.setValue(this.dataWatcher, 0, ENTITY_FLAG, Byte.valueOf((byte) (0 | 1 << 5)));
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
		try {
			DataWatcher.setValue(this.dataWatcher, 6, ENTITY_LIVING_HEALTH, this.health);

			DataWatcher.setValue(this.dataWatcher, 10, ENTITY_NAME, this.message);
			DataWatcher.setValue(this.dataWatcher, 2, ENTITY_NAME, this.message);

			DataWatcher.setValue(this.dataWatcher, 11, ENTITY_NAME_VISIBLE, (byte) 1);
			DataWatcher.setValue(this.dataWatcher, 3, ENTITY_NAME_VISIBLE, (byte) 1);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	protected void sendMetadata() {
		this.updateDataWatcher();
		try {
			final Object metaPacket = ClassBuilder.buildNameMetadataPacket(this.ID, this.dataWatcher, 2, 3,
					this.message);
			NMSHandler.getInstance().getNMSControl().sendPacket(receiver, metaPacket);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	protected void spawn() {
		try {
			this.updateMovement();
			this.updateDataWatcher();
			final Object packet = ClassBuilder.buildWitherSpawnPacket(this.ID, this.uuid, this.getLocation(),
					this.dataWatcher);
			NMSHandler.getInstance().getNMSControl().sendPacket(receiver, packet);
			this.visible = true;
			this.sendMetadata();
			this.updateMovement();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	protected void destroy() {
		try {
			final Object packet = NMSClass.PacketPlayOutEntityDestroy.getConstructor(int[].class)
					.newInstance(new int[] { this.ID });
			NMSHandler.getInstance().getNMSControl().sendPacket(receiver, packet);
			this.visible = false;
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

}
