package fr.openmc.core.features.dungeons;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;

@Getter
public enum MOBIDS {
    /**
     * Enumeration representing various monsters for dungeons.
     * Each monster has:
     * - "mobName": representing the monster's identification id
     * - "entity" the type of entity
     * - "xp": the xp it gives
     * - "health" the life it has
     * and the rest is for its equipment
     * ATTENTION THE ENTITY MUST NOT BE WRITTEN IN CAPITALS
     */

    zombie("zombie", EntityType.ZOMBIE, 5, 10, Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.DIAMOND_SWORD,null,0,null,0,null,0,null,0,null,0),
    skeleton("skeleton", EntityType.SKELETON, 5, 10, null, null, null, null, Material.BOW, null, 0, null, 0, null, 0, null, 0, null, 0 ),
    ;

    private final String mobName;
    private final EntityType entity;
    private final int xp;
    private final int health;
    private final Material helmet;
    private final Material chestplate;
    private final Material leggings;
    private final Material boots;
    private final Material weapon;
    private final Enchantment helmetEnchantment;
    private final int helmetLevel;
    private final Enchantment chestplateEnchantment;
    private final int chestplateLevel;
    private final Enchantment leggingsEnchantment;
    private final int leggingsLevel;
    private final Enchantment bootsEnchantment;
    private final int bootsLevel;
    private final Enchantment weaponEnchantment;
    private final int weaponLevel;
    
    MOBIDS (String mobName, EntityType entity, int xp, int health, Material helmet, Material chestplate, Material leggings, Material boots, Material weapon, Enchantment helmetEnchantment, int helmetLevel, Enchantment chestplateEnchantment, int chestplateLevel, Enchantment leggingsEnchantment, int leggingsLevel, Enchantment bootsEnchantment, int bootsLevel, Enchantment weaponEnchantment, int weaponLevel) {

        this.mobName = mobName;
        this.entity = entity;
        this.xp = xp;
        this.health = health;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
        this.weapon = weapon;
        this.helmetEnchantment = helmetEnchantment;
        this.helmetLevel = helmetLevel;
        this.chestplateEnchantment = chestplateEnchantment;
        this.chestplateLevel = chestplateLevel;
        this.leggingsEnchantment = leggingsEnchantment;
        this.leggingsLevel = leggingsLevel;
        this.bootsEnchantment = bootsEnchantment;
        this.bootsLevel = bootsLevel;
        this.weaponEnchantment = weaponEnchantment;
        this.weaponLevel = weaponLevel;

    }

    public static boolean isValidMobName(String mobName) {
        for (MOBIDS mob : values()) {
            if (mob.mobName.equalsIgnoreCase(mobName)) {
                return true;
            }
        }
        return false;
    }

    public static EntityType getMobByName(String mobName) {
        for (MOBIDS mob : values()) {
            if (mob.mobName.equalsIgnoreCase(mobName)) {
                return mob.getEntity();
            }
        }
        return null;
    }

    public static int getExpByName(String mobName) {
        for (MOBIDS mob : values()) {
            if (mob.mobName.equalsIgnoreCase(mobName)) {
                return mob.getXp();
            }
        }
        return 0;
    }
}
