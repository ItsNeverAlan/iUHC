package spg.lgdev.uhc.gui.spectator;

import lombok.Getter;
import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.player.PlayerProfile;
import spg.lgdev.uhc.util.ItemUtil;
import spg.lgdev.uhc.util.MathUtil;
import spg.lgdev.uhc.util.StringUtil;
import spg.lgdev.uhc.util.inventory.InventoryUI;

import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.json.simple.JSONObject;

import java.util.*;

@Getter
public class InventorySnapshot {

    private final InventoryUI inventoryUI;
    private final ItemStack[] originalInventory;
    private final ItemStack[] originalArmor;

    @Getter
    private final UUID snapshotId = UUID.randomUUID();

    @Getter
    private final String ownerName;

    @Getter
    private boolean soupMatch = false;

    @Getter
    private boolean potionMatch = false;

    @Getter
    private int potionCount = 0;

    @Getter
    private int soupCount = 0;

    public InventorySnapshot(Player player) {
        ownerName = player.getName();

        ItemStack[] contents = player.getInventory().getContents();
        ItemStack[] armor = player.getInventory().getArmorContents();

        this.originalInventory = contents;
        this.originalArmor = armor;

        PlayerProfile profile = iUHC.getInstance().getProfileManager().getProfile(player.getUniqueId());

        double health = player.getHealth();
        double food = (double) player.getFoodLevel();

        List<String> potionEffectStrings = new ArrayList<>();

        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            String romanNumeral = MathUtil.convertToRomanNumeral(potionEffect.getAmplifier() + 1);
            String effectName = StringUtil.toNiceString(potionEffect.getType().getName().toLowerCase());
            String duration = MathUtil.convertTicksToMinutes(potionEffect.getDuration());

            potionEffectStrings.add(ChatColor.RED.toString() + ChatColor.BOLD + "* " + ChatColor.WHITE + effectName + " " + romanNumeral + ChatColor.GRAY + " (" + duration + ")");
        }

        this.inventoryUI = new InventoryUI(player.getName() + "'s Inventory", true, 6);

        for (int i = 0; i < 9; i++) {
            this.inventoryUI.setItem(i + 27, new InventoryUI.EmptyClickableItem(transfer(contents[i])));
            this.inventoryUI.setItem(i + 18, new InventoryUI.EmptyClickableItem(transfer(contents[i + 27])));
            this.inventoryUI.setItem(i + 9, new InventoryUI.EmptyClickableItem(transfer(contents[i + 18])));
            this.inventoryUI.setItem(i, new InventoryUI.EmptyClickableItem(transfer(contents[i + 9])));
        }

        this.potionMatch = true;

        if (potionMatch) {
            potionCount = (int) Arrays.stream(contents).filter(Objects::nonNull).filter(item -> item.getAmount() > 0 && item.getType() == Material.POTION).map(ItemStack::getDurability).filter(d -> d == 16421).count();

            this.inventoryUI.setItem(45, new InventoryUI.EmptyClickableItem(ItemUtil.reloreItem(
                    ItemUtil.buildItem(Material.POTION, ChatColor.RED.toString() + ChatColor.BOLD + "Potions:", potionCount),
                    ChatColor.RED.toString() + ChatColor.BOLD + "* " + ChatColor.WHITE + "Health Pots: " + ChatColor.GRAY + potionCount + " Potion" + (potionCount > 1 ? "s" : ""),
                    ChatColor.RED.toString() + ChatColor.BOLD + "* " + ChatColor.WHITE + "Potion Accuracy: " + ChatColor.GRAY + (int) getPotionAccuracy(profile) + "%")));

        } else if (soupMatch) {
            soupCount = (int) Arrays.stream(contents).filter(Objects::nonNull).map(ItemStack::getType).filter(d -> d == Material.MUSHROOM_SOUP).count();

            this.inventoryUI.setItem(45, new InventoryUI.EmptyClickableItem(ItemUtil.buildItem(
                    Material.MUSHROOM_SOUP, ChatColor.RED.toString() + ChatColor.BOLD + "Soups Left: " + ChatColor.WHITE + soupCount, soupCount, (short) 16421)));
        }

        final double roundedHealth = Math.round(health / 2.0 * 2.0) / 2.0;

        this.inventoryUI.setItem(49,
                new InventoryUI.EmptyClickableItem(ItemUtil.buildItem((health > 0 ? Material.SPECKLED_MELON : Material.SKULL_ITEM), ChatColor.RED.toString() + ChatColor.BOLD + StringEscapeUtils.unescapeJava("\u2764") + roundedHealth + " Health", (int) Math.round(health / 2.0D))));

        final double roundedFood = Math.round(food / 2.0 * 2.0) / 2.0;

        this.inventoryUI.setItem(48,
                new InventoryUI.EmptyClickableItem(ItemUtil.buildItem(Material.COOKED_BEEF, ChatColor.RED.toString() + ChatColor.BOLD + roundedFood + " Hunger", (int) Math.round(food / 2.0D))));

        this.inventoryUI.setItem(47,
                new InventoryUI.EmptyClickableItem(ItemUtil.reloreItem(
                        ItemUtil.buildItem(Material.BREWING_STAND_ITEM, ChatColor.RED.toString() + ChatColor.BOLD + "Potion Effects", potionEffectStrings.size())
                        , potionEffectStrings.toArray(new String[]{}))));

        this.inventoryUI.setItem(46, new InventoryUI.EmptyClickableItem(
                ItemUtil.reloreItem(
                        ItemUtil.buildItem(Material.CAKE, ChatColor.RED.toString() + ChatColor.BOLD + "Fight Stats"),
                        ChatColor.RED.toString() + ChatColor.BOLD + "* " + ChatColor.WHITE + "Longest Combo: " + ChatColor.GRAY + profile.getLongestCombo() + " Hit" + (profile.getLongestCombo() > 1 ? "s" : ""),
                        ChatColor.RED.toString() + ChatColor.BOLD + "* " + ChatColor.WHITE + "Total Hits: " + ChatColor.GRAY + profile.getHits() + " Hit" + (profile.getHits() > 1 ? "s" : "")/*,
						ChatColor.RED.toString() + ChatColor.BOLD + "* " + ChatColor.WHITE + "Max CPS: " + ChatColor.GRAY + playerData.getMaxCPS() + " Click" + (playerData.getMaxCPS() > 1 ? "s" : ""),
						ChatColor.GOLD.toString() + ChatColor.BOLD + "* " + ChatColor.WHITE + "Hit Accuracy: " + ChatColor.GRAY + getHitAccuracy(playerData) + "%")*/)));


        for (int i = 36; i < 40; i++) {
            this.inventoryUI.setItem(i, new InventoryUI.EmptyClickableItem(transfer(armor[39 - i])));
        }
    }

    @SuppressWarnings("unchecked")
    public JSONObject toJson() {
        JSONObject object = new JSONObject();

        JSONObject inventoryObject = new JSONObject();
        for (int i = 0; i < this.originalInventory.length; i++) {
            inventoryObject.put(i, this.encodeItem(this.originalInventory[i]));
        }
        object.put("inventory", inventoryObject);

        JSONObject armourObject = new JSONObject();
        for (int i = 0; i < this.originalArmor.length; i++) {
            armourObject.put(i, this.encodeItem(this.originalArmor[i]));
        }
        object.put("armour", armourObject);

        return object;
    }

    @SuppressWarnings("unchecked")
    private JSONObject encodeItem(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return null;
        }

        JSONObject object = new JSONObject();
        object.put("material", itemStack.getType().name());
        object.put("durability", itemStack.getDurability());
        object.put("amount", itemStack.getAmount());

        JSONObject enchants = new JSONObject();
        for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
            enchants.put(enchantment.getName(), itemStack.getEnchantments().get(enchantment));
        }
        object.put("enchants", enchants);

        return object;
    }

    private ItemStack transfer(ItemStack from) {
        if (from == null) {
            return new ItemStack(Material.AIR);
        }

        if (from.getAmount() <= 0) {
            from.setType(Material.AIR);
        }

        return from;
    }

    private double getPotionAccuracy(PlayerProfile playerData) {
        double heal = playerData.getTotalHeal();
        double potion = (double) playerData.getTotalPotion();

        try {
            return Math.round(((heal * 100) / (potion * 100)) * (100.0D / 100.0D) * 100.0D);
        } catch (ArithmeticException zero) {
            return 0;
        }
    }

}
