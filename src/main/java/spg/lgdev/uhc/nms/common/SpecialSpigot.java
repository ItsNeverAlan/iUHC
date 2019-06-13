package spg.lgdev.uhc.nms.common;

import org.bukkit.entity.Player;

public interface SpecialSpigot {

    boolean isUsing();

    double[] getTPS();

    int getVersion(Player player);

}
