package com.qiuyue.goetyominous.common.mixin;

import com.qiuyue.goetyominous.common.entities.hostile.GuzzlerBoss;
import com.qiuyue.goetyominous.common.entities.hostile.ZGuzzlerBoss;
import com.qiuyue.goetyominous.common.entities.hostile.piglin.PiglinShaman;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PiglinAi.class)
public abstract class PiglinShamanAngerMixin {
    @Inject(method = "wasHurtBy", at = @At("HEAD"), cancellable = true)
    private static void goetyominous$skipPiglinAnger(Piglin piglin, LivingEntity attacker, CallbackInfo ci) {
        if (attacker instanceof PiglinShaman
                || attacker instanceof GuzzlerBoss && !(attacker instanceof ZGuzzlerBoss)) {
            ci.cancel();
        }
    }
}
