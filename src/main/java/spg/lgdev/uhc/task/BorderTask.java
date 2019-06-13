package spg.lgdev.uhc.task;

import java.util.ArrayList;
import java.util.List;

import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.util.CStringBuffer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import lombok.Getter;
import spg.lgdev.uhc.border.BorderBuilder;
import spg.lgdev.uhc.border.BorderRadius;
import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.Permissions;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.manager.ArenaManager;
import spg.lgdev.uhc.player.PlayerProfile;
import spg.lgdev.uhc.util.PlayerUtil;
import spg.lgdev.uhc.util.StringUtil;
import spg.lgdev.uhc.util.Utils;

public class BorderTask extends Task {

	public static int BORDER_SIZE_NETHER_TELEPORT;

	@Getter
	private final BorderTask instance;

	public List<Integer> BORDER_LIST = new ArrayList<>();

	public BorderTask() {
		super(false, 0, 20);
		BORDER_SIZE_NETHER_TELEPORT = plugin.getFileManager().getConfig().getInt("Border.BorderTPNether");
		game.setBorderShrinking(true);

		instance = this;

		for (final int borders : plugin.getFileManager().getConfig().getIntegerList("Border.BorderList")) {
			if (borders < game.getBorderRadius()) {
				BORDER_LIST.add(borders);
			}
		}
	}

	@Override
	public void run() {

		if (GameStatus.is(GameStatus.FINISH)) {
			cancel();
			return;
		}

		if (BORDER_LIST.isEmpty() || !game.isHasBorder()) {
			cancel();
			return;
		}

		if (game.borderCountdowns - 1 == 0) {

			final int radius = BORDER_LIST.remove(0);

			final World world = Bukkit.getWorld("UHCArena");

			new BorderBuilder(world, radius, 4).start(true);

			plugin.getBarrierManager().setPause(true);
			game.setBorderRadius(radius);

			if (!game.isFirstBorderDone()) {
				game.setFirstBorderDone(true);
			}

			game.borderCountdowns = 300;

			final boolean borderTP = radius == BorderTask.BORDER_SIZE_NETHER_TELEPORT && UHCGame.getInstance().isNether();

			PlayerUtil.broadcastAction(p -> {
				p.sendMessage(Lang.getMsg(p, "border-prefix")
						+ StringUtil.replace(Lang.getMsg(p, "Border.Shrinked"), "size", radius));
				CachedConfig.SOUND_BORDER_SHRINKED.playSound(p);
				if (borderTP && p.getWorld().getName().equals("UHCArena_nether")) {
					p.teleport(ArenaManager.getInstance().getScatterLocation(world));
					p.sendMessage(Lang.getMsg(p, "Border.Nether-Teleport"));
				} else if (p.getWorld().getName().equals("UHCArena")) {
					BorderRadius.checkPlayer(p, p.getLocation(), false);
				}
				final PlayerProfile profile = plugin.getProfileManager().getProfile(p.getUniqueId());
				if (profile.isSpectator()) {
					if (!p.hasPermission(Permissions.getSpectate(radius))) {
						p.kickPlayer("§eYou has been kicked by spectator kick!");
					}
				} else if (radius < 101 && profile.isPlayerAlive()) {
					if (p.getLocation().getY() > 100 || p.getLocation().getY() < 55) {
						p.teleport(ArenaManager.getInstance().getScatterLocation(world));
					}
				}
				return null;
			}, () -> {
				if (borderTP) {
					iUHC.getInstance().getWorldCreator().unloadWorld(Bukkit.getWorld("UHCArena_nether"));
				}
			});

			if (BORDER_LIST.isEmpty()) {

				game.setHasBorder(false);
				game.setBorderShrinking(false);

			}
			return;

		} else {

			--game.borderCountdowns;

			if (game.borderCountdowns == 2400 || game.borderCountdowns == 2100 || game.borderCountdowns == 1800
					|| game.borderCountdowns == 1500 || game.borderCountdowns == 1200 || game.borderCountdowns == 900
					|| game.borderCountdowns == 600 || game.borderCountdowns == 300 || game.borderCountdowns == 120
					|| game.borderCountdowns == 60 || game.borderCountdowns == 30 || game.borderCountdowns == 15
					|| game.borderCountdowns <= 10) {

				final boolean netherWarning = BORDER_LIST.get(0) == BORDER_SIZE_NETHER_TELEPORT && (game.borderCountdowns == 300
						|| game.borderCountdowns == 60 || game.borderCountdowns == 10 || game.borderCountdowns == 5)
						&& game.isNether();

				for (final Player p : plugin.getServer().getOnlinePlayers()) {
					p.sendMessage(Lang.getMsg(p, "border-prefix")
							+ new CStringBuffer(Lang.getMsg(p, "Border.Countdown").replaceAll("<size>", "" + BORDER_LIST.get(0)))
							.replaceAll("<timer>", Utils.formatTimes2(game.borderCountdowns - 1))
							.replaceAll("<timerFormat>", Utils.getTimeFormat(game.borderCountdowns - 1, p)).toString());
					CachedConfig.SOUND_BORDER_COUNTDOWN.playSound(p);
					if (netherWarning && p.getWorld().getName().contains("nether")) {
						p.sendMessage(Lang.getMsg(p, "Border.Nether-Warning"));
					}
				}

			}

		}
	}

}
