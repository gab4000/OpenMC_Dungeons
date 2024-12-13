package fr.openmc.core.features.dungeons.listeners.items;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemBreakEvent;

public class ItemsBreakListener implements Listener {

    @EventHandler
    public void onItemsBreak (PlayerItemBreakEvent event) {
        Player player = event.getPlayer();
        CustomStack item = CustomStack.byItemStack(event.getBrokenItem());

        if (item != null){
            player.sendMessage("test");
        }
    }
}
