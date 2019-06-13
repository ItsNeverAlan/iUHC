package spg.lgdev.uhc.listener;

import java.util.stream.Collectors;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.spigotmc.event.entity.EntityMountEvent;

import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.nms.NMSHandler;
import spg.lgdev.uhc.player.PlayerProfile;
import spg.lgdev.uhc.scenario.Scenarios;
import spg.lgdev.uhc.util.ItemUtil;
import spg.lgdev.uhc.util.StringUtil;

public class ScenariosHandler implements Listener {

	private final PotionEffect goldenHead1 = new PotionEffect(PotionEffectType.ABSORPTION, 1800, 0);
	private final PotionEffect goldenHead2 = new PotionEffect(PotionEffectType.REGENERATION, 200, 1);

	public static ItemStack buildGoldenHead() {
		final ItemStack goldenHead = ItemUtil.buildItem(Material.GOLDEN_APPLE, 1, 0, CachedConfig.GH_NAME
				, CachedConfig.GH_LORES.stream().map(string -> StringUtil.cc(string)).collect(Collectors.toList()));
		return goldenHead;
	}

	public static void setupGoldenHeads() {
		final ShapedRecipe goldenHeadRecipe = new ShapedRecipe(buildGoldenHead());
		goldenHeadRecipe.shape("@@@", "@#@", "@@@");
		goldenHeadRecipe.setIngredient('@', Material.GOLD_INGOT);
		goldenHeadRecipe.setIngredient('#', Material.SKULL_ITEM, 3);
		Bukkit.getServer().addRecipe(goldenHeadRecipe);
	}

	@EventHandler
	public void onFallDamage(final EntityDamageEvent e) {
		if (GameStatus.notStarted())
			return;
		if (!(e.getEntity() instanceof Player))
			return;
		if (Scenarios.NoFallDamage.isOn() && e.getCause() == EntityDamageEvent.DamageCause.FALL) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPrepareItemCraft(final PrepareItemCraftEvent e) {
		if (GameStatus.notStarted())
			return;
		if (Scenarios.RodLess.isOn()) {
			if (e.getInventory() instanceof CraftingInventory) {
				final CraftingInventory inv = e.getInventory();
				final ItemStack AIR = new ItemStack(Material.AIR);
				if (inv.getResult().getType() == Material.FISHING_ROD) {
					inv.setResult(AIR);
				}
			}
		}
		if (Scenarios.BowLess.isOn() && e.getInventory() instanceof CraftingInventory) {
			final CraftingInventory inv = e.getInventory();
			final ItemStack AIR = new ItemStack(Material.AIR);
			if (inv.getResult().getType() == Material.BOW) {
				inv.setResult(AIR);
			}
		}

	}

	@EventHandler
	public void onEntityMount(final EntityMountEvent event) {
		if (GameStatus.notStarted())
			return;
		if (Scenarios.HorseLess.isOn() && event.getEntity() != null && event.getEntity() instanceof Player
				&& event.getMount() != null && event.getMount() instanceof Horse) {
			event.setCancelled(true);
			final Player p = (Player) event.getEntity();
			p.sendMessage(ChatColor.RED + "The Horseless scenario is on, you can not ride horses!");
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPlayerItemConsume(final PlayerItemConsumeEvent e) {
		final Player p = e.getPlayer();
		final ItemStack item = e.getItem();
		if (item != null && item.getType().equals(Material.GOLDEN_APPLE)) {
			final PlayerProfile profile = iUHC.getInstance().getProfileManager().getDebuggedProfile(p);
			if (item.getItemMeta() != null && item.getItemMeta().hasDisplayName()
					&& item.getItemMeta().getDisplayName().equals(CachedConfig.GH_NAME)) {
				p.removePotionEffect(PotionEffectType.REGENERATION);
				p.removePotionEffect(PotionEffectType.ABSORPTION);
				goldenHead1.apply(p);
				goldenHead2.apply(p);
				profile.addConsumedGHeads();
			} else {
				profile.addConsumedGApple();
			}

			if (Scenarios.AbsorptionLess.isOn()) {
				Bukkit.getScheduler().runTaskLater(iUHC.getInstance(), () -> p.removePotionEffect(PotionEffectType.ABSORPTION), 1l);
			}

		}

	}

	@EventHandler
	public void onLeaveDecay(final LeavesDecayEvent event) {
		if (GameStatus.notStarted())
			return;
		if (Scenarios.VanillaPlus.isOn() && iUHC.getRandom().nextInt(100) + 1 <= 10) {
			event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeathWebCage(final PlayerDeathEvent e) {
		if (GameStatus.notStarted()) return;
		if (Scenarios.WebCage.isOn() && e.getEntity() instanceof Player) {
			final Player p = e.getEntity();

			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(3.0D, 0.0D, 0.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(3.0D, 1.0D, 0.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(3.0D, 2.0D, 0.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(0.0D, 0.0D, 3.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(0.0D, 1.0D, 3.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(0.0D, 2.0D, 3.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(-3.0D, 0.0D, 0.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(-3.0D, 1.0D, 0.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(-3.0D, 2.0D, 0.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(0.0D, 0.0D, -3.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(0.0D, 1.0D, -3.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(0.0D, 2.0D, -3.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(2.0D, 0.0D, 2.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(2.0D, 1.0D, 2.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(2.0D, 2.0D, 2.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(-2.0D, 0.0D, 2.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(-2.0D, 1.0D, 2.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(-2.0D, 2.0D, 2.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(-2.0D, 0.0D, -2.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(-2.0D, 1.0D, -2.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(-2.0D, 2.0D, -2.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(2.0D, 0.0D, -2.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(2.0D, 1.0D, -2.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(2.0D, 2.0D, -2.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(3.0D, 0.0D, 1.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(3.0D, 1.0D, 1.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(3.0D, 2.0D, 1.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(1.0D, 0.0D, 3.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(1.0D, 1.0D, 3.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(1.0D, 2.0D, 3.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(-3.0D, 0.0D, 1.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(-3.0D, 1.0D, 1.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(-3.0D, 2.0D, 1.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(1.0D, 0.0D, -3.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(1.0D, 1.0D, -3.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(1.0D, 2.0D, -3.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(3.0D, 0.0D, -1.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(3.0D, 1.0D, -1.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(3.0D, 2.0D, -1.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(-1.0D, 0.0D, 3.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(-1.0D, 1.0D, 3.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(-1.0D, 2.0D, 3.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(-3.0D, 0.0D, -1.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(-3.0D, 1.0D, -1.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(-3.0D, 2.0D, -1.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(-1.0D, 0.0D, -3.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(-1.0D, 1.0D, -3.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(-1.0D, 2.0D, -3.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(-1.0D, 3.0D, -2.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(-1.0D, 3.0D, -1.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(-1.0D, 3.0D, 0.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(-1.0D, 3.0D, 2.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(-1.0D, 3.0D, 1.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(0.0D, 3.0D, -1.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(1.0D, 3.0D, 2.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(1.0D, 3.0D, 1.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(1.0D, 3.0D, 0.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(1.0D, 3.0D, -2.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(1.0D, 3.0D, -1.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(0.0D, 3.0D, 1.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(2.0D, 3.0D, 1.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(-2.0D, 3.0D, 1.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(2.0D, 3.0D, -1.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(-2.0D, 3.0D, -1.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(2.0D, 3.0D, 0.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(-2.0D, 3.0D, 0.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(0.0D, 3.0D, 2.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(0.0D, 3.0D, -2.0D), Material.WEB, false);
			NMSHandler.getInstance().getFastBlockSet().setBlockFast(p.getLocation().add(0.0D, 3.0D, 0.0D), Material.WEB, false);

		}

	}

}
