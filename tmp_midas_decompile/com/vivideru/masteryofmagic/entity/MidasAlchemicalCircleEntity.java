/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.entities.ISpellEntity
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.SimpleParticleType
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.MoverType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.network.NetworkHooks
 *  net.minecraftforge.network.PlayMessages$SpawnEntity
 */
package com.vivideru.masteryofmagic.entity;

import com.Polarice3.Goety.api.entities.ISpellEntity;
import com.vivideru.masteryofmagic.entity.PhilosopherKingMidasEntity;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModBlocks;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModEntities;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModParticleTypes;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

public final class MidasAlchemicalCircleEntity
extends Entity
implements ISpellEntity {
    public static final int LIFE_TICKS = 200;
    public static final double EXPLOSION_RADIUS = 5.0;
    private static final EntityDataAccessor<Integer> OWNER_ID = MidasAlchemicalCircleEntity.data(EntityDataSerializers.f_135028_);
    private static final EntityDataAccessor<Float> START_ANGLE = MidasAlchemicalCircleEntity.data(EntityDataSerializers.f_135029_);
    private static final EntityDataAccessor<Float> BASE_RADIUS = MidasAlchemicalCircleEntity.data(EntityDataSerializers.f_135029_);
    private static final EntityDataAccessor<Float> INCLINATION = MidasAlchemicalCircleEntity.data(EntityDataSerializers.f_135029_);
    private static final EntityDataAccessor<Float> ASCENDING_NODE = MidasAlchemicalCircleEntity.data(EntityDataSerializers.f_135029_);
    private static final EntityDataAccessor<Float> ORBIT_SPEED = MidasAlchemicalCircleEntity.data(EntityDataSerializers.f_135029_);
    private static final EntityDataAccessor<Float> RADIAL_PHASE = MidasAlchemicalCircleEntity.data(EntityDataSerializers.f_135029_);
    private static final EntityDataAccessor<Integer> TEXTURE_A = MidasAlchemicalCircleEntity.data(EntityDataSerializers.f_135028_);
    private static final EntityDataAccessor<Integer> TEXTURE_B = MidasAlchemicalCircleEntity.data(EntityDataSerializers.f_135028_);
    private static final EntityDataAccessor<Boolean> LAUNCHED = MidasAlchemicalCircleEntity.data(EntityDataSerializers.f_135035_);

    private static <T> EntityDataAccessor<T> data(EntityDataSerializer<T> serializer) {
        return SynchedEntityData.m_135353_(MidasAlchemicalCircleEntity.class, serializer);
    }

    public MidasAlchemicalCircleEntity(PlayMessages.SpawnEntity packet, Level level) {
        this((EntityType)GoetyMasteryOfMagicModEntities.MIDAS_ALCHEMICAL_CIRCLE.get(), level);
    }

    public MidasAlchemicalCircleEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.f_19794_ = true;
        this.m_20242_(true);
    }

    protected void m_8097_() {
        this.f_19804_.m_135372_(OWNER_ID, (Object)0);
        this.f_19804_.m_135372_(START_ANGLE, (Object)Float.valueOf(0.0f));
        this.f_19804_.m_135372_(BASE_RADIUS, (Object)Float.valueOf(16.0f));
        this.f_19804_.m_135372_(INCLINATION, (Object)Float.valueOf(0.0f));
        this.f_19804_.m_135372_(ASCENDING_NODE, (Object)Float.valueOf(0.0f));
        this.f_19804_.m_135372_(ORBIT_SPEED, (Object)Float.valueOf(0.012f));
        this.f_19804_.m_135372_(RADIAL_PHASE, (Object)Float.valueOf(0.0f));
        this.f_19804_.m_135372_(TEXTURE_A, (Object)0);
        this.f_19804_.m_135372_(TEXTURE_B, (Object)1);
        this.f_19804_.m_135372_(LAUNCHED, (Object)false);
    }

    public void configure(PhilosopherKingMidasEntity owner, float startAngle, float baseRadius, float inclination, float ascendingNode, float orbitSpeed, float radialPhase, int textureA, int textureB) {
        this.f_19804_.m_135381_(OWNER_ID, (Object)owner.m_19879_());
        this.f_19804_.m_135381_(START_ANGLE, (Object)Float.valueOf(startAngle));
        this.f_19804_.m_135381_(BASE_RADIUS, (Object)Float.valueOf(baseRadius));
        this.f_19804_.m_135381_(INCLINATION, (Object)Float.valueOf(inclination));
        this.f_19804_.m_135381_(ASCENDING_NODE, (Object)Float.valueOf(ascendingNode));
        this.f_19804_.m_135381_(ORBIT_SPEED, (Object)Float.valueOf(orbitSpeed));
        this.f_19804_.m_135381_(RADIAL_PHASE, (Object)Float.valueOf(radialPhase));
        this.f_19804_.m_135381_(TEXTURE_A, (Object)Mth.m_14045_((int)textureA, (int)0, (int)11));
        this.f_19804_.m_135381_(TEXTURE_B, (Object)Mth.m_14045_((int)textureB, (int)0, (int)11));
        this.f_19804_.m_135381_(LAUNCHED, (Object)false);
        this.updateOrbit(owner, 0.0f);
    }

    public void configureLaunched(PhilosopherKingMidasEntity owner, Vec3 position, Vec3 direction, double speed) {
        this.f_19804_.m_135381_(OWNER_ID, (Object)owner.m_19879_());
        this.f_19804_.m_135381_(LAUNCHED, (Object)true);
        this.m_146884_(position);
        this.m_20256_(direction.m_82541_().m_82490_(speed));
    }

    public void m_8119_() {
        Level level;
        PhilosopherKingMidasEntity owner;
        super.m_8119_();
        Entity rawOwner = this.m_9236_().m_6815_(((Integer)this.f_19804_.m_135370_(OWNER_ID)).intValue());
        if (!(rawOwner instanceof PhilosopherKingMidasEntity) || (owner = (PhilosopherKingMidasEntity)rawOwner).m_213877_()) {
            if (!this.m_9236_().f_46443_) {
                this.m_146870_();
            }
            return;
        }
        boolean blockedByBarrier = false;
        if (((Boolean)this.f_19804_.m_135370_(LAUNCHED)).booleanValue()) {
            blockedByBarrier = this.findBarrierAlongMovement();
            if (!blockedByBarrier) {
                this.m_6478_(MoverType.SELF, this.m_20184_());
            }
        } else {
            this.updateOrbit(owner, 0.0f);
        }
        if ((level = this.m_9236_()) instanceof ServerLevel) {
            ServerLevel server = (ServerLevel)level;
            if (blockedByBarrier) {
                this.detonate(server, owner, true);
                return;
            }
            this.pullAndBurnCreatures(server, owner);
            this.spawnChargingParticles(server, owner);
            if (this.f_19797_ >= 200) {
                this.detonate(server, owner, false);
            }
        }
    }

    private boolean findBarrierAlongMovement() {
        Vec3 movement = this.m_20184_();
        double length = movement.m_82553_();
        if (length < 1.0E-6) {
            return false;
        }
        int steps = Math.max(1, Mth.m_14165_((double)(length / 0.2)));
        for (int step = 1; step <= steps; ++step) {
            Vec3 sample = this.m_20182_().m_82549_(movement.m_82490_((double)step / (double)steps));
            BlockState state = this.m_9236_().m_8055_(BlockPos.m_274446_((Position)sample));
            if (!MidasAlchemicalCircleEntity.isMagicalBarrier(state)) continue;
            this.m_146884_(sample.m_82546_(movement.m_82541_().m_82490_(0.16)));
            return true;
        }
        return false;
    }

    private void pullAndBurnCreatures(ServerLevel server, PhilosopherKingMidasEntity owner) {
        for (LivingEntity target2 : server.m_6443_(LivingEntity.class, this.m_20191_().m_82400_(8.0), target -> MidasAlchemicalCircleEntity.canAffect(target, owner))) {
            Vec3 toSphere = this.m_20182_().m_82546_(target2.m_20191_().m_82399_());
            double distance = toSphere.m_82553_();
            if (distance > 0.05 && distance <= 8.0) {
                double strength = 0.035 + (1.0 - distance / 8.0) * 0.105;
                target2.m_20256_(target2.m_20184_().m_82490_(0.88).m_82549_(toSphere.m_82541_().m_82490_(strength)));
                target2.f_19864_ = true;
            }
            if (!(distance <= 1.25)) continue;
            target2.f_19802_ = 0;
            target2.m_6469_(server.m_269111_().m_269425_(), 5.0f);
        }
    }

    private static boolean canAffect(LivingEntity target, PhilosopherKingMidasEntity owner) {
        Player player;
        return target != owner && !(target instanceof PhilosopherKingMidasEntity) && target.m_6084_() && (!(target instanceof Player) || !(player = (Player)target).m_7500_() && !player.m_5833_());
    }

    private void updateOrbit(PhilosopherKingMidasEntity owner, float partialTick) {
        double time = (float)this.f_19797_ + partialTick;
        double angle = (double)((Float)this.f_19804_.m_135370_(START_ANGLE)).floatValue() + time * (double)((Float)this.f_19804_.m_135370_(ORBIT_SPEED)).floatValue();
        double radius = (double)((Float)this.f_19804_.m_135370_(BASE_RADIUS)).floatValue() + 0.48 * Math.sin(time * 0.031 + (double)((Float)this.f_19804_.m_135370_(RADIAL_PHASE)).floatValue());
        double inclination = ((Float)this.f_19804_.m_135370_(INCLINATION)).floatValue();
        double node = ((Float)this.f_19804_.m_135370_(ASCENDING_NODE)).floatValue();
        Vec3 axisU = new Vec3(Math.cos(node), 0.0, Math.sin(node));
        Vec3 axisV = new Vec3(-Math.sin(node) * Math.sin(inclination), Math.cos(inclination), Math.cos(node) * Math.sin(inclination));
        Vec3 offset = axisU.m_82490_(Math.cos(angle) * radius).m_82549_(axisV.m_82490_(Math.sin(angle) * radius));
        Vec3 center = owner.m_20182_().m_82520_(0.0, (double)owner.m_20206_() * 0.55, 0.0);
        this.m_146884_(center.m_82549_(offset));
    }

    private void spawnChargingParticles(ServerLevel server, PhilosopherKingMidasEntity owner) {
        int count = 1 + this.f_19797_ / 32;
        server.m_8767_((ParticleOptions)((SimpleParticleType)GoetyMasteryOfMagicModParticleTypes.MIDAS_ALCHEMY_SMALL.get()), this.m_20185_(), this.m_20186_(), this.m_20189_(), count, 0.65, 0.65, 0.65, 0.035);
        if (this.f_19797_ % 8 == 0) {
            Vec3 toward = owner.m_20191_().m_82399_().m_82546_(this.m_20182_()).m_82541_();
            server.m_8767_((ParticleOptions)((SimpleParticleType)GoetyMasteryOfMagicModParticleTypes.MIDAS_ALCHEMY.get()), this.m_20185_(), this.m_20186_(), this.m_20189_(), 0, toward.f_82479_ * 0.1, toward.f_82480_ * 0.1, toward.f_82481_ * 0.1, 1.0);
        }
    }

    private void detonate(ServerLevel server, PhilosopherKingMidasEntity owner, boolean stoppedByBarrier) {
        server.m_8767_((ParticleOptions)((SimpleParticleType)GoetyMasteryOfMagicModParticleTypes.MIDAS_ALCHEMY.get()), this.m_20185_(), this.m_20186_(), this.m_20189_(), 90, 2.5, 2.5, 2.5, 0.28);
        server.m_8767_((ParticleOptions)((SimpleParticleType)GoetyMasteryOfMagicModParticleTypes.MIDAS_ALCHEMY_SMALL.get()), this.m_20185_(), this.m_20186_(), this.m_20189_(), 150, 3.5, 3.5, 3.5, 0.34);
        server.m_5594_(null, this.m_20183_(), (SoundEvent)GoetyMasteryOfMagicModSounds.MIDAS_TRANSMUTE.get(), SoundSource.HOSTILE, 3.0f, 1.05f);
        server.m_5594_(null, this.m_20183_(), SoundEvents.f_11913_, SoundSource.HOSTILE, 2.8f, 1.18f);
        AABB damageArea = this.m_20191_().m_82400_(5.0);
        for (LivingEntity target2 : server.m_6443_(LivingEntity.class, damageArea, target -> MidasAlchemicalCircleEntity.canAffect(target, owner))) {
            double distance = target2.m_20191_().m_82399_().m_82554_(this.m_20182_());
            if (!(distance <= 5.0) || stoppedByBarrier && this.hasBarrierBetween(target2)) continue;
            float damage = (float)(10.0 + 40.0 * (1.0 - distance / 5.0));
            target2.m_6469_(server.m_269111_().m_269425_(), Mth.m_14036_((float)damage, (float)10.0f, (float)50.0f));
        }
        BlockPos center = this.m_20183_();
        int radius = (int)Math.ceil(5.0);
        for (BlockPos position : BlockPos.m_121940_((BlockPos)center.m_7918_(-radius, -radius, -radius), (BlockPos)center.m_7918_(radius, radius, radius))) {
            BlockState state;
            if (Vec3.m_82512_((Vec3i)position).m_82557_(this.m_20182_()) > 25.0 || (state = server.m_8055_(position)).m_60795_() || state.m_60713_(Blocks.f_50375_) || state.m_60713_((Block)GoetyMasteryOfMagicModBlocks.SOUL_BARRIER_BLOCK.get())) continue;
            server.m_46953_(position, false, (Entity)owner);
        }
        this.m_146870_();
    }

    private boolean hasBarrierBetween(LivingEntity target) {
        Vec3 start = this.m_20182_();
        Vec3 end = target.m_20191_().m_82399_();
        Vec3 path = end.m_82546_(start);
        int steps = Math.max(1, Mth.m_14165_((double)(path.m_82553_() / 0.2)));
        for (int i = 1; i < steps; ++i) {
            if (!MidasAlchemicalCircleEntity.isMagicalBarrier(this.m_9236_().m_8055_(BlockPos.m_274446_((Position)start.m_82549_(path.m_82490_((double)i / (double)steps)))))) continue;
            return true;
        }
        return false;
    }

    private static boolean isMagicalBarrier(BlockState state) {
        return state.m_60713_(Blocks.f_50375_) || state.m_60713_((Block)GoetyMasteryOfMagicModBlocks.SOUL_BARRIER_BLOCK.get());
    }

    public float getSpinDegrees(float partialTick) {
        return ((float)this.f_19797_ + partialTick) * 7.5f + ((Float)this.f_19804_.m_135370_(START_ANGLE)).floatValue() * 57.295776f;
    }

    public int getTextureA() {
        return (Integer)this.f_19804_.m_135370_(TEXTURE_A);
    }

    public int getTextureB() {
        return (Integer)this.f_19804_.m_135370_(TEXTURE_B);
    }

    public float getVerticalYaw() {
        return ((Float)this.f_19804_.m_135370_(ASCENDING_NODE)).floatValue() * 57.295776f;
    }

    protected void m_7378_(CompoundTag tag) {
    }

    protected void m_7380_(CompoundTag tag) {
    }

    public boolean m_6087_() {
        return false;
    }

    public boolean m_6097_() {
        return false;
    }

    public boolean m_6783_(double distanceSquared) {
        return distanceSquared <= 4096.0;
    }

    public void m_6453_(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean teleport) {
        if (this.m_9236_().f_46443_ && !((Boolean)this.f_19804_.m_135370_(LAUNCHED)).booleanValue()) {
            return;
        }
        super.m_6453_(x, y, z, yaw, pitch, 3, teleport);
    }

    public Packet<ClientGamePacketListener> m_5654_() {
        return NetworkHooks.getEntitySpawningPacket((Entity)this);
    }
}

