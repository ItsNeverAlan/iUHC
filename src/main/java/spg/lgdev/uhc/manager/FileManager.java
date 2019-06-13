package spg.lgdev.uhc.manager;

import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.config.ConfigUpdater;
import spg.lgdev.uhc.config.Configuration;
import spg.lgdev.uhc.populator.GenRule;

public class FileManager {

	private final iUHC plugin = iUHC.getInstance();
	private Configuration mainConfiguration;
	private Configuration SB;
	private Configuration RC;
	private Configuration LC;
	private Configuration SOC;
	private Configuration IC;

	public FileManager() {
		loadConfiguration();
		plugin.saveResource("simple-infomation.txt", false);
	}

	public void loadConfiguration() {
		SB = new Configuration("scoreboard");
		RC = new Configuration("populators");
		LC = new Configuration("language");
		SOC = new Configuration("cache");
		IC = new Configuration("items");
		mainConfiguration = new Configuration("config");
	}

	public void checkUpdater() {
		if (!getConfig().getString("version").equals(plugin.getVersion())) {
			final ConfigUpdater updater = new ConfigUpdater(plugin, "config.yml", getConfig());
			updater.updateConfig();
		}
	}

	public Configuration getScoreboards() {
		return SB;
	}

	public void saveScoreboards() {
		SB.save();
	}

	public Configuration getCache() {
		return SOC;
	}

	public void saveCache() {
		SOC.save();
	}

	public Configuration getLanguage() {
		return LC;
	}

	public void saveLanguage() {
		LC.save();
	}

	public Configuration getItems() {
		return IC;
	}

	public void saveItems() {
		IC.save();
	}

	public Configuration getPopulators() {
		return RC;
	}

	public void savePopulators() {
		RC.save();
	}

	public Configuration getConfig() {
		return mainConfiguration;
	}

	public void saveConfig() {
		mainConfiguration.save();
	}

	public void LoadUpGenerate() {
		if (!this.getPopulators().getBoolean("ore-generate.enabled"))
			return;
		for (final String s : this.getPopulators().getStringList("ore-generate.rules")) {
			GenRule.unparse(s);
		}
	}

}
