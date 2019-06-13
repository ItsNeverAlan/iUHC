package spg.lgdev.uhc.command.player;

import java.util.UUID;

import org.bukkit.entity.Player;

import spg.lgdev.uhc.command.abstracton.PlayerCommand;
import spg.lgdev.uhc.gui.spectator.InventorySnapshot;
import spg.lgdev.uhc.manager.InventoryManager;
import spg.lgdev.uhc.util.FastUUID;
import net.md_5.bungee.api.ChatColor;

public class InventoryCommand extends PlayerCommand {

	public InventoryCommand() {
		super("inventory");
	}

	@Override
	public void run(final Player player, final String[] args) {
		if (args.length < 1) {
			returnTell(ChatColor.RED + "usage: /inventory <Player UUID>");
		}
		UUID uuid = null;
		try {
			uuid = FastUUID.parseUUID(args[0]);
		} catch (final Exception e) {
			returnTell(ChatColor.RED + "usage: /inventory <Player UUID>");
		}
		final InventorySnapshot snapshot = InventoryManager.instance.getSnapshot(uuid);
		checkNull(snapshot, () -> ChatColor.RED + "this player's inventory snapshot didnt found!");

		player.openInventory(snapshot.getInventoryUI().getCurrentPage());
		tell(ChatColor.GREEN + "You opened " + snapshot.getOwnerName() + "'s Inventory Snapshot!");
	}

}
