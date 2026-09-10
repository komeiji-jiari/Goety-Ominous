package com.qiuyue.goetyominous.common.entities.ally.neutral;

import com.Polarice3.Goety.api.entities.IBreathing;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ai.BreathingAttackGoal;
import com.Polarice3.Goety.common.entities.neutral.AbstractWraith;
import com.Polarice3.Goety.utils.EffectsUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.qiuyue.goetyominous.common.entities.ai.FrostBallGoal;
import com.qiuyue.goetyominous.common.entities.projectile.FrostBallEntity;
import com.qiuyue.goetyominous.common.init.ModSounds;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractDredenEntity extends AbstractWraith implements IBreathing, RangedAttackMob {

    public AbstractDredenEntity(EntityType<? extends Summoned> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.DredenHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.DredenFollowRange.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.DredenMovementSpeed.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.DredenDamage.get());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new BreathingAttackGoal<>(this, 20.0F, 20, 0.025F));
        this.goalSelector.addGoal(4, new FrostBallGoal(this, 10.0F));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.25D, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.isBreathing() && this.isAlive()) {
            if (this.level().isClientSide) {
                Vec3 look = this.getLookAngle();
                double px = this.getX() + look.x * 0.9D;
                double py = this.getY() + 1.5D + look.y * 0.9D;
                double pz = this.getZ() + look.z * 0.9D;
                for (int i = 0; i < 2; i++) {
                    double dx = look.x, dy = look.y, dz = look.z;
                    double spread = 5.0D + this.random.nextDouble() * 2.5D;
                    double vel = 0.6D + this.random.nextDouble() * 0.6D;
                    dx += this.random.nextGaussian() * 0.0075D * spread;
                    dy += this.random.nextGaussian() * 0.0075D * spread;
                    dz += this.random.nextGaussian() * 0.0075D * spread;
                    this.level().addParticle(ParticleTypes.SNOWFLAKE, px, py, pz, dx * vel, dy * vel, dz * vel);
                }
            }
            this.playSound(SoundEvents.PLAYER_BREATH, this.random.nextFloat() * 0.5F, this.random.nextFloat() * 0.5F);
        }

        if (!this.level().isClientSide && this.isAlive()) {
            ((ServerLevel) this.level()).sendParticles(ParticleTypes.CLOUD,
                    this.getRandomX(0.5D), this.getY() + 0.5D, this.getRandomZ(0.5D),
                    1, (0.5D - this.random.nextDouble()) * 0.15D, 0.01D,
                    (0.5D - this.random.nextDouble()) * 0.15D,
                    (0.5D - this.random.nextDouble()) * 0.15D);

            BlockPos pos = this.blockPosition();
            if (this.level().getBiome(pos).value().getBaseTemperature() > 1.0F && this.level().isDay()) {
                this.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 1));
            } else if (this.isOnFire() && this.tickCount % 50 == 0) {
                this.clearFire();
            }
            if (this.level().isRainingAt(pos)
                    && this.level().getBiome(pos).value().coldEnoughToSnow(pos)) {
                this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, 1, false, false));
                if (this.tickCount % 20 == 0) {
                    this.heal(1.0F);
                }
            }
        }
    }

    @Override
    public void teleportAI() {
        super.teleportAI();
        if (this.level().isClientSide && this.isTeleporting()) {
            for (int i = 0; i < 16; ++i) {
                double d0 = (double) i / 15.0D;
                double d1 = Mth.lerp(d0, this.xo, this.getX()) + (this.random.nextDouble() - 0.5D) * this.getBbWidth() * 2.0D;
                double d2 = Mth.lerp(d0, this.yo, this.getY()) + this.random.nextDouble() * this.getBbHeight();
                double d3 = Mth.lerp(d0, this.zo, this.getZ()) + (this.random.nextDouble() - 0.5D) * this.getBbWidth() * 2.0D;
                this.level().addParticle(ParticleTypes.CLOUD, d1, d2, d3,
                        (this.random.nextFloat() - 0.5F) * 0.2F,
                        (this.random.nextFloat() - 0.5F) * 0.2F,
                        (this.random.nextFloat() - 0.5F) * 0.2F);
            }
        }
    }

    @Override
    public void attackAI() {
    }

    @Override
    public void doBreathing(Entity target) {
        if (target instanceof LivingEntity living) {
            float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
            if (living.hurt(ModDamageSource.frostBreath(living, this), damage)) {
                LivingEntity master = this.getMasterOwner();
                boolean robe = master != null
                        && com.Polarice3.Goety.utils.CuriosFinder.hasFrostRobes(master);
                if (!living.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                    living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, robe ? 1 : 0));
                } else if (this.random.nextFloat() <= 0.025F) {
                    EffectsUtil.amplifyEffect(living, MobEffects.MOVEMENT_SLOWDOWN, 300);
                } else {
                    EffectsUtil.resetDuration(living, MobEffects.MOVEMENT_SLOWDOWN, 300);
                }
            }
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        boolean hurt = target.hurt(ModDamageSource.frostBreath(target, this), damage);
        if (hurt && target instanceof LivingEntity living) {
            living.knockback(0.4D, Mth.sin(this.getYRot() * Mth.DEG_TO_RAD), -Mth.cos(this.getYRot() * Mth.DEG_TO_RAD));
            this.setLastHurtMob(living);
        }
        return hurt;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        double d1 = target.getX() - this.getX();
        double d2 = (target.getY() + target.getBbHeight() * 0.35D) - this.getY(0.5D);
        double d3 = target.getZ() - this.getZ();
        FrostBallEntity frostBall = new FrostBallEntity(this.level(), this, d1, d2, d3);
        frostBall.setPos(frostBall.getX(), this.getY(0.75D), frostBall.getZ());
        this.level().addFreshEntity(frostBall);
        this.swing(InteractionHand.MAIN_HAND);
    }

    @Override protected SoundEvent getAmbientSound() { return ModSounds.DREDEN_IDLE.get(); }
    @Override protected SoundEvent getHurtSound(DamageSource damageSource) { return ModSounds.DREDEN_HURT.get(); }
    @Override protected SoundEvent getDeathSound() { return ModSounds.DREDEN_DEATH.get(); }
    @Override protected SoundEvent getStepSound() { return ModSounds.DREDEN_FLY.get(); }
}
