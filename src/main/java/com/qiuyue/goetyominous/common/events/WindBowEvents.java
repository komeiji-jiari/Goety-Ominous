package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.utils.MobUtil;
import com.mojang.logging.LogUtils;
import com.qiuyue.goetyominous.common.init.ModParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
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
import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = "goetyominous")
public class WindBowEvents {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final String TAG = "GoetyOminousWindBow";
    private static final List<AbstractArrow> ARROWS = new ArrayList<>();
    private static final List<WindField> FIELDS = new ArrayList<>();

    private static final double LURE_RADIUS = 6.0D;
    private static final double LURE_PULL = 0.035D;

    private static final double FIELD_RADIUS = 4.0D;
    private static final double FIELD_PULL = 0.09D;
    private static final int FIELD_TICKS = 60;

    private static final String FIELD_END = "GoetyOminousWindFieldEnd";

    private static final ResourceLocation WIND_SHOCKWAVE = new ResourceLocation("goety", "wind_shockwave");

    private static final int VORTEX_NO_OWNER = -1;

    private static final float FLIGHT_VORTEX_RADIUS = 0.50F;
    private static final float[] FLIGHT_RINGS = {-0.30F, 0.45F};
    private static final int FLIGHT_VORTEX_LIFE = 9;

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

    private static boolean ring(ServerLevel level, Vec3 centre, Vec3 axis, float radius, float[] heights, int life) {
        boolean any = false;
        for (float height : heights) {
            any |= vortex(level, centre.add(axis.scale(height)), radius, 0.0F, life);
        }
        return any;
    }

    private static Vec3 axisOf(Vec3 delta) {
        Vec3 flat = new Vec3(delta.x, 0.0D, delta.z);
        double span = flat.length();
        if (span < 1.0E-4D) {
            return new Vec3(0.0D, 1.0D, 0.0D);
        }
        Vec3 axis = new Vec3(0.0D, 1.0D, 0.0D).add(flat.scale(delta.y / span));
        return axis.lengthSqr() < 1.0E-4D ? new Vec3(0.0D, 1.0D, 0.0D) : axis.normalize();
    }

    private static boolean isAlly(@Nullable LivingEntity owner, Mob mob) {
        if (owner == null) {
            return false;
        }
        if (mob == owner) {
            return true;
        }
        if (mob instanceof IOwned owned && owned.getMasterOwner() == owner) {
            return true;
        }
        if (mob instanceof OwnableEntity ownable && ownable.getOwner() == owner) {
            return true;
        }
        return MobUtil.areAllies(owner, mob);
    }

    private static void pull(ServerLevel server, Vec3 centre, double radius, double pull, double lift,
                             @Nullable LivingEntity owner) {
        AABB box = new AABB(centre, centre).inflate(radius);
        for (Mob mob : server.getEntitiesOfClass(Mob.class, box)) {
            if (mob.isSpectator() || isAlly(owner, mob)) {
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
            FIELDS.add(new WindField(server, arrow.position(), axisOf(arrow.getDeltaMovement()), end,
                    arrow.getOwner() instanceof LivingEntity owner ? owner : null));
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
            pull(server, centre, LURE_RADIUS, LURE_PULL, 0.02D,
                    arrow.getOwner() instanceof LivingEntity owner ? owner : null);
            if (arrow.getPersistentData().contains(FIELD_END)
                    && level.getGameTime() >= arrow.getPersistentData().getLong(FIELD_END)) {
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
            pull(field.level, centre, FIELD_RADIUS, FIELD_PULL, 0.06D, field.owner);
            if (!ring(field.level, centre, field.axis, FIELD_VORTEX_RADIUS, FIELD_RINGS, FIELD_VORTEX_LIFE)) {
                field.level.sendParticles(ModParticleTypes.GUST.get(),
                        centre.x, centre.y, centre.z,
                        8, 4.0D, 1.0D, 4.0D, 0.0D);
            }
        }
    }

    private static final class WindField {
        private final ServerLevel level;
        private final Vec3 centre;
        private final Vec3 axis;
        private final long endTick;
        @Nullable
        private final LivingEntity owner;

        private WindField(ServerLevel level, Vec3 centre, Vec3 axis, long endTick,
                          @Nullable LivingEntity owner) {
            this.level = level;
            this.centre = centre;
            this.axis = axis;
            this.endTick = endTick;
            this.owner = owner;
        }
    }
}
