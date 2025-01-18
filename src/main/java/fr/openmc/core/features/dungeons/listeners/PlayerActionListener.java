package fr.openmc.core.features.dungeons.listeners;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dungeons.commands.DungeonsCommands;
import fr.openmc.core.features.dungeons.effects.CorruptionEffect;
import fr.openmc.core.features.dungeons.menus.ExplorerMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

import static fr.openmc.core.features.dungeons.commands.DungeonsCommands.dungeons;
import static fr.openmc.core.features.dungeons.data.DungeonManager.*;
import static fr.openmc.core.features.dungeons.menus.ExplorerMenu.dungeonSoloCondition;

public class PlayerActionListener implements Listener {

    OMCPlugin plugin;

    public PlayerActionListener (OMCPlugin plugin) {
        this.plugin = plugin;
        NoPlayerAliveInTeam();
    }

    @EventHandler
    public void OnPNJInteract (PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (player.getWorld()!= Bukkit.getWorld("Dungeons")){return;}

        Entity entity = event.getRightClicked();

        if (entity != null){
            if (entity.getCustomName()==null){return;}
            if (entity.getCustomName().equals("test")){ //TODO a changer plus tard
                ExplorerMenu menu = new ExplorerMenu(player);
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

        player.teleport(DungeonSpawn);
        String path = "dungeon." + "players_in_dungeon." + player.getName();
        if (config.get(path)==null){return;}
        String dungeonName = config.getString(path + ".dungeon");
        String dungeonPlace = config.getString(path + ".places");

        if (!IsInTeam(player)){
            String dungeonPath = "dungeon." + "dungeon_places." + dungeonName + "." + dungeonPlace;
            config.set(dungeonPath + ".available", true);
            dungeonSoloCondition.remove(player.getUniqueId());
        } else {
            DungeonsCommands.leaveTeam(player.getName());
        }

        config.set(path, null);
        saveReloadConfig();

    }

    @EventHandler
    public void onDamageTaken(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player){
            Player player = Bukkit.getPlayer(event.getEntity().getUniqueId());
            if (!DungeonsCommands.playerIsInDungeon(player) && player.getWorld().equals(dungeons)){
                if (event.getDamage() >0.1){
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerBreakBlock (BlockBreakEvent event){
        Player trigger = event.getPlayer();
        if (!trigger.isOp() && trigger.getWorld().equals(dungeons)){
            event.setCancelled(true);
            MessagesManager.sendMessageType(trigger, Component.text("§4vous ne pouvez pas détruire de block"), Prefix.DUNGEON, MessageType.ERROR, false);
        }
    }

    @EventHandler
    private void onPlayerPlaceBlock (BlockPlaceEvent event){
        Player trigger = event.getPlayer();
        if (!trigger.isOp() && trigger.getWorld().equals(dungeons)){
            event.setCancelled(true);
            MessagesManager.sendMessageType(trigger, Component.text("§4vous ne pouvez pas posez de block"), Prefix.DUNGEON, MessageType.ERROR, false);
        }
    }

    @EventHandler
    public void onPlayerDie (PlayerDeathEvent event){
        Player player = event.getPlayer();
        if (!player.getWorld().equals(dungeons)){return;}
        CustomStack item = CustomStack.getInstance("dungeon:corrupted_resurrection_stone");
        ItemStack stone = item.getItemStack();
        if (stone == null ) {
            plugin.getLogger().severe("item not defined at : " +
                    "fr.openmc.core.features.dungeons.listeners.PlayerActionListener.ItemsDetect(PlayerActionListener.java:122)"
            );
        }

        if (player.getInventory().contains(stone)) {
            event.setKeepInventory(true);
            event.setCancelled(true);
            player.setHealth(6);
            player.getInventory().removeItem(stone);
            CorruptionEffect.applyCorruptionEffect(player,10);
            return;
        }
        SpectatorMode(player, event);
    }

    @EventHandler
    public void PlayerKillMob (EntityDeathEvent event) {
        if (event.getEntity().getWorld().equals(dungeons)){
            Player player = event.getEntity().getKiller();
            if (event.getEntity()==player || player==null){return;}
            if (IsInTeam(player)){
                for (String team : config.getConfigurationSection("dungeon." + "team.").getKeys(false)){
                    String path = "dungeon." + "team." + team;
                    if (config.contains(path + player.getName())){
                        double remain = config.getDouble(path + ".remain") - 1;
                        if (remain == 0){
                            TeamWinDungeon(team);
                            return;
                        }
                        if (remain > 0){
                            config.set(path + ".remain", remain);
                            return;
                        }
                    }
                }
            } else {
                if (dungeonSoloCondition.containsKey(player.getUniqueId())){
                    int remain = dungeonSoloCondition.get(player.getUniqueId()) - 1;
                    if (remain == 0){
                        PlayerWinDungeon(player);
                        return;
                    }
                    if (remain > 0){
                        dungeonSoloCondition.replace(player.getUniqueId(), remain);
                    }
                }
            }
        }
    }

    private void SpectatorMode (Player player, PlayerDeathEvent event) {
        if (DungeonsCommands.playerIsInDungeon(player)){
            if (config.getConfigurationSection("dungeon." + "team.") == null || !IsInTeam(player)){
                PlayerLooseDungeon(player);
                event.setCancelled(true);
                player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getDefaultValue());
                return;
            }
            if (IsInTeam(player)){

                for (String team : config.getConfigurationSection("dungeon." + "team.").getKeys(false)){
                    String path = "dnugeon." + "team." + team + ".player_in_team";

                    if (config.getStringList(path).contains(player.getName())) {

                        if (config.getStringList(path).size()<=1) {
                            PlayerLooseDungeon(player);
                            event.setCancelled(true);
                            player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getDefaultValue());
                            break;
                        }

                        for (String playerInTeam : config.getStringList(path)) {
                            if (IsAlive(playerInTeam) && !Objects.equals(playerInTeam, player.getName())){
                                event.setCancelled(true);
                                player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getDefaultValue());
                                player.setGameMode(GameMode.SPECTATOR);
                                config.set("dungeon." + "players_in_dungeon." + player.getName() + ".states", "spec");
                                saveReloadConfig();
                                break;
                            }
                        }
                        TeamLooseDungeon(team);
                        event.setCancelled(true);
                        player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getDefaultValue());
                        break;
                    }
                }
            }
        }
    }

    public void NoPlayerAliveInTeam () {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (config.getConfigurationSection("dungeon." + "team.") == null){
                    return;
                }
                for (String team : config.getConfigurationSection("dungeon." + "team.").getKeys(false)){
                    String path = "dungeon." + "team." + team + ".player_in_team";

                    if (config.getStringList(path).size()<=1){
                        continue;
                    }

                    for (String playerInTeam : config.getStringList(path)) {
                        if (IsAlive(playerInTeam)){
                            return;
                        }
                    }
                    TeamLooseDungeon(team);
                }
            }
        };
    }

    @EventHandler
    public void SpectatorMoveLimit (PlayerMoveEvent event) {
        if (config.getConfigurationSection("dungeon." + "players_in_dungeon.") == null){
            return;
        }
        Player player = event.getPlayer();
        if (!IsAlive(player.getName()) && player.getWorld().equals(dungeons)){
            String dungeon = config.getString("dungeon." + "players_in_dungeon." + player.getName() + ".dungeon");
            String place = "." + config.getString("dungeon." + "players_in_dungeon." + player.getName() + ".places");
            String path = "dungeon." + "dungeon_places." + dungeon + place ;

            String loc = config.getString(path + ".spawn_co");

            if (loc != null){
                String[] parts = loc.split(",");

                if (parts.length >= 3) {
                    double x = Double.parseDouble(parts[0]);
                    double y = Double.parseDouble(parts[1]);
                    double z = Double.parseDouble(parts[2]);
                    Location zoneLocation = new Location(Bukkit.getWorld("Dungeons"), x, y, z);
                    double distance = player.getLocation().distance(zoneLocation);

                    if (distance >= 250){
                        player.teleport(zoneLocation);
                    }
                }
            }
        }
    }

    public void PlayerLooseDungeon (Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(DungeonSpawn);
        config.set("dungeon." + "players_in_dungeon." + player.getName(), null);
        saveReloadConfig();
        //TODO mettre une sorte d'image sur l'écran du joueur indiquand qu'il a perdu
    }

    public void TeamLooseDungeon (String teamName) {
        if (config.getConfigurationSection("dungeon." + "team.") == null){
            plugin.getLogger().severe("erreur lors de l'évènement TeamLooseDungeon");
        }
        for (String playerInTeam : config.getStringList("dungeon." + "team." + teamName + ".player_in_team")) {
            Player player = Bukkit.getPlayer(playerInTeam);
            player.setGameMode(GameMode.SURVIVAL);
            player.teleport(DungeonSpawn);
            config.set("dungeon." + "players_in_dungeon." + player.getName(), null);
            saveReloadConfig();
            //TODO mettre une sorte d'image sur l'écran du joueur indiquand qu'il a perdu
        }
    }

    public void PlayerWinDungeon (Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(DungeonSpawn);
        config.set("dungeon." + "players_in_dungeon." + player.getName(), null);
        saveReloadConfig();
        //TODO mettre une sorte d'image sur l'écran du joueur indiquand qu'il a gagneé + récompense
    }

    public void TeamWinDungeon (String teamName) {
        if (config.getConfigurationSection("dungeon." + "team.") == null){
            plugin.getLogger().severe("erreur lors de l'évènement TeamWinDungeon");
        }
        for (String playerInTeam : config.getStringList("dungeon." + "team." + teamName + ".player_in_team")) {
            Player player = Bukkit.getPlayer(playerInTeam);
            player.setGameMode(GameMode.SURVIVAL);
            player.teleport(DungeonSpawn);
            config.set("dungeon." + "players_in_dungeon." + player.getName(), null);
            saveReloadConfig();
            //TODO mettre une sorte d'image sur l'écran du joueur indiquand qu'il a gagneé + récompense
        }
    }

    boolean IsAlive (String playerName) {
        return Objects.equals(config.getString("dungeon." + "players_in_dungeon" + playerName + ".states"), "alive");
    }

    boolean IsInTeam (Player player){
        if (config.getConfigurationSection("dungeon." + "team.")==null){
            return false;
        }
        return config.contains("dungeon." + "team." + player.getName());
    }
}