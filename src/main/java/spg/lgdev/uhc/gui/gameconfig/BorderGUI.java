package spg.lgdev.uhc.gui.gameconfig;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.gui.GUI;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.util.ItemUtil;
import spg.lgdev.uhc.util.UHCSound;

public class BorderGUI extends GUI {

	public BorderGUI(final Player p) {
		super("&eBorder Settings", 3);
		setItem(13, ItemUtil.buildItem(Material.PAPER, 1, 0, "&e&lStatus:", "&fBorderSize: &e" + UHCGame.getInstance().getBorderRadius()));

		setItem(19, ItemUtil.buildItem(Material.ENCHANTED_BOOK, 1, 0, "&a&l100x100", ""));
		setItem(20, ItemUtil.buildItem(Material.ENCHANTED_BOOK, 1, 0, "&e&l500x500", ""));
		setItem(21, ItemUtil.buildItem(Material.ENCHANTED_BOOK, 1, 0, "&6&l1000x1000", ""));
		setItem(22, ItemUtil.buildItem(Material.ENCHANTED_BOOK, 1, 0, "&6&l1500x1500", ""));
		setItem(23, ItemUtil.buildItem(Material.ENCHANTED_BOOK, 1, 0, "&6&l2000x2000", ""));
		setItem(24, ItemUtil.buildItem(Material.ENCHANTED_BOOK, 1, 0, "&e&l2500x2500", ""));
		setItem(25, ItemUtil.buildItem(Material.ENCHANTED_BOOK, 1, 0, "&a&l3000x3000", ""));

	}

	@Override
	public void onClick(final Player p, final ItemStack stack) {

		final String name = stack.getItemMeta().getDisplayName();

		if (name.contains("§a§l100")) {

			UHCGame.getInstance().setBorderRadius(100);

		} else if (name.contains("§e§l500x500")) {

			UHCGame.getInstance().setBorderRadius(500);

		} else if (name.contains("§6§l1000x1000")) {

			UHCGame.getInstance().setBorderRadius(1000);

		} else if (name.contains("§6§l1500x1500")) {

			UHCGame.getInstance().setBorderRadius(1500);

		} else if (name.contains("§6§l2000x2000")) {

			UHCGame.getInstance().setBorderRadius(2000);

		} else if (name.contains("§e§l2500x2500")) {

			UHCGame.getInstance().setBorderRadius(2500);

		} else if (name.contains("§a§l3000x3000")) {

			UHCGame.getInstance().setBorderRadius(3000);

		}

		UHCSound.CLICK.playSound(p);
		CachedConfig.setStatsBorder();
		updateInv();

	}

	private void updateInv() {
		setItem(13, ItemUtil.buildItem(Material.PAPER, 1, 0, "&e&lStatus:", "&fBorderSize: &e" + UHCGame.getInstance().getBorderRadius()));

	}

}
