package spg.lgdev.uhc.gui.announcement;

import spg.lgdev.uhc.announce.AbstractAnnounce;
import spg.lgdev.uhc.iUHC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import spg.lgdev.uhc.enums.AnnounceType;
import spg.lgdev.uhc.gui.GUI;
import spg.lgdev.uhc.util.ItemBuilder;
import spg.lgdev.uhc.util.ItemUtil;

public class TwitterGUI extends GUI {

	public TwitterGUI() {
		super("&b&lPost &f&lTweet", 1);
		setItem(0, ItemUtil.buildItem(Material.EMERALD, 1, 0, "&a&lPost tweet", "&7Click me to post the tweet!"));

		final ItemBuilder itemBuilder = new ItemBuilder(Material.PAPER);
		itemBuilder.name("&e&lTweet preview").lore(" ");
		for (final String lore : AbstractAnnounce.getFormattedAnnounceMessage(AnnounceType.TWITTER).split("\n")) {
			itemBuilder.lore("§7- §f" + lore);
		}
		setItem(4, itemBuilder.lore(" ").build());
		setItem(8, ItemUtil.buildItem(Material.REDSTONE_BLOCK, 1, 0, "&c&lStop sending",
				"&7Click me to stop sending the tweet!"));
	}

	@Override
	public void onClick(final Player player, final ItemStack itemStack) {
		final String name = itemStack.getItemMeta().getDisplayName();
		if (name.equals("§a§lPost tweet")) {
			player.closeInventory();
			if (iUHC.getInstance().getAnnounceManager().getTwitter().postAnnounce(AbstractAnnounce.getFormattedAnnounceMessage(AnnounceType.TWITTER))) {
				player.sendMessage("§asuccess post the tweet");
				return;
			}
			player.sendMessage("§cfailed to post the tweet! maybe theres some error on console!");
		} else if (name.equals("§c§lStop sending")) {
			player.closeInventory();
			player.sendMessage(ChatColor.RED + "You are now stopped sending embed!");
		}
	}

}
