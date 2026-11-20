package com.qiuyue.goetyominous.common.mixin;

import com.qiuyue.goetyominous.common.init.ModDamageTypes;
import com.qiuyue.goetyominous.common.items.OminousCandleItem;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class OminousCandleResistanceFloorMixin {

    @Inject(method = "getDamageAfterMagicAbsorb", at = @At("RETURN"), cancellable = true)
    private void goetyominous$floorResistance(DamageSource source, float amount,
                                              CallbackInfoReturnable<Float> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.hasEffect(MobEffects.DAMAGE_RESISTANCE)
                && (source.is(DamageTypes.PLAYER_ATTACK) || source.is(ModDamageTypes.OMINOUS_SHOCKWAVE))
                && source.getDirectEntity() instanceof Player player
                && player.getMainHandItem().getItem() instanceof OminousCandleItem) {
            cir.setReturnValue(Math.max(cir.getReturnValueF(), amount * 0.5F));
        }
    }
}
