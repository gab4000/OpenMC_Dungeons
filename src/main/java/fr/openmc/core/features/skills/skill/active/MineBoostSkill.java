package fr.openmc.core.features.skills.skill.active;

import fr.openmc.core.features.skills.SKILLS;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MineBoostSkill extends ActiveSkill {
	
	public MineBoostSkill() {
		super(SKILLS.MINE_BOOST);
	}
	
	@Override
	public void activeSkill(Player player) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 10*MINUTE, 0, false, false, false), true);
	}
}
