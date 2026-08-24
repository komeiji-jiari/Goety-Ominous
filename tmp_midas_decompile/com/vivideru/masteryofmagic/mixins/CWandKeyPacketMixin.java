/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.items.magic.IWand
 *  com.Polarice3.Goety.common.items.handler.SoulUsingItemHandler
 *  com.Polarice3.Goety.common.network.client.CWandKeyPacket
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.SimpleMenuProvider
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.common.capabilities.ForgeCapabilities
 *  net.minecraftforge.items.IItemHandler
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.NetworkHooks
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.vivideru.masteryofmagic.mixins;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.items.handler.SoulUsingItemHandler;
import com.Polarice3.Goety.common.network.client.CWandKeyPacket;
import com.vivideru.masteryofmagic.MasterStaffHelper;
import com.vivideru.masteryofmagic.SpellRingItem;
import com.vivideru.masteryofmagic.capability.MasterStaffItemHandler;
import com.vivideru.masteryofmagic.capability.SpellRingItemHandler;
import com.vivideru.masteryofmagic.client.inventory.container.MasterStaffFocusContainer;
import com.vivideru.masteryofmagic.client.inventory.container.SpellRingContainer;
import com.vivideru.masteryofmagic.item.MasterStaffItem;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={CWandKeyPacket.class})
public class CWandKeyPacketMixin {
    @Inject(method={"consume(Lcom/Polarice3/Goety/common/network/client/CWandKeyPacket;Ljava/util/function/Supplier;)V"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private static void goetyMasteryOfMagic$openSpellRingMenu(CWandKeyPacket packet, Supplier<NetworkEvent.Context> ctx, CallbackInfo ci) {
        ServerPlayer player = ctx.get().getSender();
        if (player == null) {
            return;
        }
        ItemStack mainHand = player.m_21205_();
        if (mainHand.m_41720_() instanceof MasterStaffItem) {
            CWandKeyPacketMixin.openSelectedWandMenu(ctx, player, mainHand, InteractionHand.MAIN_HAND);
            ctx.get().setPacketHandled(true);
            ci.cancel();
            return;
        }
        if (!(mainHand.m_41720_() instanceof SpellRingItem) && player.m_21206_().m_41720_() instanceof MasterStaffItem) {
            CWandKeyPacketMixin.openSelectedWandMenu(ctx, player, player.m_21206_(), InteractionHand.OFF_HAND);
            ctx.get().setPacketHandled(true);
            ci.cancel();
            return;
        }
        ItemStack stack = player.m_21205_();
        InteractionHand hand = InteractionHand.MAIN_HAND;
        if (!(stack.m_41720_() instanceof SpellRingItem)) {
            stack = player.m_21206_();
            hand = InteractionHand.OFF_HAND;
        }
        if (!(stack.m_41720_() instanceof SpellRingItem)) {
            return;
        }
        ItemStack finalStack = stack;
        InteractionHand finalHand = hand;
        ctx.get().enqueueWork(() -> finalStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            if (handler instanceof SpellRingItemHandler) {
                SpellRingItemHandler spellRingHandler = (SpellRingItemHandler)((Object)((Object)handler));
                SimpleMenuProvider provider = new SimpleMenuProvider((id, inventory, playerEntity) -> new SpellRingContainer(id, inventory, spellRingHandler, finalStack), (Component)Component.m_237115_((String)finalStack.m_41778_()));
                NetworkHooks.openScreen((ServerPlayer)player, (MenuProvider)provider, buffer -> buffer.writeBoolean(finalHand == InteractionHand.MAIN_HAND));
            }
        }));
        ctx.get().setPacketHandled(true);
        ci.cancel();
    }

    private static void openSelectedWandMenu(Supplier<NetworkEvent.Context> ctx, ServerPlayer player, ItemStack masterStaff, InteractionHand hand) {
        ctx.get().enqueueWork(() -> {
            MasterStaffItemHandler masterStaffHandler = MasterStaffHelper.getHandler(masterStaff);
            if (masterStaffHandler == null) {
                return;
            }
            ItemStack selectedWand = masterStaffHandler.getSelectedWand();
            if (selectedWand.m_41619_()) {
                return;
            }
            IItemHandler patt4447$temp = IWand.getItemHandler((ItemStack)selectedWand);
            if (!(patt4447$temp instanceof SoulUsingItemHandler)) {
                return;
            }
            SoulUsingItemHandler selectedHandler = (SoulUsingItemHandler)patt4447$temp;
            SimpleMenuProvider provider = new SimpleMenuProvider((id, inventory, menuPlayer) -> new MasterStaffFocusContainer(id, inventory, selectedHandler, masterStaffHandler, selectedWand, masterStaff, hand), selectedWand.m_41786_());
            NetworkHooks.openScreen((ServerPlayer)player, (MenuProvider)provider, buffer -> buffer.writeBoolean(hand == InteractionHand.MAIN_HAND));
        });
    }
}

