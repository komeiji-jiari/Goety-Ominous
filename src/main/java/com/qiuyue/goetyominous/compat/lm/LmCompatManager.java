package com.qiuyue.goetyominous.compat.lm;

import com.qiuyue.goetyominous.common.entities.ally.lm.OvergrownColossusServant;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import com.qiuyue.goetyominous.common.init.lm.LmSounds;
import com.qiuyue.goetyominous.common.items.lm.LmItems;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * LegendaryMonsters 联动内容统一管理器
 */
public class LmCompatManager {

    /**
     * 初始化 LegendaryMonsters 相关内容
     */
    public static void init(IEventBus modEventBus) {
        // 注册 LM 实体
        LmEntityRegistry.register(modEventBus);

        // 注册 LM 音效
        LmSounds.register(modEventBus);

        // 注册 LM 物品
        LmItems.register(modEventBus);
    }

    /**
     * 注册 LegendaryMonsters 仆从的属性
     */
    public static void setCustomAttributes(net.minecraftforge.event.entity.EntityAttributeCreationEvent event) {
        event.put(LmEntityRegistry.OVERGROWN_COLOSSUS_SERVANT.get(),
                OvergrownColossusServant.createAttributes().build());
    }
}
