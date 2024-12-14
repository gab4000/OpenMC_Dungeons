package fr.openmc.core.features.dungeons.data;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.commands.CommandsManager;
import fr.openmc.core.features.dungeons.commands.DungeonsCommands;
import fr.openmc.core.features.dungeons.listeners.CreatorWandListener;
import fr.openmc.core.features.dungeons.listeners.NaturalMobSpawnListener;
import fr.openmc.core.features.dungeons.listeners.MobSpawnZoneListener;
import fr.openmc.core.features.dungeons.listeners.items.ItemsBreakListener;
import fr.openmc.core.features.dungeons.listeners.items.PlayerDeathListener;
import org.bukkit.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class DungeonManager implements Listener {
	
	OMCPlugin plugin;
	Server server;
	
	public DungeonManager(OMCPlugin plugin) {
		this.plugin = plugin;
		this.server = plugin.getServer();
		
		init();
		
		CommandsManager.getHandler().register(
				new DungeonsCommands(plugin)
		);
	}
	
	public void init() {
		
		createDungeonDim();
		
		registerEvents(
				new NaturalMobSpawnListener("Dungeons"),
				new CreatorWandListener("Dungeons"),
				new MobSpawnZoneListener(plugin),
				new PlayerDeathListener(),
				new ItemsBreakListener()
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
	
	private void registerEvents(Listener... args) {
		Server server = Bukkit.getServer();
		JavaPlugin plugin = OMCPlugin.getInstance();
		for (Listener listener : args) {
			server.getPluginManager().registerEvents(listener, plugin);
		}
	}
}
