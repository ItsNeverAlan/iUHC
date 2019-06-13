package spg.lgdev.uhc.command.permission;

import org.bukkit.command.CommandSender;

import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.command.abstracton.SenderCommand;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.Permissions;
import spg.lgdev.uhc.handler.game.UHCGame;

public class BroadRulesCommand extends SenderCommand {

	public BroadRulesCommand() {
		super("broadrules");
	}

	@Override
	public void run(final CommandSender sender, final String[] args) {

		if (!sender.hasPermission(Permissions.ADMIN)) {
			returnTell(Lang.getMsg(sender, "noPermission"));
		}

		if (UHCGame.getInstance().isBroadcastingRules())
			return;

		iUHC.getInstance().getProfileManager().broadRules();

	}

}
