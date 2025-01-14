package fr.openmc.core.features.dungeons.items;

import dev.lone.itemsadder.api.CustomStack;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
public enum DungeonItems {
    start_sword(CustomStack.getInstance("dungeon:start_sword"), CustomStack.getInstance("dungeon:start_sword_break")),
    ;

    private final ItemStack item;
    private final ItemStack breakItem;
    private final String itemId;

    DungeonItems(CustomStack customStack, CustomStack breakItem) {
        this.item = customStack.getItemStack();
        this.breakItem = breakItem.getItemStack();
        this.itemId = customStack.getId().replace("dungeon:", ""); // Normalisation de l'ID
    }
}

