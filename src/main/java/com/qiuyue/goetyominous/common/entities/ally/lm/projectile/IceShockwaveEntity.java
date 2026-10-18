package com.qiuyue.goetyominous.common.entities.ally.lm.projectile;

import com.Polarice3.Goety.api.entities.ISpellEntity;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import com.qiuyue.goetyominous.utils.ServantAllyUtil;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class IceShockwaveEntity extends Entity implements ISpellEntity {
    private static final EntityDataAccessor<Float> DAMAGE =
            SynchedEntityData.defineId(IceShockwaveEntity.class, EntityDataSerializers.FLOAT);

    private int warmupDelayTicks;
    private boolean sentSpikeEvent;
    private int lifeTicks = 3;
    private boolean clientSideAttackStarted;
    private LivingEntity caster;
    private UUID casterUuid;

    public IceShockwaveEntity(EntityType<? extends IceShockwaveEntity> entityType, Level level) {
        super(entityType, level);
    }

    public IceShockwaveEntity(Level level, double x, double y, double z, float yRot, int warmupDelay,
                              LivingEntity caster, float damage) {
        this(LmEntityRegistry.ICE_SHOCKWAVE.get(), level);
        this.warmupDelayTicks = warmupDelay;
        this.setCaster(caster);
        this.setDamage(damage);
        this.setYRot(yRot * (180F / (float) Math.PI));
        this.setPos(x, y, z);
        if (!level.isClientSide) {
            for (LivingEntity livingEntity : level.getEntitiesOfClass(LivingEntity.class,
                    this.getBoundingBox().inflate(0.2D, 0.0D, 0.2D))) {
                this.playSound(SoundEvents.STONE_BREAK, 1.0F, 0.5F);
                this.damage(livingEntity);
            }
        }
    }

    public void setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DAMAGE, 0.0F);
    }

    public void setCaster(@Nullable LivingEntity caster) {
        this.caster = caster;
        this.casterUuid = caster == null ? null : caster.getUUID();
    }

    @Nullable
    public LivingEntity getCaster() {
        if (this.caster == null && this.casterUuid != null && this.level() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(this.casterUuid);
            if (entity instanceof LivingEntity livingEntity) {
                this.caster = livingEntity;
            }
        }
        return this.caster;
    }

    @Override
    public void tick() {
        if (this.level().isClientSide) {
            BlockState blockState = this.level().getBlockState(this.blockPosition().below());
            for (int i = 0; i < 20; ++i) {
                double x = this.getX() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth();
                double y = this.getY() + 0.5D;
                double z = this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth();
                double motX = (this.random.nextFloat() - this.random.nextFloat()) * 3.0F;
                double motY = 0.5F + this.random.nextFloat() * 2.0F;
                double motZ = (this.random.nextFloat() - this.random.nextFloat()) * 3.0F;
                this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockState),
                        x, y, z, motX, motY, motZ);
            }
        }
        for (LivingEntity livingEntity : this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(0.2D, 0.0D, 0.2D))) {
            this.damage(livingEntity);
        }
        super.tick();
        if (this.level().isClientSide) {
            if (this.clientSideAttackStarted) {
                --this.lifeTicks;
            }
        } else if (--this.warmupDelayTicks < 0) {
            if (!this.sentSpikeEvent) {
                this.level().broadcastEntityEvent(this, (byte) 4);
                this.sentSpikeEvent = true;
            }
            if (--this.lifeTicks < 0) {
                this.discard();
            }
        }
    }

    private void damage(LivingEntity impactEntity) {
        LivingEntity caster = this.getCaster();
        if (caster == null || !impactEntity.isAlive() || impactEntity.isInvulnerable()
                || impactEntity == caster || this.tickCount % 5 != 0) {
            return;
        }
        if (ServantAllyUtil.areAllied(caster, impactEntity)) {
            return;
        }
        DamageSource damageSource = this.level().damageSources().mobAttack(caster);
        impactEntity.hurt(damageSource, this.getDamage());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleEntityEvent(byte id) {
        if (id == 4) {
            this.clientSideAttackStarted = true;
        } else {
            super.handleEntityEvent(id);
        }
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
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
