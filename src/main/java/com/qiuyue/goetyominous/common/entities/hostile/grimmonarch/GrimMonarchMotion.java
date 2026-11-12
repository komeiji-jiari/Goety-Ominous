package com.qiuyue.goetyominous.common.entities.hostile.grimmonarch;

public record GrimMonarchMotion(float start, float duration, Kind kind, double distance, double speed,
                                double height, float mark, float land) {
    public enum Kind {
        BACKSTEP,
        DASH,
        FLIGHT,
        LUNGE,
        SIDESTEP
    }

    public float end() {
        return this.start + this.duration;
    }
}
