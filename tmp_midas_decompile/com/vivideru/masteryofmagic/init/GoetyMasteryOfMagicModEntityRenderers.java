/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.client.render.AbstractCairnNecromancerRenderer
 *  com.Polarice3.Goety.client.render.DrownedNecromancerRenderer
 *  com.Polarice3.Goety.client.render.MossyNecromancerRenderer
 *  com.Polarice3.Goety.client.render.NecromancerRenderer
 *  com.Polarice3.Goety.client.render.WitherNecromancerRenderer
 *  net.minecraft.world.entity.EntityType
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.EntityRenderersEvent$RegisterRenderers
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.vivideru.masteryofmagic.init;

import com.Polarice3.Goety.client.render.AbstractCairnNecromancerRenderer;
import com.Polarice3.Goety.client.render.DrownedNecromancerRenderer;
import com.Polarice3.Goety.client.render.MossyNecromancerRenderer;
import com.Polarice3.Goety.client.render.NecromancerRenderer;
import com.Polarice3.Goety.client.render.WitherNecromancerRenderer;
import com.vivideru.masteryofmagic.client.renderer.GazerRenderer;
import com.vivideru.masteryofmagic.client.renderer.GhiaccioRenderer;
import com.vivideru.masteryofmagic.client.renderer.IceMonarchRenderer;
import com.vivideru.masteryofmagic.client.renderer.NamelessNecromancerRenderer;
import com.vivideru.masteryofmagic.client.renderer.VampiratorServantRenderer;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, value={Dist.CLIENT})
public class GoetyMasteryOfMagicModEntityRenderers {
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer((EntityType)GoetyMasteryOfMagicModEntities.VAMPIRATOR_SERVANT.get(), VampiratorServantRenderer::new);
        event.registerEntityRenderer((EntityType)GoetyMasteryOfMagicModEntities.GAZER.get(), GazerRenderer::new);
        event.registerEntityRenderer((EntityType)GoetyMasteryOfMagicModEntities.GHIACCIO.get(), GhiaccioRenderer::new);
        event.registerEntityRenderer((EntityType)GoetyMasteryOfMagicModEntities.ICE_MONARCH.get(), IceMonarchRenderer::new);
        event.registerEntityRenderer((EntityType)GoetyMasteryOfMagicModEntities.JARLESS_NECROMANCER.get(), NecromancerRenderer::new);
        event.registerEntityRenderer((EntityType)GoetyMasteryOfMagicModEntities.JARLESS_WITHER_NECROMANCER.get(), WitherNecromancerRenderer::new);
        event.registerEntityRenderer((EntityType)GoetyMasteryOfMagicModEntities.JARLESS_CAIRN_NECROMANCER.get(), AbstractCairnNecromancerRenderer::new);
        event.registerEntityRenderer((EntityType)GoetyMasteryOfMagicModEntities.JARLESS_MOSSY_NECROMANCER.get(), MossyNecromancerRenderer::new);
        event.registerEntityRenderer((EntityType)GoetyMasteryOfMagicModEntities.JARLESS_DROWNED_NECROMANCER.get(), DrownedNecromancerRenderer::new);
        event.registerEntityRenderer((EntityType)GoetyMasteryOfMagicModEntities.NAMELESS_NECROMANCER.get(), NamelessNecromancerRenderer::new);
    }
}

