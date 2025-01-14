package fr.openmc.core.features.dungeons.menus;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.dungeons.DungeonList;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static fr.openmc.core.features.dungeons.menus.ExplorerMenu.tpAvailableDungeon;

public class DungeonsChoiceMenu extends Menu {
    //TODO note:
    // - quand le joueur/la team est tp dans le donjons celui-ci/celle-ci doit attendre 10s ou 5s pour commencer le donjons
    // donc utiliser le DynamicCooldown
    // - faire apparaitre le nombre de mob a tuer pour finir le donjons
    Player player;

    @Getter
    public static Map<UUID, Integer> dungeonCondition = new HashMap<>();

    public DungeonsChoiceMenu(Player player) {
        super(player);
        this.player = player;
    }

    @Override
    public @NotNull String getName() {
        return "";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGE;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> map = new HashMap<>();
        int i = 0;
        for (DungeonList dungeon : DungeonList.values()) {
            final int index = i;
            map.put(index, new ItemBuilder(this, Material.PAPER, itemMeta -> {
                itemMeta.setDisplayName("donjon " + index);
            }).setOnClick(inventoryClickEvent -> {
                tpAvailableDungeon(player, dungeon + ".");
                getOwner().closeInventory();
            }));
            i++;
        }

        return map;
    }
}
