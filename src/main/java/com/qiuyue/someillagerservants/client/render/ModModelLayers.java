package com.qiuyue.someillagerservants.client.render;

import com.qiuyue.someillagerservants.SomeIllagerServants;
import com.qiuyue.someillagerservants.client.init.ModEntityLayers;
import com.qiuyue.someillagerservants.client.render.model.*;
import com.qiuyue.someillagerservants.client.render.model.mm.MutantHoglinServantModel;
import com.qiuyue.someillagerservants.common.entities.ally.mobs.AxolotlServant;
import com.qiuyue.someillagerservants.common.init.ModEntityTypes;
import com.qiuyue.someillagerservants.compat.mod.IllageAndSpillageCompat;
import com.qiuyue.someillagerservants.compat.mod.LegendaryMonstersCompat;
import com.qiuyue.someillagerservants.compat.mod.SavageRavageCompat;
import com.qiuyue.someillagerservants.compat.mod.UpgradeAquaticCompat;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
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
@Mod.EventBusSubscriber(modid = SomeIllagerServants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
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

        event.registerLayerDefinition(ModEntityLayers.SUNKEN_NECROMANCER_LAYER,
                SunkenNecromancerModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.AXOLOTL_SERVANT_LAYER,
                AxolotlServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.HERESIARCH_SERVANT_LAYER,
                HeresiarchServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.ACOLYTE_LAYER,
                AcolyteModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.ACOLYTE_SERVANT_LAYER,
                AcolyteModel::createBodyLayer);


        if (SavageRavageCompat.isSavageRavageLoaded()) {
            event.registerLayerDefinition(ModEntityLayers.CREEPIE_SERVANT_LAYER,
                    () -> com.qiuyue.someillagerservants.client.render.model.sar.CreepieServantModel.createBodyLayer(
                            net.minecraft.client.model.geom.builders.CubeDeformation.NONE));

            event.registerLayerDefinition(ModEntityLayers.GRIEFER_SERVANT_LAYER,
                    com.qiuyue.someillagerservants.client.render.model.sar.GrieferServantModel::createBodyLayer);

            event.registerLayerDefinition(ModEntityLayers.VILLAGER_INNER_ARMOR_LAYER,
                    com.qiuyue.someillagerservants.client.render.model.sar.VillagerArmorModel::createInnerArmorLayer);

            event.registerLayerDefinition(ModEntityLayers.VILLAGER_OUTER_ARMOR_LAYER,
                    com.qiuyue.someillagerservants.client.render.model.sar.VillagerArmorModel::createOuterArmorLayer);

            event.registerLayerDefinition(ModEntityLayers.EXECUTIONER_SERVANT_LAYER,
                    com.qiuyue.someillagerservants.client.render.model.sar.ExecutionerServantModel::createBodyLayer);

            event.registerLayerDefinition(ModEntityLayers.SKELETON_VILLAGER_SERVANT_LAYER,
                    com.qiuyue.someillagerservants.client.render.model.sar.SkeletonVillagerServantModel::createBodyLayer);

            event.registerLayerDefinition(ModEntityLayers.RUNE_PRISON_LAYER,
                    com.qiuyue.someillagerservants.client.render.model.sar.RunePrisonModel::createBodyLayer);

            event.registerLayerDefinition(ModEntityLayers.TRICKSTER_SERVANT_LAYER,
                    com.qiuyue.someillagerservants.client.render.model.sar.TricksterServantModel::createBodyLayer);
        }

        if (UpgradeAquaticCompat.isUpgradeAquaticLoaded()) {
            event.registerLayerDefinition(ModEntityLayers.THRASHER_SERVANT_LAYER,
                    com.qiuyue.someillagerservants.client.render.model.ua.ThrasherServantModel::createBodyLayer);

            event.registerLayerDefinition(ModEntityLayers.GREAT_THRASHER_SERVANT_LAYER,
                    com.qiuyue.someillagerservants.client.render.model.ua.ThrasherServantModel::createBodyLayer);

            event.registerLayerDefinition(ModEntityLayers.FLARE_SERVANT_LAYER,
                    com.qiuyue.someillagerservants.client.render.model.ua.FlareServantModel::createBodyLayer);
        }


        if (LegendaryMonstersCompat.isLegendaryMonstersLoaded()) {
            event.registerLayerDefinition(ModEntityLayers.OVERGROWN_COLOSSUS_SERVANT_LAYER,
                    com.qiuyue.someillagerservants.client.render.model.lm.OvergrownColossusServantModel::createBodyLayer);
        }

        if (com.qiuyue.someillagerservants.compat.mod.MutantMoreCompat.isMutantMoreLoaded()) {
            event.registerLayerDefinition(com.qiuyue.someillagerservants.client.render.model.mm.MutantWitherSkeletonServantModel.MAIN,
                    com.qiuyue.someillagerservants.client.render.model.mm.MutantWitherSkeletonServantModel::createBodyLayer);

            event.registerLayerDefinition(com.qiuyue.someillagerservants.client.render.model.mm.MutantWitherSkeletonServantModel.INNER_ARMOUR,
                    com.alexander.mutantmore.advanced_animation_utils.armour_utils.AdvancedArmourLayer::createInnerArmourLayer);

            event.registerLayerDefinition(com.qiuyue.someillagerservants.client.render.model.mm.MutantWitherSkeletonServantModel.OUTER_ARMOUR,
                    com.alexander.mutantmore.advanced_animation_utils.armour_utils.AdvancedArmourLayer::createOuterArmourLayer);

            event.registerLayerDefinition(com.qiuyue.someillagerservants.client.init.ModEntityLayers.WITHER_SLASH_LAYER,
                    com.qiuyue.someillagerservants.client.render.model.mm.WitherSlashModel::createBodyLayer);

            event.registerLayerDefinition(ModEntityLayers.MUTANT_HOGLIN_SERVANT_LAYER,
                    MutantHoglinServantModel::createBodyLayer);
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

        if (IllageAndSpillageCompat.isIllageAndSpillageLoaded()) {
            event.registerEntityRenderer(
                    com.qiuyue.someillagerservants.compat.ias.IasEntityRegistry.TWITTOLLAGER_SERVANT.get(),
                    TwittollagerServantRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.someillagerservants.compat.ias.IasEntityRegistry.ABSORBER_SERVANT.get(),
                    AbsorberServantRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.someillagerservants.compat.ias.IasEntityRegistry.CRASHAGER_SERVANT.get(),
                    CrashagerServantRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.someillagerservants.compat.ias.IasEntityRegistry.KABOOMER_SERVANT.get(),
                    KaboomerServantRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.someillagerservants.compat.ias.IasEntityRegistry.ILLASHOOTER_SERVANT.get(),
                    IllashooterServantRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.someillagerservants.compat.ias.IasEntityRegistry.DISPENSER_SERVANT.get(),
                    DispenserServantRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.someillagerservants.compat.ias.IasEntityRegistry.FAKEMAGISPELLER.get(),
                    FakeMagispellerRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.someillagerservants.compat.ias.IasEntityRegistry.MAGIHEAL.get(),
                    MagiHealRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.someillagerservants.compat.ias.IasEntityRegistry.MAGIARROW.get(),
                    MagiArrowRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.someillagerservants.compat.ias.IasEntityRegistry.MAGIFIREBALL.get(),
                    MagiFireballRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.someillagerservants.compat.ias.IasEntityRegistry.MAGISPELLER_SERVANT.get(),
                    MagispellerServantRenderer::new);
        }

        event.registerEntityRenderer(ModEntityTypes.SUNKEN_NECROMANCER.get(), SunkenNecromancerRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.SUNKEN_NECROMANCER_SERVANT.get(), SunkenNecromancerServantRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.AXOLOTL_SERVANT.get(), AxolotlServantRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.HERESIARCH_SERVANT.get(), HeresiarchServantRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.ACOLYTE.get(), AcolyteRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.ACOLYTE_SERVANT.get(), AcolyteServantRenderer::new);


        if (SavageRavageCompat.isSavageRavageLoaded()) {
            event.registerEntityRenderer(
                    com.qiuyue.someillagerservants.common.init.sar.SarEntityRegistry.CREEPIE_SERVANT.get(),
                    com.qiuyue.someillagerservants.client.render.sar.CreepieServantRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.someillagerservants.common.init.sar.SarEntityRegistry.SPORE_CLOUD.get(),
                    com.qiuyue.someillagerservants.client.render.sar.NoModelRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.someillagerservants.common.init.sar.SarEntityRegistry.GRIEFER_SERVANT.get(),
                    com.qiuyue.someillagerservants.client.render.sar.GrieferServantRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.someillagerservants.common.init.sar.SarEntityRegistry.EXECUTIONER_SERVANT.get(),
                    com.qiuyue.someillagerservants.client.render.sar.ExecutionerServantRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.someillagerservants.common.init.sar.SarEntityRegistry.SKELETON_VILLAGER_SERVANT.get(),
                    com.qiuyue.someillagerservants.client.render.sar.SkeletonVillagerServantRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.someillagerservants.common.init.sar.SarEntityRegistry.RUNE_PRISON.get(),
                    com.qiuyue.someillagerservants.client.render.sar.RunePrisonRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.someillagerservants.common.init.sar.SarEntityRegistry.CONFUSION_BOLT.get(),
                    com.qiuyue.someillagerservants.client.render.sar.NoModelRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.someillagerservants.common.init.sar.SarEntityRegistry.TRICKSTER_SERVANT.get(),
                    com.qiuyue.someillagerservants.client.render.sar.TricksterServantRenderer::new);
        }

        if (UpgradeAquaticCompat.isUpgradeAquaticLoaded()) {
            event.registerEntityRenderer(
                    com.qiuyue.someillagerservants.common.init.ua.UaEntityRegistry.THRASHER_SERVANT.get(),
                    com.qiuyue.someillagerservants.client.render.ua.ThrasherServantRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.someillagerservants.common.init.ua.UaEntityRegistry.GREAT_THRASHER_SERVANT.get(),
                    com.qiuyue.someillagerservants.client.render.ua.ThrasherServantRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.someillagerservants.common.init.ua.UaEntityRegistry.FLARE_SERVANT.get(),
                    com.qiuyue.someillagerservants.client.render.ua.FlareServantRenderer::new);

        }

        if (LegendaryMonstersCompat.isLegendaryMonstersLoaded()) {
            event.registerEntityRenderer(
                    com.qiuyue.someillagerservants.common.init.lm.LmEntityRegistry.OVERGROWN_COLOSSUS_SERVANT.get(),
                    com.qiuyue.someillagerservants.client.render.lm.OvergrownColossusServantRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.someillagerservants.common.init.lm.LmEntityRegistry.POISONOUS_SHOCKWAVE.get(),
                    EmptyRenderer::new);
        }

        if (com.qiuyue.someillagerservants.compat.mod.MutantMoreCompat.isMutantMoreLoaded()) {
            event.registerEntityRenderer(
                    com.qiuyue.someillagerservants.common.init.mm.MmEntityRegistry.MUTANT_WITHER_SKELETON_SERVANT.get(),
                    com.qiuyue.someillagerservants.client.render.MutantWitherSkeletonServantRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.someillagerservants.common.init.mm.MmEntityRegistry.WITHER_SLASH.get(),
                    com.qiuyue.someillagerservants.client.render.WitherSlashRenderer::new);

            event.registerEntityRenderer(
                    com.qiuyue.someillagerservants.common.init.mm.MmEntityRegistry.MUTANT_HOGLIN_SERVANT.get(),
                    MutantHoglinServantRenderer::new);
        }
    }
}
