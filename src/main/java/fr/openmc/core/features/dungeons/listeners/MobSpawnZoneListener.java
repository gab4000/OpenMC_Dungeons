package fr.openmc.core.features.dungeons.listeners;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dungeons.MOBIDS;
import fr.openmc.core.features.dungeons.data.DungeonManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import static fr.openmc.core.features.dungeons.data.DungeonManager.config;

public class MobSpawnZoneListener implements Listener {

    private final OMCPlugin plugin;
    World dungeons = Bukkit.getWorld("Dungeons");

    public MobSpawnZoneListener(OMCPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        World dungeons = Bukkit.getWorld("Dungeons");
        if (config.getConfigurationSection("dungeon." + "mob_spawn") == null || event.getPlayer().getWorld() != dungeons){
            return;
        }
        checkZones(event.getPlayer());
    }

    public void checkZones(Player player) {
        for (String point : config.getConfigurationSection("dungeon." + "mob_spawn").getKeys(false)) {
            String path = "dungeon."+ "mob_spawn." + point;

            double x = config.getDouble(path + ".x");
            double y = config.getDouble(path + ".y");
            double z = config.getDouble(path + ".z");
            String entity = (config.getString(path + ".mob"));
            boolean isActivated = config.getBoolean(path + ".activated");

            Location zoneLocation = new Location(dungeons, x, y, z);
            double distance = player.getLocation().distance(zoneLocation);

            if (distance > 25.0) {
                continue;
            }

            if (distance <= 10.0 && isActivated) {

                config.set(path + ".activated", false);
                player.sendMessage(entity);
                spawnMob(zoneLocation, entity);
                DungeonManager.saveReloadConfig();
                player.sendMessage("tout les tests sont bon !"); // message debug
            }
        }
    }

    private void spawnMob (Location location, String entity) {

//        // items
//        ItemStack helmet = new ItemStack(MobIDs.valueOf(entity).getHelmet());
//        ItemStack chestplate = new ItemStack(MobIDs.valueOf(entity).getChestplate());
//        ItemStack leggings = new ItemStack(MobIDs.valueOf(entity).getLeggings());
//        ItemStack boots = new ItemStack(MobIDs.valueOf(entity).getBoots());
//        ItemStack weapon = new ItemStack(MobIDs.valueOf(entity).getBoots());
//
//        // enchants
//        ItemMeta helmetMeta = helmet.getItemMeta();
//        helmetMeta.addEnchant(MobIDs.valueOf(entity).getHelmetEnchantment(), MobIDs.valueOf(entity).getHelmetLevel(), true);
//        ItemMeta chestplateMeta = chestplate.getItemMeta();
//        helmetMeta.addEnchant(MobIDs.valueOf(entity).getChestplateEnchantment(), MobIDs.valueOf(entity).getChestplateLevel(), true);
//        ItemMeta leggingsMeta = leggings.getItemMeta();
//        leggingsMeta.addEnchant(MobIDs.valueOf(entity).getLeggingsEnchantment(), MobIDs.valueOf(entity).getLeggingsLevel(), true);
//        ItemMeta bootsMeta = boots.getItemMeta();
//        bootsMeta.addEnchant(MobIDs.valueOf(entity).getBootsEnchantment(), MobIDs.valueOf(entity).getBootsLevel(), true);
//        ItemMeta weaponMeta = weapon.getItemMeta();
//        weaponMeta.addEnchant(MobIDs.valueOf(entity).getWeaponEnchantment(), MobIDs.valueOf(entity).getWeaponLevel(), true);
//
//        // data set
//        helmet.setItemMeta(helmetMeta);
//        chestplate.setItemMeta(chestplateMeta);
//        leggings.setItemMeta(leggingsMeta);
//        boots.setItemMeta(bootsMeta);
//        weapon.setItemMeta(weaponMeta);

        EntityType entityType = MOBIDS.getMobByName(entity);
        LivingEntity mob = (LivingEntity) dungeons.spawnEntity(location, entityType);

//        // attribute set
//        mob.setHealth(MobIDs.valueOf(entity).getHealth());
//        mob.getEquipment().setHelmet(helmet);
//        mob.getEquipment().setChestplate(chestplate);
//        mob.getEquipment().setLeggings(leggings);
//        mob.getEquipment().setBoots(boots);
//        mob.getEquipment().setItemInMainHand(weapon);
    }

    public void reload () {
        HandlerList.unregisterAll(this);

        registerEvents(
                new MobSpawnZoneListener(plugin)
        );
    }

    private void registerEvents(Listener... args) {
        Server server = Bukkit.getServer();
        JavaPlugin plugin = OMCPlugin.getInstance();
        for (Listener listener : args) {
            server.getPluginManager().registerEvents(listener, plugin);
        }
    }
}
