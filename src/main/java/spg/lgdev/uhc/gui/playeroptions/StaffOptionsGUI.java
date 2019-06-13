package spg.lgdev.uhc.gui.playeroptions;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import spg.lgdev.uhc.gui.GUI;
import spg.lgdev.uhc.handler.Library;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.manager.TeamManager;
import spg.lgdev.uhc.player.PlayerOptions;
import spg.lgdev.uhc.util.ItemUtil;
import spg.lgdev.uhc.util.UHCSound;

public class StaffOptionsGUI extends GUI {

	public StaffOptionsGUI(final Player p) {
		super("&6&lStaff&f&l Options", 5);

		final PlayerOptions core = Library.getPlayerData(p).getOptions();

		if (!core.isNotifyDiamond()) {
			setItem(12, ItemUtil.buildItem(264, 1, 0, "&c&lMineDiamond Notify"));
		} else {
			setItem(12, ItemUtil.buildItemEnchantment(264, 1, 0, "&a&lMineDiamond Notify"));
		}

		if (!core.isNotifySpawner()) {
			setItem(13, ItemUtil.buildItem(52, 1, 0, "&c&lMineMobSpawner Notify"));
		} else {
			setItem(13, ItemUtil.buildItemEnchantment(52, 1, 0, "&a&lMineMobSpawner Notify"));
		}

		if (!core.isNotifyGold()) {
			setItem(14, ItemUtil.buildItem(266, 1, 0, "&c&lMineGold Notify"));
		} else {
			setItem(14, ItemUtil.buildItemEnchantment(266, 1, 0, "&a&lMineGold Notify"));
		}

		/**
		 *
		 */

		setItem(22, ItemUtil.buildItem(276, 1, 0, "&c&lStop StaffMode"));

		/**
		 *
		 *
		 */

		if (!core.isHideSpectators()) {
			setItem(21, ItemUtil.buildItem(160, 1, 14, "&c&lSpectatorHider"));
		} else {
			setItem(21, ItemUtil.buildItemEnchantment(160, 1, 10, "&a&lSpectatorHider"));
		}

		if (!core.isHideStaffs()) {
			setItem(23, ItemUtil.buildItem(160, 1, 14, "&c&lStaffHider"));
		} else {
			setItem(23, ItemUtil.buildItemEnchantment(160, 1, 10, "&a&lStaffHider"));
		}

		if (!core.getSpeedLevel().equalsIgnoreCase("normal")) {
			setItem(29, ItemUtil.buildItem(301, 1, 0, "&c&lReset Speed"));
		} else {
			setItem(29, ItemUtil.buildItemEnchantment(301, 1, 0, "&a&lReset Speed"));
		}

		if (!core.getSpeedLevel().equalsIgnoreCase("level1")) {
			setItem(30, ItemUtil.buildItem(305, 1, 0, "&c&lSpeed 1"));
		} else {
			setItem(30, ItemUtil.buildItemEnchantment(305, 1, 0, "&a&lSpeed 1"));
		}

		if (!core.getSpeedLevel().equalsIgnoreCase("level2")) {
			setItem(31, ItemUtil.buildItem(309, 1, 0, "&c&lSpeed 2"));
		} else {
			setItem(31, ItemUtil.buildItemEnchantment(309, 1, 0, "&a&lSpeed 2"));
		}

		if (!core.getSpeedLevel().equalsIgnoreCase("level3")) {
			setItem(32, ItemUtil.buildItem(317, 1, 0, "&c&lSpeed 3"));
		} else {
			setItem(32, ItemUtil.buildItemEnchantment(317, 1, 0, "&a&lSpeed 3"));
		}

		if (!core.getSpeedLevel().equalsIgnoreCase("level4")) {
			setItem(33, ItemUtil.buildItem(313, 1, 0, "&c&lSpeed 4"));
		} else {
			setItem(33, ItemUtil.buildItemEnchantment(313, 1, 0, "&a&lSpeed 4"));
		}

	}

	@Override
	public void onClick(final Player p, final ItemStack stack) {

		final String name = stack.getItemMeta().getDisplayName();
		final PlayerOptions core = Library.getPlayerData(p).getOptions();

		if (name.equalsIgnoreCase("§c§lMineDiamond Notify")) {

			core.setNotifyDiamond(true);
			update(p);

		} else if (name.equalsIgnoreCase("§a§lMineDiamond Notify")) {

			core.setNotifyDiamond(false);
			update(p);

		} else if (name.equalsIgnoreCase("§c§lMineGold Notify")) {

			core.setNotifyGold(true);
			update(p);

		} else if (name.equalsIgnoreCase("§a§lMineGold Notify")) {

			core.setNotifyGold(false);
			update(p);

		} else if (name.equalsIgnoreCase("§c§lMineMobSpawner Notify")) {

			core.setNotifySpawner(true);
			update(p);

		} else if (name.equalsIgnoreCase("§a§lMineMobSpawner Notify")) {

			core.setNotifySpawner(false);
			update(p);

		}

		if (name.equalsIgnoreCase("§c§lReset Speed")) {
			setSpeed(p, "NORMAL");
			update(p);
		}
		if (name.equalsIgnoreCase("§c§lSpeed 1")) {
			setSpeed(p, "LVL1");
			update(p);
		}
		if (name.equalsIgnoreCase("§c§lSpeed 2")) {
			setSpeed(p, "LVL2");
			update(p);
		}
		if (name.equalsIgnoreCase("§c§lSpeed 3")) {
			setSpeed(p, "LVL3");
			update(p);
		}
		if (name.equalsIgnoreCase("§c§lSpeed 4")) {
			setSpeed(p, "LVL4");
			update(p);
		}

		/**
		 *
		 */

		else if (name.equalsIgnoreCase("§c§lStop StaffMode")) {
			p.performCommand("staff");
		}

		/**
		 *
		 */

		else if (name.equalsIgnoreCase("§c§lSpectatorHider")) {

			core.setHideSpectators(true);
			update(p);
			for (final Player p1 : UHCGame.getInstance().getOnlineSpectators()) {
				if (p.equals(p1))
					return;
				p.hidePlayer(p1);
			}

		} else if (name.equalsIgnoreCase("§a§lSpectatorHider")) {

			core.setHideSpectators(false);
			update(p);
			for (final Player p1 : UHCGame.getInstance().getOnlineSpectators()) {
				if (p.equals(p1))
					return;
				p.showPlayer(p1);
			}

		} else if (name.equalsIgnoreCase("§c§lStaffHider")) {

			core.setHideStaffs(true);
			update(p);
			Player spec;
			for (final UUID uuid : UHCGame.getInstance().getMods()) {
				spec = Bukkit.getPlayer(uuid);
				if (spec == null || p.equals(spec)) {
					continue;
				}
				p.hidePlayer(spec);
			}

		} else if (name.equalsIgnoreCase("§a§lStaffHider")) {

			core.setHideStaffs(false);
			update(p);
			Player spec;
			for (final UUID uuid : UHCGame.getInstance().getMods()) {
				spec = Bukkit.getPlayer(uuid);
				if (spec == null || p.equals(spec)) {
					continue;
				}
				p.showPlayer(spec);
			}

		}

	}

	public void update(final Player p) {
		final PlayerOptions core = Library.getPlayerData(p).getOptions();

		if (!core.isNotifyDiamond()) {
			setItem(12, ItemUtil.buildItem(264, 1, 0, "&c&lMineDiamond Notify"));
		} else {
			setItem(12, ItemUtil.buildItemEnchantment(264, 1, 0, "&a&lMineDiamond Notify"));
		}

		if (!core.isNotifySpawner()) {
			setItem(13, ItemUtil.buildItem(52, 1, 0, "&c&lMineMobSpawner Notify"));
		} else {
			setItem(13, ItemUtil.buildItemEnchantment(52, 1, 0, "&a&lMineMobSpawner Notify"));
		}

		if (!core.isNotifyGold()) {
			setItem(14, ItemUtil.buildItem(266, 1, 0, "&c&lMineGold Notify"));
		} else {
			setItem(14, ItemUtil.buildItemEnchantment(266, 1, 0, "&a&lMineGold Notify"));
		}

		if (!core.isHideSpectators()) {
			setItem(21, ItemUtil.buildItem(160, 1, 14, "&c&lSpectatorHider"));
		} else {
			setItem(21, ItemUtil.buildItemEnchantment(160, 1, 10, "&a&lSpectatorHider"));
		}

		if (!core.isHideStaffs()) {
			setItem(23, ItemUtil.buildItem(160, 1, 14, "&c&lStaffHider"));
		} else {
			setItem(23, ItemUtil.buildItemEnchantment(160, 1, 10, "&a&lStaffHider"));
		}

		if (!core.getSpeedLevel().equalsIgnoreCase("normal")) {
			setItem(29, ItemUtil.buildItem(301, 1, 0, "&c&lReset Speed"));
		} else {
			setItem(29, ItemUtil.buildItemEnchantment(301, 1, 0, "&a&lReset Speed"));
		}

		if (!core.getSpeedLevel().equalsIgnoreCase("level1")) {
			setItem(30, ItemUtil.buildItem(305, 1, 0, "&c&lSpeed 1"));
		} else {
			setItem(30, ItemUtil.buildItemEnchantment(305, 1, 0, "&a&lSpeed 1"));
		}

		if (!core.getSpeedLevel().equalsIgnoreCase("level2")) {
			setItem(31, ItemUtil.buildItem(309, 1, 0, "&c&lSpeed 2"));
		} else {
			setItem(31, ItemUtil.buildItemEnchantment(309, 1, 0, "&a&lSpeed 2"));
		}

		if (!core.getSpeedLevel().equalsIgnoreCase("level3")) {
			setItem(32, ItemUtil.buildItem(317, 1, 0, "&c&lSpeed 3"));
		} else {
			setItem(32, ItemUtil.buildItemEnchantment(317, 1, 0, "&a&lSpeed 3"));
		}

		if (!core.getSpeedLevel().equalsIgnoreCase("level4")) {
			setItem(33, ItemUtil.buildItem(313, 1, 0, "&c&lSpeed 4"));
		} else {
			setItem(33, ItemUtil.buildItemEnchantment(313, 1, 0, "&a&lSpeed 4"));
		}
	}

	public String getStatus() {

		if (!TeamManager.getInstance().isTeamsEnabled())
			return "&c&l" + "[CHECK_BAD]".replace("[CHECK_BAD]", "\u2718");
		return "&a&l" + "[CHECK_GOOD]".replace("[CHECK_GOOD]", "\u2714");
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