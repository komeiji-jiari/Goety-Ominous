package com.qiuyue.goetyominous.common.entities.ally.lm.projectile;

import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import net.miauczel.legendary_monsters.Particle.custom.Circle;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class LightningBoltEntity extends Entity {
    private int warmupDelayTicks;
    private boolean sentSpikeEvent;
    private int lifeTicks = 35;
    private boolean clientSideAttackStarted;
    private LivingEntity caster;
    private UUID casterUuid;
    private Entity caster2;
    private UUID casterUuid2;

    private static final EntityDataAccessor<Float> LIFE =
            SynchedEntityData.defineId(LightningBoltEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DAMAGE =
            SynchedEntityData.defineId(LightningBoltEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Optional<UUID>> CASTER2_UUID =
            SynchedEntityData.defineId(LightningBoltEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean> ATTACK =
            SynchedEntityData.defineId(LightningBoltEntity.class, EntityDataSerializers.BOOLEAN);

    public float activateProgress;
    public float prevactivateProgress;
    public float heightAB;
    public boolean ParticleAppeared = false;

    public LightningBoltEntity(EntityType<? extends LightningBoltEntity> type, Level level) {
        super(type, level);
    }

    public LightningBoltEntity(Level level, double x, double y, double z, float yRot, int warmupDelay, LivingEntity caster,
                               int life, float damage) {
        this(LmEntityRegistry.LIGHTNING_STRIKE.get(), level);
        this.warmupDelayTicks = warmupDelay;
        this.setCaster(caster);
        this.setCaster2(this.caster2);
        this.setDamage(damage);
        this.lifeTicks = life;
        this.setYRot(yRot * 57.295776F);
        this.setPos(x, y, z);
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    public float getLife() {
        return this.entityData.get(LIFE);
    }

    public void setLifeTicks(float lifeTicks) {
        this.entityData.set(LIFE, lifeTicks);
    }

    public void setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(LIFE, 0.0F);
        this.entityData.define(DAMAGE, 0.0F);
        this.entityData.define(ATTACK, false);
        this.entityData.define(CASTER2_UUID, Optional.empty());
    }

    public void setCaster(@Nullable LivingEntity caster) {
        this.caster = caster;
        this.casterUuid = caster == null ? null : caster.getUUID();
    }

    @Nullable
    public LivingEntity getCaster() {
        if (this.caster == null && this.casterUuid != null && this.level() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(this.casterUuid);
            if (entity instanceof LivingEntity living) {
                this.caster = living;
            }
        }
        return this.caster;
    }

    public void setCaster2(@Nullable Entity caster2) {
        this.caster2 = caster2;
        this.casterUuid2 = caster2 == null ? null : caster2.getUUID();
        this.entityData.set(CASTER2_UUID, Optional.ofNullable(this.casterUuid2));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.warmupDelayTicks = compound.getInt("Warmup");
        if (compound.hasUUID("Owner")) {
            this.casterUuid = compound.getUUID("Owner");
        }
        if (compound.hasUUID("Caster2")) {
            this.casterUuid2 = compound.getUUID("Caster2");
            this.entityData.set(CASTER2_UUID, Optional.of(this.casterUuid2));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        if (this.casterUuid2 != null) {
            compound.putUUID("Caster2", this.casterUuid2);
        }
        compound.putInt("Warmup", this.warmupDelayTicks);
        if (this.casterUuid != null) {
            compound.putUUID("Owner", this.casterUuid);
        }
    }

    public float GetH() {
        return this.heightAB;
    }

    public boolean GetHH() {
        return this.heightAB > 0.0F;
    }

    public void setH(float h) {
        this.heightAB = h;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (key.equals(CASTER2_UUID)) {
            Optional<UUID> optional = this.entityData.get(CASTER2_UUID);
            if (optional.isPresent() && this.level() instanceof ServerLevel serverLevel) {
                Entity entity = serverLevel.getEntity(optional.get());
                if (entity != null) {
                    this.caster2 = entity;
                }
            }
        }
    }

    public int getLifeTicks() {
        return this.lifeTicks;
    }

    @Override
    public void tick() {
        super.tick();
        this.prevactivateProgress = this.activateProgress;
        if (this.isActivate() && this.activateProgress > 0.0F) {
            this.activateProgress -= 1.0F;
        }
        if (this.level().isClientSide && this.clientSideAttackStarted && !this.ParticleAppeared) {
            this.level().addParticle(new Circle.RingData(0.0F, 1.5707964F, 30, 0.9F, 0.89F, 1.0F, 1.0F, 15.0F, false,
                    Circle.EnumRingBehavior.GROW), this.getX(), this.getY() + 0.1, this.getZ(), 0.0, 0.0, 0.0);
            this.ParticleAppeared = true;
        }
        if (this.level().isClientSide) {
            if (this.clientSideAttackStarted) {
                --this.lifeTicks;
                if (!this.isActivate() && this.activateProgress < 10.0F) {
                    this.activateProgress += 1.0F;
                }
                if (this.lifeTicks == 37) {
                    for (int i = 0; i < 80; ++i) {
                        BlockState block = this.level().getBlockState(this.blockPosition().below());
                        double d0 = this.getX() + (this.random.nextDouble() * 2.0 - 1.0) * (double) this.getBbWidth() * 0.5;
                        double d1 = this.getY() + 0.03;
                        double d2 = this.getZ() + (this.random.nextDouble() * 2.0 - 1.0) * (double) this.getBbWidth() * 0.5;
                        double d3 = this.random.nextGaussian() * 0.07;
                        double d4 = this.random.nextGaussian() * 0.07;
                        double d5 = this.random.nextGaussian() * 0.07;
                        this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, block), d0, d1, d2, d3, d4, d5);
                    }
                }
                if (this.lifeTicks == 6) {
                    this.setActivate(true);
                }
            }
        } else if (--this.warmupDelayTicks < 0) {
            if (this.warmupDelayTicks == -5 && this.isActivate()) {
                this.setActivate(false);
            }
            if (this.warmupDelayTicks < -5 && this.warmupDelayTicks > -30) {
                for (LivingEntity livingentity : this.level().getEntitiesOfClass(LivingEntity.class,
                        this.getBoundingBox().inflate(0.2, 6.0, 0.2))) {
                    this.damage(livingentity);
                }
            }
            if (!this.sentSpikeEvent) {
                this.level().broadcastEntityEvent(this, (byte) 4);
                this.sentSpikeEvent = true;
            }
            if (--this.lifeTicks < 0) {
                this.discard();
            }
        }
    }

    public boolean isActivate() {
        return this.entityData.get(ATTACK);
    }

    public void setActivate(boolean activate) {
        this.entityData.set(ATTACK, activate);
    }

    private void damage(LivingEntity impactEntity) {
        LivingEntity caster = this.getCaster();
        if (!impactEntity.isAlive() || impactEntity.isInvulnerable() || impactEntity == caster || this.tickCount % 5 != 0) {
            return;
        }
        if (caster != null && MobUtil.areAllies(caster, impactEntity)) {
            return;
        }
        if (impactEntity instanceof TamableAnimal animal && animal.getOwner() == caster) {
            return;
        }
        DamageSource damageSource = new DamageSource(
                this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.INDIRECT_MAGIC), caster);
        impactEntity.hurt(damageSource, this.getDamage());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        super.handleEntityEvent(id);
        if (id == 4) {
            this.clientSideAttackStarted = true;
        }
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public float getAnimationProgress(float partialTicks) {
        if (!this.clientSideAttackStarted) {
            return 0.0F;
        }
        int i = this.lifeTicks - 2;
        return i <= 0 ? 1.0F : 1.0F - ((float) i - partialTicks) / 20.0F;
    }
}
