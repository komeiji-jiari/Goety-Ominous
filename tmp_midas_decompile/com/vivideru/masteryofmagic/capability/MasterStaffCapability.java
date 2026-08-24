/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.minecraft.core.Direction
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.common.capabilities.Capability
 *  net.minecraftforge.common.capabilities.ForgeCapabilities
 *  net.minecraftforge.common.capabilities.ICapabilitySerializable
 *  net.minecraftforge.common.util.LazyOptional
 *  net.minecraftforge.items.IItemHandler
 */
package com.vivideru.masteryofmagic.capability;

import com.vivideru.masteryofmagic.capability.MasterStaffItemHandler;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

public class MasterStaffCapability
implements ICapabilitySerializable<Tag> {
    private final ItemStack stack;
    private final LazyOptional<IItemHandler> holder = LazyOptional.of(this::getHandler);
    private MasterStaffItemHandler handler;

    public MasterStaffCapability(ItemStack stack) {
        this.stack = stack;
    }

    @Nonnull
    private MasterStaffItemHandler getHandler() {
        if (this.handler == null) {
            this.handler = new MasterStaffItemHandler(this.stack);
        }
        return this.handler;
    }

    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
        return ForgeCapabilities.ITEM_HANDLER.orEmpty(capability, this.holder);
    }

    public Tag serializeNBT() {
        return this.getHandler().serializeNBT();
    }

    public void deserializeNBT(Tag tag) {
        if (tag instanceof CompoundTag) {
            CompoundTag compoundTag = (CompoundTag)tag;
            this.getHandler().deserializeNBT(compoundTag);
        }
    }
}

