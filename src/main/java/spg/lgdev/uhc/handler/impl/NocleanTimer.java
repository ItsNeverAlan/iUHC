package spg.lgdev.uhc.handler.impl;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import spg.lgdev.uhc.iUHC;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.config.CachedConfig;
import net.development.mitw.utils.timer.PlayerTimer;

public class NocleanTimer extends PlayerTimer {

	public NocleanTimer() {
		super("Noclean", TimeUnit.SECONDS.toMillis(CachedConfig.NoClean), false);
	}

	@Override
	protected void handleExpiry(final Player player, final UUID playerUUID) {
		super.handleExpiry(player, playerUUID);

		if (player == null)
			return;

		iUHC.getInstance().getProfileManager().getProfile(playerUUID).setNoClean(false);
	}

}
