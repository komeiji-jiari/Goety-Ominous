package com.qiuyue.goetyominous.common.entities.ally.lm.projectile;

import com.qiuyue.goetyominous.client.particle.lm.Circle;
import com.qiuyue.goetyominous.client.particle.lm.Circle.EnumRingBehavior;
import com.qiuyue.goetyominous.common.entities.ally.lm.ServantMath;
import com.qiuyue.goetyominous.utils.ServantAllyUtil;
import com.qiuyue.goetyominous.common.init.lm.LmDamageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class SoulBladeBase extends LmAnimatedProjectile {

    private int warmupDelayTicks;
    private boolean sentSpikeEvent;
    private int lifeTicks = 34;
    private boolean clientSideAttackStarted;
    private LivingEntity caster;
    private UUID casterUuid;
    public int randomRot;
    public float uR = 1.0F;
    public float uG = 0.0F;
    public float uB = 0.0F;

    private static final EntityDataAccessor<Boolean> ATTACK =
            SynchedEntityData.defineId(SoulBladeBase.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_RED =
            SynchedEntityData.defineId(SoulBladeBase.class, EntityDataSerializers.BOOLEAN);

    protected SoulBladeBase(EntityType<? extends SoulBladeBase> entityType, Level level) {
        super(entityType, level);
    }

    protected SoulBladeBase(EntityType<? extends SoulBladeBase> entityType, Level level, double x, double y,
                            double z, float yRot, int warmupDelayTicks, LivingEntity casterIn,
                            float damage, boolean isRed) {
        this(entityType, level);
        this.warmupDelayTicks = warmupDelayTicks;
        this.setCaster(casterIn);
        this.setYRot(yRot * (180F / (float) Math.PI));
        this.setPos(x, y, z);
        this.setDamage(damage);
        this.setRed(isRed);
    }

    protected abstract int discardLifeTicks();

    @Override
    public int disappearTicks() {
        return 15;
    }

    public boolean getRed() {
        return this.entityData.get(IS_RED);
    }

    public void setRed(boolean red) {
        this.entityData.set(IS_RED, red);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ATTACK, false);
        this.entityData.define(IS_RED, false);
        super.defineSynchedData();
    }

    public void setCaster(@Nullable LivingEntity casterIn) {
        this.caster = casterIn;
        this.casterUuid = casterIn == null ? null : casterIn.getUUID();
    }

    @Nullable
    public LivingEntity getCaster() {
        if (this.caster == null && this.casterUuid != null && this.level() instanceof ServerLevel) {
            Entity entity = ((ServerLevel) this.level()).getEntity(this.casterUuid);
            if (entity instanceof LivingEntity) {
                this.caster = (LivingEntity) entity;
            }
        }
        return this.caster;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.warmupDelayTicks = compound.getInt("Warmup");
        if (compound.hasUUID("Owner")) {
            this.casterUuid = compound.getUUID("Owner");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Warmup", this.warmupDelayTicks);
        if (this.casterUuid != null) {
            compound.putUUID("Owner", this.casterUuid);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.AnimationTicks > 12) {
            this.controlledAnim.increaseTimer();
        }

        if (this.AnimationTicks == 3) {
            for (LivingEntity livingentity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox())) {
                this.damage(livingentity);
            }

            if (this.level().isClientSide) {
                this.spawnGroundParticles(40);
                this.level().addParticle(
                        new Circle.RingData(0.0F, ((float) Math.PI / 2F), 15,
                                this.getRed() ? this.uR : 0.0F,
                                this.getRed() ? this.uG : 0.9F,
                                this.getRed() ? this.uB : 0.8F,
                                0.8F, 26.0F, false, EnumRingBehavior.GROW),
                        this.getX(), this.getY() + 0.2F, this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }

        if (this.lifeTicks > 20 && this.lifeTicks < 32 && this.level().isClientSide) {
            this.spawnGroundParticles(80);
        }

        if (this.level().isClientSide) {
            if (this.clientSideAttackStarted) {
                ++this.lifeTicks;
            }
        } else if (--this.warmupDelayTicks < 0) {
            if (this.warmupDelayTicks == -10 && this.getAnimationState() == 0) {
                this.setAnimationState(1);
            }

            if (this.warmupDelayTicks < -16 && this.warmupDelayTicks > -30) {
                for (LivingEntity livingentity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox())) {
                    if (this.tickCount % 15 != 0) {
                        continue;
                    }
                    this.damage(livingentity);
                }
            }

            if (!this.sentSpikeEvent) {
                this.level().broadcastEntityEvent(this, (byte) 4);
                this.sentSpikeEvent = true;
            }

            if (++this.lifeTicks > this.discardLifeTicks()) {
                this.discard();
            }
        }
    }

    private void spawnGroundParticles(int count) {
        for (int i = 0; i < count; ++i) {
            BlockState block = this.level().getBlockState(this.blockPosition().below());
            double d0 = this.getX() + (this.random.nextDouble() * 2.0D - 1.0D) * (double) this.getBbWidth() * 0.5D;
            double d1 = this.getY() + 0.03D;
            double d2 = this.getZ() + (this.random.nextDouble() * 2.0D - 1.0D) * (double) this.getBbWidth() * 0.5D;
            double d3 = this.random.nextGaussian() * 0.07D;
            double d4 = this.random.nextGaussian() * 0.07D;
            double d5 = this.random.nextGaussian() * 0.07D;
            this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, block), d0, d1, d2, d3, d4, d5);
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

        if (!impactEntity.isAlive() || impactEntity.isInvulnerable() || impactEntity == caster) {
            return;
        }

        if (caster == null) {
            impactEntity.hurt(LmDamageTypes.ghostlyOrAttackerless(this.level(), null),
                    this.getDamage() + ServantMath.entityBasedHpDamage(impactEntity, 3.0F));
            return;
        }

        if (caster.isAlliedTo(impactEntity) || ServantAllyUtil.areAllied(caster, impactEntity)) {
            return;
        }

        if (impactEntity.hurt(LmDamageTypes.ghostly(caster),
                this.getDamage() + ServantMath.entityBasedHpDamage(impactEntity, 3.0F))) {
            caster.heal(5.0F);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 4) {
            this.clientSideAttackStarted = true;
        } else if (id <= 1) {
            this.lifeTicks = 0;
            this.AnimationTicks = 0;
        } else {
            super.handleEntityEvent(id);
        }
    }

    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        this.randomRot = this.random.nextInt(-180, 2);
    }
}
