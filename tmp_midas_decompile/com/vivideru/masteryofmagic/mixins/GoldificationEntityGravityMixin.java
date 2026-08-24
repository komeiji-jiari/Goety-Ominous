/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.vivideru.masteryofmagic.mixins;

import com.vivideru.masteryofmagic.goldification.GoldificationEntityData;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Entity.class})
public abstract class GoldificationEntityGravityMixin {
    @Inject(method={"isNoGravity()Z"}, at={@At(value="HEAD")}, cancellable=true, remap=false, require=0)
    private void forceGoldifiedGravityDev(CallbackInfoReturnable<Boolean> callbackInfo) {
        this.forceGoldifiedGravity(callbackInfo);
    }

    @Inject(method={"m_20159_()Z"}, at={@At(value="HEAD")}, cancellable=true, remap=false, require=0)
    private void forceGoldifiedGravityProduction(CallbackInfoReturnable<Boolean> callbackInfo) {
        this.forceGoldifiedGravity(callbackInfo);
    }

    private void forceGoldifiedGravity(CallbackInfoReturnable<Boolean> callbackInfo) {
        Entity entity = (Entity)this;
        if (!entity.m_9236_().m_5776_() && GoldificationEntityData.isGoldified(entity)) {
            callbackInfo.setReturnValue((Object)false);
        }
    }
}

