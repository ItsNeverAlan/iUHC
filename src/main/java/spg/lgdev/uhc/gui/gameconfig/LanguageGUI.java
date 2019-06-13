package spg.lgdev.uhc.gui.gameconfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.gui.GUI;
import spg.lgdev.uhc.util.ItemUtil;
import spg.lgdev.uhc.util.StringUtil;
import spg.lgdev.uhc.util.Utils;
import net.development.mitw.Mitw;

public class LanguageGUI extends GUI {

	private static LanguageGUI instance;
	private static Map<String, String> fromDisplayName = new HashMap<>();

	public LanguageGUI(final iUHC plugin) {
		super(plugin.getFileManager().getConfig().getString("LanguageGUI.Inventory-name"), Utils.getRowsBySize(CachedConfig.getLanguage().getKeys(false).size()));
		int counter = 0;
		final List<String> lores = plugin.getFileManager().getConfig().getStringList("LanguageGUI.Icon-Lores");

		for (final String lang : CachedConfig.getLanguage().getKeys(false)) {
			final List<String> iLores = new ArrayList<>();
			final String displayName = StringUtil.cc(CachedConfig.getLanguage().getString(lang + ".displayName"));
			for (final String str : lores) {
				iLores.add(StringUtil.cc(str.replaceAll("<language>", displayName)));
			}
			setItem(counter, ItemUtil.buildItem(Material.valueOf(plugin.getFileManager().getConfig().getString("LanguageGUI.Icon-material")), 1, 0, displayName, iLores));
			fromDisplayName.put(displayName, lang);
			counter++;
		}
	}

	public static LanguageGUI getInstance() {
		if (instance == null) {
			instance = new LanguageGUI(iUHC.getInstance());
		}
		return instance;
	}

	public static void resetInstance() {
		instance = null;
	}

	@Override
	public void onClick(final Player var1, final ItemStack var2) {
		if (fromDisplayName.containsKey(var2.getItemMeta().getDisplayName())) {
			Mitw.getInstance().getLanguageData().sendLangRedis(var1, fromDisplayName.get(var2.getItemMeta().getDisplayName()));
		} else {
			var1.sendMessage("§csome werid error happend...");
			throw new NullPointerException();
		}
	}

}
