package spg.lgdev.uhc.command.permission;

import java.util.UUID;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.command.abstracton.PermissionCommand;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.Permissions;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.player.PlayerProfile;
import spg.lgdev.uhc.util.FastUUID;
import net.development.mitw.uuid.UUIDCache;

public class RespawnCommand extends PermissionCommand {

	public RespawnCommand() {
		super("respawn", Permissions.RESPAWN);
	}

	@Override
	public void execute(final Player player, final String[] args) {

		if (args.length < 1) {
			returnTell(ChatColor.RED + "/respawn <player>");
		}

		if (args[0].equalsIgnoreCase("all")) {

			for (final Player p : UHCGame.getInstance().getOnlineSpectators()) {

				player.sendMessage(ChatColor.AQUA + "You respawned " + ChatColor.GREEN + p.getName());
				iUHC.getInstance().getProfileManager().setRespawn(p, player);

			}

			return;
		}

		if (args[0].equalsIgnoreCase("killby")) {

			if (args.length == 1) {
				returnTell("§c/respawn killby <killer>");
			}


			final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

			checkNull(target, () -> Lang.getMsg(player, "TargetNotOnline"));

			final PlayerProfile st = iUHC.getInstance().getProfileManager().getProfile(target.getUniqueId());

			if (st == null)
				return;

			if (st.getKills() == 0) {
				returnTell(Lang.getMsg(player, "KillCount.NoKills"));
			}

			for (final UUID u : st.getKilled()) {

				if (iUHC.getInstance().getProfileManager().getProfile(u).isSpectator()) {

					tell(ChatColor.AQUA + "You respawned " + ChatColor.GREEN + UUIDCache.getName(u));
					iUHC.getInstance().getProfileManager().setRespawn(Bukkit.getPlayer(u), player);

				}

			}

			return;
		}

		final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);


		if (target == null) {
			returnTell(ChatColor.RED + "cant find the player!");
		}

		tell(ChatColor.GREEN + "You respawned " + ChatColor.YELLOW + target.getName());

		if (target.isOnline()) {
			iUHC.getInstance().getProfileManager().setRespawn((Player) target, player);
		} else if (!UHCGame.getInstance().getOfflineRespawns().contains(FastUUID.toString(target.getUniqueId()))) {

			UHCGame.getInstance().getOfflineRespawns().add(target.getUniqueId().toString());

			if (!UHCGame.getInstance().getWhitelist().contains(target.getUniqueId())) {
				UHCGame.getInstance().getWhitelist().add(target.getUniqueId());
			}

		}
	}

}

