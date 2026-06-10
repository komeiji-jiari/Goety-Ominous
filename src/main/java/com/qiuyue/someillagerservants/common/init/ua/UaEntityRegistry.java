package com.qiuyue.someillagerservants.common.init.ua;

import com.qiuyue.someillagerservants.SomeIllagerServants;
import com.qiuyue.someillagerservants.common.entities.ally.ua.FlareServant;
import com.qiuyue.someillagerservants.common.entities.ally.ua.GreatThrasherServant;
import com.qiuyue.someillagerservants.common.entities.ally.ua.ThrasherServant;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * UA (Upgrade Aquatic) 联动实体注册类
 * 负责注册所有 UA 仆从实体类型
 * 注意：这个类只在 UA 模组加载时才会被调用
 */
public class UaEntityRegistry {

    /**
     * UA 实体延迟注册表
     */
    private static final DeferredRegister<EntityType<?>> UA_ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SomeIllagerServants.MOD_ID);

    public static final RegistryObject<EntityType<ThrasherServant>> THRASHER_SERVANT =
            UA_ENTITIES.register("thrasher_servant",
                    () -> EntityType.Builder.of(ThrasherServant::new, MobCategory.WATER_CREATURE)
                            .sized(1.6F, 0.9F)
                            .clientTrackingRange(8)
                            .build(SomeIllagerServants.MOD_ID + ":thrasher_servant"));

    public static final RegistryObject<EntityType<GreatThrasherServant>> GREAT_THRASHER_SERVANT =
            UA_ENTITIES.register("great_thrasher_servant",
                    () -> EntityType.Builder.of(GreatThrasherServant::new, MobCategory.WATER_CREATURE)
                            .sized(2.8F, 1.575F)
                            .clientTrackingRange(10)
                            .build(SomeIllagerServants.MOD_ID + ":great_thrasher_servant"));

    public static final RegistryObject<EntityType<FlareServant>> FLARE_SERVANT =
            UA_ENTITIES.register("flare_servant",
                    () -> EntityType.Builder.of(FlareServant::new, MobCategory.MONSTER)
                            .sized(0.9F, 0.5F)
                            .clientTrackingRange(8)
                            .build(SomeIllagerServants.MOD_ID + ":flare_servant"));

    /**
     * 注册 UA 实体到模组事件总线
     * @param modEventBus 模组事件总线
     */
    public static void register(IEventBus modEventBus) {
        UA_ENTITIES.register(modEventBus);
    }
}
