package com.qiuyue.goetyominus.common.init.lm;

import com.qiuyue.goetyominus.GoetyOminous;
import com.qiuyue.goetyominus.common.entities.ally.lm.OvergrownColossusServant;
import com.qiuyue.goetyominus.common.entities.ally.lm.projectile.PoisonousShockwave;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * LegendaryMonsters 联动实体注册类
 * 负责注册 LM 联动所需的实体类型
 * 注意：这个类只在 LegendaryMonsters 模组加载时才会被调用
 */
public class LmEntityRegistry {

    private static final DeferredRegister<EntityType<?>> LM_ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, GoetyOminous.MOD_ID);

    public static final RegistryObject<EntityType<OvergrownColossusServant>> OVERGROWN_COLOSSUS_SERVANT =
            LM_ENTITIES.register("overgrown_colossus_servant",
                    () -> EntityType.Builder.of(OvergrownColossusServant::new, MobCategory.MONSTER)
                            .sized(3.0F, 5.0F).fireImmune()
                            .build(GoetyOminous.MOD_ID + ":overgrown_colossus_servant"));

    public static final RegistryObject<EntityType<PoisonousShockwave>> POISONOUS_SHOCKWAVE =
            LM_ENTITIES.register("poisonous_shockwave",
                    () -> EntityType.Builder.<PoisonousShockwave>of(PoisonousShockwave::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(1)
                            .build(GoetyOminous.MOD_ID + ":poisonous_shockwave"));

    public static void register(IEventBus modEventBus) {
        LM_ENTITIES.register(modEventBus);
    }
}
