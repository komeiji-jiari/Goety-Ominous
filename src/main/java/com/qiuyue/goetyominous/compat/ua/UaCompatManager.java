package com.qiuyue.goetyominous.compat.ua;

import com.qiuyue.goetyominous.common.entities.ally.ua.FlareServant;
import com.qiuyue.goetyominous.common.entities.ally.ua.GreatThrasherServant;
import com.qiuyue.goetyominous.common.entities.ally.ua.ThrasherServant;
import com.qiuyue.goetyominous.common.init.ua.UaEntityRegistry;
import com.qiuyue.goetyominous.common.items.ua.UaItems;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * Upgrade Aquatic 联动内容统一管理器
 * 所有 UA 相关的注册都在这里处理，确保类隔离
 */
public class UaCompatManager {

    /**
     * 初始化 UA 相关内容（实体、物品等）
     * @param modEventBus 模组事件总线
     */
    public static void init(IEventBus modEventBus) {
        // 注册 UA 实体
        UaEntityRegistry.register(modEventBus);

        // 注册 UA 物品
        UaItems.register(modEventBus);
    }

    /**
     * 注册 UA 仆从的属性
     * @param event 属性创建事件
     */
    public static void setCustomAttributes(net.minecraftforge.event.entity.EntityAttributeCreationEvent event) {
        event.put(UaEntityRegistry.THRASHER_SERVANT.get(),
                ThrasherServant.setCustomAttributes().build());

        event.put(UaEntityRegistry.GREAT_THRASHER_SERVANT.get(),
                GreatThrasherServant.setCustomAttributes().build());

        event.put(UaEntityRegistry.FLARE_SERVANT.get(),
                FlareServant.registerAttributes().build());
    }
}
