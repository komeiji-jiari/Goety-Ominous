/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.client.inventory.container.SoulItemContainer
 *  com.Polarice3.Goety.common.items.handler.SoulUsingItemHandler
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.ClickType
 *  net.minecraft.world.item.ItemStack
 */
package com.vivideru.masteryofmagic.client.inventory.container;

import com.Polarice3.Goety.client.inventory.container.SoulItemContainer;
import com.Polarice3.Goety.common.items.handler.SoulUsingItemHandler;
import com.vivideru.masteryofmagic.capability.MasterStaffItemHandler;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

public class MasterStaffFocusContainer
extends SoulItemContainer {
    private final ItemStack masterStaff;
    private final ItemStack selectedWand;
    private final InteractionHand hand;
    private final MasterStaffItemHandler masterStaffHandler;

    public MasterStaffFocusContainer(int id, Inventory inventory, SoulUsingItemHandler selectedWandHandler, MasterStaffItemHandler masterStaffHandler, ItemStack selectedWand, ItemStack masterStaff, InteractionHand hand) {
        super(id, inventory, selectedWandHandler, selectedWand, hand);
        this.masterStaffHandler = masterStaffHandler;
        this.masterStaff = masterStaff;
        this.selectedWand = selectedWand;
        this.hand = hand;
    }

    public boolean m_6875_(Player player) {
        return !this.masterStaff.m_41619_() && player.m_21120_(this.hand) == this.masterStaff && this.masterStaffHandler.getSelectedWand() == this.selectedWand;
    }

    public void m_150399_(int slotId, int button, ClickType clickType, Player player) {
        super.m_150399_(slotId, button, clickType, player);
        this.markMasterStaffDirty();
    }

    public ItemStack m_7648_(Player player, int index) {
        ItemStack result = super.m_7648_(player, index);
        this.markMasterStaffDirty();
        return result;
    }

    public void m_6877_(Player player) {
        this.markMasterStaffDirty();
        super.m_6877_(player);
    }

    private void markMasterStaffDirty() {
        this.masterStaffHandler.markDirty();
    }
}

