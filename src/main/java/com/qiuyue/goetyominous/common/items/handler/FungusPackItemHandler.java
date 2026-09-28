package com.qiuyue.goetyominous.common.items.handler;

import com.Polarice3.Goety.common.items.BerserkFungusItem;
import com.Polarice3.Goety.common.items.BlastFungusItem;
import com.Polarice3.Goety.common.items.SnapFungusItem;
import com.qiuyue.goetyominous.common.items.AcidFungusItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    @Nullable
    public static FungusPackItemHandler get(ItemStack stack) {
        net.minecraftforge.items.IItemHandler handler = stack.getCapability(
                        net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER)
                .orElse(null);
        return handler instanceof FungusPackItemHandler fungusPack ? fungusPack : null;
    }
}