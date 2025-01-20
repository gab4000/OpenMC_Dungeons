package fr.openmc.core.utils.chronometer;

import fr.openmc.core.OMCPlugin;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class Chronometer {

    // Map structure: UUID -> (Group -> Time)
    private static final HashMap<UUID, HashMap<String, Integer>> chronometer = new HashMap<>();

    /**
     * FOR "start" :
     * put "%sec%" in your message to display the remaining time
     * otherwise the default message will be displayed
     * the display time is in second

     * FOR "start" / "stopAll" / "stop" :
     * if you don't want to display a message just put "%null%"

     * @param player player to add
     * @param group Chronometer group
     * @param time duration
     * @param messageType display type
     * @param message to display the time
     * @param finishMessageType display type
     * @param finishMessage message display when the chronometer end normally
     */
    public static void start(Player player, String group, int time, ChronometerType messageType, String message,ChronometerType finishMessageType, String finishMessage) {
        UUID playerID = player.getUniqueId();
        chronometer.computeIfAbsent(playerID, k -> new HashMap<>()).put(group, time);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!Bukkit.getPlayer(playerID).isOnline()){
                    cancel();
                    return;
                }

                if (!chronometer.containsKey(playerID)) {
                    cancel();
                    return;
                }

                int remainingTime = chronometer.get(playerID).get(group);
                String timerMessage = "Il reste : " + remainingTime + "s";
                if (message!=null){
                    if (!message.contains("%null%")){
                        if (message.contains("%sec%")) {
                            timerMessage = message.replace("%sec%", String.valueOf(remainingTime));
                        }
                        player.spigot().sendMessage(messageType.getChatMessageType(),new TextComponent(timerMessage));
                    }
                } else {
                    player.spigot().sendMessage(messageType.getChatMessageType(),new TextComponent(timerMessage));
                }


                if (timerEnd(playerID, group)) {
                    player.spigot().sendMessage(finishMessageType.getChatMessageType(), new TextComponent(finishMessage != null ? finishMessage : "Le chronomètre est terminé !"));
                    chronometer.remove(playerID);
                    cancel();
                    return;
                }

                chronometer.get(playerID).put(group, remainingTime - 1);
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0, 20);
    }

    /**
     * @param player player who is affect
     * @param messageType display type
     * @param message message display when the chronometer is stopped
     */
    public static void stopAll(Player player,ChronometerType messageType, String message) {
        UUID playerID = player.getUniqueId();
        if (chronometer.containsKey(playerID)) {
            chronometer.remove(playerID);
            if (message!=null){
                if (!message.contains("%null%")){
                    player.spigot().sendMessage(messageType.getChatMessageType(), new TextComponent(message));
                }
            } else {
                player.spigot().sendMessage(messageType.getChatMessageType(), new TextComponent("chronomètre arrèté"));
            }
        }
    }

    /**
     * @param player player who is affect
     * @param group Chronometer group
     * @param messageType display type
     * @param message message display when the chronometer is stopped
     */
     public static void stop(Player player, String group,ChronometerType messageType, String message) {
        UUID playerID = player.getUniqueId();

        if (chronometer.containsKey(playerID) && chronometer.get(playerID).containsKey(group)) {
            chronometer.get(playerID).remove(group);
            if (message!=null){
                if (!message.contains("%null%")){
                    player.spigot().sendMessage(messageType.getChatMessageType(), new TextComponent(message));
                }
            } else {
                player.spigot().sendMessage(messageType.getChatMessageType(), new TextComponent("chronomètre du " + group + " arrèté"));
            }

            if (chronometer.get(playerID).isEmpty()) {
                chronometer.remove(playerID);
            }
        } else {
            player.sendMessage("§cAucun chronomètre trouvé pour le groupe §e" + group + ".");
        }
    }


    public static void listChronometers(Player target, Player owner) {
        UUID playerID = target.getUniqueId();

        if (chronometer.containsKey(playerID)) {
            owner.sendMessage("§aChronomètres actifs :");
            chronometer.get(playerID).forEach((group, time) ->
                    owner.sendMessage(" §e- " + group + ": §6" + time + "s")
            );
        } else {
            owner.sendMessage("§cVous n'avez aucun chronomètre actif.");
        }
    }

    /**
     * @return the remaining time
     */
    public static int getRemainingTime (UUID playerID, String group){
        return chronometer.get(playerID).get(group);
    }

    /**
     * @return true if chronometer has expired
     */
    public static boolean timerEnd (UUID playerID, String group){
        return chronometer.get(playerID).get(group) <= 0;
    }
}
