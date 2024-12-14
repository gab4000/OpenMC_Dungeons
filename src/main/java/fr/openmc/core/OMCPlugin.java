package fr.openmc.core;

import dev.xernas.menulib.MenuLib;
import fr.openmc.core.commands.CommandsManager;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.dungeons.data.DungeonManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.commands.utils.SpawnManager;
import fr.openmc.core.features.skills.SkillsManager;
import fr.openmc.core.listeners.ListenersManager;
import fr.openmc.core.utils.LuckPermsAPI;
import fr.openmc.core.utils.database.DatabaseManager;
import fr.openmc.core.utils.MotdUtils;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.raidstone.wgevents.WorldGuardEvents;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;

public final class OMCPlugin extends JavaPlugin {
    @Getter static OMCPlugin instance;
    @Getter static FileConfiguration configs;
    private DatabaseManager dbManager;
    FileConfiguration dungeonConfig;
    File dungeonFile;

    public LuckPerms lpApi;

    @Override
    public void onEnable() {
        instance = this;

        /* CONFIG */
        saveDefaultConfig();
        configs = this.getConfig();
        dungeonFile = new File(instance.getDataFolder(), "dungeon.yml");

        /* EXTERNALS */
        MenuLib.init(this);
        new LuckPermsAPI(this);
        new WorldGuardEvents().enable(this);

        /* MANAGERS */
        dbManager = new DatabaseManager();
        new CommandsManager();
        new SpawnManager(this);
        new CityManager();
        new ListenersManager();
        new EconomyManager();
        new MotdUtils(this);
        new DungeonManager(this);

        getLogger().info("Plugin activé");
    }

    @Override
    public void onDisable() {
        try {
            dbManager.close();
        } catch (SQLException e) {
            getLogger().severe("Impossible de fermer la connexion à la base de données");
        }
        dungeonConfig = YamlConfiguration.loadConfiguration(dungeonFile);
        dungeonConfig.set("dungeon." + "team.", null);
        getLogger().info("Plugin désactivé");
    }

    public static void registerEvents(Listener... listeners) {
        for (Listener listener : listeners) {
            instance.getServer().getPluginManager().registerEvents(listener, instance);
        }
    }
}
