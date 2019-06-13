package spg.lgdev.uhc.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.Library;
import spg.lgdev.uhc.handler.Permissions;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.player.PlayerProfile;
import spg.lgdev.uhc.player.TeamProfile;
import spg.lgdev.uhc.scenario.Scenarios;
import spg.lgdev.uhc.util.FastUUID;
import spg.lgdev.uhc.util.json.FancyMessage;

public class TeamManager {

	private static TeamManager instance = new TeamManager();
	public List<TeamProfile> teams = new ArrayList<>();

	public String[] teamColors = new String[]{
			ChatColor.AQUA.toString(),
			ChatColor.GOLD.toString(),
			ChatColor.DARK_AQUA.toString(),
			ChatColor.GOLD.toString(),
			ChatColor.GREEN.toString(),
			ChatColor.DARK_GREEN.toString(),
			ChatColor.LIGHT_PURPLE.toString(),
			ChatColor.BLUE.toString()
	};

	private boolean teamsEnabled = false;
	private boolean damageTeamMembers = false;
	private int maxSize = 2;
	private int currentTeams = 0;
	private int teamsSize = 300;

	public static TeamManager getInstance() {
		return instance;
	}

	public boolean canDamageTeamMembers() {
		return this.damageTeamMembers;
	}

	public void setCanDamageTeamMembers(final boolean bl) {
		this.damageTeamMembers = bl;
	}

	public boolean isTeamsEnabled() {
		return this.teamsEnabled;
	}

	public void setTeamsEnabled(final boolean bl) {
		this.teamsEnabled = bl;
	}

	public List<TeamProfile> getTeams() {
		return teams;
	}

	public int getMaxSize() {
		return this.maxSize;
	}

	public void setMaxSize(final int n) {
		this.maxSize = n;
	}

	public int getTeamSize() {
		return this.teamsSize;
	}

	public void setTeamSize(final int n) {
		this.teamsSize = n;
		if (Scenarios.LoveAtFirstSight.isOn() && n != 2) {
			Scenarios.LoveAtFirstSight.setOn(false);
			Bukkit.broadcast("§cThe scenario §eLove at first sight§c has been disabled! This scenario can only be enabled when its to2!", Permissions.ADMIN);
		}
	}

	public void clearTeams() {
		getTeams().forEach(team -> team.getPlayers().forEach(this::unregisterTeam));
		getTeams().clear();
		this.currentTeams = 0;
	}

	public void unregisterTeam(final UUID uUID) {
		final PlayerProfile profile = iUHC.getInstance().getProfileManager().getProfile(uUID);
		if (profile == null)
			return;
		if (profile.getTeam() == null)
			return;
		profile.getTeam().removePlayer(Bukkit.getOfflinePlayer(uUID));
		profile.setTeam(null);
	}

	public void autoPlace() {
		//		if (Scenarios.LoveAtFirstSight.isOn())
		//			return;
		for (final Player player : UHCGame.getInstance().getOnlinePlayers()) {
			final TeamProfile team = iUHC.getInstance().getProfileManager().getProfile(player.getUniqueId()).getTeam();
			if (team != null) {
				continue;
			}
			createTeam(player);
		}
	}

	public void removeOfflineTeams() {
		for (final TeamProfile team : getTeams()) {
			Player p;
			for (final UUID uid : team.getPlayers()) {
				p = Bukkit.getPlayer(uid);
				if (p == null) {
					this.unregisterTeam(uid);
				}
			}
		}
	}

	public void createTeamBypass(final Player player) {
		++this.currentTeams;
		final TeamProfile team = iUHC.getInstance().getProfileManager().getProfile(player.getUniqueId()).getTeam();
		if (team != null) {
			team.removePlayer(player);
		}
		this.registerTeam(player, new TeamProfile(player, this.currentTeams));
	}

	public TeamProfile createTeam(final Player player) {
		if (this.currentTeams >= teamsSize) {
			player.sendMessage("§b§lTEAM: §fMax teams list has reach!§f!");
			return null;
		}
		++this.currentTeams;
		final TeamProfile team = new TeamProfile(player, currentTeams);
		teams.add(team);
		if (team != null) {
			team.removePlayer(player);
		}
		this.registerTeam(player, team);
		return team;
	}

	public void registerTeam(final Player player, final TeamProfile team) {
		this.registerTeam(player, team, iUHC.getInstance().getProfileManager().getProfile(player.getUniqueId()));
	}

	public void registerTeam(final Player player, final TeamProfile team, final PlayerProfile profile) {
		if (team.getSize() >= this.getMaxSize()) {
			player.sendMessage("§cTeam is full, max-players per team is §e" + this.maxSize);
			return;
		}
		team.addPlayer(player);
		profile.setTeam(team);
	}

	public void disbandTeam(final TeamProfile team) {
		for (final UUID uUID : new ArrayList<>(team.getPlayers())) {
			this.unregisterTeam(uUID);
		}
		teams.remove(team);
	}

	public int getTeamsAlive() {
		return (int) this.getTeams().stream().filter(TeamProfile::isAlive).count();
	}

	public TeamProfile getLastTeam() {
		return this.getTeams().stream().filter(TeamProfile::isAlive).findFirst().orElse(null);
	}

	public void disableAllUnusedTeams() {
		this.getTeams().stream().filter(teamProfile -> teamProfile.getSize() == 0).forEach(this::disbandTeam);
	}

	public String getRandomColor() {
		return teamColors[iUHC.getRandom().nextInt(teamColors.length)];
	}

	public void sendInviteRequest(final Player player, final Player player2, final TeamProfile team) {

		if (!team.getInvited().containsKey(player2.getUniqueId())) {

			if (Library.getPlayerData(player2).getTeam() == null) {

				team.getInvited().put(player2.getUniqueId(), System.currentTimeMillis());

				final String ownerName = team.getOwnerName();

				for (final String msg : Lang.getInstance().getMessageList(player2, "Team.OnInvite.InviteReceiveMessage")) {
					player2.sendMessage(msg.replaceAll("<teamName>", team.getTeamName()).replaceAll("<teamOwner>", ownerName).replaceAll("<teamMembers>", team.getMemberHealths()));
				}

				new FancyMessage(Lang.getMsg(player2, "Team.OnInvite.AcceptJson"))
				.tooltip(Lang.getMsg(player2, "Team.OnInvite.AcceptJson-ShowText"))
				.command("/team accept " + FastUUID.toString(team.getOwnerUUID())).send(player2);

				Player pl;
				for (final UUID uuid : team.getPlayers()) {
					pl = Bukkit.getPlayer(uuid);
					if (pl == null) {
						continue;
					}
					pl.sendMessage(Lang.getMsg(pl, "Team.OnInvite.SendInvite").replaceAll("<teamOwner>", ownerName).replaceAll("<player>", player2.getName()));
				}

			} else {
				player.sendMessage(Lang.getMsg(player, "Team.OnInvite.PlayerHaveTeam"));
			}
		} else {
			player.sendMessage(Lang.getMsg(player, "Team.OnInvite.PlayerInvited"));
		}
	}
}

