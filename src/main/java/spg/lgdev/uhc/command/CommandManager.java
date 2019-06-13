package spg.lgdev.uhc.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.command.abstracton.PermissionCommand;
import spg.lgdev.uhc.command.abstracton.PlayerCommand;
import spg.lgdev.uhc.command.abstracton.SenderCommand;
import spg.lgdev.uhc.command.abstracton.UHCCommand;
import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.enums.InventoryTypes;
import spg.lgdev.uhc.gui.gameconfig.LanguageGUI;
import spg.lgdev.uhc.gui.gameconfig.ScenariosWatchGUI;
import spg.lgdev.uhc.gui.gameconfig.StatsGUI;
import spg.lgdev.uhc.gui.playeroptions.SpectatorOptionsGUI;
import spg.lgdev.uhc.gui.playeroptions.StaffOptionsGUI;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.Permissions;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.manager.InventoryManager;
import spg.lgdev.uhc.player.PlayerProfile;
import spg.lgdev.uhc.scenario.Scenarios;
import spg.lgdev.uhc.util.StringUtil;
import spg.lgdev.uhc.util.UHCSound;
import spg.lgdev.uhc.util.Utils;

public class CommandManager {

	private final Map<UUID, Long> cooldown = new HashMap<>();
	private final int timer = 15;

	public void registerSimpleCommands(final iUHC plugin) {
		final UHCGame game = UHCGame.getInstance();
		final CommandMap commandMap = Utils.getCommandMap();
		final String prefix = plugin.getName();
		commandMap.register(prefix, new SenderCommand("start") {
			@Override
			public void run(final CommandSender sender, final String[] args) {
				if (!sender.hasPermission(Permissions.ADMIN)) {
					sender.sendMessage(Lang.getMsg((Player) sender, "noPermission"));
					return;
				}
				if (GameStatus.is(GameStatus.LOADING)) {
					sender.sendMessage("§cthe arena world are still loading!");
					return;
				}
				game.startLobby();
			}
		});
		commandMap.register(prefix, new PlayerCommand("leave") {
			@Override
			public void run(final Player player, final String[] args) {
				game.sendToBungeeLobby(player);
			}
		});
		commandMap.register(prefix, new UHCCommand("list", "", "", Arrays.asList("playerlist")) {
			@Override
			public boolean execute(final CommandSender sender, final String label, final String[] args) {
				String hosts = "";
				Player h;
				for (final UUID u : game.getHosts()) {
					h = Bukkit.getPlayer(u);
					if (h != null && !hosts.contains(h.getName())) {
						hosts = hosts + h.getName() + ", ";
					}
				}
				String mods = "";
				for (final UUID u : game.getMods()) {
					h = Bukkit.getPlayer(u);
					if (h != null && !mods.contains(h.getName())) {
						mods = mods + h.getName() + ", ";
					}

				}

				sender.sendMessage("§7§m-----------------------------");
				sender.sendMessage("§fHost: §6" + hosts + "");
				if (sender.hasPermission(Permissions.ADMIN)) {
					sender.sendMessage("§fModerators: §b" + mods + "");
				}
				sender.sendMessage("§b");
				sender.sendMessage("§fPlayers Alive: §a" + plugin.getPlaying());
				sender.sendMessage("§fSpectators: §c" + plugin.getSpectators());
				sender.sendMessage("§7§m-----------------------------");

				return true;
			}
		});
		commandMap.register(prefix, new UHCCommand("worlds") {
			@Override
			public boolean execute(final CommandSender sender, final String label, final String[] args) {
				final World spawn = UHCGame.getInstance().getSpawnPoint() != null ? UHCGame.getInstance().getSpawnPoint().toBukkitWorld() : Bukkit.getWorlds().get(0);
				final World practice = Bukkit.getWorld("Practice");
				final World world = Bukkit.getWorld("UHCArena");
				final World worldNether = Bukkit.getWorld("UHCArena_nether");

				sender.sendMessage("§7§m-----------------------------");
				if (world != null) {
					sender.sendMessage("§aUHCArena: §f" + world.getPlayers().size() + "");
				}
				if (worldNether != null) {
					sender.sendMessage("§aUHCArena_nether: §f" + worldNether.getPlayers().size() + "");
				}
				if (practice != null) {
					sender.sendMessage("§cPractice: §f" + practice.getPlayers().size() + "");
				}
				if (spawn != null) {
					sender.sendMessage("§c" + spawn.getName() + ": §f" + spawn.getPlayers().size() + "");
				}
				sender.sendMessage("§7§m-----------------------------");

				return true;
			}
		});
		commandMap.register(prefix, new PlayerCommand("helpop") {
			@Override
			public void run(final Player p, final String[] args) {
				if (cooldown.containsKey(p.getUniqueId())) {
					final long secleft = ((cooldown.get(p.getUniqueId()) / 1000) + timer) - (System.currentTimeMillis() / 1000);
					if (secleft > 0) {
						tell("§cYou can use command every §e" + secleft + " seconds§c, please wait...");
					} else {
						cooldown.remove(p.getUniqueId());
					}
					return;
				} else {
					if (!p.hasPermission(Permissions.HELPOP_COOLDOWN_BYPASS)) {
						cooldown.put(p.getUniqueId(), System.currentTimeMillis());
					}
				}
				if (args.length < 1) {
					returnTell(ChatColor.RED + "/helpop <message>");
				}
				final StringBuilder msg = new StringBuilder();
				int n = 0;
				while (n < args.length) {
					final String string = args[n];
					msg.append(string).append(" ");
					++n;
				}
				if (!p.hasPermission(Permissions.HELPOP)) {
					tell("&7[&c&lHELP&7] &a" + p.getName() + "&7: &b" + msg);
				}
				for (final Player on : Bukkit.getOnlinePlayers()) {
					if ((game.isMod(on.getUniqueId()) || game.isHost(on.getUniqueId())
							|| on.hasPermission(Permissions.HELPOP)) && on != null) {
						on.sendMessage(StringUtil.cc("&7[&c&lHELP&7] &a" + p.getName() + "&7: &b" + msg));
					}
				}
				return;
			}
		});
		commandMap.register(prefix, new PermissionCommand("ac", Permissions.ADMINCHAT, "adminchat") {
			@Override
			public void execute(final Player p, final String[] args) {

				if (args.length < 1) {
					returnTell(ChatColor.RED + "/ac <message>");
				}

				final StringBuilder msg = new StringBuilder();
				int n = 0;
				while (n < args.length) {
					msg.append(args[n]).append(" ");
					++n;
				}

				for (final Player on : Bukkit.getOnlinePlayers()) {
					if ((game.isMod(on.getUniqueId()) || game.isHost(on.getUniqueId())
							|| on.hasPermission(Permissions.ADMINCHAT)) && on != null) {
						on.sendMessage(StringUtil.cc("&7[&b&lSTAFF&7] &a" + p.getName() + "&7: &b" + msg));
					}
				}

			}
		});
		commandMap.register(prefix, new PermissionCommand("shutdown", Permissions.ADMIN) {
			@Override
			public void execute(final Player p, final String[] args) {

				if (!GameStatus.is(GameStatus.FINISH)) {
					returnTell("§cThe game haven't ended!");
				}

				Bukkit.shutdown();

			}
		});
		commandMap.register(prefix, new PlayerCommand("tele") {
			@SuppressWarnings("deprecation")
			@Override
			public void run(final Player p, final String[] args) {
				if (plugin.getProfileManager().isAlive(p)) {
					returnTell(StringUtil.cc("&cYou are not spectator!"));
				}

				if (args.length == 0) {
					returnTell("§c/tele <player>");
				}

				final Player target = Bukkit.getPlayer(args[0]);
				checkNull(target, () -> Lang.getMsg(p, "TargetNotOnline"));

				p.teleport(target.getLocation());
				p.sendMessage("§cTeleporting to §f" + args[0]);
			}
		});
		commandMap.register(prefix, new SenderCommand("border") {
			@Override
			public void run(final CommandSender p, final String[] args) {
				if (!p.hasPermission(Permissions.ADMIN)) {
					returnTell(Lang.getMsg((Player) p, "noPermission"));
				}
				game.borderCountdowns = 11;
				p.sendMessage(ChatColor.GREEN + "You set the border timer left to 10 seconds!");
			}
		});
		commandMap.register(prefix, new PlayerCommand("rules") {
			@Override
			public void run(final Player p, final String[] args) {
				if (args.length == 0) {
					for (final String s : Lang.getInstance().getMessageList(p, "Rules.UsedCmd")) {
						p.sendMessage(StringUtil.cc(s));
					}
					return;
				}

				final String number = args[0];
				final String pagePath = "Rules.Page" + number;

				if (!CachedConfig.getLanguage().isSet(Lang.getLang(p) + "." + pagePath)) {
					returnTell(Lang.getMsg(p, "Rules.UnkownPages"));
				}

				for (String s : Lang.getInstance().getMessageList(p, pagePath)) {
					s = s.replace("<appleRate>", "" + game.getAppleRate());
					p.sendMessage(StringUtil.cc(s));
				}
			}
		});
		commandMap.register(prefix, new PlayerCommand("scenarios", "sce", "explain") {
			@Override
			public void run(final Player p, final String[] args) {
				if (args.length == 0) {
					if (Scenarios.getScenariosList().isEmpty()) {
						returnTell(Lang.getMsg(p, "NoScenarios"));
					}
					new ScenariosWatchGUI(p).open(p);
				}
			}
		});
		commandMap.register(prefix, new PlayerCommand("tpcenter") {
			@Override
			public void run(final Player p, final String[] args) {
				String world = "UHCArena";
				if (GameStatus.is(GameStatus.LOADING)) {
					returnTell(ChatColor.RED + "The UHC world are not loaded!");
				}
				if (GameStatus.is(GameStatus.DEATHMATCH) || Bukkit.getWorld("UHCArena_deathmatch") != null) {
					world = "UHCArena_deathmatch";
				}
				if (!iUHC.getInstance().getProfileManager().isAlive(p)) {
					p.teleport(new Location(Bukkit.getWorld(world), 0, 100, 0));
				}
			}
		});
		commandMap.register(prefix, new PlayerCommand("randomtp", "randomteleport") {
			@Override
			public void run(final Player p, final String[] args) {
				if (iUHC.getInstance().getProfileManager().isAlive(p))
					return;
				Utils.randomTeleport(p);
			}
		});
		commandMap.register(prefix, new PlayerCommand("alivemenu") {
			@Override
			public void run(final Player p, final String[] args) {
				if (iUHC.getInstance().getProfileManager().isAlive(p))
					return;
				p.openInventory(InventoryManager.instance.openAliveInventory(1));
			}
		});
		commandMap.register(prefix, new PlayerCommand("nethermenu") {
			@Override
			public void run(final Player p, final String[] args) {
				if (iUHC.getInstance().getProfileManager().isAlive(p))
					return;
				final Inventory inventory = InventoryManager.instance.openNetherAliveInventory(1);
				checkNull(inventory, () -> ChatColor.RED + "Nether isnt on!");
				p.openInventory(InventoryManager.instance.openNetherAliveInventory(1));
			}
		});
		commandMap.register(prefix, new PlayerCommand("specmenu", "spectatormenu") {
			@Override
			public void run(final Player p, final String[] args) {
				if (iUHC.getInstance().getProfileManager().isAlive(p))
					return;
				new SpectatorOptionsGUI(p).open(p);
			}
		});
		commandMap.register(prefix, new PlayerCommand("staffmenu", "moderatormenu") {
			@Override
			public void run(final Player p, final String[] args) {
				if (!game.isMod(p.getUniqueId()))
					return;
				new StaffOptionsGUI(p).open(p);
			}
		});
		commandMap.register(prefix, new PlayerCommand("vanish") {
			@Override
			public void run(final Player p, final String[] args) {
				if (!game.isMod(p.getUniqueId()))
					return;
				final PlayerProfile profile = iUHC.getInstance().getProfileManager().getProfile(p.getUniqueId());
				if (!profile.isVanish()) {
					UHCGame.getInstance().getOnlinePlayers().forEach(p1 -> p1.hidePlayer(p));
					UHCSound.NOTE_PLING.playSound(p);
					p.sendMessage(ChatColor.GREEN + "You are vanished!");
					profile.setVanish(true);
				} else {
					UHCGame.getInstance().getOnlinePlayers().forEach(p1 -> p1.showPlayer(p));
					UHCSound.NOTE_PLING.playSound(p);
					p.sendMessage(ChatColor.GREEN + "You are unvanished!");
					profile.setVanish(false);
				}
			}
		});
		commandMap.register(prefix, new PlayerCommand("config", "conf") {
			@Override
			public void run(final Player p, final String[] args) {
				InventoryManager.instance.createInventory(p, InventoryTypes.Config);
			}
		});
		commandMap.register(prefix, new PlayerCommand("stats") {
			@SuppressWarnings("deprecation")
			@Override
			public void run(final Player p, final String[] args) {
				if (args.length == 0) {
					new StatsGUI(p).open(p);
					return;
				}
				final Player target = Bukkit.getPlayer(args[0]);
				checkNull(target, () -> Lang.getInstance().getMessage(p, "TargetNotOnline"));
				new StatsGUI(target).open(p);
			}
		});
		commandMap.register(prefix, new PlayerCommand("lang") {
			@Override
			public void run(final Player p, final String[] args) {
				LanguageGUI.getInstance().open(p);
			}
		});
		commandMap.register(prefix, new PlayerCommand("stopfighting") {
			@Override
			public void run(final Player p, final String[] args) {

				final PlayerProfile profile = iUHC.getInstance().getProfileManager().getProfile(p.getUniqueId());
				if (profile == null)
					return;

				checkNull(profile.getFighting(), () -> Lang.getMsg(p, "NoCleanPlus.NoOpponent"));

				profile.setFighting(null);
				p.sendMessage(Lang.getMsg(p, "NoCleanPlus.Stopped"));
			}
		});
	}

}
