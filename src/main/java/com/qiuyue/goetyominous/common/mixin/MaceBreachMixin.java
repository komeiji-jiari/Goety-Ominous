package com.qiuyue.goetyominous.common.mixin;

import com.qiuyue.goetyominous.common.init.ModEnchantments;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MaceBreachMixin {
    @Inject(method = "getDamageAfterArmorAbsorb", at = @At("RETURN"), cancellable = true, require = 1)
    private void goetyominous$breachArmorAbsorb(DamageSource source, float damage, CallbackInfoReturnable<Float> cir) {
        if (damage <= 0.0F || source.is(DamageTypeTags.BYPASSES_ARMOR)) {
            return;
        }
        if (source.getEntity() instanceof LivingEntity attacker) {
            int breach = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.BREACH.get(), attacker.getMainHandItem());
            if (breach > 0) {
                float result = cir.getReturnValue();
                cir.setReturnValue(Mth.clamp(result + damage * 0.15F * breach, 0.0F, damage));
            }
        }
    }
}
