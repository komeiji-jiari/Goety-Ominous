package com.qiuyue.goetyominus.common.init.am;

import com.qiuyue.goetyominus.GoetyOminous;
import com.qiuyue.goetyominus.common.entities.ally.am.MurmurServant;
import com.qiuyue.goetyominus.common.entities.ally.am.MurmurServantHead;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * AlexMobs 联动实体注册类，在这里添加 AlexMobs 仆从的实体类型注册
 */
public class AmEntityRegistry {

    private static final DeferredRegister<EntityType<?>> AM_ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, GoetyOminous.MOD_ID);

    public static final RegistryObject<EntityType<MurmurServant>> MURMUR_SERVANT =
            AM_ENTITIES.register("murmur_servant",
                    () -> EntityType.Builder.of(MurmurServant::new, MobCategory.MISC)
                            .sized(0.7F, 1.45F)
                            .setTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":murmur_servant"));

    public static final RegistryObject<EntityType<MurmurServantHead>> MURMUR_SERVANT_HEAD =
            AM_ENTITIES.register("murmur_servant_head",
                    () -> EntityType.Builder.of(MurmurServantHead::new, MobCategory.MISC)
                            .sized(0.55F, 0.55F)
                            .setTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":murmur_servant_head"));

    // === 在这里添加实体注册 ===
    // 示例：
    // public static final RegistryObject<EntityType<XXXServant>> XXX_SERVANT =
    //         AM_ENTITIES.register("xxx_servant",
    //                 () -> EntityType.Builder.of(XXXServant::new, MobCategory.MISC)
    //                         .sized(0.6F, 1.95F)
    //                         .clientTrackingRange(8)
    //                         .build(GoetyOminous.MOD_ID + ":xxx_servant"));

    public static void register(IEventBus modEventBus) {
        AM_ENTITIES.register(modEventBus);
    }
}
