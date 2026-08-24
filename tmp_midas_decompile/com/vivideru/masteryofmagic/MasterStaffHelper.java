/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.SimpleMenuProvider
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.common.capabilities.ForgeCapabilities
 *  net.minecraftforge.network.NetworkHooks
 */
package com.vivideru.masteryofmagic;

import com.vivideru.masteryofmagic.capability.MasterStaffItemHandler;
import com.vivideru.masteryofmagic.client.inventory.container.MasterStaffContainer;
import com.vivideru.masteryofmagic.item.MasterStaffItem;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.network.NetworkHooks;

public final class MasterStaffHelper {
    private MasterStaffHelper() {
    }

    @Nullable
    public static MasterStaffItemHandler getHandler(ItemStack stack) {
        MasterStaffItemHandler masterStaffHandler;
        if (!(stack.m_41720_() instanceof MasterStaffItem)) {
            return null;
        }
        Object handler = stack.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
        return handler instanceof MasterStaffItemHandler ? (masterStaffHandler = (MasterStaffItemHandler)((Object)handler)) : null;
    }

    public static ItemStack getSelectedWand(ItemStack masterStaff) {
        MasterStaffItemHandler handler = MasterStaffHelper.getHandler(masterStaff);
        return handler == null ? ItemStack.f_41583_ : handler.getSelectedWand();
    }

    public static int getActiveSlot(ItemStack masterStaff) {
        MasterStaffItemHandler handler = MasterStaffHelper.getHandler(masterStaff);
        return handler == null ? 0 : handler.getActiveSlot();
    }

    public static int getSkin(ItemStack masterStaff) {
        MasterStaffItemHandler handler = MasterStaffHelper.getHandler(masterStaff);
        return handler == null ? 0 : handler.getSkin();
    }

    public static void replaceSelectedWand(ItemStack masterStaff, ItemStack replacement) {
        MasterStaffItemHandler handler = MasterStaffHelper.getHandler(masterStaff);
        if (handler == null) {
            return;
        }
        if (replacement.m_41619_() || handler.isItemValid(handler.getActiveSlot(), replacement)) {
            handler.setStackInSlot(handler.getActiveSlot(), replacement);
        }
    }

    public static void persistSelectedWand(ItemStack masterStaff, ItemStack selectedWand) {
        MasterStaffItemHandler handler = MasterStaffHelper.getHandler(masterStaff);
        if (handler == null) {
            return;
        }
        int slot = handler.getActiveSlot();
        if (selectedWand.m_41619_() || handler.isItemValid(slot, selectedWand)) {
            handler.setStackInSlot(slot, selectedWand);
        }
    }

    public static InteractionHand findHeldHand(Player player) {
        if (player.m_21205_().m_41720_() instanceof MasterStaffItem) {
            return InteractionHand.MAIN_HAND;
        }
        if (player.m_21206_().m_41720_() instanceof MasterStaffItem) {
            return InteractionHand.OFF_HAND;
        }
        return null;
    }

    public static void openMenu(ServerPlayer player, InteractionHand hand) {
        ItemStack stack = player.m_21120_(hand);
        MasterStaffItemHandler handler = MasterStaffHelper.getHandler(stack);
        if (handler == null) {
            return;
        }
        SimpleMenuProvider provider = new SimpleMenuProvider((id, inventory, menuPlayer) -> new MasterStaffContainer(id, inventory, handler, stack, hand), (Component)Component.m_237115_((String)stack.m_41778_()));
        NetworkHooks.openScreen((ServerPlayer)player, (MenuProvider)provider, buffer -> buffer.writeBoolean(hand == InteractionHand.MAIN_HAND));
    }

    public static void playSwitchSound(Player player) {
        player.m_9236_().m_6263_(null, player.m_20185_(), player.m_20186_(), player.m_20189_(), SoundEvents.f_144243_, SoundSource.PLAYERS, 0.8f, 1.15f);
    }
}

