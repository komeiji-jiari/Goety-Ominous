/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.blocks.entities.PedestalBlockEntity
 *  com.Polarice3.Goety.common.ritual.Ritual
 *  com.Polarice3.Goety.utils.ItemHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BucketItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.vivideru.masteryofmagic.mixins;

import com.Polarice3.Goety.common.blocks.entities.PedestalBlockEntity;
import com.Polarice3.Goety.common.ritual.Ritual;
import com.Polarice3.Goety.utils.ItemHelper;
import com.vivideru.masteryofmagic.DynamicArmorIngredientHelper;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Ritual.class})
public abstract class RitualDynamicIngredientMixin {
    @Inject(method={"matchesAdditionalIngredients"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void goetyMasteryOfMagic$matchesDynamicArmorIngredients(Player player, List<Ingredient> additionalIngredients, List<ItemStack> items, CallbackInfoReturnable<Boolean> cir) {
        if (additionalIngredients.size() != items.size()) {
            cir.setReturnValue((Object)false);
            return;
        }
        if (additionalIngredients.isEmpty()) {
            cir.setReturnValue((Object)true);
            return;
        }
        ArrayList<ItemStack> remainingItems = new ArrayList<ItemStack>(items);
        for (Ingredient ingredient : additionalIngredients) {
            boolean matched = false;
            for (int i = 0; i < remainingItems.size(); ++i) {
                ItemStack stack = (ItemStack)remainingItems.get(i);
                if (!DynamicArmorIngredientHelper.test(ingredient, stack)) continue;
                matched = true;
                remainingItems.remove(i);
                break;
            }
            if (matched) continue;
            cir.setReturnValue((Object)false);
            return;
        }
        cir.setReturnValue((Object)true);
    }

    @Inject(method={"consumeAdditionalIngredient"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void goetyMasteryOfMagic$consumeDynamicArmorIngredient(Level world, BlockPos darkAltarPos, List<PedestalBlockEntity> pedestals, Ingredient ingredient, List<ItemStack> consumedIngredients, CallbackInfoReturnable<Boolean> cir) {
        for (PedestalBlockEntity pedestal : pedestals) {
            boolean consumed = pedestal.itemStackHandler.map(handler -> {
                BucketItem bucketItem;
                ItemStack simulated = handler.extractItem(0, 1, true);
                if (!DynamicArmorIngredientHelper.test(ingredient, simulated)) {
                    return false;
                }
                ItemStack extracted = handler.extractItem(0, 1, false);
                if (extracted.m_41619_()) {
                    return false;
                }
                consumedIngredients.add(extracted);
                Item patt3422$temp = extracted.m_41720_();
                if (patt3422$temp instanceof BucketItem && !(bucketItem = (BucketItem)patt3422$temp).getFluid().m_76145_().m_76178_()) {
                    ItemHelper.addItemEntity((Level)world, (BlockPos)pedestal.m_58899_().m_7494_(), (ItemStack)new ItemStack((ItemLike)Items.f_42446_));
                    world.m_5594_(null, pedestal.m_58899_(), SoundEvents.f_11778_, SoundSource.BLOCKS, 0.7f, 0.7f);
                } else if (extracted.hasCraftingRemainingItem()) {
                    ItemHelper.addItemEntity((Level)world, (BlockPos)pedestal.m_58899_().m_7494_(), (ItemStack)extracted.getCraftingRemainingItem());
                }
                handler.setStackInSlot(0, ItemStack.f_41583_);
                world.m_5594_(null, pedestal.m_58899_(), SoundEvents.f_12019_, SoundSource.BLOCKS, 0.7f, 0.7f);
                return true;
            }).orElse(false);
            if (!consumed) continue;
            cir.setReturnValue((Object)true);
            return;
        }
        cir.setReturnValue((Object)false);
    }
}

