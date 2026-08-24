/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.client.particles.SparkleParticleOption
 *  com.Polarice3.Goety.common.entities.projectiles.RazorWind
 *  com.Polarice3.Goety.common.entities.projectiles.SlashProjectile
 *  net.minecraft.core.particles.DustParticleOptions
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
 *  net.minecraft.util.Mth
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityDimensions
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Pose
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.network.NetworkHooks
 *  net.minecraftforge.network.PlayMessages$SpawnEntity
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package com.vivideru.masteryofmagic.entity;

import com.Polarice3.Goety.client.particles.SparkleParticleOption;
import com.Polarice3.Goety.common.entities.projectiles.RazorWind;
import com.Polarice3.Goety.common.entities.projectiles.SlashProjectile;
import com.vivideru.masteryofmagic.entity.GoldenSwordProjectileEntity;
import com.vivideru.masteryofmagic.entity.PhilosopherBeamEntity;
import com.vivideru.masteryofmagic.entity.PhilosopherKingMidasEntity;
import com.vivideru.masteryofmagic.entity.PhilosopherSphereEntity;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModEntities;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModParticleTypes;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.particles.DustParticleOptions;
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
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class PhilosopherWindSlashEntity
extends RazorWind {
    public static final float RADIUS = 3.5f;
    public static final float BLADE_HALF_LENGTH = 14.0f;
    public static final float VISUAL_HALF_DEPTH = 1.75f;
    public static final double SPEED = 0.34;
    public static final int LIFETIME_TICKS = 240;
    private static final double HIT_LINE_HALF_THICKNESS = 0.22;
    private static final float FLAT_PHYSICAL_DAMAGE = 30.0f;
    private static final float MAX_HEALTH_DAMAGE_RATIO = 0.2f;
    private static final DustParticleOptions PHILOSOPHER_DUST = new DustParticleOptions(new Vector3f(1.0f, 0.08f, 1.0f), 1.4f);
    private static final SparkleParticleOption PHILOSOPHER_SPARKLE = new SparkleParticleOption(0.72f, 1.0f, 0.08f, 1.0f, 4);
    private static final SparkleParticleOption PHILOSOPHER_CORE_SPARKLE = new SparkleParticleOption(0.56f, 1.0f, 0.72f, 1.0f, 3);
    private static final EntityDataAccessor<Float> ROLL = SynchedEntityData.m_135353_(PhilosopherWindSlashEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
    private final Set<UUID> philosopherVictims = new HashSet<UUID>();
    private boolean suppressParentCollision;

    public PhilosopherWindSlashEntity(PlayMessages.SpawnEntity packet, Level level) {
        this((EntityType<? extends SlashProjectile>)((EntityType)GoetyMasteryOfMagicModEntities.PHILOSOPHER_WIND_SLASH.get()), level);
    }

    public PhilosopherWindSlashEntity(EntityType<? extends SlashProjectile> type, Level level) {
        super(type, level);
        this.configureSizeAndLifetime();
    }

    public PhilosopherWindSlashEntity(Level level, LivingEntity owner) {
        super((EntityType)GoetyMasteryOfMagicModEntities.PHILOSOPHER_WIND_SLASH.get(), level);
        this.m_5602_((Entity)owner);
        this.configureSizeAndLifetime();
    }

    private void configureSizeAndLifetime() {
        this.setMaxRadius(3.5f);
        this.setRadius(3.5f);
        this.setMaxLifeSpan(240);
        this.m_20242_(true);
    }

    protected void m_8097_() {
        super.m_8097_();
        this.f_19804_.m_135372_(ROLL, (Object)Float.valueOf(0.0f));
    }

    public void setRollDegrees(float roll) {
        this.f_19804_.m_135381_(ROLL, (Object)Float.valueOf(roll));
    }

    public float getRollDegrees() {
        return ((Float)this.f_19804_.m_135370_(ROLL)).floatValue();
    }

    public Vec3 getRenderDirection() {
        Vec3 movement = this.m_20184_();
        return movement.m_82556_() > 1.0E-7 ? movement.m_82541_() : new Vec3(0.0, 0.0, 1.0);
    }

    public Quaternionf getRenderOrientation() {
        Vec3 forward = this.getRenderDirection();
        double horizontal = Math.sqrt(forward.f_82479_ * forward.f_82479_ + forward.f_82481_ * forward.f_82481_);
        float yaw = (float)(Mth.m_14136_((double)forward.f_82481_, (double)forward.f_82479_) * 57.2957763671875) - 90.0f;
        float pitch = (float)(-(Mth.m_14136_((double)forward.f_82480_, (double)horizontal) * 57.2957763671875));
        return new Quaternionf().rotateY(-yaw * ((float)Math.PI / 180)).rotateX(pitch * ((float)Math.PI / 180)).rotateZ(this.getRollDegrees() * ((float)Math.PI / 180));
    }

    public Vec3 getBladeAxis() {
        Vector3f blade = new Vector3f(1.0f, 0.0f, 0.0f).rotate((Quaternionfc)this.getRenderOrientation());
        return new Vec3((double)blade.x, (double)blade.y, (double)blade.z).m_82541_();
    }

    public EntityDimensions m_6972_(Pose pose) {
        return EntityDimensions.m_20395_((float)0.45f, (float)0.18f);
    }

    public void m_8119_() {
        Vec3 previousPosition = this.m_20182_();
        Vec3 stableMovement = this.m_20184_();
        if (stableMovement.m_82556_() > 1.0E-8) {
            stableMovement = stableMovement.m_82541_().m_82490_(0.34);
            this.m_20256_(stableMovement);
        }
        this.suppressParentCollision = true;
        super.m_8119_();
        this.suppressParentCollision = false;
        if (!this.m_213877_() && stableMovement.m_82556_() > 1.0E-8) {
            this.m_146884_(previousPosition.m_82549_(stableMovement));
            this.m_20256_(stableMovement);
        }
        if (!this.m_9236_().f_46443_ && !this.m_213877_()) {
            this.damageEntitiesAlongBlade();
        }
    }

    private void damageEntitiesAlongBlade() {
        Vec3 blade = this.getBladeAxis();
        Vec3 start = this.m_20182_().m_82546_(blade.m_82490_(14.0));
        Vec3 end = this.m_20182_().m_82549_(blade.m_82490_(14.0));
        AABB search = new AABB(start, end).m_82400_(0.22);
        for (Entity entity : this.m_9236_().m_6249_((Entity)this, search, this::m_5603_)) {
            AABB targetBox;
            if (this.philosopherVictims.contains(entity.m_20148_()) || !(targetBox = entity.m_20191_().m_82400_(0.22)).m_82390_(start) && !targetBox.m_82390_(end) && !targetBox.m_82371_(start, end).isPresent()) continue;
            this.damageEntity(entity);
            this.philosopherVictims.add(entity.m_20148_());
        }
    }

    public DustParticleOptions getParticle() {
        return PHILOSOPHER_DUST;
    }

    public void spawnParticles() {
        ServerLevel serverLevel;
        block4: {
            block3: {
                Level level = this.m_9236_();
                if (!(level instanceof ServerLevel)) break block3;
                serverLevel = (ServerLevel)level;
                if (this.f_19797_ % 8 == 0) break block4;
            }
            return;
        }
        Vec3 blade = this.getBladeAxis();
        for (double offset = -14.0; offset <= 14.0; offset += 2.0) {
            Vec3 point = this.m_20182_().m_82549_(blade.m_82490_(offset));
            double velocityX = (this.f_19796_.m_188500_() - 0.5) * 0.08;
            double velocityY = (this.f_19796_.m_188500_() - 0.5) * 0.08;
            double velocityZ = (this.f_19796_.m_188500_() - 0.5) * 0.08;
            serverLevel.m_8767_((ParticleOptions)((SimpleParticleType)GoetyMasteryOfMagicModParticleTypes.MIDAS_ALCHEMY.get()), point.f_82479_, point.f_82480_, point.f_82481_, 0, velocityX, velocityY, velocityZ, 1.0);
        }
    }

    public void damageEntity(Entity entity) {
        if (!this.m_5603_(entity)) {
            return;
        }
        float damage = 30.0f;
        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)entity;
            damage += living.m_21233_() * 0.2f;
            living.f_19802_ = 0;
        }
        DamageSource source = entity.m_269291_().m_269425_();
        Entity entity2 = this.m_19749_();
        if (entity2 instanceof LivingEntity) {
            LivingEntity owner = (LivingEntity)entity2;
            source = entity.m_269291_().m_269104_((Entity)this, (Entity)owner);
        }
        entity.m_6469_(source, damage);
    }

    protected void m_8060_(BlockHitResult hitResult) {
    }

    protected boolean m_5603_(Entity entity) {
        Player player;
        if (this.suppressParentCollision || entity instanceof PhilosopherKingMidasEntity || entity instanceof PhilosopherSphereEntity || entity instanceof PhilosopherWindSlashEntity || entity instanceof GoldenSwordProjectileEntity || entity instanceof PhilosopherBeamEntity) {
            return false;
        }
        if (entity instanceof Player && ((player = (Player)entity).m_7500_() || player.m_5833_())) {
            return false;
        }
        return super.m_5603_(entity);
    }

    protected void m_7380_(CompoundTag tag) {
        super.m_7380_(tag);
        tag.m_128350_("PhilosopherRoll", this.getRollDegrees());
    }

    protected void m_7378_(CompoundTag tag) {
        super.m_7378_(tag);
        this.setRollDegrees(tag.m_128457_("PhilosopherRoll"));
        this.configureSizeAndLifetime();
    }

    public Packet<ClientGamePacketListener> m_5654_() {
        return NetworkHooks.getEntitySpawningPacket((Entity)this);
    }
}

