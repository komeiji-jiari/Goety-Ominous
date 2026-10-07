package com.qiuyue.goetyominous.common.effects;

import com.qiuyue.goetyominous.common.init.ModEffects;
import com.qiuyue.goetyominous.common.init.ModParticleTypes;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ModEffectParticles {

    public static List<ParticleOptions> collect(LivingEntity entity) {
        List<ParticleOptions> particles = new ArrayList<>();
        add(particles, entity, ModEffects.TRIAL_OMEN.get(), ModParticleTypes.TRIAL_OMEN.get());
        add(particles, entity, ModEffects.OOZING.get(),
                new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.SLIME_BALL)));
        add(particles, entity, ModEffects.WEAVING.get(),
                new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.COBWEB)));
        add(particles, entity, ModEffects.WIND_CHARGED.get(), ModParticleTypes.SMALL_GUST.get());
        add(particles, entity, ModEffects.INFESTED.get(), ModParticleTypes.INFESTED.get());
        return particles;
    }

    private static void add(List<ParticleOptions> particles, LivingEntity entity, MobEffect effect,
                            ParticleOptions particle) {
        MobEffectInstance instance = entity.getEffect(effect);
        if (instance != null && instance.isVisible()) {
            particles.add(particle);
        }
    }
}
