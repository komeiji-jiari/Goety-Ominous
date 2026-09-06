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

@OnlyIn(Dist.CLIENT)
public class TremorzillaRenderEvents {

    public static final List<UUID> BLOCKED_ENTITY_RENDERS = new ArrayList<>();
    private static UUID currentRenderingPassenger = null;
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
