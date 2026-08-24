/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.vivideru.masteryofmagic.mixins;

import com.vivideru.masteryofmagic.PlanetShapeAccess;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Entity.class})
public abstract class PlanetShapeSyncedDimensionsMixin {
    @Inject(method={"onSyncedDataUpdated(Lnet/minecraft/network/syncher/EntityDataAccessor;)V"}, at={@At(value="TAIL")})
    private void gmom$refreshAnimalDimensions(EntityDataAccessor<?> key, CallbackInfo ci) {
        PlanetShapeAccess access;
        Player player;
        PlanetShapeSyncedDimensionsMixin planetShapeSyncedDimensionsMixin = this;
        if (planetShapeSyncedDimensionsMixin instanceof Player && (player = (Player)planetShapeSyncedDimensionsMixin) instanceof PlanetShapeAccess && (access = (PlanetShapeAccess)player).gmom$isPlanetShapeData(key)) {
            player.m_6210_();
        }
    }
}

