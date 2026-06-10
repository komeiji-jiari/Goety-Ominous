package com.qiuyue.someillagerservants.common.network;

import com.qiuyue.someillagerservants.SomeIllagerServants;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SomeIllagerServants.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int id = 0;

    public static void init() {
        CHANNEL.registerMessage(id++, RiderChargePacket.class,
                RiderChargePacket::encode,
                RiderChargePacket::decode,
                RiderChargePacket::handle);
    }
}
