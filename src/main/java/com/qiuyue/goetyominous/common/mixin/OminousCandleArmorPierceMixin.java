package com.qiuyue.goetyominous.common.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.qiuyue.goetyominous.common.items.OminousCandleItem;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class OminousCandleArmorPierceMixin {

    @ModifyExpressionValue(method = "getDamageAfterArmorAbsorb",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getArmorValue()I"))
    private int goetyominous$halveArmor(int original, DamageSource source, float amount) {
        if (source.is(DamageTypes.PLAYER_ATTACK)
                && source.getDirectEntity() instanceof Player player
                && player.getMainHandItem().getItem() instanceof OminousCandleItem) {
            return original / 2;
        }
        return original;
    }
}
