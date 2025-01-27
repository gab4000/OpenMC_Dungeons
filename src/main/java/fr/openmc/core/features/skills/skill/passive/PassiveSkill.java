package fr.openmc.core.features.skills.skill.passive;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.skills.SKILLS;
import fr.openmc.core.features.skills.utils.SkillsUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class PassiveSkill {
	
	protected final SKILLS skills;
	
	@Getter
	protected Listener event;
	
	public PassiveSkill(SKILLS skills) {
		this.skills = skills;
	}
	
	public void registerSkillListener(OMCPlugin plugin) {
		Bukkit.getServer().getPluginManager().registerEvents(event, plugin);
	}
	
	public void unregisterSkillListener() {
		HandlerList.unregisterAll(event);
	}
	
	public boolean doesntHaveSkill(Player player) {
		return !SkillsUtils.playerPassiveSkillsActivated.containsKey(player.getUniqueId())
				|| !SkillsUtils.playerPassiveSkillsActivated.get(player.getUniqueId()).contains(this.skills.getPassiveSkill());
	}
}
