package spg.lgdev.uhc.gui.announcement;

import spg.lgdev.uhc.announce.AbstractAnnounce;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.announce.AnnounceManager;
import spg.lgdev.uhc.gui.GUI;
import spg.lgdev.uhc.util.ItemUtil;

public class AnnounceGUI extends GUI {

	public AnnounceGUI() {
		super("§e§lAnnounce§f§l System", 1);

		final AnnounceManager manager = iUHC.getInstance().getAnnounceManager();

		if (manager.getDiscord().isEnabled()) {
			setItem(0, ItemUtil.buildItem(Material.WOOL, 1, 10, "&5Post discord embed", "&7Click me to post discord embed!"));
		} else {
			setNullItem(0);
		}

		if (manager.getTwitter().isEnabled()) {
			setItem(1, ItemUtil.buildItem(Material.WOOL, 1, 3, "&bPost tweet", "&7Click me to post tweet!"));
		} else {
			setNullItem(1);
		}

		setItem(7, ItemUtil.buildItem(Material.WATCH, 1, 0, "&6&lTime Editor &7(Start times)",
				"&7Click me to edit start times!"));
		setItem(8, ItemUtil.buildItem(Material.WATCH, 1, 0, "&6&lTime Editor &7(Whitelist off times)",
				"&7Click me to edit whitelist off times!"));
	}

	@Override
	public void onClick(final Player player, final ItemStack itemStack) {
		final String name = itemStack.getItemMeta().getDisplayName();
		if (name.equals("§6§lTime Editor §7(Start times)")) {
			player.closeInventory();
			AbstractAnnounce.editStartTimes(player);
			player.sendMessage("§aYou are now editing start times! please type it in chat!");
		} else if (name.equals("§6§lTime Editor §7(Whitelist off times)")) {
			player.closeInventory();
			AbstractAnnounce.editWhitelistOffTimes(player);
			player.sendMessage("§aYou are now editing start times! please type it in chat!");
		} else if (name.equals("§5Post discord embed")) {
			player.closeInventory();
			new DiscordGUI().open(player);
		} else if (name.equals("§bPost tweet")) {
			player.closeInventory();
			new TwitterGUI().open(player);
		}
	}

}
