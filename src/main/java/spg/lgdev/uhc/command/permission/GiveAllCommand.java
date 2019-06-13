package spg.lgdev.uhc.command.permission;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import spg.lgdev.uhc.command.abstracton.PermissionCommand;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.Permissions;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.util.Utils;
import net.md_5.bungee.api.ChatColor;

public class GiveAllCommand extends PermissionCommand {

	public GiveAllCommand() {
		super("giveall", Permissions.ADMIN);
	}

	public static void giveall(final CommandSender event, final String item, final String amount) {
		int amnt = 65;
		try {
			amnt = Integer.parseInt(amount);
		} catch (final NumberFormatException e) {
		}
		if (amnt > 64) {
			amnt = 64;
		}
		ItemStack is = null;
		if (item.contains(":")) {
			final String[] itemarray = item.split(":");
			int itemid = 0;
			try {
				itemid = Integer.parseInt(itemarray[0]);
			} catch (final NumberFormatException e) {
			}

			if (itemid == 0) {
				event.sendMessage(ChatColor.RED + "There was an error in parsing the item id.");
				return;
			}

			int damage = 0;
			try {
				damage = Integer.parseInt(itemarray[1]);
			} catch (final NumberFormatException e) {
			}

			final Material m = Material.getMaterial(itemid);
			if (m == null) {
				event.sendMessage(ChatColor.RED + "Item ID not found!");
				return;
			}
			is = new ItemStack(m, amnt, (short) 0, (byte) damage);
		} else {
			int itemid = 0;
			try {
				itemid = Integer.parseInt(item);
			} catch (final NumberFormatException e) {
			}

			if (itemid == 0) {
				event.sendMessage(ChatColor.RED + "There was an error in parsing the item id.");
				return;
			}

			final Material m = Material.getMaterial(itemid);
			if (m == null) {
				event.sendMessage(ChatColor.RED + "Item ID not found!");
				return;
			}

			is = new ItemStack(m, amnt);
		}
		for (final Player p : UHCGame.getInstance().getOnlinePlayers()) {
			Utils.pickupItem(p, is);
			p.sendMessage(Lang.getInstance().getMessage(p, "GiveAll.Gived")
					.replaceAll("<item>", is.getType().name().toLowerCase())
					.replaceAll("<quantity>", "" + amnt));
			p.updateInventory();
		}
	}

	@Override
	public void execute(final Player player, final String[] args) {
		if (GameStatus.notStarted()) {
			returnTell(Lang.getInstance().getMessage("NotStarted"));
		}
		if (args.length >= 1) {
			String amnt;
			if (args.length == 1) {
				amnt = "1";
			} else {
				amnt = args[1];
			}
			giveall(player, args[0], amnt);
			return;
		}
		player.sendMessage(ChatColor.RED + "/giveall <item id>(:data) (amount)");
	}

}