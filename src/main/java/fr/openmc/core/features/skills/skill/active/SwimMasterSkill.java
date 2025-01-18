package fr.openmc.core.features.skills.skill.active;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SwimMasterSkill extends ActiveSkill implements Listener {
	
	@Override
	public void activeSkill(Player player) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 300*SECOND, 0, false, false, false));
	}
}
