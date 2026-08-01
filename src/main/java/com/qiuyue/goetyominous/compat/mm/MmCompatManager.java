package com.qiuyue.goetyominous.compat.mm;

import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantHoglinServant;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantWitherSkeletonServant;
import com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry;
import com.qiuyue.goetyominous.common.items.mm.MmItems;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * MutantMore 联动内容统一管理器
 * 所有 MutantMore 相关的注册都在这里处理，确保类隔离
 */
public class MmCompatManager {

    /**
     * 初始化 MutantMore 相关内容（实体、物品等）
     * @param modEventBus 模组事件总线
     */
    public static void init(IEventBus modEventBus) {
        // 注册 MutantMore 实体
        MmEntityRegistry.register(modEventBus);

        // 注册 MutantMore 物品
        MmItems.register(modEventBus);
    }

    /**
     * 注册 MutantMore 仆从的属性
     * @param event 属性创建事件
     */
    public static void setCustomAttributes(net.minecraftforge.event.entity.EntityAttributeCreationEvent event) {
        event.put(MmEntityRegistry.MUTANT_WITHER_SKELETON_SERVANT.get(), MutantWitherSkeletonServant.createConfiguredAttributes().build());
        event.put(MmEntityRegistry.MUTANT_HOGLIN_SERVANT.get(), MutantHoglinServant.createConfiguredAttributes().build());
    }
}