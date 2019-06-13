package spg.lgdev.uhc.gui.gameconfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import spg.lgdev.uhc.enums.InventoryTypes;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.manager.InventoryManager;
import spg.lgdev.uhc.manager.TeamManager;
import spg.lgdev.uhc.scenario.Scenarios;
import spg.lgdev.uhc.util.ItemUtil;
import spg.lgdev.uhc.util.StringUtil;
import spg.lgdev.uhc.util.UHCSound;
import spg.lgdev.uhc.util.Utils;
import spg.lgdev.uhc.util.signgui.SignFinishCallback;
import spg.lgdev.uhc.util.signgui.SignGUI;
import net.md_5.bungee.api.ChatColor;

public class PreConfigGUI implements Listener {

	@Getter
	private static Map<UUID, Integer> editingNames = new HashMap<>();
	@Getter
	private static PreConfigGUI instance;

	private final String title;
	private final Inventory inv;

	public PreConfigGUI() {
		instance = this;
		title = Colored("&6&lPre&f&l Configuration");
		inv = Bukkit.createInventory(null, 5 * 9, title);
		updateGUI();
		for (int i = 36; i < 44; i++) {
			inv.setItem(i, ItemUtil.buildItem(Material.STAINED_GLASS_PANE, 1, 14, " "));
		}
		inv.setItem(44, ItemUtil.buildItem(351, 1, 1, "&cBack to Main Edit Menu"));
	}

	public static String Colored(final String s) {
		return StringUtil.cc(s);
	}

	public void o(final Player p) {
		p.openInventory(inv);
	}

	public void updateGUI() {
		final FileConfiguration config = iUHC.getInstance().getFileManager().getCache();
		for (int i = 0; i <= 8; i++) {
			if (config.getBoolean("saveconfig." + i + ".saved")) {
				final String name = config.getString("saveconfig." + i + ".name");
				final List<String> scen = config.getStringList("saveconfig." + i + ".scenarios");
				final List<String> settings = config.getStringList("saveconfig." + i + ".settings");
				inv.setItem(i, ItemUtil.buildItem(Material.ENCHANTED_BOOK, 1, 0,
						"&e&lConfig &7[&6" + Colored(name) + "&7]", "&7click me to use this configuration!", "&7right click me to edit the name of configuration!"));
				inv.setItem(i + 9, ItemUtil.buildItem(Material.CHEST, 1, 0, "&b&lScenarios: ", scen));
				inv.setItem(i + 18, ItemUtil.buildItem(Material.WATCH, 1, 0, "&aSettings:",
						"&fFinalHeal: &6" + Utils.formatTimeHours(Integer.parseInt(settings.get(0))),
						"&fPVP: &6" + Utils.formatTimeHours(Integer.parseInt(settings.get(1))),
						"&fFirst Border: &6" + Utils.formatTimeHours(Integer.parseInt(settings.get(2))),
						"&fNether: &6" + settings.get(3), "&fSpeed: &b" + settings.get(4),
						"&fStrenght: &6" + settings.get(5), "&fTimeBomb: &6" + settings.get(7) + "s"
						, "&9Team Mode:", "&fTeamSize: &a" + settings.get(6)));
				inv.setItem(i + 27, ItemUtil.buildItem(Material.WOOD_DOOR, 1, 0, "&c&lDelete this config",
						"&7Click me to delete this config!"));
			} else {
				inv.setItem(i, ItemUtil.buildItem(Material.PAPER, 1, 0, "&e&lConfig &7[&c&lNotSaved&7]",
						"&7Click me to save a configuration of the settings right now!"));
				inv.setItem(i + 9, new ItemStack(Material.AIR));
				inv.setItem(i + 18, new ItemStack(Material.AIR));
				inv.setItem(i + 27, new ItemStack(Material.AIR));
			}
		}
	}

	@EventHandler
	public void onClick(final InventoryClickEvent e) {

		if (e.getInventory() == null)
			return;

		final String name = e.getInventory().getName();

		if (!name.equals(title)) return;

		if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)) return;

		final int slot = e.getSlot();

		final Player p = (Player) e.getWhoClicked();

		e.setCancelled(true);

		UHCSound.CLICK.playSound(p);
		final FileConfiguration config = iUHC.getInstance().getFileManager().getCache();

		for (int i = 0; i < 9; i++) {

			if (slot == i) {

				final boolean saved = config.getBoolean("saveconfig." + i + ".saved");

				if (saved) {

					final List<String> settings = config.getStringList("saveconfig." + i + ".settings");

					if (e.getAction().equals(InventoryAction.PICKUP_HALF)) {

						getEditingNames().put(p.getUniqueId(), i);
						p.closeInventory();

						SignGUI.openSignEditor(p, new String[]{"write name down below", "", "click buttom under sign", "to finish editing!"}, new SignFinishCallback() {
							@Override
							public void onFinish(final String[] lines) {
								if (lines[1] == null || lines[1].equals(""))
									return;
								setName(p, getEditingNames().get(p.getUniqueId()), lines[1]);
							}
						});
						return;

					}

					Scenarios.applyScenarios(config.getStringList("saveconfig." + i + ".scenarios"));

					UHCGame.getInstance().setFinalHealTime(Integer.parseInt(settings.get(0)));
					UHCGame.getInstance().setPvpTime(Integer.parseInt(settings.get(1)));
					UHCGame.getInstance().setFirstBorder(Integer.parseInt(settings.get(2)));
					UHCGame.getInstance().setNether(Boolean.parseBoolean(settings.get(3)));
					UHCGame.getInstance().setSpeed(Integer.parseInt(settings.get(4)));
					UHCGame.getInstance().setStreght(Integer.parseInt(settings.get(5)));
					if (Integer.parseInt(settings.get(6)) == 1) {
						TeamManager.getInstance().setTeamsEnabled(false);
					} else {
						TeamManager.getInstance().setTeamsEnabled(true);
						TeamManager.getInstance().setMaxSize(Integer.parseInt(settings.get(6)));
					}
					UHCGame.getInstance().setTimebombTimer(Integer.parseInt(settings.get(7)));

					p.sendMessage(ChatColor.YELLOW + "You used the configuration of " + config.getString("saveconfig." + i + ".name") + ChatColor.YELLOW + " !");
					return;

				}

				final List<String> settings = new ArrayList<>();
				settings.add(UHCGame.getInstance().getFinalHealTime() + "");
				settings.add(UHCGame.getInstance().getPvpTime() + "");
				settings.add(UHCGame.getInstance().getFirstBorder() + "");
				settings.add(UHCGame.getInstance().isNether() + "");
				settings.add(UHCGame.getInstance().getSpeed() + "");
				settings.add(UHCGame.getInstance().getStreght() + "");
				if (!TeamManager.getInstance().isTeamsEnabled()) {
					settings.add(1 + "");
				} else {
					settings.add(TeamManager.getInstance().getMaxSize() + "");
				}
				settings.add(UHCGame.getInstance().getTimebombTimer() + "");
				config.set("saveconfig." + i + ".saved", true);
				config.set("saveconfig." + i + ".scenarios", Scenarios.getScenariosList());
				config.set("saveconfig." + i + ".settings", settings);
				config.set("saveconfig." + i + ".name", "Config " + i);
				iUHC.getInstance().getFileManager().saveCache();
				updateGUI();
				p.sendMessage(ChatColor.GREEN + "You created a new configuration!");

				return;


			}
		}
		if (e.getCurrentItem().getType().equals(Material.WOOD_DOOR)) {
			final int i = slot;
			final int i1 = i - 27;
			if (config.getString("saveconfig." + i1 + ".saved") != null && config.getBoolean("saveconfig." + i1 + ".saved")) {
				config.set("saveconfig." + i1 + ".saved", false);
				iUHC.getInstance().getFileManager().saveCache();
				updateGUI();
				p.sendMessage(ChatColor.RED + "You deleted the configuration!");
			}
			return;
		}
		if (e.getCurrentItem().getTypeId() == 351) {
			p.closeInventory();
			InventoryManager.instance.createInventory(p, InventoryTypes.Config_Editor);
			return;
		}

	}

	public void setName(final Player p, final int i, final String name) {
		iUHC.getInstance().getFileManager().getCache().set("saveconfig." + i + ".name", Colored(name));
		iUHC.getInstance().getFileManager().saveCache();
		p.sendMessage(ChatColor.GREEN + "You renamed the configuration!");
		getEditingNames().remove(p.getUniqueId());
		updateGUI();
		o(p);
	}

}
