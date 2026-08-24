/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.world.item.ItemStack
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.vivideru.masteryofmagic.mixins;

import com.vivideru.masteryofmagic.MasterStaffHelper;
import com.vivideru.masteryofmagic.item.MasterStaffItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Minecraft.class}, priority=900)
public abstract class BetterCombatMinecraftClientMixin {
    @Inject(method={"areItemStackEqual(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"}, at={@At(value="HEAD")}, cancellable=true, require=0, remap=false)
    private static void goetyMasteryOfMagic$keepMasterStaffUpswingStable(ItemStack heldStack, ItemStack upswingStack, CallbackInfoReturnable<Boolean> cir) {
        if (!(heldStack.m_41720_() instanceof MasterStaffItem) || !(upswingStack.m_41720_() instanceof MasterStaffItem)) {
            return;
        }
        ItemStack heldWand = MasterStaffHelper.getSelectedWand(heldStack);
        ItemStack upswingWand = MasterStaffHelper.getSelectedWand(upswingStack);
        boolean sameSelection = MasterStaffHelper.getActiveSlot(heldStack) == MasterStaffHelper.getActiveSlot(upswingStack) && (heldWand.m_41619_() ? upswingWand.m_41619_() : !upswingWand.m_41619_() && heldWand.m_41720_() == upswingWand.m_41720_());
        cir.setReturnValue((Object)sameSelection);
    }
}

