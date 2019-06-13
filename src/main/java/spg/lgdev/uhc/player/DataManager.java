package spg.lgdev.uhc.player;

import org.bukkit.Bukkit;

import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.player.database.UHCMySQL;
import spg.lgdev.uhc.scenario.Scenarios;
import spg.lgdev.uhc.util.FastUUID;

public class DataManager {

	private static DataManager instance;
	private final UHCMySQL sql = UHCMySQL.getInstance();

	public static DataManager getInstance() {
		if (instance == null) {
			instance = new DataManager();
		}
		return instance;
	}

	public void loadProfile(final PlayerProfile profile) {
		if (!isHasProfileData(profile)) {
			createProfileData(profile);
			return;
		}

		sql.getDataTable().executeSelect(UHCMySQL.UUID + " = ?")
		.dataSource(sql.getDataSource())
		.statement(s -> s.setString(1, FastUUID.toString(profile.getUUID())))
		.result(profile::setDataFromResult)
		.run();
	}

	public void saveProfile(final PlayerProfile profile) {
		if (isHasProfileData(profile)) {

			sql.getDataTable().executeUpdate("UPDATE `" + UHCMySQL.STATS_TABLE + "` SET "
					+ "`" + UHCMySQL.WINS + "` = ?, "
					+ "`" + UHCMySQL.KILLS + "` = ?, "
					+ "`" + UHCMySQL.DEATHS + "` = ?, "
					+ "`" + UHCMySQL.HIGHEST_KILL_STREAK + "` = ?, "
					+ "`" + UHCMySQL.ARROW_SHOT + "` = ?, "
					+ "`" + UHCMySQL.ARROW_HIT + "` = ?, "
					+ "`" + UHCMySQL.ROD_USED + "` = ?, "
					+ "`" + UHCMySQL.ROD_HIT + "` = ?, "
					+ "`" + UHCMySQL.EXP_EARNED + "` = ?, "
					+ "`" + UHCMySQL.CONSUMED_GAPPLE + "` = ?, "
					+ "`" + UHCMySQL.CONSUMED_GHEAD + "` = ?, "
					+ "`" + UHCMySQL.POTION_DRANKED + "` = ?, "
					+ "`" + UHCMySQL.DIAMOND_MINED + "` = ?, "
					+ "`" + UHCMySQL.GOLD_MINED + "` = ?, "
					+ "`" + UHCMySQL.COAL_MINED + "` = ?, "
					+ "`" + UHCMySQL.IRON_MINED + "` = ?, "
					+ "`" + UHCMySQL.MOBSPAWNER_MINED + "` = ?, "
					+ "`" + UHCMySQL.NETHER_JOINED + "` = ?, "
					+ "`" + UHCMySQL.ELO + "` = ? "
					+ "WHERE `" + UHCMySQL.UUID + "` = ?;"
					).dataSource(sql.getDataSource())
			.statement(s -> {
				s.setInt(1, profile.getWins());
				s.setInt(2, profile.getTotalKills());
				s.setInt(3, profile.getTotalDeaths());
				s.setInt(4, profile.getHighestKillStreak());
				s.setInt(5, profile.getArrowShot());
				s.setInt(6, profile.getArrowHit());
				s.setInt(7, profile.getRodUsed());
				s.setInt(8, profile.getRodHit());
				s.setInt(9, profile.getXpLevelsEarned());
				s.setInt(10, profile.getConsumedGApple());
				s.setInt(11, profile.getConsumedGHeads());
				s.setInt(12, profile.getPotionDranked());
				s.setInt(13, profile.getTotalDiamondsMined());
				s.setInt(14, profile.getTotalGoldMined());
				s.setInt(15, profile.getTotalCoalMined());
				s.setInt(16, profile.getTotalIronMined());
				s.setInt(17, profile.getMobSpawner());
				s.setInt(18, profile.getNetherEntrances());
				s.setInt(19, profile.getElo());
				s.setString(20, FastUUID.toString(profile.getUUID()));
			}).run();
		} else {
			createProfileData(profile);
		}
	}

	public void createProfileData(final PlayerProfile profile) {
		sql.getDataTable().executeInsert("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?")
		.dataSource(sql.getDataSource())
		.statement(s -> {
			s.setString(1, FastUUID.toString(profile.getUUID()));
			s.setInt(2, profile.getWins());
			s.setInt(3, profile.getTotalKills());
			s.setInt(4, profile.getTotalDeaths());
			s.setInt(5, profile.getHighestKillStreak());
			s.setInt(6, profile.getArrowShot());
			s.setInt(7, profile.getArrowHit());
			s.setInt(8, profile.getRodUsed());
			s.setInt(9, profile.getRodHit());
			s.setInt(10, profile.getXpLevelsEarned());
			s.setInt(11, profile.getConsumedGApple());
			s.setInt(12, profile.getConsumedGHeads());
			s.setInt(13, profile.getPotionDranked());
			s.setInt(14, profile.getTotalDiamondsMined());
			s.setInt(15, profile.getTotalGoldMined());
			s.setInt(16, profile.getTotalCoalMined());
			s.setInt(17, profile.getTotalIronMined());
			s.setInt(18, profile.getMobSpawner());
			s.setInt(19, profile.getNetherEntrances());
			s.setInt(20, profile.getElo());
		}).run();
	}

	public boolean isHasProfileData(final PlayerProfile profile) {
		return sql.getDataTable().executeSelect(UHCMySQL.UUID + "= ?")
				.dataSource(sql.getDataSource())
				.statement(s -> s.setString(1, FastUUID.toString(profile.getUUID())))
				.resultNext(r -> true)
				.run(false, false);
	}

	public void saveAllDatas() {
		if (!CachedConfig.SQL || Scenarios.StatLess.isOn())
			return;
		Bukkit.getScheduler().runTaskAsynchronously(iUHC.getInstance(), () -> {
			for (final PlayerProfile profile : iUHC.getInstance().getProfileManager().getProfiles().values()) {
				saveProfile(profile);
			}
		});
	}
}
