package spg.lgdev.uhc.handler.impl;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.player.PlayerProfile;
import spg.lgdev.uhc.util.FastUUID;
import net.development.mitw.utils.timer.PlayerTimer;

public class DisconnectTimer extends PlayerTimer {

	public DisconnectTimer() {
		super("disconnect", TimeUnit.MINUTES.toMillis(10L), false);
	}

	@Override
	protected void handleExpiry(final Player player, final UUID playerUUID) {

		final PlayerProfile profile = iUHC.getInstance().getProfileManager().getProfile(playerUUID);

		UHCGame.getInstance().getOfflineKicks().add(FastUUID.toString(playerUUID));
		UHCGame.getInstance().getWhitelist().remove(playerUUID);

		for (final Player pl : Bukkit.getOnlinePlayers()) {
			pl.sendMessage(Lang.getMsg(pl, "DeathMessages.DISCONNECT")
					.replaceAll("<Player>", profile.getName())
					.replaceAll("<PlayerKills>", "" + profile.getKills()));
		}

		super.handleExpiry(player, playerUUID);
	}

}
