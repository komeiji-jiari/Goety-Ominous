/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package com.vivideru.masteryofmagic.spells.midas;

import com.vivideru.masteryofmagic.entity.MidasAlchemicalCircleEntity;
import com.vivideru.masteryofmagic.entity.PhilosopherKingMidasEntity;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModEntities;
import com.vivideru.masteryofmagic.spells.midas.MidasBossSpell;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class MidasAlchemicalOrbVolleySpell
implements MidasBossSpell {
    public static final MidasAlchemicalOrbVolleySpell INSTANCE = new MidasAlchemicalOrbVolleySpell();
    private static final ResourceLocation ID = new ResourceLocation("goety_mastery_of_magic", "midas_alchemical_orb_volley");

    private MidasAlchemicalOrbVolleySpell() {
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public int castingFlag() {
        return 8;
    }

    @Override
    public int cooldownTicks() {
        return 180;
    }

    @Override
    public int maximumCastTicks() {
        return 160;
    }

    @Override
    public void start(ServerLevel level, PhilosopherKingMidasEntity midas, @Nullable LivingEntity target) {
        midas.beginAlchemicalOrbVolley(2 + midas.m_217043_().m_188503_(3), 8);
        level.m_5594_(null, midas.m_20183_(), SoundEvents.f_11867_, SoundSource.HOSTILE, 2.0f, 0.68f);
    }

    @Override
    public void tick(ServerLevel level, PhilosopherKingMidasEntity midas, @Nullable LivingEntity target, int castTick) {
        if (target == null || !target.m_6084_() || castTick < midas.getAlchemicalOrbNextTick() || midas.getAlchemicalOrbsSpawned() >= midas.getAlchemicalOrbVolleyCount()) {
            return;
        }
        int index = midas.getAlchemicalOrbsSpawned();
        int count = midas.getAlchemicalOrbVolleyCount();
        Vec3 forward = target.m_20191_().m_82399_().m_82546_(midas.m_146892_()).m_82541_();
        Vec3 side = forward.m_82537_(new Vec3(0.0, 1.0, 0.0));
        if (side.m_82556_() < 1.0E-6) {
            side = new Vec3(1.0, 0.0, 0.0);
        }
        side = side.m_82541_();
        double lateral = ((double)index - (double)(count - 1) * 0.5) * 1.35;
        Vec3 spawn = midas.m_146892_().m_82549_(forward.m_82490_(2.6)).m_82549_(side.m_82490_(lateral));
        Vec3 direction = target.m_20191_().m_82399_().m_82546_(spawn).m_82541_();
        MidasAlchemicalCircleEntity orb = new MidasAlchemicalCircleEntity((EntityType)GoetyMasteryOfMagicModEntities.MIDAS_ALCHEMICAL_CIRCLE.get(), (Level)level);
        orb.configureLaunched(midas, spawn, direction, 0.24);
        level.m_7967_((Entity)orb);
        midas.triggerFastSlashAnimation();
        midas.advanceAlchemicalOrbVolley(castTick, 40);
        level.m_6263_(null, spawn.f_82479_, spawn.f_82480_, spawn.f_82481_, SoundEvents.f_11862_, SoundSource.HOSTILE, 1.8f, 0.82f + (float)index * 0.08f);
    }

    @Override
    public boolean shouldContinue(ServerLevel level, PhilosopherKingMidasEntity midas, @Nullable LivingEntity target, int castTick) {
        return target != null && target.m_6084_() && castTick < this.maximumCastTicks() && midas.getAlchemicalOrbsSpawned() < midas.getAlchemicalOrbVolleyCount();
    }
}

