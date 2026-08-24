/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.client.particles.ShockwaveParticleOption
 *  com.Polarice3.Goety.client.particles.SphereExplodeParticleOption
 *  com.Polarice3.Goety.init.ModSounds
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.SimpleParticleType
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraft.world.level.Level$ExplosionInteraction
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package com.vivideru.masteryofmagic.spells.midas;

import com.Polarice3.Goety.client.particles.ShockwaveParticleOption;
import com.Polarice3.Goety.client.particles.SphereExplodeParticleOption;
import com.Polarice3.Goety.init.ModSounds;
import com.vivideru.masteryofmagic.entity.PhilosopherKingMidasEntity;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModBlocks;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModParticleTypes;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModSounds;
import com.vivideru.masteryofmagic.spells.SoulBarrierSpell;
import com.vivideru.masteryofmagic.spells.midas.MidasBossSpell;
import com.vivideru.masteryofmagic.spells.midas.MidasNarrator;
import com.vivideru.masteryofmagic.spells.midas.MidasSpellThreatRegistry;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class MidasMagicBarrierSpell
implements MidasBossSpell {
    public static final MidasMagicBarrierSpell INSTANCE = new MidasMagicBarrierSpell();
    private static final ResourceLocation ID = new ResourceLocation("goety_mastery_of_magic", "midas_magic_barrier");
    private static final int DISCHARGE_WARNING_TICK = 100;
    private static final int DISCHARGE_BLAST_TICK = 140;
    private static final double DISCHARGE_RADIUS = 20.0;
    private static final double BARRIER_PUSH_RADIUS = 10.0;
    private static final double DISCHARGE_PUSH_RADIUS = 16.0;
    private static final double BARRIER_PUSH_STRENGTH = 1.75;
    private static final double DISCHARGE_PUSH_STRENGTH = 5.0;
    private static final float DISCHARGE_EXPLOSION_POWER = 10.0f;
    private static final float CORE_RED = 1.0f;
    private static final float CORE_GREEN = 0.08f;
    private static final float CORE_BLUE = 1.0f;

    private MidasMagicBarrierSpell() {
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public int castingFlag() {
        return 2;
    }

    @Override
    public int cooldownTicks() {
        return 60;
    }

    @Override
    public int maximumCastTicks() {
        return 1200;
    }

    @Override
    public void start(ServerLevel level, PhilosopherKingMidasEntity midas, @Nullable LivingEntity target) {
        MidasNarrator.announce(level, midas, "narration.goety_mastery_of_magic.midas.barrier.1", "narration.goety_mastery_of_magic.midas.barrier.2", "narration.goety_mastery_of_magic.midas.barrier.3");
        midas.clearRecentDamagePressure();
        midas.clearPendingBarrierDamageReaction();
        MidasMagicBarrierSpell.pushEntitiesRadially(level, midas, 10.0, 1.75, false);
        MidasMagicBarrierSpell.castBarrierTick(level, (LivingEntity)midas);
    }

    @Override
    public void tick(ServerLevel level, PhilosopherKingMidasEntity midas, @Nullable LivingEntity target, int castTick) {
        if (castTick > 0 && castTick % 5 == 0) {
            MidasMagicBarrierSpell.castBarrierTick(level, (LivingEntity)midas);
        }
        if (castTick == 100) {
            MidasNarrator.announce(level, midas, "narration.goety_mastery_of_magic.midas.discharge.1", "narration.goety_mastery_of_magic.midas.discharge.2", "narration.goety_mastery_of_magic.midas.discharge.3");
            MidasMagicBarrierSpell.beginDischarge(level, midas);
        } else if (castTick > 100 && castTick < 140 && (castTick & 1) == 0) {
            MidasMagicBarrierSpell.spawnDischargeChargeParticles(level, midas);
        } else if (castTick == 140) {
            MidasMagicBarrierSpell.dischargeBarrier(level, midas);
        }
    }

    @Override
    public boolean shouldContinue(ServerLevel level, PhilosopherKingMidasEntity midas, @Nullable LivingEntity target, int castTick) {
        if (castTick < 20) {
            return true;
        }
        if (castTick >= 100 && castTick <= 140) {
            return true;
        }
        if (castTick > 140) {
            return false;
        }
        return castTick < this.maximumCastTicks() && (MidasSpellThreatRegistry.hasDangerousSpellThreat(level, midas) || midas.hasPendingBarrierDamageReaction() || midas.hasRecentDamagePressure());
    }

    public static void castBarrierTick(ServerLevel level, LivingEntity caster) {
        SoulBarrierSpell.createSphereBarrier(level, caster);
    }

    private static void beginDischarge(ServerLevel level, PhilosopherKingMidasEntity midas) {
        level.m_6263_(null, midas.m_20185_(), midas.m_20186_(), midas.m_20189_(), (SoundEvent)GoetyMasteryOfMagicModSounds.MIDAS_GRUNT.get(), SoundSource.HOSTILE, 2.8f, 0.72f + midas.m_217043_().m_188501_() * 0.08f);
        Vec3 center = MidasMagicBarrierSpell.barrierCenter((Entity)midas);
        level.m_8767_((ParticleOptions)new ShockwaveParticleOption(1.0f, 0.08f, 1.0f), center.f_82479_, center.f_82480_, center.f_82481_, 0, 0.0, 0.0, 0.0, 0.0);
        MidasMagicBarrierSpell.spawnDischargeChargeParticles(level, midas);
    }

    private static void spawnDischargeChargeParticles(ServerLevel level, PhilosopherKingMidasEntity midas) {
        Vec3 center = MidasMagicBarrierSpell.barrierCenter((Entity)midas);
        RandomSource random = midas.m_217043_();
        for (int i = 0; i < 8; ++i) {
            Vec3 direction = new Vec3(random.m_188500_() * 2.0 - 1.0, random.m_188500_() * 2.0 - 1.0, random.m_188500_() * 2.0 - 1.0);
            direction = direction.m_82556_() < 1.0E-5 ? new Vec3(0.0, 1.0, 0.0) : direction.m_82541_();
            double radius = 6.0 + random.m_188500_() * 4.0;
            Vec3 spawn = center.m_82549_(direction.m_82490_(radius));
            Vec3 velocity = center.m_82546_(spawn).m_82541_().m_82490_(0.22 + random.m_188500_() * 0.12);
            level.m_8767_((ParticleOptions)((SimpleParticleType)GoetyMasteryOfMagicModParticleTypes.MIDAS_ALCHEMY.get()), spawn.f_82479_, spawn.f_82480_, spawn.f_82481_, 0, velocity.f_82479_, velocity.f_82480_, velocity.f_82481_, 1.0);
        }
    }

    private static void dischargeBarrier(ServerLevel level, PhilosopherKingMidasEntity midas) {
        Vec3 center = MidasMagicBarrierSpell.barrierCenter((Entity)midas);
        level.m_8767_((ParticleOptions)new ShockwaveParticleOption(1.0f, 0.08f, 1.0f, 1.0f, 20.0f, 2, 18, true), center.f_82479_, center.f_82480_, center.f_82481_, 0, 0.0, 0.0, 0.0, 0.0);
        level.m_8767_((ParticleOptions)new SphereExplodeParticleOption(1.0f, 0.08f, 1.0f, 20.0f, 2), center.f_82479_, center.f_82480_, center.f_82481_, 1, 0.0, 0.0, 0.0, 0.0);
        level.m_8767_((ParticleOptions)((SimpleParticleType)GoetyMasteryOfMagicModParticleTypes.MIDAS_ALCHEMY.get()), center.f_82479_, center.f_82480_, center.f_82481_, 64, 3.0, 3.0, 3.0, 0.65);
        level.m_6263_(null, center.f_82479_, center.f_82480_, center.f_82481_, (SoundEvent)ModSounds.REDSTONE_EXPLODE.get(), SoundSource.HOSTILE, 4.0f, 0.68f);
        level.m_255391_((Entity)midas, center.f_82479_, center.f_82480_, center.f_82481_, 10.0f, false, Level.ExplosionInteraction.BLOCK);
        MidasMagicBarrierSpell.pushEntitiesRadially(level, midas, 16.0, 5.0, true);
    }

    private static void pushEntitiesRadially(ServerLevel level, PhilosopherKingMidasEntity midas, double radius, double strength, boolean ignoreMidasBarrierShell) {
        Vec3 center = MidasMagicBarrierSpell.barrierCenter((Entity)midas);
        AABB area = new AABB(center, center).m_82400_(radius);
        List entities = level.m_6249_((Entity)midas, area, entity -> entity.m_6084_() && entity.m_20182_().m_82557_(center) <= radius * radius);
        for (Entity entity2 : entities) {
            Vec3 entityCenter = entity2.m_20191_().m_82399_();
            if (MidasMagicBarrierSpell.isBlockedByBarrier(level, center, entityCenter, ignoreMidasBarrierShell)) continue;
            Vec3 radial = entityCenter.m_82546_(center);
            double distance = radial.m_82553_();
            if (distance < 1.0E-4) {
                double angle = midas.m_217043_().m_188500_() * Math.PI * 2.0;
                radial = new Vec3(Math.cos(angle), 0.2, Math.sin(angle));
                distance = 0.0;
            }
            double falloff = 1.0 - Math.min(1.0, distance / radius);
            Vec3 direction = radial.m_82541_().m_82520_(0.0, 0.1 + falloff * 0.16, 0.0).m_82541_();
            double impulse = strength * (0.35 + falloff * 0.65);
            if (entity2 instanceof Projectile) {
                entity2.m_146884_(entity2.m_20182_().m_82549_(direction.m_82490_(0.75)));
            }
            entity2.m_20256_(entity2.m_20184_().m_82549_(direction.m_82490_(impulse)));
            entity2.f_19812_ = true;
            entity2.f_19864_ = true;
        }
    }

    private static boolean isBlockedByBarrier(ServerLevel level, Vec3 start, Vec3 end, boolean ignoreMidasBarrierShell) {
        Vec3 ray = end.m_82546_(start);
        double length = ray.m_82553_();
        int samples = Math.max(1, (int)Math.ceil(length * 4.0));
        BlockPos previous = null;
        for (int i = 1; i < samples; ++i) {
            Vec3 sample = start.m_82549_(ray.m_82490_((double)i / (double)samples));
            BlockPos position = BlockPos.m_274446_((Position)sample);
            if (position.equals(previous)) continue;
            previous = position;
            boolean vanillaBarrier = level.m_8055_(position).m_60713_(Blocks.f_50375_);
            boolean soulBarrier = level.m_8055_(position).m_60713_((Block)GoetyMasteryOfMagicModBlocks.SOUL_BARRIER_BLOCK.get());
            if (!vanillaBarrier && !soulBarrier || ignoreMidasBarrierShell && soulBarrier && Vec3.m_82512_((Vec3i)position).m_82554_(start) <= 5.75) continue;
            return true;
        }
        return false;
    }

    private static Vec3 barrierCenter(Entity caster) {
        return caster.m_20182_().m_82520_(0.0, (double)caster.m_20206_() * 0.5, 0.0);
    }
}

