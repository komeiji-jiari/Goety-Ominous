package com.qiuyue.goetyominous.common.events;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.mobs.Warg;
import net.minecraft.client.model.PlayerModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, value = Dist.CLIENT)
public final class WargClientEvents {

    @SubscribeEvent
    public static void poseWargRider(RenderPlayerEvent.Pre event) {
        if (!(event.getEntity().getVehicle() instanceof Warg)) {
            return;
        }
        PlayerModel<?> model = event.getRenderer().getModel();
        model.rightLeg.xRot = 0.0F;
        model.rightLeg.yRot = 0.0F;
        model.rightLeg.zRot = 0.12F;
        model.leftLeg.xRot = 0.0F;
        model.leftLeg.yRot = 0.0F;
        model.leftLeg.zRot = -0.12F;
    }
}
