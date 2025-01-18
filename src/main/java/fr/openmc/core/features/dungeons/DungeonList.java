package fr.openmc.core.features.dungeons;

import lombok.Getter;

@Getter
public enum DungeonList {
    /**
     *  -1 no finish condition
     *  time in milliseconds ( check the DynamicCooldownManager : getRemaining )
     */

    dungeon_training("Training dungeon",-1, 0),
    dungeon_remember("remember dungeon",30, 300000),;

    private final String DungeonName;
    private final int KillToFinishCondition;
    private final long time;

    DungeonList(String dungeonName, int i, long time) {
        DungeonName = dungeonName;
        this.KillToFinishCondition = i;
        this.time = time;
    }
}
