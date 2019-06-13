package spg.lgdev.uhc.command.vip;

import spg.lgdev.uhc.iUHC;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.command.abstracton.PermissionCommand;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.Permissions;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.player.PlayerProfile;

public class LateScatterCommand extends PermissionCommand {

	public LateScatterCommand() {
		super("latescatter", Permissions.LATE_SCATTER);
	}

	@Override
	public void execute(final Player player, final String[] args) {

		if (!GameStatus.started()) {
			returnTell(Lang.getMsg(player, "NotStarted"));
		}

		if (iUHC.getInstance().getProfileManager().isAlive(player)) {
			returnTell(Lang.getMsg(player, "stillAlive"));
		}

		final PlayerProfile profile = iUHC.getInstance().getProfileManager().getProfile(player.getUniqueId());

		if (!profile.isLateScatter()) {
			returnTell("§cYou can't late scatter now!");
		}

		if (UHCGame.getInstance().gameCountdowns > 300) {
			returnTell("§cToo late! You can't late scatter after 5 minutes!");
		}

		iUHC.getInstance().getProfileManager().lateScatter(player);
		profile.setLateScatter(false);

	}

}
