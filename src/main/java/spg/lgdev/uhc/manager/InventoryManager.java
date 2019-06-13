package spg.lgdev.uhc.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.enums.InventoryTypes;
import spg.lgdev.uhc.gui.announcement.AnnounceGUI;
import spg.lgdev.uhc.gui.gameconfig.AntiCraftingGUI;
import spg.lgdev.uhc.gui.gameconfig.PreConfigGUI;
import spg.lgdev.uhc.gui.gameconfig.ScenariosGUI;
import spg.lgdev.uhc.gui.gameconfig.ScenariosWatchGUI;
import spg.lgdev.uhc.gui.gameconfig.TimerGUI;
import spg.lgdev.uhc.gui.spectator.InventorySnapshot;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.scenario.Scenarios;
import spg.lgdev.uhc.util.ItemUtil;
import spg.lgdev.uhc.util.UHCSound;
import spg.lgdev.uhc.util.Utils;
import net.development.mitw.uuid.UUIDCache;

public class InventoryManager implements Listener {

	public static InventoryManager instance;
	private final UHCGame game;
	private final TeamManager team;
	@Getter
	private final Inventory editor;
	@Getter
	private final Inventory config;

	@Getter
	private final Map<UUID, InventorySnapshot> snapshots = new HashMap<UUID, InventorySnapshot>() {
		private static final long serialVersionUID = 1L;
		@Override
		public InventorySnapshot get(final Object key) {
			return containsKey(key) ? super.get(key) : null;
		}
	};

	public InventoryManager(final iUHC pl) {

		this.team = TeamManager.getInstance();
		this.game = UHCGame.getInstance();

		instance = this;

		editor = Bukkit.createInventory(null, 4 * 9, "§6§lUHC §f§lEditor");
		config = Bukkit.createInventory(null, 5 * 9, "§6§lUHC §f§lConfig");

		editor(editor);
		config();

		setup();
	}

	private void setup() {
		config.setItem(10, ItemUtil.buildItem(Material.ENDER_CHEST, 1, 0, "&eScenarios", "&7Click me to check the scenarios!"));
		config.setItem(13, ItemUtil.buildItem(Material.CHEST, 1, 0, "&ePreview kit", "&7click me to preview starter kit!"));

		final List<Integer> toSet = Arrays.asList(0, 1, 2, 3, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44);

		final ItemStack glass = ItemUtil.buildItem(Material.STAINED_GLASS_PANE, 1, 4, " ");

		toSet.forEach(i -> config.setItem(i, glass));
	}

	public void config() {

		final List<String> list = new ArrayList<>();
		list.add("§f§lList: ");
		AntiCraftingGUI.getInstance().getDisabledItems().forEach(i -> list.add("§7- §6" + i.getType().name()));
		config.setItem(4, ItemUtil.buildItem(Material.BEDROCK, 1, 0, "&eBorder", " ", "&f&lM &7- &f" + game.getBorderRadius(), "&c&lN &7- &c" + CachedConfig.NetherBorderSize));
		config.setItem(15, ItemUtil.buildSkullItem("LeeGod", "&ePlayerLimited", " ", "&fCurrent MaxPlayer Count:&6 " + game.getMaxplayers(), " "));
		config.setItem(12, ItemUtil.buildItem(Material.WORKBENCH, 1, 0, "&eBlocked Craft Items", list));
		config.setItem(11, ItemUtil.buildItem(Material.WATCH, 1, 0, "&eUHC Times", " ", "&fFinalHeal: &6" + Utils.formatTimeHours(game.getFinalHealTime()), "&fPvP: &6" + Utils.formatTimeHours(game.getPvpTime()), "&fFirstBorder:&6 " + Utils.formatTimeHours(game.getFirstBorder()), "&fTimeBomb:&6 " + game.getTimebombTimer() + "s", " "));
		config.setItem(19, ItemUtil.buildItem(Material.APPLE, 1, 0, "&eApple Rate", " ", "&fCurrent Rate:&6 " + game.getAppleRate() + "%", " "));
		config.setItem(20, ItemUtil.buildItem(Material.GOLDEN_APPLE, 1, 0, "&eGoldenApple Rate", " ", "&fCurrent Rate:&6 " + game.getGoldenAppleRate() + "%", " "));
		if (!game.isShears()) {
			config.setItem(14, ItemUtil.buildItem(Material.SHEARS, 1, 0, "&eShears", " ", "&fCurrent Status:&c&l Disabled"));
		} else {
			config.setItem(14, ItemUtil.buildItem(Material.SHEARS, 1, 0, "&eShears", " ", "&fCurrent Status:&a&l Enabled"));
		}
		config.setItem(25, ItemUtil.buildItem(Material.STAINED_GLASS_PANE, 1, 14, " "));
		config.setItem(24, ItemUtil.buildItem(Material.STAINED_GLASS_PANE, 1, 14, " "));
		if (!game.isFinalheal()) {
			config.setItem(23, ItemUtil.buildItem(Material.POTION, 1, 8261, "&eFinal Heal", " ", "&fCurrent Status: &c&lDisabled"));
		} else {
			config.setItem(23, ItemUtil.buildItemEnchantment(Material.POTION, 1, 8261, "&eFinal Heal", " ", "&fCurrent Status: &a&lEnabled"));
		}
		if (!game.isDeathKick()) {
			config.setItem(16, ItemUtil.buildSkullItem("zombie", "&eDeath Kick", " ", "&fCurrent Status: &c&lDisabled"));
		} else {
			config.setItem(16, ItemUtil.buildSkullItem("zombie", "&eDeath Kick", " ", "&fCurrent Status: &a&lEnabled"));
		}
		if (!game.isEnderpearl()) {
			config.setItem(24, ItemUtil.buildItem(Material.ENDER_PEARL, 1, 0, "&eEnder Pearl", " ", "&fCurrent Status: &c&lDisabled"));
		} else {
			config.setItem(24, ItemUtil.buildItemEnchantment(Material.ENDER_PEARL, 1, 0, "&eEnder Pearl", " ", "&fCurrent Status: &a&lEnabled"));
		}
		if (!game.isNether()) {
			config.setItem(25, ItemUtil.buildItem(Material.NETHER_BRICK, 1, 0, "&eNether", " ", "&fCurrent Status: &c&lDisabled"));
		} else {
			config.setItem(25, ItemUtil.buildItemEnchantment(Material.NETHER_BRICK, 1, 0, "&eNether", " ", "&fCurrent Status: &a&lEnabled"));
		}
		if (game.getSpeed() == 0) {
			config.setItem(21, ItemUtil.buildItem(Material.POTION, 1, 8194, "&eSpeed", " ", "&fCurrent Status:&c&l Disabled", " "));
		} else if (game.getSpeed() == 1) {
			config.setItem(21, ItemUtil.buildItem(Material.POTION, 1, 8194, "&eSpeed", " ", "&fCurrent Status:&a&l Level 1", " "));
		} else {
			config.setItem(21, ItemUtil.buildItem(Material.POTION, 1, 8226, "&eSpeed", " ", "&fCurrent Status:&a&l Level 2", " "));
		}
		if (game.getStreght() == 0) {
			config.setItem(22, ItemUtil.buildItem(Material.POTION, 1, 8201, "&eStreght", " ", "&fCurrent Status:&c&l Disabled", " "));
		} else if (game.getStreght() == 1) {
			config.setItem(22, ItemUtil.buildItem(Material.POTION, 1, 8201, "&eStreght", " ", "&fCurrent Status:&a&l Level 1", " "));
		} else {
			config.setItem(22, ItemUtil.buildItem(Material.POTION, 1, 8233, "&eStreght", " ", "&fCurrent Status:&a&l Level 2", " "));
		}
		if (!game.isGoldenHead()) {
			config.setItem(28, ItemUtil.buildItem(Material.GOLDEN_APPLE, 1, 0, "&eGoldenHead", " ", "&fCurrent Status:&c&l Disabled"));
		} else {
			config.setItem(28, ItemUtil.buildItemEnchantment(Material.GOLDEN_APPLE, 1, 0, "&eGoldenHead", " ", "&fCurrent Status:&a&l Enabled"));
		}
		if (!CachedConfig.MatchEnabled) {
			config.setItem(29, ItemUtil.buildItem(Material.DIAMOND_SWORD, 1, 0, "&eDeathmatch", " ", "&fCurrent Status:&c&l Disabled"));
		} else {
			config.setItem(29, ItemUtil.buildItemEnchantment(Material.DIAMOND_SWORD, 1, 0, "&eDeathmatch", " ", "&fCurrent Status:&a&l Enabled"));
		}
		if (!team.isTeamsEnabled()) {
			config.setItem(30, ItemUtil.buildItem(Material.BOOK, 1, 0, "&eTeam mode", " ", "&fCurrent Status: &c&lDisabled"));
			config.setItem(31, ItemUtil.buildItem(Material.STAINED_GLASS_PANE, 1, 14, " "));
			config.setItem(32, ItemUtil.buildItem(Material.STAINED_GLASS_PANE, 1, 14, " "));
			config.setItem(33, ItemUtil.buildItem(Material.STAINED_GLASS_PANE, 1, 14, " "));
			config.setItem(34, ItemUtil.buildItem(Material.STAINED_GLASS_PANE, 1, 14, " "));
		} else {
			config.setItem(30, ItemUtil.buildItemEnchantment(Material.BOOK, 1, 0, "&eTeam mode", " ", "&fCurrent Status: &a&lEnabled"));
			config.setItem(31, ItemUtil.buildItem(Material.PAPER, 1, 0, "&ePre Team Size", " ", "&fCurrent Count:&6 " + team.getMaxSize(), " "));
			config.setItem(32, ItemUtil.buildItem(Material.PAPER, 1, 0, "&eMax Teams", " ", "&fCurrent Count:&6 " + team.getTeamSize(), " "));
			if (!team.canDamageTeamMembers()) {
				config.setItem(33, ItemUtil.buildItem(351, 1, 8, "&eFriendly Fire", " ", "&fCurrent Status:&c&l Disabled"));
			} else {
				config.setItem(33, ItemUtil.buildItem(351, 1, 10, "&eFriendly Fire", " ", "&fCurrent Status:&a&l Enabled"));
			}
			if (!game.isBackpack()) {
				config.setItem(34, ItemUtil.buildItem(342, 1, 0, "&eBackpack", " ", "&fCurrent Status:&c&l Disabled"));
			} else {
				config.setItem(34, ItemUtil.buildItemEnchantment(342, 1, 0, "&eBackpack", " ", "&fCurrent Status:&a&l Enabled"));
			}
		}
	}

	public void editor(final Inventory inv) {

		if (GameStatus.is(GameStatus.WAITING)) {
			inv.setItem(8, ItemUtil.buildItem(Material.NETHER_STAR, 1, 0, "&eStart game", "&7click me to start the game!"));
		} else {
			inv.setItem(8, ItemUtil.buildItem(Material.STAINED_GLASS_PANE, 1, 14, " "));
		}
		if (iUHC.getInstance().getAnnounceManager().anyEnabled()) {
			inv.setItem(5, ItemUtil.buildItem(Material.SIGN, 1, 0, "&eAnnounces", "&7Click me to announce uhc on twitter or discord!"));
		} else {
			inv.setItem(5, ItemUtil.buildItem(Material.STAINED_GLASS_PANE, 1, 14, " "));
		}
		inv.setItem(3, ItemUtil.buildItem(Material.CHEST, 1, 0, "&eKits", "&7click me to config kits"));
		inv.setItem(4, ItemUtil.buildItem(Material.BOOK_AND_QUILL, 1, 0, "&eConfig Saver", "&7Click me go to config saver!"));
		inv.setItem(35, ItemUtil.buildSkullItem("LeeGod", "&ePlayerLimited", " ", "&fCurrent MaxPlayer Count:&6 " + game.getMaxplayers(), " ", "&7Left click to &a+5", "&7Right click to &c-5"));
		inv.setItem(2, ItemUtil.buildItem(Material.WORKBENCH, 1, 0, "&eAntiCraft Config", "&7Click me to config anticraft system"));
		inv.setItem(1, ItemUtil.buildItem(Material.WATCH, 1, 0, "&eTime Editor", "&7Click me to edit the time for this uhc!"));
		inv.setItem(0, ItemUtil.buildItem(Material.ENDER_CHEST, 1, 0, "&eScenarios Editor", "&7Click me to edit the scenarios!"));
		inv.setItem(33, ItemUtil.buildItem(Material.APPLE, 1, 0, "&eApple Rate", " ", "&fCurrent Rate:&6 " + game.getAppleRate() + "%", " ", "&7Left Click to &aadd 1% rate", "&7Right Click to &cremovd 1% rate"));
		inv.setItem(34, ItemUtil.buildItem(Material.GOLDEN_APPLE, 1, 0, "&eGoldenApple Rate", " ", "&fCurrent Rate:&6 " + game.getGoldenAppleRate() + "%", " ", "&7Left Click to &aadd 1% rate", "&7Right Click to &cremovd 1% rate", " ", "&c&lThis Rate only for luckyLeaves!"));
		if (!game.isShears()) {
			inv.setItem(26, ItemUtil.buildItem(Material.SHEARS, 1, 0, "&eShears", " ", "&fCurrent Status:&c&l Disabled"));
		} else {
			inv.setItem(26, ItemUtil.buildItemEnchantment(Material.SHEARS, 1, 0, "&eShears", " ", "&fCurrent Status:&a&l Enabled"));
		}
		if (!CachedConfig.MatchEnabled) {
			inv.setItem(25, ItemUtil.buildItem(Material.STAINED_GLASS_PANE, 1, 14, " "));
		} else if (!game.isDeathmatch()) {
			inv.setItem(25, ItemUtil.buildItem(Material.DIAMOND_SWORD, 1, 0, "&eDeathmatch", " ", "&fCurrent Status:&c&l Disabled"));
		} else {
			inv.setItem(25, ItemUtil.buildItemEnchantment(Material.DIAMOND_SWORD, 1, 0, "&eDeathmatch", " ", "&fCurrent Status:&a&l Enabled"));
		}
		if (!game.isGoldenHead()) {
			inv.setItem(24, ItemUtil.buildItem(Material.GOLDEN_APPLE, 1, 0, "&eGoldenHead", " ", "&fCurrent Status:&c&l Disabled"));
		} else {
			inv.setItem(24, ItemUtil.buildItemEnchantment(Material.GOLDEN_APPLE, 1, 0, "&eGoldenHead", " ", "&fCurrent Status:&a&l Enabled"));
		}
		if (!game.isFinalheal()) {
			inv.setItem(27, ItemUtil.buildItem(Material.POTION, 1, 8261, "&eFinal Heal", " ", "&fCurrent Status: &c&lDisabled"));
		} else {
			inv.setItem(27, ItemUtil.buildItemEnchantment(Material.POTION, 1, 8261, "&eFinal Heal", " ", "&fCurrent Status: &a&lEnabled"));
		}
		if (!game.isDeathKick()) {
			inv.setItem(28, ItemUtil.buildSkullItem("zombie", "&eDeath Kick", " ", "&fCurrent Status: &c&lDisabled"));
		} else {
			inv.setItem(28, ItemUtil.buildSkullItem("zombie", "&eDeath Kick", " ", "&fCurrent Status: &a&lEnabled"));
		}
		if (!game.isEnderpearl()) {
			inv.setItem(29, ItemUtil.buildItem(Material.ENDER_PEARL, 1, 0, "&eEnder Pearl", " ", "&fCurrent Status: &c&lDisabled"));
		} else {
			inv.setItem(29, ItemUtil.buildItemEnchantment(Material.ENDER_PEARL, 1, 0, "&eEnder Pearl", " ", "&fCurrent Status: &a&lEnabled"));
		}
		if (!game.isNether()) {
			inv.setItem(30, ItemUtil.buildItem(Material.NETHER_BRICK, 1, 0, "&eNether", " ", "&fCurrent Status: &c&lDisabled"));
		} else {
			inv.setItem(30, ItemUtil.buildItemEnchantment(Material.NETHER_BRICK, 1, 0, "&eNether", " ", "&fCurrent Status: &a&lEnabled"));
		}
		if (game.getSpeed() == 0) {
			inv.setItem(31, ItemUtil.buildItem(Material.POTION, 1, 8194, "&eSpeed", " ", "&fCurrent Status:&c&l Disabled", " ", "&7Click me to &aenable&7 !"));
		} else if (game.getSpeed() == 1) {
			inv.setItem(31, ItemUtil.buildItem(Material.POTION, 1, 8194, "&eSpeed", " ", "&fCurrent Status:&a&l Level 1", " ", "&7Click me to &alimit level 2&7 !"));
		} else {
			inv.setItem(31, ItemUtil.buildItem(Material.POTION, 1, 8226, "&eSpeed", " ", "&fCurrent Status:&a&l Level 2", " ", "&7Click me to &cDisable&7 !"));
		}
		if (game.getStreght() == 0) {
			inv.setItem(32, ItemUtil.buildItem(Material.POTION, 1, 8201, "&eStreght", " ", "&fCurrent Status:&c&l Disabled", " ", "&7Click me to &aenable&7 !"));
		} else if (game.getStreght() == 1) {
			inv.setItem(32, ItemUtil.buildItem(Material.POTION, 1, 8201, "&eStreght", " ", "&fCurrent Status:&a&l Level 1", " ", "&7Click me to &alimit level 2&7 !"));
		} else {
			inv.setItem(32, ItemUtil.buildItem(Material.POTION, 1, 8233, "&eStreght", " ", "&fCurrent Status:&a&l Level 2", " ", "&7Click me to &cDisable&7 !"));
		}
		if (!team.isTeamsEnabled()) {
			inv.setItem(9, ItemUtil.buildItem(Material.BOOK, 1, 0, "&eTeam mode", " ", "&fCurrent Status: &c&lDisabled"));
			inv.setItem(10, ItemUtil.buildItem(Material.STAINED_GLASS_PANE, 1, 14, " "));
			inv.setItem(11, ItemUtil.buildItem(Material.STAINED_GLASS_PANE, 1, 14, " "));
			inv.setItem(12, ItemUtil.buildItem(Material.STAINED_GLASS_PANE, 1, 14, " "));
			inv.setItem(13, ItemUtil.buildItem(Material.STAINED_GLASS_PANE, 1, 14, " "));
		} else {
			inv.setItem(9, ItemUtil.buildItemEnchantment(Material.BOOK, 1, 0, "&eTeam mode", " ", "&fCurrent Status: &a&lEnabled"));
			inv.setItem(10, ItemUtil.buildItem(Material.PAPER, 1, 0, "&ePre Team Size", " ", "&fCurrent Count:&6 " + team.getMaxSize(), " ", "&7Left click to &a+1", "&7Right click to &c-1"));
			inv.setItem(11, ItemUtil.buildItem(Material.PAPER, 1, 0, "&eMax Teams", " ", "&fCurrent Count:&6 " + team.getTeamSize(), " ", "&7Left click to &a+1", "&7Right click to &c-1"));
			if (!team.canDamageTeamMembers()) {
				inv.setItem(12, ItemUtil.buildItem(351, 1, 8, "&eFriendly Fire", " ", "&fCurrent Status:&c&l Disabled"));
			} else {
				inv.setItem(12, ItemUtil.buildItem(351, 1, 10, "&eFriendly Fire", " ", "&fCurrent Status:&a&l Enabled"));
			}
			if (!game.isBackpack()) {
				inv.setItem(13, ItemUtil.buildItem(342, 1, 0, "&eBackpack", " ", "&fCurrent Status:&c&l Disabled"));
			} else {
				inv.setItem(13, ItemUtil.buildItemEnchantment(342, 1, 0, "&eBackpack", " ", "&fCurrent Status:&a&l Enabled"));
			}
		}

	}

	/*
	 * Config Editor Items
	 */

	public void createInventory(final Player p, final InventoryTypes type) {
		if (type.equals(InventoryTypes.Config_Editor)) {
			p.openInventory(editor);
		} else if (type.equals(InventoryTypes.Config)) {
			p.openInventory(config);
		}
	}

	@EventHandler
	public void onClick(final InventoryClickEvent e) {
		if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)
				|| !e.getCurrentItem().getItemMeta().hasDisplayName())
			return;
		final String ITEM = e.getCurrentItem().getItemMeta().getDisplayName().replace("§e", "");
		final Inventory inv = e.getInventory();
		final Player p = (Player) e.getWhoClicked();
		final InventoryAction act = e.getAction();
		if (inv == null) return;
		if (inv.getTitle().equals("§6§lUHC §f§lEditor")) {
			e.setCancelled(true);
			if (ITEM.equals("Time Editor")) {
				p.closeInventory();
				new TimerGUI(p).open(p);
			} else if (ITEM.equals("Start game")) {
				p.closeInventory();
				game.startLobby();
			} else if (ITEM.equals("Config Saver")) {
				p.closeInventory();
				PreConfigGUI.getInstance().o(p);
			} else if (ITEM.equals("Scenarios Editor")) {
				p.closeInventory();
				new ScenariosGUI(p).open(p);
			} else if (ITEM.equals("Kits")) {
				p.closeInventory();
				createInventory(p, InventoryTypes.Kits);
			} else if (ITEM.equals("PlayerLimited")) {
				if (act.equals(InventoryAction.PICKUP_ALL)) {
					game.setMaxplayers(game.getMaxplayers() + 5);
				} else {
					game.setMaxplayers(game.getMaxplayers() - 5 <= 0 ? 5 : game.getMaxplayers() - 5);
				}
				inv.setItem(35, ItemUtil.buildSkullItem("LeeGod", "&ePlayerLimited", " ", "&fCurrent MaxPlayer Count:&6 " + game.getMaxplayers(), " ", "&7Left click to &a+5", "&7Right click to &c-5"));
			} else if (ITEM.equals("AntiCraft Config")) {
				AntiCraftingGUI.instance.openMenu(p);
			} else if (ITEM.equals("Apple Rate")) {
				if (act.equals(InventoryAction.PICKUP_ALL)) {
					game.setAppleRate(game.getAppleRate() + 1 > 100 ? 100 : game.getAppleRate() + 1);
				} else {
					game.setAppleRate(game.getAppleRate() - 1 < 0 ? 0 : game.getAppleRate() - 1);
				}
				inv.setItem(33, ItemUtil.buildItem(Material.APPLE, 1, 0, "&eApple Rate", " ", "&fCurrent Rate:&6 " + game.getAppleRate() + "%", " ", "&7Left Click to &aadd 1% rate", "&7Right Click to &cremovd 1% rate"));
			} else if (ITEM.equals("GoldenApple Rate")) {
				if (act.equals(InventoryAction.PICKUP_ALL)) {
					game.setGoldenAppleRate(game.getGoldenAppleRate() + 1 > 100 ? 100 : game.getGoldenAppleRate() + 1);
				} else {
					game.setGoldenAppleRate(game.getGoldenAppleRate() - 1 < 0 ? 0 : game.getGoldenAppleRate() - 1);
				}
				inv.setItem(34, ItemUtil.buildItem(Material.GOLDEN_APPLE, 1, 0, "&eGoldenApple Rate", " ", "&fCurrent Rate:&6 " + game.getGoldenAppleRate() + "%", " ", "&7Left Click to &aadd 1% rate", "&7Right Click to &cremovd 1% rate", " ", "&c&lThis Rate only for luckyLeaves!"));
			} else if (ITEM.equals("Shears")) {
				game.setShears(!game.isShears());
				if (!game.isShears()) {
					inv.setItem(26, ItemUtil.buildItem(Material.SHEARS, 1, 0, "&eShears", " ", "&fCurrent Status:&c&l Disabled"));
				} else {
					inv.setItem(26, ItemUtil.buildItem(Material.SHEARS, 1, 0, "&eShears", " ", "&fCurrent Status:&a&l Enabled"));
				}
			} else if (ITEM.equals("Final Heal")) {
				game.setFinalheal(!game.isFinalheal());
				if (!game.isFinalheal()) {
					inv.setItem(27, ItemUtil.buildItem(Material.POTION, 1, 8261, "&eFinal Heal", " ", "&fCurrent Status: &c&lDisabled"));
				} else {
					inv.setItem(27, ItemUtil.buildItemEnchantment(Material.POTION, 1, 8261, "&eFinal Heal", " ", "&fCurrent Status: &a&lEnabled"));
				}
			} else if (ITEM.equals("Death Kick")) {
				game.setDeathKick(!game.isDeathKick());
				if (!game.isDeathKick()) {
					inv.setItem(28, ItemUtil.buildSkullItem("zombie", "&eDeath Kick", " ", "&fCurrent Status: &c&lDisabled"));
				} else {
					inv.setItem(28, ItemUtil.buildSkullItem("zombie", "&eDeath Kick", " ", "&fCurrent Status: &a&lEnabled"));
				}
			} else if (ITEM.equals("Ender Pearl")) {
				game.setEnderpearl(!game.isEnderpearl());
				if (!game.isEnderpearl()) {
					inv.setItem(29, ItemUtil.buildItem(Material.ENDER_PEARL, 1, 0, "&eEnder Pearl", " ", "&fCurrent Status: &c&lDisabled"));
				} else {
					inv.setItem(29, ItemUtil.buildItemEnchantment(Material.ENDER_PEARL, 1, 0, "&eEnder Pearl", " ", "&fCurrent Status: &a&lEnabled"));
				}
			} else if (ITEM.equals("Nether")) {
				game.setNether(!game.isNether());
				if (!game.isNether()) {
					inv.setItem(30, ItemUtil.buildItem(Material.NETHER_BRICK, 1, 0, "&eNether", " ", "&fCurrent Status: &c&lDisabled"));
				} else {
					inv.setItem(30, ItemUtil.buildItemEnchantment(Material.NETHER_BRICK, 1, 0, "&eNether", " ", "&fCurrent Status: &a&lEnabled"));
				}
			} else if (ITEM.equals("Speed")) {
				game.setSpeed(game.getSpeed() + 1 >= 3 ? 0 : game.getSpeed() + 1);
				if (game.getSpeed() == 0) {
					inv.setItem(31, ItemUtil.buildItem(Material.POTION, 1, 8194, "&eSpeed", " ", "&fCurrent Status:&c&l Disabled", " ", "&7Click me to &aenable&7 !"));
				} else if (game.getSpeed() == 1) {
					inv.setItem(31, ItemUtil.buildItem(Material.POTION, 1, 8194, "&eSpeed", " ", "&fCurrent Status:&a&l Level 1", " ", "&7Click me to &alimit level 2&7 !"));
				} else {
					inv.setItem(31, ItemUtil.buildItem(Material.POTION, 1, 8226, "&eSpeed", " ", "&fCurrent Status:&a&l Level 2", " ", "&7Click me to &cDisable&7 !"));
				}
			} else if (ITEM.equals("Streght")) {
				game.setStreght(game.getStreght() + 1 >= 3 ? 0 : game.getStreght() + 1);
				if (game.getStreght() == 0) {
					inv.setItem(32, ItemUtil.buildItem(Material.POTION, 1, 8201, "&eStreght", " ", "&fCurrent Status:&c&l Disabled", " ", "&7Click me to &aenable&7 !"));
				} else if (game.getStreght() == 1) {
					inv.setItem(32, ItemUtil.buildItem(Material.POTION, 1, 8201, "&eStreght", " ", "&fCurrent Status:&a&l Level 1", " ", "&7Click me to &alimit level 2&7 !"));
				} else {
					inv.setItem(32, ItemUtil.buildItem(Material.POTION, 1, 8233, "&eStreght", " ", "&fCurrent Status:&a&l Level 2", " ", "&7Click me to &cDisable&7 !"));
				}
			} else if (ITEM.equals("Team mode")) {
				team.setTeamsEnabled(!team.isTeamsEnabled());
				if (!team.isTeamsEnabled()) {
					inv.setItem(9, ItemUtil.buildItem(Material.BOOK, 1, 0, "&eTeam mode", " ", "&fCurrent Status: &c&lDisabled"));
					inv.setItem(10, ItemUtil.buildItem(Material.STAINED_GLASS_PANE, 1, 14, " "));
					inv.setItem(11, ItemUtil.buildItem(Material.STAINED_GLASS_PANE, 1, 14, " "));
					inv.setItem(12, ItemUtil.buildItem(Material.STAINED_GLASS_PANE, 1, 14, " "));
					inv.setItem(13, ItemUtil.buildItem(Material.STAINED_GLASS_PANE, 1, 14, " "));
				} else {
					inv.setItem(9, ItemUtil.buildItemEnchantment(Material.BOOK, 1, 0, "&eTeam mode", " ", "&fCurrent Status: &a&lEnabled"));
					inv.setItem(10, ItemUtil.buildItem(Material.PAPER, 1, 0, "&ePre Team Size", " ", "&fCurrent Count:&6 " + team.getMaxSize(), " ", "&7Left click to &a+1", "&7Right click to &c-1"));
					inv.setItem(11, ItemUtil.buildItem(Material.PAPER, 1, 0, "&eMax Teams", " ", "&fCurrent Count:&6 " + team.getTeamSize(), " ", "&7Left click to &a+1", "&7Right click to &c-1"));
					if (!team.canDamageTeamMembers()) {
						inv.setItem(12, ItemUtil.buildItem(351, 1, 8, "&eFriendly Fire", " ", "&fCurrent Status:&c&l Disabled"));
					} else {
						inv.setItem(12, ItemUtil.buildItem(351, 1, 10, "&eFriendly Fire", " ", "&fCurrent Status:&a&l Enabled"));
					}
					if (!game.isBackpack()) {
						inv.setItem(13, ItemUtil.buildItem(342, 1, 0, "&eBackpack", " ", "&fCurrent Status:&c&l Disabled"));
					} else {
						inv.setItem(13, ItemUtil.buildItemEnchantment(342, 1, 0, "&eBackpack", " ", "&fCurrent Status:&a&l Enabled"));
					}
				}
			} else if (ITEM.equals("Pre Team Size")) {
				team.setMaxSize(act.equals(InventoryAction.PICKUP_ALL) ? team.getMaxSize() + 1 : team.getMaxSize() - 1);
				inv.setItem(10, ItemUtil.buildItem(Material.PAPER, 1, 0, "&ePre Team Size", " ", "&fCurrent Count:&6 " + team.getMaxSize(), " ", "&7Left click to &a+1", "&7Right click to &c-1"));
			} else if (ITEM.equals("Max Teams")) {
				team.setTeamSize(act.equals(InventoryAction.PICKUP_ALL) ? team.getTeamSize() + 1 : team.getTeamSize() - 1);
				inv.setItem(11, ItemUtil.buildItem(Material.PAPER, 1, 0, "&eMax Teams", " ", "&fCurrent Count:&6 " + team.getTeamSize(), " ", "&7Left click to &a+1", "&7Right click to &c-1"));
			} else if (ITEM.equals("Friendly Fire")) {
				team.setCanDamageTeamMembers(!team.canDamageTeamMembers());
				if (!team.canDamageTeamMembers()) {
					inv.setItem(12, ItemUtil.buildItem(351, 1, 8, "&eFriendly Fire", " ", "&fCurrent Status:&c&l Disabled"));
				} else {
					inv.setItem(12, ItemUtil.buildItem(351, 1, 10, "&eFriendly Fire", " ", "&fCurrent Status:&a&l Enabled"));
				}
			} else if (ITEM.equals("Deathmatch")) {
				game.setDeathmatch(!game.isDeathmatch());
				if (!game.isDeathmatch()) {
					inv.setItem(25, ItemUtil.buildItem(Material.DIAMOND_SWORD, 1, 0, "&eDeathmatch", " ", "&fCurrent Status:&c&l Disabled"));
				} else {
					inv.setItem(25, ItemUtil.buildItemEnchantment(Material.DIAMOND_SWORD, 1, 0, "&eDeathmatch", " ", "&fCurrent Status:&a&l Enabled"));
				}
			} else if (ITEM.equals("Backpack")) {
				game.setBackpack(!game.isBackpack());
				if (!game.isBackpack()) {
					inv.setItem(13, ItemUtil.buildItem(342, 1, 0, "&eBackpack", " ", "&fCurrent Status:&c&l Disabled"));
				} else {
					inv.setItem(13, ItemUtil.buildItemEnchantment(342, 1, 0, "&eBackpack", " ", "&fCurrent Status:&a&l Enabled"));
				}
			} else if (ITEM.equals("GoldenHead")) {
				game.setGoldenHead(!game.isGoldenHead());
				if (!game.isGoldenHead()) {
					inv.setItem(24, ItemUtil.buildItem(Material.GOLDEN_APPLE, 1, 0, "&eGoldenHead", " ", "&fCurrent Status:&c&l Disabled"));
				} else {
					inv.setItem(24, ItemUtil.buildItemEnchantment(Material.GOLDEN_APPLE, 1, 0, "&eGoldenHead", " ", "&fCurrent Status:&a&l Enabled"));
				}
			} else if (ITEM.equals("Announces")) {
				new AnnounceGUI().open(p);
			}
			UHCSound.CLICK.playSound(p);
			config();
		} else if (inv.getTitle().equals("§6§lUHC §f§lConfig")) {
			e.setCancelled(true);
			if (ITEM.equals("Scenarios")) {
				if (Scenarios.getScenariosList().isEmpty()) {
					p.sendMessage(Lang.getMsg(p, "NoScenarios"));
					return;
				}
				p.closeInventory();
				new ScenariosWatchGUI(p).open(p);
			}
		}
	}

	public Inventory openAliveInventory(final int page) {
		final List<UUID> players1 = new ArrayList<>(game.getPlayersUUID());
		final int players = players1.size();
		final int size = 54;
		final Inventory inventory = Bukkit.createInventory(new SpectatorHolder(page), size, "§6§lPlayer§f§lList §7#" + page);

		int j = 0;
		for (int i = (page - 1) * 45; i < size + (page - 1) * 45; i++) {
			if (players1.size() < i + 1) {
				continue;
			}
			final String name = UUIDCache.getName(players1.get(i));
			inventory.setItem(j, ItemUtil.buildSkullItem(name, ChatColor.YELLOW + name));
			j++;
		}

		if (page != 1) {
			inventory.setItem(45, ItemUtil.buildItem(Material.ARROW, "&6<--"));
		} else {
			inventory.setItem(45, ItemUtil.buildItem(Material.STAINED_GLASS_PANE, 1, 14, " "));
		}
		for (int i = 46; i < 53; i++) {
			inventory.setItem(i, ItemUtil.buildItem(Material.STAINED_GLASS_PANE, 1, 14, " "));
		}

		if (Math.ceil(players / 45) > page - 1) {
			inventory.setItem(53, ItemUtil.buildItem(Material.ARROW, "&6-->"));
		} else {
			inventory.setItem(53, ItemUtil.buildItem(Material.STAINED_GLASS_PANE, 1, 14, " "));
		}

		return inventory;
	}

	public Inventory openNetherAliveInventory(final int page) {
		if (!game.isNether() || Bukkit.getWorld("UHCArena_nether") == null)
			return null;
		final List<Player> player = new ArrayList<>(Bukkit.getWorld("UHCArena_nether").getPlayers());

		for (final Player p : new ArrayList<>(player)) {
			if (!iUHC.getInstance().getProfileManager().isAlive(p)) {
				player.remove(p);
			}
		}

		final int players = player.size();
		final int size = 54;
		final Inventory inventory = Bukkit.createInventory(new SpectatorHolder(page), size, "§6§lNether§f§lList §7#" + page);

		int j = 0;
		for (int i = (page - 1) * 45; i < size + (page - 1) * 45; i++) {
			if (player.size() < i + 1) {
				continue;
			}
			final String name = player.get(i).getName();
			inventory.setItem(j, ItemUtil.buildSkullItem(name, ChatColor.YELLOW + name));
			j++;
		}

		if (page != 1) {
			inventory.setItem(45, ItemUtil.buildItem(Material.ARROW, "&c<--"));
		} else {
			inventory.setItem(45, ItemUtil.buildItem(Material.STAINED_GLASS_PANE, 1, 14, " "));
		}
		for (int i = 46; i < 53; i++) {
			inventory.setItem(i, ItemUtil.buildItem(Material.STAINED_GLASS_PANE, 1, 14, " "));
		}

		if (Math.ceil(players / 45) > page - 1) {
			inventory.setItem(53, ItemUtil.buildItem(Material.ARROW, "&c-->"));
		} else {
			inventory.setItem(53, ItemUtil.buildItem(Material.STAINED_GLASS_PANE, 1, 14, " "));
		}

		return inventory;
	}

	public void createSnapshot(final Player p) {
		this.snapshots.put(p.getUniqueId(), new InventorySnapshot(p));
	}

	public InventorySnapshot getSnapshot(final UUID uuid) {
		return snapshots.get(uuid);
	}

	public int getRows(final int size) {
		return (int) Math.ceil((double) size / 9);
	}

	@Getter
	public static class SpectatorHolder implements InventoryHolder {

		private final int page;

		public SpectatorHolder(final int page) {
			this.page = page;
		}

		@Override
		public Inventory getInventory() {
			return null;
		}

	}

}
