/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 *  software.bernie.geckolib.core.animatable.GeoAnimatable
 */
package com.vivideru.masteryofmagic.mixins;

import com.vivideru.masteryofmagic.TimeFreezeRenderAnimationState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;

@Pseudo
@Mixin(targets={"software.bernie.geckolib.core.animation.AnimationController"}, remap=false)
public abstract class TimeFreezeGeckoLibAnimationControllerMixin {
    @Shadow
    @Final
    protected GeoAnimatable animatable;
    @Unique
    private double goetyMasteryOfMagic$frozenAnimationTick;
    @Unique
    private boolean goetyMasteryOfMagic$hasFrozenAnimationTick;

    @Inject(method={"adjustTick(D)D"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void goetyMasteryOfMagic$reuseFrozenAnimationTick(double tick, CallbackInfoReturnable<Double> cir) {
        Entity entity;
        GeoAnimatable geoAnimatable = this.animatable;
        if (geoAnimatable instanceof Entity && TimeFreezeRenderAnimationState.isAnimationFrozen((entity = (Entity)geoAnimatable).m_19879_()) && this.goetyMasteryOfMagic$hasFrozenAnimationTick) {
            cir.setReturnValue((Object)this.goetyMasteryOfMagic$frozenAnimationTick);
        }
    }

    @Inject(method={"adjustTick(D)D"}, at={@At(value="RETURN")}, remap=false)
    private void goetyMasteryOfMagic$captureAnimationTick(double tick, CallbackInfoReturnable<Double> cir) {
        GeoAnimatable geoAnimatable = this.animatable;
        if (!(geoAnimatable instanceof Entity)) {
            return;
        }
        Entity entity = (Entity)geoAnimatable;
        if (!TimeFreezeRenderAnimationState.isAnimationFrozen(entity.m_19879_()) || !this.goetyMasteryOfMagic$hasFrozenAnimationTick) {
            this.goetyMasteryOfMagic$frozenAnimationTick = (Double)cir.getReturnValue();
            this.goetyMasteryOfMagic$hasFrozenAnimationTick = true;
        }
    }
}

