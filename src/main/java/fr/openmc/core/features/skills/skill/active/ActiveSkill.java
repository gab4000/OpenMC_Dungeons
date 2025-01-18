package fr.openmc.core.features.skills.skill.active;

import org.bukkit.entity.Player;

public abstract class ActiveSkill {
	
	public static final int SECOND = 20;
	
	public abstract void activeSkill(Player player);
}
