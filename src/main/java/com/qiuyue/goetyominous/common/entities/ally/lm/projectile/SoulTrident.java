package com.qiuyue.goetyominous.common.entities.ally.lm.projectile;

import com.qiuyue.goetyominous.common.entities.ally.lm.ServantMath;
import com.qiuyue.goetyominous.common.init.lm.LmDamageTypes;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import com.qiuyue.goetyominous.common.init.lm.LmParticles;
import net.miauczel.legendary_monsters.effect.ModEffects;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Effect.CameraShakeEntity;
import net.miauczel.legendary_monsters.util.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

/**
 * 堕落圣骑仆从扔出去的那把「灵魂三叉戟」（招 38 用）。
 *
 * <p>逻辑逐字照搬传奇怪物的 {@code SoulTridentEntity}，只换了引用的一批
 * （自己家的实体类型 / 粒子 / 伤害类型 / 数学工具）。<b>数值、时序、判定一个都没动。</b>
 *
 * <h2>它都干些什么</h2>
 * <ol>
 *   <li><b>飞行途中</b>：每 tick 在身后撒灵魂粒（{@code ghostly_soul}）拖尾；</li>
 *   <li><b>命中生物</b>：8 + 目标最大生命 3% 的伤害，叠一层「灵魂碎裂」，
 *       并<b>给主人回 8 点血</b>；</li>
 *   <li><b>命中方块</b>：原地炸出一圈「灵魂柱爆炸」（{@link SoulPillarExplosionEntity}），
 *       震一下镜头（{@code CameraShakeEntity}），撒四团红光，然后自己消失。</li>
 * </ol>
 *
 * <h2>和原版的三处差异</h2>
 * <ol>
 *   <li><b>友军判定</b>。原版写的是
 *       {@code pEntity.getType().is(ModEntityTags.POSSESSED_ARMOR_TEAM) || super.isAlliedTo(pEntity)}，
 *       那个标签里装的是<b>传奇怪物自己的 Boss 和小弟</b>。我们的圣骑是仆从，
 *       主人是玩家、伙伴是别的仆从 —— 那个标签一个都不沾。照抄的后果是：
 *       三叉戟飞出去的路上只要蹭到主人或者别的仆从，就会结结实实扎上去。
 *       所以覆写成「<b>主人的自己人 = 我的自己人</b>」，和圣骑那 14 招用的是同一套判定。</li>
 *   <li><b>少了一段</b>。原版命中生物后有
 *       {@code if (result.getEntity() instanceof BeheadedKnightEntity) return;} ——
 *       那是给传奇怪物「无头骑士」开的特例（打中它时戟不掉）。我们世界里没有那个怪，
 *       这个判断恒为假，所以不 import 那个类。对本模组来说行为完全一致。</li>
 *   <li><b>伤害类型</b>。原版取 {@code ModDamageTypes.causeGhostlyDamage}；
 *       我们用 {@link LmDamageTypes#ghostly}（{@code servant_ghost}），
 *       和幻影匕首、灵魂冲击、灵魂柱保持一致。</li>
 * </ol>
 *
 * <h2>⚠️ 为什么是「抄一份」而不是「继承原版的三叉戟」</h2>
 * 原版 {@code SoulTridentEntity} 继承的是 {@code AbstractArrow}，不是原版那个
 * {@code ThrownTrident}。想「继承原版 + 只改几处」是行不通的，因为原版
 * {@code ThrownTrident} 把 {@code tridentItem}、忠诚附魔的 data accessor
 * 全设成了 {@code private}，子类既读不到也改不了，而且它的构造器
 * <b>写死了 {@code EntityType.TRIDENT}</b> —— 我们就没法用自己的实体类型注册了。
 *
 * <p>所以这里和 {@link SoulStrike} 一样：<b>直接继承 {@code AbstractArrow}，把逻辑抄全。</b>
 * 抄的是「我们需要的那些」，原版里用不到的部分（比如那三段空 {@code if}）照旧保留。
 */
public class SoulTrident extends AbstractArrow {

    /** 忠诚附魔等级。原版会从三叉戟物品上读；我们这把是凭空变出来的，永远是 0。 */
    private static final EntityDataAccessor<Byte> ID_LOYALTY =
            SynchedEntityData.defineId(SoulTrident.class, EntityDataSerializers.BYTE);
    /** 是否带附魔光效。同上，永远是 false。 */
    private static final EntityDataAccessor<Boolean> ID_FOIL =
            SynchedEntityData.defineId(SoulTrident.class, EntityDataSerializers.BOOLEAN);

    /**
     * ⚠️ 这个默认值很关键：{@link com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant#throwSoulTrident}
     * 造这把戟时<b>不走</b>那个「带物品栈」的构造器，靠的就是这里默认给一把白板三叉戟。
     * 阅读那边的注释时看到「字段默认值本来就是一把白板三叉戟」，说的就是这一行。
     */
    private ItemStack tridentItem = new ItemStack(Items.TRIDENT);

    /** 是否已经造成过伤害 —— 保证一次飞行只伤一个目标。 */
    private boolean dealtDamage;
    /** 客户端用来数「返航音效」播了几次。忠诚附魔 > 0 时才会走到，我们基本用不上。 */
    public int clientSideReturnTridentTickCount;

    public SoulTrident(EntityType<? extends SoulTrident> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_LOYALTY, (byte) 0);
        this.entityData.define(ID_FOIL, false);
    }

    /**
     * 见类注释第 1 条差异：把「友军」的定义从「传奇怪物的阵营标签」换成「主人的自己人」。
     *
     * <p>{@code super.isAlliedTo(...)} 保留 —— 它只会「多加」几个友军，不会漏判。
     */
    @Override
    public boolean isAlliedTo(Entity entity) {
        Entity owner = this.getOwner();
        return (owner != null && owner.isAlliedTo(entity)) || super.isAlliedTo(entity);
    }

    @Override
    public void tick() {
        // 插在地里超过 4 tick 就当作「已经结算过」，不再重复命中
        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }

        // 拖尾：每 tick 撒 0.5 个灵魂粒（原版就这么写的 —— 循环条件看着怪，
        // 实际效果是「一半的 tick 撒一粒」，照抄不改）
        for (int i = 0; (double) i < 0.5D; ++i) {
            if (this.level().isClientSide) {
                this.level().addParticle(LmParticles.GHOSTLY_SOUL.get(),
                        this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D),
                        0.0D, 0.025D, 0.0D);
            }
        }

        // ── 以下是「忠诚附魔」的返航逻辑 ──────────────────────────
        // 我们这把戟没有附魔，ID_LOYALTY 恒为 0，所以整块都不会执行。
        // 原样保留：将来万一要给仆从配一把带忠诚的戟，这里是现成的。
        Entity owner = this.getOwner();
        int loyalty = this.entityData.get(ID_LOYALTY);
        if (loyalty > 0 && (this.dealtDamage || this.isNoPhysics()) && owner != null) {
            if (!this.isAcceptibleReturnOwner()) {
                if (!this.level().isClientSide && this.pickup == Pickup.ALLOWED) {
                    this.spawnAtLocation(this.getPickupItem(), 0.1F);
                }
                this.discard();
            } else {
                this.setNoPhysics(true);
                Vec3 vec3 = owner.getEyePosition().subtract(this.position());
                this.setPosRaw(this.getX(), this.getY() + vec3.y * 0.015D * (double) loyalty, this.getZ());
                if (this.level().isClientSide) {
                    this.yOld = this.getY();
                }

                double d0 = 0.05D * (double) loyalty;
                this.setDeltaMovement(this.getDeltaMovement().scale(0.95D).add(vec3.normalize().scale(d0)));
                if (this.clientSideReturnTridentTickCount == 0) {
                    // ⚠️ 音量 10.0F —— 用「音量当传播距离」是原版的偷懒写法，照抄。
                    this.playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
                }

                ++this.clientSideReturnTridentTickCount;
            }
        }

        super.tick();
    }

    private boolean isAcceptibleReturnOwner() {
        Entity owner = this.getOwner();
        if (owner != null && owner.isAlive()) {
            return !(owner instanceof ServerPlayer) || !owner.isSpectator();
        }
        return false;
    }

    @Override
    protected ItemStack getPickupItem() {
        return this.tridentItem.copy();
    }

    public boolean isFoil() {
        return this.entityData.get(ID_FOIL);
    }

    /** 已经伤过一个目标之后就不再有「实体碰撞」了 —— 避免一箭穿糖葫芦。 */
    @Nullable
    @Override
    protected EntityHitResult findHitEntity(Vec3 startVec, Vec3 endVec) {
        return this.dealtDamage ? null : super.findHitEntity(startVec, endVec);
    }

    /**
     * 命中<b>生物</b>。
     *
     * <p>伤害 = {@code 8 + 目标最大生命的 3%} + 附魔加成。
     * 打中了就叠「灵魂碎裂」并给主人回 8 点血 —— <b>回血量比匕首大得多</b>，
     * 因为这是二阶段的远程起手，玩家会反复吃到它。
     */
    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity hitEntity = result.getEntity();

        float damage;
        if (hitEntity instanceof LivingEntity livingEntity) {
            damage = 8.0F + ServantMath.entityBasedHpDamage(livingEntity, 3.0F);
        } else {
            damage = 8.0F;
        }
        // 附魔伤害加成。我们的 tridentItem 是白板，恒为 0；
        // 保留是为了和原作一致（将来配附魔也不需要改这里）。
        if (hitEntity instanceof LivingEntity livingEntity) {
            damage += EnchantmentHelper.getDamageBonus(this.tridentItem, livingEntity.getMobType());
        }

        Entity owner = this.getOwner();
        if (hitEntity instanceof LivingEntity livingEntity && !this.isAlliedTo(livingEntity)) {
            DamageSource damageSource = LmDamageTypes.ghostlyOrAttackerless(this.level(), owner);
            this.dealtDamage = true;
            SoundEvent soundevent = SoundEvents.TRIDENT_HIT;

            if (hitEntity.hurt(damageSource, damage)) {
                // 主人回血。注意这里判的是「主人得是活物」，玩家也是活物，所以两种都覆盖。
                if (owner instanceof LivingEntity livingOwner) {
                    livingOwner.heal(8.0F);
                }

                EntityUtil.applyStackingEffect(livingEntity, ModEffects.SOUL_FRACTURE.get(),
                        1, 5, ServantMath.toTicks(10.0F));

                if (hitEntity.getType() == EntityType.ENDERMAN) {
                    return;
                }

                if (owner instanceof LivingEntity livingOwner) {
                    EnchantmentHelper.doPostHurtEffects(livingEntity, owner);
                    EnchantmentHelper.doPostDamageEffects(livingOwner, livingEntity);
                }

                this.doPostHurtEffects(livingEntity);
            }

            // 命中后弹回来一点点（负数 = 往回弹）
            this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));

            float volume = 1.0F;
            // 「引雷」附魔：雷雨天打中露天目标就劈一道雷。白板戟永远是 false，保留原逻辑。
            if (this.level() instanceof ServerLevel && this.level().isThundering() && this.isChanneling()) {
                BlockPos blockpos = hitEntity.blockPosition();
                if (this.level().canSeeSky(blockpos)) {
                    LightningBolt lightningbolt = EntityType.LIGHTNING_BOLT.create(this.level());
                    if (lightningbolt != null) {
                        lightningbolt.moveTo(Vec3.atBottomCenterOf(blockpos));
                        lightningbolt.setCause(owner instanceof ServerPlayer serverPlayer ? serverPlayer : null);
                        this.level().addFreshEntity(lightningbolt);
                        soundevent = SoundEvents.TRIDENT_THUNDER;
                        volume = 5.0F;
                    }
                }
            }

            this.playSound(soundevent, volume, 1.0F);
        }
    }

    /**
     * 沿一条<b>等角螺线</b>（阿基米德螺线）铺出一圈灵魂柱爆炸 ——
     * 这是三叉戟最标志性的那一下：从落点开始，一圈柱子由内向外依次炸开。
     *
     * <p>三个参数配合出「越往外越晚」的节奏：
     * <ul>
     *   <li>{@code t} 是螺线的参数（同时当日轮角度）；</li>
     *   <li>{@code r = t * inBetweenGapFill} 让半径随角度线性增长 —— 这就是螺线；</li>
     *   <li>{@code delay = constantDelay + t * delayFactor} 让外围的柱子<b>引信更长</b>，
     *       于是看起来是一圈圈扩散，而不是同时全炸。</li>
     * </ul>
     *
     * @param max              螺线转到多大（招 38 传 18.0）
     * @param gapFill          每一步转多少（0.3 → 约 60 个柱子）
     * @param inBetweenGapFill 半径增长系数（0.65）
     * @param constantDelay    基础引信（3 tick）
     * @param delayFactor      每单位 t 额外加的引信（1.0）
     */
    public void spawnSpiralStrike(double max, double gapFill, double inBetweenGapFill, int constantDelay, double delayFactor) {
        for (double t = 0.0D; t < max; t += gapFill) {
            int delay = constantDelay + (int) (t * delayFactor);
            double r = t * inBetweenGapFill;
            double x = this.getX() + r * Math.cos(t);
            double y = this.getY();
            double z = this.getZ() + r * Math.sin(t);
            this.spawnSoulPillarExplosions(x, y, z, (int) y - 1, 0.0F, delay);
        }
    }

    /**
     * 从 {@code (x, y, z)} 开始<b>往下找地面</b>，找到就在那儿长一根灵魂柱。
     *
     * <p>「地面」的判据是「脚下方块的<b>上表面</b>是实心的」。找到之后还要看一眼
     * 那一格自己是不是被占了（比如雪层、草），占了多少就把柱子往上顶多少 ——
     * 免得柱子陷进地板里。
     *
     * @param lowestYCheck 往下最多找到这一层就放弃（省得挖穿世界）
     * @return 成功长出来就 true，一路探到底都没找到地面就 false
     */
    private boolean spawnSoulPillarExplosions(double x, double y, double z, int lowestYCheck, float yRot, int warmupDelayTicks) {
        BlockPos blockpos = BlockPos.containing(x, y, z);
        boolean foundGround = false;
        double surfaceOffset = 0.0D;

        do {
            BlockPos below = blockpos.below();
            BlockState belowState = this.level().getBlockState(below);
            if (belowState.isFaceSturdy(this.level(), below, Direction.UP)) {
                // 脚下是实心的 —— 但当前这格可能被雪/草之类占了一部分，把它顶上来的高度量出来
                if (!this.level().isEmptyBlock(blockpos)) {
                    BlockState currentState = this.level().getBlockState(blockpos);
                    VoxelShape shape = currentState.getCollisionShape(this.level(), blockpos);
                    if (!shape.isEmpty()) {
                        surfaceOffset = shape.max(Axis.Y);
                    }
                }
                foundGround = true;
                break;
            }
            blockpos = blockpos.below();
        } while (blockpos.getY() >= lowestYCheck);

        if (!foundGround) {
            return false;
        }

        this.level().addFreshEntity(new SoulPillarExplosionEntity(
                this.level(), x, (double) blockpos.getY() + surfaceOffset, z,
                yRot, warmupDelayTicks, (LivingEntity) this.getOwner(), 20, 8.0F, true));
        return true;
    }

    /**
     * 命中任何东西（生物或方块）时都走这里 —— <b>这是整把戟的收尾演出</b>。
     *
     * <p>注意顺序：先让 {@code super.onHit} 结算基础的插地逻辑，
     * 再撒柱子、震屏、撒红光，最后才决定要不要自己消失。
     */
    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);

        this.spawnSpiralStrike(18.0D, 0.3D, 0.65F, 3, 1.0D);
        CameraShakeEntity.cameraShake(this.level(), this.position(), 10.0F, 0.15F, 10, 5);

        // 四团红光，两团贴着落点、两团抬高 2 格。
        // ⚠️ 原版在这里<b>没有</b> isClientSide 判断。服务端的 addParticle 是空操作，
        //    所以这四团光能不能看见要实机确认 —— 原版什么表现我们就是什么表现，
        //    先照抄不动。
        float f9 = (this.random.nextFloat() - 0.5F) * 8.0F;
        float f10 = (this.random.nextFloat() - 0.5F) * 4.0F;
        float f2 = (this.random.nextFloat() - 0.5F) * 8.0F;
        float f8 = (this.random.nextFloat() - 0.75F) * 5.0F;
        float f6 = (this.random.nextFloat() - 0.75F) * 3.0F;
        float f7 = (this.random.nextFloat() - 0.75F) * 5.0F;
        this.level().addParticle(LmParticles.SOUL_EXPLOSION_RED.get(),
                this.getX() + (double) f9, this.getY() + (double) f10, this.getZ() + (double) f2,
                0.0D, 0.0D, 0.0D);
        this.level().addParticle(LmParticles.SOUL_EXPLOSION_RED.get(),
                this.getX() + (double) f8, this.getY() + (double) f6, this.getZ() + (double) f7,
                0.0D, 0.0D, 0.0D);
        this.level().addParticle(LmParticles.SOUL_EXPLOSION_RED.get(),
                this.getX() + (double) f9, this.getY() + 2.0D + (double) f10, this.getZ() + (double) f2,
                0.0D, 0.5D, 0.0D);
        this.level().addParticle(LmParticles.SOUL_EXPLOSION_RED.get(),
                this.getX() + (double) f8, this.getY() + 2.0D + (double) f6, this.getZ() + (double) f7,
                0.0D, 0.5D, 0.0D);

        if (!this.level().isClientSide) {
            // 命中方块 → 自己消失。命中生物 → 继续飞（dealtDamage 已经挡住二次伤害）。
            if (result instanceof BlockHitResult) {
                this.discard();
            }
        }
    }

    public boolean isChanneling() {
        return EnchantmentHelper.hasChanneling(this.tridentItem);
    }

    @Override
    protected boolean tryPickup(Player player) {
        return super.tryPickup(player)
                || this.isNoPhysics() && this.ownedBy(player)
                && player.getInventory().add(this.getPickupItem());
    }

    /** 插地时的音效 —— 原版用的是「末影龙火球爆炸」，比三叉戟自带的那个响得多。 */
    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.DRAGON_FIREBALL_EXPLODE;
    }

    @Override
    public void playerTouch(Player player) {
        if (this.ownedBy(player) || this.getOwner() == null) {
            super.playerTouch(player);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Trident", 10)) {
            this.tridentItem = ItemStack.of(compound.getCompound("Trident"));
        }
        this.dealtDamage = compound.getBoolean("DealtDamage");
        this.entityData.set(ID_LOYALTY, (byte) EnchantmentHelper.getLoyalty(this.tridentItem));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.put("Trident", this.tridentItem.save(new CompoundTag()));
        compound.putBoolean("DealtDamage", this.dealtDamage);
    }

    /**
     * 原版这两句的「原话」出自 {@code AbstractArrow}，一般是
     * {@code ALLOWED} 时才走超类。我们抄成更直白的等价写法，行为一字不差。
     */
    @Override
    public void tickDespawn() {
        int loyalty = this.entityData.get(ID_LOYALTY);
        if (this.pickup != Pickup.ALLOWED || loyalty <= 0) {
            super.tickDespawn();
        }
    }

    /** 水里的阻力 —— 0.99 表示几乎不减速（原版如此）。 */
    @Override
    protected float getWaterInertia() {
        return 0.99F;
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }
}
