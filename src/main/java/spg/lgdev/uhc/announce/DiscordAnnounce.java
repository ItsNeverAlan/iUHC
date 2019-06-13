package spg.lgdev.uhc.announce;

import spg.lgdev.uhc.iUHC;
import org.bukkit.configuration.file.FileConfiguration;

import lombok.Getter;
import spg.lgdev.uhc.config.CachedConfig;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

@Getter
public class DiscordAnnounce extends AbstractAnnounce {

	private JDA jda;
	private final String channelId;

	private boolean enabled;

	public DiscordAnnounce(final iUHC plugin) {

		final FileConfiguration config = plugin.getFileManager().getConfig();

		enabled = config.getBoolean("Discord.Enabled");
		channelId = config.getString("Discord.ChannelID");

		if (!enabled)
			return;

		try {
			jda = new JDABuilder(AccountType.BOT).setToken(config.getString("Discord.TokenBot")).build();
			jda.addEventListener(new ListenerAdapter() {
				@Override
				public void onReady(final ReadyEvent event) {
					plugin.log(true, "&eDiscord bot has been build!");
					super.onReady(event);
				}
			});
		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean postAnnounce(final String message) {
		if (!enabled)
			return false;
		jda.getTextChannelById(channelId).sendMessage(new EmbedBuilder()
				.setAuthor(CachedConfig.DISCORD_AUTHOR)
				.setTitle(CachedConfig.DISCORD_TITLE)
				.setDescription(message).build())
		.append(CachedConfig.DISCORD_TAG).queue();
		return true;
	}

}
