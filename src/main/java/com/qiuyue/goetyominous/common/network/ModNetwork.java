package com.qiuyue.goetyominous.common.network;

import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import com.qiuyue.goetyominous.common.network.mm.CWitherScytheStrikePacket;
import com.qiuyue.goetyominous.common.network.am.ElephantChargePacket;
import com.qiuyue.goetyominous.common.network.ac.ForsakenRiderJumpPacket;
import com.qiuyue.goetyominous.common.network.ac.NuclearExplosionEffectPacket;
import com.qiuyue.goetyominous.common.network.mm.RiderChargePacket;
import com.qiuyue.goetyominous.common.network.ac.NucleeperExplosionZonePacket;
import com.qiuyue.goetyominous.common.network.ac.ServantAbilityKeyPacket;
import com.qiuyue.goetyominous.common.network.ac.TremorzillaBreathPacket;
import com.qiuyue.goetyominous.common.network.ac.XRayPacket;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(GoetyOminous.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int id = 0;

    public static void init() {
        CHANNEL.registerMessage(id++, ForsakenRiderJumpPacket.class,
                ForsakenRiderJumpPacket::encode,
                ForsakenRiderJumpPacket::decode,
                ForsakenRiderJumpPacket::handle);
        CHANNEL.registerMessage(id++, RiderChargePacket.class,
                RiderChargePacket::encode,
                RiderChargePacket::decode,
                RiderChargePacket::handle);
        CHANNEL.registerMessage(id++, OpenFungusPackPacket.class,
                OpenFungusPackPacket::encode,
                OpenFungusPackPacket::decode,
                OpenFungusPackPacket::handle);
        CHANNEL.registerMessage(id++, CWitherScytheStrikePacket.class,
                CWitherScytheStrikePacket::encode,
                CWitherScytheStrikePacket::decode,
                CWitherScytheStrikePacket::handle);
        CHANNEL.registerMessage(id++, ElephantChargePacket.class,
                ElephantChargePacket::encode,
                ElephantChargePacket::decode,
                ElephantChargePacket::handle);
        CHANNEL.registerMessage(id++, NucleeperExplosionZonePacket.class,
                NucleeperExplosionZonePacket::encode,
                NucleeperExplosionZonePacket::decode,
                NucleeperExplosionZonePacket::handle);
        CHANNEL.registerMessage(id++, CCroneRobePacket.class,
                CCroneRobePacket::encode,
                CCroneRobePacket::decode,
                CCroneRobePacket::handle);
        CHANNEL.registerMessage(id++, CExtractPotionPacket.class,
                CExtractPotionPacket::encode,
                CExtractPotionPacket::decode,
                CExtractPotionPacket::handle);
        CHANNEL.registerMessage(id++, TremorzillaBreathPacket.class,
                TremorzillaBreathPacket::encode,
                TremorzillaBreathPacket::decode,
                TremorzillaBreathPacket::handle);
        CHANNEL.registerMessage(id++, XRayPacket.class,
                XRayPacket::encode,
                XRayPacket::decode,
                XRayPacket::handle);
        CHANNEL.registerMessage(id++, NuclearExplosionEffectPacket.class,
                NuclearExplosionEffectPacket::encode,
                NuclearExplosionEffectPacket::decode,
                NuclearExplosionEffectPacket::handle);
        CHANNEL.registerMessage(id++, WindChargeImpulsePacket.class,
                WindChargeImpulsePacket::encode,
                WindChargeImpulsePacket::decode,
                WindChargeImpulsePacket::handle);
        CHANNEL.registerMessage(id++, ServantAbilityKeyPacket.class,
                ServantAbilityKeyPacket::encode,
                ServantAbilityKeyPacket::decode,
                ServantAbilityKeyPacket::handle);
    }
}
