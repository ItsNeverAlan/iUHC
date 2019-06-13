package spg.lgdev.uhc.handler.impl;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import spg.lgdev.uhc.iUHC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import spg.lgdev.uhc.api.events.UHCPlayerDeathEvent;
import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.player.PlayerProfile;
import net.development.mitw.utils.timer.PlayerTimer;

public class CombatTimer extends PlayerTimer implements Listener {

	public CombatTimer() {
		super("combat", TimeUnit.SECONDS.toMillis(CachedConfig.Combat), false);
	}

	@Override
	protected void handleExpiry(final Player player, final UUID playerUUID) {
		super.handleExpiry(player, playerUUID);

		if (player == null)
			return;

		UHCGame.getInstance().removeCombatTag(player);
		final PlayerProfile profile = iUHC.getInstance().getProfileManager().getProfile(playerUUID);

		if (profile.getFighting() != null) {
			profile.setFighting(null);
			player.sendMessage(Lang.getMsg(player, "NoCleanPlus.Stopped"));
		}
	}

	@EventHandler
	public void onUHCDeath(final UHCPlayerDeathEvent event) {
		final UUID uuid = event.getPlayer().getUniqueId();
		if (this.isCooldown(uuid)) {
			this.clearCooldown(uuid);
		}
	}

}
