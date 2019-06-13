package spg.lgdev.uhc.command.permission;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.command.abstracton.PermissionCommand;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.handler.Permissions;
import spg.lgdev.uhc.handler.game.UHCGame;

public class WhitelistCommand extends PermissionCommand {

	public WhitelistCommand() {
		super("whitelist", Permissions.ADMIN, "wl");
	}

	@Override
	public void execute(final Player player, final String[] args) {
		if (args.length == 0) {
			returnTell(ChatColor.AQUA + "/whitelist <on/off/add/remove/all/clear> <player>");
		}

		if (args.length > 1) {
			if (args[0].equalsIgnoreCase("add")) {
				tell("§bYou added player §a" + args[1] + " §bin the whitelist!");
				UHCGame.getInstance().getPlayerWhitelists().add(args[1]);
				return;
			}
			if (args[0].equalsIgnoreCase("remove")) {
				tell("§cYou removed player §a" + args[1] + " §bfrom the whitelist!");
				UHCGame.getInstance().getPlayerWhitelists().remove(args[1]);
				final OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);
				if (p != null) {
					UHCGame.getInstance().getWhitelist().remove(p.getUniqueId());
				}
				return;
			}
		}

		if (args[0].equalsIgnoreCase("on")) {
			tell(ChatColor.GREEN + "Whitelist: On");
			UHCGame.getInstance().setWhitelisted(true);
			return;
		}

		if (args[0].equalsIgnoreCase("off")) {
			if (GameStatus.is(GameStatus.LOADING)) {
				tell("§cYou can not turn off the whitelist while the world is loading.");
				returnTell("§cChunks: §e" + Bukkit.getWorld("UHCArena").getLoadedChunks());
			}
			tell(ChatColor.RED + "Whitelist: Off");
			UHCGame.getInstance().setWhitelisted(false);
			return;
		}

		if (args[0].equalsIgnoreCase("all")) {
			for (final UUID u : UHCGame.getInstance().getPlayersUUID()) {
				UHCGame.getInstance().getWhitelist().add(u);
			}
			returnTell("§aSuccess set all players to whitelist");
		}

		if (args[0].equalsIgnoreCase("clear")) {
			UHCGame.getInstance().getWhitelist().clear();
			returnTell(ChatColor.GREEN + "Whitelist cleared successfully!");
		}
	}

}

