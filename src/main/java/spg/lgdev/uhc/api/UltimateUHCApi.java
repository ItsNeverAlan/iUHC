package spg.lgdev.uhc.api;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import lombok.Getter;
import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.handler.game.Loggers;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.nms.NMSHandler;
import spg.lgdev.uhc.player.PlayerProfile;
import spg.lgdev.uhc.player.TeamProfile;

public class UltimateUHCApi {

	@Getter
	private static UltimateUHCApi api = new UltimateUHCApi();

	public List<UUID> getAlive() {

		return UHCGame.getInstance().getPlayersUUID();
	}

	public List<UUID> getMods() {
		return UHCGame.getInstance().getMods();
	}

	public List<UUID> getHost() {
		return UHCGame.getInstance().getHosts();
	}

	public PlayerProfile getPlayerData(final Player p) {
		return this.getPlayerData(p.getUniqueId());
	}

	public PlayerProfile getPlayerData(final UUID uuid) {
		return iUHC.getInstance().getProfileManager().getProfile(uuid);
	}

	public boolean isGameStarted() {
		return GameStatus.started();
	}

	public long getTime() {
		return UHCGame.getInstance().gameCountdowns;
	}

	public TeamProfile getTeam(final Player p) {
		return this.getPlayerData(p).getTeam();
	}

	public UUID getCombatLogger(final Player p) {
		return getPlayerData(p).getCombatLoggerUUID();
	}

	public Loggers getLoggerManager() {
		return Loggers.getInstance();
	}

	public UHCGame getGame() {
		return UHCGame.getInstance();
	}

	public iUHC getPlugin() {
		return iUHC.getInstance();
	}

	public boolean isScattering() {
		return GameStatus.is(GameStatus.TELEPORT);
	}

}
