package fr.openmc.core.features.skills.skill.active;

import fr.openmc.core.features.skills.SKILLS;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HealSkill extends ActiveSkill {
	
	public HealSkill() {
		super(SKILLS.HEAL);
	}
	
	@Override
	public void activeSkill(Player player) {
		// Apply instant health effect
		player.addPotionEffect(new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 1, false, false, false), true);
	}
}
