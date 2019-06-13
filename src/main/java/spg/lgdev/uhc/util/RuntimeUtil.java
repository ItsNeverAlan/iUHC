package spg.lgdev.uhc.util;

import spg.lgdev.uhc.nms.NMSHandler;
import org.bukkit.Bukkit;

import java.text.DecimalFormat;

public class RuntimeUtil {

    private static final DecimalFormat format = new DecimalFormat("00.00");
    private static Class<?> clazz = null;
    private static Object si = null;
    private static Runtime rt = Runtime.getRuntime();
    private static int fillMemoryTolerance = 500;

    static {
        try {
            clazz = Class.forName("net.minecraft.server." + NMSHandler.getInstance().getNMSVersion() + "." + "MinecraftServer");
            si = clazz.getMethod("getServer").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static double getTPS(int time) {
        return Bukkit.spigot().getTPS()[time];
    }

    public static DecimalFormat getTPSFormat() {
        return format;
    }

    public static long Now() {
        return System.currentTimeMillis();
    }

    public static int AvailableMemory() {
        return (int) ((rt.maxMemory() - rt.totalMemory() + rt.freeMemory()) / 1048576);
    }

    public static boolean AvailableMemoryTooLow() {
        return AvailableMemory() < fillMemoryTolerance;
    }


}
