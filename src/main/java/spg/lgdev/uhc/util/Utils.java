package spg.lgdev.uhc.util;

import static org.bukkit.Bukkit.getServer;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.nms.NMSHandler;
import spg.lgdev.uhc.util.reflection.resolver.FieldResolver;

public class Utils {

	public static String formatTimes(final int o) {
		final int totalSecs = o;
		final int hours = totalSecs / 3600;
		int minutes = totalSecs / 60;
		final int seconds = totalSecs;
		String str = null;
		if (o >= 3600) {
			str = "" + hours + "h";
		}
		if (o < 3600 && o >= 60) {
			minutes = minutes + 1;
			str = "" + (minutes) + "m";
		}
		if (o < 60) {
			str = "" + seconds + "s";
		}
		return str;
	}

	public static String formatTimes2(int o) {
		final int i = ++o / 60;
		final int j = o - i * 60;
		String str = null;
		str = i <= 0 ? (j < 10 ? "" + j : "" + j)
				: (i < 10 && i > 0 ? (j < 10 ? String.valueOf(i) + ":0" + j : String.valueOf(i) + ":" + j)
						: (j < 10 ? String.valueOf(i) + ":0" + j : String.valueOf(i) + ":" + j));
		return str;
	}

	public static String formatTimeHours(final int o) {
		String timer;
		final int totalSecs = o;
		final int hours = totalSecs / 3600;
		final int minutes = totalSecs % 3600 / 60;
		final int seconds = totalSecs % 60;
		if (totalSecs >= 3600) {
			timer = String.format("%02d:%02d:%02d", hours, minutes, seconds);
		} else {
			timer = String.format("%02d:%02d", minutes, seconds);
		}
		return timer;
	}

	public static String getTimeFormat(int o, final Player player) {
		final int i = ++o / 60;
		final int j = o - i * 60;
		return i <= 0 ? (j < 2 ? Lang.getMsg(player, "TimeFormat.Second") : Lang.getMsg(player, "TimeFormat.Seconds"))
				: (i < 2 && i > 0 ? Lang.getMsg(player, "TimeFormat.Minute")
						: Lang.getMsg(player, "TimeFormat.Minutes"));
	}

	public static int getRowsBySize(final int size) {
		if (size <= 9)
			return 1;
		if (size <= 18)
			return 2;
		if (size <= 27)
			return 3;
		if (size <= 36)
			return 4;
		if (size <= 45)
			return 5;
		return 6;
	}

	public static void teleport(final Player player, final Location location) {
		if (!location.getWorld().isChunkLoaded(location.getChunk())) {
			location.getWorld().loadChunk(location.getChunk());
		}
		player.teleport(location);
	}

	public static void removeCrafting(final ItemStack itemStack) {
		final Iterator<Recipe> iterator = getServer().recipeIterator();

		while (iterator.hasNext()) {
			final Recipe recipe = iterator.next();
			if (recipe != null && recipe.getResult().isSimilar(itemStack)) {

				iterator.remove();
			}
		}
	}

	public static void randomTeleport(final Player player) {

		final List<Player> players = UHCGame.getInstance().getOnlinePlayers();

		if (players.isEmpty())
			return;

		final Player random = players.stream().collect(new RandomListCollector<>(1))
				.stream().findFirst().orElse(null);

		if (random == null)
			return;

		player.teleport(random);
	}

	public static BukkitTask runTaskTimer(final BukkitRunnable runnable, final long delay, final long time, final boolean async) {
		if (async)
			return runnable.runTaskTimerAsynchronously(iUHC.getInstance(), delay, time);
		return runnable.runTaskTimer(iUHC.getInstance(), delay, time);
	}

	public static BukkitTask runTaskLater(final Runnable runnable, final long delay, final boolean async) {
		if (async)
			return Bukkit.getScheduler().runTaskLaterAsynchronously(iUHC.getInstance(), runnable, delay);
		return Bukkit.getScheduler().runTaskLater(iUHC.getInstance(), runnable, delay);
	}

	public static void pickupItem(final Player p, final ItemStack item) {
		if (item == null)
			return;
		if (p == null)
			return;
		if (item.getType() == Material.AIR)
			return;
		if (item.getAmount() == 0) {
			item.setAmount(1);
		}
		NMSHandler.getInstance().getNMSControl().pickup(p, item);
	}

	public static void hideAllPlayers(final Player p) {
		Bukkit.getOnlinePlayers().stream().filter(p2 -> !p.equals(p2)).forEach(p2 -> {
			p.hidePlayer(p2);
			p2.hidePlayer(p);
		});
	}

	public static void showAllPlayers(final Player p) {
		Bukkit.getOnlinePlayers().stream().filter(p2 -> !p.equals(p2)).forEach(p2 -> {
			p.showPlayer(p2);
			p2.showPlayer(p);
		});
	}

	public static void closeAsyncCatcher() {
		try {
			new FieldResolver(Class.forName("org.spigotmc.AsyncCatcher")).resolve("enabled").set(null, false);
			iUHC.getInstance().log(true, ChatColor.GREEN + "Async catcher disable success!");
		} catch (final Exception e) {
			e.printStackTrace();
			iUHC.getInstance().log(true, ChatColor.RED + "an issue happend on disabling async catcher!");
		}
	}

	public static void tell(final CommandSender sender, final String str) {
		sender.sendMessage(StringUtil.cc(str));
	}

	public static boolean fileExists(final String fileName) {
		return new File(fileName).exists();
	}

	public static void registerCommand(final Command command) {
		try {
			getCommandMap().register(iUHC.getInstance().getName(), command);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public static CommandMap getCommandMap() {
		try {
			final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

			bukkitCommandMap.setAccessible(true);
			return (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Player getDamager(final EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player)
			return (Player) event.getDamager();
		else if (event.getDamager() instanceof Projectile) {
			if (((Projectile) event.getDamager()).getShooter() instanceof Player)
				return (Player) ((Projectile) event.getDamager()).getShooter();
		}

		return null;
	}

	public static void playDeathAnimation(final Player player) {
		((CraftPlayer) player).getHandle().setFakingDeath(true);
		((CraftPlayer) player).getHandle().getDataWatcher().watch(6, 0.0F);
	}

}
