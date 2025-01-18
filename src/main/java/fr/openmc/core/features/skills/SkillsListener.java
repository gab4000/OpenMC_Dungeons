package fr.openmc.core.features.skills;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.skills.skill.passive.PassiveSkill;
import fr.openmc.core.features.skills.utils.SkillStateManager;
import fr.openmc.core.features.skills.utils.SkillsUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SkillsListener implements Listener {
	
	private final OMCPlugin plugin;
	
	private final SkillStateManager skillStateManager = new SkillStateManager();
	
	public SkillsListener(OMCPlugin plugin) {
		this.plugin = plugin;
	}
	
	/*
	* -------------------------------------------------- ACTIVE SKILLS -------------------------------------------------
	*/
	
	@EventHandler
	public void onWandUse(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		
		if (item == null) return;
		
		ItemMeta meta = item.getItemMeta();
		
		if (item.getType() == Material.STICK
				&& meta != null
				&& ChatColor.stripColor(meta.getDisplayName()).equals("Skills Stick")
				&& (! skillStateManager.isInSkillMode(player.getUniqueId()) || ! skillStateManager.contains(player.getUniqueId()))) {
			if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
			useWand(player);
		}
		
		if (item.getType() == Material.CHERRY_DOOR
				&& skillStateManager.isInSkillMode(player.getUniqueId())) { //TODO si l'item est le bon
			if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK
					&& e.getAction() != Action.LEFT_CLICK_AIR && e.getAction() != Action.LEFT_CLICK_BLOCK) {
				return;
			}
			SkillsUtils.restoreHotbar(player);
			skillStateManager.setSkillMode(player.getUniqueId(), false);
			e.setCancelled(true);
		}
		
		if (meta != null
				&& meta.getDisplayName().contains("Parchemin")
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
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		List<Class<? extends PassiveSkill>> list = new ArrayList<>();
		list.add(SKILLS.ROLL.getPassiveSkill());
		list.add(SKILLS.RESILIENCE.getPassiveSkill());
		list.add(SKILLS.AGILITY.getPassiveSkill());
		SkillsUtils.playerPassiveSkillsActivated.put(e.getPlayer().getUniqueId(), list);
		if (! SkillsUtils.playerPassiveSkillsActivated.containsKey(e.getPlayer().getUniqueId())) return;
		
		Player player = e.getPlayer();
		plugin.getPassiveSkillsManager().activatePlayerSkills(player, plugin);
	}
}