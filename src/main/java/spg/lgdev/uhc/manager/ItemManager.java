package spg.lgdev.uhc.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.iUHC;
import org.bukkit.Material;
import org.bukkit.block.Skull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.gui.spectator.SimpleInventorySnapshot;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.player.PlayerProfile;
import spg.lgdev.uhc.scenario.Scenarios;
import spg.lgdev.uhc.util.ItemUtil;
import spg.lgdev.uhc.util.Utils;

@Getter
public class ItemManager implements Listener {

    private final List<ItemInfo> spawnItems;
    private final List<ItemInfo> spectatorItems;

    public ItemManager(iUHC plugin) {
        spawnItems = getItemList(CachedConfig.getItems().getConfigurationSection("Lobby"));
        spectatorItems = getItemList(CachedConfig.getItems().getConfigurationSection("Spectator"));
    }

    public void setSpawnItems(Player p) {
        giveItems(p, getSpawnItems());
    }

    public void setSpectatorItems(Player p) {
        Utils.runTaskLater(() -> giveItems(p, getSpectatorItems()), 2L, false);
    }

    public List<ItemInfo> getItemList(ConfigurationSection c) {
        final List<ItemInfo> list = new ArrayList<>();
        for (final String s : c.getKeys(false)) {
            if (c.getBoolean(s + ".Enable")) {
                int amount = 1;
                int data = 0;
                boolean after_finish = false;
                String mode = "Player";
                if (c.contains(s + ".Amount"))
                    amount = c.getInt(s + ".Amount");
                if (c.contains(s + ".ItemData"))
                    data = c.getInt(s + ".ItemData");
                if (c.contains(s + ".Mode"))
                    mode = c.getString(s + ".Mode");
                if (c.contains(s + ".AfterGameEnd"))
                    after_finish = c.getBoolean(s + ".AfterGameEnd");
                final ItemInfo info = new ItemInfo(
                        ItemUtil.buildItem(Material.getMaterial(c.getString(s + ".Material")), amount, data,
                                c.getString(s + ".Name"), c.getStringList("Lore")),
                        c.getInt(s + ".Slot"), c.getStringList(s + ".Commands"), mode, after_finish);
                list.add(info);
            }
        }
        return list;
    }

    public void giveItems(Player p, List<ItemInfo> list) {

        final PlayerProfile profile = iUHC.getInstance().getProfileManager().getProfile(p.getUniqueId());

        for (final ItemInfo info : list) {

            boolean give = false;

            switch (info.mode) {

                case "player":
                    give = true;
                    break;

                case "playeronly":
                    if (!UHCGame.getInstance().isMod(p.getUniqueId())
                            && !UHCGame.getInstance().isHost(p.getUniqueId())
                            && !profile.isSpectator()) {
                        give = true;
                    }
                    break;

                case "spec":
                    if (profile.isSpectator()) {
                        give = true;
                    }
                    break;

                case "speconly":
                    if (profile.isSpectator()
                            && !UHCGame.getInstance().isMod(p.getUniqueId())) {
                        give = true;
                    }
                    break;

                case "staff":
                    if (UHCGame.getInstance().isMod(p.getUniqueId())) {
                        give = true;
                    }
                    break;

                case "staffonly":
                    if (UHCGame.getInstance().isMod(p.getUniqueId())
                            && !profile.isSpectator()
                            && !UHCGame.getInstance().isHost(p.getUniqueId())) {
                        give = true;
                    }
                    break;

                case "hoster":
                    if (UHCGame.getInstance().isHost(p.getUniqueId())) {
                        give = true;
                    }
                    break;

                case "hoster-staff":
                    if (UHCGame.getInstance().isHost(p.getUniqueId())
                        && UHCGame.getInstance().isMod(p.getUniqueId())
                            && !profile.isSpectator()) {
                        give = true;
                    }
                    break;

            }

            if (info.isAfter_finish() && !GameStatus.is(GameStatus.FINISH)) {
                give = false;
            }

            if (give) {
                p.getInventory().setItem(info.getSlot(), info.getItemStack());
            }
        }

        p.updateInventory();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent e) {

        final Player p = e.getPlayer();

        final PlayerProfile profile = iUHC.getInstance().getProfileManager().getProfile(p.getUniqueId());

        if (!profile.isPlayerAlive()) {
            e.setCancelled(true);
        }

        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (ItemInfo.hasItemInfo(e.getItem())) {

            if (profile.isActionCountdown()) {
                p.sendMessage(Lang.getMsg(p, "Cooldown"));
                return;
            }

            ItemInfo.fromItemStack(e.getItem()).forEach(p::performCommand);

            e.setCancelled(true);
            profile.startActionCountdown();
            return;

        }

        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {

            if (e.getClickedBlock().getType().equals(Material.SKULL)) {
                final Skull skull = (Skull) e.getClickedBlock().getState();
                if (skull.hasOwner()) {
                    p.sendMessage("§eThis head is for " + skull.getOwner() + ".");
                }
                return;
            }

            if (Scenarios.NoEnchants.isOn()) {

                if (e.getClickedBlock().getType().equals(Material.ANVIL)
                        || e.getClickedBlock().getType().equals(Material.ENCHANTMENT_TABLE)) {
                    e.setCancelled(true);
                    p.sendMessage("§cYou can't interact with this block! Scenario §eNoEnchants §cis enable!.");
                    return;
                }

            }

        }

    }

    @EventHandler
    public void onPlayerEntityInteract(PlayerInteractEntityEvent e) {

        final Player p = e.getPlayer();

        if (iUHC.getInstance().getProfileManager().getProfile(p.getUniqueId()).isSpectator()) {
            e.setCancelled(true);
            return;
        }

        if (UHCGame.getInstance().isMod(p.getUniqueId()) && e.getRightClicked() != null) {

            e.setCancelled(true);

            if (e.getRightClicked() instanceof Player) {

                new SimpleInventorySnapshot((Player) e.getRightClicked()).open(p);
                return;

            }

        }

    }

    @Getter
    public static class ItemInfo {

        @Getter
        public static Map<ItemStack, ItemInfo> list = new HashMap<>();

        private int slot;
        private boolean after_finish;
        private String mode;
        private ItemStack itemStack;
        private List<String> command;

        public ItemInfo(ItemStack item, Integer slot, List<String> command, String mode, boolean after_finish) {
            this.itemStack = item;
            this.slot = slot;
            this.command = command;
            this.after_finish = after_finish;
            this.mode = mode.toLowerCase();
            list.put(item, this);
        }

        public static boolean hasItemInfo(ItemStack from) {
            return list.containsKey(from);
        }

        public static List<String> fromItemStack(ItemStack from) {
            if (list.containsKey(from)) {
                final List<String> cmd = list.get(from).command;
                return cmd;
            }
            return null;
        }
    }

}
