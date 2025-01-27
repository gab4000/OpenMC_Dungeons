package fr.openmc.core.features.skills;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.features.skills.utils.SkillStateManager;
import fr.openmc.core.features.skills.utils.SkillsUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SkillsListener implements Listener {
	
	private final SkillStateManager skillStateManager = new SkillStateManager();
	
	/*
	 * -------------------------------------------------- ACTIVE SKILLS -------------------------------------------------
	 */
	
	@EventHandler
	public void onWandUse(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		
		ItemMeta meta = item.getItemMeta();
		
		if (item.getType() == Material.STICK
				&& meta != null
				&& ChatColor.stripColor(meta.getDisplayName()).equals("Skills Stick")
				&& (! skillStateManager.isInSkillMode(player.getUniqueId()) || ! skillStateManager.contains(player.getUniqueId()))) {
			if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
			useWand(player);
		}
		
		if (item.getType() == Material.PAPER
				&& meta != null
				&& skillStateManager.isInSkillMode(player.getUniqueId())
				&& ChatColor.stripColor(meta.getDisplayName()).equals("< Back")) {
			if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK
					&& e.getAction() != Action.LEFT_CLICK_AIR && e.getAction() != Action.LEFT_CLICK_BLOCK) {
				return;
			}
			SkillsUtils.restoreHotbar(player);
			skillStateManager.setSkillMode(player.getUniqueId(), false);
			e.setCancelled(true);
		}
		
		if (meta != null
				&& meta.getDisplayName().contains("Compétence")
				&& skillStateManager.isInSkillMode(player.getUniqueId())) {
			if (e.getAction() != Action.LEFT_CLICK_AIR && e.getAction() != Action.LEFT_CLICK_BLOCK) return;
			useSkill(player, SKILLS.getSkillByNamespace(CustomStack.byItemStack(item).getNamespacedID()));
			e.setCancelled(true);
		}
	}
	
	private void useWand(Player player) {
		if (player.isSneaking()) {
			SkillsUtils.openMenu(player);
		} else {
			SkillsUtils.saveHotbar(player);
			SkillsUtils.placeSkills(player);
			skillStateManager.setSkillMode(player.getUniqueId(), true);
		}
	}
	
	private void useSkill(Player player, SKILLS skills) {
		SkillsUtils.useSkill(player, skills);
		SkillsUtils.restoreHotbar(player);
		skillStateManager.setSkillMode(player.getUniqueId(), false);
	}
	
	/*
	 * ----------------------------------------------- PASSIVE SKILLS ---------------------------------------------------
	 */
}