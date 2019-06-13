package spg.lgdev.uhc.announce;

import java.util.Iterator;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.enums.AnnounceType;
import spg.lgdev.uhc.gui.announcement.AnnounceGUI;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.manager.TeamManager;
import spg.lgdev.uhc.scenario.Scenarios;
import spg.lgdev.uhc.util.StringUtil;
import spg.lgdev.uhc.util.Utils;
import spg.lgdev.uhc.util.signgui.SignFinishCallback;
import spg.lgdev.uhc.util.signgui.SignGUI;
import net.development.mitw.uuid.UUIDCache;

public abstract class AbstractAnnounce {

	private static String startTimes = "00:00";
	private static String whitelistOffTimes = "00:00";

	public abstract boolean postAnnounce(String message);

	public abstract boolean isEnabled();

	public static String getFormattedAnnounceMessage(final AnnounceType type){
		Iterator<String> messages = null;
		switch (type) {
		case TWITTER:
			messages = CachedConfig.TWITTER_ANNOUNCE.iterator();
			break;
		case DISCORD:
			messages = CachedConfig.DISCORD_ANNOUNCE.iterator();
			break;
		}

		StringBuilder formatted = new StringBuilder();
		while (messages.hasNext()) {
			final String message = messages.next();
			if (message.equals("<scenarios>")) {
				final Iterator<String> scenarios = Scenarios.getScenariosList().iterator();
				while (scenarios.hasNext()) {
					formatted.append("- " + scenarios.next() + (scenarios.hasNext() ? "\n" : ""));
				}
				continue;
			}
			formatted.append(message + (messages.hasNext() ? "\n" : ""));
		}

		return placeholder(formatted.toString(), 0);
	}

	public static String getFormattedFFAWinMessage(final AnnounceType type){

		Iterator<String> messages = null;
		switch (type) {
		case TWITTER:
			messages = CachedConfig.TWITTER_FFA_WIN.iterator();
			break;
		case DISCORD:
			messages = CachedConfig.DISCORD_FFA_WIN.iterator();
			break;
		}

		String formatted = "";
		while (messages.hasNext()) {
			formatted = formatted + messages.next() + (messages.hasNext() ? "\n" : "");
		}

		return placeholder(formatted, 1);
	}

	public static String getFormattedTeamWinMessage(final AnnounceType type){

		Iterator<String> messages = null;
		switch (type) {
		case TWITTER:
			messages = CachedConfig.TWITTER_TEAM_WIN.iterator();
			break;
		case DISCORD:
			messages = CachedConfig.DISCORD_TEAM_WIN.iterator();
			break;
		}

		String formatted = "";
		while (messages.hasNext()) {
			formatted = formatted + messages.next() + (messages.hasNext() ? "\n" : "");
		}

		return placeholder(formatted, 2);
	}

	public static String placeholder(String string, final int type) {
		string = StringUtil.replace(string, "<mode>", UHCGame.getInstance().getGameType());
		string = StringUtil.replace(string, "<hoster>", UHCGame.getInstance().getHostName());
		string = StringUtil.replace(string, "<scenarios>", Scenarios.getScenariosList().stream().collect(Collectors.joining(" ")));
		string = StringUtil.replace(string, "<nether>", UHCGame.getInstance().isNether() ? "ON" : "OFF");
		string = StringUtil.replace(string, "<applerate>", UHCGame.getInstance().getAppleRate());
		string = StringUtil.replace(string, "<speed>", UHCGame.getInstance().getSpeed() == 0 ? "OFF" : "Lv" + UHCGame.getInstance().getSpeed());
		string = StringUtil.replace(string, "<strength>", UHCGame.getInstance().getStreght() == 0 ? "OFF" : "Lv" + UHCGame.getInstance().getStreght());
		string = StringUtil.replace(string, "<border>", UHCGame.getInstance().getBorderRadius());
		string = StringUtil.replace(string, "<firstborder>", Utils.formatTimeHours(UHCGame.getInstance().getFirstBorder()));
		string = StringUtil.replace(string, "<finalheal>", Utils.formatTimeHours(UHCGame.getInstance().getFinalHealTime()));
		string = StringUtil.replace(string, "<pvp>", Utils.formatTimeHours(UHCGame.getInstance().getPvpTime()));
		if (type == 0) {
			string = StringUtil.replace(string, "<whitelistOffTimes>", whitelistOffTimes);
			string = StringUtil.replace(string, "<startTimes>", startTimes);
		} else if (type == 1) {
			string = StringUtil.replace(string, "<winner>", UUIDCache.getName(UHCGame.getInstance().getWinners().get(0)));
			string = StringUtil.replace(string, "<ListKilled>", iUHC.getInstance().getProfileManager().getProfile(UHCGame.getInstance().getWinners().get(0)).getKilled().toString());
		} else {
			string = StringUtil.replace(string, "<winnerTeam>", TeamManager.getInstance().getLastTeam().getTeamName());
			string = StringUtil.replace(string, "<ListTeamMember>", TeamManager.getInstance().getLastTeam().getMembers());
		}
		return string;
	}

	public static void editStartTimes(final Player player) {
		SignGUI.openSignEditor(player, new String[] { "", "^^^", "^^^", "Start Time Edit"}, new SignFinishCallback() {
			@Override
			public void onFinish(final String[] lines) {
				startTimes = lines[0];
				player.sendMessage(ChatColor.GREEN + "Success edit start times!");
				new AnnounceGUI().open(player);
			}
		});
	}

	public static void editWhitelistOffTimes(final Player player) {
		SignGUI.openSignEditor(player, new String[] { "", "^^^", "^^^", "WL Off Edit"}, new SignFinishCallback() {
			@Override
			public void onFinish(final String[] lines) {
				whitelistOffTimes = lines[0];
				player.sendMessage(ChatColor.GREEN + "Success edit whitelist off times!");
				new AnnounceGUI().open(player);
			}
		});
	}

}
