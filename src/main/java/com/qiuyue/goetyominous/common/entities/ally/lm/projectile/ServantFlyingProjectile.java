package com.qiuyue.goetyominous.common.entities.ally.lm.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

/**
 * 「会飞的弹射物」基类 —— 给那些不受重力管、靠惯性直线飞出去的家伙当地基用的。
 *
 * <h2>它是原版 {@code AbstractHurtingProjectile} 的一份拷贝</h2>
 * 没错，<b>原版本来就有这么一个类</b>（恶魂火球、凋灵之首、潜影贝导弹都继承它），
 * 我们之所以不用原版那个、而是自己拷一份，是因为原作（传奇怪物）当年也是拷的，
 * 拷完<b>改掉了 3 个地方</b>：
 *
 * <ol>
 *   <li>{@link #shouldBurn()} 从 {@code true} 改成了 {@code false}。
 *       原版那个类<b>每 tick 都会给自己点火</b>（火球飞着飞着烧起来就是这么来的）。
 *       匕首如果直接继承原版，就会变成一把永远烧着的匕首；</li>
 *   <li>{@code tick()} 里删掉了「每 tick 在身后撒一颗拖尾粒子」那一行。
 *       原版撒的是烟。匕首有自己的拖尾粒子（写在
 *       {@code ThrownPhantomDagger.tick()} 里），再撒烟就重复了；</li>
 *   <li>{@code tick()} 里删掉了「入水时冒气泡」的循环 —— 原版每 tick 在水里冒 4 颗泡泡。</li>
 * </ol>
 *
 * 除此之外<b>逐字一致</b>：那个 0.95 的惯性、入水减速到 0.8、被打中会被打得掉头、
 * 以及 {@code xPower/yPower/zPower} 这三个「每 tick 额外加速度」的同步，全都照搬。
 *
 * <h2>为什么不干脆「继承原版再覆写」</h2>
 * 第 2、3 条做不到 —— 它们藏在 {@code tick()} 的中间，覆写 {@code tick()} 等于把整个方法
 * 重抄一遍，那还不如直接把类整个拷出来。只有第 1 条（{@code shouldBurn}）能靠覆写解决。
 *
 * <p>⚠️ <b>改动这个类 = 改动所有继承它的弹射物的飞行手感。</b>动手前先想清楚你要改的是
 * 「所有飞弹」还是「某一个飞弹」—— 后者请去那个子类里覆写对应方法，别动这里。
 */
public abstract class ServantFlyingProjectile extends Projectile {

    /**
     * 每 tick 额外施加的加速度（不是速度）。
     *
     * <p>名字里的 Power 容易和「威力」混淆，其实是「推进力」：飞行时每 tick 会在现有速度上
     * 再叠加这么一点，然后整体乘 {@link #getInertia()} 衰减。
     * 这三个值由构造器根据初始朝向算出来，之后<b>不再改变</b>（除非被 {[@link #hurt} 打歪）。
     */
    public double xPower;
    public double yPower;
    public double zPower;

    protected ServantFlyingProjectile(EntityType<? extends ServantFlyingProjectile> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * 「从某个坐标朝某个方向飞」用的构造器。
     *
     * @param offsetX/Y/Z 初始朝向向量，<b>不需要</b>预先归一化（内部会除长度）。
     *                    如果三个都是 0，那就不设推进力，弹射物会原地不动。
     */
    public ServantFlyingProjectile(EntityType<? extends ServantFlyingProjectile> entityType,
                                   double x, double y, double z,
                                   double offsetX, double offsetY, double offsetZ,
                                   Level level) {
        this(entityType, level);
        this.moveTo(x, y, z, this.getYRot(), this.getXRot());
        this.reapplyPosition();
        double length = Math.sqrt(offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ);
        if (length != 0.0D) {
            // 归一化之后乘 0.1 —— 所以初始朝向的「长短」不影响飞行，只影响方向。
            this.xPower = offsetX / length * 0.1D;
            this.yPower = offsetY / length * 0.1D;
            this.zPower = offsetZ / length * 0.1D;
        }
    }

    /**
     * 「从某个生物的当前位置朝某个方向飞」用的构造器。
     *
     * <p>注意它<b>不是</b>「朝着该生物看的方向飞」—— 方向完全由 offset 决定，
     * 生物只提供起点坐标、以及被记成 owner。想要「朝着视线飞」，自己把
     * {@code shooter.getViewVector(1.0F)} 的分量当 offset 传进来。
     */
    public ServantFlyingProjectile(EntityType<? extends ServantFlyingProjectile> entityType,
                                   LivingEntity shooter,
                                   double offsetX, double offsetY, double offsetZ,
                                   Level level) {
        this(entityType, shooter.getX(), shooter.getY(), shooter.getZ(),
                offsetX, offsetY, offsetZ, level);
        this.setOwner(shooter);
        this.setRot(shooter.getYRot(), shooter.getXRot());
    }

    /**
     * 同步数据的声明处。
     *
     * <p>父类 {@code Entity} 把这个方法声明成 {@code abstract}，所以<b>必须实现</b>，
     * 哪怕什么都不写。子类要加自己的同步字段，先 {@code super.defineSynchedData()} 再 define。
     */
    @Override
    protected void defineSynchedData() {
    }

    /**
     * 决定「这个弹射物在多远之外就不用画了」。
     *
     * <p>原版 {@code Entity} 默认按碰撞箱大小算一个很小的距离，飞弹飞远一点就会凭空消失。
     * 这里改成了脏话版：用碰撞箱尺寸 ×4 ×64 再平方 —— 对匕首这种小碰撞箱的弹射物来说，
     * 实际可视距离大约 256 格，足够它飞完全程。
     *
     * <p>那个 {@code Double.isNaN} 的兜底是原版写的：碰撞箱尺寸理论上不该是 NaN，
     * 但历史上真出过，所以留了一手。
     */
    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double size = this.getBoundingBox().getSize() * 4.0D;
        if (Double.isNaN(size)) {
            size = 4.0D;
        }
        size *= 64.0D;
        return distance < size * size;
    }

    /**
     * 每 tick 的飞行主循环 —— <b>整个类的核心</b>。
     *
     * <p>流程：<b>主人没了就自毁</b> → 先让父类自己 tick 一轮 → 该着火的着火 →
     * 射线检测这一 tick 有没有撞到东西 → 撞到就交给 {@code onHit} →
     * 处理卡进方块 → 算新坐标 → 朝飞行方向转头 → 更新速度（叠加推进力后乘惯性）→ 挪过去。
     *
     * <p>⚠️ 三个和原版不同的地方见类注释，这里的代码<b>已经是改过的样子</b>：
     * 没有拖尾烟、没有入水气泡。
     */
    @Override
    public void tick() {
        Entity owner = this.getOwner();
        // 服务端：主人还活着（或者压根没有主人）并且所在区块已加载，才继续飞。
        // 客户端：无条件飞（靠同步包驱动）。
        // 否则 —— 也就是服务端发现主人已经没了 —— 直接 discard 自己。
        if (this.level().isClientSide
                || (owner == null || !owner.isRemoved()) && this.level().hasChunkAt(this.blockPosition())) {
            super.tick();
            if (this.shouldBurn()) {
                this.setSecondsOnFire(1);
            }

            // 从「这一 tick 的起点」射到「起点 + 本 tick 位移」，看途中有没有撞上什么。
            // 第二个参数是过滤器：canHitEntity 返回 false 的东西会被直接无视（比如主人自己）。
            HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            // MISS = 没撞到。撞到了还要问一遍 Forge 事件（别的模组可以在这里取消命中）。
            if (hitResult.getType() != HitResult.Type.MISS && !ForgeEventFactory.onProjectileImpact(this, hitResult)) {
                this.onHit(hitResult);
            }

            this.checkInsideBlocks();
            Vec3 movement = this.getDeltaMovement();
            double nextX = this.getX() + movement.x;
            double nextY = this.getY() + movement.y;
            double nextZ = this.getZ() + movement.z;
            // 把弹射物的朝向掰向它的飞行方向（0.2F 是转头速度，越小转得越慢越飘）。
            ProjectileUtil.rotateTowardsMovement(this, 0.2F);
            float inertia = this.getInertia();
            if (this.isInWater()) {
                // 水里额外减速 —— 原版这一支原本还带冒泡泡循环，去掉了（见类注释第 3 条）。
                inertia = 0.8F;
            }

            // 先加推进力，再整体乘惯性衰减。
            this.setDeltaMovement(movement.add(this.xPower, this.yPower, this.zPower).scale(inertia));
            this.setPos(nextX, nextY, nextZ);
        } else {
            this.discard();
        }
    }

    /**
     * 「这一发能不能打中它」。
     *
     * <p>{@code super} 负责主人、友军、已死亡、无敌这些常规过滤；这里额外加的一条是
     * <b>{@code noPhysics} 的东西打不中</b> —— 也就是「穿墙状态」下的实体（比如飞回程中的匕首
     * 自己）不会被另一发弹射物撞下来。
     */
    @Override
    protected boolean canHitEntity(Entity target) {
        return super.canHitEntity(target) && !target.noPhysics;
    }

    /**
     * 飞行途中要不要给自己点火。
     *
     * <p>⚠️ <b>这里返回 {@code false}，和原版的 {@code true} 相反 —— 见类注释第 1 条。</b>
     * 子类如果想要「火焰飞弹」那种效果，覆写成 {@code true} 就行。
     */
    protected boolean shouldBurn() {
        return false;
    }

    /**
     * 惯性系数 —— 每 tick 速度乘这个数。1.0 是「永不减速」，0.95 是原版火球的默认值。
     *
     * <p>子类可以覆写（比如匕首就覆写成了读它自己的字段），飞行手感全靠这个数。
     */
    protected float getInertia() {
        return 0.95F;
    }

    /**
     * 存盘时把三个推进力一起记下来。
     *
     * <p>{@code newDoubleList} 是父类 {@code Entity} 的工具方法，把若干 double 包成一个 ListTag。
     * 这里的 {@code 9} 是 NBT 的 LIST 类型编号，{@code 6} 是「元素类型 = DOUBLE」。
     */
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.put("power", this.newDoubleList(this.xPower, this.yPower, this.zPower));
    }

    /**
     * 读档时恢复三个推进力。
     *
     * <p>三个数少一个就整个不认（{@code size() == 3}），保持全有或全无 —— 免得读到半截数据。
     */
    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("power", 9)) {
            ListTag listTag = compound.getList("power", 6);
            if (listTag.size() == 3) {
                this.xPower = listTag.getDouble(0);
                this.yPower = listTag.getDouble(1);
                this.zPower = listTag.getDouble(2);
            }
        }
    }

    /** 能不能被玩家用碰撞箱「捡」起来 / 被箭打掉。子类可覆写成 false 让它纯粹当个飞行物。 */
    @Override
    public boolean isPickable() {
        return true;
    }

    /** 被「捡」的判定半径。 */
    @Override
    public float getPickRadius() {
        return 1.0F;
    }

    /**
     * 被打中时。
     *
     * <p>不是掉血，而是<b>整个被打得掉头</b>：朝着打人者看的方向重新飞出去，
     * 并且把打人者认成新主人 —— 这行为继承自原版火球，所有飞弹都一样。
     *
     * <p>返回 {@code true} 表示「这一下我接住了」。注意只在服务端真正改向，
     * 客户端那边是靠同步包知道新方向的，不在这里算。
     */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        this.markHurt();
        Entity attacker = source.getEntity();
        if (attacker != null) {
            if (!this.level().isClientSide) {
                Vec3 look = attacker.getLookAngle();
                this.setDeltaMovement(look);
                this.xPower = look.x * 0.1D;
                this.yPower = look.y * 0.1D;
                this.zPower = look.z * 0.1D;
                this.setOwner(attacker);
            }
            return true;
        }
        return false;
    }

    /**
     * 光照亮度 —— 返回固定的 1.0，也就是<b>永远按最亮渲染</b>。
     *
     * <p>不这样的话，飞进洞穴的飞弹会跟着环境一起变暗，发光贴图那种效果就白做了。
     */
    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    /**
     * 生成时发给客户端的同步包。
     *
     * <p>这里之所以要覆写，是因为<b>要把三个推进力塞进去</b>：
     * 客户端的弹射物必须知道它该往哪飞，否则会原地不动然后被自己的 tick 逻辑删掉。
     *
     * <p>⚠️ 注意原版这个构造器的参数顺序是 {@code getXRot(), getYRot()} ——
     * <b>先俯仰后偏航</b>，和 {@code setRot(yRot, xRot)} 那种常见顺序是反的。
     * 照着抄，别顺手「修正」。
     */
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        Entity owner = this.getOwner();
        int ownerId = owner == null ? 0 : owner.getId();
        return new ClientboundAddEntityPacket(this.getId(), this.getUUID(),
                this.getX(), this.getY(), this.getZ(),
                this.getXRot(), this.getYRot(), this.getType(), ownerId,
                new Vec3(this.xPower, this.yPower, this.zPower), 0.0D);
    }

    /**
     * 客户端收到上面那个包之后，把推进力还原出来。
     *
     * <p>发包时传的是「归一化前的原始向量 × 0.1」，这里再归一化一次还原 ——
     * 和构造器里那套算法是同一份，改一个必须改另一个。
     */
    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        double xa = packet.getXa();
        double ya = packet.getYa();
        double za = packet.getZa();
        double length = Math.sqrt(xa * xa + ya * ya + za * za);
        if (length != 0.0D) {
            this.xPower = xa / length * 0.1D;
            this.yPower = ya / length * 0.1D;
            this.zPower = za / length * 0.1D;
        }
    }
}
