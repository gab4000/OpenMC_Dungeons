package fr.openmc.core.features.skills.skill.passive;

import fr.openmc.core.features.skills.SKILLS;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SwimMasterSkill extends PassiveSkill {
	
	public SwimMasterSkill() {
		super(SKILLS.SWIM_MASTER);
		
		this.event = new Listener() {
			@EventHandler
			public void onPlayerSwim(PlayerMoveEvent e) {
				if (! e.getPlayer().isInWater()) return;
				
				Player player = e.getPlayer();
				if (doesntHaveSkill(player)) return;
				player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 10, 0, false, false, false));
			}
		};
	}
}
