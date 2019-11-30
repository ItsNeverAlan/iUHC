package spg.lgdev.uhc.command.player;

import org.bukkit.entity.Player;

import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.command.abstracton.PlayerCommand;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.Library;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.manager.TeamManager;
import spg.lgdev.uhc.nms.NMSHandler;
import spg.lgdev.uhc.player.TeamProfile;
import spg.lgdev.uhc.util.StringUtil;

public class BackpackCommand extends PlayerCommand {

	public BackpackCommand() {
		super("backpack");
		setAliases("bp");
	}

	@Override
	public void run(final Player player, final String[] args) {
		if (!TeamManager.getInstance().isTeamsEnabled()) {
			returnTell(Lang.getInstance().getMessage(player, "Team.IsDisable"));
		}

		if (!UHCGame.getInstance().isBackpack()) {
			returnTell(Lang.getInstance().getMessage(player, "BackPack.IsDisable"));
		}

		if (GameStatus.notStarted()) {
			returnTell(StringUtil.cc(Lang.getInstance().getMessage(player, "NotStarted")));
		}

		if (!iUHC.getInstance().getProfileManager().getProfile(player.getUniqueId()).isPlayerAlive()) {
			returnTell(Lang.getMsg(player, "IsSpectator"));
		}

		final TeamProfile team = Library.getTeam(player);

		if (team != null) {

			player.openInventory(team.getBackPack());

		}
	}

}

