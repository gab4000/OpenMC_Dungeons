package fr.openmc.core.features.skills.skill.passive;

import fr.openmc.core.features.skills.SKILLS;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class AgilitySkill extends PassiveSkill {
	
	public AgilitySkill() {
		super(SKILLS.AGILITY);
		
		this.event = new Listener() {
			@EventHandler
			public void onPlayerMove(PlayerMoveEvent e) {
				// Check if the player is on the ground
				if (! e.getPlayer().isOnGround()) return;
				
				// Check if the player has the skill
				Player player = e.getPlayer();
				if (doesntHaveSkill(player)) return;
				
				// Increase the player's walk speed by 15%
				player.setWalkSpeed(0.2f * 1.15f);
			}
		};
	}
}