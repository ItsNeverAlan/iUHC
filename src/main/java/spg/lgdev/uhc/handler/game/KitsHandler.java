package spg.lgdev.uhc.handler.game;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import spg.lgdev.uhc.iUHC;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import spg.lgdev.uhc.config.Configuration;
import spg.lgdev.uhc.exception.NewInstanceRejectException;
import spg.lgdev.uhc.util.Base64Inventory;

@SuppressWarnings("deprecation")
public class KitsHandler {

	@Getter
	private static KitsHandler instance;
	public boolean isTemporaly = false;
	@Getter
	public ItemStack[] armor;
	@Getter
	public ItemStack[] content;

	public KitsHandler() {
		if (instance != null && instance != this)
			throw new NewInstanceRejectException("KitHandler");
		instance = this;
	}

	public void giveKit(final Player p, final String type) {

		final FileConfiguration config = iUHC.getInstance().getFileManager().getCache();

		if (type.equals("PRACTICE")) {
			if (config.getString("practice") == null)
				return;
		} else {
			if (config.getString("starter") == null)
				return;
		}

		String contentPath = "";

		if (type.equals("PRACTICE")) {
			contentPath = "practice";
			ItemStack[] content = null;
			ItemStack[] armor = null;
			try {
				content = Base64Inventory.itemStackArrayFromBase64(config.getStringList(contentPath).get(0));
				armor = Base64Inventory.itemStackArrayFromBase64(config.getStringList(contentPath).get(1));
			} catch (final IOException e) {
				e.printStackTrace();
			}

			p.getInventory().setContents(content);
			p.getInventory().setArmorContents(armor);

			p.updateInventory();
			return;
		} else if (this.armor != null && this.content != null) {
			p.getInventory().setArmorContents(this.armor);
			p.getInventory().setContents(this.content);
			p.updateInventory();
			return;
		}

		contentPath = "starter";
		ItemStack[] content = null;
		ItemStack[] armor = null;
		try {
			content = Base64Inventory.itemStackArrayFromBase64(config.getStringList(contentPath).get(0));
			armor = Base64Inventory.itemStackArrayFromBase64(config.getStringList(contentPath).get(1));
		} catch (final IOException e) {
			e.printStackTrace();
		}

		p.getInventory().setContents(content);
		p.getInventory().setArmorContents(armor);

		p.updateInventory();

	}

	public void giveKitWithoutSaved(final Player p, final String type) {

		final FileConfiguration config = iUHC.getInstance().getFileManager().getCache();

		String contentPath = "";

		if (type.equals("PRACTICE")) {
			if (config.getString("practice") == null)
				return;
			contentPath = "practice";
		} else {
			if (config.getString("starter") == null)
				return;
			contentPath = "starter";
		}

		ItemStack[] content = null;
		ItemStack[] armor = null;
		try {
			content = Base64Inventory.itemStackArrayFromBase64(config.getStringList(contentPath).get(0));
			armor = Base64Inventory.itemStackArrayFromBase64(config.getStringList(contentPath).get(1));
		} catch (final IOException e) {
			e.printStackTrace();
		}

		p.getInventory().setContents(content);
		p.getInventory().setArmorContents(armor);

		p.updateInventory();

	}

	public void saveKit(final Player p, final String type) {

		if (this.isTemporaly)
			return;

		String contentPath;

		if (type.equals("PRACTICE")) {
			contentPath = "practice";
		} else {
			contentPath = "starter";
		}

		final List<String> list = Arrays.asList(Base64Inventory.itemStackArrayToBase64(p.getInventory().getContents()),
				Base64Inventory.itemStackArrayToBase64(p.getInventory().getArmorContents()));
		iUHC.getInstance().getFileManager().getCache().set(contentPath, list);

		iUHC.getInstance().getFileManager().saveCache();

	}

	public void setKitInToItemStack() {

		if (this.isTemporaly)
			return;

		final Configuration config = iUHC.getInstance().getFileManager().getCache();
		final List<String> list = config.getStringList("starter");

		ItemStack[] content = null;
		ItemStack[] armor = null;

		try {
			content = Base64Inventory.itemStackArrayFromBase64(list.get(0));
			armor = Base64Inventory.itemStackArrayFromBase64(list.get(1));
		} catch (final IOException e) {
			e.printStackTrace();
		}

		this.armor = armor;
		this.content = content;

	}

}
