package com.qiuyue.goetyominous.common.entities.ally.lm;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IMoveGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IStateGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.PossessedPaladinAlertedGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.PossessedPaladinAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.PossessedPaladinAttackMinGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.PossessedPaladinDoubleSlashSlamAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.PossessedPaladinFinisherAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.PossessedPaladinFlipSmashGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.PossessedPaladinJumpFallGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.PossessedPaladinJumpSmashComboGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.PossessedPaladinSecondPhaseGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.PossessedPaladinSideRollSpinGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.PossessedPaladinSlamAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.PossessedPaladinSlashFromGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.PossessedPaladinStabGrabGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.PossessedPaladinThrowDaggersGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.PossessedPaladinStateGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.SoulStrike;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.SoulTrident;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.ThrownPhantomDagger;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.miauczel.legendary_monsters.damagetype.ModDamageTypes;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.ThrownEntity.EntityThrownEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.miauczel.legendary_monsters.Particle.custom.Circle;
import net.miauczel.legendary_monsters.Particle.custom.SoulSweepParticle;
import net.miauczel.legendary_monsters.Particle.custom.SoulSweepRedParticle;
import net.miauczel.legendary_monsters.Particle.ModParticles;
import net.miauczel.legendary_monsters.config.ModConfig;
import net.miauczel.legendary_monsters.effect.ModEffects;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.AnimatedEntity.FallingSoulBladeEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.AnimatedEntity.SoulBladeEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.AnimatedEntity.SoulShieldEntity;
import net.miauczel.legendary_monsters.effect.DynamicCameraZoomEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Effect.CameraShakeEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.SoulPillarEntity;
// ⚠️ 这个 ModItems 是「LM 的」net.miauczel.legendary_monsters.item.ModItems，
//    不是我们自己的 com.qiuyue.goetyominous.common.items.ModItems。
//    两边类名一模一样，将来要是在本文件里引用我们自己的物品，得写全限定名。
import net.miauczel.legendary_monsters.item.ModItems;
import net.miauczel.legendary_monsters.sound.ModSounds;
import net.miauczel.legendary_monsters.util.EntityUtil;
import net.miauczel.legendary_monsters.util.MathUtils;
import net.miauczel.legendary_monsters.util.ParticleUtils;
import org.jetbrains.annotations.Nullable;

/**
 * 堕落圣骑仆从 —— 传奇怪物（Legendary Monsters）的 Possessed Paladin 移植版。
 *
 * <p>对应原版 PossessedPaladinEntity（原版 2990 行）。搬运分了几段：
 * <ul>
 *   <li><b>Stage 1</b>：只做「能站、能跟主人、能看目标」的骨架。一个攻击招式都没挂。</li>
 *   <li><b>Stage 2A</b>：铺 14 招共用的地基 —— 攻击状态机、40 个动画状态、冷却计时器、触发概率。</li>
 *   <li><b>Stage 2B ~ 2D</b>：逐个搬 14 个招式，最后是二阶段（变身演出 + 终结技）。<b>现已全部搬完。</b></li>
 * </ul>
 *
 * <p>原版有、但这里<b>故意不要</b>的东西：Boss 血条（LMBossInfoServer）、Boss 音乐、
 * 出生点传送回家、休眠、拆方块（原版的 {@code destroy()} 会把挡路的方块轰掉，
 * 仆从在主人家里放这招是灾难）。限伤、效果免疫、<b>伤害适应</b>都在父类
 * {@link IAnimatedBossServant} 里，别以为这里没搬。
 *
 * <p><b>脱战回血换了一种做法</b>：原版脱战 40 tick 之后每 15 tick 自回 20 点血，
 * 那是 BOSS 的续航；仆从版改成「主人拿诅咒金属块右键修，一次 30 点」，
 * 见 {@link #mobInteract}。
 */
public class PossessedPaladinServant extends IAnimatedBossServant {

    // ==================================================================
    //  一、攻击状态机（地基）
    // ==================================================================

    /**
     * 二阶段标记。1 = 一阶段，2 = 二阶段。
     *
     * <p>必须同步到客户端：客户端要靠它决定用哪张贴图、播哪个动画。
     */
    private static final EntityDataAccessor<Integer> PHASE =
            SynchedEntityData.defineId(PossessedPaladinServant.class, EntityDataSerializers.INT);

    /**
     * 是否已唤醒。原版用于「沉眠的圣骑」开场。
     *
     * <p>仆从版<b>当前没有消费者</b>：原版配套的状态 34（沉睡）/ 35（苏醒）没有搬过来
     * （原因见 {@link #hurt} 的注释），所以这个标志位一直是默认值 {@code true}。
     * 留着是为了和原版 {@code PossessedPaladinEntity} 的结构对齐 ——
     * 哪天要把沉睡演出接回来，开关是现成的。
     */
    private static final EntityDataAccessor<Boolean> AWAKENED =
            SynchedEntityData.defineId(PossessedPaladinServant.class, EntityDataSerializers.BOOLEAN);

    /** 一阶段/二阶段的贴图与行为切换阈值。 */
    public static final int SECOND_PHASE = 2;

    /**
     * 二阶段变身时，把一阶段的青绿色特效（圆环、灵魂爆、地面魂）整片换成红色用的三个分量。
     *
     * <p>原版 {@code PossessedPaladinEntity.uR/uG/uB}，构造里写死 {@code (1.0, 0.0, 0.0)}，纯红。
     * 全类里有 20 多处写成这个形状：
     * <pre>
     * this.getPhase() &gt;= 2 ? this.uR : 0.0F,
     * this.getPhase() &gt;= 2 ? this.uG : 0.9F,
     * this.getPhase() &gt;= 2 ? this.uB : 0.8F,
     * </pre>
     * 也就是「二阶段用 uR/uG/uB，一阶段用后面那三个写死的青绿色」。
     *
     * <p>为什么不做成常量、非要留三个字段？因为原版它们是 {@code public} 且<b>非 final</b> ——
     * 作者留了口子，方便别的 Boss 子类改色。这里保持同样的形状，方便以后复用。
     */
    public float uR = 1.0F;
    public float uG = 0.0F;
    public float uB = 0.0F;

    /**
     * 「血量裂纹等级」—— 圣骑的血量分档，二阶段的触发条件就是它。
     *
     * <p>⚠️ 这<b>不是</b>原版 Minecraft 的那个 {@code net.minecraft.world.entity.Crackiness}，
     * 是传奇怪物自己写在 {@code PossessedPaladinEntity} 里的<b>同名嵌套枚举</b>。
     * 两者阈值完全不同，别混：
     *
     * <pre>
     *              本枚举      原版 MC 的 Crackiness
     * NONE  (满血)  1.00        1.00
     * LOW           0.75        0.75
     * MEDIUM        0.65   ←    0.50
     * HIGH          0.30        0.25
     * </pre>
     *
     * <p>用哪一个很要紧：圣骑的变身线挂在 MEDIUM 上，用原版那个就变成 50% 血才变身，
     * 用这个才是原作的 <b>65%</b>。
     *
     * <p>{@link #byFraction} 的取法是「从低到高扫，第一个比当前血量比大的档位」，
     * 所以实际区间是：
     * <pre>
     * 血量比 &lt; 0.30 → HIGH
     *        &lt; 0.65 → MEDIUM   （变身线）
     *        &lt; 0.75 → LOW
     *        &lt; 1.00 → NONE
     * </pre>
     */
    public enum Crackiness {
        NONE(1.0F),
        LOW(0.75F),
        MEDIUM(0.65F),
        HIGH(0.3F);

        /** 从低到高排好的档位表。 */
        private static final java.util.List<Crackiness> BY_DAMAGE = java.util.Arrays.stream(values())
                .sorted(java.util.Comparator.comparingDouble(c -> c.fraction))
                .toList();

        public final float fraction;

        Crackiness(float fraction) {
            this.fraction = fraction;
        }

        /** 按「当前血量 / 最大血量」取档。满血以上落到最后，返回 NONE。 */
        public static Crackiness byFraction(float fraction) {
            for (Crackiness crackiness : BY_DAMAGE) {
                if (fraction < crackiness.fraction) {
                    return crackiness;
                }
            }
            return NONE;
        }
    }

    // ==================================================================
    //  二、每招一个冷却计时器
    //
    //  命名规律是成对出现：全大写的 final 常量存「冷却总时长」，
    //  小写的变量存「还剩多少 tick」，每 tick 在 tick() 里减 1，减到 0 就能再次发招。
    //  ==================================================================

    public final int PARRY_COOLDOWN;
    public int parry_cooldown;
    public final int THROW_COOLDOWN;
    public int throw_cooldown;
    public final int STAB_GRAB_COOLDOWN;
    public int stab_grab_cooldown;
    public final int SLAM_COOLDOWN;
    public int slam_cooldown;
    public final int FLIP_SMASH_COOLDOWN;
    public int flip_smash_cooldown;
    public final int SIDE_ROLL_SPIN_COOLDOWN;
    public int side_roll_spin_cooldown;
    public final int BACKSTEP_COOLDOWN;
    public int backstep_cooldown;
    public final int SLASH_FROM_COOLDOWN;
    public int slash_from_cooldown;
    public final int DOUBLE_SLASH_COOLDOWN;
    public int double_slash_cooldown;
    public final int JUMP_COOLDOWN;
    public int jump_cooldown;
    public final int SHIELD_SMASH_COOLDOWN;
    public int shield_smash_cooldown;
    public final int FINISHER_COOLDOWN;
    public int finisher_cooldown;
    public final int TRIDENT_THROW_SPIN;
    public int trident_throw_spin;

    // ==================================================================
    //  二·五、随机数发生器
    //
    //  原版声明的是 public Random random1（java.util.Random）。这里换成 Minecraft 的
    //  RandomSource —— 行为等价，而且是本项目其余地方的统一写法，顺便省掉一个 java.util 的 import。
    //
    //  目前只有 randomizedSoulStrike 用它（砸地第二下的灵魂尖柱要在周围随机挑落点），
    //  后面的招式如果也需要随机落点，共用这一个就好。
    //  ==================================================================

    public final RandomSource random1 = RandomSource.create();

    // ==================================================================
    //  二·六、物品淡入淡出计时器（给 Stage 2D 的渲染图层用）
    //
    //  圣骑手里会「凭空出现」几样东西 —— 盾牌（招 25）、三叉戟（招 38）。
    //  这两个计时器负责它们什么时候显形、什么时候消失，渲染器每帧读一次决定画多透明。
    //  数值 (10 / 15) 照抄原版构造函数。
    //
    //  推进它们的永远是招式代码（UpdateWithAttack），读它们的永远是渲染端，
    //  两边互不相干 —— 计时器本身不参与任何游戏逻辑判定。
    //  ==================================================================

    /**
     * 手里那件「幽灵物品」（盾牌 / 三叉戟）的显隐。
     *
     * <p>读它的是三叉戟图层（{@code PossessedPaladinServantTridentLayer}）。
     * 盾牌图层还没做，所以招 25 虽然在推这个数，目前看不到东西。
     */
    public final ControlledAnim ghostItemFade = new ControlledAnim(10);

    /**
     * 地面预警光圈的显隐。
     *
     * <p>读它的是渲染器里的 {@code renderTelegraph}。状态 32（二阶段跳砸）
     * 和状态 38（掷三叉戟）都会推它。
     */
    public final ControlledAnim telegraphFadeAway = new ControlledAnim(15);

    // ==================================================================
    //  二·七、死亡演出的「灵魂射线」计数器（状态 36 用）
    //  ==================================================================

    /**
     * 已经该画几束灵魂射线。0 = 不画，1 = 一束，以此类推。
     *
     * <p>这不是计时器，而是个<b>只许往上加的计数器</b>：死亡演出的第 180 / 240 /
     * 250 / 260 tick 各加 1，第 1 tick 清零，中间没有任何代码把它降下来。
     * 所以射线是「越聚越多」，不会退回去。
     *
     * <p>它由实体每 tick 推进，由渲染器每帧读 —— 渲染器拿它当循环次数，
     * 画 {@code rayAmount} 条从身体中心朝四面八方射出去的三角光柱。
     * 每一条的朝向都是当场随机算的，所以<b>每帧都在抖</b>，看起来才像电光在乱窜。
     *
     * <p>⚠️ 名字叫「amount」但类型是 int —— 原版如此，照抄。
     * 渲染时会被转成 float 传进去（那边形参叫 deathProgress，有点名不副实：
     * 它其实是「已经射出去几条」，不是 0~1 的进度）。
     *
     * <p>⚠️ 状态 36 的 goal 时限是 240 tick，所以实际上只有第 180 tick 那一次
     * 加得进去，240 / 250 / 260 三次永远跑不到（原因见状态 36 分支开头的长注释）
     * —— 也就是说游戏里最多只会看到 1 条射线。这是原版的锅，不是漏搬。
     */
    public int rayAmount;

    // ==================================================================
    //  三、每招的触发概率
    //
    //  这些数在整个生命周期里<b>只赋值一次、永不改变</b>（原版就是这么写的）。
    //  goal 里的用法是 nextFloat() * 100 < 权重，所以数值含义是
    //  「每 tick 有百分之几的概率发动这一招」—— 不是权重比例。
    //  ==================================================================

    // 这几个必须是 public：原版实体和 goal 在同一个包里，包级私有就够用了；
    // 本项目的 goal 放在 ...llm.goals 子包里，包级私有会读不到。
    public int SideRollSpinRandom;
    public int FinisherRandom;
    public int DoubleSlashRandom;
    public int SlashFromRandom;
    public int ShieldSmashRandom;
    public int JumpRandom;
    public int backflipRandom;
    public int throwRandom;
    public int slamRandom;
    public int flipSmashRandom;
    public int throwTridentSpinRandom;

    // ==================================================================
    //  四、动画状态
    //
    //  原版 AnimationState（net.minecraft.world.entity.AnimationState，是原版类不是自研的）。
    //  每招一个，由 onSyncedDataUpdated() 在攻击状态变化时 startIfStopped，
    //  再由模型 setupAnim() 通过 getAnimationState("名字") 取出来播放。
    //
    //  字段多但都是哑数据，照抄即可。Stage 2A 先把 40 个全建好，
    //  否则 onSyncedDataUpdated 的 switch 没法完整搬过来。
    //  ==================================================================

    public AnimationState idleAnimationState;
    public AnimationState DoubleSlashAnimationState;
    public AnimationState DoubleSlashEndAnimationState;
    public AnimationState DoubleSlashSlamEndAnimationState;
    public AnimationState ParryAnimationState;
    public AnimationState deathAnimationState;
    public AnimationState SwordSlamAnimationState;
    public AnimationState SwordSlamEndAnimationState;
    public AnimationState SwordSlamCounterEndAnimationState;
    public AnimationState SwordSlamCounterReleaseAnimationState;
    public AnimationState AllertedAnimationState;
    public AnimationState BackflipAnimationState;
    public AnimationState BackflipEndAnimationState;
    public AnimationState BackflipDoubleAnimationState;
    public AnimationState FlyAwaySlashAnimationState;
    public AnimationState FlipSmashAnimationState;
    public AnimationState FlipSmashEndAnimationState;
    public AnimationState FlipSmashFlipAnimationState;
    public AnimationState SlashFromAnimationState;
    public AnimationState SlashFromEndAnimationState;
    public AnimationState SlashFromStabAnimationState;
    public AnimationState SlashFromStabGrabAnimationState;
    public AnimationState SlashFromStabGrabFailAnimationState;
    public AnimationState SlashFromStabGrabStabFailAnimationState;
    public AnimationState SlashFromStabGrabSuccessAnimationState;
    public AnimationState ThrowAnimationState;
    public AnimationState ThrowDoubleAnimationState;
    public AnimationState JumpPreAnimationState;
    public AnimationState JumpFallAnimationState;
    public AnimationState JumpSmashAnimationState;
    public AnimationState JumpSmashComboAnimationState;
    public AnimationState ShieldSmashAnimationState;
    public AnimationState SecondPhaseAnimationState;
    public AnimationState SideRollSpinAnimationState;
    public AnimationState LeftSideRollSpinAnimationState;
    public AnimationState SleepAnimationState;
    public AnimationState AwakenAnimationState;
    public AnimationState DeathAnimationState;
    public AnimationState FinisherAnimationState;
    public AnimationState TridentThrowSpinAnimationState;

    // ==================================================================
    //  五、招式变体与临时标记
    // ==================================================================

    /** 二连斩的两种收招变体（1 = 普通收招，2 = 接砸地）。每轮招式结束后重掷。 */
    public int DoubleSlashType;
    /** 侧滚旋转的两个方向（1 = 右，2 = 左）。每轮招式结束后重掷。 */
    public int SideRollSpinType;
    /** 本轮的格挡是否成功反击过。 */
    public boolean hasParried;
    /** 突刺抓取是否成功抓到了目标。 */
    public boolean succedGrabbing;

    /**
     * 「这一条命是被主人处决掉的」。
     *
     * <p>处决指的是玩家<b>蹲下 + 拿法杖右键自己的仆从</b>（Goety 的 {@code Summoned.tryKill}）。
     * 它和「被敌人打死」在代码里本来是同一件事 —— 都是走 {@code hurt()} 打到血量为 0 ——
     * 但这个标记把它们分开了，好让 {@link #hurt} 里那条<b>跳过全部四层免伤</b>生效：
     * 格挡、无敌帧、变身无敌本来是用来扛敌人的，如果连主人自己都挡，
     * 仆从就永远杀不掉了（主人会看到「处决没反应」）。
     *
     * <p>⚠️ 只在 {@link #tryKill} 里<b>临时</b>置上、调用一结束就清掉，
     * 不能常驻 —— 它是「这一次伤害」的通行证，不是一个长期状态。
     *
     * <p>⚠️ 它<b>只在这</b>一次调用期间有意义，死亡演出要跑 14 秒，它在 {@code tryKill()}
     * 返回时就被清掉了。所以<b>没有任何「事后还想知道这条命是不是处决掉的」的需求</b>
     * —— 曾经为此养过一个同步字段，后来发现两种死法的表现已经完全一致（见 {@link #die}），
     * 就删掉了。
     */
    public boolean executedByOwner;

    /**
     * 我们自己的死亡计时器，单位 tick，从 {@link #die} 起数到 280（14 秒）就把尸体删掉。
     *
     * <p>明明有原版的 {@code deathTime} 可以用，为什么还要多养一个？因为 {@code deathTime}
     * 被<b>永远摁死在 0</b>（原因见 {@link #tickDeath()}），它已经数不到 280 了。
     * 两个计数器分工：
     *
     * <pre>
     * deathTicks  只管「什么时候删尸体」。正常递增到 280。
     * deathTime   只管「要不要糊红光 + 要不要侧倒」—— 那是原版渲染器读的。恒为 0。
     * </pre>
     */
    public int deathTicks;

    /**
     * 「无敌帧」还剩多少 tick。原版 {@code PossessedPaladinEntity.BossInvulnerabilityTime}。
     *
     * <p>和格挡是<b>两回事</b>，别搞混：
     * <ul>
     *   <li><b>格挡</b>（{@link #hasParried} + 状态 5）：圣骑主动举盾，把这一下<b>完全挡掉</b>，
     *       还会顺势反击。有冷却（{@link #parry_cooldown}），而且只在待机 / 警觉时能触发。</li>
     *   <li><b>无敌帧</b>（本字段）：<b>挨打之后</b>给的一小段喘息，单纯让接下来 10 tick 的伤害归零。
     *       没有冷却，也不需要摆姿势，纯粹是防止「一秒被砍十几刀」。</li>
     * </ul>
     *
     * <p>它只负责「挡伤害」，<b>不会</b>重置 {@code hurtTime} / 播放受击动画，
     * 所以视觉上还会正常闪红。
     *
     * <p>⚠️ 原版它在 {@code customServerAiStep()} 里递减。我们没有那个方法，
     * 递减放在 {@link #tick()} 里，和其余十几个冷却排在一起。
     */
    public int BossInvulnerabilityTime;

    /** 一次成功的受击之后，无敌帧持续多少 tick。原版写死在 {@code hurt()} 里的 10。 */
    public static final int BOSS_INVULNERABILITY_TICKS = 10;
    /**
     * 处决（状态 20）里已经打出了几轮「灵魂冲击」。
     *
     * <p>原版拿它计数，但实际只做两件事：招式开头和中间各清零一次，中间 {@code ++} 三次。
     * <b>没有任何一处读取它的值</b> —— 真正的效果由那三个时间点上的伤害和粒子产生。
     * 保留是为了跟原版对齐（也方便以后想做「第几轮」的差异表现时直接用）。
     */
    public int soulRaysCount;

    // ==================================================================
    //  六、招式临时状态
    // ==================================================================

    /** 本次攻击有没有打中过人。砸地等招式靠它决定收招之后接哪一招。 */
    public boolean shouldAttackMore;

    /**
     * 「这一招有没有打到人」的另一个标记，和 {@link #shouldAttackMore} <b>不是一回事</b>，
     * 别互抄：
     * <ul>
     *   <li>{@code shouldAttackMore} 由砸地第二下（状态 8）的伤害分支置上；</li>
     *   <li>{@code hasHurt} 由<b>剑气斩</b>（状态 16）的伤害分支置上
     *       —— 它走的是 {@code GambitedSideAreaAttack}，那个重载里才写了这一句。</li>
     * </ul>
     * 两者的共同点是：都在招式起手时被清掉。{@code hasHurt} 是被
     * {@code PossessedPaladinSlashFromGoal.stop()} 清掉的，而且<b>清在读它之后</b> ——
     * 顺序反了就等于永远读到 false。
     */
    public boolean hasHurt;

    /** 上一次出招时目标所在的坐标。原版用它让「打空之后」的后续动作有地方可去。 */
    public double lastTargetX;
    public double lastTargetY;
    public double lastTargetZ;

    // ==================================================================
    //  七、构造与初始化
    // ==================================================================

    public PossessedPaladinServant(EntityType<? extends IAnimatedBossServant> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.xpReward = 100;
        // 原版是 2.0F。圣骑的招式里有跳劈、翻跟头这类位移，步子小了会卡住，先照抄。
        this.setMaxUpStep(2.0F);
        this.setPersistenceRequired();

        // 冷却时长。原版用 MathUtils.toTicks(秒) 换算，这里是 (int)(秒 × 20)，
        // 结果一样，但少一个对外部类的依赖。
        this.PARRY_COOLDOWN = toTicks(5.0F);
        this.parry_cooldown = this.PARRY_COOLDOWN;
        this.THROW_COOLDOWN = toTicks(5.0F);
        this.throw_cooldown = 0;
        this.STAB_GRAB_COOLDOWN = toTicks(10.0F);
        this.stab_grab_cooldown = 0;
        this.SLAM_COOLDOWN = toTicks(6.0F);
        this.slam_cooldown = 0;
        this.FLIP_SMASH_COOLDOWN = toTicks(4.0F);
        this.flip_smash_cooldown = 0;
        this.SIDE_ROLL_SPIN_COOLDOWN = toTicks(4.0F);
        this.side_roll_spin_cooldown = 0;
        this.BACKSTEP_COOLDOWN = toTicks(2.0F);
        this.backstep_cooldown = 0;
        this.SLASH_FROM_COOLDOWN = toTicks(1.0F);
        this.slash_from_cooldown = 0;
        this.DOUBLE_SLASH_COOLDOWN = toTicks(3.0F);
        this.double_slash_cooldown = 0;
        this.JUMP_COOLDOWN = toTicks(6.0F);
        this.jump_cooldown = 0;
        this.SHIELD_SMASH_COOLDOWN = toTicks(6.0F);
        this.shield_smash_cooldown = 0;
        this.FINISHER_COOLDOWN = toTicks(16.0F);
        this.finisher_cooldown = 0;
        this.TRIDENT_THROW_SPIN = toTicks(8.0F);
        this.trident_throw_spin = 0;

        // 触发概率（见上方说明：这是「每 tick 的百分比」，不是权重）。
        this.SideRollSpinRandom = 16;
        this.FinisherRandom = 25;
        this.DoubleSlashRandom = 10;
        this.SlashFromRandom = 15;
        this.ShieldSmashRandom = 15;
        this.JumpRandom = 10;
        this.backflipRandom = 10;
        this.throwRandom = 10;
        this.slamRandom = 10;
        this.flipSmashRandom = 10;
        this.throwTridentSpinRandom = 15;

        // 40 个动画状态，逐个 new。少 new 一个就会在播放时 NPE。
        this.idleAnimationState = new AnimationState();
        this.DoubleSlashAnimationState = new AnimationState();
        this.DoubleSlashEndAnimationState = new AnimationState();
        this.DoubleSlashSlamEndAnimationState = new AnimationState();
        this.ParryAnimationState = new AnimationState();
        this.deathAnimationState = new AnimationState();
        this.SwordSlamAnimationState = new AnimationState();
        this.SwordSlamEndAnimationState = new AnimationState();
        this.SwordSlamCounterEndAnimationState = new AnimationState();
        this.SwordSlamCounterReleaseAnimationState = new AnimationState();
        this.AllertedAnimationState = new AnimationState();
        this.BackflipAnimationState = new AnimationState();
        this.BackflipEndAnimationState = new AnimationState();
        this.BackflipDoubleAnimationState = new AnimationState();
        this.FlyAwaySlashAnimationState = new AnimationState();
        this.FlipSmashAnimationState = new AnimationState();
        this.FlipSmashEndAnimationState = new AnimationState();
        this.FlipSmashFlipAnimationState = new AnimationState();
        this.SlashFromAnimationState = new AnimationState();
        this.SlashFromEndAnimationState = new AnimationState();
        this.SlashFromStabAnimationState = new AnimationState();
        this.SlashFromStabGrabAnimationState = new AnimationState();
        this.SlashFromStabGrabFailAnimationState = new AnimationState();
        this.SlashFromStabGrabStabFailAnimationState = new AnimationState();
        this.SlashFromStabGrabSuccessAnimationState = new AnimationState();
        this.ThrowAnimationState = new AnimationState();
        this.ThrowDoubleAnimationState = new AnimationState();
        this.JumpPreAnimationState = new AnimationState();
        this.JumpFallAnimationState = new AnimationState();
        this.JumpSmashAnimationState = new AnimationState();
        this.JumpSmashComboAnimationState = new AnimationState();
        this.ShieldSmashAnimationState = new AnimationState();
        this.SecondPhaseAnimationState = new AnimationState();
        this.SideRollSpinAnimationState = new AnimationState();
        this.LeftSideRollSpinAnimationState = new AnimationState();
        this.SleepAnimationState = new AnimationState();
        this.AwakenAnimationState = new AnimationState();
        this.DeathAnimationState = new AnimationState();
        this.FinisherAnimationState = new AnimationState();
        this.TridentThrowSpinAnimationState = new AnimationState();

        this.DoubleSlashType = 1;
        this.SideRollSpinType = 1;
        this.hasParried = false;
        this.succedGrabbing = false;
        this.hasHurt = false;
        this.shouldAttackMore = false;
    }

    /** 秒 → tick。原版 MathUtils.toTicks 的等价实现。 */
    private static int toTicks(float seconds) {
        return (int) (seconds * 20.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.PossessedPaladinServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.PossessedPaladinServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.PossessedPaladinServantArmorToughness.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.PossessedPaladinServantDamage.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.PossessedPaladinServantMovementSpeed.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.PossessedPaladinServantFollowRange.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.PossessedPaladinServantKnockbackResistance.get())
                .add(Attributes.ATTACK_KNOCKBACK, AttributesConfig.PossessedPaladinServantAttackKnockback.get());
    }

    /**
     * 注意：这个方法在整个仓库里<b>没有任何地方调用</b>（我搜过了）。
     * 留着纯粹是为了跟蔓生巨像仆从等 30 多个同类文件保持一致——
     * 万一将来接上「游戏内改配置热重载」的钩子，这里就是入口。
     */
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.PossessedPaladinServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.PossessedPaladinServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.PossessedPaladinServantDamage.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PHASE, 1);
        this.entityData.define(AWAKENED, true);
    }

    /**
     * 存档。<b>只存「阶段」这一个数</b>，和原版一致（原版存 {@code phase} + {@code is_Sleep}，
     * 我们没搬沉睡系统，所以只剩 phase）。
     *
     * <p>⚠️ 为什么这条不能省：{@code PHASE} 是 {@code SynchedEntityData}，
     * 它<b>只负责「服务端改了 → 同步给客户端」，不会自己进存档</b>。
     * 不写这个方法的话，二阶段的圣骑一读档就会掉回一阶段，
     * 然后因为 {@link #shouldEnterSecondPhase()} 只看血量（血量是原版实体自己存的，
     * 读档后还是低血）→ 立刻又满足变身条件 → <b>原地重播一遍 7.35 秒的变身动画</b>，
     * 而且状态 26 期间是完全免伤的（见 {@code hurt} 的第一层）。
     * 玩家看到的现象就是「每次读档圣骑都要再吼一声、再无敌七秒」，而且明明二阶段的盔甲会先变回一阶段。
     *
     * <p>键名沿用原版的 {@code "phase"}，这样原版的世界存档和我们的仆从存档能互认。
     */
    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("phase", this.getPhase());
    }

    /**
     * 读档。见 {@link #addAdditionalSaveData} 说明为什么必须存阶段。
     *
     * <p>⚠️ 顺序别动：{@code super} 那一句必须先跑（Goety 的 {@code Summoned}
     * 在这里读主人 UUID、命令状态等一堆东西，我们只是往后面追加一个自己的键）。
     *
     * <p>另外注意这里<b>没有</b> read 那三个字段之外的任何东西：
     * {@code AWAKENED} 原版也不存（见上），读档后取默认值即可。
     * {@code ATTACK_STATE} 同理不存 —— 原版也这样，读档就回待机，不会接着挥到一半的刀。
     */
    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        // ⚠️ 那个 contains 判断是<b>我们加的</b>，原版直接 getInt。
        //
        // 原因：{@code CompoundTag.getInt(缺失的键)} 返回的是 <b>0</b>，不是 1。
        // 本次改动之前召唤出来的圣骑，存档里根本没有 "phase" 这个键，
        // 一读档阶段就变成 0。当前所有判断都是「>= 2 / < 2 / > 1」，
        // 0 和一阶段完全等价、暂时不会出问题 —— 但这等于埋了个「阶段是 0」的雷：
        // 哪天有人写一句 {@code getPhase() == 1}，老档的圣骑就会静默走错分支。
        // 所以这里补一句，键不在就按一阶段算。
        //
        // （顺带一提：原版也有同样的毛病，只是它只影响「升级 mod 之前的老世界」，
        //   而我们的仆从是跟着 mod 版本走的，覆盖面大得多。）
        this.setPhase(pCompound.contains("phase") ? pCompound.getInt("phase") : 1);
    }

    // ==================================================================
    //  七、二阶段开关
    // ==================================================================

    /**
     * 骑在身上的那个实体<b>不摆骑乘姿势</b>（原版 PossessedPaladinEntity.java:1249）。
     *
     * <p>原版 {@code Entity.shouldRiderSit()} 默认返回 true —— 被骑的实体会被强行摆成
     * 「骑在鞍上」的坐姿。但圣骑「抓取」抓的是敌人，是<b>被拎在半空中</b>的，
     * 摆出骑乘坐姿会很出戏。所以原版覆写成 false。
     *
     * <p>这个方法只在「突刺抓取」成功、受害者被 {@code startRiding} 挂上来之后才起作用，
     * 平时没有乘客、也就没有影响。
     */
    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    /**
     * 被抓取的受害者<b>摆在哪儿</b>（原版 PossessedPaladinEntity.java:2687）。
     *
     * <p>⚠️ 这个覆写和上面那个 {@link #shouldRiderSit()} 是<b>配套的一对</b>，
     * 只做一半等于没做：
     * <ul>
     *   <li>{@code shouldRiderSit() = false} 管的是<b>姿势</b>（别摆成骑马坐姿）；</li>
     *   <li>本方法管的是<b>位置</b>。不覆写的话走 {@code Entity.positionRider} 的默认实现
     *       ——「乘客摆在骑乘者正上方再抬一个 {@code getPassengersRidingOffset()}
     *       （{@code LivingEntity} 实现是 {@code 身高 × 0.75}）」。圣骑是个大个子，
     *       于是被抓的敌人会被顶在它<b>头顶上方</b>飘着，而不是被举在剑尖前面。</li>
     * </ul>
     *
     * <p>原版的算法：拿身体朝向 {@code yBodyRot} 算出「正前方」，把乘客放到
     * <b>身前 1 格、抬高 1 格</b>的地方 —— 读起来就像被圣骑单手拎在半空。
     *
     * <p>⚠️ 那几个变量的怪写法（{@code theta} 先转成弧度、<b>再 {@code ++} 加 1</b>，
     * 然后只拿 {@code cos/sin} 出来的 {@code vecX/vecZ} 乘 {@code vec}）是原版原样照抄的。
     * 那两行 {@code f} / {@code f1} 和 {@code offset} 相乘的结果恒为 0
     * （{@code offset} 写死 0.0F），所以其实是<b>白算的</b>；后面那个 {@code ++theta}
     * 也纯属多余（{@code vecX/vecZ} 本来就能直接用 {@code f}/{@code f1}）。
     * 留着是因为：一是照抄最不容易抄错，二是万一哪天要按原版微调站位，数值对得上。
     * <b>别顺手"化简"</b> —— 化简要动浮点运算顺序，站位会有亚像素级偏移。
     *
     * <p>只在「突刺抓取」（状态 19/20/21/33）真的把敌人 {@code startRiding} 挂上来之后才有影响；
     * 平时没有乘客，本方法什么都不做。
     */
    @Override
    protected void positionRider(Entity pPassenger, Entity.MoveFunction pCallback) {
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180F));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180F));
        double theta = (double) this.yBodyRot * (Math.PI / 180D);
        ++theta;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        float vec = 1.0F;
        float offset = 0.0F;
        if (this.hasPassenger(pPassenger)) {
            pCallback.accept(pPassenger,
                    this.getX() + (double) vec * vecX + (double) (f * offset),
                    this.getY() + (double) 1.0F,
                    this.getZ() + (double) vec * vecZ + (double) (f1 * offset));
        }
    }

    public void setPhase(int phase) {
        this.entityData.set(PHASE, phase);
    }

    public int getPhase() {
        return this.entityData.get(PHASE);
    }

    /** 是不是二阶段。多处 goal 用它做门槛（比如掷三叉戟、终结技只有二阶段才会用）。 */
    public boolean getIsSecondPhase() {
        return this.getPhase() >= SECOND_PHASE;
    }

    /** 当前血量落在哪一档。原版 {@code PossessedPaladinEntity.getCrackiness()}。 */
    public Crackiness getCrackiness() {
        return Crackiness.byFraction(this.getHealth() / this.getMaxHealth());
    }

    /**
     * 该不该变身二阶段了。原版把这条判据直接塞在 {@code P_PSecondPhaseStateGoal.canUse()} 里，
     * 这里提出来给它单独一个名字，顺便让别的地方（比如调试、日志）也能复用。
     *
     * <p>两个条件：
     * <ol>
     *   <li>{@code getPhase() <= 1} —— 已经变过身就不再变第二次。
     *       用 {@code <=} 而不是 {@code ==} 是原版写法，对 {@code /data} 改出来的
     *       越界值也安全；</li>
     *   <li>血量档位是 MEDIUM 或 HIGH，也就是<b>血量低于 65%</b>（见 {@link Crackiness}）。</li>
     * </ol>
     */
    public boolean shouldEnterSecondPhase() {
        if (this.getPhase() > 1) {
            return false;
        }
        Crackiness crackiness = this.getCrackiness();
        return crackiness == Crackiness.MEDIUM || crackiness == Crackiness.HIGH;
    }

    public boolean getIsAwakened() {
        return this.entityData.get(AWAKENED);
    }

    public void setAwakened(boolean awakened) {
        this.entityData.set(AWAKENED, awakened);
    }

    /**
     * 是不是「睡着」的状态 —— 原版指沉睡（34）和苏醒中（35）两个。
     *
     * <p>⚠️ 仆从版<b>当前进不去这两个状态</b>：原版那套沉睡演出没有搬过来
     * （原因见 {@link #hurt} 的注释），所以这个方法现在恒为 {@code false}。
     * 留着是为了和原版 {@code PossessedPaladinEntity} 的结构对齐，不是当前的功能开关。
     *
     * <p>「待命时该不该有保护」判的是 {@link #isStaying()}，别拿这个当开关。
     */
    public boolean isSleep() {
        int state = this.getAttackState();
        return state == 34 || state == 35;
    }

    /**
     * 待命的时候，把递过来的目标<b>原路退回</b> —— 不主动锁敌。
     *
     * <h2>为什么必须覆写这个，光在 tick 里 setTarget(null) 是不够的</h2>
     * Goety 的 {@code Summoned} 在 targetSelector 里挂了一个 {@code SummonTargetGoal}，
     * 它<b>每 tick 都在找人打</b>，找到就直接调 {@code setTarget(...)}。
     * 所以「待命时把目标清空」这种写法只能清掉一瞬间 ——
     * 下一 tick 它自己又选一个回来，然后攻击 goal 看目标不为空就出招，
     * 坐着待命的圣骑照样会冲出去打架。
     *
     * <p>把闸设在这里，等于把所有「主动选目标」的路径一并堵死 ——
     * 它们的终点都是这个方法。
     *
     * <h2>⚠️ 只拦「主动选」，绝不拦「还手」</h2>
     * 判断依据是 {@link #getLastHurtByMob()}，规则就一条：<b>谁打我，我就只打谁</b>。
     * <ul>
     *   <li>{@code SummonTargetGoal} 随便挑了个路过的怪 → 不是打我的人 → <b>拦下</b>；</li>
     *   <li>{@code HurtByTargetGoal} 把打我的人递进来 → 正好是 {@code getLastHurtByMob()} → <b>放行</b>。</li>
     * </ul>
     * 这条口子必须留着。否则待命中的圣骑挨了打不还手，而它血再厚也只是站着挨揍 ——
     * 玩家会看到「我的仆从被野怪活活打死，全程一点反应没有」，那比乱打还糟。
     * 「待命」的语义是<b>不主动惹事</b>，不是挨打不还手。
     *
     * <h2>⚠️ 只拦「塞新目标」，绝不拦「清空」</h2>
     * 传进来的 {@code pTarget} 是 null 时必须放行。否则一旦切到待命，
     * 之前锁定的目标就再也清不掉了，反而更糟。
     */
    @Override
    public void setTarget(@Nullable LivingEntity pTarget) {
        if (pTarget != null && this.isStaying() && pTarget != this.getLastHurtByMob()) {
            return;
        }
        super.setTarget(pTarget);
    }

    // ==================================================================
    //  八、攻击状态 → 动画 的映射
    // ==================================================================

    /**
     * 按名字取动画状态，供模型 {@code setupAnim()} 调用。
     *
     * <p>原版这里写的是 {@code input == "idle"} 这种<b>字符串引用比较</b>，
     * 能跑是因为字面量在常量池里被 intern 过。这里统一改成 {@code .equals()}：
     * 行为完全一样，但不会因为哪天有人传了个拼出来的字符串就静默失效。
     *
     * <p>找不到就返回一个<b>新建的空</b> AnimationState（原版也是这么干的）。
     * 虽然每次调用都 new 一个对象有点浪费，但这条路径在正常游戏里走不到。
     */
    public AnimationState getAnimationState(String input) {
        if (input.equals("idle")) {
            return this.idleAnimationState;
        } else if (input.equals("allerted")) {
            return this.AllertedAnimationState;
        } else if (input.equals("double_slash")) {
            return this.DoubleSlashAnimationState;
        } else if (input.equals("double_slash_end")) {
            return this.DoubleSlashEndAnimationState;
        } else if (input.equals("double_slash_slam_end")) {
            return this.DoubleSlashSlamEndAnimationState;
        } else if (input.equals("parry")) {
            return this.ParryAnimationState;
        } else if (input.equals("sword_slam_cut")) {
            return this.SwordSlamAnimationState;
        } else if (input.equals("sword_slam_end")) {
            return this.SwordSlamEndAnimationState;
        } else if (input.equals("sword_slam_counter_end")) {
            return this.SwordSlamCounterEndAnimationState;
        } else if (input.equals("sword_slam_counter_release")) {
            return this.SwordSlamCounterReleaseAnimationState;
        } else if (input.equals("backflip")) {
            return this.BackflipAnimationState;
        } else if (input.equals("fly_away_slash")) {
            return this.FlyAwaySlashAnimationState;
        } else if (input.equals("flip_smash")) {
            return this.FlipSmashAnimationState;
        } else if (input.equals("flip_smash_end")) {
            return this.FlipSmashEndAnimationState;
        } else if (input.equals("flip_smash_flip")) {
            return this.FlipSmashFlipAnimationState;
        } else if (input.equals("throw")) {
            return this.ThrowAnimationState;
        } else if (input.equals("throw_double")) {
            return this.ThrowDoubleAnimationState;
        } else if (input.equals("slash_from")) {
            return this.SlashFromAnimationState;
        } else if (input.equals("slash_from_end")) {
            return this.SlashFromEndAnimationState;
        } else if (input.equals("slash_from_stab")) {
            return this.SlashFromStabAnimationState;
        } else if (input.equals("slash_from_stab_grab_pre")) {
            return this.SlashFromStabGrabAnimationState;
        } else if (input.equals("slash_from_stab_grab_fail")) {
            return this.SlashFromStabGrabFailAnimationState;
        } else if (input.equals("slash_from_stab_grab_stab_fail")) {
            return this.SlashFromStabGrabStabFailAnimationState;
        } else if (input.equals("slash_from_stab_grab_success")) {
            return this.SlashFromStabGrabSuccessAnimationState;
        } else if (input.equals("jump_pre")) {
            return this.JumpPreAnimationState;
        } else if (input.equals("jump_fall")) {
            return this.JumpFallAnimationState;
        } else if (input.equals("jump_smash")) {
            return this.JumpSmashAnimationState;
        } else if (input.equals("shield_smash")) {
            return this.ShieldSmashAnimationState;
        } else if (input.equals("backflip_double")) {
            return this.BackflipDoubleAnimationState;
        } else if (input.equals("backflip_end")) {
            return this.BackflipEndAnimationState;
        } else if (input.equals("second_phase")) {
            return this.SecondPhaseAnimationState;
        } else if (input.equals("side_roll_spin")) {
            return this.SideRollSpinAnimationState;
        } else if (input.equals("left_side_roll_spin")) {
            return this.LeftSideRollSpinAnimationState;
        } else if (input.equals("jump_smash_combo")) {
            return this.JumpSmashComboAnimationState;
        } else if (input.equals("sleep")) {
            return this.SleepAnimationState;
        } else if (input.equals("awaken")) {
            return this.AwakenAnimationState;
        } else if (input.equals("death")) {
            return this.DeathAnimationState;
        } else if (input.equals("finisher")) {
            return this.FinisherAnimationState;
        } else if (input.equals("trident_throw_spin")) {
            return this.TridentThrowSpinAnimationState;
        }
        return new AnimationState();
    }

    /**
     * 攻击状态一变，客户端就切换动画。
     *
     * <p>为什么写在 {@code onSyncedDataUpdated} 而不是 tick 里？因为这个方法是
     * 「同步字段到达客户端」的回调，天然只在状态真正变化时触发一次 ——
     * 换成每 tick 检查反而要自己记上一帧的状态。
     *
     * <p>每个 case 都先 {@code stopAllAnimationStates()} 再启动新的：
     * 不先停掉旧的，两段动画会叠在一起，模型会扭曲成一团。
     */
    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        if (ATTACK_STATE.equals(pKey) && this.level().isClientSide) {
            switch (this.getAttackState()) {
                case 0 -> this.stopAllAnimationStates();
                case 1 -> {
                    this.stopAllAnimationStates();
                    this.idleAnimationState.startIfStopped(this.tickCount);
                }
                case 2 -> {
                    this.stopAllAnimationStates();
                    this.DoubleSlashAnimationState.startIfStopped(this.tickCount);
                }
                case 3 -> {
                    this.stopAllAnimationStates();
                    this.DoubleSlashEndAnimationState.startIfStopped(this.tickCount);
                }
                case 4 -> {
                    this.stopAllAnimationStates();
                    this.DoubleSlashSlamEndAnimationState.startIfStopped(this.tickCount);
                }
                case 5 -> {
                    this.stopAllAnimationStates();
                    this.ParryAnimationState.startIfStopped(this.tickCount);
                }
                case 6 -> {
                    this.stopAllAnimationStates();
                    this.SwordSlamAnimationState.startIfStopped(this.tickCount);
                }
                case 7 -> {
                    this.stopAllAnimationStates();
                    this.SwordSlamEndAnimationState.startIfStopped(this.tickCount);
                }
                case 8 -> {
                    this.stopAllAnimationStates();
                    this.SwordSlamCounterEndAnimationState.startIfStopped(this.tickCount);
                }
                case 9 -> {
                    this.stopAllAnimationStates();
                    this.AllertedAnimationState.startIfStopped(this.tickCount);
                }
                case 10 -> {
                    this.stopAllAnimationStates();
                    this.BackflipAnimationState.startIfStopped(this.tickCount);
                }
                case 11 -> {
                    this.stopAllAnimationStates();
                    this.FlyAwaySlashAnimationState.startIfStopped(this.tickCount);
                }
                case 12 -> {
                    this.stopAllAnimationStates();
                    this.FlipSmashAnimationState.startIfStopped(this.tickCount);
                }
                case 13 -> {
                    this.stopAllAnimationStates();
                    this.FlipSmashEndAnimationState.startIfStopped(this.tickCount);
                }
                case 14 -> {
                    this.stopAllAnimationStates();
                    this.FlipSmashFlipAnimationState.startIfStopped(this.tickCount);
                }
                case 15 -> {
                    this.stopAllAnimationStates();
                    this.ThrowAnimationState.startIfStopped(this.tickCount);
                }
                case 16 -> {
                    this.stopAllAnimationStates();
                    this.SlashFromAnimationState.startIfStopped(this.tickCount);
                }
                case 17 -> {
                    this.stopAllAnimationStates();
                    this.SlashFromEndAnimationState.startIfStopped(this.tickCount);
                }
                case 18 -> {
                    this.stopAllAnimationStates();
                    this.SlashFromStabAnimationState.startIfStopped(this.tickCount);
                }
                case 19 -> {
                    this.stopAllAnimationStates();
                    this.SlashFromStabGrabAnimationState.startIfStopped(this.tickCount);
                }
                case 20 -> {
                    this.stopAllAnimationStates();
                    this.SlashFromStabGrabSuccessAnimationState.startIfStopped(this.tickCount);
                }
                case 21 -> {
                    this.stopAllAnimationStates();
                    this.SlashFromStabGrabFailAnimationState.startIfStopped(this.tickCount);
                }
                case 22 -> {
                    this.stopAllAnimationStates();
                    this.JumpPreAnimationState.startIfStopped(this.tickCount);
                }
                case 23 -> {
                    this.stopAllAnimationStates();
                    this.JumpFallAnimationState.startIfStopped(this.tickCount);
                }
                case 24 -> {
                    this.stopAllAnimationStates();
                    this.JumpSmashAnimationState.startIfStopped(this.tickCount);
                }
                case 25 -> {
                    this.stopAllAnimationStates();
                    this.ShieldSmashAnimationState.startIfStopped(this.tickCount);
                }
                case 26 -> {
                    this.stopAllAnimationStates();
                    this.SecondPhaseAnimationState.startIfStopped(this.tickCount);
                }
                case 27 -> {
                    this.stopAllAnimationStates();
                    this.BackflipEndAnimationState.startIfStopped(this.tickCount);
                }
                case 28 -> {
                    this.stopAllAnimationStates();
                    this.ThrowDoubleAnimationState.startIfStopped(this.tickCount);
                }
                case 29 -> {
                    this.stopAllAnimationStates();
                    this.SideRollSpinAnimationState.startIfStopped(this.tickCount);
                }
                case 30 -> {
                    this.stopAllAnimationStates();
                    this.LeftSideRollSpinAnimationState.startIfStopped(this.tickCount);
                }
                case 31 -> {
                    this.stopAllAnimationStates();
                    this.SwordSlamCounterReleaseAnimationState.startIfStopped(this.tickCount);
                }
                case 32 -> {
                    this.stopAllAnimationStates();
                    this.JumpSmashComboAnimationState.startIfStopped(this.tickCount);
                }
                case 33 -> {
                    this.stopAllAnimationStates();
                    this.SlashFromStabGrabStabFailAnimationState.startIfStopped(this.tickCount);
                }
                case 34 -> {
                    this.stopAllAnimationStates();
                    this.SleepAnimationState.startIfStopped(this.tickCount);
                }
                case 35 -> {
                    this.stopAllAnimationStates();
                    this.AwakenAnimationState.startIfStopped(this.tickCount);
                }
                case 36 -> {
                    this.stopAllAnimationStates();
                    this.DeathAnimationState.startIfStopped(this.tickCount);
                }
                case 37 -> {
                    this.stopAllAnimationStates();
                    this.FinisherAnimationState.startIfStopped(this.tickCount);
                }
                case 38 -> {
                    this.stopAllAnimationStates();
                    this.TridentThrowSpinAnimationState.startIfStopped(this.tickCount);
                }
                default -> {
                    // 状态表里没有的编号：什么都不播，保持上一个 pose。
                }
            }
        }

        super.onSyncedDataUpdated(pKey);
    }

    /** 把所有动画状态停掉。配合 onSyncedDataUpdated 使用，避免多段动画叠加。 */
    public void stopAllAnimationStates() {
        this.ThrowDoubleAnimationState.stop();
        this.idleAnimationState.stop();
        this.DoubleSlashAnimationState.stop();
        this.deathAnimationState.stop();
        this.DoubleSlashEndAnimationState.stop();
        this.DoubleSlashSlamEndAnimationState.stop();
        this.ParryAnimationState.stop();
        this.SwordSlamEndAnimationState.stop();
        this.SwordSlamCounterEndAnimationState.stop();
        this.SwordSlamAnimationState.stop();
        this.AllertedAnimationState.stop();
        this.BackflipAnimationState.stop();
        this.FlyAwaySlashAnimationState.stop();
        this.FlipSmashEndAnimationState.stop();
        this.FlipSmashFlipAnimationState.stop();
        this.FlipSmashAnimationState.stop();
        this.ThrowAnimationState.stop();
        this.SlashFromEndAnimationState.stop();
        this.SlashFromAnimationState.stop();
        this.SlashFromStabAnimationState.stop();
        this.SlashFromStabGrabSuccessAnimationState.stop();
        this.SlashFromStabGrabAnimationState.stop();
        this.SlashFromStabGrabFailAnimationState.stop();
        this.JumpPreAnimationState.stop();
        this.JumpFallAnimationState.stop();
        this.JumpSmashAnimationState.stop();
        this.BackflipDoubleAnimationState.stop();
        this.BackflipEndAnimationState.stop();
        this.ShieldSmashAnimationState.stop();
        this.SecondPhaseAnimationState.stop();
        this.SideRollSpinAnimationState.stop();
        this.LeftSideRollSpinAnimationState.stop();
        this.SwordSlamCounterReleaseAnimationState.stop();
        this.JumpSmashComboAnimationState.stop();
        this.SlashFromStabGrabStabFailAnimationState.stop();
        this.AwakenAnimationState.stop();
        this.SleepAnimationState.stop();
        this.DeathAnimationState.stop();
        this.FinisherAnimationState.stop();
        this.TridentThrowSpinAnimationState.stop();
    }

    // ==================================================================
    //  九、招式变体的重掷
    // ==================================================================

    public int getNextDoubleSlashType() {
        return this.DoubleSlashType;
    }

    public int getNextSideRollSpinType() {
        return this.SideRollSpinType;
    }

    /**
     * 重掷二连斩的收招变体。
     *
     * <p>参数 rolls 是「掷几次骰子」：传 2 表示 1/2 概率各一半。
     * 原版用 switch 写的，逻辑等价于 {@code nextInt(rolls) + 1}，
     * 但这里保留原版的写法以便和反编译源码逐行对照。
     */
    public void randomizeNextDoubleSlashType(int rolls) {
        switch (this.getRandom().nextInt(rolls)) {
            case 0 -> this.DoubleSlashType = 1;
            case 1 -> this.DoubleSlashType = 2;
            default -> this.DoubleSlashType = 1;
        }
    }

    /** 重掷侧滚旋转的方向。参数含义同 {@link #randomizeNextDoubleSlashType(int)}。 */
    public void randomizeNextSideRollSpinType(int rolls) {
        switch (this.getRandom().nextInt(rolls)) {
            case 0 -> this.SideRollSpinType = 1;
            case 1 -> this.SideRollSpinType = 2;
            default -> this.SideRollSpinType = 1;
        }
    }

    /**
     * 一轮招式结束后调用，重掷「下一轮用哪个变体」。
     * 由各招式的 goal 在 stop() 里触发（原版 P_PAttackGoal.stop 就调了它）。
     */
    public void randomizeAttacks() {
        this.randomizeNextSideRollSpinType(2);
        this.randomizeNextDoubleSlashType(2);
    }

    // ==================================================================
    //  十、渲染图层的显隐开关
    //
    //  匕首 / 盾牌 / 三叉戟 / 翅膀这四个图层不是常驻显示的，
    //  而是「手里真拿着东西的时候才画」。原版靠这几个方法判断。
    //
    //  ⚠️ 这几个方法<b>只在客户端渲染时被调用</b>（渲染器每帧问一次），
    //     所以里面只读状态、不做任何副作用 —— 别在这儿改字段或发粒子。
    //     另外它们是 public 的：渲染图层在另一个包里，够不着包级私有。
    //  ==================================================================

    /** 投匕首（15 / 28）期间手上才有匕首。 */
    public boolean hasDagger() {
        return this.getAttackState() == 15 || this.getAttackState() == 28;
    }

    /** 盾击（25）期间才举盾。 */
    public boolean hasShield() {
        return this.getAttackState() == 25;
    }

    /** 终结技（37）和掷三叉戟（38）期间才握三叉戟。 */
    public boolean hasTrident() {
        return this.getAttackState() == 37 || this.getAttackState() == 38;
    }

    /** 只有终结技（37）会展开翅膀。 */
    public boolean hasWings() {
        return this.getAttackState() == 37;
    }

    /**
     * 这一刻该不该在脚下画那片「地面预警光圈」。
     *
     * <p>预警圈是给玩家的公平性提示 —— 光圈亮起就是在说「这一片马上要炸，快躲」。
     * 它<b>纯客户端显示，不影响任何判定</b>；伤害该在哪儿还是在哪儿。
     * 每 tick 被渲染器问一次，所以这里只读状态、不做任何副作用。
     *
     * <h2>为什么是两段区间拼起来的</h2>
     * 原版这句是个不带括号的 {@code &&} / {@code ||} 混写，照抄如下：
     * <pre>
     *   attackTicks >= 48 &amp;&amp; attackTicks &lt;= 60 &amp;&amp; getAttackState() == 32
     *   || getAttackState() == 38 &amp;&amp; attackTicks >= 48 &amp;&amp; attackTicks &lt;= 69
     * </pre>
     * {@code &&} 的优先级比 {@code ||} 高，所以实际等价于「下面两种情况之一」：
     * <ul>
     *   <li><b>状态 32</b>（二阶段跳砸连招）第 48~60 tick；</li>
     *   <li><b>状态 38</b>（掷三叉戟）第 48~69 tick。</li>
     * </ul>
     * 把左半边和右半边写成两个括号包起来的条件，和原版完全等价，
     * 但不用去记优先级。⚠️ 别「顺手」改成 {@code && getAttackState() == 32 || ...} 那种半吊子写法。
     *
     * <h2>为什么都是 48 起步</h2>
     * 因为那正是两招「起手动作演完、真正开始打人」的时刻。光圈只在
     * <b>快要挨打的那段时间</b>亮，前面挥剑蓄力的过程不亮 —— 否则满屏都是圈，等于没提示。
     *
     * <p>⚠️ 这里只回答「该不该画」，<b>画多亮</b>是另一个计时器
     * （{@code telegraphFadeAway}）说了算的。两者是「与」的关系：
     * 状态对了、时间也对了，但计时器已经涨满，圈还是看不见。
     * 两个状态各自推进计时器的时机见各自的 UpdateWithAttack 分支。
     *
     * @return 该画预警圈返回 true
     */
    public boolean canRenderTelegraph() {
        return this.attackTicks >= 48 && this.attackTicks <= 60 && this.getAttackState() == 32
                || this.getAttackState() == 38 && this.attackTicks >= 48 && this.attackTicks <= 69;
    }

    // ==================================================================
    //  十一、寻路 / 目标
    // ==================================================================

    /**
     * 必须覆写成原版寻路器 —— 这是「不跟随主人」和「走得极慢」的根因。
     *
     * <p>父类 {@link IAnimatedMonsterServant} 返回的是传奇怪物自己的
     * {@code ModPathNavigation}，那是给 BOSS 设计的：
     * <ul>
     *   <li>用自定义的 {@code ModPathfinder} 寻路</li>
     *   <li>对 WATER / LAVA / OPEN 三种地形一律拒绝更新路径</li>
     *   <li>整个重写了「卡住检测」，把原版「卡住就重新寻路」换成了每 tick 强制
     *       把实体 moveTo 回当前位置 —— 所以仆从一旦卡住就永远恢复不了</li>
     * </ul>
     * 当仆从用就会表现为站在原地看着主人走远。蔓生巨像仆从也是靠这个覆写绕开的，
     * 见 {@code OvergrownColossusServant:445}。
     */
    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        return new GroundPathNavigation(this, pLevel);
    }

    // 这里**刻意不覆写** IServant.getFollowSpeed()，走 Goety 默认的 1.0。
    // 本仆从的移动速度已和蔓生巨像仆从（OvergrownColossusServant）完全对齐：
    //     基础移速 0.3 × 1.0 = 0.30，追击 / 跟随 / 闲逛三档全都是 0.30。
    // 曾经试着单独压过跟随（0.5 → 0.9）和闲逛（0.5 → 0.75），实机手感不对，已全部撤销。
    // 教训记在 错题本.md 第 21 条，别再走一遍。

    @Override
    protected void registerGoals() {
        super.registerGoals();

        // 优先级必须大于 5：Goety 的 FollowOwnerGoal 占了 5，游荡/环视低于它就会被跟随挤掉。
        // 闲逛倍率 1.0：1.0 × 0.3(基础移速) = 0.30，和蔓生巨像仆从完全一致（它也写 1.0D）。
        // 曾经压到 0.75（= 0.225）想让闲逛慢一档，实机手感不对，已撤销。
        this.goalSelector.addGoal(6, new Summoned.WanderGoal<>(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));

        // 走向目标（追击）。倍率保持 1.0，和原版 BOSS 的实际追击速度一致：
        //     原版 0.1(基础移速) × 3.0(这个倍率) = 0.30
        //     我们 0.3(基础移速) × 1.0(这个倍率) = 0.30
        // ⚠️ 别动这里，也别动 AttributesConfig 里的基础移速 ——
        //    0.3 这个基础值被「追击 / 跟随 / 闲逛」三档共用，一动三档全变。
        //    本仆从刻意和蔓生巨像仆从保持一致，三档都是 0.30。
        this.goalSelector.addGoal(2, new IMoveGoal(this, false, 1.0D));

        // ==================================================================
        //  ⚠️ 下面所有招式 goal 的注册顺序，是<b>照抄原版</b>的，不要随手调整
        // ==================================================================
        // 原因：{@code GoalSelector} 内部是一个 <b>LinkedHashSet</b>（保插入顺序），
        // 每 tick 从头到尾遍历，<b>第一个 canUse() 返回 true 且能抢到行为标志的就赢</b>。
        // 一个 goal 抢到 MOVE/LOOK/JUMP 之后会把它们锁住，同优先级（都是 1）的后来者
        // 就抢不到了 —— 因为 {@code WrappedGoal.canBeReplacedBy()} 要求新 goal 的
        // 优先级数字<b>严格更小</b>，同优先级一律不算「可以顶替」。
        //
        // 所以「14 招里谁的掷骰先被问到」完全由注册顺序决定，而不是由优先级决定。
        // 举一个会看出差别的地方：目标离圣骑 5~7 格时，砸地（射程 7）和翻跟头砸（射程 16，
        // 但要求 &gt; 5 格）<b>同时</b>可能满足条件 —— 原版里砸地排在后面，所以这种距离下
        // 是翻跟头砸先被问到。顺序抄错的话，玩家会感觉「怎么老是砸地」，虽然不报错。
        //
        // 原版的注册顺序（{@code PossessedPaladinEntity.java} 行号）：
        //   优先级 1：2(580) 3(591) 6(602) 10(663) 38(674) 12(685) 15(715) 28(726) 16(737)
        //            22(814) 23(819) 25(840) 29(850) 30(861) 37(872)
        //   优先级 0：26(567) 8(613) 31(623) 7(633) 5(643) 13(695) 14(705) 18(747) 17(757)
        //            19(767) 20(777) 21(790) 33(802) 24(820) 32(830) 34(883) 35(892) 36(897)
        // 优先级 0 那批全是「认领某个状态」的收招 goal，彼此靠 attackState 互斥，
        // 顺序无所谓；真正要紧的是优先级 1 那批攻击 goal 的先后。
        //
        // 已经搬过来的部分按原版顺序排：**2 → 3 → 6 → 10 → 12 → 25 → 29 → 30**。

        // ---- 收招状态（原版 P_PStateGoal，三处都注册在优先级 0）----
        // 这三条不是「可选的补充」，而是砸地能正常循环的<b>必要条件</b>：
        // 砸地的 stop() 会把状态切成 7 或 8（二阶段 31），必须有 goal 认领并把状态转回 0，
        // 否则圣骑打完一下就永久定住。详细机制见 PossessedPaladinStateGoal 的类注释。
        //
        // 参数依次是 (getattackstate, attackstate, attackendstate, attackfinaltick, attackseetick,
        // canAllertedState, allertedStateChance)，数值照抄原版 PossessedPaladinEntity.java:613/623/633。
        // canAllertedState 传 true：这三处收招时都有一半左右概率转进状态 9（警觉），
        // 由本文件下面注册的 PossessedPaladinAlertedGoal 接管（原版这三处也是 true）。

        // 状态 26：二阶段变身。toTicks(7.38F) = 147 tick（7.35 秒）。原版 PossessedPaladinEntity.java:567。
        //
        // ⚠️ 它必须排在所有优先级 0 的 goal <b>最前面</b>，理由和别的收招 goal 不一样：
        //    别的收招 goal 都要求「状态已经是某个具体数」，而本类要求的是「状态是 0」——
        //    也就是它和**所有攻击 goal** 争同一个状态 0。变身时圣骑要停下来演动画，
        //    不能被别的 goal 抢先拿去出招，所以注册顺序放在第一位。
        //    （原版也是第一处注册的，第 567 行，在 registerGoals 开头。）
        //
        // 它自己不看距离、不看冷却，只看血量有没有掉到 65% 以下，见类的注释。
        this.goalSelector.addGoal(0, new PossessedPaladinSecondPhaseGoal(this, 0, 26, 0,
                MathUtils.toTicks(7.38F), 0));

        // 状态 8：砸地的<b>第二下</b>（落地转身横扫），toTicks(3.38F)。
        // 触发条件是「第一下打中了人」或「离目标还有 6 格以上」，见 PossessedPaladinSlamAttackGoal.stop()。
        // ⚠️ 它不是单纯的过场动画 —— 有自己的伤害判定，写在 UpdateWithAttack 的状态 8 分支里。
        // 早先这里只注册了 goal 没搬伤害，结果就是「第二下动作照播、却完全打不到人」。
        this.goalSelector.addGoal(0, new PossessedPaladinStateGoal(this, 8, 8, 0,
                MathUtils.toTicks(3.38F), 18, true, 50.0D) {
            @Override
            public void stop() {
                PossessedPaladinServant.this.parry_cooldown = PossessedPaladinServant.this.PARRY_COOLDOWN;
                super.stop();
            }
        });

        // 状态 31：二阶段版本的同类收招，toTicks(4.17F)。
        this.goalSelector.addGoal(0, new PossessedPaladinStateGoal(this, 31, 31, 0,
                MathUtils.toTicks(4.17F), 18, true, 50.0D) {
            @Override
            public void stop() {
                PossessedPaladinServant.this.parry_cooldown = PossessedPaladinServant.this.PARRY_COOLDOWN;
                super.stop();
            }
        });

        // 状态 7：近身收招，很短（toTicks(1.29F)），一阶段二阶段共用。
        // attackseetick 传 0 —— 这一招从头到尾都不看目标，直接冻结朝向。
        this.goalSelector.addGoal(0, new PossessedPaladinStateGoal(this, 7, 7, 0,
                MathUtils.toTicks(1.29F), 0, true, 50.0D) {
            @Override
            public void stop() {
                PossessedPaladinServant.this.parry_cooldown = PossessedPaladinServant.this.PARRY_COOLDOWN;
                super.stop();
            }
        });

        // ---- 通用攻击招式（原版 P_PAttackGoal，四招共用一个类）----
        // 参数依次是 (getattackstate, attackstate, attackendstate, attackMaxtick, attackseetick,
        // attackrange, canAllertedState, allertedStateChance)，照抄原版 PossessedPaladinEntity 的注册。
        //
        // 每条 canUse() 里的掷骰和冷却检查，原版写在注册处的匿名子类里，
        // 机制见 PossessedPaladinSlamAttackGoal 的类注释。
        // 原版条件里还有 !animationLockedForTests()，那是作者的调试开关（恒返回 false），省掉。
        // 另外原版条件里重复写的 target != null 也省掉 —— 父类 canUse() 已经查过了。
        //
        // ⚠️ 下面这些招式的<b>注册顺序不能随便调</b>，砸地（招 6）就夹在招 3 和招 10 中间 ——
        //    这不是笔误，而是原版的顺序。原因见下面那段说明。

        // 招 2：二连斩第一段（状态 2）。只有掷到 DoubleSlashType == 1 时才走这条。
        this.goalSelector.addGoal(1, new PossessedPaladinAttackGoal(this, 0, 2, 0, 68, 68, 5.0F, false, 10.0D) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && PossessedPaladinServant.this.getRandom().nextFloat() * 100.0F
                        < (float) PossessedPaladinServant.this.DoubleSlashRandom
                        && PossessedPaladinServant.this.getNextDoubleSlashType() == 1
                        && PossessedPaladinServant.this.double_slash_cooldown <= 0;
            }

            @Override
            public void stop() {
                PossessedPaladinServant.this.double_slash_cooldown = PossessedPaladinServant.this.DOUBLE_SLASH_COOLDOWN;
                super.stop();
            }
        });

        // 招 3：二连斩砸（状态 3）。原版注册位置 PossessedPaladinEntity.java:591。
        // 它是招 2 的「加强版」：同样两刀，后面再接一下跳劈砸地（第三下）。
        // 和招 2 共用 double_slash_cooldown，靠 getNextDoubleSlashType() 二选一 ——
        // 掷到 1 走招 2（两刀收工），掷到 2 走这一招（两刀 + 砸地）。
        //
        // ⚠️ 两处容易抄错的细节：
        //   1. 掷骰这里乘的是 <b>40.0F 不是 100.0F</b>（招 2 是 100.0F），
        //      配合 DoubleSlashRandom = 10，实际触发率约 25%，比招 2 的 10% 高出一倍多；
        //   2. MathUtils.toTicks(3.46F) ≈ 69 tick 总长，第三下砸地落在第 62 tick，
        //      所以 attackseetick 是 55 而不是招 2 的 68 —— 收招前 14 tick 就锁定朝向了。
        this.goalSelector.addGoal(1, new PossessedPaladinDoubleSlashSlamAttackGoal(this, 0, 3, 0,
                MathUtils.toTicks(3.46F), 55, 5.0F, false, 10.0D) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && PossessedPaladinServant.this.getRandom().nextFloat() * 40.0F
                        < (float) PossessedPaladinServant.this.DoubleSlashRandom
                        && PossessedPaladinServant.this.getNextDoubleSlashType() == 2
                        && PossessedPaladinServant.this.double_slash_cooldown <= 0;
            }

            @Override
            public void stop() {
                PossessedPaladinServant.this.double_slash_cooldown = PossessedPaladinServant.this.DOUBLE_SLASH_COOLDOWN;
                super.stop();
            }
        });

        // 招 6：砸地（状态 6）。原版注册位置 PossessedPaladinEntity.java:602 ——
        // <b>排在招 2 和招 3 后面</b>，见上面那段「注册顺序」的说明。
        // 30 tick = 原版 MathUtils.toTicks(1.54F)；(0, 6, 0) = 待机起手、切到砸地、收招回待机。
        // canAllertedState 传 false：砸地的收招去向由 PossessedPaladinSlamAttackGoal.stop()
        // 自己决定（切 7 或 8），不走警觉那条路。
        this.goalSelector.addGoal(1, new PossessedPaladinSlamAttackGoal(this, 0, 6, 0, 30, 20, 7.0F));

        // 状态 9：警觉（横向滑步）。优先级 1，原版注册位置 PossessedPaladinEntity.java:654 ——
        // 就夹在招 6 和招 10 中间。
        //
        // ⚠️ 它排在这里纯粹是<b>为了和原版逐行对齐</b>，行为上没有区别：
        //    本类的 canUse() 只认 {@code getAttackState() == 9}，而所有攻击 goal 都要求状态是 0，
        //    两者天然互斥，永远不可能同时满足条件，所以先问谁后问谁都一样。
        //    放在这里的好处是以后拿原版逐行 diff 时不会再被这一条干扰。
        //
        // 注意 canAllertedState 传 false：状态 9 收招后不再二次转警觉，直接回 0 —— 原版这一处也是 false。
        this.goalSelector.addGoal(1, new PossessedPaladinAlertedGoal(this, 9, 9, 0, 20, 20, false, 0.0D));

        // 招 10：后空翻（状态 10）。canAllertedState 传 true —— 收招时 35% 概率转进警觉（状态 9）。
        this.goalSelector.addGoal(1, new PossessedPaladinAttackGoal(this, 0, 10, 0,
                MathUtils.toTicks(1.42F), 20, 5.0F, true, 35.0D) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && PossessedPaladinServant.this.getRandom().nextFloat() * 100.0F
                        < (float) PossessedPaladinServant.this.backflipRandom
                        && PossessedPaladinServant.this.backstep_cooldown <= 0;
            }

            @Override
            public void stop() {
                PossessedPaladinServant.this.backstep_cooldown = PossessedPaladinServant.this.BACKSTEP_COOLDOWN;
                super.stop();
            }
        });

        // ---- 招 38：掷三叉戟旋转（状态 38，二阶段专属）----
        // 原版注册位置 PossessedPaladinEntity.java:674。
        //
        // ⚠️ 这块<b>必须待在招 10 和招 12 中间</b>，不能挪到后面去。
        //    原因见上面那段「注册顺序」：GoalSelector 按插入顺序问 canUse，同优先级先问谁谁先赢。
        //    招 38 和招 12（翻跟头砸）都是「7~16 格」的远程起手，目标站在这个距离上时
        //    两条同时满足条件 —— 原版先问 38，所以优先掷三叉戟；要是把这块排到招 12 后面，
        //    二阶段一开就会变成永远先出翻跟头砸，手感和原作对不上。
        //
        // 参数（对照招 10 那条看）：
        //   38 —— 打这一招时切到状态 38；
        //   MathUtils.toTicks(5.42F) = 108 tick —— 整招时长，和 UpdateWithAttack 里最后那根
        //   柱子（第 83 tick）加上收招时间对得上；
        //   72 —— attackseetick：前 72 tick 一直盯着目标，之后锁死朝向。
        //   72 这个数就是「第二刀横扫（第 69 tick）打完」的时刻，锁得刚刚好；
        //   16.0F —— 判定距离；canUse 里又额外要求 ≥ 7 格，两条一起就是 7~16 格的环带。
        //
        // canUse 里的四个条件，缺一不可：
        //   getPhase() >= 2 —— 二阶段才解锁。这条不满足，下面掷骰子一律不进行；
        //   掷骰子 < throwTridentSpinRandom（15%）—— 每 tick 掷一次，所以是「平均几秒出一次」；
        //   trident_throw_spin <= 0 —— 冷却好了没；
        //   距离 ≥ 7 格 —— 太近了用不着扔（贴脸直接砍就行）。
        //
        // stop() 里上冷却，和别的招一样：不上的话一阶段结束后连着扔。
        this.goalSelector.addGoal(1, new PossessedPaladinAttackGoal(this, 0, 38, 0,
                MathUtils.toTicks(5.42F), 72, 16.0F, true, 35.0D) {
            @Override
            public boolean canUse() {
                if (PossessedPaladinServant.this.getPhase() < 2) {
                    return false;
                }
                return super.canUse()
                        && PossessedPaladinServant.this.getRandom().nextFloat() * 100.0F
                        < (float) PossessedPaladinServant.this.throwTridentSpinRandom
                        && PossessedPaladinServant.this.trident_throw_spin <= 0
                        && PossessedPaladinServant.this.targetIsNotNull()
                        && PossessedPaladinServant.this.distanceTo(PossessedPaladinServant.this.target()) >= 7.0F;
            }

            @Override
            public void stop() {
                PossessedPaladinServant.this.trident_throw_spin = PossessedPaladinServant.this.TRIDENT_THROW_SPIN;
                super.stop();
            }
        });

        // ---- 招 12 / 13 / 14：翻跟头砸 ----
        // 原版注册位置 PossessedPaladinEntity.java:685（状态 12）、695（状态 13）、705（状态 14）。
        //
        // 这是圣骑唯一的远程起手：离目标 5~16 格才放，跳过去砸一下。
        // 和前几招不同，它<b>不是一个 goal 管一招，而是一个 goal 管起手、另两条 goal 管收招</b>：
        //   FlipSmashGoal（优先级 1）从状态 0 起手 → 切到状态 12；
        //   状态 12 演完，它的 stop() 掷骰子切到 13（稀有）或 14（几乎总是）；
        //   13 / 14 由两条 PossessedPaladinStateGoal（优先级 0）认领，演完回状态 0。
        //
        // ⚠️ 两条收招 goal 的 stop() 里都要设 flip_smash_cooldown —— 只写一条的话，
        //    走另一条支线时冷却就不生效，圣骑会连着扑。原版两处都写了。
        //
        // ⚠️ 状态 13 和 14 的时长差得很远：20 tick vs 47 tick。别以为 13 短就漏了它 ——
        //    它虽然是罕见分支，但原版确实注册了，漏了会导致「掷到 13 之后圣骑永久卡死」。

        // 状态 12：起手扑击。toTicks(1.63F) = 32 tick，判定距离 16 格（配合 canUse 的 > 5 格）。
        // attackseetick 传 13 —— 前 13 tick 盯着目标，之后锁死朝向开始扑。
        // canAllertedState 传 false：这一招收尾不转警觉（改由 stop() 自己掷骰切 13/14）。
        this.goalSelector.addGoal(1, new PossessedPaladinFlipSmashGoal(this, 0, 12, 0,
                MathUtils.toTicks(1.63F), 13, 16.0F, false, 10.0D) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && PossessedPaladinServant.this.getRandom().nextFloat() * 100.0F
                        < (float) PossessedPaladinServant.this.flipSmashRandom
                        && PossessedPaladinServant.this.flip_smash_cooldown <= 0;
            }
        });

        // 状态 13：短收招，toTicks(1.04F) = 20 tick。attackseetick 传 20 —— 全程都在看目标。
        // 这条分支很少被掷中（见 PossessedPaladinFlipSmashGoal 类注释里那段溢出说明），
        // 但少了它，掷中的那一次就会永久卡住。
        this.goalSelector.addGoal(0, new PossessedPaladinStateGoal(this, 13, 13, 0,
                MathUtils.toTicks(1.04F), 20, true, 35.0D) {
            @Override
            public void stop() {
                PossessedPaladinServant.this.flip_smash_cooldown = PossessedPaladinServant.this.FLIP_SMASH_COOLDOWN;
                super.stop();
            }
        });

        // 状态 14：长收招，toTicks(2.38F) = 47 tick。attackseetick 传 8 —— 只看前 8 tick，
        // 之后锁死朝向。它<b>有</b>独立的落伤害判定（第 14 tick），见 UpdateWithAttack 的状态 14 分支。
        this.goalSelector.addGoal(0, new PossessedPaladinStateGoal(this, 14, 14, 0,
                MathUtils.toTicks(2.38F), 8, true, 35.0D) {
            @Override
            public void stop() {
                PossessedPaladinServant.this.flip_smash_cooldown = PossessedPaladinServant.this.FLIP_SMASH_COOLDOWN;
                super.stop();
            }
        });

        // ---- 招 15 / 28：投匕首 ----
        // 原版注册位置 PossessedPaladinEntity.java:715（状态 15）、726（状态 28）。
        //
        // ⚠️⚠️ 位置很要紧，<b>必须待在招 14 和招 16 中间</b>。
        //    这里原先排在招 22（跳劈）后面，并在注释里写「位置无所谓，两边靠距离互斥」——
        //    那个说法<b>只对了一半</b>：本招要 7 格以上，剑气斩（16）要 5 格以内，
        //    这两条确实互斥；但<b>跳劈（22）的门槛是 6~16 格</b>，和本招的 7~12 格
        //    <b>是重叠的</b>。于是 7~12 格这个距离带上，两条同时满足条件，
        //    谁先被问到就由注册顺序决定：
        //      原版：…… 38 → 12 → <b>15 / 28</b> → 16 → 22 ……  → 先掏匕首
        //      改之前：…… 38 → 12 → 16 → <b>22</b> → 15 / 28 …… → 先起跳
        //    后果是圣骑在中等距离上「老是扑过来」而不是「先扔一轮匕首」，
        //    和原版手感明显不同。2026-09-24 的对比里发现并搬回原位的。
        //
        // ⚠️ 两条都带阶段判断 —— 一阶段只会扔三把（15），二阶段才解锁十把（28）。
        //    这是 14 招里<b>唯一</b>用 getPhase() 把同名招式拆成两个状态的地方，
        //    别把这两条合并成一条，合并了二阶段就只剩三把匕首了。
        //
        // ⚠️ 两条的 stop() 都要设 throw_cooldown（THROW_COOLDOWN = 5 秒）——
        //    这是这招唯一的节奏阀，漏设圣骑会站远了无脑刷匕首。

        // 状态 15：一阶段投匕首。toTicks(2.04F) = 40 tick、attackseetick 30、射程 12 格。
        // 掷骰是 nextFloat() * 40 < throwRandom（默认 10），所以实际通过率约 25%。
        this.goalSelector.addGoal(1, new PossessedPaladinThrowDaggersGoal(this, 0, 15, 0,
                MathUtils.toTicks(2.04F), 30, 12.0F, true, 25.0D) {
            @Override
            public boolean canUse() {
                LivingEntity target = PossessedPaladinServant.this.getTarget();
                return super.canUse()
                        && PossessedPaladinServant.this.getRandom().nextFloat() * 40.0F
                        < (float) PossessedPaladinServant.this.throwRandom
                        && PossessedPaladinServant.this.throw_cooldown <= 0
                        && PossessedPaladinServant.this.getPhase() < 2
                        && target != null;
            }

            @Override
            public void stop() {
                PossessedPaladinServant.this.throw_cooldown = PossessedPaladinServant.this.THROW_COOLDOWN;
                super.stop();
            }
        });

        // 状态 28：二阶段投匕首。toTicks(3.5F) = 70 tick、attackseetick 50、射程同样是 12 格。
        // 时长几乎翻倍就是因为要扔三轮（第 12 / 30 / 32 tick）。
        this.goalSelector.addGoal(1, new PossessedPaladinThrowDaggersGoal(this, 0, 28, 0,
                MathUtils.toTicks(3.5F), 50, 12.0F, true, 25.0D) {
            @Override
            public boolean canUse() {
                LivingEntity target = PossessedPaladinServant.this.getTarget();
                return super.canUse()
                        && PossessedPaladinServant.this.getRandom().nextFloat() * 40.0F
                        < (float) PossessedPaladinServant.this.throwRandom
                        && PossessedPaladinServant.this.throw_cooldown <= 0
                        && PossessedPaladinServant.this.getPhase() >= 2
                        && target != null;
            }

            @Override
            public void stop() {
                PossessedPaladinServant.this.throw_cooldown = PossessedPaladinServant.this.THROW_COOLDOWN;
                super.stop();
            }
        });

        // ---- 招 16 / 17 / 18：剑气斩 ----
        // 原版注册位置 PossessedPaladinEntity.java:737（状态 16）、747（状态 17）、757（状态 18）。
        // 和翻跟头砸一样是「一条起手 + 两条收招」的三段结构：
        //   SlashFromGoal（优先级 1）从状态 0 起手 → 切到状态 16；
        //   状态 16 演完，它的 stop() 掷骰子切到 18 / 17 / 19（见那个类的注释）；
        //   17 / 18 由两条 PossessedPaladinStateGoal（优先级 0）认领。
        //
        // ⚠️ 两条收招 goal 的 stop() 里都要设 slash_from_cooldown —— 原版两处都写了。
        //    注意状态 18 是<b>唯一的例外</b>：它自己带突刺伤害判定，
        //    所以「收招」这两个字在它身上只是长得像而已，见 UpdateWithAttack 的状态 18 分支。
        // ⚠️ 状态 17 一刀不砍、也没有任何判定，但<b>必须注册</b>：
        //    SlashFromGoal.stop() 会掷中它，没有 goal 认领就会永久卡在状态 17。

        // 状态 16：起手斩。toTicks 由原版直接写死 20 tick，判定距离 5 格 —— 注意这是个<b>近招</b>，
        // 比翻跟头砸（16 格）短得多。attackseetick 传 20 表示全程盯着目标。
        // canAllertedState 传 false：收招去向由 stop() 自己掷骰决定。
        this.goalSelector.addGoal(1, new PossessedPaladinSlashFromGoal(this, 0, 16, 0,
                20, 20, 5.0F, false, 10.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && PossessedPaladinServant.this.getRandom().nextFloat() * 100.0F
                        < (float) PossessedPaladinServant.this.SlashFromRandom
                        && PossessedPaladinServant.this.slash_from_cooldown <= 0;
            }
        });

        // 状态 18：长收招（50 tick = 原版 toTicks(2.5F)）。attackseetick 传 10 —— 只看前 10 tick。
        // 它<b>带独立伤害判定</b>：第 10 tick 记录目标位置，第 15 tick 突刺过去，第 18 tick 直线判定。
        this.goalSelector.addGoal(0, new PossessedPaladinStateGoal(this, 18, 18, 0,
                MathUtils.toTicks(2.5F), 10, true, 25.0D) {
            @Override
            public void stop() {
                PossessedPaladinServant.this.slash_from_cooldown = PossessedPaladinServant.this.SLASH_FROM_COOLDOWN;
                super.stop();
            }
        });

        // 状态 17：短收招（30 tick，原版直接写死）。attackseetick 传 11。纯动画，无判定。
        // 它在实战里很少被掷中（见 PossessedPaladinSlashFromGoal 类注释），但注册不能省。
        this.goalSelector.addGoal(0, new PossessedPaladinStateGoal(this, 17, 17, 0,
                30, 11, true, 15.0D) {
            @Override
            public void stop() {
                PossessedPaladinServant.this.slash_from_cooldown = PossessedPaladinServant.this.SLASH_FROM_COOLDOWN;
                super.stop();
            }
        });

        // ---- 招 19 / 20 / 21 / 33：突刺抓取 ----
        // 原版注册位置 PossessedPaladinEntity.java:767（19）、777（20）、790（21）、802（33）。
        //
        // 这是四条 goal 组成的一个完整分岔，<b>必须四条一起注册</b>，少任何一条都会卡死：
        //   19 → 起手突刺（28 tick）
        //   20 → 抓住了的处决（toTicks(7.5F) = 150 tick）
        //   21 → 一阶段没抓住的收招（toTicks(1.5F)，纯动画）
        //   33 → 二阶段没抓住的收招（toTicks(2.63F)，<b>带一记突刺</b>）
        //
        // ⚠️ 尤其是 19。剑气斩（16）的 stop() 里有「突刺冷却好了就接 19」这一支，
        //    而 stab_grab_cooldown 的初始值就是 0 —— 也就是说<b>打完剑气斩几乎必定进状态 19</b>。
        //    之前这四条一条都没注册，所以圣骑放完一次剑气斩就永久定住、再也不动了。
        //    本批的这四条就是那个 bug 的修复。
        //
        // ⚠️ 状态 19 用的是 PossessedPaladinStabGrabGoal，不是那两条通用的状态 goal ——
        //    它的 canUse 只认状态、canContinueToUse 用 <=、stop 还要读 succedGrabbing。
        //    细节见那个类的注释，别图省事换成 PossessedPaladinStateGoal。

        // 状态 19：突刺抓取起手。attackfinaltick 传 28、attackseetick 传 20。
        // ⚠️ 最后一个 25.0D 在这个 goal 里<b>根本不会被读取</b>（它的 stop() 不问警觉），
        //    保留只是为了和原版注册处逐字对齐。
        this.goalSelector.addGoal(0, new PossessedPaladinStabGrabGoal(this, 19, 19, 0,
                28, 20, false, 25.0D) {
            @Override
            public void stop() {
                PossessedPaladinServant.this.slash_from_cooldown = PossessedPaladinServant.this.SLASH_FROM_COOLDOWN;
                super.stop();
            }
        });

        // 状态 20：抓取处决。attackseetick 传 0 = 全程不盯目标（人都拽在手上了，不用看）。
        // ⚠️ start() 和 stop() <b>都</b>要把 succedGrabbing 清成 false。漏了的话下一次突刺
        //    会误以为「已经抓住过」，还没抓到就直接跳进处决。
        // ⚠️ stop() 里三个赋值一个都不能少：stab_grab_cooldown 是这招的节奏阀，
        //    succedGrabbing 是上面那条，slash_from_cooldown 是剑气斩的节奏阀。
        this.goalSelector.addGoal(0, new PossessedPaladinStateGoal(this, 20, 20, 0,
                MathUtils.toTicks(7.5F), 0, true, 25.0D) {
            @Override
            public void start() {
                PossessedPaladinServant.this.succedGrabbing = false;
                super.start();
            }

            @Override
            public void stop() {
                PossessedPaladinServant.this.stab_grab_cooldown = PossessedPaladinServant.this.STAB_GRAB_COOLDOWN;
                PossessedPaladinServant.this.succedGrabbing = false;
                PossessedPaladinServant.this.slash_from_cooldown = PossessedPaladinServant.this.SLASH_FROM_COOLDOWN;
                super.stop();
            }
        });

        // 状态 21：一阶段的「没抓住」短收招。一刀不砍，但注册不能省。
        this.goalSelector.addGoal(0, new PossessedPaladinStateGoal(this, 21, 21, 0,
                MathUtils.toTicks(1.5F), 0, true, 25.0D) {
            @Override
            public void stop() {
                PossessedPaladinServant.this.stab_grab_cooldown = PossessedPaladinServant.this.STAB_GRAB_COOLDOWN;
                PossessedPaladinServant.this.succedGrabbing = false;
                PossessedPaladinServant.this.slash_from_cooldown = PossessedPaladinServant.this.SLASH_FROM_COOLDOWN;
                super.stop();
            }
        });

        // 状态 33：二阶段的「没抓住」收招。attackseetick 传 12。
        // ⚠️ 名字叫收招，但它<b>有伤害判定</b>（第 20 tick 一记直线突刺），
        //    这也是二阶段比一阶段难缠的地方之一。见 UpdateWithAttack 的状态 33 分支。
        this.goalSelector.addGoal(0, new PossessedPaladinStateGoal(this, 33, 33, 0,
                MathUtils.toTicks(2.63F), 12, false, 25.0D) {
            @Override
            public void stop() {
                PossessedPaladinServant.this.stab_grab_cooldown = PossessedPaladinServant.this.STAB_GRAB_COOLDOWN;
                PossessedPaladinServant.this.succedGrabbing = false;
                PossessedPaladinServant.this.slash_from_cooldown = PossessedPaladinServant.this.SLASH_FROM_COOLDOWN;
                super.stop();
            }
        });

        // ---- 招 22 / 23 / 24：跳劈 ----
        // 原版注册位置 PossessedPaladinEntity.java:814（22）、819（23）、820（24）。
        //
        // ⚠️ 这不是三招，是<b>一条三状态接力链</b>，三个状态连着跑完一整套动作：
        //     22 起跳前摇（33 tick）—— 第 24 tick 给自己一个朝目标斜上方的冲量，人就飞出去了
        //     ↓ 22 的 goal 演完自己把状态切成 23
        //     23 滞空下劈（最长 100 tick）—— 人还在天上，什么都不做，等落地
        //     ↓ 由 UpdateWithAttack 里的 onGround() 判断接管，一落地立刻切走
        //     24 落地砸（65 tick）—— 第 4 tick 一记范围伤害 + 一圈灵魂剑刃
        //
        // ⚠️ 22 用的不是 PossessedPaladinAttackGoal，而是 PossessedPaladinAttackMinGoal。
        //    差别只有一条：目标必须大于 6 格才起跳（跳劈是拉近距离用的，贴脸了再跳没意义）。
        //
        // ⚠️ 23 的 attackSeetick 传 0，意思是一进状态 23 就锁死朝向 ——
        //    起跳那一刻朝向就定了，半空中再扭头会让落点看起来「拐弯」，很怪。

        // 状态 22：跳劈起手。toTicks(1.67F) = 33 tick；距离区间 6~16 格。
        // 掷骰是 nextFloat() * 100 < JumpRandom（默认 10），通过率 10%。
        this.goalSelector.addGoal(1, new PossessedPaladinAttackMinGoal(this, 0, 22, 23,
                MathUtils.toTicks(1.67F), MathUtils.toTicks(1.67F), 16.0F, 6.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && PossessedPaladinServant.this.getRandom().nextFloat() * 100.0F
                                < (float) PossessedPaladinServant.this.JumpRandom
                        && PossessedPaladinServant.this.jump_cooldown <= 0
                        && PossessedPaladinServant.this.getTarget() != null;
            }
        });

        // 状态 23：滞空下劈。attackfinaltick 传 100（从起跳算起的总时限）。
        // 注册参数里的 attackendstate = 24 其实读不到，去向由那个类的 stop() 写死，见它的注释。
        this.goalSelector.addGoal(1, new PossessedPaladinJumpFallGoal(this, 23, 23, 24,
                100, 0, false, 0.0D));

        // 状态 24：落地砸收招。toTicks(3.25F) = 65 tick。
        // ⚠️ stop() 里必须设 jump_cooldown（JUMP_COOLDOWN = 6 秒）——
        //    这是跳劈唯一的节奏阀，漏设圣骑会一落地就又起跳，变成一只跳蚤。
        this.goalSelector.addGoal(0, new PossessedPaladinStateGoal(this, 24, 24, 0,
                MathUtils.toTicks(3.25F), 0, true, 25.0D) {
            @Override
            public void stop() {
                PossessedPaladinServant.this.jump_cooldown = PossessedPaladinServant.this.JUMP_COOLDOWN;
                super.stop();
            }
        });

        // 状态 32：二阶段跳砸连招。toTicks(5.79F) = 115 tick。
        // 它和一阶段的状态 24 是同一条链的两条岔路：状态 23 落地时按阶段二选一
        // （见 UpdateWithAttack 里那句三元），一阶段去 24，二阶段来这。
        //
        // ⚠️ 这一条是 2D-1 之后补的，之前<b>完全缺失</b>，而且症状很隐蔽：
        //    状态 23 落地照样会把状态切成 32，但没有任何 goal 认领 32 ——
        //    于是 MOVE / LOOK / JUMP 三个行为标志全是空的，别的 goal（跟随主人、近战）
        //    趁虚而入，圣骑一边播着跳砸动画、一边被拖着走；更糟的是 attackTicks 是实体
        //    自己每 tick 涨的，没人管就会一直涨，<b>永远回不到状态 0</b> —— 从此再也
        //    不出招，彻底失控。补上这一条，状态 32 才真正被「锁住」。
        //
        // 这里的 stop() 和上面 24 那条一样要设 jump_cooldown，原版 830 行就是这么写的。
        this.goalSelector.addGoal(0, new PossessedPaladinJumpSmashComboGoal(this, 32, 32, 0,
                MathUtils.toTicks(5.79F), 0, true, 25.0D) {
            @Override
            public void stop() {
                PossessedPaladinServant.this.jump_cooldown = PossessedPaladinServant.this.JUMP_COOLDOWN;
                super.stop();
            }
        });

        // 招 15 / 28（投匕首）不在这里 —— 原版把它注册在招 14 和招 16 中间，
        // 已经搬到上面「招 15 / 28」那一节去了。顺序有实际影响，理由写在那边的注释里。

        // 招 25：盾击（状态 25）。原版注册位置 PossessedPaladinEntity.java:840。
        // 它是个「两段式」招式：先摆一圈 12 格的大盾阵，再贴身摆一圈 2 格的小盾阵，
        // 两段的伤害来源主要都是灵魂盾实体，具体写在 UpdateWithAttack 的状态 25 分支里。
        this.goalSelector.addGoal(1, new PossessedPaladinAttackGoal(this, 0, 25, 0,
                MathUtils.toTicks(5.04F), MathUtils.toTicks(3.75F), 6.0F, false, 0.0D) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && PossessedPaladinServant.this.getRandom().nextFloat() * 100.0F
                        < (float) PossessedPaladinServant.this.ShieldSmashRandom
                        && PossessedPaladinServant.this.shield_smash_cooldown <= 0;
            }

            @Override
            public void stop() {
                PossessedPaladinServant.this.shield_smash_cooldown = PossessedPaladinServant.this.SHIELD_SMASH_COOLDOWN;
                super.stop();
            }
        });

        // ---- 招 29 / 30：侧滚旋转（往左滚 / 往右滚，然后原地转两圈全周旋风斩）----
        // 原版注册位置 PossessedPaladinEntity.java:850（状态 29）与 861（状态 30）。
        // 两招共用一个 goal 类，靠 getNextSideRollSpinType() 决定这轮走哪条：
        // 1 = 向左滚（状态 29），2 = 向右滚（状态 30）。
        // 变体由 randomizeAttacks() 在每招收尾时重掷，所以不会老往同一边滚。
        //
        // ⚠️ 两招的 UpdateWithAttack 伤害分支是复制粘贴的两大段，改的时候要一起改。
        //    它们唯一的差别是翻滚方向和判定射程（17.0F / 18.0F）。

        // 招 29：向左滚。MathUtils.toTicks(4.88F) ≈ 97 tick 总长，判断距离 6 格。
        this.goalSelector.addGoal(1, new PossessedPaladinSideRollSpinGoal(this, 0, 29, 0,
                MathUtils.toTicks(4.88F), 0, 6.0F, true, 10.0D) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && PossessedPaladinServant.this.getRandom().nextFloat() * 100.0F
                        < (float) PossessedPaladinServant.this.SideRollSpinRandom
                        && PossessedPaladinServant.this.getNextSideRollSpinType() == 1
                        && PossessedPaladinServant.this.side_roll_spin_cooldown <= 0;
            }

            @Override
            public void stop() {
                PossessedPaladinServant.this.side_roll_spin_cooldown =
                        PossessedPaladinServant.this.SIDE_ROLL_SPIN_COOLDOWN;
                super.stop();
            }
        });

        // 招 30：向右滚。参数与招 29 完全相同，只有状态号和变体判定不一样。
        this.goalSelector.addGoal(1, new PossessedPaladinSideRollSpinGoal(this, 0, 30, 0,
                MathUtils.toTicks(4.88F), 0, 6.0F, true, 10.0D) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && PossessedPaladinServant.this.getRandom().nextFloat() * 100.0F
                        < (float) PossessedPaladinServant.this.SideRollSpinRandom
                        && PossessedPaladinServant.this.getNextSideRollSpinType() == 2
                        && PossessedPaladinServant.this.side_roll_spin_cooldown <= 0;
            }

            @Override
            public void stop() {
                PossessedPaladinServant.this.side_roll_spin_cooldown =
                        PossessedPaladinServant.this.SIDE_ROLL_SPIN_COOLDOWN;
                super.stop();
            }
        });

        // ---- 招 37：终结技（二阶段专属，十六秒半长连招）----
        // 原版注册位置 PossessedPaladinEntity.java:872。
        //
        // ⚠️ 这块<b>必须待在招 30 之后、「过渡状态」之前</b>。原版的注册顺序是：
        //      …… 招 25(840) → 招 29(850) → 招 30(861) → <b>招 37(872)</b> → 状态 34(883) / 35(892) / 36(897)
        //    本文件按同一顺序排，所以这里就是招 37 的位置，别挪。
        //    （GoalSelector 是 LinkedHashSet：同优先级下<b>先注册的先被问 canUse()</b>，
        //      顺序一变，几招同时满足条件时谁抢到就变了。）
        //
        // 门槛比别的招都高，四个条件缺一不可：
        //   · 二阶段（getPhase() >= 2）—— 一阶段根本不放；
        //   · 概率 FinisherRandom = 25（每 tick 掷一次骰）；
        //   · finisher_cooldown 归零 —— 16 秒的冷却，和招式本身一样长；
        //   · 目标在 7 格以内（goal 类里的 attackrange）。
        this.goalSelector.addGoal(1, new PossessedPaladinFinisherAttackGoal(this, 0, 37, 0,
                MathUtils.toTicks(16.33F), 0, 7.0F, false, 0.0D) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && PossessedPaladinServant.this.getPhase() >= 2
                        && PossessedPaladinServant.this.getRandom().nextFloat() * 100.0F
                        < (float) PossessedPaladinServant.this.FinisherRandom
                        && PossessedPaladinServant.this.finisher_cooldown <= 0;
            }

            @Override
            public void stop() {
                PossessedPaladinServant.this.finisher_cooldown = PossessedPaladinServant.this.FINISHER_COOLDOWN;
                super.stop();
            }
        });

        // ---- 招式之外的过渡状态 ----

        // 状态 5：格挡（举盾 → 反击 → 收招）。优先级 0，原版 PossessedPaladinEntity.java:643。
        //
        // ── 谁会切进状态 5 ──
        // 两个入口，别只记住一个：
        //   1) 实体自己的 hurt()：挨打时判定格挡成功 → 立刻 setAttackState(5)。这是<b>主入口</b>；
        //   2) PossessedPaladinAlertedGoal.stop()：警觉状态（9）被格挡打断时，靠 hasParried 转进来。
        // 两条路都靠上面的 stop() 收尾 —— 清 hasParried、上格挡冷却，缺一不可：
        // hasParried 不清就成了「挡过一次，永远无敌」，冷却不上就是「无限格挡」。
        this.goalSelector.addGoal(0, new IStateGoal(this, 5, 5, 0,
                MathUtils.toTicks(2.92F), MathUtils.toTicks(3.71F)) {
            @Override
            public void stop() {
                PossessedPaladinServant.this.hasParried = false;
                PossessedPaladinServant.this.parry_cooldown = PossessedPaladinServant.this.PARRY_COOLDOWN;
                super.stop();
            }
        });

        // 状态 9（警觉）不在这里 —— 原版把它注册在招 6 和招 10 中间，已经搬上去了。

        // ---- 状态 34（沉睡）/ 35（苏醒）没有搬 ----
        //
        // 原版这两条是「遗迹里躺着的圣骑慢慢醒过来」的开场演出，仆从版不做这件事，
        // 所以下面<b>没有</b>对应 goal：34 / 35 全项目没有任何地方设置，
        // 上面 onSyncedDataUpdated 里那两格永远不会走到，isSleep() 也恒为 false。
        //
        // ⚠️ 别把这段当成「漏了」—— hurt() 的 javadoc 里那条
        // 「原版还多包着 !isSleep()」的差异说明，引用的就是这一段。
        // 哪天想把沉睡演出接回来，照抄下面状态 36（死亡演出）那条 goal 的写法补两条即可。

        // 状态 36：死亡演出。优先级 0，原版 PossessedPaladinEntity.java:897。
        //
        // ⚠️ 原版的 34 / 35 就在这条的上面，内容已经搬过来了，别以为是漏了。
        // 原版这三条是连在一起注册的，本移植在中间插了状态 39，顺序变成 39 / 34 / 35 / 36。
        //
        // 顺带记一笔原版那两条的处境，免得以后翻原版代码时看糊涂：
        // 原版状态 34 的唯一入口是 setSleep(true)，而 setSleep(true) 只在
        // readAdditionalSaveData 里被调用 —— 也就是「存档里写着 is_Sleep=true」才睡，
        // 从来没有播过躺下的过程；35 的唯一入口是 mobInteract（右键点睡着的圣骑）。
        // 我们把它改成了由「待命」状态驱动，所以才有 39 这一格。
        //
        // 另外原版的 talk1~talk3（苏醒演讲）没搬，理由见 UpdateWithAttack 的状态 36 分支；
        // 睡眠呼噜和起身台词在 #98 单独处理。
        this.goalSelector.addGoal(0, new IStateGoal(this, 36, 36, 0, MathUtils.toTicks(12.0F), 0));

        // 其余 9 招 Stage 2C 在这里往上挂。
    }

    // ==================================================================
    //  十二、数量上限
    // ==================================================================

    /**
     * 数量上限。
     *
     * <p>只在「被主人召唤出来」这一条路径上拦（MOB_SUMMONED）。自然生成、刷怪蛋
     * 走的是别的 spawnType，不受这个上限约束 —— 这也是本仓库其它 BOSS 仆从的既有做法。
     *
     * <p>返回 null 是 Forge 约定的「生成失败」信号，<b>不是</b>返回数据。返回 null 后
     * 召唤方会把这个实体丢掉，等于这次召唤白费。
     */
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
                                        MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData,
                                        @Nullable CompoundTag pDataTag) {
        if (pReason == MobSpawnType.MOB_SUMMONED && this.getTrueOwner() instanceof Player player) {
            if (countServants(player) >= MobsConfig.PossessedPaladinServantLimit.get()) {
                return null;
            }
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    /**
     * 数一数这位玩家名下已经有多少只堕落圣骑。
     *
     * <p>用 {@code getAllEntities()} 全量扫而不是维护一个计数器：召唤/死亡/卸载区块
     * 都会改变存活数量，自己记账很容易和实际对不上。
     */
    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof PossessedPaladinServant servant
                        && servant.getTrueOwner() == player) {
                    count++;
                }
            }
        }
        return count;
    }

    // ==================================================================
    //  十三、每 tick
    // ==================================================================

    @Override
    public void tick() {
        super.tick();

        // ---- 待命时清掉手头的目标 ----
        //
        // ⚠️ 光靠 setTarget 的覆写还不够，这一句是必须的：
        // 那道闸只拦「新塞进来的目标」，而玩家按下待命键的那一刻，
        // 圣骑手里很可能已经锁着一个敌人了 —— 不主动清掉的话它会带着这个目标
        // 把当前的架打完，甚至追出去。所以每 tick 检查一次，不干净就清掉。
        //
        // ⚠️ 清之前要排除「还手目标」：setTarget 的覆写特意给挨打还手留了口子
        // （谁打我我打谁），这里要是不排除，就会被这一句当场清掉 ——
        // 结果还是挨打不还手，白留那个口子。
        //
        // ⚠️ 只在服务端清。客户端调 setTarget 没有意义（目标本来就不由客户端定），
        // 平白多一次无用的状态变更。
        //
        // ⚠️ isStaying() 就是「待命」：Goety 的 Summoned 里
        // isStaying() = 标志位2 && !isCommanded() && 没骑东西，由玩家右键切换。
        // 它是同步标志位，读档后会自动恢复，所以这里不需要额外存 NBT。
        if (!this.level().isClientSide && this.isStaying()) {
            LivingEntity currentTarget = this.getTarget();
            if (currentTarget != null && currentTarget != this.getLastHurtByMob()) {
                this.setTarget(null);
            }
        }

        // ---- 抓取系统的安全网（原版 PossessedPaladinEntity.java:451-457）----
        // 这两句是「突刺抓取」能成立的前提，也是它不会失控的保险：
        //
        // 1) 被抓住的受害者坐在圣骑身上时，每 tick 强制把它自己的潜行键松开 ——
        //    否则玩家会靠按住潜行直接从坐骑位上滑下来，抓取就白做了。
        // 2) 只要当前状态不是 19（突刺中）或 20（处决中），就把所有乘客<b>弹下去</b>。
        //    这一句看着多余，其实是<b>兜底</b>：万一处决演到一半被打断、状态被别的招顶掉，
        //    或者状态机出了任何意外，受害者也不会永远挂在圣骑背上。
        //    ⚠️ 少了这一句，被抓住的实体会永久骑在圣骑身上 —— 那是删不掉的 bug。
        if (this.isVehicle() && this.getFirstPassenger() != null) {
            this.getFirstPassenger().setShiftKeyDown(false);
        }

        if (this.getAttackState() != 20 && this.getAttackState() != 19) {
            this.ejectPassengers();
        }

        // 冷却递减。少减一个，对应的招式就永远只能用一次。
        if (this.parry_cooldown > 0) {
            --this.parry_cooldown;
        }
        if (this.slam_cooldown > 0) {
            --this.slam_cooldown;
        }
        if (this.throw_cooldown > 0) {
            --this.throw_cooldown;
        }
        if (this.stab_grab_cooldown > 0) {
            --this.stab_grab_cooldown;
        }
        if (this.flip_smash_cooldown > 0) {
            --this.flip_smash_cooldown;
        }
        if (this.side_roll_spin_cooldown > 0) {
            --this.side_roll_spin_cooldown;
        }
        if (this.backstep_cooldown > 0) {
            --this.backstep_cooldown;
        }
        if (this.slash_from_cooldown > 0) {
            --this.slash_from_cooldown;
        }
        if (this.double_slash_cooldown > 0) {
            --this.double_slash_cooldown;
        }
        if (this.jump_cooldown > 0) {
            --this.jump_cooldown;
        }
        if (this.shield_smash_cooldown > 0) {
            --this.shield_smash_cooldown;
        }
        if (this.finisher_cooldown > 0) {
            --this.finisher_cooldown;
        }
        if (this.trident_throw_spin > 0) {
            --this.trident_throw_spin;
        }

        // 受击后的无敌帧。原版在 customServerAiStep() 里减（那边是服务端专属），
        // 我们统一放这里 —— 多个十几行条件分支除了难读，没有任何实际差别：
        // 这个字段只有 hurt() 会读，而 hurt() 在客户端本来就走不到这里。
        if (this.BossInvulnerabilityTime > 0) {
            --this.BossInvulnerabilityTime;
        }

        // 待机呼吸动画：只在「没有任何招式在进行」时播。
        // 放在 isClientSide 里是因为动画纯粹是客户端的事，服务端算它纯属浪费。
        if (this.level().isClientSide) {
            this.idleAnimationState.animateWhen(this.getAttackState() == 0, this.tickCount);
        }
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        setPersistenceRequired();
    }

    /**
     * 死亡。原版 PossessedPaladinEntity.java:3370。
     *
     * <p>原版圣骑不播原版那套「红了 20 tick 就消失」的死亡，而是切进状态 36 演一整段
     * 12 秒的死亡动画（挣扎、遗言、爆魂）。所以这里要动三样东西：
     *
     * <ol>
     *   <li>{@code super.die(source)} 先走完常规流程 —— 血量归零、记录击杀者、
     *       通知主人（Goety 的 {@code Summoned} 在这一步处理）。</li>
     *   <li><b>{@code this.deathTime = 0}</b>：原版的死亡计时器归零，而且从此
     *       <b>再也不让它涨</b>（见 {@link #tickDeath()}）。原版 {@code tickDeath()}
     *       会在 {@code deathTime >= 20} 时把实体删掉，我们把它整个覆写掉了。</li>
     *   <li><b>{@code setAttackState(36)}</b>：灵魂。所有死亡相关的表现 ——
     *       死亡动画、粒子、遗言、音效 —— 全部挂在状态 36 上，切过去才会开始演。</li>
     * </ol>
     *
     * <p>{@code setNoGravity(false)} 是防止「死在半空中」的情况（比如跳劈途中被打死）
     * 尸体一直浮着不落地。
     *
     * <h2>任何死法都不长红斑</h2>
     * 状态 36 那 14 秒演出<b>照播</b> —— 倒地、遗言、爆魂一个不少，
     * 主人处决也好、被怪打死也好，完全一样。而 MC 原版那套「死亡皮」
     * （红光 + 侧倒）<b>两种死法都没有</b>，因为 {@code deathTime} 被永远摁在 0
     * （见 {@link #tickDeath()}）。
     *
     * <p>⚠️ 这一点上我们是<b>对齐原版</b>的，不是自创：原版传奇怪物也是两种死法都不发红
     * —— 它在自己类里声明了 {@code public int deathTime;} 把 {@code LivingEntity}
     * 那个同名字段<b>遮蔽</b>掉，于是原版那个永远是 0，渲染器读到的也就永远是 0。
     * 我们没走遮蔽那条路（同名字段容易把人绕晕），改成每 tick 写回 0，效果一模一样。
     *
     * <h2>为什么摁死它就能去掉「原版那套死亡皮」</h2>
     * <b>Minecraft 原版</b>的渲染器只认 {@code deathTime > 0} 这一个开关，而它同时管着两件事 ——
     * <ol>
     *   <li>{@code LivingEntityRenderer.getOverlayCoords()}：给整只怪糊一层红色遮罩
     *       （就是玩家看到的<b>全身变红</b>）；</li>
     *   <li>{@code LivingEntityRenderer.setupRotations()}：把模型绕 Z 轴一点点拧倒
     *       （就是玩家看到的<b>直挺挺侧倒</b>）。</li>
     * </ol>
     * 这两样都是 Minecraft 原版硬加在所有生物身上的皮，不是传奇怪物画的动画。
     * 摁死 {@code deathTime}，两样一起消失 —— <b>这正是要的效果</b>。
     *
     * <p>⚠️ 那「倒地」呢？处决时该播的倒地是<b>传奇怪物自己那段</b>：
     * 状态 36 的 {@code PossessedPaladinAnimations4.death2}，走 {@code attackTicks}，
     * 和 {@code deathTime} 毫无关系 —— 所以<b>演出照播，只是脱掉了原版那层皮</b>。
     *
     * <p>⚠️ 渲染器那边<b>故意没有</b>覆写 {@code setupRotations()} 去把它补回来，
     * 别以后看着「怎么不倒了」就顺手加 —— 加回来 = 原版侧倒又回来了。
     * 详见 {@code PossessedPaladinServantRenderer} 里那段注释。
     *
     * <p>注意这里仍然调用 {@code super.die()}。血量归零、记录击杀者、通知主人
     * 这些常规流程一个都不能少。
     */
    @Override
    public void die(DamageSource pDamageSource) {
        super.die(pDamageSource);
        this.deathTicks = 0;
        this.deathTime = 0;
        this.setAttackState(36);
        this.setNoGravity(false);
    }

    /**
     * 死亡计时器。原版 PossessedPaladinEntity.java:3377。
     *
     * <p>覆写原版是为了<b>把 20 tick 拖到 280 tick</b>（{@code toTicks(14.0F)}，14 秒）——
     * 状态 36 的演出时刻表一直到第 270 tick 都还有内容，按原版 1 秒就删掉的话，
     * 遗言和爆魂全看不见。
     *
     * <p>⚠️ 这里<b>没有</b>调用 {@code super.tickDeath()}。原版基类那个方法里就写着
     * 「{@code deathTime >= 20} → 广播事件 60 → 删除实体」，调了就等于把 280 改回 20。
     * 所以删除和广播都得自己来，一行都不能少：少了 {@code broadcastEntityEvent(this, 60)}
     * 的等价物（{@code gameEvent}）客户端不会收到死亡事件，少了 {@code remove} 尸体永远不消失。
     *
     * <p>参考实现：{@code OvergrownColossusServant.tickDeath()}，同样的套路，只是它的时长是 60。
     *
     * <h2>把 {@code deathTime} 摁死在 0</h2>
     * 每 tick 都往原版的 {@code deathTime} 里写回一个 0，让它永远涨不上去。
     *
     * <p>为什么摁死它就够：<b>Minecraft 原版</b>渲染器只认 {@code deathTime > 0}
     * 这一个开关，它同时驱动 {@code getOverlayCoords()} 的红遮罩和
     * {@code setupRotations()} 的绕 Z 轴侧倒。摁死在 0，两样一起消失 ——
     * 见 {@link #die} 的说明。
     *
     * <p>⚠️ 原理补一句：<b>JVM 读字段不做虚分派</b>。{@code LivingEntityRenderer}
     * 是按 {@code LivingEntity.deathTime} 编译出来的，它读的永远是 {@code LivingEntity}
     * 里声明的那一个字段。原版传奇怪物是另声明一个同名字段把它遮蔽掉（自己写的都是自己那个，
     * 原版那个保持 0）；我们不遮蔽，写的<b>正是</b> {@code LivingEntity} 那一个。
     * 两条路殊途同归，结果都是渲染器读到 0。
     *
     * <p>⚠️ 删尸体的活儿不能再看 {@code deathTime} 了（它永远是 0），
     * 所以改用 {@link #deathTicks}。这就是为什么要有第二个计数器。
     */
    @Override
    protected void tickDeath() {
        ++this.deathTicks;

        // ⚠️ 每 tick 往原版的 deathTime 里写回一个 0，让它<b>永远涨不上去</b>。
        //    写死 0 而不是「不递增」，是为了万一别处动过它也能拉回来。
        this.deathTime = 0;

        if (this.deathTicks == MathUtils.toTicks(14.0F)) {
            this.remove(Entity.RemovalReason.KILLED);
            this.gameEvent(GameEvent.ENTITY_DIE);
        }
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    // ==================================================================
    //  十三之二、与主人交互（修复 + 降阶段）
    // ==================================================================

    /** 用诅咒金属块修一次能回多少血。想调数值改这里，别去方法里改字面量。 */
    private static final float CURSED_METAL_REPAIR_AMOUNT = 30.0F;

    /**
     * 主人拿<b>诅咒金属块</b>右键圣骑 → 回 30 点血，消耗一块。
     *
     * <p>这一条是拿来<b>顶替原版「脱战回血」</b>的。原版在
     * {@code regainHealthWithoutTarget} 里：脱战 40 tick 之后，每 15 tick 自己回 20 点血。
     * 那是给 BOSS 用的续航 —— 玩家打断不了，只能眼睁睁看它回满。
     * 当仆从要是一直这样，圣骑打完一架自己就满血了，主人完全没有参与感。
     * 改成「要回血就得自己掏材料来修」，既保留了修复能力，又不是白送。
     *
     * <p><b>手感对齐原版 Minecraft 的铁傀儡修复</b>（拿铁锭右键铁傀儡）：
     * 音效用 {@code IRON_GOLEM_REPAIR}。隔壁 {@code OvergrownColossusServant}
     * 拿苔藓块修自己也是这套写法，两边保持一致，将来一起改也好找。
     *
     * <p>⚠️ 三个判断缺一不可：
     * <ol>
     *   <li><b>是主人</b> —— 仆从是认主的，路人拿方块右键不该能白嫖治疗。</li>
     *   <li><b>没满血</b> —— 满血右键就该「什么都不发生」，否则手一抖就废掉一块材料。
     *       而且这一条不满足时会落到最后的 {@code super.mobInteract}，
     *       方块拿在手里该有的正常交互（放方块、开别的界面）不会被我们吞掉。</li>
     *   <li><b>没在死亡演出里</b> —— 倒地那 14 秒血条已经归零，这时候 {@code heal}
     *       是白喂，材料却照扣不误，所以直接不许修。</li>
     * </ol>
     *
     * <p>创造模式的玩家不扣材料（和铁傀儡修复一致）。
     *
     * <p>⚠️ 一次固定回 30 点，<b>差多少血都只回 30</b>：{@code heal} 自己会把溢出
     * 截掉（不会超过 {@code getMaxHealth()}），所以差 5 点血时用一块是有点亏的。
     * 这是按「1 次恢复 30 点」的需求写死的；想改成「缺多少补多少」，
     * 就把上面的 {@code CURSED_METAL_REPAIR_AMOUNT} 换成 {@code getMaxHealth() - getHealth()} 即可。
     *
     * <p>⚠️ 认方块用的是 {@code ModBlocks.CURSED_METAL_BLOCK.get().asItem()}。
     * Goety 的诅咒金属块走的是 {@code register(name, supplier)} 那个重载
     * （和 {@code PALE_STEEL_BLOCK}、{@code DARK_ALLOY_BLOCK} 同一个），
     * 会连 {@code BlockItem} 一起注册，所以 {@code asItem()} 一定拿得到东西。
     * 它的注册名是 {@code goety:cursed_metal_block}，中文名「诅咒金属块」。
     *
     * <h2>⚠️ 这个方法和原版的 {@code mobInteract} 是<b>两回事</b>，别对着原版核对</h2>
     * 原版也有一个 {@code mobInteract}，但它是给<b>休眠系统</b>用的 —— 右键把沉睡的圣骑叫醒：
     * <pre>
     *   沉睡中（状态 34）→ 切成 35 唤醒，然后返回 FAIL
     *   醒着          → 直接返回 {@code InteractionResult.FAIL}
     * </pre>
     * 也就是说，<b>原版圣骑醒着时右键它，什么都不会发生</b>（FAIL 会把这次点击整个吞掉）。
     * 那是 BOSS 该有的脾气：不想被玩家摆弄。
     *
     * <p>仆从<b>不能</b>照抄这个。{@code FAIL} 会把 Goety 仆从自带的右键功能
     * （改姿态、下指令那些）一并挡在门外，主人会以为自己的仆从坏了。
     * 所以这里的兜底是 {@code super.mobInteract(...)} 而不是 {@code FAIL} ——
     * 「不能修就把点击让给别人」，这是仆从版和怪物的根本区别。
     *
     * <h2>本方法一共有两条交互，靠血量和手里的东西岔开</h2>
     * <table border="1">
     *   <caption>右键圣骑会发生什么</caption>
     *   <tr><th>手里的东西</th><th>圣骑状态</th><th>结果</th></tr>
     *   <tr><td>诅咒金属块</td><td>没满血</td><td>回 {@code CURSED_METAL_REPAIR_AMOUNT} 点血</td></tr>
     *   <tr><td>金属残骸（LM）</td><td>满血的二阶段</td><td>降回一阶段</td></tr>
     *   <tr><td>其它任何东西</td><td>—</td><td>交给 {@code super}</td></tr>
     * </table>
     * 两条的血量条件正好互补（一条要「没满血」、一条要「满血」），所以谁都不会抢谁。
     * 具体判定在 {@link #tryRevertToFirstPhase(Player, ItemStack, boolean)}。
     */
    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        // getItemInHand 拿的是「玩家这一只手正拿着的东西」。
        // ⚠️ 不要图省事换成 getMainHandItem()：那样副手右键也会被当成主手来判断。
        ItemStack itemstack = pPlayer.getItemInHand(pHand);

        boolean isOwner = this.getTrueOwner() != null && pPlayer == this.getTrueOwner();

        if (isOwner
                && this.getHealth() < this.getMaxHealth()
                && !this.isDeadOrDying()
                && itemstack.is(ModBlocks.CURSED_METAL_BLOCK.get().asItem())) {
            if (!pPlayer.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, 1.0F);
            this.heal(CURSED_METAL_REPAIR_AMOUNT);

            // 修复粒子：服务端才放得出（sendParticles 在 ServerLevel 上）。
            // 客户端啥也不用做 —— 粒子包会自己广播过去。
            if (this.level() instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 7; ++i) {
                    // 三个方向各来一点随机初速度，粒子才会「炸开」而不是笔直向上飘。
                    double d0 = this.random.nextGaussian() * 0.02D;
                    double d1 = this.random.nextGaussian() * 0.02D;
                    double d2 = this.random.nextGaussian() * 0.02D;
                    // ⚠️ 这里的 count 传 0 <b>不是「不放粒子」</b>，是原版 sendParticles 的
                    //    特殊约定：count == 0 时「只放 1 颗，并把后面三个偏移量当成它的初速度」。
                    //    所以真正决定数量的是外面这层 for 循环（7 颗）。别把 0 改成 7 ——
                    //    那样会变成 7×7=49 颗「原地不动」的粒子，看起来是一团糊住的雾。
                    serverLevel.sendParticles(ParticleTypes.SOUL,
                            this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D),
                            0, d0, d1, d2, 0.5F);
                }
            }

            // ⚠️ 返回 SUCCESS 而不是 PASS：SUCCESS 会让玩家客户端播一下挥手动作，
            //    「修了一下」的手感就是从这里来的。隔壁蔓生巨像用的也是 SUCCESS。
            return InteractionResult.SUCCESS;
        }

        // 再试第二条路：金属残骸降阶段。
        // 两条路的血量条件正好互补（上面那条要「没满血」，这条要「满血」），
        // 所以顺序无所谓，谁都不会抢谁的。
        if (this.tryRevertToFirstPhase(pPlayer, itemstack, isOwner)) {
            return InteractionResult.SUCCESS;
        }

        // 两条路都不满足 —— 一律交还给父类，
        // 别把 Goety 仆从自带的那些右键功能（比如潜行右键的指令）挡掉。
        return super.mobInteract(pPlayer, pHand);
    }

    /**
     * 主人拿 LM 原版物品<b>金属残骸</b>右键<b>满血的二阶段</b>圣骑 → 把它拨回一阶段。
     *
     * <p>这是把原版的「脱战自动降阶段」搬到玩家手上。原版在
     * {@code PossessedPaladinEntity.tick()} 有一条：脱战够久、血量回到 65% 以上就
     * {@code setPhase(1)}，好让玩家能重新打一遍完整的 BOSS 战。那条<b>不能</b>照搬
     * （理由见 {@code PossessedPaladinSecondPhaseGoal#stop()} 的注释），
     * 但「我想让它变回一阶段」这个诉求本身是合理的 —— 于是改成<b>玩家掏材料手动触发</b>。
     *
     * <h2>⚠️ 四个条件缺一不可，第三条是防死循环的硬性前提</h2>
     * <ol>
     *   <li><b>是主人</b> —— 和修复那条一致，仆从认主。</li>
     *   <li><b>手拿的是金属残骸</b> —— 凭这个和诅咒金属块那条岔开。</li>
     *   <li><b>已经满血</b> —— <b>不是</b>手感上的讲究：{@link #shouldEnterSecondPhase()}
     *       只在血量低于 65% 时才成立（见 {@link Crackiness}）。满血时档位是
     *       {@code NONE}，降完阶段<b>不会</b>立刻又满足变身条件。
     *       要是允许半血降阶段，下一 tick 就会重新变身 —— 材料白花，
     *       而且状态 26 全程免伤，等于白送七秒无敌。</li>
     *   <li><b>当前没在出招</b>（{@code getAttackState() == 0}）—— 中途降有两个麻烦：
     *       ① 正在播的二阶段招式会红绿闪烁；② 最阴的是正好卡在变身演出（状态 26）里，
     *       那条线在第 49 tick 会把阶段<b>又写回 2</b>
     *       （见 {@code PossessedPaladinSecondPhaseGoal#tick()}），
     *       材料花了、阶段却没降。所以干脆等它站定再动手。</li>
     * </ol>
     *
     * <p>颜色<b>不用</b>手动还原：全项目的颜色都写成
     * {@code getPhase() >= 2 ? this.uR : 0.0F} 这种即时求值的形式，
     * 而 {@code uR/uG/uB} 三个字段本身是常量、从不被改写。
     * 所以 {@code setPhase(1)} 一写完，下一次渲染就自动变回青绿。
     *
     * <h2>⚠️ 认物品用的是 LM 的 {@code ModItems}</h2>
     * 注册名 {@code legendary_monsters:metal_debris}，中文名「金属残骸」。
     * LM 没装的话这个类根本加载不出来 —— 和本文件里其它 LM 引用是同一处境，
     * 所以这里不再单独做「LM 在不在」的判断。
     *
     * @return 有没有真的降成功。返回 false 时调用方应该把这次右键让给别人。
     */
    private boolean tryRevertToFirstPhase(Player pPlayer, ItemStack itemstack, boolean isOwner) {
        // 六个条件一次性判完，任何一个不满足都直接放行。
        // 顺序按「最便宜的先判」排：布尔和血量都是读字段，最后才碰 ItemStack。
        if (!isOwner
                || this.isDeadOrDying()
                || !this.getIsSecondPhase()
                || this.getHealth() < this.getMaxHealth()
                || this.getAttackState() != 0
                || !itemstack.is(ModItems.METAL_DEBRIS.get())) {
            return false;
        }

        // 创造模式不扣材料，和修复那条、以及原版铁傀儡修复保持一致。
        if (!pPlayer.getAbilities().instabuild) {
            itemstack.shrink(1);
        }

        this.setPhase(1);

        // 音效：下行的「断电」音，和变身那种上扬的音正好相反，
        // 一听就知道是在往回走，而不是又变身了。
        this.playSound(SoundEvents.BEACON_DEACTIVATE, 1.0F, 1.0F);

        if (this.level() instanceof ServerLevel serverLevel) {
            // 一圈从脚下往外扩散的灵魂粒子，读作「力量正从盔甲里泄出去」。
            // 半径 1.2 格上均匀取 24 个点，每点给一点朝外的初速度。
            for (int i = 0; i < 24; ++i) {
                double angle = Math.PI * 2.0D * ((double) i / 24.0D);
                double dirX = Math.cos(angle);
                double dirZ = Math.sin(angle);
                // 和上面修复那条一样的约定：count 传 0 表示「只放 1 颗，
                // 后面三个偏移量当它的初速度」。别改成 1。
                serverLevel.sendParticles(ParticleTypes.SOUL,
                        this.getX() + dirX * 1.2D, this.getY() + 0.2D, this.getZ() + dirZ * 1.2D,
                        0, dirX * 0.05D, 0.08D, dirZ * 0.05D, 1.0F);
            }

            // 身上再炸一把，比修复那条（7 颗）更散更密，读作「盔甲里的东西正在褪掉」。
            for (int i = 0; i < 15; ++i) {
                double d0 = this.random.nextGaussian() * 0.06D;
                double d1 = this.random.nextGaussian() * 0.06D;
                double d2 = this.random.nextGaussian() * 0.06D;
                serverLevel.sendParticles(ParticleTypes.SOUL,
                        this.getRandomX(1.2D), this.getRandomY() + 0.5D, this.getRandomZ(1.2D),
                        0, d0, d1, d2, 0.6F);
            }
        }

        return true;
    }

    // ==================================================================
    //  十四、友军判定
    // ==================================================================

    /**
     * 谁算「自己人」。
     *
     * <p><b>这个覆写是必须的，不是锦上添花。</b>原版的 {@code isAlliedTo} 只认记分板队伍
     * （没队伍一律返回 false），而传奇怪物的招式代码里到处拿 {@code isAlliedTo} 当友军过滤器
     * —— 砸地的直线范围伤害、灵魂尖柱实体（{@code SoulPillarEntity.damage}）的伤害判定，
     * 都是这么写的。原版圣骑是 BOSS，全场皆敌，这个过滤器怎么写都不会误伤；可当仆从之后
     * 主人身上没有队伍，过滤器就整个失效，圣骑的每一发范围招式都会把主人一起打飞。
     *
     * <p>在这里补一次，14 个招式全部受益。（蔓生巨像当年就是没覆写它，才被迫在
     * {@code AreaAttack} 里手写 {@code MobUtil.areAllies} 来兜底。）
     *
     * <p><b>千万不要在这里调 {@code MobUtil.areAllies}。</b>那个方法内部会回头调
     * {@code isAlliedTo}（MobUtil 第 198 行），会形成
     * isAlliedTo → areAllies → isAlliedTo 的无限递归，直接栈溢出。
     */
    @Override
    public boolean isAlliedTo(Entity pEntity) {
        if (super.isAlliedTo(pEntity)) {
            return true;
        }
        if (pEntity == null) {
            return false;
        }
        LivingEntity owner = this.getOwner();
        if (owner != null) {
            if (pEntity == owner) {
                return true;
            }
            // 同一个主人手下的其他仆从之间不互相伤害
            if (pEntity instanceof OwnableEntity) {
                OwnableEntity other = (OwnableEntity) pEntity;
                if (other.getOwner() == owner) {
                    return true;
                }
            }
            // 主人的盟友（同队伍的其他玩家等）
            if (owner.isAlliedTo(pEntity)) {
                return true;
            }
        }
        return false;
    }

    // ==================================================================
    //  十五、招式辅助
    // ==================================================================

    /**
     * 出招后的硬直。原版规则是「格挡冷却中 1 tick，否则 3 tick」—— 举着盾时出招更快。
     * 基类默认返回 0（等于没有硬直）。
     */
    @Override
    public int attackDelayTicksValue() {
        return this.parry_cooldown <= 0 ? 3 : 1;
    }

    /**
     * 「现在这一下能不能格挡掉」。原版 {@code PossessedPaladinEntity.canParry()}。
     *
     * <p>三个条件缺一不可：
     * <ol>
     *   <li>{@code !hasParried} —— 一轮里只能格挡<b>一次</b>。用它兜住，防止被连击时反复触发；</li>
     *   <li>{@code parry_cooldown <= 0} —— 格挡有 125 tick（6.25 秒）冷却，
     *       没冷却完就只能老老实实挨打；</li>
     *   <li>{@code 状态是 0 或 9} —— 只有<b>待机</b>和<b>警觉</b>这两种「没在出招」的状态才举得起盾。
     *       正在挥剑、正在砸地的时候是没法中途收招去挡的。</li>
     * </ol>
     *
     * <p>⚠️ 注意条件 3 用「状态 5」是不行的 —— 状态 5 本身就是格挡动作。
     * 这两条判据合起来看才是完整的盾牌逻辑：
     * <pre>
     * canParry()  ：还没开始挡，问「要不要挡」
     * isBlockin() ：已经挡上了（状态 5 且还没到收招时刻），问「盾还举着吗」
     * </pre>
     */
    public boolean canParry() {
        return !this.hasParried
                && this.parry_cooldown <= 0
                && (this.getAttackState() == 0 || this.getAttackState() == 9);
    }

    /**
     * 「盾牌是不是还举在身前」。原版 {@code PossessedPaladinEntity.isBlockin()}。
     *
     * <p>状态 5 是整段格挡演出（举盾 → 反击 → 收招），整整 {@code toTicks(2.92F)}=58 tick。
     * 但盾只在<b>前 18 tick</b>（{@code toTicks(0.92F)}）有效 —— 之后圣骑已经挥出去反击了，
     * 再免伤就变成「全程无敌」。
     *
     * <p>所以这里的时间上限是这招的平衡点，不是随手写的数字。
     */
    public boolean isBlockin() {
        return this.getAttackState() == 5
                && this.attackTicks < MathUtils.toTicks(0.92F);
    }

    /**
     * 记下目标此刻的坐标。砸地起手时存一次，落地时拿它算地裂往哪边延伸。
     *
     * <p>原版这个方法不带参数（内部自己读 {@code target()}）。隔壁还有一个收 x/y/z 三个参数的
     * 同名方法，见下面那个重载 —— 两个是<b>不同的方法</b>，别搞混。
     */
    public void saveTargetPos() {
        if (targetIsNotNull()) {
            this.lastTargetX = this.target().getX();
            this.lastTargetY = this.target().getY();
            this.lastTargetZ = this.target().getZ();
        }
    }

    /**
     * 直接记下<b>指定的</b>坐标，不看目标在哪。
     *
     * <p>和上面那个无参版的区别就这一条：无参版永远存「目标此刻站的地方」，
     * 这个版本存的是调用方算出来的任意一点。二阶段跳砸连招（状态 32）第 30 tick
     * 要找的落点是「目标正前方再往外一点」，不是目标脚下，所以必须用这个版本。
     *
     * <p>⚠️ 原版这个方法定义在 {@code IAnimatedMiniBoss} 里（对应我们的
     * {@code IAnimatedMiniBossServant}），而圣骑继承的是 {@code IAnimatedBossServant} ——
     * 两条继承链是平行的，那边的方法这边继承不到，所以得自己写一份。
     * 判定条件（{@code targetIsNotNull()}）原样保留：目标没了就不更新落点，
     * 沿用上一次存的坐标。
     */
    public void saveTargetPos(double x, double y, double z) {
        if (targetIsNotNull()) {
            this.lastTargetX = x;
            this.lastTargetY = y;
            this.lastTargetZ = z;
        }
    }

    /**
     * 把 {@link #saveTargetPos()} 记下的坐标打包成一个 {@code Vec3}。
     *
     * <p>给「朝存档位置扑过去」这种用法准备的，比如二连斩砸（状态 3）的第三下：
     * {@code calculatedDashToPositon(0.15F, lastTargetPos())}。
     *
     * <p>⚠️ 原版这个方法定义在 {@code IAnimatedMiniBoss} 里（对应我们的
     * {@code IAnimatedMiniBossServant}），而<b>圣骑继承的是 {@code IAnimatedBossServant}</b>——
     * 两个基类是平行的，另一个只含限伤，没有这个方法。所以这里得自己写一份。
     * 顺带一提，{@code lastTargetX/Y/Z} 三个字段圣骑也是自己声明的，
     * 不是从基类继承来的（原因同上）。
     */
    public Vec3 lastTargetPos() {
        return new Vec3(this.lastTargetX, this.lastTargetY, this.lastTargetZ);
    }

    // ==================================================================
    //  十六、每 tick 的招式驱动
    // ==================================================================

    /**
     * 每 tick 推进招式。原版把伤害判定挂在 {@code aiStep()} 里。
     *
     * <p>为什么是 {@code aiStep()} 而不是 {@code tick()}：{@code aiStep()} 是原版放 AI 逻辑的
     * 地方，每 tick 调用一次，服务端和客户端都会走。而 {@code attackTicks} 的自增由基类
     * {@code IAnimatedMonsterServant.tick()} 负责，这里只读不写。
     *
     * <p>原版这个方法里还有一行 Boss 血条刷新，我们不要血条，省掉。
     */
    @Override
    public void aiStep() {
        super.aiStep();
        this.UpdateWithAttack();
    }

    /**
     * 按 {@code attackState} 分派伤害判定。
     *
     * <p>原版这里有两千多行，14 个招式各占一个<b>并列的 {@code if}</b>（不是 {@code else if}，
     * 所以理论上可以同时命中多个分支，但状态互斥，实际不会）。目前已经搬过来的有：
     * 状态 2（二连斩）、5（格挡反击）、6（砸地第一下）、8（砸地第二下）、10（后空翻）、
     * 12 / 14（翻跟头砸）、15 / 28（投匕首）、16 / 18（剑气斩）、19（突刺抓取起手）、
     * 20（抓取处决）、22 / 23 / 24（跳劈三段）、25（盾击）、26（二阶段变身）、
     * 29 / 30（侧滚旋转）、31（二阶段砸地第二下）、32（二阶段跳砸连招）、33（二阶段突刺收招）、
     * 36（死亡演出）、38（二阶段掷三叉戟），其余招式后续阶段继续补。
     *
     * <p>其中<b>状态 31 是「和状态 8 长得一样、但多了两下」</b>：主体判定和状态 8 完全一致
     * （同样的灵魂柱、同样的横扫、同样的同心圆冲击波），额外多出第 45 tick 的收缩光环
     * 和第 53 tick 的<b>一整圈灵魂冲击弹幕</b>（{@link #soulStrikeRing}）。
     * 二阶段的圣骑就是靠这两下把「砸地」从单体控场升级成清场技的。
     *
     * <p>方法开头那几个局部变量是 14 招共用的「招式时刻表」，原版写在方法最前面。
     * 搬到哪一招就打开哪几个，不要提前删 —— 后面的招式还会用到。
     */
    public void UpdateWithAttack() {
        // 14 招共用的「招式时刻表」，原版写在方法最前面。搬哪招就打开哪几个。
        float sweepSize = 2.0F;
        float sweepRot = 20.0F;
        float bigSweepHeight = 3.0F;
        float bigSweepAdditionalY = 1.0F;
        float doubleSlashRange = 3.5F;
        float smashRange = 5.0F;
        // 翻跟头砸（状态 12 / 14）落地那一下用的两个数。
        // hitBoxWidth 是判定箱相对身体中心的横向偏移（取负值 = 往身后偏），
        // verticalAttackHeight 是判定箱的高度跨度。原版分别写在 1557 / 1295 行，
        // 这里提前到方法头部统一放，值一样。
        float hitBoxWidth = 0.5F;
        float verticalAttackHeight = 2.0F;
        // 二连斩两刀分别落在第 20 / 36 tick（招式总长 68 tick）。
        float doubleSlashAttack1 = 20.0F;
        float doubleSlashAttack2 = 36.0F;
        // 第三下（砸地）落在第 62 tick。只有状态 3「二连斩砸」会用到它，
        // 状态 2「二连斩」打到第二刀就收招了。
        float doubleSlashAttack3 = 62.0F;

        // 下面四个是「拿身体朝向算前后左右」用的，原版写在方法最前面，多招共用。
        // f / f1 是身体朝向的单位向量（cos / sin），用来算「身前 / 身后」的偏移。
        // vecX / vecZ 是同一个方向再转 90 度，也就是「身体左侧」，用来算横向偏移。
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180F));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180F));
        double theta = (double) this.yBodyRot * (Math.PI / 180D);
        ++theta;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);

        // 原版写作 int slamAttackTick = MathUtils.toTicks(1.17F)，即 23 tick。
        int slamAttackTick = MathUtils.toTicks(1.17F);

        // ---------------------------------------------------------------
        //  砸地（attackState 6）
        // ---------------------------------------------------------------
        if (this.getAttackState() == 6) {
            // 起手后第 18 tick：把目标此刻的位置记下来。打空时后续动作要有个去处。
            if (this.attackTicks == slamAttackTick - 5) {
                this.saveTargetPos();
            }

            // 第 20 tick：举剑挥下的特效 + 挥空音效。纯客户端视觉。
            if (this.attackTicks == slamAttackTick - 3) {
                double d0 = this.getX();
                double d1 = this.getY() + (double) bigSweepAdditionalY;
                double d2 = this.getZ();
                float yaw = (float) Math.toRadians((double) (-this.yBodyRot + 90.0F));
                float pitch = (float) Math.toRadians((double) (-this.getXRot() + 180.0F));
                if (this.level().isClientSide) {
                    // 原版写的是 this.getScale() * 2.0F。getScale() 来自传奇怪物自己的
                    // IAnimatedBoss 接口，我们的继承链里没有，用等价的常量 2.0F 代替。
                    if (this.getPhase() < 2) {
                        this.level().addParticle(new SoulSweepParticle.SweepData(2.0F, yaw, pitch),
                                d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    } else {
                        // 二阶段换成红色版本。
                        this.level().addParticle(new SoulSweepRedParticle.SweepData(2.0F, yaw, pitch),
                                d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    }
                }
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
            }

            // 第 23 tick：落地。震动 + 地裂 + 冲击音 + 真正的伤害。
            if (this.attackTicks == slamAttackTick) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.15F, 5, 10);
                this.spawnSoulPillar(3.0F, 0.0F, 5);
                this.playSound(ModSounds.POWERFUL_SWORD_IMPACT2.get(), 1.0F, 1.0F);
                this.StraightLineAreaAttack(-0.35F, 2.5F, smashRange, 100, 18.0F, true, 1.5F);
            }
        }

        // ---------------------------------------------------------------
        //  砸地第二下（attackState 8）
        // ---------------------------------------------------------------
        // ⚠️ 砸地是一套「两段连招」，不是一个动作：
        //     第一下在状态 6 —— 跳起来劈一条直线（StraightLineAreaAttack）
        //     第二下在状态 8 —— 落地后接一记转身横扫（SideAreaAttack）
        //   砸地 goal 收招时会把状态切成 8，由 PossessedPaladinStateGoal 认领并接管计时。
        //   所以状态 8 不是「收招动画」，它有自己的伤害判定 —— 漏掉这段就会出现
        //   「第二下动作照播、却完全打不到人」的现象。
        //
        // 这一段的节奏和第一下不同，是先贴身再爆发：
        //   tick 6~19 ：一边生成小灵魂尖柱一边朝目标冲（离得越远冲得越狠）
        //   tick 20    ：记下目标位置（打空时后续位移有个去处，同第一下）
        //   tick 21    ：挥剑特效 + 挥空音效
        //   tick 24    ：落地。震屏 + 灵魂光环 + 音效 + 扇形伤害 + 三排灵魂尖柱
        int counterTick = 24;
        if (this.getAttackState() == 8) {
            // 「追击」阶段：目标越远，这一冲力度越大，最猛 0.15。
            // 每到第 3 tick 还会在脚边冒一根小灵魂尖柱（数量 1，无延迟）。
            if (this.attackTicks > 5 && this.attackTicks < 20 && this.targetIsNotNull()) {
                if (this.tickCount % 3 == 0) {
                    this.spawnSoulPillar(-1.0F, 0.0F, 1);
                }
                float distance = this.distanceTo(this.target());
                float multiplier = Math.min(distance * 0.025F, 0.15F);
                this.calculatedDash(multiplier);
            }

            if (this.attackTicks == counterTick - 4) {
                this.saveTargetPos();
            }

            // 第 21 tick：挥剑的剑光 + 挥空音效。纯客户端视觉。
            if (this.attackTicks == counterTick - 3) {
                double d0 = this.getX();
                double d1 = this.getY() + (double) bigSweepAdditionalY;
                double d2 = this.getZ();
                float yaw = (float) Math.toRadians((double) (-this.yBodyRot + 90.0F));
                float pitch = (float) Math.toRadians((double) (-this.getXRot() + 180.0F));
                if (this.level().isClientSide) {
                    if (this.getPhase() < 2) {
                        this.level().addParticle(new SoulSweepParticle.SweepData(2.0F, yaw, pitch),
                                d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    } else {
                        this.level().addParticle(new SoulSweepRedParticle.SweepData(2.0F, yaw, pitch),
                                d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    }
                }
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
            }

            // 第 24 tick：落地爆发。这一整个 if 是「第二下能打到人」的关键。
            if (this.attackTicks == counterTick) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.15F, 5, 10);
                // 光圈颜色随阶段变：一阶段青绿，二阶段纯红（uR/uG/uB）。
                this.spawnCircleParticle(1.5F, 0.0F, 30.0F, true, 1.0F,
                        this.getPhase() >= 2 ? this.uR : 0.0F,
                        this.getPhase() >= 2 ? this.uG : 0.9F,
                        this.getPhase() >= 2 ? this.uB : 0.8F,
                        0.8F, Circle.EnumRingBehavior.GROW, 20);
                this.playSound(SoundEvents.TOTEM_USE, 1.0F, 0.65F);
                // 第二下的主伤害：正面 100 度扇、3.5 格、20 点，命中还会把目标挑飞。
                this.SideAreaAttack(3.5F, 3.0F, 100.0F, 0.0F, 0.0F, 20.0F, 100,
                        SoundEvents.EMPTY, 1.0F, true, 2.0F);
                // 再补三排灵魂尖柱，各排距离角度都随机，把周围铺满。
                this.randomizedSoulStrike(3, 4, 3);
                this.randomizedSoulStrike(2, 3, 5);
                this.randomizedSoulStrike(5, 6, 2);
                ParticleUtils.controlledSmashParticles(this, 2.5F, 0.0F, 0.0F, 0.5F, 1.0F);
            }
        }

        // ---------------------------------------------------------------
        //  砸地第二下 · 二阶段版（attackState 31）
        // ---------------------------------------------------------------
        // 这就是上面状态 8 的「二阶段换皮版」，由 PossessedPaladinSlamAttackGoal
        // 在二阶段收招时切过来（一阶段切 8，二阶段切 31）。
        //
        // ⚠️ 逐行比对过原版（第 1436~1532 行）：**前 24 tick 和状态 8 一模一样**，
        //    唯一的差别是收尾多两段：
        //      第 45 tick：补一圈向内收缩的光环（状态 8 没有）
        //      第 53 tick：震屏 + 音效 + 撒一整圈 15 颗灵魂弹（状态 8 没有）
        //    所以二阶段这一招比一阶段长得多，也狠得多。
        //
        //    之前这里只注册了 goal、没搬 UpdateWithAttack，
        //    结果就是「动作照播但完全打不到人」—— 和状态 8 早先那个坑一模一样。
        if (this.getAttackState() == 31) {
            if (this.attackTicks > 5 && this.attackTicks < 20 && this.targetIsNotNull()) {
                if (this.tickCount % 3 == 0) {
                    this.spawnSoulPillar(-1.0F, 0.0F, 1);
                }
                float distance = this.distanceTo(this.target());
                float multiplier = Math.min(distance * 0.025F, 0.15F);
                this.calculatedDash(multiplier);
            }

            if (this.attackTicks == counterTick - 4) {
                this.saveTargetPos();
            }

            if (this.attackTicks == counterTick - 3) {
                double d0 = this.getX();
                double d1 = this.getY() + (double) bigSweepAdditionalY;
                double d2 = this.getZ();
                float yaw = (float) Math.toRadians((double) (-this.yBodyRot + 90.0F));
                float pitch = (float) Math.toRadians((double) (-this.getXRot() + 180.0F));
                if (this.level().isClientSide) {
                    if (this.getPhase() < 2) {
                        this.level().addParticle(new SoulSweepParticle.SweepData(2.0F, yaw, pitch),
                                d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    } else {
                        this.level().addParticle(new SoulSweepRedParticle.SweepData(2.0F, yaw, pitch),
                                d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    }
                }
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
            }

            // 第 24 tick：和一阶段那一记转身横扫完全相同的落地爆发。
            if (this.attackTicks == counterTick) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.15F, 5, 10);
                this.spawnCircleParticle(1.5F, 0.0F, 30.0F, true, 1.0F,
                        this.getPhase() >= 2 ? this.uR : 0.0F,
                        this.getPhase() >= 2 ? this.uG : 0.9F,
                        this.getPhase() >= 2 ? this.uB : 0.8F,
                        0.8F, Circle.EnumRingBehavior.GROW, 20);
                this.playSound(SoundEvents.TOTEM_USE, 1.0F, 0.65F);
                this.SideAreaAttack(3.5F, 3.0F, 100.0F, 0.0F, 0.0F, 20.0F, 100,
                        SoundEvents.EMPTY, 1.0F, true, 2.0F);
                this.randomizedSoulStrike(3, 4, 3);
                this.randomizedSoulStrike(2, 3, 5);
                this.randomizedSoulStrike(5, 6, 2);
                ParticleUtils.controlledSmashParticles(this, 2.5F, 0.0F, 0.0F, 0.5F, 1.0F);
            }

            // 第 45 tick：二阶段独有。一圈 100 个点、向内收缩的光环 ——
            // 「先把气势收拢，再一口气炸开」的那个前摇，和下一段第 53 tick 的弹幕配套。
            if (this.attackTicks == 45) {
                this.spawnCircleParticle(0.0F, 0.0F, 100.0F, true, 2.0F,
                        this.getPhase() >= 2 ? this.uR : 0.0F,
                        this.getPhase() >= 2 ? this.uG : 0.9F,
                        this.getPhase() >= 2 ? this.uB : 0.8F,
                        0.8F, Circle.EnumRingBehavior.SHRINK, 30);
            }

            // 第 53 tick：二阶段独有的收尾大招 —— 一整圈 15 颗灵魂弹。
            if (this.attackTicks == 53) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.1F, 5, 5);
                this.playSound(SoundEvents.WITHER_SHOOT, 1.0F, 1.0F);
                this.soulStrikeRing(1.0F, 15, 0.0F);
            }
        }

        // ---------------------------------------------------------------
        //  盾击（attackState 25）
        // ---------------------------------------------------------------
        // 这一招和砸地一样是「两段式」，但节奏完全不同：它<b>不冲不打</b>，
        // 而是原地摆两圈灵魂盾阵，靠盾自己飞出去撞人造成伤害。
        //
        //   第 28 tick：摆出大圈（半径 12 格，16 个点）的「预警光圈」—— 只画地上那个缩小的圈
        //   第 32 tick：挥剑音效
        //   第 35 tick：【第一段爆发】小范围扇形伤害 + 灵魂盾阵真正生成（大圈）
        //   第 52 tick：摆出小圈（半径 2 格，贴脸）的预警光圈
        //   第 56 tick：挥剑音效
        //   第 59 tick：【第二段爆发】盾阵生成（小圈）+ 命中音效
        //
        // ⚠️ 注意第二段（59 tick）<b>没有</b> SideAreaAttack，伤害完全来自那一圈贴身灵魂盾。
        // 也就是说这一招有一半的输出依赖 SoulShieldEntity 实体能正常打人。
        //
        // 两段都是「先预警、后生成」成对出现的：spawnSoulShieldRing 的最后一个参数
        // true 表示只画光圈，false 才真正生成盾。
        if (this.getAttackState() == 25) {
            double totalRadius = 12.0D;
            int points = 16;
            int smashTick1 = 35;
            int smashTick2 = 59;

            // ---- 手里那件「幽灵物品」的显隐（当前没有渲染图层读它，见字段注释）----
            if (this.attackTicks == 1) {
                this.ghostItemFade.setTimer(5);
            }
            if (this.attackTicks >= 1 && this.attackTicks <= 70 && this.ghostItemFade.getTimer() > 0) {
                this.ghostItemFade.decreaseTimer();
            }
            if (this.attackTicks == 64) {
                this.ghostItemFade.setTimer(0);
            }
            if (this.attackTicks >= 65) {
                this.ghostItemFade.increaseTimer();
            }

            // ---- 第一段：大圈 ----
            if (this.attackTicks == smashTick1 - 7) {
                this.spawnSoulShieldRing(0, 2, points, totalRadius, false, true);
            }
            if (this.attackTicks == smashTick1 - 3) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
            }
            if (this.attackTicks == smashTick1) {
                // 第一段的直接伤害：正面 180 度扇、3.25 格、20 点，命中挑飞。
                this.SideAreaAttack(3.25F, 3.0F, 180.0F, 0.0F, 0.0F, 20.0F, 100,
                        SoundEvents.EMPTY, 0.0F, true, 1.0F);
                this.playSound(ModSounds.SOUL_SHIELD_SMASH.get(), 1.0F, 1.0F);
                // 原版这里是 SHRINK（收缩圈），第二段是 GROW（扩散圈）——
                // 一个大圈收拢、一个小圈炸开，视觉上是两种不同的"劲"。
                this.spawnCircleParticle(0.0F, 0.0F, 100.0F, true, 2.0F,
                        this.getPhase() >= 2 ? this.uR : 0.0F,
                        this.getPhase() >= 2 ? this.uG : 0.9F,
                        this.getPhase() >= 2 ? this.uB : 0.8F,
                        0.8F, Circle.EnumRingBehavior.SHRINK, 30);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.15F, 5, 5);
                this.spawnSoulShieldRing(0, 2, points, totalRadius, false, false);
            }

            // ---- 第二段：贴脸小圈 ----
            if (this.attackTicks == smashTick2 - 7) {
                this.spawnSoulShieldRing(1, 2, points, 2.0D, true, true);
            }
            if (this.attackTicks == smashTick2 - 3) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
            }
            if (this.attackTicks == smashTick2) {
                this.playSound(ModSounds.STAB_HIT.get(), 1.0F, 1.0F);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.15F, 5, 5);
                this.spawnCircleParticle(0.0F, 0.0F, 100.0F, true, 2.0F,
                        this.getPhase() >= 2 ? this.uR : 0.0F,
                        this.getPhase() >= 2 ? this.uG : 0.9F,
                        this.getPhase() >= 2 ? this.uB : 0.8F,
                        0.8F, Circle.EnumRingBehavior.GROW, 30);
                this.spawnSoulShieldRing(1, 2, points, 2.0D, true, false);
            }
        }

        // ---------------------------------------------------------------
        //  二阶段变身（attackState 26）
        // ---------------------------------------------------------------
        // 整段演出 147 tick（7.35 秒），真正的时间线只有三个点：
        //
        //   第 30 tick：念第 4 句台词（青色）—— 变身前的宣告
        //   第 49 tick：【爆发点】震屏 + 360 度无死角 15 点伤害 + 图腾音效 + 全场挑飞
        //              ⚠️ 同一 tick，PossessedPaladinSecondPhaseGoal 把阶段切成 2
        //   第 82 tick：念第 5 句台词（红色）+ 震屏 + 凋灵发射音效 + 一圈灵魂弹（还没搬）
        //
        // 中间 49~55 tick 是纯粒子：先青后红，贴着身体灌一层球壳，
        // 「撕开盔甲、露出红光」的观感就靠这一段。
        // 颜色之所以能自动从青变红，是因为它读的是 getPhase() ——
        // 49~50 tick 时阶段还是 1（青），51 tick 之后已经是 2（红），衔接是白送的。
        //
        // ⚠️ 这一整段期间实体是完全免伤的，见 hurt() 的第一层判断。
        // 免伤 + 大范围击飞是刻意的：原版就是「变身时没人能打断你，但你也别想靠近」。
        if (this.getAttackState() == 26) {
            if (this.attackTicks == 30) {
                this.sendAdvancedHotBarMessage("legendary_monsters.message.possessed_paladin_talk4",
                        ChatFormatting.AQUA, 10.0F);
            }

            if (this.attackTicks == 49) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.15F, 5, 5);
                // 360 度、半径 3 格、15 点伤害，但 launch = false —— 不挑飞。
                // 挑飞交给下一句 earthquakeEffect，两下叠起来才是「又挨打又上天」。
                this.SideAreaAttack(3.0F, 4.0F, 360.0F, 0.0F, 0.0F, 15.0F, 0,
                        SoundEvents.EMPTY, 0.0F, false, 0.0F);
                this.playSound(SoundEvents.TOTEM_USE, 1.0F, 0.75F);
                // ⚠️ 见 earthquakeEffect 的注释：原版这个方法没有友军筛选，我们补了一道。
                this.earthquakeEffect(15.0F, 0.25F);
            }

            if (this.attackTicks >= 49 && this.attackTicks < 51) {
                // 变身「撕甲」的第一拍，还是青色（此刻阶段尚未切换）。
                this.SphereParticle(ModParticles.GHOSTLY_SOUL.get(), 0.0F, 2.0F, 6.0F);
            }

            if (this.attackTicks >= 51 && this.attackTicks <= 53) {
                // 第二拍转红，正好接在阶段切换之后。
                this.SphereParticle(ModParticles.GHOSTLY_SOUL_RED.get(), 0.0F, 2.0F, 6.0F);
            }

            if (this.attackTicks >= 51 && this.attackTicks < 55) {
                // 四颗「溅射」碎屑。上面 SphereParticle 灌的是规整的球壳，
                // 这四颗位置全随机、往外崩，两者叠在一起才不显得假。
                float f9 = (this.random.nextFloat() - 0.5F) * 8.0F;
                float f10 = (this.random.nextFloat() - 0.5F) * 4.0F;
                float f2 = (this.random.nextFloat() - 0.5F) * 8.0F;
                float f8 = (this.random.nextFloat() - 0.75F) * 5.0F;
                float f6 = (this.random.nextFloat() - 0.75F) * 3.0F;
                float f7 = (this.random.nextFloat() - 0.75F) * 5.0F;
                this.level().addParticle(this.getPhase() >= 2
                                ? ModParticles.SOUL_EXPLOSION_RED.get() : ModParticles.SOUL_EXPLOSION.get(),
                        this.getX() + (double) f9, this.getY() + 2.0D + (double) f10,
                        this.getZ() + (double) f2, 0.0D, 0.0D, 0.0D);
                this.level().addParticle(this.getPhase() >= 2
                                ? ModParticles.SOUL_EXPLOSION_RED.get() : ModParticles.SOUL_EXPLOSION.get(),
                        this.getX() + (double) f8, this.getY() + 2.0D + (double) f6,
                        this.getZ() + (double) f7, 0.0D, 0.0D, 0.0D);
                this.level().addParticle(this.getPhase() >= 2
                                ? ModParticles.GHOSTLY_SOUL_RED.get() : ModParticles.GHOSTLY_SOUL.get(),
                        this.getX() + (double) f9, this.getY() + 2.0D + (double) f10,
                        this.getZ() + (double) f2, 0.0D, 0.5D, 0.0D);
                this.level().addParticle(this.getPhase() >= 2
                                ? ModParticles.GHOSTLY_SOUL_RED.get() : ModParticles.GHOSTLY_SOUL.get(),
                        this.getX() + (double) f8, this.getY() + 2.0D + (double) f6,
                        this.getZ() + (double) f7, 0.0D, 0.5D, 0.0D);
            }

            if (this.attackTicks == MathUtils.toTicks(4.13F)) {
                this.sendAdvancedHotBarMessage("legendary_monsters.message.possessed_paladin_talk5",
                        ChatFormatting.RED, 10.0F);
                // 原版这一句后面还有个 bossInfo.setName(..._p2)，是换血条标题的。
                // 我们的圣骑没有血条，省掉。
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.1F, 5, 5);
                this.playSound(SoundEvents.WITHER_SHOOT, 1.0F, 1.0F);
                // 变身收尾：撒一整圈 15 颗灵魂弹（每颗 12 点伤害）把周围清场。
                // 这就是「变身时打不动它、变完身立刻被弹幕糊一脸」的那圈弹幕。
                this.soulStrikeRing(1.0F, 15, 0.0F);
            }
        }

        // ---------------------------------------------------------------
        //  二连斩第一段（attackState 2）
        // ---------------------------------------------------------------
        // 结构很规整：挥两刀，每刀都是「提前 3 tick 出剑光 + 往前冲刺，到点震屏 + 扇形范围伤害」。
        // 两刀只有伤害数值不同（15 和 17）。
        //
        // 判定用的是 SideAreaAttack（扇形），和砸地的 StraightLineAreaAttack（直线）不是一回事：
        // 二连斩是横扫，所以要按角度筛；砸地是劈砍，所以要按直线拉长。
        if (this.getAttackState() == 2) {
            if ((float) this.attackTicks == doubleSlashAttack1 - 3.0F) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, (double) bigSweepAdditionalY, false,
                        sweepSize, sweepRot, false);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
                this.calculatedDash(0.25F);
            }

            if ((float) this.attackTicks == doubleSlashAttack1) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.SideAreaAttack(doubleSlashRange, 3.0F, 180.0F, 0.0F, 0.0F, 15.0F, 100,
                        SoundEvents.EMPTY, 1.0F, false, 0.0F);
            }

            if ((float) this.attackTicks == doubleSlashAttack2 - 3.0F) {
                // reverse 传 true —— 第二刀是反手挥，剑光方向整个反过来。
                this.createSweep(0.0F, 0.0F, bigSweepHeight, (double) bigSweepAdditionalY, true,
                        sweepSize, sweepRot, false);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
                this.calculatedDash(0.25F);
            }

            if ((float) this.attackTicks == doubleSlashAttack2) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.SideAreaAttack(doubleSlashRange, 3.0F, 180.0F, 0.0F, 0.0F, 17.0F, 100,
                        SoundEvents.EMPTY, 1.0F, false, 0.0F);
            }
        }

        // ---------------------------------------------------------------
        //  二连斩砸（attackState 3）
        // ---------------------------------------------------------------
        // 本质上是「二连斩（状态 2）打完再接一下砸地」，所以前两刀的三行代码
        // 和状态 2 完全一样（挥剑特效 + 前冲 + 扇形伤害），只是后面多了第三下。
        //
        // 第三下就是砸地那一套的翻版：先扑向刚才记下的目标位置，落地时
        // 地裂 + 冲击音 + 直线范围伤害。所以它其实是「加强版二连斩」。
        //
        // 一整招的时间表：第 20 tick 第一刀（15 点）→ 第 36 tick 第二刀（17 点）
        // → 第 62 tick 第三下砸地（18 点）。
        if (this.getAttackState() == 3) {
            // ---- 第一刀（第 20 tick），与状态 2 完全一致 ----
            if ((float) this.attackTicks == doubleSlashAttack1 - 3.0F) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, (double) bigSweepAdditionalY, false,
                        sweepSize, sweepRot, false);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
                this.calculatedDash(0.25F);
            }

            if ((float) this.attackTicks == doubleSlashAttack1) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.SideAreaAttack(doubleSlashRange, 3.0F, 180.0F, 0.0F, 0.0F, 15.0F, 100,
                        SoundEvents.EMPTY, 1.0F, false, 0.0F);
            }

            // ---- 第二刀（第 36 tick），反手挥，与状态 2 一致 ----
            if ((float) this.attackTicks == doubleSlashAttack2 - 3.0F) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, (double) bigSweepAdditionalY, true,
                        sweepSize, sweepRot, false);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
                this.calculatedDash(0.25F);
            }

            if ((float) this.attackTicks == doubleSlashAttack2) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.SideAreaAttack(doubleSlashRange, 3.0F, 180.0F, 0.0F, 0.0F, 17.0F, 100,
                        SoundEvents.EMPTY, 1.0F, false, 0.0F);
            }

            // ---- 第三下：砸地（第 62 tick）----
            // 第 57 tick：把目标此刻的位置记下来。这一下是「跳劈」，中途目标跑了就用旧位置落地，
            // 所以需要提前存档 —— 和砸地（状态 6）用 saveTargetPos() 是同一个套路。
            if ((float) this.attackTicks == doubleSlashAttack3 - 5.0F) {
                this.saveTargetPos();
            }

            // 第 59 tick：举剑挥下的剑光 + 挥剑音，然后朝存档位置扑过去。
            if ((float) this.attackTicks == doubleSlashAttack3 - 3.0F) {
                double d0 = this.getX();
                double d1 = this.getY() + (double) bigSweepAdditionalY;
                double d2 = this.getZ();
                float yaw = (float) Math.toRadians((double) (-this.yBodyRot + 90.0F));
                float pitch = (float) Math.toRadians((double) (-this.getXRot() + 180.0F));
                if (this.level().isClientSide) {
                    // 原版写的是 this.getScale() * 2.0F，getScale() 来自传奇怪物的
                    // IAnimatedBoss 接口，我们的继承链里没有，用等价的常量 2.0F 代替。
                    if (this.getPhase() < 2) {
                        this.level().addParticle(new SoulSweepParticle.SweepData(2.0F, yaw, pitch),
                                d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    } else {
                        this.level().addParticle(new SoulSweepRedParticle.SweepData(2.0F, yaw, pitch),
                                d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    }
                }
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
                // 朝「刚才存下来的目标位置」扑过去，而不是朝目标本人 —— 这样目标就算跑了，
                // 这一下也会落在原处，而不是拐弯追人（追人交给下一招的寻路去做）。
                this.calculatedDashToPositon(0.15F, this.lastTargetPos());
            }

            // 第 62 tick：落地。这一下和砸地（状态 6）落地那三行完全一样。
            if ((float) this.attackTicks == doubleSlashAttack3) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.15F, 5, 10);
                this.spawnSoulPillar(3.0F, 0.0F, 5);
                this.playSound(ModSounds.POWERFUL_SWORD_IMPACT2.get(), 1.0F, 1.0F);
                this.StraightLineAreaAttack(-0.35F, 2.5F, smashRange, 100, 18.0F, true, 1.5F);
            }
        }

        // ---------------------------------------------------------------
        //  后空翻（attackState 10）
        // ---------------------------------------------------------------
        // 注意这一招<b>没有任何伤害判定</b> —— 它在原版整个 UpdateWithAttack 里就这孤零零一行。
        // 本质是个后撤位移：往身后弹一下，拉开距离。真正的价值在于它收招时有 35% 概率
        // 转进警觉状态（9）绕到侧面。
        //
        if (this.getAttackState() == 10 && this.attackTicks == 8) {
            this.backStep(-1.5F, 0.2F);
        }

        // ---------------------------------------------------------------
        //  格挡反击（attackState 5）
        // ---------------------------------------------------------------
        // 圣骑被打时若格挡成功，就会切到状态 5，打出「横扫一刀 + 六把匕首齐射」的反击。
        // 从哪个状态切过来只看一件事 —— 挨打那一刻是不是闲着（待机 0 或警觉 9），
        // 判定写在实体的 hurt() 里，见 {@code PossessedPaladinServant#hurt}。
        //
        // ⚠️ 它的<b>眩晕</b>不在这里 —— 眩晕挂在 SideAreaAttack 的判定内部
        //    （命中且 getAttackState() == 5 时附加 55 tick），见那个方法的实现。
        //    也就是说这个分支只负责「何时打」，不负责「打中会怎样」。
        //
        // ⚠️ 原版第 28~30 tick 有个 for 循环，循环体只是把 i-26 赋给一个<b>从不被读取</b>的
        //    局部变量。死代码，不搬（和状态 12 开头那两个死变量一样）。
        if (this.getAttackState() == 5) {
            if (this.attackTicks == 27) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, (double) bigSweepAdditionalY, true,
                        sweepSize, sweepRot, false);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
                this.calculatedDash(0.25F);
            }
            if (this.attackTicks == 30) {
                // 六把匕首、相邻 30 度、returnTick 固定 10 —— 这一招不看距离，飞出去就回来。
                this.throwDaggers(1.0F, this.getX(), this.getY(), this.getZ(), 6, 30.0F, 0.0F, 10);
                // ⚠️ 末尾两个参数是「击飞」和击飞力度。这里是全招式里力度最大的一下（2.0），
                //    别处要么不击飞、要么只给 1.5。
                this.SideAreaAttack(doubleSlashRange, 3.0F, 180.0F, 0.0F, 0.0F, 20.0F, 100,
                        SoundEvents.ANVIL_LAND, 1.0F, true, 2.0F);
            }
        }

        // ---------------------------------------------------------------
        //  投匕首（attackState 15 = 一阶段三把 / 28 = 二阶段十把）
        // ---------------------------------------------------------------
        // 圣骑唯一的<b>纯远程</b>招式：先后撤一步拉开身位，再把匕首撒成扇形甩出去。
        // 匕首是回旋镖（飞出去还会绕回来），具体行为见 ThrownPhantomDagger。
        //
        // 两段的时刻表：
        //   状态 15：第 10 tick 后撤 → 第 12 tick 扔 3 把，returnTick = 10 + 距离
        //   状态 28：第 10 tick 后撤 → 第 12 tick 扔 3 把
        //            → 第 28 tick 再后撤 → 第 30 tick 扔 4 把（returnTick = 15 + 距离）
        //            → 第 32 tick 再扔 3 把
        //
        // ⚠️ returnTick 里的「距离」是<b>到目标的直线距离取整</b>，不是格数常量 ——
        //    离得越远飞得越久才回得来。原版把它提前算在 if 外面，我们挪进各自的分支里，
        //    值完全一样（原版那个 distance 也只有这两个状态读）。
        // ⚠️ 注意状态 28 的第 32 tick 那一轮<b>不播投掷音效</b>，三轮里唯独它是静音的。
        //    漏了或者多加都会让听感跟前两轮不一样。
        if (this.getAttackState() == 15) {
            if (this.attackTicks == 10) {
                this.backStep(-1.0F, 0.0F);
            }
            if (this.attackTicks == 12) {
                int distance = this.targetIsNotNull() ? (int) this.distanceTo(this.target()) : 0;
                this.throwDaggers(1.0F, this.getX(), this.getY(), this.getZ(), 3, 30.0F, 0.0F,
                        10 + distance);
                this.playSound(ModSounds.DAGGER_THROW.get(), 1.0F, 0.75F);
            }
        }

        if (this.getAttackState() == 28) {
            if (this.attackTicks == 10) {
                this.backStep(1.0F, 0.0F);
            }
            if (this.attackTicks == 12) {
                int distance = this.targetIsNotNull() ? (int) this.distanceTo(this.target()) : 0;
                this.throwDaggers(1.0F, this.getX(), this.getY(), this.getZ(), 3, 30.0F, 0.0F,
                        10 + distance);
                this.playSound(ModSounds.DAGGER_THROW.get(), 1.0F, 0.75F);
            }
            if (this.attackTicks == 28) {
                this.backStep(1.0F, 0.0F);
            }
            if (this.attackTicks == 30) {
                int distance = this.targetIsNotNull() ? (int) this.distanceTo(this.target()) : 0;
                this.throwDaggers(1.0F, this.getX(), this.getY(), this.getZ(), 4, 30.0F, 0.0F,
                        15 + distance);
                this.playSound(ModSounds.DAGGER_THROW.get(), 1.0F, 0.75F);
            }
            if (this.attackTicks == 32) {
                int distance = this.targetIsNotNull() ? (int) this.distanceTo(this.target()) : 0;
                this.throwDaggers(1.0F, this.getX(), this.getY(), this.getZ(), 3, 30.0F, 0.0F,
                        10 + distance);
            }
        }

        // ---------------------------------------------------------------
        //  翻跟头砸（attackState 12 = 扑击本体 / 14 = 落地长收招）
        // ---------------------------------------------------------------
        // 这一招是圣骑唯一的<b>远程起手</b>：跳起来整个扑过去砸一下。两段的动作结构完全一样，
        // 只有时刻表不同：
        //   状态 12：第  6 tick 腾空扑 → 第 16 tick 挥剑特效 → 第 19 tick 落地砸（32 tick 总长）
        //   状态 14：第  2 tick 腾空扑 → 第 11 tick 挥剑特效 → 第 14 tick 落地砸（47 tick 总长）
        // 状态 13 是另一条收招支线（20 tick），它<b>没有任何伤害判定</b>，所以这里没有它的分支。
        //
        // ⚠️ 落地那一下比砸地（状态 6）多了<b>一记扇形</b>：
        //    砸地只有 StraightLineAreaAttack 一条直线判定，这里是「扇形 + 直线」双层。
        //    而且两者的直线射程还不一样 —— 状态 12 是 18.0F，状态 14 是 <b>21.0F</b>（更远）。
        //    抄的时候这两处最容易看漏，看漏了就是「动作照播、伤害范围不对」。
        //
        // ⚠️ 原版状态 12 分支开头还算了两个局部变量 dx / dz（拿 vecX、f、f1 摆出一个落点），
        //    但算完之后<b>从没被读过</b> —— 真正的位移是紧跟其后那行 jumpTowardsPosition，
        //    直接朝目标本人的坐标扑。死代码，这里不搬。

        if (this.getAttackState() == 12) {
            // 第 6 tick：腾空。传的是目标「此刻」的坐标，不是预判点，
            // 所以目标横向跑动是躲得开的 —— 这是原版刻意的设计（扑击能被走位闪掉）。
            if (this.attackTicks == 6 && this.targetIsNotNull()) {
                this.jumpTowardsPosition(this.target().getX(), this.target().getY(), this.target().getZ());
            }

            // 第 16 tick：举剑挥下的剑光特效。纯客户端视觉，和砸地（状态 6）那一段逐行相同。
            if (this.attackTicks == 16) {
                double d0 = this.getX();
                double d1 = this.getY() + (double) bigSweepAdditionalY;
                double d2 = this.getZ();
                float yaw = (float) Math.toRadians((double) (-this.yBodyRot + 90.0F));
                float pitch = (float) Math.toRadians((double) (-this.getXRot() + 180.0F));
                if (this.level().isClientSide) {
                    // 原版是 this.getScale() * 2.0F，我们的继承链里没有 getScale()，用常量 2.0F。
                    if (this.getPhase() < 2) {
                        this.level().addParticle(new SoulSweepParticle.SweepData(2.0F, yaw, pitch),
                                d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    } else {
                        this.level().addParticle(new SoulSweepRedParticle.SweepData(2.0F, yaw, pitch),
                                d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    }
                }
            }

            // 第 19 tick：落地。震屏 + 灵魂光环 + 音效 + 扇形 + 直线，五行连招。
            if (this.attackTicks == 19) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.15F, 5, 10);
                this.spawnSoulPillar(3.0F, 0.0F, 5);
                this.playSound(ModSounds.POWERFUL_SWORD_IMPACT2.get(), 1.0F, 1.0F);
                this.SideAreaAttack(1.0F, 3.0F, 70.0F, -180.0F, -0.5F, 18.0F, 100,
                        SoundEvents.EMPTY, 1.0F, true, 1.5F);
                this.StraightLineAreaAttack(-hitBoxWidth, verticalAttackHeight, smashRange, 100, 18.0F, true, 1.5F);
            }
        }

        if (this.getAttackState() == 14) {
            // 第 2 tick：腾空。比状态 12 早了 4 tick —— 这是长收招的起跳点。
            if (this.attackTicks == 2 && this.targetIsNotNull()) {
                this.jumpTowardsPosition(this.target().getX(), this.target().getY(), this.target().getZ());
            }

            // 第 11 tick：挥剑特效。
            if (this.attackTicks == 11) {
                double d0 = this.getX();
                double d1 = this.getY() + (double) bigSweepAdditionalY;
                double d2 = this.getZ();
                float yaw = (float) Math.toRadians((double) (-this.yBodyRot + 90.0F));
                float pitch = (float) Math.toRadians((double) (-this.getXRot() + 180.0F));
                if (this.level().isClientSide) {
                    if (this.getPhase() < 2) {
                        this.level().addParticle(new SoulSweepParticle.SweepData(2.0F, yaw, pitch),
                                d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    } else {
                        this.level().addParticle(new SoulSweepRedParticle.SweepData(2.0F, yaw, pitch),
                                d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    }
                }
            }

            // 第 14 tick：落地。和状态 12 唯一的不同是直线判定的射程 —— 21.0F，比状态 12 远 3 格。
            if (this.attackTicks == 14) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.15F, 5, 10);
                this.spawnSoulPillar(3.0F, 0.0F, 5);
                this.playSound(ModSounds.POWERFUL_SWORD_IMPACT2.get(), 1.0F, 1.0F);
                this.SideAreaAttack(1.0F, 3.0F, 70.0F, -180.0F, -0.5F, 18.0F, 100,
                        SoundEvents.EMPTY, 1.0F, true, 1.5F);
                this.StraightLineAreaAttack(-hitBoxWidth, verticalAttackHeight, smashRange, 100, 21.0F, true, 1.5F);
            }
        }

        // ---------------------------------------------------------------
        //  剑气斩（attackState 16 = 起手斩 / 18 = 突刺收招 / 17 = 空收招）
        // ---------------------------------------------------------------
        // 原版把这两个数字定义在分支<b>前面</b>（PossessedPaladinEntity.java:1660-1661），
        // 因为后面 19/20/21 那几招还会接着用同样的写法（XX - 8 / XX - 3 / XX）。
        // 两个都等于 18，但含义不同，别合并成一个常量：
        //   slashFromAttack —— 第一刀落点（第 18 tick）；
        //   stabAttack      —— 突刺落点（第 18 tick）。
        //
        // ⚠️ 状态 17 <b>没有</b>分支。它是「短收招」，30 tick 纯动画，一刀不砍。
        //    它照样要注册 goal（优先级 0），否则掷中它的那一次圣骑会永久卡在状态 17。
        int slashFromAttack = 18;
        int stabAttack = 18;

        if (this.getAttackState() == 16) {
            // 第 15 tick：剑光 + 挥剑音 + 往前冲一步。
            // reverse 传 true —— 反手挥，和二连斩第二刀同一种。
            // calculatedDash(0.25F) 是「朝当前朝向小冲一段」，不是扑击；系数比二连斩的 0.15 大。
            if (this.attackTicks == slashFromAttack - 3) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, (double) bigSweepAdditionalY, true,
                        sweepSize, sweepRot, false);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
                this.calculatedDash(0.25F);
            }

            // 第 18 tick：伤害判定。注意这里是 GambitedSideAreaAttack 而不是 SideAreaAttack ——
            // 两者判定形状完全一样，唯一区别是它会在命中时置 hasHurt，供收招时决定接哪一招。
            // 震屏强度和别的招不同：0.05F，且 damageTicks 传 0（不加屏幕持续抖动）。
            if (this.attackTicks == slashFromAttack) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.GambitedSideAreaAttack(doubleSlashRange, 3.0F, 180.0F, 0.0F, 0.0F, 17.0F, 100,
                        SoundEvents.EMPTY, 1.0F, false, 0.0F);
            }
        }

        if (this.getAttackState() == 18) {
            // 第 10 tick：记下目标当前坐标。这是「预判」—— 第 15 tick 突刺时朝的是<b>这一刻</b>
            // 记下的点，不是目标当时的实时位置。所以这招是可以靠横向走位躲开的。
            if (this.attackTicks == stabAttack - 8) {
                this.saveTargetPos();
            }

            // 第 15 tick：突刺音 + 朝刚才记下的点冲过去。
            // 用 calculatedDashToPositon（朝一个坐标冲）而不是 calculatedDash（朝朝向冲），
            // 系数同样 0.25F。
            if (this.attackTicks == stabAttack - 3) {
                this.playSound(ModSounds.POSSESSED_PALADIN_STAB.get(), 1.0F, 0.8F);
                this.calculatedDashToPositon(0.25F, this.lastTargetPos());
            }

            // 第 18 tick：伤害判定。这是<b>直线突刺</b>不是扇形 ——
            // 判定箱横向偏移 -0.6F（比翻跟头砸的 -0.5F 更偏）、高 2.5F、射程取通用的 smashRange。
            // 注意它的击退系数是 1.35F，比翻跟头砸的 1.5F 小。
            //
            // ⚠️ 状态 18 虽然长着「收招」的脸（50 tick 的长动画），但它<b>是真真正正的主攻</b>。
            //    这也是那几条 StateGoal 存在的意义 —— 别以为是纯过场就把它们删了。
            if (this.attackTicks == stabAttack) {
                this.StraightLineAreaAttack(-0.6F, 2.5F, smashRange, 100, 17.0F, true, 1.35F);
            }
        }

        // ---------------------------------------------------------------
        //  突刺抓取（attackState 19 = 突刺 / 20 = 抓住了的处决 / 21 与 33 = 没抓到的收招）
        // ---------------------------------------------------------------
        // 这是圣骑唯一会<b>改变敌人位置</b>的招式：刺中了就把人拽到自己背上，
        // 原地锤 110 tick 再扔出去。四条分支构成一个完整的分岔：
        //
        //   19（起手，28 tick）—— 记位置 → 第 25 tick 突刺过去 → 第 28 tick 判定抓取
        //        ├─ 抓住了        → 20（处决，由 StabGrabGoal.stop() 切过去）
        //        ├─ 没抓住·一阶段 → 21（短收招，toTicks(1.5F) = 30 tick）
        //        └─ 没抓住·二阶段 → 33（长收招，toTicks(2.63F) = 52 tick，<b>带一记突刺</b>）
        //
        // ⚠️ 21 <b>没有</b>自己的分支 —— 它和状态 17 一样是纯动画，一刀不砍。
        // ⚠️ 33 虽然叫「收招」，但它<b>有伤害判定</b>（第 20 tick 一记直线突刺），
        //    和一阶段的 21 完全不是一回事。这也是二阶段更难缠的原因之一。
        int stabGrabAttack = 28;
        int stabStabAttack = 20;

        if (this.getAttackState() == 19) {
            // 第 20 tick：记下目标位置（和状态 18 的突刺同一个套路 —— 朝「记下的点」冲，
            // 所以目标横向走位能躲开）。
            if (this.attackTicks == stabGrabAttack - 8) {
                this.saveTargetPos();
            }

            // 第 25 tick：突刺音 + 冲过去。注意冲刺系数是 <b>0.35F</b>，
            // 比状态 18 的 0.25F 更猛 —— 抓取必须贴到脸上才能把人拽上来。
            if (this.attackTicks == stabGrabAttack - 3) {
                this.playSound(ModSounds.POSSESSED_PALADIN_STAB.get(), 1.0F, 0.8F);
                this.calculatedDashToPositon(0.35F, this.lastTargetPos());
            }

            // 第 25~27 tick：脚下转一圈光环（GROW_THEN_SHRINK = 先胀后缩），
            // 作为「要抓了」的预警。yaw 取的是 -getYRot() + 180，和其它招式同一个约定。
            if (this.attackTicks >= stabGrabAttack - 3 && this.attackTicks < stabGrabAttack) {
                float g = (float) Math.toRadians((double) (-this.getYRot() + 180.0F));
                double spawnX = this.getX() + vecX * 1.5D;
                double spawnZ = this.getZ() + vecZ * 1.5D;
                this.level().addParticle(new Circle.RingData(g, 0.0F, 30,
                                this.getPhase() >= 2 ? this.uR : 0.0F,
                                this.getPhase() >= 2 ? this.uG : 0.9F,
                                this.getPhase() >= 2 ? this.uB : 0.8F,
                                1.0F, 40.0F, false,
                                Circle.EnumRingBehavior.GROW_THEN_SHRINK),
                        spawnX, this.getY() + 1.0D, spawnZ, 0.0D, 0.0D, 0.0D);
            }

            // 第 28 tick：抓取判定。参数依次是「盒子左右鼓 0.6、上下鼓 3.0、往前伸 4.0 格、
            // 破盾 100 tick、伤害 17 点、击飞 true / 1.35（这两个原版没用上，见方法注释）」。
            if (this.attackTicks == stabGrabAttack) {
                this.StabGrab(-0.6F, 3.0F, 4.0F, 100, 17.0F, true, 1.35F);
            }
        }

        if (this.getAttackState() == 33) {
            // 第 12 tick：记位置。
            if (this.attackTicks == stabStabAttack - 8) {
                this.saveTargetPos();
            }

            // ⚠️ 第 18 tick 响突刺音 —— 注意它用的是 <b>stabAttack（18）</b> 而不是
            //    状态 33 自己的 stabStabAttack（20）。也就是说<b>先出声、后出手</b>，
            //    中间差 2 tick。原版就是这么写的（PossessedPaladinEntity.java:1719），
            //    别「顺手改对」成 20，那会让音效和动作对不上。
            if (this.attackTicks == stabAttack - 2) {
                this.playSound(ModSounds.POSSESSED_PALADIN_STAB.get(), 1.0F, 0.8F);
            }

            // 第 15 tick：冲过去。系数又回到 0.25F。
            if (this.attackTicks == stabAttack - 3) {
                this.calculatedDashToPositon(0.25F, this.lastTargetPos());
            }

            // 第 20 tick：直线突刺。参数和状态 18 的那一记完全一样。
            if (this.attackTicks == stabStabAttack) {
                this.StraightLineAreaAttack(-0.6F, 2.5F, smashRange, 100, 17.0F, true, 1.35F);
            }
        }

        if (this.getAttackState() == 20) {
            // 前 20 tick 是「举起受害者」的起手动画，把计数清零。
            if (this.attackTicks < 20) {
                this.soulRaysCount = 0;
            }

            // ---- 第 20 / 30 / 40 tick：三轮「灵魂冲击」----
            // 每轮对<b>背上所有乘客</b>造成一次伤害，伤害随对方血量百分比成长
            // （entityBasedHpDamage），并且<b>给圣骑回血</b> —— 这是处决过程中唯一的续航。
            // 因为是直接对乘客结算，所以这招绕开了普通攻击的无敌帧。
            if (this.attackTicks == 20 || this.attackTicks == 30 || this.attackTicks == 40) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 10.0F, 0.1F, 0, 10);

                for (Entity entity : this.getPassengers()) {
                    if (!(entity instanceof LivingEntity livingPassenger)) {
                        continue;
                    }
                    // 原版直接强转 LivingEntity —— 会骑上来的本来就是生物，
                    // 这里加个 instanceof 只是防意外，行为不变。
                    boolean hurt = livingPassenger.hurt(ModDamageTypes.causeGhostlyDamage(this, this),
                            (float) (2.0D + (double) MathUtils.entityBasedHpDamage(livingPassenger, 5.0F)
                                    * (Double) ModConfig.MOB_CONFIG.PosessedPaladinDamageMutliplier.get()));
                    if (hurt) {
                        this.heal(3.0F + MathUtils.entityBasedHpDamage(livingPassenger, 0.25F));
                    }
                }

                // 最后一组参数 20 / 4.0D / true 分别是「存活 20 tick」「挂在 y=4 的高度」
                // 「朝向跟随身体」。false 那个是「不生成方块粒子」。
                this.spawnCircleParticle(2.0F, -1.0F, 30.0F, false, 1.0F,
                        this.getPhase() >= 2 ? this.uR : 0.0F,
                        this.getPhase() >= 2 ? this.uG : 0.9F,
                        this.getPhase() >= 2 ? this.uB : 0.8F,
                        0.8F, Circle.EnumRingBehavior.GROW, 20, 4.0D, true);
                ++this.soulRaysCount;
            }

            // ---- 三轮的音效，音调一轮比一轮高（0.75 → 1.0 → 1.25），越锤越急促 ----
            if (this.attackTicks == 20) {
                this.playSound(SoundEvents.RESPAWN_ANCHOR_CHARGE, 1.0F, 0.75F);
            }
            if (this.attackTicks == 30) {
                this.playSound(SoundEvents.RESPAWN_ANCHOR_CHARGE, 1.0F, 1.0F);
            }
            if (this.attackTicks == 40) {
                this.playSound(SoundEvents.RESPAWN_ANCHOR_CHARGE, 1.0F, 1.25F);
            }
            if (this.attackTicks == 60) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
            }

            // ---- 第 63 tick：第一记大锤 ----
            if (this.attackTicks == 63) {
                this.soulRaysCount = 0;
                double d0 = this.getX();
                double d1 = this.getY() + (double) bigSweepAdditionalY;
                double d2 = this.getZ();
                float yaw = (float) Math.toRadians((double) (-this.yBodyRot + 90.0F));
                float pitch = (float) Math.toRadians((double) (-this.getXRot() + 180.0F));
                if (this.level().isClientSide) {
                    if (this.getPhase() < 2) {
                        this.level().addParticle(new SoulSweepParticle.SweepData(2.0F, yaw, pitch),
                                d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    } else {
                        this.level().addParticle(new SoulSweepRedParticle.SweepData(2.0F, yaw, pitch),
                                d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    }
                }
                this.playSound(SoundEvents.TOTEM_USE, 1.0F, 1.0F);
                Vec3 pos = new Vec3(this.getX(), this.getY(), this.getZ());
                CameraShakeEntity.cameraShake(this.level(), pos, 20.0F, 0.25F, 0, 20);
                this.spawnCircleParticle(1.5F, -0.25F, 50.0F, true, 5.0F,
                        this.getPhase() >= 2 ? this.uR : 0.25F,
                        this.getPhase() >= 2 ? this.uG : 1.0F,
                        this.getPhase() >= 2 ? this.uB : 0.75F, 1.0F);
                // ⚠️ 这是全周（arc = 360）大范围伤害，半径 4 格 —— 但 brokenShieldTicks 传 0，
                //    所以<b>不会</b>破盾。而且它是 SideAreaAttack 不是 Gambited，
                //    不会置 hasHurt。
                this.SideAreaAttack(4.0F, 4.0F, 360.0F, 0.0F, 1.0F, 10.0F, 0,
                        SoundEvents.EMPTY, 0.0F, false, 0.0F);
                ParticleUtils.controlledSmashParticles(this, 2.0F, 0.0F, 0.0F, 7.5F, 3.5F);
            }

            // ---- 第 63~68 tick：从身体里往外灌灵魂粒子（SphereParticle 自带隔 tick 节流）----
            if (this.attackTicks >= 63 && this.attackTicks <= 68) {
                this.SphereParticle(this.getPhase() >= 2
                                ? ModParticles.GHOSTLY_SOUL_RED.get()
                                : ModParticles.GHOSTLY_SOUL.get(),
                        0.35F, 2.0F, 3.0F);
            }

            if (this.attackTicks == 106) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
            }

            // ---- 第 109 tick：第二记大锤，同时把背上的人扔出去 ----
            // 「挥空」的 createSweep 是这一下的视觉，真正的位移交给 throwAnGravityEntity。
            // ⚠️ 扔出去的目标点 destVec 取 15 格远、竖直 +2；出手点 vec 取身前 3 格、竖直 +1。
            //    两者都按身体朝向算（vecX/vecZ 是「左侧」，f/f1 是「正前」——
            //    这里 destoffset 和 offset 都是 0，所以实际只用到了「正前」那一组）。
            if (this.attackTicks == 109) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, (double) bigSweepAdditionalY, false,
                        sweepSize, sweepRot, false);
                float destVec = 15.0F;
                float destoffset = 0.0F;
                float vec = 3.0F;
                float offset = 0.0F;
                Entity firstPassenger = this.getFirstPassenger();
                if (firstPassenger instanceof LivingEntity livingPassenger
                        && !this.level().isClientSide) {
                    this.throwAnGravityEntity(1.0F,
                            this.getX() + (double) destVec * vecX + (double) (f * destoffset),
                            this.getY() + 2.0D,
                            this.getZ() + (double) destVec * vecZ + (double) (f1 * destoffset),
                            this.getX() + (double) vec * vecX + (double) (f * offset),
                            this.getY() + 1.0D,
                            this.getZ() + (double) vec * vecZ + (double) (f1 * offset),
                            1.0F, livingPassenger);
                }
            }
        }

        // ---------------------------------------------------------------
        //  跳劈（attackState 22 = 起跳 / 23 = 滞空 / 24 = 落地砸）
        // ---------------------------------------------------------------
        // 原版对应 PossessedPaladinEntity.java:1815（22）、1832（23）、1836（24）。
        // 三个状态是同一条链上的三站，完整的流程图写在 registerGoals 里「招 22 / 23 / 24」那一段。

        // ---- 状态 22：起跳 ----
        // 整个状态只干一件事：第 24 tick 给自己一个冲量，人就窜出去了。
        // 方向 = 朝目标的水平向量 + 竖直 0.7；水平部分再乘 0.2 的系数。
        // 所以水平速度其实很温和，窜出去主要靠那 0.7 的升力。
        // 竖直分量额外叠了目标相对高度的一小块（clamp(高度差 × 0.075, 0, 10)）——
        // 目标站在高处时会跳得更高一点，才追得上去。
        if (this.getAttackState() == 22 && this.attackTicks == 24) {
            LivingEntity jumpTarget = this.getTarget();
            if (jumpTarget != null) {
                double d0 = jumpTarget.getX() - this.getX();
                double d1 = jumpTarget.getY() - this.getY();
                double d2 = jumpTarget.getZ() - this.getZ();
                double mult = 0.2D;
                this.setDeltaMovement(new Vec3(d0,
                        0.7D + Mth.clamp(d1 * 0.075D, 0.0D, 10.0D), d2).multiply(mult, 1.0D, mult));
            } else {
                // 目标中途没了：原地往上蹦一下，总比僵在半空强。
                this.setDeltaMovement(new Vec3(0.0D, 0.7D, 0.0D));
            }
        }

        // ---- 状态 23：滞空 ----
        // 这里只干一件事：等落地。人一沾地就把状态交给 24（一阶段）或 32（二阶段）。
        // ⚠️ 注意这里是<b>改状态</b>，不是让 goal 退出 —— 状态一变，
        //    认领 23 的 PossessedPaladinJumpFallGoal 下一 tick 自己就放手了。
        // ⚠️ 也正是因为这一句，JumpFallGoal 的 100 tick 上限平时根本用不到，
        //    只有「一直没落地」的极端情况才会撞上。
        if (this.getAttackState() == 23 && this.onGround()) {
            this.setAttackState(this.getPhase() >= 2 ? 32 : 24);
        }

        // ---- 状态 24：落地砸 ----
        // 第 4 tick：一记 180° 范围伤害 + 音效 + 一圈扩散光环 + 镜头抖动 + 扬尘，
        //             同时用 strikeZigzagXBlades(..., true) 铺一圈<b>只有粒子、没有实体</b>的预警。
        // 第 7 tick：同一套再来一遍，但 warning 传 false —— 这次真正的灵魂剑刃实体从地里冒出来了。
        //
        // 这就是那套「先给你 3 tick 看清楚要挨打，再真的打」的预警设计，和砸地（状态 6）是同一个思路。
        if (this.getAttackState() == 24) {
            if (this.attackTicks == 4) {
                this.SideAreaAttack(3.0F, 3.0F, 180.0F, 0.0F, 0.0F, 24.0F, 100,
                        SoundEvents.EMPTY, 0.0F, true, 1.5F);
                this.playSound(SoundEvents.TOTEM_USE, 1.0F, 0.75F);
                this.strikeZigzagXBlades(12, 1.0D, false, 10.0F, 4.0F, 1, 2.0D, true);
                // 9 参重载内部固定就是 GROW + life=35（见 IAnimatedMonsterServant），
                // 所以这里和另外两处 60.0F 的写法保持一致，不额外传环行为。
                this.spawnCircleParticle(1.5F, -0.25F, 60.0F, true, 5.0F,
                        this.getPhase() >= 2 ? this.uR : 0.25F,
                        this.getPhase() >= 2 ? this.uG : 1.0F,
                        this.getPhase() >= 2 ? this.uB : 0.75F, 1.0F);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.25F, 0, 20);
                ParticleUtils.controlledSmashParticles(this, 1.0F, 0.0F, 0.0F, 7.5F, 3.0F);
            }

            if (this.attackTicks == 7) {
                this.strikeZigzagXBlades(12, 1.0D, false, 12.0F, 4.0F, 1, 2.0D, false);
            }
        }

        // ---- 状态 32：二阶段的落地砸连招 ----
        // 原版 PossessedPaladinEntity.java:1851。这是状态 24 的「二阶段加强版」，
        // 由状态 23 落地时切过来（见上面那句 `getPhase() >= 2 ? 32 : 24`）。
        // 一阶段落一下就收工（65 tick），二阶段落完还要再接两段，整条 115 tick（5.79 秒）：
        //
        //   tick  1   ：收起上一招留下的预警贴图
        //   tick  4   ：【第一砸】180° 范围伤害 + 图腾音效 + 剑刃预警 + 光环 + 震屏 + 扬尘
        //   tick  7   ：灵魂剑刃实体从地里冒出来
        //   tick 30   ：记下落点 —— 目标正前方「5 + 距离 × 0.5」格（目标跑得越远，扑得越远）
        //   tick 38   ：挥剑音效 + 朝那个落点冲过去
        //   tick 41   ：【第二砸】180° 范围伤害
        //   tick 50   ：脚边冒两根灵魂尖柱
        //   tick 55~56：再记一次目标位置（给第三砸定位）
        //   tick 58~61：一边冲一边每 tick 冒三根灵魂尖柱
        //   tick 67   ：【第三砸】100° 范围伤害 + 剑刃预警
        //   tick 70   ：灵魂剑刃实体冒出
        //   tick 56 起：预警贴图渐隐
        //
        // ⚠️ 三砸的判定角度是 180° → 180° → 100°，刻意收窄的：
        //    前两下清场，第三下聚焦 —— 打到那时目标基本已经被逼到正面了，
        //    收成 100° 能少误伤旁边的友军，伤害一点没减。
        //
        // ⚠️ tick 30 那个落点用的是 saveTargetPos(x, y, z) 三参数版（刚补的那个重载），
        //    不是无参版 —— 它存的是「目标前方」而不是「目标脚下」，两者差着好几格。
        if (this.getAttackState() == 32) {
            if (this.attackTicks == 1) {
                this.telegraphFadeAway.resetTimer();
            }

            if (this.attackTicks == 4) {
                this.SideAreaAttack(3.0F, 3.0F, 180.0F, 0.0F, 0.0F, 24.0F, 100,
                        SoundEvents.EMPTY, 0.0F, true, 1.5F);
                this.playSound(SoundEvents.TOTEM_USE, 1.0F, 0.75F);
                this.strikeZigzagXBlades(12, 1.0D, false, 10.0F, 4.0F, 1, 2.0D, true);
                this.spawnCircleParticle(1.5F, -0.25F, 60.0F, true, 5.0F,
                        this.getPhase() >= 2 ? this.uR : 0.25F,
                        this.getPhase() >= 2 ? this.uG : 1.0F,
                        this.getPhase() >= 2 ? this.uB : 0.75F, 1.0F);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.25F, 0, 20);
                ParticleUtils.controlledSmashParticles(this, 1.0F, 0.0F, 0.0F, 7.5F, 3.0F);
            }

            if (this.attackTicks == 7) {
                this.strikeZigzagXBlades(12, 1.0D, false, 12.0F, 4.0F, 1, 2.0D, false);
            }

            // tick 30：见上面注释，存的是「目标前方」那个点。
            if (this.attackTicks == 30) {
                float offset = -0.5F;
                float vec = this.targetIsNotNull() ? 5.0F + this.distanceTo(this.target()) * 0.5F : 5.0F;
                this.saveTargetPos(
                        this.getX() + (double) vec * vecX + (double) (f * offset),
                        this.getY(),
                        this.getZ() + (double) vec * vecZ + (double) (f1 * offset));
            }

            if (this.attackTicks == 38) {
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
                this.calculatedDashToPositon(0.25F, this.lastTargetPos());
            }

            if (this.attackTicks == 41) {
                this.SideAreaAttack(3.0F, 3.0F, 180.0F, 0.0F, 0.0F, 20.0F, 100,
                        SoundEvents.EMPTY, 0.0F, true, 1.5F);
            }

            if (this.attackTicks == 50) {
                this.spawnSoulPillar(0.0F, -2.0F, 1);
                this.spawnSoulPillar(0.0F, 2.0F, 1);
            }

            if (this.attackTicks >= 55 && this.attackTicks <= 56) {
                this.saveTargetPos();
            }

            if (this.attackTicks >= 56) {
                this.telegraphFadeAway.increaseTimer();
            }

            if (this.attackTicks >= 58 && this.attackTicks < 62) {
                this.spawnSoulPillar(0.0F, -2.0F, 1);
                this.spawnSoulPillar(0.0F, 2.0F, 1);
                this.spawnSoulPillar(0.0F, 0.0F, 1);
                this.calculatedDashToPositon(0.2F, this.lastTargetPos());
            }

            if (this.attackTicks == 67) {
                this.SideAreaAttack(3.0F, 3.0F, 100.0F, 0.0F, 0.0F, 24.0F, 100,
                        SoundEvents.EMPTY, 0.0F, true, 1.5F);
                this.playSound(SoundEvents.TOTEM_USE, 1.0F, 0.75F);
                // 第三砸的剑刃传 true（isFalling）—— 前两砸是从地里「冒」出来的，这次是「砸下来」的。
                this.strikeZigzagXBlades(12, 1.0D, true, 10.0F, 4.0F, 1, 2.0D, true);
                this.spawnCircleParticle(1.5F, -0.25F, 60.0F, true, 5.0F,
                        this.getPhase() >= 2 ? this.uR : 0.25F,
                        this.getPhase() >= 2 ? this.uG : 1.0F,
                        this.getPhase() >= 2 ? this.uB : 0.75F, 1.0F);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.25F, 0, 20);
                ParticleUtils.controlledSmashParticles(this, 1.0F, 0.0F, 0.0F, 7.5F, 3.0F);
            }

            if (this.attackTicks == 70) {
                this.strikeZigzagXBlades(12, 1.0D, true, 12.0F, 4.0F, 1, 2.0D, false);
            }
        }

        // ---------------------------------------------------------------
        //  侧滚旋转（attackState 29 = 向左滚 / 30 = 向右滚）
        // ---------------------------------------------------------------
        // 两招的代码是逐行重复的，唯一差别是两处：
        //   1. 第 4 tick 翻滚的偏移方向（offset 为 +2 / -2，一左一右）；
        //   2. 伤害判定的射程（17.0F / 18.0F）。
        // 原版就是复制粘贴的两大段，这里保持同样的形状，方便和反编译源码对照。
        //
        // 招式流程：滚开（tick 4）→ 起手挥剑（tick 38）→ 一边前冲一边脚下冒灵魂尖柱
        // （tick 38~53，每 3 tick 一次，一次三根）→ 第 41 tick 第一刀 360° 旋风斩
        // → 第 54 tick 第二刀 360° 旋风斩。
        //
        // 注意 arc 是 360 —— 这是全周伤害，前后左右都打得到，和砸地那种
        // 180/90 度的扇形不一样。

        if (this.getAttackState() == 29) {
            float attackTick1 = 41.0F;
            float attackTick2 = 54.0F;
            float dashA = 0.6F;
            int arc = 360;

            // 第 4 tick：往身体正后方弹开一小段，也就是「翻滚」那一下。
            // 算法是「算出身前 2 格的那个点，再把当前位置减去它」—— 得到的正好是反方向。
            if (this.attackTicks == 4) {
                float vec = 0.0F;
                float offset = 2.0F;
                float scale = 1.0F;
                Vec3 rollPos = new Vec3(
                        this.getX() + (double) vec * vecX + (double) (f * offset),
                        this.getY(),
                        this.getZ() + (double) vec * vecZ + (double) (f1 * offset));
                Vec3 sub = this.position().subtract(rollPos);
                Vec3 finalPos = sub.scale((double) scale);
                this.setDeltaMovement(finalPos.x, this.getDeltaMovement().y, finalPos.z);
                this.playSound(ModSounds.POSSESSED_PALADIN_ROLL.get(), 1.0F, 1.0F);
            }

            // 第 38 tick：挥剑起手特效（纯视觉，无伤害）。
            if ((float) this.attackTicks == attackTick1 - 3.0F) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, (double) bigSweepAdditionalY, true,
                        sweepSize, sweepRot, false);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
            }

            // 第 38~53 tick：一边前冲一边铺灵魂尖柱。三条一起刷是为了让轨道宽一点，
            // 中间那条是主轨，上下 ±2 格算两翼。
            if ((float) this.attackTicks >= attackTick1 - 3.0F && (float) this.attackTicks < attackTick2) {
                this.basicDash(dashA, 0.0F, false);
                if (this.tickCount % 3 == 0) {
                    this.spawnSoulPillar(-1.0F, 0.0F, 1);
                    this.spawnSoulPillar(-1.0F, 2.0F, 1);
                    this.spawnSoulPillar(-1.0F, -2.0F, 1);
                }
            }

            // 第 41 tick：第一刀，360° 全周横扫。
            if ((float) this.attackTicks == attackTick1) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.SideAreaAttack(doubleSlashRange, 3.0F, (float) arc, 0.0F, 0.0F, 17.0F, 100,
                        SoundEvents.EMPTY, 1.0F, false, 0.0F);
            }

            // 第 54 tick：第二刀。原版把「挥剑特效」和「伤害」写成了两个并列的 if，
            // 条件都是 attackTicks == attackTick2，这里保持原样（照抄比合并更不容易出错）。
            if ((float) this.attackTicks == attackTick2) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, (double) bigSweepAdditionalY, true,
                        sweepSize, sweepRot, false);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
            }

            if ((float) this.attackTicks == attackTick2) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.SideAreaAttack(doubleSlashRange, 3.0F, (float) arc, 0.0F, 0.0F, 17.0F, 100,
                        SoundEvents.EMPTY, 1.0F, false, 0.0F);
            }
        }

        if (this.getAttackState() == 30) {
            float attackTick1 = 41.0F;
            float attackTick2 = 54.0F;
            float dashA = 0.6F;
            int arc = 360;

            // 和状态 29 唯一的区别之一：offset 取 -2.0F，于是翻滚方向左右相反。
            if (this.attackTicks == 4) {
                float vec = 0.0F;
                float offset = -2.0F;
                float scale = 1.0F;
                Vec3 rollPos = new Vec3(
                        this.getX() + (double) vec * vecX + (double) (f * offset),
                        this.getY(),
                        this.getZ() + (double) vec * vecZ + (double) (f1 * offset));
                Vec3 sub = this.position().subtract(rollPos);
                Vec3 finalPos = sub.scale((double) scale);
                this.setDeltaMovement(finalPos.x, this.getDeltaMovement().y, finalPos.z);
                this.playSound(ModSounds.POSSESSED_PALADIN_ROLL.get(), 1.0F, 1.0F);
            }

            if ((float) this.attackTicks == attackTick1 - 3.0F) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, (double) bigSweepAdditionalY, true,
                        sweepSize, sweepRot, false);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
            }

            if ((float) this.attackTicks >= attackTick1 - 3.0F && (float) this.attackTicks < attackTick2) {
                this.basicDash(dashA, 0.0F, false);
                if (this.tickCount % 3 == 0) {
                    this.spawnSoulPillar(-1.0F, 0.0F, 1);
                    this.spawnSoulPillar(-1.0F, 2.0F, 1);
                    this.spawnSoulPillar(-1.0F, -2.0F, 1);
                }
            }

            // 第二个区别：射程是 18.0F 而不是 17.0F，比状态 29 略远一点点。
            if ((float) this.attackTicks == attackTick1) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.SideAreaAttack(doubleSlashRange, 3.0F, (float) arc, 0.0F, 0.0F, 18.0F, 100,
                        SoundEvents.EMPTY, 1.0F, false, 0.0F);
            }

            if ((float) this.attackTicks == attackTick2) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, (double) bigSweepAdditionalY, true,
                        sweepSize, sweepRot, false);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
            }

            if ((float) this.attackTicks == attackTick2) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.SideAreaAttack(doubleSlashRange, 3.0F, (float) arc, 0.0F, 0.0F, 18.0F, 100,
                        SoundEvents.EMPTY, 1.0F, false, 0.0F);
            }
        }

        // ---------------------------------------------------------------
        //  死亡演出（attackState 36）
        //
        //  原版对应 PossessedPaladinEntity.java:2142。整段没有一行伤害判定 ——
        //  它纯粹是「死给你看」的表演，一共 280 tick（14 秒），由 tickDeath() 负责到点删除。
        //
        //  时间表（tick 数全是原版写死的绝对值，不是相对值）：
        //    1      清零 rayAmount（灵魂射线计数器）
        //    20     遗言 talk9「一切...还未结束...」（红字）
        //    1~     每 10 tick 在身体周围炸一簇灵魂粒子，一直到死
        //    85     刺出第一刀：挥击音 + 遗言 talk6「吾...」
        //    85~    每 tick 在身体随机位置冒一缕幽魂
        //    115    刺出第二刀：命中音 + 遗言 talk7「...终得解脱...」
        //    180    蓄力音（低音）+ 镜头震动 + 遗言 talk8 + 第一束灵魂射线
        //    240/250/260  连续三下蓄力音，音调一路升高（0.75 → 1.0 → 1.25），各再加一束射线
        //    270    爆炸音 + 大幅度镜头震动
        //    270~   灵魂粒子大爆发（每 tick 四簇，范围也更大）
        //
        //  ⚠️ 少了原版一处，是有意为之：
        //    · bossInfo.setName(...) —— 我们没有 Boss 血条，血条改名自然也没有。
        //
        //  ⚠️⚠️ 第 241 tick 之后的那几段（270 的爆炸、270+ 的大爆发）<b>在原版里也是跑不到的</b>，
        //  这里照抄保留。原因是状态 36 的 goal 注册时限是 toTicks(12.0F) = 240 tick
        //  （PossessedPaladinEntity.java:897），一到 241 就 canContinueToUse 为假 → stop() →
        //  setAttackState(0) → attackTicks 被清零，后面的判断再也不会成立。
        //  这大概是原作者把「12 秒的时限」和「14 秒的尸体存活」写岔了 —— 但那是原版行为，
        //  移植的口径是「不对原作做修正」，所以不动。想验证的话：死亡演出到第 12 秒会戛然而止，
        //  尸体再躺 2 秒才消失。
        // ---------------------------------------------------------------

        // ---------------------------------------------------------------
        if (this.getAttackState() == 36) {
            // 遗言要对周围 10 格内的玩家喊（和状态 35 的苏醒演讲用同一个值）。
            float playerHearTalking = 10.0F;

            // 下面三个是「往哪个方向偏一点」的随机偏移，范围 ±0.25 格。
            // vec / offset 在原版里是装配位置用的，状态 36 这一支恒为 1.0 / 0.0，
            // 也就是「就放在身体正前方 1 格」—— 保留变量是为了和原版逐行对齐。
            float vec = 1.0F;
            float offset = 0.0F;
            // ⚠️ 这一段原版用的是 this.random（vanilla 共享随机源），不是 this.random1。
            //    两个随机源分布一样，但「多消耗谁几次」会连带影响其它调用者的结果，
            //    所以照抄原版用的那个，别图省事统一成 random1。
            float f3 = (this.random.nextFloat() - 0.0F) * 0.5F;
            float f4 = (this.random.nextFloat() - 0.0F) * 0.5F;
            float f5 = (this.random.nextFloat() - 0.0F) * 0.5F;
            // 两刀的时间点。原版写死 85 / 115，没有配置项。
            int stab1 = 85;
            int stab2 = 115;

            // 灵魂射线计数器：开场清零，之后只加不减。
            // 渲染器每帧读它决定画几条射线；加到几条就是几条（不会淡出、不会回收）。
            // 真正加数的四个时刻是 180 / 240 / 250 / 260，也就是下面「蓄力」那几段 ——
            // 射线是随着蓄力一段段冒出来的，不是一开始就有。
            if (this.attackTicks == 1) {
                this.rayAmount = 0;
            }

            // 从死的那一刻起，每半秒在身体周围随机炸一簇灵魂粒子，一直到尸体消失。
            if (this.attackTicks >= 1 && this.tickCount % 10 == 0) {
                float f9 = (this.random.nextFloat() - 0.5F) * 4.0F;
                float f10 = (this.random.nextFloat() - 0.5F) * 2.0F;
                float f2 = (this.random.nextFloat() - 0.5F) * 4.0F;
                // 死的时候也分阶段：二阶段是红色的灵魂在往外炸。
                this.level().addParticle(this.getPhase() >= 2
                                ? ModParticles.SOUL_EXPLOSION_RED.get() : ModParticles.SOUL_EXPLOSION.get(),
                        this.getX() + (double) f9, this.getY() + 2.0D + (double) f10,
                        this.getZ() + (double) f2, 0.0D, 0.0D, 0.0D);
                this.level().addParticle(this.getPhase() >= 2
                                ? ModParticles.GHOSTLY_SOUL_RED.get() : ModParticles.GHOSTLY_SOUL.get(),
                        this.getX() + (double) f9, this.getY() + 2.0D + (double) f10,
                        this.getZ() + (double) f2, 0.0D, 0.5D, 0.0D);
            }

            if (this.attackTicks == 20) {
                this.sendAdvancedHotBarMessage("legendary_monsters.message.possessed_paladin_talk9",
                        ChatFormatting.RED, playerHearTalking);
            }

            // ---- 第一刀（第 85 tick）----
            if (this.attackTicks == stab1) {
                this.playSound(ModSounds.POSSESSED_PALADIN_STAB.get(), 1.0F, 0.75F);
                this.sendAdvancedHotBarMessage("legendary_monsters.message.possessed_paladin_talk6",
                        ChatFormatting.AQUA, playerHearTalking);
                if (this.level().isClientSide) {
                    this.level().addParticle(this.getPhase() >= 2
                                    ? ModParticles.SOUL_EXPLOSION_RED.get() : ModParticles.SOUL_EXPLOSION.get(),
                            this.getX() + (double) vec * vecX + (double) (f * offset) + (double) f3,
                            this.getY() + 1.0D + (double) f4,
                            this.getZ() + (double) vec * vecZ + (double) (f1 * offset) + (double) f5,
                            0.0D, 0.0D, 0.0D);
                }
            }

            // 第一刀之后持续冒幽魂，一直冒到尸体消失（不只是到第二刀）。
            // 原版写成 for (int i = 0; (double) i < 0.5; ++i) —— 0 < 0.5 成立、1 < 0.5 不成立，
            // 所以这个循环实际只跑一圈。照抄，不要「顺手优化」成去掉循环。
            if (this.attackTicks >= stab1) {
                for (int i = 0; (double) i < 0.5; ++i) {
                    if (this.level().isClientSide) {
                        this.level().addParticle(this.getPhase() >= 2
                                        ? ModParticles.GHOSTLY_SOUL_RED.get() : ModParticles.GHOSTLY_SOUL.get(),
                                this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D),
                                0.0D, 0.025D, 0.0D);
                    }
                }
            }

            // ---- 第二刀（第 115 tick）----
            if (this.attackTicks == stab2) {
                if (this.level().isClientSide) {
                    this.level().addParticle(this.getPhase() >= 2
                                    ? ModParticles.SOUL_EXPLOSION_RED.get() : ModParticles.SOUL_EXPLOSION.get(),
                            this.getX() + (double) vec * vecX + (double) (f * offset) + (double) f3,
                            this.getY() + 1.0D + (double) f4,
                            this.getZ() + (double) vec * vecZ + (double) (f1 * offset) + (double) f5,
                            0.0D, 0.0D, 0.0D);
                }
                this.playSound(ModSounds.STAB_HIT.get(), 1.0F, 1.0F);
                this.sendAdvancedHotBarMessage("legendary_monsters.message.possessed_paladin_talk7",
                        ChatFormatting.AQUA, playerHearTalking);
            }

            // ---- 第 180 tick：蓄力开始 + 最后一句遗言 + 第一束灵魂射线 ----
            if (this.attackTicks == 180) {
                ++this.rayAmount;
                this.playSound(SoundEvents.RESPAWN_ANCHOR_CHARGE, 1.0F, 0.75F);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.1F, 5, 5);
                this.sendAdvancedHotBarMessage("legendary_monsters.message.possessed_paladin_talk8",
                        ChatFormatting.RED, playerHearTalking);
            }

            // ---- 240 / 250 / 260：三连蓄力，音调和震动规模一路往上叠 ----
            // ⚠️ 三段都各自再加一束射线。事实上这三段和下面第 270 那段都跑不到
            //    （状态 36 的 goal 只有 240 tick，见本段开头的长注释），
            //    所以游戏里最终只会看到第 180 tick 加的那一束 —— 照抄原版，不修。
            if (this.attackTicks == 240) {
                this.level().addParticle(this.getPhase() >= 2
                                ? ModParticles.SOUL_EXPLOSION_RED.get() : ModParticles.SOUL_EXPLOSION.get(),
                        this.getX() + (double) vec * vecX + (double) (f * offset) + (double) f3,
                        this.getY() + 2.0D + (double) f4,
                        this.getZ() + (double) vec * vecZ + (double) (f1 * offset) + (double) f5,
                        0.0D, 0.0D, 0.0D);
                ++this.rayAmount;
                this.playSound(SoundEvents.RESPAWN_ANCHOR_CHARGE, 1.0F, 0.75F);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.1F, 5, 5);
            }

            if (this.attackTicks == 250) {
                ++this.rayAmount;
                this.playSound(SoundEvents.RESPAWN_ANCHOR_CHARGE, 1.0F, 1.0F);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.1F, 5, 5);
            }

            if (this.attackTicks == 260) {
                this.level().addParticle(this.getPhase() >= 2
                                ? ModParticles.SOUL_EXPLOSION_RED.get() : ModParticles.SOUL_EXPLOSION.get(),
                        this.getX() + (double) vec * vecX + (double) (f * offset) + (double) f3,
                        this.getY() + 2.0D + (double) f4,
                        this.getZ() + (double) vec * vecZ + (double) (f1 * offset) + (double) f5,
                        0.0D, 0.0D, 0.0D);
                ++this.rayAmount;
                this.playSound(SoundEvents.RESPAWN_ANCHOR_CHARGE, 1.0F, 1.25F);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.1F, 5, 5);
            }

            // ---- 第 270 tick：炸开 ----
            if (this.attackTicks == 270) {
                this.level().addParticle(this.getPhase() >= 2
                                ? ModParticles.SOUL_EXPLOSION_RED.get() : ModParticles.SOUL_EXPLOSION.get(),
                        this.getX() + (double) vec * vecX + (double) (f * offset) + (double) f3,
                        this.getY() + 2.0D + (double) f4,
                        this.getZ() + (double) vec * vecZ + (double) (f1 * offset) + (double) f5,
                        0.0D, 0.0D, 0.0D);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.25F, 5, 10);
                this.playSound(SoundEvents.DRAGON_FIREBALL_EXPLODE, 1.0F, 0.5F);
            }

            // ---- 第 270 tick 之后：魂飞魄散，范围比开头那圈大得多（±4 / ±2 / ±4）----
            if (this.attackTicks >= 270) {
                float f9 = (this.random.nextFloat() - 0.5F) * 8.0F;
                float f10 = (this.random.nextFloat() - 0.5F) * 4.0F;
                float f2 = (this.random.nextFloat() - 0.5F) * 8.0F;
                float f8 = (this.random.nextFloat() - 0.75F) * 5.0F;
                float f6 = (this.random.nextFloat() - 0.75F) * 3.0F;
                float f7 = (this.random.nextFloat() - 0.75F) * 5.0F;
                this.level().addParticle(this.getPhase() >= 2
                                ? ModParticles.SOUL_EXPLOSION_RED.get() : ModParticles.SOUL_EXPLOSION.get(),
                        this.getX() + (double) f9, this.getY() + 2.0D + (double) f10,
                        this.getZ() + (double) f2, 0.0D, 0.0D, 0.0D);
                this.level().addParticle(this.getPhase() >= 2
                                ? ModParticles.SOUL_EXPLOSION_RED.get() : ModParticles.SOUL_EXPLOSION.get(),
                        this.getX() + (double) f8, this.getY() + 2.0D + (double) f6,
                        this.getZ() + (double) f7, 0.0D, 0.0D, 0.0D);
                this.level().addParticle(this.getPhase() >= 2
                                ? ModParticles.GHOSTLY_SOUL_RED.get() : ModParticles.GHOSTLY_SOUL.get(),
                        this.getX() + (double) f9, this.getY() + 2.0D + (double) f10,
                        this.getZ() + (double) f2, 0.0D, 0.5D, 0.0D);
                this.level().addParticle(this.getPhase() >= 2
                                ? ModParticles.GHOSTLY_SOUL_RED.get() : ModParticles.GHOSTLY_SOUL.get(),
                        this.getX() + (double) f8, this.getY() + 2.0D + (double) f6,
                        this.getZ() + (double) f7, 0.0D, 0.5D, 0.0D);
            }
        }

        // ---------------------------------------------------------------
        //  招 37：终结技（attackState 37，二阶段专属）
        //  原版 PossessedPaladinEntity.java:2240-2466
        // ---------------------------------------------------------------
        // 全游戏最长的一招：326 tick ≈ 16.3 秒，一共十一段动作连打，中途不换招。
        // 这也是为什么它自带 16 秒冷却 —— 冷却时长和招式本身一样长。
        //
        // 时间轴（数字都是 tick，命名照抄原版）：
        //    25   说话 talk10（「死吧」之类的台词）
        //    50   起手：前冲 + 双臂挥砍音 + 左右各一道剑光
        //    53   第一记交叉斩：正前方 180 度、17 点
        //    86   三叉戟第一挥（有音效、剑光、前冲，但<b>没有伤害判定</b>，纯演出）
        //    89   三叉戟横扫：180 度、15 点
        //   100   往后撤一步（advancedDash 传负 offset，所以是「后撤再前冲」的手感）
        //   117   剑第一挥（同上，纯演出）
        //   120   剑横扫：180 度、17 点
        //   117/120 三叉戟第二组，参数和第一组完全一样
        //   123   剑第二刀：180 度、<b>19 点</b>（整招伤害最高的一刀）
        //   130   剑第三挥 + 往另一个方向撤一步
        //   133   剑第三刀：180 度、17 点
        //   145   突刺起手：朝目标落点冲过去 + 长枪音效
        //   148   突刺：正前方一条直线、17 点
        //   160   记下落点
        //   165   朝落点冲过去
        //   168   上挑：正前方直线、<b>15 点</b>（比突刺低，因为紧接着要跳）
        //   187   第二记交叉斩起手（两把武器同时挥）
        //   190   交叉斩：180 度、<b>18 点</b>
        //   229   起跳：关重力，开始往上飘，同时吸灵魂粒子
        //   235   镜头拉近（DynamicCameraZoomEntity）
        //   229~240  每 tick 往上顶一点，三组粒子往身上吸
        //   245   蓄力风声
        //   250   记下落点
        //   255   关重力、朝落点扑下去
        //   265   落地砸：正东南西北四串灵魂柱 + 全周 360 度、<b>25 点</b>重击 + 地面光圈
        //   289   双上挑起手
        //   292   双上挑：正前方一串灵魂柱、直线 17 点
        //   312   手里的三叉戟开始淡出（ghostItemFade 反向涨）
        //
        // ⚠️ 三处和原版不一样，都是<b>有意为之</b>：
        //   1. <b>不搬 this.destroy()。</b> 原版在「attackTicks < 265」这段时间里<b>每 tick</b> 调一次
        //      destroy()，那是个开着 mobGriefing 就炸周围一圈方块的挖掘器 ——
        //      也就是说原版从第 1 tick 到第 264 tick 一直在拆地形（多半是原版写漏了
        //      「==」，本意应该是落地那一下才拆）。当 BOSS 时无所谓，当<b>仆从</b>就是灾难：
        //      主人带它出门，它一路走一路把家拆了。所以整段不搬。
        //   2. <b>不搬 tickCount % 10 == 0 那个循环。</b> 原版里面算的 d1/ran/r/g/var55
        //      五个变量算完一个都没用，纯粹是没写完死的死代码。
        //   3. <b>不加 Boss 血条相关的东西。</b> 我们没有血条。
        if (this.getAttackState() == 37) {
            // 十一个时间点。原版写死，没有配置项。
            int crossSlash = 53;
            int tridentSwing1 = 89;
            int swordSwing1 = 92;
            int tridentSwing2 = 120;
            int swordSwing2 = 123;
            int swordSwing3 = 133;
            int swordStab = 148;
            int tridentUppercut = 168;
            int crossSlash2 = 190;
            int landSlam = 265;
            int doubleUppercut = 292;
            // 三叉戟横扫的判定半宽 / 半高。比剑的 3.5 大一截，因为戟更长。
            float tridentRange = 4.0F;
            // ⚠️ 原版这里还声明了一个 int chainedStrikeRadius = 4，但<b>从来没被读过</b>
            //    （落地砸那里写的是字面量 4.0F）。死代码，不搬。

            // ── 手里那两件「幽灵武器」的显隐 ──
            // 和招 25（盾）/ 招 38（戟）同一套写法：先给计时器设 5，再一路减到 0 就是完全显形。
            // 这一段前 70 tick 是「淡入」，第 312 tick 之后再反向涨回去（淡出）。
            if (this.attackTicks == 1) {
                this.ghostItemFade.setTimer(5);
            }
            if (this.attackTicks >= 5 && this.attackTicks <= 70 && this.ghostItemFade.getTimer() > 0) {
                this.ghostItemFade.decreaseTimer();
            }
            // 先归零再涨：不归零的话它从哪儿开始涨是不确定的（前面减到过多少就是多少）。
            if (this.attackTicks == 312) {
                this.ghostItemFade.setTimer(0);
            }
            if (this.attackTicks >= 312) {
                this.ghostItemFade.increaseTimer();
            }

            if (this.attackTicks == 25) {
                // 第三个参数是「多少格内的玩家听得见」，原版在这里写的是字面量 10.0F
                // （状态 36 那边是声明成局部变量 playerHearTalking，作用域不同，不能借过来用）。
                this.sendAdvancedHotBarMessage("legendary_monsters.message.possessed_paladin_talk10",
                        ChatFormatting.RED, 10.0F);
            }

            // ── 第一记交叉斩 ──
            if (this.attackTicks == crossSlash - 3) {
                this.calculatedDash(0.25F);
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, true, sweepSize, sweepRot, false);
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, false, sweepSize, sweepRot, false);
            }
            if (this.attackTicks == crossSlash) {
                this.spawnCircleParticle(0.0F, 0.0F, 100.0F, true, 2.0F,
                        this.getPhase() >= 2 ? this.uR : 0.0F,
                        this.getPhase() >= 2 ? this.uG : 0.9F,
                        this.getPhase() >= 2 ? this.uB : 0.8F,
                        0.8F, Circle.EnumRingBehavior.CONSTANT, 30);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.SideAreaAttack(3.5F, 3.5F, 180.0F, 0.0F, 0.0F, 17.0F, 100,
                        ModSounds.EMPTY, 1.0F, false, 1.0F);
            }

            // ── 三叉戟第一组（起手 + 横扫）──
            if (this.attackTicks == tridentSwing1 - 3) {
                this.calculatedDash(0.25F);
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
                // ⚠️ 戟的剑光比剑大 0.5（sweepSize + 0.5F），别和下面剑那几处抄混了。
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, false,
                        sweepSize + 0.5F, sweepRot, false);
            }
            if (this.attackTicks == tridentSwing1) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.SideAreaAttack(tridentRange, tridentRange, 180.0F, 0.0F, 0.0F, 15.0F, 100,
                        ModSounds.EMPTY, 1.0F, false, 1.0F);
            }

            // ── 剑第一组 ──
            if (this.attackTicks == swordSwing1 - 3) {
                // 剑的冲刺系数比戟小（0.15 对 0.25）—— 剑短，冲太远会贴脸穿模。
                this.calculatedDash(0.15F);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, false,
                        sweepSize, sweepRot, false);
            }
            if (this.attackTicks == swordSwing1) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.SideAreaAttack(3.5F, 3.5F, 180.0F, 0.0F, 0.0F, 17.0F, 100,
                        ModSounds.EMPTY, 1.0F, false, 1.0F);
            }

            // 第 100 tick 往后撤一步。offset 传的是 +2.5F（往身体右侧挪），
            // 配合前面几刀的前冲，看起来就是「打完一组往后跳开找角度」。
            if (this.attackTicks == 100) {
                this.advancedDash(this, -3.0F, 2.5F, 0.75F);
            }

            // ── 三叉戟第二组：参数与第一组逐字相同，只是时间点往后挪 ──
            if (this.attackTicks == tridentSwing2 - 3) {
                this.calculatedDash(0.25F);
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, false,
                        sweepSize + 0.5F, sweepRot, false);
            }
            if (this.attackTicks == tridentSwing2) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.SideAreaAttack(tridentRange, tridentRange, 180.0F, 0.0F, 0.0F, 15.0F, 100,
                        ModSounds.EMPTY, 1.0F, false, 1.0F);
            }

            // ── 剑第二组（第二刀伤害 19.0F，是整招里最重的一刀）──
            if (this.attackTicks == swordSwing2 - 3) {
                this.calculatedDash(0.15F);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, false,
                        sweepSize, sweepRot, false);
            }
            if (this.attackTicks == swordSwing2) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.SideAreaAttack(3.5F, 3.5F, 180.0F, 0.0F, 0.0F, 19.0F, 100,
                        ModSounds.EMPTY, 1.0F, false, 1.0F);
            }

            // ── 剑第三组 ──
            if (this.attackTicks == swordSwing3 - 3) {
                // 往身体<b>左侧</b>撤（-2.5F），和前面第 100 tick 那次(+2.5F)对称。
                this.advancedDash(this, -3.0F, -2.5F, 0.75F);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
                // ⚠️ 只有这一处的 reverse 传 true（另外五处都是 false）——
                //    因为这一刀是反手从另一边砍回来的。
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, true,
                        sweepSize, sweepRot, false);
            }
            if (this.attackTicks == swordSwing3) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                // 注意判定范围是 3.0F × 3.5F —— 比其他几刀窄 0.5，因为这一刀是斜着收的。
                this.SideAreaAttack(3.0F, 3.5F, 180.0F, 0.0F, 0.0F, 17.0F, 100,
                        ModSounds.EMPTY, 1.0F, false, 1.0F);
            }

            // ── 突刺 ──
            if (this.attackTicks == swordStab - 8) {
                this.saveTargetPos();
            }
            if (this.attackTicks == swordStab - 3) {
                this.calculatedDashToPositon(0.25F, this.lastTargetPos());
                this.playSound(ModSounds.POSSESSED_PALADIN_STAB.get(), 1.0F, 0.8F);
                // 突刺的起手剑光是抬高的：bigSweepAdditionalY 让光弧往上偏移。
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, true,
                        sweepSize, sweepRot, false);
            }
            if (this.attackTicks == swordStab) {
                // 冲击判定：正前方一条 5 格长的直线（smashRange）。
                this.StraightLineAreaAttack(-0.6F, 2.5F, smashRange, 100, 17.0F, true, 1.35F);
            }

            // ── 上挑 ──
            if (this.attackTicks == tridentUppercut - 8) {
                this.saveTargetPos();
            }
            if (this.attackTicks == tridentUppercut - 3) {
                this.calculatedDashToPositon(0.25F, this.lastTargetPos());
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
            }
            if (this.attackTicks == tridentUppercut) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                // 直线长度只有 5.0F（突刺用的是 smashRange = 5.0F，数值上正好一样），
                // 但伤害是 15.0F，比突刺低 2 点。
                this.StraightLineAreaAttack(-0.6F, 2.5F, 5.0F, 100, 15.0F, true, 1.35F);
            }

            // ── 第二记交叉斩 ──
            if (this.attackTicks == crossSlash2 - 3) {
                this.calculatedDash(0.25F);
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, true, sweepSize, sweepRot, false);
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, false, sweepSize, sweepRot, false);
            }
            if (this.attackTicks == crossSlash2) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                // 和第一记交叉斩同角度同范围，只有伤害从 17 涨到 18。
                this.SideAreaAttack(3.5F, 3.5F, 180.0F, 0.0F, 0.0F, 18.0F, 100,
                        ModSounds.EMPTY, 1.0F, false, 1.0F);
            }

            // ── 起跳蓄力（第 229 ~ 240 tick）──
            if (this.attackTicks == landSlam - 36) {
                this.setNoGravity(true);
            }
            if (this.attackTicks == landSlam - 30) {
                // 镜头拉近：半径 50 格内的玩家镜头慢慢推近，持续 30 tick、之后再停 55 tick。
                // ⚠️ 这个方法自己会判断 !world.isClientSide，所以两端都调是安全的（客户端什么都不做）。
                DynamicCameraZoomEntity.dynamicCameraZoom(this.level(), this.position(),
                        50.0F, 4.0F, 30, 55, 5.0F, false, this);
            }
            if (this.attackTicks >= landSlam - 36 && this.attackTicks <= landSlam - 25) {
                // 每 tick 往上顶 0.35 格 —— 这是「飘起来」而不是「跳起来」，
                // 因为重力已经关了，不会掉回去。
                this.setDeltaMovement(this.getDeltaMovement().x, 0.35D, this.getDeltaMovement().z);
                // 三组粒子，起点高度分别 5 / 3 / 2 格，终点都是 5 格（也就是身体上方）。
                // 传 3 次而不是传一个循环，是因为同一个 startY 的粒子位置完全相同，
                // 得靠「多调几次、每次换个高度」才有立体感。见 attractParticles 的说明。
                this.attractParticles(ModParticles.GHOSTLY_SOUL_RED.get(), 5, 4, 0.0F, 0.0F,
                        5.0F, 5.0F, 0.075F);
                this.attractParticles(ModParticles.GHOSTLY_SOUL_RED.get(), 5, 4, 0.0F, 0.0F,
                        3.0F, 5.0F, 0.075F);
                this.attractParticles(ModParticles.GHOSTLY_SOUL_RED.get(), 5, 4, 0.0F, 0.0F,
                        2.0F, 5.0F, 0.075F);
            }
            if (this.attackTicks == landSlam - 20) {
                this.playSound(ModSounds.OMINOUS_WIND_UP.get(), 1.0F, 1.0F);
            }
            if (this.attackTicks == landSlam - 15) {
                this.saveTargetPos();
            }
            if (this.attackTicks == landSlam - 10) {
                this.setNoGravity(false);
                // 朝落点扑过去，冲多远由距离决定（越远冲得越猛）。
                if (this.targetIsNotNull()) {
                    Vec3 start = this.position();
                    Vec3 sub = this.lastTargetPos().subtract(start);
                    Vec3 normal = sub.normalize();
                    float fl1 = this.distanceTo(this.target()) * 0.25F;
                    this.setDeltaMovement(this.getDeltaMovement().add(
                            normal.x * (double) fl1, normal.y * (double) fl1, normal.z * (double) fl1));
                } else {
                    // 目标没了就干脆垂直往下砸。⚠️ 只改 y，水平速度保留。
                    this.setDeltaMovement(this.getDeltaMovement().x, -1.0D, this.getDeltaMovement().z);
                }
            }

            // ── 落地砸（第 265 tick）──
            if (this.attackTicks == landSlam) {
                // 正东南西北四个方向各一串灵魂柱，拼成一个十字。
                // 参数 (reps=1, amount=5)：只铺一波，一波是个边长 5 的方框 —— 也就是每边 3 根(隔一个取一个)。
                this.spawnChainedStrike(4.0F, 0.0F, 1, 5, 10, true);
                this.spawnChainedStrike(-4.0F, 0.0F, 1, 5, 10, true);
                this.spawnChainedStrike(0.0F, -4.0F, 1, 5, 10, true);
                this.spawnChainedStrike(0.0F, 4.0F, 1, 5, 10, true);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 10.0F, 0.15F, 10, 10);
                this.playSound(ModSounds.STAB_HIT.get(), 1.0F, 1.0F);
                // 全周 360 度、25 点 —— 整招最重的一下，而且<b>没有击退</b>
                // （launch=false，launchPower 传 0.0F，最后那个 0.0F 同理）。
                this.SideAreaAttack(3.0F, 3.0F, 360.0F, 0.0F, 0.0F, 25.0F, 150,
                        ModSounds.EMPTY, 0.0F, false, 0.0F);
                // 地面光圈：GROW 表示由内往外扩，30 tick 时长。一阶段青白色、二阶段红色。
                this.spawnCircleParticle(0.0F, 0.0F, 100.0F, true, 2.0F,
                        this.getPhase() >= 2 ? this.uR : 0.0F,
                        this.getPhase() >= 2 ? this.uG : 0.9F,
                        this.getPhase() >= 2 ? this.uB : 0.8F,
                        0.8F, Circle.EnumRingBehavior.GROW, 30);
            }

            // ── 双上挑（第 292 tick）──
            if (this.attackTicks == doubleUppercut - 3) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
                // basicDash(a1, minD, cap)：往前扑 1.5 格、至少扑 3 格距离、cap=true 表示会截断。
                this.basicDash(1.5F, 3.0F, true);
            }
            if (this.attackTicks == doubleUppercut) {
                // reps=3、amount=4：往前叠三波、每波是个边长 4 的方框（每边 2 根），
                // 一路推到 12 格开外。
                this.spawnChainedStrike(3.0F, 0.0F, 3, 4, 10, true);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.StraightLineAreaAttack(-0.6F, 2.5F, 5.0F, 100, 17.0F, true, 1.35F);
            }
        }

        // ---------------------------------------------------------------
        //  招 38：掷三叉戟旋转（attackState 38，二阶段专属）
        //  原版 PossessedPaladinEntity.java:2468-2524
        // ---------------------------------------------------------------
        // 整招的流程就是一条直线：
        //   第 28 tick  → 把三叉戟朝目标扔出去（弹射物，见 throwSoulTrident）
        //   第 51 tick  → 反手一挥（只有剑光 + 音效 + 前冲，没有伤害判定）
        //   第 54 tick  → 第一刀真伤害：正前方 90 度、18 点、3.5 格
        //   第 62 tick  → 往前突进一段
        //   第 66 tick  → 再反手一挥（同上，无伤害）
        //   第 69 tick  → 第二刀真伤害，参数和第一刀一模一样
        //   第 69~83  → 每隔 2 tick 铺一排灵魂柱冲击波，一路炸出去
        //
        // ⚠️ 三处容易踩的坑：
        //   1. 斧正一下按钮名：真伤害只有两下（54 / 69），
        //      「第 51 / 66 tick」那两下是<b>纯演出</b> —— 只有一个剑光粒子加音效，
        //      再加一个 calculatedDash 让你往前滑一下。别以为漏了伤害判定。
        //   2. 第 62 tick 的 advancedDash 是<b>唯一的位移</b>，传的是负数（-3.0F 的 offset），
        //      所以是「后撤再前冲」的手感，不是纯前冲。
        //   3. 最后的冲击波循环是 <b>i += 2</b>，8 次，而且 distance 传的是 i - 67
        //      （也就是 2, 4, 6, 8, 10, 12, 14, 16），半径一路往外推。
        //      循环变量从 69 起 —— 所以<b>第 69 tick 那一发同时也是第二刀的判定时刻</b>，
        //      那一 tick 身上会同时挨一刀和一个冲击波。原版就是这么叠的。
        if (this.getAttackState() == 38) {
            // 四个时间点。原版写死，没有配置项。
            int throwAttack = 28;
            int slash1 = 54;
            int slash2 = 69;

            // 三叉戟从手里「淡入」：前 5 tick 给计时器设个值，之后一路减到 0
            // （减到 0 就是完全显形）。渲染图层每帧读这个数决定画多透明。
            // 这一段和招 25（盾击）手里那把盾的写法完全一样。
            if (this.attackTicks == 1) {
                this.ghostItemFade.setTimer(5);
            }
            if (this.attackTicks >= 5 && this.attackTicks <= 70 && this.ghostItemFade.getTimer() > 0) {
                this.ghostItemFade.decreaseTimer();
            }

            // 脚下的地面预警光圈：起手时清零（立刻显形），
            // 从第 64 tick 开始一格格涨回去（慢慢淡掉）。
            // 配合渲染器里的 canRenderTelegraph()，玩家看到的就是
            // 「第 48~69 tick 之间地上有个圈，砍完就消失」。
            if (this.attackTicks == 1) {
                this.telegraphFadeAway.resetTimer();
            }

            // 第 28 tick：扔戟。这是整招唯一一次远程攻击。
            if (this.attackTicks == throwAttack && this.targetIsNotNull()) {
                this.throwSoulTrident(this.target(), 1.0F);
            }

            // ---- 第一刀 ----
            // 前 3 tick 先把「反手挥」的剑光放出来，同时往前滑一点点（冲刺的起手）。
            // ⚠️ 这里<b>没有</b>伤害判定 —— 真正的伤害要等到第 54 tick。
            if (this.attackTicks == slash1 - 3) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, (double) bigSweepAdditionalY,
                        true, sweepSize, sweepRot, false);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
                this.calculatedDash(0.25F);
            }

            // 第 54 tick：第一刀。正前方 90 度扇形、18 点伤害、不挑飞。
            // brokenShieldTicks 传 100，比别的招（17）高得多 —— 这一刀砍中盾牌能削掉一大截。
            if (this.attackTicks == slash1) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.SideAreaAttack(doubleSlashRange, 3.0F, 90.0F, 0.0F, 0.0F, 18.0F, 100,
                        SoundEvents.EMPTY, 1.0F, false, 0.0F);
            }

            // 第 62 tick：突进。见上面第 2 条坑。
            if (this.attackTicks == 62) {
                this.advancedDash(this, 1.5F, -3.0F, 0.75F);
            }

            // ---- 第二刀：和第一刀逐字相同，只有时间点不一样 ----
            if (this.attackTicks == slash2 - 3) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, (double) bigSweepAdditionalY,
                        true, sweepSize, sweepRot, false);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
                this.calculatedDash(0.25F);
            }

            if (this.attackTicks == slash2) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.SideAreaAttack(doubleSlashRange, 3.0F, 90.0F, 0.0F, 0.0F, 18.0F, 100,
                        SoundEvents.EMPTY, 1.0F, false, 0.0F);
            }

            // 第二刀砍完，地面预警光圈开始淡出。
            if (this.attackTicks >= 64 && this.attackTicks <= 69) {
                this.telegraphFadeAway.increaseTimer();
            }

            // ---- 第 69 ~ 83 tick：一路铺出去的灵魂柱冲击波 ----
            // distance = i - 67，所以第 69 tick 那一发是 2 格，第 83 tick 那一发是 16 格。
            // delay 固定 5 tick —— 柱子不是立刻冒出来，而是「先预警、5 tick 后炸」，
            // 给玩家留出躲开的时间（这也是为什么它伤害高却不算耍赖）。
            for (int i = 69; i <= 83; i += 2) {
                if (this.attackTicks == i) {
                    int distance = i - 67;
                    this.flameRadagonShockwave(0.2F, distance, 1.0F, 5, 0.0F, 0.0F, 6.0F);
                }
            }
        }
    }

    // ==================================================================
    //  十七、砸地用到的攻击与特效
    // ==================================================================

    /**
     * 正前方一条直线上的范围伤害。砸地落地那一下调用。
     *
     * <p>做法是「把自身碰撞箱往正前方拉长成一个盒子，再取盒子里的所有生物」。
     * 正前方由 {@code getYRot() + 90} 决定，这是传奇怪物那套代码的约定。
     *
     * <p>友军过滤只写了 {@code !isAlliedTo(entityHit)} 一层。原版是 BOSS，全场皆敌，
     * 这么写没问题；我们靠 {@link #isAlliedTo} 的覆写来兜住主人和同伴，
     * 所以这里不用再叠第二层判断。
     *
     * <p>{@code launchPower} 这个参数原版传了但函数体里从没用过，保留是为了跟原版对齐。
     */
    private void StraightLineAreaAttack(float boxWidth, float yHeight, float range, int brokenShieldTicks,
                                        float damage, boolean launch, float launchPower) {
        double rad = Math.toRadians((double) (this.getYRot() + 90.0F));
        double xRange = (double) range * Math.cos(rad);
        double zRange = (double) range * Math.sin(rad);
        AABB attackRange = this.getBoundingBox()
                .inflate((double) boxWidth, (double) yHeight, (double) boxWidth)
                .expandTowards(xRange, 0.0D, zRange);

        for (LivingEntity entityHit : this.level().getEntitiesOfClass(LivingEntity.class, attackRange)) {
            if (!this.isAlliedTo(entityHit) && entityHit != this) {
                boolean flag = entityHit.hurt(this.damageSources().mobAttack(this),
                        (float) ((double) damage
                                + (double) MathUtils.entityBasedHpDamage(entityHit, 3.0F)
                                * (Double) ModConfig.MOB_CONFIG.PosessedPaladinDamageMutliplier.get()));
                if (flag) {
                    EntityUtil.cancelBuffs(entityHit);
                    // 清掉无敌帧，否则连击的后续几段会被吃掉。
                    entityHit.invulnerableTime = 0;
                    this.shouldAttackMore = true;
                    this.applyStackingEffect(entityHit, ModEffects.SOUL_FRACTURE.get(), 1, 4,
                            MathUtils.toTicks(10.0F));
                }
                if (flag && launch) {
                    this.launch(entityHit, true);
                }
                if (entityHit instanceof Player && entityHit.isBlocking() && brokenShieldTicks > 0) {
                    disableShield(entityHit, brokenShieldTicks);
                }
            }
        }
    }

    /**
     * 在正前方排开一串灵魂尖柱。
     *
     * <p>砸地传的是 {@code (3.0F, 0.0F, 5)}，意思是「从正前方 3 格开始，每根间隔 1.25 格，
     * 一共 5 根」。
     */
    public void spawnSoulPillar(float vec, float offset, int amount) {
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180F));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180F));
        double theta = (double) this.yBodyRot * (Math.PI / 180D);
        ++theta;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        int standingOnY = Mth.floor(this.getY()) - 1;
        double headY = this.getY() + 1.0D;
        float yawRadians = (float) Math.toRadians((double) (90.0F + this.getYRot()));

        for (int l = 0; l < amount; ++l) {
            double d2 = 1.25D * (double) (l + 1);
            this.spawnSoulPillars(
                    this.getX() + (double) vec * vecX + (double) (f * offset) + (double) Mth.cos(yawRadians) * d2,
                    headY,
                    this.getZ() + (double) vec * vecZ + (double) (f1 * offset) + (double) Mth.sin(yawRadians) * d2,
                    standingOnY, yawRadians, l, this.level(), this);
        }
    }

    /**
     * 在圣骑周围随机挑一个落点，朝随机方向铺出一排灵魂尖柱（原版 {@code randomizedSoulStrike}）。
     *
     * <p><b>它不用新实体，底子就是 {@link #spawnSoulPillars}</b>。「随机」体现在两处：
     * 起点在水平 ±5 格内随机，方向则由另外两个随机偏移算出的夹角决定。
     * {@code amount} 是这一排铺几根，间距固定 1.25 格。
     *
     * <p>砸地第二下会连铺三排（3 根 / 5 根 / 2 根），每排各自掷一次随机，
     * 所以每次打出来的形状都不一样 —— 这是那一招看着"很吵"的原因。
     *
     * <p>⚠️ 原版前两个参数 {@code firstRandomMin} / {@code secondRandomMin} 在方法体里
     * <b>压根没被用到</b>，是死参数。签名照抄原版只是为了以后对照方便，不要以为它们有作用。
     */
    public void randomizedSoulStrike(int firstRandomMin, int secondRandomMin, int amount) {
        int uCap = 5;
        int rX = this.random1.nextInt(-uCap, uCap);
        int rX2 = this.random1.nextInt(-uCap, uCap);
        int rZ = this.random1.nextInt(-uCap, uCap);
        int rZ2 = this.random1.nextInt(-uCap, uCap);
        Vec3 randomPos = new Vec3(this.getX() + (double) rX, this.getY(), this.getZ() + (double) rZ);
        Vec3 atanPos = new Vec3(randomPos.x + (double) rX2, randomPos.y, randomPos.z + (double) rZ2);
        // 原版这两行写的是 Math.min(this.getY(), this.getY()) 和 Math.max(this.getY(), this.getY())
        // —— 自己和自己比大小，等价于常量。直接写成结果，省得看的人以为里面有讲究。
        // 它传给 spawnSoulPillars 的 lowestYCheck 参数，含义是「往下找到这个高度就放弃」。
        double lowestYCheck = this.getY() + 1.0D;
        float f = (float) Mth.atan2(atanPos.z - randomPos.z, atanPos.x - randomPos.x);

        for (int l = 0; l < amount; ++l) {
            double d2 = 1.25D * (double) (l + 1);
            // 每根依次多等 1 tick 才冒出来，形成"一路铺过去"的观感。
            int warmup = l + 10;
            this.spawnSoulPillars(randomPos.x + (double) Mth.cos(f) * d2, this.getY(),
                    randomPos.z + (double) Mth.sin(f) * d2, (int) lowestYCheck, f, warmup,
                    this.level(), this);
        }
    }

    /**
     * 【二阶段专属】以自身为圆心，朝 360 度<b>均匀撒出一整圈灵魂冲击弹</b>。
     * 原版 {@code PossessedPaladinEntity.SoulStrike(float, int, float)}，第 3230 行。
     *
     * <p>⚠️ 方法名和原版不一样：原版就叫 {@code SoulStrike}，但那个名字和我们
     * 刚移植过来的弹射物类 {@link SoulStrike} <b>完全同名</b>，
     * 同一个文件里既是类型又是方法，读代码的人（包括三个月后的你）一定会看糊涂。
     * 这里改叫 {@code soulStrikeRing}，一眼能看出「撒了一圈」。
     *
     * <h2>三个参数</h2>
     * <ul>
     *   <li>{@code vec} —— 圆心沿圣骑<b>朝向</b>往前推几格。圣骑两次调用都传 1.0，
     *       也就是「圆心在自己身前 1 格」，免得弹幕从自己身体里冒出来。</li>
     *   <li>{@code quake} —— 撒几颗。两次调用都传 15，所以每颗之间隔 24 度。</li>
     *   <li>{@code math} —— 圆心沿圣骑<b>侧向</b>偏移几格。两次调用都传 0.0。</li>
     * </ul>
     *
     * <h2>方向是怎么算的</h2>
     * 原版这里有个容易看漏的地方：{@code theta} 由 {@code yBodyRot} 换算而来，
     * 然后<b>当场 +1 弧度</b>。这个 +1 没有任何解释，是原作者调出来的手感偏移 ——
     * 别以为它是「+1 度」，是 1 弧度（约 57.3 度）。照抄，不要"顺手修正"。
     *
     * <h2>每颗弹的配置</h2>
     * 伤害固定 12 点，初速 0.45（很慢，肉眼能看清），散布 0（走直线）。
     * 颜色跟着阶段走：二阶段才是红的 —— 但这个方法本来就只在二阶段被调用，
     * 所以恒为红。留着三元是照抄原版，也方便哪天一阶段也想用。
     */
    public void soulStrikeRing(float vec, int quake, float math) {
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180F));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180F));
        double theta = (double) this.yBodyRot * (Math.PI / 180D);
        ++theta;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        // 15 颗 → 每颗之间 24 度，正好凑成一整圈。
        float angle = 360.0F / (float) quake;

        for (int i = 0; i < quake; ++i) {
            SoulStrike strike = new SoulStrike(LmEntityRegistry.SOUL_STRIKE.get(), this.level());
            strike.setDamage(12.0F);
            // 先定方向再定位置 —— 顺序和原版一致。
            // shootFromRotation 内部会读发射者的速度并叠加上去，
            // 所以放在 setPos 之前，弹幕的初速里才有圣骑自己的位移。
            strike.shootFromRotation(this, 0.0F, angle * (float) i, 0.0F, 0.45F, 0.0F);
            strike.setPos(this.getX() + (double) vec * vecX + (double) (f * math),
                    this.getY() + 0.3D,
                    this.getZ() + (double) vec * vecZ + (double) (f1 * math));
            strike.setOwner(this);
            strike.setRed(this.getPhase() >= 2);
            this.level().addFreshEntity(strike);
        }
    }

    /**
     * 从给定位置往下找地面，找到就在地面上生成一根灵魂尖柱实体。
     *
     * <p><b>注意这不是粒子，是传奇怪物的一个真·实体</b>（{@code SoulPillarEntity}）。
     * 它自己会 tick、自己找范围内的生物造成伤害、还会把人挑飞并给圣骑回血。
     * 它内部是拿 <b>caster（也就是圣骑）的 {@code isAlliedTo}</b> 来过滤友军的，
     * 所以它能不能不误伤主人，完全取决于 {@link #isAlliedTo} 那个覆写。
     *
     * @return 找到地面并且成功生成返回 true；脚下一路没地面返回 false（比如悬空施法）
     */
    private boolean spawnSoulPillars(double x, double y, double z, int lowestYCheck, float yRot,
                                     int warmupDelayTicks, Level world, LivingEntity player) {
        BlockPos blockpos = BlockPos.containing(x, y, z);
        boolean flag = false;
        double d0 = 0.0D;

        // 从落点一路往下扫，找到第一块「上表面能站人」的方块，尖柱就长在它顶上。
        // 扫到 lowestYCheck 还没找到就放弃（脚下一片虚空）。
        do {
            BlockPos blockpos1 = blockpos.below();
            BlockState blockstate = world.getBlockState(blockpos1);
            if (blockstate.isFaceSturdy(world, blockpos1, Direction.UP)) {
                // 落点位置本身如果有方块（半砖之类），把尖柱再往上抬一点，免得埋进去。
                if (!world.isEmptyBlock(blockpos)) {
                    BlockState blockstate1 = world.getBlockState(blockpos);
                    VoxelShape voxelshape = blockstate1.getCollisionShape(world, blockpos);
                    if (!voxelshape.isEmpty()) {
                        d0 = voxelshape.max(Direction.Axis.Y);
                    }
                }
                flag = true;
                break;
            }
            blockpos = blockpos.below();
        } while (blockpos.getY() >= lowestYCheck);

        if (flag) {
            // 参数依次是：世界, x, y, z, 朝向, 预热 tick, 施法者, 存活 tick, 伤害, 是否二阶段。
            world.addFreshEntity(new SoulPillarEntity(world, x, (double) blockpos.getY() + d0, z, yRot,
                    warmupDelayTicks, this, 20, 8.0F, this.getIsSecondPhase()));
            return true;
        }
        return false;
    }

    /**
     * 以圣骑为圆心，摆一圈灵魂盾（原版 {@code spawnSoulShieldRing}）。
     *
     * <p>一圈一共 {@code totalPoints} 个点位，分 {@code wavesTotal} 波生成 ——
     * 所以每个波次只摆其中「隔一个取一个」的那些，两波拼起来才是一整圈。
     * 招 25 传的是 {@code (0, 2, 16, ...)} 和 {@code (1, 2, 16, ...)}，即 16 个点分两波，各 8 个。
     *
     * <p>{@code isWarningParticle} 为 true 时只在地面画预警光圈（告诉玩家「这里马上要出事」），
     * 为 false 时才真正生成盾。所以每一波都是「先预警、后实体」成对出现的。
     *
     * @param waveIndex        这是第几波（招 25 传 0 或 1）
     * @param wavesTotal       一共分几波（招 25 传 2）
     * @param totalPoints      整圈多少个点（招 25 传 16）
     * @param radius           圈的半径。第一段用 12 格（大圈），第二段用 2 格（贴脸小圈）
     * @param isOuter          是否用「朝外」的贴图朝向。大圈 true 的那种会转过来对着圆心
     * @param isWarningParticle true = 只画预警光圈，false = 真生成灵魂盾实体
     */
    private void spawnSoulShieldRing(int waveIndex, int wavesTotal, int totalPoints, double radius,
                                     boolean isOuter, boolean isWarningParticle) {
        double step = (Math.PI * 2D) / (double) totalPoints;
        int pointsThisWave = totalPoints / wavesTotal;
        // 原版这里写的是字面量 -0.41887902047863906，其实就是 -π/7.5。
        // 这个偏移量让整圈不是从正东方起头，而是稍微转一点，视觉上不那么"对齐"。
        double base = -(Math.PI / 7.5D);
        double waveOffset = (double) waveIndex * step;

        for (int i = 0; i < pointsThisWave; ++i) {
            double angle = base + waveOffset + (double) i * (double) wavesTotal * step;
            double spawnX = this.getX() + Math.cos(angle) * radius;
            double spawnZ = this.getZ() + Math.sin(angle) * radius;
            double spawnY = (double) Mth.floor(this.getY());
            double headY = this.getY() + 1.0D;
            // 盾是朝圣骑这边望的：注意是「圆心 - 盾」，不是反过来。
            double dx = this.getX() - spawnX;
            double dz = this.getZ() - spawnZ;
            float yawRad = (float) Math.atan2(dz, dx);
            // 外圈用的朝向是「沿圆周切向」而不是「朝圆心」，否则一圈盾会全部正面朝着中心，很难看。
            float f2 = (float) i * ((float) Math.PI * 2F) / (float) pointsThisWave
                    + (((float) Math.PI * 2F) / (float) pointsThisWave - 1.0F);
            this.spawnSoulShields(spawnX, spawnZ, spawnY, headY, isOuter ? f2 : yawRad, 4,
                    (float) this.getX(), (float) this.getY(), (float) this.getZ(),
                    isOuter, isWarningParticle);
        }
    }

    /**
     * 从给定位置往下找地面，找到就在那儿生成一面灵魂盾（原版 {@code spawnSoulShields}）。
     *
     * <p>结构和 {@link #spawnSoulPillars} 是同一套「往下扫找地面」的写法，
     * 区别只在于扫到之后生成的东西不同：这边是 {@link SoulShieldEntity}（一面会飞出去打人的盾），
     * 那边是 {@code SoulPillarEntity}（一根从地里冒出来的尖柱）。
     *
     * <p>盾实体自己会 tick、自己找目标撞过去造成伤害，并且和尖柱一样靠
     * <b>施法者的 {@code isAlliedTo}</b> 来过滤友军 —— 所以它会不会误伤主人，
     * 同样取决于 {@link #isAlliedTo} 那个覆写。
     *
     * <p>⚠️ 一处与原版的有意偏差：原版起手写的是 {@code new BlockPos((int)x, (int)maxY, (int)z)}，
     * 是<b>向零截断</b>；这里用 {@code BlockPos.containing} 是<b>向下取整</b>。
     * 两者只在负坐标下差一格，而这个格子马上要往下扫一大段找地面，最终落点基本一致。
     * 取整方式更符合方块坐标的常识，就按这个来了。
     *
     * @param minY 往下找到这个高度还没地面就放弃
     * @param maxY 从这个高度开始往下扫
     */
    public void spawnSoulShields(double x, double z, double minY, double maxY, float rotation, int delay,
                                 float destX, float destY, float destZ, boolean isOuter,
                                 boolean isWarningParticle) {
        BlockPos blockpos = BlockPos.containing(x, maxY, z);
        boolean flag = false;
        double d0 = 0.0D;

        do {
            BlockPos blockpos1 = blockpos.below();
            BlockState blockstate = this.level().getBlockState(blockpos1);
            if (blockstate.isFaceSturdy(this.level(), blockpos1, Direction.UP)) {
                if (!this.level().isEmptyBlock(blockpos)) {
                    BlockState blockstate1 = this.level().getBlockState(blockpos);
                    VoxelShape voxelshape = blockstate1.getCollisionShape(this.level(), blockpos);
                    if (!voxelshape.isEmpty()) {
                        d0 = voxelshape.max(Direction.Axis.Y);
                    }
                }
                flag = true;
                break;
            }
            blockpos = blockpos.below();
        } while (blockpos.getY() >= Mth.floor(minY) - 1);

        if (!flag) {
            return;
        }

        if (isWarningParticle) {
            // 预警阶段：地面上画一个慢慢缩小的圈 + 一小撮魂火，纯客户端视觉。
            if (this.level().isClientSide) {
                this.level().addParticle(
                        this.getPhase() >= 2
                                ? (ParticleOptions) ModParticles.GROUNDSOUL_RED.get()
                                : (ParticleOptions) ModParticles.GROUNDSOUL.get(),
                        x, (double) blockpos.getY() + 2.0D + d0, z, 0.0D, 0.0D, 0.0D);
                this.level().addParticle(
                        new Circle.RingData(0.0F, (float) Math.PI / 2F, 35,
                                this.getPhase() >= 2 ? this.uR : 0.0F,
                                this.getPhase() >= 2 ? this.uG : 0.9F,
                                this.getPhase() >= 2 ? this.uB : 0.8F,
                                0.8F, 15.0F, false, Circle.EnumRingBehavior.SHRINK),
                        x, (double) blockpos.getY() + 0.25D + d0, z, 0.0D, 0.0D, 0.0D);
            }
        } else {
            // 实体阶段：真正生成灵魂盾。参数依次是
            // 世界, x, y, z, 朝向, 预热 tick, 施法者, 伤害, 目标 x/y/z, 是否外圈, 是否二阶段。
            this.level().addFreshEntity(new SoulShieldEntity(this.level(), x,
                    (double) blockpos.getY() + d0, z, rotation, delay, this, 10.0F,
                    destX, destY, destZ, isOuter, this.getPhase() >= 2));
        }
    }

    // ==================================================================
    //  十八、二连斩 / 后空翻用到的攻击与特效
    // ==================================================================

    /**
     * 朝身体侧向弹开一小段。后空翻（attackState 10）第 8 tick 调用。
     *
     * <p>做法是把「当前速度」直接加上一个侧向量。角度取 {@code getYRot() + 90}，
     * 和传奇怪物那套代码里所有方向计算保持一致。{@code v} 传负数就是往反方向弹 ——
     * 原版后空翻传的是 -1.5，所以实际是往<b>后</b>撤。
     */
    public void backStep(float v, float y) {
        float yaw = (float) Math.toRadians((double) (this.getYRot() + 90.0F));
        Vec3 dodgePos = this.getDeltaMovement().add((double) v * Math.cos((double) yaw), (double) y,
                (double) v * Math.sin((double) yaw));
        this.setDeltaMovement(dodgePos.x, dodgePos.y, dodgePos.z);
    }

    // ==================================================================
    //  十八之二、投匕首（招 15 / 28）
    // ==================================================================

    /**
     * 在「手部位置」撒一颗灵魂弹粒子，做出「匕首脱手」的视觉。
     *
     * <p>{@code vec} 是沿身体<b>前方</b>推出去的距离（2 格左右，即手臂长度 + 一点余量），
     * {@code offset} 是沿身体<b>右侧</b>的横移（左右手各给一个正负值）。
     * 两者用两套不同的三角函数算：
     * <ul>
     *   <li>横移用 {@code yBodyRot}（不含那 +1 弧度）；</li>
     *   <li>前推用的 {@code theta} 算完还 {@code ++} 了一下，也就是整体再转 <b>1 弧度（约 57°）</b>。
     *       看着像笔误，但这是原版行为，改了匕首就会从肩膀旁边冒出来。</li>
     * </ul>
     *
     * <p>粒子颜色跟阶段走：一阶段 {@code SOUL_SHOOT}，二阶段 {@code SOUL_SHOOT_RED}。
     * 高度固定 {@code +1.75} —— 圣骑高 3 格，这大概在胸口偏上，就是持械手的位置。
     */
    public void addShootParticle(float vec, float offset) {
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180F));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180F));
        double theta = (double) this.yBodyRot * (Math.PI / 180D);
        ++theta;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        this.level().addParticle(
                this.getPhase() >= 2 ? ModParticles.SOUL_SHOOT_RED.get() : ModParticles.SOUL_SHOOT.get(),
                this.getX() + (double) vec * vecX + (double) (f * offset),
                this.getY() + (double) 1.75F,
                this.getZ() + (double) vec * vecZ + (double) (f1 * offset),
                0.0D, 0.0D, 0.0D);
    }

    /**
     * 朝目标甩出一把匕首，构成一个<b>扇形弹幕</b>。招 15 / 28 以及格挡反击（5）都用它。
     *
     * <p>参数：{@code velocity} 初速度；{@code x/y/z} 出手点；{@code daggerCount} 这一轮几把；
     * {@code angleBetween} 相邻两把之间的夹角（度）；{@code returnTick} 飞多久开始往回拐
     * （越大飞得越远，由调用方按「到目标的距离」算出来）。
     *
     * <p>⚠️ {@code elevationAngle} 这个参数<b>原版传了但从头到尾没被用过</b> ——
     * 真正决定仰角的是下面那个 {@code Math.toRadians(dy)}。其中 {@code dy} 是
     * 「目标 Y − 出手点 Y」的<b>格数差</b>，却直接当成了角度来转弧度：
     * 目标高 3 格就当成 3 弧度（≈172°，几乎是正上方）。
     * 这显然是原版的笔误，但照抄 —— 改成正确的角度反而会让匕首的飞行弧线跟原作不一样。
     *
     * <p>出手点统一抬到 {@code y + 碰撞箱高度/2}，也就是身体中部，别的招式也都这么算。
     */
    public void throwDaggers(float velocity, double x, double y, double z, int daggerCount,
                             float angleBetween, float elevationAngle, int returnTick) {
        float offset = 2.0F;
        float vec = 2.0F;
        float behindVec = 0.5F;
        // 三颗粒子：正中一颗，左右手各一颗（靠 offset 的正负区分）。
        this.addShootParticle(vec, 0.0F);
        this.addShootParticle(vec - behindVec, offset);
        this.addShootParticle(vec - behindVec, -offset);

        if (this.targetIsNotNull()) {
            double dx = this.target().getX() - x;
            double dz = this.target().getZ() - z;
            double dy = this.target().getY() - y;
            Vec3 flatDir = new Vec3(dx, dy, dz).normalize();
            double elevRad = Math.toRadians(dy);
            // 扇形以「指向目标」为中心线，向左右各摊开一半。
            double totalSpread = (double) (angleBetween * (float) (daggerCount - 1));
            double startYaw = -totalSpread * 0.5D;

            for (int i = 0; i < daggerCount; ++i) {
                double yawOffset = startYaw + (double) ((float) i * angleBetween);
                Vec3 dirYaw = MathUtils.rotateYaw(flatDir, yawOffset);
                double cosP = Math.cos(elevRad);
                double sinP = Math.sin(elevRad);
                Vec3 finalVec = new Vec3(dirYaw.x * cosP, sinP, dirYaw.z * cosP);

                ThrownPhantomDagger dagger = new ThrownPhantomDagger(
                        LmEntityRegistry.THROWN_PHANTOM_DAGGER.get(), this.level());
                // 用 setPosRaw 而不是 setPos：setPos 会顺带做碰撞箱重算和区块更新，
                // 每 tick 密集刷十几把的时候没必要，原版也是这么写的。
                dagger.setPosRaw(x, y + (double) (this.getBbHeight() / 2.0F), z);
                dagger.setReturnEntity(this);
                dagger.setReturnTick(returnTick);
                dagger.setDamage(6.0F);
                dagger.shoot(finalVec.x, finalVec.y, finalVec.z, velocity, 0.0F);
                // owner 决定「这发算谁打的」，回程目标由上面的 setReturnEntity 单独指定。两者都是圣骑。
                dagger.setOwner(this);
                dagger.setRed(this.getPhase() >= 2);
                this.level().addFreshEntity(dagger);
            }
        }
    }

    /**
     * 朝某个坐标「扑」过去。翻跟头砸（状态 12 / 14）第 6 / 2 tick 调用。
     *
     * <p>它不是「瞬移」也不是「寻路」，而是<b>一次性给一个初速度</b>然后让物理自己飞：
     * <ol>
     *   <li>算出「目标 − 自己」的方向向量，整体乘 0.8；</li>
     *   <li>水平方向再乘 0.2 —— 所以水平速度其实是 <b>距离 × 0.16</b>，离得越远飞得越快；</li>
     *   <li>竖直方向取常量 0.3（约等于一个普通跳跃），再加上「高度差 × 0.075」，
     *       并用 {@link Mth#clamp} 夹在 0 ~ 10 之间 —— 目标在高处就跳得更猛，但不会离谱；</li>
     *   <li>竖直分量最后<b>不乘 0.2</b>（乘的是 1.0），这是它和水平分量唯一的区别。</li>
     * </ol>
     *
     * <p>0.3 这个起跳速度值得记一下：原版把它写死在代码里，和最大跳跃力度完全无关 ——
     * 也就是说扑上三格高的台子这种活它做不到，会一头撞在墙上。原版行为如此。
     */
    public void jumpTowardsPosition(double x, double y, double z) {
        Vec3 start = new Vec3(this.getX(), this.getY(), this.getZ());
        Vec3 end = new Vec3(x, y, z);
        Vec3 sub = end.subtract(start);
        Vec3 finalPos = sub.scale(0.8);
        double d0 = finalPos.x;
        double d1 = finalPos.y;
        double d2 = finalPos.z;
        Vec3 vec3 = new Vec3(d0, 0.3 + Mth.clamp(d1 * 0.075, 0.0, 10.0), d2)
                .multiply(0.2, 1.0, 0.2);
        this.setDeltaMovement(vec3);
    }

    /**
     * 只在翻跟头扑击的那两个状态里把自己变成「实体障碍」。
     *
     * <p>原版是这么写的（{@code PossessedPaladinEntity.java:2748}），照抄。
     * 有意思的是它的实际作用和直觉相反，值得说清楚：
     *
     * <p>Minecraft 里生物的 {@code canBeCollidedWith()} <b>默认返回 false</b> ——
     * 所以我们平时走路会<b>穿过</b>其它生物（只靠推力互相挤开，不会挡住路）。
     * 这个方法返回 true 的时候，实体的移动碰撞才会把它当成一堵墙。
     * 换句话说：圣骑平时是「幽灵」，只有在扑过来砸你那一下（状态 12 起跳后、状态 14 收招时）
     * 才变成「实心」—— 撞上去会被顶住。
     *
     * <p>这两个状态号不是随手写的：12 是扑击本体，14 是它落地后的长收招（47 tick）。
     * 状态 13（20 tick 的短收招）<b>不在</b>其中，所以那一下是穿得过去的。
     */
    @Override
    public boolean canBeCollidedWith() {
        return this.getAttackState() == 12 || this.getAttackState() == 14;
    }

    /**
     * 挥剑的「剑光」特效。纯客户端视觉，<b>不参与判定</b> —— 打不打得中另由
     * {@link #SideAreaAttack} 负责。
     *
     * <p>参数含义（照抄原版）：
     * <ul>
     *   <li>{@code pos} / {@code posOffset} —— 以身体朝向为基准的偏移，决定剑光出现在身前还是身侧；</li>
     *   <li>{@code yHeight} —— 剑光的高度跨度，配合 {@code additionalY} 决定它出现在多高；</li>
     *   <li>{@code reverse} —— 反手挥。为 true 时剑光方向整个翻转，二连斩第二刀用的就是它；</li>
     *   <li>{@code scale} —— 剑光大小；{@code rot} —— 反手时的基准角度；</li>
     *   <li>{@code small} —— 原版有这个参数，但函数体里从没用过，保留只为对齐签名。</li>
     * </ul>
     */
    public void createSweep(float pos, float posOffset, float yHeight, double additionalY, boolean reverse,
                            float scale, float rot, boolean small) {
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180F));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180F));
        double theta = (double) this.yBodyRot * (Math.PI / 180D);
        ++theta;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        double x = this.getX() + (double) pos * vecX + (double) (f * posOffset);
        double z = this.getZ() + (double) pos * vecZ + (double) (f1 * posOffset);
        if (this.level().isClientSide) {
            // 高度取「脚底 + 身高一半 + 额外抬升」，所以剑光大致落在腰胸位置。
            double d1 = this.getY() + (double) (this.getBbHeight() / 2.0F) + additionalY;
            float yaw = (float) Math.toRadians((double) (-this.yBodyRot + (reverse ? rot : 180.0F)));
            double lookX = -Math.cos((double) yaw);
            double lookZ = -Math.sin((double) yaw);
            // 俯仰角由「高度跨度」和「水平长度」算出来，反手时再取反。
            float pitch = (float) (reverse ? -1 : 1)
                    * (float) Math.atan2((double) yHeight, Math.sqrt(lookX * lookX + lookZ * lookZ));
            // 原版写的是 this.getScale() * scale。getScale() 来自传奇怪物自己的 IAnimatedBoss 接口，
            // 我们的继承链里没有，用等价的常量 2.0F 代替（和砸地那里的处理一致）。
            if (this.getPhase() >= 2) {
                this.level().addParticle(new SoulSweepRedParticle.SweepData(2.0F * scale, yaw, pitch),
                        x, d1, z, 0.0D, 0.0D, 0.0D);
            } else {
                this.level().addParticle(new SoulSweepParticle.SweepData(2.0F * scale, yaw, pitch),
                        x, d1, z, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    // ==================================================================
    //  十八之三、突刺抓取（招 19 / 20 / 21 / 33）
    // ==================================================================

    /**
     * 突刺抓取的核心判定：正前方一个「又扁又长」的盒子，打中就把人<b>拽上背</b>。
     *
     * <p>盒子的做法和 {@code StraightLineAreaAttack} 一样 —— 从自身碰撞箱出发，
     * 往左右各鼓出 {@code boxWidth}、往上鼓出 {@code yHeight}，再朝<b>朝向的前方</b>
     * 拉长 {@code range} 格。注意这里的「前方」用的是 {@code getYRot() + 90}，
     * 和其它招式一致（不是 {@code yBodyRot}，别改）。
     *
     * <p>抓取成功与否记在 {@link #succedGrabbing}，下一招的去向由它决定
     * （见 {@code PossessedPaladinStabGrabGoal.stop()}）。
     *
     * <p>⚠️ 两个坑：
     * <ol>
     *   <li>{@code succedGrabbing} 是在<b>循环里逐个体</b>赋值的。也就是说打到多个目标时，
     *       最终值取决于<b>最后</b>一个被处理的实体 —— 最后一个没骑上，前面骑上的也算没成功。
     *       原版就是这么写的，照抄。</li>
     *   <li>方法签名里的 {@code launch} / {@code launchPower} 两个参数，原版<b>传了但函数体里
     *       一次都没用过</b>（抓取本身就是位移，不需要额外击飞）。保留是为了跟注册处对齐。</li>
     * </ol>
     *
     * <p>原版开头还有个 {@code boolean var10000 = entityHit == this.target();}，
     * 算完从不读取，死代码，不搬。
     */
    private void StabGrab(float boxWidth, float yHeight, float range, int brokenShieldTicks,
                          float damage, boolean launch, float launchPower) {
        double rad = Math.toRadians((double) (this.getYRot() + 90.0F));
        double xRange = (double) range * Math.cos(rad);
        double zRange = (double) range * Math.sin(rad);
        AABB attackRange = this.getBoundingBox()
                .inflate((double) boxWidth, (double) yHeight, (double) boxWidth)
                .expandTowards(xRange, 0.0D, zRange);

        for (LivingEntity entityHit : this.level().getEntitiesOfClass(LivingEntity.class, attackRange)) {
            if (!this.isAlliedTo(entityHit) && entityHit != this) {
                boolean flag = entityHit.hurt(this.damageSources().mobAttack(this),
                        (float) ((double) damage
                                + (double) MathUtils.entityBasedHpDamage(entityHit, 3.0F)
                                * (Double) ModConfig.MOB_CONFIG.PosessedPaladinDamageMutliplier.get()));
                if (flag) {
                    this.applyStackingEffect(entityHit, ModEffects.SOUL_FRACTURE.get(), 1, 4,
                            MathUtils.toTicks(10.0F));
                    EntityUtil.cancelBuffs(entityHit);
                    // startRiding 的第二个参数 true = 强制骑乘（绕过各种「能不能骑」的检查）。
                    // 骑上去了才叫抓成功。
                    boolean mounted = entityHit.startRiding(this, true);
                    if (mounted) {
                        entityHit.setShiftKeyDown(false);
                        this.succedGrabbing = true;
                    } else {
                        this.succedGrabbing = false;
                    }
                } else {
                    this.succedGrabbing = false;
                }

                if (entityHit instanceof Player && entityHit.isBlocking() && brokenShieldTicks > 0) {
                    disableShield(entityHit, brokenShieldTicks);
                }
            }
        }
    }

    /**
     * 在身体前方灌一团粒子球。抓取处决（状态 20）第 63~68 tick 用它做「灵魂外溢」。
     *
     * <p>三层嵌套循环在 {@code [-size, size]} 的立方体里逐格取点，每格再按「球面方向」
     * 撒一颗粒子 —— 分母 {@code d6} 是到球心的距离，把偏移量除一遍就等于<b>投影到球面上</b>，
     * 所以灌出来的是一个壳，不是实心方块。
     *
     * <p>那个 {@code k += size * 2 - 1} 是作者省算力的写法：只要不在立方体的六个面上，
     * 就直接把内层循环跳到最后 —— 因为球壳内部根本不需要撒点。
     *
     * <p>只在客户端跑，而且隔 tick 一次（{@code tickCount % 2 == 0}），否则粒子量会翻四倍。
     */
    private void SphereParticle(ParticleOptions particleType, float height, float vec, float size) {
        if (this.level().isClientSide && this.tickCount % 2 == 0) {
            double d0 = this.getX();
            double d1 = this.getY() + (double) height;
            double d2 = this.getZ();
            double theta = (double) this.yBodyRot * (Math.PI / 180D);
            ++theta;
            double vecX = Math.cos(theta);
            double vecZ = Math.sin(theta);

            for (float i = -size; i <= size; ++i) {
                for (float j = -size; j <= size; ++j) {
                    for (float k = -size; k <= size; ++k) {
                        double d3 = (double) j + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                        double d4 = (double) i + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                        double d5 = (double) k + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                        double d6 = (double) Mth.sqrt((float) (d3 * d3 + d4 * d4 + d5 * d5)) / 0.5D
                                + this.random.nextGaussian() * 0.05D;
                        this.level().addParticle(particleType,
                                d0 + (double) vec * vecX, d1, d2 + (double) vec * vecZ,
                                d3 / d6, d4 / d6, d5 / d6);
                        if (i != -size && i != size && j != -size && j != size) {
                            k += size * 2.0F - 1.0F;
                        }
                    }
                }
            }
        }
    }

    /**
     * 把背上的受害者<b>朝指定方向扔出去</b>。处决（状态 20）第 109 tick 用它收尾。
     *
     * <p>真正飞出去的东西是传奇怪物的 {@code EntityThrownEntity} —— 一个会带着乘客一起
     * 抛物线飞行的投射物。我们直接引用它（和本项目引用传奇怪物的粒子、音效、弹射物基类
     * 是同一个做法），不另造一个。
     *
     * <p>参数：{@code destX/Y/Z} 是<b>目标落点</b>，{@code x/y/z} 是<b>出手点</b>；
     * 最后那个 {@code passenger} 就是背上那位。整个方法做的就是「算出从出手点到落点的向量，
     * 交给 {@code shoot()} 让它自己飞」。
     *
     * <p>{@code d3 * 0.2} 那一项是<b>距离补偿</b>：水平距离越远，竖直初速度就抬得越高，
     * 这样不管扔多远都能砸到差不多的地方。后面的散布角度
     * {@code 14 - 难度等级 * 4} 是「难度越高越准」。
     *
     * <p>原版还算了 {@code double d4 = (destX - y) * 0.5F;}，算完从不读取，死代码，不搬。
     */
    public void throwAnGravityEntity(float velocity, double destX, double destY, double destZ,
                                     double x, double y, double z, float damage, LivingEntity passenger) {
        if (passenger != null) {
            EntityThrownEntity thrownEntity = new EntityThrownEntity(this.level(), this, x, y, z,
                    damage, 1.0F, passenger);
            thrownEntity.setPosRaw(x, y, z);
            double d0 = destX - x;
            double d1 = destY + 0.5D - thrownEntity.getY();
            double d2 = destZ - z;
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            thrownEntity.shoot(d0, d1 + d3 * 0.2D, d2, velocity,
                    (float) (14 - this.level().getDifficulty().getId() * 4));
            thrownEntity.setOwner(this);
            this.level().addFreshEntity(thrownEntity);
        }
    }

    /**
     * 格挡反击打出的眩晕时长（55 tick ≈ 2.75 秒）。
     *
     * <p>原版就是这么个孤零零写死 55 的方法，没有配置项、也不随阶段变化 ——
     * 单独抽成方法大概只是为了以后好改。照搬。
     */
    public int getStunDuration() {
        return 55;
    }

    /**
     * 在身体周围铺一圈「Z 字形」的灵魂剑刃。跳劈落地（状态 24）用它，二阶段的跳砸（状态 32）也用。
     *
     * <h2>它在铺什么</h2>
     * 先画 {@code rune} 条<b>放射线</b>（注册处传 12，也就是绕一圈 12 个方向），
     * 每条线上再等距排 <b>8</b> 个点，每个点上试着放一柄剑刃 —— 理论上是 12 × 8 = 96 个。
     *
     * <h2>名字里的 Zigzag 是从哪来的</h2>
     * 每条线上的 8 个点<b>并不在一条直线上</b>：{@code group = (k / offset) % 2} 让相邻的点
     * 交替地往左 / 往右偏 {@code amplitude} 格（注册处传 2.0）。{@code offset} 是
     * 「每几个点换一次边」，传 1 就是隔一格换一次 —— 于是 8 个点串起来是一条 <b>Z 字形锯齿</b>。
     * 这就是「剑刃从地里一路歪歪扭扭地扎过来」那个视觉效果的来源。
     *
     * <h2>⚠️ 同一个 {@code particleWarning} 参数，决定了「真打」还是「预告」</h2>
     * 见 {@link #spawnBlades}：{@code warning} 为 true 时只撒粒子、<b>不生成任何实体</b>。
     * 所以同一招会调它两次 —— 第 4 tick 撒一圈粒子预告落点，第 7 tick 才真把剑刃放出来。
     *
     * <h2>{@code delay} 是怎么算的</h2>
     * {@code delay = time × (k + 1)}，注册处 time 传 1.0 —— 于是第 k 圈点的延迟正好是 k+1 tick。
     * 效果是从内圈往外圈<b>依次</b>冒出来，而不是 96 柄剑「哗」地一下全炸开。
     *
     * <p>原版签名里还有 {@code isFalling}：为 true 时生成会从天上砸下来的
     * {@code FallingSoulBladeEntity}。一阶段的跳劈固定传 false（从地里冒出来那种）。
     */
    private void strikeZigzagXBlades(int rune, double time, boolean isFalling, float damage,
                                     float divider, int offset, double amplitude, boolean particleWarning) {
        for (int i = 0; i < rune; ++i) {
            float throwAngle = (float) i * (float) Math.PI / ((float) rune / divider);
            // 这条放射线的「垂直方向」单位向量 —— 上面说的左右偏移就是沿着它推的。
            float perpX = -Mth.sin(throwAngle);
            float perpZ = Mth.cos(throwAngle);

            for (int k = 0; k < 8; ++k) {
                double forward = 1.25F * (double) (k + 1);
                int group = k / offset % 2;
                double lateral = amplitude * (double) (group == 0 ? 1 : -1);
                double xOff = (double) Mth.cos(throwAngle) * forward + (double) perpX * lateral;
                double zOff = (double) Mth.sin(throwAngle) * forward + (double) perpZ * lateral;
                int delay = (int) (time * (double) (k + 1));
                this.spawnBlades(this.getX() + xOff, this.getZ() + zOff, this.getY(),
                        this.getY() + 2.0D, throwAngle, delay, isFalling, damage, particleWarning);
            }
        }
    }

    /**
     * 在指定的水平位置上<b>往下找到地面</b>，再决定是撒预警粒子还是真生成一柄灵魂剑刃。
     *
     * <h2>找地面那一段</h2>
     * 从 {@code maxY}（身体高度再往上 2 格）开始，用 {@code blockpos.below()} 一格一格往下扫，直到：
     * <ul>
     *   <li>脚下那块方块的<b>上表面是实心的</b>（{@code isFaceSturdy(..., UP)}）→ 找到了，{@code foundGround} 置真；</li>
     *   <li>或者已经扫到 {@code minY}（身体高度）下面 1 格 → 放弃，这一根就不生成了。</li>
     * </ul>
     * 找到地面之后还要再量一下「站的那一格自己有没有碰撞箱」（台阶、雪层、地毯这类），
     * 有的话把 {@code groundOffset} 抬到它的顶面 —— 免得剑刃陷进方块里只露个尖。
     *
     * <h2>两种结局</h2>
     * 只有找到了地面（{@code foundGround}）才有下文：
     * <ul>
     *   <li>{@code warning = true}：脚下撒一颗粒子（二阶段会变红）+ 一圈往<b>内收缩</b>的环。
     *       纯预告，不生成实体 —— 玩家看到这圈东西就知道「这里等会儿要冒剑，快走」。</li>
     *   <li>{@code warning = false}：真生成一柄剑刃。{@code falling} 决定是
     *       {@code FallingSoulBladeEntity}（砸下来）还是 {@code SoulBladeEntity}（地里冒出来）。</li>
     * </ul>
     *
     * <h2>⚠️ 友军安全（这一条查过字节码了）</h2>
     * 这两种剑刃实体是传奇怪物的，我们<b>原样引用、没有改造</b>。查过它们的 {@code damage()} 字节码：
     * 里面调的是 {@code caster.isAlliedTo(目标)}，而 caster 就是我们传进去的圣骑仆从本身 ——
     * 所以主人和同伴不会被自己的剑刃扎到，和那 14 招用的是同一套判定。
     * <br>（对比一下：幻影匕首 {@code ThrownPhantomDagger} 原版查的是传奇怪物的阵营标签，
     * 那个我们<b>改过</b>。两者的处理方式不同，别混。）
     *
     * <h2>⚠️ 客户端也会跑一遍这个循环</h2>
     * {@code UpdateWithAttack} 在 {@code aiStep} 里调用，没有分客户端 / 服务端，所以两边都会
     * 各跑一遍这 96 次循环。生成实体那半边是安全的 —— 客户端 {@code addFreshEntity} 不生效，
     * 真正加进世界的只有服务端那一次。粒子那半边原版没做客户端判断，这里照抄，不额外加。
     */
    private void spawnBlades(double x, double z, double minY, double maxY, float rotation,
                             int delay, boolean falling, float damage, boolean warning) {
        BlockPos blockpos = new BlockPos((int) x, (int) maxY, (int) z);
        boolean foundGround = false;
        double groundOffset = 0.0D;

        do {
            BlockPos below = blockpos.below();
            BlockState belowState = this.level().getBlockState(below);
            if (belowState.isFaceSturdy(this.level(), below, Direction.UP)) {
                if (!this.level().isEmptyBlock(blockpos)) {
                    BlockState hereState = this.level().getBlockState(blockpos);
                    VoxelShape shape = hereState.getCollisionShape(this.level(), blockpos);
                    if (!shape.isEmpty()) {
                        groundOffset = shape.max(Axis.Y);
                    }
                }
                foundGround = true;
                break;
            }
            blockpos = blockpos.below();
        } while (blockpos.getY() >= Mth.floor(minY) - 1);

        if (foundGround) {
            // 剑刃的落脚高度：地面那格的 y，再加上「站在上面的东西」的顶面高度。
            double spawnY = (double) blockpos.getY() + groundOffset;

            if (warning) {
                // 预警光柱和地面光圈都分阶段：二阶段换成红色版。
                this.level().addAlwaysVisibleParticle(this.getPhase() >= 2
                                ? ModParticles.GROUNDSOUL_RED.get() : ModParticles.GROUNDSOUL.get(),
                        x, spawnY + 2.0D, z, 0.0D, 0.0D, 0.0D);
                this.level().addParticle(new Circle.RingData(0.0F, (float) Math.PI / 2F, 35,
                                this.getPhase() >= 2 ? this.uR : 0.0F,
                                this.getPhase() >= 2 ? this.uG : 0.9F,
                                this.getPhase() >= 2 ? this.uB : 0.8F,
                                0.8F, 15.0F, false, Circle.EnumRingBehavior.SHRINK),
                        x, spawnY + 0.25D, z, 0.0D, 0.0D, 0.0D);
            } else if (falling) {
                // 最后一个参数是「红色版」开关，二阶段才亮红。
                this.level().addFreshEntity(new FallingSoulBladeEntity(this.level(), x, spawnY, z,
                        rotation, delay, this, damage, this.getPhase() >= 2));
            } else {
                this.level().addFreshEntity(new SoulBladeEntity(this.level(), x, spawnY, z,
                        rotation, delay, this, damage, this.getPhase() >= 2));
            }
        }
    }

    /**
     * 震地：把周围一圈生物<b>往上弹</b>。
     *
     * <p>目前只有二阶段变身第 49 tick 用它（半径 15 格、力度 0.25），
     * 配合同一 tick 的 360 度范围伤害，做出「一跺脚，全场人被掀起来」的效果。
     *
     * <p>它<b>不造成任何伤害</b>，只改 Y 轴速度。这一点和原版一致 ——
     * 伤害是上一条 {@link #SideAreaAttack} 打的，这里纯粹是位移。
     *
     * <p>{@code EntityUtil.applyPlayerDeltaMovement()} 是传奇怪物的工具方法。原版玩家和
     * 服务端之间不同步速度，玩家自己算的位移和服务端算的对不上，会「弹起来又瞬移回去」；
     * 这个方法就是把这个速度同步给玩家客户端用的。不调它，玩家看到的就是抽搐。
     *
     * <h2>⚠️ 这里的 {@code isAlliedTo} 筛选是本移植加的，原版没有</h2>
     * 原版是 BOSS，全场都是敌人，所以直接遍历、见到谁弹谁。
     * 但仆从身边站着的是<b>主人和主人的其它仆从</b> —— 照抄的话，圣骑每次变身都会把主人弹上天，
     * 摔下来还要吃摔落伤害（顺带一提，主人不是圣骑，可没有免摔落）。
     * 所以这里必须跳过自己人。
     */
    public void earthquakeEffect(float range, float amplitude) {
        for (LivingEntity livingEntity : this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate((double) range))) {
            if (this.isAlliedTo(livingEntity)) {
                continue;
            }
            Vec3 delta = livingEntity.getDeltaMovement();
            livingEntity.setDeltaMovement(delta.x, delta.y + (double) amplitude, delta.z);
            EntityUtil.applyPlayerDeltaMovement(livingEntity);
        }
    }

    /**
     * 这一刻打出去的算不算「法术攻击」—— 原版拿它决定 {@link #SideAreaAttack} 用哪种伤害类型。
     *
     * <p>只有两个状态算：<b>状态 20（突刺抓取处决）</b>和<b>状态 26（二阶段变身）</b>。
     * 这两个都是「灵魂力量外放」的表现，所以走 {@code ModDamageTypes.causeGhostlyDamage}
     * 那套幽灵伤害（无视护甲）；其余横扫全是普通近战伤害，会被护甲减。
     *
     * <p>⚠️ 别顺手把它扩成「所有范围招都算」—— 原版就只有这两个状态返回 true，
     * 改成别的会让圣骑的平砍变成无视护甲，伤害直接失控。
     */
    public boolean isMagicAttack() {
        return this.getAttackState() == 20 || this.getAttackState() == 26;
    }

    /**
     * 身边的<b>扇形</b>范围伤害。横扫类招式都用它 —— 二连斩、盾击、掷三叉戟的挥砍段。
     *
     * <p>和 {@link #StraightLineAreaAttack} 的区别值得记一下：那个是把碰撞箱往正前方
     * <b>拉长成一条直线</b>再取盒子里的实体（适合劈砍），这个是以自身为圆心、按<b>角度</b>
     * 筛出面前 {@code arc} 度扇形内的敌人（适合横扫）。
     *
     * <p>参数：{@code range} 半径；{@code height} 高度；{@code arc} 张角（度，180 就是正前方半圆）；
     * {@code boxOffset} 扇形整体偏转的角度；{@code forwardOffset} 圆心往前挪多少格；
     * {@code damage} 基础伤害；{@code brokenShieldTicks} 破盾时长；{@code soundEvent}/{@code pitch}
     * 命中音效与音调；{@code launch} 是否击飞。
     *
     * <p>伤害类型由 {@link #isMagicAttack()} 决定：状态 20（处决）和状态 26（变身）的横扫走
     * 「幽灵伤害」，其余走普通近战伤害。这一点原先被简化掉了，2026-09-24 的对比里补回来的。
     * 状态 5 命中附加眩晕、状态 20 命中回血那两条见 {@link #sideAreaAttack}。
     */
    public void SideAreaAttack(float range, float height, float arc, float boxOffset, float forwardOffset,
                               float damage, int brokenShieldTicks, SoundEvent soundEvent, float pitch,
                               boolean launch, float launchPower) {
        this.sideAreaAttack(range, height, arc, boxOffset, forwardOffset, damage, brokenShieldTicks,
                soundEvent, pitch, launch, launchPower, false);
    }

    /**
     * 和 {@link #SideAreaAttack} 几乎完全一样的扇形判定，唯一的实际差别是：
     * <b>打中时会把 {@link #hasHurt} 置 true</b>（{@code SideAreaAttack} 不置）。
     *
     * <p>这个标记是给剑气斩（招 16）用的：它收招时要靠 {@code hasHurt} 决定接哪一招 ——
     * 砍空了就顺势接突刺抓取（19），砍中了就接收招（18/17）。所以这个「有没有打中」的记录
     * 必须由这一招自己的判定来写。
     *
     * <p>原版把这两个方法各写了一份一百多行的复制粘贴，这里抽成共用的私有实现 +
     * 一个 {@code gambited} 开关。行为完全一致，但以后改判定逻辑只用改一处。
     *
     * <p>除了 {@code hasHurt}，两者<b>还有</b>一处差别：原版 {@code SideAreaAttack} 的伤害来源会问一句
     * {@code isMagicAttack()}，是法术攻击就走「灵魂伤害」类型；{@code GambitedSideAreaAttack}
     * 则一律走普通近战伤害、<b>不问</b>。所以本方法内部有个 {@code gambited} 条件，
     * 别看着多余就删掉。
     *
     * <p>{@code SideAreaAttack} 独有的第三处差别是<b>状态 20 命中回血</b>，
     * 同样挂在 {@code !gambited} 上。详见 {@link #sideAreaAttack}。
     */
    public void GambitedSideAreaAttack(float range, float height, float arc, float boxOffset, float forwardOffset,
                                       float damage, int brokenShieldTicks, SoundEvent soundEvent, float pitch,
                                       boolean launch, float launchPower) {
        this.sideAreaAttack(range, height, arc, boxOffset, forwardOffset, damage, brokenShieldTicks,
                soundEvent, pitch, launch, launchPower, true);
    }

    /**
     * 扇形判定的真正实现。{@code gambited} 见 {@link #GambitedSideAreaAttack} 的说明。
     */
    private void sideAreaAttack(float range, float height, float arc, float boxOffset, float forwardOffset,
                                float damage, int brokenShieldTicks, SoundEvent soundEvent, float pitch,
                                boolean launch, float launchPower, boolean gambited) {
        // 圆心可以往前挪；不挪就是以自己为圆心。
        double theta = Math.toRadians((double) this.yBodyRot) + (Math.PI / 2D);
        double forwardX = Math.cos(theta) * (double) forwardOffset;
        double forwardZ = Math.sin(theta) * (double) forwardOffset;

        for (LivingEntity entityHit : this.getEntityLivingBaseNearby(
                (double) range, (double) height, (double) range, (double) range)) {
            double dx = entityHit.getX() - (this.getX() + forwardX);
            double dz = entityHit.getZ() - (this.getZ() + forwardZ);
            // 目标相对自身的方向角，减 90 度换算成「以身体朝向为 0 度」的坐标系。
            float entityHitAngle = (float) ((Math.toDegrees(Math.atan2(dz, dx)) - (double) 90.0F)
                    % (double) 360.0F);
            if (entityHitAngle < 0.0F) {
                entityHitAngle += 360.0F;
            }

            float entityAttackingAngle = (this.yBodyRot - boxOffset) % 360.0F;
            if (entityAttackingAngle < 0.0F) {
                entityAttackingAngle += 360.0F;
            }

            float entityHitDistance = (float) Math.sqrt(dx * dx + dz * dz);
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            // 中间那三个角度条件是在处理「绕圈」：-350 度和 +10 度其实是同一个方向，
            // 只写区间判断会漏掉跨过 0 度的那部分。
            if (entityHitDistance <= range
                    && (entityRelativeAngle <= arc / 2.0F && entityRelativeAngle >= -arc / 2.0F
                    || entityRelativeAngle >= 360.0F - arc / 2.0F
                    || entityRelativeAngle <= -360.0F + arc / 2.0F)
                    && !this.isAlliedTo(entityHit)
                    && !(entityHit instanceof PossessedPaladinServant)
                    && entityHit != this) {
                // 伤害类型：状态 20（处决）/ 26（变身）走「幽灵伤害」（无视护甲），其余走普通近战。
                // ⚠️ Gambited 那一支<b>没有</b>这个开关，永远走近战 —— 原版如此，别合并。
                DamageSource damageSource = !gambited && this.isMagicAttack()
                        ? ModDamageTypes.causeGhostlyDamage(this, this)
                        : this.damageSources().mobAttack(this);
                boolean flag = entityHit.hurt(damageSource,
                        (float) ((double) damage
                                + (double) MathUtils.entityBasedHpDamage(entityHit, 3.0F)
                                * (Double) ModConfig.MOB_CONFIG.PosessedPaladinDamageMutliplier.get()));
                if (flag) {
                    EntityUtil.cancelBuffs(entityHit);
                    // 清零无敌帧，否则同一次挥砍对同一个目标只能打到一下。
                    entityHit.invulnerableTime = 0;
                    // ⚠️ 这一句是<b>状态 20（突刺抓取处决）专属的回血</b>，别当成通用逻辑挪走。
                    //    只有状态 20 才会用 SideAreaAttack 打出那记 arc=360 的全周横扫
                    //    （就是第 63 tick 那一下，半径 4 格）。
                    //    判定挂在这里而不是状态 20 的分支里，所以「只要在扇形判定里打中了就回血」——
                    //    一次挥砍打到几个人就回几次，这也是处决中途的主要续航来源。
                    //    ⚠️ 同文件里的 GambitedSideAreaAttack（剑气斩）<b>没有</b>这一句，原版如此。
                    if (!gambited && this.getAttackState() == 20) {
                        this.heal(3.0F + MathUtils.entityBasedHpDamage(entityHit, 1.0F));
                    }
                    if (gambited) {
                        // 剑气斩专用：记下「这一招打中了」，收招时据此决定接哪一招。
                        this.hasHurt = true;
                    }
                    if (launch) {
                        this.launch(entityHit, true);
                    }
                    // 格挡反击（状态 5）的专属追加：打中就把人<b>眩晕</b> 55 tick（约 2.75 秒）。
                    // 这是圣骑整套招式里唯一的硬控，也是「格挡成功了要付出代价」的那部分。
                    // 注意它挂在判定里而不是招式分支里 —— 状态 5 的扇形判定用的是本方法，
                    // 所以只要在 UpdateWithAttack 起手前把状态设成 5，这一条就会生效。
                    if (this.getAttackState() == 5) {
                        entityHit.addEffect(new MobEffectInstance(ModEffects.STUN.get(),
                                this.getStunDuration(), 0));
                    }
                    this.applyStackingEffect(entityHit, ModEffects.SOUL_FRACTURE.get(), 1, 4,
                            MathUtils.toTicks(10.0F));
                    this.playSound(soundEvent, 1.0F, pitch);
                }
                if (entityHit instanceof Player && entityHit.isBlocking() && brokenShieldTicks > 0) {
                    disableShield(entityHit, brokenShieldTicks);
                }
            }
        }
    }

    // ==================================================================
    //  十八之四、掷三叉戟（招 38）
    // ==================================================================

    /**
     * 把手里那把灵魂三叉戟朝目标扔出去。招 38 第 28 tick 调用一次。
     *
     * <p>它是这一招唯一的远程手段，也是整招的「开场」——
     * 扔完之后圣骑才贴上去补两刀横扫。三叉戟本身的行为（飞行、插地炸一圈灵魂柱、
     * 命中回血）全部在 {@link SoulTrident} 里，这里只负责把它<b>生出来并瞄准</b>。
     *
     * <h2>⚠️ 和原版的两处写法差异</h2>
     * <ol>
     *   <li><b>类型从哪来。</b>原版走 {@code new SoulTridentEntity(level, this, new ItemStack(Items.TRIDENT))}
     *       那个三参构造，里面写死了 {@code ModEntities.SOUL_TRIDENT}。
     *       我们用不了那个构造（类型得是自己的），改成「先 new、再逐个 set」——
     *       和 {@link #throwDaggers} 完全同一套路。设的这几项（坐标 / 主人 / 速度）
     *       就是原版那个构造做的事，一项不多一项不少。</li>
     *   <li><b>没有 {@code tridentItem} 那一步。</b>原版三参构造里还会把
     *       {@code new ItemStack(Items.TRIDENT)} 存下来并读它的附魔（忠诚 / 引雷）。
     *       我们这把戟是凭空变出来的、没有附魔，而 {@code SoulTrident} 的字段默认值
     *       本来就是一把白板三叉戟 —— 所以那两步在我们的场景下等于没做，
     *       省掉不影响任何表现。</li>
     * </ol>
     *
     * <h2>瞄准公式逐项说明</h2>
     * <ul>
     *   <li>{@code d0 / d2} 是水平方向到目标的差值，{@code d3} 是它的长度；</li>
     *   <li>{@code d1} 是竖直差，用的是目标「脚往上三分之一身位」的高度
     *       （{@code getY(0.333)}）而不是脚底 —— 这样打的是胸口不是鞋；</li>
     *   <li>竖直方向额外加 {@code d3 * 0.2}，也就是<b>距离越远抬得越高</b>，
     *       补偿抛物线。这是原版的抛射手感，别改成 0；</li>
     *   <li>散布传 {@code 14 - 难度 × 4}：和平难度 14（很飘），困难难度 2（很准）。
     *       注意这个数是<b>散布，不是精度</b> —— 越大越打不准。</li>
     * </ul>
     *
     * @param target   往哪儿扔。调用方已经判过非空（见状态 38 分支）
     * @param velocity 初速。状态 38 传 1.0F，照抄原版
     */
    public void throwSoulTrident(LivingEntity target, float velocity) {
        SoulTrident soulTrident = new SoulTrident(LmEntityRegistry.SOUL_TRIDENT.get(), this.level());

        // 先摆位置再算弹道 —— 顺序不能反。原版那句 d1 读的是 soulTrident.getY()，
        // 而三参构造已经把位置设成了「主人的眼睛高度再往下 0.1」，所以这里也得先设好。
        // 0.1 这个数是 Projectile 里写死的，照抄。
        soulTrident.setPos(this.getX(), this.getEyeY() - 0.1D, this.getZ());
        soulTrident.setOwner(this);

        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3333333333333333D) - soulTrident.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        soulTrident.shoot(d0, d1 + d3 * 0.2D, d2, velocity,
                (float) (14 - this.level().getDifficulty().getId() * 4));
        this.playSound(SoundEvents.DROWNED_SHOOT, 1.0F,
                0.75F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(soulTrident);
    }

    /**
     * 沿一条<b>扇形弧线</b>铺出一排灵魂尖柱冲击波。招 38 第 69~83 tick 每隔 2 tick 调一次。
     *
     * <p>它和 {@link #randomizedSoulStrike} 是一对孪生兄弟，都建立在
     * {@link #spawnSoulPillars} 之上，区别在「落点怎么选」：
     * <ul>
     *   <li>{@code randomizedSoulStrike} 是<b>随机</b>撒 —— 起点随机、方向随机，用来铺满周围；</li>
     *   <li>这个方法是有序的 —— 落点在一条<b>以自己为圆心、朝正前方张开的圆弧</b>上均匀铺开，
     *       而且半径由 {@code distance} 给定。所以八次调用叠起来是一条向外扩散的波纹，
     *       视觉上就是「一圈一圈炸出去」。</li>
     * </ul>
     *
     * <h2>参数含义（名字不好懂，逐个说）</h2>
     * <ul>
     *   <li>{@code spreadarc} —— 圆弧的张角，单位是 <b>π 的倍数</b>（不是度！）。
     *       招 38 传 0.2，即 0.2π ≈ 36 度；</li>
     *   <li>{@code distance} —— 圆心到落点的半径，单位格；</li>
     *   <li>{@code vec} —— 沿「身体左侧」再推出去多少格（招 38 传 1.0，让整条弧稍微侧移）；</li>
     *   <li>{@code delay} —— 每根柱子自己的预热 tick（招 38 传 5，即先预警半秒再炸）；</li>
     *   <li>{@code pos} / {@code offset} —— 弧心相对自己的身体偏移（前 / 侧），招 38 都传 0；</li>
     *   <li>{@code damage} —— ⚠️ <b>这个参数在函数体里从头到尾没被用过。</b>
     *       真正决定伤害的是 {@link #spawnSoulPillars} 里写死的那个 8.0F。
     *       原版就是这个样子（作者留下的死参数），我们照抄保留，
     *       免得以后对着原版代码逐行核对时对不上。</li>
     * </ul>
     *
     * <h2>弧上铺几根？</h2>
     * {@code arcLen = ceil(distance × π × spreadarc)}，也就是说<b>半径越大铺得越多</b>，
     * 保证相邻两根之间始终是差不多一格的距离，不会越往外越稀。
     * 招 38 的 distance 是 2 ~ 16，算出来是 2 ~ 11 根。
     *
     * <p>⚠️ 这里有个极端情况：如果 {@code arcLen} 算出来是 1，下面那句
     * {@code arcLen - 1} 就成了 0，会除出个无穷大。招 38 传的 distance 最小是 2
     * （{@code arcLen = ceil(2 × 0.628) = 2}），碰不到这个坑，所以不额外加保护 ——
     * 加了反而和原版行为不一致。以后想让别的招也调用它的话，
     * <b>先确认 distance 不会小于 2</b>。
     */
    private void flameRadagonShockwave(float spreadarc, int distance, float vec, int delay,
                                       float pos, float offset, float damage) {
        // 下面这四行是「拿身体朝向算前后左右」的标准写法，和 UpdateWithAttack 开头那一段一样。
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180F));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180F));
        double theta1 = (double) this.yBodyRot * (Math.PI / 180D);
        ++theta1;
        double vecX = Math.cos(theta1);
        double vecZ = Math.sin(theta1);

        // 弧心的落点。pos / offset 都为 0 时就是脚下。
        double x = this.getX() + (double) pos * vecX + (double) (f * offset);
        double z = this.getZ() + (double) pos * vecZ + (double) (f1 * offset);

        // 弧线的「中心朝向」= 身体朝向再转 90 度（和别处的 forward 定义一致）。
        double facingAngle = (double) this.yBodyRot * (Math.PI / 180D) + (Math.PI / 2D);
        double spread = Math.PI * (double) spreadarc;
        int arcLen = Mth.ceil((double) distance * spread);

        // 原版在这里还有一句 int floor = Mth.floor(this.getY())，算了之后从没被读过。
        // 死代码，不搬（下面 lowestYCheck 用的是等价但独立算出来的 (int) this.getY() - 1）。
        for (int i = 0; i < arcLen; ++i) {
            // i 从 0 走到 arcLen-1，映射到 -0.5 ~ +0.5 再乘张角 —— 于是弧线以 facingAngle 为中心对称铺开。
            double theta = ((double) i / ((double) arcLen - 1.0D) - 0.5D) * spread + facingAngle;
            double vx = Math.cos(theta);
            double vz = Math.sin(theta);
            // 落点 = 弧心 + 弧上的单位向量 × 半径，再沿身体左侧整体推出 vec 格。
            double px = x + vx * (double) distance
                    + (double) vec * Math.cos((double) (this.yBodyRot + 90.0F) * Math.PI / 180D);
            double pz = z + vz * (double) distance
                    + (double) vec * Math.sin((double) (this.yBodyRot + 90.0F) * Math.PI / 180D);
            // 取整再补 0.5 —— 让柱子落在方块正中心，而不是卡在四个方块的角上。
            // 朝向传的是弧线上那一点的切角 theta（所以每根柱子扭的方向都不一样，看起来像波纹）。
            // lowestYCheck 传「自己脚下一格」：往下扫地面时扫到这儿还没有就放弃
            // （站在悬崖边往外炸时，柱子在虚空那侧会直接不生成，这是有意的）。
            this.spawnSoulPillars((double) Mth.floor(px) + 0.5D, this.getY(),
                    (double) Mth.floor(pz) + 0.5D, (int) this.getY() - 1, (float) theta,
                    delay, this.level(), null);
        }
    }

    // ==================================================================
    //  十八之五、招 37 用到的两个辅助方法
    // ==================================================================

    /**
     * 摆一串「灵魂柱爆炸」（原版 {@code spawnChainedStrike}）。
     *
     * <p>只被招 37 用，两个时刻各调一次：
     * <ul>
     *   <li><b>落地砸（第 265 tick）</b>：{@code (±4, 0)}、{@code (0, ±4)} 四个方向各一串，
     *       每串 {@code reps=1}、{@code amount=5} —— 也就是正东南西北各炸出一排 5 根柱子，
     *       拼成一个十字。</li>
     *   <li><b>双上挑（第 292 tick）</b>：只朝正前方一串，{@code reps=3}、{@code amount=4} ——
     *       一条直线上叠三波、每波 4 根，往外推 12 格。</li>
     * </ul>
     *
     * <h2>它到底把柱子摆在哪</h2>
     * 以圣骑为原点、朝身体正前方，一边往前走 {@code reps} 步、一边在垂直方向上摊开：
     * <ol>
     *   <li>每一步先算一个「方框」中心点 —— 沿朝向走 {@code k × amount × 2 + startVec} 格，
     *       再往侧面挪 {@code startOffset} 格。十字砸法靠 {@code startVec} 分开四个方向
     *       （{@code (±4, 0)} / {@code (0, ±4)}，正好是「前 / 后」和「左 / 右」两对）。</li>
     *   <li>以这个中心点为中心、边长 {@code amount} 划一个正方形，
     *       把它的四个顶点 V1~V4 算出来。</li>
     *   <li>沿着正方形的四条边，每边往外甩一排柱子，间隔 {@code 1.25 × (l+1)} 格。</li>
     *   <li>{@code l % 2 == 0} —— 隔一个取一个，也就是<b>四边同时、每边都只铺一半的柱子</b>。
     *       看着像漏了，其实是原版故意留的疏密节奏。</li>
     * </ol>
     *
     * <h2>⚠️ 三个照抄的死代码，别「顺手」清掉</h2>
     * <ol>
     *   <li>{@code d0 = min(getY(), getY() - 3.0)} —— 算出来从没被读过，
     *       而且不管怎么算结果都是 {@code getY() - 3.0}，看着像是想拿来当「最低扫描高度」的。</li>
     *   <li>{@code rMove} 恒为 0，{@code yaw} 只用来乘它 —— 等于最后 {@code x / z} 就是
     *       {@code rawX / rawZ}。原版大概本来想让方框整体平移，后来改没了。</li>
     *   <li>{@code int loopDelay = k * 5} 让每一波比上一波晚 5 tick 出场，
     *       这个<b>是有效的</b>，别跟上面两个搞混。</li>
     * </ol>
     *
     * @param startVec       沿朝向前后挪多少格（十字砸法用 ±4 分前后，双上挑用 3）
     * @param startOffset    往左右挪多少格（两个调用点都传 0）
     * @param reps           铺几波
     * @param amount         每波方框的边长（同时决定每边几根柱子、往外推多远）
     * @param delay          柱子生成后的预热 tick（柱子先生成、过这么多 tick 才炸）
     * @param centeredStrike 是否在方框中心额外补一根。招 37 两处都传 {@code true}
     */
    public void spawnChainedStrike(float startVec, float startOffset, int reps, int amount, int delay,
                                   boolean centeredStrike) {
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180F));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180F));
        double theta = (double) this.yBodyRot * (Math.PI / 180D);
        ++theta;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        // 柱子往下扫地面时扫到「自己脚下一格」还没有就放弃。
        double lowestYCheck = this.getY() - 1.0D;
        // 死代码，见方法注释第 1 条。
        double unusedD0 = Math.min(this.getY(), this.getY() - 3.0D);

        for (int k = 0; k < reps; ++k) {
            float r = (float) amount;
            // 死代码，见方法注释第 2 条：这俩一起构成一次「没起作用的平移」。
            float rMove = 0.0F;
            float yaw = (float) Math.toRadians(90.0D);
            int squareMove = k * amount * 2;
            double rawX = this.getX() + (double) ((float) squareMove + startVec) * vecX
                    + (double) (f * startOffset);
            double rawZ = this.getZ() + (double) ((float) squareMove + startVec) * vecZ
                    + (double) (f1 * startOffset);
            double x = rawX + (double) rMove * Math.cos((double) yaw);
            double z = rawZ + (double) rMove * Math.sin((double) yaw);
            double y = this.getY();
            // 方框的半边长：让「边长 = amount」成立。所以 amount=4 的方框顶点两两相距 4 格。
            double div = (double) r / Math.sqrt(2.0F);
            Vec3 v1 = new Vec3(x + div, y, z + div);
            Vec3 v2 = new Vec3(x - div, y, z + div);
            Vec3 v3 = new Vec3(x - div, y, z - div);
            Vec3 v4 = new Vec3(x + div, y, z - div);
            // 四条边各自的朝向角：柱子生成时按这个角扭向，看起来像沿着边铺开的波纹。
            float v1v2atan2 = (float) Mth.atan2(v2.z - v1.z, v2.x - v1.x);
            float v2v3atan2 = (float) Mth.atan2(v3.z - v2.z, v3.x - v2.x);
            float v1v4atan2 = (float) Mth.atan2(v4.z - v1.z, v4.x - v1.x);
            float v4v3atan2 = (float) Mth.atan2(v3.z - v4.z, v3.x - v4.x);
            int loopDelay = k * 5;

            for (int l = 0; l < amount; ++l) {
                if (l % 2 == 0) {
                    double d2 = 1.25D * (double) (l + 1);
                    double lowestY = this.getY();
                    this.spawnSoulPillars(v1.x + (double) Mth.cos(v1v2atan2) * d2, lowestY,
                            v1.z + (double) Mth.sin(v1v2atan2) * d2, (int) lowestYCheck, f,
                            delay + loopDelay, this.level(), this);
                    this.spawnSoulPillars(v2.x + (double) Mth.cos(v2v3atan2) * d2, lowestY,
                            v2.z + (double) Mth.sin(v2v3atan2) * d2, (int) lowestYCheck, f,
                            delay + loopDelay, this.level(), this);
                    this.spawnSoulPillars(v1.x + (double) Mth.cos(v1v4atan2) * d2, lowestY,
                            v1.z + (double) Mth.sin(v1v4atan2) * d2, (int) lowestYCheck, f,
                            delay + loopDelay, this.level(), this);
                    this.spawnSoulPillars(v4.x + (double) Mth.cos(v4v3atan2) * d2, lowestY,
                            v4.z + (double) Mth.sin(v4v3atan2) * d2, (int) lowestYCheck, f,
                            delay + loopDelay, this.level(), this);
                }
            }

            if (centeredStrike) {
                // 中心那根额外配一发粒子（只有客户端画，所以加了 isClientSide 判断）。
                if (this.level().isClientSide) {
                    this.level().addParticle(ModParticles.SOUL_EXPLOSION_RED.get(),
                            x, this.getY() + 0.5D, z, 0.0D, 0.0D, 0.0D);
                }
                // ⚠️ 这里传的施法者是 this（不是 null）—— 和 flameRadagonShockwave 不一样。
                //    真相是两根柱子走的是同一个方法，区别只在这个参数：
                //    SoulPillarEntity 拿施法者做友军过滤，传 this 才不会炸到主人自己。
                this.spawnSoulPillars(x, this.getY() - 1.0D, z, (int) lowestYCheck, f,
                        delay, this.level(), this);
            }
        }
    }

    /**
     * 把一团粒子从「随机散布的位置」吸向圣骑身上某个点（原版 {@code attractParticles}，
     * 定义在基类 {@code IAnimatedBoss} 里）。
     *
     * <p>只被招 37 的跳砸蓄力段（第 229~240 tick）用，每 tick 三发，看着就是
     * 红色灵魂粒子从四周被吸进身体里、越聚越亮。
     *
     * <h2>「吸附」是怎么做出来的</h2>
     * 粒子本身没有寻的能力。这里的做法是：<b>生成的那一刻就把它这一辈子的速度定死</b> ——
     * 速度 = (终点 - 起点) × velocity。粒子按这个速度直线飞过去，一帧帧看着就像被吸过去了。
     * 所以 {@code velocity} 越小飞得越慢（也越早消散在半路），传 1.0 才是「精确命中终点」。
     *
     * <h2>⚠️ 两个照抄的点</h2>
     * <ol>
     *   <li><b>起点和终点都是算一次、喂给整组粒子</b>（{@code reps} 个粒子位置完全相同）。
     *       看上去应该是「reps 个粒子各随机一个位置」，但原版循环写在算完之后，
     *       所以实际上是同一位置叠了 {@code reps + 1} 发。想要散开的效果，
     *       得<b>每 tick 调多次</b>（招 37 就是这么干的，一口气调三次、每次都换一个 startY）。</li>
     *   <li>循环是 {@code i <= reps}（闭区间），所以实际发射数是 {@code reps + 1} 发。
     *       照抄，别改成 {@code <}。</li>
     * </ol>
     *
     * @param particleOptions 粒子类型。招 37 传 {@code GHOSTLY_SOUL_RED}
     * @param cap             起点在水平方向上随机偏移多少格（矩形范围，不是半径）
     * @param reps            多发几遍（实际发 {@code reps + 1} 次）
     * @param vec             终点沿身体前方偏移多少格
     * @param offset          终点往左右偏移多少格
     * @param startY          起点的高度（相对自己脚底）
     * @param endY            终点的高度（相对自己脚底）
     * @param velocity        速度倍率，见上面「吸附是怎么做出来的」
     */
    public void attractParticles(ParticleOptions particleOptions, int cap, int reps, float vec, float offset,
                                 float startY, float endY, float velocity) {
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180F));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180F));
        double theta = (double) this.yBodyRot * (Math.PI / 180D);
        ++theta;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        int rX = this.random1.nextInt(-cap, cap);
        int rZ = this.random1.nextInt(-cap, cap);
        // 再抖一点点高度，免得同一 tick 的几发粒子完全重叠。
        float f2 = (this.random.nextFloat() - 0.0F) * 0.5F;
        double d1 = this.getX() + (double) rX;
        double d2 = this.getY() + (double) startY + (double) f2;
        double d3 = this.getZ() + (double) rZ;
        Vec3 from = new Vec3(d1, d2, d3);
        Vec3 to = new Vec3(this.getX() + (double) vec * vecX + (double) (f * offset),
                this.position().y + (double) endY,
                this.getZ() + (double) vec * vecZ + (double) (f1 * offset));
        Vec3 v = to.subtract(from).scale((double) velocity);

        for (int i = 0; i <= reps; ++i) {
            if (this.level().isClientSide) {
                this.level().addParticle(particleOptions, d1, d2, d3, v.x, v.y, v.z);
            }
        }
    }

    // ==================================================================
    //  十九、防御特性（免火 / 受击音效 / 限伤配置 / 伤害适应）
    // ==================================================================

    /**
     * 免火。原版 {@code PossessedPaladinEntity.fireImmune()} 就是直接返回 true。
     *
     * <p>⚠️ 这个方法<b>不能</b>和 {@code IAnimatedBossServant.hurt()} 里那条
     * 「火焰伤害直接免掉」互相替代 —— 两者挡的是不同的东西：
     * <ul>
     *   <li>{@code fireImmune()} 由 {@code LivingEntity.hurt} 读取，挡「火焰伤害」，
     *       同时让圣骑<b>不会着火</b>（{@code lavaHurt()}、{@code setSecondsOnFire()} 都会先问它）；</li>
     *   <li>{@code hurt()} 里那条挡的是绕过常规流程直接打进来的火焰伤害。</li>
     * </ul>
     * 两道都留着，这才是原版的结构，也才是真正意义上的「免火」。
     */
    @Override
    public boolean fireImmune() {
        return true;
    }

    /**
     * 受击音效。原版用的是「活体盔甲」挨打的声音 —— 圣骑本体就是一具空壳盔甲，
     * 打上去是金属闷响，而不是血肉声。
     *
     * <p>不覆写的话会走原版默认的 {@code SoundEvents.GENERIC_HURT}，
     * 听起来和打一只僵尸没区别，盔甲感全没了。
     */
    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.LIVING_ARMOR_HURT.get();
    }

    /**
     * 单次伤害上限，接到配置项上。
     *
     * <p>⚠️ 这一条曾经是<b>漏的</b>：配置项
     * {@code AttributesConfig.PossessedPaladinServantDamageCap} 早就加好了，
     * 但实体没覆写本方法，于是圣骑一直用着基类默认的 {@code 21.0D} ——
     * 配置项改多少都没反应。原版对应的是 {@code PossessedPaladinDamageCap}。
     */
    @Override
    public double damageCap() {
        return AttributesConfig.PossessedPaladinServantDamageCap.get();
    }

    /**
     * 伤害适应的推进速度：每挨一下推进 {@code 20}（基类默认 10）。
     *
     * <p>这是<b>圣骑专属的覆写</b>，和原版一致（{@code PossessedPaladinEntity.adaptationFactor()}
     * 返回 20）。效果是 5 下就打到最深的减伤，普通 Boss 要 10 下。
     * 减伤本身的逻辑写在基类 {@code IAnimatedBossServant.hurt()} 里。
     */
    @Override
    public int adaptationFactor() {
        return 20;
    }

    /**
     * 主人处决。Goety {@code Summoned.tryKill(Player)} 的覆写。
     *
     * <p><b>怎么触发</b>：玩家<b>蹲下 + 拿法杖右键自己的仆从</b>，Goety 就会调到这里。
     *
     * <p><b>为什么要覆写</b>，看基类的原文（反编译自 {@code goety-2.5.56.0}）：
     * <pre>
     * public void warnKill(Player player) {
     *     this.killChance = 60;
     *     player.displayClientMessage(
     *         Component.translatable("info.goety.servant.tryKill", this.getDisplayName()), true);
     * }
     *
     * public void tryKill(Player player) {
     *     this.hurt(ModDamageSource.getDamageSource(this.level, DISMISSED), Float.MAX_VALUE);
     * }
     * </pre>
     * 基类的 {@code tryKill} <b>一上来就杀</b>，而 Goety 的设计是「先警告再杀」两步走，
     * 这个两步走是靠子类覆写 {@code tryKill} + 读 {@code killChance} 实现的 ——
     * 也就是下面这段。本项目里所有可处决的仆从都是同一个写法
     * （{@code MutantHoglinServant}、{@code AtlatitanServant} 等十来处），照抄即可：
     * <pre>
     * if (this.killChance &lt;= 0) {
     *     this.warnKill(player);   // 第一次：提示「再点一次就没了」，并把 killChance 顶到 60
     * } else {
     *     super.tryKill(player);   // 第二次：真的动手
     * }
     * </pre>
     *
     * <p><b>本类比通用写法多做的事</b>：动手之前把 {@link #executedByOwner} 置上。
     * 因为基类的 {@code tryKill} 最后落到的还是 {@link #hurt}，
     * 而圣骑的 {@code hurt} 有整整四层免伤（格挡 / 无敌帧 / 变身无敌）——
     * 不放行的话，主人处决自己的仆从会被它举盾挡下来。详见 {@link #hurt} 的 ⓪ 层。
     *
     * <p>⚠️ 清标记的写法有讲究：<b>要判「还活着」才清，不能无条件清</b>。
     * 因为 {@code die()} 是在 {@code super.tryKill()} <b>内部</b>被调到的 ——
     * 那时候实体已经死了，标记留着还是清掉都无所谓（{@code tickDeath()} 不再读它，
     * 两种死法的表现也完全一致）。
     *
     * <p>反过来，如果这一下没打死（理论上不会，但万一被别的 mod 拦了一手），
     * 实体还活着，就得把标记撤掉 —— 否则它身上会一直挂着一张
     * 「四层免伤全部失效」的通行证。
     */
    @Override
    public void tryKill(Player player) {
        if (this.killChance <= 0) {
            this.warnKill(player);
        } else {
            this.executedByOwner = true;
            super.tryKill(player);
            if (this.isAlive()) {
                this.executedByOwner = false;
            }
        }
    }

    /**
     * 受击总入口。原版 {@code PossessedPaladinEntity.hurt()}（PossessedPaladinEntity.java:2794）。
     *
     * <p>这是圣骑「打不死」的全部秘密所在。它把一次挨打分成了<b>四层</b>，从上往下依次过筛：
     *
     * <pre>
     * ⓪ 主人处决                → <b>全部放行</b>，直接交给 super.hurt()（见下）
     * ① 状态 26（二阶段变身中）  → 完全免伤，直接返回 false
     * ② 无敌帧还没用完          → 完全免伤，直接返回 false
     * ③ 盾还举着（状态 5 前段） → 完全免伤 + 金属撞击声 + 震屏，直接返回 false
     * ④ 状态空闲 &amp; 冷却好了 &amp; 伤害 &gt; 1
     *                           → <b>格挡成功</b>：切状态 5 开始举盾反击，这一下同样免伤
     * ⑤ 以上都不满足            → 老老实实挨打，挨完给 10 tick 无敌帧
     * </pre>
     *
     * <p>⚠️ <b>四层里只有第 ⑤ 层真的掉血。</b>前三层全是「把这次攻击吞掉」。
     * 这就是为什么拿木剑连点圣骑会感觉完全打不动 —— 每一次连点都在被 ②③④ 轮着挡。
     *
     * <h2>⓪ 为什么处决必须单开一条路</h2>
     * 处决走的是 Goety 的 {@code Summoned.tryKill()}，它内部只有一句：
     * <pre>
     * this.hurt(ModDamageSource.getDamageSource(level, DISMISSED), Float.MAX_VALUE);
     * </pre>
     * 也就是说处决<b>本来就是一次普通的 hurt</b>。<b>而 {@code Float.MAX_VALUE} 大于 1.0F，
     * 必然满足第 ④ 层的 {@code amount > 1.0F}</b> —— 不特判的话，主人处决自己的仆从
     * 会被仆从举盾挡下来，而且还会反过来被扫一盾牌。这显然不是主人想要的结果。
     *
     * <p>所以这里用 {@link #executedByOwner} 当通行证：置上时四层全跳，
     * 直接进 {@code super.hurt()}。
     *
     * <p>好消息是 {@code super.hurt()} 那一侧不用再操心：{@code goety:dismissed}
     * 这个伤害类型<b>在 {@code minecraft:bypasses_invulnerability} 标签里</b>
     * （Goety 自己的 {@code data/minecraft/tags/damage_type/bypasses_invulnerability.json}
     * 就是为它写的），所以 {@code IAnimatedBossServant.hurt()} 会第一句就放行，
     * <b>动态减伤和 {@code damageCap()} 限伤都不会生效</b>，{@code Float.MAX_VALUE} 原样打到血量归零。
     *
     * <h2>为什么 {@code super.hurt()} 不能省</h2>
     * 本方法覆写的链条是 {@code PossessedPaladinServant → IAnimatedBossServant →
     * IAnimatedMonsterServant → LivingEntity}。第 ⑤ 层的 {@code super.hurt()} 会依次经过：
     * <ul>
     *   <li>{@code IAnimatedMonsterServant.hurt()} 的「骑在别人身上时免伤」；</li>
     *   <li>{@code IAnimatedBossServant.hurt()} 的<b>免摔落 / 免溺水 / 免火焰 / 免冰冻</b>
     *       和<b>动态减伤</b>（短时间内连续挨打，第二下开始打折）。</li>
     * </ul>
     * 直接抄 {@code LivingEntity.hurt()} 的逻辑就等于把上面这些全丢了。
     *
     * <h2>⚠️ 和原版的两处主动差异</h2>
     * 原版最外层还包着一条 {@code !isTargetCheesing(-4.0F, 4.0F) && ... && !isSleep()}，
     * 这里<b>两条都没搬</b>：
     * <ol>
     *   <li>{@code isTargetCheesing(-4, 4)} 是「目标比自己高/低超过 4 格就完全免伤」的防偷鸡判据。
     *       它是给「玩家站在高台上放风筝打 Boss」设计的，搬到<b>玩家的仆从</b>身上会变成
     *       「敌人只要站高一点，圣骑就彻底无敌」的漏洞。和基类里那条 15 格距离保护同理
     *       （见 {@code IAnimatedBossServant} 的类注释），一并去掉。</li>
     *   <li>{@code isSleep()} 判的是状态 34 / 35（沉睡 / 苏醒），而这两个状态在仆从身上
     *       <b>永远不会进入</b>（原因见 {@code registerGoals} 里那段说明），
     *       留着也只会是一个恒为 false 的常量。</li>
     * </ol>
     * 其余每一层都逐行对齐原版。
     */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        // ⓪ 主人处决。见上面 javadoc：处决本身就是一次 amount = Float.MAX_VALUE 的 hurt，
        //    不在这里放行的话会被第 ④ 层当成「重击」格挡掉，仆从就永远处决不掉了。
        if (this.executedByOwner) {
            return super.hurt(source, amount);
        }

        // ① 二阶段变身演出中。变身那几秒被打死就没法演了，所以全程无敌。
        //    状态 26 还没实现（在 2D），但这一条是「免伤」而不是「表现」，
        //    现在留着不影响任何东西，等 2D 接上就自动生效。
        if (this.getAttackState() == 26) {
            return false;
        }

        // ② 无敌帧。注意配置开关是「与」进来的：关掉开关，圣骑就完全没有无敌帧。
        if (this.BossInvulnerabilityTime > 0
                && ModConfig.MOB_CONFIG.PossessedPaladinInvulnerabilityTime.get()) {
            return false;
        }

        // ③ 盾还举在身前。这一层连无敌帧都不给 —— 下一 tick 再打过来还能接着挡。
        if (this.isBlockin()) {
            this.playSound(ModSounds.BLOCK.get(), 1.0F, 1.0F);
            CameraShakeEntity.cameraShake(this.level(), this.position(), 10.0F, 0.15F, 5, 5);
            return false;
        }

        // ④ 格挡成功。三个条件：正闲着、冷却好了、这一下够重（&lt;=1 的刮痧不挡，省得被小鸡啄一下就举盾）。
        //    ⚠️ isClientSide 的判断必须有：setAttackState 是网络同步的状态切换，
        //    只有服务端说了算，客户端自作主张切状态会立刻被服务端的同步数据打回去。
        if (!this.level().isClientSide && this.canParry() && amount > 1.0F) {
            this.hasParried = true;
            this.setAttackState(5);
            // 把手上正在播的动画全砍掉，好让格挡动画从第 0 帧干净地开始。
            this.stopAllAnimationStates();
            this.playSound(ModSounds.BLOCK.get(), 1.0F, 1.0F);
            CameraShakeEntity.cameraShake(this.level(), this.position(), 10.0F, 0.15F, 5, 5);

            // 只有「无视无敌的伤害」（/kill、虚空这类）被挡下来时才喊这句话 ——
            // 挡下普通攻击是家常便饭，不值得到处刷屏。
            if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                // 二阶段喊红字，一阶段喊青字 —— 和原版一致。
                this.sendAdvancedHotBarMessage("legendary_monsters.message.possessed_paladin_kill_parry",
                        this.getPhase() >= 2 ? ChatFormatting.RED : ChatFormatting.AQUA, 10.0F);
            }
            return false;
        }

        // ⑤ 真的挨打。
        boolean hurt1 = super.hurt(source, amount);
        // 挨打成功才给无敌帧。被上面四层挡掉的都不算 —— 否则格挡一次能白送 10 tick 无敌，太强了。
        // BossInvulnerabilityTime <= 0 这一条看着多余（②已经保证走到这里时它是 0），
        // 但原版就是这么写的，照抄以免哪天 ② 改了这里跟着出问题。
        if (hurt1 && !this.level().isClientSide
                && ModConfig.MOB_CONFIG.PossessedPaladinInvulnerabilityTime.get()
                && this.BossInvulnerabilityTime <= 0) {
            this.BossInvulnerabilityTime = BOSS_INVULNERABILITY_TICKS;
        }
        return hurt1;
    }
}
