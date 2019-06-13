package spg.lgdev.uhc.gui.playeroptions;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import spg.lgdev.uhc.gui.GUI;
import spg.lgdev.uhc.handler.Library;
import spg.lgdev.uhc.handler.Permissions;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.player.PlayerOptions;
import spg.lgdev.uhc.util.ItemUtil;
import spg.lgdev.uhc.util.UHCSound;

public class SpectatorOptionsGUI extends GUI {

	public SpectatorOptionsGUI(final Player p) {
		super("&6&lSpectator&f&l Options", 3);

		final PlayerOptions core = Library.getPlayerData(p).getOptions();

		if (!core.getSpeedLevel().equalsIgnoreCase("normal")) {
			setItem(2, ItemUtil.buildItem(301, 1, 0, "&c&lReset Speed"));
		} else {
			setItem(2, ItemUtil.buildItemEnchantment(301, 1, 0, "&a&lReset Speed"));
		}

		if (!core.getSpeedLevel().equalsIgnoreCase("level1")) {
			setItem(3, ItemUtil.buildItem(305, 1, 0, "&c&lSpeed 1"));
		} else {
			setItem(3, ItemUtil.buildItemEnchantment(305, 1, 0, "&a&lSpeed 1"));
		}

		if (!core.getSpeedLevel().equalsIgnoreCase("level2")) {
			setItem(4, ItemUtil.buildItem(309, 1, 0, "&c&lSpeed 2"));
		} else {
			setItem(4, ItemUtil.buildItemEnchantment(309, 1, 0, "&a&lSpeed 2"));
		}

		if (!core.getSpeedLevel().equalsIgnoreCase("level3")) {
			setItem(5, ItemUtil.buildItem(317, 1, 0, "&c&lSpeed 3"));
		} else {
			setItem(5, ItemUtil.buildItemEnchantment(317, 1, 0, "&a&lSpeed 3"));
		}

		if (!core.getSpeedLevel().equalsIgnoreCase("level4")) {
			setItem(6, ItemUtil.buildItem(313, 1, 0, "&c&lSpeed 4"));
		} else {
			setItem(6, ItemUtil.buildItemEnchantment(313, 1, 0, "&a&lSpeed 4"));
		}

		/**
		 *
		 */

		if (!core.isNightVision()) {
			setItem(12, ItemUtil.buildItem(381, 1, 0, "&c&lNightVision"));
		} else {
			setItem(12, ItemUtil.buildItemEnchantment(381, 1, 0, "&a&lNightVision"));
		}

		if (!core.isHideSpectators()) {
			setItem(13, ItemUtil.buildItem(160, 1, 14, "&c&lSpectatorHider"));
		} else {
			setItem(13, ItemUtil.buildItemEnchantment(160, 1, 10, "&a&lSpectatorHider"));
		}

		if (!core.isFly()) {
			setItem(14, ItemUtil.buildItem(288, 1, 0, "&c&lFly Enabled"));
		} else {
			setItem(14, ItemUtil.buildItemEnchantment(288, 1, 0, "&a&lFly Enabled"));
		}

		if (p.hasPermission(Permissions.ADMIN)) {
			setItem(22, ItemUtil.buildItem(Material.GOLD_HELMET, 1, 0, "&e&lStaffMode",
					"&7Click me to enable staff mode!"));
		}

	}

	@Override
	public void onClick(final Player p, final ItemStack i) {

		final String name = i.getItemMeta().getDisplayName();

		if (name.equalsIgnoreCase("§c§lReset Speed")) {
			setSpeed(p, "NORMAL");
		}
		if (name.equalsIgnoreCase("§c§lSpeed 1")) {
			setSpeed(p, "LVL1");
		}
		if (name.equalsIgnoreCase("§c§lSpeed 2")) {
			setSpeed(p, "LVL2");
		}
		if (name.equalsIgnoreCase("§c§lSpeed 3")) {
			setSpeed(p, "LVL3");
		}
		if (name.equalsIgnoreCase("§c§lSpeed 4")) {
			setSpeed(p, "LVL4");
		}
		if (name.equalsIgnoreCase("§c§lNightVision")) {
			setNightVision(p, true);
		}
		if (name.equalsIgnoreCase("§a§lNightVision")) {
			setNightVision(p, false);
		}
		if (name.equalsIgnoreCase("§c§lSpectatorHider")) {
			setHiderSpecs(p, true);
		}
		if (name.equalsIgnoreCase("§a§lSpectatorHider")) {
			setHiderSpecs(p, false);
		}
		if (name.equalsIgnoreCase("§c§lFly Enabled")) {
			setFly(p, true);
		}
		if (name.equalsIgnoreCase("§a§lFly Enabled")) {
			setFly(p, false);
		}
		if (name.equalsIgnoreCase("§e§lStaffMode")) {
			p.closeInventory();
			p.performCommand("staff");
			return;
		}

		update(p);

	}

	public void update(final Player p) {
		final PlayerOptions core = Library.getPlayerData(p).getOptions();

		if (!core.getSpeedLevel().equalsIgnoreCase("normal")) {
			setItem(2, ItemUtil.buildItem(301, 1, 0, "&c&lReset Speed"));
		} else {
			setItem(2, ItemUtil.buildItemEnchantment(301, 1, 0, "&a&lReset Speed"));
		}

		if (!core.getSpeedLevel().equalsIgnoreCase("level1")) {
			setItem(3, ItemUtil.buildItem(305, 1, 0, "&c&lSpeed 1"));
		} else {
			setItem(3, ItemUtil.buildItemEnchantment(305, 1, 0, "&a&lSpeed 1"));
		}

		if (!core.getSpeedLevel().equalsIgnoreCase("level2")) {
			setItem(4, ItemUtil.buildItem(309, 1, 0, "&c&lSpeed 2"));
		} else {
			setItem(4, ItemUtil.buildItemEnchantment(309, 1, 0, "&a&lSpeed 2"));
		}

		if (!core.getSpeedLevel().equalsIgnoreCase("level3")) {
			setItem(5, ItemUtil.buildItem(317, 1, 0, "&c&lSpeed 3"));
		} else {
			setItem(5, ItemUtil.buildItemEnchantment(317, 1, 0, "&a&lSpeed 3"));
		}

		if (!core.getSpeedLevel().equalsIgnoreCase("level4")) {
			setItem(6, ItemUtil.buildItem(313, 1, 0, "&c&lSpeed 4"));
		} else {
			setItem(6, ItemUtil.buildItemEnchantment(313, 1, 0, "&a&lSpeed 4"));
		}

		/**
		 *
		 */

		if (!core.isNightVision()) {
			setItem(12, ItemUtil.buildItem(381, 1, 0, "&c&lNightVision"));
		} else {
			setItem(12, ItemUtil.buildItemEnchantment(381, 1, 0, "&a&lNightVision"));
		}

		if (!core.isHideSpectators()) {
			setItem(13, ItemUtil.buildItem(160, 1, 14, "&c&lSpectatorHider"));
		} else {
			setItem(13, ItemUtil.buildItemEnchantment(160, 1, 10, "&a&lSpectatorHider"));
		}

		if (!core.isFly()) {
			setItem(14, ItemUtil.buildItem(288, 1, 0, "&c&lFly Enabled"));
		} else {
			setItem(14, ItemUtil.buildItemEnchantment(288, 1, 0, "&a&lFly Enabled"));
		}

		if (p.hasPermission(Permissions.ADMIN)) {
			setItem(22, ItemUtil.buildItem(Material.GOLD_HELMET, 1, 0, "&e&lStaffMode",
					"&7Click me to enable staff mode!"));
		}
	}

	public void setFly(final Player p, final boolean bol) {

		final PlayerOptions core = Library.getPlayerData(p).getOptions();

		if (bol) {
			core.setFly(true);
			p.setAllowFlight(true);
			p.setFlying(true);
			UHCSound.NOTE_PLING.playSound(p);
		} else {
			core.setFly(false);
			p.setAllowFlight(false);
			p.setFlying(false);
			UHCSound.NOTE_PLING.playSound(p);
		}

	}

	public void setHiderSpecs(final Player p, final boolean bol) {

		final PlayerOptions core = Library.getPlayerData(p).getOptions();

		if (bol) {
			core.setHideSpectators(true);
			for (final Player p1 : UHCGame.getInstance().getOnlineSpectators()) {
				if (p.equals(p1))
					return;
				p.hidePlayer(p1);
			}
			UHCSound.NOTE_PLING.playSound(p);
		} else {
			core.setHideSpectators(false);
			for (final Player p1 : UHCGame.getInstance().getOnlineSpectators()) {
				if (p.equals(p1))
					return;
				p.showPlayer(p1);
			}
			UHCSound.NOTE_PLING.playSound(p);
		}

	}

	public void setNightVision(final Player p, final boolean bol) {

		final PlayerOptions core = Library.getPlayerData(p).getOptions();

		if (bol) {
			core.setNightVision(true);
			p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 999999, 2));
			UHCSound.NOTE_PLING.playSound(p);
		} else {
			core.setNightVision(false);
			p.removePotionEffect(PotionEffectType.NIGHT_VISION);
			UHCSound.NOTE_PLING.playSound(p);
		}

	}

	public void setSpeed(final Player p, final String level) {

		final PlayerOptions core = Library.getPlayerData(p).getOptions();

		UHCSound.NOTE_PLING.playSound(p);

		if (level.equalsIgnoreCase("NORMAL")) {
			core.setSpeedLevel("normal");
			p.removePotionEffect(PotionEffectType.SPEED);
		}
		if (level.equalsIgnoreCase("LVL1")) {
			core.setSpeedLevel("level1");
			p.removePotionEffect(PotionEffectType.SPEED);
			p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 0));
		}
		if (level.equalsIgnoreCase("LVL2")) {
			core.setSpeedLevel("level2");
			p.removePotionEffect(PotionEffectType.SPEED);
			p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 1));
		}
		if (level.equalsIgnoreCase("LVL3")) {
			core.setSpeedLevel("level3");
			p.removePotionEffect(PotionEffectType.SPEED);
			p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 2));
		}
		if (level.equalsIgnoreCase("LVL4")) {
			core.setSpeedLevel("level4");
			p.removePotionEffect(PotionEffectType.SPEED);
			p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 3));
		}

	}

}