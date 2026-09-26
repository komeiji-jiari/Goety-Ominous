package com.qiuyue.goetyominous.common.entities.ally.lm;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IMoveGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IStateGoal;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.miauczel.legendary_monsters.config.ModConfig;
import net.miauczel.legendary_monsters.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.List;

public class FlamebornWarriorServant extends AbstractFlamebornServant {

    public int hitType = 1;
    public int doubleHitType = 1;
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState hitAnimationState = new AnimationState();
    public final AnimationState hit2AnimationState = new AnimationState();
    public final AnimationState hitDoubleAnimationState = new AnimationState();
    public final AnimationState hitDouble2AnimationState = new AnimationState();
    public final AnimationState deathAnimationState = new AnimationState();
    public int deathAnimTicks;

    public FlamebornWarriorServant(EntityType<? extends Summoned> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 5;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.FlamebornWarriorServantHealth.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.FlamebornWarriorServantKnockbackResistance.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.FlamebornWarriorServantFollowRange.get())
                .add(Attributes.ARMOR, AttributesConfig.FlamebornWarriorServantArmor.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.FlamebornWarriorServantMovementSpeed.get())
                .add(Attributes.ATTACK_KNOCKBACK, AttributesConfig.FlamebornWarriorServantAttackKnockback.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.FlamebornWarriorServantDamage.get());
    }

    @Override
    public double getFollowSpeed() {
        return 3.0D;
    }

    @Override
    public double getCommandSpeed() {
        return 3.0D;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(6, new Summoned.WanderGoal<>(this, this.getFollowSpeed(), 40, 0.001F));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(2, new IMoveGoal(this, false, 3.0D));

        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 2, 0, 49, 49, 3.5F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && FlamebornWarriorServant.this.getRandom().nextFloat() * 100.0F < 20.0F
                        && FlamebornWarriorServant.this.getTarget() != null
                        && FlamebornWarriorServant.this.getNextHitType() == 1;
            }

            @Override
            public void stop() {
                FlamebornWarriorServant.this.randomizeAttacks();
                super.stop();
            }
        });
        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 3, 0, 49, 49, 3.5F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && FlamebornWarriorServant.this.getRandom().nextFloat() * 100.0F < 20.0F
                        && FlamebornWarriorServant.this.getTarget() != null
                        && FlamebornWarriorServant.this.getNextHitType() == 2;
            }

            @Override
            public void stop() {
                FlamebornWarriorServant.this.randomizeAttacks();
                super.stop();
            }
        });
        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 4, 0, 62, 62, 3.5F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && FlamebornWarriorServant.this.getRandom().nextFloat() * 100.0F < 20.0F
                        && FlamebornWarriorServant.this.getTarget() != null
                        && FlamebornWarriorServant.this.getNextDoubleHitType() == 1;
            }

            @Override
            public void stop() {
                FlamebornWarriorServant.this.randomizeAttacks();
                super.stop();
            }
        });
        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 5, 0, 62, 62, 3.5F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && FlamebornWarriorServant.this.getRandom().nextFloat() * 100.0F < 20.0F
                        && FlamebornWarriorServant.this.getTarget() != null
                        && FlamebornWarriorServant.this.getNextDoubleHitType() == 2;
            }

            @Override
            public void stop() {
                FlamebornWarriorServant.this.randomizeAttacks();
                super.stop();
            }
        });
        this.goalSelector.addGoal(1, new IStateGoal(this, 6, 6, 0, 60, 0) {
            @Override
            public void stop() {
                FlamebornWarriorServant.this.randomizeAttacks();
                super.stop();
            }
        });
    }

    public void randomizeAttacks() {
        this.randomizeNextDoubleHitType(2);
        this.randomizeNextHitType(2);
    }

    public int getNextHitType() {
        return this.hitType;
    }

    public void randomizeNextHitType(int rolls) {
        switch (this.getRandom().nextInt(rolls)) {
            case 0 -> this.hitType = 1;
            case 1 -> this.hitType = 2;
        }
    }

    public int getNextDoubleHitType() {
        return this.doubleHitType;
    }

    public void randomizeNextDoubleHitType(int rolls) {
        switch (this.getRandom().nextInt(rolls)) {
            case 0 -> this.doubleHitType = 1;
            case 1 -> this.doubleHitType = 2;
        }
    }

    public void UpdateWithAttack() {
        float range = 2.5F;
        float dash = 0.25F;
        if (this.getAttackState() == 2 || this.getAttackState() == 3) {
            if (this.attackTicks == 13) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
                this.calculatedDash(dash);
            }
            if (this.attackTicks == 16) {
                this.SideAreaAttack(range, 2.0F, 180.0F, 0.0F, 0.0F, 12.0F, 0, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 1.0F);
            }
        }
        if (this.getAttackState() == 4 || this.getAttackState() == 5) {
            if (this.attackTicks == 13) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
                this.calculatedDash(dash);
            }
            if (this.attackTicks == 16) {
                this.SideAreaAttack(range, 2.0F, 180.0F, 0.0F, 0.0F, 12.0F, 0, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 1.0F);
            }
            if (this.attackTicks == 31) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
                this.calculatedDash(dash - 0.05F);
            }
            if (this.attackTicks == 34) {
                this.SideAreaAttack(range, 4.0F, 180.0F, 0.0F, 0.0F, 12.0F, 0, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 1.0F);
            }
        }
    }

    @Override
    public void tick() {
        if (this.level().isClientSide) {
            this.idleAnimationState.animateWhen(this.getAttackState() == 0, this.tickCount);
        }
        this.UpdateWithAttack();
        super.tick();
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.is(DamageTypes.FALL)) {
            return false;
        }
        if (pSource.is(DamageTypeTags.IS_PROJECTILE)) {
            this.playSound(SoundEvents.SHULKER_TELEPORT, 1.0F, 1.0F);
            this.teleportRandomly(this, 8.0F, 10.0F);
            return false;
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    public boolean teleportBoolean(double x, double y, double z, boolean playSound) {
        double oldX = this.getX();
        double oldY = this.getY();
        double oldZ = this.getZ();
        double targetY = y;
        boolean success = false;
        BlockPos pos = BlockPos.containing(x, y, z);
        Level level = this.level();
        if (level.hasChunkAt(pos)) {
            boolean foundGround = false;
            while (!foundGround && pos.getY() > level.getMinBuildHeight()) {
                BlockPos below = pos.below();
                if (level.getBlockState(below).blocksMotion()) {
                    foundGround = true;
                    continue;
                }
                targetY -= 1.0D;
                pos = below;
            }
            if (foundGround) {
                this.teleportTo(x, targetY, z);
                if (level.noCollision(this) && !level.containsAnyLiquid(this.getBoundingBox())) {
                    success = true;
                }
            }
        }
        if (!success) {
            this.teleportTo(oldX, oldY, oldZ);
            return false;
        }
        if (playSound) {
            level.broadcastEntityEvent(this, (byte) 46);
        }
        this.getNavigation().stop();
        return true;
    }

    public void SideAreaAttack(float range, float height, float arc, float boxOffset, float forwardOffset, float damage, int brokenShieldTicks, SoundEvent soundEvent, float pitch) {
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
            if (MobUtil.areAllies(this, entityHit) || entityHit == this) {
                continue;
            }
            boolean flag = entityHit.hurt(this.damageSources().mobAttack(this),
                    (float) ((double) damage * ModConfig.MOB_CONFIG.FlamebornWarriorDamageMutliplier.get()
                            * AttributesConfig.FlamebornWarriorServantDamage.get() / 12.0D));
            if (flag) {
                this.playSound(soundEvent, 1.0F, pitch);
            }
            if (entityHit instanceof Player && entityHit.isBlocking() && brokenShieldTicks > 0) {
                disableShield(entityHit, brokenShieldTicks);
            }
        }
    }

    public AnimationState getAnimationState(String input) {
        if (input.equals("hit2")) {
            return this.hit2AnimationState;
        }
        if (input.equals("idle")) {
            return this.idleAnimationState;
        }
        if (input.equals("hit")) {
            return this.hitAnimationState;
        }
        if (input.equals("hit_double2")) {
            return this.hitDouble2AnimationState;
        }
        if (input.equals("hit_double")) {
            return this.hitDoubleAnimationState;
        }
        if (input.equals("death")) {
            return this.deathAnimationState;
        }
        return new AnimationState();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (ATTACK_STATE.equals(accessor) && this.level().isClientSide) {
            switch (this.getAttackState()) {
                case 0 -> this.stopAllAnimationStates();
                case 2 -> {
                    this.stopAllAnimationStates();
                    this.hitAnimationState.startIfStopped(this.tickCount);
                }
                case 3 -> {
                    this.stopAllAnimationStates();
                    this.hit2AnimationState.startIfStopped(this.tickCount);
                }
                case 4 -> {
                    this.stopAllAnimationStates();
                    this.hitDoubleAnimationState.startIfStopped(this.tickCount);
                }
                case 5 -> {
                    this.stopAllAnimationStates();
                    this.hitDouble2AnimationState.startIfStopped(this.tickCount);
                }
                case 6 -> {
                    this.stopAllAnimationStates();
                    this.deathAnimationState.startIfStopped(this.tickCount);
                }
                default -> {
                }
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    public void stopAllAnimationStates() {
        this.idleAnimationState.stop();
        this.hitAnimationState.stop();
        this.hit2AnimationState.stop();
        this.deathAnimationState.stop();
        this.hitDouble2AnimationState.stop();
        this.hitDoubleAnimationState.stop();
    }

    @Override
    public void die(DamageSource pDamageSource) {
        super.die(pDamageSource);
        this.deathAnimTicks = 0;
        this.setAttackState(6);
        this.stopAllAnimationStates();
    }

    @Override
    protected void tickDeath() {
        ++this.deathAnimTicks;
        if (this.deathAnimTicks == 60) {
            this.remove(Entity.RemovalReason.KILLED);
            this.gameEvent(GameEvent.ENTITY_DIE);
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pSource) {
        return SoundEvents.ENDERMAN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENDERMAN_DEATH;
    }
}
