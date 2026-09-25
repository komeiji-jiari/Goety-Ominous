package com.qiuyue.goetyominous.compat.lm;

import com.qiuyue.goetyominous.common.entities.ally.lm.CloudGolemServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.HoveringHurricaneServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.OvergrownColossusServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import com.qiuyue.goetyominous.common.init.lm.LmSounds;
import com.qiuyue.goetyominous.common.items.lm.LmItems;
import net.minecraftforge.eventbus.api.IEventBus;

public class LmCompatManager {

    public static void init(IEventBus modEventBus) {
        LmEntityRegistry.register(modEventBus);

        LmSounds.register(modEventBus);

        LmItems.register(modEventBus);
    }

    public static void setCustomAttributes(net.minecraftforge.event.entity.EntityAttributeCreationEvent event) {
        event.put(LmEntityRegistry.OVERGROWN_COLOSSUS_SERVANT.get(),
                OvergrownColossusServant.createAttributes().build());
        event.put(LmEntityRegistry.POSSESSED_PALADIN_SERVANT.get(),
                PossessedPaladinServant.createAttributes().build());

        event.put(LmEntityRegistry.HOVERING_HURRICANE_SERVANT.get(),
                HoveringHurricaneServant.createAttributes().build());

        event.put(LmEntityRegistry.CLOUD_GOLEM_SERVANT.get(),
                CloudGolemServant.createAttributes().build());
    }
}
