package fr.openmc.core.features.dungeons.data;

import fr.openmc.core.OMCPlugin;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class PlayerDataSaver implements Listener {

    private final OMCPlugin plugin;

    public PlayerDataSaver(OMCPlugin plugin) {
        this.plugin = plugin;
    }

    //TODO utiliser la DB plus tard si besoin

    public void savePlayerData(Player player, String dimensionName) {

        try {
            File dataFile = new File(plugin.getDataFolder(), "player_data/" + player.getUniqueId() + "_" + dimensionName + ".dat");
            if (!dataFile.getParentFile().exists()) {
                dataFile.getParentFile().mkdirs();
            }
            player.saveData();

            File defaultDataFile = new File(plugin.getServer().getWorld("world").getWorldFolder(), "playerdata/" + player.getUniqueId() + ".dat");
            if (defaultDataFile.exists()) {
                Files.copy(defaultDataFile.toPath(), dataFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadPlayerData(Player player, String dimensionName) {

        File dataFile = new File(plugin.getDataFolder(), "player_data/" + player.getUniqueId() + "_" + dimensionName + ".dat");
        if (dataFile.exists()) {
            try {
                File defaultDataFile = new File(plugin.getServer().getWorld("world").getWorldFolder(), "playerdata/" + player.getUniqueId() + ".dat");

                Files.copy(dataFile.toPath(), defaultDataFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                player.loadData();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //TODO Si le joueur arrive pour la 1er fois dans le dongeon
            //TODO Son inventaire, ect... seront réinitialisés

            player.getInventory().clear();

            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }

            player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getDefaultValue());
            player.setFoodLevel(20);
            player.setLevel(0);
            player.setSaturation(5.0f);
            player.setExp(0);
            player.setFireTicks(0);

            player.sendMessage("Initialisation de l'inventaire de dongeon. Si vous pocédiez déjà un inventaire de dongeon, contacter un modérateur !");
        }
    }
}