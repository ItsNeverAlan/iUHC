package spg.lgdev.uhc.nms;

import org.bukkit.Bukkit;

import lombok.Getter;
import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.enums.ServerVersion;
import spg.lgdev.uhc.exception.NewInstanceRejectException;
import spg.lgdev.uhc.exception.UnsupportedVersionException;
import spg.lgdev.uhc.nms.common.BiomeHandler;
import spg.lgdev.uhc.nms.common.BiomeReplacer;
import spg.lgdev.uhc.nms.common.IFastBlockSet;
import spg.lgdev.uhc.nms.common.NMSControl;
import spg.lgdev.uhc.nms.common.SitHandler;
import spg.lgdev.uhc.nms.v1_8_R3.BiomeReplacer1_8;
import spg.lgdev.uhc.nms.v1_8_R3.Biomeswap1_8;
import spg.lgdev.uhc.nms.v1_8_R3.FastBlockSet1_8;
import spg.lgdev.uhc.nms.v1_8_R3.NMSControl1_8;
import spg.lgdev.uhc.nms.v1_8_R3.Sit1_8;

public class NMSHandler {

	@Getter
	private static NMSHandler instance;

	@Getter
	private SitHandler sit;
	@Getter
	private BiomeReplacer biomeReplacer;
	@Getter
	private BiomeHandler biomeHandler;
	@Getter
	private NMSControl nMSControl;
	@Getter
	private IFastBlockSet fastBlockSet;

	@Getter
	private final String NMSVersion;

	public NMSHandler(final iUHC plugin) throws NewInstanceRejectException {
		if (instance != null)
			throw new NewInstanceRejectException("NMSHandler");
		instance = this;
		final String packageName = Bukkit.getServer().getClass().getPackage().getName();
		NMSVersion = packageName.substring(packageName.lastIndexOf('.') + 1);
		try {
			loadNMS();
		} catch (final UnsupportedVersionException e) {
			plugin.log(false, "&8[&4&l!ERROR!&8] &c&lYOUR NMS IS NOT compatible WITH PLUGIN ULTIMATEUHC!");
			plugin.log(false, "&8[&4&l!ERROR!&8] &c&lPLEASE USE THE SPIGOT VERSION WE SUPPORTED IF YOU WANT USE ULTIMATEUHC!");
			plugin.log(false, "&6Your version is " + NMSVersion);
			plugin.log(false, "&bThe Version Ultimate UHC Supported:");
			plugin.log(false, "&f- &a&l1_8_R3");
			Bukkit.getPluginManager().disablePlugin(plugin);
			e.printStackTrace();
		}
	}

	private void loadNMS() throws UnsupportedVersionException {
		if (!NMSVersion.equals("v1_7_R4") && !NMSVersion.equals("v1_8_R3"))
			throw new UnsupportedVersionException();
		if (NMSVersion.equals("v1_8_R3")) {
			ServerVersion.set(ServerVersion.v1_8_R3);

			sit = new Sit1_8();
			biomeReplacer = new BiomeReplacer1_8();
			nMSControl = new NMSControl1_8();
			fastBlockSet = new FastBlockSet1_8();
			biomeHandler = new Biomeswap1_8();
		}
		if (NMSVersion.equals("v1_9_R1")) {
			ServerVersion.set(ServerVersion.v1_9_R1);
		}
		if (NMSVersion.equals("v1_9_R2")) {
			ServerVersion.set(ServerVersion.v1_9_R2);
		}
		if (NMSVersion.equals("v1_10_R1")) {
			ServerVersion.set(ServerVersion.v1_10_R1);
		}
		if (NMSVersion.equals("v1_11_R1")) {
			ServerVersion.set(ServerVersion.v1_11_R1);
		}
		if (NMSVersion.equals("v1_12_R1")) {
			ServerVersion.set(ServerVersion.v1_12_R1);
		}
		Bukkit.getConsoleSender().sendMessage("§e§lYour NMS is compatible!");
		Bukkit.getConsoleSender().sendMessage("§fNMS Version: §6§l" + NMSVersion);
	}

}
