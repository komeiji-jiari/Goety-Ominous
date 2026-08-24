/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.WalkAnimationState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package com.vivideru.masteryofmagic.mixins;

import net.minecraft.world.entity.WalkAnimationState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={WalkAnimationState.class})
public interface WalkAnimationStateAccessor {
    @Accessor(value="speedOld")
    public float gmom$getSpeedOld();

    @Accessor(value="speedOld")
    public void gmom$setSpeedOld(float var1);

    @Accessor(value="speed")
    public float gmom$getSpeed();

    @Accessor(value="speed")
    public void gmom$setSpeed(float var1);

    @Accessor(value="position")
    public float gmom$getPosition();

    @Accessor(value="position")
    public void gmom$setPosition(float var1);
}

