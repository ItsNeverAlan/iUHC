package spg.lgdev.uhc.gui.announcement;

import spg.lgdev.uhc.gui.GUI;
import spg.lgdev.uhc.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PostWinnerGUI extends GUI {

    public PostWinnerGUI() {
        super("§ePost winner", 1);

        setItem(0, ItemUtil.buildItem(Material.WOOL, 1, 10, "&5Post discord embed", "&7Click me to post discord embed!"));
        setItem(1, ItemUtil.buildItem(Material.WOOL, 1, 3, "&bPost tweet", "&7Click me to post tweet!"));
    }

    @Override
    public void onClick(Player player, ItemStack itemStack) {
        switch (itemStack.getItemMeta().getDisplayName()) {
            case "§5Post discord embed":
                player.chat("/postwinner discord");
                break;
            case "§bPost tweet":
                player.chat("/postwinner twitter");
                break;
        }
    }

}
