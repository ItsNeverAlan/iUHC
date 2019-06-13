package spg.lgdev.uhc.nms.v1_8_R3;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.event.world.WorldInitEvent;

import com.google.common.base.Throwables;

import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.nms.common.BiomeReplacer;
import net.minecraft.server.v1_8_R3.BiomeBase;
import net.minecraft.server.v1_8_R3.GenLayer;
import net.minecraft.server.v1_8_R3.GenLayerRegionHills;
import net.minecraft.server.v1_8_R3.IntCache;
import net.minecraft.server.v1_8_R3.WorldChunkManager;

public class BiomeReplacer1_8 implements BiomeReplacer {

    private static final BiomeBase BIOME = BiomeBase.PLAINS;
    private static final Field GEN_LAYER_FIELD;

    static {
        GEN_LAYER_FIELD = findField(WorldChunkManager.class, GenLayer.class, 0);
    }

    private static <T extends GenLayer> void findAndReplaceLayer(GenLayer base, Class<T> replacedType, Function<T, GenLayer> replacementProvider) {
        try {
            if (!findAndReplaceLayer0(base, replacedType, replacementProvider)) {
                throw new IllegalStateException("Unable to find the layer: " + replacedType.getName());
            }
        } catch (final Exception e) {
            throw Throwables.propagate(e);
        }
    }

    private static <T extends GenLayer> boolean findAndReplaceLayer0(GenLayer base, Class<T> replacedType, Function<T, GenLayer> replacementProvider) throws Exception {
        final List<GenLayer> children = new ArrayList<>();
        Class<?> clazz = base.getClass();
        while (clazz != Object.class) {
            for (final Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.getType().isAssignableFrom(GenLayer.class)) {
                    final GenLayer child = (GenLayer) field.get(base);
                    if (child != null) {
                        if (replacedType.isInstance(child)) {
                            field.set(base, replacementProvider.apply(replacedType.cast(child)));
                            return true;
                        }
                        children.add(child);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }

        for (final GenLayer child : children) {
            if (findAndReplaceLayer0(child, replacedType, replacementProvider)) {
                return true;
            }
        }

        return false;
    }

    private static Field findField(Class<?> target, Class<?> fieldType, int index) {
        for (final Field field : target.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getType().isAssignableFrom(fieldType)) {
                if (index == 0) {
                    return field;
                }
                index--;
            }
        }
        throw new IllegalArgumentException("Unable to find a field of type " + fieldType.getName() + " at index " + index + " in the target class " + target.getName());
    }

    @Override
    public void initWorld(WorldInitEvent event) {
        final World world = event.getWorld();
        if (!world.getName().equals("UHCArena")) {
            return;
        }
        iUHC.getInstance().log(false, " [DEBUG] Biomes have been replaced to the plains in the center.");
        final net.minecraft.server.v1_8_R3.World mcWorld = ((CraftWorld) world).getHandle();
        final WorldChunkManager biomeManager = mcWorld.getWorldChunkManager();
        try {
            final GenLayer baseLayer = (GenLayer) GEN_LAYER_FIELD.get(biomeManager);
            findAndReplaceLayer(baseLayer, GenLayerRegionHills.class, BiomeSpawnLayer::new);
        } catch (final Exception e) {
            throw Throwables.propagate(e);
        }
    }

    private static class BiomeSpawnLayer extends GenLayer {
        BiomeSpawnLayer(GenLayer base) {
            super(0L);
            this.a = base;
        }
        @Override
        public int[] a(int areaX, int areaY, int areaWidth, int areaHeight) {
            final int SIZE_FACTOR = CachedConfig.MandatorySize;
            final int[] biomeIds = this.a.a(areaX, areaY, areaWidth, areaHeight);
            final int maxX = areaX + areaWidth;
            final int maxY = areaY + areaHeight;
            if (areaX > SIZE_FACTOR || areaY > SIZE_FACTOR || maxX < -SIZE_FACTOR || maxY < -SIZE_FACTOR) {
                return biomeIds;
            }
            final int area = areaWidth * areaHeight;
            final int[] outBiomeIds = IntCache.a(area);
            System.arraycopy(biomeIds, 0, outBiomeIds, 0, area);
            final int startX = Math.max(0, areaX - Math.max(areaX, -SIZE_FACTOR));
            final int startY = Math.max(0, areaY - Math.max(areaY, -SIZE_FACTOR));
            final int offX = areaWidth - (maxX - Math.min(maxX, SIZE_FACTOR));
            final int offY = areaHeight - (maxY - Math.min(maxY, SIZE_FACTOR));
            final int biomeId = BIOME.id;
            for (int i = startX; i < offX; i++) {
                for (int j = startY; j < offY; j++) {
                    outBiomeIds[i + j * areaWidth] = biomeId;
                }
            }
            outBiomeIds[BiomeBase.RIVER.id] = biomeId;
            outBiomeIds[BiomeBase.FROZEN_RIVER.id] = biomeId;
            return outBiomeIds;
        }
    }

}