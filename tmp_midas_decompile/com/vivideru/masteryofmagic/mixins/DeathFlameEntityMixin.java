/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.vivideru.masteryofmagic.mixins;

import com.vivideru.masteryofmagic.SchoolSupremeEvents;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Entity.class})
public class DeathFlameEntityMixin {
    @Inject(method={"tick"}, at={@At(value="TAIL")})
    private void gmom$deathFlameTick(CallbackInfo ci) {
        Entity self = (Entity)this;
        SchoolSupremeEvents.tickDeathFlame(self);
    }

    @Inject(method={"remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V"}, at={@At(value="HEAD")})
    private void gmom$deathFlameRemoved(Entity.RemovalReason reason, CallbackInfo ci) {
        SchoolSupremeEvents.deathFlameRemoved((Entity)this);
    }

    @Inject(method={"getTags"}, at={@At(value="RETURN")}, cancellable=true)
    private void gmom$syncDeathFlameMarker(CallbackInfoReturnable<Set<String>> cir) {
        Entity self = (Entity)this;
        if (self.m_8077_() && "Death Flames".equals(self.m_7770_().getString()) && !((Set)cir.getReturnValue()).contains("gmom_death_flame")) {
            HashSet<String> tags = new HashSet<String>((Collection)cir.getReturnValue());
            tags.add("gmom_death_flame");
            cir.setReturnValue(tags);
        }
    }

    @Inject(method={"playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"}, at={@At(value="HEAD")}, cancellable=true)
    private void gmom$lowerDeathFlamePitch(SoundEvent sound, float volume, float pitch, CallbackInfo ci) {
        Entity self = (Entity)this;
        if (self.m_19880_().contains("gmom_death_flame")) {
            self.m_9236_().m_6263_(null, self.m_20185_(), self.m_20186_(), self.m_20189_(), sound, self.m_5720_(), volume, pitch * 0.42f);
            ci.cancel();
        }
    }
}

