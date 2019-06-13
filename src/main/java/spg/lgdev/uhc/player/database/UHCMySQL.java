package spg.lgdev.uhc.player.database;

import javax.sql.DataSource;

import lombok.Getter;
import me.skymc.taboolib.mysql.builder.SQLColumn;
import me.skymc.taboolib.mysql.builder.SQLColumnType;
import me.skymc.taboolib.mysql.builder.SQLHost;
import me.skymc.taboolib.mysql.builder.SQLTable;
import me.skymc.taboolib.mysql.builder.hikari.HikariHandler;
import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.config.Configuration;

public class UHCMySQL {

	public static final String STATS_TABLE = "iUHC";
	public static final String UUID = "uuid";
	public static final String WINS = "wins";
	public static final String KILLS = "kills";
	public static final String DEATHS = "deaths";
	public static final String HIGHEST_KILL_STREAK = "highest_kill_streak";
	public static final String ARROW_SHOT = "arrow_shot";
	public static final String ARROW_HIT = "arrow_hit";
	public static final String ROD_USED = "rod_used";
	public static final String ROD_HIT = "rod_hit";
	public static final String CONSUMED_GAPPLE = "consumed_goldenapples";
	public static final String CONSUMED_GHEAD = "consumed_goldenheads";
	public static final String EXP_EARNED = "exp_earned";
	public static final String POTION_DRANKED = "potion_dranked";
	public static final String DIAMOND_MINED = "diamondore_mined";
	public static final String IRON_MINED = "ironore_mined";
	public static final String COAL_MINED = "coalore_mined";
	public static final String GOLD_MINED = "goldore_mined";
	public static final String MOBSPAWNER_MINED = "mobspawner_mined";
	public static final String NETHER_JOINED = "nether_entrances";
	public static final String ELO = "elo";
	private static UHCMySQL instance;
	private String hostname;
	private int port;
	private String database;
	private String username;
	private String password;
	@Getter
	private SQLTable dataTable;
	@Getter
	private DataSource dataSource;
	@Getter
	private SQLHost sqlHost;

	public static UHCMySQL getInstance() {
		if (instance == null) {
			instance = new UHCMySQL();
		}
		return instance;
	}

	public void openConnection() {
		try {
			final Configuration config = iUHC.getInstance().getFileManager().getConfig();
			hostname = config.getString("MySQL.host");
			port = config.getInt("MySQL.port");
			database = config.getString("MySQL.database");
			username = config.getString("MySQL.username");
			password = config.getString("MySQL.password");
			sqlHost = new SQLHost(hostname, username, String.valueOf(port), password, database);
			dataSource = HikariHandler.createDataSource(sqlHost);
			createTables();
			iUHC.getInstance().log(true, "SQL Database connected!");
		} catch (final Exception e) {
			iUHC.getInstance().log(true, "SQL Database connect failed to mysql!");
		}
	}

	public void closeConnection() {
		HikariHandler.closeDataSource(sqlHost);
	}

	public void createTables() {
		dataTable = new SQLTable(STATS_TABLE)
				.addColumn(new SQLColumn(SQLColumnType.TEXT, UUID))
				.addColumn(new SQLColumn(SQLColumnType.INT, WINS, 0))
				.addColumn(new SQLColumn(SQLColumnType.INT, KILLS, 0))
				.addColumn(new SQLColumn(SQLColumnType.INT, DEATHS, 0))
				.addColumn(new SQLColumn(SQLColumnType.INT, HIGHEST_KILL_STREAK, 0))
				.addColumn(new SQLColumn(SQLColumnType.INT, ARROW_SHOT, 0))
				.addColumn(new SQLColumn(SQLColumnType.INT, ARROW_HIT, 0))
				.addColumn(new SQLColumn(SQLColumnType.INT, ROD_USED, 0))
				.addColumn(new SQLColumn(SQLColumnType.INT, ROD_HIT, 0))
				.addColumn(new SQLColumn(SQLColumnType.INT, EXP_EARNED, 0))
				.addColumn(new SQLColumn(SQLColumnType.INT, CONSUMED_GAPPLE, 0L))
				.addColumn(new SQLColumn(SQLColumnType.INT, CONSUMED_GHEAD, 0))
				.addColumn(new SQLColumn(SQLColumnType.INT, POTION_DRANKED, 0))
				.addColumn(new SQLColumn(SQLColumnType.INT, DIAMOND_MINED, 0))
				.addColumn(new SQLColumn(SQLColumnType.INT, GOLD_MINED, 0))
				.addColumn(new SQLColumn(SQLColumnType.INT, COAL_MINED, 0))
				.addColumn(new SQLColumn(SQLColumnType.INT, IRON_MINED, 0))
				.addColumn(new SQLColumn(SQLColumnType.INT, MOBSPAWNER_MINED, 0))
				.addColumn(new SQLColumn(SQLColumnType.INT, NETHER_JOINED, 0))
				.addColumn(new SQLColumn(SQLColumnType.INT, ELO, 1000));

		dataTable.executeUpdate(dataTable.createQuery()).dataSource(dataSource).run();
	}
}

