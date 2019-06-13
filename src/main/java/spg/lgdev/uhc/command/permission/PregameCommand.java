package spg.lgdev.uhc.command.permission;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.command.abstracton.PermissionCommand;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.gui.gameconfig.ArenaConfigGUI;
import spg.lgdev.uhc.gui.gameconfig.BorderGUI;
import spg.lgdev.uhc.handler.Permissions;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.util.UHCSound;
import spg.lgdev.uhc.util.Utils;
import net.md_5.bungee.api.ChatColor;

public class PregameCommand extends PermissionCommand {

	public PregameCommand() {
		super("pregame", Permissions.ADMIN);
	}

	@Override
	public void execute(final Player player, final String[] args) {
		if (!GameStatus.is(GameStatus.LOADING)) {
			returnTell(ChatColor.RED + "The server are not in loading!");
		}
		if (args.length == 0) {
			if (!iUHC.getInstance().getChunkManager().isLoading()) {
				ArenaConfigGUI.getInstance().open(p);
			}
			return;
		}
		if (iUHC.getInstance().getChunkManager().isLoading())
			return;
		if (args[0].equalsIgnoreCase("1")) {
			if (Utils.fileExists("UHCArena")) {
				returnTell(ChatColor.RED + "the arena world is already exists!");
			}
			tell(ChatColor.GREEN + "Arena worlds are creating now!...");
			UHCGame.getInstance().setWorldCreating(true);
			iUHC.getInstance().getWorldCreator().prepareToWorlds();
			return;
		}
		if (args[0].equalsIgnoreCase("2")) {
			if (!Utils.fileExists("UHCArena")) {
				returnTell(ChatColor.RED + "the arena world is not exists!");
			}
			if (UHCGame.getInstance().isWorldCreating()) {
				returnTell(ChatColor.RED + "Arena World are creating! please wait 10 second or more!");
			}
			tell(ChatColor.GREEN + "you teleported to arena worlds!");
			final World w = Bukkit.getWorld("UHCArena");
			final Location loc = new Location(w, 0.0, w.getHighestBlockYAt(0, 0) + 15, 0.0);
			p.teleport(loc);
		}
		if (args[0].equalsIgnoreCase("3")) {
			if (!Utils.fileExists("UHCArena")) {
				returnTell(ChatColor.RED + "the arena world is not exists!");
			}
			if (UHCGame.getInstance().isWorldCreating()) {
				returnTell(ChatColor.RED + "Arena World are creating! please wait 10 second or more!");
			}
			Location loc = new Location(Bukkit.getWorlds().get(0), 0, 100, 0, 0, 0);
			if (UHCGame.getInstance().getSpawnPoint() != null) {
				loc = UHCGame.getInstance().getSpawnPoint().toBukkitLocation();
			}
			for (final Player p1 : Bukkit.getWorld("UHCArena").getPlayers()) {
				p1.teleport(loc);
			}
			tell(ChatColor.GREEN + "you started to deleted Arena worlds!");
			if (Utils.fileExists("UHCArena")) {
				final World w1 = Bukkit.getWorld("UHCArena");
				if (iUHC.getInstance().multiverse) {
					Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
							"multiverse-core:mvdelete UHCArena");
					Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
							"multiverse-core:mv confirm");
				}
				iUHC.getInstance().log(true, "&eWorld UHCArena has been deleted");

				iUHC.getInstance().getWorldCreator().unloadWorld(w1);
				iUHC.getInstance().getWorldCreator().deleteWorld("UHCArena");

			}
			tell(ChatColor.GOLD + "arena world is deleted!");
		}
		if (args[0].equalsIgnoreCase("4")) {
			tell(ChatColor.GREEN + "You opened Border Settings!");
			new BorderGUI(p).open(p);
		}
		if (args[0].equalsIgnoreCase("5")) {
			if (!Utils.fileExists("UHCArena")) {
				returnTell(ChatColor.RED + "the arena world is not exists!");
			}
			if (UHCGame.getInstance().isWorldCreating()) {
				returnTell(ChatColor.RED + "Arena World are creating! please wait 10 second or more!");
			}
			Location loc = new Location(Bukkit.getWorlds().get(0), 0, 100, 0, 0, 0);
			if (UHCGame.getInstance().getSpawnPoint() != null) {
				loc = UHCGame.getInstance().getSpawnPoint().toBukkitLocation();
			}
			for (final Player p1 : Bukkit.getWorld("UHCArena").getPlayers()) {
				p1.teleport(loc);
			}
			iUHC.getInstance().getChunkManager().load(iUHC.getInstance(), "UHCArena");
			iUHC.getInstance().getChunkManager().moveToNext(iUHC.getInstance());
		}
		UHCSound.NOTE_PLING.playSound(player);
	}

}
