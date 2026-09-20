package com.qiuyue.goetyominous.common.entities.ally.mobs.mm;

import com.Polarice3.Goety.common.entities.projectiles.HellBlast;
import com.Polarice3.Goety.common.entities.projectiles.Hellfire;
import com.alexander.mutantmore.config.mutant_blaze.MutantBlazeCommonConfig;
import com.alexander.mutantmore.init.ParticleTypeInit;
import com.alexander.mutantmore.init.SoundEventInit;
import com.alexander.mutantmore.util.MiscUtils;
import com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class GiantHellBlast extends HellBlast {
    public float explosionDamage = 0.0F;
    public float explosionRadius = 1.0F;
    public boolean explosionFire = true;
    public boolean spawnedByDyingMutantBlaze = false;

    public GiantHellBlast(EntityType<? extends GiantHellBlast> type, Level level) {
        super(type, level);
    }

    public GiantHellBlast(Level level, LivingEntity shooter, double dx, double dy, double dz) {
        super(MmEntityRegistry.GIANT_HELL_BLAST.get(), level);
        this.setOwner(shooter);
        this.moveTo(shooter.getX(), shooter.getY(0.5), shooter.getZ(), shooter.getYRot(), shooter.getXRot());
        this.setDeltaMovement(new Vec3(dx, dy, dz).normalize());
    }

    @Override
    public void travel() {
        if (!this.level().isClientSide) {
            ProjectileUtil.rotateTowardsMovement(this, 0.2F);
            Vec3 motion = this.getDeltaMovement();
            this.setPos(this.getX() + motion.x, this.getY() + motion.y, this.getZ() + motion.z);
        }
    }

    @Override
    public void push(double p_20287_, double p_20288_, double p_20289_) {
    }

    @Override
    public void push(Entity p_20294_) {
    }

    @Override
    protected void onHit(HitResult hitResult) {
        if (!this.level().isClientSide) {
            if (hitResult instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof MutantBlazeServantShieldPart
                    && this.tickCount <= 10) {
                return;
            }
            if (this.spawnedByDyingMutantBlaze && hitResult instanceof EntityHitResult entityHit
                    && (entityHit.getEntity() instanceof MutantBlazeServantRodProjectile
                    || entityHit.getEntity() instanceof MutantBlazeServantFireball)) {
                return;
            }
            if (hitResult instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof MutantBlazeServantShieldPart
                    && this.tickCount > 10) {
                this.shieldExplosion();
                this.discard();
                return;
            }
            if (hitResult instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof MutantBlazeServant blaze
                    && this.tickCount > 10 && MutantBlazeCommonConfig.stunnable.get()) {
                blaze.playSound(SoundEventInit.MUTANT_BLAZE_STUN_START.get(), 2.0F, MiscUtils.randomSoundPitch());
                blaze.stunnedTicks = blaze.stunnedLength;
                this.level().broadcastEntityEvent(blaze, (byte) 12);
                this.shieldExplosion();
                this.discard();
                return;
            }

            Entity owner = this.getOwner();
            Vec3 pos = this.position();
            MiscUtils.customExplosion(this.level(), owner != null ? owner : this,
                    this.damageSources().explosion(this, owner instanceof LivingEntity ? owner : null),
                    null, pos.x, pos.y, pos.z,
                    Mth.clamp(this.explosionRadius, 1.0F, Float.MAX_VALUE), false,
                    BlockInteraction.KEEP, SoundEvents.GENERIC_EXPLODE, this.getSoundSource(),
                    ParticleTypes.EXPLOSION, ParticleTypeInit.FIRE_TRAIL.get(), this.explosionDamage, true, false);

            if (this.level() instanceof ServerLevel serverLevel && owner instanceof LivingEntity ownerLiving) {
                BlockPos center = this.blockPosition();
                for (int dx = -2; dx <= 2; ++dx) {
                    for (int dz = -2; dz <= 2; ++dz) {
                        if (Math.abs(dx) + Math.abs(dz) <= 2) {
                            Hellfire hellfire = new Hellfire(this.level(),
                                    new Vec3(center.getX() + dx + 0.5D, center.getY() + 0.2D, center.getZ() + dz + 0.5D),
                                    ownerLiving);
                            serverLevel.addFreshEntity(hellfire);
                        }
                    }
                }
            }
            this.discard();
        }
    }

    private void shieldExplosion() {
        Entity owner = this.getOwner();
        MiscUtils.customExplosion(this.level(), owner != null ? owner : this,
                this.damageSources().explosion(this, owner instanceof LivingEntity ? owner : null),
                null, this.getX(), this.getY(), this.getZ(), 1.5F, false,
                BlockInteraction.KEEP, SoundEvents.GENERIC_EXPLODE, this.getSoundSource(),
                ParticleTypes.EXPLOSION, ParticleTypeInit.FIRE_TRAIL.get(), 1.0F, true, false);
    }
}
