package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.common.entities.projectiles.WaterHurtingProjectile;
import com.qiuyue.goetyominous.client.sound.VoltServantElectricChargeSoundHandler;
import com.unusualmodding.opposing_force.entity.projectile.FrictionlessProjectile;
import com.unusualmodding.opposing_force.utils.ParticleUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.NetworkHooks;

public class VoltServantElectricCharge extends WaterHurtingProjectile {

    private static final EntityDataAccessor<Boolean> RAINBOW = SynchedEntityData.defineId(VoltServantElectricCharge.class, EntityDataSerializers.BOOLEAN);

    private float damageBonus = 0.0F;
    private int effectDuration = 300;
    private int spasmsDuration = 0;
    private float radiusBonus = 0.0F;

    public VoltServantElectricCharge(EntityType<? extends AbstractHurtingProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public void setDamageBonus(float damageBonus) {
        this.damageBonus = damageBonus;
    }

    public void setEffectDuration(int effectDuration) {
        this.effectDuration = effectDuration;
    }

    public void setSpasmsDuration(int spasmsDuration) {
        this.spasmsDuration = spasmsDuration;
    }

    public void setRadiusBonus(float radiusBonus) {
        this.radiusBonus = radiusBonus;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(RAINBOW, false);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setRainbow(compound.getBoolean("Rainbow"));
        this.damageBonus = compound.getFloat("DamageBonus");
        this.effectDuration = compound.contains("EffectDuration") ? compound.getInt("EffectDuration") : 300;
        this.spasmsDuration = compound.getInt("SpasmsDuration");
        this.radiusBonus = compound.getFloat("RadiusBonus");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Rainbow", this.isRainbow());
        compound.putFloat("DamageBonus", this.damageBonus);
        compound.putInt("EffectDuration", this.effectDuration);
        compound.putInt("SpasmsDuration", this.spasmsDuration);
        compound.putFloat("RadiusBonus", this.radiusBonus);
    }

    public boolean isRainbow() {
        return this.entityData.get(RAINBOW);
    }

    public void setRainbow(boolean rainbow) {
        this.entityData.set(RAINBOW, rainbow);
    }

    @Override
    protected AABB makeBoundingBox() {
        float f = this.getType().getDimensions().width / 2.0F;
        float f1 = this.getType().getDimensions().height;
        return new AABB(this.getX() - (double) f, this.getY() - 0.15D, this.getZ() - (double) f,
                this.getX() + (double) f, this.getY() - 0.15D + (double) f1, this.getZ() + (double) f);
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return !this.isFrictionless(entity) && super.canCollideWith(entity);
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        if (this.isFrictionless(entity)) {
            return false;
        }
        return super.canHitEntity(entity);
    }

    private boolean isFrictionless(Entity entity) {
        return entity instanceof FrictionlessProjectile || entity instanceof VoltServantElectricCharge;
    }

    @Override
    public void push(double x, double y, double z) {
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    protected float getInertia() {
        return 1.0F;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public void tick() {
        this.baseTick();
        if (this.level().isClientSide && this.isAlive()) {
            VoltServantElectricChargeSoundHandler.playFor(this);
        }
        this.moveCharge();
        this.spawnElectricParticles(1 + this.random.nextInt(3), 0.0F, 16.0F);
        if (!this.level().isClientSide && this.level().getFluidState(this.blockPosition()).is(FluidTags.WATER)) {
            this.createExplosion(4.0F);
            this.discard();
        }
        if (!this.level().isClientSide && this.getBlockY() > this.level().getMaxBuildHeight() + 30) {
            this.createExplosion(3.0F);
            this.discard();
        }
    }

    private void moveCharge() {
        if (!this.level().isClientSide && this.getBlockY() > this.level().getMaxBuildHeight() + 30) {
            this.discard();
            return;
        }
        Entity owner = this.getOwner();
        if (this.level().isClientSide || (owner == null || !owner.isRemoved()) && this.level().hasChunkAt(this.blockPosition())) {
            HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitResult.getType() != HitResult.Type.MISS && !ForgeEventFactory.onProjectileImpact(this, hitResult)) {
                this.onHit(hitResult);
            }
            this.checkInsideBlocks();
            Vec3 vec3 = this.getDeltaMovement();
            double d0 = this.getX() + vec3.x;
            double d1 = this.getY() + vec3.y;
            double d2 = this.getZ() + vec3.z;
            ProjectileUtil.rotateTowardsMovement(this, 0.2F);
            float f;
            if (this.isInWater()) {
                for (int i = 0; i < 4; ++i) {
                    this.level().addParticle(ParticleTypes.BUBBLE, d0 - vec3.x * 0.25D, d1 - vec3.y * 0.25D, d2 - vec3.z * 0.25D, vec3.x, vec3.y, vec3.z);
                }
                f = 0.8F;
            } else {
                f = this.getInertia();
            }
            this.setDeltaMovement(vec3.add(this.xPower, this.yPower, this.zPower).scale((double) f));
            this.setPos(d0, d1, d2);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide) {
            this.createExplosion(3.0F + this.radiusBonus);
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide) {
            this.createExplosion(3.0F + this.radiusBonus);
            this.discard();
        }
    }

    protected void createExplosion(float radius) {
        if (!this.level().isClientSide) {
            this.spawnElectricParticles((int) (radius + 1.0F + this.random.nextInt((int) radius + 1)), 0.25F, 16.0F);
            VoltServantElectricExplosion explosion = new VoltServantElectricExplosion(
                    this.level(), this, this.getX(), this.getY(0.0625D), this.getZ(), radius,
                    this.damageBonus, this.effectDuration, this.spasmsDuration);
            explosion.explode();
            explosion.finalizeExplosion(true);
        }
    }

    private void spawnElectricParticles(int range, float yHeight, float particleMax) {
        Vec3 movement = this.getDeltaMovement();
        double x = this.getX() + movement.x;
        double y = this.getY() + movement.y + (double) yHeight;
        double z = this.getZ() + movement.z;
        int lightningLength = (int) ((float) range + 0.8F);
        int i = 0;
        while ((float) i < particleMax) {
            if (!this.level().isClientSide) {
                if (this.isRainbow()) {
                    ParticleUtils.spawnLightningParticles(x, y, z, lightningLength,
                            0.1F + this.random.nextFloat(), 0.1F + this.random.nextFloat(), 0.1F + this.random.nextFloat());
                } else {
                    ParticleUtils.spawnLightningParticles(x, y, z, lightningLength,
                            0.3F + this.random.nextFloat() / 8.0F, 0.5F + this.random.nextFloat() / 8.0F, 0.8F + this.random.nextFloat() / 8.0F);
                }
            }
            ++i;
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        if (this.level().isClientSide) {
            VoltServantElectricChargeSoundHandler.clearFor(this);
        }
        super.remove(reason);
    }
}
