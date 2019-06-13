package spg.lgdev.uhc.command.player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import spg.lgdev.uhc.command.abstracton.PlayerCommand;
import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.Library;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.task.MLGTask;
import spg.lgdev.uhc.util.ItemUtil;

public class MLGCommand extends PlayerCommand {

	public MLGCommand() {
		super("mlg");
	}

	public static List<UUID> InMLG = new ArrayList<>();
	public static List<UUID> InCountDown = new ArrayList<>();

	private boolean isInt(final String s) {
		try {
			Integer.parseInt(s);
		} catch (final NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	@Override
	public void run(final Player p, final String[] args) {

		if (!GameStatus.is(GameStatus.FINISH))
			return;

		if (InMLG.contains(p.getUniqueId()))
			return;

		if (!Library.getPlayerData(p).isPlayerAlive())
			return;

		int level = 0;

		InMLG.add(p.getUniqueId());
		InCountDown.add(p.getUniqueId());

		if (args.length == 0) {
			level = 2;
		} else if (!isInt(args[0]) || Integer.parseInt(args[0]) > 20 || Integer.parseInt(args[0]) <= 0) {
			p.sendMessage(ChatColor.RED + "the level haves some wrong , auto set the level to 2");
			level = 2;
		} else {
			level = Integer.parseInt(args[0]);
		}

		level = 30 * level;

		CachedConfig.SOUND_MLG_ACCEPT.playSoundToEveryone();

		UHCGame.getInstance().clear(p, GameMode.SURVIVAL);

		final ItemStack bucket = ItemUtil.buildItem(Material.WATER_BUCKET, 1, 0, "&6&lMLGGGGG", "&7MLGGGGGG WOOO");

		for (int i = 0; i < 9; i++) {
			p.getInventory().setItem(i, bucket);
		}

		for (final Player pl : Bukkit.getOnlinePlayers()) {
			if (pl.equals(p)) {
				p.sendMessage(Lang.getMsg(p, "MLG.accepted").replaceAll("<level>", level + ""));
				continue;
			}

			pl.sendMessage(Lang.getMsg(p, "MLG.broadcast").replaceAll("<player>", p.getName()).replaceAll("<level>", level + ""));
		}

		new MLGTask(p, level);

		final String w = Bukkit.getWorld("UHCArena_deathmatch") != null ? "UHCArena_deathmatch" : "UHCArena";
		p.teleport(Bukkit.getWorld(w).getHighestBlockAt(p.getLocation()).getLocation().add(0.0, 1.0, 0.0));
	}

}
