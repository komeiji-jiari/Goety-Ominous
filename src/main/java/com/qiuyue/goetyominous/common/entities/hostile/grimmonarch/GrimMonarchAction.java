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
    DEATH,
    SLICE_START,
    SLICE_HIT_1,
    SLICE_HIT_2,
    SLICE_HIT_3,
    SLICE_HIDE_SPRINT,
    SLICE_HIDE_NORMAL,
    SLICE_HIDE_SWING,
    SLICE_FINISH,
    SLICE_SUMMON,
    COUNTER_LEFT,
    COUNTER_RIGHT,
    COUNTER_HIDE,
    COUNTER_SLAM;

    public boolean dash() {
        return this == DASH_RUN || this == CALAMITY_RUN;
    }

    public boolean calamity() {
        return this == CALAMITY_ONE || this == CALAMITY_WINDUP || this == CALAMITY_RUN
                || this == CALAMITY_FINISH || this == CALAMITY_THREE;
    }

    public boolean counter() {
        return this == COUNTER_LEFT || this == COUNTER_RIGHT
                || this == COUNTER_HIDE || this == COUNTER_SLAM;
    }

    public boolean slam() {
        return this == SLICE_FINISH || this == COUNTER_SLAM;
    }

    public boolean slice() {
        return this == SLICE_START || this == SLICE_HIT_1 || this == SLICE_HIT_2
                || this == SLICE_HIT_3 || this == SLICE_HIDE_SPRINT || this == SLICE_HIDE_NORMAL
                || this == SLICE_HIDE_SWING || this == SLICE_FINISH || this == SLICE_SUMMON;
    }
}
