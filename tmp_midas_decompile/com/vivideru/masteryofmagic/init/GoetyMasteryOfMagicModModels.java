/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.EntityRenderersEvent$RegisterLayerDefinitions
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.vivideru.masteryofmagic.init;

import com.vivideru.masteryofmagic.client.model.GazerModel;
import com.vivideru.masteryofmagic.client.model.GhiaccioModel;
import com.vivideru.masteryofmagic.client.model.IceMonarchModel;
import com.vivideru.masteryofmagic.client.model.MasterStaffModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, value={Dist.CLIENT})
public class GoetyMasteryOfMagicModModels {
    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(GhiaccioModel.LAYER_LOCATION, GhiaccioModel::createBodyLayer);
        event.registerLayerDefinition(GazerModel.LAYER_LOCATION, GazerModel::createBodyLayer);
        event.registerLayerDefinition(IceMonarchModel.LAYER_LOCATION, IceMonarchModel::createBodyLayer);
        event.registerLayerDefinition(MasterStaffModel.LAYER_LOCATION, MasterStaffModel::createBodyLayer);
    }
}

