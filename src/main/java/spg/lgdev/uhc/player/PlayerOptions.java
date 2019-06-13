package spg.lgdev.uhc.player;

import java.util.UUID;

import lombok.Data;
import net.development.mitw.uuid.UUIDCache;

@Data
public class PlayerOptions {

	private UUID uuid;

	private boolean hideSpectators = true;
	private boolean hideStaffs = true;

	private boolean notifyDiamond = false;
	private boolean notifyGold = false;
	private boolean notifySpawner = false;

	private boolean fly = true;
	private boolean nightVision = false;
	private String speedLevel = "normal";

	public PlayerOptions(final UUID uUID) {
		this.uuid = uUID;
	}

	public String getName() {
		return UUIDCache.getName(uuid);
	}

}
