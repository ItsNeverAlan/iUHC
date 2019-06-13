package spg.lgdev.uhc.border;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.Data;
import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.api.events.BorderGeneratedEvent;
import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.nms.NMSHandler;

@Data
public class BorderBuilder {

	private World world;
	private int radius;
	private int highestBlock;

	private boolean force;

	@SuppressWarnings("unused")
	private BorderBuilder() {}

	public BorderBuilder(final World world, final int radius, final int highestBlock) {
		this.world = world;
		this.radius = radius;
		this.highestBlock = highestBlock;
	}

	public BorderBuilder force() {
		force = true;
		return this;
	}

	@SuppressWarnings("deprecation")
	private void setBorderBlock(final int x, final int z) {

		Block block = world.getHighestBlockAt(x, z).getRelative(BlockFace.DOWN);

		while (isContainAbleMaterials(block.getType()) && block.getY() > 1) {
			block = block.getRelative(BlockFace.DOWN);
		}

		final Location location = block.getLocation();

		for (int i = 0 ; i < highestBlock ; i++) {
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(location.add(0, 1, 0)
					, iUHC.getInstance().getBorderStyle().getRealMaterial().getId()
					, iUHC.getInstance().getBorderStyle().getRealData(), true);
		}
	}

	public void start(final boolean callEvent) {

		final int plus = force ? 5000 : 500;

		new BukkitRunnable() {

			private int counter = -radius;
			private boolean phase1 = false;
			private boolean phase2 = false;
			private boolean phase3 = false;

			@Override
			public void run() {

				if (!phase1) {
					final int maxCounter = counter + plus;
					final int x = -radius;
					for (int z = counter; z <= radius && counter <= maxCounter; z++, counter++) {
						setBorderBlock(x, z);
					}

					if (counter >= radius) {
						counter = -radius;
						phase1 = true;
					}
					return;
				}

				if (!phase2) {
					final int maxCounter = counter + plus;
					final int x = radius;
					for (int z = counter; z <= radius && counter <= maxCounter; z++, counter++) {
						setBorderBlock(x, z);
					}

					if (counter >= radius) {
						counter = -radius;
						phase2 = true;
					}
					return;
				}

				if (!phase3) {
					final int maxCounter = counter + plus;
					final int z = -radius;
					for (int x = counter; x <= radius && counter <= maxCounter; x++, counter++) {
						if (x == radius || x == -radius) {
							continue;
						}
						setBorderBlock(x, z);
					}

					if (counter >= radius) {
						counter = -radius;
						phase3 = true;
					}
					return;
				}


				final int maxCounter = counter + plus;
				final int z = radius;
				for (int x = counter; x <= radius && counter <= maxCounter; x++, counter++) {
					if (x == radius || x == -radius) {
						continue;
					}
					setBorderBlock(x, z);
				}

				if (counter >= radius) {
					if (callEvent) {
						Bukkit.getPluginManager().callEvent(new BorderGeneratedEvent(world.getName(), world, radius));
						this.cancel();
					}
				}
			}
		}.runTaskTimer(iUHC.getInstance(), 0L, force ? 1L : CachedConfig.PerformanceMode ? 10L : 5L);
	}

	public boolean isContainAbleMaterials(final Material material) {
		return (material == Material.AIR || material == Material.LOG || material == Material.LOG_2 || material == Material.LEAVES || material == Material.LEAVES_2 || material == Material.STATIONARY_WATER);
	}

}
