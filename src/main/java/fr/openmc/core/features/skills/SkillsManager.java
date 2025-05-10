package fr.openmc.core.features.skills;

import fr.openmc.core.OMCPlugin;

public class SkillsManager {
	
	/**
	 * Constructor for SkillsManager.
	 * Initializes the manager and registers all passive skills.
	 */
	public SkillsManager(OMCPlugin plugin) {
		for (SKILLS skills : SKILLS.values()) {
			if (SKILLS.isPassiveSkill(skills)) {
				skills.getPassiveSkill().registerSkillListener(plugin);
			}
		}
	}
}