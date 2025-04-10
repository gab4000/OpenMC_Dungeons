package fr.openmc.core.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class PlayerUtils {

    /**
     * @param player Player to be tested
     * @return If the player is safe
     */
    private boolean isInSafePosition(Player player) {
        if (player.isFlying()) return false;
        if (player.isInsideVehicle()) return false;
        if (player.isGliding()) return false;
        if (player.isSleeping()) return false;
        if (player.isUnderWater()) return false;
        if (player.isFlying()) return false;
        if (player.isVisualFire()) return false;
        // TODO: Check si le block en pile, sur la tête et en dessous (trapdoor) est plein

        return true;
    }

    /**
     * Return a skull of a player.
     *
     * @param player A Player
     * @return an ItemStack
     */
    public static ItemStack getPlayerSkull(Player player) {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        meta.setOwner(player.getName());
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
