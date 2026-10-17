package com.qiuyue.goetyominous.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.curios.CroneRobeRenderer;
import com.qiuyue.goetyominous.client.render.ias.*;
import com.qiuyue.goetyominous.client.render.layer.CursedWolfArmorLayer;
import com.qiuyue.goetyominous.client.render.model.*;
import com.qiuyue.goetyominous.client.render.model.ias.*;
import com.qiuyue.goetyominous.client.render.model.curios.CroneRobeModel;
import com.qiuyue.goetyominous.client.render.model.equipment.BoneCudgelModel;
import com.qiuyue.goetyominous.client.render.model.projectile.AcidFungus;
import com.qiuyue.goetyominous.client.render.model.projectile.PitchforkModel;
import com.qiuyue.goetyominous.client.render.projectile.*;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.compat.mod.*;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModModelLayers {

    public static final net.minecraft.client.model.geom.ModelLayerLocation SERVANT_WIND_CHARGE =
            new net.minecraft.client.model.geom.ModelLayerLocation(
                    new net.minecraft.resources.ResourceLocation(
                            com.qiuyue.goetyominous.GoetyOminous.MOD_ID, "servant_wind_charge"), "main");

    public static final net.minecraft.client.model.geom.ModelLayerLocation BREEZE_SERVANT =
            new net.minecraft.client.model.geom.ModelLayerLocation(
                    new net.minecraft.resources.ResourceLocation(
                            com.qiuyue.goetyominous.GoetyOminous.MOD_ID, "breeze_servant"), "main");

    public static final net.minecraft.client.model.geom.ModelLayerLocation BREEZE_SERVANT_WIND =
            new net.minecraft.client.model.geom.ModelLayerLocation(
                    new net.minecraft.resources.ResourceLocation(
                            com.qiuyue.goetyominous.GoetyOminous.MOD_ID, "breeze_servant_wind"), "main");

    public static final net.minecraft.client.model.geom.ModelLayerLocation HURRICANE =
            new net.minecraft.client.model.geom.ModelLayerLocation(
                    new net.minecraft.resources.ResourceLocation(
                            com.qiuyue.goetyominous.GoetyOminous.MOD_ID, "hurricane"), "main");

    public static final net.minecraft.client.model.geom.ModelLayerLocation HURRICANE_PUNCH =
            new net.minecraft.client.model.geom.ModelLayerLocation(
                    new net.minecraft.resources.ResourceLocation(
                            com.qiuyue.goetyominous.GoetyOminous.MOD_ID, "hurricane_punch"), "main");

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModEntityLayers.CONQUILLAGER_SERVANT_LAYER,
                ConquillagerServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.INQUILLAGER_SERVANT_LAYER,
                InquillagerServantModel::createBodyLayer);

        event.registerLayerDefinition(SERVANT_WIND_CHARGE,
                com.qiuyue.goetyominous.client.render.model.WindChargeModel::createBodyLayer);

        event.registerLayerDefinition(BREEZE_SERVANT,
                () -> com.qiuyue.goetyominous.client.render.model.BreezeServantModel.createBodyLayer(32, 32));

        event.registerLayerDefinition(BREEZE_SERVANT_WIND,
                () -> com.qiuyue.goetyominous.client.render.model.BreezeServantModel.createBodyLayer(128, 128));

        event.registerLayerDefinition(HURRICANE,
                com.qiuyue.goetyominous.client.render.model.HurricaneModel::createBodyLayer);

        event.registerLayerDefinition(HURRICANE_PUNCH,
                com.qiuyue.goetyominous.client.render.model.HurricanePunchModel::createBodyLayer);

        event.registerLayerDefinition(CroneRobeModel.LAYER_LOCATION, CroneRobeModel::createBodyLayer);

        event.registerLayerDefinition(BoneCudgelModel.LAYER_LOCATION, BoneCudgelModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.ACID_FUNGUS_LAYER,
                AcidFungus::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.DREDEN_LAYER,
                DredenModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.URBHADHACH_LAYER,
                UrbhadhachModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.URBHADHACH_SERVANT_LAYER,
                UrbhadhachServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.FANATIC_LAYER,
                FanaticModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.BELDAM_LAYER,
                ModWitchModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.ZEALOT_LAYER,
                ZealotModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.THUG_LAYER,
                ThugModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.CHANNELLER_LAYER,
                ChannellerModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.AGONY_LAYER,
                AgonyModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.WARG,
                WargModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.WARG_ARMOR,
                WargArmorModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.WARG_SADDLE,
                WargSaddleModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.CERBERUS,
                CerberusModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.CERBERUS_ARMOR,
                CerberusArmorModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.CURSED_BLACK_BEAST_ARMOR_LAYER,
                CursedBlackBeastArmorModel::createBodyLayer);

        event.registerLayerDefinition(CroneRobeModel.LAYER_LOCATION, CroneRobeModel::createBodyLayer);
        CroneRobeRenderer.register();

        event.registerLayerDefinition(ModEntityLayers.FUNGUS_PACK_LAYER, FungusPackModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.PITCHFORK_LAYER, PitchforkModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.BOGGED_LAYER,
                BoggedModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.BOGGED_OUTER_LAYER,
                () -> LayerDefinition.create(
                        HumanoidModel.createMesh(new CubeDeformation(0.25F), 0.0F), 64, 32));

        event.registerLayerDefinition(ModEntityLayers.SUNKEN_NECROMANCER_LAYER,
                SunkenNecromancerModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.AXOLOTL_SERVANT_LAYER,
                AxolotlServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.HERESIARCH_SERVANT_LAYER,
                HeresiarchServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.HERESIARCH_SERVANT_SHADOW_LAYER,
                HeresiarchServantModel::createShadowLayer);

        event.registerLayerDefinition(ModEntityLayers.DISCIPLE_LAYER,
                DiscipleModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.DISCIPLE_SERVANT_LAYER,
                DiscipleModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.STORM_NECROMANCER_LAYER,
                StormNecromancerModel::createBodyLayer);

        event.registerLayerDefinition(PiglinMerchantModel.LAYER_LOCATION,
                PiglinMerchantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.CURSED_BLACK_WOLF_ARMOR_LAYER,
                CursedBlackWolfArmorModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.CURSED_WOLF_ARMOR_LAYER,
                CursedWolfArmorModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.ARCH_GEOMANCER_LAYER,
                ArchGeomancerModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.PIGLIN_SERVANT_LAYER,
                () -> net.minecraft.client.model.geom.builders.LayerDefinition.create(
                        PiglinServantModel.createMesh(net.minecraft.client.model.geom.builders.CubeDeformation.NONE), 64, 64));

        event.registerLayerDefinition(ModEntityLayers.PIGLIN_SERVANT_INNER_ARMOR_LAYER,
                () -> LayerDefinition.create(
                        HumanoidModel.createMesh(new CubeDeformation(0.5F), 1.0F), 64, 32));
        event.registerLayerDefinition(ModEntityLayers.PIGLIN_SERVANT_OUTER_ARMOR_LAYER,
                () -> LayerDefinition.create(
                        HumanoidModel.createMesh(new CubeDeformation(1.0F), 1.0F), 64, 32));

    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
                event.registerEntityRenderer(ModEntityTypes.CONQUILLAGER_SERVANT.get(),
                ConquillagerServantRenderer::new);
                event.registerEntityRenderer(ModEntityTypes.INQUILLAGER_SERVANT.get(), InquillagerServantRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.FUNGUS_THROWER.get(),
                context -> new PiglinServantRenderer(context,
                        ModEntityLayers.PIGLIN_SERVANT_LAYER,
                        ModEntityLayers.PIGLIN_SERVANT_INNER_ARMOR_LAYER,
                        ModEntityLayers.PIGLIN_SERVANT_OUTER_ARMOR_LAYER));

        event.registerEntityRenderer(ModEntityTypes.PIGLIN_SERVANT.get(),
                (context) -> new PiglinServantRenderer(context,
                        ModEntityLayers.PIGLIN_SERVANT_LAYER,
                        ModEntityLayers.PIGLIN_SERVANT_INNER_ARMOR_LAYER,
                        ModEntityLayers.PIGLIN_SERVANT_OUTER_ARMOR_LAYER));

        event.registerEntityRenderer(ModEntityTypes.PIGLIN_BRUTE_SERVANT.get(),
                (context) -> new PiglinServantRenderer(context,
                        ModEntityLayers.PIGLIN_SERVANT_LAYER,
                        ModEntityLayers.PIGLIN_SERVANT_INNER_ARMOR_LAYER,
                        ModEntityLayers.PIGLIN_SERVANT_OUTER_ARMOR_LAYER));

        event.registerEntityRenderer(ModEntityTypes.STRONG_PIGLIN_BRUTE_SERVANT.get(),
                (context) -> new PiglinServantRenderer(context,
                        ModEntityLayers.PIGLIN_SERVANT_LAYER,
                        ModEntityLayers.PIGLIN_SERVANT_INNER_ARMOR_LAYER,
                        ModEntityLayers.PIGLIN_SERVANT_OUTER_ARMOR_LAYER));

        event.registerEntityRenderer(ModEntityTypes.ELITE_PIGLIN_BRUTE_SERVANT.get(),
                (context) -> new PiglinServantRenderer(context,
                        ModEntityLayers.PIGLIN_SERVANT_LAYER,
                        ModEntityLayers.PIGLIN_SERVANT_INNER_ARMOR_LAYER,
                        ModEntityLayers.PIGLIN_SERVANT_OUTER_ARMOR_LAYER) {
                    @Override
                    protected void scale(Mob entity, PoseStack poseStack, float partialTicks) {
                        poseStack.scale(1.25F, 1.25F, 1.25F);
                        super.scale(entity, poseStack, partialTicks);
                    }
                });

        event.registerEntityRenderer(ModEntityTypes.STRONG_ZPIGLIN_BRUTE_SERVANT.get(),
                (context) -> new ZPiglinBruteRenderer(context));

        event.registerEntityRenderer(ModEntityTypes.ELITE_ZPIGLIN_BRUTE_SERVANT.get(),
                (context) -> new ZPiglinBruteRenderer(context) {
                    @Override
                    protected void scale(Mob entity, PoseStack poseStack, float partialTicks) {
                        poseStack.scale(1.25F, 1.25F, 1.25F);
                        super.scale(entity, poseStack, partialTicks);
                    }
                });

        event.registerEntityRenderer(ModEntityTypes.PIGLIN_HUNTER_SERVANT.get(),
                (context) -> new PiglinServantRenderer(context,
                        ModEntityLayers.PIGLIN_SERVANT_LAYER,
                        ModEntityLayers.PIGLIN_SERVANT_INNER_ARMOR_LAYER,
                        ModEntityLayers.PIGLIN_SERVANT_OUTER_ARMOR_LAYER));

        event.registerEntityRenderer(ModEntityTypes.STRONG_PIGLIN_HUNTER_SERVANT.get(),
                (context) -> new PiglinServantRenderer(context,
                        ModEntityLayers.PIGLIN_SERVANT_LAYER,
                        ModEntityLayers.PIGLIN_SERVANT_INNER_ARMOR_LAYER,
                        ModEntityLayers.PIGLIN_SERVANT_OUTER_ARMOR_LAYER));

        event.registerEntityRenderer(ModEntityTypes.ELITE_PIGLIN_HUNTER_SERVANT.get(),
                (context) -> new PiglinServantRenderer(context,
                        ModEntityLayers.PIGLIN_SERVANT_LAYER,
                        ModEntityLayers.PIGLIN_SERVANT_INNER_ARMOR_LAYER,
                        ModEntityLayers.PIGLIN_SERVANT_OUTER_ARMOR_LAYER) {
                    @Override
                    protected void scale(Mob entity, PoseStack poseStack, float partialTicks) {
                        poseStack.scale(1.25F, 1.25F, 1.25F);
                        super.scale(entity, poseStack, partialTicks);
                    }
                });

        event.registerEntityRenderer(ModEntityTypes.ZPIGLIN_HUNTER_SERVANT.get(),
                (context) -> new ZPiglinBruteServantRenderer(context));

        event.registerEntityRenderer(ModEntityTypes.STRONG_ZPIGLIN_HUNTER_SERVANT.get(),
                (context) -> new ZPiglinBruteServantRenderer(context));

        event.registerEntityRenderer(ModEntityTypes.ELITE_ZPIGLIN_HUNTER_SERVANT.get(),
                (context) -> new ZPiglinBruteServantRenderer(context) {
                    @Override
                    protected void scale(Mob entity, PoseStack poseStack, float partialTicks) {
                        poseStack.scale(1.25F, 1.25F, 1.25F);
                        super.scale(entity, poseStack, partialTicks);
                    }
                });

        event.registerEntityRenderer(ModEntityTypes.MIRED_SERVANT.get(), MiredRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.MIRED.get(), MiredRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.BOGGED_SERVANT.get(), BoggedServantRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.BOGGED.get(), BoggedRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.SWAMP_WOLF.get(), SwampWolfRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.BURNING_POTION.get(), BurningPotionRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.BURNING_GROUND.get(), BurningGroundRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.WITCH_BOMB.get(), WitchBombRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.ACID_FUNGUS.get(), AcidFungusRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.PITCHFORK.get(), PitchforkRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.WIND_CHARGE.get(),
                com.qiuyue.goetyominous.client.render.projectile.ServantWindChargeRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.SERVANT_WIND_CHARGE.get(),
                com.qiuyue.goetyominous.client.render.projectile.ServantWindChargeRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.BREEZE_WIND_CHARGE.get(),
                com.qiuyue.goetyominous.client.render.projectile.ServantWindChargeRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.BREEZE_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.BreezeServantRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.BREEZE.get(),
                com.qiuyue.goetyominous.client.render.BreezeRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.HURRICANE.get(),
                com.qiuyue.goetyominous.client.render.HurricaneRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.HURRICANE_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.HurricaneRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.HURRICANE_PUNCH.get(),
                com.qiuyue.goetyominous.client.render.HurricanePunchRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.HURRICANE_CYCLONE.get(),
                com.qiuyue.goetyominous.client.render.HurricaneCycloneRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.HURRICANE_CORE_SUMMON.get(),
                com.qiuyue.goetyominous.client.render.HurricaneCoreSummonRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.FEL_BOLT.get(), FelBoltRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.DREDEN.get(), DredenRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.DREDEN_SERVANT.get(), DredenServantRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.FROST_BALL.get(), FrostBallRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.URBHADHACH.get(), UrbhadhachRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.URBHADHACH_SERVANT.get(), UrbhadhachServantRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.ZEALOT.get(), ZealotRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.FANATIC.get(), FanaticRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.BELDAM.get(), BeldamRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.THUG.get(), ThugRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.CHANNELLER.get(), ChannellerRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.AGONY.get(), AgonyRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.RETURNED.get(), ReturnedRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.SCORCH.get(), ScorchRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.CRIMSON_SPIDER_SERVANT.get(), CrimsonSpiderServantRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.WARG.get(), WargRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.CERBERUS.get(), CerberusRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.ZFUNGUS_THROWER.get(), ZFungusThrowerRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.SUNKEN_NECROMANCER.get(), SunkenNecromancerRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.SUNKEN_NECROMANCER_SERVANT.get(), SunkenNecromancerServantRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.AXOLOTL_SERVANT.get(), AxolotlServantRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.LEAPKELP.get(), LeapkelpRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.HERESIARCH_SERVANT.get(), HeresiarchServantRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.DISCIPLE.get(), DiscipleRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.DISCIPLE_SERVANT.get(), DiscipleServantRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.PIGLIN_MERCHANT.get(), PiglinMerchantRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.STORM_NECROMANCER.get(),
                com.qiuyue.goetyominous.client.render.StormNecromancerRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.STORM_NECROMANCER_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.StormNecromancerRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.ARCH_GEOMANCER.get(), ArchGeomancerRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.IMPACT_BLOCK.get(),
                com.qiuyue.goetyominous.client.render.projectile.ImpactBlockRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.TREMOR_BLOCK.get(),
                com.qiuyue.goetyominous.client.render.projectile.TremorBlockRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ModEntityTypes.POISON_BALL.get(),
                com.qiuyue.goetyominous.client.render.projectile.RenderPoisonBall::new);
    }

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        WolfRenderer wolfRenderer = event.getRenderer(EntityType.WOLF);
        if (wolfRenderer != null) {
            wolfRenderer.addLayer(new CursedWolfArmorLayer(wolfRenderer, event.getEntityModels()));
        }
    }
}
