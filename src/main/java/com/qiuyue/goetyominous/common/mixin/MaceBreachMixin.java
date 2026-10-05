package com.qiuyue.goetyominous.common.mixin;

import com.qiuyue.goetyominous.common.init.ModEnchantments;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MaceBreachMixin {
    @Shadow
    protected abstract void hurtArmor(DamageSource source, float damage);

    @Shadow
    public abstract int getArmorValue();

    @Shadow
    public abstract double getAttributeValue(net.minecraft.world.entity.ai.attributes.Attribute attribute);

    @Inject(method = "getDamageAfterArmorAbsorb", at = @At("HEAD"), cancellable = true, require = 1)
    private void goetyominous$breachArmorAbsorb(DamageSource source, float damage, CallbackInfoReturnable<Float> cir) {
        if (source.is(DamageTypeTags.BYPASSES_ARMOR)) {
            return;
        }
        this.hurtArmor(source, damage);
        float armor = (float) this.getArmorValue();
        float toughness = (float) this.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
        float f = 2.0F + toughness / 4.0F;
        float j = Mth.clamp(armor - damage / f, armor * 0.2F, 20.0F);
        float ratio = j / 25.0F;
        if (source.getEntity() instanceof LivingEntity attacker) {
            int breach = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.BREACH.get(), attacker.getMainHandItem());
            if (breach > 0) {
                ratio = Mth.clamp(ratio - 0.15F * breach, 0.0F, 1.0F);
            }
        }
        cir.setReturnValue(damage * (1.0F - ratio));
    }
}
