package com.qiuyue.goetyominous.common.entities.hostile.grimmonarch;

public record GrimMonarchHitWindow(float start, float end, double damageMultiplier, Kind kind, double radius) {
    public enum Kind {
        CLAW,
        SWEEP,
        PATH,
        RADIAL
    }
}
