package com.qiuyue.goetyominous.common.network.ac;

import com.qiuyue.goetyominous.common.network.ModNetwork;

public class AcPackets {

    public static void register() {
        ModNetwork.register(ForsakenRiderJumpPacket.class,
                ForsakenRiderJumpPacket::encode,
                ForsakenRiderJumpPacket::decode,
                ForsakenRiderJumpPacket::handle);
        ModNetwork.register(NucleeperExplosionZonePacket.class,
                NucleeperExplosionZonePacket::encode,
                NucleeperExplosionZonePacket::decode,
                NucleeperExplosionZonePacket::handle);
        ModNetwork.register(TremorzillaBreathPacket.class,
                TremorzillaBreathPacket::encode,
                TremorzillaBreathPacket::decode,
                TremorzillaBreathPacket::handle);
        ModNetwork.register(XRayPacket.class,
                XRayPacket::encode,
                XRayPacket::decode,
                XRayPacket::handle);
        ModNetwork.register(NuclearExplosionEffectPacket.class,
                NuclearExplosionEffectPacket::encode,
                NuclearExplosionEffectPacket::decode,
                NuclearExplosionEffectPacket::handle);
        ModNetwork.register(ServantAbilityKeyPacket.class,
                ServantAbilityKeyPacket::encode,
                ServantAbilityKeyPacket::decode,
                ServantAbilityKeyPacket::handle);
    }
}
