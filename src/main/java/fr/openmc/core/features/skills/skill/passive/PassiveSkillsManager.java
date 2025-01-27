package fr.openmc.core.features.skills.skill.passive;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.skills.SKILLS;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PassiveSkillsManager {
	
	private final Map<String, PassiveSkill> passiveSkills = new HashMap<>();
	
	public PassiveSkillsManager() {
		registerSkill(new RollSkill());
		registerSkill(new ResilienceSkill());
		registerSkill(new AgilitySkill());
		registerSkill(new SwimMasterSkill());
		registerSkill(new IronSkinSkill());
	}
	
	public void registerSkill(PassiveSkill skill) {
		passiveSkills.put(Objects.requireNonNull(SKILLS.getSkillByClass(skill.getClass())).getName(), skill);
	}
	
	public void activateSkills(OMCPlugin plugin) {
		for (PassiveSkill skill : passiveSkills.values()) {
			skill.registerSkillListener(plugin);
		}
	}
	
	public void deactivateSkills() {
		for (PassiveSkill skill : passiveSkills.values()) {
			skill.unregisterSkillListener();
		}
	}
}