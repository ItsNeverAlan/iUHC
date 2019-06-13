package spg.lgdev.uhc.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import lombok.Getter;
import spg.lgdev.uhc.config.CachedConfig;
import net.development.mitw.utils.StringUtil;

@Getter
public class Board {

	private final BoardAdapter adapter;
	private final Player player;
	private final List<BoardEntry> entries = new ArrayList<>();
	private final Set<BoardTimer> timers = new HashSet<>();
	private final Set<String> keys = new HashSet<>();
	private Scoreboard scoreboard;
	private Objective objective;
	private final Map<String, Team> prefixs = new HashMap<String, Team>() {
		private static final long serialVersionUID = 1L;

		@Override
		public Team put(String key, final Team value) {
			key = StringUtil.replace(key, "§", "");
			return super.put(key, value);
		}

		@Override
		public Team get(final Object key) {
			if (key instanceof String) {
				String string = (String) key;
				string = StringUtil.replace(string, "§", "");
				return super.get(string);
			}
			return super.get(key);
		}
	};

	public Board(final JavaPlugin plugin, final Player player, final BoardAdapter adapter) {
		this.adapter = adapter;
		this.player = player;

		this.init(plugin);
	}

	private void init(final JavaPlugin plugin) {
		if (!this.player.getScoreboard().equals(plugin.getServer().getScoreboardManager().getMainScoreboard())) {
			this.scoreboard = this.player.getScoreboard();
		} else {
			this.scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
		}

		this.objective = this.scoreboard.registerNewObjective("Default", "dummy");

		this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		this.objective.setDisplayName(this.adapter.getTitle(player));

		scoreboard.registerNewObjective("tabhealth", "health").setDisplaySlot(DisplaySlot.PLAYER_LIST);

		final Objective belowObjective = scoreboard.registerNewObjective("belowNameHealth", "health");
		belowObjective.setDisplayName("§c§l\u2764");
		belowObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);

		final Team teammate = scoreboard.registerNewTeam("TEAMMATE");
		teammate.setPrefix(CachedConfig.TEAMMATE_TAG);
		prefixs.put(CachedConfig.TEAMMATE_TAG, teammate);

		final Team enemy = scoreboard.registerNewTeam("ENEMY");
		enemy.setPrefix(CachedConfig.ENEMY_TAG);
		prefixs.put(CachedConfig.ENEMY_TAG, enemy);

		final Team staff = scoreboard.registerNewTeam("STAFF");
		staff.setPrefix(CachedConfig.STAFF_TAG);
		prefixs.put(CachedConfig.STAFF_TAG, staff);
	}

	public String getNewKey(final BoardEntry entry) {
		for (final ChatColor color : ChatColor.values()) {
			String colorText = color + "" + ChatColor.WHITE;

			if (entry.getText().length() > 16) {
				final String sub = entry.getText().substring(0, 16);
				colorText = colorText + ChatColor.getLastColors(sub);
			}

			if (!keys.contains(colorText)) {
				keys.add(colorText);
				return colorText;
			}
		}

		throw new IndexOutOfBoundsException("No more keys available!");
	}

	public List<String> getBoardEntriesFormatted() {
		final List<String> toReturn = new ArrayList<>();

		for (final BoardEntry entry : new ArrayList<>(entries)) {
			toReturn.add(entry.getText());
		}

		return toReturn;
	}

	public BoardEntry getByPosition(final int position) {
		for (int i = 0; i < this.entries.size(); i++) {
			if (i == position)
				return this.entries.get(i);
		}

		return null;
	}

	public BoardTimer getCooldown(final String id) {
		for (final BoardTimer cooldown : getTimers()) {
			if (cooldown.getId().equals(id))
				return cooldown;
		}

		return null;
	}

	public Set<BoardTimer> getTimers() {
		this.timers.removeIf(cooldown -> System.currentTimeMillis() >= cooldown.getEnd());
		return this.timers;
	}

	public Objective getTabObjective() {
		return scoreboard.getObjective("tabhealth");
	}

}