package com.qiuyue.goetyominous.common.entities.ai.ac;

import com.github.alexthe666.citadel.animation.Animation;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * 深潜者(Deep One)/深潜者骑士(Deep One Knight)仆从共有的"渊海祭坛易货"能力抽象。
 *
 * 原版 AC 的 DeepOneBarterGoal 只认 instanceof DeepOneBaseEntity,而 Goety 仆从是
 * Summoned,会被原版祭坛 tick 当作普通掉落吞掉(详见 DeepOneBarterGoal 类注释)。
 * 本接口把 DeepOneBarterGoal 需要的仆从侧方法抽出来,让 DeepOneServant 与
 * DeepOneKnightServant 都能走同一套祭坛易货流程。
 */
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
