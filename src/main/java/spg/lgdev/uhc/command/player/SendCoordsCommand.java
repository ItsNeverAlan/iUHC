package spg.lgdev.uhc.command.player;

import org.bukkit.entity.Player;

import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.command.abstracton.PlayerCommand;
import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.Library;
import spg.lgdev.uhc.manager.TeamManager;
import spg.lgdev.uhc.player.TeamProfile;

public class SendCoordsCommand extends PlayerCommand {

	public SendCoordsCommand() {
		super("sendcoords", "sc", "scs", "sendcoord");
	}

	@Override
	public void run(final Player p, final String[] args) {

		if (GameStatus.notStarted()) {

			returnTell(Lang.getMsg(p, "NotStarted"));

		}

		if (!TeamManager.getInstance().isTeamsEnabled()) {

			returnTell(Lang.getMsg(p, "Team.IsDisable"));

		}

		final TeamProfile team = Library.getTeam(p);

		if (!iUHC.getInstance().getProfileManager().getProfile(p.getUniqueId()).isPlayerAlive()) {

			returnTell(Lang.getMsg(p, "IsSpectator"));

		}

		if (team != null) {

			team.sendMessage(CachedConfig.TeamPrivateChat.replace("<player>", p.getName()) + "x: " + p.getLocation().getBlockX() + " y: " + p.getLocation().getBlockY() + " z: " + p.getLocation().getBlockZ());

		} else {

			p.sendMessage(Lang.getMsg(p, "Team.NotInTeam"));

		}
	}

}
