package spg.lgdev.uhc.manager;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;

import lombok.Getter;
import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.handler.Library;
import spg.lgdev.uhc.handler.Permissions;
import spg.lgdev.uhc.handler.game.KitsHandler;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.nms.NMSHandler;
import spg.lgdev.uhc.player.TeamProfile;
import spg.lgdev.uhc.scenario.Scenarios;
import spg.lgdev.uhc.util.Utils;
import spg.lgdev.uhc.util.memory.MemoryList;
import spg.lgdev.uhc.util.memory.MemoryLocation;

public class ArenaManager {

	@Getter
	private static ArenaManager instance;
	private MemoryList<MemoryLocation> locations;

	public ArenaManager() {
		instance = this;
	}

	public void scatter(final Player p) {

		try {

			UHCGame.getInstance().clear(p, GameMode.SURVIVAL);

			if (UHCGame.getInstance().getPracticePlayers().contains(p.getUniqueId())) {
				PracticeManager.quit(p, false);
			}

			p.getEnderChest().clear();
			p.getInventory().setContents(KitsHandler.getInstance().getContent());
			p.getInventory().setArmorContents(KitsHandler.getInstance().getArmor());

			if (Scenarios.InfiniteEnchanter.isOn()) {
				p.getInventory().addItem(new ItemStack(Material.ENCHANTMENT_TABLE, 64));
				p.getInventory().addItem(new ItemStack(Material.ANVIL, 64));
				p.getInventory().addItem(new ItemStack(Material.BOOKSHELF, 64));
				p.getInventory().addItem(new ItemStack(Material.BOOKSHELF, 64));
				p.setExp(0);
				p.setLevel(30000);
			}

			p.updateInventory();

			if (GameStatus.notStarted()) {
				NMSHandler.getInstance().getNMSControl().setScatterEffects(p);
			}
			MemoryLocation loc;
			if (TeamManager.getInstance().isTeamsEnabled()) {
				final TeamProfile team = Library.getTeam(p);
				loc = team.getScatterLocation();
				if (loc == null) {
					loc = locations.getNext();
					team.setScatterLocation(loc);
				}
			} else if (locations != null) {
				loc = locations.getNext();
			} else {
				loc = getGreatScatterLocation(Bukkit.getWorld("UHCArena"));
			}

			if (loc == null) {
				loc = getGreatScatterLocation(Bukkit.getWorld("UHCArena"));
			}

			Utils.teleport(p, loc.toBukkitLocation());

		} catch (final Exception e) {
			e.printStackTrace();
			System.out.println("[ERROR] An error has been reported on scattering players!");
		}
	}

	public void loadScatterPoints() {

		final World mainWorld = Bukkit.getWorld("UHCArena");

		locations = new MemoryList<>();
		locations.setContents(new Object[CachedConfig.LocationGenerates]);

		for (int i = 0; i < CachedConfig.LocationGenerates; i++) {
			locations.getContents()[i] = getGreatScatterLocation(mainWorld);
		}

	}

	public MemoryLocation getGreatScatterLocation(final World world) {

		int times = 0;

		Location location = getScatterLocation(world);
		Material material = location.getBlock().getRelative(BlockFace.DOWN).getType();

		while ((material == Material.WATER
				|| material == Material.STATIONARY_WATER
				|| material == Material.LAVA
				|| material == Material.STATIONARY_LAVA
				|| material == Material.CACTUS) && times < 10) {

			location = getScatterLocation(world);
			material = location.getBlock().getRelative(BlockFace.DOWN).getType();
			times++;

		}

		if (times > 9) {

			Bukkit.broadcast(ChatColor.RED
					+ "[Scattering Point Generator] reached to 10 times attempts, failed to generate a good location!",
					Permissions.ADMIN);

		}

		return MemoryLocation.copyOf(location.add(0, 1, 0));
	}

	public void memoryFree() {
		locations.clear();
		locations = null;
	}

	public Location getScatterLocation(final World world) {

		final int radius = UHCGame.getInstance().getBorderRadius();
		final double x = Math.round(iUHC.getRandom().nextDouble() * radius * 2.0D - radius) + 0.5D;
		final double z = Math.round(iUHC.getRandom().nextDouble() * radius * 2.0D - radius) + 0.5D;

		return new Location(world, x, world.getHighestBlockYAt(NumberConversions.floor(x), NumberConversions.floor(z)), z);
	}

}
