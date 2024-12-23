package fr.openmc.core.features.dungeons.menus;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dungeons.data.DungeonManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static fr.openmc.core.features.dungeons.data.DungeonManager.config;

public class ExploreurMenu extends Menu {

    OMCPlugin plugin;
    Player player;

    public ExploreurMenu(Player player, OMCPlugin plugin) {
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
            itemMeta.setDisplayName("Training dungeon");
            tpAvailableDungeon(player, "dungeon_training"); //TODO mis icic pour test
        }).setCloseButton());

        map.put(15, new ItemBuilder(this, Material.PAPER, itemMeta -> {
            itemMeta.setDisplayName("Dungeons");
        }).setNextMenu(new DungeonsChoiceMenu(player)));

        return map;
    }

    public void tpAvailableDungeon(Player player, String dungeons) {

        for (String dungeon : config.getConfigurationSection("dungeon." + "dungeon_places." + "dungeon_training").getKeys(false)){
            String path = "dungeon." + "dungeon_places." + "dungeon_training." + dungeon;

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
                        DungeonManager.saveReloadConfig();
                        break;
                    }
                }
            }

            if (location == null) {
                plugin.getLogger().info("§4 ERROR : a location in the dungeon.yml incorrectly initialized or right");
            }
        }
    }
}
