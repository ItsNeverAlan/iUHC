package spg.lgdev.uhc.command.permission;

import java.util.stream.Stream;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.command.abstracton.PermissionCommand;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.Permissions;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.util.StringUtil;

public class HostSetCommand extends PermissionCommand {

	public HostSetCommand() {
		super("sethost", Permissions.ADMIN);
	}

	@Override
	public void execute(final Player player, final String[] args) {

		checkArgsLengh(1, "§6/sethost <player>");

		final Player target = Bukkit.getPlayer(args[0]);

		checkNull(target, () -> Lang.getMsg(player, "TargetNotOnline"));

		if (UHCGame.getInstance().isHost(target.getUniqueId())) {
			returnTell("§cTarget " + args[0] + " already is a host");
		}

		tell("§aset target to host!");
		iUHC.getInstance().getProfileManager().setHost(target);
		Stream.of(Lang.getInstance().getMessageList(player, "Staff.SetHost"))
		.map(StringUtil::cc)
		.forEach(target::sendMessage);
	}
}