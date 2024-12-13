package fr.openmc.core.features.dungeons.menus;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class DungeonMenu extends Menu {

    Player player;

    public DungeonMenu(Player player) {
        super(player);
        this.player = player;
    }

    @Override
    public @NotNull String getName() {
        return PlaceholderAPI.setPlaceholders(player, "§r§f%img_offset_-8%%img_test%");
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
