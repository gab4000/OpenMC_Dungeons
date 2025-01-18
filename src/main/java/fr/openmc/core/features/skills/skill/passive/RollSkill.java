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
import org.bukkit.event.entity.EntityDamageEvent;

public class RollSkill extends PassiveSkill {
	
	public RollSkill() {
		super(SKILLS.ROLL);
	}
	
	@Override
	public void registerSkillListener(OMCPlugin plugin) {
		Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
			@EventHandler
			public void onPlayerFall(EntityDamageEvent e) {
				if (!(e.getEntity() instanceof Player)) return;
				if (e.getCause() != EntityDamageEvent.DamageCause.FALL) return;
				if (e.getDamage() == 0) return;
				
				Player player = ((Player) e.getEntity()).getPlayer();
				if (player == null) return;
				if (! hasSkill(player)) return;
				
				apply(player);
				e.setDamage(e.getDamage() * 0.9);
			}
		}, plugin);
	}
	
	@Override
	public void apply(Player player) {
		MessagesManager.sendMessageType(player, Component.text("§3Votre compétence §6Roulade §3réduit vos dégâts de chute !"), Prefix.SKILLS, MessageType.INFO, false);
	}
}
