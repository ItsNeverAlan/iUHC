package spg.lgdev.uhc.gui.gameconfig;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.gui.GUI;
import spg.lgdev.uhc.manager.InventoryManager;
import spg.lgdev.uhc.scenario.Scenarios;
import spg.lgdev.uhc.util.ItemUtil;
import spg.lgdev.uhc.util.StringUtil;

public class ScenariosWatchGUI extends GUI {

	public ScenariosWatchGUI(final Player p) {
		super("&6&lScen&f&larios", InventoryManager.instance.getRows(Scenarios.getScenariosList().size()));

		int i = 0;

		for (final Scenarios scenarios : Scenarios.values()) {
			if (scenarios.isOn()) {
				setItem(i, ItemUtil.buildItem(scenarios.getMaterialData().getItemType(), 1, scenarios.getMaterialData().getData(), CachedConfig.C + StringUtil.replace(scenarios.name(), "Plus", "+"), scenarios.getDescription(p)));
				i++;
			}
		}

	}

	@Override
	public void onClick(final Player p, final ItemStack stack) {}

}
