package spg.lgdev.uhc.command.permission;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.command.abstracton.PermissionCommand;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.Permissions;
import spg.lgdev.uhc.handler.game.UHCGame;

public class KickSpectatorsCommand extends PermissionCommand {

	public KickSpectatorsCommand() {
		super("kickSpectators", Permissions.ADMIN);
		setAliases("speckick");
	}

	@Override
	public void execute(final Player p, final String[] args) {

		if (iUHC.getInstance().getSpectators() == 0) {
			returnTell("§ctheres no any specatotrs!");
		}

		new ArrayList<>(UHCGame.getInstance().getOnlineSpectators())
		.forEach(spec -> spec.kickPlayer(Lang.getMsg(spec, "DeathKick.KickMessage").replace("<player>", spec.getName())));

		tell("§aYou kicked all spectators!");
	}
}