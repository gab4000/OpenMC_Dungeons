package fr.openmc.core.features.dungeons.commands;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dungeons.MOBIDS;
import fr.openmc.core.features.dungeons.data.DungeonManager;
import fr.openmc.core.features.dungeons.data.PlayerDataSaver;
import fr.openmc.core.features.dungeons.listeners.MobSpawnZoneListener;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static fr.openmc.core.features.dungeons.data.DungeonManager.config;
import static fr.openmc.core.features.dungeons.data.DungeonManager.file;
import static fr.openmc.core.features.dungeons.listeners.CreatorWandListener.MobPos;

@Command({"dungeon", "dungeons", "donjon", "donjons", "d"})
@Description("Acceder au donjons")
public class DungeonsCommands {

    private final PlayerDataSaver playerDataSaver;
    private final Map<UUID, UUID> invitations = new HashMap<>();
    private final HashMap<UUID, Location> lastOverworldLocations = new HashMap<>();
    private final OMCPlugin plugin;
    MobSpawnZoneListener listener;

    FileConfiguration backupConfig;
    File backupFile;

    public DungeonsCommands(OMCPlugin plugin) {

        this.plugin = plugin;
        this.playerDataSaver = new PlayerDataSaver(plugin);
        listener = new MobSpawnZoneListener(plugin);

        this.backupFile = new File(plugin.getDataFolder(), "dungeon_backup.yml");
        backupConfig = YamlConfiguration.loadConfiguration(backupFile);
    }

    @Subcommand("enter")
    @Description("Tp dans le dongeon")
    public void onCommandEnter(Player player) {

        World world = Bukkit.getWorld("world"); // overworld

        if (player.getWorld() == world ){
            World dungeons = Bukkit.getWorld("Dungeons");
            if (dungeons != null) {
                playerDataSaver.savePlayerData(player, world.getName());

                lastOverworldLocations.put(player.getUniqueId(), player.getLocation());
                player.teleport(new Location(dungeons, 0, 60, 0));

                playerDataSaver.loadPlayerData(player, dungeons.getName());
            } else {
                player.sendMessage("Le dongeon n'a pas été trouvé.");
            }
        } else {
            MessagesManager.sendMessageType(player, Component.text("§4Vous devez être dans l'overworld pour utiliser cette commande"), Prefix.DUNGEON, MessageType.ERROR, false);
        }
    }

    @Subcommand("exit")
    @Description("Retourner dans l'Overworld")
    public void onCommandExit(Player player) {

        if (playerIsInDungeon(player)) {
            MessagesManager.sendMessageType(player,Component.text("§4Vous êtes en plein donjon impossible de retourner dans l'overworld"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        World dungeons = Bukkit.getWorld("Dungeons");

        if (player.getWorld() == dungeons ){
            World overworld = Bukkit.getWorld("world"); // overworld
            if (overworld != null) {
                playerDataSaver.savePlayerData(player, dungeons.getName());

                UUID playerUUID = player.getUniqueId();
                if (lastOverworldLocations.containsKey(playerUUID)) {
                    Location lastLocation = lastOverworldLocations.get(playerUUID);
                    player.teleport(lastLocation);
                    lastOverworldLocations.remove(playerUUID, lastLocation);
                    playerDataSaver.loadPlayerData(player, overworld.getName());
                }

            } else {
                player.sendMessage("L'overworld n'a pas été trouvé.");
            }
        } else {
            MessagesManager.sendMessageType(player,Component.text("§4Vous devez être dans le donjon pour utiliser cette commande"), Prefix.DUNGEON, MessageType.ERROR, false);
        }
    }

//    @Subcommand("menu")
//    @Description("menu")
//    public void onCommandMenu(Player player) {
//        DungeonMenu menu = new DungeonMenu(player);
//        menu.open();
//    }

    @Subcommand("team create")
    @Description("créer une team")
    public void onTeamCreate(Player player) {

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

        for (String team : config.getConfigurationSection(basepath).getKeys(false)) {
            String path = basepath + team + "player_in_team.";

            if (config.getStringList(path).contains(playerName)) {
                MessagesManager.sendMessageType(player,Component.text("§4tu es déjà dans une team"), Prefix.DUNGEON, MessageType.ERROR, false);
                return;
            }
        }

        if (config.getConfigurationSection(basepath).contains(playerName)){
            MessagesManager.sendMessageType(player,Component.text("§4Tu as déjà une team"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }
        MessagesManager.sendMessageType(player,Component.text("Team créée ! Utilisez team invite pour inviter des joueurs dans votre team"), Prefix.DUNGEON, MessageType.SUCCESS, false);
        createTeam(playerName);
    }

    @Subcommand("team invite")
    @Description("invite un joueur dans ta team")
    public void onTeamInvite(Player player, @Named("player") Player invite) {

        String basepath = "dungeon." + "team.";

        if (config.getConfigurationSection(basepath) == null){
            MessagesManager.sendMessageType(player,Component.text("§4Tu n'as pas de team ou n'es pas déjà dans une team"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        String playerName = player.getName();
        String inviteName = invite.getName();

        for (String team : config.getConfigurationSection(basepath).getKeys(false)) {
            String path = basepath + team + "player_in_team.";

            if (config.getStringList(path).contains(playerName)) {
                MessagesManager.sendMessageType(player,Component.text("§4tu n'es pas le chef de ta team"), Prefix.DUNGEON, MessageType.ERROR, false);
                return;
            }
        }

        if (!config.getConfigurationSection(basepath).contains(playerName)){
            MessagesManager.sendMessageType(player,Component.text("§4Tu n'as pas de team ou n'es pas déjà dans une team"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        if (playerName.equals(inviteName)){
            MessagesManager.sendMessageType(player,Component.text("§4Vous ne pouvez pas vous inviter dans votre team"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        if (config.getStringList(basepath + playerName + "player_in_team.").size() == 4){
            MessagesManager.sendMessageType(player,Component.text("§4Vous êtes déjà 4 dans votre équipe !"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        for (String team : config.getConfigurationSection(basepath).getKeys(false)) {
            String path = basepath + team + "player_in_team.";

            if (config.getStringList(path).contains(inviteName)) {
                MessagesManager.sendMessageType(player,Component.text("§4le joueur est déjà dans une team"), Prefix.DUNGEON, MessageType.ERROR, false);
                return;
            }
        }

        if (config.getConfigurationSection(basepath).contains(inviteName)) {
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

        invitations.put(player.getUniqueId(), invite.getUniqueId());
        MessagesManager.sendMessageType(player,Component.text(" Invitation dans la team envoyer a " + inviteName), Prefix.DUNGEON, MessageType.SUCCESS, false);
        MessagesManager.sendMessageType(invite,Component.text(playerName + " vous invite dans ça team ( /team accept : pour rejoindre, sinon /team deny : pour refuser )" + inviteName), Prefix.DUNGEON, MessageType.SUCCESS, false);
    }

    @Subcommand("team accept")
    @Description("accepter l'invitation")
    public void onTeamInviteAccept (Player player) {

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


        MessagesManager.sendMessageType(player,Component.text("Vous avez rejoind la team de " + inviter.getName() + " !"), Prefix.DUNGEON, MessageType.SUCCESS, false);
        MessagesManager.sendMessageType(inviter,Component.text(player.getName() + " a rejoind votre team !"), Prefix.DUNGEON, MessageType.SUCCESS, false);
        invitations.remove(player.getUniqueId());
        inviteAccept(inviter.getName(), player.getName());
    }

    @Subcommand("team deny")
    @Description("refuser l'invitation")
    public void onTeamInviteDeny (Player player) {

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
    public void onTeamLeave(Player player) {

        if (playerIsInDungeon(player)){
            MessagesManager.sendMessageType(player, Component.text("§4Vous ne pouvez pas quittez une team car vous êtes en plein donjon"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        String playerName = player.getName();
        String basepath = "dungeon." + "team.";
        boolean inTeam = false;

        if (config.getConfigurationSection(basepath) == null){
            MessagesManager.sendMessageType(player, Component.text("§4Tu n'as pas de team ou n'es pas déjà dans une team"), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        for (String team : config.getConfigurationSection(basepath).getKeys(false)) {
            String path = basepath + team + "player_in_team.";

            if (config.getStringList(path).contains(playerName)){
                inTeam = true;
            }
        }

        if (!config.getConfigurationSection("dungeon." + "team.").contains(playerName) && !inTeam){
            MessagesManager.sendMessageType(player, Component.text("§4Tu n'as pas de team "), Prefix.DUNGEON, MessageType.ERROR, false);
            return;
        }

        leaveTeam(playerName);
    }

    @Subcommand("mobspawn list spawn_point")
    @Description("mobspawn list spawn_point")
    @CommandPermission("omc.admin.commands.dungeon.mobspawn.list.spawn_point")
    public void onZoneListSpawnPoint (Player player) {

        if (config.contains("dungeon." + "mob_spawn.")) {
            Set<String> spawn_point = config.getConfigurationSection("dungeon." + "mob_spawn.").getKeys(false);

            if (spawn_point.isEmpty()) {
                MessagesManager.sendMessageType(player, Component.text("§cAucune spawn_point n'est enregistrée"), Prefix.DUNGEON, MessageType.ERROR, false);
            } else {
                MessagesManager.sendMessageType(player, Component.text("§aspawn_point enregistrés :"), Prefix.DUNGEON, MessageType.ERROR, false);
                for (String zone : spawn_point) {
                    player.sendMessage("§e- " + zone);
                }
            }
        } else {
            MessagesManager.sendMessageType(player, Component.text("§cAucune spawn_point n'est enregistré"), Prefix.DUNGEON, MessageType.ERROR, false);
        }
    }

    @Subcommand("mobspawn list mob")
    @Description("mobspawn list mob")
    @CommandPermission("omc.admin.commands.dungeon.mobspawn.list.mob")
    public void onZoneListMob (Player player) { //TODO modifier la liste est pas bonne
        List<String> mobList = new ArrayList<>();

        for (MOBIDS type : MOBIDS.values()) {
            mobList.add(MOBIDS.valueOf(String.valueOf(type)).getMobName());
        }

        if (mobList.isEmpty()) {
            MessagesManager.sendMessageType(player, Component.text("§cAucune entité n'est enregistrée"), Prefix.DUNGEON, MessageType.ERROR, false);
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
    public void onMobSpawn(Player player, @Named("mob") String mob) {

        World dungeons = Bukkit.getWorld("Dungeons");


        if (player.getWorld() != dungeons){
            player.sendMessage("Vous ne pouver pas utiliser ceci dans ce monde");
            return;
        }

        if (! MOBIDS.isValidMobName(mob)){ //TODO modifier pour vérifier si le mob à ajouter existe avec une énumération répertoriant les mobs
            player.sendMessage("ce mob n'existe pas");
            return;
        }

        double x = MobPos.getX();
        double y = MobPos.getY();
        double z = MobPos.getZ();

        addMobSpawn(mob,x,y,z);

        player.sendMessage("mob ajouter");
    }

    @Subcommand("mobspawn remove")
    @Description("mobspawn remove")
    @CommandPermission("omc.admin.commands.dungeon.mobspawn.remove")
    public void removeMobSpawn (Player player, @Named("spawn_point") String spawn_point) {

        if (!config.contains("dungeon." + "mob_spawn." + spawn_point)){
            player.sendMessage(spawn_point + " n'éxiste pas");
            return;
        }

        String path = "dungeon." + "mob_spawn." + spawn_point;
        config.set(path, null);
        DungeonManager.saveReloadConfig();
        listener.reload();
    }

    @Subcommand("yml update")
    @Description("importation du dungeon.yml")
    @CommandPermission("omc.admin.commands.dungeon.yml.update")
    public void updateImportDungeonYML () {
        //TODO importer les données du yml de "resources"
        updateDungeonYML();
    }

    @Subcommand("yml backup")
    @Description("importation du dungeon.yml")
    @CommandPermission("omc.admin.commands.dungeon.yml.backup")
    public void backupDungeonYML (Player player) {
        createBackup(player);
    }

    private void inviteAccept (String teamName, String invite){
        String path = "dungeon." + "team." + teamName;
        List<String>playerInTeam = new ArrayList<>();
        playerInTeam.add(invite);
        config.set(path + ".player_in_team", playerInTeam);
        DungeonManager.saveReloadConfig();

    }

    private void createTeam (String teamName) {
        String path = "dungeon." + "team." + teamName;
        List<String>playerInTeam = new ArrayList<>();
        playerInTeam.add(teamName);
        config.set(path + ".player_in_team", playerInTeam);
        DungeonManager.saveReloadConfig();
    }

    public void leaveTeam (String teamName) {
        String basepath = "dungeon." + "team.";
        //if (config.getBoolean(basepath + ".in_dungeon")){
        //    Player player = Bukkit.getPlayer(teamName);
        //    player.sendMessage("Tu es dans un donjon tu ne peux pas quitter la team");
        //}
        if (config.getConfigurationSection(basepath).contains(teamName)) {
            String path = basepath + teamName + ".player_in_team";

            for (String playerInTeam : config.getStringList(path)) {
                Player player = Bukkit.getPlayer(playerInTeam);
                if ( player != null && !player.getName().equals(teamName)){
                    MessagesManager.sendMessageType(player, Component.text("§4Le chef à dissous la team"), Prefix.DUNGEON, MessageType.INFO, false);
                }
            }

            config.set(basepath + teamName, null);
            Player player = Bukkit.getPlayer(teamName);
            MessagesManager.sendMessageType(player, Component.text("§4Vous avez dissous votre team"), Prefix.DUNGEON, MessageType.INFO, false);
            DungeonManager.saveReloadConfig();
            return;
        }

        for (String team : config.getConfigurationSection(basepath).getKeys(false)) {
            String path = basepath + team + ".player_in_team";

            //TODO Envoyer un message au joueur de la team pour dire qu'un' joueur a quitter celle-ci

            if (config.getStringList(path).contains(teamName)) {

                for (String playerInTeam : config.getStringList(path)) {
                    Player player = Bukkit.getPlayer(playerInTeam);
                    if ( player != null && !player.getName().equals(teamName)){
                        MessagesManager.sendMessageType(player, Component.text("§4" + teamName + " a quitté la team"), Prefix.DUNGEON, MessageType.INFO, false);
                    }
                }

                List<String> playerInTeam = config.getStringList(path);
                playerInTeam.remove(teamName);
                config.set(path, playerInTeam);
                DungeonManager.saveReloadConfig();
                break;
            }
        }
    }

    private void addMobSpawn(String mob, double x, double y, double z) {

        if (config.getConfigurationSection("dungeon." + "mob_spawn") != null){

            Set<String> spawnPoints = config.getConfigurationSection("dungeon." + "mob_spawn").getKeys(false);
            String basePath = "dungeon." + "mob_spawn";

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

            String path = basePath + ".spawn_point" + pointIndex; //TODO probleme certains spawn_points se remettent a true

            config.set(path + ".activated", false); //TODO définit sur désactiver pour éviter qu'un mob apparaisse a l'ajout du point
            config.set(path + ".mob", mob);
            config.set(path + ".x", x);
            config.set(path + ".y", y);
            config.set(path + ".z", z);

        } else { // si le mob_spawn de dungeon.yml ne contient pas encore de spawn_point

            String path = "dungeon" + ".mob_spawn" + ".spawn_point1";

            config.set(path + ".activated", false); //TODO définit sur désactiver pour éviter qu'un mob apparaisse a l'ajout du point
            config.set(path + ".mob", mob);
            config.set(path + ".x", x);
            config.set(path + ".y", y);
            config.set(path + ".z", z);
        }

        DungeonManager.saveReloadConfig();
        listener.reload();
    }

    public void updateDungeonYML() {
        try {

            Reader resourceReader = new InputStreamReader(plugin.getResource("dungeon.yml"));
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

        } catch (Exception e) {
            plugin.getLogger().severe("Error while updating dungeon.yml: " + e.getMessage());
        }
    }

    public void createBackup(Player player) {
        if (!file.exists()) {
            player.sendMessage("§4Une erreur est survenu lors de la création de la backup");
            System.out.println("dungeon.yml do not exist, no save create !");
            return;
        }

        try {
            Files.copy(file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            backupConfig = YamlConfiguration.loadConfiguration(backupFile);
            backupConfig.set("dungeon." + "team.",null);
            backupConfig.save(backupFile);
            System.out.println("backup of dungeon.yml successfully create.");
            player.sendMessage("Backup créer avec succès");
        } catch (IOException e) {
            player.sendMessage("§4Une erreur est survenu lors de la création de la backup");
            System.err.println("ERROR during the creation of the backup : " + e.getMessage());
        }
    }

    boolean playerIsInDungeon (Player player) {
        return config.getConfigurationSection("dungeon." + "players_in_dungeon." + player.getName()) != null;
    }

    //if (config.getConfigurationSection("dungeon." + "players_in_dungeon." + player.getName()) == null){
    //            return false;
    //        }
    //        return true;
}