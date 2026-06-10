package com.qiuyue.someillagerservants.compat.ias;

import com.qiuyue.someillagerservants.common.entities.ally.illager.AbsorberServant;
import com.qiuyue.someillagerservants.common.entities.ally.illager.MagispellerServant;
import com.qiuyue.someillagerservants.common.entities.ally.illager.TwittollagerServant;
import com.qiuyue.someillagerservants.common.entities.ally.mobs.*;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * IllageAndSpillage 联动内容统一管理器
 * 所有 I&S 相关的注册都在这里处理，确保类隔离
 */
public class IasCompatManager {

    /**
     * 初始化 I&S 相关内容（实体、物品等）
     * @param modEventBus 模组事件总线
     */
    public static void init(IEventBus modEventBus) {
        // 注册 I&S 实体
        IasEntityRegistry.register(modEventBus);

        // 注册 I&S 物品
        IasItems.register(modEventBus);
    }

    /**
     * 注册 I&S 仆从的属性
     * @param event 属性创建事件
     */
    public static void setCustomAttributes(net.minecraftforge.event.entity.EntityAttributeCreationEvent event) {
        event.put(IasEntityRegistry.TWITTOLLAGER_SERVANT.get(), TwittollagerServant.setCustomAttributes().build());
        event.put(IasEntityRegistry.ABSORBER_SERVANT.get(), AbsorberServant.setCustomAttributes().build());
        event.put(IasEntityRegistry.CRASHAGER_SERVANT.get(), CrashagerServant.setCustomAttributes().build());
        event.put(IasEntityRegistry.KABOOMER_SERVANT.get(), KaboomerServant.setCustomAttributes().build());
        event.put(IasEntityRegistry.ILLASHOOTER_SERVANT.get(), IllashooterServant.setCustomAttributes().build());
        event.put(IasEntityRegistry.DISPENSER_SERVANT.get(), DispenserServant.setCustomAttributes().build());
        event.put(IasEntityRegistry.FAKEMAGISPELLER.get(), FakeMagispeller.createAttributes().build());
        event.put(IasEntityRegistry.MAGIHEAL.get(), MagiHeal.createAttributes().build());
        event.put(IasEntityRegistry.MAGISPELLER_SERVANT.get(), MagispellerServant.setCustomAttributes().build());
    }
}
