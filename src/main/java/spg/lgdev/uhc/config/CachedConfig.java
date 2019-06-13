package spg.lgdev.uhc.config;

import java.text.DecimalFormat;
import java.util.List;

import spg.lgdev.uhc.iUHC;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import lombok.Getter;
import spg.lgdev.uhc.exception.NullConfigurationSectionException;
import spg.lgdev.uhc.gui.gameconfig.LanguageGUI;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.manager.FileManager;
import spg.lgdev.uhc.util.StringUtil;
import spg.lgdev.uhc.util.UHCSound;

public class CachedConfig {

	private static final FileManager fileManager = iUHC.getInstance().getFileManager();

	@Getter
	private static final String HWID = fileManager.getConfig().getString("HWID");

	public static boolean PerformanceMode = false, DISABLE_LOBBY_SOUNDS = false, OLD_ENCHANTING = false, sugarcaneEnabled = false, SQL, LESS_CPU_USAGE;
	public static double BORDER_KNOCKBACK;
	public static String GH_NAME, ChatFFA, ChatTeam, ChatHost, ChatMod, ChatSpectator, TeamPrivateChat, MatchArenaType, RestartCMD, TAB_TYPE;
	public static String A, B, C, LOGIN_LOADING, LOGIN_FULL, LOGIN_WHITELIST, LOGIN_INGAME, LOGIN_DISCONNECT, LOGIN_FINISH, title;
	public static String MOTD_INSETUP, MOTD_WHITELISTED, MOTD_LOBBY, MOTD_STARTING, MOTD_INGAME, MOTD_FINISHING, DISCORD_TITLE, DISCORD_AUTHOR;
	public static String TEAMMATE_TAG, ENEMY_TAG, STAFF_TAG, DISCORD_TAG;
	public static List<String> BLOCKED_CMDS, COMBAT_BLOCKED_CMDS, FROZEN_BLOCKED_CMDS, HUBS, DISCORD_ANNOUNCE, DISCORD_FFA_WIN, DISCORD_TEAM_WIN, GH_LORES;
	public static List<String> TWITTER_ANNOUNCE, TWITTER_FFA_WIN, TWITTER_TEAM_WIN, startCommands;
	public static List<Long> ARENA_SEEDS, PRACTICE_SEEDS;
	public static DecimalFormat FORMAT;
	public static long NoClean;
	public static int center, LoadFrequency, MandatorySize, BiomeLava, BiomeTree, MaxHigher, BiomeDisAllowed, RiverInsideCenter, Combat, LocationGenerates, ELOCostRating;
	public static int MatchStartIn, MatchCountDown, MatchBorderSize, NetherBorderSize, NetherTP, LoadPadding, sugarcanePrcent, INTERACT_COOLDOWN;
	public static int caveRarity, caveFrequency, caveMinAltitude, caveMaxAltitude, individualCaveRarity, caveSystemFrequency, caveSystemPocketChance, caveSystemPocketMinSize;
	public static int caveSystemPocketMaxSize, worldHeightCap;
	public static boolean MandatoryEnabled, BiomeChecker, RiverCheck, WorldBorder, isLoadChunk, LoadFinishRestart, UNLOADWB, iPvP, AVOID_ASYNC_CATCHER, LOW_MEMORY_CHUNK_LOAD_RESTART;
	public static boolean MatchEnabled, MatchBorderEnabled, MatchWorldBorderEnabled, UNLIMIT_TP_TIME, afkkick, BoardEnabled, PRACTICE, HIDEALL, RESTART_GAMEFINISH;
	public static boolean LAG_BROADCAST, evenCaveDistribution, BIOMESWAP, FPS_BOOSTER, BOSSBAR;

	public static UHCSound SOUND_BORDER_COUNTDOWN, SOUND_SCATTER, SOUND_FIRSTHEAL, SOUND_COUNTDOWN, SOUND_FINALHEAL, SOUND_PVP, SOUND_BORDER_SHRINKED, SOUND_GAMESTART, SOUND_PRACTICE, SOUND_JOIN, SOUND_MLG_ACCEPT, SOUND_DEATHMATCH, SOUND_GAME_END;

	public static void reloadConfig() {

		// DEFAULT NOT CONFIGUREABLE
		BOSSBAR = false;
		ELOCostRating = 1250;

		Lang.getInstance().reloadMessages();
		LanguageGUI.resetInstance();
		final FileConfiguration config = fileManager.getConfig();
		final FileConfiguration rules = getRules();
		final FileConfiguration score = getBoard();
		try {
			final ConfigurationSection deathmatch = config.getConfigurationSection("DeathMatch");
			final ConfigurationSection cave = rules.getConfigurationSection("Custom-Cave");
			startCommands = config.getStringList("startCommands");
			LAG_BROADCAST = config.getBoolean("lag-broadcast");
			FPS_BOOSTER = config.getBoolean("fps-booster");
			sugarcaneEnabled = rules.getBoolean("SugarCane-Generate.Enabled");
			sugarcanePrcent = rules.getInt("SugarCane-Generate.Percent");
			ARENA_SEEDS = rules.getLongList("WorldCreator.Arena-Seeds");
			PRACTICE_SEEDS = rules.getLongList("WorldCreator.Practice-Seeds");
			afkkick = config.getBoolean("AFK-KICK");
			title = StringUtil.cc(score.getString("UHC-Scoreboard.title"));
			MOTD_INSETUP = StringUtil.cc(config.getString("motd.InSetup"));
			MOTD_WHITELISTED = StringUtil.cc(config.getString("motd.Lobby-Whitelisted"));
			MOTD_LOBBY = StringUtil.cc(config.getString("motd.Lobby-NoWhitelist"));
			MOTD_STARTING = StringUtil.cc(config.getString("motd.Starting-Game"));
			MOTD_INGAME = StringUtil.cc(config.getString("motd.InGame"));
			MOTD_FINISHING = StringUtil.cc(config.getString("motd.Finished-MLG"));
			MandatoryEnabled = config.getBoolean("CenterClear.MandatoryChanger.Enabled");
			MandatorySize = config.getInt("CenterClear.MandatoryChanger.Size-Factor");
			center = config.getInt("CenterClear.MandatoryChanger.Size-Factor");
			BiomeChecker = config.getBoolean("CenterClear.Enabled");
			BiomeLava = config.getInt("CenterClear.Checker.WaterCountLimited");
			BiomeTree = config.getInt("CenterClear.Checker.TreeCountLimited");
			BiomeDisAllowed = config.getInt("CenterClear.Checker.DisAllowedBiomeLimited");
			RiverCheck = config.getBoolean("CenterClear.Checker.RiverCheckEnabled");
			RiverInsideCenter = config.getInt("CenterClear.Checker.RiverCheckIn");
			MaxHigher = config.getInt("CenterClear.Checker.MaxHigherOfCenter");
			LESS_CPU_USAGE = config.getBoolean("less-cpu-usage");
			MatchEnabled = deathmatch.getBoolean("Enabled");
			MatchBorderEnabled = deathmatch.getBoolean("Border.Bedrock");
			MatchWorldBorderEnabled = deathmatch.getBoolean("Border.WorldBorder");
			MatchArenaType = deathmatch.getString("ArenaType");
			FORMAT = new DecimalFormat(CachedConfig.getBoard().getString("UHC-Scoreboard.timer-format"));
			MatchStartIn = deathmatch.getInt("StartIn");
			HUBS = config.getStringList("bungee.hubNames");
			AVOID_ASYNC_CATCHER = config.getBoolean("Avoid-async-catcher");
			MatchCountDown = deathmatch.getInt("CountDown");
			MatchBorderSize = deathmatch.getInt("Border.Size");
			LOW_MEMORY_CHUNK_LOAD_RESTART = config.getBoolean("Chunk.LowMemoryRestart");
			WorldBorder = config.getBoolean("WorldBorderEnabled");
			TEAMMATE_TAG = StringUtil.cc(config.getString("TeamColor.TeammateColor"));
			ENEMY_TAG = StringUtil.cc(config.getString("TeamColor.EnemyColor"));
			STAFF_TAG = StringUtil.cc(config.getString("TeamColor.StaffColor"));
			NetherTP = config.getInt("Border.BorderTPNether");
			PerformanceMode = config.getBoolean("performance-mode");
			DISABLE_LOBBY_SOUNDS = config.getBoolean("sounds.disable-sounds-in-lobby");
			OLD_ENCHANTING = config.getBoolean("OldEnchant.Enabled");
			GH_NAME = StringUtil.cc(config.getString("scenarios.GoldenHead.display-name"));
			GH_LORES = config.getStringList("scenarios.GoldenHead.lore");
			NetherBorderSize = config.getInt("Border.NetherDefault");
			SQL = config.getBoolean("MySQL.enabled");
			INTERACT_COOLDOWN = config.getInt("gameConfig.interact-cooldown");
			ChatFFA = StringUtil.cc(config.getString("Chat.Format.Solo"));
			ChatTeam = StringUtil.cc(config.getString("Chat.Format.Team"));
			ChatHost = StringUtil.cc(config.getString("Chat.Format.Host"));
			ChatMod = StringUtil.cc(config.getString("Chat.Format.Mod"));
			ChatSpectator = StringUtil.cc(config.getString("Chat.Format.Spec"));
			TeamPrivateChat = StringUtil.cc(config.getString("Chat.Format.TeamChat"));
			LOGIN_LOADING = StringUtil.cc(config.getString("onLogin-Blocked.ChunkLoading"));
			LOGIN_FULL = StringUtil.cc(config.getString("onLogin-Blocked.FullGame"));
			LOGIN_WHITELIST = StringUtil.cc(config.getString("onLogin-Blocked.Whitelist"));
			LOGIN_INGAME = StringUtil.cc(config.getString("onLogin-Blocked.InGame"));
			LOGIN_DISCONNECT = StringUtil.cc("&cYou Leave the game 10 mins already , removed from whitelist!");
			LOGIN_FINISH = StringUtil.cc(config.getString("onLogin-Blocked.Finish"));
			UNLIMIT_TP_TIME = config.getBoolean("Unlimit-Scatter-Times");
			LoadFrequency = config.getInt("Chunk.LoadFrequency");
			BLOCKED_CMDS = config.getStringList("blocked-cmds");
			LoadPadding = config.getInt("Chunk.LoadPadding");
			BORDER_KNOCKBACK = config.getDouble("Border.Knockback");
			isLoadChunk = config.getBoolean("Chunk.LoadEnabled");
			BIOMESWAP = rules.getBoolean("biomeswap.enabled");
			LoadFinishRestart = config.getBoolean("Chunk.FinishRestart");
			RestartCMD = config.getString("Chunk.RestartCMD");
			TAB_TYPE = config.getString("TabHealth.Type");
			NoClean = config.getLong("scenarios.config.NoCleanTimer");
			UNLOADWB = config.getBoolean("Chunk.unloadWorldborderOnFinished");
			PRACTICE = config.getBoolean("PracticeEnabled");
			iPvP = config.getBoolean("iPvP-Protactor");
			A = config.getString("color.normal");
			HIDEALL = config.getBoolean("Lobby.HideAllPlayer");
			BoardEnabled = score.getBoolean("UHC-Scoreboard.enabled");
			COMBAT_BLOCKED_CMDS = config.getStringList("combatLogger.block-cmds");
			Combat = config.getInt("combatLogger.timer", 25);
			FROZEN_BLOCKED_CMDS = config.getStringList("freezer.blacklist-cmds");
			SOUND_BORDER_COUNTDOWN = UHCSound.fromName(config.getString("sounds.border-countdown"));
			SOUND_SCATTER = UHCSound.fromName(config.getString("sounds.scatter"));
			SOUND_FIRSTHEAL = UHCSound.fromName(config.getString("sounds.firstheal"));
			SOUND_COUNTDOWN = UHCSound.fromName(config.getString("sounds.countdown"));
			SOUND_FINALHEAL = UHCSound.fromName(config.getString("sounds.finalheal"));
			SOUND_PVP = UHCSound.fromName(config.getString("sounds.pvpenabled"));
			SOUND_BORDER_SHRINKED = UHCSound.fromName(config.getString("sounds.border-shrinked"));
			SOUND_GAMESTART = UHCSound.fromName(config.getString("sounds.gamestart"));
			SOUND_PRACTICE = UHCSound.fromName(config.getString("sounds.practice"));
			SOUND_JOIN = UHCSound.fromName(config.getString("sounds.join"));
			SOUND_MLG_ACCEPT = UHCSound.fromName(config.getString("sounds.mlg-accept"));
			SOUND_DEATHMATCH = UHCSound.fromName(config.getString("sounds.deathmatch"));
			SOUND_GAME_END = UHCSound.fromName(config.getString("sounds.game-end"));
			RESTART_GAMEFINISH = config.getBoolean("restart-onmatch-ended");
			DISCORD_ANNOUNCE = config.getStringList("Discord.UHC-Announce");
			DISCORD_FFA_WIN = config.getStringList("Discord.Solo-Winner-Announce");
			DISCORD_TEAM_WIN = config.getStringList("Discord.Team-Winner-Announce");
			TWITTER_ANNOUNCE = config.getStringList("Twitter.UHC-Announce");
			TWITTER_FFA_WIN = config.getStringList("Twitter.Solo-Winner-Announce");
			TWITTER_TEAM_WIN = config.getStringList("Twitter.Team-Winner-Announce");
			DISCORD_TITLE = config.getString("Discord.Embed-Title");
			DISCORD_AUTHOR = config.getString("Discord.Embed-Author");
			DISCORD_TAG = config.getString("Discord.Tag");
			caveRarity = cave.getInt("CaveRarity", 55);
			caveFrequency = cave.getInt("CaveFrequency", 20);
			caveMinAltitude = cave.getInt("CaveMinAltitude", 12);
			caveMaxAltitude = cave.getInt("CaveMaxAltitude");
			individualCaveRarity = cave.getInt("IndividualCaveRarity");
			caveSystemFrequency = cave.getInt("CaveSystemFrequency");
			caveSystemPocketChance = cave.getInt("CaveSystemPocketChance");
			caveSystemPocketMinSize = cave.getInt("CaveSystemPocketMinSize");
			caveSystemPocketMaxSize = cave.getInt("CaveSystemPocketMaxSize");
			evenCaveDistribution = cave.getBoolean("EvenCaveDistrubution");
			LocationGenerates = config.getInt("default-location-generates");
			if (A != null) {
				A = StringUtil.cc(A);
			} else
				throw new NullConfigurationSectionException("color.normal is not inside config.yml!");
			B = config.getString("color.important");
			if (B != null) {
				B = StringUtil.cc(B);
			} else
				throw new NullConfigurationSectionException("color.important is not inside config.yml!");
			C = config.getString("color.a_bit_important");
			if (C != null) {
				C = StringUtil.cc(C);
			} else
				throw new NullConfigurationSectionException("color.a_bit_important is not inside config.yml!");
		} catch (final Exception e) {
			throw new NullConfigurationSectionException("an error has been cause when loading config: " + e.getCause().getMessage());
		}
	}


	/**
	 * Config Returns
	 *
	 * @return
	 */

	public static FileConfiguration getConfig() {
		return fileManager.getConfig();
	}

	public static FileConfiguration getLanguage() {
		return fileManager.getLanguage();
	}

	public static FileConfiguration getBoard() {
		return fileManager.getScoreboards();
	}

	public static FileConfiguration getRules() {
		return fileManager.getPopulators();
	}

	public static FileConfiguration getItems() {
		return fileManager.getItems();
	}

	public static boolean isCaneEnabled() {
		boolean b = false;
		if (sugarcaneEnabled) {
			b = true;
		}
		return b;
	}

	public static int getCanePercent() {
		final int i = sugarcanePrcent;
		return i;
	}

	public static boolean getAfkKickEnabled() {
		return afkkick;
	}

	public static int getCenterCount() {
		return center;
	}

	public static void setIsGenerated() {
		fileManager.getCache().set("IsWorldGenerated", true);
		fileManager.saveCache();
	}

	public static boolean IsGenerated() {
		return fileManager.getCache().getBoolean("IsWorldGenerated");
	}

	public static void setIsNotGenerated() {
		fileManager.getCache().set("IsWorldGenerated", false);
		fileManager.saveCache();
	}

	public static void setIsGenerating() {
		fileManager.getCache().set("IsWorldInGenerating", true);
		fileManager.saveCache();
	}

	public static boolean IsGenerating() {
		return fileManager.getCache().getBoolean("IsWorldInGenerating");
	}

	public static void setIsNotGenerating() {
		fileManager.getCache().set("IsWorldInGenerating", false);
		fileManager.saveCache();
	}

	public static String getWorldIsInGen() {
		return fileManager.getCache().getString("WorldIsIn");
	}

	public static void setWorldIsInGen(final String worldName) {
		fileManager.getCache().set("WorldIsIn", worldName);
		fileManager.saveCache();
	}

	public static void setStatsBorder() {
		fileManager.getCache().set("border", UHCGame.getInstance().getBorderRadius());
		fileManager.saveCache();
	}

}
