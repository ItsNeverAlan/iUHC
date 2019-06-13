package spg.lgdev.uhc.command.permission;

import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.command.abstracton.PermissionCommand;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.Permissions;
import spg.lgdev.uhc.handler.game.UHCGame;
import net.md_5.bungee.api.ChatColor;

public class HealCommand extends PermissionCommand {

	public HealCommand() {
		super("heal", Permissions.ADMIN);
	}

	@Override
	public void execute(final Player player, final String[] args) {

		checkArgsLengh(1, ChatColor.RED + "/heal <playerName/all>");

		if (args[0].equalsIgnoreCase("all")) {
			UHCGame.getInstance().getOnlinePlayers().forEach(p -> {
				p.setHealth(p.getMaxHealth());
				final Player p1 = p;
				Stream.of(Lang.getInstance().getMessageList(p1, "HealCMD.HealAll")).forEach(p1::sendMessage);
			});
			returnTell(ChatColor.GREEN + "heal success");
		}

		final Player p = Bukkit.getPlayer(args[0]);

		checkNull(p, () -> Lang.getInstance().getMessage(p, "TargetNotOnline"));

		p.setHealth(p.getMaxHealth());
		Stream.of(Lang.getInstance().getMessageList(p, "HealCMD.HealTarget")).forEach(p::sendMessage);
		tell(ChatColor.GREEN + "heal success");

	}

}
