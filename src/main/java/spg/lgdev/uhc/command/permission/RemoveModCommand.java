package spg.lgdev.uhc.command.permission;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.command.abstracton.PermissionCommand;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.Permissions;
import spg.lgdev.uhc.handler.game.UHCGame;

public class RemoveModCommand extends PermissionCommand {

	public RemoveModCommand() {
		super("removemod", Permissions.ADMIN);
		setAliases("modremove");
	}

	@Override
	public void execute(final Player player, final String[] args) {
		final UHCGame game = UHCGame.getInstance();

		if (args.length == 0) {
			returnTell("§cUsage: /removemod <player>");
		}

		final Player target = Bukkit.getPlayer(args[0]);
		checkNull(target, () -> Lang.getMsg(player, "TargetNotOnline"));

		p.sendMessage("§atarget are now removed staff mode");
		target.sendMessage("§cYour staff mode has been disabled by " + p.getName());

		game.getMods().remove(target.getUniqueId());
		if (GameStatus.notStarted()) {
			iUHC.getInstance().getProfileManager().getProfile(target.getUniqueId()).setPlayerAlive(true);
			game.lobby(target, true);
			return;
		}

		iUHC.getInstance().getProfileManager().setRespawn(target, target);
	}

}