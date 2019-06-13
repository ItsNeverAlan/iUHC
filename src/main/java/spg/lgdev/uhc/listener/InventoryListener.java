package spg.lgdev.uhc.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import spg.lgdev.uhc.gui.GUI;
import spg.lgdev.uhc.manager.InventoryManager;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        if (e.getInventory() != null) {

            final InventoryHolder holder = e.getInventory().getHolder();
            final Player player = (Player) e.getWhoClicked();
            final ItemStack itemStack = e.getCurrentItem();

            if (holder instanceof GUI.GUIHolder) {

                if (itemStack == null || itemStack.getType() == Material.AIR || !itemStack.getItemMeta().hasDisplayName())
                    return;

                e.setCancelled(true);

                final GUI gui = ((GUI.GUIHolder) holder).getGui();

                gui.onClick(player, itemStack);

            } else if (holder instanceof InventoryManager.SpectatorHolder) {

                if (itemStack == null || itemStack.getType() == Material.AIR)
                    return;

                e.setCancelled(true);

                if (!itemStack.getItemMeta().hasDisplayName())
                    return;

                final String name = itemStack.getItemMeta().getDisplayName();
                final int page = ((InventoryManager.SpectatorHolder) e.getInventory().getHolder()).getPage();

                switch (name) {
                    case "§6<--":
                        player.closeInventory();
                        player.openInventory(InventoryManager.instance.openAliveInventory(page - 1));
                        return;
                    case "§6-->":
                        player.closeInventory();
                        player.openInventory(InventoryManager.instance.openAliveInventory(page + 1));
                        return;
                    case "§c<--":
                        player.closeInventory();
                        player.openInventory(InventoryManager.instance.openNetherAliveInventory(page - 1));
                        return;
                    case "§c-->":
                        player.closeInventory();
                        player.openInventory(InventoryManager.instance.openNetherAliveInventory(page + 1));
                        return;
                }

                final Player target = Bukkit.getPlayer(name.replace(ChatColor.YELLOW.toString(), ""));

                if (target == null)
                    return;

                player.teleport(target);
                return;

            }

        }
    }

}
