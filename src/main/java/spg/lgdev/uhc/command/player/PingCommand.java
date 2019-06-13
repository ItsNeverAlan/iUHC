package spg.lgdev.uhc.command.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.command.abstracton.PlayerCommand;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.nms.NMSHandler;

public class PingCommand extends PlayerCommand {

	public PingCommand() {
		super("ping");
	}

	private int getPing(final Player p) {
		return NMSHandler.getInstance().getNMSControl().getPing(p);
	}

	@Override
	public void run(final Player p, final String[] args) {
		if (args.length == 0) {
			final int ping = getPing(p);
			returnTell(Lang.getMsg(p, "PingCMD").replaceAll("<ping>", ping + ""));
		}

		if (args.length == 1) {
			final Player target = Bukkit.getPlayer(args[0]);
			if (target == null) {
				returnTell(Lang.getMsg(p, "PingCMD-CantFindTarget"));
			}
			final int ping = getPing(target);
			returnTell(Lang.getMsg(p, "PingCMD-Other").replaceAll("<player>", target.getName()).replaceAll("<ping>", ping + ""));
		}

		tell(Lang.getMsg(p, "PingCMD-Usage"));
	}

}
