package fr.openmc.core.features.dungeons.effects;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CorruptionEffect {

    public static void applyCorruptionEffect(Player player, int duration) {

        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20*duration, 0));
        player.sendTitle("", PlaceholderAPI.setPlaceholders(player, "%img_souvenir_1%"), 20, 20*duration, 10);
        //TODO mettre un effet sur l'écran du joueur et/ou modifier la texture des coeurs comme avec le poison ou la poudreuse par exemple
    }
}
