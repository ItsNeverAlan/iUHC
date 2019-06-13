package spg.lgdev.uhc.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import lombok.Data;
import lombok.Getter;
import spg.lgdev.uhc.util.ItemUtil;
import spg.lgdev.uhc.util.StringUtil;

@Getter
public abstract class GUI {

	private final Inventory inventory;
	private final String displayName;

	public GUI(final String name, final int rows) {
		this.inventory = Bukkit.createInventory(new GUIHolder(this), 9 * rows, this.displayName = StringUtil.cc(name));
	}

	public void add(final ItemStack itemStack) {
		this.inventory.addItem(itemStack);
	}

	public void setItem(final int slot, final ItemStack itemStack) {
		this.inventory.setItem(slot, itemStack);
	}

	public void setNullItem(final int slot) {
		this.inventory.setItem(slot, ItemUtil.buildItem(Material.STAINED_GLASS_PANE, 1, 14, " "));
	}

	public void setItems(final ItemStack[] itemStacks) {
		this.inventory.addItem(itemStacks);
	}

	public void open(final Player p) {
		p.openInventory(this.inventory);
	}

	public abstract void onClick(Player player, ItemStack itemStack);

	public static class GUIHolder implements InventoryHolder {

		@Getter
		private final GUI gui;

		public GUIHolder(final GUI gui) {
			this.gui = gui;
		}

		@Override
		public Inventory getInventory() {
			return gui.getInventory();
		}

	}

	@Data
	public static class ItemStackData {

		private final int type;
		private byte data;
		private int amount = 1;

		public ItemStackData(final int type) {
			this.type = type;
		}

		public ItemStackData(final int type, final int data) {
			this(type);
			this.data = (byte) data;
		}

		public ItemStackData(final int type, final int data, final int amount) {
			this(type, data);
			this.amount = amount;
		}

		public ItemStackData(final Material type) {
			this(type.getId());
		}

		public ItemStackData(final Material type, final int data) {
			this(type.getId(), data);
		}

		public ItemStackData(final Material type, final int data, final int amount) {
			this(type.getId(), data, amount);
		}

		public Material getItemType() {
			return Material.getMaterial(type);
		}

	}
}

