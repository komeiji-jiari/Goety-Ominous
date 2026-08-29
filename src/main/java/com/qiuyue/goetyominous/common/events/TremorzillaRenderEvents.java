package com.qiuyue.goetyominous.common.events;

import com.github.alexmodguy.alexscaves.server.entity.util.ShakesScreen;
import com.qiuyue.goetyominous.common.entities.ally.ac.TremorzillaServant;
import net.minecraft.client.Minecraft;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 特雷莫兹拉仆从客户端渲染事件。
 * 1) 补回 AC 的 blockRenderingEntity/releaseRenderingEntity 渲染锁:
 *    骑乘者由 TremorzillaServantRiderLayer 手动渲染在颈部时,先 release(放行手动渲染),
 *    渲染完再 block 其 UUID。骑乘者自身的 Level 渲染通道触发 RenderLivingEvent.Pre 时
 *    命中被锁集合即被取消,从而避免"颈部一次 + 座位一次"的双重渲染。
 * 2) 补回 AC 的第三视角相机拉远:骑乘巨兽时默认相机距离过近会钻进模型身体,
 *    导致视角/界面错乱;拉远 getMaxZoom(10.0) 使相机脱离身体。
 * 3) 补回 AC 的震颤屏幕震动 (ShakesScreen):每帧扫描半径 64 格内的 ShakesScreen 实体,
 *    按距离计算 tremorAmount 并抖动相机,表现巨兽脚步引发的镜头晃动。
 */
@OnlyIn(Dist.CLIENT)
public class TremorzillaRenderEvents {

    public static final List<UUID> BLOCKED_ENTITY_RENDERS = new ArrayList<>();
    /**
     * 当前正被骑乘者渲染层手动渲染的乘客 UUID(仅在本帧该次 renderPassenger 期间非空)。
     * 用于区分"骑乘者渲染层手动渲染"与"世界渲染通道的渲染",使取消逻辑与渲染顺序无关。
     */
    private static UUID currentRenderingPassenger = null;
    /**
     * 震颤屏幕震动状态(对应 AC ClientProxy.randomTremorOffsets/lastTremorTick):
     * 每玩家 tick 重新生成一次随机偏移,其余帧沿用,保证震动平滑无抖动。
     */
    private static final float[] randomTremorOffsets = new float[3];
    private static int lastTremorTick = -1;

    public static void blockRenderingEntity(UUID entityUUID) {
        if (!BLOCKED_ENTITY_RENDERS.contains(entityUUID)) {
            BLOCKED_ENTITY_RENDERS.add(entityUUID);
        }
    }

    public static void releaseRenderingEntity(UUID entityUUID) {
        BLOCKED_ENTITY_RENDERS.remove(entityUUID);
    }

    public static void setCurrentRenderingPassenger(UUID entityUUID) {
        currentRenderingPassenger = entityUUID;
    }

    public static void clearCurrentRenderingPassenger() {
        currentRenderingPassenger = null;
    }

    private static boolean isCurrentRenderingPassenger(UUID entityUUID) {
        return currentRenderingPassenger != null && currentRenderingPassenger.equals(entityUUID);
    }

    @SuppressWarnings("rawtypes")
    @SubscribeEvent
    public static void preRenderLiving(RenderLivingEvent.Pre event) {
        LivingEntity entity = event.getEntity();
        if (entity.getVehicle() instanceof TremorzillaServant) {
            // 骑乘撼地斯拉仆从的乘客:只放行骑乘者渲染层手动渲染在颈部的那一次。
            // 世界渲染通道按实体 ID 插入顺序遍历(本地玩家 ID 最小),乘客必然先于巨兽渲染,
            // 因此 AC 的 block/release 锁在"乘客先渲染"时必然失效,造成颈部+座位双重渲染。
            // 这里改用"当前手动渲染的乘客 UUID"来区分手动渲染与通道渲染,与遍历顺序无关:
            // 通道渲染一律取消,只有渲染层 renderPassenger 期间的那一次放行。
            if (!isCurrentRenderingPassenger(entity.getUUID())) {
                if (!isFirstPersonPlayer(entity)) {
                    MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Post(
                            entity, event.getRenderer(), event.getPartialTick(),
                            event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight()));
                    event.setCanceled(true);
                }
                BLOCKED_ENTITY_RENDERS.remove(entity.getUUID());
            }
            return;
        }
        if (BLOCKED_ENTITY_RENDERS.contains(entity.getUUID())) {
            if (!isFirstPersonPlayer(entity)) {
                MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Post(
                        entity, event.getRenderer(), event.getPartialTick(),
                        event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight()));
                event.setCanceled(true);
            }
            BLOCKED_ENTITY_RENDERS.remove(entity.getUUID());
        }
    }

    @SubscribeEvent
    public static void computeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
        // 3) 震颤屏幕震动:逐帧移植 AC ClientEvents.computeCameraAngles 的 ShakesScreen 逻辑。
        //    AC 原版用 CLIENT_CONFIG.screenShaking(默认 true)做总开关,本移植无此配置,
        //    故按默认行为无条件启用。nuke/possession 两种 tremor 来源未移植,恒从 0 开始。
        if (cameraEntity != null && cameraEntity.level() != null) {
            float partialTick = Minecraft.getInstance().getPartialTick();
            float tremorAmount = 0.0F;
            double shakeDistanceScale = 64.0;
            double distance = Double.MAX_VALUE;
            AABB aabb = cameraEntity.getBoundingBox().inflate(shakeDistanceScale);
            for (Mob screenShaker : Minecraft.getInstance().level.getEntitiesOfClass(Mob.class, aabb, mob -> mob instanceof ShakesScreen)) {
                ShakesScreen shakesScreen = (ShakesScreen) screenShaker;
                if (!shakesScreen.canFeelShake(cameraEntity) || !((double) screenShaker.distanceTo(cameraEntity) < distance)) continue;
                distance = screenShaker.distanceTo(cameraEntity);
                tremorAmount = Math.min((1.0F - (float) Math.min(1.0, distance / shakesScreen.getShakeDistance())) * Math.max(shakesScreen.getScreenShakeAmount(partialTick), 0.0F), 2.0F);
            }
            if (tremorAmount > 0.0F) {
                if (lastTremorTick != cameraEntity.tickCount) {
                    RandomSource rng = cameraEntity.level().random;
                    randomTremorOffsets[0] = rng.nextFloat();
                    randomTremorOffsets[1] = rng.nextFloat();
                    randomTremorOffsets[2] = rng.nextFloat();
                    lastTremorTick = cameraEntity.tickCount;
                }
                double intensity = (double) tremorAmount * Minecraft.getInstance().options.screenEffectScale().get();
                event.getCamera().move((double) (randomTremorOffsets[0] * 0.2F) * intensity, (double) (randomTremorOffsets[1] * 0.2F) * intensity, (double) (randomTremorOffsets[2] * 0.5F) * intensity);
            }
        }
        if (cameraEntity != null && cameraEntity.isPassenger()
                && cameraEntity.getVehicle() instanceof TremorzillaServant
                && event.getCamera().isDetached()) {
            event.getCamera().move(-event.getCamera().getMaxZoom(10.0), 0.0, 0.0);
        }
    }

    private static boolean isFirstPersonPlayer(Entity entity) {
        return entity.equals(Minecraft.getInstance().cameraEntity)
                && Minecraft.getInstance().options.getCameraType().isFirstPerson();
    }
}
