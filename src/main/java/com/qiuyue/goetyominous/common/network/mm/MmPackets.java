package com.qiuyue.goetyominous.common.network.mm;

import com.qiuyue.goetyominous.common.network.ModNetwork;

public class MmPackets {

    public static void register() {
        ModNetwork.register(RiderChargePacket.class,
                RiderChargePacket::encode,
                RiderChargePacket::decode,
                RiderChargePacket::handle);
        ModNetwork.register(CWitherScytheStrikePacket.class,
                CWitherScytheStrikePacket::encode,
                CWitherScytheStrikePacket::decode,
                CWitherScytheStrikePacket::handle);
    }
}
