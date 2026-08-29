package com.qiuyue.goetyominous.common.entities.ai.ac;

import com.github.alexthe666.citadel.animation.Animation;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;


public interface IDeepOneBarterer {

    @Nullable
    LivingEntity getTarget();

    Animation getAnimation();

    void setAnimation(Animation animation);

    Animation getTradingAnimation();

    boolean isFocusSummoned();

    boolean isWandering();

    boolean isCommanded();

    @Nullable
    BlockPos getLastAltarPos();

    void setLastAltarPos(@Nullable BlockPos lastAltarPos);

    void swapItemsForAnimation(ItemStack item);

    SoundEvent getAdmireSound();
}
