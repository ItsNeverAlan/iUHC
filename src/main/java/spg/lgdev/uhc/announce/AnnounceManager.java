package spg.lgdev.uhc.announce;

import lombok.Getter;
import spg.lgdev.uhc.iUHC;

@Getter
public class AnnounceManager {

	private final AbstractAnnounce discord;
	private final AbstractAnnounce twitter;

	public AnnounceManager(final iUHC plugin) {
		discord = new DiscordAnnounce(plugin);
		twitter = new TwitterAnnounce(plugin);
	}

	public boolean anyEnabled() {
		return discord.isEnabled() || twitter.isEnabled();
	}

}
