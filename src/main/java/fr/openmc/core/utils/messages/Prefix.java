package fr.openmc.core.utils.messages;

import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.util.Arrays;

/**
 * Enum representing various prefixes for messages.
 * Each prefix is associated with a formatted string using custom colors and fonts.
 */
public enum Prefix {

    // Font: https://lingojam.com/MinecraftSmallFont
    // For gradient color: https://www.birdflop.com/resources/rgb/
    // Color format: MiniMessage

    OPENMC("<gradient:#BD45E6:#F99BEB>ᴏᴘᴇɴᴍᴄ</gradient>"),
    STAFF("<gradient:#AC3535:#8C052B>ѕᴛᴀꜰꜰ</gradient>"),
    CITY("<gradient:#026404:#2E8F38>ᴄɪᴛʏ</gradient>"),
    CONTEST("<gradient:#FFB800:#F0DF49>ᴄᴏɴᴛᴇѕᴛ</gradient>"),
	DUNGEON("§x§F§F§8§4§0§0§l☠ §x§F§E§9§3§1§2§lD§x§F§E§9§B§1§C§lu§x§F§E§A§2§2§5§ln§x§F§E§A§A§2§E§lg§x§F§D§B§2§3§7§le§x§F§D§B§9§4§0§lo§x§F§D§C§1§4§A§ln §x§F§C§D§0§5§C§l☠")
    ;

    @Getter private final String prefix;
    Prefix(String prefix) {
        this.prefix = prefix;
    }
}