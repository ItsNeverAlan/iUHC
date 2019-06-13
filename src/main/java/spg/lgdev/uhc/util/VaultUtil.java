package spg.lgdev.uhc.util;

import spg.lgdev.uhc.iUHC;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultUtil {

    private static Chat chat = null;

    public static String getPrefix(Player p) {
        String prefix = "";
        if (iUHC.getInstance().vault) {
            prefix = chat.getPlayerPrefix(p);
        }
        return prefix;
    }

    public static boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = iUHC.getInstance().getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

}
