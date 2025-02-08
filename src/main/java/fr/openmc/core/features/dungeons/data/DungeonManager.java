package fr.openmc.core.features.dungeons.data;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.commands.CommandsManager;
import fr.openmc.core.features.dungeons.commands.DungeonsCommands;
import fr.openmc.core.features.dungeons.levels.DungeonLevelsListener;
import fr.openmc.core.features.dungeons.listeners.CreatorWandListener;
import fr.openmc.core.features.dungeons.listeners.NaturalMobSpawnListener;
import fr.openmc.core.features.dungeons.listeners.MobSpawnZoneListener;
import fr.openmc.core.features.dungeons.listeners.PlayerActionListener;
import fr.openmc.core.features.dungeons.items.ItemsBreakListener;
import fr.openmc.core.features.dungeons.menus.DungeonInventoryMenu;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static fr.openmc.core.OMCPlugin.registerEvents;

public class DungeonManager {

    OMCPlugin plugin;
    Server server;
    static Connection conn;

    @Getter
    public static FileConfiguration config;
    public static File file;
    public static FileConfiguration dl_config;
    public static File dl_file;
    public static Location DungeonSpawn;
    public static World dungeons;

    public DungeonManager(OMCPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
        DungeonSpawn = new Location(Bukkit.getWorld("Dungeons"), 0.5, 60, 0.5);

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
        dungeons = Bukkit.getWorld("Dungeons");
        plugin.getLogger().info("Dungeon dimension : " + dungeons);

    }

    public static void init_db(Connection conn) throws SQLException {
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS dungeon (" +
                "player_uuid VARCHAR(36) PRIMARY KEY, " + // Un seul enregistrement par joueur
                "inventory BLOB" +  // Stocke la map sous forme de JSON
                ")"
        ).executeUpdate();
        OMCPlugin.getInstance().getLogger().info("Initialisation des maps");
    }

    /**
     * Début de la partie réaliser avec l'aide de ChatGPT
     * */

    public static Map<Integer, ItemStack> loadInventoryFromDatabase(UUID playerUUID, Connection conn) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("SELECT inventory FROM dungeon WHERE player_uuid = ?");
        statement.setString(1, playerUUID.toString());
        ResultSet results = statement.executeQuery();

        if (results.next()) {
            String inventoryBase64 = results.getString("inventory");
            return deserializeInventory(inventoryBase64);
        }
        return new HashMap<>();
    }

    public static void saveInventoryToDatabase(UUID playerUUID, Map<Integer, ItemStack> inventory, Connection conn) throws SQLException {
        String inventoryBase64 = serializeInventory(inventory);

        PreparedStatement statement = conn.prepareStatement(
                "INSERT INTO dungeon (player_uuid, inventory) VALUES (?, ?) " +
                        "ON DUPLICATE KEY UPDATE inventory = VALUES(inventory)");

        statement.setString(1, playerUUID.toString());
        statement.setString(2, inventoryBase64); // 🔥 Stocke en Base64
        statement.executeUpdate();
    }

    public static String serializeItemStack(ItemStack item) {
        if (item == null) return "";
        YamlConfiguration config = new YamlConfiguration();
        config.set("item", item.serialize());
        return Base64.getEncoder().encodeToString(config.saveToString().getBytes());
    }

    public static String serializeInventory(Map<Integer, ItemStack> inventory) {
        Map<Integer, String> serializedMap = new HashMap<>();
        for (Map.Entry<Integer, ItemStack> entry : inventory.entrySet()) {
            serializedMap.put(entry.getKey(), serializeItemStack(entry.getValue()));
        }
        return Base64.getEncoder().encodeToString(new Gson().toJson(serializedMap).getBytes());
    }

    public static ItemStack deserializeItemStack(String base64) {
        if (base64 == null || base64.isEmpty()) return null;

        // Décoder Base64 en String
        String yamlData = new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);

        // Utiliser un StringReader pour charger la configuration YAML
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new StringReader(yamlData));

        // Convertir la section "item" en ItemStack
        return ItemStack.deserialize(config.getConfigurationSection("item").getValues(false));
    }

    public static Map<Integer, ItemStack> deserializeInventory(String base64) {
        if (base64 == null || base64.isEmpty()) return new HashMap<>();
        String json = new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);
        Map<Integer, String> serializedMap = new Gson().fromJson(json, new TypeToken<Map<Integer, String>>() {}.getType());

        Map<Integer, ItemStack> inventory = new HashMap<>();
        for (Map.Entry<Integer, String> entry : serializedMap.entrySet()) {
            inventory.put(entry.getKey(), deserializeItemStack(entry.getValue()));
        }
        return inventory;
    }

    /**
     * Fin de la Partie réaliser avec ChatGPT
     **/

    public void init() {

        if (config.getBoolean("dungeon." + "yml_auto_update.")) {
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
                new DungeonLevelsListener(),
                new DungeonInventoryMenu(null)
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