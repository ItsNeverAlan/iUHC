package spg.lgdev.uhc.command.permission;

import org.bukkit.entity.Player;

import spg.lgdev.uhc.command.abstracton.PermissionCommand;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.Permissions;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.manager.ArenaManager;

public class RescatterCommand extends PermissionCommand {

	public RescatterCommand() {
		super("rescatter", Permissions.ADMIN);
	}

	@Override
	public void execute(final Player p, final String[] args) {
		if (GameStatus.notStarted()) {
			p.sendMessage(Lang.getMsg(p, "NotStarted"));
		}

		if (!UHCGame.getInstance().isAllowRescatter()) {
			p.sendMessage("§cYou are not able to use this command at this time!");
		}

		ArenaManager.getInstance().scatter(p);
	}

}
