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

	public String Colored(final String s) {
		return StringUtil.cc(s);
	}

	@EventHandler
	public void onPlayerDeath(final PlayerDeathEvent e) {

		final Player p = e.getEntity().getPlayer();
		final Player k = e.getEntity().getKiller();

		e.setDeathMessage(null);

		if (GameStatus.notStarted()) {
			practiceDeath(e, p, k);
			return;
		}

		final DamageCause d = e.getEntity().getLastDamageCause() != null ? e.getEntity().getLastDamageCause().getCause() : null;
		final PlayerProfile profile = iUHC.getInstance().getProfileManager().getProfile(p.getUniqueId());

		if (!profile.isPlayerAlive())
			return;

		final UHCPlayerDeathEvent event = new UHCPlayerDeathEvent(p);
		Bukkit.getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			if (!p.isOnline()) {
				game.getOfflineRespawns().add(FastUUID.toString(p.getUniqueId()));
			}
			return;
		}

		game.getWhitelist().remove(p.getUniqueId());
		game.removeCombatTag(p);
		saveDeadData(p, profile);

		Utils.playDeathAnimation(p);

		if (k != null) {

			final PlayerProfile uHCKiller = iUHC.getInstance().getProfileManager().getProfile(k.getUniqueId());

			if (!GameStatus.is(GameStatus.FINISH)) {
				uHCKiller.addKills();
				uHCKiller.addTotalKills();
				if (uHCKiller.getKills() > uHCKiller.getHighestKillStreak()) {
					uHCKiller.setHighestKillStreak(uHCKiller.getKills());
				}
				uHCKiller.addKilled(p);
				final int ratingChanged = iUHC.getRandom().nextInt(10, 15);
				uHCKiller.setElo(uHCKiller.getElo() + ratingChanged);
				uHCKiller.addRatingHistory(new RatingHistory(uHCKiller.getUUID(), ratingChanged, RatingChangeReason.KILL));
				k.sendMessage(Lang.getMsg(k, "eloChanged-Kill").replaceAll("<elo>", uHCKiller.getElo() + "").replaceAll("<eloChanged>", ratingChanged + ""));
			}

			k.setLevel(k.getLevel() + (iUHC.getRandom().nextInt(4) + 1));

			if (TeamManager.getInstance().isTeamsEnabled()) {
				final TeamProfile team = uHCKiller.getTeam();
				team.addKill();
			}

			if (Scenarios.NoClean.isOn()) {
				uHCKiller.enableNoClean();
			}

			if (d != null && d.equals(DamageCause.PROJECTILE)) {

				for (final Player pl : iUHC.getInstance().getServer().getOnlinePlayers()) {
					pl.sendMessage(Lang.getMsg(pl, "DeathMessages.SHOT")
							.replaceAll("<Player>", p.getName())
							.replaceAll("<PlayerKills>", "" + profile.getKills())
							.replaceAll("<Killer>", k.getName())
							.replaceAll("<KillerKills>", uHCKiller.getKills() + ""));
				}

			} else {

				for (final Player pl : iUHC.getInstance().getServer().getOnlinePlayers()) {
					pl.sendMessage(Lang.getMsg(pl, "DeathMessages.KILLED")
							.replaceAll("<Player>", p.getName())
							.replaceAll("<PlayerKills>", "" + profile.getKills())
							.replaceAll("<Killer>", k.getName())
							.replaceAll("<KillerKills>", uHCKiller.getKills() + ""));
				}

			}

		} else if (p.getLastDamageCause() instanceof EntityDamageByEntityEvent) {

			final EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) p.getLastDamageCause();
			final Entity kk = ev.getDamager();
			if (kk.getType().equals(EntityType.ZOMBIE)) {
				for (final Player pl : iUHC.getInstance().getServer().getOnlinePlayers()) {
					pl.sendMessage(Lang.getMsg(pl, "DeathMessages.ZOMBIE")
							.replaceAll("<Player>", p.getName()).replaceAll("<PlayerKills>", "" + profile.getKills()));
				}
			} else if (kk.getType().equals(EntityType.SKELETON)) {
				for (final Player pl : iUHC.getInstance().getServer().getOnlinePlayers()) {
					pl.sendMessage(Lang.getMsg(pl, "DeathMessages.SKELETON")
							.replaceAll("<Player>", p.getName()).replaceAll("<PlayerKills>", "" + profile.getKills()));
				}
			} else if (kk.getType().equals(EntityType.CREEPER)) {
				for (final Player pl : iUHC.getInstance().getServer().getOnlinePlayers()) {
					pl.sendMessage(Lang.getMsg(pl, "DeathMessages.CREEPER")
							.replaceAll("<Player>", p.getName()).replaceAll("<PlayerKills>", "" + profile.getKills()));
				}
			} else if (kk.getType().equals(EntityType.SPIDER)) {
				for (final Player pl : iUHC.getInstance().getServer().getOnlinePlayers()) {
					pl.sendMessage(Lang.getMsg(pl, "DeathMessages.SPIDER")
							.replaceAll("<Player>", p.getName()).replaceAll("<PlayerKills>", "" + profile.getKills()));
				}
			}

		} else {

			if (d != null && d.equals(DamageCause.FALL)) {
				for (final Player pl : iUHC.getInstance().getServer().getOnlinePlayers()) {
					pl.sendMessage(Lang.getMsg(pl, "DeathMessages.FALL")
							.replaceAll("<Player>", p.getName()).replaceAll("<PlayerKills>", "" + profile.getKills()));
				}
			} else if (d != null && d.equals(DamageCause.LAVA)) {
				for (final Player pl : iUHC.getInstance().getServer().getOnlinePlayers()) {
					pl.sendMessage(Lang.getMsg(pl, "DeathMessages.LAVA")
							.replaceAll("<Player>", p.getName()).replaceAll("<PlayerKills>", "" + profile.getKills()));
				}
			} else if (d != null && d.equals(DamageCause.BLOCK_EXPLOSION)) {
				for (final Player pl : iUHC.getInstance().getServer().getOnlinePlayers()) {
					pl.sendMessage(Lang.getMsg(pl, "DeathMessages.EXPLOSION")
							.replaceAll("<Player>", p.getName()).replaceAll("<PlayerKills>", "" + profile.getKills()));
				}
			} else {
				for (final Player pl : iUHC.getInstance().getServer().getOnlinePlayers()) {
					pl.sendMessage(Lang.getMsg(pl, "DeathMessages.UNKOWN")
							.replaceAll("<Player>", p.getName()).replaceAll("<PlayerKills>", "" + profile.getKills()));
				}
			}

		}

		if (!GameStatus.is(GameStatus.FINISH)) {

			game.checkWin();
			profile.addTotalDeaths();
			executeDeathEvent(p, e);
			if (profile.getElo() > CachedConfig.ELOCostRating) {
				final int ratingChanged = iUHC.getRandom().nextInt(-8, -20);
				profile.setElo(profile.getElo() + ratingChanged);
				profile.addRatingHistory(new RatingHistory(profile.getUUID(), ratingChanged, RatingChangeReason.DEATH));
				p.sendMessage(Lang.getMsg(p, "eloChanged-Death").replaceAll("<elo>", profile.getElo() + "").replaceAll("<eloChanged>", ratingChanged + ""));
			}

		}
	}

	public void practiceDeath(final PlayerDeathEvent e, final Player p, final Player k) {
		e.getDrops().clear();
		if (UHCGame.getInstance().getPracticePlayers().contains(p.getUniqueId())) {
			p.setHealth(20.0);
			p.setAllowFlight(true);
			p.setFlying(true);
			p.teleport(new Location(Bukkit.getWorld("UHCArena_practice"), 0, 120, 0));
			PracticeManager.respawn(p);
			if (k == null)
				return;
			Utils.pickupItem(k, new ItemStack(Material.GOLDEN_APPLE, 1));
			k.setHealth(20.0);

			UHCGame.getInstance().getPracticePlayers().stream()
			.map(u -> Bukkit.getPlayer(u))
			.filter(Objects::nonNull)
			.forEach(b -> b.sendMessage(Lang.getMsg(b, "Practice.KilledMessage")
					.replaceAll("<player>", p.getName())
					.replaceAll("<killer>", k.getName())));
		}
	}

	public void saveDeadData(final Player p, final PlayerProfile profile) {

		game.getWhitelist().remove(p.getUniqueId());

		profile.saveData(p);
		profile.getData().setHealth(20.0D);

		iUHC.getInstance().getProfileManager().setSpectator(p, true);
		profile.startActionCountdown();

	}

	public void placeHead(final Player p) {

		final Block blockOne = p.getLocation().getBlock();
		blockOne.setType(Material.NETHER_FENCE);

		final Block block = blockOne.getRelative(BlockFace.UP);
		block.setType(Material.SKULL);

		final Skull skull = (Skull) block.getState();
		skull.setOwner(p.getName());
		skull.update();

		block.setData((byte) 1);
	}

	public void executeDeathEvent(final Player p, final PlayerDeathEvent e) {

		if (Scenarios.TimeBomb.isOn()) {

			final Timebomb timebomb = new Timebomb(p.getName(), p.getLocation());
			timebomb.prepare(e);
			UHCGame.getInstance().getTimebombTask().add(timebomb);

		} else {

			if (Scenarios.LuckyKill.isOn()) {
				p.getWorld().dropItemNaturally(p.getLocation(), new ItemStack(Material.GOLDEN_APPLE, 1));
			}

			if (Scenarios.Barebones.isOn()) {

				if (Scenarios.CutClean.isOn()) {
					p.getWorld().dropItem(p.getLocation(), new ItemStack(Material.DIAMOND, 1));
				} else if (Scenarios.DoubleOres.isOn()) {
					p.getWorld().dropItem(p.getLocation(), new ItemStack(Material.DIAMOND, 2));
				} else if (Scenarios.TripleOres.isOn()) {
					p.getWorld().dropItem(p.getLocation(), new ItemStack(Material.DIAMOND, 3));
				} else {
					p.getWorld().dropItem(p.getLocation(), new ItemStack(Material.DIAMOND, 1));
				}

				p.getWorld().dropItem(p.getLocation(), new ItemStack(Material.GOLDEN_APPLE, 2));
				p.getWorld().dropItem(p.getLocation(), new ItemStack(Material.ARROW, 32));
				p.getWorld().dropItem(p.getLocation(), new ItemStack(Material.STRING, 2));

			}

			if (Scenarios.DiamondLess.isOn()) {
				p.getWorld().dropItem(p.getLocation(), new ItemStack(Material.DIAMOND, 1));
			}

			if (Scenarios.GoldLess.isOn()) {
				p.getWorld().dropItem(p.getLocation(), new ItemStack(Material.GOLD_INGOT, 8));
				p.getWorld().dropItem(p.getLocation(), ScenariosHandler.buildGoldenHead());
			}

			if (UHCGame.getInstance().isGoldenHead()) {
				placeHead(p);
			}

		}

		if (Scenarios.ExtraInventory.isOn()) {

			p.getLocation().add(1.0, 1.0, 0.0).getBlock().setType(Material.CHEST);
			final Chest chestBlock = (Chest) p.getLocation().add(1.0, 1.0, 0.0).getBlock().getState();
			chestBlock.getInventory().setContents(p.getEnderChest().getContents());

		}
	}

	@EventHandler
	public void onEntityDeath(final EntityDeathEvent e) {

		if ((e.getEntity() instanceof Villager)) {

			final Villager entity = (Villager) e.getEntity();
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
			e.getDrops().clear();

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
				WorldUtil.entityDeathDrops(e, 1);
			}
			if (Scenarios.DoubleOres.isOn()) {
				WorldUtil.entityDeathDrops(e, 2);
			}
			if (Scenarios.TripleOres.isOn()) {
				WorldUtil.entityDeathDrops(e, 3);
			}
		}

	}

}
