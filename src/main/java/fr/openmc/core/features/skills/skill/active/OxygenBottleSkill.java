package fr.openmc.core.features.skills.skill.active;

import fr.openmc.core.features.skills.SKILLS;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class OxygenBottleSkill extends ActiveSkill {
	
	public OxygenBottleSkill() {
		super(SKILLS.OXYGEN_BOTTLE);
	}
	
	@Override
	public void activeSkill(Player player) {
		// Apply the Conduit Power effect to the player for 5 minutes
		player.addPotionEffect(new PotionEffect(PotionEffectType.CONDUIT_POWER, 5 * MINUTE, 0, false, false, false), true);
	}
}
