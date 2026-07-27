package com.qiuyue.goetyominus.compat.sar;

import com.qiuyue.goetyominus.common.entities.ally.sar.*;
import com.qiuyue.goetyominus.common.init.sar.SarEntityRegistry;
import com.qiuyue.goetyominus.common.items.sar.SarItems;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * SAR 联动内容统一管理器
 * 所有 SAR 相关的注册都在这里处理，确保类隔离
 */
public class SarCompatManager {

    /**
     * 初始化 SAR 相关内容（实体、物品等）
     * @param modEventBus 模组事件总线
     */
    public static void init(IEventBus modEventBus) {
        // 注册 SAR 实体
        SarEntityRegistry.register(modEventBus);

        // 注册 SAR 物品
        SarItems.register(modEventBus);
    }

    /**
     * 注册 SAR 仆从的属性
     * @param event 属性创建事件
     */
    public static void setCustomAttributes(EntityAttributeCreationEvent event) {
        event.put(SarEntityRegistry.CREEPIE_SERVANT.get(), CreepieServant.setCustomAttributes().build());
        event.put(SarEntityRegistry.GRIEFER_SERVANT.get(), GrieferServant.setCustomAttributes().build());
        event.put(SarEntityRegistry.EXECUTIONER_SERVANT.get(), ExecutionerServant.setCustomAttributes().build());
        event.put(SarEntityRegistry.SKELETON_VILLAGER_SERVANT.get(), SkeletonVillagerServant.setCustomAttributes().build());
        event.put(SarEntityRegistry.TRICKSTER_SERVANT.get(), TricksterServant.setCustomAttributes().build());
        event.put(SarEntityRegistry.RUNE_PRISON.get(), RunePrison.setCustomAttributes().build());
    }
}
