package fr.openmc.core.features.skills.skill.passive;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.skills.SKILLS;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ResilienceSkill extends PassiveSkill {
	
	public ResilienceSkill() {
		super(SKILLS.RESILIENCE);
	}
	
	@Override
	public void registerSkillListener(OMCPlugin plugin) {
		Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
			@EventHandler
			public void onPlayerDamage(EntityDamageByEntityEvent e) {
				if (! (e.getDamager() instanceof Player)) return;
				
				Player player = ((Player) e.getDamager()).getPlayer();
				if (player == null) return;
				if (! hasSkill(player)) return;
				
				apply(player);
				e.setDamage(e.getDamage() * 1.05);
			}
		}, plugin);
	}
	
	@Override
	public void apply(Player player) {
		MessagesManager.sendMessageType(player, Component.text("§3Votre compétence §6Résilience §3augmente vos dégats de combat !"), Prefix.SKILLS, MessageType.INFO, false);
	}
}