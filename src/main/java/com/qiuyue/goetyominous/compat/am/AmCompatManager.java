package com.qiuyue.goetyominous.compat.am;

import com.qiuyue.goetyominous.common.entities.ally.am.BunfungusServant;
import com.qiuyue.goetyominous.common.entities.ally.am.CrimsonMosquitoServant;
import com.qiuyue.goetyominous.common.entities.ally.am.IllagerElephantServant;
import com.qiuyue.goetyominous.common.entities.ally.am.ZombieCrocodileServant;
import com.qiuyue.goetyominous.common.entities.ally.am.FarseerServant;
import com.qiuyue.goetyominous.common.entities.ally.am.MurmurServant;
import com.qiuyue.goetyominous.common.entities.ally.am.MurmurServantHead;
import com.qiuyue.goetyominous.common.entities.ally.am.FroststalkerServant;
import com.qiuyue.goetyominous.common.entities.ally.am.RockyRollerServant;
import com.qiuyue.goetyominous.common.entities.ally.am.SkelewagServant;
import com.qiuyue.goetyominous.common.entities.ally.am.StraySkelewagServant;
import com.qiuyue.goetyominous.common.entities.ally.am.TusklinServant;
import com.qiuyue.goetyominous.common.entities.ally.am.WitherSkelewagServant;
import com.qiuyue.goetyominous.common.entities.ally.am.WarpedMoscoServant;
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
        event.put(AmEntityRegistry.CRIMSON_MOSQUITO_SERVANT.get(), CrimsonMosquitoServant.setCustomAttributes().build());
        event.put(AmEntityRegistry.WARPED_MOSCO_SERVANT.get(), WarpedMoscoServant.setCustomAttributes().build());
        event.put(AmEntityRegistry.FARSEER_SERVANT.get(), FarseerServant.bakeAttributes().build());
        event.put(AmEntityRegistry.TUSKLIN_SERVANT.get(), TusklinServant.setCustomAttributes().build());
        event.put(AmEntityRegistry.FROSTSTALKER_SERVANT.get(), FroststalkerServant.setCustomAttributes().build());
        event.put(AmEntityRegistry.ROCKY_ROLLER_SERVANT.get(), RockyRollerServant.setCustomAttributes().build());
        event.put(AmEntityRegistry.ZOMBIE_CROCODILE_SERVANT.get(), ZombieCrocodileServant.setCustomAttributes().build());
        event.put(AmEntityRegistry.BUNFUNGUS_SERVANT.get(), BunfungusServant.setCustomAttributes().build());
        event.put(AmEntityRegistry.ILLAGER_ELEPHANT_SERVANT.get(), IllagerElephantServant.setCustomAttributes().build());
        event.put(AmEntityRegistry.SKELEWAG_SERVANT.get(), SkelewagServant.setCustomAttributes().build());
        event.put(AmEntityRegistry.WITHER_SKELEWAG_SERVANT.get(), WitherSkelewagServant.setCustomAttributes().build());
        event.put(AmEntityRegistry.STRAY_SKELEWAG_SERVANT.get(), StraySkelewagServant.setCustomAttributes().build());
    }
}
