package com.qiuyue.goetyominous.compat.am;

import com.qiuyue.goetyominous.common.entities.ally.am.MurmurServant;
import com.qiuyue.goetyominous.common.entities.ally.am.MurmurServantHead;
import com.qiuyue.goetyominous.common.init.am.AmEntityRegistry;
import com.qiuyue.goetyominous.common.items.am.AmItems;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * AlexMobs 联动内容统一管理器，在这里添加所有 AlexMobs 仆从的注册流程
 */
public class AmCompatManager {

    /**
     * 初始化 AlexMobs 相关内容（实体、物品等），在这里添加新实体/物品的注册
     */
    public static void init(IEventBus modEventBus) {
        // 注册 AlexMobs 联动实体
        AmEntityRegistry.register(modEventBus);

        // 注册 AlexMobs 联动物品
        AmItems.register(modEventBus);
    }

    /**
     * 注册 AlexMobs 仆从的属性，在这里添加新实体的属性注册
     */
    public static void setCustomAttributes(EntityAttributeCreationEvent event) {
        event.put(AmEntityRegistry.MURMUR_SERVANT.get(), MurmurServant.setCustomAttributes().build());
        event.put(AmEntityRegistry.MURMUR_SERVANT_HEAD.get(), MurmurServantHead.setCustomAttributes().build());
    }
}
