package spg.lgdev.uhc.nms.common;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;


public interface SitHandler {

    public void spawn(Player p, Location loc);

    public void removeHorses(Player p);

    public boolean isSet(UUID u);

}
