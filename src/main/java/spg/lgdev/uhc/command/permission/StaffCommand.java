package spg.lgdev.uhc.command.permission;

import org.bukkit.entity.Player;

import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.command.abstracton.PermissionCommand;
import spg.lgdev.uhc.handler.Permissions;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.player.PlayerProfile;
import net.md_5.bungee.api.ChatColor;

public class StaffCommand extends PermissionCommand {

	public StaffCommand() {
		super("staff", Permissions.TOGGLE_STAFF);
		setAliases("mod", "moderator");
	}

	@Override
	public void execute(final Player p, final String[] args) {
		if (!UHCGame.getInstance().isMod(p.getUniqueId())) {

			final PlayerProfile profile = iUHC.getInstance().getProfileManager().getDebuggedProfile(p);

			if (profile.isPlayerAlive()) {

				profile.saveData(p);
			}

			profile.setPlayerAlive(false);
			iUHC.getInstance().getProfileManager().setMod(p);

		} else {
			UHCGame.getInstance().getMods().remove(p.getUniqueId());
			iUHC.getInstance().getProfileManager().setRespawn(p, p);
			p.sendMessage(ChatColor.GREEN + "stopped staff mode! Respawned!");
		}
	}

}