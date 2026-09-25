package com.qiuyue.goetyominous.common.entities.ally.lm.projectile;

import net.miauczel.legendary_monsters.Particle.ModParticles;
import net.miauczel.legendary_monsters.damagetype.ModDamageTypes;
import net.miauczel.legendary_monsters.util.MathUtils;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

/**
 * 堕落圣骑仆从的「灵魂冲击」弹射物（对应原版
 * {@code net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.SoulStrike}）。
 *
 * <p>它是圣骑二阶段的招牌：以自身为圆心，<b>一口气朝 360 度撒出一整圈</b>灵魂弹，
 * 每颗 12 点伤害，飞出去 1 秒后自己消失。用在两个地方：
 * <ul>
 *   <li>二阶段变身演出的收尾（状态 26 第 82 tick）—— 变身完成时炸一圈，把周围清场；</li>
 *   <li>状态 31 第 53 tick —— 二阶段版「砸地第二下」的最后一下。</li>
 * </ul>
 * 调用点见 {@code PossessedPaladinServant.soulStrikeRing(int, int, float)}。
 *
 * <h2>⚠️ 它没有模型、没有贴图</h2>
 * 这一点很容易让人以为移植漏了东西，其实<b>原版就是这样</b>：传奇怪物给 soul_strike
 * 注册的渲染器是个空壳（`render()` 方法体是空的，`getTextureLocation()` 直接返回 null），
 * 连模型类都没有。玩家看到的「弹」完全是粒子堆出来的 ——
 * {@code GHOSTLY_SOUL}（灵魂蓝）或 {@code GHOSTLY_SOUL_RED}（二阶段红），
 * 外加 {@code SOUL_FIRE_FLAME} / {@code RED_SOUL_FLAME} 的火苗。
 *
 * <p>所以我们的渲染注册也用 {@code EmptyRenderer}，和原版行为一致，不用怀疑。
 *
 * <h2>继承的是原版的 vanilla 基类，不是传奇怪物的</h2>
 * 匕首 {@link ThrownPhantomDagger} 继承的是传奇怪物自己的 {@code AbstractFlyingProjectile}，
 * 因为它需要那套飞行 / 回旋镖逻辑。灵魂冲击<b>不需要</b> —— 它的运动就是
 * 「直线飞 + 一点点重力」，本身已经继承 {@code ThrowableProjectile} 了，
 * 而且 {@link #tick()} 整个覆写掉，基类的飞行逻辑基本没参与。
 *
 * <h2>和原版的两处有意偏差</h2>
 * <ol>
 *   <li><b>主人为空时的空指针保护。</b>原版的回血那一步写的是
 *       {@code livingOwner.heal(8.0F)}，而 {@code livingOwner} 在「主人不是生物」时是 null
 *       —— 原版靠前面那句强制转型先崩，等于是默认「主人一定是生物」。
 *       我们这边主人永远是圣骑，条件恒成立，但空判断留着不花钱，见 {@link #onUpdateInAir()}。</li>
 *   <li><b>删掉三处死代码。</b>原版读了一个 {@code BlockState} 却从没用过；
 *       粒子循环里算了 {@code motx/moty/motz} 三个速度分量也从没用过；
 *       内层还套了一个和外层一模一样的 {@code isClientSide} 判断。
 *       这三处都是原作者留下的，删掉不影响任何表现。</li>
 * </ol>
 *
 * <h2>✅ 和匕首不同，这个的友军过滤原版就是对的</h2>
 * 原版的伤害判定里写着 {@code !this.getOwner().isAlliedTo(livingentity)} ——
 * 而我们的圣骑仆从正好覆写了 {@code isAlliedTo}（主人、同主人的其他仆从、主人的队友
 * 全部算自己人）。所以这一句<b>不用改</b>，直接就能正确避开主人。
 * 对比一下：匕首那边原版查的是传奇怪物的阵营标签，必须换掉才行。
 */
public class SoulStrike extends ThrowableProjectile {

    /** 寿命，单位 tick。20 tick = 1 秒，到点无论飞到哪里都自己消失。 */
    private int lifeTime = 20;

    private static final EntityDataAccessor<Float> DAMAGE =
            SynchedEntityData.defineId(SoulStrike.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> IS_RED =
            SynchedEntityData.defineId(SoulStrike.class, EntityDataSerializers.BOOLEAN);

    /** 原版留的字段，我们这边没有任何地方读它（见 {@link #particleOptions()}）。 */
    public ParticleOptions soulParticle = ModParticles.GHOSTLY_SOUL.get();

    public SoulStrike(EntityType<? extends SoulStrike> entityType, Level level) {
        super(entityType, level);
    }

    // 原版还有两个额外构造函数 —— 「在固定坐标生成」和「由某个生物发射」。
    // 我们把这两个都去掉了，换成调用处直接 new + 逐个 set（和幻影匕首 ThrownPhantomDagger 同一套路）：
    // 这样实体类不用反过来引用 LmEntityRegistry，依赖方向是干净的
    // 「调用方 → 注册表 → 实体」，而不是三者互相纠缠。
    // 这两个构造函数原版也只有圣骑在用，去掉不丢任何能力。

    // ------------------------------------------------------------------
    //  数据同步
    // ------------------------------------------------------------------

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DAMAGE, 0.0F);
        this.entityData.define(IS_RED, false);
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    public void setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
    }

    /** 二阶段配色开关。true = 全红（GHOSTLY_SOUL_RED + RED_SOUL_FLAME）。 */
    public boolean getRed() {
        return this.entityData.get(IS_RED);
    }

    public void setRed(boolean red) {
        this.entityData.set(IS_RED, red);
    }

    /** 见类注释「有意偏差」第 1 条：这个返回值我们自己也没用，保留是为了和原版对齐。 */
    public ParticleOptions particleOptions() {
        return this.soulParticle;
    }

    // ------------------------------------------------------------------
    //  发射
    // ------------------------------------------------------------------

    /**
     * 覆写原版的 {@code Projectile.shoot}。
     *
     * <p>和原版的差别只有一处：<b>散布系数</b>。原版是
     * {@code 0.0172275}（约 1/58），这里写的是 {@code 0.0075} ——
     * 也就是「打得更准，飞得更直」。灵魂冲击是撒一圈的弹幕，作者显然希望
     * 每颗都规规矩矩走直线，而不是随机飘。
     *
     * <p>圣骑调用时 {@code inaccuracy} 传的是 0，所以这个系数实际不起作用 ——
     * 但它是原作者刻意的改动，留着。
     */
    @Override
    public void shoot(double pX, double pY, double pZ, float pVelocity, float pInaccuracy) {
        Vec3 vec3 = (new Vec3(pX, pY, pZ)).normalize()
                .add(this.random.nextGaussian() * 0.0075D * (double) pInaccuracy,
                        this.random.nextGaussian() * 0.0075D * (double) pInaccuracy,
                        this.random.nextGaussian() * 0.0075D * (double) pInaccuracy)
                .scale((double) pVelocity);
        this.setDeltaMovement(vec3);
        double d0 = vec3.horizontalDistance();
        this.setYRot((float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI)));
        this.setXRot((float) (Mth.atan2(vec3.y, d0) * (double) (180F / (float) Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    /**
     * 覆写原版的 {@code Projectile.shootFromRotation}。
     *
     * <p>和原版的差别同样只有一处：原版竖直分量是
     * {@code -sin(pitch)}（真的按俯仰角算），这里<b>写死成 -1.0</b>。
     * 效果是「不管仰望还是俯视，都朝正上方偏一点再往前飞」——
     * 圣骑撒这一圈时不需要瞄准，这个写死值正好给弹幕一个统一的上扬弧度。
     */
    @Override
    public void shootFromRotation(Entity shooter, float pX, float pY, float pZ, float pVelocity, float pInaccuracy) {
        float f = -Mth.sin(pY * ((float) Math.PI / 180F)) * Mth.cos(pX * ((float) Math.PI / 180F));
        float f1 = -1.0F;
        float f2 = Mth.cos(pY * ((float) Math.PI / 180F)) * Mth.cos(pX * ((float) Math.PI / 180F));
        this.shoot((double) f, (double) f1, (double) f2, pVelocity, pInaccuracy);
        // 把发射者自身的速度叠上去 —— 圣骑在冲刺时撒弹幕，弹幕会跟着「顺路」飘出去。
        Vec3 vec3 = shooter.getDeltaMovement();
        this.setDeltaMovement(this.getDeltaMovement().add(vec3.x,
                shooter.onGround() ? 0.0D : vec3.y, vec3.z));
    }

    // ------------------------------------------------------------------
    //  每 tick
    // ------------------------------------------------------------------

    /**
     * ⚠️ 注意 {@code super.tick()} 在最<b>后面</b>，这是原版的写法，不是笔误。
     *
     * <p>常规写法是「先跑基类，再做自己的事」，但那样基类
     * （{@code ThrowableProjectile.tick}）里的命中检测会先跑一遍，
     * 和这里自己写的 {@link #move} + {@link #onUpdateInAir} 的判定顺序就反了。
     * 照抄原版顺序，行为才和传奇怪物里一模一样。
     */
    @Override
    public void tick() {
        if (this.getOwner() != null && !this.getOwner().isAlive()) {
            // 主人死了，弹幕跟着消散 —— 免得主人没了子弹还在天上飘。
            this.discard();
        } else {
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.onUpdateInAir();
        }
        super.tick();
    }

    /**
     * 灵魂冲击的全部实质都在这儿：寿命、伤害、粒子。
     */
    private void onUpdateInAir() {
        --this.lifeTime;
        if (this.lifeTime <= 0) {
            this.discard();
        }

        Entity ownerEntity = this.getOwner();
        LivingEntity livingOwner = ownerEntity instanceof LivingEntity ? (LivingEntity) ownerEntity : null;

        // ---- 伤害 ----
        // ⚠️ `tickCount % 5 == 0` 是关键：弹幕有 20 tick 寿命，
        //    不隔 5 tick 结算一次的话，同一颗弹会在同一个人身上连打 20 下。
        if (livingOwner != null && this.tickCount % 5 == 0) {
            for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class,
                    this.getBoundingBox().inflate(0.5D, 0.5D, 0.5D))) {
                // 四条过滤：不是主人本人、脚踩实地、还活着、不是自己人。
                // 「脚踩实地」是原版的规矩 —— 飞在半空的单位吃不到这一圈弹幕。
                if (target == livingOwner || !target.onGround()
                        || !target.isAlive() || livingOwner.isAlliedTo(target)) {
                    continue;
                }

                // 伤害 = 弹自带伤害(12) + 目标最大生命值换算来的一小截
                // （MathUtils.entityBasedHpDamage(entity, 3.0F) 里的 3.0F 是百分比系数）。
                if (target.hurt(ModDamageTypes.causeGhostlyDamage(livingOwner, livingOwner),
                        this.getDamage() + MathUtils.entityBasedHpDamage(target, 3.0F))) {
                    // 命中了就给主人回 8 点血。
                    // 原版这里还挂了一个 `!(getOwner() instanceof Player)` —— 那是「主人是玩家就不回」，
                    // 圣骑的主人永远是圣骑，条件恒成立，所以直接回。
                    livingOwner.heal(8.0F);
                }
            }
        }

        // ---- 拖尾粒子（纯客户端）----
        if (this.level().isClientSide) {
            // 第一层：5 颗灵魂粒，在碰撞箱范围内随机抖。
            for (int i = 0; i < 5; ++i) {
                double x = this.getX() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F)
                        - (double) this.getBbWidth();
                double y = this.getY() + 0.5D + (double) (this.random.nextFloat() * this.getBbHeight());
                double z = this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F)
                        - (double) this.getBbWidth();
                this.level().addParticle(this.getRed()
                                ? ModParticles.GHOSTLY_SOUL_RED.get()
                                : ModParticles.GHOSTLY_SOUL.get(),
                        x, y, z, 0.0D, 0.0D, 0.0D);
            }

            // 第二层：2 颗火苗，叠在灵魂粒上面做出「烧起来」的感觉。
            // 一阶段用原版蓝色灵魂火（ParticleTypes.SOUL_FIRE_FLAME），二阶段换成红火。
            for (int i = 0; i < 2; ++i) {
                double x = this.getX() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F)
                        - (double) this.getBbWidth();
                double y = this.getY() + 0.5D + (double) (this.random.nextFloat() * this.getBbHeight());
                double z = this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F)
                        - (double) this.getBbWidth();
                this.level().addParticle(this.getRed()
                                ? ModParticles.RED_SOUL_FLAME.get()
                                : ParticleTypes.SOUL_FIRE_FLAME,
                        x, y, z, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    // ------------------------------------------------------------------
    //  碰撞
    // ------------------------------------------------------------------

    /**
     * Forge 的「能不能和这个实体发生碰撞」。原版直接转发到
     * {@code canHitEntity} —— 也就是「能打进谁，就能撞谁」。这样一来自动继承了
     * {@code Projectile.canHitEntity} 的规则：<b>不打自己人</b>（这里指不打发射者）。
     */
    @Override
    public boolean canCollideWith(Entity pEntity) {
        return this.canHitEntity(pEntity);
    }

    /**
     * 原版覆写成 false：灵魂冲击不会被别的实体「撞到」，
     * 也就不会被玩家用身体顶飞、或者被箭打掉。
     *
     * <p>（基类默认本来就是 false，这句严格说是多余的 —— 但原版写了，照抄不亏。）
     */
    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    /**
     * 原版覆写成 false：<b>永远不显示着火</b>。
     *
     * <p>这个实体类型本来就注册了 {@code fireImmune()}，压根点不着，
     * 所以这同样是道保险 —— 纯粹是防止别的模组强行给它点火时糊上一层火焰贴图。
     */
    @Override
    public boolean isOnFire() {
        return false;
    }

    /**
     * Forge 的自定义生成包。不写这句，客户端收到生成包时拿不到同步字段，
     * 弹幕的伤害和颜色（尤其是二阶段的红）会全部是默认值。
     */
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
