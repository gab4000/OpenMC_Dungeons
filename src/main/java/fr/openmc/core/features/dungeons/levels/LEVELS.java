package fr.openmc.core.features.dungeons.levels;

import lombok.Getter;

@Getter
public enum LEVELS {
    level_1(1, 1, 20),
    level_2(2, 20, 50),
    level_3(3, 50, 100),
    level_max(-1, 100, 0), //-1 signifis que le niveau max a été atteind
    ;

    private final int level;
    private final int minXp;
    private final int maxXp;

    LEVELS(int level, int minXp, int maxXp) {
        this.level = level;
        this.minXp = minXp;
        this.maxXp = maxXp;
    }
}
