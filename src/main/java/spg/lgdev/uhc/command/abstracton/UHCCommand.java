package spg.lgdev.uhc.command.abstracton;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;

import spg.lgdev.uhc.util.Utils;

public abstract class UHCCommand extends Command {

	private static final Set<UHCCommand> commands = new HashSet<>();

	protected UHCCommand(final String name) {
		super(name);
	}

	protected UHCCommand(final String name, final String description, final String usageMessage, final List<String> aliases) {
		super(name, description, usageMessage, aliases);
	}

	public static void unregisterAll() {
		commands.forEach(cmd -> cmd.unregister(Utils.getCommandMap()));
		commands.clear();
	}

}
