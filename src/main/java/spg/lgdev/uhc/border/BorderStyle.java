package spg.lgdev.uhc.border;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import lombok.Getter;
import spg.lgdev.uhc.iUHC;

@Getter
public class BorderStyle {

	private final Material visualMaterial;
	private final byte visualData;
	private final Material realMaterial;
	private final byte realData;

	public BorderStyle(final FileConfiguration config) {
		final String path = "Border.Style." + config.getString("Border.defaultStyle") + ".";
		if (config.getString(path + "RealBlock.Material") == null) {
			this.visualMaterial = Material.STAINED_GLASS;
			this.visualData = 3;
			this.realMaterial = Material.BEDROCK;
			this.realData = (byte) 0;
			iUHC.getInstance().log(true, "&cdidnt find the section of the default border style! used the default settings");
			return;
		}
		this.visualMaterial = Material.valueOf(config.getString(path + "VisualBlock.Material"));
		this.visualData = (byte) config.getInt(path + "VisualBlock.DATA");
		this.realMaterial = Material.valueOf(config.getString(path + "RealBlock.Material"));
		this.realData = (byte) config.getInt(path + "RealBlock.DATA");
	}

}
