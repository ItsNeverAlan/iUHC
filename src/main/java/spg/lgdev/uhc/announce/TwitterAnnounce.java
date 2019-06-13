package spg.lgdev.uhc.announce;

import org.bukkit.configuration.file.FileConfiguration;

import lombok.Getter;
import spg.lgdev.uhc.iUHC;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@Getter
public class TwitterAnnounce extends AbstractAnnounce {

	private Twitter twitter;
	private boolean enabled;

	public TwitterAnnounce(final iUHC plugin) {

		final FileConfiguration config = plugin.getFileManager().getConfig();

		if (!(enabled = config.getBoolean("Twitter.Enabled")))
			return;

		try {
			final ConfigurationBuilder builder = new ConfigurationBuilder();

			builder.setDebugEnabled(true);
			builder.setOAuthConsumerKey(config.getString("Twitter.Auth-consumer_key"));
			builder.setOAuthConsumerSecret(config.getString("Twitter.Auth-consumer_secret"));
			builder.setOAuthAccessToken(config.getString("Twitter.Auth-accesstoken"));
			builder.setOAuthAccessTokenSecret(config.getString("Twitter.Auth-accesstoken_secret"));

			twitter = new TwitterFactory(builder.build()).getInstance();

			plugin.log(false, "[DEBUG] Successfull to login the twitter!");
		} catch (final Exception e) {
			e.printStackTrace();
			plugin.log(false, "[DEBUG] Failed to login twitter!");
			enabled = false;
		}

	}

	@Override
	public boolean postAnnounce(final String message) {
		Status status = null;
		try {
			status = twitter.updateStatus(message);
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		}
		assert (status == null);
		return true;
	}

}
