package fr.openmc.core.features.dungeons.listeners;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dungeons.data.DungeonManager;
import fr.openmc.core.features.dungeons.commands.DungeonsCommands;
import fr.openmc.core.features.dungeons.menus.ExploreurMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static fr.openmc.core.features.dungeons.data.DungeonManager.config;

public class PlayerActionListener implements Listener {

    OMCPlugin plugin;

    DungeonsCommands dungeonsCommands;

    public PlayerActionListener (OMCPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void OnPNJInteract (PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (player.getWorld()!= Bukkit.getWorld("Dungeons")){return;}

        Entity entity = event.getRightClicked();

        if (entity != null){
            if (entity.getCustomName().equals("test")){
                ExploreurMenu menu = new ExploreurMenu(player, plugin);
                menu.open();
            }
        }
    }

    @EventHandler
    public void playerDisconnect (PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (player.getWorld() != Bukkit.getWorld("Dungeons")) {
            return;
        }

        player.getLocation().set(0, 100, 0);
        String path = "dungeon." + "players_in_dungeon." + player.getName();

        for (String players : config.getConfigurationSection(path).getKeys(false)){
            if (players == null){
                return;
            }

            String dungeonName = config.getString(path + ".dungeon");
            String dungeonPlace = config.getString(path + ".places");

            String dungeonPath = "dungeon." + "dungeon_places." + dungeonName + dungeonPlace;

            config.set(path, null);
            if (!config.getConfigurationSection("dungeon." + "players_in_dungeon.").contains(dungeonPlace)){
                config.set(dungeonPath + "available", true);
            }
            dungeonsCommands.leaveTeam(player.getName());
            DungeonManager.saveReloadConfig();
            //TODO peut-être faire une backup de l'inventaire avant le donjon et la set sur le joueur si celui-ci se déconnect dans donjon
            //TODO sauf si le donjon de celui-ci est l'entrainement
        }
    }
}
