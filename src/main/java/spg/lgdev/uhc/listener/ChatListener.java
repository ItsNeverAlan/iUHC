package spg.lgdev.uhc.listener;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.Permissions;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.manager.TeamManager;
import spg.lgdev.uhc.player.PlayerProfile;
import spg.lgdev.uhc.player.TeamProfile;
import spg.lgdev.uhc.util.StringUtil;
import spg.lgdev.uhc.util.VaultUtil;

public class ChatListener implements org.bukkit.event.Listener {

	private final iUHC plugin;

	public ChatListener(final iUHC plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onAsyncPlayerChat(final AsyncPlayerChatEvent e) {
		final Player p = e.getPlayer();
		final String message = p.hasPermission(Permissions.COLOR) ? StringUtil.cc(e.getMessage()) : e.getMessage();

		final PlayerProfile profile = plugin.getProfileManager().getProfile(p.getUniqueId());

		if (profile.isActionCountdown()) {
			p.sendMessage(Lang.getMsg(p, "Cooldown"));
			e.setCancelled(true);
			return;
		}

		profile.startActionCountdown();
		final String prefix = plugin.vault ? StringUtil.cc(VaultUtil.getPrefix(p)) : "";

		if (profile.isTeamChat()) {

			e.setCancelled(true);

			final TeamProfile team = profile.getTeam();

			if (team == null) {

				profile.setTeamChat(false);
				p.sendMessage(Lang.getMsg(p, "Team.TeamChat.AutoDisabled"));
				return;

			}

			team.sendMessage(StringUtil.replace(CachedConfig.TeamPrivateChat, "<player>", p.getName()) + message);
			return;

		}

		if (!UHCGame.getInstance().isOpenChat() && !p.hasPermission(Permissions.MUTECHAT_BYPASS)) {

			p.sendMessage(Lang.getMsg(p, "Chat.is-Disable"));
			e.setCancelled(true);
			return;

		}

		if (UHCGame.getInstance().isHost(p.getUniqueId())) {

			e.setFormat(CachedConfig.ChatHost
					.replaceAll("<prefix>", prefix)
					.replaceAll("<player>", p.getDisplayName()) + message);

			return;
		}

		if (UHCGame.getInstance().isMod(p.getUniqueId())) {

			e.setFormat(CachedConfig.ChatMod.replaceAll("<prefix>", prefix).replaceAll("<player>", p.getDisplayName()) + message);
			return;

		}

		if (profile.isSpectator()) {

			final String formatMessage = StringUtil.replace(CachedConfig.ChatSpectator, new StringUtil.ReplaceValue("<prefix>", prefix), new StringUtil.ReplaceValue("<player>", p.getDisplayName())) + message;

			Bukkit.getOnlinePlayers().stream()
			.filter(player -> !plugin.getProfileManager().isAlive(player) || UHCGame.getInstance().isHost(player.getUniqueId()))
			.forEach(player -> player.sendMessage(formatMessage));

			e.setCancelled(true);
			return;

		}

		if (profile.isPlayerAlive()) {

			if (!TeamManager.getInstance().isTeamsEnabled()) {

				e.setFormat(CachedConfig.ChatFFA.replaceAll("<prefix>", prefix).replaceAll("<player>", p.getDisplayName()) + message);
				return;

			} else {

				final TeamProfile team = profile.getTeam();
				final String name = team != null ? team.getTeamName() : "";

				e.setFormat(CachedConfig.ChatTeam
						.replaceAll("<prefix>", prefix)
						.replaceAll("<player>", p.getDisplayName())
						.replaceAll("<teamName>", name) + message);
				return;
			}

		}

	}

}

