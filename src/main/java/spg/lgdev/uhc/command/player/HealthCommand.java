package spg.lgdev.uhc.command.player;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;

import spg.lgdev.uhc.command.abstracton.PlayerCommand;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.handler.Lang;

public class HealthCommand extends PlayerCommand {

	public HealthCommand() {
		super("health", "h");
	}

	@Override
	public void run(final Player player, final String[] args) {

		if (GameStatus.notStarted()) {
			returnTell(Lang.getMsg(player, "Health.NotStarted"));
		}

		if (args.length == 0) {
			final Damageable pd = player;
			final DecimalFormat dc = new DecimalFormat("#.#");
			final double health = pd.getHealth();
			final String vida = dc.format(health);
			returnTell(Lang.getMsg(player, "Health.PlayerHealth")
					.replaceAll("<health>", vida).replaceAll("<heart>", "\u2764"));
		}

		if (args.length > 0) {

			final Player target = Bukkit.getPlayer(args[0]);

			if (target == null) {
				returnTell(Lang.getMsg(player, "Health.TargetNotOnline"));
			}

			if (target == player) {
				final Damageable pd = player;
				final DecimalFormat dc = new DecimalFormat("#.#");
				final double health = pd.getHealth();
				final String vida = dc.format(health);
				returnTell(Lang.getMsg(player, "Health.PlayerHealth")
						.replaceAll("<health>", vida).replaceAll("<heart>", "\u2764"));
			}

			final Damageable pd = target;
			final DecimalFormat dc = new DecimalFormat("#.#");
			final double health = pd.getHealth();
			final String vida = dc.format(health);
			returnTell(Lang.getMsg(player, "Health.OtherHealth").replaceAll("<name>", target.getName()).replaceAll("<health>", vida).replaceAll("<heart>", "\u2764"));

		}
	}
}
