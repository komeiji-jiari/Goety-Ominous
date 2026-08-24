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
 *  net.minecraftforge.common.capabilities.ForgeCapabilities
 *  net.minecraftforge.items.IItemHandler
 *  net.minecraftforge.items.SlotItemHandler
 */
package com.vivideru.masteryofmagic.client.inventory.container;

import com.vivideru.masteryofmagic.capability.SpellRingItemHandler;
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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SpellRingContainer
extends AbstractContainerMenu {
    private final ItemStack stack;

    public static SpellRingContainer createContainerClientSide(int id, Inventory inventory, FriendlyByteBuf buffer) {
        InteractionHand hand = buffer.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        ItemStack stack = hand == InteractionHand.MAIN_HAND ? inventory.f_35978_.m_21205_() : inventory.f_35978_.m_21206_();
        SpellRingItemHandler handler = stack.getCapability(ForgeCapabilities.ITEM_HANDLER).map(itemHandler -> (SpellRingItemHandler)((Object)itemHandler)).orElse(new SpellRingItemHandler(ItemStack.f_41583_));
        return new SpellRingContainer(id, inventory, handler, stack);
    }

    public SpellRingContainer(int id, Inventory playerInventory, SpellRingItemHandler handler, ItemStack stack) {
        super((MenuType)GoetyMasteryOfMagicModMenus.SPELL_RING.get(), id);
        this.stack = stack;
        this.m_38897_((Slot)new SlotItemHandler((IItemHandler)handler, 0, 24, 35));
        this.m_38897_((Slot)new SlotItemHandler((IItemHandler)handler, 1, 80, 35));
        this.m_38897_((Slot)new SlotItemHandler((IItemHandler)handler, 2, 136, 35));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.m_38897_(new Slot((Container)playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int k = 0; k < 9; ++k) {
            this.m_38897_(new Slot((Container)playerInventory, k, 8 + k * 18, 142));
        }
    }

    public boolean m_6875_(Player player) {
        return (player.m_21205_() == this.stack || player.m_21206_() == this.stack) && !this.stack.m_41619_();
    }

    public ItemStack m_7648_(Player player, int index) {
        ItemStack itemstack = ItemStack.f_41583_;
        Slot slot = (Slot)this.f_38839_.get(index);
        if (slot != null && slot.m_6657_()) {
            ItemStack itemstack1 = slot.m_7993_();
            itemstack = itemstack1.m_41777_();
            if (index >= 0 && index < 3 ? !this.m_38903_(itemstack1, 3, 39, true) : !this.m_38903_(itemstack1, 0, 3, false)) {
                return ItemStack.f_41583_;
            }
            if (itemstack1.m_41619_()) {
                slot.m_5852_(ItemStack.f_41583_);
            } else {
                slot.m_6654_();
            }
            if (itemstack1.m_41613_() == itemstack.m_41613_()) {
                return ItemStack.f_41583_;
            }
            slot.m_142406_(player, itemstack1);
        }
        return itemstack;
    }
}

