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

    /** 落地风场：三层环，半径和寿命都放大，攒成旋风聚晶那种白条柱。 */
    private static final float FIELD_VORTEX_RADIUS = 1.15F;
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

    private static boolean ring(ServerLevel level, Vec3 pos, float radius, float[] heights, int life) {
        boolean any = false;
        for (float height : heights) {
            any |= vortex(level, pos, radius, height, life);
        }
        return any;
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
        arrow.getPersistentData().putLong(FIELD_END, level.getGameTime() + FIELD_TICKS);
        if (level instanceof ServerLevel server) {
            server.sendParticles(wind(WIND_SHOCKWAVE), arrow.getX(), arrow.getY(), arrow.getZ(),
                    8, 1.2D, 0.1D, 1.2D, 0.05D);
        }
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || ARROWS.isEmpty()) {
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
            boolean field = arrow.getPersistentData().contains(FIELD_END)
                    && level.getGameTime() < arrow.getPersistentData().getLong(FIELD_END);
            double radius = field ? FIELD_RADIUS : LURE_RADIUS;
            double pull = field ? FIELD_PULL : LURE_PULL;
            Vec3 centre = arrow.position();
            AABB box = new AABB(centre, centre).inflate(radius);
            for (Mob mob : level.getEntitiesOfClass(Mob.class, box)) {
                if (mob.isSpectator()) {
                    continue;
                }
                Vec3 away = centre.subtract(mob.position());
                double distance = away.length();
                if (distance < 0.35D || distance > radius) {
                    continue;
                }
                Vec3 dir = away.scale(1.0D / distance);
                mob.push(dir.x * pull, field ? 0.06D : 0.02D, dir.z * pull);
                mob.hurtMarked = true;
            }
            if (field) {
                if (!ring(server, centre, FIELD_VORTEX_RADIUS, FIELD_RINGS, FIELD_VORTEX_LIFE)) {
                    server.sendParticles(ModParticleTypes.GUST.get(),
                            arrow.getX(), arrow.getY(), arrow.getZ(),
                            8, 2.0D, 0.5D, 2.0D, 0.0D);
                }
            } else if (!ring(server, centre, FLIGHT_VORTEX_RADIUS, FLIGHT_RINGS, FLIGHT_VORTEX_LIFE)) {
                server.sendParticles(ModParticleTypes.GUST.get(),
                        arrow.getX(), arrow.getY(), arrow.getZ(),
                        2, 0.4D, 0.3D, 0.4D, 0.0D);
            }
        }
    }
}
