package spg.lgdev.uhc.command.permission;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.command.abstracton.PermissionCommand;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.Permissions;
import spg.lgdev.uhc.listener.FreezeListener;

public class FreezeCommand extends PermissionCommand {

	public FreezeCommand() {
		super("freeze", Permissions.FREEZE);
		setAliases("ss", "frozen");
	}

	@Override
	public void execute(final Player p, final String[] args) {

		checkArgsLengh(1, "§c/freeze <Player>");

		final Player target = Bukkit.getPlayer(args[0]);

		checkNull(p, () -> Lang.getMsg(p, "TargetNotOnline"));

		if (target == p) {
			returnTell("§cYou can't freezer yourself!");
		}

		if (target.hasPermission(Permissions.FREEZE_BYPASS)) {
			if (!p.hasPermission(Permissions.ADMIN)) {
				returnTell("§cYou can't freezer this player!");
			}
		}

		FreezeListener.freezerPlayer(target, p);
	}

}
