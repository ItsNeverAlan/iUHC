package spg.lgdev.uhc.command.player;

import org.bukkit.entity.Player;

import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.command.abstracton.PlayerCommand;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.scenario.Scenarios;
import spg.lgdev.uhc.util.StringUtil;

public class ExtraInventoryCommand extends PlayerCommand {

	public ExtraInventoryCommand() {
		super("extrainventory");
		setAliases("extrainv");
	}

	@Override
	public void run(final Player player, final String[] args) {
		if (!Scenarios.ExtraInventory.isOn()) {
			returnTell(StringUtil.replace(Lang.getInstance().getMessage(player, "schenarios.is-disable"), "<scenario>", "ExtraInventory"));
		}

		if (GameStatus.notStarted()) {
			returnTell(Lang.getInstance().getMessage(player, "NotStarted"));
		}

		if (!iUHC.getInstance().getProfileManager().getProfile(player.getUniqueId()).isPlayerAlive()) {

			returnTell(Lang.getInstance().getMessage(player, "IsSpectator"));

		}

		player.openInventory(player.getEnderChest());
		tell(Lang.getInstance().getMessage(player, "ExtraInventory.Open-ExtraInventory"));

	}
}
