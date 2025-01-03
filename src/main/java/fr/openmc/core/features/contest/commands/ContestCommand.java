package fr.openmc.core.features.contest.commands;

import fr.openmc.core.features.contest.managers.ContestManager;
import fr.openmc.core.features.contest.managers.ContestPlayerManager;
import fr.openmc.core.features.contest.menu.ContributionMenu;
import fr.openmc.core.features.contest.menu.VoteMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Command("contest")
@Description("Ouvre l'interface des festivals, et quand un festival commence, vous pouvez choisir votre camp")
public class ContestCommand {
    private final ContestManager contestManager;

    public ContestCommand() {
        this.contestManager = ContestManager.getInstance();
    }

    @Cooldown(4)
    @DefaultFor("~")
    public void defaultCommand(Player player) {
        int phase = contestManager.data.getPhase();
        if ((phase >= 2 && contestManager.dataPlayer.get(player.getUniqueId().toString()) == null) || (phase == 2)) {
            VoteMenu menu = new VoteMenu(player);
            menu.open();
        } else if (phase == 3 && contestManager.dataPlayer.get(player.getUniqueId().toString()) != null) {
            ContributionMenu menu = new ContributionMenu(player);
            menu.open();

        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E", Locale.FRENCH);
            DayOfWeek dayStartContestOfWeek = DayOfWeek.from(formatter.parse(contestManager.data.getStartdate()));

            int days = (dayStartContestOfWeek.getValue() - contestManager.getCurrentDayOfWeek().getValue() + 7) % 7;

            MessagesManager.sendMessageType(player, Component.text("§cIl n'y a aucun Contest ! Revenez dans " + days + " jour(s)."), Prefix.CONTEST, MessageType.ERROR, true);
        }
    }

    @Subcommand("setphase")
    @Description("Permet de lancer une procédure de phase")
    @CommandPermission("ayw.command.contest.setphase")
    public void setPhase(Integer phase) {
        switch(phase) {
            case 1:
                this.contestManager.initPhase1();
                break;
            case 2:
                this.contestManager.initPhase2();
                break;
             case 3:
                this.contestManager.initPhase3();
                break;
        }
    }

    @Subcommand("setcontest")
    @Description("Permet de définir un Contest")
    @CommandPermission("ayw.command.contest.setcontest")
    @AutoComplete("@colorContest")
    public void setContest(Player player, String camp1, @Named("colorContest") String color1, String camp2, @Named("colorContest") String color2) {
        int phase = contestManager.data.getPhase();
        if (phase == 1) {
            if (contestManager.getColorContestList().containsAll(Arrays.asList(color1, color2))) {
                contestManager.deleteTableContest("contest");
                contestManager.deleteTableContest("camps");
                contestManager.insertCustomContest(camp1, color1, camp2, color2);

                MessagesManager.sendMessageType(player, Component.text("§aLe Contest : " + camp1 + " VS " + camp2 + " a bien été sauvegarder\nMerci d'attendre que les données en cache s'actualise."), Prefix.STAFF, MessageType.SUCCESS, true);
            } else {
                MessagesManager.sendMessageType(player, Component.text("§c/contest setcontest <camp1> <color1> <camp2> <color2> et color doit comporter une couleur valide"), Prefix.STAFF, MessageType.ERROR, true);
            }
        } else {
            MessagesManager.sendMessageType(player, Component.text("§cVous pouvez pas définir un contest lorsqu'il a commencé"), Prefix.STAFF, MessageType.ERROR, true);
        }
    }

    @Subcommand("settrade")
    @Description("Permet de définir un Trade")
    @CommandPermission("ayw.command.contest.settrade")
    @AutoComplete("@trade")
    public void setTrade(Player player, @Named("trade") String trade, int amount, int amountShell) {
        YamlConfiguration config = ContestManager.getInstance().contestConfig;
        List<Map<?, ?>> trades = config.getMapList("contestTrades");

        boolean tradeFound = false;

        for (Map<?, ?> tradeEntry : trades) {
            if (tradeEntry.get("ress").equals(trade)) {
                ((Map<String, Object>) tradeEntry).put("amount", amount);
                ((Map<String, Object>) tradeEntry).put("amount_shell", amountShell);
                tradeFound = true;
                break;
            }
        }

        if (tradeFound) {
            ContestManager.getInstance().saveContestConfig();
            MessagesManager.sendMessageType(player, Component.text("Le trade de " + trade + " a été mis à jour avec " + amount + " pour " + amountShell + " coquillages de contest."), Prefix.STAFF, MessageType.SUCCESS, true);
        } else {
            MessagesManager.sendMessageType(player, Component.text("Le trade n'existe pas.\n/contest settrade <mat> <amount> <amount_shell>"), Prefix.STAFF, MessageType.ERROR, true);
        }
    }

    @Subcommand("addpoints")
    @Description("Permet d'ajouter des points a un membre")
    @CommandPermission("ayw.command.contest.addpoints")
    public void addPoints(Player player, Player target, Integer points) {
        ContestPlayerManager.getInstance().setPointsPlayer(target,points + contestManager.dataPlayer.get(target).getPoints());
        MessagesManager.sendMessageType(player, Component.text("§aVous avez ajouté " + points + " §apoint(s) à " + target.getName()), Prefix.STAFF, MessageType.SUCCESS, true);
    }

    @Subcommand("color")
    @Description("test pour transition ChatColor => Color")
    @CommandPermission("ayw.command.contest.color")
    public void color(Player player) {
       MessagesManager.sendMessage(player,
               Component.text(NamedTextColor.AQUA + "Color.AQUA"), Prefix.OPENMC);
    }
}
