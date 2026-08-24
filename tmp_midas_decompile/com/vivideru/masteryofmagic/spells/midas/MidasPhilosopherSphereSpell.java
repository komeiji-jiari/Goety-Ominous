/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
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

import com.vivideru.masteryofmagic.entity.PhilosopherKingMidasEntity;
import com.vivideru.masteryofmagic.entity.PhilosopherSphereEntity;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModEntities;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModSounds;
import com.vivideru.masteryofmagic.spells.midas.MidasBossSpell;
import com.vivideru.masteryofmagic.spells.midas.MidasNarrator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class MidasPhilosopherSphereSpell
implements MidasBossSpell {
    public static final MidasPhilosopherSphereSpell INSTANCE = new MidasPhilosopherSphereSpell();
    public static final int MAX_ACTIVE_SPHERES = 1;
    private static final int SUMMON_INTERVAL_TICKS = 600;
    private static final int SUMMON_TICK = 12;
    private static final ResourceLocation ID = new ResourceLocation("goety_mastery_of_magic", "midas_philosopher_sphere");

    private MidasPhilosopherSphereSpell() {
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public int castingFlag() {
        return 4;
    }

    @Override
    public int cooldownTicks() {
        return 600 - this.maximumCastTicks();
    }

    @Override
    public int maximumCastTicks() {
        return 28;
    }

    @Override
    public void start(ServerLevel level, PhilosopherKingMidasEntity midas, @Nullable LivingEntity target) {
        MidasNarrator.announce(level, midas, "narration.goety_mastery_of_magic.midas.beam.1", "narration.goety_mastery_of_magic.midas.beam.2", "narration.goety_mastery_of_magic.midas.beam.3");
        level.m_5594_(null, midas.m_20183_(), SoundEvents.f_11868_, SoundSource.HOSTILE, 1.8f, 1.22f);
    }

    @Override
    public void tick(ServerLevel level, PhilosopherKingMidasEntity midas, @Nullable LivingEntity target, int castTick) {
        if (castTick == 12 && target != null && target.m_6084_() && MidasPhilosopherSphereSpell.canSummon(level, midas)) {
            MidasPhilosopherSphereSpell.spawnSphere(level, midas, target);
        }
    }

    @Override
    public boolean shouldContinue(ServerLevel level, PhilosopherKingMidasEntity midas, @Nullable LivingEntity target, int castTick) {
        return target != null && target.m_6084_() && castTick < this.maximumCastTicks();
    }

    public static boolean canSummon(ServerLevel level, PhilosopherKingMidasEntity midas) {
        return level.m_6443_(PhilosopherSphereEntity.class, midas.m_20191_().m_82400_(192.0), sphere -> sphere.isOwnedBy((Entity)midas) && sphere.m_6084_()).size() < 1;
    }

    public static PhilosopherSphereEntity spawnSphere(ServerLevel level, PhilosopherKingMidasEntity midas, LivingEntity target) {
        int existing = level.m_6443_(PhilosopherSphereEntity.class, midas.m_20191_().m_82400_(192.0), sphere -> sphere.isOwnedBy((Entity)midas) && sphere.m_6084_()).size();
        double angle = midas.m_217043_().m_188500_() * Math.PI * 2.0;
        double spawnRadius = 18.0 + midas.m_217043_().m_188500_() * 6.0;
        Vec3 spawn = midas.m_20182_().m_82520_(Math.cos(angle) * spawnRadius, (double)midas.m_20206_() + 5.0 + midas.m_217043_().m_188500_() * 3.0, Math.sin(angle) * spawnRadius);
        PhilosopherSphereEntity sphere2 = new PhilosopherSphereEntity((EntityType<? extends PhilosopherSphereEntity>)((EntityType)GoetyMasteryOfMagicModEntities.PHILOSOPHER_SPHERE.get()), (Level)level);
        sphere2.setMidasOwner(midas);
        sphere2.setTargetEntity(target);
        sphere2.initializeOrbit(angle, existing % 2 == 0 ? 1 : -1);
        sphere2.m_6034_(spawn.f_82479_, spawn.f_82480_, spawn.f_82481_);
        level.m_7967_((Entity)sphere2);
        level.m_6263_(null, sphere2.m_20185_(), sphere2.m_20186_(), sphere2.m_20189_(), (SoundEvent)GoetyMasteryOfMagicModSounds.MIDAS_TRANSMUTE.get(), SoundSource.HOSTILE, 8.0f, 0.72f);
        level.m_5594_(null, sphere2.m_20183_(), SoundEvents.f_276532_, SoundSource.HOSTILE, 2.8f, 1.25f);
        return sphere2;
    }
}

