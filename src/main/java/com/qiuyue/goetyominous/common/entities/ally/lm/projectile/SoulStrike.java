package com.qiuyue.goetyominous.common.entities.ally.lm.projectile;

import com.qiuyue.goetyominous.common.entities.ally.lm.ServantMath;
import com.qiuyue.goetyominous.common.init.lm.LmDamageTypes;
import com.qiuyue.goetyominous.common.init.lm.LmParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class SoulStrike extends ThrowableProjectile {

    private int lifeTime = 20;

    private static final EntityDataAccessor<Float> DAMAGE =
            SynchedEntityData.defineId(SoulStrike.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> IS_RED =
            SynchedEntityData.defineId(SoulStrike.class, EntityDataSerializers.BOOLEAN);

    public ParticleOptions soulParticle = LmParticles.GHOSTLY_SOUL.get();

    public SoulStrike(EntityType<? extends SoulStrike> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DAMAGE, 0.0F);
        this.entityData.define(IS_RED, false);
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    public void setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
    }

    public boolean getRed() {
        return this.entityData.get(IS_RED);
    }

    public void setRed(boolean red) {
        this.entityData.set(IS_RED, red);
    }

    public ParticleOptions particleOptions() {
        return this.soulParticle;
    }

    @Override
    public void shoot(double pX, double pY, double pZ, float pVelocity, float pInaccuracy) {
        Vec3 vec3 = (new Vec3(pX, pY, pZ)).normalize()
                .add(this.random.nextGaussian() * 0.0075D * (double) pInaccuracy,
                        this.random.nextGaussian() * 0.0075D * (double) pInaccuracy,
                        this.random.nextGaussian() * 0.0075D * (double) pInaccuracy)
                .scale((double) pVelocity);
        this.setDeltaMovement(vec3);
        double d0 = vec3.horizontalDistance();
        this.setYRot((float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI)));
        this.setXRot((float) (Mth.atan2(vec3.y, d0) * (double) (180F / (float) Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    @Override
    public void shootFromRotation(Entity shooter, float pX, float pY, float pZ, float pVelocity, float pInaccuracy) {
        float f = -Mth.sin(pY * ((float) Math.PI / 180F)) * Mth.cos(pX * ((float) Math.PI / 180F));
        float f1 = -1.0F;
        float f2 = Mth.cos(pY * ((float) Math.PI / 180F)) * Mth.cos(pX * ((float) Math.PI / 180F));
        this.shoot((double) f, (double) f1, (double) f2, pVelocity, pInaccuracy);
        Vec3 vec3 = shooter.getDeltaMovement();
        this.setDeltaMovement(this.getDeltaMovement().add(vec3.x,
                shooter.onGround() ? 0.0D : vec3.y, vec3.z));
    }

    @Override
    public void tick() {
        if (this.getOwner() != null && !this.getOwner().isAlive()) {
            this.discard();
        } else {
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.onUpdateInAir();
        }
        super.tick();
    }

    private void onUpdateInAir() {
        --this.lifeTime;
        if (this.lifeTime <= 0) {
            this.discard();
        }

        Entity ownerEntity = this.getOwner();
        LivingEntity livingOwner = ownerEntity instanceof LivingEntity ? (LivingEntity) ownerEntity : null;

        if (livingOwner != null && this.tickCount % 5 == 0) {
            for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class,
                    this.getBoundingBox().inflate(0.5D, 0.5D, 0.5D))) {
                if (target == livingOwner || !target.onGround()
                        || !target.isAlive() || livingOwner.isAlliedTo(target)) {
                    continue;
                }

                if (target.hurt(LmDamageTypes.ghostly(livingOwner),
                        this.getDamage() + ServantMath.entityBasedHpDamage(target, 3.0F))) {
                    livingOwner.heal(8.0F);
                }
            }
        }

        if (this.level().isClientSide) {
            for (int i = 0; i < 5; ++i) {
                double x = this.getX() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F)
                        - (double) this.getBbWidth();
                double y = this.getY() + 0.5D + (double) (this.random.nextFloat() * this.getBbHeight());
                double z = this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F)
                        - (double) this.getBbWidth();
                this.level().addParticle(this.getRed()
                                ? LmParticles.GHOSTLY_SOUL_RED.get()
                                : LmParticles.GHOSTLY_SOUL.get(),
                        x, y, z, 0.0D, 0.0D, 0.0D);
            }

            for (int i = 0; i < 2; ++i) {
                double x = this.getX() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F)
                        - (double) this.getBbWidth();
                double y = this.getY() + 0.5D + (double) (this.random.nextFloat() * this.getBbHeight());
                double z = this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F)
                        - (double) this.getBbWidth();
                this.level().addParticle(this.getRed()
                                ? LmParticles.RED_SOUL_FLAME.get()
                                : ParticleTypes.SOUL_FIRE_FLAME,
                        x, y, z, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public boolean canCollideWith(Entity pEntity) {
        return this.canHitEntity(pEntity);
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
