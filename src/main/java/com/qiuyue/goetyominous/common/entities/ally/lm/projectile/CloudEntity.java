package com.qiuyue.goetyominous.common.entities.ally.lm.projectile;

import com.qiuyue.goetyominous.utils.ServantAllyUtil;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.miauczel.legendary_monsters.damagetype.ModDamageTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import com.Polarice3.Goety.common.entities.projectiles.SpellThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class CloudEntity extends SpellThrowableProjectile {
    public boolean particleOptimalization = false;
    public int damage = 5;
    public float attackRange = 2.0F;

    public CloudEntity(EntityType<CloudEntity> type, Level world) {
        super(type, world);
    }

    public CloudEntity(EntityType<CloudEntity> type, Level world, LivingEntity thrower) {
        super(type, thrower, world);
        this.setOwner(thrower);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    public void Particle() {
        if (!MobsConfig.CloudGolemServantFallingCloudParticles.get()) {
            return;
        }
        for (int i = 0; i < 360; ++i) {
            if (i % 1 != 0 || !this.level().isClientSide()) {
                continue;
            }
            this.level().addParticle(ParticleTypes.CLOUD, this.getX(), this.getY(), this.getZ(), Math.cos(i) * 0.1, 0.0, Math.sin(i) * 0.1);
        }
    }

    public boolean setParticleOptimalization(boolean particleOptimalization) {
        this.particleOptimalization = particleOptimalization;
        return this.particleOptimalization;
    }

    public float setAttackRange(float input) {
        this.attackRange = input;
        return this.attackRange;
    }

    public int setDamage(int damage) {
        this.damage = damage;
        return this.damage;
    }

    @Override
    public void onHit(HitResult result) {
        for (LivingEntity livingEntity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(this.attackRange, this.attackRange, this.attackRange))) {
            Entity ownerEntity = this.getOwner();
            if (livingEntity == ownerEntity || ServantAllyUtil.areAllied(ownerEntity, livingEntity)
                    || livingEntity instanceof TamableAnimal animal && animal.getOwner() == ownerEntity) {
                continue;
            }
            if (!(ownerEntity instanceof LivingEntity owner)) {
                continue;
            }
            double distance = this.distanceTo(livingEntity) / 2.0F;
            double effectiveDistance = Math.max(distance, 1.0D);
            float damage = (float) this.damage / (float) effectiveDistance;
            if (ownerEntity instanceof Mob mob) {
                LivingEntity target = mob.getTarget();
                if (target != null) {
                    damage += target.getMaxHealth() * 0.01F;
                }
            }
            livingEntity.hurt(ModDamageTypes.causeCloudDamage(owner, owner), damage);
        }
        super.onHit(result);
        if (!this.particleOptimalization) {
            this.Particle();
        }
        this.playSound(SoundEvents.SHULKER_BULLET_HURT, 3.0F, 1.0F);
        this.discard();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
    }

    @Override
    public void tick() {
        super.tick();
        if (!MobsConfig.CloudGolemServantFallingCloudParticles.get() && this.level().isClientSide) {
            Vec3 vec3 = this.getDeltaMovement();
            if (this.level().isClientSide()) {
                this.level().addParticle(ParticleTypes.CLOUD, this.getX() - vec3.x, this.getY() - vec3.y, this.getZ() - vec3.z, 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
