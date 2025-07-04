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
	
	/// Map to store the activated passive skills of players.
	public static final HashMap<UUID, List<Class<? extends PassiveSkill>>> playerPassiveSkillsActivated = new HashMap<>();
	
	/// Map to store the hotbar items of players.
	private static final HashMap<UUID, List<ItemStack>> hotbarMap = new HashMap<>();
	
	/**
	 * Saves the player's hotbar items.
	 * @param player The player whose hotbar items are to be saved.
	 */
	public static void saveHotbar(Player player) {
		List<ItemStack> hotbar = new ArrayList<>();
		for (int i = 0; i < 9; i++) { // Les 9 premiers slots de l'inventaire
			hotbar.add(player.getInventory().getItem(i));
			player.getInventory().setItem(i, new ItemStack(Material.AIR));
		}
		hotbarMap.put(player.getUniqueId(), hotbar);
	}
	
	/**
	 * Places the skills items in the player's hotbar.
	 * @param player The player whose hotbar items are to be set.
	 */
	public static void placeSkills(Player player) {
		player.getInventory().setItem(0, CustomStack.getInstance("skills:night_vision_skill").getItemStack());
		player.getInventory().setItem(1, CustomStack.getInstance("skills:mine_boost_skill").getItemStack());
		player.getInventory().setItem(2, CustomStack.getInstance("skills:heal_skill").getItemStack());
		player.getInventory().setItem(3, CustomStack.getInstance("skills:ore_detector_skill").getItemStack());
		player.getInventory().setItem(8, CustomStack.getInstance("_iainternal:icon_back_orange").getItemStack());
	}
	
	/**
	 * Restores the player's hotbar items.
	 * @param player The player whose hotbar items are to be restored.
	 */
	public static void restoreHotbar(Player player) {
		List<ItemStack> hotbar = hotbarMap.get(player.getUniqueId());
		if (hotbar == null) {
			return;
		}
		for (int i = 0; i < hotbar.size(); i++) {
			player.getInventory().setItem(i, hotbar.get(i));
		}
	}
	
	/**
	 * Activates an active skill for the player.
	 * @param player The player for whom the active skill is to be activated.
	 * @param skills The class of the active skill to be activated.
	 */
	public static void useSkill(Player player, SKILLS skills) {
		skills.getActiveSkill().activeSkill(player);
		MessagesManager.sendMessage(player, Component.text("Compétence utilisée"), Prefix.SKILLS, MessageType.SUCCESS, false);
	}
	
	/**
	 * Opens the skills menu for the player.
	 * @param player The player for whom the passive skill is to be activated.
	 */
	public static void openMenu(Player player) {
		SkillsMenu menu = new SkillsMenu(player);
		menu.open();
	}
}