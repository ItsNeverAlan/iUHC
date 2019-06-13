package spg.lgdev.uhc.populator;

import lombok.Getter;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class GenRule {
    public static List<GenRule> rules = new ArrayList<>();

    @Getter
    private Material material;
    @Getter
    private Integer propability;
    @Getter
    private int minHeight;
    @Getter
    private int maxHeight;
    @Getter
    private int size;
    @Getter
    private int rounds;

    public GenRule(Material material, Integer propability, int minHeight, int maxHeight, int size, int rounds) {
        this.material = material;
        this.propability = propability;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.size = size;
        this.rounds = rounds;
        rules.add(this);
    }

    public static GenRule unparse(String parsed) {
        String[] splitted = parsed.split(":");

        Material material = Material.getMaterial(Integer.parseInt(splitted[0]));
        Integer probability = Integer.parseInt(splitted[1]);
        Integer minHeight = Integer.parseInt(splitted[2]);
        Integer maxHeight = Integer.parseInt(splitted[3]);
        Integer size = Integer.parseInt(splitted[4]);
        Integer round = Integer.parseInt(splitted[5]);

        return new GenRule(material, probability, minHeight, maxHeight, size, round);
    }

}
