package fr.openmc.core.features.dungeons.commands;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.commands.utils.SpawnManager;
import fr.openmc.core.features.dungeons.MOBIDS;
import fr.openmc.core.features.dungeons.data.PlayerDataSaver;
import fr.openmc.core.features.dungeons.listeners.MobSpawnZoneListener;
import fr.openmc.core.features.dungeons.menus.DungeonInventoryMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.*;

import static fr.openmc.core.features.dungeons.data.DungeonManager.*;
import static fr.openmc.core.features.dungeons.levels.DungeonLevelsListener.*;
import static fr.openmc.core.features.dungeons.listeners.CreatorWandListener.MobPos;

@Command({"dungeon", "dungeons", "donjon", "donjons", "d"})
@Description("Acceder au donjons")
public class DungeonsCommands {

    private final PlayerDataSaver playerDataSaver;
    private final Map<UUID, UUID> invitations = new HashMap<>();
    private final HashMap<UUID, UUID> exitDungeons = new HashMap<>();
    private final OMCPlugin plugin;
    public static final World world = Bukkit.getWorld("world");// overworld
    MobSpawnZoneListener listener;
    Location spawnLocation = SpawnManager.getInstance().getSpawnLocation();
    FileConfiguration backupConfig;
    File backupFile;

    public DungeonsCommands(OMCPlugin plugin) {

        this.plugin = plugin;
        this.playerDataSaver = new PlayerDataSaver(plugin);
        listener = new MobSpawnZoneListener(plugin);

        this.backupFile = new File(plugin.getDataFolder(), "dungeon_backup.yml");
        backupConfig = YamlConfiguration.loadConfiguration(backupFile);
    }

    @Subcommand("lore")
    @Description("ouvre le livre du lore des dungeons")
    private void onCommandLore(Player player) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setTitle("dungeons");
        meta.setAuthor("Nocolm");
        meta.addPages(Component.text("")); //TODO rajouter le lore
    }

    @Subcommand("enter")
    @Description("Tp dans le dongeon")
    private void onCommandEnter(Player player) throws SQLException {

        if (player.getWorld() == world ){
            if (Bukkit.getWorld("Dungeons") != null) {
                playerDataSaver.savePlayerData(player, world.getName());

                double x = player.getX();
                double y = player.getY();
                double z = player.getZ();

                config.set("dungeon." + "player_spawn_point." + player.getUniqueId(), x + "," + y + "," + z);
                player.teleport(DungeonSpawn);
                playerDataSaver.loadPlayerData(player, dungeons.getName());
                saveReloadConfig();
            } else {
                player.sendMessage("Le dongeon n'a pas été trouvé.");
            }
        } else {
            MessagesManager.sendMessageType(player, Component.text("§4Vous devez être dans l'overworld pour utiliser cette commande"), Prefix.DUNGEON, MessageType.ERROR, false);
        }
    }

    @Subcommand("exit world")
    @Description("Retourner dans l'Overworld")
    private void onCommandExitWorld(Player player) {

        if (playerIsInDungeon(player)) {
            MessagesManager.sendMessageType(player,Component.text("§4Vous êtes en plein donjon impossible de retourner dans l'overworld"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        if (player.getWorld() == dungeons ){
            if (world != null) {
                playerDataSaver.savePlayerData(player, dungeons.getName());

                UUID playerUUID = player.getUniqueId();
                if (config.contains("dungeon." + "player_spawn_point." + playerUUID) && config.getConfigurationSection("dungeon." + "player_spawn_point.") != null) {
                    String location = config.getString("dungeon." + "player_spawn_point." + playerUUID);
                    if (location != null){

                        String[] parts = location.split(",");

                        double x = Double.parseDouble(parts[0]);
                        double y = Double.parseDouble(parts[1]);
                        double z = Double.parseDouble(parts[2]);
                        player.teleport(new Location(world, x, y, z));
                        config.set("dungeon." + "player_spawn_point." + player.getUniqueId(), null);
                        playerDataSaver.loadPlayerData(player, world.getName());
                        if (hasDungeonTeam(player)){
                            leaveTeam(player);
                        }

                        saveReloadConfig();
                    } else {
                        player.teleport(spawnLocation);
                        playerDataSaver.loadPlayerData(player, world.getName());
                        MessagesManager.sendMessageType(player,Component.text("§4Vous avez été envoyés au spawn car votre ancienne position n'a pas été trouvée"), Prefix.DUNGEON, MessageType.ERROR, false);
                        saveReloadConfig();
                    }
                } else {
                    player.teleport(spawnLocation);
                    playerDataSaver.loadPlayerData(player, world.getName());
                    MessagesManager.sendMessageType(player,Component.text("§4Vous avez été envoyés au spawn car votre ancienne position n'a pas été trouvée"), Prefix.DUNGEON, MessageType.ERROR, false);
                    saveReloadConfig();
                }

            } else {
                player.sendMessage("L'overworld n'a pas été trouvé.");
            }
        } else {
            MessagesManager.sendMessageType(player,Component.text("§4Vous devez être dans le donjon pour utiliser cette commande"), Prefix.DUNGEON, MessageType.ERROR, false);
        }
    }

    @Subcommand("exit dungeon")
    @Description("Retourner dans l'Overworld")
    private void onCommandExitDungeon(Player player) {
        if (player.getWorld()!=dungeons){
            MessagesManager.sendMessageType(player, Component.text("§4Vous ne pouver pas utiliser ceci dans ce monde"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }
        if (exitDungeons.containsKey(player.getUniqueId())){
            player.teleport(DungeonSpawn);
            MessagesManager.sendMessageType(player, Component.text("Vous avez quitter votre donjons actuel"), Prefix.DUNGEON, MessageType.WARNING, false);
            return;
        }

        if (hasDungeonTeam(player)){
            // dire que si le joueur quitte le donjons ça team perdera un membre
        }
        MessagesManager.sendMessageType(player, Component.text("§4Vous êtes sur le point de quitter le donjon qui est en cours, refaite la commande pour confirmer"), Prefix.DUNGEON, MessageType.WARNING, false);
        exitDungeons.put(player.getUniqueId(), player.getUniqueId());
        //TODO ajouter une certification de la commande qui s'enleve au bout de 20s
        // si il est en spectateur le mettre en survie
    }

    @Subcommand("inventory")
    @Description("ouvre l'inventaire de stockage")
    private void onCommandInventory(Player player){
        new DungeonInventoryMenu(player).open();
    }

    @Subcommand("team create")
    @Description("créer une team")
    private void onTeamCreate(Player player) {
        if (player.getWorld()!=dungeons){
            MessagesManager.sendMessageType(player, Component.text("§4Vous ne pouver pas utiliser ceci dans ce monde"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        if (playerIsInDungeon(player)) {
            MessagesManager.sendMessageType(player,Component.text("§4Vous êtes en plein donjon impossible de créer une team"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        String playerName = player.getName();
        String basepath = "dungeon." + "team.";

        if (config.getConfigurationSection(basepath) == null){
            createTeam(playerName);
            return;
        }

        if (hasDungeonTeam(player)){
            MessagesManager.sendMessageType(player,Component.text("§4tu es déjà dans une team"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        if (playerIsInDungeon(player)) {
            MessagesManager.sendMessageType(player,Component.text("§4Vous êtes en plein donjon impossible de créer une team"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        MessagesManager.sendMessageType(player,Component.text("Team créée ! Utilisez /dungeon team invite pour inviter des joueurs dans votre team"), Prefix.DUNGEON, MessageType.SUCCESS, false);
        createTeam(playerName);
    }

    @Subcommand("team invite")
    @Description("invite un joueur dans ta team")
    private void onTeamInvite(Player player, @Named("player") Player invite) {
        if (player.getWorld()!=dungeons){
            MessagesManager.sendMessageType(player, Component.text("§4Vous ne pouver pas utiliser ceci dans ce monde"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        if (!hasDungeonTeam(player)){
            MessagesManager.sendMessageType(player,Component.text("§4Tu n'as pas de team ou n'es pas déjà dans une team"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        String playerName = player.getName();
        String inviteName = invite.getName();
        String basepath = "dungeon." + "team.";

        for (String team : config.getConfigurationSection(basepath).getKeys(false)) {
            String path = basepath + team + "player_in_team.";

            if (config.getStringList(path).contains(playerName)) {
                MessagesManager.sendMessageType(player,Component.text("§4tu n'es pas le chef de ta team"), Prefix.DUNGEON, MessageType.ERROR, false);
                return;
            }
        }

        if (playerName.equals(inviteName)){
            MessagesManager.sendMessageType(player,Component.text("§4Vous ne pouvez pas vous inviter dans votre team"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        if (config.getStringList(basepath + playerName + "player_in_team.").size() == 4){
            MessagesManager.sendMessageType(player,Component.text("§4Vous êtes déjà 4 dans votre équipe !"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }


        if (hasDungeonTeam(invite)) {
            MessagesManager.sendMessageType(player,Component.text("§4le joueur est déjà dans une team"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        if (playerIsInDungeon(invite)){
            MessagesManager.sendMessageType(player,Component.text("§4" + inviteName + " est dans un donjon"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        if (playerIsInDungeon(player)){
            MessagesManager.sendMessageType(player,Component.text("§4Vous ne pouvez inviter personne car vous êtes en plein donjon"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        if (invite.getWorld()!=dungeons){
            MessagesManager.sendMessageType(player, Component.text("§4Ce joueur ne se situe pas dans ce monde"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        invitations.put(player.getUniqueId(), invite.getUniqueId());
        MessagesManager.sendMessageType(player,Component.text(" Invitation dans la team envoyer a " + inviteName), Prefix.DUNGEON, MessageType.SUCCESS, false);
        MessagesManager.sendMessageType(invite,Component.text(playerName + " vous invite dans ça team ( /team accept : pour rejoindre, sinon /team deny : pour refuser )" + inviteName), Prefix.DUNGEON, MessageType.SUCCESS, false);
    }

    @Subcommand("team accept")
    @Description("accepter l'invitation")
    private void onTeamInviteAccept (Player player) {
        if (player.getWorld()!=dungeons){
            MessagesManager.sendMessageType(player, Component.text("§4Vous ne pouver pas utiliser ceci dans ce monde"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        if (playerIsInDungeon(player)){
            MessagesManager.sendMessageType(player,Component.text("§4Vous ne pouvez pas rejoindre une team car vous êtes en plein donjon"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        UUID inviterUUID = invitations.get(player.getUniqueId());

        if (inviterUUID == null) {
            MessagesManager.sendMessageType(player,Component.text("§4Tu n'as pas reçu d'invitation"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        Player inviter = Bukkit.getPlayer(inviterUUID);

        if (inviter == null || !inviter.isOnline()) {
            MessagesManager.sendMessageType(player,Component.text("§4Le joueur n'est plus en ligne"), Prefix.DUNGEON, MessageType.INFO, false);
            invitations.remove(player.getUniqueId());
            return;
        }

        if (playerIsInDungeon(player)){
            MessagesManager.sendMessageType(player,Component.text("§4Vous ne pouvez pas rejoindre une team car vous êtes en plein donjon"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        if (hasDungeonTeam(player)){
            MessagesManager.sendMessageType(player,Component.text("§4Vous êtes dékà dans une team"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        MessagesManager.sendMessageType(player,Component.text("Vous avez rejoind la team de " + inviter.getName() + " !"), Prefix.DUNGEON, MessageType.SUCCESS, false);
        MessagesManager.sendMessageType(inviter,Component.text(player.getName() + " a rejoind votre team !"), Prefix.DUNGEON, MessageType.SUCCESS, false);
        invitations.remove(player.getUniqueId());
        inviteAccept(inviter.getName(), player.getName());
    }

    @Subcommand("team deny")
    @Description("refuser l'invitation")
    private void onTeamInviteDeny (Player player) {
        if (player.getWorld()!=dungeons){
            MessagesManager.sendMessageType(player, Component.text("§4Vous ne pouver pas utiliser ceci dans ce monde"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        UUID inviterUUID = invitations.get(player.getUniqueId());

        if (inviterUUID == null) {
            MessagesManager.sendMessageType(player, Component.text("§4Tu n'as pas reçu d'invitation"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        Player inviter = Bukkit.getPlayer(inviterUUID);

        if (inviter != null && inviter.isOnline()) {
            MessagesManager.sendMessageType(inviter, Component.text("§4" + player.getName() + "a refuser votre invitaion !"), Prefix.DUNGEON, MessageType.INFO, false);
        }

        MessagesManager.sendMessageType(player, Component.text("§4Vous avez refusé l'invitation"), Prefix.DUNGEON, MessageType.INFO, false);
        invitations.remove(player.getUniqueId());
    }

    @Subcommand("team leave")
    @Description("quitte une team")
    private void onTeamLeave(Player player) {
        if (player.getWorld()!=dungeons){
            MessagesManager.sendMessageType(player, Component.text("§4Vous ne pouver pas utiliser ceci dans ce monde"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        if (playerIsInDungeon(player)){
            MessagesManager.sendMessageType(player, Component.text("§4Vous ne pouvez pas quitter une team car vous êtes en plein donjon"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        if (!hasDungeonTeam(player)){
            MessagesManager.sendMessageType(player, Component.text("§4Tu n'as pas de team ou n'es pas déjà dans une team"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        if (playerIsInDungeon(player)){
            MessagesManager.sendMessageType(player,Component.text("§4Vous ne pouvez pas quittez une team car vous êtes en plein donjon"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        leaveTeam(player);
    }

    @Subcommand("team info")
    @Description("quitte une team")
    private void onTeamInfo(Player player) {
        if (player.getWorld()!=dungeons){
            MessagesManager.sendMessageType(player, Component.text("§4Vous ne pouver pas utiliser ceci dans ce monde"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }
        if (!hasDungeonTeam(player)){
            MessagesManager.sendMessageType(player,Component.text("§4Vous n'avez pas de team"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }
        for (String team : config.getConfigurationSection("dungeon." + "team.").getKeys(false)){
            String path = "dungeon." + "team." + team;
            if (config.contains(path + ".player_in_team." + player.getName())){
                List<String> info = config.getStringList(path + ".player_in_team");
                String teamLevel = config.getString(path + ".level");
                MessagesManager.sendMessageType(player,Component.text("Voici les info de votre Team :"), Prefix.DUNGEON, MessageType.INFO, false);
                player.sendMessage("§e- level de team : " + teamLevel);
                player.sendMessage("§e- chef de la team : " + team);
                player.sendMessage("§e- joueur dans la team :");
                for (String players : info){
                    player.sendMessage("§e- " + players);
                }
                break;
            }
        }
    }

    @Subcommand("level info")
    @Description("désactiver/activer info")
    private void onLevelInfo (Player player) {
        if (player.getWorld()==dungeons){
            if (!dl_config.contains("player_info." + player.getName()) || dl_config.getBoolean("player_info." + player.getName())){
                dl_config.set("player_info." + player.getName(), false);
                MessagesManager.sendMessageType(player,Component.text("info sur l'xp reçu désactiver"), Prefix.DUNGEON, MessageType.INFO, false);
                return;
            } else {
                dl_config.set("player_info." + player.getName(), true);
                MessagesManager.sendMessageType(player,Component.text("info sur l'xp reçu activer"), Prefix.DUNGEON, MessageType.INFO, false);
                return;
            }
        }
        MessagesManager.sendMessageType(player,Component.text("§4Vous ne pouver pas utiliser ceci dans ce monde"), Prefix.DUNGEON, MessageType.ERROR, false);
    }

    @Subcommand("level get")
    @Description("connaitre le niveau d'un joueur")
    private void onLevelGet (Player player, @Named("target") Player target){
        if (player.getWorld()!=dungeons){
            MessagesManager.sendMessageType(player, Component.text("§4Vous ne pouver pas utiliser ceci dans ce monde"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }
        if (target == null || target.getName().equals(player.getName())){
            player.sendMessage("vous êtes level " + getPlayerDungeonLevels(player) + ", " + getPlayerDungeonXP(player) + " xp");
            return;
        }
        player.sendMessage(target.getName() + " est level " + getPlayerDungeonLevels(target) + ", " + getPlayerDungeonXP(target) + " xp");
    }

    @Subcommand("mobspawn list spawn_point")
    @Description("mobspawn list spawn_point")
    @CommandPermission("omc.admin.commands.dungeon.mobspawn.list.spawn_point")
    private void onZoneListSpawnPoint (Player player) {
        if (player.getWorld()!=dungeons){
            MessagesManager.sendMessageType(player, Component.text("§4Vous ne pouver pas utiliser ceci dans ce monde"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        List<String> spawn_point = new ArrayList<>();

        if (config.contains("dungeon." + "mob_spawn.")) {
            for (String point : config.getConfigurationSection("dungeon." + "mob_spawn.").getKeys(false)){
                spawn_point.add(point);
            }
            if (spawn_point.isEmpty()) {
                MessagesManager.sendMessageType(player, Component.text("§4Aucune spawn_point n'est enregistrée"), Prefix.DUNGEON, MessageType.ERROR, false);
            } else {
                Collections.sort(spawn_point);
                MessagesManager.sendMessageType(player, Component.text("§aspawn_point enregistrés :"), Prefix.DUNGEON, MessageType.ERROR, false);
                for (String zone : spawn_point) {
                    player.sendMessage("§e- " + zone);
                }
            }
        } else {
            MessagesManager.sendMessageType(player, Component.text("§4Aucune spawn_point n'est enregistré"), Prefix.DUNGEON, MessageType.ERROR, false);
        }
    }

    @Subcommand("mobspawn list mob")
    @Description("mobspawn list mob")
    @CommandPermission("omc.admin.commands.dungeon.mobspawn.list.mob")
    private void onZoneListMob (Player player) {
        if (player.getWorld()!=dungeons){
            MessagesManager.sendMessageType(player, Component.text("§4Vous ne pouver pas utiliser ceci dans ce monde"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        List<String> mobList = new ArrayList<>();

        for (MOBIDS type : MOBIDS.values()) {
            mobList.add(MOBIDS.valueOf(String.valueOf(type)).getMobName());
        }

        if (mobList.isEmpty()) {
            MessagesManager.sendMessageType(player, Component.text("§4Aucune entité n'est enregistrée"), Prefix.DUNGEON, MessageType.ERROR, false);
        } else {
            player.sendMessage("§aEntités disponibles :");
            for (String mob : mobList) {
                player.sendMessage("§e- " + mob);
            }
        }
    }

    @Subcommand("mobspawn add")
    @Description("mobspawn add")
    @CommandPermission("omc.admin.commands.dungeon.mobspawn.add")
    private void onMobSpawn(Player player, @Named("mob") String mob) {
        if (player.getWorld() != dungeons){
            MessagesManager.sendMessageType(player, Component.text("§4Vous ne pouver pas utiliser ceci dans ce monde"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        if (! MOBIDS.isValidMobName(mob)){ //TODO modifier pour vérifier si le mob à ajouter existe avec une énumération répertoriant les mobs
            MessagesManager.sendMessageType(player, Component.text("§4ce mob n'existe pas"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        double x = MobPos.getX();
        double y = MobPos.getY();
        double z = MobPos.getZ();

        addMobSpawn(mob,x,y,z);
        MessagesManager.sendMessageType(player, Component.text("§amob ajouter"), Prefix.DUNGEON, MessageType.SUCCESS, false);
    }

    @Subcommand("mobspawn remove")
    @Description("mobspawn remove")
    @CommandPermission("omc.admin.commands.dungeon.mobspawn.remove")
    private void removeMobSpawn (Player player, @Named("spawn_point") String spawn_point) {
        if (player.getWorld()!=dungeons){return;}

        if (!config.contains("dungeon." + "mob_spawn." + spawn_point)){
            MessagesManager.sendMessageType(player, Component.text("§4" + spawn_point + " n'éxiste pas"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        String path = "dungeon." + "mob_spawn." + spawn_point;
        config.set(path, null);
        saveReloadConfig();
        listener.reload();
        MessagesManager.sendMessageType(player, Component.text("§a" + spawn_point + " retirer"), Prefix.DUNGEON, MessageType.SUCCESS, false);
    }

    @Subcommand("mobspawn tp")
    @Description("mobspawn tp")
    @CommandPermission("omc.admin.commands.dungeon.mobspawn.tp")
    private void showMobSpawn (Player sender, @Named("spawn_point") String spawn_point){
        if (sender.getWorld()!=dungeons){return;}

        String path = "dungeon." + "mob_spawn." + spawn_point;
        if (config.contains(path) && config != null){
            double x = config.getDouble("dungeon." + "mob_spawn." + spawn_point + ".x");
            double y = config.getDouble("dungeon." + "mob_spawn." + spawn_point + ".y");
            double z = config.getDouble("dungeon." + "mob_spawn." + spawn_point + ".z");
            sender.teleport(new Location(dungeons,x,y,z));
            MessagesManager.sendMessageType(sender, Component.text("§avous avez été téléporter au " + spawn_point), Prefix.DUNGEON, MessageType.SUCCESS, false);
            return;
        }
        MessagesManager.sendMessageType(sender, Component.text("§4Vous ne pouver pas utiliser ceci dans ce monde"), Prefix.DUNGEON, MessageType.ERROR, false);
    }

    @Subcommand("yml update")
    @Description("importation du dungeon.yml")
    @CommandPermission("omc.admin.commands.dungeon.yml.update")
    private void updateImportDungeonYML (Player sender) {
        sender.sendMessage("mise a jour du yml en cours si des joueurs étaient présent dans un donjons lors de l'update, ceci ont été dédommagés");
        updateDungeonYML(plugin);
    }

    @Subcommand("yml autoupdate")
    @Description("importation du dungeon.yml")
    @CommandPermission("omc.admin.commands.dungeon.yml.autoupdate")
    private void autoUpdateImportDungeonYML (Player sender) {
        boolean onOff = config.getBoolean("dungeon." + "yml_auto_update");
        if (!onOff){
            sender.sendMessage("La mise a jour du yml a été programmer pour le prochain reset du server vers 3H00 heure Française");
            config.set("dungeon." + "yml_auto_update.", true);
            return;
        }
        sender.sendMessage("La mise a jour du yml a été déprogrammer");
        config.set("dungeon." + "yml_auto_update.", false);
    }

    @Subcommand("yml backup")
    @Description("importation du dungeon.yml")
    @CommandPermission("omc.admin.commands.dungeon.yml.backup")
    private void backupDungeonYML (Player player) {
        createBackup(player);
    }

    private void inviteAccept (String teamName, String invite){
        String path = "dungeon." + "team." + teamName;
        List<String>playerInTeam = new ArrayList<>();
        for (String teamMate : config.getStringList(path + ".player_in_team")){
            playerInTeam.add(teamMate);
        }
        playerInTeam.add(invite);
        config.set(path + ".player_in_team", playerInTeam);
        int teamLevel = getLowestTeamLevel(teamName);
        if (teamLevel == -1){
            config.set(path + ".level", "level max");
        } else {
            config.set(path + ".level", "level " + teamLevel);
        }
        saveReloadConfig();

    }

    private void createTeam (String teamName) {
        String path = "dungeon." + "team." + teamName;
        List<String>playerInTeam = new ArrayList<>();
        playerInTeam.add(teamName);
        config.set(path + ".player_in_team", playerInTeam);
        config.set(path + ".remain", -1);
        saveReloadConfig();
        int teamLevel = getLowestTeamLevel(teamName);
        if (teamLevel == -1){
            config.set(path + ".level", "level max");
        } else {
            config.set(path + ".level", "level " + teamLevel);
        }
    }

    public static void leaveTeam (Player player) {
        String basepath = "dungeon." + "team.";

        if (config.contains(basepath + player.getName())) {
            String path = basepath + player.getName() + ".player_in_team";

            for (String playerInTeam : config.getStringList(path)) {
                Player teamMate = Bukkit.getPlayer(playerInTeam);
                if ( teamMate != null && !teamMate.getName().equals(player.getName())){
                    MessagesManager.sendMessageType(player, Component.text("§4Le chef à dissous la team"), Prefix.DUNGEON, MessageType.INFO, false);
                }
            }

            config.set(basepath + player.getName(), null);
            MessagesManager.sendMessageType(player, Component.text("§4Vous avez dissous votre team"), Prefix.DUNGEON, MessageType.INFO, false);
            saveReloadConfig();
            return;
        }

        for (String team : config.getConfigurationSection(basepath).getKeys(false)) {
            String path = basepath + team + ".player_in_team";

            if (config.getStringList(path).contains(player.getName())) {

                for (String playerInTeam : config.getStringList(path)) {
                    Player teamMate = Bukkit.getPlayer(playerInTeam);
                    if ( teamMate != null && !teamMate.getName().equals(player.getName())){
                        MessagesManager.sendMessageType(player, Component.text("§4" + player.getName() + " a quitté la team"), Prefix.DUNGEON, MessageType.INFO, false);
                    } else {
                        MessagesManager.sendMessageType(player, Component.text("§4vouas avez quitté la team"), Prefix.DUNGEON, MessageType.INFO, false);
                    }
                }

                List<String> playerInTeam = config.getStringList(path);
                playerInTeam.remove(player.getName());
                config.set(path, playerInTeam);
                int teamLevel = getLowestTeamLevel(player.getName());
                if (teamLevel == -1){
                    config.set(path + ".level", "level max");
                } else {
                    config.set(path + ".level", "level " + teamLevel);
                }
                saveReloadConfig();
                break;
            }
        }
    }

    private static int getLowestTeamLevel(String teamName) {
        saveReloadConfig();
        int teamLevel = config.getInt("dungeon." + "team." + teamName + ".level");
        for (String playerInTeam : config.getStringList("dungeon." + "team." + teamName + ".player_in_team")) {
            Player player = Bukkit.getPlayer(playerInTeam);
            if (player != null){
                int playerLevel = getPlayerDungeonLevels(player);
                if (teamLevel>playerLevel || teamLevel == 0 || teamLevel == -1){
                    teamLevel = playerLevel;
                }
            }
        }
        return teamLevel;
    }

    private void addMobSpawn(String mob, double x, double y, double z) {

        if (config.getConfigurationSection("dungeon." + "mob_spawn") != null){

            Set<String> spawnPoints = config.getConfigurationSection("dungeon." + "mob_spawn.").getKeys(false);
            String basePath = "dungeon." + "mob_spawn.";

            //Extraire les indices et les trier //TODO problème ici je pense
            List<Integer> indices = new ArrayList<>();
            for (String spawnPoint : spawnPoints) {
                if (spawnPoint.startsWith("spawn_point")) {
                    String indexStr = spawnPoint.replace("spawn_point", "");
                    try {
                        indices.add(Integer.parseInt(indexStr));
                    } catch (NumberFormatException ignored) {
                        // Ignorer les erreurs de conversion si le nom n'est pas valide
                    }
                }
            }
            Collections.sort(indices);

            int pointIndex = 1;
            for (int index : indices) {
                if (index != pointIndex) {
                    break;
                }
                pointIndex++;
            }

            String path = basePath + "spawn_point" + pointIndex; //TODO probleme certains spawn_points se remettent a true

            config.set(path + ".activated", false); //TODO définit sur désactiver pour éviter qu'un mob apparaisse a l'ajout du point
            config.set(path + ".mob", mob);
            config.set(path + ".x", x);
            config.set(path + ".y", y);
            config.set(path + ".z", z);

        } else { // si le mob_spawn de dungeon.yml ne contient pas encore de spawn_point

            String path = "dungeon." + "mob_spawn." + "spawn_point1";

            config.set(path + ".activated", false); //TODO définit sur désactiver pour éviter qu'un mob apparaisse a l'ajout du point
            config.set(path + ".mob", mob);
            config.set(path + ".x", x);
            config.set(path + ".y", y);
            config.set(path + ".z", z);
        }

        saveReloadConfig();
        listener.reload();
    }

    public static void updateDungeonYML(OMCPlugin plugin) {
        try {
            //TODO stocker puis enregistrer les co des joueurs dans le yml
            Reader resourceReader = new InputStreamReader(Objects.requireNonNull(plugin.getResource("dungeon.yml")));
            FileConfiguration resourceConfig = YamlConfiguration.loadConfiguration(resourceReader);

            if (!file.exists()) {
                file.getParentFile().mkdirs();
                plugin.saveResource("dungeon.yml", false);
            }

            FileConfiguration dataConfig = YamlConfiguration.loadConfiguration(file);

            for (String key : resourceConfig.getKeys(true)) {
                dataConfig.set(key, resourceConfig.get(key));
            }

            dataConfig.save(file);
            config = YamlConfiguration.loadConfiguration(file);
            plugin.getLogger().info("Dungeon configuration updated starting");

            resourceReader.close();

            if (config != null && config.contains("dungeon." + "players_in_dungeon.")){
                for (String players : config.getConfigurationSection("dungeon." + "players_in_dungeon.").getKeys(false)){
                    Player player = Bukkit.getPlayer(players);
                    MessagesManager.sendMessageType(player, Component.text("§4Une mise a jour des donjons a eu lieu vous avez donc été renvoyer au spawn. Pour nous faire pardonner nous vous avons fait gagner le donjons dans lequel vous étiez"), Prefix.DUNGEON, MessageType.INFO, false);
                    //TODO faire PlayerWinDungeon ou TeamWinDungeon
                }
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Error while updating dungeon.yml: " + e.getMessage());
        }
    }

    public void createBackup(Player player) {
        if (!file.exists()) {
            player.sendMessage("§4Une erreur est survenu lors de la création de la backup");
            plugin.getLogger().severe("dungeon.yml do not exist, no save create !");
            return;
        }

        try {
            Files.copy(file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            backupConfig.save(backupFile);
            backupConfig = YamlConfiguration.loadConfiguration(backupFile);
            backupConfig.set("dungeon." + "team.",null);
            backupConfig.save(backupFile);
            plugin.getLogger().info("backup of dungeon.yml successfully create.");
            player.sendMessage("Backup créer avec succès");
        } catch (IOException e) {
            player.sendMessage("§4Une erreur est survenu lors de la création de la backup");
            plugin.getLogger().severe("ERROR during the creation of the backup : " + e.getMessage());
        }
    }

    public static boolean playerIsInDungeon (Player player) {
        return config.getConfigurationSection("dungeon." + "players_in_dungeon." + player.getName()) != null;
    }

    public static boolean hasDungeonTeam (Player player){
        if (config == null || config.getConfigurationSection("dungeon." + "team.")==null){
            return false;
        }
        for (String team : config.getConfigurationSection("dungeon." + "team.").getKeys(false)){
            if (config.getStringList("dungeon." + "team." + team + ".player_in_team").contains(player.getName())){
                return true;
            }
        }
        return config.contains("dungeon." + "team." + player.getName());
    }
}