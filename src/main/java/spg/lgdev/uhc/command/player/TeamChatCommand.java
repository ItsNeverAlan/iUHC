package spg.lgdev.uhc.command.player;

import org.bukkit.entity.Player;

import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.command.abstracton.PlayerCommand;
import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.Library;
import spg.lgdev.uhc.manager.TeamManager;
import spg.lgdev.uhc.player.PlayerProfile;
import spg.lgdev.uhc.player.TeamProfile;

public class TeamChatCommand extends PlayerCommand {

	public TeamChatCommand() {
		super("teamchat", "tc");
	}

	@Override
	public void run(final Player p, final String[] args) {
		final PlayerProfile uHCPlayer = iUHC.getInstance().getProfileManager().getDebuggedProfile(p);

		if (!TeamManager.getInstance().isTeamsEnabled()) {
			p.sendMessage(Lang.getMsg(p, "Team.IsDisable"));
			return;
		}

		if (args.length > 0) {

			if (p instanceof Player) {

				final TeamProfile team = Library.getTeam(p);

				if (team != null) {

					final StringBuilder stringBuilder = new StringBuilder();

					for (int i = 0; i < args.length; ++i) {

						final String string2 = args[i];

						if (i != 0) {

							stringBuilder.append(" ");

						}

						stringBuilder.append(string2);
					}

					team.sendMessage(CachedConfig.TeamPrivateChat
							.replaceAll("<player>", p.getName()) + stringBuilder.toString().replaceAll("%", "%%"));

				} else {

					p.sendMessage(Lang.getMsg(p, "Team.NotInTeam"));

				}

			}

		} else {
			if (!TeamManager.getInstance().isTeamsEnabled()) {
				returnTell(Lang.getMsg(p, "Team.IsDisable"));
			}
			if (Library.getTeam(p) != null) {
				if (uHCPlayer.isTeamChat()) {
					p.sendMessage(Lang.getMsg(p, "Team.TeamChat.Disabled"));
					uHCPlayer.setTeamChat(false);
				} else {
					p.sendMessage(Lang.getMsg(p, "Team.TeamChat.Enabled"));
					uHCPlayer.setTeamChat(true);
				}
			} else {
				p.sendMessage(Lang.getMsg(p, "Team.NotInTeam"));
			}

		}
	}

}
