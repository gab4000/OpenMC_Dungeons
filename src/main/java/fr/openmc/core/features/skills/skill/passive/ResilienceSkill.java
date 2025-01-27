package fr.openmc.core.features.skills.skill.passive;

import fr.openmc.core.features.skills.SKILLS;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ResilienceSkill extends PassiveSkill {
	
	public ResilienceSkill() {
		super(SKILLS.RESILIENCE);
		
		this.event = new Listener() {
			@EventHandler
			public void onPlayerDamage(EntityDamageByEntityEvent e) {
				if (! (e.getDamager() instanceof Player)) return;
				
				Player player = ((Player) e.getDamager()).getPlayer();
				if (player == null) return;
				if (doesntHaveSkill(player)) return;
				
				e.setDamage(e.getDamage() * 1.05);
			}
		};
	}
}