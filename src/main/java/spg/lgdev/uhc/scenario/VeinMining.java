package spg.lgdev.uhc.scenario;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import spg.lgdev.uhc.util.Utils;
import spg.lgdev.uhc.util.WorldUtil;

public class VeinMining {

    private Material material;
    private int dropcounts;
    private int toDrops;

    private int expToDrops;
    private int expcounts;

    public VeinMining(Material material, int dropcounts, int expcounts) {
        this.material = material;
        this.dropcounts = dropcounts;
        this.expcounts = expcounts;
    }

    public void addtoDrops() {
        this.toDrops += dropcounts;
        this.expToDrops += expcounts;
    }

    public void giveDrops(Player p) {
        Utils.pickupItem(p, new ItemStack(material, toDrops));
        if (expToDrops != 0) {
            WorldUtil.dropExperience(p.getLocation(), expToDrops);
        }
    }

}
