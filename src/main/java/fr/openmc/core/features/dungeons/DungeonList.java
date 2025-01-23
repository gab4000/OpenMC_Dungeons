package fr.openmc.core.features.dungeons;

import lombok.Getter;

@Getter
public enum DungeonList {
    /**
     *  -1 no finish condition
     *  time in second
     */

    dungeon_training("Training dungeon",-1, 60*5),
    dungeon_remember("remember dungeon",30, 60*5),;

    private final String DungeonName;
    private final int KillToFinishCondition;
    private final int time;

    DungeonList(String dungeonName, int i, int time) {
        DungeonName = dungeonName;
        this.KillToFinishCondition = i;
        this.time = time;
    }
}
