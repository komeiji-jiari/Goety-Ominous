/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.items.magic.IWand
 *  com.Polarice3.Goety.common.items.handler.SoulUsingItemHandler
 *  javax.annotation.Nonnull
 *  net.minecraft.core.NonNullList
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.items.IItemHandler
 */
package com.vivideru.masteryofmagic.capability;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.items.handler.SoulUsingItemHandler;
import com.vivideru.masteryofmagic.item.MasterStaffItem;
import javax.annotation.Nonnull;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class MasterStaffItemHandler
extends SoulUsingItemHandler {
    public static final int WAND_SLOTS = 7;
    public static final String ACTIVE_SLOT = "MasterStaffActiveSlot";
    public static final String SKIN = "MasterStaffSkin";
    private final ItemStack masterStaff;

    public MasterStaffItemHandler(ItemStack masterStaff) {
        super(masterStaff);
        this.masterStaff = masterStaff;
        this.stacks = NonNullList.m_122780_((int)7, (Object)ItemStack.f_41583_);
    }

    public int getSlots() {
        return 7;
    }

    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return stack.m_41720_() instanceof IWand && !(stack.m_41720_() instanceof MasterStaffItem);
    }

    public int getSlotLimit(int slot) {
        return 1;
    }

    protected void onContentsChanged(int slot) {
        this.markDirty();
    }

    public void markDirty() {
        CompoundTag tag;
        tag.m_128379_("goety-dirty", !(tag = this.masterStaff.m_41784_()).m_128471_("goety-dirty"));
    }

    public int getActiveSlot() {
        return Mth.m_14045_((int)this.masterStaff.m_41784_().m_128451_(ACTIVE_SLOT), (int)0, (int)6);
    }

    public void setActiveSlot(int slot) {
        this.masterStaff.m_41784_().m_128405_(ACTIVE_SLOT, Mth.m_14045_((int)slot, (int)0, (int)6));
        this.markDirty();
    }

    public int getSkin() {
        return Mth.m_14045_((int)this.masterStaff.m_41784_().m_128451_(SKIN), (int)0, (int)7);
    }

    public void setSkin(int skin) {
        this.masterStaff.m_41784_().m_128405_(SKIN, Math.floorMod(skin, 8));
        this.markDirty();
    }

    public ItemStack getSelectedWand() {
        return this.getStackInSlot(this.getActiveSlot());
    }

    public boolean cycleSelectedWand() {
        int activeSlot = this.getActiveSlot();
        for (int offset = 1; offset <= 7; ++offset) {
            int candidate = (activeSlot + offset) % 7;
            if (this.getStackInSlot(candidate).m_41619_()) continue;
            if (candidate == activeSlot) {
                return false;
            }
            this.setActiveSlot(candidate);
            return true;
        }
        return false;
    }

    public ItemStack getSlot() {
        ItemStack selectedWand = this.getSelectedWand();
        if (selectedWand.m_41619_()) {
            return ItemStack.f_41583_;
        }
        return IWand.getFocus((ItemStack)selectedWand);
    }

    public ItemStack extractItem() {
        IItemHandler iItemHandler;
        ItemStack selectedWand = this.getSelectedWand();
        if (!selectedWand.m_41619_() && (iItemHandler = IWand.getItemHandler((ItemStack)selectedWand)) instanceof SoulUsingItemHandler) {
            SoulUsingItemHandler handler = (SoulUsingItemHandler)iItemHandler;
            ItemStack extracted = handler.extractItem();
            this.markDirty();
            return extracted;
        }
        return ItemStack.f_41583_;
    }

    public ItemStack insertItem(ItemStack insert) {
        IItemHandler iItemHandler;
        ItemStack selectedWand = this.getSelectedWand();
        if (!selectedWand.m_41619_() && (iItemHandler = IWand.getItemHandler((ItemStack)selectedWand)) instanceof SoulUsingItemHandler) {
            SoulUsingItemHandler handler = (SoulUsingItemHandler)iItemHandler;
            ItemStack remainder = handler.insertItem(insert);
            this.markDirty();
            return remainder;
        }
        return insert;
    }
}

