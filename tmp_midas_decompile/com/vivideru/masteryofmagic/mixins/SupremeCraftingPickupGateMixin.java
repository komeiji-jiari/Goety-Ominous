/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.Slot
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.vivideru.masteryofmagic.mixins;

import com.vivideru.masteryofmagic.MasteryData;
import com.vivideru.masteryofmagic.SupremeDuplicationRecipe;
import com.vivideru.masteryofmagic.mixins.ResultSlotAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Slot.class})
public class SupremeCraftingPickupGateMixin {
    @Inject(method={"mayPickup"}, at={@At(value="HEAD")}, cancellable=true)
    private void goetyMasteryOfMagic$gateSupremeDuplication(Player player, CallbackInfoReturnable<Boolean> cir) {
        SupremeCraftingPickupGateMixin supremeCraftingPickupGateMixin;
        if (player.m_9236_().f_46443_ || !((supremeCraftingPickupGateMixin = this) instanceof ResultSlotAccessor)) {
            return;
        }
        ResultSlotAccessor resultSlot = (ResultSlotAccessor)((Object)supremeCraftingPickupGateMixin);
        int required = SupremeDuplicationRecipe.requiredLevel(resultSlot.goetyMasteryOfMagic$getCraftSlots());
        if (required > 0 && MasteryData.getWizardry(player) < required) {
            cir.setReturnValue((Object)false);
        }
    }
}

