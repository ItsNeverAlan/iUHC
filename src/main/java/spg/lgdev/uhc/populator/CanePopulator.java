package spg.lgdev.uhc.populator;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

import spg.lgdev.uhc.config.CachedConfig;

public class CanePopulator extends BlockPopulator {

	private final BlockFace[] faces = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
	private final int canePatchChance;
	private final Material cane;

	public CanePopulator() {
		this.canePatchChance = CachedConfig.getCanePercent();
		cane = Material.SUGAR_CANE_BLOCK;
	}

	@Override
	public void populate(final World world, final Random random, final Chunk source) {

		if (Math.random() * 100 <= canePatchChance) {
			for (int i = 0; i < 16; i++) {
				Block b;
				if (random.nextBoolean()) {
					b = getHighestBlock(source, random.nextInt(16), i);
				} else {
					b = getHighestBlock(source, i, random.nextInt(16));
				}

				if (b != null) {
					createCane(b, random);
				}
			}
		}
	}

	public void createCane(final Block b, final Random rand) {
		boolean create = false;
		for (final BlockFace face : faces) {
			if (b.getRelative(face).getType().name().toLowerCase().contains("water")) {
				create = true;
			}
		}
		if (!create)
			return;

		for (int i = 1; i < rand.nextInt(3) + 3; i++) {
			b.getRelative(0, i, 0).setType(cane);
		}
	}

	public Block getHighestBlock(final Chunk chunk, final int x, final int z) {
		Block block = null;
		for (int i = chunk.getWorld().getMaxHeight(); i >= 0; i--) {
			if ((block = chunk.getBlock(x, i, z)).getTypeId() == 9 || (block = chunk.getBlock(x, i, z)).getTypeId() == 8)
				return null;
			if ((block = chunk.getBlock(x, i, z)).getTypeId() == 2 || (block = chunk.getBlock(x, i, z)).getTypeId() == 12)
				return block;
		}
		return null;
	}

}