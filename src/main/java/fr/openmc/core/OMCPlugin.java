package fr.openmc.core;

import fr.openmc.api.menulib.MenuLib;
import fr.openmc.core.features.leaderboards.LeaderboardManager;
import fr.openmc.core.features.adminshop.AdminShopManager;
import fr.openmc.core.features.scoreboards.ScoreboardManager;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mascots.MascotsManager;
import fr.openmc.core.features.contest.managers.ContestManager;
import fr.openmc.core.features.contest.managers.ContestPlayerManager;
import fr.openmc.core.features.dungeons.data.DungeonManager;
import fr.openmc.core.features.economy.BankManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.commands.utils.SpawnManager;
import fr.openmc.core.features.friend.FriendManager;
import fr.openmc.core.features.homes.HomeUpgradeManager;
import fr.openmc.core.features.homes.HomesManager;
import fr.openmc.core.features.quests.QuestsManager;
import fr.openmc.core.features.scoreboards.TabList;
import fr.openmc.core.features.tpa.TPAManager;
import fr.openmc.core.features.updates.UpdateManager;
import fr.openmc.core.listeners.CubeListener;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.features.skills.SkillsManager;
import fr.openmc.core.features.skills.skill.passive.PassiveSkillsManager;
import fr.openmc.core.listeners.ListenersManager;
import fr.openmc.core.utils.LuckPermsAPI;
import fr.openmc.core.utils.PapiAPI;
import fr.openmc.core.utils.WorldGuardApi;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.database.DatabaseManager;
import fr.openmc.core.utils.MotdUtils;
import fr.openmc.core.utils.freeze.FreezeManager;
import fr.openmc.core.utils.translation.TranslationManager;
import lombok.Getter;
import net.raidstone.wgevents.WorldGuardEvents;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;
import static fr.openmc.core.features.dungeons.data.DungeonManager.config;

public class OMCPlugin extends JavaPlugin {
    @Getter static OMCPlugin instance;
    @Getter static FileConfiguration configs;
    @Getter static TranslationManager translationManager;
    private DatabaseManager dbManager;
	@Getter private PassiveSkillsManager passiveSkillsManager;

    @Override
    public void onEnable() {
        instance = this;

        /* CONFIG */
        saveDefaultConfig();
        configs = this.getConfig();

        /* EXTERNALS */
        MenuLib.init(this);
        new LuckPermsAPI();
        new PapiAPI();
        new WorldGuardApi();
        new WorldGuardEvents().enable(this);

        /* MANAGERS */
        dbManager = new DatabaseManager();
        new CommandsManager();
        CustomItemRegistry.init();
        ContestManager contestManager = new ContestManager(this);
        ContestPlayerManager contestPlayerManager = new ContestPlayerManager();
        new SpawnManager(this);
        new UpdateManager();
        new MascotsManager(this); // laisser avant CityManager
        new CityManager();
        new ListenersManager();
        new EconomyManager();
        new MailboxManager();
        new BankManager();
        new ScoreboardManager();
        new HomesManager();
        new HomeUpgradeManager(HomesManager.getInstance());
        new TPAManager();
        new FreezeManager();
        new FriendManager();
        new QuestsManager();
        new TabList();
        if (!OMCPlugin.isUnitTestVersion())
            new LeaderboardManager(this);
        new AdminShopManager(this);

        contestPlayerManager.setContestManager(contestManager); // else ContestPlayerManager crash because ContestManager is null
        contestManager.setContestPlayerManager(contestPlayerManager);
        new MotdUtils(this);
	    new DungeonManager(this);
	    new SkillsManager();
	    this.passiveSkillsManager = new PassiveSkillsManager();
	    this.passiveSkillsManager.activateSkills(this);
	    translationManager = new TranslationManager(this, new File(this.getDataFolder(), "translations"), "fr");
        translationManager.loadAllLanguages();
	    getLogger().info("Plugin activé");
    }

    @Override
    public void onDisable() {
        HomesManager.getInstance().saveHomesData();
        ContestManager.getInstance().saveContestData();
        ContestManager.getInstance().saveContestPlayerData();
        QuestsManager.getInstance().saveQuests();

        MascotsManager.saveMascots(MascotsManager.mascots);
        CityManager.saveFreeClaims(CityManager.freeClaim);
		
	    SkillsManager.savePlayerSkills(DatabaseManager.getConnection());
		
        CubeListener.clearCube(CubeListener.currentLocation);
		if (dbManager != null) {
            try {
                dbManager.close();
            } catch (SQLException e) {
                getLogger().severe("Impossible de fermer la connexion à la base de données");
            }
        }

        if (config.getConfigurationSection("dungeon." + "team") != null){ //TODO problème ici : "path is null"
            try {
                config.set("dungeon." + "team", null);
                getLogger().info("Dungeon Team reset");
            } catch (Exception exception){
                getLogger().info("Error during the reset of the team");
            }
        }
		
		this.passiveSkillsManager.deactivateSkills();
		
        //TODO remettre tous les donjons a 0

        getLogger().info("Plugin désactivé");
    }

    public static void registerEvents(Listener... listeners) {
        for (Listener listener : listeners) {
            instance.getServer().getPluginManager().registerEvents(listener, instance);
        }
    }

    public static boolean isUnitTestVersion() {
        return OMCPlugin.instance.getServer().getVersion().contains("MockBukkit");
    }
}
