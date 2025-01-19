package fr.openmc.core.utils.chronometer;

import fr.openmc.core.OMCPlugin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class Chronometer {

    private final HashMap<UUID, Integer> chronometer = new HashMap<>();

    /**
     * FOR "start" :
     * put %sec% in your message to display the remaining time
     * otherwise the default message will be displayed
     * the time is in second

     * FOR "start" AND "stop" :
     * if you don't want to display a message just put %null%
     */

    public void start(Player player, int time, String message, String finishMessage) {
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
                if (!message.contains("%null%")){
                    if (message.contains("%sec%")) {
                        timerMessage = message.replace("%sec%", String.valueOf(remainingTime));
                    }
                    player.sendMessage(timerMessage);
                }

                if (timerEnd(playerID)) {
                    player.sendMessage(finishMessage != null ? finishMessage : "Le chronomètre est terminé !");
                    chronometer.remove(playerID);
                    cancel();
                    return;
                }

                chronometer.put(playerID, remainingTime - 1);
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0, 20);
    }

    public void stop(Player player, String message) {
        UUID playerID = player.getUniqueId();
        if (chronometer.containsKey(playerID)) {
            chronometer.remove(playerID);
            if (!message.contains("%null%")){
                player.sendMessage(message);
            }
        }
    }

    public boolean timerEnd (UUID player){
        return chronometer.get(player) <= 0;
    }
}
