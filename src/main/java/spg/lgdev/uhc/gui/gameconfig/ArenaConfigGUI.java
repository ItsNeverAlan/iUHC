package spg.lgdev.uhc.gui.gameconfig;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import spg.lgdev.uhc.gui.GUI;
import spg.lgdev.uhc.util.ItemUtil;
import spg.lgdev.uhc.util.StringUtil;

public class ArenaConfigGUI extends GUI {

	private static ArenaConfigGUI instance;

	public ArenaConfigGUI() {
		super("&6&lLoadChunk&f&l Settings", 4);
		for (int i = 0; i <= 8; i++) {
			setItem(i, ItemUtil.buildItem(Material.STAINED_GLASS_PANE, 1, 14, " ", " "));
		}
		setItem(11, ItemUtil.buildItem(Material.GRASS, 1, 0, "&6Create Arena World", "&7Click me to create arena world"));
		setItem(13, ItemUtil.buildItem(Material.OBSIDIAN, 1, 0, "&c&lDelete Arena World", "&7Click me to delete arena world"));
		setItem(15, ItemUtil.buildItem(Material.WOOD, 1, 0, "&e&lReGenerate Arena World", "&7Click me to regenerate arena world"));
		setItem(20, ItemUtil.buildItem(Material.ARROW, 1, 0, "&6Teleport to Arena World", "&7Click me teleport to arena world"));
		setItem(22, ItemUtil.buildItem(Material.BEDROCK, 1, 0, "&9&lBorder Settings", "&7Click to edit the border"));
		setItem(24, ItemUtil.buildItem(Material.DIAMOND, 1, 0, "&a&lLoad Chunk", "&7Click me to load the chunk"));
		for (int i = 27; i <= 35; i++) {
			setItem(i, ItemUtil.buildItem(Material.STAINED_GLASS_PANE, 1, 14, " ", " "));
		}
	}

	public static ArenaConfigGUI getInstance() {
		if (instance == null) {
			instance = new ArenaConfigGUI();
		}
		return instance;
	}

	public String c(final String s) {
		return StringUtil.cc(s);
	}

	@Override
	public void onClick(final Player p, final ItemStack var2) {
		final String name = var2.getItemMeta().getDisplayName();
		if (name.equals(c("&6Create Arena World"))) {
			p.closeInventory();
			p.performCommand("pregame 1");
		} else if (name.equals(c("&c&lDelete Arena World"))) {
			p.closeInventory();
			p.performCommand("pregame 3");
		} else if (name.equals(c("&e&lReGenerate Arena World"))) {
			p.closeInventory();
			p.performCommand("pregame 3");
			Bukkit.getScheduler().runTaskLater(iUHC.getInstance(), () -> p.performCommand("pregame 1"), 20L);
		} else if (name.equals(c("&6Teleport to Arena World"))) {
			p.closeInventory();
			p.performCommand("pregame 2");
		} else if (name.equals(c("&9&lBorder Settings"))) {
			p.closeInventory();
			p.performCommand("pregame 4");
		} else if (name.equals(c("&a&lLoad Chunk"))) {
			p.closeInventory();
			p.performCommand("pregame 5");
		}

	}

}
