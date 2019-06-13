package spg.lgdev.uhc.world.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import spg.lgdev.uhc.iUHC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.command.abstracton.SenderCommand;
import spg.lgdev.uhc.world.CoordXZ;


public class GeneratorCommand extends SenderCommand
{
	private final iUHC plugin;

	// color values for strings
	private final String clrCmd = ChatColor.AQUA.toString();		// main commands
	private final String clrReq = ChatColor.GREEN.toString();		// required values
	private final String clrOpt = ChatColor.DARK_GREEN.toString();	// optional values
	private final String clrDesc = ChatColor.WHITE.toString();		// command descriptions
	private final String clrHead = ChatColor.YELLOW.toString();		// command listing header
	private final String clrErr = ChatColor.RED.toString();			// errors / notices

	public GeneratorCommand (final iUHC plugin)
	{
		super("wb");
		this.plugin = plugin;
	}

	@Override
	public void run(final CommandSender sender, String[] split)
	{
		Player player = (sender instanceof Player) ? (Player)sender : null;

		final String cmd = clrCmd + ((player == null) ? "wb" : "/wb");
		final String cmdW = clrCmd + ((player == null) ? "wb " + clrReq + "<world>" : "/wb " + clrOpt + "[world]") + clrCmd;

		// if world name is passed inside quotation marks, handle that
		if (split.length > 2 && split[0].startsWith("\""))
		{
			if (split[0].endsWith("\""))
			{
				split[0] = split[0].substring(1, split[0].length() - 1);
			}
			else
			{
				final List<String> args = new ArrayList<>();
				String quote = split[0];
				int loop;
				for (loop = 1; loop < split.length; loop++)
				{
					quote += " " + split[loop];
					if (split[loop].endsWith("\"")) {
						break;
					}
				}

				if (loop < split.length || !split[loop].endsWith("\""))
				{
					args.add(quote.substring(1, quote.length() - 1));
					loop++;
					while (loop < split.length)
					{
						args.add(split[loop]);
						loop++;
					}
					split = args.toArray(new String[0]);
				}
			}
		}

		// "set" command from player or console, world specified
		if ((split.length >= 4) && split[1].equalsIgnoreCase("set"))
		{
			if (!GeneratorManager.HasPermission(player, "set")) return;

			if (split.length == 4 && ! split[split.length - 1].equalsIgnoreCase("spawn"))
			{	// command can only be this short if "spawn" is specified rather than x + z or player name
				sender.sendMessage(clrErr + "You have not provided a sufficient number of arguments. Check command list using root /wb command.");
				return;
			}

			final World world = sender.getServer().getWorld(split[0]);
			if (world == null) {
				sender.sendMessage("The world you specified (\"" + split[0] + "\") could not be found on the server, but data for it will be stored anyway.");
			}

			if (cmdSet(sender, world, player, split, 2)) {
				sender.sendMessage("Border has been set. " + GeneratorManager.BorderDescription(split[0]));
			}
		}

		// "set" command from player using current world since it isn't specified, or allowed from console only if player name is specified
		else if ((split.length >= 2) && split[0].equalsIgnoreCase("set"))
		{
			if (!GeneratorManager.HasPermission(player, "set")) return;

			if (player == null)
			{
				if (! split[split.length - 2].equalsIgnoreCase("player"))
				{	// command can only be called by console without world specified if player is specified instead
					sender.sendMessage(clrErr + "You must specify a world name from console if not specifying a player name. Check command list using root \"wb\" command.");
					return;
				}
				player = Bukkit.getPlayer(split[split.length - 1]);
				if (player == null || ! player.isOnline())
				{
					sender.sendMessage(clrErr + "The player you specified (\"" + split[split.length - 1] + "\") does not appear to be online.");
					return;
				}
			}

			if (cmdSet(sender, player.getWorld(), player, split, 1)) {
				sender.sendMessage("Border has been set. " + GeneratorManager.BorderDescription(player.getWorld().getName()));
			}
		}

		// "setcorners" command from player or console, world specified
		else if (split.length == 6 && split[1].equalsIgnoreCase("setcorners"))
		{
			if (!GeneratorManager.HasPermission(player, "set")) return;

			final String world = split[0];
			final World worldTest = sender.getServer().getWorld(world);
			if (worldTest == null) {
				sender.sendMessage("The world you specified (\"" + world + "\") could not be found on the server, but data for it will be stored anyway.");
			}

			try
			{
				final double x1 = Double.parseDouble(split[2]);
				final double z1 = Double.parseDouble(split[3]);
				final double x2 = Double.parseDouble(split[4]);
				final double z2 = Double.parseDouble(split[5]);
				GeneratorManager.setBorderCorners(world, x1, z1, x2, z2);
			}
			catch(final NumberFormatException ex)
			{
				sender.sendMessage(clrErr + "The x1, z1, x2, and z2 values must be numerical.");
				return;
			}

			if (player != null) {
				sender.sendMessage("Border has been set. " + GeneratorManager.BorderDescription(world));
			}
		}

		// "setcorners" command from player, using current world
		else if (split.length == 5 && split[0].equalsIgnoreCase("setcorners") && player != null)
		{
			if (!GeneratorManager.HasPermission(player, "set")) return;

			final String world = player.getWorld().getName();

			try
			{
				final double x1 = Double.parseDouble(split[1]);
				final double z1 = Double.parseDouble(split[2]);
				final double x2 = Double.parseDouble(split[3]);
				final double z2 = Double.parseDouble(split[4]);
				GeneratorManager.setBorderCorners(world, x1, z1, x2, z2);
			}
			catch(final NumberFormatException ex)
			{
				sender.sendMessage(clrErr + "The x1, z1, x2, and z2 values must be numerical.");
				return;
			}

			sender.sendMessage("Border has been set. " + GeneratorManager.BorderDescription(world));
		}

		// "radius" command from player or console, world specified
		else if ((split.length == 3 || split.length == 4) && split[1].equalsIgnoreCase("radius"))
		{
			if (!player.hasPermission("badlion.uhctrial")) return;

			final String world = split[0];

			final GeneratorData border = (GeneratorData)GeneratorManager.Border(world);
			if (border == null)
			{
				sender.sendMessage(clrErr + "That world (\"" + world + "\") must first have a border set normally.");
				return;
			}

			final double x = border.getX();
			final double z = border.getZ();
			int radiusX;
			int radiusZ;
			try
			{
				radiusX = Integer.parseInt(split[2]);
				if (split.length == 4) {
					radiusZ = Integer.parseInt(split[3]);
				} else {
					radiusZ = radiusX;
				}
			}
			catch(final NumberFormatException ex)
			{
				sender.sendMessage(clrErr + "The radius value(s) must be integers.");
				return;
			}

			GeneratorManager.setBorder(world, radiusX, radiusZ, x, z);

			if (player != null) {
				sender.sendMessage("Radius has been set. " + GeneratorManager.BorderDescription(world));
			}
		}

		// "radius" command from player, using current world
		else if ((split.length == 2 || split.length == 3) && split[0].equalsIgnoreCase("radius") && player != null)
		{
			if (!GeneratorManager.HasPermission(player, "radius")) return;

			final String world = player.getWorld().getName();

			final GeneratorData border = (GeneratorData)GeneratorManager.Border(world);
			if (border == null)
			{
				sender.sendMessage(clrErr + "This world (\"" + world + "\") must first have a border set normally.");
				return;
			}

			final double x = border.getX();
			final double z = border.getZ();
			int radiusX;
			int radiusZ;
			try
			{
				radiusX = Integer.parseInt(split[1]);
				if (split.length == 3) {
					radiusZ = Integer.parseInt(split[2]);
				} else {
					radiusZ = radiusX;
				}
			}
			catch(final NumberFormatException ex)
			{
				sender.sendMessage(clrErr + "The radius value(s) must be integers.");
				return;
			}

			GeneratorManager.setBorder(world, radiusX, radiusZ, x, z);
			sender.sendMessage("Radius has been set. " + GeneratorManager.BorderDescription(world));
		}

		// "clear" command from player or console, world specified
		else if (split.length == 2 && split[1].equalsIgnoreCase("clear"))
		{
			if (!GeneratorManager.HasPermission(player, "clear")) return;

			final String world = split[0];
			final GeneratorData border = (GeneratorData)GeneratorManager.Border(world);
			if (border == null)
			{
				sender.sendMessage("The world you specified (\"" + world + "\") does not have a border set.");
				return;
			}

			GeneratorManager.removeBorder(world);

			if (player != null) {
				sender.sendMessage("Border cleared for world \"" + world + "\".");
			}
		}

		// "clear" command from player, using current world
		else if (split.length == 1 && split[0].equalsIgnoreCase("clear") && player != null)
		{
			if (!GeneratorManager.HasPermission(player, "clear")) return;

			final String world = player.getWorld().getName();
			final GeneratorData border = (GeneratorData)GeneratorManager.Border(world);
			if (border == null)
			{
				sender.sendMessage(clrErr + "Your current world (\"" + world + "\") does not have a border set.");
				return;
			}

			GeneratorManager.removeBorder(world);
			sender.sendMessage("Border cleared for world \"" + world + "\".");
		}

		// "clear all" command from player or console
		else if (split.length == 2 && split[0].equalsIgnoreCase("clear") && split[1].equalsIgnoreCase("all"))
		{
			if (!GeneratorManager.HasPermission(player, "clear")) return;

			GeneratorManager.removeAllBorders();

			if (player != null) {
				sender.sendMessage("All borders cleared for all worlds.");
			}
		}

		// "list" command from player or console
		else if (split.length == 1 && split[0].equalsIgnoreCase("list"))
		{
			if (!GeneratorManager.HasPermission(player, "list")) return;

			final Set<String> list = GeneratorManager.BorderDescriptions();

			if (list.isEmpty())
			{
				sender.sendMessage("There are no borders currently set.");
				return;
			}

			for (final String borderDesc : list)
			{
				sender.sendMessage(borderDesc);
			}
		}

		// "getmsg" command from player or console
		else if (split.length == 1 && split[0].equalsIgnoreCase("getmsg"))
		{
			if (!GeneratorManager.HasPermission(player, "getmsg")) return;

			sender.sendMessage("Border message is currently set to:");
			sender.sendMessage(GeneratorManager.MessageRaw());
			sender.sendMessage("Formatted border message:");
			sender.sendMessage(GeneratorManager.Message());
		}

		// "setmsg" command from player or console
		else if (split.length >= 2 && split[0].equalsIgnoreCase("setmsg"))
		{
			if (!GeneratorManager.HasPermission(player, "setmsg")) return;

			String message = "";
			for (int i = 1; i < split.length; i++)
			{
				if (i != 1) {
					message += ' ';
				}
				message += split[i];
			}

			GeneratorManager.setMessage(message);

			if (player != null)
			{
				sender.sendMessage("Border message is now set to:");
				sender.sendMessage(GeneratorManager.MessageRaw());
				sender.sendMessage("Formatted border message:");
				sender.sendMessage(GeneratorManager.Message());
			}
		}

		// "debug" command from player or console
		else if (split.length == 2 && split[0].equalsIgnoreCase("debug"))
		{
			if (!GeneratorManager.HasPermission(player, "debug")) return;

			GeneratorManager.setDebug(strAsBool(split[1]));

			if (player != null) {
				GeneratorManager.log((GeneratorManager.Debug() ? "Enabling" : "Disabling") + " debug output at the command of player \"" + player.getName() + "\".");
			}

			if (player != null) {
				sender.sendMessage("Debug mode " + enabledColored(GeneratorManager.Debug()) + ".");
			}
		}

		// "whoosh" command from player or console
		else if (split.length == 2 && split[0].equalsIgnoreCase("whoosh"))
		{
			if (!GeneratorManager.HasPermission(player, "whoosh")) return;

			GeneratorManager.setWhooshEffect(strAsBool(split[1]));

			if (player != null)
			{
				GeneratorManager.log((GeneratorManager.whooshEffect() ? "Enabling" : "Disabling") + " \"whoosh\" knockback effect at the command of player \"" + player.getName() + "\".");
				sender.sendMessage("\"Whoosh\" knockback effect " + enabledColored(GeneratorManager.whooshEffect()) + ".");
			}
		}

		// "denypearl" command from player or console
		else if (split.length == 2 && split[0].equalsIgnoreCase("denypearl"))
		{
			if (!GeneratorManager.HasPermission(player, "denypearl")) return;

			GeneratorManager.setDenyEnderpearl(strAsBool(split[1]));

			if (player != null)
			{
				GeneratorManager.log((GeneratorManager.whooshEffect() ? "Enabling" : "Disabling") + " direct cancellation of ender pearls thrown past the border at the command of player \"" + player.getName() + "\".");
				sender.sendMessage("Direct cancellation of ender pearls thrown past the border " + enabledColored(GeneratorManager.whooshEffect()) + ".");
			}
		}

		// "knockback" command from player or console
		else if (split.length == 2 && split[0].equalsIgnoreCase("knockback"))
		{
			if (!GeneratorManager.HasPermission(player, "knockback")) return;

			double numBlocks = 0.0;
			try
			{
				numBlocks = Double.parseDouble(split[1]);
			}
			catch(final NumberFormatException ex)
			{
				sender.sendMessage(clrErr + "The knockback must be a decimal value of at least 1.0, or it can be 0.");
				return;
			}

			if (numBlocks < 0.0 || (numBlocks > 0.0 && numBlocks < 1.0))
			{
				sender.sendMessage(clrErr + "The knockback must be a decimal value of at least 1.0, or it can be 0.");
				return;
			}

			GeneratorManager.setKnockBack(numBlocks);

			if (player != null) {
				sender.sendMessage("Knockback set to " + numBlocks + " blocks inside the border.");
			}
		}

		// "wrap" command from player or console, world specified
		else if (split.length == 3 && split[0].equalsIgnoreCase("wrap"))
		{
			if (!GeneratorManager.HasPermission(player, "wrap")) return;

			final String world = split[1];
			final GeneratorData border = (GeneratorData)GeneratorManager.Border(world);
			if (border == null)
			{
				sender.sendMessage("The world you specified (\"" + world + "\") does not have a border set.");
				return;
			}

			final boolean wrap = strAsBool(split[2]);
			border.setWrapping(wrap);
			GeneratorManager.setBorder(world, border);

			if (player != null) {
				sender.sendMessage("Border for world \"" + world + "\" is now set to " + (wrap ? "" : "not ") + "wrap around.");
			}
		}

		// "wrap" command from player, using current world
		else if (split.length == 2 && split[0].equalsIgnoreCase("wrap") && player != null)
		{
			if (!GeneratorManager.HasPermission(player, "wrap")) return;

			final String world = player.getWorld().getName();
			final GeneratorData border = (GeneratorData)GeneratorManager.Border(world);
			if (border == null)
			{
				sender.sendMessage("This world (\"" + world + "\") does not have a border set.");
				return;
			}

			final boolean wrap = strAsBool(split[1]);
			border.setWrapping(wrap);
			GeneratorManager.setBorder(world, border);

			sender.sendMessage("Border for world \"" + world + "\" is now set to " + (wrap ? "" : "not ") + "wrap around.");
		}

		// "portal" command from player or console
		else if (split.length == 2 && split[0].equalsIgnoreCase("portal"))
		{
			if (!GeneratorManager.HasPermission(player, "portal")) return;

			GeneratorManager.setPortalRedirection(strAsBool(split[1]));

			if (player != null)
			{
				GeneratorManager.log((GeneratorManager.portalRedirection() ? "Enabling" : "Disabling") + " portal redirection at the command of player \"" + player.getName() + "\".");
				sender.sendMessage("Portal redirection " + enabledColored(GeneratorManager.portalRedirection()) + ".");
			}
		}

		// "fill" command from player or console, world specified
		else if (split.length >= 2 && split[1].equalsIgnoreCase("fill"))
		{
			// GNote - Fix this...console should be able to do this too
			if (player != null && !player.hasPermission("badlion.uhctrial")) return;

			boolean cancel = false, confirm = false, pause = false;
			String frequency = "";
			if (split.length >= 3)
			{
				cancel = split[2].equalsIgnoreCase("cancel") || split[2].equalsIgnoreCase("stop");
				confirm = split[2].equalsIgnoreCase("confirm");
				pause = split[2].equalsIgnoreCase("pause");
				if (!cancel && !confirm && !pause) {
					frequency = split[2];
				}
			}
			final String pad = (split.length >= 4) ? split[3] : "";
			final String forceLoad = (split.length >= 5) ? split[4] : "";

			final String world = split[0];

			cmdFill(sender, player, world, confirm, cancel, pause, pad, frequency, forceLoad);
		}

		// "fill" command from player (or from console solely if using cancel or confirm), using current world
		else if (split.length >= 1 && split[0].equalsIgnoreCase("fill"))
		{
			if (!GeneratorManager.HasPermission(player, "fill")) return;

			boolean cancel = false, confirm = false, pause = false;
			String frequency = "";
			if (split.length >= 2)
			{
				cancel = split[1].equalsIgnoreCase("cancel") || split[1].equalsIgnoreCase("stop");
				confirm = split[1].equalsIgnoreCase("confirm");
				pause = split[1].equalsIgnoreCase("pause");
				if (!cancel && !confirm && !pause) {
					frequency = split[1];
				}
			}
			final String pad = (split.length >= 3) ? split[2] : "";
			final String forceLoad = (split.length >= 4) ? split[3] : "";

			String world = "";
			if (player != null && !cancel && !confirm && !pause) {
				world = player.getWorld().getName();
			}

			if (!cancel && !confirm && !pause && world.isEmpty())
			{
				sender.sendMessage("You must specify a world! Example: " + cmdW + " fill " + clrOpt + "[freq] [pad] [force]");
				return;
			}

			cmdFill(sender, player, world, confirm, cancel, pause, pad, frequency, forceLoad);
		}

		// "remount" command from player or console
		else if (split.length == 2 && split[0].equalsIgnoreCase("remount"))
		{
			if (!GeneratorManager.HasPermission(player, "remount")) return;

			int delay = 0;
			try
			{
				delay = Integer.parseInt(split[1]);
				if (delay < 0)
					throw new NumberFormatException();
			}
			catch(final NumberFormatException ex)
			{
				sender.sendMessage(clrErr + "The remount delay must be an integer of 0 or higher. Setting to 0 will disable remounting.");
				return;
			}

			GeneratorManager.setRemountTicks(delay);

			if (player != null)
			{
				if (delay == 0) {
					sender.sendMessage("Remount delay set to 0. Players will be left dismounted when knocked back from the border while on a vehicle.");
				} else
				{
					sender.sendMessage("Remount delay set to " + delay + " tick(s). That is roughly " + (delay * 50) + "ms / " + ((delay * 50.0) / 1000.0) + " seconds. Setting to 0 would disable remounting.");
					if (delay < 10) {
						sender.sendMessage(clrErr + "WARNING:" + clrDesc + " setting this to less than 10 (and greater than 0) is not recommended. This can lead to nasty client glitches.");
					}
				}
			}
		}

		// "fillautosave" command from player or console
		else if (split.length == 2 && split[0].equalsIgnoreCase("fillautosave"))
		{
			if (!GeneratorManager.HasPermission(player, "fillautosave")) return;

			int seconds = 0;
			try
			{
				seconds = Integer.parseInt(split[1]);
				if (seconds < 0)
					throw new NumberFormatException();
			}
			catch(final NumberFormatException ex)
			{
				sender.sendMessage(clrErr + "The world autosave frequency must be an integer of 0 or higher. Setting to 0 will disable autosaving of the world during the Fill process.");
				return;
			}

			GeneratorManager.setFillAutosaveFrequency(seconds);

			if (player != null)
			{
				if (seconds == 0)
				{
					sender.sendMessage("World autosave frequency during Fill process set to 0, disabling it.");
					sender.sendMessage("Note that much progress can be lost this way if there is a bug or crash in the world generation process from Bukkit or any world generation plugin you use.");
				}
				else
				{
					sender.sendMessage("World autosave frequency during Fill process set to " + seconds + " seconds (rounded to a multiple of 5).");
					sender.sendMessage("New chunks generated by the Fill process will be forcibly saved to disk this often to prevent loss of progress due to bugs or crashes in the world generation process.");
				}
			}
		}

		// "dynmap" command from player or console
		else if (split.length == 2 && split[0].equalsIgnoreCase("dynmap"))
		{
			if (!GeneratorManager.HasPermission(player, "dynmap")) return;

			GeneratorManager.setDynmapBorderEnabled(strAsBool(split[1]));

			sender.sendMessage("DynMap border display " + (GeneratorManager.DynmapBorderEnabled() ? "enabled" : "disabled") + ".");

			if (player != null) {
				GeneratorManager.log((GeneratorManager.DynmapBorderEnabled() ? "Enabled" : "Disabled") + " DynMap border display at the command of player \"" + player.getName() + "\".");
			}
		}

		// "dynmapmsg" command from player or console
		else if (split.length >= 2 && split[0].equalsIgnoreCase("dynmapmsg"))
		{
			if (!GeneratorManager.HasPermission(player, "dynmapmsg")) return;

			String message = "";
			for (int i = 1; i < split.length; i++)
			{
				if (i != 1) {
					message += ' ';
				}
				message += split[i];
			}

			GeneratorManager.setDynmapMessage(message);

			if (player != null)
			{
				sender.sendMessage("DynMap border label is now set to:");
				sender.sendMessage(clrErr + GeneratorManager.DynmapMessage());
			}
		}

		// "bypass" command from player or console, player specified, on/off optionally specified
		else if (split.length >= 2 && split[0].equalsIgnoreCase("bypass"))
		{
			if (!GeneratorManager.HasPermission(player, "bypass")) return;

			final String sPlayer = split[1];

			boolean bypassing = !GeneratorManager.isPlayerBypassing(sPlayer);
			if (split.length > 2) {
				bypassing = strAsBool(split[2]);
			}

			GeneratorManager.setPlayerBypass(sPlayer, bypassing);

			final Player target = Bukkit.getPlayer(sPlayer);
			if (target != null && target.isOnline()) {
				target.sendMessage("Border bypass is now " + enabledColored(bypassing) + ".");
			}

			GeneratorManager.log("Border bypass for player \"" + sPlayer + "\" is " + (bypassing ? "enabled" : "disabled") + (player != null ? " at the command of player \"" + player.getName() + "\"" : "") + ".");
			if (player != null && player != target) {
				sender.sendMessage("Border bypass for player \"" + sPlayer + "\" is " + enabledColored(bypassing) + ".");
			}
		}

		// "bypass" command from player, using them for player
		else if (split.length == 1 && split[0].equalsIgnoreCase("bypass") && player != null)
		{
			if (!GeneratorManager.HasPermission(player, "bypass")) return;

			final String sPlayer = player.getName();

			final boolean bypassing = !GeneratorManager.isPlayerBypassing(sPlayer);
			GeneratorManager.setPlayerBypass(sPlayer, bypassing);

			GeneratorManager.log("Border bypass is " + (bypassing ? "enabled" : "disabled") + " for player \"" + sPlayer + "\".");
			sender.sendMessage("Border bypass is now " + enabledColored(bypassing) + ".");
		}

		// "bypasslist" command from player or console
		else if (split.length == 1 && split[0].equalsIgnoreCase("bypasslist"))
		{
			if (!GeneratorManager.HasPermission(player, "bypasslist")) return;

			sender.sendMessage("Players with border bypass enabled: " + GeneratorManager.getPlayerBypassList());
		}

		// we couldn't decipher any known commands, so show help
		else
		{
			if (!GeneratorManager.HasPermission(player, "help")) return;

			int page = (player == null) ? 0 : 1;
			if (split.length == 1)
			{
				try
				{
					page = Integer.parseInt(split[0]);
				}
				catch(final NumberFormatException ignored)
				{
				}
				if (page > 4) {
					page = 1;
				}
			}

			sender.sendMessage(clrHead + plugin.getDescription().getFullName() + " - commands (" + clrReq + "<required> " + clrOpt + "[optional]" + clrHead + ")" + (page > 0 ? " " + page + "/4" : "") + ":");

			if (page == 0 || page == 1)
			{
				if (player != null) {
					sender.sendMessage(cmd+" set " + clrReq + "<radiusX> " + clrOpt + "[radiusZ]" + clrDesc + " - set border, centered on you.");
				}
				sender.sendMessage(cmdW+" set " + clrReq + "<radiusX> " + clrOpt + "[radiusZ] <x> <z>" + clrDesc + " - set border.");
				sender.sendMessage(cmdW+" set " + clrReq + "<radiusX> " + clrOpt + "[radiusZ] spawn" + clrDesc + " - use spawn point.");
				sender.sendMessage(cmd+" set " + clrReq + "<radiusX> " + clrOpt + "[radiusZ] player <name>" + clrDesc + " - center on player.");
				sender.sendMessage(cmdW+" setcorners " + clrReq + "<x1> <z1> <x2> <z2>" + clrDesc + " - set by corners.");
				sender.sendMessage(cmdW+" radius " + clrReq + "<radiusX> " + clrOpt + "[radiusZ]" + clrDesc + " - change radius.");
				sender.sendMessage(cmd+" shape " + clrReq + "<elliptic|rectangular>" + clrDesc + " - set the default shape.");
				sender.sendMessage(cmd+" shape " + clrReq + "<round|square>" + clrDesc + " - same as above.");
				if (page == 1) {
					sender.sendMessage(cmd+" 2" + clrDesc + " - view second page of commands.");
				}
			}
			if (page == 0 || page == 2)
			{
				sender.sendMessage(cmdW+" clear" + clrDesc + " - remove border for this world.");
				sender.sendMessage(cmd+" clear all" + clrDesc + " - remove border for all worlds.");
				sender.sendMessage(cmd+" list" + clrDesc + " - show border information for all worlds.");
				sender.sendMessage(cmdW+" fill " + clrOpt + "[freq] [pad] [force]" + clrDesc + " - fill world to border.");
				sender.sendMessage(cmdW+" trim " + clrOpt + "[freq] [pad]" + clrDesc + " - trim world outside of border.");
				sender.sendMessage(cmd+" bypass " + ((player == null) ? clrReq + "<player>" : clrOpt + "[player]") + clrOpt + " [on/off]" + clrDesc + " - let player go beyond border.");
				sender.sendMessage(cmd+" knockback " + clrReq + "<distance>" + clrDesc + " - how far to move the player back.");
				sender.sendMessage(cmd+" wrap " + ((player == null) ? clrReq + "<world>" : clrOpt + "[world]") + clrReq + " <on/off>" + clrDesc + " - can make border crossings wrap.");
				if (page == 2) {
					sender.sendMessage(cmd+" 3" + clrDesc + " - view third page of commands.");
				}
			}
			if (page == 0 || page == 3)
			{
				sender.sendMessage(cmd+" whoosh " + clrReq + "<on|off>" + clrDesc + " - turn knockback effect on or off.");
				sender.sendMessage(cmd+" getmsg" + clrDesc + " - display border message.");
				sender.sendMessage(cmd+" setmsg " + clrReq + "<text>" + clrDesc + " - set border message.");
				sender.sendMessage(cmd+" delay " + clrReq + "<amount>" + clrDesc + " - time between border checks.");
				sender.sendMessage(cmd+" remount " + clrReq + "<amount>" + clrDesc + " - player remount delay after knockback.");
				sender.sendMessage(cmd+" wshape " + ((player == null) ? clrReq + "<world>" : clrOpt + "[world]") + clrReq + " <elliptic|rectangular|default>" + clrDesc + " - shape override for this world.");
				// above command takes 2 lines, so only 7 commands total listed for this page
				sender.sendMessage(cmd+" wshape " + ((player == null) ? clrReq + "<world>" : clrOpt + "[world]") + clrReq + " <round|square|default>" + clrDesc + " - same as above.");
				if (page == 3) {
					sender.sendMessage(cmd+" 4" + clrDesc + " - view fourth page of commands.");
				}
			}
			if (page == 0 || page == 4)
			{
				sender.sendMessage(cmd+" dynmap " + clrReq + "<on|off>" + clrDesc + " - turn DynMap border display on or off.");
				sender.sendMessage(cmd+" dynmapmsg " + clrReq + "<text>" + clrDesc + " - DynMap border labels will show this.");
				sender.sendMessage(cmd+" bypasslist " + clrDesc + " - list players with border bypass enabled.");
				sender.sendMessage(cmd+" fillautosave " + clrReq + "<seconds>" + clrDesc + " - world save interval for Fill.");
				sender.sendMessage(cmd+" portal " + clrReq + "<on|off>" + clrDesc + " - turn portal redirection on or off.");
				sender.sendMessage(cmd+" denypearl " + clrReq + "<on|off>" + clrDesc + " - stop ender pearls past the border.");
				sender.sendMessage(cmd+" reload" + clrDesc + " - re-load data from config.yml.");
				sender.sendMessage(cmd+" debug " + clrReq + "<on|off>" + clrDesc + " - turn console debug output on or off.");
				if (page == 4) {
					sender.sendMessage(cmd + clrDesc + " - view first page of commands.");
				}
			}
		}

		return;
	}


	private boolean strAsBool(String str)
	{
		str = str.toLowerCase();
		return str.startsWith("y") || str.startsWith("t") || str.startsWith("on") || str.startsWith("+") || str.startsWith("1");
	}

	private String enabledColored(final boolean enabled)
	{
		return enabled ? clrReq+"enabled" : clrErr+"disabled";
	}

	private boolean cmdSet(final CommandSender sender, World world, final Player player, final String[] data, final int offset)
	{
		int radiusX, radiusZ;
		double x, z;
		int radiusCount = data.length - offset;

		try
		{
			if (data[data.length - 1].equalsIgnoreCase("spawn"))
			{	// "spawn" specified for x/z coordinates
				final Location loc = world.getSpawnLocation();
				x = loc.getX();
				z = loc.getZ();
				radiusCount -= 1;
			}
			else if (data[data.length - 2].equalsIgnoreCase("player"))
			{	// player name specified for x/z coordinates
				final Player playerT = Bukkit.getPlayer(data[data.length - 1]);
				if (playerT == null || ! playerT.isOnline())
				{
					sender.sendMessage(clrErr + "The player you specified (\"" + data[data.length - 1] + "\") does not appear to be online.");
					return false;
				}
				world = playerT.getWorld();
				x = playerT.getLocation().getX();
				z = playerT.getLocation().getZ();
				radiusCount -= 2;
			}
			else
			{
				if (player == null || radiusCount > 2)
				{	// x and z specified
					x = Double.parseDouble(data[data.length - 2]);
					z = Double.parseDouble(data[data.length - 1]);
					radiusCount -= 2;
				}
				else
				{	// using coordinates of command sender (player)
					x = player.getLocation().getX();
					z = player.getLocation().getZ();
				}
			}

			radiusX = Integer.parseInt(data[offset]);
			if (radiusCount < 2) {
				radiusZ = radiusX;
			} else {
				radiusZ = Integer.parseInt(data[offset+1]);
			}
		}
		catch(final NumberFormatException ex)
		{
			sender.sendMessage(clrErr + "The radius value(s) must be integers and the x and z values must be numerical.");
			return false;
		}

		GeneratorManager.setBorder(world.getName(), radiusX, radiusZ, x, z);
		return true;
	}


	private String fillWorld = "";
	private int fillFrequency = 20;
	private int fillPadding = CoordXZ.chunkToBlock(13);
	private boolean fillForceLoad = false;

	private void fillDefaults()
	{
		fillWorld = "";
		fillFrequency = 20;
		// with "view-distance=10" in server.properties and "Render Distance: Far" in client, hitting border during testing
		// was loading 11 chunks beyond the border in a couple of directions (10 chunks in the other two directions); thus:
		fillPadding = CoordXZ.chunkToBlock(13);
		fillForceLoad = false;
	}

	private boolean cmdFill(final CommandSender sender, final Player player, final String world, final boolean confirm, final boolean cancel, final boolean pause, final String pad, final String frequency, final String forceLoad)
	{
		if (cancel)
		{
			sender.sendMessage(clrHead + "Cancelling the world map generation task.");
			fillDefaults();
			GeneratorManager.StopFillTask();
			return true;
		}

		if (pause)
		{
			if (GeneratorManager.fillTask == null || !GeneratorManager.fillTask.valid())
			{
				sender.sendMessage(clrHead + "The world map generation task is not currently running.");
				return true;
			}
			GeneratorManager.fillTask.pause();
			sender.sendMessage(clrHead + "The world map generation task is now " + (GeneratorManager.fillTask.isPaused() ? "" : "un") + "paused.");
			return true;
		}

		if (GeneratorManager.fillTask != null && GeneratorManager.fillTask.valid())
		{
			sender.sendMessage(clrHead + "The world map generation task is already running.");
			return true;
		}

		// set padding and/or delay if those were specified
		try
		{
			if (!pad.isEmpty()) {
				fillPadding = Math.abs(Integer.parseInt(pad));
			}
			if (!frequency.isEmpty()) {
				fillFrequency = Math.abs(Integer.parseInt(frequency));
			}
		}
		catch(final NumberFormatException ex)
		{
			sender.sendMessage(clrErr + "The frequency and padding values must be integers.");
			fillDefaults();
			return false;
		}
		if (fillFrequency <= 0)
		{
			sender.sendMessage(clrErr + "The frequency value must be greater than zero.");
			fillDefaults();
			return false;
		}

		if (!forceLoad.isEmpty()) {
			fillForceLoad = strAsBool(forceLoad);
		}

		// set world if it was specified
		if (!world.isEmpty()) {
			fillWorld = world;
		}

		if (confirm)
		{	// command confirmed, go ahead with it
			if (fillWorld.isEmpty())
			{
				sender.sendMessage(clrErr + "You must first use this command successfully without confirming.");
				return false;
			}

			if (player != null) {
				GeneratorManager.log("Filling out world to border at the command of player \"" + player.getName() + "\".");
			}

			int ticks = 1, repeats = 1;
			if (fillFrequency > 20) {
				repeats = fillFrequency / 20;
			} else {
				ticks = 20 / fillFrequency;
			}

			GeneratorManager.fillTask = new GeneratorFillTask(plugin.getServer(), player, fillWorld, fillPadding, repeats, ticks, fillForceLoad);
			if (GeneratorManager.fillTask.valid())
			{
				final int task = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, GeneratorManager.fillTask, ticks, ticks);
				GeneratorManager.fillTask.setTaskID(task);
				sender.sendMessage("WorldBorder map generation for world \"" + fillWorld + "\" task started.");
			} else {
				sender.sendMessage(clrErr + "The world map generation task failed to start.");
			}

			fillDefaults();
		}
		else
		{
			if (fillWorld.isEmpty())
			{
				sender.sendMessage(clrErr + "You must first specify a valid world.");
				return false;
			}

		}
		return true;
	}


	private String trimWorld = "";
	private int trimFrequency = 5000;
	private int trimPadding = CoordXZ.chunkToBlock(13);

	private void trimDefaults()
	{
		trimWorld = "";
		trimFrequency = 5000;
		trimPadding = CoordXZ.chunkToBlock(13);
	}
}