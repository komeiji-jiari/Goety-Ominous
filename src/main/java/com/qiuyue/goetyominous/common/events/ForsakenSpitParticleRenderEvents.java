package com.qiuyue.goetyominous.common.events;

import com.mojang.blaze3d.systems.RenderSystem;
import com.qiuyue.goetyominous.client.render.ac.RenderForsakenServant;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class ForsakenSpitParticleRenderEvents {

    @SubscribeEvent
    public static void postRenderStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS) {
            RenderSystem.runAsFancy(() -> RenderForsakenServant.renderEntireBatch(event.getLevelRenderer(), event.getPoseStack(), event.getRenderTick(), event.getCamera(), event.getPartialTick()));
        }
    }
}
