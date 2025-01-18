package fr.openmc.core.features.skills.utils;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.features.skills.SKILLS;
import fr.openmc.core.features.skills.menus.SkillsMenu;
import fr.openmc.core.features.skills.skill.passive.PassiveSkill;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SkillsUtils {
	
	private static final HashMap<UUID, List<ItemStack>> hotbarMap = new HashMap<>();
	public static final HashMap<UUID, List<Class<? extends PassiveSkill>>> playerPassiveSkillsActivated = new HashMap<>();
	
	public static void saveHotbar(Player player) {
		List<ItemStack> hotbar = new ArrayList<>();
		for (int i = 0; i < 9; i++) { // Les 9 premiers slots de l'inventaire
			hotbar.add(player.getInventory().getItem(i));
			player.getInventory().setItem(i, new ItemStack(Material.AIR));
		}
		player.getInventory().setItem(8, new ItemStack(Material.CHERRY_DOOR));
		hotbarMap.put(player.getUniqueId(), hotbar);
	}
	
	public static void placeSkills(Player player) {
		player.getInventory().setItem(0, CustomStack.getInstance("skills:night_vision_scroll").getItemStack());
		player.getInventory().setItem(1, CustomStack.getInstance("skills:mine_boost_scroll").getItemStack());
		player.getInventory().setItem(2, CustomStack.getInstance("skills:heal_scroll").getItemStack());
		player.getInventory().setItem(3, CustomStack.getInstance("skills:ore_detector_scroll").getItemStack());
		player.getInventory().setItem(4, CustomStack.getInstance("skills:swim_master_scroll").getItemStack());
	}
	
	public static void restoreHotbar(Player player) {
		List<ItemStack> hotbar = hotbarMap.get(player.getUniqueId());
		if (hotbar == null) {
			return;
		}
		for (int i = 0; i < hotbar.size(); i++) {
			player.getInventory().setItem(i, hotbar.get(i));
		}
	}
	
	public static void useSkill(Player player, SKILLS skills) {
		skills.getActiveSkill().activeSkill(player);
		MessagesManager.sendMessageType(player, Component.text("Compétence utilisée"), Prefix.SKILLS, MessageType.SUCCESS, false);
	}
	
	public static void openMenu(Player player) {
		SkillsMenu menu = new SkillsMenu(player);
		menu.open();
	}
}