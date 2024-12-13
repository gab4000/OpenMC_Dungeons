package fr.openmc.core.features.dungeons.listeners;

import dev.lone.itemsadder.api.CustomStack;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class CreatorWandListener implements Listener {

    private final String dimensionName;
    private boolean click = false;

    @Getter
    public static Location MobPos;

    public CreatorWandListener(String dimensionName) {
        this.dimensionName = dimensionName;
    }

    @EventHandler
    public void OnWandClick (PlayerInteractEvent event) {

        Player player = event.getPlayer();
        World world = player.getWorld();
        CustomStack customStack = CustomStack.byItemStack(player.getInventory().getItemInMainHand());

        if (world.getName().equals(dimensionName)){
            if (!click && event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
                if (customStack != null && customStack.getId().equals("mob_wand")) {
                    Location mobPos = event.getClickedBlock().getLocation();
                    MobPos = mobPos;
                    player.sendMessage("point d'apparition du mob définit a : x," + mobPos.getBlockX() + " y," + mobPos.getBlockY() + " z," + mobPos.getBlockZ() );
                    click = true;
                }
            }

            else if (click && event.getAction() != org.bukkit.event.block.Action.LEFT_CLICK_BLOCK || event.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK ) {
                click = false;
            }
        }
    }
}