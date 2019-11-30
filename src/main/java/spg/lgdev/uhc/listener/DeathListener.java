package spg.lgdev.uhc.listener;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Skull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import spg.lgdev.uhc.api.events.UHCPlayerDeathEvent;
import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.manager.PracticeManager;
import spg.lgdev.uhc.manager.TeamManager;
import spg.lgdev.uhc.nms.NMSHandler;
import spg.lgdev.uhc.player.PlayerProfile;
import spg.lgdev.uhc.player.TeamProfile;
import spg.lgdev.uhc.player.rating.RatingChangeReason;
import spg.lgdev.uhc.player.rating.RatingHistory;
import spg.lgdev.uhc.scenario.Scenarios;
import spg.lgdev.uhc.scenario.Timebomb;
import spg.lgdev.uhc.util.CustomLocation;
import spg.lgdev.uhc.util.FastUUID;
import spg.lgdev.uhc.util.StringUtil;
import spg.lgdev.uhc.util.Utils;
import spg.lgdev.uhc.util.WorldUtil;
import net.development.mitw.uuid.UUIDCache;

public class DeathListener implements org.bukkit.event.Listener {

	private final UHCGame game;

	public DeathListener(final iUHC plugin) {
		this.game = UHCGame.getInstance();
	}

	@EventHandler
	public void onPlayerDeath(final PlayerDeathEvent event) {

		final Player player = event.getEntity().getPlayer();
		final Player killer = event.getEntity().getKiller();

		event.setDeathMessage(null);

		if (GameStatus.notStarted()) {
			practiceDeath(event, player, killer);
			return;
		}

		final DamageCause damageCause = event.getEntity().getLastDamageCause() != null ? event.getEntity().getLastDamageCause().getCause() : null;
		final PlayerProfile profile = iUHC.getInstance().getProfileManager().getProfile(player.getUniqueId());

		if (!profile.isPlayerAlive())
			return;

		final UHCPlayerDeathEvent uhcEvent = new UHCPlayerDeathEvent(player);
		Bukkit.getPluginManager().callEvent(uhcEvent);

		if (uhcEvent.isCancelled()) {
			if (!player.isOnline()) {
				game.getOfflineRespawns().add(FastUUID.toString(player.getUniqueId()));
			}
			return;
		}

		game.getWhitelist().remove(player.getUniqueId());
		game.removeCombatTag(player);
		saveDeadData(player, profile);

		NMSHandler.getInstance().getNMSControl().showDyingNPC(player);

		if (killer != null) {

			final PlayerProfile killerProfile = iUHC.getInstance().getProfileManager().getProfile(killer.getUniqueId());

			if (!GameStatus.is(GameStatus.FINISH)) {
				killerProfile.addKills();
				killerProfile.addTotalKills();
				if (killerProfile.getKills() > killerProfile.getHighestKillStreak()) {
					killerProfile.setHighestKillStreak(killerProfile.getKills());
				}
				killerProfile.addKilled(player);
				final int ratingChanged = iUHC.getRandom().nextInt(10, 15);
				killerProfile.setElo(killerProfile.getElo() + ratingChanged);
				killerProfile.addRatingHistory(new RatingHistory(killerProfile.getUUID(), ratingChanged, RatingChangeReason.KILL));
				killer.sendMessage(Lang.getMsg(killer, "eloChanged-Kill").replaceAll("<elo>", killerProfile.getElo() + "").replaceAll("<eloChanged>", ratingChanged + ""));
			}

			killer.setLevel(killer.getLevel() + (iUHC.getRandom().nextInt(4) + 1));

			if (TeamManager.getInstance().isTeamsEnabled()) {
				final TeamProfile team = killerProfile.getTeam();
				team.addKill();
			}

			if (Scenarios.NoClean.isOn()) {
				killerProfile.enableNoClean();
			}

			if (damageCause != null && damageCause.equals(DamageCause.PROJECTILE)) {

				for (final Player pl : iUHC.getInstance().getServer().getOnlinePlayers()) {
					pl.sendMessage(Lang.getMsg(pl, "DeathMessages.SHOT")
							.replaceAll("<Player>", player.getName())
							.replaceAll("<PlayerKills>", "" + profile.getKills())
							.replaceAll("<Killer>", killer.getName())
							.replaceAll("<KillerKills>", killerProfile.getKills() + ""));
				}

			} else {

				for (final Player pl : iUHC.getInstance().getServer().getOnlinePlayers()) {
					pl.sendMessage(Lang.getMsg(pl, "DeathMessages.KILLED")
							.replaceAll("<Player>", player.getName())
							.replaceAll("<PlayerKills>", "" + profile.getKills())
							.replaceAll("<Killer>", killer.getName())
							.replaceAll("<KillerKills>", killerProfile.getKills() + ""));
				}

			}

		} else if (player.getLastDamageCause() instanceof EntityDamageByEntityEvent) {

			final EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) player.getLastDamageCause();
			final Entity kk = ev.getDamager();
			if (kk.getType().equals(EntityType.ZOMBIE)) {
				for (final Player pl : iUHC.getInstance().getServer().getOnlinePlayers()) {
					pl.sendMessage(Lang.getMsg(pl, "DeathMessages.ZOMBIE")
							.replaceAll("<Player>", player.getName()).replaceAll("<PlayerKills>", "" + profile.getKills()));
				}
			} else if (kk.getType().equals(EntityType.SKELETON)) {
				for (final Player pl : iUHC.getInstance().getServer().getOnlinePlayers()) {
					pl.sendMessage(Lang.getMsg(pl, "DeathMessages.SKELETON")
							.replaceAll("<Player>", player.getName()).replaceAll("<PlayerKills>", "" + profile.getKills()));
				}
			} else if (kk.getType().equals(EntityType.CREEPER)) {
				for (final Player pl : iUHC.getInstance().getServer().getOnlinePlayers()) {
					pl.sendMessage(Lang.getMsg(pl, "DeathMessages.CREEPER")
							.replaceAll("<Player>", player.getName()).replaceAll("<PlayerKills>", "" + profile.getKills()));
				}
			} else if (kk.getType().equals(EntityType.SPIDER)) {
				for (final Player pl : iUHC.getInstance().getServer().getOnlinePlayers()) {
					pl.sendMessage(Lang.getMsg(pl, "DeathMessages.SPIDER")
							.replaceAll("<Player>", player.getName()).replaceAll("<PlayerKills>", "" + profile.getKills()));
				}
			}

		} else {

			if (damageCause != null && damageCause.equals(DamageCause.FALL)) {
				for (final Player pl : iUHC.getInstance().getServer().getOnlinePlayers()) {
					pl.sendMessage(Lang.getMsg(pl, "DeathMessages.FALL")
							.replaceAll("<Player>", player.getName()).replaceAll("<PlayerKills>", "" + profile.getKills()));
				}
			} else if (damageCause != null && damageCause.equals(DamageCause.LAVA)) {
				for (final Player pl : iUHC.getInstance().getServer().getOnlinePlayers()) {
					pl.sendMessage(Lang.getMsg(pl, "DeathMessages.LAVA")
							.replaceAll("<Player>", player.getName()).replaceAll("<PlayerKills>", "" + profile.getKills()));
				}
			} else if (damageCause != null && damageCause.equals(DamageCause.BLOCK_EXPLOSION)) {
				for (final Player pl : iUHC.getInstance().getServer().getOnlinePlayers()) {
					pl.sendMessage(Lang.getMsg(pl, "DeathMessages.EXPLOSION")
							.replaceAll("<Player>", player.getName()).replaceAll("<PlayerKills>", "" + profile.getKills()));
				}
			} else {
				for (final Player pl : iUHC.getInstance().getServer().getOnlinePlayers()) {
					pl.sendMessage(Lang.getMsg(pl, "DeathMessages.UNKOWN")
							.replaceAll("<Player>", player.getName()).replaceAll("<PlayerKills>", "" + profile.getKills()));
				}
			}

		}

		if (!GameStatus.is(GameStatus.FINISH)) {

			game.checkWin();
			profile.addTotalDeaths();
			executeDeathEvent(player, event);
			if (profile.getElo() > CachedConfig.ELOCostRating) {
				final int ratingChanged = iUHC.getRandom().nextInt(-8, -20);
				profile.setElo(profile.getElo() + ratingChanged);
				profile.addRatingHistory(new RatingHistory(profile.getUUID(), ratingChanged, RatingChangeReason.DEATH));
				player.sendMessage(Lang.getMsg(player, "eloChanged-Death").replaceAll("<elo>", profile.getElo() + "").replaceAll("<eloChanged>", ratingChanged + ""));
			}

		}
	}

	public void practiceDeath(final PlayerDeathEvent event, final Player player, final Player killer) {
		event.getDrops().clear();
		if (UHCGame.getInstance().getPracticePlayers().contains(player.getUniqueId())) {
			player.setHealth(20.0);
			player.setAllowFlight(true);
			player.setFlying(true);
			player.teleport(new Location(Bukkit.getWorld("UHCArena_practice"), 0, 120, 0));
			PracticeManager.respawn(player);
			if (killer == null)
				return;
			Utils.pickupItem(killer, new ItemStack(Material.GOLDEN_APPLE, 1));
			killer.setHealth(20.0);

			UHCGame.getInstance().getPracticePlayers().stream()
			.map(Bukkit::getPlayer)
			.filter(Objects::nonNull)
			.forEach(other -> other.sendMessage(Lang.getMsg(other, "Practice.KilledMessage")
					.replaceAll("<player>", player.getName())
					.replaceAll("<killer>", killer.getName())));
		}
	}

	public void saveDeadData(final Player player, final PlayerProfile profile) {

		game.getWhitelist().remove(player.getUniqueId());

		profile.saveData(player);
		profile.getData().setHealth(20.0D);

		iUHC.getInstance().getProfileManager().setSpectator(player, false);
		profile.startActionCountdown();

	}

	public void placeHead(final Player player) {

		final Block blockFence = player.getLocation().getBlock();
		blockFence.setType(Material.NETHER_FENCE);

		final Block blockSkull = blockFence.getRelative(BlockFace.UP);
		blockSkull.setType(Material.SKULL);

		final Skull skull = (Skull) blockSkull.getState();
		skull.setOwner(player.getName());
		skull.update();

		blockSkull.setData((byte) 1);
	}

	public void executeDeathEvent(final Player player, final PlayerDeathEvent event) {

		if (Scenarios.TimeBomb.isOn()) {

			final Timebomb timebomb = new Timebomb(player.getName(), player.getLocation());
			timebomb.prepare(event);
			UHCGame.getInstance().getTimebombTask().add(timebomb);

		} else {

			if (Scenarios.LuckyKill.isOn()) {
				player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.GOLDEN_APPLE, 1));
			}

			if (Scenarios.Barebones.isOn()) {

				if (Scenarios.CutClean.isOn()) {
					player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.DIAMOND, 1));
				} else if (Scenarios.DoubleOres.isOn()) {
					player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.DIAMOND, 2));
				} else if (Scenarios.TripleOres.isOn()) {
					player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.DIAMOND, 3));
				} else {
					player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.DIAMOND, 1));
				}

				player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.GOLDEN_APPLE, 2));
				player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.ARROW, 32));
				player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.STRING, 2));

			}

			if (Scenarios.DiamondLess.isOn()) {
				player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.DIAMOND, 1));
			}

			if (Scenarios.GoldLess.isOn()) {
				player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.GOLD_INGOT, 8));
				player.getWorld().dropItem(player.getLocation(), ScenariosHandler.buildGoldenHead());
			}

			if (UHCGame.getInstance().isGoldenHead()) {
				placeHead(player);
			}

		}

		if (Scenarios.ExtraInventory.isOn()) {

			player.getLocation().add(1.0, 1.0, 0.0).getBlock().setType(Material.CHEST);
			final Chest chestBlock = (Chest) player.getLocation().add(1.0, 1.0, 0.0).getBlock().getState();
			chestBlock.getInventory().setContents(player.getEnderChest().getContents());

		}
	}

	@EventHandler
	public void onEntityDeath(final EntityDeathEvent event) {

		if ((event.getEntity() instanceof Villager)) {

			final Villager entity = (Villager) event.getEntity();
			if (entity.getCustomName() == null)
				return;

			final Player k = entity.getKiller();
			final UUID uuid = Bukkit.getOfflinePlayer(entity.getCustomName().replace("§c", "")).getUniqueId();
			final PlayerProfile profile = iUHC.getInstance().getProfileManager().getProfile(uuid);

			final ItemStack[] content = profile.getData().getContents();
			final ItemStack[] armor = profile.getData().getArmorContents();

			int spkills = 0;
			if (profile != null) {

				profile.addTotalDeaths();
				spkills = profile.getKills();
				profile.getData().setLocation(CustomLocation.fromBukkitLocation(entity.getLocation()));

			}
			game.getOfflineKicks().remove(FastUUID.toString(uuid));
			if (k != null) {
				final PlayerProfile kp = iUHC.getInstance().getProfileManager().getProfile(k.getUniqueId());
				int kills = 0;
				if (kp != null) {
					kp.addTotalKills();
					kp.addKills();
					kills = kp.getKills();
				}
				for (final Player pl : Bukkit.getOnlinePlayers()) {
					pl.sendMessage(Lang.getMsg(pl, "DeathMessages.KILLED")
							.replace("<Player>", UUIDCache.getName(uuid) + "§7(CombatLogger)").replace("<PlayerKills>", "" + spkills)
							.replaceAll("<Killer>", k.getName())
							.replaceAll("<KillerKills>", kills + ""));
				}
			} else {
				for (final Player pl : Bukkit.getOnlinePlayers()) {
					pl.sendMessage(Lang.getMsg(pl, "DeathMessages.UNKOWN")
							.replace("<Player>", UUIDCache.getName(uuid) + "§7(CombatLogger)").replace("<PlayerKills>", "" + spkills));
				}
			}
			event.getDrops().clear();

			if (content != null) {
				Stream.of(content)
				.filter(item -> Objects.nonNull(item) && !item.getType().equals(Material.AIR))
				.forEach(item -> entity.getWorld().dropItemNaturally(entity.getLocation(), item));
			}
			if (armor != null) {
				Stream.of(armor)
				.filter(item -> Objects.nonNull(item) && !item.getType().equals(Material.AIR))
				.forEach(item -> entity.getWorld().dropItemNaturally(entity.getLocation(), item));
			}

			profile.setLoggerDied(true);
			return;
		} else {
			if (Scenarios.CutClean.isOn()) {
				WorldUtil.entityDeathDrops(event, 1);
			}
			if (Scenarios.DoubleOres.isOn()) {
				WorldUtil.entityDeathDrops(event, 2);
			}
			if (Scenarios.TripleOres.isOn()) {
				WorldUtil.entityDeathDrops(event, 3);
			}
		}

	}

}
