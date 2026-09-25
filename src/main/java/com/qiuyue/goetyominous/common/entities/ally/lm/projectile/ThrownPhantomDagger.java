package com.qiuyue.goetyominous.common.entities.ally.lm.projectile;

import com.qiuyue.goetyominous.client.particle.lm.PhantomDaggerTrail;
import com.qiuyue.goetyominous.common.entities.ally.lm.ControlledAnim;
import com.qiuyue.goetyominous.common.entities.ally.lm.ServantMath;
import com.qiuyue.goetyominous.common.init.lm.LmDamageTypes;
import net.miauczel.legendary_monsters.effect.ModEffects;
import net.miauczel.legendary_monsters.util.EntityUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * 堕落圣骑仆从的「幻影匕首」弹射物（对应原版 {@code ThrownPhantomDaggerEntity}）。
 *
 * <h2>它是一把「回旋镖」，不是普通的直线飞行物</h2>
 * 飞行轨迹由基类 {@link ServantFlyingProjectile} 负责，本类只管三件事：
 * <ol>
 *   <li><b>拖尾粒子</b> —— 每 tick 在身后撒一颗 {@code PhantomDaggerTrail}（青色；二阶段变红）；</li>
 *   <li><b>回收</b> —— 飞过 {@code returnTick} 之后打开穿墙、转向飞回主人身上，
 *       最后在 {@code 100 - lessLifeTicks + returnTick} 那一刻 {@code discard()} 掉；</li>
 *   <li><b>命中处理</b> —— 见下面 {@link #onHitEntity}。</li>
 * </ol>
 *
 * <h2>飞行逻辑是哪来的</h2>
 * 来自 {@link ServantFlyingProjectile} —— 我们自己抄的一份，
 * 源头是原作那个「通篇只用到原版类」的飞行基类
 * （见那个类的注释，里面写清了它和原版的 3 处差异）。
 * 继承过来 = 飞行、惯性、入水减速这些行为零改动的和原作一致。
 * 自己重写一遍反而容易在某个系数上走样。
 *
 * <p>这和 {@code PoisonousShockwave} 的处理方式不同 —— 那边是「抄一份改掉内容」
 * （因为要让蔓生巨像的冲击波走 Goety 的毒素而不是原版的），这边是「原样继承」
 * （因为匕首的飞行行为我们一点都不想改）。选择依据是<b>要不要改行为</b>，不是哪个更省事。
 *
 * <h2>✅ 匕首自己的东西，全都是本项目自己的了</h2>
 * 飞行基类、伤害类型、数学换算、动画计时器、拖尾粒子
 * （{@link com.qiuyue.goetyominous.client.particle.lm.PhantomDaggerTrail}，
 * 贴图和粒子行为原样照搬，只是命名空间换成了 {@code goetyominous}）——
 * 这些「只属于这发弹射物」的东西，一个 LM 的都不剩。
 *
 * <h2>⏳ 还剩两个 import —— 这两个【故意】保留</h2>
 * <ul>
 *   <li>{@code ModEffects.SOUL_FRACTURE} —— 命中后叠的「灵魂碎裂」效果；</li>
 *   <li>{@code EntityUtil} —— 只是上面那个效果的「叠层」工具方法。</li>
 * </ul>
 * <b>为什么不动它们</b>：「灵魂碎裂」不是匕首独有的东西 ——
 * 圣骑本体（剑砍中时，3 处）也在叠同一个，三叉戟（{@code SoulTrident}）
 * 继承的那份 LM 原版类里也写死了它。我们要是自己另注册一个同名的，
 * 同一个目标身上就会冒出<b>两个一模一样的图标</b>、减血上限还会叠两次
 * （0.8 × 0.8 = 0.64，而不是 0.8）。
 *
 * <p><b>结论：LM 是本模组的硬依赖，它的公开 API 直接用就好。</b>
 * 「去 LM 化」只做弹射物自己的东西，别顺手把公用的效果也拆成两份。
 * 详见错题本第 4 条 / 第 27 条。
 *
 * <h2>⚠️ 与原版的两处有意偏差</h2>
 * <ol>
 *   <li><b>友军判定换掉了。</b>原版查的是传奇怪物的 {@code POSSESSED_ARMOR_TEAM} 标签
 *       （「圣骑和它的小弟之间不互打」）。我们是仆从，主人是玩家、伙伴是别的仆从，
 *       那个标签一个都不沾 —— 照抄的话匕首会直接扎在主人脸上。
 *       改成 {@code livingOwner.isAlliedTo(target)}，和圣骑那 14 招用的是同一套判定。
 *       详见 {@link #onHitEntity}。</li>
 *   <li><b>伤害不乘配置倍率。</b>原版 {@code getDamage()} 在「主人是玩家」时会乘上传奇怪物的
 *       {@code SoulGreatSwordAbilityDamageMultiplier} —— 那是给「灵魂巨剑」那把武器用的配置项。
 *       我们的匕首主人永远是圣骑仆从（不是玩家），原版那个三元本来就永远走 else 分支，
 *       所以这里直接取原始值，连配置项都不用读。</li>
 * </ol>
 *
 * <p>另外原版还跳过了 {@code FracturedApostleEntity}（传奇怪物的另一个 Boss）的叠加效果 ——
 * 那个类不在我们的世界里，删掉。
 */
public class ThrownPhantomDagger extends ServantFlyingProjectile {

    private static final EntityDataAccessor<Integer> RETURN_TICK =
            SynchedEntityData.defineId(ThrownPhantomDagger.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DAMAGE =
            SynchedEntityData.defineId(ThrownPhantomDagger.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> IS_RED =
            SynchedEntityData.defineId(ThrownPhantomDagger.class, EntityDataSerializers.BOOLEAN);

    /** 飞回主人身边时用到的客户端计数器，原版只增不减、也不被读取，保留是为了对齐。 */
    public int clientSideReturnTridentTickCount;

    /**
     * 飞出去之后要回到谁身上。
     *
     * <p>注意它是<b>独立于 {@code getOwner()} 的另一个字段</b>，不是同一个东西：
     * owner 决定「这发匕首算谁打的」（决定伤害归属和友军过滤），
     * 这个字段只决定「回程往谁身上飞」。这里两者都是圣骑，但概念上别混。
     *
     * <p>字段名沿用原版的 {@code rEntity} —— 因为它和下面那个
     * {@link #returnEntity()} 方法同名会很容易看错，原版正是靠缩写避开这件事的。
     */
    private LivingEntity rEntity;

    /**
     * 惯性（每 tick 速度乘数）。
     *
     * <p>注意<b>不是</b>基类的 {@code getInertia()} 返回值 —— 本类覆写了那个方法，
     * 让它读这个字段。默认 1.0 表示「不减速」，比基类的 0.95 更飘。
     */
    private float interia = 1.0F;

    /** 二阶段的红色配色（RGB 分量，0~255）。青色那组写在 {@link #tick()} 里。 */
    public float uR = 195.0F;
    public float uG = 24.0F;
    public float uB = 30.0F;

    /** 让匕首提前消失用的偏移量，原版给「灵魂巨剑」那套武器留的，我们这条路径不用，保留对齐。 */
    public int lessLifeTicks = 0;

    /** 消失淡出的进度条（0 → 6 tick）。渲染器拿它算透明度。 */
    public ControlledAnim fade = new ControlledAnim(6);

    public ThrownPhantomDagger(EntityType<? extends ThrownPhantomDagger> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(RETURN_TICK, 20);
        this.entityData.define(IS_RED, false);
        this.entityData.define(DAMAGE, 0.0F);
    }

    // ------------------------------------------------------------------
    //  数据同步
    // ------------------------------------------------------------------

    public boolean getRed() {
        return this.entityData.get(IS_RED);
    }

    public void setRed(boolean red) {
        this.entityData.set(IS_RED, red);
    }

    public float getReturnTick() {
        return (float) (int) this.entityData.get(RETURN_TICK);
    }

    public void setReturnTick(int returnTick) {
        this.entityData.set(RETURN_TICK, returnTick);
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    public void setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
    }

    public int getLessLifeTicks() {
        return this.lessLifeTicks;
    }

    public void setLessLifeTicks(int lessLifeTicks) {
        this.lessLifeTicks = lessLifeTicks;
    }

    public LivingEntity returnEntity() {
        return this.rEntity;
    }

    public void setReturnEntity(LivingEntity returnEntity) {
        if (returnEntity != null) {
            this.rEntity = returnEntity;
        }
    }

    /** 见类注释「有意偏差」第 2 条：原版那个玩家配置倍率在这里是死分支，直接去掉。 */
    @Override
    protected float getInertia() {
        return this.interia;
    }

    public void setInteria(float interia) {
        this.interia = interia;
    }

    /** 原版覆写成 false —— 匕首不能被玩家用碰撞箱「捡」起来，也不能被箭打掉。 */
    @Override
    public boolean isPickable() {
        return false;
    }

    // ------------------------------------------------------------------
    //  每 tick
    // ------------------------------------------------------------------

    @Override
    public void tick() {
        super.tick();

        // 总寿命 = (100 - lessLifeTicks) + returnTick。默认就是 100 + 20 = 120 tick。
        // 注意 returnTick 是「飞到第几 tick 开始往回飞」，不是「活多久」——
        // 匕首回程也要时间，所以总寿命是「飞出去的那段时间」加上「回来的那段时间」。
        double finalTick = (double) ((float) (100 - this.getLessLifeTicks()) + this.getReturnTick());

        // 最后 6 tick 开始淡出，渲染器据此压透明度。
        if ((double) this.tickCount >= finalTick - 6.0D) {
            this.fade.increaseTimer();
        }

        // 到点就消失。只在服务端 discard，客户端等同步包。
        if ((double) this.tickCount >= finalTick && !this.level().isClientSide) {
            this.discard();
        }

        // ---- 拖尾粒子：在自身附近随机抖 1.5 格撒点 ----
        double dx = this.getX() + (double) (1.5F * (this.random.nextFloat() - 0.5F));
        double dy = this.getY() + (double) (1.5F * (this.random.nextFloat() - 0.5F));
        double dz = this.getZ() + (double) (1.5F * (this.random.nextFloat() - 0.5F));
        // 青色 = 57/190/197，红色（二阶段）= 195/24/30。除以 255 转成 0~1 的浮点色。
        float r = (this.getRed() ? this.uR : 57.0F) / 255.0F;
        float g = (this.getRed() ? this.uG : 190.0F) / 255.0F;
        float b = (this.getRed() ? this.uB : 197.0F) / 255.0F;
        if (this.level().isClientSide) {
            // 第五个参数是「轨道半径高度 0.25」，最后那个 getId() 让粒子每 tick 跟着匕首跑
            // ——见 PhantomDaggerTrail.getOrbitPosition()。
            this.level().addParticle(new PhantomDaggerTrail.OrbData(r, g, b, 0.0F, 0.25F, this.getId()),
                    dx, dy, dz, 0.0D, 0.0D, 0.0D);
        }

        // ---- 回收阶段：飞过 returnTick 之后调头追主人 ----
        if ((float) this.tickCount >= this.getReturnTick() && this.returnEntity() != null) {
            // 打开穿墙，免得回来路上撞在地形上卡住。
            // ⚠️ noPhysics 在原版 Entity 里是个 <b>public 字段</b>，没有 setNoPhysics() 这个方法。
            // 原版是自己写了个同名包装方法转发到这个字段；我们只在这一处用到，就直接赋值。
            this.noPhysics = true;
            // 目标是「主人脚下往上 1 格」。
            Vec3 returnPos = new Vec3(this.returnEntity().getX(),
                    this.returnEntity().getY() + 1.0D, this.returnEntity().getZ());
            Vec3 vec3 = returnPos.subtract(this.position());

            // 竖直方向上直接「贴」过去一点，让回程有个上扬的手感。
            this.setPosRaw(this.getX(), this.getY() + vec3.y * 0.015D * (double) this.interia, this.getZ());
            if (this.level().isClientSide) {
                // 位置是硬改的，得手动把上一帧坐标对齐，否则渲染插值会拉出一道残影。
                this.yOld = this.getY();
            }

            // 水平回程：先保持 95% 的现有速度，再加上一个朝主人的小加速度。
            double d0 = 0.05D * (double) this.interia;
            this.setDeltaMovement(this.getDeltaMovement().scale(0.95D)
                    .add(vec3.normalize().scale(d0)));
            ++this.clientSideReturnTridentTickCount;
        }
    }

    // ------------------------------------------------------------------
    //  命中
    // ------------------------------------------------------------------

    /**
     * 打中生物时。
     *
     * <p><b>友军过滤是这里唯一改过原版的地方</b>，见类注释。流程：
     * <ol>
     *   <li>没有主人、或者打中的就是主人本人 → 直接返回；</li>
     *   <li>{@code livingOwner.isAlliedTo(target)} 为真 → 返回。
     *       这一条同时挡住了主人、同主人的其他仆从、以及主人同队伍的玩家；</li>
     *   <li>造成伤害。伤害 = 匕首自带伤害 + 目标<b>最大生命值</b>的一个百分比
     *       （{@code MathUtils.toPercent} 换算，所以它是「越肉的单位挨得越疼」）；</li>
     *   <li>打中了就再叠一层「灵魂碎裂」效果，并给主人回 3 点血。
     *       <b>回血这件事是匕首在整套招式里唯一的续航手段</b>，别删。</li>
     * </ol>
     */
    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        Entity owner = this.getOwner();

        if (owner == null || target == owner) {
            return;
        }
        if (!(owner instanceof LivingEntity livingOwner) || !(target instanceof LivingEntity livingTarget)) {
            return;
        }

        // ← 这一行就是「有意偏差」第 1 条。原版在这里查的是传奇怪物的阵营标签。
        if (livingOwner.isAlliedTo(livingTarget)) {
            return;
        }

        // 主人是玩家时原版不加成；我们的主人永远是圣骑仆从，所以恒加上这个百分比。
        float m = ServantMath.toPercent(livingTarget.getMaxHealth());
        boolean hurt = livingTarget.hurt(LmDamageTypes.ghostly(livingOwner),
                this.getDamage() + m);

        if (hurt) {
            // 参数含义见 EntityUtil：效果、层数、等级、持续 tick。10 秒。
            //
            // ⚠️ 这个效果【故意】用传奇怪物原版的，我们不自己造一份。
            //    因为圣骑本体（剑砍中时，3 处）用的也是它，而且三叉戟那边
            //    （继承自传奇怪物自己的类）里面也写死了它。
            //    自己再注册一个同名效果的话，同一个目标身上会冒出两个一模一样的
            //    图标、减血上限还会叠两次（0.8 × 0.8 = 0.64，而不是 0.8）。
            //
            //    教训：LM 是本模组的**硬依赖**，它的公开 API 直接用就好。
            //    「去 LM 化」只做弹射物自己的东西（飞行、伤害、粒子），
            //    别顺手把公用的效果也拆成两份。详见错题本第 4 条 / 第 27 条。
            EntityUtil.applyStackingEffect(livingTarget, ModEffects.SOUL_FRACTURE.get(),
                    1, 4, ServantMath.toTicks(10.0F));
            livingOwner.heal(3.0F);
        }
    }
}
