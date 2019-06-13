package spg.lgdev.uhc.gui.gameconfig;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.gui.GUI;
import spg.lgdev.uhc.player.PlayerProfile;
import spg.lgdev.uhc.util.ItemUtil;

public class StatsGUI extends GUI {

	public StatsGUI(final Player p) {
		super(CachedConfig.B + p.getName() + CachedConfig.C + "'s Stats", 2);

		final PlayerProfile sp = iUHC.getInstance().getProfileManager().getProfile(p.getUniqueId());

		setItem(0, ItemUtil.buildItem(Material.EMERALD, 1, 0, CachedConfig.A + "Elo: " + CachedConfig.B + sp.getElo()));
		setItem(1, ItemUtil.buildItem(Material.GOLD_INGOT, 1, 0, CachedConfig.A + "Wins: " + CachedConfig.B + sp.getWins()));
		setItem(2, ItemUtil.buildItem(Material.IRON_SWORD, 1, 0, CachedConfig.A + "Kills: " + CachedConfig.B + sp.getTotalKills()));
		setItem(3, ItemUtil.buildItem(397, 1, 2, CachedConfig.A + "Deaths: " + CachedConfig.B + sp.getTotalDeaths()));
		setItem(4, ItemUtil.buildItem(Material.BOOK, 1, 0, CachedConfig.A + "KDR: " + CachedConfig.B + sp.getKd()));
		setItem(5, ItemUtil.buildItem(Material.DIAMOND_SWORD, 1, 0, CachedConfig.A + "Highest Kill Steak: " + CachedConfig.B + sp.getHighestKillStreak()));
		setItem(6, ItemUtil.buildItem(Material.BOW, 1, 0, CachedConfig.C + "Bow: ", CachedConfig.A + "Arrow Shot: " + CachedConfig.B + sp.getArrowShot(), CachedConfig.A + "Arrow Hit: " + CachedConfig.B + sp.getArrowHit()));
		setItem(7, ItemUtil.buildItem(Material.FISHING_ROD, 1, 0, CachedConfig.C + "Rod: ", CachedConfig.A + "Rod Used: " + CachedConfig.B + sp.getRodUsed(), CachedConfig.A + "Rod Hit: " + CachedConfig.B + sp.getRodHit()));
		setItem(8, ItemUtil.buildItem(Material.GOLDEN_APPLE, 1, 0, CachedConfig.A + "Consumed Golden Apple: " + CachedConfig.B + sp.getConsumedGApple()));
		setItem(9, ItemUtil.buildItem(Material.SKULL_ITEM, 1, 3, CachedConfig.A + "Consumed Golden Heads: " + CachedConfig.B + sp.getConsumedGHeads()));
		setItem(10, ItemUtil.buildItem(Material.EXP_BOTTLE, 1, 0, CachedConfig.A + "Exp earned: " + CachedConfig.B + sp.getXpLevelsEarned()));
		setItem(11, ItemUtil.buildItem(Material.POTION, 1, 0, CachedConfig.A + "Potion dranked: " + CachedConfig.B + sp.getPotionDranked()));
		setItem(12, ItemUtil.buildItem(Material.DIAMOND_ORE, 1, 0, CachedConfig.C + "Ores Mined: ", CachedConfig.A + "Iron: §7" + sp.getTotalIronMined(), CachedConfig.A + "Coal: §8" + sp.getTotalCoalMined(), CachedConfig.A + "Gold: §e" + sp.getTotalGoldMined(), CachedConfig.A + "Diamond: §b" + sp.getTotalDiamondsMined()));
		setItem(13, ItemUtil.buildItem(Material.MOB_SPAWNER, 1, 0, CachedConfig.A + "Mob Spawner Mined: " + CachedConfig.B + sp.getTotalSpawnersMined()));
		setItem(14, ItemUtil.buildItem(Material.NETHERRACK, 1, 0, CachedConfig.A + "Nether entrances: " + CachedConfig.B + sp.getNetherEntrances()));
	}

	@Override
	public void onClick(final Player p, final ItemStack stack) {
	}

}

