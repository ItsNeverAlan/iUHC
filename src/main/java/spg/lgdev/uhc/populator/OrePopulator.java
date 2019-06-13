package spg.lgdev.uhc.populator;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import spg.lgdev.uhc.nms.NMSHandler;


public class OrePopulator extends BlockPopulator {
	private int stackDepth = 0;
	private final Queue<DeferredGenerateTask> deferredGenerateTasks;

	public OrePopulator() {
		this.deferredGenerateTasks = new LinkedList<>();
	}

	@Override
	public void populate(final World world, final Random random, final Chunk chunk) {
		applyGenerateRules(world, random, chunk);
		if (this.stackDepth == 0) {
			while (this.deferredGenerateTasks.size() > 0) {
				final DeferredGenerateTask task = this.deferredGenerateTasks.remove();
				task.execute();
			}
		}
	}

	private void applyGenerateRules(final World world, final Random random, final Chunk chunk) {

		if (this.stackDepth > 0) {
			this.deferredGenerateTasks.add(new DeferredGenerateTask(world, random, chunk.getX(), chunk.getZ()));
			return;
		}
		this.stackDepth += 1;
		for (final GenRule rule : GenRule.rules) {
			for (int i = 0; i < rule.getRounds(); i++)
				if (random.nextInt(100) <= rule.getPropability()) {
					try {
						final int x = chunk.getX() * 16 + random.nextInt(16);
						final int y = rule.getMinHeight() + random.nextInt(rule.getMaxHeight() - rule.getMinHeight());
						final int z = chunk.getZ() * 16 + random.nextInt(16);
						generate(world, random, x, y, z, rule.getSize(), rule.getMaterial());
					} catch (final NullPointerException ignored) {
					}

				}
		}
		this.stackDepth -= 1;

	}

	private void generate(final World world, final Random rand, final int x, final int y, final int z, final int size, final Material material) {
		final double rpi = rand.nextDouble() * 3.141592653589793D;
		final double x1 = x + 8 + Math.sin(rpi) * size / 8.0D;
		final double x2 = x + 8 - Math.sin(rpi) * size / 8.0D;
		final double z1 = z + 8 + Math.cos(rpi) * size / 8.0D;
		final double z2 = z + 8 - Math.cos(rpi) * size / 8.0D;
		final double y1 = y + rand.nextInt(3) + 2;
		final double y2 = y + rand.nextInt(3) + 2;
		for (int i = 0; i <= size; i++) {
			final double xPos = x1 + (x2 - x1) * i / size;
			final double yPos = y1 + (y2 - y1) * i / size;
			final double zPos = z1 + (z2 - z1) * i / size;
			final double fuzz = rand.nextDouble() * size / 16.0D;
			final double fuzzXZ = (Math.sin((float) (i * 3.141592653589793D / size)) + 1.0D) * fuzz + 1.0D;
			final double fuzzY = (Math.sin((float) (i * 3.141592653589793D / size)) + 1.0D) * fuzz + 1.0D;
			final int xStart = (int) Math.floor(xPos - fuzzXZ / 2.0D);
			final int yStart = (int) Math.floor(yPos - fuzzY / 2.0D);
			final int zStart = (int) Math.floor(zPos - fuzzXZ / 2.0D);
			final int xEnd = (int) Math.floor(xPos + fuzzXZ / 2.0D);
			final int yEnd = (int) Math.floor(yPos + fuzzY / 2.0D);
			final int zEnd = (int) Math.floor(zPos + fuzzXZ / 2.0D);
			for (int ix = xStart; ix <= xEnd; ix++) {
				final double xThresh = (ix + 0.5D - xPos) / (fuzzXZ / 2.0D);
				if (xThresh * xThresh < 1.0D) {
					for (int iy = yStart; iy <= yEnd; iy++) {
						final double yThresh = (iy + 0.5D - yPos) / (fuzzY / 2.0D);
						if (xThresh * xThresh + yThresh * yThresh < 1.0D) {
							for (int iz = zStart; iz <= zEnd; iz++) {
								final double zThresh = (iz + 0.5D - zPos) / (fuzzXZ / 2.0D);
								if (xThresh * xThresh + yThresh * yThresh + zThresh * zThresh < 1.0D) {
									NMSHandler.getInstance().getNMSControl().placeOre(ix, iy, iz, world, material);
								}
							}
						}
					}
				}
			}
		}
	}

	//	private Block tryGetBlock(final World world, final int x, final int y, final int z) {
	//		final int cx = x >> 4;
	//								final int cz = z >> 4;
	//						if ((!world.isChunkLoaded(cx, cz)) && (!world.loadChunk(cx, cz, false))) return null;
	//						final Chunk chunk = world.getChunkAt(cx, cz);
	//						if (chunk == null) return null;
	//						return chunk.getBlock(x & 0xF, y, z & 0xF);
	//	}

	private class DeferredGenerateTask {
		private final World world;
		private final Random random;
		private final int cx;
		private final int cz;

		public DeferredGenerateTask(final World world, final Random random, final int cx, final int cz) {
			this.world = world;
			this.random = random;
			this.cx = cx;
			this.cz = cz;
		}

		public void execute() {
			OrePopulator.this.applyGenerateRules(this.world, this.random, this.world.getChunkAt(this.cx, this.cz));
		}
	}
}