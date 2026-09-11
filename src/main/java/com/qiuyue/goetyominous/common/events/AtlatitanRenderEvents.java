package com.qiuyue.goetyominous.common.events;

import com.qiuyue.goetyominous.common.entities.ally.ac.AtlatitanServant;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class AtlatitanRenderEvents {

    public static final List<UUID> BLOCKED_ENTITY_RENDERS = new ArrayList<>();
    private static UUID currentRenderingPassenger = null;

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
        if (entity.getVehicle() instanceof AtlatitanServant) {
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

    private static boolean isFirstPersonPlayer(Entity entity) {
        return entity.equals(Minecraft.getInstance().cameraEntity)
                && Minecraft.getInstance().options.getCameraType().isFirstPerson();
    }
}
