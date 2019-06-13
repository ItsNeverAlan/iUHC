package spg.lgdev.uhc;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import spg.lgdev.uhc.player.PlayerProfile;

@Getter
public class iUHCEngine extends JavaPlugin implements Runnable {

	private int playing;
	private int spectators;

	@Override
	public void run() {

		int playing = 0;
		int spectators = 0;

		PlayerProfile profile;
		for (final Player player : this.getServer().getOnlinePlayers()) {
			profile = iUHC.getInstance().getProfileManager().getProfile(player.getUniqueId());
			if (profile.isPlayerAlive()) {
				playing++;
				continue;
			}
			if (profile.isSpectator()) {
				spectators++;
				continue;
			}
		}

		this.playing = playing;
		this.spectators = spectators;

	}

	public int getPlayingFast() {
		return (int) this.getServer().getOnlinePlayers().stream()
				.map(player -> iUHC.getInstance().getProfileManager().getProfile(player.getUniqueId()))
				.filter(PlayerProfile::isPlayerAlive)
				.count();
	}

}
