package fr.openmc.core.commands.debug;

import fr.openmc.core.utils.chronometer.Chronometer;
import fr.openmc.core.utils.chronometer.ChronometerType;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class ChronometerCommand {
    @Command("debug chronometer start")
    @CommandPermission("omc.debug.chronometer.start")
    @Description("Test du chronometre")
    private void chronometerStart (Player target,@Named("time") int time){
        if (time>90){
            target.sendMessage("§4Ne pas dépasser plus de 90s pour le debugage");
            return;
        }
        Chronometer.start(target,"debug",time, ChronometerType.ACTION_BAR, null, ChronometerType.ACTION_BAR, null);
    }

    @Command("debug chronometer stop_all")
    @CommandPermission("omc.debug.chronometer.stop_all")
    @Description("Test du chronometre")
    private void chronometerStopAll (Player target){
        Chronometer.stopAll(target, ChronometerType.ACTION_BAR, null);
    }

    @Command("debug chronometer stop")
    @CommandPermission("omc.debug.chronometer.stop")
    @Description("Test du chronometre")
    private void chronometerStop (Player target,@Named("group") String group){
        Chronometer.stop(target, group, ChronometerType.ACTION_BAR, null);
    }

    @Command("debug chronometer list")
    @CommandPermission("omc.debug.chronometer.list")
    @Description("Test du chronometre")
    private void chronometerList (Player owner, @Named("target") Player target){
        Chronometer.listChronometers(target, owner);
    }
}
