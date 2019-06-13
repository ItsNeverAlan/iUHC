package spg.lgdev.uhc.command.permission;

import spg.lgdev.uhc.iUHC;
import spg.lgdev.uhc.announce.AbstractAnnounce;
import spg.lgdev.uhc.announce.AnnounceManager;
import spg.lgdev.uhc.command.abstracton.PermissionCommand;
import spg.lgdev.uhc.enums.AnnounceType;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.gui.announcement.PostWinnerGUI;
import spg.lgdev.uhc.handler.Permissions;
import spg.lgdev.uhc.manager.TeamManager;
import org.bukkit.entity.Player;

public class PostWinnierCommand extends PermissionCommand {

    public PostWinnierCommand() {
        super("postwinner", Permissions.ADMIN);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (GameStatus.is(GameStatus.FINISH)) {
            if (args.length == 0) {
                new PostWinnerGUI().open(player);
                return;
            }
            AnnounceManager manager = iUHC.getInstance().getAnnounceManager();
            switch (args[0]) {
                case "discord": {
                    String messages = TeamManager.getInstance().isTeamsEnabled() ? AbstractAnnounce.getFormattedTeamWinMessage(AnnounceType.DISCORD) :
                            AbstractAnnounce.getFormattedFFAWinMessage(AnnounceType.DISCORD);
                    if (manager.getDiscord().isEnabled()) {
                        manager.getDiscord().postAnnounce(messages);
                        player.sendMessage("§aSuccess to announce winner on discord!");
                    } else {
                        player.sendMessage("§cDiscord announcer is not enabled!");
                    }
                    break;
                }
                case "twitter": {
                    String messages = TeamManager.getInstance().isTeamsEnabled() ? AbstractAnnounce.getFormattedTeamWinMessage(AnnounceType.TWITTER) :
                            AbstractAnnounce.getFormattedFFAWinMessage(AnnounceType.TWITTER);
                    if (manager.getTwitter().isEnabled()) {
                        manager.getTwitter().postAnnounce(messages);
                        player.sendMessage("§aSuccess to post winner tweet!");
                    } else {
                        player.sendMessage("§cTweet announcer is not enabled!");
                    }
                    break;
                }
            }
        }
    }
}
