package fr.openmc.core.features.skills.skill.passive;

import fr.openmc.core.features.skills.SKILLS;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.EnumSet;
import java.util.Set;

public class IronSkinSkill extends PassiveSkill {
	
	private static final Set<EntityDamageEvent.DamageCause> IGNORED_CAUSES = EnumSet.of(
			EntityDamageEvent.DamageCause.DROWNING,
			EntityDamageEvent.DamageCause.FALL,
			EntityDamageEvent.DamageCause.FLY_INTO_WALL,
			EntityDamageEvent.DamageCause.SONIC_BOOM,
			EntityDamageEvent.DamageCause.POISON,
			EntityDamageEvent.DamageCause.STARVATION,
			EntityDamageEvent.DamageCause.SUFFOCATION,
			EntityDamageEvent.DamageCause.WORLD_BORDER,
			EntityDamageEvent.DamageCause.VOID,
			EntityDamageEvent.DamageCause.CAMPFIRE,
			EntityDamageEvent.DamageCause.CRAMMING,
			EntityDamageEvent.DamageCause.FREEZE,
			EntityDamageEvent.DamageCause.KILL,
			EntityDamageEvent.DamageCause.MAGIC,
			EntityDamageEvent.DamageCause.SUICIDE,
			EntityDamageEvent.DamageCause.WITHER
	);
	
	public IronSkinSkill() {
		super(SKILLS.IRON_SKIN);
		
		this.event = new Listener() {
			@EventHandler
			public void onPlayerDamage(EntityDamageEvent e) {
				if (! (e.getEntity() instanceof Player player)) return;
				if (IGNORED_CAUSES.contains(e.getCause())) return;
				if (e.getDamage() == 0) return;
				
				if (doesntHaveSkill(player)) return;
				
				e.setDamage(e.getDamage() * (1 - (Math.max(3, 9 - ((4 * e.getDamage()) / 8))) / 25));
			}
		};
	}
}
