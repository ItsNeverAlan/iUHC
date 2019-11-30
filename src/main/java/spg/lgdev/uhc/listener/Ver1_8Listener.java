package spg.lgdev.uhc.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Rabbit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;

public class Ver1_8Listener implements Listener {

	@EventHandler
	public void onChunkLoad(final ChunkLoadEvent event) {
		for (final Entity entity : event.getChunk().getEntities()) {
			if (entity instanceof Rabbit || entity instanceof Guardian) {
				entity.remove();
			}
		}
	}

	@EventHandler
	public void onEntitySpawn(final EntitySpawnEvent event) {
		if (event.getEntityType() == EntityType.RABBIT || event.getEntityType() == EntityType.GUARDIAN) {
			event.setCancelled(true);
			event.getEntity().remove();
		}
	}

}
