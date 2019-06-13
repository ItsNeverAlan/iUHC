package spg.lgdev.uhc.command.player;

import org.bukkit.entity.Player;

import spg.lgdev.uhc.command.abstracton.PlayerCommand;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.manager.PracticeManager;

public class PracticeCommand extends PlayerCommand {

	public PracticeCommand() {
		super("practice", "prac");
	}

	@Override
	public void run(final Player p, final String[] args) {
		if (!UHCGame.getInstance().isPracticeEnabled()) {
			returnTell(Lang.getMsg(p, "Practice.isDisable"));
		}

		if (!UHCGame.getInstance().getPracticePlayers().contains(p.getUniqueId())) {
			PracticeManager.join(p);
		} else {
			PracticeManager.quit(p, true);
		}
	}


}