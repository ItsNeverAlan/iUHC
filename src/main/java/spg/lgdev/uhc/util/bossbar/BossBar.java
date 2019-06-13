package spg.lgdev.uhc.util.bossbar;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;

public interface BossBar {

    /**
     * @return The players which can see the BossBar
     */
    Collection<? extends Player> getPlayers();

    /**
     * @return the message
     */
    String getMessage();

    void setMessage(String message);

    /**
     * @return whether the BossBar is visible
     */
    boolean isVisible();

    /**
     * @param flag whether the BossBar is visible
     */
    void setVisible(boolean flag);

    /**
     * @return the progress (0.0 - 1.0)
     */
    float getProgress();

    /**
     * @param progress the new progress (0.0 - 1.0)
     */
    void setProgress(float progress);

    float getMaxHealth();

    float getHealth();

    void setHealth(float percentage);

    Player getReceiver();

    Location getLocation();

    void updateMovement();

    World getWorld();
}
