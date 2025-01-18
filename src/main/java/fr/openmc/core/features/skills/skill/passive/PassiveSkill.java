package fr.openmc.core.features.skills.skill.passive;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.skills.SKILLS;
import fr.openmc.core.features.skills.utils.SkillsUtils;
import org.bukkit.entity.Player;

public abstract class PassiveSkill {
	
	protected final SKILLS skills;
	
	public PassiveSkill(SKILLS skills) {
		this.skills = skills;
	}
	
	public abstract void registerSkillListener(OMCPlugin plugin);
	
	public abstract void apply(Player player);
	
	public boolean hasSkill(Player player) {
		return SkillsUtils.playerPassiveSkillsActivated.get(player.getUniqueId()).contains(this.skills.getPassiveSkill());
	}
}
