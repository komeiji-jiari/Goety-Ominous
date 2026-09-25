package com.qiuyue.goetyominous.common.entities.ally.lm.projectile;

import com.qiuyue.goetyominous.client.particle.lm.Circle;
import com.qiuyue.goetyominous.client.particle.lm.Circle.EnumRingBehavior;
import com.qiuyue.goetyominous.common.entities.ally.lm.ServantMath;
import com.qiuyue.goetyominous.common.init.lm.LmDamageTypes;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import com.qiuyue.goetyominous.common.init.lm.LmParticles;
import com.qiuyue.goetyominous.common.init.lm.LmSounds;
import net.miauczel.legendary_monsters.effect.ModEffects;
import net.miauczel.legendary_monsters.util.EntityUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * 「灵魂柱爆炸」—— 灵魂三叉戟插到地上之后，从地里炸出来的那一圈柱子。
 *
 * <p>逻辑逐字照搬传奇怪物的同名类，只换了引用的那一批（自己家的粒子 / 音效 / 伤害类型 /
 * 实体类型 / 数学工具）。<b>数值、时序、判定一个都没动。</b>
 *
 * <h2>它在什么时候出现</h2>
 * 三叉戟落地时调 {@code spawnSpiralStrike}，绕着落点按「等角螺线」撒一圈这个实体。
 * 每个实体自己有一个 {@code warmupDelayTicks}（越远的位置延得越久），
 * 所以看起来是<b>从中心一圈圈往外炸开</b>的，而不是同时爆。
 *
 * <h2>它长什么样？—— 它什么都没有</h2>
 * 它<b>没有模型、没有贴图</b>，连渲染器都注册成 {@code EmptyRenderer}。
 * 你看到的全部画面都是粒子：
 * <ol>
 *   <li>刚出生（第 1 tick）：地上先亮一圈 7 帧的 {@code ground_soul_red}，
 *       同时一个 {@code facesCamera=false} 的<b>缩小的</b>光圈躺在地上；</li>
 *   <li>「引信」走完（服务端发实体事件 4 给客户端）：又炸一个 {@code soul_pillar_explosion}
 *       + 一个<b>张开的</b>光圈；</li>
 *   <li>音效用 {@code soul_fly}。</li>
 * </ol>
 *
 * <h2>和原版的四处差异（都是「照搬」而不是「改动」）</h2>
 * <ol>
 *   <li><b>父类</b>：原版继承 {@code INoRendererEntity}，那个类点开一看就是
 *       {@code Entity} 加三个空实现，<b>一个字都没有</b>。所以这里直接
 *       {@code extends Entity}，同时自己实现那三个必须实现的方法。</li>
 *   <li><b>少了一段</b>：原版的伤害方法里有一整块
 *       {@code if (this.getCaster() instanceof Frostbitten_GolemEntity)} ——
 *       那是给「霜冻魔像」这个另一只 BOSS 用的分支。<b>它其实是死代码</b>：
 *       那个 if 套在 {@code if (livingentity == null)} 里面，而
 *       {@code livingentity} 就是 {@code getCaster()} 本身 ——
 *       既然它是 null，{@code null instanceof X} 必然为 false，永远进不去。
 *       （真进去还会在主人为 null 时 {@code caster.heal(...)} 直接空指针。）
 *       所以我们不 import 别人的实体类，只保留真正会走的那条路。</li>
 *   <li><b>没搬的几个字段</b>：{@code activateProgress}、{@code prevactivateProgress}、
 *       {@code emergeAnimationState}、{@code getAnimationState(String)}、
 *       {@code setSleep(boolean)} —— 这些是从「灵魂剑刃」那边复制过来的残留，
 *       没有渲染器读它们，也没有任何代码写它们。搬过来只是噪音。</li>
 *   <li><b>伤害类型</b>：原版直接取 {@code ModDamageTypes.GHOST}；
 *       我们用 {@link LmDamageTypes#ghostly}（{@code servant_ghost}），
 *       和幻影匕首、灵魂冲击保持一致 —— 这是三个弹射物统一做过的事。</li>
 * </ol>
 *
 * <p>⚠️ {@code IS_RED}（{@code isRed}）这个参数原版传的是 {@code true}，
 * 但它只被 {@code getRed()/setRed()} 读写，而<b>没有任何地方读 getRed()</b>
 * （没有渲染器）。照抄保留，纯粹为了和原作对得上号。
 */
public class SoulPillarExplosionEntity extends Entity {

    /** 还有多久才「长出来」。三叉戟按距离算出一圈递增的值，做出波浪感。 */
    private int warmupDelayTicks;
    /** 是否已经把「爆炸」这件事广播给客户端了（只广播一次）。 */
    private boolean sentSpikeEvent;
    /** 长出之后的存活 tick 数，默认 20。 */
    private int lifeTicks = 20;
    /** 客户端是否已经收到事件 4、开始倒计时演出了。 */
    private boolean clientSideAttackStarted;
    private LivingEntity caster;
    private UUID casterUuid;

    private static final EntityDataAccessor<Integer> LIFE =
            SynchedEntityData.defineId(SoulPillarExplosionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DAMAGE =
            SynchedEntityData.defineId(SoulPillarExplosionEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> ATTACK =
            SynchedEntityData.defineId(SoulPillarExplosionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_RED =
            SynchedEntityData.defineId(SoulPillarExplosionEntity.class, EntityDataSerializers.BOOLEAN);

    /** 光束的 RGB。默认纯红 —— 三叉戟那边没有改它，所以一直是红的。 */
    public float uR = 1.0F;
    public float uG = 0.0F;
    public float uB = 0.0F;

    public SoulPillarExplosionEntity(EntityType<? extends SoulPillarExplosionEntity> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * 三叉戟用的那个构造 —— 一次把该给的信息全给了。
     *
     * @param yRot            朝向（弧度制，外面传的是 {@code Math.atan2(...)} 之类算出来的角度）
     * @param warmupDelayTicks 引信长度，越远的位置越长
     * @param casterIn        扔三叉戟的那个人（我们这边是圣骑仆从）
     * @param lifeTicks       长出之后活多久
     * @param damage          每次判定打多少伤害
     * @param isRed           原版遗留参数，见类注释
     */
    public SoulPillarExplosionEntity(
            Level worldIn, double x, double y, double z, float yRot, int warmupDelayTicks,
            LivingEntity casterIn, int lifeTicks, float damage, boolean isRed
    ) {
        this(LmEntityRegistry.SOUL_PILLAR_EXPLOSION.get(), worldIn);
        this.warmupDelayTicks = warmupDelayTicks;
        this.setCaster(casterIn);
        // 弧度 → 角度
        this.setYRot(yRot * (180F / (float) Math.PI));
        this.setPos(x, y, z);
        this.setLifeTicks(lifeTicks);
        this.setDamage(damage);
        this.setRed(isRed);
    }

    public boolean getRed() {
        return this.entityData.get(IS_RED);
    }

    public void setRed(boolean damage) {
        this.entityData.set(IS_RED, damage);
    }

    public void setLifeTicks(int lifeTicks) {
        this.lifeTicks = lifeTicks;
    }

    public void setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ATTACK, false);
        this.entityData.define(LIFE, 0);
        this.entityData.define(DAMAGE, 0.0F);
        this.entityData.define(IS_RED, false);
    }

    /**
     * 存盘时不存实体本身，只存一个 UUID。
     * 读档时如果那个 UUID 对应的实体还没加载，就先记着 ——
     * {@link #getCaster()} 会在真正需要的时候再去世界里捞一次。
     */
    public void setCaster(@Nullable LivingEntity casterIn) {
        this.caster = casterIn;
        this.casterUuid = casterIn == null ? null : casterIn.getUUID();
    }

    @Nullable
    public LivingEntity getCaster() {
        if (this.caster == null && this.casterUuid != null && this.level() instanceof ServerLevel) {
            Entity entity = ((ServerLevel) this.level()).getEntity(this.casterUuid);
            if (entity instanceof LivingEntity) {
                this.caster = (LivingEntity) entity;
            }
        }
        return this.caster;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.warmupDelayTicks = compound.getInt("Warmup");
        if (compound.hasUUID("Owner")) {
            this.casterUuid = compound.getUUID("Owner");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Warmup", this.warmupDelayTicks);
        if (this.casterUuid != null) {
            compound.putUUID("Owner", this.casterUuid);
        }
    }

    @Override
    public void tick() {
        super.tick();

        // ── 第一 tick：地上先亮一下 ──────────────────────────────
        // 注意这两句【没有】包在 isClientSide 里 —— 原版就这样。
        // 服务端调 addParticle 是空操作（Level 里是空方法），所以没影响。
        if (this.tickCount == 1) {
            this.level().addParticle(LmParticles.GROUNDSOUL_RED.get(),
                    this.getX(), this.getY() + 2.0D, this.getZ(), 0.0D, 0.0D, 0.0D);
            // 参数依次是：yaw、pitch(π/2 = 平躺)、时长、r、g、b、透明度、大小、是否正对镜头、半径变化
            this.level().addParticle(
                    new Circle.RingData(0.0F, ((float) Math.PI / 2F), 25, this.uR, this.uG, this.uB,
                            0.8F, 20.0F, false, EnumRingBehavior.SHRINK),
                    this.getX(), this.getY() + 0.2F, this.getZ(), 0.0D, 0.0D, 0.0D);
        }

        // 「风声」。注意判的是 lifeTicks == 19 而不是 tickCount ——
        // 因为 lifeTicks 会被下面扣减，所以这一句实际只在刚出生那一下成立。
        if (this.lifeTicks == 19 && !this.isSilent()) {
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(),
                    LmSounds.SOUL_FLY.get(), this.getSoundSource(), 0.3F, 1.25F, false);
        }

        if (this.level().isClientSide) {
            // ── 客户端：收到事件 4 之后开始演出 ──────────────────
            if (this.clientSideAttackStarted) {
                --this.lifeTicks;
                if (this.lifeTicks == 19) {
                    // ⚠️ 原版死代码：这 80 次循环算了一堆随机数（d0~d4、var13）和一次
                    //    方块查询，然后【一个都没用】。看着像是作者本来要撒粒子，
                    //    后来改成用 GROUNDSOUL_RED 了，但忘记把循环删掉。
                    //    照抄保留，改了两边对不上号。
                    for (int i = 0; i < 80; ++i) {
                        BlockState block = this.level().getBlockState(this.blockPosition().below());
                        double d0 = this.getX() + (this.random.nextDouble() * 2.0D - 1.0D) * (double) this.getBbWidth() * 0.5D;
                        double d1 = this.getY() + 0.03D;
                        double d2 = this.getZ() + (this.random.nextDouble() * 2.0D - 1.0D) * (double) this.getBbWidth() * 0.5D;
                        double d3 = this.random.nextGaussian() * 0.07D;
                        double d4 = this.random.nextGaussian() * 0.07D;
                        double var13 = this.random.nextGaussian() * 0.07D;
                    }

                    // 这个光圈是 GROW（张开），和出生那一下的 SHRINK 正好相反
                    this.level().addParticle(
                            new Circle.RingData(0.0F, ((float) Math.PI / 2F), 15, this.uR, this.uG, this.uB,
                                    0.8F, 20.0F, false, EnumRingBehavior.GROW),
                            this.getX(), this.getY() + 0.2F, this.getZ(), 0.0D, 0.0D, 0.0D);
                    this.level().addAlwaysVisibleParticle(LmParticles.SOUL_PILLAR_EXPLOSION.get(),
                            this.getX(), this.getY() + 2.0D, this.getZ(), 0.0D, 0.0D, 0.0D);
                }

                // ⚠️ 原版还有一个空的 if (lifeTicks == 15) {} —— 里面什么都没有，删掉。
            }
        } else if (--this.warmupDelayTicks < 0) {
            // ── 服务端：引信烧完了 ──────────────────────────────
            // 前 8 tick 里每 tick 判定一次伤害（实际每次判定内部还有 %5 的限制）
            if (this.warmupDelayTicks > -8) {
                for (LivingEntity livingentity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox())) {
                    this.damage(livingentity);
                }
            }

            // 只广播一次，客户端收到后才开始演上面的动画
            if (!this.sentSpikeEvent) {
                this.level().broadcastEntityEvent(this, (byte) 4);
                this.sentSpikeEvent = true;
            }

            if (--this.lifeTicks < 0) {
                this.discard();
            }
        }
    }

    public boolean isActivate() {
        return this.entityData.get(ATTACK);
    }

    public void setActivate(boolean activate) {
        this.entityData.set(ATTACK, activate);
    }

    /**
     * 对范围内一个生物做一次伤害判定。
     *
     * <p>几个门槛（任何一个不满足就整个跳过）：
     * <ul>
     *   <li>目标活着、不是无敌帧 —— 不然会白打；</li>
     *   <li>目标不是主人自己；</li>
     *   <li>{@code tickCount % 5 == 0} —— <b>每 5 tick 才真正打一次</b>。
     *       外面那圈循环每 tick 都在跑，但真正的伤害被这个取模卡住了。</li>
     * </ul>
     *
     * <p>打中的额外效果：叠一层「灵魂碎裂」、把目标往上顶 0.85 格、
     * 并且<b>给主人回 4 点血</b>（三叉戟的续航就靠这个）。
     */
    private void damage(LivingEntity impactEntity) {
        LivingEntity caster = this.getCaster();

        if (!impactEntity.isAlive() || impactEntity.isInvulnerable()
                || impactEntity == caster || this.tickCount % 5 != 0) {
            return;
        }

        if (caster == null) {
            // 主人没了（退出游戏 / 死亡卸载）时的兜底：照样打，但没有人头归属，
            // 也不回血、不叠效果 —— 和原版这条分支的行为一致。
            impactEntity.hurt(LmDamageTypes.ghostlyOrAttackerless(this.level(), null),
                    this.getDamage() + ServantMath.entityBasedHpDamage(impactEntity, 3.0F));
            return;
        }

        // 主人是自己人 → 不打。这一句是「仆从招式不误伤主人和队友」的关键。
        if (caster.isAlliedTo(impactEntity)) {
            return;
        }

        if (impactEntity.hurt(LmDamageTypes.ghostly(caster), this.getDamage())) {
            EntityUtil.applyStackingEffect(impactEntity, ModEffects.SOUL_FRACTURE.get(),
                    1, 4, ServantMath.toTicks(10.0F));
            impactEntity.setDeltaMovement(this.getDeltaMovement().x,
                    this.getDeltaMovement().y + 0.85D, this.getDeltaMovement().z);
            EntityUtil.applyPlayerDeltaMovement(impactEntity);
            caster.heal(4.0F);
        }
    }

    /**
     * 服务端发来的「实体事件」。
     *
     * <p>事件号 4 = 「开始演出」，这是本模组里约定的暗号（原版也是），
     * 收到就把 {@link #clientSideAttackStarted} 立起来，上面的演出分支才会开始跑。
     *
     * <p>⚠️ 原版这个方法上写了 {@code @OnlyIn(Dist.CLIENT)}，我们没抄那个注解 ——
     * 它会让 Forge 在服务端把这个覆写整个抹掉，虽然实际上没影响
     * （这个事件只有客户端会收到），但抹掉之后 {@code id <= 0} 那条分支在服务端就没了。
     * 不加注解是严格的行为超集，更安全。
     */
    @Override
    public void handleEntityEvent(byte id) {
        if (id == 4) {
            this.clientSideAttackStarted = true;
        } else if (id <= 0) {
            this.lifeTicks = 0;
        } else {
            super.handleEntityEvent(id);
        }
    }

    /** 无视光照等级，永远按最亮渲染（原版如此）。 */
    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    /**
     * ⚠️ 这一句不能省：Forge 需要它才知道「这个实体该用什么包发给客户端」。
     * 少了它，实体在客户端根本不会出现（而且不报错）。
     */
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
    }
}
