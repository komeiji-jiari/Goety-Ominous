package com.qiuyue.goetyominous.common.init.ac;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.ac.GrottoceratopsServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.TremorsaurusServant;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class AcEntityRegistry {

    private static final DeferredRegister<EntityType<?>> AC_ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, GoetyOminous.MOD_ID);

    public static final RegistryObject<EntityType<GrottoceratopsServant>> GROTTOCERATOPS_SERVANT =
            AC_ENTITIES.register("grottoceratops_servant",
                    () -> EntityType.Builder.<GrottoceratopsServant>of((type, worldIn) -> new GrottoceratopsServant(type, worldIn), MobCategory.MISC)
                            .sized(2.3F, 2.5F)
                            .setTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":grottoceratops_servant"));

    public static final RegistryObject<EntityType<TremorsaurusServant>> TREMORSAURUS_SERVANT =
            AC_ENTITIES.register("tremorsaurus_servant",
                    () -> EntityType.Builder.<TremorsaurusServant>of((type, worldIn) -> new TremorsaurusServant(type, worldIn), MobCategory.MISC)
                            .sized(2.5F, 3.85F)
                            .setTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":tremorsaurus_servant"));

    public static void register(IEventBus modEventBus) {
        AC_ENTITIES.register(modEventBus);
    }
}
