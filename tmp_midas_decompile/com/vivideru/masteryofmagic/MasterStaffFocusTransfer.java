/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.items.magic.IWand
 *  com.Polarice3.Goety.common.items.handler.FocusBagItemHandler
 *  com.Polarice3.Goety.common.items.handler.SoulUsingItemHandler
 *  com.Polarice3.Goety.init.ModSounds
 *  com.Polarice3.Goety.utils.TotemFinder
 *  javax.annotation.Nullable
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.items.IItemHandler
 */
package com.vivideru.masteryofmagic;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.items.handler.FocusBagItemHandler;
import com.Polarice3.Goety.common.items.handler.SoulUsingItemHandler;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.TotemFinder;
import com.vivideru.masteryofmagic.MasterStaffHelper;
import com.vivideru.masteryofmagic.capability.MasterStaffItemHandler;
import com.vivideru.masteryofmagic.item.MasterStaffItem;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public final class MasterStaffFocusTransfer {
    private MasterStaffFocusTransfer() {
    }

    public static boolean hasActiveMasterStaff(Player player) {
        return !MasterStaffFocusTransfer.findActiveMasterStaff(player).m_41619_();
    }

    public static void addFocusToBag(ServerPlayer player) {
        Selection selection = MasterStaffFocusTransfer.resolve((Player)player);
        if (selection == null) {
            return;
        }
        ItemStack bag = TotemFinder.findBag((Player)player);
        if (bag.m_41619_()) {
            return;
        }
        FocusBagItemHandler bagHandler = FocusBagItemHandler.get((ItemStack)bag);
        if (selection.wandHandler().getSlot().m_41619_()) {
            return;
        }
        for (int slot = 1; slot < bagHandler.getSlots(); ++slot) {
            if (!bagHandler.getStackInSlot(slot).m_41619_()) continue;
            ItemStack extracted = selection.wandHandler().extractItem();
            if (extracted.m_41619_()) {
                return;
            }
            bagHandler.setStackInSlot(slot, extracted);
            MasterStaffFocusTransfer.commit(player, selection);
            MasterStaffFocusTransfer.playSound(player);
            return;
        }
    }

    public static void addFocusToInventory(ServerPlayer player) {
        Selection selection = MasterStaffFocusTransfer.resolve((Player)player);
        if (selection == null || selection.wandHandler().getSlot().m_41619_()) {
            return;
        }
        for (int slot = 0; slot < player.m_150109_().f_35974_.size(); ++slot) {
            if (!player.m_150109_().m_8020_(slot).m_41619_()) continue;
            ItemStack extracted = selection.wandHandler().extractItem();
            if (extracted.m_41619_()) {
                return;
            }
            player.m_150109_().m_6836_(slot, extracted);
            MasterStaffFocusTransfer.commit(player, selection);
            MasterStaffFocusTransfer.playSound(player);
            return;
        }
    }

    public static void swapFocusWithBag(ServerPlayer player, int slot) {
        ItemStack remainder;
        Selection selection = MasterStaffFocusTransfer.resolve((Player)player);
        if (selection == null) {
            return;
        }
        ItemStack bag = TotemFinder.findBag((Player)player);
        if (bag.m_41619_()) {
            return;
        }
        FocusBagItemHandler bagHandler = FocusBagItemHandler.get((ItemStack)bag);
        if (slot < 0 || slot >= bagHandler.getSlots()) {
            return;
        }
        ItemStack target = bagHandler.getStackInSlot(slot).m_41777_();
        if (!target.m_41619_() && !selection.wandHandler().isItemValid(0, target)) {
            return;
        }
        ItemStack extracted = selection.wandHandler().extractItem();
        ItemStack itemStack = remainder = target.m_41619_() ? ItemStack.f_41583_ : selection.wandHandler().insertItem(target);
        if (!remainder.m_41619_()) {
            if (!extracted.m_41619_()) {
                selection.wandHandler().insertItem(extracted);
            }
            return;
        }
        bagHandler.setStackInSlot(slot, extracted);
        MasterStaffFocusTransfer.commit(player, selection);
        MasterStaffFocusTransfer.playSound(player);
    }

    public static void swapFocusWithInventory(ServerPlayer player, int slot) {
        ItemStack remainder;
        Selection selection = MasterStaffFocusTransfer.resolve((Player)player);
        if (selection == null || slot < 0 || slot >= player.m_150109_().m_6643_()) {
            return;
        }
        ItemStack target = player.m_150109_().m_8020_(slot).m_41777_();
        if (!target.m_41619_() && !selection.wandHandler().isItemValid(0, target)) {
            return;
        }
        ItemStack extracted = selection.wandHandler().extractItem();
        ItemStack itemStack = remainder = target.m_41619_() ? ItemStack.f_41583_ : selection.wandHandler().insertItem(target);
        if (!remainder.m_41619_()) {
            if (!extracted.m_41619_()) {
                selection.wandHandler().insertItem(extracted);
            }
            return;
        }
        player.m_150109_().m_6836_(slot, extracted);
        MasterStaffFocusTransfer.commit(player, selection);
        MasterStaffFocusTransfer.playSound(player);
    }

    private static ItemStack findActiveMasterStaff(Player player) {
        ItemStack mainHand = player.m_21205_();
        if (mainHand.m_41720_() instanceof MasterStaffItem) {
            return mainHand;
        }
        if (mainHand.m_41720_() instanceof IWand) {
            return ItemStack.f_41583_;
        }
        ItemStack offHand = player.m_21206_();
        return offHand.m_41720_() instanceof MasterStaffItem ? offHand : ItemStack.f_41583_;
    }

    @Nullable
    private static Selection resolve(Player player) {
        IItemHandler iItemHandler;
        ItemStack masterStaff = MasterStaffFocusTransfer.findActiveMasterStaff(player);
        MasterStaffItemHandler masterHandler = MasterStaffHelper.getHandler(masterStaff);
        if (masterHandler == null) {
            return null;
        }
        int slot = masterHandler.getActiveSlot();
        ItemStack selectedWand = masterHandler.getStackInSlot(slot);
        if (selectedWand.m_41619_() || !((iItemHandler = IWand.getItemHandler((ItemStack)selectedWand)) instanceof SoulUsingItemHandler)) {
            return null;
        }
        SoulUsingItemHandler wandHandler = (SoulUsingItemHandler)iItemHandler;
        return new Selection(masterHandler, slot, selectedWand, wandHandler);
    }

    private static void commit(ServerPlayer player, Selection selection) {
        selection.masterHandler().setStackInSlot(selection.slot(), selection.selectedWand());
        player.m_150109_().m_6596_();
        player.f_36095_.m_38946_();
        player.f_36096_.m_38946_();
    }

    private static void playSound(ServerPlayer player) {
        player.m_9236_().m_5594_(null, player.m_20183_(), (SoundEvent)ModSounds.FOCUS_PICK.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    private record Selection(MasterStaffItemHandler masterHandler, int slot, ItemStack selectedWand, SoulUsingItemHandler wandHandler) {
    }
}

