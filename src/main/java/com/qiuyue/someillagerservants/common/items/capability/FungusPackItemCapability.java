package com.qiuyue.someillagerservants.common.items.capability;

import com.qiuyue.someillagerservants.common.items.handler.FungusPackItemHandler;
import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FungusPackItemCapability implements ICapabilitySerializable<Tag> {

    private final ItemStack stack;
    private final LazyOptional<IItemHandler> holder;
    private FungusPackItemHandler handler;

    public FungusPackItemCapability(ItemStack stack) {
        this.stack = stack;
        this.handler = new FungusPackItemHandler();
        this.holder = LazyOptional.of(() -> handler);
    }

    private FungusPackItemHandler getHandler() {
        if (handler == null) {
            handler = new FungusPackItemHandler();
        }
        return handler;
    }

    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return holder.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public Tag serializeNBT() {
        return getHandler().serializeNBT();
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        getHandler().deserializeNBT((net.minecraft.nbt.CompoundTag) nbt);
    }
}
