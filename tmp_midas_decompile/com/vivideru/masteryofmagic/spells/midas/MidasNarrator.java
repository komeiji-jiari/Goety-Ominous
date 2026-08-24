/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 */
package com.vivideru.masteryofmagic.spells.midas;

import com.vivideru.masteryofmagic.config.BossConfig;
import com.vivideru.masteryofmagic.entity.PhilosopherKingMidasEntity;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicNetwork;
import com.vivideru.masteryofmagic.network.MidasNarrationPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public final class MidasNarrator {
    private static final double RANGE_SQR = 10000.0;

    private MidasNarrator() {
    }

    public static void announce(ServerLevel level, PhilosopherKingMidasEntity midas, String ... keys) {
        if (keys.length == 0 || !((Boolean)BossConfig.MIDAS_ANNOUNCEMENTS_ENABLED.get()).booleanValue()) {
            return;
        }
        String key = keys[midas.m_217043_().m_188503_(keys.length)];
        MidasNarrationPacket packet = new MidasNarrationPacket(key);
        for (ServerPlayer player : level.m_6907_()) {
            if (!(player.m_20280_((Entity)midas) <= 10000.0)) continue;
            GoetyMasteryOfMagicNetwork.sendToPlayer(player, packet);
        }
    }
}

