package fr.openmc.core.utils.chronometer;

import fr.openmc.core.OMCPlugin;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class Chronometer {

    private static final HashMap<UUID, Integer> chronometer = new HashMap<>();

    /**
     * FOR "start" :
     * put "%sec%" in your message to display the remaining time
     * otherwise the default message will be displayed
     * the display time is in second

     * FOR "start" AND "stop" :
     * if you don't want to display a message just put "%null%"
     */

    public static void start(Player player, int time,ChronometerType messageType, String message,ChronometerType finishMessageType, String finishMessage) {
        UUID playerID = player.getUniqueId();
        chronometer.put(playerID, time);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!chronometer.containsKey(playerID)) {
                    cancel();
                    return;
                }

                int remainingTime = chronometer.get(playerID);
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


                if (timerEnd(playerID)) {
                    player.spigot().sendMessage(finishMessageType.getChatMessageType(), new TextComponent(finishMessage != null ? finishMessage : "Le chronomètre est terminé !"));
                    chronometer.remove(playerID);
                    cancel();
                    return;
                }

                chronometer.put(playerID, remainingTime - 1);
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0, 20);
    }

    public static void stop(Player player,ChronometerType messageType, String message) {
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

    public static int getRemainingTime (UUID playerID){
        return chronometer.get(playerID);
    }

    public static boolean timerEnd (UUID playerID){
        return chronometer.get(playerID) <= 0;
    }
}
