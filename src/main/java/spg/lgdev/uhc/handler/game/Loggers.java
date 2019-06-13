package spg.lgdev.uhc.handler.game;

import java.util.UUID;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import spg.lgdev.uhc.listener.MainListener;
import spg.lgdev.uhc.player.PlayerData;
import spg.lgdev.uhc.player.PlayerProfile;

public class Loggers {

	private static Loggers instance;

	public Loggers(final iUHC plugin) {
		instance = this;
	}

	public static Loggers getInstance() {
		return instance;
	}

	public void spawnLogger(final Player p) {

		final World world = p.getWorld();
		final Villager entity = (Villager) world.spawnEntity(p.getLocation(), EntityType.VILLAGER);

		entity.setCustomName("§c" + p.getName());
		entity.setMaxHealth(20.0);
		entity.setHealth(p.getHealth());

		entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 100));
		entity.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 100));

		if (!MainListener.keepChunks.contains(entity.getLocation().getChunk())) {
			MainListener.keepChunks.add(entity.getLocation().getChunk());
		}

		iUHC.getInstance().getProfileManager().getProfile(p.getUniqueId()).setCombatLoggerUUID(entity.getUniqueId());

	}

	public boolean removeEntity(final Player p) {

		final PlayerProfile profile = iUHC.getInstance().getProfileManager().getProfile(p.getUniqueId());
		final UUID uuid = profile.getCombatLoggerUUID();

		if (uuid == null)
			return false;

		final PlayerData data = profile.getData();
		final World world = data == null ? Bukkit.getWorld("UHCArena") : data.getLocation().toBukkitWorld();

		for (final Entity entity1 : world.getEntities())

			if (entity1 instanceof Villager) {

				final Villager entity = (Villager) entity1;

				if (entity.getUniqueId().equals(uuid)) {

					p.setHealth(entity.getHealth());
					entity.remove();

					profile.setCombatLoggerUUID(null);

					MainListener.keepChunks.remove(entity.getLocation().getChunk());

					return true;

				}

			}

		profile.setCombatLoggerUUID(null);
		return false;

	}

}
