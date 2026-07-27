package com.qiuyue.goetyominus.common.items.handler;

import com.Polarice3.Goety.common.items.BerserkFungusItem;
import com.Polarice3.Goety.common.items.BlastFungusItem;
import com.Polarice3.Goety.common.items.SnapFungusItem;
import com.qiuyue.goetyominus.common.items.AcidFungusItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class FungusPackItemHandler extends ItemStackHandler {

    public FungusPackItemHandler() {
        super(1);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return stack.getItem() instanceof SnapFungusItem
                || stack.getItem() instanceof BlastFungusItem
                || stack.getItem() instanceof BerserkFungusItem
                || stack.getItem() instanceof AcidFungusItem;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    public ItemStack getFungus() {
        return this.getStackInSlot(0);
    }

    public ItemStack insertFungus(ItemStack stack) {
        return this.insertItem(0, stack, false);
    }

    public ItemStack extractFungus() {
        return this.extractItem(0, 1, false);
    }

    public static FungusPackItemHandler get(ItemStack stack) {
        return (FungusPackItemHandler) stack.getCapability(
                        net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER)
                .orElseThrow(() -> new IllegalArgumentException("No handler for " + stack));
    }
}