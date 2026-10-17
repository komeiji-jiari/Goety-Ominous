package com.qiuyue.goetyominous.common.events;

import com.mojang.logging.LogUtils;
import com.qiuyue.goetyominous.common.init.ModParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mod.EventBusSubscriber(modid = "goetyominous")
public class WindBowEvents {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final String TAG = "GoetyOminousWindBow";
    /**
     * 服务端侧的风箭名单。🔴 只在服务端有效 —— 客户端那份静态列表永远是空的（单机也分逻辑端），
     * 粒子改由服务端 {@code sendParticles} 发包，客户端自动接收渲染。
     */
    private static final List<AbstractArrow> ARROWS = new ArrayList<>();

    /**
     * 命中后原地残留的风场名单。箭命中实体时 vanilla 在 {@code onHitEntity} 末尾直接
     * {@code discard()}，挂在箭身上的风场一个 tick 都跑不到，所以从箭身上剥出来自己跑完 3 秒。
     */
    private static final List<WindField> FIELDS = new ArrayList<>();

    private static final double LURE_RADIUS = 6.0D;
    private static final double LURE_PULL = 0.035D;

    private static final double FIELD_RADIUS = 4.0D;
    private static final double FIELD_PULL = 0.09D;
    private static final int FIELD_TICKS = 60;

    private static final String FIELD_END = "GoetyOminousWindFieldEnd";

    /** 落地冲击波仍用 Goety 的 wind_shockwave（一次性爆开，不是旋转）。 */
    private static final ResourceLocation WIND_SHOCKWAVE = new ResourceLocation("goety", "wind_shockwave");

    /**
     * 风粒子不挂任何实体（{@code ownerId = -1}）：{@code WindParticle} 用 {@code ownerId > 0} 判
     * {@code isEntity}，为真且客户端查不到该实体时**第一 tick 就 remove 自己**。挂箭头 id 又会整条涡
     * 跟着箭飞、被拉成白线，所以一律不挂 —— 每颗粒子绕自己出生的那个点转，箭飞过去留下一串涡。
     */
    private static final int VORTEX_NO_OWNER = -1;

    /** 飞行中：两个环，半径小、活得短（箭移动快，长了就糊成一片白）。 */
    private static final float FLIGHT_VORTEX_RADIUS = 0.50F;
    private static final float[] FLIGHT_RINGS = {-0.30F, 0.45F};
    private static final int FLIGHT_VORTEX_LIFE = 9;

    /**
     * 落地风场：三层环。半径 = {@link #FIELD_RADIUS}，也就是拉拽真正够得着的那个圈，
     * 视觉范围和手感范围从此是同一个大小（原来是 1.15，圈明显比手小一圈）。
     */
    private static final float FIELD_VORTEX_RADIUS = 4.0F;
    private static final float[] FIELD_RINGS = {-0.50F, 0.55F, 1.35F};
    private static final int FIELD_VORTEX_LIFE = 22;

    private static Method vortexMethod;
    private static Object vortexColour;
    private static boolean vortexReady;
    private static boolean vortexTried;

    private static SimpleParticleType wind(ResourceLocation id) {
        return ForgeRegistries.PARTICLE_TYPES.getValue(id) instanceof SimpleParticleType type
                ? type : net.minecraft.core.particles.ParticleTypes.CLOUD;
    }

    private static void prepareVortex() {
        if (vortexTried) {
            return;
        }
        vortexTried = true;
        try {
            Class<?> colour = Class.forName("com.Polarice3.Goety.utils.ColorUtil");
            Class<?> util = Class.forName("com.Polarice3.Goety.utils.ServerParticleUtil");
            vortexColour = colour.getConstructor(float.class, float.class, float.class)
                    .newInstance(0.93F, 0.97F, 1.00F);
            vortexMethod = util.getMethod("windParticle", ServerLevel.class, colour,
                    float.class, float.class, int.class, int.class, Vec3.class);
            vortexReady = true;
        } catch (Throwable failed) {
            vortexReady = false;
            LOGGER.warn("[windbow] Goety windParticle is not reachable, wind becomes GUST", failed);
        }
    }

    private static boolean vortex(ServerLevel level, Vec3 pos, float width, float height, int life) {
        prepareVortex();
        if (!vortexReady) {
            return false;
        }
        try {
            vortexMethod.invoke(null, level, vortexColour, width, height, life, VORTEX_NO_OWNER, pos);
            return true;
        } catch (Throwable failed) {
            vortexReady = false;
            LOGGER.warn("[windbow] Goety windParticle threw, wind becomes GUST", failed);
            return false;
        }
    }

    /**
     * 沿 {@code axis} 铺一圈环。Goety 的风粒子轨道是水平圆（{@code pos + (0,height,width).yRot(θ)}），
     * 本身不会倾斜，倾斜只能靠把每一环的出生点沿轴挪：{@code height} 传 0，centre 逐环加
     * {@code axis * h}。于是环所在平面的法线就是 {@code axis} —— 箭往上打，风柱就跟着仰起来。
     */
    private static boolean ring(ServerLevel level, Vec3 centre, Vec3 axis, float radius, float[] heights, int life) {
        boolean any = false;
        for (float height : heights) {
            any |= vortex(level, centre.add(axis.scale(height)), radius, 0.0F, life);
        }
        return any;
    }

    /**
     * 把飞行方向摊成一根风柱的单位轴：水平分量定倾斜的方向和幅度（仰角多大就倾斜多大），
     * 垂直分量只留符号。纯垂直射（上下直射）时水平分量退化成 0，回退成竖直轴。
     */
    private static Vec3 axisOf(Vec3 delta) {
        Vec3 flat = new Vec3(delta.x, 0.0D, delta.z);
        double span = flat.length();
        if (span < 1.0E-4D) {
            return new Vec3(0.0D, 1.0D, 0.0D);
        }
        Vec3 axis = new Vec3(0.0D, 1.0D, 0.0D).add(flat.scale(delta.y / span));
        return axis.lengthSqr() < 1.0E-4D ? new Vec3(0.0D, 1.0D, 0.0D) : axis.normalize();
    }

    /** 把范围内的 Mob 往 centre 吸一小把；{@code lift} 是额外塞上去的升力。 */
    private static void pull(ServerLevel server, Vec3 centre, double radius, double pull, double lift) {
        AABB box = new AABB(centre, centre).inflate(radius);
        for (Mob mob : server.getEntitiesOfClass(Mob.class, box)) {
            if (mob.isSpectator()) {
                continue;
            }
            Vec3 away = centre.subtract(mob.position());
            double distance = away.length();
            if (distance < 0.35D || distance > radius) {
                continue;
            }
            Vec3 dir = away.scale(1.0D / distance);
            mob.push(dir.x * pull, lift, dir.z * pull);
            mob.hurtMarked = true;
        }
    }

    public static void mark(AbstractArrow arrow) {
        arrow.getPersistentData().putBoolean(TAG, true);
        if (!ARROWS.contains(arrow)) {
            ARROWS.add(arrow);
        }
    }

    @SubscribeEvent
    public static void onImpact(ProjectileImpactEvent event) {
        if (!(event.getProjectile() instanceof AbstractArrow arrow)) {
            return;
        }
        if (!arrow.getPersistentData().getBoolean(TAG)) {
            return;
        }
        Level level = arrow.level();
        if (level.isClientSide || arrow.getPersistentData().contains(FIELD_END)) {
            return;
        }
        long end = level.getGameTime() + FIELD_TICKS;
        arrow.getPersistentData().putLong(FIELD_END, end);
        if (level instanceof ServerLevel server) {
            // 风场在事件里当场建。此刻 delta 还是飞行速度（事件在 onHit 之前，onHit 才会把速度改掉/把箭
            // discard），所以偏转角只能从这里取。命中实体和撞块都建 —— 用户：「击中目标也会在原地产生风场」。
            FIELDS.add(new WindField(server, arrow.position(), axisOf(arrow.getDeltaMovement()), end));
            server.sendParticles(wind(WIND_SHOCKWAVE), arrow.getX(), arrow.getY(), arrow.getZ(),
                    8, 1.2D, 0.1D, 1.2D, 0.05D);
        }
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || (ARROWS.isEmpty() && FIELDS.isEmpty())) {
            return;
        }
        Level level = event.level;
        if (level.isClientSide) {
            return;
        }
        if (!(level instanceof ServerLevel server)) {
            return;
        }
        Iterator<AbstractArrow> it = ARROWS.iterator();
        while (it.hasNext()) {
            AbstractArrow arrow = it.next();
            if (!arrow.isAlive()) {
                it.remove();
                continue;
            }
            if (arrow.level() != level) {
                continue;
            }
            Vec3 centre = arrow.position();
            pull(server, centre, LURE_RADIUS, LURE_PULL, 0.02D);
            if (arrow.getPersistentData().contains(FIELD_END)
                    && level.getGameTime() >= arrow.getPersistentData().getLong(FIELD_END)) {
                // 落地 3 秒：用户：「风弓的箭矢落地3s后就会自动消失」。命中实体的那批 vanilla 早就
                // discard 了，这里收的是插进地里、本来要挂 60 秒的那些。
                arrow.discard();
                it.remove();
                continue;
            }
            if (!ring(server, centre, axisOf(arrow.getDeltaMovement()),
                    FLIGHT_VORTEX_RADIUS, FLIGHT_RINGS, FLIGHT_VORTEX_LIFE)) {
                server.sendParticles(ModParticleTypes.GUST.get(),
                        centre.x, centre.y, centre.z,
                        2, 0.4D, 0.3D, 0.4D, 0.0D);
            }
        }
        Iterator<WindField> fit = FIELDS.iterator();
        while (fit.hasNext()) {
            WindField field = fit.next();
            if (field.level.getGameTime() >= field.endTick) {
                fit.remove();
                continue;
            }
            if (field.level != server) {
                continue;
            }
            Vec3 centre = field.centre;
            pull(field.level, centre, FIELD_RADIUS, FIELD_PULL, 0.06D);
            if (!ring(field.level, centre, field.axis, FIELD_VORTEX_RADIUS, FIELD_RINGS, FIELD_VORTEX_LIFE)) {
                field.level.sendParticles(ModParticleTypes.GUST.get(),
                        centre.x, centre.y, centre.z,
                        8, 4.0D, 1.0D, 4.0D, 0.0D);
            }
        }
    }

    /** 命中/落地后原地残留的风场：中心、跟着箭偏转的轴、以及到点自己消失的 tick。 */
    private static final class WindField {
        private final ServerLevel level;
        private final Vec3 centre;
        private final Vec3 axis;
        private final long endTick;

        private WindField(ServerLevel level, Vec3 centre, Vec3 axis, long endTick) {
            this.level = level;
            this.centre = centre;
            this.axis = axis;
            this.endTick = endTick;
        }
    }
}
