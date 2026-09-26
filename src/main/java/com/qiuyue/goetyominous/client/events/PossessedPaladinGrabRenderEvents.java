package com.qiuyue.goetyominous.client.events;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.layer.lm.PossessedPaladinServantGrabLayer;
import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, value = Dist.CLIENT)
public final class PossessedPaladinGrabRenderEvents {

    private PossessedPaladinGrabRenderEvents() {
    }

    @SuppressWarnings("rawtypes")
    @SubscribeEvent
    public static void hideHeldVictim(RenderLivingEvent.Pre event) {
        LivingEntity entity = event.getEntity();

        if (!(entity.getVehicle() instanceof PossessedPaladinServant)) {
            return;
        }

        if (isFirstPersonPlayer(entity)) {
            return;
        }

        if (PossessedPaladinServantGrabLayer.isCurrentlyRendering(entity.getUUID())) {
            return;
        }

        MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Post(
                entity, event.getRenderer(), event.getPartialTick(),
                event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight()));
        event.setCanceled(true);
    }

    private static boolean isFirstPersonPlayer(Entity entity) {
        return entity.equals(Minecraft.getInstance().cameraEntity)
                && Minecraft.getInstance().options.getCameraType().isFirstPerson();
    }
}
