package fr.openmc.core.features.dungeons;

import lombok.Getter;

@Getter
public enum DungeonList {
    //TODO -1 no finish condition

    dungeon_training("Training dungeon",-1),
    dungeon_remember("remember dungeon",30),;

    private final String DungeonName;
    private final int KillToFinishCondition;

    DungeonList(String dungeonName, int i) {
        DungeonName = dungeonName;
        this.KillToFinishCondition = i;
    }
}
