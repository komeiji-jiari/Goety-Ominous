package com.qiuyue.goetyominous.common.entities.ally.pp;

import net.minecraft.world.entity.monster.piglin.PiglinArmPose;

public interface TravelerAnimatable {

    PiglinArmPose getArmPose();

    boolean isSitting();

    void sit(boolean sit);

    int getKickTicks();

    void setKickTicks(int kickTicks);

    int getKickCoolDown();

    void setKickCoolDown(int kickCoolDown);
}
