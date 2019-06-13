package spg.lgdev.uhc.scenario;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.listener.ScenariosHandler;
import spg.lgdev.uhc.nms.NMSHandler;
import spg.lgdev.uhc.util.StringUtil;
import spg.lgdev.uhc.util.memory.MemoryLocation;
import net.development.mitw.utils.holograms.Hologram;
import net.development.mitw.utils.holograms.HologramAPI;

@Getter
public class Timebomb {

	private final String name;
	private final MemoryLocation location;
	private final String world;

	private Hologram holograms;
	private int timer;
	private boolean hologramSpawned;
	private long lastStep;

	public Timebomb(final String name, final Location location) {
		this.name = name;
		this.location = MemoryLocation.copyOf(location);
		this.world = location.getWorld().getName();
		this.timer = UHCGame.getInstance().getTimebombTimer();
		this.hologramSpawned = false;
	}

	public void step() {
		lastStep = System.currentTimeMillis();
	}

	public boolean canStep() {
		return System.currentTimeMillis() - lastStep > 1000;
	}

	public void prepare(final PlayerDeathEvent e) {
		final Location location = this.location.toBukkitLocation();
		NMSHandler.getInstance().getFastBlockSet().setBlockFast(location, Material.CHEST, true);

		final Chest chestBlock = (Chest) location.getBlock().getState();

		NMSHandler.getInstance().getFastBlockSet().setBlockFast(location.clone().add(1, 0, 0), Material.CHEST, true);
		NMSHandler.getInstance().getFastBlockSet().setBlockFast(location.clone().add(0, 1, 0), Material.AIR, false);
		NMSHandler.getInstance().getFastBlockSet().setBlockFast(location.clone().add(1, 1, 0), Material.AIR, false);

		for (final ItemStack items : e.getDrops()) {
			if (items == null || items.getType() == Material.AIR) {
				continue;
			}
			chestBlock.getInventory().addItem(items);
		}
		if (Scenarios.Barebones.isOn()) {

			if (Scenarios.CutClean.isOn()) {

				chestBlock.getInventory().addItem(new ItemStack(Material.DIAMOND, 1));

			} else if (Scenarios.DoubleOres.isOn()) {

				chestBlock.getInventory().addItem(new ItemStack(Material.DIAMOND, 2));

			} else if (Scenarios.TripleOres.isOn()) {

				chestBlock.getInventory().addItem(new ItemStack(Material.DIAMOND, 3));

			} else {

				chestBlock.getInventory().addItem(new ItemStack(Material.DIAMOND, 1));

			}

			chestBlock.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 2));
			chestBlock.getInventory().addItem(new ItemStack(Material.ARROW, 32));
			chestBlock.getInventory().addItem(new ItemStack(Material.STRING, 2));

		}

		if (Scenarios.DiamondLess.isOn()) {
			chestBlock.getInventory().addItem(new ItemStack(Material.DIAMOND, 1));
		}

		if (Scenarios.GoldLess.isOn()) {

			chestBlock.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 8));
			chestBlock.getInventory().addItem(ScenariosHandler.buildGoldenHead());

		}

		if (UHCGame.getInstance().isGoldenHead()) {

			chestBlock.getInventory().addItem(ScenariosHandler.buildGoldenHead());

		}

		if (Scenarios.LuckyKill.isOn()) {

			chestBlock.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 1));

		}

		chestBlock.update();
		e.getDrops().clear();
	}

	public void spawnHologram() {
		if (!hologramSpawned) {
			holograms = HologramAPI.createHologram(location.toBukkitLocation().add(1, 1.15, 0.5), CachedConfig.B + timer + "s");
			holograms.spawn();
			step();
			hologramSpawned = true;
		}
	}

	public void cancel() {
		UHCGame.getInstance().getTimebombTask().remove(this);
	}

	public void run() {
		if (timer - 1 == 0) {

			HologramAPI.removeHologram(holograms);

			iUHC.getInstance().getServer().getOnlinePlayers().forEach(pl -> pl.sendMessage(StringUtil.replace(Lang.getMsg(pl, "Timebomb.Exploded"), "<player>", name)));

			NMSHandler.getInstance().getNMSControl().fastSync(() -> {
				final Location location = this.location.toBukkitLocation();
				final Block block = location.getBlock();

				if (block.getType() == Material.CHEST) {
					((Chest)block.getState()).getInventory().clear();
				}
				NMSHandler.getInstance().getFastBlockSet().setBlockFast(location, Material.AIR, false);
				NMSHandler.getInstance().getFastBlockSet().setBlockFast(location.add(1, 0, 0), Material.AIR, false);

				location.getWorld().createExplosion(location.getX() + 0.5, location.getY() + 0.5, location.getZ() + 0.5, 5.0f, false, true);
				location.getWorld().strikeLightning(location);
			});
			this.cancel();
			return;

		} else {

			timer--;
			holograms.setText(CachedConfig.B + timer + "s");

		}
	}

}
