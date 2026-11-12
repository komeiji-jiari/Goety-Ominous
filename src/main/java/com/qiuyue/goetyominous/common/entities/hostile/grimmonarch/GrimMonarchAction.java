package com.qiuyue.goetyominous.common.entities.hostile.grimmonarch;

public enum GrimMonarchAction {
    IDLE,
    SPAWN,
    SWEEP,
    DASH_WINDUP,
    DASH_RUN,
    DASH_FINISH,
    CALAMITY_ONE,
    CALAMITY_WINDUP,
    CALAMITY_RUN,
    CALAMITY_FINISH,
    CALAMITY_THREE,
    CLONE,
    ECHO_SPAWN,
    SUMMON,
    DEATH;

    public boolean dash() {
        return this == DASH_RUN || this == CALAMITY_RUN;
    }

    public boolean calamity() {
        return this == CALAMITY_ONE || this == CALAMITY_WINDUP || this == CALAMITY_RUN
                || this == CALAMITY_FINISH || this == CALAMITY_THREE;
    }
}
