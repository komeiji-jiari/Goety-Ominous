package com.qiuyue.goetyominous.common.entities.ally.of;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.ally.of.goals.UmberSpiderServantAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.of.goals.UmberSpiderServantFearLightGoal;
import com.qiuyue.goetyominous.common.entities.ally.of.goals.UmberSpiderServantLeapAtTargetGoal;
import com.qiuyue.goetyominous.common.entities.ally.of.goals.UmberSpiderServantRandomLookAroundGoal;
import com.qiuyue.goetyominous.common.entities.ally.of.goals.UmberSpiderServantRandomStrollGoal;
import com.unusualmodding.opposing_force.entity.utils.AttackState;
import com.unusualmodding.opposing_force.entity.utils.EliteVariant;
import com.unusualmodding.opposing_force.registry.OPMobEffects;
import com.unusualmodding.opposing_force.registry.OPSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;

/**
 * 阴影蜘蛛仆从：对照 OF 原版 UmberSpider 逐项移植。
 * 原版是"阴影"主题的蜘蛛怪，核心特色三件套：
 *  1) 怕光：周围块光照超过阈值(LIGHT_THRESHOLD=10)就会跑开；白天被太阳晒会着火 8 秒；
 *     精英(黑暗形态 TENEBROUS)免疫怕光逃跑，但白天依然会被晒。
 *  2) 阴郁毒素(GLOOM_TOXIN)：近战咬中按难度附加，普通难度 5 秒、困难 10 秒、精英再叠 1 级。
 *  3) 蜘蛛本能：继承原版蜘蛛的爬墙(WallClimberNavigation)、远距离跳扑扑脸。
 */
public class UmberSpiderServant extends Summoned implements AttackState, EliteVariant {
    // ===== 同步数据（字段名照抄 OF 原版 UmberSpider）=====
    private static final EntityDataAccessor<Integer> ATTACK_STATE;   // 攻击状态：0=待机 1=撕咬中
    private static final EntityDataAccessor<Boolean> ATTACKING;      // 是否正在攻击（驱动动画/跳扑衔接）
    public static final EntityDataAccessor<Integer> LIGHT_THRESHOLD; // 怕光阈值（默认 10）
    private static final EntityDataAccessor<Boolean> TENEBROUS;      // 黑暗精英形态（isElite 存这里）
    /** 爬墙标志（照抄原版 Spider 的 DATA_FLAGS_ID，bit0 = 是否贴墙）。 */
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID;

    // ===== 怕光逃跑字段（照抄原版）=====
    public int fleeLightFor;              // 逃跑倒计时（>0 时不会主动攻击）
    public Vec3 fleeFromPosition;         // 光的位置，蜘蛛要跑远的地方

    // ===== 动画：原版只有两个（爬行 + 待机）=====
    public final AnimationState idleAnimationState = new AnimationState();

    public UmberSpiderServant(EntityType<? extends Owned> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        // 逐项对齐 OF 原版 UmberSpider（继承原版蜘蛛的属性）：
        // 20 血 / 0.3 速 / 5 攻击力；FOLLOW_RANGE 原版继承 Spider 的默认 16，仆从跟随也需要
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // 主动索敌：Goety 默认的 SummonTargetGoal 是"仇恨驱动"，不会见敌就打。
        // 补上 NearestAttackableTargetGoal 才能像原版 UmberSpider 一样主动追敌对生物（Enemy）。
        // mustSee 必须是 true：Goety 的 FollowOwnerGoal.canUse() 要求 getTarget() == null，
        // 而 mustSee=false 时 TargetGoal.canContinueToUse() 恒为 true，会隔着墙永久锁定看不见的敌人，
        // 导致跟随 goal 永远启动不了。
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
                (target) -> target instanceof Enemy && !MobUtil.areAllies(this, target)));
        // 对齐原版优先级：1=怕光逃跑（最高），2=跳扑，3=撕咬攻击，5=怕光闲逛，6/7=观察环视
        // 0 号 FloatGoal 是原版 UmberSpider 就有的：蜘蛛掉进水里得先浮起来，
        // 少了它会沉底淹死。移植时漏了这一个。
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new UmberSpiderServantFearLightGoal(this));
        this.goalSelector.addGoal(2, new UmberSpiderServantLeapAtTargetGoal(this));
        this.goalSelector.addGoal(3, new UmberSpiderServantAttackGoal(this));
        // 游荡/环视排在 7 之后：Goety 的 FollowOwnerGoal 优先级是 5，
        // 而 Goal.canBeReplacedBy 允许「优先级数字更小」的 goal 抢占正在跑的 goal，
        // 所以数字一旦小于 5，仆从就会追到一半跑去闲逛、回不到主人身边。
        this.goalSelector.addGoal(7, new UmberSpiderServantRandomStrollGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(9, new UmberSpiderServantRandomLookAroundGoal(this));
    }

    // ===== 蜘蛛爬墙：用原版蜘蛛的贴墙导航 =====
    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        return new WallClimberNavigation(this, level);
    }

    // ===== 蜘蛛爬墙三件套（逐字照抄原版 Spider）=====
    // 为什么必须自己写：原版 UmberSpider 是 extends Spider，白送这三样；
    // 我们继承的是 Goety 的 Summoned，这个血统里根本没有蜘蛛的爬墙代码。
    // 而 WallClimberNavigation 只负责「在墙上也能算出路径」，真正让蜘蛛贴墙往上爬的
    // 是下面这套标志位 —— 少了它，路径点算得出来，但物理一贴墙就被重力拽下来，
    // 表现就是「阴影蜘蛛完全不爬墙」。
    public boolean isClimbing() {
        return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
    }

    public void setClimbing(boolean climbing) {
        byte flag = this.entityData.get(DATA_FLAGS_ID);
        if (climbing) {
            flag = (byte) (flag | 1);
        } else {
            flag = (byte) (flag & -2);
        }
        this.entityData.set(DATA_FLAGS_ID, flag);
    }

    /** 原版 Entity 的物理靠 onClimbable() 决定「贴墙时能不能垂直移动」。 */
    @Override
    public boolean onClimbable() {
        return this.isClimbing();
    }

    // ===== 节肢动物（照抄原版 Spider.getMobType）=====
    // 影响「节肢杀手」附魔对它的额外伤害，以及其它按生物类型判定的逻辑。
    @Override
    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    // ===== 蛛网不减速（照抄原版 Spider.makeStuckInBlock）=====
    @Override
    public void makeStuckInBlock(@NotNull BlockState state, @NotNull Vec3 motion) {
        if (!state.is(Blocks.COBWEB)) {
            super.makeStuckInBlock(state, motion);
        }
    }

    @Override
    protected float getStandingEyeHeight(@NotNull Pose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.65F;
    }

    // ===== AttackState 接口：攻击状态 =====
    @Override
    public int getAttackState() {
        return this.entityData.get(ATTACK_STATE);
    }

    @Override
    public void setAttackState(int attackState) {
        this.entityData.set(ATTACK_STATE, attackState);
    }

    // ===== AttackState 接口：是否正在攻击 =====
    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(ATTACKING, attacking);
    }

    // ===== 怕光阈值 =====
    public int getLightThreshold() {
        return this.entityData.get(LIGHT_THRESHOLD);
    }

    public void setLightThreshold(int lightThreshold) {
        this.entityData.set(LIGHT_THRESHOLD, lightThreshold);
    }

    // ===== EliteVariant 接口：黑暗精英形态（isElite 读的就是 TENEBROUS）=====
    @Override
    public boolean isElite() {
        return this.entityData.get(TENEBROUS);
    }

    @Override
    public void setElite(boolean elite) {
        this.entityData.set(TENEBROUS, elite);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACK_STATE, 0);
        this.entityData.define(ATTACKING, false);
        this.entityData.define(LIGHT_THRESHOLD, 10);
        this.entityData.define(TENEBROUS, false);
        this.entityData.define(DATA_FLAGS_ID, (byte) 0);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("AttackState", this.getAttackState());
        compoundTag.putBoolean("Attacking", this.isAttacking());
        compoundTag.putInt("LightThreshold", this.getLightThreshold());
        compoundTag.putBoolean("Tenebrous", this.isElite());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setAttackState(compoundTag.getInt("AttackState"));
        this.setAttacking(compoundTag.getBoolean("Attacking"));
        this.setLightThreshold(compoundTag.getInt("LightThreshold"));
        this.setElite(compoundTag.getBoolean("Tenebrous"));
    }

    @Override
    public void tick() {
        super.tick();
        // 照抄原版 Spider.tick()：服务端每 tick 把「是否有横向碰撞」同步成爬墙标志。
        if (!this.level().isClientSide) {
            this.setClimbing(this.horizontalCollision);
        }
        if (this.level().isClientSide) {
            this.setupAnimationStates();
        }
    }

    // ===== 客户端动画状态：只有待机动画（原版 setupAnimationStates）=====
    private void setupAnimationStates() {
        this.idleAnimationState.animateWhen(this.isAlive(), this.tickCount);
    }

    // ===== 免疫毒药与阴郁毒素（照抄原版 canBeAffected，走 Forge 事件）=====
    @Override
    public boolean canBeAffected(@NotNull MobEffectInstance effect) {
        if (effect.getEffect() == MobEffects.POISON || effect.getEffect() == OPMobEffects.GLOOM_TOXIN.get()) {
            MobEffectEvent.Applicable event = new MobEffectEvent.Applicable(this, effect);
            MinecraftForge.EVENT_BUS.post(event);
            return event.getResult() == Event.Result.ALLOW;
        }
        return super.canBeAffected(effect);
    }

    // ===== 怕太阳：白天被晒会着火 8 秒（同 Rambler，原版 aiStep 行为）=====
    @Override
    protected boolean isSunSensitive() {
        return true;
    }

    @Override
    public void aiStep() {
        if (this.isAlive()) {
            boolean flag = this.isSunSensitive() && this.isSunBurnTick();
            if (flag) {
                this.setSecondsOnFire(8);
            }
        }
        // ===== 怕光逃跑位置刷新（照抄原版 aiStep）：非精英才会跑 =====
        if (!this.isElite()) {
            BlockPos pos = this.blockPosition();
            BlockPos offset = pos.offset(this.getRandom().nextInt(20) - 10,
                    this.getRandom().nextInt(6) - 3,
                    this.getRandom().nextInt(20) - 10);
            if (this.level().getBrightness(LightLayer.BLOCK, this.blockPosition()) > this.getLightThreshold()
                    || this.isOnFire()) {
                this.fleeFromPosition = Vec3.atBottomCenterOf(offset);
            }
        }
        if (this.fleeLightFor > 0) {
            --this.fleeLightFor;
        }
        super.aiStep();
    }

    // ===== 咬人附带阴郁毒素（照抄原版 doHurtTarget）=====
    @Override
    public boolean doHurtTarget(@NotNull Entity target) {
        if (super.doHurtTarget(target)) {
            if (target instanceof LivingEntity living) {
                int duration = 0;
                if (this.level().getDifficulty() == Difficulty.NORMAL) {
                    duration = 5;
                } else if (this.level().getDifficulty() == Difficulty.HARD) {
                    duration = 10;
                }
                if (duration > 0) {
                    living.addEffect(new MobEffectInstance(OPMobEffects.GLOOM_TOXIN.get(), duration * 20,
                            this.isElite() ? 1 : 0), this);
                }
            }
            return true;
        }
        return false;
    }

    // ===== 音效（原版 3 个 + 蜘蛛脚步）=====
    @Override
    protected SoundEvent getAmbientSound() {
        return OPSoundEvents.UMBER_SPIDER_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return OPSoundEvents.UMBER_SPIDER_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return OPSoundEvents.UMBER_SPIDER_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockState) {
        this.playSound(SoundEvents.SPIDER_STEP, 0.1F, 0.8F);
    }

    @Override
    public int getAmbientSoundInterval() {
        return 180;
    }

    static {
        ATTACK_STATE = SynchedEntityData.defineId(UmberSpiderServant.class, EntityDataSerializers.INT);
        ATTACKING = SynchedEntityData.defineId(UmberSpiderServant.class, EntityDataSerializers.BOOLEAN);
        LIGHT_THRESHOLD = SynchedEntityData.defineId(UmberSpiderServant.class, EntityDataSerializers.INT);
        TENEBROUS = SynchedEntityData.defineId(UmberSpiderServant.class, EntityDataSerializers.BOOLEAN);
        DATA_FLAGS_ID = SynchedEntityData.defineId(UmberSpiderServant.class, EntityDataSerializers.BYTE);
    }
}
