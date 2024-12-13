package fr.openmc.core.features.dungeons.listeners.items;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.features.dungeons.effects.CorruptionEffect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDie (PlayerDeathEvent event){
        Player player = event.getPlayer();
        CustomStack item = CustomStack.getInstance("dungeon:corrupted_resurrection_stone");

        if (item == null ) {
            return;
        }
        ItemStack stone = item.getItemStack();
        if (player.getInventory().contains(stone)) {
            event.setKeepInventory(true);
            event.setCancelled(true);
            player.setHealth(6);
            player.getInventory().removeItem(stone);
            CorruptionEffect.applyCorruptionEffect(player,10);
        }
    }
}
