package spg.lgdev.uhc.gui.announcement;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.announce.AbstractAnnounce;
import spg.lgdev.uhc.enums.AnnounceType;
import spg.lgdev.uhc.gui.GUI;
import spg.lgdev.uhc.util.ItemBuilder;
import spg.lgdev.uhc.util.ItemUtil;

public class DiscordGUI extends GUI {

	public DiscordGUI() {
		super("&5&lDiscord &f&lPoster", 1);
		setItem(0, ItemUtil.buildItem(Material.EMERALD, 1, 0, "&a&lSend embed", "&7Click me to send the embed!"));

		final ItemBuilder itemBuilder = new ItemBuilder(Material.PAPER);
		itemBuilder.name("&e&lEmbed preview").lore(" ");
		for (final String lore : AbstractAnnounce.getFormattedAnnounceMessage(AnnounceType.DISCORD).split("\n")) {
			itemBuilder.lore("§7- §f" + lore);
		}
		setItem(4, itemBuilder.lore(" ").build());
		setItem(8, ItemUtil.buildItem(Material.REDSTONE_BLOCK, 1, 0, "&c&lStop sending",
				"&7Click me to stop sending embed!"));
	}

	@Override
	public void onClick(final Player player, final ItemStack itemStack) {
		final String name = itemStack.getItemMeta().getDisplayName();
		if (name.equals("§a§lSend embed")) {
			player.closeInventory();
			if (iUHC.getInstance().getAnnounceManager().getDiscord().postAnnounce(AbstractAnnounce.getFormattedAnnounceMessage(AnnounceType.DISCORD))) {
				player.sendMessage("§asuccess send the embed");
				return;
			}
			player.sendMessage("§cfailed to send the embed! maybe theres some error on console!");
		} else if (name.equals("§c§lStop sending")) {
			player.closeInventory();
			player.sendMessage(ChatColor.RED + "You are now stopped sending embed!");
		}
	}

}
