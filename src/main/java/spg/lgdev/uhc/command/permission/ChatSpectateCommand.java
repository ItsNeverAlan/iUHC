package spg.lgdev.uhc.command.permission;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.command.abstracton.PermissionCommand;
import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.handler.Permissions;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.util.StringUtil;

public class ChatSpectateCommand extends PermissionCommand {

	public ChatSpectateCommand() {
		super("chatspectators", Permissions.SPECTATORCHAT);
		setAliases("chatspec", "specchat");
	}

	@Override
	public void execute(final Player player, final String[] args) {

		if (args.length == 0) {
			returnTell("§c/chatspectators <messages>");
		}

		final String prefix = "";

		final StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < args.length; ++i) {
			final String string2 = args[i];
			if (i != 0) {
				stringBuilder.append(" ");
			}
			stringBuilder.append(string2);
		}

		final String message = stringBuilder.toString();
		final String formatMessage = StringUtil.replace(CachedConfig.ChatSpectator,
				new StringUtil.ReplaceValue("<prefix>", prefix),
				new StringUtil.ReplaceValue("<player>", player.getDisplayName())) + message;

		Bukkit.getOnlinePlayers().stream()
		.filter(player1 -> iUHC.getInstance().getProfileManager().isAlive(player1)
				|| UHCGame.getInstance().isHost(player1.getUniqueId()))
		.forEach(player1 -> player.sendMessage(formatMessage));
	}

}
