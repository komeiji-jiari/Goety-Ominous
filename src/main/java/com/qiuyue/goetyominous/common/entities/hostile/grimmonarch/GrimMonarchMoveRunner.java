package com.qiuyue.goetyominous.common.entities.hostile.grimmonarch;

public final class GrimMonarchMoveRunner {
    private final GrimMonarchEntity entity;
    private GrimMonarchMove move;
    private int age;
    private int phaseIndex;

    public GrimMonarchMoveRunner(GrimMonarchEntity entity) {
        this.entity = entity;
        this.phaseIndex = -1;
    }

    public GrimMonarchEntity entity() {
        return this.entity;
    }

    public void start(GrimMonarchMove move) {
        this.move = move;
        this.age = 0;
        this.phaseIndex = -1;
    }

    public void stop() {
        this.move = null;
        this.age = 0;
        this.phaseIndex = -1;
    }

    public boolean isRunning() {
        return this.move != null;
    }

    public GrimMonarchMove move() {
        return this.move;
    }

    public float seconds() {
        return (float) this.age / 20.0F;
    }

    public GrimMonarchPhase phase() {
        if (this.move == null || this.phaseIndex < 0 || this.phaseIndex >= this.move.phases().size()) {
            return null;
        }
        return this.move.phases().get(this.phaseIndex);
    }

    public boolean finished() {
        return this.move != null && this.seconds() >= this.move.length();
    }

    public void tick() {
        if (this.move == null) {
            return;
        }
        float seconds = this.seconds();
        while (this.phaseIndex + 1 < this.move.phases().size()
                && seconds >= this.move.phases().get(this.phaseIndex + 1).start()) {
            ++this.phaseIndex;
        }
        ++this.age;
    }
}
