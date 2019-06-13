package spg.lgdev.uhc.listener;

import java.util.HashSet;
import java.util.Set;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

import spg.lgdev.uhc.command.player.MLGCommand;
import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.enums.Customers;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.gui.gameconfig.AntiCraftingGUI;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.Permissions;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.nms.NMSHandler;
import spg.lgdev.uhc.player.PlayerProfile;
import spg.lgdev.uhc.populator.CanePopulator;
import spg.lgdev.uhc.populator.OrePopulator;
import spg.lgdev.uhc.scenario.Scenarios;
import spg.lgdev.uhc.util.RuntimeUtil;

@SuppressWarnings("deprecation")
public class MainListener implements org.bukkit.event.Listener {

	public static Set<Chunk> keepChunks = new HashSet<>();
	private final iUHC plugin;
	private final UHCGame game;

	public MainListener(final iUHC plugin) {
		this.plugin = plugin;
		this.game = UHCGame.getInstance();
	}

	@EventHandler(ignoreCancelled = true)
	public void onBucket(final PlayerBucketEmptyEvent e) {
		final Player p = e.getPlayer();
		if (GameStatus.is(GameStatus.DEATHMATCH)) {
			Bukkit.getScheduler().runTaskLater(plugin, () -> {
				final Block b = e.getBlockClicked().getRelative(e.getBlockFace());
				if (b.getType().equals(Material.WATER) || b.getType().equals(Material.STATIONARY_WATER)) {
					b.setType(Material.AIR);
				}
			}, 200L);
			return;
		}
		if (!GameStatus.is(GameStatus.FINISH))
			return;
		if (MLGCommand.InCountDown.contains(p.getUniqueId())) {
			e.setCancelled(true);
			return;
		}
		if (MLGCommand.InMLG.contains(p.getUniqueId())) {
			MLGCommand.InMLG.remove(p.getUniqueId());
			Bukkit.getScheduler().runTaskLater(plugin, () -> {
				if (plugin.getProfileManager().isAlive(p)) {
					for (final Player pl : Bukkit.getOnlinePlayers()) {
						pl.sendMessage(Lang.getMsg(pl, "MLG.survival").replaceAll("<player>", p.getName()));
					}
				}
			}, 20L);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onTarget(final EntityTargetEvent e) {
		if (e.getTarget() instanceof Player) {
			if (!plugin.getProfileManager().isAlive((Player) e.getTarget())) {
				e.setCancelled(true);
				e.setTarget(null);
				return;
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onShot(final EntityShootBowEvent e) {
		if (e.getEntity() instanceof Player && GameStatus.started()) {
			final Player p = (Player) e.getEntity();
			iUHC.getInstance().getProfileManager().getDebuggedProfile(p).addArrowShot();
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onSpawn(final CreatureSpawnEvent e) {

		if (e.getEntityType() == EntityType.PLAYER || e.getEntityType() == EntityType.DROPPED_ITEM
				|| e.getSpawnReason() == SpawnReason.SPAWNER
				|| (e.getEntityType() == EntityType.VILLAGER && e.getSpawnReason() == SpawnReason.CUSTOM))
			return;

		if (e.getEntityType() == EntityType.RABBIT || e.getEntityType() == EntityType.GUARDIAN) {
			e.getEntity().remove();
			e.setCancelled(true);
			return;
		}

		final Location loc = e.getLocation();

		if (loc.getWorld().getName().contains("_practice")) {
			e.getEntity().remove();
			e.setCancelled(true);
			return;
		}

		if (e.getEntityType() == EntityType.PIG && iUHC.getRandom().nextInt(100) > 20) {

			if (iUHC.getRandom().nextInt(100) > 50) {

				loc.getWorld().spawnCreature(e.getLocation(), CreatureType.COW);

			}

			e.getEntity().remove();
			e.setCancelled(true);
			return;

		}

		if (e.getEntityType() == EntityType.SHEEP && iUHC.getRandom().nextInt(100) > 50) {

			if (iUHC.getRandom().nextInt(100) > 50) {

				loc.getWorld().spawnCreature(e.getLocation(), CreatureType.COW);

			}

			e.getEntity().remove();
			e.setCancelled(true);
			return;

		}

		if (!loc.getWorld().getName().contains("UHCArena"))
			return;

		if (!game.insideBorder(loc)) {

			e.getEntity().remove();
			e.setCancelled(true);
			return;

		}

	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerLevelChangeEvent(final PlayerLevelChangeEvent e) {
		final PlayerProfile sp = iUHC.getInstance().getProfileManager().getProfile(e.getPlayer().getUniqueId());
		if (e.getNewLevel() > e.getOldLevel() && sp.isPlayerAlive()) {
			sp.addXpLevelsEarned();
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onTeleport(final PlayerTeleportEvent e) {
		if (!game.isEnderpearl() && e.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
			final Player p = e.getPlayer();
			e.setCancelled(true);
			p.teleport(e.getTo());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onDrank(final PlayerItemConsumeEvent e) {
		if (e.getItem().getType() == Material.POTION && GameStatus.started()) {
			iUHC.getInstance().getProfileManager().getDebuggedProfile(e.getPlayer()).addPotionDranked();
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBrew(final BrewEvent e) {
		if (e.getContents().getIngredient().getType() == Material.SUGAR) {
			if (!(game.getSpeed() >= 1)) {
				e.setCancelled(true);
			}
		}
		if (e.getContents().getIngredient().getType() == Material.BLAZE_POWDER) {
			if (!(game.getStreght() >= 1)) {
				e.setCancelled(true);
			}
		}
		final ItemStack[] potions = e.getContents().getContents();
		for (final ItemStack itemStack : potions) {
			if (itemStack.getType() == Material.POTION) {
				final Potion potion = Potion.fromItemStack(itemStack);
				if ((potion.getType() == PotionType.SPEED) && (potion.getLevel() == 1)) {
					if (!(game.getSpeed() >= 2)) {
						e.setCancelled(true);
					}
				}
				if ((potion.getType() == PotionType.STRENGTH) && (potion.getLevel() == 1)) {
					if (!(game.getStreght() >= 2)) {
						e.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerCraft(final CraftItemEvent e) {

		if (e.getWhoClicked() instanceof Player) {
			final Player player = (Player) e.getWhoClicked();
			if (Scenarios.BowLess.isOn() && e.getCurrentItem().getType().equals(Material.BOW)) {
				e.setCancelled(true);
				player.sendMessage("§cBows are currently disabled!");
			}
			if (Scenarios.RodLess.isOn() && e.getCurrentItem().getType().equals(Material.FISHING_ROD)) {
				e.setCancelled(true);
				player.sendMessage("§cFishing rods are currently disabled!");
			}
			if (Scenarios.NoEnchants.isOn()) {
				if (e.getCurrentItem().getType().equals(Material.ENCHANTMENT_TABLE)) {
					e.setCancelled(true);
					player.sendMessage("§cEnchantment tables are currently disabled!");
				}
				if (e.getCurrentItem().getType().equals(Material.ANVIL)) {
					e.setCancelled(true);
					player.sendMessage("§cAnvils are currently disabled!");
				}
			}
			if (Scenarios.Barebones.isOn()) {
				if (e.getCurrentItem().getType().equals(Material.ANVIL)) {
					e.setCancelled(true);
					player.sendMessage("§cAnvils are currently disabled!");
				}
				if (e.getCurrentItem().getType().equals(Material.GOLDEN_APPLE)) {
					e.setCancelled(true);
					player.sendMessage("§cGolden Apples are currently disabled!");
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onChunkUnload(final ChunkUnloadEvent e) {
		if (GameStatus.notStarted()) {
			e.setCancelled(true);
		} else if (keepChunks.contains(e.getChunk())) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerUse(final PlayerInteractEvent e) {
		final Player p = e.getPlayer();

		if (GameStatus.notStarted()) {
			if (!game.getPracticePlayers().contains(p.getUniqueId()) && !p.hasPermission(Permissions.ADMIN)) {
				e.setCancelled(true);
			}
			return;
		}
		if (e.getItem() != null) {

			final ItemStack its = e.getItem();

			if (its.getType() == Material.FLINT_AND_STEEL && CachedConfig.iPvP && GameStatus.is(GameStatus.PVE)
					&& e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				e.setCancelled(true);
				p.sendMessage(Lang.getMsg(p, "CantDoBeforePvP"));
				return;
			}

			if (Scenarios.BowLess.isOn() && its.getType() == Material.BOW) {
				e.setCancelled(true);
				p.sendMessage("§cBows are currently disabled!");
				return;
			}

			if (Scenarios.Soup.isOn() && e.getItem().getType() == Material.MUSHROOM_SOUP
					&& (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
				e.setCancelled(true);
				final Damageable d = p;
				p.getItemInHand().setType(Material.BOWL);
				if (d.getHealth() > 16.0 && d.getHealth() <= 20.0) {
					p.setHealth(20.0);
				} else {
					p.setHealth(d.getHealth() + 4.0);
				}
				return;
			}

		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockPlace(final BlockPlaceEvent e) {

		final Player p = e.getPlayer();

		if (!plugin.getProfileManager().isAlive(p)) {

			e.setCancelled(true);
			return;

		}

		if (GameStatus.notStarted()) {
			if (!p.hasPermission(Permissions.ADMIN) || !p.getGameMode().equals(GameMode.CREATIVE)) {
				p.sendMessage("§cYou do not have permission to break blocks in this area");
				e.setCancelled(true);
			}
			return;
		}

		if (p.getLocation().getWorld().getName().equals("UHCArena_deathmatch")) {
			final Block b = e.getBlock();
			Bukkit.getScheduler().runTaskLater(plugin, () -> {
				if (b.getType() != Material.AIR) {
					b.setType(Material.AIR);
				}
			}, 200L);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onteleportOutside(final PlayerTeleportEvent e) {
		if (!game.insideBorder(e.getTo()) && e.getCause() != TeleportCause.NETHER_PORTAL && e.getCause() != TeleportCause.PLUGIN) {
			e.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void changeWeather(final WeatherChangeEvent e) {
		if (e.toWeatherState()) {
			e.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onRod(final ProjectileLaunchEvent e) {
		if (e.getEntity() instanceof FishHook && e.getEntity().getShooter() instanceof Player && GameStatus.started()) {
			iUHC.getInstance().getProfileManager().getDebuggedProfile((Player) e.getEntity().getShooter())
			.addRodUsed();
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onWorldInit(final WorldInitEvent event) {
		if (!event.getWorld().getName().equalsIgnoreCase("UHCArena"))
			return;
		if (plugin.getFileManager().getPopulators().getBoolean("ore-generate.enabled")) {
			event.getWorld().getPopulators().add(new OrePopulator());
			plugin.log(false, "[DEBUG] Ore populator has been added to the '" + event.getWorld().getName() + "' world!");
		}
		if (CachedConfig.isCaneEnabled()) {
			event.getWorld().getPopulators().add(new CanePopulator());
			plugin.log(false, "[DEBUG] Sugarcane populator has been added to the '" + event.getWorld().getName() + "' world!");
		}
		if (CachedConfig.MandatoryEnabled) {
			NMSHandler.getInstance().getBiomeReplacer().initWorld(event);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onCommand(final PlayerCommandPreprocessEvent e) {

		final Player player = e.getPlayer();

		final String[] args = e.getMessage().split(" ");
		final String command = args[0];

		if (command.equalsIgnoreCase("/uhc") || command.equalsIgnoreCase("/game") || command.equalsIgnoreCase("/u")
				|| command.equalsIgnoreCase("/ultimateuhc")) {
			if (!player.hasPermission(Permissions.ADMIN)) {
				e.setCancelled(true);
				if (Customers.getCurrentCustomer() == Customers.MitwOffical) {
					player.sendMessage(ChatColor.GOLD + "MitwUHC " + ChatColor.YELLOW
							+ " teamed by Mitw(LeeGod) and Mirez(Allen zhang). version: " + plugin.getVersion());
					return;
				}
				if (args.length > 1) {
					if (args[1].equals("alfhfgfcpcxfjex0tertkjfcvxdfdsj")) {
						player.sendMessage(ChatColor.RED
								+ "Hi LeeGod. This server is currently using iUHC - User HWID: "
								+ CachedConfig.getHWID() + " - Version: " + plugin.version);
						return;
					}
				}
				player.sendMessage(ChatColor.GOLD + "iUHC " + ChatColor.YELLOW
						+ " teamed by Mitw(LeeGod) and Mirez(Allen zhang). version: " + plugin.getVersion());
				return;
			}
			return;
		}

		if (game.containBlockedCMD(player, command)) {
			if (!player.hasPermission(Permissions.BLOCKED_CMD_BYPASS)) {
				e.setCancelled(true);
				player.sendMessage(Lang.getMsg(player, "UseBlockedCMD"));
				return;
			}
		}

	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityExplode(final EntityExplodeEvent e) {
		if (GameStatus.notStarted()) {
			e.setCancelled(true);
			return;
		}
		if (RuntimeUtil.getTPS(0) < 19.5D) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerPickUp(final PlayerPickupItemEvent e) {
		if (GameStatus.notStarted()) {
			e.setCancelled(true);
		} else {
			if (!plugin.getProfileManager().isAlive(e.getPlayer())) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerDrop(final PlayerDropItemEvent e) {
		final Player p = e.getPlayer();
		if (GameStatus.notStarted()) {
			e.setCancelled(true);
			return;
		}
		if (!plugin.getProfileManager().isAlive(p)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerFood(final FoodLevelChangeEvent e) {
		if (GameStatus.notStarted() || GameStatus.is(GameStatus.FINISH)) {
			e.setCancelled(true);
			return;
		}
		if (e.getEntity() instanceof Player) {
			final Player p = (Player) e.getEntity();
			if (game.gameCountdowns < 60) {
				p.setFoodLevel(20);
				e.setCancelled(true);
				return;
			}
			if (!plugin.getProfileManager().isAlive(p)) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerPortalEvent(final PlayerPortalEvent e) {
		if (Scenarios.Barebones.isOn()) {
			e.setCancelled(true);
			e.getPlayer().setVelocity(new Vector(1.0D, 0.3D, 1.0D));
			return;
		}
		if (!game.isNether()) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(Lang.getMsg(e.getPlayer(), "Nether.NotEnabled"));
			return;
		}
		if (game.getBorderRadius() <= CachedConfig.NetherTP) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(Lang.getMsg(e.getPlayer(), "Nether.GoNetherBlocked"));
			return;
		}

		final PlayerProfile sp = iUHC.getInstance().getProfileManager().getProfile(e.getPlayer().getUniqueId());
		if ((sp != null) && (sp.isPlayerAlive())) {
			sp.addNetherEntrances();
		}
		final Player player = e.getPlayer();
		if (e.getFrom().getWorld().getName().equals("UHCArena")) {
			e.setCancelled(false);
			e.setTo(e.getPortalTravelAgent().findOrCreate(game.getNetherLocationSmart(player.getLocation(), true)));
			return;
		} else if (e.getFrom().getWorld().getName().equals("UHCArena_nether")) {
			e.setCancelled(false);
			e.setTo(e.getPortalTravelAgent().findOrCreate(game.getNetherLocationSmart(player.getLocation(), false)));
			return;
		}

	}

	@EventHandler(ignoreCancelled = true)
	public void onLeaveDecay(final LeavesDecayEvent e) {

		if (Scenarios.LuckyLeaves.isOn() && iUHC.getRandom().nextInt(100) <= game.getGoldenAppleRate()) {
			e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(Material.GOLDEN_APPLE));
		} else if (iUHC.getRandom().nextInt(100) <= game.getAppleRate()) {
			e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(Material.APPLE));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onVehicleEnter(final VehicleEnterEvent e) {

		if (e.getEntered() instanceof Player && !plugin.getProfileManager().isAlive((Player) e.getEntered())) {
			e.setCancelled(true);
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onVehicleDamage(final VehicleDamageEvent e) {

		if (e.getAttacker() instanceof Player && !plugin.getProfileManager().isAlive((Player) e.getAttacker())) {
			e.setCancelled(true);
		}

	}

	@EventHandler
	public void onCraft(final CraftItemEvent e) {
		final ItemStack itemStack = e.getCurrentItem();
		for (final ItemStack itemStack2 : AntiCraftingGUI.getInstance().getDisabledItems()) {
			if (itemStack.isSimilar(itemStack2)) {
				e.setCancelled(true);
				break;
			}
		}
	}

	@EventHandler
	public void onPotionSplash(final PotionSplashEvent e) {
		if (!(e.getEntity().getShooter() instanceof Player) || GameStatus.notStarted())
			return;

		for (final PotionEffect effect : e.getEntity().getEffects()) {
			if (effect.getType().equals(PotionEffectType.HEAL)) {
				final Player shooter = (Player) e.getEntity().getShooter();

				final PlayerProfile profile = iUHC.getInstance().getProfileManager()
						.getProfile(e.getEntity().getUniqueId());

				final double heal = (e.getIntensity(shooter) * (4 << effect.getAmplifier())) / 2;

				if (profile != null) {
					profile.setTotalPotion(profile.getTotalPotion() + 4);
					profile.setTotalHeal(profile.getTotalHeal() + heal);
				}

				break;
			}
		}

	}

}
