package com.qiuyue.goetyominous.common.entities.ai;

public interface BreezeLike {

    boolean isBreezeStanding();

    void setBreezeSliding();

    void setBreezeStanding();

    void emitGroundParticles(int amount);

    boolean canIdleSlide();
}
