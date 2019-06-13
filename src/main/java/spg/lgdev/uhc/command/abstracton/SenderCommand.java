package spg.lgdev.uhc.command.abstracton;

import java.util.function.Supplier;

import org.apache.commons.lang.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import spg.lgdev.uhc.util.Utils;

public abstract class SenderCommand extends UHCCommand {

	protected SenderCommand(final String name) {
		super(name);
	}
	protected CommandSender s;
	protected String[] args;
	@Setter
	protected Supplier<String> prefix = () -> "";

	@Override
	public boolean execute(final CommandSender sender, final String label, final String[] args) {
		s = sender;
		this.args = args;
		try {
			run(s, args);
		} catch (final CommandException e) {
			tell(e.tellMessage);
		}
		return true;
	}

	public abstract void run(CommandSender s, String[] args);

	public void tell(final String msg) {
		Utils.tell(s, prefix.get() + msg);
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
		int number = 0;
		try {
			number = Integer.parseInt(toCheck);
		} catch (final Exception e) {
			returnTell(message);
		}
		return number;
	}

	public int checkDigitalLegit(final String toCheck, final int from, final int to, final String message) {
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
		if (args.length >= requireArgs)
			return true;
		return false;
	}

	public Player checkPlayer(final CommandSender s , final String message) {
		if(!(s instanceof Player)) {
			returnTell(message);
		}
		return (Player) s;
	}

	public void checkNull(final Object obj, final String message) {
		if (obj == null) {
			returnTell(message);
		}
	}

	@RequiredArgsConstructor
	final class CommandException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		final String tellMessage;
	}

}
