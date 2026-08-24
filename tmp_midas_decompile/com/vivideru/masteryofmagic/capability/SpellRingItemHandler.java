/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.items.magic.IFocus
 *  com.Polarice3.Goety.common.items.handler.SoulUsingItemHandler
 *  javax.annotation.Nonnull
 *  net.minecraft.core.NonNullList
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.ItemStack
 */
package com.vivideru.masteryofmagic.capability;

import com.Polarice3.Goety.api.items.magic.IFocus;
import com.Polarice3.Goety.common.items.handler.SoulUsingItemHandler;
import javax.annotation.Nonnull;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class SpellRingItemHandler
extends SoulUsingItemHandler {
    public static final String ACTIVE_SLOT = "SpellRingActiveSlot";
    private final ItemStack ringStack;

    public SpellRingItemHandler(ItemStack itemStack) {
        super(itemStack);
        this.ringStack = itemStack;
        this.stacks = NonNullList.m_122780_((int)3, (Object)ItemStack.f_41583_);
    }

    public int getSlots() {
        return 3;
    }

    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return stack.m_41720_() instanceof IFocus;
    }

    public int getSlotLimit(int slot) {
        return 1;
    }

    protected void onContentsChanged(int slot) {
        CompoundTag nbt;
        nbt.m_128379_("goety-dirty", !(nbt = this.ringStack.m_41784_()).m_128471_("goety-dirty"));
    }

    public int getActiveSlot() {
        if (this.ringStack.m_41783_() == null) {
            return 0;
        }
        return Mth.m_14045_((int)this.ringStack.m_41783_().m_128451_(ACTIVE_SLOT), (int)0, (int)2);
    }

    public void setActiveSlot(int slot) {
        this.ringStack.m_41784_().m_128405_(ACTIVE_SLOT, Mth.m_14045_((int)slot, (int)0, (int)2));
    }

    public ItemStack getSlot() {
        return this.getStackInSlot(this.getActiveSlot());
    }

    public ItemStack extractItem() {
        return this.extractItem(this.getActiveSlot(), 1, false);
    }

    public ItemStack insertItem(ItemStack insert) {
        return this.insertItem(this.getActiveSlot(), insert, false);
    }

    public ItemStack getFocusInSlot(int slot) {
        return this.getStackInSlot(Mth.m_14045_((int)slot, (int)0, (int)2));
    }
}

