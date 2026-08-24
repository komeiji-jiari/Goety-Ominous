/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.projectile.Projectile
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.vivideru.masteryofmagic.mixins;

import com.vivideru.masteryofmagic.TimeFreezeRenderAnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Entity.class})
public abstract class TimeFreezeEntityClientSyncMixin {
    @Inject(method={"m_6453_(DDDFFIZ)V"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void goetyMasteryOfMagic$blockFrozenProjectileLerpTo(double x, double y, double z, float yRot, float xRot, int lerpSteps, boolean teleport, CallbackInfo ci) {
        Entity entity = (Entity)this;
        if (!this.goetyMasteryOfMagic$shouldBlockProjectileClientSync(entity)) {
            return;
        }
        ci.cancel();
    }

    @Inject(method={"m_6001_(DDD)V"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void goetyMasteryOfMagic$blockFrozenProjectileLerpMotion(double x, double y, double z, CallbackInfo ci) {
        Entity entity = (Entity)this;
        if (!this.goetyMasteryOfMagic$shouldBlockProjectileClientSync(entity)) {
            return;
        }
        ci.cancel();
    }

    @Inject(method={"m_217006_(DDD)V"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void goetyMasteryOfMagic$blockFrozenProjectilePacketPosition(double x, double y, double z, CallbackInfo ci) {
        Entity entity = (Entity)this;
        if (!this.goetyMasteryOfMagic$shouldBlockProjectileClientSync(entity)) {
            return;
        }
        ci.cancel();
    }

    private boolean goetyMasteryOfMagic$shouldBlockProjectileClientSync(Entity entity) {
        if (!entity.m_9236_().m_5776_()) {
            return false;
        }
        if (!(entity instanceof Projectile)) {
            return false;
        }
        return TimeFreezeRenderAnimationState.isFrozen(entity.m_19879_());
    }
}

