package fr.openmc.core.features.skills.skill.active;

import fr.openmc.core.features.skills.SKILLS;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class OreDetectorSkill extends ActiveSkill {
	
	/// List of ores to detect
	Material[] list = new Material[]{
			Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE,
			Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE,
			Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE,
			Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE,
			Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE,
			Material.NETHER_QUARTZ_ORE, Material.ANCIENT_DEBRIS,
	};
	
	public OreDetectorSkill() {
		super(SKILLS.ORE_DETECTOR);
	}
	
	@Override
	public void activeSkill(Player player) {
		
		Location location = player.getLocation();
		boolean hasOre = false;
		
		// Check if the player is in the end and send a message if so
		if (location.getWorld().getEnvironment().equals(World.Environment.THE_END)) {
			MessagesManager.sendMessage(player, Component.text("§3Vous ne pouvez pas activer cette compétence dans l'end"), Prefix.SKILLS, MessageType.INFO, false);
			return;
		}
		
		// Loop through the blocks around the player to check if there is an ore
		outerloop:
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				for (int z = 0; z < 8; z++) {
					Location newLoc = location.getBlock().getLocation().add(x - 4, y - 4, z - 4);
					if (Arrays.asList(list).contains(newLoc.getBlock().getType())) {
						hasOre = true;
						break outerloop;
					}
				}
			}
		}
		
		// If there is an ore, send a message and play a sound
		if (hasOre) {
			MessagesManager.sendMessage(player, Component.text("§2Votre instinct a détécté des minerais proches d'ici"), Prefix.SKILLS, MessageType.SUCCESS, false);
			player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_STEP, 1, 1);
		}
		// If there is no ore, send a message and play a sound
		else {
			MessagesManager.sendMessage(player, Component.text("§4Aucun minerai n'a été détécté aux alentours"), Prefix.SKILLS, MessageType.ERROR, false);
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
		}
	}
}
