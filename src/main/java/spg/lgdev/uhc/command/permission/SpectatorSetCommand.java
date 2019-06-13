package spg.lgdev.uhc.command.permission;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.command.abstracton.PermissionCommand;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.Permissions;

public class SpectatorSetCommand extends PermissionCommand {

	public SpectatorSetCommand() {
		super("setspectator", Permissions.ADMIN);
		setAliases("setspec");
	}

	@Override
	public void execute(final Player player, final String[] args) {
		if (args.length == 0) {
			returnTell("§6/setspectator <player>");
		}

		final Player target = Bukkit.getPlayer(args[0]);
		checkNull(target, () -> Lang.getMsg(player, "TargetNotOnline"));

		if (iUHC.getInstance().getProfileManager().getProfile(target.getUniqueId()).isSpectator()) {
			returnTell("§cPlayer " + args[0] + " already is a Spectator!");
		}

		iUHC.getInstance().getProfileManager().setSpectator(target, false);
		tell("§aSuccess set spectator");

	}
}