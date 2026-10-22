package com.qiuyue.goetyominous.common.entities.ally.pp;

import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.piglin.PiglinArmPose;

public interface AlchemistAnimatable extends RangedAttackMob {

    PiglinArmPose getArmPose();

    boolean isGonnaThrowPotion();

    int getArrowsShot();

    void setArrowsShot(int arrowsShot);
}
