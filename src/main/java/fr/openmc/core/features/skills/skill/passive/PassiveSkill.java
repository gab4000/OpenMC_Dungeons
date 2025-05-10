package fr.openmc.core.features.skills.skill.passive;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.skills.SKILLS;
import fr.openmc.core.features.skills.utils.SkillsUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class PassiveSkill {
	
	protected final SKILLS skills;
	
	@Getter
	protected Listener event;
	
	/**
	 * Constructor
	 * @param skills The passive skill
	 */
	public PassiveSkill(SKILLS skills) {
		this.skills = skills;
	}
	
	/**
	 * Register the listener for the passive skill
	 * @param plugin The plugin
	 */
	public void registerSkillListener(OMCPlugin plugin) {
		Bukkit.getServer().getPluginManager().registerEvents(event, plugin);
	}
	
	
	/**
	 * Check if the player haven't the passive skill
	 * @param player The player to check
	 * @return true if the player doesn't have the passive skill, false otherwise
	 */
	public boolean doesntHaveSkill(Player player) {
		return ! SkillsUtils.playerPassiveSkillsActivated.containsKey(player.getUniqueId())
				|| ! SkillsUtils.playerPassiveSkillsActivated.get(player.getUniqueId()).contains(this.skills.getPassiveSkill());
	}
	
	/**
	 * Get the passive enum skill
	 * @return The passive enum skill
	 */
	public SKILLS getPassiveSKILLS() {
		return skills;
	}
}
