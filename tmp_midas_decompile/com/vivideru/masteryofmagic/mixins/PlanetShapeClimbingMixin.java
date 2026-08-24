/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.vivideru.masteryofmagic.mixins;

import com.vivideru.masteryofmagic.PlanetShapeAccess;
import java.util.Locale;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Player.class})
public abstract class PlanetShapeClimbingMixin {
    private static final double WALL_PROBE = 0.09;

    @Inject(method={"aiStep"}, at={@At(value="TAIL")})
    private void gmom$spiderWallMovement(CallbackInfo ci) {
        boolean touchingWall;
        Player player = (Player)this;
        String shape = ((PlanetShapeAccess)player).gmom$getPlanetShape();
        if (shape == null || !shape.toLowerCase(Locale.ROOT).contains("spider") || player.m_150110_().f_35935_ || player.m_20072_()) {
            return;
        }
        boolean bl = touchingWall = player.f_19862_ || PlanetShapeClimbingMixin.gmom$touchingWall(player);
        if (!touchingWall) {
            return;
        }
        Vec3 movement = player.m_20184_();
        double vertical = movement.f_82480_;
        if (player.m_6144_() && !player.m_20096_()) {
            vertical = -0.15;
        } else if (player.f_19862_) {
            vertical = Math.max(vertical, 0.2);
        } else if (!player.m_20096_()) {
            vertical = Math.max(vertical, -0.04);
        }
        if (vertical != movement.f_82480_) {
            player.m_20334_(movement.f_82479_, vertical, movement.f_82481_);
        }
        player.f_19789_ = 0.0f;
    }

    private static boolean gmom$touchingWall(Player player) {
        AABB probe = player.m_20191_().m_165897_(0.01, 0.05, 0.01);
        return !player.m_9236_().m_45756_((Entity)player, probe.m_82386_(0.09, 0.0, 0.0)) || !player.m_9236_().m_45756_((Entity)player, probe.m_82386_(-0.09, 0.0, 0.0)) || !player.m_9236_().m_45756_((Entity)player, probe.m_82386_(0.0, 0.0, 0.09)) || !player.m_9236_().m_45756_((Entity)player, probe.m_82386_(0.0, 0.0, -0.09));
    }
}

