package spg.lgdev.uhc.listener;

import static spg.lgdev.uhc.scenario.Scenarios.Barebones;
import static spg.lgdev.uhc.scenario.Scenarios.CutClean;
import static spg.lgdev.uhc.scenario.Scenarios.DiamondLess;
import static spg.lgdev.uhc.scenario.Scenarios.DoubleOres;
import static spg.lgdev.uhc.scenario.Scenarios.FlowerPower;
import static spg.lgdev.uhc.scenario.Scenarios.GoldLess;
import static spg.lgdev.uhc.scenario.Scenarios.LuckyLeaves;
import static spg.lgdev.uhc.scenario.Scenarios.Timber;
import static spg.lgdev.uhc.scenario.Scenarios.TripleOres;
import static spg.lgdev.uhc.scenario.Scenarios.VeinMiners;
import static spg.lgdev.uhc.scenario.Scenarios.bloodDiamonds;
import static spg.lgdev.uhc.scenario.Scenarios.bloodGold;

import java.util.UUID;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.handler.Library;
import spg.lgdev.uhc.handler.Permissions;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.player.PlayerProfile;
import spg.lgdev.uhc.scenario.VeinMining;
import spg.lgdev.uhc.util.StringUtil;
import spg.lgdev.uhc.util.Utils;
import spg.lgdev.uhc.util.WorldUtil;

public class BlockListener implements Listener {

	private final short AREA[][] = {
			{-1, -1, -1}, {0, -1, -1}, {1, -1, -1},
			{-1, -1, 0}, {0, -1, 0}, {1, -1, 0},
			{-1, -1, 1}, {0, -1, 1}, {1, -1, 1},

			{-1, 0, -1}, {0, 0, -1}, {1, 0, -1},
			{-1, 0, 0}, {1, 0, 0},
			{-1, 0, 1}, {0, 0, 1}, {1, 0, 1},

			{-1, 1, -1}, {0, 1, -1}, {1, 1, -1},
			{-1, 1, 0}, {0, 1, 0}, {1, 1, 0},
			{-1, 1, 1}, {0, 1, 1}, {1, 1, 1},
	};

	public static void checkOre(final BlockBreakEvent event, final int count) {
		final PlayerProfile uHCPlayer = iUHC.getInstance().getProfileManager().getProfile(event.getPlayer().getUniqueId());
		final Material itemInHand = event.getPlayer().getItemInHand().getType();
		switch (event.getBlock().getType()) {
		case DIAMOND_ORE:
			if ((itemInHand != Material.IRON_PICKAXE && itemInHand != Material.DIAMOND_PICKAXE) || DiamondLess.isOn()) {
				break;
			}
			WorldUtil.dropBlockbreaks(event, Material.DIAMOND, count, 4 * count, true);
			break;
		case GOLD_ORE:
			if ((itemInHand != Material.IRON_PICKAXE && itemInHand != Material.DIAMOND_PICKAXE) || GoldLess.isOn()) {
				break;
			}
			WorldUtil.dropBlockbreaks(event, Material.GOLD_INGOT, count, 2 * count, true);
			break;
		case GRAVEL:
			WorldUtil.dropBlockbreaks(event, Material.FLINT, count, count * 2, false);
			break;
		case IRON_ORE:
			WorldUtil.dropBlockbreaks(event, Material.IRON_INGOT, count, count, true);
			uHCPlayer.addTotalIronMined();
			break;
		case COAL_ORE:
			uHCPlayer.addTotalCoalMined();
			break;
		case OBSIDIAN:
			if (itemInHand != Material.DIAMOND_PICKAXE) {
				break;
			}
			WorldUtil.dropBlockbreaks(event, Material.OBSIDIAN, count, 0, false);
			break;
		default:
			break;
		}
	}

	public static Material normalCheckOre(final Block block) {
		if (CutClean.isOn() || DoubleOres.isOn() || TripleOres.isOn()) {
			if (block.getType().equals(Material.DIAMOND_ORE))
				return Material.DIAMOND;
			if (block.getType().equals(Material.GOLD_ORE))
				return Material.GOLD_INGOT;
			if (block.getType().equals(Material.GRAVEL))
				return Material.FLINT;
			if (block.getType().equals(Material.IRON_ORE))
				return Material.IRON_INGOT;
			if (block.getType().equals(Material.REDSTONE_ORE)) {
				WorldUtil.dropExperience(block.getLocation(), 3);
				return Material.REDSTONE;
			}
			if (block.getType().equals(Material.GLOWING_REDSTONE_ORE))
				return Material.REDSTONE;
			if (block.getType().equals(Material.QUARTZ_BLOCK))
				return Material.QUARTZ;
			if (block.getType().equals(Material.LAPIS_ORE))
				return Material.LAPIS_BLOCK;
			if (block.getType().equals(Material.COAL_ORE))
				return Material.COAL;
		} else {
			if (block.getType().equals(Material.DIAMOND_ORE))
				return Material.DIAMOND;
			if (block.getType().equals(Material.GOLD_ORE))
				return Material.GOLD_ORE;
			if (block.getType().equals(Material.GRAVEL))
				return Material.FLINT;
			if (block.getType().equals(Material.IRON_ORE))
				return Material.IRON_ORE;
			if (block.getType().equals(Material.REDSTONE_ORE))
				return Material.REDSTONE;
			if (block.getType().equals(Material.GLOWING_REDSTONE_ORE))
				return Material.REDSTONE;
			if (block.getType().equals(Material.QUARTZ_BLOCK))
				return Material.QUARTZ;
			if (block.getType().equals(Material.LAPIS_ORE))
				return Material.LAPIS_BLOCK;
			if (block.getType().equals(Material.COAL_ORE))
				return Material.COAL;
		}
		return Material.IRON_ORE;
	}

	public static int getXP(final Material material, final int i) {
		switch (material) {
		case DIAMOND_ORE:
			return i * 4;
		case REDSTONE_ORE:
			return i;
		case GOLD_ORE:
			if (CutClean.isOn() || DoubleOres.isOn() || TripleOres.isOn())
				return i * 2;
			return 0;
		case IRON_ORE:
			if (CutClean.isOn() || DoubleOres.isOn() || TripleOres.isOn())
				return i;
			return 0;
		default:
			break;
		}
		return 0;
	}

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true)
	public void onBreak(final BlockBreakEvent e) {

		final Player p = e.getPlayer();

		if (GameStatus.notStarted()) {

			if (!p.hasPermission(Permissions.ADMIN) || !p.getGameMode().equals(GameMode.CREATIVE)) {

				p.sendMessage("§cYou do not have permission to break blocks in this area");
				e.setCancelled(true);
				return;

			}

			return;
		}

		if (!iUHC.getInstance().getProfileManager().isAlive(p)) {
			e.setCancelled(true);
			return;
		}

		if (!p.getWorld().getName().contains("UHCArena")) {
			UHCGame.getInstance().ScatterInGame(p, true);
			e.setCancelled(true);
			return;
		}

		final Material mat = e.getBlock().getType();

		if (UHCGame.getInstance().isShears() && p.getItemInHand().getType().equals(Material.SHEARS)
				&& mat.equals(Material.LEAVES)
				|| mat.equals(Material.LEAVES_2)) {

			if (LuckyLeaves.isOn() && iUHC.getRandom().nextInt(100) <= UHCGame.getInstance().getGoldenAppleRate()) {
				WorldUtil.dropBlockbreaks(e, Material.GOLDEN_APPLE, 1, 0, false);
			}

			if (iUHC.getRandom().nextInt(100) <= UHCGame.getInstance().getAppleRate()) {
				WorldUtil.dropBlockbreaks(e, Material.APPLE, 1, 0, false);
			}

			return;
		}

		if ((mat == Material.YELLOW_FLOWER || mat == Material.RED_ROSE) && FlowerPower.isOn()) {
			e.setCancelled(true);

			e.getBlock().setType(Material.AIR);

			final ItemStack[] drops = UHCGame.getInstance().getFlowerPowerDrops();
			final ItemStack itemStack = drops[iUHC.getRandom().nextInt(drops.length)];

			if (itemStack != null && itemStack.getType() != Material.AIR && itemStack.getAmount() != 0) {
				e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), drops[iUHC.getRandom().nextInt(drops.length)]);
			}
		}

		final PlayerProfile playerProfile = iUHC.getInstance().getProfileManager().getProfile(p.getUniqueId());

		if (Timber.isOn() && (mat.equals(Material.LOG) || mat.equals(Material.LOG_2))) {
			Block b1 = e.getBlock().getRelative(BlockFace.UP);
			e.setCancelled(true);
			e.getBlock().setType(Material.AIR);

			int count = 1;

			while (b1.getType().equals(Material.LOG) || b1.getType().equals(Material.LOG_2)) {
				b1.setType(Material.AIR);
				b1 = b1.getRelative(BlockFace.UP);
				count++;
			}

			b1 = e.getBlock().getRelative(BlockFace.DOWN);

			while (b1.getType().equals(Material.LOG) || b1.getType().equals(Material.LOG_2)) {
				b1.setType(Material.AIR);
				b1 = b1.getRelative(BlockFace.DOWN);
				count++;
			}

			Utils.pickupItem(p, new ItemStack(Material.LOG, count));
		}

		if (mat.name().contains("ORE")) {

			final Block b = e.getBlock();

			if (DiamondLess.isOn()) {
				if (b.getType() == Material.DIAMOND_ORE) {
					e.setCancelled(true);
					b.getLocation().getBlock().setType(Material.AIR);
					b.getWorld().spawn(b.getLocation(), ExperienceOrb.class).setExperience(4);
					return;
				}
			}

			if (GoldLess.isOn()) {
				if (b.getType() == Material.GOLD_ORE) {
					e.setCancelled(true);
					b.getLocation().getBlock().setType(Material.AIR);
					b.getWorld().spawn(b.getLocation(), ExperienceOrb.class).setExperience(2);
					return;
				}
			}

			alertMined(p, mat, playerProfile);

			if (bloodDiamonds.isOn()) {
				if (b.getType() == Material.DIAMOND_ORE) {
					e.getPlayer().damage(1.0);
				}
			}
			if (bloodGold.isOn()) {
				if (b.getType() == Material.GOLD_ORE) {
					e.getPlayer().damage(1.0);
				}
			}

			if (VeinMiners.isOn() && p.isSneaking()
					&& (mat.equals(Material.IRON_ORE) || mat.equals(Material.GOLD_ORE) || mat.equals(Material.DIAMOND_ORE))) {

				final ItemStack item = p.getItemInHand();
				e.setCancelled(true);
				if (item.getType().name().contains("PICKUP")) {
					if (item.getDurability() >= item.getType().getMaxDurability()) {
						p.setItemInHand(new ItemStack(Material.AIR));
					} else {
						p.setItemInHand(item);
					}
				}

				final int i = TripleOres.isOn() ? 3 : DoubleOres.isOn() ? 2 : 1;

				final VeinMining veinMining = new VeinMining(normalCheckOre(b), i, getXP(mat, i));
				veinminer(p, e.getBlock(), mat, veinMining);
				veinMining.giveDrops(p);
				return;
			}

			if (Barebones.isOn() && mat.name().contains("ORE")
					&& !mat.equals(Material.COAL_ORE) && !mat.equals(Material.IRON_ORE)) {

				e.setCancelled(true);

				if (CutClean.isOn() || DoubleOres.isOn() || TripleOres.isOn()) {
					WorldUtil.dropBlockbreaks(e, Material.IRON_INGOT, TripleOres.isOn() ? 3 : DoubleOres.isOn() ? 2 : 1
							, TripleOres.isOn() ? 12 : DoubleOres.isOn() ? 8 : 4, true);
				}

				WorldUtil.dropBlockbreaks(e, Material.IRON_ORE, TripleOres.isOn() ? 3 : DoubleOres.isOn() ? 2 : 1
						, TripleOres.isOn() ? 12 : DoubleOres.isOn() ? 8 : 4, true);

			}

			if (CutClean.isOn() || DoubleOres.isOn() || TripleOres.isOn()) {
				checkOre(e, TripleOres.isOn() ? 3 : DoubleOres.isOn() ? 2 : 1);
			}

			return;

		}

		if (CutClean.isOn() || DoubleOres.isOn() || TripleOres.isOn()) {
			checkOre(e, TripleOres.isOn() ? 3 : DoubleOres.isOn() ? 2 : 1);
		}

	}

	private void veinminer(final Player p, final Block block, final Material mainType, final VeinMining veinMining) {
		if (block.getType().equals(Material.AIR))
			return;
		block.setType(Material.AIR);
		veinMining.addtoDrops();
		for (final short[] areas : AREA) {
			final Block block1 = block.getWorld().getBlockAt(block.getX() + areas[0], block.getY() + areas[1], block.getZ() + areas[2]);
			if (mainType == block1.getType()) {
				veinminer(p, block1, mainType, veinMining);
			}
		}
	}

	public void alertMined(final Player p, final Material mat, final PlayerProfile uHCPlayer) {
		switch (mat) {
		case DIAMOND_ORE:
			uHCPlayer.addDiamond();
			uHCPlayer.addTotalDiamondsMined();
			final int diaAmount = uHCPlayer.getDiamond();
			for (final UUID uuid : UHCGame.getInstance().getMods()) {
				final Player on = Bukkit.getPlayer(uuid);
				if (on != null && Library.getPlayerData(on).getOptions().isNotifyDiamond()) {
					if (diaAmount % 5 == 0) {
						on.sendMessage(StringUtil.cc("&7[&6&lO&e&lA&7]&6 " + p.getName() + "&7 Might be xraying, Mined "
								+ diaAmount + " diamonds!"));
					} else if (diaAmount == 1) {
						on.sendMessage(StringUtil.cc("&7[&6&lO&e&lA&7]&6 " + p.getName() + "&7 Mined the first diamond!"));
					}
				}
			}
			break;
		case GOLD_ORE:
			uHCPlayer.addGold();
			uHCPlayer.addTotalGoldMined();
			final int goldAmount = uHCPlayer.getGold();
			for (final UUID uuid : UHCGame.getInstance().getMods()) {
				final Player on = Bukkit.getPlayer(uuid);
				if (on != null && Library.getPlayerData(on).getOptions().isNotifyGold()) {
					if (goldAmount % 10 == 0) {
						on.sendMessage(StringUtil.cc("&7[&6&lO&e&lA&7]&6 " + p.getName() + "&7 Might be xraying, Mined "
								+ goldAmount + " golds!"));
					} else if (goldAmount == 1) {
						on.sendMessage(StringUtil.cc("&7[&6&lO&e&lA&7]&6 " + p.getName() + "&7 Mined the first gold!"));
					}
				}
			}
			break;
		case MOB_SPAWNER:
			uHCPlayer.addMobSpawner();
			uHCPlayer.addTotalSpawnersMined();
			final int mobAmount = uHCPlayer.getMobSpawner();
			if (mobAmount > 0) {
				for (final UUID uuid : UHCGame.getInstance().getMods()) {
					final Player on = Bukkit.getPlayer(uuid);
					if (on != null && Library.getPlayerData(on).getOptions().isNotifySpawner()) {
						if (mobAmount % 3 == 0) {
							on.sendMessage(StringUtil.cc("&7[&6&lO&e&lA&7]&6 " + p.getName() + "&7 Might be xraying, Mined "
									+ mobAmount + " mobSpawners!"));
						} else if (mobAmount == 1) {
							on.sendMessage(StringUtil.cc("&7[&6&lO&e&lA&7]&6 " + p.getName() + "&7 Mined the first mobSpawner!"));
						}
					}
				}
			}
			break;
		default:
			break;
		}
	}

}
