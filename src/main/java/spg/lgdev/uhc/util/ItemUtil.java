package spg.lgdev.uhc.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemUtil {

	public static ItemStack buildSkullItem(final String owner, final String name, final String... loreOptions) {

		final ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
		final SkullMeta meta = (SkullMeta) item.getItemMeta();

		meta.setOwner(owner);
		meta.setDisplayName(StringUtil.cc( name));

		final List<String> color = new ArrayList<>();

		for (final String string : loreOptions) {
			color.add(StringUtil.cc( string));
		}

		meta.setLore(color);
		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack buildSkullItem(final String owner, final String name, final int amount, final String... loreOptions) {

		final ItemStack item = new ItemStack(Material.SKULL_ITEM, amount, (byte) 3);
		final SkullMeta meta = (SkullMeta) item.getItemMeta();

		meta.setOwner(owner);
		meta.setDisplayName(StringUtil.cc( name));

		final List<String> color = new ArrayList<>();

		for (final String string : loreOptions) {
			color.add(StringUtil.cc( string));
		}

		meta.setLore(color);
		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack buildItem(final int id, final int amount, final int data, final String name, final String... lore) {

		final ItemStack item = new ItemStack(id, amount, (short) data);
		final ItemMeta meta = item.getItemMeta();

		final List<String> color = new ArrayList<>();

		for (final String string : lore) {
			color.add(StringUtil.cc( string));
		}

		meta.setDisplayName(StringUtil.cc( name));
		meta.setLore(color);
		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack buildItem(final Material mat, final int amount, final int data, final String name, final String... lore) {

		final ItemStack item = new ItemStack(mat, amount, (short) data);
		final ItemMeta meta = item.getItemMeta();

		final List<String> color = new ArrayList<>();

		for (final String string : lore) {
			color.add(StringUtil.cc(string));
		}

		meta.setDisplayName(StringUtil.cc(name));
		meta.setLore(color);
		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack buildItem(final int id, final int amount, final int data, final String name, final List<String> lore) {

		final ItemStack item = new ItemStack(id, amount, (short) data);
		final ItemMeta meta = item.getItemMeta();

		final List<String> color = new ArrayList<>();

		for (final String string : lore) {
			color.add(StringUtil.cc( string));
		}

		meta.setDisplayName(StringUtil.cc( name));
		meta.setLore(color);
		item.setItemMeta(meta);

		return item;

	}

	public static ItemStack buildItem(final Material mat, final int amount, final int data, final String name, final List<String> lore) {

		final ItemStack item = new ItemStack(mat, amount, (short) data);
		final ItemMeta meta = item.getItemMeta();

		final List<String> color = new ArrayList<>();

		for (final String string : lore) {
			color.add(StringUtil.cc( string));
		}

		meta.setDisplayName(StringUtil.cc( name));
		meta.setLore(color);
		item.setItemMeta(meta);

		return item;

	}

	public static ItemStack buildItem(final Material mat, final int amount, final int data, final String name, final String[] lore, final String... lore2) {
		final ItemStack item = new ItemStack(mat, amount, (short) data);
		final ItemMeta meta = item.getItemMeta();

		final List<String> color = new ArrayList<>();

		for (final String string : lore) {
			color.add(StringUtil.cc( string));
		}

		for (final String string : lore2) {
			color.add(StringUtil.cc( string));
		}

		meta.setDisplayName(StringUtil.cc( name));
		meta.setLore(color);
		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack buildItemEnchantment(final int id, final int amount, final int data, final String name, final String... lore) {

		final ItemStack item = new ItemStack(id, amount, (short) data);
		final ItemMeta meta = item.getItemMeta();

		final List<String> color = new ArrayList<>();

		for (final String string : lore) {
			color.add(StringUtil.cc( string));
		}

		meta.setDisplayName(StringUtil.cc( name));
		meta.setLore(color);
		item.setItemMeta(meta);

		EnchantGlow.addGlow(item);

		return item;
	}

	public static ItemStack buildItemEnchantment(final Material material, final int amount, final int data, final String name, final String... lore) {

		final ItemStack item = new ItemStack(material, amount, (short) data);
		final ItemMeta meta = item.getItemMeta();

		final List<String> color = new ArrayList<>();

		for (final String string : lore) {
			color.add(StringUtil.cc( string));
		}

		meta.setDisplayName(StringUtil.cc( name));
		meta.setLore(color);
		item.setItemMeta(meta);

		EnchantGlow.addGlow(item);

		return item;
	}

	public static ItemStack buildItemEnchantment(final int id, final int amount, final int data, final String name, final List<String> lore) {

		final ItemStack item = new ItemStack(id, amount, (short) data);
		final ItemMeta meta = item.getItemMeta();

		final List<String> color = new ArrayList<>();

		for (final String string : lore) {
			color.add(StringUtil.cc( string));
		}

		meta.setDisplayName(StringUtil.cc( name));
		meta.setLore(color);
		item.setItemMeta(meta);

		EnchantGlow.addGlow(item);

		return item;
	}

	public static ItemStack buildItem(final Material material, final String name) {
		final ItemStack itemStack = new ItemStack(material);
		final ItemMeta meta = itemStack.getItemMeta();
		meta.setDisplayName(StringUtil.cc( name));
		itemStack.setItemMeta(meta);
		return itemStack;
	}

	public static ItemStack buildItem(final Material material, final String name, final int amount) {
		final ItemStack item = new ItemStack(material, amount);
		final ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(name);
		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack buildItem(final Material material, final String name, final int amount, final short damage) {
		final ItemStack item = new ItemStack(material, amount, damage);
		final ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(name);
		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack renameItem(final ItemStack item, final String name) {
		final ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(name);
		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack reloreItem(final ItemStack item, final String... lores) {
		return reloreItem(ReloreType.OVERWRITE, item, lores);
	}

	public static ItemStack reloreItem(final ReloreType type, final ItemStack item, final String... lores) {
		final ItemMeta meta = item.getItemMeta();

		List<String> lore = meta.getLore();
		if (lore == null) {
			lore = new LinkedList<>();
		}

		switch (type) {
		case APPEND:
			lore.addAll(Arrays.asList(lores));
			meta.setLore(lore);
			break;
		case PREPEND:
			final List<String> nLore = new LinkedList<>(Arrays.asList(lores));
			nLore.addAll(lore);
			meta.setLore(nLore);
			break;
		case OVERWRITE:
			meta.setLore(Arrays.asList(lores));
			break;
		}

		item.setItemMeta(meta);
		return item;
	}

	public enum ReloreType {
		OVERWRITE,
		PREPEND,
		APPEND
	}
}

class EnchantGlow extends EnchantmentWrapper {

	private static Enchantment glow;

	public EnchantGlow(final int id) {
		super(id);
	}

	public static Enchantment getGlow() {
		if (glow != null)
			return glow;
		try {
			final Field f = Enchantment.class.getDeclaredField("acceptingNew");
			f.setAccessible(true);
			f.set(null, true);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		glow = new EnchantGlow(255);
		Enchantment.registerEnchantment(glow);
		return glow;
	}

	public static void addGlow(final ItemStack item) {
		final Enchantment glow = EnchantGlow.getGlow();
		item.addEnchantment(glow, 1);
	}

	@Override
	public boolean canEnchantItem(final ItemStack item) {
		return true;
	}

	@Override
	public boolean conflictsWith(final Enchantment other) {
		return false;
	}

	public EnchantmentTarget getItemTargetInstance() {
		return null;
	}

	@Override
	public int getMaxLevel() {
		return 10;
	}

	@Override
	public String getName() {
		return "Glow";
	}

	@Override
	public int getStartLevel() {
		return 1;
	}
}

