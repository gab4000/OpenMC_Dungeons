package fr.openmc.core.features.skills.skill.active;

import fr.openmc.core.features.skills.SKILLS;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NightVisionSkill extends ActiveSkill {
	
	public NightVisionSkill() {
		super(SKILLS.NIGHT_VISION);
	}
	
	@Override
	public void activeSkill(Player player) {
		// Apply the night vision effect to the player for 5 minutes
		player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 5 * MINUTE, 0, false, false, false), true);
	}
}
