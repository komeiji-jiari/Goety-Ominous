package com.qiuyue.goetyominous.common.network;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.explore.network.ExpeditionClaimPacket;
import com.qiuyue.goetyominous.common.explore.network.ExpeditionGatherPacket;
import com.qiuyue.goetyominous.common.explore.network.ExpeditionOpenPacket;
import com.qiuyue.goetyominous.common.explore.network.ExpeditionProgressPacket;
import com.qiuyue.goetyominous.common.explore.network.ExpeditionRecallPacket;
import com.qiuyue.goetyominous.common.explore.network.ExpeditionRequestPacket;
import com.qiuyue.goetyominous.common.explore.network.ExpeditionStartPacket;
import com.qiuyue.goetyominous.common.explore.network.ExpeditionViewPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "7";
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
        register(ExpeditionOpenPacket.class,
                ExpeditionOpenPacket::encode,
                ExpeditionOpenPacket::decode,
                ExpeditionOpenPacket::handle);
        register(ExpeditionRequestPacket.class,
                ExpeditionRequestPacket::encode,
                ExpeditionRequestPacket::decode,
                ExpeditionRequestPacket::handle);
        register(ExpeditionStartPacket.class,
                ExpeditionStartPacket::encode,
                ExpeditionStartPacket::decode,
                ExpeditionStartPacket::handle);
        register(ExpeditionClaimPacket.class,
                ExpeditionClaimPacket::encode,
                ExpeditionClaimPacket::decode,
                ExpeditionClaimPacket::handle);
        register(ExpeditionRecallPacket.class,
                ExpeditionRecallPacket::encode,
                ExpeditionRecallPacket::decode,
                ExpeditionRecallPacket::handle);
        register(ExpeditionProgressPacket.class,
                ExpeditionProgressPacket::encode,
                ExpeditionProgressPacket::decode,
                ExpeditionProgressPacket::handle);
        register(ExpeditionViewPacket.class,
                ExpeditionViewPacket::encode,
                ExpeditionViewPacket::decode,
                ExpeditionViewPacket::handle);
        register(ExpeditionGatherPacket.class,
                ExpeditionGatherPacket::encode,
                ExpeditionGatherPacket::decode,
                ExpeditionGatherPacket::handle);
    }

    public static <T> void register(Class<T> messageType,
                                    BiConsumer<T, FriendlyByteBuf> encoder,
                                    Function<FriendlyByteBuf, T> decoder,
                                    BiConsumer<T, Supplier<NetworkEvent.Context>> handler) {
        CHANNEL.registerMessage(id++, messageType, encoder, decoder, handler);
    }
}
