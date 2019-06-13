package spg.lgdev.uhc.command.player;

import java.util.UUID;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.command.abstracton.PlayerCommand;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.player.PlayerProfile;
import net.development.mitw.uuid.UUIDCache;

public class KillCountCommand extends PlayerCommand {

	public KillCountCommand() {
		super("killcount", "kc");
	}

	@Override
	public void run(final Player p, final String[] args) {
		if (GameStatus.notStarted()) {
			returnTell(Lang.getMsg(p, "NotStarted"));
		}

		if (args.length == 0) {
			returnTell(ChatColor.RED + "/killcount <player>");
		}

		if (args.length > 0) {

			final Player target = Bukkit.getPlayer(args[0]);
			checkNull(target, () -> Lang.getMsg(p, "TargetNotOnline"));

			final PlayerProfile st = iUHC.getInstance().getProfileManager().getProfile(target.getUniqueId());

			if (st.getKills() == 0) {
				returnTell(Lang.getMsg(p, "KillCount.NoKills"));
			}

			String names = "";
			for (final UUID u : st.getKilled()) {
				names = names + "§c" + UUIDCache.getName(u) + ", ";
			}
			names = names.substring(0, names.lastIndexOf(", "));
			final String teamString = names.replaceAll(", ", new StringBuilder().append("§f").append(", ").toString());

			for (String msg : Lang.getInstance().getMessageList(p, "KillCount.CMDMessage")) {
				msg = msg.replaceAll("<player>", target.getName())
						.replaceAll("<killCount>", "" + st.getKills()).replaceAll("<listKilled>", teamString);
				p.sendMessage(msg);
			}

		}
	}
}
