package spg.lgdev.uhc.player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import lombok.Getter;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.manager.TeamManager;
import spg.lgdev.uhc.util.StringUtil;
import spg.lgdev.uhc.util.memory.MemoryLocation;
import net.development.mitw.uuid.UUIDCache;

public class TeamProfile {

	private final Set<UUID> players = new HashSet<>();
	@Getter
	private final Map<UUID, Long> invited = new HashMap<>();

	private final UUID owner;
	private final int id;
	private String teamName;
	private int kills = 0;
	private final Inventory backPack;

	private MemoryLocation scatterLocation;

	public TeamProfile(final Player player, final int n) {
		this.owner = player.getUniqueId();
		this.id = n;
		this.teamName = TeamManager.getInstance().getRandomColor() + "Team" + n;
		this.backPack = Bukkit.createInventory(null, 5 * 9, StringUtil.replace(Lang.getInstance().getMessage("backpack-title"), "<teamName>", this.getTeamName()));
	}

	public Inventory getBackPack() {
		return this.backPack;
	}

	public MemoryLocation getScatterLocation() {
		return this.scatterLocation;
	}

	public void setScatterLocation(final MemoryLocation location) {
		this.scatterLocation = location;
	}

	public Set<UUID> getPlayers() {
		return this.players;
	}

	public Player getOwner() {
		return Bukkit.getPlayer(owner);
	}

	public String getOwnerName() {
		return UUIDCache.getName(owner);
	}

	public UUID getOwnerUUID() {
		return owner;
	}

	public boolean isAlive() {
		return iUHC.getInstance().getProfileManager().profileSet(this.players).stream().anyMatch(profile -> Objects.nonNull(profile) && profile.isOnline() && profile.isPlayerAlive());
	}

	public int getSize() {
		return this.getPlayers().size();
	}

	public int getId() {
		return this.id;
	}

	public String getTeamName() {
		return this.teamName;
	}

	public String setTeamName(final String name) {
		this.teamName = name;
		teamNameChanged();
		return this.teamName;
	}

	public int getKills() {
		return this.kills;
	}

	public void addKill() {
		++this.kills;
	}

	public String getMembers() {
	    return this.players.stream().map(UUIDCache::getName).filter(Objects::nonNull).collect(Collectors.joining(", "));
    }

	public String getMemberHealths() {

		String string = "";

		for (final UUID uuid : this.players) {

			final PlayerProfile profile = iUHC.getInstance().getProfileManager().getProfile(uuid);
			final Player player = profile.getPlayer();

			if (player != null && profile.isOnline() && profile.isPlayerAlive()) {

				string = string + "§a " + UUIDCache.getName(uuid) + "§e(§a" + StringUtil.FORMAT.format(player.getHealth()) + "§c§l\u2764§e)§f,";
				continue;

			}

			string = string + "§c " + UUIDCache.getName(uuid) + "§f,§a";

		}

		return string;
	}

	public void addPlayer(final Player player) {
		this.players.add(player.getUniqueId());
		Player pl;
		for (final UUID uuid : players) {
			pl = Bukkit.getPlayer(uuid);
			if (pl == null) {
				continue;
			}
			pl.sendMessage(Lang.getMsg(pl, "Team.JoinTeam").replace("<player>", player.getName()));
		}
	}

	public void removePlayer(final OfflinePlayer player) {
		this.players.remove(player.getUniqueId());
		Player pl;
		for (final UUID uuid : players) {
			pl = Bukkit.getPlayer(uuid);
			if (pl == null) {
				continue;
			}
			pl.sendMessage(Lang.getMsg(pl, "Team.QuitTeam").replace("<player>", player.getName()));
		}
	}

	public void removePlayer(final UUID uuid) {
		this.players.remove(uuid);
		final String name = UUIDCache.getName(uuid);
		Player pl;
		for (final UUID uuid2 : players) {
			pl = Bukkit.getPlayer(uuid2);
			if (pl == null) {
				continue;
			}
			pl.sendMessage(Lang.getMsg(pl, "Team.QuitTeam").replace("<player>", name));
		}
	}

	void teamNameChanged() {
		this.sendMessage("§aThe name from the team has been changed to §e" + this.getTeamName());
	}

	public void sendMessage(final String string) {
		for (final UUID uUID : this.getPlayers()) {
			final Player player = Bukkit.getPlayer(uUID);
			if (player == null) {
				continue;
			}
			player.sendMessage(string);
		}
	}

	public void sendMessageLanguage(final String string) {
		for (final UUID uUID : this.getPlayers()) {
			final Player player = Bukkit.getPlayer(uUID);
			if (player == null) {
				continue;
			}
			player.sendMessage(Lang.getMsg(player, string));
		}
	}

	public boolean isFull() {
		return this.players.size() >= TeamManager.getInstance().getMaxSize();
	}

}

