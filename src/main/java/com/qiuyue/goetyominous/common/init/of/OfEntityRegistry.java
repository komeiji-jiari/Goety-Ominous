package com.qiuyue.goetyominous.common.init.of;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.of.RamblerServant;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Opposing Force 联动实体注册类，在这里添加 Opposing Force 仆从的实体类型注册
 */
public class OfEntityRegistry {

    private static final DeferredRegister<EntityType<?>> OF_ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, GoetyOminous.MOD_ID);

    public static final RegistryObject<EntityType<RamblerServant>> RAMBLER_SERVANT =
            OF_ENTITIES.register("rambler_servant",
                    () -> EntityType.Builder.<RamblerServant>of(RamblerServant::new, MobCategory.MISC)
                            .sized(1.98F, 2.25F)
                            .setTrackingRange(10)
                            .build(GoetyOminous.MOD_ID + ":rambler_servant"));


    public static void register(IEventBus modEventBus) {
        OF_ENTITIES.register(modEventBus);
    }
}
