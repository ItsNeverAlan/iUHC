package spg.lgdev.uhc.command.permission;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.command.abstracton.PermissionCommand;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.Permissions;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.player.PlayerProfile;
import net.md_5.bungee.api.ChatColor;

public class ModSetCommand extends PermissionCommand {

	public ModSetCommand() {
		super("setmod", Permissions.ADMIN);
		setAliases("setmoderator");
	}

	@Override
	public void execute(final Player player, final String[] args) {
		if (args.length == 0) {
			returnTell("§c/setmod <player>");
		}

		if (args.length > 0) {

			final Player p = Bukkit.getPlayer(args[0]);

			checkNull(p, () -> Lang.getMsg(player, "TargetNotOnline"));

			if (!UHCGame.getInstance().isMod(p.getUniqueId())) {

				final PlayerProfile profile = iUHC.getInstance().getProfileManager().getDebuggedProfile(p);

				if (profile.isPlayerAlive()) {

					profile.saveData(p);
				}

				profile.setPlayerAlive(false);
				iUHC.getInstance().getProfileManager().setMod(p);

				tell("§ayou set " + args[0] + " to staff!");

			} else {

				UHCGame.getInstance().getMods().remove(p.getUniqueId());
				iUHC.getInstance().getProfileManager().setRespawn(p, p);
				p.sendMessage(ChatColor.GREEN + "stopped staff mode! Respawned!");
				tell("§ayou removed " + args[0] + " from staff!");

			}

		}
	}

}