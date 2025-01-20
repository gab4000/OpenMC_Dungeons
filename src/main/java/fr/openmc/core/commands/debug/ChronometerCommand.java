package fr.openmc.core.commands.debug;

import fr.openmc.core.utils.chronometer.Chronometer;
import fr.openmc.core.utils.chronometer.ChronometerType;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class ChronometerCommand {
    @Command("debug chronometer")
    @CommandPermission("omc.debug.chronometer")
    @Description("Test du chronometre")
    private void chronometer (Player target,@Named("time") int time){
        if (time>90){
            target.sendMessage("§4Ne pas dépasser plus de 90s pour le debugage");
            return;
        }
        Chronometer.start(target,time, ChronometerType.ACTION_BAR, null, ChronometerType.ACTION_BAR, null);
    }
}
