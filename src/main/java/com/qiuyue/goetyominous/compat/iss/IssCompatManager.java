package com.qiuyue.goetyominous.compat.iss;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;

public class IssCompatManager {

    public static void init(IEventBus modEventBus) {
        MinecraftForge.EVENT_BUS.register(IssTrialOmenEvents.class);
    }
}
