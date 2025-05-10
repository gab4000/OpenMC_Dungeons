package fr.openmc.core.features.skills.menus;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SkillsMenu extends Menu {
	
	public SkillsMenu(Player owner) {
		super(owner);
	}
	
	@Override
	public @NotNull String getName() {
		return PlaceholderAPI.setPlaceholders(getOwner(), "§r§f%img_offset_-8%%img_skills%");
	}
	
	@Override
	public @NotNull InventorySize getInventorySize() {
		return InventorySize.LARGEST;
	}
	
	@Override
	public void onInventoryClick(InventoryClickEvent e) {
	}
	
	@Override
	public @NotNull Map<Integer, ItemStack> getContent() {
		Map map = new HashMap();
		
		map.put(29, new ItemBuilder(this, new ItemStack(Material.DRAGON_EGG)).setNextMenu(new ActiveSkillsMenu(getOwner())));
		map.put(33, new ItemBuilder(this, new ItemStack(Material.BLACKSTONE_STAIRS)).setNextMenu(new PassiveSkillsMenu(getOwner())));
		
		return map;
	}
}
