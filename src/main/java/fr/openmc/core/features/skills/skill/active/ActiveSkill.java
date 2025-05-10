package fr.openmc.core.features.skills.skill.active;

import fr.openmc.core.features.skills.SKILLS;
import fr.openmc.core.features.skills.SkillsDatabase;
import org.bukkit.entity.Player;

public abstract class ActiveSkill {
	
	public static final int SECOND = 20;
	public static final int MINUTE = 1200;
	protected final SKILLS skills;
	
	/**
	 * Constructor
	 * @param skills The active skill
	 */
	public ActiveSkill(SKILLS skills) {
		this.skills = skills;
	}
	
	/**
	 * Activate the active skill
	 * @param player The player to activate the active skill
	 */
	public abstract void activeSkill(Player player);
	
	/**
	 * Check if the player has the active skill
	 * @param player The player to check the active skill
	 * @return True if the player has the active skill, false otherwise
	 */
	public boolean hasSkill(Player player) {
		return SkillsDatabase.playerSkills.containsKey(player.getUniqueId())
				&& SkillsDatabase.playerSkills.get(player.getUniqueId()).contains(this.skills.getId());
	}
	
	/**
	 * Get the active enum skill
	 * @return The active enum skill
	 */
	public SKILLS getActiveSKILLS() {
		return skills;
	}
}
