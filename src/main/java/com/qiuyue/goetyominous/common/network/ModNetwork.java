package com.qiuyue.goetyominous.common.network;

import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "2";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(GoetyOminous.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int id = 0;

    public static void init() {
        register(OpenFungusPackPacket.class,
                OpenFungusPackPacket::encode,
                OpenFungusPackPacket::decode,
                OpenFungusPackPacket::handle);
        register(CCroneRobePacket.class,
                CCroneRobePacket::encode,
                CCroneRobePacket::decode,
                CCroneRobePacket::handle);
        register(CExtractPotionPacket.class,
                CExtractPotionPacket::encode,
                CExtractPotionPacket::decode,
                CExtractPotionPacket::handle);
        register(WindChargeImpulsePacket.class,
                WindChargeImpulsePacket::encode,
                WindChargeImpulsePacket::decode,
                WindChargeImpulsePacket::handle);
    }

    public static <T> void register(Class<T> messageType,
                                    BiConsumer<T, FriendlyByteBuf> encoder,
                                    Function<FriendlyByteBuf, T> decoder,
                                    BiConsumer<T, Supplier<NetworkEvent.Context>> handler) {
        CHANNEL.registerMessage(id++, messageType, encoder, decoder, handler);
    }
}
