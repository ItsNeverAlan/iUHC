package spg.lgdev.uhc.gui.spectator;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import spg.lgdev.uhc.gui.GUI;
import spg.lgdev.uhc.util.ItemUtil;
import spg.lgdev.uhc.util.StringUtil;

public class SimpleInventorySnapshot extends GUI {

	private final String name;

	public SimpleInventorySnapshot(final Player target) {

		super(target.getName(), 5);

		name = target.getName();

		getInventory().setContents(target.getInventory().getContents());

		for (int i = 0; i < target.getInventory().getArmorContents().length; i++) {
			setItem(4 * 9 + i, target.getInventory().getArmorContents()[i]);
		}

		this.setItem(44, ItemUtil.buildItem(Material.GOLDEN_APPLE, 1, 0, "&fHealth:&6&l " + StringUtil.FORMAT.format(target.getHealth()) + "/" + StringUtil.FORMAT.format(target.getMaxHealth())));
		this.setItem(43, ItemUtil.buildItem(Material.COOKED_BEEF, 1, 0, "&fFood Level:&6&l " + StringUtil.FORMAT.format(target.getFoodLevel())));
		this.setItem(42, ItemUtil.buildItem(Material.PAPER, 1, 0, "&bPlayer Stats"));
	}

	@Override
	public void onClick(final Player p, final ItemStack stack) {
		if (stack.getType().equals(Material.PAPER)) {
			p.closeInventory();
			p.chat("/stats " + name);
		}
	}
}

