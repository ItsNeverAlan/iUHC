package spg.lgdev.uhc.player;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.Data;
import spg.lgdev.uhc.border.BorderRadius;
import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.handler.game.KitsHandler;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.manager.ArenaManager;
import spg.lgdev.uhc.nms.NMSHandler;
import spg.lgdev.uhc.util.CustomLocation;

@Data
public class PlayerData {

	private ItemStack[] contents;
	private ItemStack[] armorContents;
	private CustomLocation location;

	private double health;
	private float absorptionHearts;

	private int foodLevel;

	private int expLevel;
	private float exp;

	public PlayerData(final Player p) {

		set(p);

	}

	public PlayerData() {

		setContents(KitsHandler.getInstance().getContent());
		setArmorContents(KitsHandler.getInstance().getArmor());
		setLocation(CustomLocation.fromBukkitLocation(ArenaManager.getInstance().getScatterLocation(Bukkit.getWorld("UHCArena"))));

		setHealth(20.0D);
		setAbsorptionHearts(0.0F);

		setFoodLevel(20);
		setExpLevel(0);
		setExp(0);
	}

	public void set(final Player p) {

		setContents(p.getInventory().getContents());
		setArmorContents(p.getInventory().getArmorContents());
		setLocation(CustomLocation.fromBukkitLocation(p.getLocation()));

		setHealth(p.getHealth());
		setAbsorptionHearts(NMSHandler.getInstance().getNMSControl().getAbsorptionHearts(p));

		setFoodLevel(p.getFoodLevel());

		setExpLevel(p.getLevel());
		setExp(p.getExp());

	}

	public void restore(final Player p, final boolean healthSet) {

		p.getInventory().setContents(getContents());
		p.getInventory().setArmorContents(getArmorContents());

		Location location = getLocation().toBukkitLocation();
		if (!UHCGame.getInstance().insideBorder(location)) {
			location = BorderRadius.correctedPosition(location, false, CachedConfig.BORDER_KNOCKBACK);
			setLocation(CustomLocation.fromBukkitLocation(p.getLocation()));
		}

		p.teleport(location);

		p.setLevel(getExpLevel());
		p.setExp(getExp());

		p.setFoodLevel(getFoodLevel());

		NMSHandler.getInstance().getNMSControl().setAbsorptionHearts(p, getAbsorptionHearts());
		iUHC.getInstance().getProfileManager().getProfile(p.getUniqueId()).setSuperHero(p);

		if (healthSet) {
			p.setHealth(getHealth());
		}

	}

	public void restoreWithoutTeleport(final Player p, final boolean healthSet) {

		p.getInventory().setContents(getContents());
		p.getInventory().setArmorContents(getArmorContents());

		Location location = getLocation().toBukkitLocation();
		if (!UHCGame.getInstance().insideBorder(location)) {
			location = BorderRadius.correctedPosition(location, false, CachedConfig.BORDER_KNOCKBACK);
			setLocation(CustomLocation.fromBukkitLocation(p.getLocation()));
		}

		p.setLevel(getExpLevel());
		p.setExp(getExp());

		p.setFoodLevel(getFoodLevel());

		NMSHandler.getInstance().getNMSControl().setAbsorptionHearts(p, getAbsorptionHearts());
		iUHC.getInstance().getProfileManager().getProfile(p.getUniqueId()).setSuperHero(p);

		if (healthSet) {
			p.setHealth(getHealth());
		}

	}

}
