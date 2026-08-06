package com.qiuyue.goetyominous.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.*;
import com.qiuyue.goetyominous.client.render.model.equipment.BoneCudgelModel;
import com.qiuyue.goetyominous.client.render.model.mm.MutantHoglinServantModel;
import com.qiuyue.goetyominous.client.render.model.projectile.AcidFungus;
import com.qiuyue.goetyominous.client.render.model.projectile.PitchforkModel;
import com.qiuyue.goetyominous.client.render.projectile.AcidFungusRenderer;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.compat.mod.*;
import com.qiuyue.goetyominous.client.render.projectile.PitchforkRenderer;
import com.qiuyue.goetyominous.client.render.projectile.BurningPotionRenderer;
import com.qiuyue.goetyominous.client.render.projectile.WitchBombRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 客户端模型层注册类
 * 仅在客户端加载，负责注册实体的模型层定义和实体渲染器
 * 使用@Mod.EventBusSubscriber 注解自动注册到模组事件总线
 *
 * bus = Mod.EventBusSubscriber.Bus.MOD - 监听模组事件总线 (而非 Forge 通用事件总线)
 * value = Dist.CLIENT - 仅在客户端生效
 */
@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModModelLayers {

    /**
     * 注册模型层定义方法
     * 在模型层注册事件中被调用，用于将模型层位置与模型创建方法关联
     * Minecraft 使用此映射来加载对应的模型数据
     *
     * @param event 模型层注册事件对象
     */
    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        // 注册征服者仆从的模型层，绑定到 ConquillagerServantModel 的 createBodyLayer 方法
        event.registerLayerDefinition(ModEntityLayers.CONQUILLAGER_SERVANT_LAYER,
                ConquillagerServantModel::createBodyLayer);
        // 注册巡查官仆从的模型层，绑定到 InquillagerServantModel 的 createBodyLayer 方法
        event.registerLayerDefinition(ModEntityLayers.INQUILLAGER_SERVANT_LAYER,
                InquillagerServantModel::createBodyLayer);

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

        if (AlexMobsCompat.isAlexMobsLoaded()) {
            // 徒弟：在这里添加 AlexMobs 联动仆从的模型层注册
        }
    }

    /**
     * 注册实体渲染器方法
     * 在实体渲染器注册事件中被调用，用于为每个实体类型指定对应的渲染器
     * 渲染器负责将 3D 模型渲染到游戏中
     *
     * @param event 实体渲染器注册事件对象
     */
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // 注册征服者仆从的渲染器，使用 ConquillagerServantRenderer
        event.registerEntityRenderer(ModEntityTypes.CONQUILLAGER_SERVANT.get(),
                ConquillagerServantRenderer::new);
        // 注册巡查官仆从的渲染器，使用 InquillagerServantRenderer
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

        if (AlexMobsCompat.isAlexMobsLoaded()) {
            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.MURMUR_SERVANT.get(),
                    com.qiuyue.goetyominous.client.render.am.RenderMurmurServantBody::new);
            event.registerEntityRenderer(
                    com.qiuyue.goetyominous.common.init.am.AmEntityRegistry.MURMUR_SERVANT_HEAD.get(),
                    com.qiuyue.goetyominous.client.render.am.RenderMurmurServantHead::new);

            // 在这里添加 AlexMobs 联动仆从的渲染器注册
        }
    }
}
