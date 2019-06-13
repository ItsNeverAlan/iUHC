package spg.lgdev.uhc.listener;

import java.text.DecimalFormat;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.enums.PlayerStatus;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.manager.TeamManager;
import spg.lgdev.uhc.nms.NMSHandler;
import spg.lgdev.uhc.player.PlayerProfile;
import spg.lgdev.uhc.player.TeamProfile;
import spg.lgdev.uhc.scenario.Scenarios;
import spg.lgdev.uhc.util.StringUtil;
import spg.lgdev.uhc.util.NameTagUtil;
import spg.lgdev.uhc.util.Utils;

public class DamageListener implements org.bukkit.event.Listener {

	private final iUHC plugin;
	private final TeamManager tm;
	private final UHCGame game;
	private final DecimalFormat bowformat = new DecimalFormat("0.0");

	public DamageListener(final iUHC main) {
		this.plugin = main;
		this.tm = TeamManager.getInstance();
		this.game = UHCGame.getInstance();
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamageByEntity(final EntityDamageByEntityEvent e) {

		if (GameStatus.notStarted()) {

			if (e.getEntity() instanceof Player) {

				final Player damaged = (Player) e.getEntity();
				final Player damager = Utils.getDamager(e);

				if (damager != null) {

					if (!game.getPracticePlayers().contains(damaged.getUniqueId())
							|| !game.getPracticePlayers().contains(damager.getUniqueId())) {

						e.setCancelled(true);

						return;

					}

				}

			}
		} else if (GameStatus.is(GameStatus.PVP) || GameStatus.is(GameStatus.DEATHMATCH)
				|| GameStatus.is(GameStatus.FINISH)) {
			if (e.getEntity() instanceof Player) {

				final Player damaged = (Player) e.getEntity();
				final Player damager = Utils.getDamager(e);

				if (damager != null) {

					final PlayerProfile playerProfile = plugin.getProfileManager().getProfile(damaged.getUniqueId());
					final PlayerProfile damagerProfile = plugin.getProfileManager().getProfile(damager.getUniqueId());

					if (!playerProfile.isPlayerAlive() || !playerProfile.isPlayerAlive()) {
						e.setCancelled(true);
						return;
					}
					if (tm.isTeamsEnabled() && !tm.canDamageTeamMembers() && damagerProfile.getTeam() != null
							&& playerProfile.getTeam() != null
							&& damagerProfile.getTeam().getOwnerUUID().equals(playerProfile.getTeam().getOwnerUUID())) {
						e.setCancelled(true);
						return;
					}
					if (playerProfile.isNoClean()) {
						e.setCancelled(true);
						return;
					}
					if (damagerProfile.isNoClean()) {
						damagerProfile.setNoClean(false);
						return;
					}

					damagerProfile.setCombo(playerProfile.getCombo() + 1);
					damagerProfile.setHits(playerProfile.getHits() + 1);
					playerProfile.setCombo(0);

					if (damagerProfile.getCombo() > damagerProfile.getLongestCombo()) {
						damagerProfile.setLongestCombo(damagerProfile.getCombo());
					}

					if (e.getDamager() instanceof Arrow) {

						double damage = e.getFinalDamage();
						double absorptionHealth = NMSHandler.getInstance().getNMSControl()
								.getAbsorptionHearts(((Player) e.getEntity()));

						if (damage - absorptionHealth > 0.0D) {
							absorptionHealth = Math.ceil(absorptionHealth - damage) / 2.0;
							damage = 0;
						} else {
							damage -= absorptionHealth;
							absorptionHealth = 0;
						}

						final double health = Math.ceil(((Player) e.getEntity()).getHealth() - damage) / 2.0D;

						if (health > 0.0D) {
							damager.sendMessage(Lang.getMsg(damager, "game-prefix")
									+ StringUtil.replace(Lang.getMsg(damager, "BowHealth"),
											new StringUtil.ReplaceValue("<Player>", e.getEntity().getName()),
											new StringUtil.ReplaceValue("<PlayerHealth>", bowformat.format(health)),
											new StringUtil.ReplaceValue("<HeartIcon>", "\u2764"),
											new StringUtil.ReplaceValue("<PlayerAbsorptionHealth>",
													bowformat.format(absorptionHealth))));
						}

						damagerProfile.addArrowHit();

					}

				}

				if (e.getDamager() instanceof FishHook) {
					final FishHook hook = (FishHook) e.getDamager();
					if (hook.getShooter() instanceof Player) {

						plugin.getProfileManager().getDebuggedProfile(((Player) hook.getShooter())).addRodHit();
						return;

					}
				}

			} else if (e.getDamager() instanceof Player) {
				final Player d = (Player) e.getDamager();
				if (!plugin.getProfileManager().isAlive(d)) {
					e.setCancelled(true);
					return;
				}
			}
		} else if (GameStatus.is(GameStatus.PVE)) {
			if (e.getDamager() instanceof Player) {
				final Player damager = (Player) e.getDamager();
				if (!plugin.getProfileManager().isAlive(damager)) {
					e.setCancelled(true);
					return;
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void combatDamage(final EntityDamageByEntityEvent e) {
		if (e.isCancelled())
			return;

		if (GameStatus.notStarted())
			return;

		if (e.getEntity() instanceof Player) {

			final Player damaged = (Player) e.getEntity();
			final Player damager = Utils.getDamager(e);

			if (damager != null) {

				final PlayerProfile profile = plugin.getProfileManager().getProfile(damager.getUniqueId());
				final PlayerProfile dprofile = plugin.getProfileManager().getProfile(damaged.getUniqueId());

				if (!profile.isPlayerAlive() || !dprofile.isPlayerAlive()) {
					e.setCancelled(true);
					return;
				}

				if (!game.isPvp()) {
					e.setCancelled(true);
				}

				lafs:
					if (Scenarios.LoveAtFirstSight.isOn()) {
						if (damager.isSneaking()
								&& (damager.getItemInHand() == null || damager.getItemInHand().getType() == Material.AIR)) {
							final TeamProfile dTeam = dprofile.getTeam();
							final TeamProfile team = profile.getTeam();
							if ((dTeam != null && dTeam.isFull()) || (team != null && team.isFull())) {
								break lafs;
							}
							if (profile.getLafsTarget() == damaged.getUniqueId()) {
								TeamManager.getInstance().disbandTeam(dTeam);
								TeamManager.getInstance().registerTeam(damaged, team);
								NameTagUtil.updateTags(damaged, damager);
								NameTagUtil.updateTags(damager, damaged);
								damaged.sendMessage(StringUtil.replace(Lang.getMsg(damaged, "LoveAtFirstSight.TeammateSet"),
										"<player>", damager.getName()));
								damager.sendMessage(StringUtil.replace(Lang.getMsg(damager, "LoveAtFirstSight.TeammateSet"),
										"<player>", damaged.getName()));
							} else {
								dprofile.setLafsTarget(damager.getUniqueId());
								damager.sendMessage(StringUtil.replace(Lang.getMsg(damager, "LoveAtFirstSight.Invited"),
										"<player>", damaged.getName()));
								damaged.sendMessage(StringUtil.replace(Lang.getMsg(damaged, "LoveAtFirstSight.WantToTeam"),
										"<player>", damager.getName()));
							}
							if (game.isPvp())
								return;
						}
					}

				if (!game.isPvp())
					return;

				if (Scenarios.NoCleanPlus.isOn()) {
					if (profile.getFighting() == null && dprofile.getFighting() == null) {
						profile.setFighting(damaged.getUniqueId());
						dprofile.setFighting(damager.getUniqueId());
						damager.sendMessage(
								Lang.getMsg(damager, "NoCleanPlus.Started").replaceAll("<player>", damaged.getName()));
						damaged.sendMessage(
								Lang.getMsg(damaged, "NoCleanPlus.Started").replaceAll("<player>", damager.getName()));
					} else if ((profile.getFighting() != null && !profile.getFighting().equals(damaged.getUniqueId()))
							&& plugin.getCombatTimer().isCooldown(damager)
							|| (dprofile.getFighting() != null && !dprofile.getFighting().equals(damager.getUniqueId())
							&& plugin.getCombatTimer().isCooldown(damaged))) {
						e.setCancelled(true);
						damager.sendMessage(Lang.getMsg(damager, "NoCleanPlus.OnTryToClean"));
						return;
					}
				}

				game.setCombatTag(damager, damaged.getName());
				game.setCombatTag(damaged, damager.getName());

			}

		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityDamage(final EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			final Player p = (Player) e.getEntity();
			if (!plugin.getProfileManager().isAlive(p)) {
				e.setCancelled(true);
				return;
			}
			final PlayerProfile playerProfile = plugin.getProfileManager().getProfile(p.getUniqueId());

			if (playerProfile.getStatus() == PlayerStatus.LOBBY
					&& !game.getPracticePlayers().contains(p.getUniqueId())) {
				e.setCancelled(true);
				return;
			}

			if (GameStatus.notStarted()) {
				if (game.getPracticePlayers().contains(p.getUniqueId())) {
					if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
						e.setCancelled(true);
						return;
					}
				} else {
					if (e.getCause() == EntityDamageEvent.DamageCause.VOID && game.getSpawnPoint() != null) {
						p.teleport(game.getSpawnPoint().toBukkitLocation());
					}
					e.setCancelled(true);
					return;
				}
			} else {
				if ((plugin.getProfileManager().isNoClean(p) || Scenarios.FireLess.isOn())
						&& (e.getCause().equals(DamageCause.FIRE) || e.getCause().equals(DamageCause.FIRE_TICK)
								|| e.getCause().equals(DamageCause.LAVA))) {
					e.setCancelled(true);
					return;
				}
			}
		}

	}
}
