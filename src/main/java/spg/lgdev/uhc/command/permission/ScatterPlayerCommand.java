package spg.lgdev.uhc.command.permission;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.command.abstracton.PermissionCommand;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.Permissions;
import spg.lgdev.uhc.handler.game.UHCGame;

public class ScatterPlayerCommand extends PermissionCommand {

	public ScatterPlayerCommand() {
		super("scatter", Permissions.ADMIN);
	}

	@Override
	public void execute(final Player player, final String[] args) {

		if (GameStatus.notStarted()) {

			returnTell(Lang.getMsg(player, "NotStarted"));

		}

		checkArgsLengh(1, "§cUsage: /scatter <worldName>/all");

		if (args.length > 0) {
			final String w = args[0];

			if (w.equalsIgnoreCase("All")) {
				UHCGame.getInstance().ScatterAllNotInArena();
				return;
			}
			if (w.equalsIgnoreCase("UHCArena") || w.equalsIgnoreCase("UHCArena_nether")) {
				returnTell("§cYou can't scatter players inside arena!");
			}

			Bukkit.getWorld(w).getPlayers().forEach(player2 -> UHCGame.getInstance().ScatterInGame(player2, true));
			tell("§aSuccess scatter");
		}
	}

}
