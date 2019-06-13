package spg.lgdev.uhc.util;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import lombok.Getter;
import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.enums.ServerVersion;

public enum UHCSound {

	NOTE_PLING("NOTE_PLING", "BLOCK_NOTE_PLING"),
	NOTE_BASS("NOTE_BASS", "BLOCK_NOTE_BASS"),
	CHICKEN_EGG_POP("CHICKEN_EGG_POP", "ENTITY_CHICKEN_EGG"),
	BURP("BURP", "ENTITY_PLAYER_BURP"),
	ORB_PICKUP("ORB_PICKUP", "ENTITY_EXPERIENCE_ORB_PICKUP"),
	SPLASH2("SPLASH2", "ENTITY_PLAYER_SPLASH"),
	LEVEL_UP("LEVEL_UP", "ENTITY_PLAYER_LEVELUP"),
	WITHER_DEATH("WITHER_DEATH", "ENTITY_WITHER_DEATH"),
	ANVIL_USE("ANVIL_USE", "BLOCK_ANVIL_USE"),
	CAT_MEOW("CAT_MEOW", "ENTITY_CAT_AMBIENT"),
	DRINK("DRINK", "ENTITY_WITCH_DRINK"),
	EAT("EAT", "ENTITY_PLAYER_BURP"),
	NOTE_STICKS("NOTE_STICKS", "BLOCK_NOTE_SNARE"),
	ZOMBIE_WOODBREAK("ZOMBIE_WOODBREAK", "ENTITY_ZOMBIE_BREAK_DOOR_WOOD"),
	WITHER_SPAWN("WITHER_SPAWN", "ENTITY_WITHER_SPAWN"),
	FIREWORK_LARGE_BLAST("FIREWORK_LARGE_BLAST", "ENTITY_FIREWORK_LARGE_BLAST"),
	ENDERMEN_TELEPORT("ENDERMAN_TELEPORT", "ENTITY_ENDERMEN_TELEPORT"),
	CLICK("CLICK", "UI_BUTTON_CLICK"),
	ENDERDRAGON_GROWL("ENDERDRAGON_GROWL", "ENTITY_ENDERDRAGON_GROWL"),
	NULL("NULL", "NULL");

	@Getter
	private Sound sound;

	private final String v1_8;
	private final String v1_9;

	private UHCSound(final String v1_8, final String v1_9) {
		this.v1_8 = v1_8;
		this.v1_9 = v1_9;
	}

	public void playSound(final Player p, final float level, final float level2) {
		if (sound == null)
			return;
		p.playSound(p.getLocation(), sound, level, level2);
	}

	public void playSound(final Player p, final float level) {
		this.playSound(p, level, 1.0F);
	}

	public void playSound(final Player p) {
		this.playSound(p, 1.0F, 1.0F);
	}

	public void playSoundToEveryone(final float level, final float level2) {
		if (sound == null)
			return;
		iUHC.getInstance().getServer().getOnlinePlayers().forEach(p -> playSound(p, level, level2));
	}

	public void playSoundToEveryone() {
		this.playSoundToEveryone(1.0F, 1.0F);
	}

	public static UHCSound fromName(final String name) {
		if (name.equals("false"))
			return UHCSound.NULL;
		for (final UHCSound sound : values()) {
			if (sound.name().equals(name))
				return sound;
		}
		return UHCSound.NULL;
	}

	public static void load() {
		final boolean v1_9 = ServerVersion.isUnder(ServerVersion.get(), ServerVersion.v1_9_R1);
		for (final UHCSound sound : values()) {
			if (sound == UHCSound.NULL) {
				continue;
			}
			sound.sound = Sound.valueOf(v1_9 ? sound.v1_8 : sound.v1_9);
		}
	}

}
