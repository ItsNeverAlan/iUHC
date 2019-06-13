package spg.lgdev.uhc.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import spg.lgdev.uhc.iUHC;

public class ConfigUpdater {

	private final iUHC pl;
	private final String fileName;
	private final FileConfiguration conf;

	public ConfigUpdater(final iUHC main, final String fileName, final FileConfiguration conf) {
		this.pl = main;
		this.conf = conf;
		this.fileName = fileName;
	}

	public void updateConfig() {
		pl.log(true, "&aFirst Time load version " + pl.getVersion() + "...");
		final HashMap<String, Object> newConfig = getConfigVals();
		for (final String var : conf.getKeys(false)) {
			newConfig.remove(var);
		}
		if (newConfig.size() != 0) {
			for (final String key : newConfig.keySet()) {
				conf.set(key, newConfig.get(key));
			}
		}
		try {
			conf.set("version", pl.getVersion());
			conf.save(new File(pl.getDataFolder(), fileName));
		} catch (final IOException e) {
		}
	}

	public HashMap<String, Object> getConfigVals() {
		final HashMap<String, Object> var = new HashMap<>();
		final YamlConfiguration config = new YamlConfiguration();
		try {
			config.load(pl.getResource(fileName));
		} catch (IOException | InvalidConfigurationException e) {
		}
		for (final String key : config.getKeys(false)) {
			var.put(key, config.get(key));
		}
		return var;
	}

}
