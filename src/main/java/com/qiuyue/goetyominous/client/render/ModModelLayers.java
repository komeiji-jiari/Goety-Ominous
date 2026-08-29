package com.qiuyue.goetyominous.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.curios.CroneRobeRenderer;
import com.qiuyue.goetyominous.client.render.curios.RaycatAmuletRenderer;
import com.qiuyue.goetyominous.client.render.layer.CursedWolfArmorLayer;
import com.qiuyue.goetyominous.client.render.model.*;
import com.qiuyue.goetyominous.client.render.model.curios.CroneRobeModel;
import com.qiuyue.goetyominous.client.render.model.equipment.BoneCudgelModel;
import com.qiuyue.goetyominous.client.render.model.mm.MutantHoglinServantModel;
import com.qiuyue.goetyominous.client.render.model.of.RamblerServantModel;
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

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
                event.registerLayerDefinition(ModEntityLayers.CONQUILLAGER_SERVANT_LAYER,
                ConquillagerServantModel::createBodyLayer);
                event.registerLayerDefinition(ModEntityLayers.INQUILLAGER_SERVANT_LAYER,
                InquillagerServantModel::createBodyLayer);

        event.registerLayerDefinition(CroneRobeModel.LAYER_LOCATION, CroneRobeModel::createBodyLayer);

        event.registerLayerDefinition(BoneCudgelModel.LAYER_LOCATION, BoneCudgelModel::createBodyLayer);

        if (IllageAndSpillageCompat.isIllageAndSpillageLoaded()) {
            event.registerLayerDefinition(ModEntityLayers.TWITTOLLAGER_SERVANT_LAYER,
                    TwittollagerServantModel::createBodyLayer);

            event.registerLayerDefinition(ModEntityLayers.ABSORBER_SERVANT_LAYER,
                    AbsorberServantModel::createBodyLayer);

            event.registerLayerDefinition(ModEntityLayers.CRASHAGER_SERVANT_LAYER,
                    CrashagerServantModel::createBodyLayer);

            event.registerLayerDefinition(ModEntityLayers.KABOOMER_SERVANT_LAYER,
                    KaboomerServantModel::createBodyLayer);

            event.registerLayerDefinition(ModEntityLayers.ILLASHOOTER_SERVANT_LAYER,
                    CrashagerServantModel::createBodyLayer);

            event.registerLayerDefinition(ModEntityLayers.DISPENSER_SERVANT_LAYER,
                    KaboomerServantModel::createBodyLayer);

            event.registerLayerDefinition(ModEntityLayers.FAKEMAGISPELLER_LAYER,
                    FakeMagispellerModel::createBodyLayer);

            event.registerLayerDefinition(ModEntityLayers.MAGIHEAL_LAYER,
                    MagiHealModel::createBodyLayer);

            event.registerLayerDefinition(ModEntityLayers.MAGISPELLER_SERVANT_LAYER,
                    MagispellerServantModel::createBodyLayer);
        }

        event.registerLayerDefinition(ModEntityLayers.ACID_FUNGUS_LAYER,
                AcidFungus::createBodyLayer);

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

        event.registerLayerDefinition(ModEntityLayers.CURSED_BLACK_BEAST_ARMOR_LAYER,
                CursedBlackBeastArmorModel::createBodyLayer);

        event.registerLayerDefinition(CroneRobeModel.LAYER_LOCATION, CroneRobeModel::createBodyLayer);
        CroneRobeRenderer.register();
        RaycatAmuletRenderer.register();

        event.registerLayerDefinition(ModEntityLayers.FUNGUS_PACK_LAYER, FungusPackModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.PITCHFORK_LAYER, PitchforkModel::createBodyLayer);

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

        if (SavageRavageCompat.isSavageRavageLoaded()) {
            event.registerLayerDefinition(ModEntityLayers.CREEPIE_SERVANT_LAYER,
                    () -> com.qiuyue.goetyominous.client.render.model.sar.CreepieServantModel.createBodyLayer(
                            net.minecraft.client.model.geom.builders.CubeDeformation.NONE));

            event.registerLayerDefinition(ModEntityLayers.GRIEFER_SERVANT_LAYER,
                    com.qiuyue.goetyominous.client.render.model.sar.GrieferServantModel::createBodyLayer);

            event.registerLayerDefinition(ModEntityLayers.VILLAGER_INNER_ARMOR_LAYER,
                    com.qiuyue.goetyominous.client.render.model.sar.VillagerArmorModel::createInnerArmorLayer);

            event.registerLayerDefinition(ModEntityLayers.VILLAGER_OUTER_ARMOR_LAYER,
                    com.qiuyue.goetyominous.client.render.model.sar.VillagerArmorModel::createOuterArmorLayer);

            event.registerLayerDefinition(ModEntityLayers.EXECUTIONER_SERVANT_LAYER,
                    com.qiuyue.goetyominous.client.render.model.sar.ExecutionerServantModel::createBodyLayer);

            event.registerLayerDefinition(ModEntityLayers.SKELETON_VILLAGER_SERVANT_LAYER,
                    com.qiuyue.goetyominous.client.render.model.sar.SkeletonVillagerServantModel::createBodyLayer);

            event.registerLayerDefinition(ModEntityLayers.RUNE_PRISON_LAYER,
                    com.qiuyue.goetyominous.client.render.model.sar.RunePrisonModel::createBodyLayer);

            event.registerLayerDefinition(ModEntityLayers.TRICKSTER_SERVANT_LAYER,
                    com.qiuyue.goetyominous.client.render.model.sar.TricksterServantModel::createBodyLayer);
        }

        if (UpgradeAquaticCompat.isUpgradeAquaticLoaded()) {
            event.registerLayerDefinition(ModEntityLayers.THRASHER_SERVANT_LAYER,
                    com.qiuyue.goetyominous.client.render.model.ua.ThrasherServantModel::createBodyLayer);

            event.registerLayerDefinition(ModEntityLayers.GREAT_THRASHER_SERVANT_LAYER,
                    com.qiuyue.goetyominous.client.render.model.ua.ThrasherServantModel::createBodyLayer);

            event.registerLayerDefinition(ModEntityLayers.FLARE_SERVANT_LAYER,
                    com.qiuyue.goetyominous.client.render.model.ua.FlareServantModel::createBodyLayer);
        }


        if (LegendaryMonstersCompat.isLegendaryMonstersLoaded()) {
            event.registerLayerDefinition(ModEntityLayers.OVERGROWN_COLOSSUS_SERVANT_LAYER,
                    com.qiuyue.goetyominous.client.render.model.lm.OvergrownColossusServantModel::createBodyLayer);
        }

        if (OpposingForceCompat.isOpposingForceLoaded()) {
            event.registerLayerDefinition(ModEntityLayers.RAMBLER_SERVANT_LAYER,
                    RamblerServantModel::createBodyLayer);

            event.registerLayerDefinition(ModEntityLayers.DICER_SERVANT_LAYER,
                    com.qiuyue.goetyominous.client.render.model.of.DicerServantModel::createBodyLayer);

            event.registerLayerDefinition(ModEntityLayers.VOLT_SERVANT_LAYER,
                    com.qiuyue.goetyominous.client.render.model.of.VoltServantModel::createBodyLayer);

            event.registerLayerDefinition(ModEntityLayers.TREMBLER_SERVANT_LAYER,
                    com.qiuyue.goetyominous.client.render.model.of.TremblerServantModel::createBodyLayer);
        }

        if (com.qiuyue.goetyominous.compat.mod.MutantMoreCompat.isMutantMoreLoaded()) {
            event.registerLayerDefinition(com.qiuyue.goetyominous.client.render.model.mm.MutantWitherSkeletonServantModel.MAIN,
                    com.qiuyue.goetyominous.client.render.model.mm.MutantWitherSkeletonServantModel::createBodyLayer);

            event.registerLayerDefinition(com.qiuyue.goetyominous.client.render.model.mm.MutantWitherSkeletonServantModel.INNER_ARMOUR,
                    com.alexander.mutantmore.advanced_animation_utils.armour_utils.AdvancedArmourLayer::createInnerArmourLayer);

            event.registerLayerDefinition(com.qiuyue.goetyominous.client.render.model.mm.MutantWitherSkeletonServantModel.OUTER_ARMOUR,
                    com.alexander.mutantmore.advanced_animation_utils.armour_utils.AdvancedArmourLayer::createOuterArmourLayer);

            event.registerLayerDefinition(com.qiuyue.goetyominous.client.init.ModEntityLayers.WITHER_SLASH_LAYER,
                    com.qiuyue.goetyominous.client.render.model.mm.WitherSlashModel::createBodyLayer);

            event.registerLayerDefinition(ModEntityLayers.MUTANT_HOGLIN_SERVANT_LAYER,
                    MutantHoglinServantModel::createBodyLayer);
        }

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
                (context) -> new ZPiglinBruteServantRenderer(context));

        event.registerEntityRenderer(ModEntityTypes.ELITE_ZPIGLIN_BRUTE_SERVANT.get(),
                (context) -> new ZPiglinBruteServantRenderer(context) {
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

        if (IllageAndSpillageCompat.isIllageAndSpillageLoaded()) {
            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.compat.ias.IasEntityRegistry.TWITTOLLAGER_SERVANT.get(),
                    TwittollagerServantRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.compat.ias.IasEntityRegistry.ABSORBER_SERVANT.get(),
                    AbsorberServantRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.compat.ias.IasEntityRegistry.CRASHAGER_SERVANT.get(),
                    CrashagerServantRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.compat.ias.IasEntityRegistry.KABOOMER_SERVANT.get(),
                    KaboomerServantRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.compat.ias.IasEntityRegistry.ILLASHOOTER_SERVANT.get(),
                    IllashooterServantRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.compat.ias.IasEntityRegistry.DISPENSER_SERVANT.get(),
                    DispenserServantRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.compat.ias.IasEntityRegistry.FAKEMAGISPELLER.get(),
                    FakeMagispellerRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.compat.ias.IasEntityRegistry.MAGIHEAL.get(),
                    MagiHealRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.compat.ias.IasEntityRegistry.MAGIARROW.get(),
                    MagiArrowRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.compat.ias.IasEntityRegistry.MAGIFIREBALL.get(),
                    MagiFireballRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.compat.ias.IasEntityRegistry.MAGISPELLER_SERVANT.get(),
                    MagispellerServantRenderer::new);
        }

        event.registerEntityRenderer(ModEntityTypes.BURNING_POTION.get(), BurningPotionRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.BURNING_GROUND.get(), BurningGroundRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.WITCH_BOMB.get(), WitchBombRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.ACID_FUNGUS.get(), AcidFungusRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.PITCHFORK.get(), PitchforkRenderer::new);

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

        event.registerEntityRenderer(ModEntityTypes.ZFUNGUS_THROWER.get(), ZFungusThrowerRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.SUNKEN_NECROMANCER.get(), SunkenNecromancerRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.SUNKEN_NECROMANCER_SERVANT.get(), SunkenNecromancerServantRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.AXOLOTL_SERVANT.get(), AxolotlServantRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.HERESIARCH_SERVANT.get(), HeresiarchServantRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.DISCIPLE.get(), DiscipleRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.DISCIPLE_SERVANT.get(), DiscipleServantRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.PIGLIN_MERCHANT.get(), PiglinMerchantRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.STORM_NECROMANCER.get(),
                com.qiuyue.goetyominous.client.render.StormNecromancerRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.STORM_NECROMANCER_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.StormNecromancerRenderer::new);

        if (SavageRavageCompat.isSavageRavageLoaded()) {
            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.sar.SarEntityRegistry.CREEPIE_SERVANT.get(),
                    com.qiuyue.goetyominous.client.render.sar.CreepieServantRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.sar.SarEntityRegistry.SPORE_CLOUD.get(),
                    com.qiuyue.goetyominous.client.render.sar.NoModelRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.sar.SarEntityRegistry.GRIEFER_SERVANT.get(),
                    com.qiuyue.goetyominous.client.render.sar.GrieferServantRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.sar.SarEntityRegistry.EXECUTIONER_SERVANT.get(),
                    com.qiuyue.goetyominous.client.render.sar.ExecutionerServantRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.sar.SarEntityRegistry.SKELETON_VILLAGER_SERVANT.get(),
                    com.qiuyue.goetyominous.client.render.sar.SkeletonVillagerServantRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.sar.SarEntityRegistry.RUNE_PRISON.get(),
                    com.qiuyue.goetyominous.client.render.sar.RunePrisonRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.sar.SarEntityRegistry.CONFUSION_BOLT.get(),
                    com.qiuyue.goetyominous.client.render.sar.NoModelRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.sar.SarEntityRegistry.TRICKSTER_SERVANT.get(),
                    com.qiuyue.goetyominous.client.render.sar.TricksterServantRenderer::new);
        }

        if (UpgradeAquaticCompat.isUpgradeAquaticLoaded()) {
            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.ua.UaEntityRegistry.THRASHER_SERVANT.get(),
                    com.qiuyue.goetyominous.client.render.ua.ThrasherServantRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.ua.UaEntityRegistry.GREAT_THRASHER_SERVANT.get(),
                    com.qiuyue.goetyominous.client.render.ua.ThrasherServantRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.ua.UaEntityRegistry.FLARE_SERVANT.get(),
                    com.qiuyue.goetyominous.client.render.ua.FlareServantRenderer::new);

        }

        if (LegendaryMonstersCompat.isLegendaryMonstersLoaded()) {
            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.OVERGROWN_COLOSSUS_SERVANT.get(),
                    com.qiuyue.goetyominous.client.render.lm.OvergrownColossusServantRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.POISONOUS_SHOCKWAVE.get(),
                    EmptyRenderer::new);
        }

        if (com.qiuyue.goetyominous.compat.mod.MutantMoreCompat.isMutantMoreLoaded()) {
            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry.MUTANT_WITHER_SKELETON_SERVANT.get(),
                    com.qiuyue.goetyominous.client.render.MutantWitherSkeletonServantRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry.WITHER_SLASH.get(),
                    com.qiuyue.goetyominous.client.render.WitherSlashRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry.MUTANT_HOGLIN_SERVANT.get(),
                    MutantHoglinServantRenderer::new);
        }

        event.registerEntityRenderer(ModEntityTypes.ARCH_GEOMANCER.get(), ArchGeomancerRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.IMPACT_BLOCK.get(),
                com.qiuyue.goetyominous.client.render.projectile.ImpactBlockRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.TREMOR_BLOCK.get(),
                com.qiuyue.goetyominous.client.render.projectile.TremorBlockRenderer::new);

        if (OpposingForceCompat.isOpposingForceLoaded()) {
            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.of.OfEntityRegistry.RAMBLER_SERVANT.get(),
                    com.qiuyue.goetyominous.client.render.of.RamblerServantRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.of.OfEntityRegistry.DICER_SERVANT.get(),
                    com.qiuyue.goetyominous.client.render.of.DicerServantRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.of.OfEntityRegistry.VOLT_SERVANT.get(),
                    com.qiuyue.goetyominous.client.render.of.VoltServantRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.of.OfEntityRegistry.TREMBLER_SERVANT.get(),
                    com.qiuyue.goetyominous.client.render.of.TremblerServantRenderer::new);
        }

        if (AlexMobsCompat.isAlexMobsLoaded()) {
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
                    com.qiuyue.goetyominous.client.render.projectile.RenderMosquitoServantSpit::new);

            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.SERVANT_SAND_SHOT.get(),
                    com.qiuyue.goetyominous.client.render.projectile.RenderServantSandShot::new);

            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.SERVANT_HEMOLYMPH.get(),
                    com.qiuyue.goetyominous.client.render.projectile.RenderServantHemolymph::new);

            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.FARSEER_SERVANT.get(),
                    com.qiuyue.goetyominous.client.render.am.RenderFarseerServant::new);

            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.TUSKLIN_SERVANT.get(),
                    com.qiuyue.goetyominous.client.render.am.RenderTusklinServant::new);

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
                    com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.SERVANT_CENTIPEDE_HEAD.get(),
                    com.qiuyue.goetyominous.client.render.am.RenderServantCentipedeHead::new);

            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.SERVANT_CENTIPEDE_BODY.get(),
                    com.qiuyue.goetyominous.client.render.am.RenderServantCentipedeBody::new);

            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.SERVANT_CENTIPEDE_TAIL.get(),
                    com.qiuyue.goetyominous.client.render.am.RenderServantCentipedeTail::new);

        }

        if (AlexCavesCompat.isAlexCavesLoaded()) {
            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.GROTTOCERATOPS_SERVANT.get(),
                    com.qiuyue.goetyominous.client.render.ac.RenderGrottoceratopsServant::new);
            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.TREMORSAURUS_SERVANT.get(),
                    com.qiuyue.goetyominous.client.render.ac.RenderTremorsaurusServant::new);
            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.VALLUMRAPTOR_SERVANT.get(),
                    com.qiuyue.goetyominous.client.render.ac.RenderVallumraptorServant::new);
            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.NUCLEEPER_SERVANT.get(),
                    com.qiuyue.goetyominous.client.render.ac.RenderNucleeperServant::new);
            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.BRAINIAC_SERVANT.get(),
                    com.qiuyue.goetyominous.client.render.ac.RenderBrainiacServant::new);
            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.GAMMAROACH_SERVANT.get(),
                    com.qiuyue.goetyominous.client.render.ac.RenderGammaroachServant::new);
            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.MINE_GUARDIAN_SERVANT.get(),
                    com.qiuyue.goetyominous.client.render.ac.RenderMineGuardianServant::new);
            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.HULLBREAKER_SERVANT.get(),
                    com.qiuyue.goetyominous.client.render.ac.RenderHullbreakerServant::new);
            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.DEEP_ONE_SERVANT.get(),
                    com.qiuyue.goetyominous.client.render.ac.RenderDeepOneServant::new);
            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.DEEP_ONE_KNIGHT_SERVANT.get(),
                    com.qiuyue.goetyominous.client.render.ac.RenderDeepOneKnightServant::new);
            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.DEEP_ONE_MAGE_SERVANT.get(),
                    com.qiuyue.goetyominous.client.render.ac.RenderDeepOneMageServant::new);
            // 奥托兰长矛水浪弹射物:专用渲染器,贴图用 AC 原版 Ortholance 的水浪(与玩家发射外观一致)
            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.DEEP_ONE_SERVANT_WAVE.get(),
                    com.qiuyue.goetyominous.client.render.ac.RenderDeepOneServantWave::new);
            // 法师水浪:忠实移植 AC 原版 WaveEntity,渲染器与骑士水浪共用同套模型贴图
            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.DEEP_ONE_MAGE_SERVANT_WAVE.get(),
                    com.qiuyue.goetyominous.client.render.ac.RenderDeepOneMageServantWave::new);
            // 法师水弹:忠实移植 AC 原版 WaterBoltEntity,模型/贴图/尾迹渲染与 AC 原版一致
            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.DEEP_ONE_MAGE_SERVANT_WATER_BOLT.get(),
                    com.qiuyue.goetyominous.client.render.ac.RenderDeepOneMageServantWaterBolt::new);
            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.TREMORZILLA_SERVANT.get(),
                    com.qiuyue.goetyominous.client.render.ac.RenderTremorzillaServant::new);
        }
    }

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        WolfRenderer wolfRenderer = event.getRenderer(EntityType.WOLF);
        if (wolfRenderer != null) {
            wolfRenderer.addLayer(new CursedWolfArmorLayer(wolfRenderer, event.getEntityModels()));
        }
    }
}
