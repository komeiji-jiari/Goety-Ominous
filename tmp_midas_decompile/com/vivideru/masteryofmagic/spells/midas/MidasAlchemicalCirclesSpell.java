/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.init.ModSounds
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.SimpleParticleType
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.Nullable
 */
package com.vivideru.masteryofmagic.spells.midas;

import com.Polarice3.Goety.init.ModSounds;
import com.vivideru.masteryofmagic.entity.MidasAlchemicalCircleEntity;
import com.vivideru.masteryofmagic.entity.PhilosopherKingMidasEntity;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModEntities;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModParticleTypes;
import com.vivideru.masteryofmagic.spells.midas.MidasBossSpell;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public final class MidasAlchemicalCirclesSpell
implements MidasBossSpell {
    public static final MidasAlchemicalCirclesSpell INSTANCE = new MidasAlchemicalCirclesSpell();
    private static final ResourceLocation ID = new ResourceLocation("goety_mastery_of_magic", "midas_alchemical_circles");
    private static final int CAST_TICKS = 60;

    private MidasAlchemicalCirclesSpell() {
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public int castingFlag() {
        return 64;
    }

    @Override
    public int cooldownTicks() {
        return 400;
    }

    @Override
    public int maximumCastTicks() {
        return 60;
    }

    @Override
    public void start(ServerLevel level, PhilosopherKingMidasEntity midas, @Nullable LivingEntity target) {
        level.m_5594_(null, midas.m_20183_(), SoundEvents.f_11868_, SoundSource.HOSTILE, 2.2f, 0.62f);
        int formation = midas.m_217043_().m_188503_(2);
        int count = 3 + midas.m_217043_().m_188503_(3);
        float commonStart = midas.m_217043_().m_188501_() * ((float)Math.PI * 2);
        float commonRadius = 16.0f;
        float firstPlaneInclination = formation == 0 ? (float)Math.toRadians(32.0) : (float)Math.toRadians(67.0);
        float node = formation == 0 ? (float)Math.toRadians(18.0) : (float)Math.toRadians(112.0);
        float speed = formation == 0 ? 0.014f : -0.012f;
        float radialPhase = midas.m_217043_().m_188501_() * ((float)Math.PI * 2);
        for (int i = 0; i < count; ++i) {
            MidasAlchemicalCircleEntity circle = new MidasAlchemicalCircleEntity((EntityType)GoetyMasteryOfMagicModEntities.MIDAS_ALCHEMICAL_CIRCLE.get(), (Level)level);
            float angle = commonStart + (float)(Math.PI * 2 * (double)i / (double)count);
            float inclination = firstPlaneInclination + ((i & 1) == 0 ? 0.0f : 1.5707964f);
            int textureA = midas.m_217043_().m_188503_(12);
            int textureB = midas.m_217043_().m_188503_(12);
            if (textureB == textureA) {
                textureB = (textureB + 1) % 12;
            }
            circle.configure(midas, angle, commonRadius, inclination, node, speed, radialPhase, textureA, textureB);
            level.m_7967_((Entity)circle);
        }
    }

    @Override
    public void tick(ServerLevel level, PhilosopherKingMidasEntity midas, @Nullable LivingEntity target, int castTick) {
        if (castTick % 5 == 0) {
            level.m_8767_((ParticleOptions)((SimpleParticleType)GoetyMasteryOfMagicModParticleTypes.MIDAS_ALCHEMY.get()), midas.m_20185_(), midas.m_20186_() + (double)midas.m_20206_() * 0.5, midas.m_20189_(), 6, 2.0, 2.0, 2.0, 0.04);
        }
        if (castTick % 12 == 0) {
            level.m_5594_(null, midas.m_20183_(), (SoundEvent)ModSounds.PREPARE_SPELL.get(), SoundSource.HOSTILE, 1.7f, 0.85f + midas.m_217043_().m_188501_() * 0.18f);
        }
    }
}

