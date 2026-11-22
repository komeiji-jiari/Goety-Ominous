package com.qiuyue.goetyominous.common.mixin;

import com.qiuyue.goetyominous.common.entities.hostile.GuzzlerBoss;
import com.qiuyue.goetyominous.common.entities.hostile.ZGuzzlerBoss;
import com.qiuyue.goetyominous.common.entities.hostile.piglin.PiglinShaman;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class PiglinShamanAllianceMixin {
    @Inject(method = "isAlliedTo", at = @At("HEAD"), cancellable = true)
    private void goetyominous$piglinAlliance(Entity other, CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity) (Object) this;
        boolean shaman = self instanceof PiglinShaman
                && (other instanceof AbstractPiglin || other instanceof ZombifiedPiglin)
                || other instanceof PiglinShaman
                && (self instanceof AbstractPiglin || self instanceof ZombifiedPiglin);
        boolean normalGuzzler = self instanceof GuzzlerBoss && !(self instanceof ZGuzzlerBoss)
                && other instanceof AbstractPiglin
                || other instanceof GuzzlerBoss && !(other instanceof ZGuzzlerBoss)
                && self instanceof AbstractPiglin;
        boolean zombifiedGuzzler = self instanceof ZGuzzlerBoss && other instanceof ZombifiedPiglin
                || other instanceof ZGuzzlerBoss && self instanceof ZombifiedPiglin;
        if (shaman || normalGuzzler || zombifiedGuzzler) {
            cir.setReturnValue(true);
        }
    }
}
