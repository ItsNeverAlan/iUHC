package spg.lgdev.uhc.command.abstracton;

import java.util.Arrays;
import java.util.function.Supplier;

import org.apache.commons.lang.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import spg.lgdev.uhc.util.Utils;

public abstract class PlayerCommand extends UHCCommand {

	protected Player p;
	protected String[] args;
	@Setter
	protected Supplier<String> prefix = () -> "";

	public PlayerCommand(final String name) {
		super(name);
	}

	public PlayerCommand(final String name, final String... aliases) {
		super(name);
		setAliases(Arrays.asList(aliases));
	}

	@Override
	public boolean execute(final CommandSender sender, final String label, final String[] args) {
		if (!(sender instanceof Player)) {
			Utils.tell(sender, "&c這個指令只能由玩家使用!");
			return false;
		}
		p = (Player) sender;
		this.args = args;
		try {
			run(p, args);
		} catch (final CommandException e) {
			tell(e.tellMessage);
		} catch (final Throwable e) {
			e.printStackTrace();
		}
		return true;
	}

	public void setAliases(final String... aliases) {
		super.setAliases(Arrays.asList(aliases));
	}

	public abstract void run(Player player, String[] args);

	public void tell(final String msg) {
		Utils.tell(p, prefix.get() + msg);
	}

	public void returnTell(final String msg) {
		throw new CommandException(msg);
	}

	public void checkArgsLengh(final int requireArgs, final String message) {
		if (args.length != requireArgs) {
			returnTell(message);
		}
	}

	public int checkDigitalLegit(final String toCheck, final String message) {
		try {
			return Integer.parseInt(toCheck);
		} catch (final Exception e) {
			returnTell(message);
		}
		return 0;
	}

	public int checkDigitalLegit(final String toCheck, final int to, final int from, final String message) {
		int number = 0;
		try {
			number = Integer.parseInt(toCheck);
			Validate.isTrue(number <= to && number >= from);
		} catch (final Exception e) {
			returnTell(message);
		}
		return number;
	}

	public boolean reachRequireArgs(final int requireArgs) {
		return args.length >= requireArgs;
	}

	public void checkNull(final Object obj, final Supplier<String> function) {
		if (obj == null) {
			returnTell(function.get());
		}
	}

	@RequiredArgsConstructor
	final class CommandException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		final String tellMessage;
	}

}
