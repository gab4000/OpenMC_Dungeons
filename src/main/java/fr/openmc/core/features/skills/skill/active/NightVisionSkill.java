package fr.openmc.core.features.skills.skill.active;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NightVisionSkill extends ActiveSkill {
	
	@Override
	public void activeSkill(Player player) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 300*SECOND, 0, false, false, false));
	}
}
