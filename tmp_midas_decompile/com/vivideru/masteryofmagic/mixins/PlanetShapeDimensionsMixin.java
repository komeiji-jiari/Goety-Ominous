/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.EntityDimensions
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.Pose
 *  net.minecraft.world.entity.player.Player
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.vivideru.masteryofmagic.mixins;

import com.vivideru.masteryofmagic.PlanetShapeAccess;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Player.class})
public abstract class PlanetShapeDimensionsMixin {
    @Inject(method={"getDimensions"}, at={@At(value="HEAD")}, cancellable=true)
    private void gmom$animalDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> callback) {
        String shape = ((PlanetShapeAccess)((Object)this)).gmom$getPlanetShape();
        if (shape == null || shape.isBlank()) {
            return;
        }
        EntityType.m_20632_((String)shape).ifPresent(type -> callback.setReturnValue((Object)type.m_20680_()));
    }
}

