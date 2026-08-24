/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.entities.vehicle.HauntedBroom
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package com.vivideru.masteryofmagic.mixins;

import com.Polarice3.Goety.common.entities.vehicle.HauntedBroom;
import com.vivideru.masteryofmagic.MasteryData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={HauntedBroom.class})
public class SupremeSkiesBroomMixin {
    @Redirect(method={"handleInputs"}, at=@At(value="INVOKE", target="Lcom/Polarice3/Goety/common/entities/vehicle/HauntedBroom;setDeltaMovement(DDD)V", remap=true), remap=false)
    private void gmom$tripleMovement(HauntedBroom broom, double x, double y, double z) {
        Player player;
        LivingEntity rider = broom.m_6688_();
        double multiplier = rider instanceof Player && MasteryData.hasSupreme(player = (Player)rider, MasteryData.SupremeSchool.SKIES) ? 3.0 : 1.0;
        broom.m_20334_(x * multiplier, y * multiplier, z * multiplier);
    }
}

