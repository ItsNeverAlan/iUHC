package spg.lgdev.uhc.gui.gameconfig;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import spg.lgdev.uhc.enums.InventoryTypes;
import spg.lgdev.uhc.gui.GUI;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.manager.InventoryManager;
import spg.lgdev.uhc.util.ItemUtil;
import spg.lgdev.uhc.util.UHCSound;
import spg.lgdev.uhc.util.Utils;

public class TimerGUI extends GUI {

	public TimerGUI(final Player p) {
		super("&6&lTimer &f&lSettings", 4);

		setItem(26, ItemUtil.buildItem(35, 1, 14, "&cBack"));

		setItem(2, ItemUtil.buildItem(160, 1, 14, "&c-5", ""));
		setItem(3, ItemUtil.buildItem(160, 1, 14, "&c-1", ""));

		setItem(5, ItemUtil.buildItem(160, 1, 5, "&a+1", ""));
		setItem(6, ItemUtil.buildItem(160, 1, 5, "&a+5", ""));

		setItem(11, ItemUtil.buildItem(160, 1, 14, "&4-5", ""));
		setItem(12, ItemUtil.buildItem(160, 1, 14, "&4-1", ""));

		setItem(14, ItemUtil.buildItem(160, 1, 5, "&2+1", ""));
		setItem(15, ItemUtil.buildItem(160, 1, 5, "&2+5", ""));

		setItem(20, ItemUtil.buildItem(160, 1, 14, "&c-5 ", ""));
		setItem(21, ItemUtil.buildItem(160, 1, 14, "&c-1 ", ""));

		setItem(23, ItemUtil.buildItem(160, 1, 5, "&a+1 ", ""));
		setItem(24, ItemUtil.buildItem(160, 1, 5, "&a+5 ", ""));

		setItem(29, ItemUtil.buildItem(160, 1, 14, "&c-5  ", ""));
		setItem(30, ItemUtil.buildItem(160, 1, 14, "&c-1  ", ""));

		setItem(32, ItemUtil.buildItem(160, 1, 5, "&a+1  ", ""));
		setItem(33, ItemUtil.buildItem(160, 1, 5, "&a+5  ", ""));

		setItem(4, ItemUtil.buildItem(347, 1, 0, "&fPVP Timer: &6&l<tiempoPVE> mins".replaceAll("<tiempoPVE>", "" + UHCGame.getInstance().getPvpTime() / 60)));

		setItem(13, ItemUtil.buildItem(347, 1, 0, "&fFinal Heal Timer: &6&l<tiempoFH> mins".replaceAll("<tiempoFH>", "" + UHCGame.getInstance().getFinalHealTime() / 60)));

		setItem(22, ItemUtil.buildItem(7, 1, 0, "&fFirst Border Timer: &6&l" + Utils.formatTimeHours(UHCGame.getInstance().getFirstBorder())));

		setItem(31, ItemUtil.buildItem(Material.CHEST, 1, 0, "&fTimeBomb Timer:&6&l " + UHCGame.getInstance().getTimebombTimer() + "s"));

	}

	@Override
	public void onClick(final Player p, final ItemStack stack) {

		final String name = stack.getItemMeta().getDisplayName();

		final UHCGame game = UHCGame.getInstance();

		final int i = game.getPvpTime();
		final int i2 = game.getFinalHealTime();
		final int i3 = game.getFirstBorder();

		if (name.equals("§c-5")) {

			game.setPvpTime(i - 300 < 600 ? 600 : i - 300);

		} else if (name.equals("§c-1")) {

			game.setPvpTime(i - 60 < 600 ? 600 : i - 60);

		} else if (name.equals("§a+1")) {

			game.setPvpTime(i + 60 > 1800 ? 1800 : i + 60);

		} else if (name.equals("§a+5")) {

			game.setPvpTime(i + 300 > 1800 ? 1800 : i + 300);

		} else if (name.contains("§4-5")) {

			game.setFinalHealTime(i2 - 300 < 300 ? 300 : i2 - 300);

		} else if (name.contains("§4-1")) {

			game.setFinalHealTime(i2 - 60 < 300 ? 300 : i2 - 60);

		} else if (name.contains("§2+1")) {

			game.setFinalHealTime(i2 + 60 > 1800 ? 1800 : i2 + 60);

		} else if (name.contains("§2+5")) {

			game.setFinalHealTime(i2 + 300 > 1800 ? 1800 : i2 + 300);

		} else if (name.contains("§cBack")) {

			InventoryManager.instance.createInventory(p, InventoryTypes.Config_Editor);

		} else if (name.equals("§c-5 ")) {

			game.setFirstBorder(i3 - 300);

		} else if (name.equals("§c-1 ")) {

			game.setFirstBorder(i3 - 60);

		} else if (name.equals("§a+1 ")) {

			game.setFirstBorder(i3 + 60);

		} else if (name.equals("§a+5 ")) {

			game.setFirstBorder(i3 + 300);

		} else if (name.equals("§c-5  ")) {

			game.setTimebombTimer(game.getTimebombTimer() - 5);

		} else if (name.equals("§c-1  ")) {

			game.setTimebombTimer(game.getTimebombTimer() - 1);

		} else if (name.equals("§a+1  ")) {

			game.setTimebombTimer(game.getTimebombTimer() + 1);

		} else if (name.equals("§a+5  ")) {

			game.setTimebombTimer(game.getTimebombTimer() + 5);

		}

		UHCSound.CLICK.playSound(p);

		updateInv(game);
		InventoryManager.instance.config();

	}

	private void updateInv(final UHCGame game) {
		setItem(4, ItemUtil.buildItem(347, 1, 0, "&fPVP Timer: &6&l<tiempoPVE> mins".replaceAll("<tiempoPVE>", "" + game.getPvpTime() / 60)));

		setItem(13, ItemUtil.buildItem(347, 1, 0, "&fFinal Heal Timer: &6&l<tiempoFH> mins".replaceAll("<tiempoFH>", "" + game.getFinalHealTime() / 60)));

		setItem(22, ItemUtil.buildItem(7, 1, 0, "&fFirst Border Timer: &6&l" + Utils.formatTimeHours(game.getFirstBorder())));

		setItem(31, ItemUtil.buildItem(Material.CHEST, 1, 0, "&fTimeBomb Timer:&6&l " + game.getTimebombTimer() + "s", ""));

	}

}

