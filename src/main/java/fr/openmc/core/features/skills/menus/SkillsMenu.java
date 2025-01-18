package fr.openmc.core.features.skills.menus;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class SkillsMenu extends Menu {
	
	public SkillsMenu(Player owner) {
		super(owner);
	}
	
	@Override
	public @NotNull String getName() {
		return "";
	}
	
	@Override
	public @NotNull InventorySize getInventorySize() {
		return InventorySize.NORMAL;
	}
	
	@Override
	public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
	
	}
	
	@Override
	public @NotNull Map<Integer, ItemStack> getContent() {
		return Map.of();
	}
}
