package spg.lgdev.uhc.scenario;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.Getter;
import spg.lgdev.uhc.handler.Lang;
import spg.lgdev.uhc.handler.Permissions;
import spg.lgdev.uhc.manager.TeamManager;
import spg.lgdev.uhc.util.StringUtil;
import spg.lgdev.uhc.gui.GUI;

public enum Scenarios {

	Timber(false, new GUI.ItemStackData(6, 0)),
	CutClean(false, new GUI.ItemStackData(257, 0)),
	DiamondLess(false, new GUI.ItemStackData(264, 0)),
	GoldLess(false, new GUI.ItemStackData(266, 0)),
	bloodDiamonds(false, new GUI.ItemStackData(264, 0)),
	bloodGold(false, new GUI.ItemStackData(266, 0)),
	BowLess(false, new GUI.ItemStackData(261, 0)),
	VanillaPlus(false, new GUI.ItemStackData(318, 0)),
	HorseLess(false, new GUI.ItemStackData(329, 0)),
	TimeBomb(false, new GUI.ItemStackData(407, 0)),
	NoFallDamage(false, new GUI.ItemStackData(313, 0)),
	FireLess(false, new GUI.ItemStackData(259, 0)),
	NoClean(false, new GUI.ItemStackData(373, 16449)),
	DoubleOres(false, new GUI.ItemStackData(265, 0, 2)),
	TripleOres(false, new GUI.ItemStackData(265, 0, 3)),
	AbsorptionLess(false, new GUI.ItemStackData(373, 0)),
	RodLess(false, new GUI.ItemStackData(346, 0)),
	LuckyKill(false, new GUI.ItemStackData(283, 0)),
	LuckyLeaves(false, new GUI.ItemStackData(18, 0)),
	ExtraInventory(false, new GUI.ItemStackData(54, 0)),
	NoEnchants(false, new GUI.ItemStackData(116, 0)),
	Barebones(false, new GUI.ItemStackData(352, 0)),
	WebCage(false, new GUI.ItemStackData(30, 0)),
	Soup(false, new GUI.ItemStackData(282, 0)),
	Radar(false, new GUI.ItemStackData(381, 0)),
	VeinMiners(false, new GUI.ItemStackData(278, 0)),
	SuperHeroes(false, new GUI.ItemStackData(373, 8270)),
	NoCleanPlus(false, new GUI.ItemStackData(267, 0)),
	StatLess(false, new GUI.ItemStackData(379, 0)),
	FlowerPower(false, new GUI.ItemStackData(37, 0)),
	LoveAtFirstSight(false, new GUI.ItemStackData(38, 4)),
	InfiniteEnchanter(false, new GUI.ItemStackData(384, 0));

	@Getter
	private static List<String> scenariosList = new ArrayList<>();
	@Getter
	private static String scenariosString = "";
	@Getter
	private boolean on;
	@Getter
	private GUI.ItemStackData materialData;

	Scenarios(final boolean defaultOn, final GUI.ItemStackData materialData) {
		this.on = defaultOn;
		this.materialData = materialData;
	}

	public static void reset() {
		Stream.of(values()).forEach(scenario -> scenario.setOn(false));
		updateScenarios();
	}

	public static void updateScenarios() {
		scenariosList.clear();
		Stream.of(values()).filter(Scenarios::isOn).forEach(scenario -> scenariosList.add(scenario.name()));
		scenariosString = scenariosList.stream().collect(Collectors.joining(", "));
	}

	public static void applyScenarios(final List<String> scenarios) {
		reset();
		scenarios.stream().map(Scenarios::valueOf).forEach(scenario -> scenario.setOn(true));
		updateScenarios();
	}

	public void toggle() {
		setOn(!isOn());
	}

	public void setOn(final boolean on) {

		if (this.on == on)
			return;

		if (on && this == Scenarios.LoveAtFirstSight && (!TeamManager.getInstance().isTeamsEnabled() || TeamManager.getInstance().getMaxSize() != 2)) {
			Bukkit.broadcast("§cThe scenario §eLove at first sight§c can only be enabled when its to2!", Permissions.ADMIN);
			return;
		}

		this.on = on;

		final String messageFrom = on ? "scenarios.enabled" : "scenarios.disband";

		for (final Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage(Lang.getMsg(player, "scenarios.prefix")
					+ StringUtil.replace(Lang.getMsg(player, messageFrom), "<scenario>", name()));
		}

		if (on) {
			if (this == CutClean) {
				DoubleOres.setOn(false);
				TripleOres.setOn(false);
			} else if (this == DoubleOres) {
				CutClean.setOn(false);
				TripleOres.setOn(false);
			} else if (this == TripleOres) {
				CutClean.setOn(false);
				DoubleOres.setOn(false);
			}
		}
	}

	public String[] getDescription(final Player p) {
		return Lang.getInstance().getMessageList(p, "scenarios.description." + name());
	}

}
