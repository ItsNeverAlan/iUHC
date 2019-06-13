package spg.lgdev.uhc.world;

import java.util.List;

import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.enums.ServerVersion;
import spg.lgdev.uhc.nms.NMSHandler;

public class Biomes {

	private Biomes() {}

	public static enum BIOME_THRESHOLD {
		DISALLOWED, LIMITED, ALLOWED
	}

	public static BIOME_THRESHOLD isValidBiome(final Block block) {
		final Biome biome = block.getBiome();
		final int i = block.getX();
		final int j = block.getZ();

		final boolean flag = i <= 100 && i >= -100 && j <= 100 && j >= -100;
		if(ServerVersion.isUnder(ServerVersion.get(), ServerVersion.v1_9_R1)) {
			if (biome == Biome.DESERT || biome == Biome.DESERT_HILLS || biome == Biome.PLAINS || biome == Biome.SWAMPLAND || biome == Biome.SAVANNA)
				return BIOME_THRESHOLD.ALLOWED;
			else if (flag && (biome == Biome.FOREST || biome == Biome.RIVER || biome == Biome.FROZEN_RIVER || biome == Biome.FOREST_HILLS
					|| biome == Biome.BIRCH_FOREST || biome == Biome.BIRCH_FOREST_HILLS || biome == Biome.TAIGA_HILLS || biome == Biome.COLD_BEACH))
				return BIOME_THRESHOLD.LIMITED;
			else if (flag && (biome == Biome.ROOFED_FOREST || biome == Biome.MESA || biome == Biome.ICE_MOUNTAINS))
				return BIOME_THRESHOLD.DISALLOWED;
		} else {
			if (biome == Biome.DESERT || biome == Biome.DESERT_HILLS || biome == Biome.valueOf("MUTATED_DESERT")
					|| biome == Biome.PLAINS || biome == Biome.valueOf("MUTATED_PLAINS") || biome == Biome.SWAMPLAND
					|| biome == Biome.valueOf("MUTATED_SWAMPLAND") || biome == Biome.SAVANNA
					|| biome == Biome.valueOf("MUTATED_SAVANNA") || biome == Biome.valueOf("SAVANNA_ROCK") || biome == Biome.valueOf("SAVANNA_ROCK")
					|| biome == Biome.valueOf("ICE_FLATS"))
				return BIOME_THRESHOLD.ALLOWED;
			else if (flag && (biome == Biome.FOREST || biome == Biome.RIVER || biome == Biome.FROZEN_RIVER
					|| biome == Biome.FOREST_HILLS || biome == Biome.BIRCH_FOREST || biome == Biome.BIRCH_FOREST_HILLS
					|| biome == Biome.valueOf("MUTATED_BIRCH_FOREST_HILLS") || biome == Biome.valueOf("MUTATED_BIRCH_FOREST")
					|| biome == Biome.TAIGA_HILLS || biome == Biome.valueOf("MUTATED_TAIGA") || biome == Biome.valueOf("ICE_FLATS")
					|| biome == Biome.valueOf("MUTATED_REDWOOD_TAIGA") || biome == Biome.valueOf("MUTATED_REDWOOD_TAIGA_HILLS")
					|| biome == Biome.valueOf("REDWOOD_TAIGA") || biome == Biome.valueOf("TAIGA_COLD_HILLS") || biome == Biome.COLD_BEACH
					|| biome == Biome.valueOf("TAIGA_COLD_HILLS") || biome == Biome.valueOf("MUTATED_TAIGA_COLD")))
				return BIOME_THRESHOLD.LIMITED;
			else if (flag && (biome == Biome.ROOFED_FOREST || biome == Biome.valueOf("MUTATED_ROOFED_FOREST") || biome == Biome.MESA || biome == Biome.valueOf("MESA_ROCK") || biome == Biome.valueOf("MUTATED_SAVANNA") || biome == Biome.valueOf("MUTATED_MESA") || biome == Biome.EXTREME_HILLS || biome == Biome.valueOf("TAIGA_COLD") || biome == Biome.valueOf("EXTREME_HILLS_WITH_TREES") || biome == Biome.TAIGA || biome == Biome.valueOf("MUTATED_EXTREME_HILLS") || biome == Biome.FROZEN_OCEAN || biome == Biome.valueOf("TAIGA_COLD_HILLS") || biome == Biome.ICE_MOUNTAINS))
				return BIOME_THRESHOLD.DISALLOWED;
		}

		return flag ? BIOME_THRESHOLD.DISALLOWED : BIOME_THRESHOLD.ALLOWED;
	}

	public static void setupBiomes(final iUHC plugin) {

		try {

			final List<String> swaps = plugin.getFileManager().getPopulators().getStringList("biomeswap.swaps");

			for (final String swap : swaps) {

				final String[] split = swap.split(";");
				if (split.length < 2) {
					continue;
				}

				if (split[0].equals("ALL")) {

					NMSHandler.getInstance().getBiomeHandler().swap(split[1]);

					continue;

				}

				NMSHandler.getInstance().getBiomeHandler().swap(split[0], split[1]);

			}

		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

}
