package fr.openmc.core.features.skills.skill.active;

import fr.openmc.core.features.skills.SKILLS;
import fr.openmc.core.features.skills.SkillsManager;
import org.bukkit.entity.Player;

public abstract class ActiveSkill {
	
	protected final SKILLS skills;
	
	public static final int SECOND = 20;
	public static final int MINUTE = 1200;
	
	public ActiveSkill(SKILLS skills) {
		this.skills = skills;
	}
	
	public abstract void activeSkill(Player player);
	
	public boolean hasSkill(Player player) {
		return SkillsManager.playerSkills.containsKey(player.getUniqueId())
				|| SkillsManager.playerSkills.get(player.getUniqueId()).contains(this.skills.getId());
	}
}
