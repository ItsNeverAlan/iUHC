package spg.lgdev.uhc.util;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import spg.lgdev.uhc.enums.ServerVersion;

public class WorldUtil {

	public static void entityDeathDrops(final EntityDeathEvent e, final int experience) {
		final Location location = e.getEntity().getLocation();
		switch (e.getEntityType()) {
		case COW: {
			e.getDrops().clear();
			drop(location, Material.COOKED_BEEF, 2 * experience, 2 * experience);
			drop(location, Material.LEATHER, 2 * experience, 2 * experience);
			break;
		}
		case SHEEP: {
			e.getDrops().clear();
			drop(location, Material.COOKED_BEEF, 2 * experience, experience);
			if (iUHC.getRandom().nextInt(100) > 4) {
				break;
			}
			drop(location, Material.STRING, experience, experience);
			break;
		}
		case HORSE: {
			e.getDrops().clear();
			drop(location, Material.COOKED_BEEF, 2 * experience, 2 * experience);
			drop(location, Material.LEATHER, 2 * experience, 2 * experience);
			break;
		}
		case CHICKEN: {
			e.getDrops().clear();
			drop(location, Material.COOKED_CHICKEN, 2 * experience, experience);
			drop(location, Material.FEATHER, 2 * experience, experience);
			break;
		}
		case PIG: {
			e.getDrops().clear();
			drop(location, Material.GRILLED_PORK, 2 * experience, 2 * experience);
			break;
		}
		case WITCH: {
			e.getDrops().clear();
			break;
		}
		default:
			break;
		}
	}

	private static void drop(final Location location, final Material material, final int amount, final int experience) {
		final Item item = location.getWorld().dropItemNaturally(location, new ItemStack(material, amount));
		if (ServerVersion.is1_7()) {
			item.setVelocity(new Vector(0, 0, 0));
		}
		dropExperience(location, experience);
	}

	public static void dropBlockbreaks(final BlockBreakEvent event, final Material material, final int amount,
			final int experience, final boolean withXp) {
		if (event.isCancelled())
			return;

		final Location loc = event.getBlock().getLocation().add(0.5f, material == Material.OBSIDIAN ? 0.9f : 0.3f,
				0.5f);

		event.getBlock().setType(Material.AIR);
		final Item item = loc.getWorld().dropItemNaturally(loc, new ItemStack(material, amount));

		if (ServerVersion.is1_7()) {
			item.setVelocity(new Vector(0, 0, 0));
		}

		if (withXp) {
			dropExperience(loc.clone(), experience);
		}
	}

	public static void dropExperience(final Location location, final int experience) {
		if (experience == 0)
			return;
		location.getWorld().spawn(location, ExperienceOrb.class).setExperience(experience);
	}

}
