package com.qiuyue.goetyominous.common.init.pp;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.pp.PiglinAlchemistServant;
import com.qiuyue.goetyominous.common.entities.ally.pp.PiglinTravelerServant;
import com.qiuyue.goetyominous.common.entities.ally.pp.ZPiglinAlchemistServant;
import com.qiuyue.goetyominous.common.entities.ally.pp.ZPiglinTravelerServant;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class PpEntityRegistry {

    private static final DeferredRegister<EntityType<?>> PP_ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, GoetyOminous.MOD_ID);

    public static final RegistryObject<EntityType<PiglinAlchemistServant>> PIGLIN_ALCHEMIST_SERVANT =
            PP_ENTITIES.register("piglin_alchemist_servant",
                    () -> EntityType.Builder.of(PiglinAlchemistServant::new, MobCategory.MISC)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":piglin_alchemist_servant"));

    public static final RegistryObject<EntityType<PiglinTravelerServant>> PIGLIN_TRAVELER_SERVANT =
            PP_ENTITIES.register("piglin_traveler_servant",
                    () -> EntityType.Builder.of(PiglinTravelerServant::new, MobCategory.MISC)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":piglin_traveler_servant"));

    public static final RegistryObject<EntityType<ZPiglinAlchemistServant>> ZPIGLIN_ALCHEMIST_SERVANT =
            PP_ENTITIES.register("zpiglin_alchemist_servant",
                    () -> EntityType.Builder.of(ZPiglinAlchemistServant::new, MobCategory.MISC)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":zpiglin_alchemist_servant"));

    public static final RegistryObject<EntityType<ZPiglinTravelerServant>> ZPIGLIN_TRAVELER_SERVANT =
            PP_ENTITIES.register("zpiglin_traveler_servant",
                    () -> EntityType.Builder.of(ZPiglinTravelerServant::new, MobCategory.MISC)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":zpiglin_traveler_servant"));

    public static void register(IEventBus modEventBus) {
        PP_ENTITIES.register(modEventBus);
    }
}
