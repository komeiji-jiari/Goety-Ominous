package com.qiuyue.goetyominous.common.entities.hostile.grimmonarch;

import java.util.List;

public record GrimMonarchPhase(float start, float duration, List<GrimMonarchHitWindow> hits,
                               List<GrimMonarchMotion> motions) {
}
