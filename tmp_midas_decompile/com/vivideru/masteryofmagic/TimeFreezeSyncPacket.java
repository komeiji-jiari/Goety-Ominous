/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraft.world.phys.AABB
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package com.vivideru.masteryofmagic;

import com.vivideru.masteryofmagic.TimeFreezeRenderAnimationState;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkEvent;

public class TimeFreezeSyncPacket {
    private final double x;
    private final double y;
    private final double z;
    private final double radius;
    private final UUID immuneEntityUuid;
    private final boolean frozen;

    public TimeFreezeSyncPacket(double x, double y, double z, double radius, UUID immuneEntityUuid, boolean frozen) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.immuneEntityUuid = immuneEntityUuid;
        this.frozen = frozen;
    }

    public static void encode(TimeFreezeSyncPacket packet, FriendlyByteBuf buffer) {
        buffer.writeDouble(packet.x);
        buffer.writeDouble(packet.y);
        buffer.writeDouble(packet.z);
        buffer.writeDouble(packet.radius);
        buffer.writeBoolean(packet.immuneEntityUuid != null);
        if (packet.immuneEntityUuid != null) {
            buffer.m_130077_(packet.immuneEntityUuid);
        }
        buffer.writeBoolean(packet.frozen);
    }

    public static TimeFreezeSyncPacket decode(FriendlyByteBuf buffer) {
        double x = buffer.readDouble();
        double y = buffer.readDouble();
        double z = buffer.readDouble();
        double radius = buffer.readDouble();
        UUID immuneEntityUuid = null;
        if (buffer.readBoolean()) {
            immuneEntityUuid = buffer.m_130259_();
        }
        boolean frozen = buffer.readBoolean();
        return new TimeFreezeSyncPacket(x, y, z, radius, immuneEntityUuid, frozen);
    }

    public static void handle(TimeFreezeSyncPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.m_91087_();
            if (minecraft.f_91073_ == null) {
                return;
            }
            AABB area = new AABB(packet.x - packet.radius, packet.y - packet.radius, packet.z - packet.radius, packet.x + packet.radius, packet.y + packet.radius, packet.z + packet.radius);
            if (!packet.frozen) {
                for (Entity entity2 : minecraft.f_91073_.m_6249_((Entity)null, area, entity -> {
                    if (entity == null) {
                        return false;
                    }
                    return entity.m_20182_().m_82531_(packet.x, packet.y, packet.z) <= packet.radius * packet.radius;
                })) {
                    if (TimeFreezeRenderAnimationState.isFrozen(entity2.m_19879_())) {
                        TimeFreezeRenderAnimationState.setFrozen(entity2.m_19879_(), false);
                    }
                    if (packet.immuneEntityUuid == null || !entity2.m_20148_().equals(packet.immuneEntityUuid)) continue;
                    TimeFreezeRenderAnimationState.setTimeFreezeImmune(entity2.m_19879_(), false);
                }
                return;
            }
            for (LivingEntity entity3 : minecraft.f_91073_.m_45976_(LivingEntity.class, area)) {
                Player player;
                if (packet.immuneEntityUuid != null && entity3.m_20148_().equals(packet.immuneEntityUuid) || entity3 == minecraft.f_91074_ || entity3 instanceof Player && (player = (Player)entity3).m_7500_() || TimeFreezeRenderAnimationState.isFrozen(entity3.m_19879_())) continue;
                TimeFreezeRenderAnimationState.setFrozen(entity3.m_19879_(), true);
            }
            for (LivingEntity entity3 : minecraft.f_91073_.m_6249_((Entity)null, area, entity -> {
                if (entity == null || !entity.m_6084_()) {
                    return false;
                }
                if (!(entity instanceof Projectile)) {
                    return false;
                }
                if (packet.immuneEntityUuid != null && entity.m_20148_().equals(packet.immuneEntityUuid)) {
                    return false;
                }
                return entity.m_20182_().m_82531_(packet.x, packet.y, packet.z) <= packet.radius * packet.radius;
            })) {
                if (TimeFreezeRenderAnimationState.isFrozen(entity3.m_19879_())) continue;
                TimeFreezeRenderAnimationState.setFrozen(entity3.m_19879_(), true);
            }
        });
        context.setPacketHandled(true);
    }
}

