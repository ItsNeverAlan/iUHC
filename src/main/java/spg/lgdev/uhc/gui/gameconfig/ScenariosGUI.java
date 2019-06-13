package spg.lgdev.uhc.gui.gameconfig;

import static spg.lgdev.uhc.scenario.Scenarios.updateScenarios;
import static spg.lgdev.uhc.scenario.Scenarios.valueOf;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import spg.lgdev.uhc.enums.InventoryTypes;
import spg.lgdev.uhc.gui.GUI;
import spg.lgdev.uhc.manager.InventoryManager;
import spg.lgdev.uhc.scenario.Scenarios;
import spg.lgdev.uhc.util.ItemBuilder;
import spg.lgdev.uhc.util.ItemUtil;
import spg.lgdev.uhc.util.StringUtil;
import spg.lgdev.uhc.util.UHCSound;

public class ScenariosGUI extends GUI {

	public ScenariosGUI(final Player player) {
		super("&6&lScenarios &f&lEdit", 5);
		updateInv(player);
		setItem(getInventory().getSize() - 9, ItemUtil.buildItem(Material.TNT, 1, 0, "&e&lReset", "&7click me to reset scenarios! (disable all)"));
		setItem(getInventory().getSize() - 1, ItemUtil.buildItem(35, 1, 14, "&c&lBack EditMenu", "&7click me to back EditMenu"));
	}

	@Override
	public void onClick(final Player player, final ItemStack stack) {

		String name = stack.getItemMeta().getDisplayName();

		if (name.equals("§c§lBack EditMenu")) {
			InventoryManager.instance.createInventory(player, InventoryTypes.Config_Editor);
			return;
		}

		if (name.equals("§e§lReset")) {
			Scenarios.reset();
			player.sendMessage("§cYou reset the scenarios!");
			updateInv(player);
			return;
		}

		if (name.equals("§6Scenarios"))
			return;

		boolean on = false;

		if (name.contains("§c§l")) {
			name = StringUtil.replace(name, "§c§l", "");
			on = true;
		} else {
			name = StringUtil.replace(name, "§a§l", "");
		}
		name = StringUtil.replace(name, "+", "Plus");

		valueOf(name).setOn(on);

		updateScenarios();
		updateInv(player);
		UHCSound.CLICK.playSound(player);
	}

	public void updateInv(final Player player) {

		int i = 0;

		for (final Scenarios scenarios : Scenarios.values()) {

			ItemStack itemStack = null;

			if (scenarios.isOn()) {
				itemStack = ItemUtil.buildItem(scenarios.getMaterialData().getItemType()
						, scenarios.getMaterialData().getAmount()
						, scenarios.getMaterialData().getData()
						, ChatColor.GREEN.toString() + ChatColor.BOLD + StringUtil.replace(scenarios.name(), "Plus", "+")
						, scenarios.getDescription(player), " ", "§fStatus: " + ChatColor.GREEN.toString() + ChatColor.BOLD + "\u2714");
			} else {
				itemStack = ItemUtil.buildItem(scenarios.getMaterialData().getItemType()
						, scenarios.getMaterialData().getAmount()
						, scenarios.getMaterialData().getData()
						, ChatColor.RED.toString() + ChatColor.BOLD + StringUtil.replace(scenarios.name(), "Plus", "+")
						, scenarios.getDescription(player), " ", "§fStatus: " + ChatColor.RED.toString() + ChatColor.BOLD + "\u2718");
			}

			setItem(i, itemStack);

			i++;
		}

		final ItemBuilder itemBuilder = new ItemBuilder(Material.PAPER);
		itemBuilder.name("§6Scenarios").lore(" ", "§fScenarios:");

		for (final String string : Scenarios.getScenariosList()) {
			itemBuilder.lore("§7- " + string);
		}

		setItem(40, itemBuilder.build());

	}

}
