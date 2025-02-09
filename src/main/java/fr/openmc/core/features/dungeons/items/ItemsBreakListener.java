package fr.openmc.core.features.dungeons.items;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.OMCPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;

public class ItemsBreakListener implements Listener {

    OMCPlugin plugin;

    @EventHandler
    public void onItemBreak(PlayerItemBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack brokenItem = event.getBrokenItem();

        CustomStack customStack = CustomStack.byItemStack(brokenItem);
        if (customStack == null) return;
        for (DungeonEquipmentsItems dungeonItem : DungeonEquipmentsItems.values()) {
            if (CustomStack.byItemStack(brokenItem).getId().equals(dungeonItem.getItemId())) {
                for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
                    ItemStack item = player.getInventory().getItem(slot);
                    if (item != null && item.equals(brokenItem)) {
                        player.getInventory().setItem(slot, dungeonItem.getBreakItem());
                        return;
                    }
                }
            }
        }
        plugin.getLogger().severe("No matching item found for ID: " + customStack.getId());
    }
}
