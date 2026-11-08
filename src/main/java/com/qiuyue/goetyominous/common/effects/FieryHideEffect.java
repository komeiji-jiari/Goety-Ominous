package com.qiuyue.goetyominous.common.effects;

import com.Polarice3.Goety.client.particles.AuraParticleOption;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class FieryHideEffect extends MobEffect {
    private static final ColorUtil FIERY_HIDE = new ColorUtil(1.0F, 0.35F, 0.05F);
    public static final ColorUtil FIERY_HIDE_AURA = new ColorUtil(1.0F, 0.8F, 0.15F);

    public FieryHideEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF6A1E);
        this.addAttributeModifier(Attributes.ARMOR,
                "b3fd2e00-13bb-43f4-be6f-53bcffaea36c", 3.0D,
                AttributeModifier.Operation.ADDITION);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level() instanceof ServerLevel serverLevel) {
            entity.clearFire();
            if (entity.tickCount % 5 == 0) {
                ServerParticleUtil.windParticle(serverLevel, FIERY_HIDE,
                        entity.getBbWidth(), entity.getBbHeight() / 2.0F,
                        entity.getId(), entity.position());
            }
            if (entity.tickCount % 15 == 0) {
                serverLevel.sendParticles(
                        new AuraParticleOption(entity.getId(), entity.getBbHeight(), FIERY_HIDE_AURA),
                        entity.getX(), entity.getY() + entity.getBbHeight() / 2.0F, entity.getZ(),
                        1, 0.0D, 0.0D, 0.0D, 0.5D);
            }
        }
    }
}
