/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.player.Player
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.vivideru.masteryofmagic.mixins;

import com.vivideru.masteryofmagic.MasteryData;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Player.class})
public class PlanetCanEatMixin {
    @Inject(method={"canEat"}, at={@At(value="HEAD")}, cancellable=true)
    private void gmom$alwaysEat(boolean always, CallbackInfoReturnable<Boolean> cir) {
        Player p = (Player)this;
        if (MasteryData.hasSupreme(p, MasteryData.SupremeSchool.PLANET)) {
            cir.setReturnValue((Object)true);
        }
    }
}

