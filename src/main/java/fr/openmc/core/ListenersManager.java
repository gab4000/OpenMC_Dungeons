package fr.openmc.core;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.skills.SkillsListener;
import fr.openmc.core.features.mailboxes.MailboxListener;
import fr.openmc.core.features.updates.UpdateListener;
import fr.openmc.core.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ListenersManager {
    public ListenersManager() {
        registerEvents(
                new SessionsListener(),
                new JoinMessageListener(),
                new UpdateListener(),
                new ClockInfos(),
                new MailboxListener(),
                new ChronometerListener(),
                new CubeListener(OMCPlugin.getInstance()),
                new RespawnListener(),
                new SleepListener(),
                new PlayerDeathListener(),
                new AsyncChatListener(OMCPlugin.getInstance()),
                //new DisableCreakings(),
                new SkillsListener()
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
