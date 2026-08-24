package com.qiuyue.goetyominous.common.init.of;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.of.DicerServant;
import com.qiuyue.goetyominous.common.entities.ally.of.RamblerServant;
import com.qiuyue.goetyominous.common.entities.ally.of.TremblerServant;
import com.qiuyue.goetyominous.common.entities.ally.of.VoltServant;
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

    public static final RegistryObject<EntityType<DicerServant>> DICER_SERVANT =
            OF_ENTITIES.register("dicer_servant",
                    () -> EntityType.Builder.<DicerServant>of(DicerServant::new, MobCategory.MISC)
                            .sized(2.0F, 2.4F)
                            .setTrackingRange(10)
                            .build(GoetyOminous.MOD_ID + ":dicer_servant"));
    public static final RegistryObject<EntityType<VoltServant>> VOLT_SERVANT =
            OF_ENTITIES.register("volt_servant",
                    () -> EntityType.Builder.<VoltServant>of(VoltServant::new, MobCategory.MISC)
                            .sized(1.0F, 1.3F)          // 尺寸我估的，进游戏对不上再调
                            .setTrackingRange(10)
                            .build(GoetyOminous.MOD_ID + ":volt_servant"));

    public static final RegistryObject<EntityType<TremblerServant>> TREMBLER_SERVANT =
            OF_ENTITIES.register("trembler_servant",
                    () -> EntityType.Builder.<TremblerServant>of(TremblerServant::new, MobCategory.MISC)
                            .sized(1.1F, 1.0F)          // 尺寸我估的，进游戏对不上再调
                            .setTrackingRange(10)
                            .build(GoetyOminous.MOD_ID + ":trembler_servant"));

    public static void register(IEventBus modEventBus) {
        OF_ENTITIES.register(modEventBus);
    }
}
