package fr.openmc.core.features.dungeons.menus;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dungeons.DungeonList;
import fr.openmc.core.utils.chronometer.Chronometer;
import fr.openmc.core.utils.chronometer.ChronometerType;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import lombok.Getter;
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
import java.util.UUID;

import static fr.openmc.core.features.dungeons.data.DungeonManager.config;
import static fr.openmc.core.features.dungeons.data.DungeonManager.saveReloadConfig;

public class ExplorerMenu extends Menu {
	
	@Getter
	public static Map<UUID, Integer> dungeonSoloCondition = new HashMap<>();
	
	public static OMCPlugin plugin;
	Player player;
	
	
	public ExplorerMenu(Player player) {
		super(player);
		this.player = player;
	}
	
	public static void tpAvailableDungeon(Player player, DungeonList dungeons) {
		
		if (config.getConfigurationSection("dungeon." + "dungeon_places.") == null) {
			MessagesManager.sendMessage(player, Component.text("§4Erreur lors de la téléportation vers le donjon"), Prefix.DUNGEON, MessageType.ERROR, false);
			return;
		}
		
		if (config.getConfigurationSection("dungeon." + "team.") != null && ! dungeons.equals(DungeonList.dungeon_training)) {
			for (String team : config.getConfigurationSection("dungeon." + "team.").getKeys(false)) {
				String basePath = "dungeon." + "team." + team;
				if (config.getStringList(basePath + ".player_in_team").contains(player.getName())) {
					if (team.equals(player.getName())) {
						for (String dungeon : config.getConfigurationSection("dungeon." + "dungeon_places." + dungeons + ".").getKeys(false)) {
							String path = "dungeon." + "dungeon_places." + dungeons + "." + dungeon;
							
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
										for (String playerInTeam : config.getStringList(basePath + ".player_in_team")) {
											Player teamMate = Bukkit.getPlayer(playerInTeam);
											Location tp = new Location(Bukkit.getWorld("Dungeons"), x, y, z);
											String dungeonPath = "dungeon." + "players_in_dungeon." + teamMate.getName();
											teamMate.teleport(tp);
											config.set(dungeonPath + ".dungeon", String.valueOf(dungeons));
											config.set(dungeonPath + ".places", dungeon);
											config.set(dungeonPath + ".states", "alive");
											config.set(basePath + ".remain", dungeons.getKillToFinishCondition());
											Chronometer.startChronometer(teamMate, "dungeons", dungeons.getTime(), ChronometerType.ACTION_BAR, "temps : %sec%", ChronometerType.ACTION_BAR, "%null%");
										}
										saveReloadConfig();
									} else {
										MessagesManager.sendMessage(player, Component.text("§4Erreur"), Prefix.DUNGEON, MessageType.ERROR, false);
									}
									break;
								}
							}
							
							if (location == null) {
								plugin.getLogger().info("§4ERROR : a location in the dungeon.yml incorrectly initialized or right");
							}
						}
					} else {
						MessagesManager.sendMessage(player, Component.text("§4Vous n'êtes pas le chef de votre team"), Prefix.DUNGEON, MessageType.ERROR, false);
					}
					return;
				}
			}
		}
		
		for (String dungeon : config.getConfigurationSection("dungeon." + "dungeon_places." + dungeons + ".").getKeys(false)) {
			String path = "dungeon." + "dungeon_places." + dungeons + "." + dungeon;
			
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
						config.set(dungeonPath + ".dungeon", String.valueOf(dungeons));
						config.set(dungeonPath + ".places", dungeon);
						config.set(dungeonPath + ".states", "alive");
						if (dungeons.getKillToFinishCondition() != - 1) {
							dungeonSoloCondition.put(player.getUniqueId(), dungeons.getKillToFinishCondition());
						}
						Chronometer.startChronometer(player, "dungeons", dungeons.getTime(), ChronometerType.ACTION_BAR, "temps : %sec%", ChronometerType.ACTION_BAR, "%null%");
						saveReloadConfig();
					} else {
						MessagesManager.sendMessage(player, Component.text("§4Erreur"), Prefix.DUNGEON, MessageType.ERROR, false);
					}
					break;
				}
			}
			
			if (location == null) {
				plugin.getLogger().info("§4 ERROR : a location in the dungeon.yml incorrectly initialized or right");
			}
		}
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
	public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
	}
	
	@Override
	public @NotNull Map<Integer, ItemStack> getContent() {
		Map<Integer, ItemStack> map = new HashMap<>();
		
		map.put(13, new ItemBuilder(this, Material.PAPER, itemMeta -> {
			itemMeta.setDisplayName(DungeonList.dungeon_training.getDungeonName());
		}).setOnClick(inventoryClickEvent -> {
			tpAvailableDungeon(player, DungeonList.dungeon_training);
			getOwner().closeInventory();
		}));
		
		map.put(15, new ItemBuilder(this, Material.PAPER, itemMeta -> {
			itemMeta.setDisplayName("Dungeons");
		}).setNextMenu(new DungeonsChoiceMenu(player)));
		
		return map;
	}
}
