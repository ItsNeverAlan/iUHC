package spg.lgdev.uhc.command.permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.command.abstracton.PermissionCommand;
import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.enums.InventoryTypes;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.Permissions;
import spg.lgdev.uhc.handler.game.KitsHandler;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.manager.InventoryManager;
import spg.lgdev.uhc.player.DataManager;
import spg.lgdev.uhc.task.DeathmatchTask;
import spg.lgdev.uhc.util.StringUtil;

public class MainCommand extends PermissionCommand {

	public MainCommand() {
		super("uhc", Permissions.ADMIN);
		setPrefix(() -> Lang.getMsg(p, "prefix"));
		setAliases("u", "ultimateuhc", "game");
	}

	@Override
	public void execute(final Player player, final String[] args) {
		if (args.length == 0) {

			tell(StringUtil.cc("&6/uhc start"));
			tell(StringUtil.cc("&6/uhc editor"));
			tell(StringUtil.cc("&6/uhc setTitle <title>"));
			tell(StringUtil.cc("&6/uhc reload"));
			tell(StringUtil.cc("&6/uhc setspawn"));
			tell(StringUtil.cc("&6/uhc kits"));
			tell(StringUtil.cc("&6/uhc debug"));
			returnTell(StringUtil.cc("&6/uhc toggle"));

		}
		if (args[0].equalsIgnoreCase("start")) {

			if (GameStatus.is(GameStatus.LOADING)) {

				returnTell("§cYou can not start the UHCGame.getInstance() while the world is loading.");

			}

			UHCGame.getInstance().startLobby();
			return;

		}
		if (args[0].equalsIgnoreCase("edit") || args[0].equalsIgnoreCase("config")
				|| args[0].equalsIgnoreCase("editor")) {

			InventoryManager.instance.createInventory(player, InventoryTypes.Config_Editor);

			return;

		}
		if (args[0].equalsIgnoreCase("setTitle")) {

			if (!(args.length >= 2)) {

				returnTell(StringUtil.cc("&6/uhc setTitle <Title>"));

			}

			String base = args[1];

			if (base.length() > 16) {
				base.substring(0, 16);
			}

			for (int i = 2; i < args.length; i++) {
				base = base + " " + args[i];
			}

			base = StringUtil.cc(base);
			tell(ChatColor.GREEN + "Title set!");

			UHCGame.getInstance().setScoreboardTitle(base);

			return;

		} else if (args[0].equalsIgnoreCase("reload")) {

			iUHC.getInstance().getFileManager().loadConfiguration();
			CachedConfig.reloadConfig();
			tell(ChatColor.GREEN + "reloaded all configuration");

		} else if (args[0].equalsIgnoreCase("deathmatch")) {

			if (GameStatus.notStarted()) {
				returnTell("§cGame is not started!");
			}

			if (UHCGame.getInstance().isDeathmatchCountdowning()) {
				returnTell("§cDeathmatch is in countdown!");
			}

			if (GameStatus.is(GameStatus.DEATHMATCH)) {
				returnTell("§cDeathmatch are started already!");
			}

			new DeathmatchTask(true);

		} else if (args[0].equalsIgnoreCase("setspawn")) {

			UHCGame.getInstance().setSpawnByPlayer(player);

			return;

		}
		if (args[0].equalsIgnoreCase("toggle") || args[0].equalsIgnoreCase("enable")) {

			if (args.length < 2) {

				final String u1 = args[0];
				tell(StringUtil.cc("&6/uhc " + u1 + " finalheal"));
				returnTell(StringUtil.cc("&6/uhc " + u1 + " pvp"));

			}
			if (args[1].equalsIgnoreCase("pvp") || args[1].equalsIgnoreCase("enablepvp")) {

				if (GameStatus.is(GameStatus.PVP)) {
					returnTell("§cPVP is already enabled!");
				}
				if (UHCGame.getInstance().isFinalheal()
						&& UHCGame.getInstance().finalHealCountdowns < UHCGame.getInstance().getFinalHealTime()) {
					UHCGame.getInstance().finalHealCountdowns = 3;
				}
				UHCGame.getInstance().pvpCountdowns = 10;
				return;
			} else if (args[1].equalsIgnoreCase("finalheal") || args[1].equalsIgnoreCase("fh")
					|| args[1].equalsIgnoreCase("heal")) {

				UHCGame.getInstance().finalHealCountdowns = 5;
				return;

			}

			final String u1 = args[0];
			tell(StringUtil.cc("&6/uhc " + u1 + " finalheal"));
			tell(StringUtil.cc("&6/uhc " + u1 + " pvp"));
			return;

		} else if (args[0].equalsIgnoreCase("debug")) {

			DataManager.getInstance().saveProfile(
					iUHC.getInstance().getProfileManager().getProfile(player.getUniqueId()));

		} else if (args[0].equalsIgnoreCase("kits")) {
			p = player;
			if (args.length <= 1) {
				tell(StringUtil.cc("&6/uhc kits save <temporarily> <kit>"));
				tell(StringUtil.cc("&6/uhc kits setInventory <kit> (<player>)"));
			} else {
				final String arg2 = args[1];
				if (arg2.equalsIgnoreCase("setInventory")) {
					if (args.length <= 2) {
						tell(StringUtil.cc("&6/uhc kits " + arg2 + " <kit> (<player>)"));
						tell(StringUtil.cc("&7Kit List: &ePractice, Start"));
					} else {
						final String arg3 = args[2];
						if (!arg3.equalsIgnoreCase("start") && !arg3.equalsIgnoreCase("practice")) {
							tell(StringUtil.cc("&6/uhc kits " + arg2 + " <kit> (<player>)"));
							returnTell(StringUtil.cc("&7Kit List: &ePractice, Start"));
						}
						if (args.length <= 3) {
							KitsHandler.getInstance().giveKitWithoutSaved(p, arg3);
							tell(ChatColor.GREEN + "You get kit " + arg3 + " !");
						} else {
							final String arg4 = args[3];
							final Player target = Bukkit.getPlayer(arg4);
							if (target != null) {
								KitsHandler.getInstance().giveKitWithoutSaved(target, arg3);
								tell(ChatColor.GREEN + "You gived " + arg4 + " kit " + arg3 + " !");
							}
						}
					}
					return;
				} else if (args.length <= 2) {
					tell(StringUtil.cc("&6/uhc kits " + arg2 + " <temporarily> <kit>"));
					tell(StringUtil.cc("&7Temporarily boolean:&e true, false"));
				} else {
					final String arg3 = args[2];
					if (args.length <= 3) {
						tell(StringUtil.cc("&6/uhc kits " + arg2 + " " + arg3 + " <kit>"));
						tell(StringUtil.cc("&7Kit List: &ePractice, Start"));
					} else if (arg3.equalsIgnoreCase("false") || arg3.equalsIgnoreCase("true")) {
						final String arg4 = args[3];
						if (!arg4.equalsIgnoreCase("start") && !arg4.equalsIgnoreCase("practice")) {
							tell(StringUtil.cc("&6/uhc kits " + arg2 + " " + arg3 + " <kit>"));
							returnTell(StringUtil.cc("&7Kit List: &ePractice, Start"));
						}
						if (arg3.equalsIgnoreCase("false")) {
							if (arg4.equalsIgnoreCase("start")) {
								KitsHandler.getInstance().isTemporaly = false;
							}
							KitsHandler.getInstance().saveKit(p, arg4.toUpperCase());
							tell(StringUtil.cc("&aYou saved " + arg4.toUpperCase() + " inventory!"));
						} else {
							if (arg4.equalsIgnoreCase("practice")) {
								returnTell(StringUtil.cc("&cYou cant save practice kit temporarily!"));
							}
							KitsHandler.getInstance().armor = p.getInventory().getArmorContents();
							KitsHandler.getInstance().content = p.getInventory().getContents();
							KitsHandler.getInstance().isTemporaly = true;
							tell(StringUtil.cc("&aYou saved " + arg4.toUpperCase() + " inventory temporarily!"));
						}
					} else {
						tell(StringUtil.cc("&6/uhc kits " + arg2 + " <temporarily> <kit>"));
						tell(StringUtil.cc("&7Temporarily Types: &etrue, false"));
					}
				}
			}
		} else if (args[0].equalsIgnoreCase("checkwin")) {
			UHCGame.getInstance().checkWin();
			return;
		} else {

			tell(StringUtil.cc("&6/uhc start"));
			tell(StringUtil.cc("&6/uhc editor"));
			tell(StringUtil.cc("&6/uhc setTitle"));
			tell(StringUtil.cc("&6/uhc reload"));
			tell(StringUtil.cc("&6/uhc setspawn"));
			returnTell(StringUtil.cc("&6/uhc toggle"));
		}
	}

}
