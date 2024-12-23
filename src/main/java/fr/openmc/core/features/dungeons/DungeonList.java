package fr.openmc.core.features.dungeons;

import lombok.Getter;

@Getter
public enum DungeonList {
    dungeon_training("dungeon_training"),;

    private final String dungeonName;

    DungeonList (String dungeonName) {
        this.dungeonName = dungeonName;
    }
}
