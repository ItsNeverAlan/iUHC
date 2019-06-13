package spg.lgdev.uhc.command.permission;

import org.bukkit.entity.Player;

import spg.lgdev.uhc.command.abstracton.PermissionCommand;
import spg.lgdev.uhc.handler.Permissions;
import spg.lgdev.uhc.handler.game.UHCGame;

public class MuteChatCommand extends PermissionCommand {

	public MuteChatCommand() {
		super("mutechat", Permissions.MUTECHAT);
	}

	@Override
	public void execute(final Player player, final String[] args) {

		if (UHCGame.getInstance().isOpenChat()) {

			UHCGame.getInstance().setOpenChat(false);

		} else {

			UHCGame.getInstance().setOpenChat(true);

		}
	}
}

