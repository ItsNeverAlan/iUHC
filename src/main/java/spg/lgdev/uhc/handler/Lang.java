package spg.lgdev.uhc.handler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import spg.lgdev.uhc.iUHC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.util.StringUtil;
import spg.lgdev.uhc.util.UHCSound;
import net.development.mitw.Mitw;

public class Lang {

	private static Lang instance;
	private static String defaultLanguage;

	private final Map<String, String> messages = new HashMap<>();
	private final Map<String, String[]> messageArray = new HashMap<>();
	private final Map<String, List<String>> sidebar = new HashMap<>();

	public static Lang getInstance() {
		if (instance == null) {
			instance = new Lang();
		}
		return instance;
	}

	public static String getDefaultLanguage() {
		if (defaultLanguage == null) {
			defaultLanguage = iUHC.getInstance().getFileManager().getConfig().getString("Language");
		}
		return defaultLanguage;
	}

	public static String getMsg(final Player p, final String message) {
		return getInstance().getMessage(p, message);
	}

	public static String getMsg(final CommandSender sender, final String message) {
		if (sender instanceof Player)
			return getInstance().getMessage((Player) sender, message);
		return getInstance().getMessage(message);
	}

	public static String getMsg(final UUID uuid, final String message) {
		return getInstance().getMessage(uuid, message);
	}

	public static String getLang(final Player p) {
		return Mitw.getInstance().getLanguageData().getLang(p);
	}

	public void reloadMessages() {
		messages.clear();
		sidebar.clear();
	}

	public String getMessage(final Player p, final String message) {
		final String from = Mitw.getInstance().getLanguageData().getLang(p) + "." + message;
		if (messages.containsKey(from))
			return messages.get(from);
		String to = CachedConfig.getLanguage().getString(from);
		final boolean found = to != null;
		if (!found) {
			to = CachedConfig.getLanguage().getString(getDefaultLanguage() + "." + message);
		} else {
			to = StringUtil.cc(to);
		}
		if (found) {
			messages.put(from, to);
		}
		return to;
	}

	public String getMessage(final UUID uuid, final String message) {
		final String from = Mitw.getInstance().getLanguageData().getLang(uuid) + "." + message;
		if (messages.containsKey(from))
			return messages.get(from);
		String to = CachedConfig.getLanguage().getString(from);
		final boolean found = to != null;
		if (!found) {
			to = CachedConfig.getLanguage().getString(getDefaultLanguage() + "." + message);
		} else {
			to = StringUtil.cc(to);
		}
		if (found) {
			messages.put(from, to);
		}
		return to;
	}

	public String getSidebarLine(final Player p, final String message) {
		final String from = "UHC-Scoreboard." + Mitw.getInstance().getLanguageData().getLang(p) + "." + message;
		if (messages.containsKey(from))
			return messages.get(from);
		String to = CachedConfig.getBoard().getString(from);
		final boolean found = to != null;
		if (!found) {
			to = CachedConfig.getBoard().getString("UHCScoreboard." + getDefaultLanguage() + "." + message);
		} else {
			to = StringUtil.cc(to);
		}
		if (found) {
			messages.put(from, to);
		}
		return to;
	}

	public List<String> getSidebarList(final Player p, final String message) {
		final String from = "UHC-Scoreboard." + Mitw.getInstance().getLanguageData().getLang(p) + "." + message;
		if (sidebar.containsKey(from))
			return sidebar.get(from);
		final List<String> check = CachedConfig.getBoard().getStringList(from);
		final boolean found = check != null;
		if (!found)
			return Arrays.asList("null");
		if (found) {
			sidebar.put(from, check);
		}
		return check;
	}

	public String getMessage(final String message) {
		final String from = getDefaultLanguage() + "." + message;
		if (messages.containsKey(from))
			return messages.get(from);
		String to = CachedConfig.getLanguage().getString(from);
		final boolean found = to != null;
		if (!found) {
			to = CachedConfig.getLanguage().getString(getDefaultLanguage() + "." + message);
		} else {
			to = StringUtil.cc(to);
		}
		if (found) {
			messages.put(from, to);
		}
		return to;
	}

	public String[] getMessageList(final Player p, final String message) {
		final String from = Mitw.getInstance().getLanguageData().getLang(p) + "." + message;
		if (messageArray.containsKey(from))
			return messageArray.get(from);
		final List<String> check = CachedConfig.getLanguage().getStringList(from);
		final boolean found = check != null;
		if (!found)
			return new String[] { "null" };
		final String[] to = new String[check == null ? 1 : check.size()];
		for (int i = 0; i < to.length; i++) {
			to[i] = StringUtil.cc(check.get(i));
		}
		if (found) {
			messageArray.put(from, to);
		}
		return to;
	}

	public void broadCast(final String message) {
		for (final Player p : iUHC.getInstance().getServer().getOnlinePlayers()) {
			p.sendMessage(getMessage(p, "game-prefix") + getMessage(p, message));
		}
	}

	public void broadCastWithSound(final String message, final UHCSound sound) {
		for (final Player p : iUHC.getInstance().getServer().getOnlinePlayers()) {
			p.sendMessage(getMessage(p, "game-prefix") + getMessage(p, message));
			sound.playSound(p);
		}
	}

	public void broadCastList(final String message) {
		for (final Player p : iUHC.getInstance().getServer().getOnlinePlayers()) {
			for (final String msg : getMessageList(p, message)) {
				p.sendMessage(getMessage(p, "game-prefix") + msg);
			}
		}
	}

	public void broadCastListWithSound(final String message, final UHCSound sound) {
		for (final Player p : iUHC.getInstance().getServer().getOnlinePlayers()) {
			for (final String msg : getMessageList(p, message)) {
				p.sendMessage(getMessage(p, "game-prefix") + msg);
			}
			sound.playSound(p);
		}
	}

}
