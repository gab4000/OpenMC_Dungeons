package fr.openmc.core.features.dungeons.listeners;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class NaturalMobSpawnListener implements Listener {

    private final String dimensionName;

    public NaturalMobSpawnListener(String dimensionName) {
        this.dimensionName = dimensionName;
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {

        World world = event.getLocation().getWorld();
        if (world != null && world.getName().equals(dimensionName)) {
            CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();
            if (reason == CreatureSpawnEvent.SpawnReason.NATURAL) {
                event.setCancelled(true);
            }
        }
    }
}