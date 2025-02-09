package fr.openmc.core.features.dungeons.items;

import dev.lone.itemsadder.api.CustomStack;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
public enum DungeonEquipmentsItems {
    start_sword(CustomStack.getInstance("dungeon:start_sword"), CustomStack.getInstance("dungeon:start_sword_break"), 10),
    ;

    private final ItemStack item;
    private final ItemStack breakItem;
    private final String itemId;
    private final int repairCost;

    DungeonEquipmentsItems(CustomStack customStack, CustomStack breakItem, int repairCost) {
        this.item = customStack.getItemStack();
        this.breakItem = breakItem.getItemStack();
        this.itemId = customStack.getId().replace("dungeon:", ""); // Normalisation de l'ID
        this.repairCost = repairCost;
    }
}

