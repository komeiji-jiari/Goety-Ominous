package com.qiuyue.goetyominous.common.entities.hostile.grimmonarch;

import java.util.List;

public record GrimMonarchMove(String id, float length, List<GrimMonarchPhase> phases,
                              List<GrimMonarchMoveEvent> events, boolean loop) {
}
