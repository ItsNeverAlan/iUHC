package spg.lgdev.uhc.player;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import lombok.Data;
import lombok.Setter;
import spg.lgdev.uhc.api.events.UHCProfileCreatedEvent;
import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.enums.PlayerStatus;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.player.database.UHCMySQL;
import spg.lgdev.uhc.player.rating.RatingHistory;
import spg.lgdev.uhc.util.CustomLocation;

@Data
public class PlayerProfile {

	private UUID UUID;
	private String name;

	private short superHeroID = -1;

	private int kills = 0;
	private int diamond = 0;
	private int gold = 0;
	private int mobSpawner = 0;

	private int wins = 0;
	private int totalDeaths = 0;
	private int totalKills = 0;
	private int highestKillStreak = 0;

	private int netherEntrances = 0;
	private int potionDranked = 0;
	private int xpLevelsEarned = 0;

	private int totalDiamondsMined = 0;
	private int totalGoldMined = 0;
	private int totalIronMined = 0;
	private int totalCoalMined = 0;
	private int totalSpawnersMined = 0;
	private int consumedGApple = 0;
	private int consumedGHeads = 0;
	private int arrowShot = 0;
	private int arrowHit = 0;
	private int rodUsed = 0;
	private int rodHit = 0;
	private int totalPotion = 0;
	private double totalHeal = 0;
	private int longestCombo = 0;
	private int combo = 0;
	private int hits = 0;

	private int elo = 1000;

	private boolean vanish = true;
	private boolean playerAlive = true;
	private boolean frozen = false;

	private boolean teamChat = false;
	private boolean isNoClean = false;

	private boolean online = true;
	private boolean lateScatter = true;

	private UUID combatLoggerUUID = null;
	private boolean loggerDied = false;

	private PlayerOptions options;
	private PlayerData data;

	private long actionCountdown;

	private CustomLocation scatterLocation;
	@Setter
	private PlayerStatus status;
	private UUID fighting;

	private TeamProfile team;
	private UUID lafsTarget;

	private List<UUID> killed = new ArrayList<>();
	private List<RatingHistory> ratingHistories = new ArrayList<>();

	public PlayerProfile(final UUID uuid, final String name) {
		this.UUID = uuid;
		this.name = name;
		this.status = PlayerStatus.LOBBY;
		final Player p = Bukkit.getPlayer(UUID);
		if (GameStatus.started() && !p.getWorld().getName().equalsIgnoreCase("UHCArena")
				&& !p.getWorld().getName().equalsIgnoreCase("UHCArena_nether")
				&& !p.getWorld().getName().equalsIgnoreCase("UHCArena_deathmatch")) {
			this.status = PlayerStatus.SCATTERING;
		}
		if (CachedConfig.SQL) {
			DataManager.getInstance().loadProfile(this);
		}
		setOptions(new PlayerOptions(UUID));
		Bukkit.getPluginManager().callEvent(new UHCProfileCreatedEvent(this));
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(this.UUID);
	}

	public void addKilled(final Player player) {
		killed.add(player.getUniqueId());
	}

	public void addMobSpawner() {
		++this.mobSpawner;
	}

	public void addDiamond() {
		++this.diamond;
	}

	public void addGold() {
		++this.gold;
	}

	public void addTotalKills() {
		++this.totalKills;
	}

	public void addTotalDeaths() {
		++this.totalDeaths;
	}

	public void toggleTeamChat() {
		teamChat = !teamChat;
	}

	public double getKd() {
		double d = 0.0D;
		if ((this.totalKills > 0) && (this.totalDeaths == 0)) {
			d = this.totalKills;
		} else if ((this.totalKills == 0) && (this.totalDeaths == 0)) {
			d = 0.0D;
		} else {
			d = this.totalKills / this.totalDeaths;
		}
		return d;
	}

	public void addPotionDranked() {
		potionDranked++;
	}

	public void addNetherEntrances() {
		++this.netherEntrances;
	}

	public void addXpLevelsEarned() {
		++this.xpLevelsEarned;
	}

	public void addTotalDiamondsMined() {
		++this.totalDiamondsMined;
	}

	public void addTotalGoldMined() {
		++this.totalGoldMined;
	}

	public void addTotalIronMined() {
		++this.totalIronMined;
	}

	public void addTotalCoalMined() {
		++this.totalCoalMined;
	}

	public void addTotalSpawnersMined() {
		++this.totalSpawnersMined;
	}

	public void addConsumedGApple() {
		consumedGApple++;
	}

	public void addConsumedGHeads() {
		consumedGHeads++;
	}

	public void addArrowShot() {
		arrowShot++;
	}

	public void addArrowHit() {
		arrowHit++;
	}

	public void addRodUsed() {
		rodUsed++;
	}

	public void addRodHit() {
		rodHit++;
	}

	public boolean isNoClean() {
		return isNoClean;
	}

	public void setNoClean(final boolean isNoClean) {
		if (this.isNoClean == isNoClean)
			return;
		this.isNoClean = isNoClean;
		final Player p = getPlayer();
		if (p != null) {
			if (isNoClean) {
				p.sendMessage(Lang.getMsg(p, "NoClean.Started"));
			} else {
				p.sendMessage(Lang.getMsg(p, "NoClean.Stoped"));
			}
		}
	}

	public PlayerProfile setDataFromResult(final ResultSet result) {
		try {
			if (result.isBeforeFirst()) {
				while (result.next()) {
					setWins(result.getInt(UHCMySQL.WINS));
					setTotalKills(result.getInt(UHCMySQL.KILLS));
					setTotalDeaths(result.getInt(UHCMySQL.DEATHS));
					setHighestKillStreak(result.getInt(UHCMySQL.HIGHEST_KILL_STREAK));
					setArrowShot(result.getInt(UHCMySQL.ARROW_SHOT));
					setArrowHit(result.getInt(UHCMySQL.ARROW_HIT));
					setRodUsed(result.getInt(UHCMySQL.ROD_USED));
					setRodHit(result.getInt(UHCMySQL.ROD_HIT));
					setXpLevelsEarned(result.getInt(UHCMySQL.EXP_EARNED));
					setConsumedGApple(result.getInt(UHCMySQL.CONSUMED_GAPPLE));
					setConsumedGHeads(result.getInt(UHCMySQL.CONSUMED_GHEAD));
					setPotionDranked(result.getInt(UHCMySQL.POTION_DRANKED));
					totalDiamondsMined = result.getInt(UHCMySQL.DIAMOND_MINED);
					totalGoldMined = result.getInt(UHCMySQL.GOLD_MINED);
					totalCoalMined = result.getInt(UHCMySQL.COAL_MINED);
					totalIronMined = result.getInt(UHCMySQL.IRON_MINED);
					totalSpawnersMined = result.getInt(UHCMySQL.MOBSPAWNER_MINED);
					netherEntrances = result.getInt(UHCMySQL.NETHER_JOINED);
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}

		return this;
	}

	public void enableNoClean() {
		setNoClean(true);
		iUHC.getInstance().getNocleanTimer().setCooldown(getPlayer(), UUID);
	}

	public void saveData(final Player p) {
		if (data == null) {
			data = new PlayerData(p);
		} else {
			data.set(p);
		}
	}

	public void setSuperHero(final Player p) {
		if (superHeroID == -1)
			return;
		p.addPotionEffect((PotionEffect) UHCGame.getInstance().getEffectMap().keySet().toArray()[superHeroID]);
	}

	public boolean isActionCountdown() {
		return System.currentTimeMillis() - actionCountdown < CachedConfig.INTERACT_COOLDOWN * 1000;
	}

	public void startActionCountdown() {
		actionCountdown = System.currentTimeMillis();
	}

	public void addKills() {
		kills++;
	}

	public void addWins() {
		wins++;
	}

	public boolean isSpectator() {
		return !playerAlive && !UHCGame.getInstance().isMod(UUID);
	}

	public void addRatingHistory(final RatingHistory history) {
		this.ratingHistories.add(history);
	}

	public void removeRatingHistory(final RatingHistory history) {
		this.ratingHistories.remove(history);
	}

}
