/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.items.magic.IWand
 *  com.Polarice3.Goety.utils.TotemFinder
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.common.capabilities.ForgeCapabilities
 *  net.minecraftforge.fml.ModList
 *  net.minecraftforge.items.IItemHandler
 *  net.minecraftforge.registries.ForgeRegistries
 *  org.apache.commons.lang3.tuple.ImmutableTriple
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 *  top.theillusivec4.curios.api.CuriosApi
 */
package com.vivideru.masteryofmagic.mixins;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.utils.TotemFinder;
import com.vivideru.masteryofmagic.MasterStaffHelper;
import com.vivideru.masteryofmagic.item.MasterStaffItem;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;

@Mixin(value={TotemFinder.class})
public class FocusBagPriorityMixin {
    private static final ItemStack EMPTY = ItemStack.f_41583_;

    @Inject(method={"findBag"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private static void prioritizeBag(Player player, CallbackInfoReturnable<ItemStack> cir) {
        ImmutableTriple result;
        if (player == null) {
            return;
        }
        ItemStack wand = FocusBagPriorityMixin.findHeldWand(player);
        if (!wand.m_41782_() || !wand.m_41783_().m_128403_("BoundFocusBag")) {
            return;
        }
        UUID id = wand.m_41783_().m_128342_("BoundFocusBag");
        ItemStack found = FocusBagPriorityMixin.findMatchingBagInInventory(player, id);
        if (!found.m_41619_()) {
            cir.setReturnValue((Object)found);
            return;
        }
        if (ModList.get().isLoaded("curios")) {
            AtomicReference<ItemStack> curioBag = new AtomicReference<ItemStack>(ItemStack.f_41583_);
            CuriosApi.getCuriosInventory((LivingEntity)player).ifPresent(handler -> handler.findFirstCurio(stack -> FocusBagPriorityMixin.matchesID(stack, id)).ifPresent(slotResult -> curioBag.set(slotResult.stack())));
            if (!curioBag.get().m_41619_()) {
                cir.setReturnValue((Object)curioBag.get());
                return;
            }
        }
        if (ModList.get().isLoaded("sophisticatedbackpacks") && ModList.get().isLoaded("curios") && (result = (ImmutableTriple)CuriosApi.getCuriosHelper().findEquippedCurio(FocusBagPriorityMixin::isBackpack, (LivingEntity)player).orElse(null)) != null) {
            ItemStack backpack = (ItemStack)result.getRight();
            backpack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                ItemStack bag = FocusBagPriorityMixin.findMatchingBagInHandler(handler, id);
                if (!bag.m_41619_()) {
                    cir.setReturnValue((Object)bag);
                }
            });
            if (cir.getReturnValue() != null) {
                return;
            }
        }
        if (ModList.get().isLoaded("sophisticatedbackpacks")) {
            for (ItemStack stack : player.m_150109_().f_35974_) {
                if (!FocusBagPriorityMixin.isBackpack(stack)) continue;
                stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                    ItemStack bag = FocusBagPriorityMixin.findMatchingBagInHandler(handler, id);
                    if (!bag.m_41619_()) {
                        cir.setReturnValue((Object)bag);
                    }
                });
                if (cir.getReturnValue() == null) continue;
                return;
            }
        }
        cir.setReturnValue((Object)ItemStack.f_41583_);
    }

    private static ItemStack findHeldWand(Player player) {
        ItemStack mainHand = player.m_21205_();
        if (mainHand.m_41720_() instanceof MasterStaffItem) {
            return MasterStaffHelper.getSelectedWand(mainHand);
        }
        if (mainHand.m_41720_() instanceof IWand) {
            return mainHand;
        }
        ItemStack offHand = player.m_21206_();
        if (offHand.m_41720_() instanceof MasterStaffItem) {
            return MasterStaffHelper.getSelectedWand(offHand);
        }
        if (offHand.m_41720_() instanceof IWand) {
            return offHand;
        }
        return ItemStack.f_41583_;
    }

    private static boolean matchesID(ItemStack stack, UUID id) {
        return FocusBagPriorityMixin.isFocusContainer(stack) && stack.m_41782_() && stack.m_41783_().m_128403_("FocusBagID") && stack.m_41783_().m_128342_("FocusBagID").equals(id);
    }

    private static boolean isFocusContainer(ItemStack stack) {
        if (stack.m_41619_()) {
            return false;
        }
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey((Object)stack.m_41720_());
        return itemId != null && "goety".equals(itemId.m_135827_()) && ("focus_bag".equals(itemId.m_135815_()) || "focus_pack".equals(itemId.m_135815_()));
    }

    private static boolean isBackpack(ItemStack stack) {
        if (stack.m_41619_()) {
            return false;
        }
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey((Object)stack.m_41720_());
        return itemId != null && "sophisticatedbackpacks".equals(itemId.m_135827_());
    }

    private static ItemStack findMatchingBagInInventory(Player player, UUID id) {
        for (int slot = 0; slot < player.m_150109_().m_6643_(); ++slot) {
            ItemStack stack = player.m_150109_().m_8020_(slot);
            if (!FocusBagPriorityMixin.matchesID(stack, id)) continue;
            return stack;
        }
        return EMPTY;
    }

    private static ItemStack findMatchingBagInHandler(IItemHandler handler, UUID id) {
        for (int i = 0; i < handler.getSlots(); ++i) {
            ItemStack s = handler.getStackInSlot(i);
            if (!FocusBagPriorityMixin.matchesID(s, id)) continue;
            return s;
        }
        return EMPTY;
    }
}

