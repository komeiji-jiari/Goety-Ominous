package com.qiuyue.goetyominous.common.init.sar;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.sar.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SarEntityRegistry {

    private static final DeferredRegister<EntityType<?>> SAR_ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, GoetyOminous.MOD_ID);

    public static final RegistryObject<EntityType<CreepieServant>> CREEPIE_SERVANT =
            SAR_ENTITIES.register("creepie_servant",
                    () -> EntityType.Builder.of(CreepieServant::new, MobCategory.CREATURE)
                            .sized(0.5F, 0.90F)
                            .clientTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":creepie_servant"));

    public static final RegistryObject<EntityType<SporeCloud>> SPORE_CLOUD =
            SAR_ENTITIES.register("spore_cloud",
                    () -> EntityType.Builder.<SporeCloud>of(SporeCloud::new, MobCategory.MISC)
                            .fireImmune()
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build(GoetyOminous.MOD_ID + ":spore_cloud"));

    public static final RegistryObject<EntityType<GrieferServant>> GRIEFER_SERVANT =
            SAR_ENTITIES.register("griefer_servant",
                    () -> EntityType.Builder.of(GrieferServant::new, MobCategory.MISC)
                            .sized(0.6F, 1.99F)
                            .clientTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":griefer_servant"));

    public static final RegistryObject<EntityType<ExecutionerServant>> EXECUTIONER_SERVANT =
            SAR_ENTITIES.register("executioner_servant",
                    () -> EntityType.Builder.of(ExecutionerServant::new, MobCategory.MISC)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":executioner_servant"));

    public static final RegistryObject<EntityType<SkeletonVillagerServant>> SKELETON_VILLAGER_SERVANT =
            SAR_ENTITIES.register("skeleton_villager_servant",
                    () -> EntityType.Builder.of(SkeletonVillagerServant::new, MobCategory.MISC)
                            .sized(0.6F, 1.99F)
                            .clientTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":skeleton_villager_servant"));

    public static final RegistryObject<EntityType<ConfusionBolt>> CONFUSION_BOLT =
            SAR_ENTITIES.register("confusion_bolt",
                    () -> EntityType.Builder.<ConfusionBolt>of(ConfusionBolt::new, MobCategory.MISC)
                            .fireImmune()
                            .sized(1.0F, 1.0F)
                            .build(GoetyOminous.MOD_ID + ":confusion_bolt"));

    public static final RegistryObject<EntityType<RunePrison>> RUNE_PRISON =
            SAR_ENTITIES.register("rune_prison",
                    () -> EntityType.Builder.<RunePrison>of(RunePrison::new, MobCategory.MISC)
                            .fireImmune()
                            .sized(1.35F, 0.7F)
                            .build(GoetyOminous.MOD_ID + ":rune_prison"));

    public static final RegistryObject<EntityType<TricksterServant>> TRICKSTER_SERVANT =
            SAR_ENTITIES.register("trickster_servant",
                    () -> EntityType.Builder.of(TricksterServant::new, MobCategory.MISC)
                            .sized(0.6F, 1.89F)
                            .clientTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":trickster_servant"));


    public static void register(IEventBus modEventBus) {
        SAR_ENTITIES.register(modEventBus);
    }
}