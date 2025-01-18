package fr.openmc.core.features.skills.skill.passive;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.skills.SKILLS;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PassiveSkillsManager {
	
	private final Map<String, PassiveSkill> passiveSkills = new HashMap<>();
	
	public PassiveSkillsManager() {
		registerSkill(new RollSkill());
		registerSkill(new ResilienceSkill());
		registerSkill(new AgilitySkill());
	}
	
	public void registerSkill(PassiveSkill skill) {
		passiveSkills.put(Objects.requireNonNull(SKILLS.getSkillByClass(skill.getClass())).getName(), skill);
	}
	
	public void activatePlayerSkills(Player player, OMCPlugin plugin) {
		for (PassiveSkill skill : passiveSkills.values()) {
			if (skill.hasSkill(player)) {
				skill.registerSkillListener(plugin);
			}
		}
	}
}