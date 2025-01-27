package fr.openmc.core.features.skills.skill.passive;

import fr.openmc.core.features.skills.SKILLS;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class RollSkill extends PassiveSkill {
	
	public RollSkill() {
		super(SKILLS.ROLL);
		
		this.event = new Listener() {
			@EventHandler
			public void onPlayerFall(EntityDamageEvent e) {
				if (! (e.getEntity() instanceof Player)) return;
				if (e.getCause() != EntityDamageEvent.DamageCause.FALL) return;
				if (e.getDamage() == 0) return;
				
				Player player = ((Player) e.getEntity()).getPlayer();
				if (player == null) return;
				if (doesntHaveSkill(player)) return;
				
				e.setDamage(e.getDamage() * 0.9);
			}
		};
	}
}
