/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package com.vivideru.masteryofmagic.network;

import com.vivideru.masteryofmagic.PlanetMasteryEvents;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

public record PlanetShapeshiftPacket(int targetId) {
    public static void encode(PlanetShapeshiftPacket p, FriendlyByteBuf b) {
        b.m_130130_(p.targetId + 1);
    }

    public static PlanetShapeshiftPacket decode(FriendlyByteBuf b) {
        return new PlanetShapeshiftPacket(b.m_130242_() - 1);
    }

    public static void handle(PlanetShapeshiftPacket p, Supplier<NetworkEvent.Context> s) {
        NetworkEvent.Context c = s.get();
        c.enqueueWork(() -> {
            LivingEntity e;
            Entity patt818$temp;
            ServerPlayer u = c.getSender();
            if (u == null) {
                return;
            }
            if (p.targetId >= 0 && (patt818$temp = u.m_9236_().m_6815_(p.targetId)) instanceof LivingEntity && u.m_20280_((Entity)(e = (LivingEntity)patt818$temp)) <= 1024.0 && u.m_142582_((Entity)e)) {
                PlanetMasteryEvents.copyShape(u, e);
            }
        });
        c.setPacketHandled(true);
    }
}

