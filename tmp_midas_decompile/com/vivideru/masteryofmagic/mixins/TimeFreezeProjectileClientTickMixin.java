/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.vivideru.masteryofmagic.mixins;

import com.vivideru.masteryofmagic.TimeFreezeRenderAnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Projectile.class})
public abstract class TimeFreezeProjectileClientTickMixin {
    @Unique
    private boolean goetyMasteryOfMagic$capturedFreezeState = false;
    @Unique
    private double goetyMasteryOfMagic$frozenX = 0.0;
    @Unique
    private double goetyMasteryOfMagic$frozenY = 0.0;
    @Unique
    private double goetyMasteryOfMagic$frozenZ = 0.0;
    @Unique
    private Vec3 goetyMasteryOfMagic$frozenDeltaMovement = Vec3.f_82478_;
    @Unique
    private float goetyMasteryOfMagic$frozenYRot = 0.0f;
    @Unique
    private float goetyMasteryOfMagic$frozenXRot = 0.0f;

    @Inject(method={"m_8119_()V"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void goetyMasteryOfMagic$freezeProjectileClientTick(CallbackInfo ci) {
        Entity entity = (Entity)this;
        if (!entity.m_9236_().m_5776_()) {
            return;
        }
        if (!TimeFreezeRenderAnimationState.isFrozen(entity.m_19879_())) {
            this.goetyMasteryOfMagic$capturedFreezeState = false;
            this.goetyMasteryOfMagic$frozenDeltaMovement = Vec3.f_82478_;
            return;
        }
        if (!this.goetyMasteryOfMagic$capturedFreezeState) {
            this.goetyMasteryOfMagic$capturedFreezeState = true;
            this.goetyMasteryOfMagic$frozenX = entity.m_20185_();
            this.goetyMasteryOfMagic$frozenY = entity.m_20186_();
            this.goetyMasteryOfMagic$frozenZ = entity.m_20189_();
            this.goetyMasteryOfMagic$frozenDeltaMovement = entity.m_20184_();
            this.goetyMasteryOfMagic$frozenYRot = entity.m_146908_();
            this.goetyMasteryOfMagic$frozenXRot = entity.m_146909_();
            if (this.goetyMasteryOfMagic$frozenDeltaMovement.m_82556_() < 1.0E-7) {
                this.goetyMasteryOfMagic$frozenDeltaMovement = Vec3.m_82498_((float)this.goetyMasteryOfMagic$frozenXRot, (float)this.goetyMasteryOfMagic$frozenYRot);
            }
        }
        entity.m_6034_(this.goetyMasteryOfMagic$frozenX, this.goetyMasteryOfMagic$frozenY, this.goetyMasteryOfMagic$frozenZ);
        entity.f_19854_ = this.goetyMasteryOfMagic$frozenX;
        entity.f_19855_ = this.goetyMasteryOfMagic$frozenY;
        entity.f_19856_ = this.goetyMasteryOfMagic$frozenZ;
        entity.f_19790_ = this.goetyMasteryOfMagic$frozenX;
        entity.f_19791_ = this.goetyMasteryOfMagic$frozenY;
        entity.f_19792_ = this.goetyMasteryOfMagic$frozenZ;
        entity.m_146867_();
        entity.m_20256_(Vec3.f_82478_);
        entity.m_146922_(this.goetyMasteryOfMagic$frozenYRot);
        entity.m_146926_(this.goetyMasteryOfMagic$frozenXRot);
        entity.f_19859_ = this.goetyMasteryOfMagic$frozenYRot;
        entity.f_19860_ = this.goetyMasteryOfMagic$frozenXRot;
        ci.cancel();
    }
}

