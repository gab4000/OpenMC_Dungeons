package fr.openmc.core.features.skills.menus;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.skills.SkillsDatabase;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActiveSkillsMenu extends Menu {
	
	public ActiveSkillsMenu(Player owner) {
		super(owner);
	}
	
	@Override
	public @NotNull String getName() {
		return PlaceholderAPI.setPlaceholders(getOwner(), "§r§f%img_offset_-8%%img_active_skills%");
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
		
		int i = 0;
		for (ItemStack item : getOwner().getInventory().getContents()) {
			assert item != null;
			if (item.hasItemMeta() && item.getItemMeta().getDisplayName().contains("Parchemin")) {
				map.put(18 + i, new ItemBuilder(this, item).setOnClick(inventoryClickEvent -> {
					SkillsDatabase.playerSkills.computeIfAbsent(getOwner().getUniqueId(), k -> new ArrayList<>()); //TODO ArrayList.add(skill from scroll)
				}));
				i += 2;
			}
		}
		
		map.put(27, new ItemBuilder(this, CustomStack.getInstance("_iainternal:icon_back_orange").getItemStack()).setBackButton());
		// map.put(35, new ItemBuilder(this, CustomStack.getInstance("openmc:accept").getItemStack()));
		
		return map;
	}
}
