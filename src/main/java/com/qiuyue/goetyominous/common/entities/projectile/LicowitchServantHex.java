package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.common.entities.projectiles.SpellTargetProjectile;
import com.Polarice3.Goety.utils.MobUtil;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class LicowitchServantHex extends SpellTargetProjectile {

    private static final EntityDataAccessor<Integer> LIFESPAN = SynchedEntityData.defineId(LicowitchServantHex.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> HEX_SCALE = SynchedEntityData.defineId(LicowitchServantHex.class, EntityDataSerializers.FLOAT);

    private int despawnsIn = -1;
    private int prevDespawnsIn;
    private final float yRenderOffset = this.random.nextFloat() * 0.05F;

    public LicowitchServantHex(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LIFESPAN, 100);
        this.entityData.define(HEX_SCALE, 1.0F);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.despawnsIn == -1) {
            this.despawnsIn = this.getLifespan();
        }
        this.prevDespawnsIn = this.despawnsIn;
        if (this.despawnsIn > 0) {
            this.despawnsIn--;
        } else {
            this.discard();
        }
        if (this.level().isClientSide) {
            if (this.despawnsIn < 5) {
                for (int i = 0; i < 8 + this.random.nextInt(8); ++i) {
                    this.level().addParticle((ParticleOptions) ACParticleRegistry.PURPLE_WITCH_EXPLOSION.get(),
                            this.getRandomX(0.45F), this.getRandomY(), this.getRandomZ(0.45F), 0.0D, 0.0D, 0.0D);
                }
            } else if (this.random.nextFloat() < 0.6F) {
                Vec3 ambientParticlePos = new Vec3((double) ((this.random.nextFloat() * 4.0F - 2.0F) * this.getHexScale()),
                        0.1D, (double) ((this.random.nextFloat() * 4.0F - 2.0F) * this.getHexScale()));
                Vec3 vec3 = this.position().add(ambientParticlePos);
                Vec3 vec31 = this.position().add(ambientParticlePos.scale(1.5D).add(0.0D, this.random.nextFloat(), 0.0D));
                this.level().addParticle((ParticleOptions) ACParticleRegistry.PURPLE_WITCH_MAGIC.get(),
                        vec3.x, vec3.y, vec3.z, vec31.x, vec31.y, vec31.z);
            }
        }
        this.hurtEntities(this.despawnsIn < 5);
        Vec3 vec3 = this.getDeltaMovement();
        this.noPhysics = true;
        this.move(MoverType.SELF, vec3);
        this.setDeltaMovement(vec3.multiply(0.7F, 0.8F, 0.7F));
    }

    public float getDespawnTime(float partialTicks) {
        return (float) this.prevDespawnsIn + (float) (this.despawnsIn - this.prevDespawnsIn) * partialTicks;
    }

    public void setLifespan(int lifespan) {
        this.entityData.set(LIFESPAN, lifespan);
    }

    public int getLifespan() {
        return this.entityData.get(LIFESPAN);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("DespawnsIn")) {
            this.despawnsIn = compound.getInt("DespawnsIn");
        }
        this.setLifespan(compound.getInt("Lifespan"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("DespawnsIn", this.despawnsIn);
        compound.putInt("Lifespan", this.getLifespan());
    }

    private void hurtEntities(boolean finalExplosion) {
        if (this.level().isClientSide) {
            return;
        }
        LivingEntity owner = this.getOwner();
        DamageSource source = this.damageSources().indirectMagic(this, owner);
        for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox())) {
            if (this.isAlliedTo(entity)) {
                continue;
            }
            if (entity.distanceTo(this) > 4.0F) {
                continue;
            }
            if (owner != null) {
                if (entity.is(owner)) {
                    continue;
                }
                if (entity.isAlliedTo(owner)) {
                    continue;
                }
            }
            if (MobUtil.areAllies(entity, owner != null ? owner : this)) {
                continue;
            }
            entity.hurt(source, finalExplosion ? 6.0F : 1.0F);
            if (finalExplosion) {
                entity.knockback(0.9F, this.getX() - entity.getX(), this.getZ() - entity.getZ());
            }
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (HEX_SCALE.equals(key)) {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated(key);
    }

    public float getHexScale() {
        return this.entityData.get(HEX_SCALE);
    }

    public void setHexScale(float hexScale) {
        this.entityData.set(HEX_SCALE, hexScale);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.scalable(this.getHexScale() * 4.0F, 0.25F);
    }

    public float getYRenderOffset() {
        return this.yRenderOffset;
    }
}
