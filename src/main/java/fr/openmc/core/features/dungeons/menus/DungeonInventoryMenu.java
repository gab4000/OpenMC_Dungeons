package fr.openmc.core.features.dungeons.menus;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.utils.database.DatabaseManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static fr.openmc.core.features.dungeons.data.DungeonManager.*;

public class DungeonInventoryMenu extends Menu implements Listener {

    Map<Integer, ItemStack> map = new HashMap<>();
    Player player;
    String menuName = "Dungeon Inventory";

    public DungeonInventoryMenu(Player owner) {
        super(owner);
        this.player = owner;
    }

    @Override
    public @NotNull String getName() {
        return menuName;
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        if (inventoryClickEvent.getClick().isLeftClick() || inventoryClickEvent.getClick().isRightClick()){
            inventoryClickEvent.setCancelled(false);
        }
        inventoryClickEvent.setCancelled(false);
    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {

        try {
            map.putAll(loadMenuFromDatabase(player.getUniqueId(), DatabaseManager.getConnection()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        map.put(47, new ItemBuilder(this, Material.PAPER, itemMeta -> {
            itemMeta.setDisplayName("Page précédente");
        }).setCloseButton());

        map.put(49, new ItemBuilder(this, Material.PAPER, itemMeta -> {
            itemMeta.setDisplayName("Exit");
        }).setCloseButton());

        map.put(51, new ItemBuilder(this, Material.PAPER, itemMeta -> {
            itemMeta.setDisplayName("Page suivante");
        }).setCloseButton());

        return map;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent inventoryCloseEvent) throws SQLException {
        Player user = (Player) inventoryCloseEvent.getPlayer();

        if (!inventoryCloseEvent.getView().getTitle().equals(menuName)) {
            return;
        }

        Inventory inventory = inventoryCloseEvent.getInventory();

        map.clear();
        for (int i = 0; i < inventory.getSize(); i++) { // TODO changer mettre "inventory.getSize()-9" plus tard
            ItemStack item = inventory.getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                map.put(i, item);
            }
        }

        saveMenuToDatabase(user.getUniqueId(), map, DatabaseManager.getConnection());

        user.sendMessage("Menu bien identifié et sauvegardé !");
    }
}
