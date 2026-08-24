/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.entities.ISpellEntity
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.MoverType
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier$Builder
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.monster.Monster
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.network.NetworkHooks
 *  net.minecraftforge.network.PlayMessages$SpawnEntity
 *  org.jetbrains.annotations.Nullable
 */
package com.vivideru.masteryofmagic.entity;

import com.Polarice3.Goety.api.entities.ISpellEntity;
import com.vivideru.masteryofmagic.entity.PhilosopherBeamEntity;
import com.vivideru.masteryofmagic.entity.PhilosopherKingMidasEntity;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModEntities;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModParticleTypes;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModSounds;
import java.util.UUID;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.Nullable;

public class PhilosopherSphereEntity
extends Monster
implements ISpellEntity {
    private static final float TECHNICAL_HEALTH = 1.0f;
    public static final int BEAM_CHARGE_TICKS = 60;
    public static final int BEAM_DURATION_TICKS = 80;
    private static final int REQUIRED_VISIBLE_TICKS = 8;
    private static final int MAX_SEARCH_TICKS = 240;
    private static final int CHARGE_SOUND_INTERVAL_TICKS = 10;
    private static final double ORBIT_RADIUS = 8.0;
    private static final double MAX_ORBIT_SPEED = 0.72;
    private static final EntityDataAccessor<Integer> MIDAS_ID = SynchedEntityData.m_135353_(PhilosopherSphereEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
    private static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.m_135353_(PhilosopherSphereEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
    private static final EntityDataAccessor<Integer> CHARGE_START_TICK = SynchedEntityData.m_135353_(PhilosopherSphereEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
    private UUID midasUuid;
    private UUID targetUuid;
    private UUID activeBeamUuid;
    private double orbitAngle;
    private int orbitDirection = 1;
    private int beamTicksRemaining;
    private int chargeTicksRemaining;
    private int visibleTargetTicks;
    private int searchTicks;
    private boolean beamFired;
    private float fixedBeamYaw;
    private float fixedBeamPitch;

    public PhilosopherSphereEntity(PlayMessages.SpawnEntity packet, Level level) {
        this((EntityType<? extends PhilosopherSphereEntity>)((EntityType)GoetyMasteryOfMagicModEntities.PHILOSOPHER_SPHERE.get()), level);
    }

    public PhilosopherSphereEntity(EntityType<? extends PhilosopherSphereEntity> type, Level level) {
        super(type, level);
        this.m_20242_(true);
        this.f_19794_ = true;
        this.m_21530_();
        this.f_21364_ = 0;
    }

    protected void m_8097_() {
        super.m_8097_();
        this.f_19804_.m_135372_(MIDAS_ID, (Object)-1);
        this.f_19804_.m_135372_(TARGET_ID, (Object)-1);
        this.f_19804_.m_135372_(CHARGE_START_TICK, (Object)-1);
    }

    protected void m_8099_() {
    }

    public void setMidasOwner(@Nullable PhilosopherKingMidasEntity midas) {
        this.f_19804_.m_135381_(MIDAS_ID, (Object)(midas == null ? -1 : midas.m_19879_()));
        this.midasUuid = midas == null ? null : midas.m_20148_();
    }

    @Nullable
    public PhilosopherKingMidasEntity getMidasOwner() {
        ServerLevel level;
        Entity byUuid;
        Level level2;
        Entity byId = this.m_9236_().m_6815_(((Integer)this.f_19804_.m_135370_(MIDAS_ID)).intValue());
        if (byId instanceof PhilosopherKingMidasEntity) {
            PhilosopherKingMidasEntity midas = (PhilosopherKingMidasEntity)byId;
            return midas;
        }
        if (!this.m_9236_().f_46443_ && this.midasUuid != null && (level2 = this.m_9236_()) instanceof ServerLevel && (byUuid = (level = (ServerLevel)level2).m_8791_(this.midasUuid)) instanceof PhilosopherKingMidasEntity) {
            PhilosopherKingMidasEntity midas = (PhilosopherKingMidasEntity)byUuid;
            this.f_19804_.m_135381_(MIDAS_ID, (Object)midas.m_19879_());
            return midas;
        }
        return null;
    }

    public boolean isOwnedBy(Entity entity) {
        return entity != null && (entity.m_20148_().equals(this.midasUuid) || ((Integer)this.f_19804_.m_135370_(MIDAS_ID)).intValue() == entity.m_19879_());
    }

    public void setTargetEntity(@Nullable LivingEntity target) {
        this.f_19804_.m_135381_(TARGET_ID, (Object)(target == null ? -1 : target.m_19879_()));
        this.targetUuid = target == null ? null : target.m_20148_();
    }

    @Nullable
    public LivingEntity getTargetEntity() {
        ServerLevel level;
        Entity byUuid;
        Level level2;
        Entity byId = this.m_9236_().m_6815_(((Integer)this.f_19804_.m_135370_(TARGET_ID)).intValue());
        if (byId instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)byId;
            return living;
        }
        if (!this.m_9236_().f_46443_ && this.targetUuid != null && (level2 = this.m_9236_()) instanceof ServerLevel && (byUuid = (level = (ServerLevel)level2).m_8791_(this.targetUuid)) instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)byUuid;
            this.f_19804_.m_135381_(TARGET_ID, (Object)living.m_19879_());
            return living;
        }
        return null;
    }

    public void initializeOrbit(double angle, int direction) {
        this.orbitAngle = angle;
        this.orbitDirection = direction < 0 ? -1 : 1;
    }

    public boolean isChargingBeam() {
        return (Integer)this.f_19804_.m_135370_(CHARGE_START_TICK) >= 0;
    }

    public float getBeamChargeProgress(float partialTick) {
        int startTick = (Integer)this.f_19804_.m_135370_(CHARGE_START_TICK);
        if (startTick < 0) {
            return 0.0f;
        }
        return Mth.m_14036_((float)(((float)this.f_19797_ + partialTick - (float)startTick) / 60.0f), (float)0.0f, (float)1.0f);
    }

    public void m_8119_() {
        LivingEntity target;
        if (!this.m_9236_().f_46443_) {
            this.m_20256_(Vec3.f_82478_);
        }
        super.m_8119_();
        this.m_20242_(true);
        this.f_19794_ = true;
        if (this.m_9236_().f_46443_) {
            this.spawnClientParticles();
            return;
        }
        Level level = this.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel level2 = (ServerLevel)level;
        PhilosopherKingMidasEntity midas = this.getMidasOwner();
        if (midas == null || !midas.m_6084_() || midas.m_213877_()) {
            this.discardActiveBeam(level2);
            this.m_146870_();
            return;
        }
        PhilosopherBeamEntity activeBeam = this.resolveActiveBeam(level2);
        if (activeBeam != null) {
            this.m_20256_(Vec3.f_82478_);
            this.tickActiveBeam(level2, activeBeam);
            return;
        }
        LivingEntity ownerTarget = midas.m_5448_();
        if (ownerTarget != null && ownerTarget.m_6084_()) {
            this.setTargetEntity(ownerTarget);
        }
        if ((target = this.getTargetEntity()) == null || !target.m_6084_() || target.m_213877_()) {
            this.setTargetEntity(null);
            this.discardActiveBeam(level2);
            this.m_146870_();
            return;
        }
        if (this.isChargingBeam()) {
            this.m_20256_(Vec3.f_82478_);
            this.lookAtTarget(target);
            this.tickBeamCharge(level2);
            return;
        }
        if (this.beamFired || ++this.searchTicks > 240) {
            this.m_146870_();
            return;
        }
        if (this.f_19797_ % 10 == 0 && !this.hasClearSight(level2, target)) {
            this.chooseVisibleOrbitDirection(level2, target);
        }
        this.orbitAround(target, true);
        this.lookAtTarget(target);
        this.visibleTargetTicks = this.hasClearSight(level2, target) ? ++this.visibleTargetTicks : 0;
        if (this.visibleTargetTicks >= 8) {
            this.beginBeamCharge(level2);
        }
    }

    private void orbitAround(LivingEntity center, boolean combatOrbit) {
        this.orbitAngle += (double)this.orbitDirection * (combatOrbit ? 0.042 : 0.025);
        Vec3 desired = this.orbitPosition(center, this.orbitAngle, combatOrbit);
        Vec3 motion = desired.m_82546_(this.m_20182_()).m_82490_(0.18);
        if (motion.m_82553_() > 0.72) {
            motion = motion.m_82541_().m_82490_(0.72);
        }
        this.m_20256_(motion);
        this.m_6478_(MoverType.SELF, motion);
    }

    private Vec3 orbitPosition(LivingEntity center, double angle, boolean combatOrbit) {
        double radius = combatOrbit ? 8.0 : 4.0;
        double bob = Math.sin(angle * 1.7) * 1.35;
        return new Vec3(center.m_20185_() + Math.cos(angle) * radius, center.m_20186_() + (double)center.m_20206_() + (combatOrbit ? 3.5 : 2.0) + bob, center.m_20189_() + Math.sin(angle) * radius);
    }

    private void chooseVisibleOrbitDirection(ServerLevel level, LivingEntity target) {
        int counterClockwiseSteps;
        int clockwiseSteps = this.stepsToVisiblePosition(level, target, 1);
        if (clockwiseSteps < (counterClockwiseSteps = this.stepsToVisiblePosition(level, target, -1))) {
            this.orbitDirection = 1;
        } else if (counterClockwiseSteps < clockwiseSteps) {
            this.orbitDirection = -1;
        }
    }

    private int stepsToVisiblePosition(ServerLevel level, LivingEntity target, int direction) {
        Vec3 targetCenter = target.m_20191_().m_82399_();
        for (int step = 1; step <= 24; ++step) {
            BlockHitResult sight;
            double candidateAngle = this.orbitAngle + (double)(direction * step) * 0.16;
            Vec3 candidate = this.orbitPosition(target, candidateAngle, true);
            Vec3 displacement = candidate.m_82546_(this.m_20182_());
            AABB candidateBounds = this.m_20191_().m_82383_(displacement);
            if (!level.m_45756_((Entity)this, candidateBounds) || (sight = level.m_45547_(new ClipContext(candidate, targetCenter, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, (Entity)this))).m_6662_() != HitResult.Type.MISS) continue;
            return step;
        }
        return Integer.MAX_VALUE;
    }

    private boolean hasClearSight(ServerLevel level, LivingEntity target) {
        BlockHitResult sight = level.m_45547_(new ClipContext(this.m_146892_(), target.m_20191_().m_82399_(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, (Entity)this));
        return sight.m_6662_() == HitResult.Type.MISS;
    }

    private void lookAtTarget(LivingEntity target) {
        Vec3 direction = target.m_20191_().m_82399_().m_82546_(this.m_146892_());
        double horizontal = Math.sqrt(direction.f_82479_ * direction.f_82479_ + direction.f_82481_ * direction.f_82481_);
        float yaw = (float)(Mth.m_14136_((double)direction.f_82481_, (double)direction.f_82479_) * 57.2957763671875) - 90.0f;
        float pitch = (float)(-(Mth.m_14136_((double)direction.f_82480_, (double)horizontal) * 57.2957763671875));
        this.m_146922_(yaw);
        this.f_20883_ = yaw;
        this.f_20885_ = yaw;
        this.m_146926_(Mth.m_14036_((float)pitch, (float)-90.0f, (float)90.0f));
    }

    private void beginBeamCharge(ServerLevel level) {
        if (this.isChargingBeam() || this.beamFired) {
            return;
        }
        this.chargeTicksRemaining = 60;
        this.f_19804_.m_135381_(CHARGE_START_TICK, (Object)this.f_19797_);
        this.m_20256_(Vec3.f_82478_);
        this.playChargeSound(level, 0.0f);
    }

    private void tickBeamCharge(ServerLevel level) {
        int elapsed = 60 - this.chargeTicksRemaining;
        if (elapsed > 0 && elapsed % 10 == 0) {
            this.playChargeSound(level, (float)elapsed / 60.0f);
        }
        if (--this.chargeTicksRemaining <= 0) {
            this.chargeTicksRemaining = 0;
            this.f_19804_.m_135381_(CHARGE_START_TICK, (Object)-1);
            this.startBeam(level);
        }
    }

    private void playChargeSound(ServerLevel level, float progress) {
        level.m_6263_(null, this.m_20185_(), this.m_20186_() + (double)this.m_20206_() * 0.5, this.m_20189_(), (SoundEvent)GoetyMasteryOfMagicModSounds.MIDAS_TRANSMUTE.get(), SoundSource.HOSTILE, 2.4f + progress * 1.6f, 0.72f + progress * 0.52f);
    }

    private void startBeam(ServerLevel level) {
        this.fixedBeamYaw = this.m_146908_();
        this.fixedBeamPitch = this.m_146909_();
        PhilosopherBeamEntity beam = new PhilosopherBeamEntity((Level)level, (LivingEntity)this);
        beam.setItemBase(false);
        level.m_7967_((Entity)beam);
        this.activeBeamUuid = beam.m_20148_();
        this.beamTicksRemaining = 80;
        this.beamFired = true;
        this.m_20256_(Vec3.f_82478_);
    }

    private void tickActiveBeam(ServerLevel level, PhilosopherBeamEntity activeBeam) {
        this.m_146922_(this.fixedBeamYaw);
        this.f_20883_ = this.fixedBeamYaw;
        this.f_20885_ = this.fixedBeamYaw;
        this.m_146926_(this.fixedBeamPitch);
        if (--this.beamTicksRemaining <= 0) {
            activeBeam.m_146870_();
            this.activeBeamUuid = null;
            this.beamTicksRemaining = 0;
            this.m_146870_();
        }
    }

    @Nullable
    private PhilosopherBeamEntity resolveActiveBeam(ServerLevel level) {
        PhilosopherBeamEntity beam;
        if (this.activeBeamUuid == null) {
            return null;
        }
        Entity entity = level.m_8791_(this.activeBeamUuid);
        return entity instanceof PhilosopherBeamEntity && !(beam = (PhilosopherBeamEntity)entity).m_213877_() ? beam : null;
    }

    private void discardActiveBeam(ServerLevel level) {
        PhilosopherBeamEntity beam = this.resolveActiveBeam(level);
        if (beam != null) {
            beam.m_146870_();
        }
        this.activeBeamUuid = null;
        this.beamTicksRemaining = 0;
    }

    private void spawnClientParticles() {
        int index;
        float charge = this.getBeamChargeProgress(0.0f);
        if ((this.f_19797_ & 1) == 0) {
            int coreParticleCount = this.isChargingBeam() ? 2 : 1;
            for (index = 0; index < coreParticleCount; ++index) {
                double spread = 0.62;
                this.m_9236_().m_7106_((ParticleOptions)GoetyMasteryOfMagicModParticleTypes.MIDAS_ALCHEMY.get(), this.m_20185_() + (this.f_19796_.m_188500_() - 0.5) * spread, this.m_20186_() + (double)this.m_20206_() * 0.5 + (this.f_19796_.m_188500_() - 0.5) * spread, this.m_20189_() + (this.f_19796_.m_188500_() - 0.5) * spread, 0.0, 0.006, 0.0);
            }
        }
        if (this.isChargingBeam() && this.f_19797_ % 4 == 0) {
            Vec3 center = this.m_20182_().m_82520_(0.0, (double)this.m_20206_() * 0.5, 0.0);
            for (index = 0; index < 3; ++index) {
                double angle = (double)this.f_19797_ * 0.34 + (double)index * 2.0943951023931953;
                double radius = 1.8 - (double)charge * 0.75;
                Vec3 particlePosition = center.m_82520_(Math.cos(angle) * radius, Math.sin(angle * 1.7) * 0.65, Math.sin(angle) * radius);
                Vec3 inward = center.m_82546_(particlePosition).m_82541_().m_82490_(0.1 + (double)charge * 0.08);
                this.m_9236_().m_7106_((ParticleOptions)GoetyMasteryOfMagicModParticleTypes.MIDAS_ALCHEMY.get(), particlePosition.f_82479_, particlePosition.f_82480_, particlePosition.f_82481_, inward.f_82479_, inward.f_82480_, inward.f_82481_);
            }
        }
        if (this.f_19797_ % 3 == 0) {
            this.m_9236_().m_7106_((ParticleOptions)GoetyMasteryOfMagicModParticleTypes.MIDAS_ALCHEMY.get(), this.m_20185_(), this.m_20186_() + (double)this.m_20206_() * 0.5, this.m_20189_(), 0.0, 0.005, 0.0);
        }
    }

    public boolean m_6469_(DamageSource source, float amount) {
        return false;
    }

    public boolean m_6087_() {
        return false;
    }

    public boolean m_6097_() {
        return false;
    }

    public boolean m_142066_() {
        return false;
    }

    public boolean m_271807_() {
        return false;
    }

    public boolean m_6094_() {
        return false;
    }

    protected void m_7324_(Entity entity) {
    }

    public boolean m_142535_(float distance, float multiplier, DamageSource source) {
        return false;
    }

    public boolean m_6785_(double distanceToClosestPlayer) {
        return false;
    }

    public void m_142687_(Entity.RemovalReason reason) {
        Level level;
        if (!this.m_9236_().f_46443_ && (level = this.m_9236_()) instanceof ServerLevel) {
            ServerLevel level2 = (ServerLevel)level;
            this.discardActiveBeam(level2);
        }
        super.m_142687_(reason);
    }

    public void m_7380_(CompoundTag tag) {
        super.m_7380_(tag);
        if (this.midasUuid != null) {
            tag.m_128362_("MidasOwner", this.midasUuid);
        }
        if (this.targetUuid != null) {
            tag.m_128362_("SphereTarget", this.targetUuid);
        }
        if (this.activeBeamUuid != null) {
            tag.m_128362_("ActiveBeam", this.activeBeamUuid);
        }
        tag.m_128347_("OrbitAngle", this.orbitAngle);
        tag.m_128405_("OrbitDirection", this.orbitDirection);
        tag.m_128405_("BeamTicks", this.beamTicksRemaining);
        tag.m_128405_("ChargeTicks", this.chargeTicksRemaining);
        tag.m_128405_("VisibleTargetTicks", this.visibleTargetTicks);
        tag.m_128405_("SearchTicks", this.searchTicks);
        tag.m_128379_("BeamFired", this.beamFired);
    }

    public void m_7378_(CompoundTag tag) {
        super.m_7378_(tag);
        this.midasUuid = tag.m_128403_("MidasOwner") ? tag.m_128342_("MidasOwner") : null;
        this.targetUuid = tag.m_128403_("SphereTarget") ? tag.m_128342_("SphereTarget") : null;
        this.activeBeamUuid = tag.m_128403_("ActiveBeam") ? tag.m_128342_("ActiveBeam") : null;
        this.orbitAngle = tag.m_128459_("OrbitAngle");
        this.orbitDirection = tag.m_128451_("OrbitDirection") < 0 ? -1 : 1;
        this.beamTicksRemaining = Math.max(0, tag.m_128451_("BeamTicks"));
        this.chargeTicksRemaining = Mth.m_14045_((int)tag.m_128451_("ChargeTicks"), (int)0, (int)60);
        this.f_19804_.m_135381_(CHARGE_START_TICK, (Object)(this.chargeTicksRemaining > 0 ? this.f_19797_ - (60 - this.chargeTicksRemaining) : -1));
        this.visibleTargetTicks = Math.max(0, tag.m_128451_("VisibleTargetTicks"));
        this.searchTicks = Math.max(0, tag.m_128451_("SearchTicks"));
        this.beamFired = tag.m_128471_("BeamFired");
    }

    public Packet<ClientGamePacketListener> m_5654_() {
        return NetworkHooks.getEntitySpawningPacket((Entity)this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.m_21552_().m_22268_(Attributes.f_22276_, 1.0).m_22268_(Attributes.f_22278_, 1.0).m_22268_(Attributes.f_22277_, 1.0).m_22268_(Attributes.f_22279_, 0.0).m_22268_(Attributes.f_22280_, 0.0);
    }
}

