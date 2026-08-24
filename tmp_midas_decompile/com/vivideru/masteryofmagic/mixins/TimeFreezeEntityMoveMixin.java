/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.MoverType
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyArg
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.vivideru.masteryofmagic.mixins;

import com.vivideru.masteryofmagic.TimeFreezeRenderAnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Entity.class})
public abstract class TimeFreezeEntityMoveMixin {
    @Inject(method={"m_6478_(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void goetyMasteryOfMagic$blockFrozenProjectileMove(MoverType moverType, Vec3 movement, CallbackInfo ci) {
        Entity entity = (Entity)this;
        if (!entity.m_9236_().m_5776_()) {
            return;
        }
        if (!(entity instanceof Projectile)) {
            return;
        }
        if (!TimeFreezeRenderAnimationState.isFrozen(entity.m_19879_())) {
            return;
        }
        ci.cancel();
    }

    @ModifyArg(method={"m_20343_(DDD)V"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/Entity;m_20011_(Lnet/minecraft/world/phys/AABB;)V", remap=false), index=0, remap=false)
    private AABB goetyMasteryOfMagic$freezeProjectileBoundingBox(AABB original) {
        Entity entity = (Entity)this;
        if (!entity.m_9236_().m_5776_()) {
            return original;
        }
        if (!(entity instanceof Projectile)) {
            return original;
        }
        if (!TimeFreezeRenderAnimationState.isFrozen(entity.m_19879_())) {
            return original;
        }
        return entity.m_20191_();
    }
}

