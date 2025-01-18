package fr.openmc.core.features.skills.skill.passive;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.skills.SKILLS;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class AgilitySkill extends PassiveSkill {
	
	public AgilitySkill() {
		super(SKILLS.AGILITY);
	}
	
	@Override
	public void registerSkillListener(OMCPlugin plugin) {
		Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
			@EventHandler
			public void onPlayerMove(PlayerMoveEvent e) {
				Player player = e.getPlayer();
				if (!player.isOnGround()) return;
				
				if (! hasSkill(player)) return;
				player.setWalkSpeed(0.2f * 1.15f);
			}
		}, plugin);
	}
	
	@Override
	public void apply(Player player) {}
}