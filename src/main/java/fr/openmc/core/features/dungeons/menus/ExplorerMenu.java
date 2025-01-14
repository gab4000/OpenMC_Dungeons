package fr.openmc.core.features.dungeons.menus;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dungeons.DungeonList;
import fr.openmc.core.features.dungeons.commands.DungeonsCommands;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static fr.openmc.core.features.dungeons.data.DungeonManager.*;

public class ExplorerMenu extends Menu {

    static OMCPlugin plugin;
    Player player;

    public ExplorerMenu(Player player, OMCPlugin plugin) {
        super(player);
        this.player = player;
        this.plugin = plugin;
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
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {}

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> map = new HashMap<>();

        map.put(13, new ItemBuilder(this, Material.PAPER, itemMeta -> {
            itemMeta.setDisplayName(DungeonList.dungeon_training.getDungeonName());
        }).setOnClick(inventoryClickEvent -> {
            tpAvailableDungeon(player, DungeonList.dungeon_training + ".");
            getOwner().closeInventory();
        }));

        map.put(15, new ItemBuilder(this, Material.PAPER, itemMeta -> {
            itemMeta.setDisplayName("Dungeons");
        }).setNextMenu(new DungeonsChoiceMenu(player)));

        return map;
    }

    public static void tpAvailableDungeon(Player player, String dungeons) {

        if (config.getConfigurationSection("dungeon." + "dungeon_places.")==null){
            MessagesManager.sendMessageType(player, Component.text("§4Erreur lors de la téléportation vers le donjon"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        if (config.getConfigurationSection("dungeon." + "team.")!=null && !dungeons.equals(DungeonList.dungeon_training + ".")){
            for (String team : config.getConfigurationSection("dungeon." + "team.").getKeys(false)){
                String basePath = "dungeon." + "team." + team;
                if (config.getStringList(basePath + ".player_in_team").contains(player.getName())){
                    if (team.equals(player.getName())) {
                        for (String dungeon : config.getConfigurationSection("dungeon." + "dungeon_places." + dungeons).getKeys(false)){
                            String path = "dungeon." + "dungeon_places." + dungeons + dungeon;

                            boolean available = config.getBoolean(path + ".available");
                            String location = config.getString(path + ".spawn_co");

                            if (location != null && available) {

                                String[] parts = location.split(",");

                                if (parts.length >= 3) {
                                    double x = Double.parseDouble(parts[0]);
                                    double y = Double.parseDouble(parts[1]);
                                    double z = Double.parseDouble(parts[2]);

                                    if (Bukkit.getWorld("Dungeons") != null) {
                                        config.set(path + ".available", false);
                                        for (String playerInTeam : config.getStringList(basePath + ".player_in_team")){
                                            Player target = Bukkit.getPlayer(playerInTeam);
                                            Location tp = new Location(Bukkit.getWorld("Dungeons"), x, y, z);
                                            String dungeonPath = "dungeon." + "players_in_dungeon." + target.getName();
                                            target.teleport(tp);
                                            config.set(dungeonPath + ".dungeon", dungeons);
                                            config.set(dungeonPath + ".places", dungeon);
                                            config.set(dungeonPath + ".states", "alive");
                                        }
                                        saveReloadConfig();
                                    } else {
                                        MessagesManager.sendMessageType(player, Component.text("§4Erreur"), Prefix.DUNGEON, MessageType.ERROR, false);
                                    }
                                    break;
                                }
                            }

                            if (location == null) {
                                plugin.getLogger().info("§4ERROR : a location in the dungeon.yml incorrectly initialized or right");
                            }
                        }
                    } else {
                        MessagesManager.sendMessageType(player, Component.text("§4Vous n'êtes pas le chef de votre team"), Prefix.DUNGEON, MessageType.ERROR, false);
                    }
                    return;
                }
            }
        }

        for (String dungeon : config.getConfigurationSection("dungeon." + "dungeon_places." + dungeons).getKeys(false)){
            String path = "dungeon." + "dungeon_places." + dungeons + dungeon;

            boolean available = config.getBoolean(path + ".available");
            String location = config.getString(path + ".spawn_co");

            if (location != null && available) {

                String[] parts = location.split(",");

                if (parts.length >= 3) {
                    double x = Double.parseDouble(parts[0]);
                    double y = Double.parseDouble(parts[1]);
                    double z = Double.parseDouble(parts[2]);

                    if (Bukkit.getWorld("Dungeons") != null) {
                        Location tp = new Location(Bukkit.getWorld("Dungeons"), x, y, z);
                        String dungeonPath = "dungeon." + "players_in_dungeon." + player.getName();
                        player.teleport(tp);
                        config.set(path + ".available", false);
                        config.set(dungeonPath + ".dungeon", dungeons);
                        config.set(dungeonPath + ".places", dungeon);
                        config.set(dungeonPath + ".states", "alive");
                        saveReloadConfig();
                    } else {
                        MessagesManager.sendMessageType(player, Component.text("§4Erreur"), Prefix.DUNGEON, MessageType.ERROR, false);
                    }
                    break;
                }
            }

            if (location == null) {
                plugin.getLogger().info("§4 ERROR : a location in the dungeon.yml incorrectly initialized or right");
            }
        }
    }
}
