package fr.openmc.core.features.dungeons.data;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.commands.CommandsManager;
import fr.openmc.core.features.dungeons.commands.DungeonsCommands;
import fr.openmc.core.features.dungeons.levels.DungeonLevelsListener;
import fr.openmc.core.features.dungeons.listeners.CreatorWandListener;
import fr.openmc.core.features.dungeons.listeners.NaturalMobSpawnListener;
import fr.openmc.core.features.dungeons.listeners.MobSpawnZoneListener;
import fr.openmc.core.features.dungeons.listeners.PlayerActionListener;
import fr.openmc.core.features.dungeons.items.ItemsBreakListener;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import static fr.openmc.core.OMCPlugin.registerEvents;

public class DungeonManager {

    OMCPlugin plugin;
    Server server;

    @Getter
    public static FileConfiguration config;
    public static File file;
    public static FileConfiguration dl_config;
    public static File dl_file;
    public static Location DungeonSpawn;

    public DungeonManager(OMCPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
        DungeonSpawn = new Location(Bukkit.getWorld("Dungeons"), 0, 100, 0);

        CommandsManager.getHandler().register(
                new DungeonsCommands(plugin)
        );

        file = new File(plugin.getDataFolder(), "dungeon.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                config = YamlConfiguration.loadConfiguration(file);
                DungeonsCommands.updateDungeonYML(plugin);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);

        dl_file = new File(plugin.getDataFolder(), "dungeon_levels.yml");
        if (!dl_file.exists()) {
            try {
                dl_file.createNewFile();
                dl_config = YamlConfiguration.loadConfiguration(dl_file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        dl_config = YamlConfiguration.loadConfiguration(dl_file);

        init();

    }

    public void init() {

        if (config.getBoolean("dungeon." + "yml_auto_update.")){
            DungeonsCommands.updateDungeonYML(plugin);
            config.set("dungeon." + "yml_auto_update.", false);
        }

        createDungeonDim();

        registerEvents(
                new NaturalMobSpawnListener("Dungeons"),
                new CreatorWandListener("Dungeons"),
                new MobSpawnZoneListener(plugin),
                new PlayerActionListener(plugin),
                new ItemsBreakListener(),
                new DungeonLevelsListener()
        );
    }

    public void createDungeonDim() {

        WorldCreator creator = new WorldCreator("Dungeons");
        creator.environment(World.Environment.NORMAL);
        creator.type(WorldType.FLAT);
        creator.generateStructures(false);
        creator.generatorSettings("{\"layers\": [{\"block\": \"minecraft:air\",\"height\":1}], \"biome\":\"plains\"}");
        World Manson = creator.createWorld();

        Manson.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        Manson.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        Manson.setGameRule(GameRule.DISABLE_RAIDS, true);
        Manson.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
        Manson.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
        Manson.setGameRule(GameRule.NATURAL_REGENERATION, false);
        Manson.setGameRule(GameRule.DO_FIRE_TICK, false);

        plugin.getLogger().info("Dungeon dimension created successfully!");
    }

    public static void saveReloadConfig() {
        try {
            config.save(file);
            config = YamlConfiguration.loadConfiguration(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveReloadDLConfig() {
        try {
            dl_config.save(dl_file);
            dl_config = YamlConfiguration.loadConfiguration(dl_file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
