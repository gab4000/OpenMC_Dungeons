package fr.openmc.core.features.dungeons.menus;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class WeeklyShopMenu extends Menu {
	
	Player player;
	
	public WeeklyShopMenu(Player owner) {
		super(owner);
		this.player = owner;
	}
	
	@Override
	public @NotNull String getName() {
		return PlaceholderAPI.setPlaceholders(player, "§r§f%img_offset_-8%%img_weekly_shop_menu%");
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
