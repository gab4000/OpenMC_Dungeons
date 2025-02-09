package fr.openmc.core.features.dungeons.levels;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dungeons.MOBIDS;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import static fr.openmc.core.features.dungeons.data.DungeonManager.*;

public class DungeonLevelsListener implements Listener {

    OMCPlugin plugin;

    @EventHandler
    private void onPlayerJoin (PlayerJoinEvent e){
        Player player = e.getPlayer();
        if (!dl_config.contains("player_info." + player.getName()) || !dl_config.contains("player_xp." + player.getName()) || !dl_config.contains("skills_point." + player.getName())){
            dl_config.set("player_xp." + player.getName(), 0);
            dl_config.set("player_info." + player.getName(), true);
            dl_config.set("skills_point." + player.getName(), 0);
            saveReloadDLConfig();
        }
    }

    @EventHandler
    private void onEntityDeath (EntityDeathEvent event) {
        Entity killer = event.getEntity().getKiller();
        Entity entity = event.getEntity();
        if (killer ==null || entity == killer|| event.getEntity().getKiller().getWorld() != Bukkit.getWorld("Dungeons")){
            return;
        }

        if (killer instanceof Player && killer != null) {
            Player player = Bukkit.getPlayer(killer.getUniqueId());
            int xp = MOBIDS.getExpByName(entity.getCustomName());

            if (!dl_file.exists()){
                plugin.getLogger().severe("dl_file not exist ot not correctly recognise");
                MessagesManager.sendMessageType(player, Component.text("§4erreur lors de l'optention d'xp"), Prefix.DUNGEON, MessageType.ERROR, false);
                return;
            }

            if (xp == 0 || dl_config.getBoolean("player_info." + player.getName())){player.sendMessage(xp + " xp");}
            giveDungeonXP(player, xp);
            saveReloadDLConfig();
        }
    }

    public static int getPlayerDungeonXP(Player player) {
        if (!dl_config.contains("player_xp." + player.getName()) || dl_config == null){
            return 0;
        }
        return dl_config.getInt("player_xp." + player.getName());
    }

    public static int getPlayerDungeonLevels (Player player) {
        if (!dl_config.contains("player_xp." + player.getName()) || dl_config == null){
            return 0;
        }
        int xp = dl_config.getInt("player_xp." + player.getName());
        if ( xp < 0 ){
            return 0;
        }
        for (LEVELS levels : LEVELS.values()){
            if (levels == LEVELS.level_max){
                if (xp > levels.getMinXp()){
                    return levels.getLevel();
                }
            }
            if (xp >= levels.getMinXp() && xp>= levels.getMaxXp()) {
                return levels.getLevel();
            }
        }
        return 0;
    }

    public static void giveDungeonXP(Player player, int xp) {
        int lastLevel = getPlayerDungeonLevels(player);
        dl_config.addDefault("player_xp." + player.getName(), xp);
        if (lastLevel < getPlayerDungeonLevels(player)){
            giveSkillsPoint(player, 1);
            if (getPlayerDungeonLevels(player) == -1){
                MessagesManager.sendMessageType(player, Component.text("""
                    §8§m§r
                    §7
                    §7Level UP !§7\
                    §7
                    Vous êtes désormais niveau MAX
                    De futur niveaux seront potentiellement optenable
                    §7
                    §8§m§r"""
                ), Prefix.DUNGEON, MessageType.SUCCESS, false);
                return;
            }

            MessagesManager.sendMessageType(player, Component.text("""
                    §8§m§r
                    §7
                    §7Level UP !§7\
                    §7
                    Vous êtes désormais niveau \s""" + getPlayerDungeonLevels(player) + """
                    §7
                    §8§m§r"""
            ), Prefix.DUNGEON, MessageType.SUCCESS, false);
        }
    }

    public static void giveSkillsPoint (Player player, int point) {
        dl_config.addDefault("skills_point." + player.getName(), point);
    }

    public static int getSkillsPoint (Player player) {
        return dl_config.getInt("skills_point." + player.getName());
    }
}
