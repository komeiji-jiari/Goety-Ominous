package com.qiuyue.goetyominous.common.network.am;

import com.qiuyue.goetyominous.common.network.ModNetwork;

public class AmPackets {

    public static void register() {
        ModNetwork.register(ElephantChargePacket.class,
                ElephantChargePacket::encode,
                ElephantChargePacket::decode,
                ElephantChargePacket::handle);
    }
}
