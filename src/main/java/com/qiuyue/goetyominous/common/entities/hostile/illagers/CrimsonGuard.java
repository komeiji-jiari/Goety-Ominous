package com.qiuyue.goetyominous.common.entities.hostile.illagers;

import com.Polarice3.Goety.client.particles.SmashParticleOption;
import com.Polarice3.Goety.common.entities.ai.ModMeleeAttackGoal;
import com.Polarice3.Goety.common.entities.hostile.illagers.HuntingIllagerEntity;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class CrimsonGuard extends HuntingIllagerEntity {
    public static final int ANIM_IDLE = 0;
    public static final int ANIM_SLAM = 1;
    public static final int ANIM_SWEEP = 2;
    private static final EntityDataAccessor<Boolean> DATA_ATTACKING =
            SynchedEntityData.defineId(CrimsonGuard.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_ANIM_STATE =
            SynchedEntityData.defineId(CrimsonGuard.class, EntityDataSerializers.INT);
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState slamAnimationState = new AnimationState();
    public final AnimationState sweepAnimationState = new AnimationState();
    private int attackTick;
    private int sweepCooldown;

    public CrimsonGuard(EntityType<? extends CrimsonGuard> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.CrimsonGuardHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.CrimsonGuardArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.CrimsonGuardToughness.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.CrimsonGuardSpeed.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.CrimsonGuardFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.CrimsonGuardDamage.get())
                .add(Attributes.ATTACK_KNOCKBACK, AttributesConfig.CrimsonGuardAttackKnockback.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.CrimsonGuardKnockbackResistance.get());
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.CrimsonGuardHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.CrimsonGuardArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS), AttributesConfig.CrimsonGuardToughness.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED), AttributesConfig.CrimsonGuardSpeed.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE), AttributesConfig.CrimsonGuardFollowRange.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.CrimsonGuardDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_KNOCKBACK), AttributesConfig.CrimsonGuardAttackKnockback.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.KNOCKBACK_RESISTANCE), AttributesConfig.CrimsonGuardKnockbackResistance.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ATTACKING, false);
        this.entityData.define(DATA_ANIM_STATE, ANIM_IDLE);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("SweepCooldown", this.sweepCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.sweepCooldown = Math.max(0, compound.getInt("SweepCooldown"));
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new AttackSequenceGoal());
        this.goalSelector.addGoal(5, new ApproachGoal(this, 1.25D));
        this.goalSelector.addGoal(2, new RaiderOpenDoorGoal(this));
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public void knockback(double strength, double x, double z) {
    }

    public boolean isMeleeAttacking() {
        return this.entityData.get(DATA_ATTACKING);
    }

    public int getCurrentAnimation() {
        return this.entityData.get(DATA_ANIM_STATE);
    }

    public int getAttackTick() {
        return this.attackTick;
    }

    public boolean isMoving() {
        return this.walkAnimation.speed() >= 0.01F;
    }

    protected void beginAttack(int type) {
        this.attackTick = 0;
        this.entityData.set(DATA_ATTACKING, true);
        this.setAnimationState(type);
    }

    protected void stopAttack() {
        this.attackTick = 0;
        this.entityData.set(DATA_ATTACKING, false);
        this.setAnimationState(ANIM_IDLE);
    }

    protected void setAnimationState(int state) {
        this.entityData.set(DATA_ANIM_STATE, state);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (DATA_ANIM_STATE.equals(accessor) && this.level().isClientSide) {
            switch (this.getCurrentAnimation()) {
                case ANIM_SLAM -> {
                    this.slamAnimationState.start(this.tickCount);
                    this.sweepAnimationState.stop();
                    this.idleAnimationState.stop();
                }
                case ANIM_SWEEP -> {
                    this.sweepAnimationState.start(this.tickCount);
                    this.slamAnimationState.stop();
                    this.idleAnimationState.stop();
                }
                default -> {
                    this.slamAnimationState.stop();
                    this.sweepAnimationState.stop();
                }
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            if (this.isAlive()) {
                this.idleAnimationState.animateWhen(!this.isMeleeAttacking(), this.tickCount);
            }
            if (!this.isMeleeAttacking()) {
                this.slamAnimationState.stop();
                this.sweepAnimationState.stop();
            }
        } else {
            if (this.sweepCooldown > 0) {
                --this.sweepCooldown;
            }
            if (this.isMeleeAttacking()) {
                ++this.attackTick;
            }
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.VINDICATOR_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.VINDICATOR_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VINDICATOR_DEATH;
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.VINDICATOR_CELEBRATE;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(ModSounds.ROYAL_GUARD_STEP.get(), 0.15F, 1.0F);
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        SoundEvent sound = this.getHurtSound(source);
        if (sound != null) {
            this.playSound(sound, 0.8F, this.getRandom().nextBoolean() ? 0.9F : 0.8F);
        }
        this.ambientSoundTime = -this.getAmbientSoundInterval();
        this.playSound(ModSounds.DOUBLE_AXE_IMPACT_SHING.get(), 0.4F,
                this.getRandom().nextIntBetweenInclusive(5, 7) * 0.1F);
        this.playSound(ModSounds.DIRT_SHATTER_THREE.get(),
                0.2F * this.getRandom().nextIntBetweenInclusive(7, 10) * 0.1F,
                this.getRandom().nextIntBetweenInclusive(7, 10) * 0.1F);
        this.playSound(ModSounds.HAMMER_SHIMMER_IMPACT_FOUR.get(),
                0.32F * this.getRandom().nextIntBetweenInclusive(5, 10) * 0.1F,
                this.getRandom().nextIntBetweenInclusive(5, 7) * 0.1F);
        this.playSound(ModSounds.PLATE.get(), this.getSoundVolume(), this.getVoicePitch());
    }

    @Override
    public void die(DamageSource source) {
        this.playSound(ModSounds.DOUBLE_AXE_IMPACT_SHING.get(),
                0.75F * this.getRandom().nextIntBetweenInclusive(5, 10) * 0.1F,
                (this.getRandom().nextBoolean() ? 0.65F : 0.6F)
                        * this.getRandom().nextIntBetweenInclusive(5, 7) * 0.1F);
        this.playSound(ModSounds.DIRT_SHATTER_THREE.get(),
                0.375F * this.getRandom().nextIntBetweenInclusive(7, 10) * 0.1F,
                (this.getRandom().nextBoolean() ? 0.65F : 0.6F)
                        * this.getRandom().nextIntBetweenInclusive(7, 10) * 0.1F);
        this.playSound(ModSounds.HAMMER_SHIMMER_IMPACT_FOUR.get(),
                0.6F * this.getRandom().nextIntBetweenInclusive(5, 10) * 0.1F,
                (this.getRandom().nextBoolean() ? 0.65F : 0.6F)
                        * this.getRandom().nextIntBetweenInclusive(5, 7) * 0.1F);
        this.playSound(ModSounds.PLATE_DROP.get(), this.getSoundVolume(), this.getVoicePitch());
        super.die(source);
    }

    @Override
    public void extraGoals() {
    }

    @Override
    public void setRider(boolean rider) {
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.EMPTY;
    }

    @Override
    public void applyRaidBuffs(int wave, boolean noClue) {
    }

    private class ApproachGoal extends ModMeleeAttackGoal {
        private final CrimsonGuard guard;

        public ApproachGoal(CrimsonGuard guard, double speed) {
            super(guard, speed, true);
            this.guard = guard;
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity target, double distToEnemySqr) {
            if (target == null || !target.isAlive() || this.guard.isMeleeAttacking()) {
                return;
            }
            if (distToEnemySqr > 12.25D) {
                return;
            }
            int type = ANIM_SLAM;
            if (this.guard.sweepCooldown <= 0 && this.guard.getRandom().nextFloat() < 0.5F) {
                type = ANIM_SWEEP;
                this.guard.sweepCooldown = 100;
            }
            this.guard.beginAttack(type);
            this.guard.getNavigation().stop();
        }

        @Override
        public void stop() {
            this.guard.getNavigation().stop();
            if (this.guard.getTarget() == null) {
                this.guard.setAggressive(false);
            }
        }
    }

    private class AttackSequenceGoal extends Goal {
        private float lockedYaw;
        private int attackType;
        private int hitTick;
        private int endTick;

        public AttackSequenceGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return CrimsonGuard.this.isMeleeAttacking();
        }

        @Override
        public boolean canContinueToUse() {
            return CrimsonGuard.this.isMeleeAttacking()
                    && CrimsonGuard.this.getAttackTick() < this.endTick;
        }

        @Override
        public void start() {
            LivingEntity target = CrimsonGuard.this.getTarget();
            if (target != null) {
                MobUtil.instaLook(CrimsonGuard.this, target);
            }
            this.lockedYaw = CrimsonGuard.this.getYRot();
            this.attackType = CrimsonGuard.this.getCurrentAnimation();
            this.hitTick = this.attackType == ANIM_SWEEP ? 22 : 19;
            this.endTick = this.attackType == ANIM_SWEEP ? 38 : 36;
            CrimsonGuard.this.getNavigation().stop();
            CrimsonGuard.this.getMoveControl().strafe(0.0F, 0.0F);
        }

        @Override
        public void stop() {
            CrimsonGuard.this.stopAttack();
            CrimsonGuard.this.getNavigation().stop();
        }

        @Override
        public void tick() {
            CrimsonGuard guard = CrimsonGuard.this;
            guard.getNavigation().stop();
            guard.getMoveControl().strafe(0.0F, 0.0F);
            guard.setYRot(this.lockedYaw);
            guard.setYBodyRot(this.lockedYaw);
            LivingEntity target = guard.getTarget();
            if (target != null && target.isAlive()) {
                double dx = target.getX() - guard.getX();
                double dz = target.getZ() - guard.getZ();
                float yaw = (float) (Math.atan2(dz, dx) * (180.0D / Math.PI)) - 90.0F;
                guard.setYHeadRot(yaw);
                double dy = target.getEyeY() - guard.getEyeY();
                double distance = Math.sqrt(dx * dx + dz * dz);
                float pitch = (float) (-(Math.atan2(dy, distance) * (180.0D / Math.PI)));
                guard.setXRot(pitch);
            }
            int tick = guard.getAttackTick();
            if (tick == 1) {
                guard.playSound(ModSounds.ROYAL_GUARD_PRE_ATTACK.get(), 0.5F, guard.getVoicePitch() * 0.75F);
            }
            if (tick == this.hitTick - 5) {
                guard.playSound(ModSounds.BLACKGUARD_SMASH.get(), 2.0F, guard.getVoicePitch());
            }
            if (tick == this.hitTick) {
                if (this.attackType == ANIM_SWEEP) {
                    this.sweep();
                } else {
                    this.slam();
                }
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        private float damage(LivingEntity target) {
            float before = target.getHealth();
            if (CrimsonGuard.this.doHurtTarget(target)) {
                return Math.max(0.0F, before - target.getHealth());
            }
            return 0.0F;
        }

        private void slam() {
            CrimsonGuard guard = CrimsonGuard.this;
            Vec3 look = guard.calculateViewVector(0.0F, guard.getYRot());
            double x = guard.getX() + look.x * 2.0D;
            double y = guard.getY();
            double z = guard.getZ() + look.z * 2.0D;
            AABB range = MobUtil.makeAttackRange(x, y, z, 3.0D, 3.0D, 3.0D);
            AABB box = guard.getBoundingBox();
            float dealt = 0.0F;
            for (LivingEntity target : guard.level().getEntitiesOfClass(LivingEntity.class, range.minmax(box))) {
                if (target == guard || !target.isAlive() || MobUtil.areAllies(guard, target)) {
                    continue;
                }
                dealt += this.damage(target);
            }
            if (dealt > 0.0F) {
                guard.heal(dealt * 0.15F);
            }
            this.particles(x, y, z);
        }

        private void sweep() {
            CrimsonGuard guard = CrimsonGuard.this;
            AttributeInstance attribute = guard.getAttribute(Attributes.ATTACK_DAMAGE);
            AttributeModifier modifier = new AttributeModifier("crimson_guard_sweep", 0.5D,
                    AttributeModifier.Operation.MULTIPLY_TOTAL);
            AttributeInstance knockback = guard.getAttribute(Attributes.ATTACK_KNOCKBACK);
            AttributeModifier knockbackModifier = new AttributeModifier("crimson_guard_sweep_knockback", 1.0D,
                    AttributeModifier.Operation.ADDITION);
            if (attribute != null) {
                attribute.addTransientModifier(modifier);
            }
            if (knockback != null) {
                knockback.addTransientModifier(knockbackModifier);
            }
            float dealt = 0.0F;
            try {
                for (LivingEntity target : MobUtil.getAttackableLivingEntitiesNearby(guard, 3.5D, 1.0D, 3.5D, 3.5D)) {
                    if (target == guard || MobUtil.areAllies(guard, target)) {
                        continue;
                    }
                    float angle = MobUtil.getRelativeAngle(guard, target);
                    if (Math.abs(angle) <= 50.0F || angle >= 310.0F || angle <= -310.0F) {
                        dealt += this.damage(target);
                    }
                }
            } finally {
                if (attribute != null) {
                    attribute.removeModifier(modifier.getId());
                }
                if (knockback != null) {
                    knockback.removeModifier(knockbackModifier.getId());
                }
            }
            if (dealt > 0.0F) {
                guard.heal(dealt * 0.10F);
            }
        }

        private void particles(double x, double y, double z) {
            CrimsonGuard guard = CrimsonGuard.this;
            if (!(guard.level() instanceof ServerLevel serverLevel)) {
                return;
            }
            double groundY = BlockFinder.findGroundY(serverLevel, x, y, z);
            BlockPos pos = BlockPos.containing(x, groundY - 1.0D, z);
            BlockState state = serverLevel.getBlockState(pos);
            BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, state);
            for (int i = 0; i < 2; ++i) {
                ServerParticleUtil.circularParticles(serverLevel, option, x, groundY + 0.25D, z, 1.5F);
            }
            ColorUtil color = new ColorUtil(0xB20000);
            serverLevel.sendParticles(new SmashParticleOption(color.red(), color.green(), color.blue(), 1.5F, 10),
                    x, groundY, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }
}
