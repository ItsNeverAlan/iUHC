package spg.lgdev.uhc.handler;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.api.UltimateUHCApi;
import spg.lgdev.uhc.handler.game.Loggers;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.player.PlayerProfile;
import spg.lgdev.uhc.player.TeamProfile;

public final class Library {

	private static UltimateUHCApi api = UltimateUHCApi.getApi();

	private Library() {
	}

	public static List<UUID> getAlive() {
		return api.getAlive();
	}

	public static List<UUID> getMods() {
		return api.getMods();
	}

	public static List<UUID> getHost() {
		return api.getHost();
	}

	public static PlayerProfile getPlayerData(final Player p) {
		return api.getPlayerData(p);
	}

	public static PlayerProfile getPlayerData(final UUID uuid) {
		return api.getPlayerData(uuid);
	}

	public static Boolean isGameStarted() {
		return api.isGameStarted();
	}

	public static long getTime() {
		return api.getTime();
	}

	public static TeamProfile getTeam(final Player p) {
		return api.getTeam(p);
	}

	public static UUID getCombatLogger(final Player p) {
		return api.getCombatLogger(p);
	}

	public static Loggers getLoggerManager() {
		return api.getLoggerManager();
	}

	public static UHCGame getGame() {
		return api.getGame();
	}

	public static iUHC getPlugin() {
		return api.getPlugin();
	}

	public static boolean isUsingySpigot() {
		return api.isUsingySpigot();
	}

	public static boolean isUsingnSpigot() {
		return api.isUsingnSpigot();
	}

}
