package spg.lgdev.uhc.command.permission;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.command.abstracton.PermissionCommand;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.Permissions;
import spg.lgdev.uhc.handler.game.UHCGame;
import net.development.mitw.uuid.UUIDCache;

public class RemoveHostCommand extends PermissionCommand {

	public RemoveHostCommand() {
		super("removehost", Permissions.ADMIN);
		setAliases("hostremove");
	}

	@Override
	public void execute(final Player player, final String[] args) {
		if (args.length == 0) {
			returnTell("§cUsage: /removehost <player>");
		}
		final Player target = Bukkit.getPlayer(args[0]);
		checkNull(target, () -> Lang.getMsg(player, "TargetNotOnline"));

		tell("§aremoved " + target.getName() + "'s host");
		target.sendMessage("§cyour host has been removed by " + p.getName());

		final UHCGame game = UHCGame.getInstance();

		game.getHosts().remove(target.getUniqueId());
		if (game.getHosts().size() <= 0) {
			game.setHostName("");
		} else {
			game.setHostName(UUIDCache.getName(UHCGame.getInstance().getHosts().get(0)));
		}

	}

}