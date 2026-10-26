package com.qiuyue.goetyominous.common.entities.ally.lm;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IMoveGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IStateGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.ThrownPhantomDagger;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import com.qiuyue.goetyominous.utils.ServantAllyUtil;
import net.miauczel.legendary_monsters.Particle.ModParticles;
import net.miauczel.legendary_monsters.Particle.custom.Circle;
import net.miauczel.legendary_monsters.Particle.custom.SoulSigil;
import net.miauczel.legendary_monsters.entity.client.ControlledAnim;
import net.miauczel.legendary_monsters.sound.ModSounds;
import net.miauczel.legendary_monsters.util.MathUtils;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import com.qiuyue.goetyominous.config.lm.LmConfig;

public class FracturedApostleServant extends IAnimatedMobServant {

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState summonSwordsAnimationState = new AnimationState();
    public final AnimationState deathAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();
    public ControlledAnim bodyFadeAway = new ControlledAnim(10);
    public ControlledAnim weaponFade = new ControlledAnim(10);
    public int deathAnimTicks;
    private final Random random1 = new Random();

    public FracturedApostleServant(EntityType<? extends FracturedApostleServant> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 5;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, LmConfig.FracturedApostleServantHealth.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.7)
                .add(Attributes.FOLLOW_RANGE, 30.0)
                .add(Attributes.ARMOR, LmConfig.FracturedApostleServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, LmConfig.FracturedApostleServantArmorToughness.get())
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_KNOCKBACK, 1.3)
                .add(Attributes.ATTACK_DAMAGE, LmConfig.FracturedApostleServantDamage.get());
    }

    @Override
    public double damageMultiplier() {
        return LmConfig.FracturedApostleServantDamageMultiplier.get();
    }

    @Override
    public double getFollowSpeed() {
        return 1.0D;
    }

    @Override
    public double getCommandSpeed() {
        return 1.0D;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
                                        MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData,
                                        @Nullable CompoundTag pDataTag) {
        if (pReason == MobSpawnType.MOB_SUMMONED && this.getTrueOwner() instanceof Player player) {
            if (countServants(player) >= LmConfig.FracturedApostleServantLimit.get()) {
                return null;
            }
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof FracturedApostleServant servant && servant.getTrueOwner() == player) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(6, new Summoned.WanderGoal<>(this, this.getFollowSpeed()));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(2, new IMoveGoal(this, false, 1.0D));
        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 2, 0, 40, 40, 10.0F) {
            @Override
            public boolean canUse() {
                LivingEntity target = FracturedApostleServant.this.getTarget();
                return super.canUse()
                        && FracturedApostleServant.this.getRandom().nextFloat() * 40.0F < 16.0F
                        && target != null
                        && FracturedApostleServant.this.distanceTo(target) >= 4.0F;
            }
        });
        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 4, 0, 38, 38, 3.5F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && FracturedApostleServant.this.getRandom().nextFloat() * 40.0F < 16.0F
                        && FracturedApostleServant.this.getTarget() != null;
            }
        });
        this.goalSelector.addGoal(0, new IStateGoal(this, 3, 3, 0, 20, 0) {
            @Override
            public void tick() {
                this.entity.setDeltaMovement(0.0D, this.entity.getDeltaMovement().y, 0.0D);
            }
        });
    }

    @Override
    public void tick() {
        if (this.level().isClientSide) {
            this.idleAnimationState.animateWhen(this.getAttackState() == 0, this.tickCount);
            if (this.getAttackState() != 3) {
                this.level().addParticle(ModParticles.GHOSTLY_SOUL.get(),
                        this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D),
                        0.0D, 0.025D, 0.0D);
            }
        }
        this.updateWithAttack();
        super.tick();
    }

    public void updateWithAttack() {
        if (this.getAttackState() == 2) {
            if (this.attackTicks < MathUtils.toTicks(0.92F) && this.tickCount % 5 == 0) {
                int cap = 5;
                float velocity = 0.075F;
                float endY = 1.5F;
                this.attractParticles(this, ModParticles.GHOSTLY_SOUL.get(), cap, 4, 0.0F, 0.0F, 5.0F, endY, velocity);
                this.attractParticles(this, ModParticles.GHOSTLY_SOUL.get(), cap, 4, 0.0F, 0.0F, 3.0F, endY, velocity);
                this.attractParticles(this, ModParticles.GHOSTLY_SOUL.get(), cap, 4, 0.0F, 0.0F, 2.0F, endY, velocity);
            }
            if (this.attackTicks == MathUtils.toTicks(0.42F)) {
                this.playSound(SoundEvents.VILLAGER_WORK_LIBRARIAN, 1.0F, 1.0F);
                this.spawnCircleParticle(30.0F, 0.0F, 0.9F, 0.8F, 0.5F, Circle.EnumRingBehavior.SHRINK, 20);
            }
            if (this.attackTicks == MathUtils.toTicks(0.92F)) {
                this.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1.0F, 1.0F);
                if (this.level().isClientSide) {
                    this.level().addParticle(ModParticles.SOUL_SHOOT.get(), this.getX(), this.getY() + 4.0D, this.getZ(), 0.0D, 0.0D, 0.0D);
                    this.level().addParticle(new SoulSigil.RingData(0.0F, 0.0F, 20, 1.0F, 1.0F, 1.0F, 0.5F, 15.0F, true, SoulSigil.EnumRingBehavior.CONSTANT),
                            this.getX(), this.getY() + 4.0D, this.getZ(), 0.0D, 0.0D, 0.0D);
                }
                if (this.targetIsNotNull()) {
                    ThrownPhantomDagger dagger = new ThrownPhantomDagger(LmEntityRegistry.THROWN_PHANTOM_DAGGER.get(), this.level());
                    dagger.setPos(this.getX(), this.getY() + 4.0D, this.getZ());
                    dagger.setReturnEntity(this.target());
                    dagger.setReturnTick(0);
                    dagger.setDamage(4.0F);
                    dagger.setOwner(this);
                    dagger.setInteria(0.9F);
                    dagger.setLessLifeTicks(50);
                    this.level().addFreshEntity(dagger);
                }
            }
        }
        if (this.getAttackState() == 4) {
            if (this.attackTicks == 1) {
                this.weaponFade.resetTimer();
                this.weaponFade.setTimer(5);
            }
            if (this.attackTicks >= 2 && this.attackTicks <= 30 && this.weaponFade.getTimer() > 0) {
                this.weaponFade.decreaseTimer();
            }
            if (this.attackTicks == 29) {
                this.weaponFade.setTimer(0);
            }
            if (this.attackTicks >= 30) {
                this.weaponFade.increaseTimer();
            }
            int attackTick = MathUtils.toTicks(0.67F);
            if (this.attackTicks == attackTick - 3) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.25F);
                this.calculatedDash(0.25F);
            }
            if (this.attackTicks == attackTick) {
                this.sideAreaAttack(1.75F, 3.0F, 180.0F, 0.0F, 0.0F, 14.0F, 30,
                        SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 1.0F);
            }
        }
    }

    public boolean hasGhostKnife() {
        return this.getAttackState() == 4;
    }

    public void attractParticles(LivingEntity entity, ParticleOptions particleOptions, int cap, int reps,
                                 float vec, float offset, float startY, float endY, float velocity) {
        float yBodyRot = entity.yBodyRot;
        float f = Mth.cos(yBodyRot * ((float) Math.PI / 180));
        float f1 = Mth.sin(yBodyRot * ((float) Math.PI / 180));
        double theta = (double) yBodyRot * (Math.PI / 180);
        double vecX = Math.cos(theta + 1.5707963267948966D);
        double vecZ = Math.sin(theta + 1.5707963267948966D);
        int rX = this.random1.nextInt(-cap, cap);
        int rZ = this.random1.nextInt(-cap, cap);
        float f2 = this.random.nextFloat() * 0.5F;
        double d1 = entity.getX() + (double) rX;
        double d2 = entity.getY() + (double) startY + (double) f2;
        double d3 = entity.getZ() + (double) rZ;
        Vec3 vec3 = new Vec3(d1, d2, d3);
        Vec3 vec4 = new Vec3(entity.getX() + (double) vec * vecX + (double) (f * offset),
                entity.getY() + (double) endY,
                entity.getZ() + (double) vec * vecZ + (double) (f1 * offset));
        Vec3 vf = vec4.subtract(vec3);
        Vec3 v = vf.scale(velocity);
        for (int i = 0; i <= reps; ++i) {
            entity.level().addParticle(particleOptions, d1, d2, d3, v.x, v.y, v.z);
        }
    }

    private void spawnCircleParticle(float size, float r, float g, float b, float a,
                                     Circle.EnumRingBehavior behavior, int life) {
        if (this.level().isClientSide) {
            this.level().addParticle(new Circle.RingData(0.0F, 1.5707964F, life, r, g, b, a, size, false, behavior),
                    this.getX(), this.getY() + 0.2D, this.getZ(), 0.0D, 0.0D, 0.0D);
        }
    }

    public void sideAreaAttack(float range, float height, float arc, float boxOffset, float forwardOffset,
                               float damage, int brokenShieldTicks, SoundEvent soundEvent, float pitch) {
        double theta = Math.toRadians(this.yBodyRot) + 1.5707963267948966D;
        double forwardX = Math.cos(theta) * (double) forwardOffset;
        double forwardZ = Math.sin(theta) * (double) forwardOffset;
        List<LivingEntity> entitiesHit = this.getEntityLivingBaseNearby(range, height, range, range);
        for (LivingEntity entityHit : entitiesHit) {
            double dx = entityHit.getX() - (this.getX() + forwardX);
            double dz = entityHit.getZ() - (this.getZ() + forwardZ);
            float entityHitAngle = (float) ((Math.toDegrees(Math.atan2(dz, dx)) - 90.0D) % 360.0D);
            if (entityHitAngle < 0.0F) {
                entityHitAngle += 360.0F;
            }
            float entityAttackingAngle = (this.yBodyRot - boxOffset) % 360.0F;
            if (entityAttackingAngle < 0.0F) {
                entityAttackingAngle += 360.0F;
            }
            float entityHitDistance = (float) Math.sqrt(dx * dx + dz * dz);
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            if (entityHitDistance > range) {
                continue;
            }
            if (!(entityRelativeAngle <= arc / 2.0F && entityRelativeAngle >= -arc / 2.0F)
                    && !(entityRelativeAngle >= 360.0F - arc / 2.0F)
                    && !(entityRelativeAngle <= -360.0F + arc / 2.0F)) {
                continue;
            }
            if (ServantAllyUtil.areAllied(this, entityHit) || entityHit == this) {
                continue;
            }
            boolean flag = entityHit.hurt(this.damageSources().mobAttack(this),
                    (float) (damage * this.damageMultiplier()));
            if (flag) {
                this.playSound(soundEvent, 1.0F, pitch);
            }
            if (entityHit instanceof Player player && player.isBlocking() && brokenShieldTicks > 0) {
                disableShield(player, brokenShieldTicks);
            }
        }
    }

    public AnimationState getAnimationState(String input) {
        if (input.equals("idle")) {
            return this.idleAnimationState;
        }
        if (input.equals("summon")) {
            return this.summonSwordsAnimationState;
        }
        if (input.equals("death")) {
            return this.deathAnimationState;
        }
        if (input.equals("attack")) {
            return this.attackAnimationState;
        }
        return new AnimationState();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (ATTACK_STATE.equals(accessor) && this.level().isClientSide) {
            switch (this.getAttackState()) {
                case 0 -> this.stopAllAnimationStates();
                case 1 -> {
                    this.stopAllAnimationStates();
                    this.idleAnimationState.startIfStopped(this.tickCount);
                }
                case 2 -> {
                    this.stopAllAnimationStates();
                    this.summonSwordsAnimationState.startIfStopped(this.tickCount);
                }
                case 3 -> {
                    this.stopAllAnimationStates();
                    this.deathAnimationState.startIfStopped(this.tickCount);
                }
                case 4 -> {
                    this.stopAllAnimationStates();
                    this.attackAnimationState.startIfStopped(this.tickCount);
                }
                default -> {
                }
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    public void stopAllAnimationStates() {
        this.idleAnimationState.stop();
        this.summonSwordsAnimationState.stop();
        this.deathAnimationState.stop();
        this.attackAnimationState.stop();
    }

    @Override
    public void die(DamageSource source) {
        this.deathAnimTicks = 0;
        this.setAttackState(3);
        super.die(source);
    }

    @Override
    protected void tickDeath() {
        ++this.deathAnimTicks;
        if (this.deathAnimTicks > 10) {
            this.bodyFadeAway.increaseTimer();
        }
        if (this.deathAnimTicks == 60) {
            this.remove(Entity.RemovalReason.KILLED);
            this.gameEvent(GameEvent.ENTITY_DIE);
        }
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pSource) {
        return SoundEvents.PHANTOM_HURT;
    }
}
