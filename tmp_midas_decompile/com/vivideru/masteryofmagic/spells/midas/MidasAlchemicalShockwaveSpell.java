/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.init.ModSounds
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.SimpleParticleType
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.damagesource.DamageTypes
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package com.vivideru.masteryofmagic.spells.midas;

import com.Polarice3.Goety.init.ModSounds;
import com.vivideru.masteryofmagic.entity.PhilosopherKingMidasEntity;
import com.vivideru.masteryofmagic.entity.PhilosopherSphereEntity;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModParticleTypes;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModSounds;
import com.vivideru.masteryofmagic.spells.midas.MidasBossSpell;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class MidasAlchemicalShockwaveSpell
implements MidasBossSpell {
    public static final MidasAlchemicalShockwaveSpell INSTANCE = new MidasAlchemicalShockwaveSpell();
    private static final ResourceLocation ID = new ResourceLocation("goety_mastery_of_magic", "midas_alchemical_shockwave");
    private static final int CHARGE_TICKS = 90;
    private static final int WAVE_SPEED_BLOCKS_PER_TICK = 4;
    private static final int MAX_RANGE = 60;
    private static final int WAVE_TICKS = 15;
    private static final float MAX_DAMAGE = 300.0f;
    private static final float MIN_DAMAGE = 50.0f;
    private static final double FULL_DAMAGE_DISTANCE = 10.0;
    private static final float MAX_YAW_CHANGE_PER_TICK = 1.5f;
    private static final float MAX_PITCH_CHANGE_PER_TICK = 1.0f;

    private MidasAlchemicalShockwaveSpell() {
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public int castingFlag() {
        return 32;
    }

    @Override
    public int cooldownTicks() {
        return 900;
    }

    @Override
    public int maximumCastTicks() {
        return 107;
    }

    @Override
    public void start(ServerLevel level, PhilosopherKingMidasEntity midas, @Nullable LivingEntity target) {
        midas.beginAlchemicalShockwave();
        level.m_5594_(null, midas.m_20183_(), (SoundEvent)ModSounds.PREPARE_SPELL.get(), SoundSource.HOSTILE, 3.25f, 0.62f + midas.m_217043_().m_188501_() * 0.2f);
    }

    @Override
    public void tick(ServerLevel level, PhilosopherKingMidasEntity midas, @Nullable LivingEntity target, int castTick) {
        int waveTick;
        if (castTick < 90) {
            if (target != null && target.m_6084_()) {
                MidasAlchemicalShockwaveSpell.turnSlowlyToward(midas, target);
            }
            MidasAlchemicalShockwaveSpell.spawnChargingSphere(level, midas, castTick);
            MidasAlchemicalShockwaveSpell.playChargingSounds(level, midas, castTick);
            return;
        }
        if (castTick == 90) {
            Vec3 direction = midas.m_20154_().m_82541_();
            Vec3 origin = midas.m_146892_().m_82549_(direction.m_82490_(1.5));
            midas.releaseAlchemicalShockwave(origin, direction);
            level.m_6263_(null, origin.f_82479_, origin.f_82480_, origin.f_82481_, (SoundEvent)GoetyMasteryOfMagicModSounds.MIDAS_TRANSMUTE.get(), SoundSource.HOSTILE, 4.5f, 0.62f);
            level.m_6263_(null, origin.f_82479_, origin.f_82480_, origin.f_82481_, SoundEvents.f_11913_, SoundSource.HOSTILE, 4.0f, 0.78f);
        }
        if ((waveTick = castTick - 90) < 0 || waveTick >= 15) {
            return;
        }
        double fromDistance = waveTick * 4;
        double toDistance = Math.min(60.0, fromDistance + 4.0);
        MidasAlchemicalShockwaveSpell.propagateWaveSlice(level, midas, fromDistance, toDistance);
    }

    @Override
    public boolean shouldContinue(ServerLevel level, PhilosopherKingMidasEntity midas, @Nullable LivingEntity target, int castTick) {
        return castTick < this.maximumCastTicks();
    }

    private static void turnSlowlyToward(PhilosopherKingMidasEntity midas, LivingEntity target) {
        Vec3 aim = target.m_20191_().m_82399_().m_82546_(midas.m_146892_());
        if (aim.m_82556_() < 1.0E-6) {
            return;
        }
        double horizontal = Math.sqrt(aim.f_82479_ * aim.f_82479_ + aim.f_82481_ * aim.f_82481_);
        float desiredYaw = (float)(Mth.m_14136_((double)aim.f_82481_, (double)aim.f_82479_) * 57.2957763671875) - 90.0f;
        float desiredPitch = (float)(-(Mth.m_14136_((double)aim.f_82480_, (double)horizontal) * 57.2957763671875));
        float yawStep = Mth.m_14036_((float)Mth.m_14177_((float)(desiredYaw - midas.m_146908_())), (float)-1.5f, (float)1.5f);
        float pitchStep = Mth.m_14036_((float)Mth.m_14177_((float)(desiredPitch - midas.m_146909_())), (float)-1.0f, (float)1.0f);
        float yaw = midas.m_146908_() + yawStep;
        float pitch = Mth.m_14036_((float)(midas.m_146909_() + pitchStep), (float)-90.0f, (float)90.0f);
        midas.m_146922_(yaw);
        midas.f_20885_ = yaw;
        midas.f_20883_ = yaw;
        midas.m_146926_(pitch);
    }

    private static void spawnChargingSphere(ServerLevel level, PhilosopherKingMidasEntity midas, int castTick) {
        Vec3 facing = midas.m_20154_().m_82541_();
        Vec3 center = midas.m_146892_().m_82549_(facing.m_82490_(3.0));
        RandomSource random = midas.m_217043_();
        float progress = Mth.m_14036_((float)((float)castTick / 90.0f), (float)0.0f, (float)1.0f);
        double sphereRadius = 0.55 + (double)progress * 1.05;
        level.m_8767_((ParticleOptions)((SimpleParticleType)GoetyMasteryOfMagicModParticleTypes.MIDAS_ALCHEMY.get()), center.f_82479_, center.f_82480_, center.f_82481_, 4 + (int)(progress * 8.0f), sphereRadius * 0.25, sphereRadius * 0.25, sphereRadius * 0.25, 0.015);
        int streams = 5 + (int)(progress * 5.0f);
        for (int index = 0; index < streams; ++index) {
            Vec3 offset = MidasAlchemicalShockwaveSpell.randomUnitVector(random).m_82490_(sphereRadius + 1.1 + random.m_188500_() * 1.8);
            Vec3 spawn = center.m_82549_(offset);
            Vec3 velocity = center.m_82546_(spawn).m_82541_().m_82490_(0.12 + (double)progress * 0.16);
            level.m_8767_((ParticleOptions)((SimpleParticleType)GoetyMasteryOfMagicModParticleTypes.MIDAS_ALCHEMY_SMALL.get()), spawn.f_82479_, spawn.f_82480_, spawn.f_82481_, 0, velocity.f_82479_, velocity.f_82480_, velocity.f_82481_, 1.0);
        }
    }

    private static void playChargingSounds(ServerLevel level, PhilosopherKingMidasEntity midas, int castTick) {
        RandomSource random = midas.m_217043_();
        if (castTick % 8 == 0) {
            level.m_5594_(null, midas.m_20183_(), (SoundEvent)GoetyMasteryOfMagicModSounds.MIDAS_GRUNT.get(), SoundSource.HOSTILE, 2.8f, 0.72f + random.m_188501_() * 0.42f);
        }
        if (castTick % 4 == 0) {
            level.m_5594_(null, midas.m_20183_(), (SoundEvent)ModSounds.PREPARE_SPELL.get(), SoundSource.HOSTILE, 2.5f, 0.58f + random.m_188501_() * 0.7f);
        }
    }

    private static void propagateWaveSlice(ServerLevel level, PhilosopherKingMidasEntity midas, double fromDistance, double toDistance) {
        Vec3 origin = midas.getAlchemicalShockwaveOrigin();
        Vec3 direction = midas.getAlchemicalShockwaveDirection();
        if (direction.m_82556_() < 0.99) {
            return;
        }
        MidasAlchemicalShockwaveSpell.destroyBlocksInSlice(level, midas, origin, direction, fromDistance, toDistance);
        MidasAlchemicalShockwaveSpell.damageEntitiesInSlice(level, midas, origin, direction, fromDistance, toDistance);
        MidasAlchemicalShockwaveSpell.spawnWaveParticles(level, midas.m_217043_(), origin, direction, fromDistance, toDistance);
    }

    private static void destroyBlocksInSlice(ServerLevel level, PhilosopherKingMidasEntity midas, Vec3 origin, Vec3 direction, double fromDistance, double toDistance) {
        double maximumRadius = MidasAlchemicalShockwaveSpell.radiusAt(toDistance) + 1.5;
        Vec3 start = origin.m_82549_(direction.m_82490_(fromDistance));
        Vec3 end = origin.m_82549_(direction.m_82490_(toDistance));
        int minX = Mth.m_14107_((double)(Math.min(start.f_82479_, end.f_82479_) - maximumRadius));
        int minY = Mth.m_14107_((double)(Math.min(start.f_82480_, end.f_82480_) - maximumRadius));
        int minZ = Mth.m_14107_((double)(Math.min(start.f_82481_, end.f_82481_) - maximumRadius));
        int maxX = Mth.m_14107_((double)(Math.max(start.f_82479_, end.f_82479_) + maximumRadius));
        int maxY = Mth.m_14107_((double)(Math.max(start.f_82480_, end.f_82480_) + maximumRadius));
        int maxZ = Mth.m_14107_((double)(Math.max(start.f_82481_, end.f_82481_) + maximumRadius));
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    BlockState state;
                    cursor.m_122178_(x, y, z);
                    Vec3 center = Vec3.m_82512_((Vec3i)cursor);
                    Vec3 delta = center.m_82546_(origin);
                    double forwardDistance = delta.m_82526_(direction);
                    if (forwardDistance < fromDistance || forwardDistance >= toDistance) continue;
                    Vec3 perpendicular = delta.m_82546_(direction.m_82490_(forwardDistance));
                    double radius = MidasAlchemicalShockwaveSpell.radiusAt(forwardDistance);
                    if (perpendicular.m_82556_() > radius * radius || (state = level.m_8055_((BlockPos)cursor)).m_60795_() || state.m_60713_(Blocks.f_50752_)) continue;
                    level.m_46953_((BlockPos)cursor, false, (Entity)midas);
                }
            }
        }
    }

    private static void damageEntitiesInSlice(ServerLevel level, PhilosopherKingMidasEntity midas, Vec3 origin, Vec3 direction, double fromDistance, double toDistance) {
        double maximumRadius = MidasAlchemicalShockwaveSpell.radiusAt(toDistance) + 3.0;
        Vec3 middle = origin.m_82549_(direction.m_82490_((fromDistance + toDistance) * 0.5));
        AABB search = new AABB(middle, middle).m_82400_(maximumRadius + (toDistance - fromDistance) * 0.5);
        List targets = level.m_6443_(LivingEntity.class, search, target -> {
            Player player;
            return target.m_6084_() && target != midas && !(target instanceof PhilosopherSphereEntity) && (!(target instanceof Player) || !(player = (Player)target).m_7500_() && !player.m_5833_());
        });
        Holder.Reference damageType = level.m_9598_().m_175515_(Registries.f_268580_).m_246971_(DamageTypes.f_268534_);
        DamageSource source = new DamageSource((Holder)damageType, (Entity)midas, (Entity)midas);
        for (LivingEntity target2 : targets) {
            float damage;
            Vec3 delta = target2.m_20191_().m_82399_().m_82546_(origin);
            double forwardDistance = delta.m_82526_(direction);
            if (forwardDistance < fromDistance || forwardDistance >= toDistance) continue;
            Vec3 perpendicular = delta.m_82546_(direction.m_82490_(forwardDistance));
            double radius = MidasAlchemicalShockwaveSpell.radiusAt(forwardDistance) + (double)target2.m_20205_() * 0.5;
            if (perpendicular.m_82556_() > radius * radius || !midas.markAlchemicalShockwaveHit(target2) || !target2.m_6469_(source, damage = MidasAlchemicalShockwaveSpell.damageAt(forwardDistance))) continue;
            Vec3 impulse = direction.m_82490_(2.4).m_82549_(perpendicular.m_82556_() < 1.0E-6 ? Vec3.f_82478_ : perpendicular.m_82541_().m_82490_(0.65)).m_82520_(0.0, 0.35, 0.0);
            target2.m_20256_(target2.m_20184_().m_82549_(impulse));
            target2.f_19812_ = true;
            target2.f_19864_ = true;
        }
    }

    private static void spawnWaveParticles(ServerLevel level, RandomSource random, Vec3 origin, Vec3 direction, double fromDistance, double toDistance) {
        Vec3 referenceUp = Math.abs(direction.f_82480_) < 0.95 ? new Vec3(0.0, 1.0, 0.0) : new Vec3(1.0, 0.0, 0.0);
        Vec3 right = direction.m_82537_(referenceUp).m_82541_();
        Vec3 up = right.m_82537_(direction).m_82541_();
        double frontDistance = (fromDistance + toDistance) * 0.5;
        double radius = MidasAlchemicalShockwaveSpell.radiusAt(frontDistance);
        Vec3 frontCenter = origin.m_82549_(direction.m_82490_(frontDistance));
        level.m_8767_((ParticleOptions)((SimpleParticleType)GoetyMasteryOfMagicModParticleTypes.MIDAS_ALCHEMY.get()), frontCenter.f_82479_, frontCenter.f_82480_, frontCenter.f_82481_, 28, radius * 0.55, radius * 0.55, radius * 0.55, 0.22);
        for (int index = 0; index < 18; ++index) {
            double angle = random.m_188500_() * Math.PI * 2.0;
            double radial = Math.sqrt(random.m_188500_()) * radius;
            Vec3 position = frontCenter.m_82549_(right.m_82490_(Math.cos(angle) * radial)).m_82549_(up.m_82490_(Math.sin(angle) * radial));
            Vec3 velocity = direction.m_82490_(0.75 + random.m_188500_() * 0.65);
            level.m_8767_((ParticleOptions)((SimpleParticleType)GoetyMasteryOfMagicModParticleTypes.MIDAS_ALCHEMY_SMALL.get()), position.f_82479_, position.f_82480_, position.f_82481_, 0, velocity.f_82479_, velocity.f_82480_, velocity.f_82481_, 1.0);
        }
    }

    private static double radiusAt(double distance) {
        return Math.max(1.0, 1.0 + (Math.max(1.0, distance) - 1.0) * 9.0 / 39.0);
    }

    private static float damageAt(double distance) {
        if (distance <= 10.0) {
            return 300.0f;
        }
        double progress = Mth.m_14008_((double)((distance - 10.0) / 50.0), (double)0.0, (double)1.0);
        return (float)Mth.m_14139_((double)progress, (double)300.0, (double)50.0);
    }

    private static Vec3 randomUnitVector(RandomSource random) {
        Vec3 vector = new Vec3(random.m_188583_(), random.m_188583_(), random.m_188583_());
        return vector.m_82556_() < 1.0E-6 ? new Vec3(0.0, 1.0, 0.0) : vector.m_82541_();
    }
}

