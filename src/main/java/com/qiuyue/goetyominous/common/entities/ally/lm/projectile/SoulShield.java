package com.qiuyue.goetyominous.common.entities.ally.lm.projectile;

import com.qiuyue.goetyominous.config.MobsConfig;
import com.qiuyue.goetyominous.common.entities.ally.lm.ServantMath;
import com.qiuyue.goetyominous.utils.ServantAllyUtil;
import com.qiuyue.goetyominous.common.init.lm.LmDamageTypes;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import net.miauczel.legendary_monsters.Particle.ModParticles;
import net.miauczel.legendary_monsters.effect.ModEffects;
import net.miauczel.legendary_monsters.util.EntityUtil;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.UUID;

public class SoulShield extends LmAnimatedProjectile {

    private int warmupDelayTicks;
    private boolean sentSpikeEvent;
    private int lifeTicks = 30;
    private boolean clientSideAttackStarted;
    private LivingEntity caster;
    private UUID casterUuid;

    private static final EntityDataAccessor<Boolean> IS_OUTER =
            SynchedEntityData.defineId(SoulShield.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DESTINATION_Z =
            SynchedEntityData.defineId(SoulShield.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DESTINATION_Y =
            SynchedEntityData.defineId(SoulShield.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DESTINATION_X =
            SynchedEntityData.defineId(SoulShield.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> ATTACK =
            SynchedEntityData.defineId(SoulShield.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_RED =
            SynchedEntityData.defineId(SoulShield.class, EntityDataSerializers.BOOLEAN);

    public SoulShield(EntityType<? extends SoulShield> entityType, Level level) {
        super(entityType, level);
    }

    public SoulShield(Level worldIn, double x, double y, double z, float yawRad, int warmupDelayTicks,
                      LivingEntity casterIn, float damage, float destX, float destY, float destZ,
                      boolean isOuter, boolean isRed) {
        this(LmEntityRegistry.SOUL_SHIELD.get(), worldIn);
        this.setIsOuter(isOuter);
        this.warmupDelayTicks = warmupDelayTicks;
        this.setCaster(casterIn);
        this.setYRot(yawRad * (180F / (float) Math.PI) - 90.0F);
        this.setPos(x, y, z);
        if (!isOuter) {
            this.setDestinationX(destX);
            this.setDestinationY(destY);
            this.setDestinationZ(destZ);
        }
        this.setRed(isRed);
        this.setDamage(damage);
        this.setMaxUpStep(1.5F);
        this.noPhysics = true;
    }

    @Override
    public int disappearTicks() {
        return 10;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    public boolean getRed() {
        return this.entityData.get(IS_RED);
    }

    public void setRed(boolean red) {
        this.entityData.set(IS_RED, red);
    }

    public float getDestinationX() {
        return this.entityData.get(DESTINATION_X);
    }

    public float getDestinationY() {
        return this.entityData.get(DESTINATION_Y);
    }

    public float getDestinationZ() {
        return this.entityData.get(DESTINATION_Z);
    }

    public boolean getIsOuter() {
        return this.entityData.get(IS_OUTER);
    }

    public void setDestinationX(float destination) {
        this.entityData.set(DESTINATION_X, destination);
    }

    public void setDestinationY(float destination) {
        this.entityData.set(DESTINATION_Y, destination);
    }

    public void setDestinationZ(float destination) {
        this.entityData.set(DESTINATION_Z, destination);
    }

    public void setIsOuter(boolean isOuter) {
        this.entityData.set(IS_OUTER, isOuter);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ATTACK, false);
        this.entityData.define(DESTINATION_X, 0.0F);
        this.entityData.define(DESTINATION_Y, 0.0F);
        this.entityData.define(DESTINATION_Z, 0.0F);
        this.entityData.define(IS_OUTER, false);
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

        if (this.lifeTicks > 50) {
            this.controlledAnim.increaseTimer();
        }

        if (this.level().isClientSide) {
            for (int i = 0; i < 10; ++i) {
                double d0 = this.getX() + (this.random.nextDouble() * 2.0D - 1.0D) * (double) this.getBbWidth() * 0.5D;
                double d1 = this.getY() + 0.03D;
                double d2 = this.getZ() + (this.random.nextDouble() * 2.0D - 1.0D) * (double) this.getBbWidth() * 0.5D;
                double d3 = this.random.nextGaussian() * 0.07D;
                double d4 = this.random.nextGaussian() * 0.07D;
                double d5 = this.random.nextGaussian() * 0.07D;

                if (this.tickCount % 5 == 0) {
                    this.level().addParticle(this.getRed() ? ModParticles.GROUNDSOUL_RED.get() : ModParticles.GROUNDSOUL.get(),
                            this.getX(), this.getY() + 2.0D, this.getZ(), 0.0D, 0.0D, 0.0D);
                }
                this.level().addParticle(this.getRed() ? ModParticles.GHOSTLY_SOUL_RED.get() : ModParticles.GHOSTLY_SOUL.get(),
                        d0, d1, d2, d3, d4, d5);
            }
        }

        if (this.getIsOuter()) {
            float f = Mth.cos(this.getYRot() * ((float) Math.PI / 180));
            float f1 = Mth.sin(this.getYRot() * ((float) Math.PI / 180));
            double theta = (double) this.getYRot() * (Math.PI / 180);
            double vecX = Math.cos(theta += 1.5707963267948966);
            double vecZ = Math.sin(theta);
            float vec = 8.0F;
            float offset = 0.0F;
            this.setDestinationX((float) (this.getX() + (double) vec * vecX + (double) (f * offset)));
            this.setDestinationY((float) this.getY());
            this.setDestinationZ((float) (this.getZ() + (double) vec * vecZ + (double) (f1 * offset)));
        }

        for (LivingEntity livingentity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.25))) {
            this.damage(livingentity);
        }

        Vec3 toDest = new Vec3(
                (double) this.getDestinationX() - this.getX(),
                (double) this.getDestinationY() - this.getY(),
                (double) this.getDestinationZ() - this.getZ());
        double distance = toDest.length();
        double speed = 0.1D + toDest.length() * 0.05D;
        if (distance > speed) {
            this.move(MoverType.SELF, toDest.normalize().scale(speed));
        }

        this.lookAt(EntityAnchorArgument.Anchor.EYES,
                new Vec3(this.getDestinationX(), this.getDestinationY(), this.getDestinationZ()));

        if (this.level().isClientSide) {
            if (this.clientSideAttackStarted) {
                ++this.lifeTicks;
            }
        } else if (--this.warmupDelayTicks < 0) {
            if (!this.sentSpikeEvent) {
                this.level().broadcastEntityEvent(this, (byte) 4);
                this.sentSpikeEvent = true;
            }

            if (++this.lifeTicks > 55) {
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

    @Override
    public void handleEntityEvent(byte id) {
        super.handleEntityEvent(id);
        if (id == 4) {
            this.clientSideAttackStarted = true;
        }
        if (id <= 0) {
            this.lifeTicks = 0;
        }
    }

    private void damage(LivingEntity impactEntity) {
        LivingEntity caster = this.getCaster();

        if (!impactEntity.isAlive() || impactEntity.isInvulnerable()
                || impactEntity == caster || this.tickCount % 5 != 0) {
            return;
        }

        float amount = this.getDamage() + ServantMath.entityBasedHpDamage(impactEntity, 3.0F);

        if (caster == null) {
            if (impactEntity.hurt(LmDamageTypes.ghostlyOrAttackerless(this.level(), null), amount)) {
                if (MobsConfig.SoulFractureOnServantHit.get()) {
                    EntityUtil.applyStackingEffect(impactEntity, ModEffects.SOUL_FRACTURE.get(),
                            1, 4, ServantMath.toTicks(10.0F));
                }
            }
            return;
        }

        if (caster.isAlliedTo(impactEntity) || ServantAllyUtil.areAllied(caster, impactEntity)) {
            return;
        }

        if (impactEntity.hurt(LmDamageTypes.ghostly(caster), amount)) {
            if (MobsConfig.SoulFractureOnServantHit.get()) {
                EntityUtil.applyStackingEffect(impactEntity, ModEffects.SOUL_FRACTURE.get(),
                        1, 4, ServantMath.toTicks(10.0F));
            }
            caster.heal(5.0F);
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
        if (this.getAnimationState() == 0) {
            this.setAnimationState(1);
        }
    }
}
