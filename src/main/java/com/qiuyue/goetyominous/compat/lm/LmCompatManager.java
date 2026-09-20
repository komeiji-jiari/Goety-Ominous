package com.qiuyue.goetyominous.compat.lm;

import com.qiuyue.goetyominous.common.entities.ally.lm.OvergrownColossusServant;
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
    }
}
