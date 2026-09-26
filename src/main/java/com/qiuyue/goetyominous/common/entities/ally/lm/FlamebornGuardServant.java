package com.qiuyue.goetyominous.common.entities.ally.lm;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.FlamebornGuardBlockGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IMoveGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IStateGoal;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.miauczel.legendary_monsters.Particle.ModParticles;
import net.miauczel.legendary_monsters.Particle.custom.BigAnnihilationSweepParticle;
import net.miauczel.legendary_monsters.config.ModConfig;
import net.miauczel.legendary_monsters.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;

import java.util.List;

public class FlamebornGuardServant extends AbstractFlamebornServant {

    public int block_cooldown = 60;
    public int SweepType = 1;
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState SweepAnimationState = new AnimationState();
    public final AnimationState DoubleSweepAnimationState = new AnimationState();
    public final AnimationState BlockAnimationState = new AnimationState();
    public final AnimationState deathAnimationState = new AnimationState();
    public int deathAnimTicks;

    public FlamebornGuardServant(EntityType<? extends Summoned> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 5;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.FlamebornGuardServantHealth.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.FlamebornGuardServantKnockbackResistance.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.FlamebornGuardServantFollowRange.get())
                .add(Attributes.ARMOR, AttributesConfig.FlamebornGuardServantArmor.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.FlamebornGuardServantMovementSpeed.get())
                .add(Attributes.ATTACK_KNOCKBACK, AttributesConfig.FlamebornGuardServantAttackKnockback.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.FlamebornGuardServantDamage.get());
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

        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 2, 0, 40, 35, 3.5F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && FlamebornGuardServant.this.getRandom().nextFloat() * 100.0F < 20.0F
                        && FlamebornGuardServant.this.getTarget() != null
                        && FlamebornGuardServant.this.getNextSweepType() == 1;
            }

            @Override
            public void stop() {
                FlamebornGuardServant.this.randomizeAttacks();
                super.stop();
            }
        });
        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 5, 0, 60, 50, 3.5F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && FlamebornGuardServant.this.getRandom().nextFloat() * 100.0F < 16.0F
                        && FlamebornGuardServant.this.getTarget() != null;
            }

            @Override
            public void stop() {
                FlamebornGuardServant.this.randomizeAttacks();
                super.stop();
            }
        });
        this.goalSelector.addGoal(0, new FlamebornGuardBlockGoal(this, 4, 4, 0, 26, 26) {
            @Override
            public void stop() {
                super.stop();
                FlamebornGuardServant.this.randomizeAttacks();
                FlamebornGuardServant.this.block_cooldown = 60;
            }
        });
        this.goalSelector.addGoal(1, new IStateGoal(this, 6, 6, 0, 60, 0) {
            @Override
            public void stop() {
                FlamebornGuardServant.this.randomizeAttacks();
                super.stop();
            }
        });
    }

    public void randomizeAttacks() {
        this.randomizeNextSweepType(2);
    }

    public int getNextSweepType() {
        return this.SweepType;
    }

    public void randomizeNextSweepType(int rolls) {
        switch (this.getRandom().nextInt(rolls)) {
            case 0 -> this.SweepType = 1;
            case 1 -> this.SweepType = 2;
        }
    }

    public boolean isDuringTeleportation() {
        return this.getAttackState() == 4 && this.attackTicks > 9 && this.attackTicks < 15;
    }

    public void updateWithAttack() {
        float sweepSize = 1.25F;
        float sweepRot = 0.0F;
        float uRange = 3.25F;
        if (this.getAttackState() == 2) {
            if (this.attackTicks == 13) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.25F);
                this.calculatedDash(0.2F);
            }
            if (this.attackTicks == 14) {
                this.createSweep(0.0F, 0.0F, 3.0F, true, sweepSize, sweepRot);
            }
            if (this.attackTicks == 16) {
                this.SideAreaAttack(uRange, 3.0F, 180.0F, 0.0F, 0.0F, 12.0F, 30, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 0.75F);
            }
        }
        if (this.getAttackState() == 5) {
            if (this.attackTicks == 15) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.25F);
                this.calculatedDash(0.2F);
            }
            if (this.attackTicks == 16) {
                this.createSweep(0.0F, 0.0F, 3.0F, false, sweepSize, sweepRot);
            }
            if (this.attackTicks == 18) {
                this.SideAreaAttack(uRange, 3.0F, 180.0F, 0.0F, 0.0F, 12.0F, 30, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 0.75F);
            }
            if (this.attackTicks == 28) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.25F);
                this.calculatedDash(0.2F);
            }
            if (this.attackTicks == 29) {
                this.createSweep(0.0F, 0.0F, 3.0F, true, sweepSize, sweepRot);
            }
            if (this.attackTicks == 31) {
                this.SideAreaAttack(uRange, 3.0F, 180.0F, 0.0F, 0.0F, 12.0F, 30, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 0.75F);
            }
        }
    }

    @Override
    public void tick() {
        if (this.level().isClientSide) {
            if (this.isDuringTeleportation() && !this.isInvisible()) {
                this.setInvisible(true);
            } else if (!this.isDuringTeleportation() && this.isInvisible()) {
                this.setInvisible(false);
            }
        }
        if (this.block_cooldown > 0) {
            --this.block_cooldown;
        }
        this.idleAnimationState.animateWhen(this.getAttackState() == 0, this.tickCount);
        this.updateWithAttack();
        super.tick();
    }

    public boolean canBlock() {
        return this.getAttackState() == 0 && this.block_cooldown <= 0 && !this.level().isClientSide && this.getTarget() != null;
    }

    @Override
    public int attackDelayTicksValue() {
        return this.block_cooldown <= 0 ? 3 : 0;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (this.getAttackState() == 4 && this.attackTicks < 15) {
            return false;
        }
        if (pSource.is(DamageTypeTags.IS_PROJECTILE)) {
            this.playSound(SoundEvents.SHULKER_TELEPORT, 1.0F, 1.0F);
            this.teleportRandomly(this, 8.0F, 10.0F);
            return false;
        }
        pAmount *= 0.75F;
        if (this.canBlock() && pAmount > 1.0F) {
            this.setAttackState(4);
            this.playSound(ModSounds.BLOCK.get(), 1.0F, 1.25F);
            return false;
        }
        return super.hurt(pSource, pAmount);
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
                    (float) ((double) damage * ModConfig.MOB_CONFIG.FlamebornGuardDamageMutliplier.get()
                            * AttributesConfig.FlamebornGuardServantDamage.get() / 12.0D));
            if (flag) {
                this.playSound(soundEvent, 1.0F, pitch);
            }
            if (entityHit instanceof Player && entityHit.isBlocking() && brokenShieldTicks > 0) {
                disableShield(entityHit, brokenShieldTicks);
            }
        }
    }

    public void createSweep(float pos, float posOffset, float yHeight, boolean reverse, float scale, float rot) {
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180));
        double theta = this.yBodyRot * (Math.PI / 180) + 1.5707963267948966;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        double x = this.getX() + (double) pos * vecX + (double) (f * posOffset);
        double z = this.getZ() + (double) pos * vecZ + (double) (f1 * posOffset);
        if (this.level().isClientSide) {
            double d1 = this.getY() + (double) (this.getBbHeight() / 2.0F) + 0.4D;
            float yaw = (float) Math.toRadians(-this.yBodyRot + (reverse ? rot : 180.0F));
            double lookX = -Math.cos(yaw);
            double lookZ = -Math.sin(yaw);
            float pitch = (float) (reverse ? -1 : 1) * (float) Math.atan2(yHeight, Math.sqrt(lookX * lookX + lookZ * lookZ));
            this.level().addParticle(new BigAnnihilationSweepParticle.SweepData(this.getScale() * scale, yaw, pitch), x, d1, z, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public boolean teleport(double x, double y, double z) {
        Level level = this.level();
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ModParticles.TELEPORT_EFFECT.get(), this.getX(), this.getY() + 3.0D, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(x, y, z);
        while (mutablePos.getY() > level.getMinBuildHeight() && !level.getBlockState(mutablePos).blocksMotion()) {
            mutablePos.move(Direction.DOWN);
        }
        if (!level.getBlockState(mutablePos).blocksMotion()) {
            return false;
        }
        EntityTeleportEvent.EnderEntity event = ForgeEventFactory.onEnderTeleport(this, x, y, z);
        if (event.isCanceled()) {
            return false;
        }
        Vec3 oldPos = this.position();
        if (this.teleportBoolean(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true)) {
            level.gameEvent(GameEvent.TELEPORT, oldPos, GameEvent.Context.of(this));
            return true;
        }
        return false;
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

    public AnimationState getAnimationState(String input) {
        if (input.equals("idle")) {
            return this.idleAnimationState;
        }
        if (input.equals("sweep")) {
            return this.SweepAnimationState;
        }
        if (input.equals("block")) {
            return this.BlockAnimationState;
        }
        if (input.equals("double_sweep")) {
            return this.DoubleSweepAnimationState;
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
                    this.SweepAnimationState.startIfStopped(this.tickCount);
                }
                case 4 -> {
                    this.stopAllAnimationStates();
                    this.BlockAnimationState.startIfStopped(this.tickCount);
                }
                case 5 -> {
                    this.stopAllAnimationStates();
                    this.DoubleSweepAnimationState.startIfStopped(this.tickCount);
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
        this.SweepAnimationState.stop();
        this.deathAnimationState.stop();
        this.BlockAnimationState.stop();
        this.DoubleSweepAnimationState.stop();
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
