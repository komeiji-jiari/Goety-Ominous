package com.qiuyue.goetyominous.common.entities.ally.mobs.mm;

import com.Polarice3.Goety.common.entities.projectiles.SpellThrowableProjectile;
import com.Polarice3.Goety.utils.MobUtil;
import com.alexander.mutantmore.audio.soundinstances.EntityLoopingSoundInstance;
import com.alexander.mutantmore.entities.MutantShulker;
import com.alexander.mutantmore.events.ShakeCameraEvent;
import com.alexander.mutantmore.init.MMDamageTypes;
import com.alexander.mutantmore.init.ParticleTypeInit;
import com.alexander.mutantmore.init.SoundEventInit;
import com.alexander.mutantmore.init.TagInit.Blocks;
import com.alexander.mutantmore.util.MiscUtils;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;

public class MutantShulkerServantBullet extends SpellThrowableProjectile {
    private static final EntityDataAccessor<Integer> HITS_LEFT =
            SynchedEntityData.defineId(MutantShulkerServantBullet.class, EntityDataSerializers.INT);

    public int immuneTicks = 0;
    public float damage = 0.0F;
    public float explosionSize = 1.0F;
    public int levitationLength = 0;
    public int levitationLevel = 0;
    public boolean ignoresInvulTime = true;
    public float trackSpeed = 1.25F;
    public int moveDelay = 0;
    private Vec3 targetMovement = null;

    public MutantShulkerServantBullet(EntityType<? extends MutantShulkerServantBullet> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setRemainingHits(3);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HITS_LEFT, 0);
    }

    @Override
    public void tick() {
        this.baseTick();
        if (this.level().isClientSide) {
            ShakeCameraEvent.shake(this.level(), 3, 0.0075F, this.blockPosition(), 5);
        }
        if (this.immuneTicks > 0) {
            --this.immuneTicks;
        }
        if (!this.level().isClientSide) {
            HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitresult.getType() != Type.MISS && !ForgeEventFactory.onProjectileImpact(this, hitresult)) {
                this.onHit(hitresult);
            }
        }
        this.checkInsideBlocks();

        LivingEntity target = this.getTarget();
        if (this.getOwner() != null && this.tickCount > 20 && this.immuneTicks <= 0 && this.distanceTo(this.getOwner()) <= this.getOwner().getBbWidth() * 2.0F) {
            double dx = this.getOwner().getX() - this.getX();
            double dz = this.getOwner().getZ() - this.getZ();
            double dist = this.distanceTo(this.getOwner());
            this.setDeltaMovement(this.getDeltaMovement().add((new Vec3(dx / dist, 0.0D, dz / dist)).scale(-0.3D)));
        }
        if (target != null) {
            if (this.moveDelay > 0 && this.distanceTo(target) >= 7.5) {
                --this.moveDelay;
            }
            if (!this.level().getBlockState(this.blockPosition().below()).isAir()
                    && !this.level().getBlockState(this.blockPosition().below()).getCollisionShape(this.level(), this.blockPosition().below()).isEmpty()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.1D, 0.0D));
            }
            this.xOld = this.getX();
            this.yOld = this.getY();
            this.zOld = this.getZ();
            if (this.moveDelay <= 0) {
                this.moveDelay = 20;
                double dx = target.getX() - this.getX();
                double dy = target.getY(0.5) - this.getY();
                double dz = target.getZ() - this.getZ();
                double distance = this.distanceTo(target);
                this.targetMovement = new Vec3(dx / distance * this.trackSpeed, dy / distance * this.trackSpeed, dz / distance * this.trackSpeed);
            }
            if (this.targetMovement != null) {
                this.setDeltaMovement(Mth.lerp(0.1D, this.getDeltaMovement().x, this.targetMovement.x),
                        Mth.lerp(0.1D, this.getDeltaMovement().y, this.targetMovement.y),
                        Mth.lerp(0.1D, this.getDeltaMovement().z, this.targetMovement.z));
            }
            this.move(MoverType.SELF, this.getDeltaMovement());
        } else if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.05D, 0.0D));
            this.move(MoverType.SELF, this.getDeltaMovement());
        }

        if (this.level().isClientSide && this.immuneTicks <= 0) {
            Vec3 motion = this.getDeltaMovement();
            this.level().addParticle(ParticleTypeInit.MUTANT_SHULKER_BULLET.get(),
                    this.getX() - motion.x, this.getY() - motion.y + 0.425D, this.getZ() - motion.z, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public boolean ignoreExplosion() {
        return true;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (this.level().isClientSide) {
            Minecraft.getInstance().getSoundManager().play(
                    new EntityLoopingSoundInstance(this, SoundEventInit.MUTANT_SHULKER_PROJECTILE_LOOP.get(),
                            this.getSoundSource(), 0.7F, 1.5F, 1.2F, true, 0.0F, 0.0F, false));
        }
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        LivingEntity owner = this.getOwner();
        if (owner != null && MobUtil.areAllies(owner, target)) {
            return false;
        }
        return super.canHitEntity(target);
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        Entity entity = hitResult.getEntity();
        if (!(entity instanceof MutantShulkerServantBullet) && !(entity instanceof MutantShulker)) {
            LivingEntity owner = this.getOwner();
            if (this.canHarm(entity)) {
                if (this.ignoresInvulTime) {
                    entity.invulnerableTime = 0;
                }
                boolean flag = entity.hurt(MMDamageTypes.mutantShulkerBulletAttack(this.damageSources(), this, owner), this.damage);
                this.explode();
                if (flag) {
                    this.doEnchantDamageEffects(owner, entity);
                    if (entity instanceof LivingEntity living && living.getMaxHealth() <= MobsConfig.MutantShulkerBulletLevitationMaxHealth.get().floatValue()) {
                        living.addEffect(new MobEffectInstance(MobEffects.LEVITATION, this.levitationLength, this.levitationLevel), owner != null ? owner : this);
                    }
                }
            }
        } else if (entity instanceof MutantShulkerServantBullet) {
            entity.setDeltaMovement(entity.getDeltaMovement().add(this.getDeltaMovement().scale(0.75D)));
        }
    }

    boolean canHarm(Entity target) {
        if (MiscUtils.canHarmBasedOnTeamAndTag(null, this, target, this.getOwner(), (entity) -> true)) {
            return true;
        }
        return false;
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        if (!this.level().isClientSide && !this.level().getBlockState(hitResult.getBlockPos()).is(Blocks.MUTANT_SHULKER_BULLET_FLIES_THROUGH)) {
            this.explode();
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide) {
            if (hitResult.getType() == Type.ENTITY && ((EntityHitResult) hitResult).getEntity() instanceof MutantShulker mutantShulker) {
                mutantShulker.stunnedTicks = mutantShulker.stunLength;
                this.level().broadcastEntityEvent(mutantShulker, (byte) 9);
                this.explode();
                this.discard();
            }
            if ((hitResult.getType() != Type.BLOCK || !this.level().getBlockState(((BlockHitResult) hitResult).getBlockPos()).is(Blocks.MUTANT_SHULKER_BULLET_FLIES_THROUGH))) {
                this.discard();
            }
        }
    }

    private void explode() {
        Entity damagingMob = this.getOwner() != null ? this.getOwner() : this;
        LivingEntity owner = this.getOwner();
        MiscUtils.customExplosion(this.level(), damagingMob,
                this.damageSources().explosion(this, owner),
                null, this.getX(), this.getY(), this.getZ(),
                Mth.clamp(this.explosionSize, 1.0F, Float.MAX_VALUE), false,
                BlockInteraction.KEEP, SoundEventInit.MUTANT_SHULKER_PROJECTILE_IMPACT.get(),
                this.getSoundSource(), ParticleTypeInit.MUTANT_SHULKER_BULLET.get(), ParticleTypeInit.MUTANT_SHULKER_BULLET.get(),
                this.damage, true, false);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float amount) {
        if (this.immuneTicks <= 0) {
            if (damageSource.getDirectEntity() != null) {
                this.shootFromRotation(damageSource.getDirectEntity(), damageSource.getDirectEntity().getXRot(),
                        damageSource.getDirectEntity().getYRot(), 0.0F, 0.5F, 1.0F);
                this.targetMovement = null;
                this.moveDelay = 60;
            }
            this.setRemainingHits(this.getRemainingHits() - 1);
            this.immuneTicks = 60;
            if (!this.level().isClientSide) {
                this.playSound(SoundEvents.SHULKER_BULLET_HURT, 1.0F, 1.0F);
                for (int i = 0; i < 3; ++i) {
                    if (this.getRemainingHits() <= 0) {
                        ((ServerLevel) this.level()).sendParticles(this.random.nextBoolean() ? ParticleTypes.END_ROD : ParticleTypes.CRIT,
                                this.getRandomX(1.25), this.getRandomY(), this.getRandomZ(1.25), 15, 0.2D, 0.2D, 0.2D, 0.0D);
                    } else {
                        ((ServerLevel) this.level()).sendParticles(ParticleTypes.CRIT,
                                this.getRandomX(1.25), this.getRandomY(), this.getRandomZ(1.25), 15, 0.2D, 0.2D, 0.2D, 0.0D);
                    }
                }
                if (this.getRemainingHits() <= 0) {
                    this.discard();
                }
            }
        }
        return true;
    }

    @Override
    public float getPickRadius() {
        return 2.0F;
    }

    @Override
    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    public int getRemainingHits() {
        return this.entityData.get(HITS_LEFT);
    }

    public void setRemainingHits(int remainingHits) {
        this.entityData.set(HITS_LEFT, remainingHits);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("RemainingHits", this.getRemainingHits());
        tag.putFloat("Damage", this.damage);
        tag.putFloat("ExplosionSize", this.explosionSize);
        tag.putInt("LevitationLength", this.levitationLength);
        tag.putInt("LevitationLevel", this.levitationLevel);
        tag.putBoolean("IgnoresInvulTime", this.ignoresInvulTime);
        tag.putFloat("TrackSpeed", this.trackSpeed);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("RemainingHits")) {
            this.setRemainingHits(tag.getInt("RemainingHits"));
        }
        if (tag.contains("Damage")) {
            this.damage = tag.getFloat("Damage");
        }
        if (tag.contains("ExplosionSize")) {
            this.explosionSize = tag.getFloat("ExplosionSize");
        }
        if (tag.contains("LevitationLength")) {
            this.levitationLength = tag.getInt("LevitationLength");
        }
        if (tag.contains("LevitationLevel")) {
            this.levitationLevel = tag.getInt("LevitationLevel");
        }
        if (tag.contains("IgnoresInvulTime")) {
            this.ignoresInvulTime = tag.getBoolean("IgnoresInvulTime");
        }
        if (tag.contains("TrackSpeed")) {
            this.trackSpeed = tag.getFloat("TrackSpeed");
        }
    }
}
