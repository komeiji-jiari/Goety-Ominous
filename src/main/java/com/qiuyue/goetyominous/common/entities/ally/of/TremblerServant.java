package com.qiuyue.goetyominous.common.entities.ally.of;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.ally.of.goals.TremblerServantRollGoal;
import com.unusualmodding.opposing_force.entity.ai.navigation.SmoothGroundPathNavigation;
import com.unusualmodding.opposing_force.entity.utils.EliteVariant;
import com.unusualmodding.opposing_force.registry.OPSoundEvents;
import com.unusualmodding.opposing_force.registry.tags.OPDamageTypeTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Trembler（震颤蜗牛）仆从 —— 滚动撞击型，行为逐字节复刻 OF 原版 Trembler。
 * 与原版唯一差别：继承 Summoned（仆从体系），而不是敌对 Monster。
 */
public class TremblerServant extends Summoned implements EliteVariant {
    // ===== 同步数据（服务端<->客户端传状态）：字段名照抄 OF 原版 Trembler =====
    private static final EntityDataAccessor<Boolean> ROLLING;        // 正在滚动？
    private static final EntityDataAccessor<Integer> ROLL_COOLDOWN;  // 滚动冷却
    private static final EntityDataAccessor<Integer> STUNNED_TICKS;  // 眩晕剩余时间
    private static final EntityDataAccessor<Boolean> TURBO;          // 涡轮精英变体？

    // ===== 动画状态：OF 原版 Trembler 就这三个 =====
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState rollAnimationState = new AnimationState();
    public final AnimationState stunnedAnimationState = new AnimationState();

    public TremblerServant(EntityType<? extends Owned> entityType, Level level) {
        super(entityType, level);
        // 换用原版的三件套控制器：眩晕时会冻结移动/转头/转身
        this.moveControl = new TremblerServantMoveControl();
        this.lookControl = new TremblerServantLookControl(this);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        // 数值照抄原版：16血 / 20甲 / 速度0.15(蜗牛慢) / 攻5 / 击退1 / 抗击退0.5
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 16.0D)
                .add(Attributes.ARMOR, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.15D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // 主动索敌：Goety 默认的 SummonTargetGoal 是"仇恨驱动"，不会见敌就打。
        // 补上 NearestAttackableTargetGoal 才能像原版 Trembler 一样主动追敌对生物（Enemy）。
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, false, false,
                (target) -> target instanceof Enemy && !MobUtil.areAllies(this, target)));
        // 优先级照抄原版：1=滚动，4=闲逛，5=看玩家，6=四处乱看
        this.goalSelector.addGoal(1, new TremblerServantRollGoal(this));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    // 滚动时能翻越更高的台阶（1.1 格），平时 0.6 格
    @Override
    public float maxUpStep() {
        return this.isRolling() ? 1.1F : 0.6F;
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        return new SmoothGroundPathNavigation(this, level);
    }

    @Override
    protected @NotNull BodyRotationControl createBodyControl() {
        return new TremblerServantBodyRotationControl(this);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ROLLING, false);
        this.entityData.define(ROLL_COOLDOWN, 60);   // 初始 60 tick 冷却，别一出生就滚
        this.entityData.define(STUNNED_TICKS, 0);
        this.entityData.define(TURBO, false);
    }

    // ===== 滚动 =====
    public boolean isRolling() {
        return this.entityData.get(ROLLING);
    }

    public void setRolling(boolean rolling) {
        this.entityData.set(ROLLING, rolling);
    }

    // ===== 滚动冷却 =====
    public int getRollCooldown() {
        return this.entityData.get(ROLL_COOLDOWN);
    }

    public void setRollCooldown(int cooldown) {
        this.entityData.set(ROLL_COOLDOWN, cooldown);
    }

    public void rollCooldown() {
        this.entityData.set(ROLL_COOLDOWN, 60);
    }

    // ===== 眩晕 =====
    public int getStunnedTicks() {
        return this.entityData.get(STUNNED_TICKS);
    }

    public void setStunnedTicks(int stunnedTicks) {
        this.entityData.set(STUNNED_TICKS, stunnedTicks);
    }

    public void stunnedTicks() {
        this.entityData.set(STUNNED_TICKS, 54);
    }

    // ===== 涡轮精英变体（EliteVariant 接口要求实现）=====
    @Override
    public boolean isElite() {
        return this.entityData.get(TURBO);
    }

    @Override
    public void setElite(boolean elite) {
        this.entityData.set(TURBO, elite);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.setupAnimationStates();
        }
        // 冷却只在"没晕 + 冷却没走完"时递减（眩晕期间冷却冻结，醒来了还要等）
        if (this.getStunnedTicks() <= 0 && this.getRollCooldown() > 0) {
            this.setRollCooldown(this.getRollCooldown() - 1);
        }
        // 眩晕中：关冲刺 + 倒计时 + 播眩晕粒子
        if (this.getStunnedTicks() > 0) {
            this.setSprinting(false);
            this.setStunnedTicks(this.getStunnedTicks() - 1);
            this.level().broadcastEntityEvent(this, (byte) 39);
        }
    }

    // 动画触发（客户端）：照原版，idle=没在滚，roll=在滚，stunned=在晕
    private void setupAnimationStates() {
        this.idleAnimationState.animateWhen(!this.isRolling(), this.tickCount);
        this.rollAnimationState.animateWhen(this.isRolling(), this.tickCount);
        this.stunnedAnimationState.animateWhen(this.getStunnedTicks() > 0, this.tickCount);
    }

    // 驱动腿部摆动动画的幅度（原版系数 16，比默认 4 更灵敏）
    @Override
    public void calculateEntityAnimation(boolean flying) {
        float f = (float) Mth.length(this.getX() - this.xo, this.getY() - this.yo, this.getZ() - this.zo);
        float f1 = Math.min(f * 16.0F, 1.0F);
        this.walkAnimation.update(f1, 0.4F);
    }

    // 眩晕粒子：六分之一概率在身体旁冒一圈星光
    private void stunEffect() {
        if (this.random.nextInt(6) == 0) {
            double d = this.getX() - (double) this.getBbWidth() * Math.sin((double) (this.yBodyRot * ((float) Math.PI / 180F))) + (this.random.nextDouble() * 0.6 - 0.3);
            double e = this.getY() + (double) this.getBbHeight() - 0.3;
            double f = this.getZ() + (double) this.getBbWidth() * Math.cos((double) (this.yBodyRot * ((float) Math.PI / 180F))) + (this.random.nextDouble() * 0.6 - 0.3);
            this.level().addParticle(ParticleTypes.ENTITY_EFFECT, d, e, f, 0.5D, 0.6D, 0.5D);
        }
    }

    // 撞到举盾的人：把自己撞晕 + 进冷却 + 停下，把防御者推开
    @Override
    protected void blockedByShield(LivingEntity defender) {
        this.stunnedTicks();
        this.rollCooldown();
        this.setRolling(false);
        this.getNavigation().stop();
        defender.push(this);
        defender.hurtMarked = true;
        super.blockedByShield(defender);
    }

    // 能打坏盾牌
    @Override
    public boolean canDisableShield() {
        return true;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 39) {
            this.stunEffect();
        }
        super.handleEntityEvent(id);
    }

    // 目标是否在可滚的高度范围（上下相差 < 3 格）
    public boolean isWithinYRange(LivingEntity target) {
        if (target == null) {
            return false;
        }
        return Math.abs(target.getY() - this.getY()) < 3.0D;
    }

    // 滚动时格挡大部分伤害（除非是能打穿滚动状态的伤害标签），播放格挡音效
    @Override
    public boolean hurt(@NotNull DamageSource damageSource, float amount) {
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        }
        if (!damageSource.is(OPDamageTypeTags.DAMAGES_ROLLING_TREMBLER) && this.isRolling()) {
            this.playSound(OPSoundEvents.TREMBLER_BLOCK.get(), 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
            return false;
        }
        return super.hurt(damageSource, amount);
    }

    // 滚动时免疫摔落伤害
    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, @NotNull DamageSource damageSource) {
        return !this.isRolling();
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return OPSoundEvents.TREMBLER_IDLE.get();
    }

    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return OPSoundEvents.TREMBLER_HURT.get();
    }

    protected @NotNull SoundEvent getDeathSound() {
        return OPSoundEvents.TREMBLER_DEATH.get();
    }

    // 脚步声：滚动时是滴水石块的摩擦声，平时是黏液滑行的声音
    @Override
    protected void playStepSound(@NotNull BlockPos pos, @NotNull BlockState state) {
        SoundType soundtype = state.getSoundType(this.level(), pos, this);
        if (this.isRolling()) {
            this.playSound(SoundEvents.DRIPSTONE_BLOCK_STEP, 0.4F, (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2F + 0.9F);
            this.playSound(soundtype.getStepSound(), 0.1F, soundtype.getPitch());
        } else {
            this.playSound(OPSoundEvents.SLUG_SLIDE.get(), 0.15F, 0.9F);
        }
    }

    static {
        ROLLING = SynchedEntityData.defineId(TremblerServant.class, EntityDataSerializers.BOOLEAN);
        ROLL_COOLDOWN = SynchedEntityData.defineId(TremblerServant.class, EntityDataSerializers.INT);
        STUNNED_TICKS = SynchedEntityData.defineId(TremblerServant.class, EntityDataSerializers.INT);
        TURBO = SynchedEntityData.defineId(TremblerServant.class, EntityDataSerializers.BOOLEAN);
    }

    // ===== 眩晕时冻结移动/转头/转身的三个控制器（照抄原版）=====
    private class TremblerServantMoveControl extends MoveControl {
        public TremblerServantMoveControl() {
            super(TremblerServant.this);
        }

        @Override
        public void tick() {
            if (TremblerServant.this.getStunnedTicks() <= 0) {
                super.tick();
            }
        }
    }

    private class TremblerServantLookControl extends LookControl {
        public TremblerServantLookControl(TremblerServant trembler) {
            super(trembler);
        }

        @Override
        public void tick() {
            if (TremblerServant.this.getStunnedTicks() <= 0) {
                super.tick();
            }
        }
    }

    private class TremblerServantBodyRotationControl extends BodyRotationControl {
        public TremblerServantBodyRotationControl(TremblerServant trembler) {
            super(trembler);
        }

        @Override
        public void clientTick() {
            if (TremblerServant.this.getStunnedTicks() <= 0) {
                super.clientTick();
            }
        }
    }
}
