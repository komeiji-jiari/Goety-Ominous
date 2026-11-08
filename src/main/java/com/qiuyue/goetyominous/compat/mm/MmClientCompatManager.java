package com.qiuyue.goetyominous.compat.mm;

import com.qiuyue.goetyominous.client.events.mm.HogChargeAuraEvents;
import com.qiuyue.goetyominous.client.events.mm.MmClientEvents;
import com.qiuyue.goetyominous.client.events.mm.RodStrikeCastRenderEvents;
import com.qiuyue.goetyominous.client.mm.RiderChargeInputHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;

public class MmClientCompatManager {

    public static void register(IEventBus modEventBus) {
        modEventBus.register(MmClientEvents.class);
        MinecraftForge.EVENT_BUS.register(RodStrikeCastRenderEvents.class);
        MinecraftForge.EVENT_BUS.register(HogChargeAuraEvents.class);
        MinecraftForge.EVENT_BUS.register(RiderChargeInputHandler.class);
    }
}
