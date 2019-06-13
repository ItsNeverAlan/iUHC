package spg.lgdev.uhc.command.player;

import java.util.UUID;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.command.abstracton.PlayerCommand;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.Library;
import spg.lgdev.uhc.handler.Permissions;
import spg.lgdev.uhc.manager.TeamManager;
import spg.lgdev.uhc.player.PlayerProfile;
import spg.lgdev.uhc.player.TeamProfile;
import spg.lgdev.uhc.scenario.Scenarios;
import spg.lgdev.uhc.util.FastUUID;
import spg.lgdev.uhc.util.StringUtil;

public class TeamCommands extends PlayerCommand {

	public TeamCommands() {
		super("team", "party");
	}

	@Override
	public void run(final Player player, final String[] args) {

		if (args.length == 0) {

			Stream.of(Lang.getInstance().getMessageList(player, "Team.TeamHelp")).forEach(player::sendMessage);
			return;

		}

		if (args[0].equalsIgnoreCase("list")) {

			Player target = player;
			if (args.length == 2) {
				target = Bukkit.getPlayer(args[1]);
			}

			if (target != null) {
				final TeamProfile team = Library.getTeam(target);
				if (team != null) {
					for (final String s : Lang.getInstance().getMessageList(player, "Team.TeamList")) {
						player.sendMessage(StringUtil.cc(
								s.replaceAll("<leader>", team.getOwnerName())
								.replaceAll("<teamName>", team.getTeamName())
								.replaceAll("<member>", team.getMemberHealths())));
					}
					return;
				}
				if (target == player) {
					returnTell(Lang.getMsg(player, "Team.NotInTeam"));
				}
				returnTell(Lang.getMsg(player, "Team.TargetNotInTeam").replaceAll("<target>", target.getName()));
			}
			returnTell(Lang.getMsg(player, "TargetNotOnline"));
		}

		if (!GameStatus.is(GameStatus.WAITING)) {
			returnTell(Lang.getMsg(player, "Started"));
		}

		if (!TeamManager.getInstance().isTeamsEnabled()) {

			returnTell(Lang.getMsg(player, "Team.IsDisable"));

		}

		if (args[0].equalsIgnoreCase("name")) {

			if (!player.hasPermission(Permissions.CHANGE_TEAM_NAME)) {
				returnTell(Lang.getMsg(player, "noPermission"));
			}

			if (args.length == 1) {
				returnTell("§c/team name <teamName>");
			}

			final String nameTest = args[1];

			if (nameTest.length() < 6) {
				returnTell("§cThe name from you team must be more 6 characters");
			}

			String name = "";

			if (player.hasPermission(Permissions.TEAMNAME_COLOR_ABLE)) {
				name = StringUtil.cc(nameTest);
			}

			TeamProfile team = Library.getTeam(player);

			if (team == null) {
				team = TeamManager.getInstance().createTeam(player);
				team.setTeamName(name);
				return;
			}

			if (team.getOwner() != player) {
				returnTell("§cYou must be the team leader to invite players to the team!!");
			}

			if (name.length() > 10) {
				name = name.substring(0, 10);
				player.sendMessage("§cThe name from you team is more highest 12 characters");
				player.sendMessage("§cHas been modifier");
			}

			team.setTeamName(name);

			return;
		}

		if (Scenarios.LoveAtFirstSight.isOn()) {
			returnTell(Lang.getMsg(player, "LoveAtFirstSight.CantDoThis"));
		}

		if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("solo")) {

			if (Library.getTeam(player) != null) {
				returnTell(Lang.getMsg(player, "Team.AlreadyInTeam"));
			}
			TeamManager.getInstance().createTeam(player);

			if (player.hasPermission(Permissions.CHANGE_TEAM_NAME)) {
				returnTell("§cTo change your team name, use /team name <teamName> !");
			}

			return;
		}

		if (args[0].equalsIgnoreCase("accept")) {

			if (args.length > 1) {

				try {

					final UUID uuid = FastUUID.parseUUID(args[1]);

					final PlayerProfile profile = iUHC.getInstance().getProfileManager().getProfile(uuid);

					if (profile == null) {
						returnTell("§cPlease rejoin and try again!");
					}

					final TeamProfile team = profile.getTeam();

					if (team == null) {
						returnTell(Lang.getMsg(player, "TargetNotInTeam"));
					}

					if (!team.getInvited().containsKey(player.getUniqueId())) {
						returnTell("§cPlease rejoin and try again!");
					}

					TeamManager.getInstance().registerTeam(player, team);
					returnTell("");

				} catch (final IllegalArgumentException | NullPointerException e) {


				}

				returnTell(ChatColor.RED + "/team accept <UUID>");

			}

		}

		if (args[0].equalsIgnoreCase("invite")) {

			if (args.length == 1) {
				returnTell("§c/team invite <player>");
			}

			final Player target = Bukkit.getServer().getPlayer(args[1]);

			if (target == null) {
				returnTell(Lang.getMsg(player, "TargetNotOnline"));
			}

			if (target == player) {
				returnTell(Lang.getMsg(player, "Team.OnInvite.CantInviteSelf"));
			}

			TeamProfile team = Library.getTeam(player);

			if (team == null) {
				team = TeamManager.getInstance().createTeam(player);
				TeamManager.getInstance().sendInviteRequest(player, target, team);
				return;
			}

			if (team.getOwner() != player) {
				returnTell(Lang.getMsg(player, "Team.OnlyTeamLeader"));
			}

			TeamManager.getInstance().sendInviteRequest(player, target, team);

			return;
		}

		if (args[0].equalsIgnoreCase("kick")) {
			if (args.length == 1) {
				returnTell("§c/team kick <player>");
			}
			final TeamProfile team = Library.getTeam(player);
			if (team == null) {
				returnTell(Lang.getMsg(p, "Team.NotInTeam"));
			}
			if (team.getOwner() != player) {
				returnTell(Lang.getMsg(p, "Team.OnlyTeamLeader"));
			}
			final OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[1]);
			if (offlinePlayer == null || !offlinePlayer.isOnline()) {
				returnTell(Lang.getMsg(p, "TargetNotOnline"));
			}
			if (offlinePlayer == player) {
				returnTell("§cYou cannot kick yourself out of the team, use /team leave to leave the team!");
			}
			if (!team.getPlayers().contains(offlinePlayer.getUniqueId())) {
				returnTell("§cThis player is not part of your team!");
			}
			TeamManager.getInstance().unregisterTeam(offlinePlayer.getUniqueId());
			if (offlinePlayer.isOnline()) {
				offlinePlayer.getPlayer().sendMessage("§cHas been kicked from the team by " + player.getName());
			}
			returnTell("§c" + offlinePlayer.getName() + " has been kicked from the team.");
		}

		if (args[0].equalsIgnoreCase("disband")) {
			final TeamProfile team = Library.getTeam(player);
			if (team == null) {
				returnTell(Lang.getMsg(p, "Team.NotInTeam"));
			}
			if (team.getOwnerUUID().equals(player.getUniqueId())) {
				TeamManager.getInstance().disbandTeam(team);
				return;
			}
			player.sendMessage(Lang.getMsg(player, "Team.OnlyTeamLeader"));
		}

		if (args[0].equalsIgnoreCase("leave")) {
			final TeamProfile team = Library.getTeam(player);
			if (team == null) {
				returnTell(Lang.getMsg(player, "Team.NotInTeam"));
			}
			if (team.getOwnerUUID().equals(player.getUniqueId())) {
				TeamManager.getInstance().disbandTeam(team);
				return;
			}
			TeamManager.getInstance().unregisterTeam(player.getUniqueId());
			return;
		}

		final Player p = Bukkit.getPlayer(args[0]);

		checkNull(p, () -> Lang.getMsg(player, "TargetNotOnline"));
		if (p == player) {
			returnTell("§cYou can't invite yourself to team!!");
		}
		TeamProfile team = Library.getTeam(player);
		if (team == null) {
			team = TeamManager.getInstance().createTeam(player);
			TeamManager.getInstance().sendInviteRequest(player, p, team);
			return;
		}
		if (team.getOwner() != player) {
			returnTell("§cYou must be the team leader to invite players to the team!!");
		}
		TeamManager.getInstance().sendInviteRequest(player, p, team);

	}

}
