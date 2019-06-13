package spg.lgdev.uhc.nms.v1_8_R3;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.bukkit.Bukkit;

import spg.lgdev.uhc.nms.common.BiomeHandler;
import net.minecraft.server.v1_8_R3.BiomeBase;

public class Biomeswap1_8 implements BiomeHandler {

	public static BiomeBase getBiome(final String b) {
		try {
			return (BiomeBase) BiomeBase.class.getField(b).get(null);
		} catch (final IllegalAccessError | IllegalArgumentException | IllegalAccessException | NoSuchFieldException
				| SecurityException ex) {
			Bukkit.getConsoleSender().sendMessage("Invalid biome: " + b);
			return BiomeBase.PLAINS;
		}
	}

	public BiomeBase[] copy;

	public Biomeswap1_8() {
		try {
			final Field biomeF = BiomeBase.class.getDeclaredField("biomes");
			biomeF.setAccessible(true);
			copy = getMcBiomesCopy();
		} catch (final NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void swap(final String from, final String to) {
		getMcBiomes()[getBiome(from).id] = copy[getBiome(to).id];
	}

	@Override
	public void swap(final String to) {
		for (int i = 0; i < getMcBiomes().length; i++) {
			if (i != getBiome(to).id && getMcBiomes()[i] != null) {
				getMcBiomes()[i] = copy[getBiome(to).id];
			}
		}
	}

	private BiomeBase[] getMcBiomesCopy() {
		final BiomeBase[] b = getMcBiomes();
		return Arrays.copyOf(b, b.length);
	}

	private BiomeBase[] getMcBiomes() {
		try {
			final Field biomeF = BiomeBase.class.getDeclaredField("biomes");
			biomeF.setAccessible(true);
			return (BiomeBase[]) biomeF.get(null);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			e.printStackTrace();
		}
		return new BiomeBase[256];
	}

}
