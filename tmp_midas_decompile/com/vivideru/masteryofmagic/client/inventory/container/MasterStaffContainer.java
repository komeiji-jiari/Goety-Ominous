/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.world.Container
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.items.IItemHandler
 *  net.minecraftforge.items.SlotItemHandler
 */
package com.vivideru.masteryofmagic.client.inventory.container;

import com.vivideru.masteryofmagic.MasterStaffHelper;
import com.vivideru.masteryofmagic.capability.MasterStaffItemHandler;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class MasterStaffContainer
extends AbstractContainerMenu {
    public static final int WAND_SLOT_COUNT = 7;
    public static final int SKIN_BUTTON_ID = 100;
    private final MasterStaffItemHandler handler;
    private final ItemStack masterStaff;
    private final InteractionHand hand;

    public static MasterStaffContainer createContainerClientSide(int id, Inventory inventory, FriendlyByteBuf buffer) {
        InteractionHand hand = buffer.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        ItemStack stack = inventory.f_35978_.m_21120_(hand);
        MasterStaffItemHandler handler = MasterStaffHelper.getHandler(stack);
        if (handler == null) {
            handler = new MasterStaffItemHandler(ItemStack.f_41583_);
        }
        return new MasterStaffContainer(id, inventory, handler, stack, hand);
    }

    public MasterStaffContainer(int id, Inventory playerInventory, MasterStaffItemHandler handler, ItemStack masterStaff, InteractionHand hand) {
        super((MenuType)GoetyMasteryOfMagicModMenus.MASTER_STAFF.get(), id);
        this.handler = handler;
        this.masterStaff = masterStaff;
        this.hand = hand;
        for (int slot = 0; slot < 7; ++slot) {
            this.m_38897_((Slot)new SlotItemHandler((IItemHandler)handler, slot, 34 + slot * 18, 43));
        }
        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 9; ++column) {
                this.m_38897_(new Slot((Container)playerInventory, column + row * 9 + 9, 16 + column * 18, 94 + row * 18));
            }
        }
        for (int column = 0; column < 9; ++column) {
            this.m_38897_(new Slot((Container)playerInventory, column, 16 + column * 18, 152));
        }
    }

    public int getActiveSlot() {
        return this.handler.getActiveSlot();
    }

    public int getSkin() {
        return this.handler.getSkin();
    }

    public boolean m_6366_(Player player, int id) {
        if (id >= 0 && id < 7) {
            this.handler.setActiveSlot(id);
            MasterStaffHelper.playSwitchSound(player);
            return true;
        }
        if (id == 100) {
            this.handler.setSkin(this.handler.getSkin() + 1);
            MasterStaffHelper.playSwitchSound(player);
            return true;
        }
        return false;
    }

    public boolean m_6875_(Player player) {
        return !this.masterStaff.m_41619_() && player.m_21120_(this.hand) == this.masterStaff;
    }

    public ItemStack m_7648_(Player player, int index) {
        ItemStack result = ItemStack.f_41583_;
        Slot slot = (Slot)this.f_38839_.get(index);
        if (!slot.m_6657_()) {
            return result;
        }
        ItemStack moving = slot.m_7993_();
        result = moving.m_41777_();
        if (index < 7 ? !this.m_38903_(moving, 7, this.f_38839_.size(), true) : !this.m_38903_(moving, 0, 7, false)) {
            return ItemStack.f_41583_;
        }
        if (moving.m_41619_()) {
            slot.m_5852_(ItemStack.f_41583_);
        } else {
            slot.m_6654_();
        }
        if (moving.m_41613_() == result.m_41613_()) {
            return ItemStack.f_41583_;
        }
        slot.m_142406_(player, moving);
        return result;
    }
}

