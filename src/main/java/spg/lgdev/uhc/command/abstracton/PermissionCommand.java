package spg.lgdev.uhc.command.abstracton;

import org.bukkit.entity.Player;

import spg.lgdev.uhc.handler.Lang;
import net.development.mitw.utils.StringUtil;

public abstract class PermissionCommand extends PlayerCommand {

	private final String permissionNode;

	public PermissionCommand(final String name, final String permission) {
		super(name);
		this.permissionNode = permission;
	}

	public PermissionCommand(final String name, final String permission, final String... aliases) {
		super(name, aliases);
		this.permissionNode = permission;
	}

	@Override
	public void run(final Player player, final String[] args) {
		if (player.hasPermission(permissionNode)) {
			execute(player, args);
			return;
		}
		player.sendMessage(StringUtil.cc(Lang.getMsg(player, "noPermission")));
	}

	public abstract void execute(Player player, String[] args);

}
