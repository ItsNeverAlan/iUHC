package spg.lgdev.uhc.nms.common;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import spg.lgdev.uhc.util.signgui.SignUpdate;

public interface NMSControl {

	void setAllowedFly(Boolean bl);

	void setWorldBorder1_8(World w, int size);

	float getAbsorptionHearts(Player p);

	void setAbsorptionHearts(Player p, float hearts);

	void pickup(Player p, ItemStack itemStack);

	void setScatterEffects(Player p);

	void clearScatterEffects(Player p);

	void sendPacket(Player player, Object packet);

	void openSignEditor(Player p, Location l);

	SignUpdate getSignUpdate(Object packet);

	void clearTab(Player player);

	void setMotd(String motd);

	void fastSync(Runnable runnable);

	int getPing(Player player);

	boolean canFall(MaterialData materialData);

	boolean isLiquid(MaterialData materialData);

	void changeMenuTitle(Player player, String title);

	void placeOre(int x, int y, int z, final World world, Material material);

}
