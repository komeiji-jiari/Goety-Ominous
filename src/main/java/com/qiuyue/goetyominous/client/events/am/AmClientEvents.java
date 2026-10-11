package com.qiuyue.goetyominous.client.events.am;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.projectile.am.RenderIceShard;
import com.qiuyue.goetyominous.compat.mod.AlexMobsCompat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AmClientEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        if (!AlexMobsCompat.isAlexMobsLoaded()) {
            return;
        }

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.MURMUR_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.am.RenderMurmurServantBody::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.MURMUR_SERVANT_HEAD.get(),
                com.qiuyue.goetyominous.client.render.am.RenderMurmurServantHead::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.CRIMSON_MOSQUITO_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.am.RenderCrimsonMosquitoServant::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.WARPED_MOSCO_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.am.RenderWarpedMoscoServant::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.MOSQUITO_SERVANT_SPIT.get(),
                com.qiuyue.goetyominous.client.render.projectile.am.RenderMosquitoServantSpit::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.SERVANT_SAND_SHOT.get(),
                com.qiuyue.goetyominous.client.render.projectile.am.RenderServantSandShot::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.SERVANT_HEMOLYMPH.get(),
                com.qiuyue.goetyominous.client.render.projectile.am.RenderServantHemolymph::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.FARSEER_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.am.RenderFarseerServant::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.TUSKLIN_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.am.RenderTusklinServant::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.TIGER_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.am.RenderTigerServant::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.FROSTSTALKER_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.am.RenderFroststalkerServant::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.ROCKY_ROLLER_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.am.RenderRockyRollerServant::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.ZOMBIE_CROCODILE_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.am.RenderZombieCrocodileServant::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.BUNFUNGUS_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.am.RenderBunfungusServant::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.ILLAGER_ELEPHANT_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.am.RenderIllagerElephantServant::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.SKELEWAG_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.am.RenderSkelewagServant::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.WITHER_SKELEWAG_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.am.RenderWitherSkelewagServant::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.STRAY_SKELEWAG_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.am.RenderStraySkelewagServant::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.ICE_SHARD.get(),
                RenderIceShard::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.SERVANT_VOID_WORM_SHOT.get(),
                com.qiuyue.goetyominous.client.render.am.RenderServantVoidWormShot::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.DROPBEAR_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.am.RenderDropBearServant::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.GUSTER_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.am.RenderGusterServant::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.EMU_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.am.RenderEmuServant::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.SOUL_VULTURE_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.am.RenderSoulVultureServant::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.SERVANT_CENTIPEDE_HEAD.get(),
                com.qiuyue.goetyominous.client.render.am.RenderServantCentipedeHead::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.SERVANT_CENTIPEDE_BODY.get(),
                com.qiuyue.goetyominous.client.render.am.RenderServantCentipedeBody::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.SERVANT_CENTIPEDE_TAIL.get(),
                com.qiuyue.goetyominous.client.render.am.RenderServantCentipedeTail::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.FART_SERVANT.get(),
                com.github.alexthe666.alexsmobs.client.render.RenderFart::new);
    }
}
