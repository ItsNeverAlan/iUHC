package spg.lgdev.uhc.gui.gameconfig;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import spg.lgdev.uhc.enums.InventoryTypes;
import spg.lgdev.uhc.manager.InventoryManager;
import spg.lgdev.uhc.util.ItemUtil;
import spg.lgdev.uhc.util.Utils;

public class AntiCraftingGUI implements Listener {

	@Getter
    private final List<ItemStack> disabledItems = new ArrayList<>();

    @Getter
    public static AntiCraftingGUI instance;

    private final ItemStack item1 = ItemUtil.buildItem(345, 1, 0, "&6&lUsage:",
            "&7Put the items that you want to disband &e&lRecipe", "&7in to &e&lThis inventory &7then is system will",
            "disaband the recipe in this uhc!", "&7if &e&lRight click&7 on the items you dont wanna disband",
            "&7the system will &e&lRemove&7 the item for here if you do the action!");
    private final ItemStack item2 = ItemUtil.buildItem(388, 1, 0,
    		"&a&lSave Items",
            "&7Save the items you want disband in this &e&lInventory");
    private final ItemStack item3 = ItemUtil.buildItem(160, 1, 14, " ", " ");
    private final ItemStack item4 = ItemUtil.buildItem(351, 1, 1, "&cBack to Main Edit Menu");
    private final ItemStack item5 = ItemUtil.buildItem(Material.WOOL, 1, 14, "&cClear All",
    		"&7Right Click me to &c&lRemove &7all disband items!");

    private final String inv_name = "§6§lAntiCraft §f§lEdit";
    private final Inventory inv = Bukkit.createInventory(null, 9 * 3, inv_name);

    public AntiCraftingGUI() {
        instance = this;
        disabledItems.add(new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1));
    }

    public void openMenu(Player p) {
        inv.setItem(26, item1);
        inv.setItem(18, item2);
        inv.setItem(19, item3);
        inv.setItem(20, item3);
        inv.setItem(21, item5);
        inv.setItem(22, item3);
        inv.setItem(23, item4);
        inv.setItem(24, item3);
        inv.setItem(25, item3);
        int slot = 0;
        for (final ItemStack item99 : disabledItems) {
            if (item99 == null) {
                continue;
            }
            inv.setItem(slot, item99);
            slot++;
        }
        p.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        final Player p = (Player) e.getWhoClicked();
        final Inventory inv = e.getInventory();
        final String name = e.getView().getTopInventory().getName();
        final ItemStack item = e.getCurrentItem();
        if (!name.equals(inv_name)) {
            return;
        }
        if (item == null) {
            return;
        }
        if (item.equals(item1)) {
            e.setCancelled(true);
            return;
        }
        if (item.equals(item2)) {
            saveItems(true, p, inv);
            e.setCancelled(true);
            return;
        }
        if (item.equals(item3)) {
            e.setCancelled(true);
            return;
        }
        if (item.equals(item4)) {
            InventoryManager.instance.createInventory(p, InventoryTypes.Config_Editor);
            e.setCancelled(true);
            return;
        }
        if (item.equals(item5)) {
            if (!e.getAction().equals(InventoryAction.PICKUP_HALF)) {
                e.setCancelled(true);
                return;
            }
            for (int i = 0; i <= 17; i++) {
                final ItemStack item1 = inv.getItem(i);
                if (item1 != null && !item1.equals(item1) && !item1.equals(item2) && !item1.equals(item3) && !item1.equals(item4) && !item1.equals(item5)) {
                    inv.remove(item1);
                }
            }
            disabledItems.clear();
            p.sendMessage(ChatColor.RED + "You removed all disband items");
            e.setCancelled(true);
            this.openMenu(p);
            return;
        }
        if (e.getAction().equals(InventoryAction.PICKUP_HALF)) {
            if (disabledItems.contains(item)) {
                e.setCancelled(true);
                inv.remove(item);
            }
        } else {
            if (disabledItems.contains(item)) {
                e.setCancelled(true);
            }
        }

    }

    private void saveItems(boolean b, Player p, Inventory inv1) {
        if (b) {
            disabledItems.clear();
            for (int i = 0; i <= 17; i++) {

                final ItemStack item = inv1.getItem(i);
                if (item != null && !item.equals(item1) && !item.equals(item2) && !item.equals(item3) && !item.equals(item4) && !item.equals(item5)) {
                    if (!disabledItems.contains(item)) {
                        disabledItems.add(item);
                    }
                }
            }
            p.sendMessage(ChatColor.GREEN + "Disband Item Craft List has been saved");
        }
    }

    public void removeRecipes() {
    	getDisabledItems().forEach(Utils::removeCrafting);
    }

}
