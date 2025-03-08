package fr.openmc.core.features.skills.skill.active;

import fr.openmc.core.features.skills.SKILLS;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ApneaSkill extends ActiveSkill {
	
	public ApneaSkill() {
		super(SKILLS.APNEA);
	}
	
	@Override
	public void activeSkill(Player player) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.CONDUIT_POWER, 5*MINUTE, 0, false, false, false), true);
	}
}
