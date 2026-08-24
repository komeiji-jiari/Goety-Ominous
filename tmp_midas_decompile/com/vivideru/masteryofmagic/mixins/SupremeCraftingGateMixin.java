/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.CraftingContainer
 *  net.minecraft.world.inventory.CraftingMenu
 *  net.minecraft.world.inventory.ResultContainer
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.vivideru.masteryofmagic.mixins;

import com.vivideru.masteryofmagic.MasteryData;
import com.vivideru.masteryofmagic.SupremeDuplicationRecipe;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={CraftingMenu.class})
public class SupremeCraftingGateMixin {
    @Inject(method={"slotChangedCraftingGrid"}, at={@At(value="HEAD")}, cancellable=true)
    private static void gmom$gateDuplication(AbstractContainerMenu menu, Level level, Player player, CraftingContainer grid, ResultContainer result, CallbackInfo ci) {
        if (level.f_46443_) {
            return;
        }
        int required = SupremeDuplicationRecipe.requiredLevel(grid);
        if (required > 0 && MasteryData.getWizardry(player) < required) {
            result.m_6836_(0, ItemStack.f_41583_);
            menu.m_150404_(0, ItemStack.f_41583_);
            ci.cancel();
        }
    }
}

