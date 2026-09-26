package com.qiuyue.goetyominous.common.entities.ally.lm.projectile;

import com.qiuyue.goetyominous.client.particle.lm.Circle;
import com.qiuyue.goetyominous.client.particle.lm.Circle.EnumRingBehavior;
import com.qiuyue.goetyominous.common.entities.ally.lm.ServantMath;
import com.qiuyue.goetyominous.common.init.lm.LmDamageTypes;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import com.qiuyue.goetyominous.common.init.lm.LmParticles;
import com.qiuyue.goetyominous.common.init.lm.LmSounds;
import net.miauczel.legendary_monsters.effect.ModEffects;
import net.miauczel.legendary_monsters.util.EntityUtil;
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

public class SoulPillarExplosionEntity extends Entity {

    private int warmupDelayTicks;
    private boolean sentSpikeEvent;
    private int lifeTicks = 20;
    private boolean clientSideAttackStarted;
    private LivingEntity caster;
    private UUID casterUuid;

    private static final EntityDataAccessor<Integer> LIFE =
            SynchedEntityData.defineId(SoulPillarExplosionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DAMAGE =
            SynchedEntityData.defineId(SoulPillarExplosionEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> ATTACK =
            SynchedEntityData.defineId(SoulPillarExplosionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_RED =
            SynchedEntityData.defineId(SoulPillarExplosionEntity.class, EntityDataSerializers.BOOLEAN);

    public float uR = 1.0F;
    public float uG = 0.0F;
    public float uB = 0.0F;

    public SoulPillarExplosionEntity(EntityType<? extends SoulPillarExplosionEntity> entityType, Level level) {
        super(entityType, level);
    }

    public SoulPillarExplosionEntity(
            Level worldIn, double x, double y, double z, float yRot, int warmupDelayTicks,
            LivingEntity casterIn, int lifeTicks, float damage, boolean isRed
    ) {
        this(LmEntityRegistry.SOUL_PILLAR_EXPLOSION.get(), worldIn);
        this.warmupDelayTicks = warmupDelayTicks;
        this.setCaster(casterIn);
        this.setYRot(yRot * (180F / (float) Math.PI));
        this.setPos(x, y, z);
        this.setLifeTicks(lifeTicks);
        this.setDamage(damage);
        this.setRed(isRed);
    }

    public boolean getRed() {
        return this.entityData.get(IS_RED);
    }

    public void setRed(boolean damage) {
        this.entityData.set(IS_RED, damage);
    }

    public void setLifeTicks(int lifeTicks) {
        this.lifeTicks = lifeTicks;
    }

    public void setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ATTACK, false);
        this.entityData.define(LIFE, 0);
        this.entityData.define(DAMAGE, 0.0F);
        this.entityData.define(IS_RED, false);
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

        if (this.tickCount == 1) {
            this.level().addParticle(LmParticles.GROUNDSOUL_RED.get(),
                    this.getX(), this.getY() + 2.0D, this.getZ(), 0.0D, 0.0D, 0.0D);
            this.level().addParticle(
                    new Circle.RingData(0.0F, ((float) Math.PI / 2F), 25, this.uR, this.uG, this.uB,
                            0.8F, 20.0F, false, EnumRingBehavior.SHRINK),
                    this.getX(), this.getY() + 0.2F, this.getZ(), 0.0D, 0.0D, 0.0D);
        }

        if (this.lifeTicks == 19 && !this.isSilent()) {
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(),
                    LmSounds.SOUL_FLY.get(), this.getSoundSource(), 0.3F, 1.25F, false);
        }

        if (this.level().isClientSide) {
            if (this.clientSideAttackStarted) {
                --this.lifeTicks;
                if (this.lifeTicks == 19) {
                    for (int i = 0; i < 80; ++i) {
                        BlockState block = this.level().getBlockState(this.blockPosition().below());
                        double d0 = this.getX() + (this.random.nextDouble() * 2.0D - 1.0D) * (double) this.getBbWidth() * 0.5D;
                        double d1 = this.getY() + 0.03D;
                        double d2 = this.getZ() + (this.random.nextDouble() * 2.0D - 1.0D) * (double) this.getBbWidth() * 0.5D;
                        double d3 = this.random.nextGaussian() * 0.07D;
                        double d4 = this.random.nextGaussian() * 0.07D;
                        double var13 = this.random.nextGaussian() * 0.07D;
                    }

                    this.level().addParticle(
                            new Circle.RingData(0.0F, ((float) Math.PI / 2F), 15, this.uR, this.uG, this.uB,
                                    0.8F, 20.0F, false, EnumRingBehavior.GROW),
                            this.getX(), this.getY() + 0.2F, this.getZ(), 0.0D, 0.0D, 0.0D);
                    this.level().addAlwaysVisibleParticle(LmParticles.SOUL_PILLAR_EXPLOSION.get(),
                            this.getX(), this.getY() + 2.0D, this.getZ(), 0.0D, 0.0D, 0.0D);
                }

            }
        } else if (--this.warmupDelayTicks < 0) {
            if (this.warmupDelayTicks > -8) {
                for (LivingEntity livingentity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox())) {
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

        if (!impactEntity.isAlive() || impactEntity.isInvulnerable()
                || impactEntity == caster || this.tickCount % 5 != 0) {
            return;
        }

        if (caster == null) {
            impactEntity.hurt(LmDamageTypes.ghostlyOrAttackerless(this.level(), null),
                    this.getDamage() + ServantMath.entityBasedHpDamage(impactEntity, 3.0F));
            return;
        }

        if (caster.isAlliedTo(impactEntity)) {
            return;
        }

        if (impactEntity.hurt(LmDamageTypes.ghostly(caster), this.getDamage())) {
            EntityUtil.applyStackingEffect(impactEntity, ModEffects.SOUL_FRACTURE.get(),
                    1, 4, ServantMath.toTicks(10.0F));
            impactEntity.setDeltaMovement(this.getDeltaMovement().x,
                    this.getDeltaMovement().y + 0.85D, this.getDeltaMovement().z);
            EntityUtil.applyPlayerDeltaMovement(impactEntity);
            caster.heal(4.0F);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 4) {
            this.clientSideAttackStarted = true;
        } else if (id <= 0) {
            this.lifeTicks = 0;
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
    }
}
