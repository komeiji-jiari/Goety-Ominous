/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.items.magic.IWand
 *  com.Polarice3.Goety.utils.WandUtil
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.vivideru.masteryofmagic.mixins;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.utils.WandUtil;
import com.vivideru.masteryofmagic.MasterStaffHelper;
import com.vivideru.masteryofmagic.SpellRingHelper;
import com.vivideru.masteryofmagic.item.MasterStaffItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={WandUtil.class})
public class WandUtilMixin {
    @Inject(method={"findWand"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private static void goetyMasteryOfMagic$findSpellRingAsWand(LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack offHand;
        if (!(livingEntity instanceof Player)) {
            return;
        }
        Player player = (Player)livingEntity;
        ItemStack castingRing = SpellRingHelper.getCastingRing(player);
        if (!castingRing.m_41619_()) {
            cir.setReturnValue((Object)castingRing);
            return;
        }
        ItemStack mainHand = player.m_21205_();
        if (mainHand.m_41720_() instanceof MasterStaffItem) {
            cir.setReturnValue((Object)MasterStaffHelper.getSelectedWand(mainHand));
            return;
        }
        if (!(mainHand.m_41720_() instanceof IWand) && (offHand = player.m_21206_()).m_41720_() instanceof MasterStaffItem) {
            cir.setReturnValue((Object)MasterStaffHelper.getSelectedWand(offHand));
            return;
        }
        if (player.m_21205_().m_41720_() instanceof IWand) {
            return;
        }
        if (player.m_21206_().m_41720_() instanceof IWand) {
            return;
        }
        ItemStack ring = SpellRingHelper.findSpellRing(player);
        if (!ring.m_41619_()) {
            cir.setReturnValue((Object)ring);
        }
    }

    @Inject(method={"findFocus"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private static void goetyMasteryOfMagic$findSpellRingFocus(LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack focus;
        ItemStack offMasterStaff;
        if (!(livingEntity instanceof Player)) {
            return;
        }
        Player player = (Player)livingEntity;
        ItemStack castingFocus = SpellRingHelper.getCastingFocus(player);
        if (!castingFocus.m_41619_()) {
            cir.setReturnValue((Object)castingFocus);
            return;
        }
        ItemStack mainMasterStaff = player.m_21205_();
        if (mainMasterStaff.m_41720_() instanceof MasterStaffItem) {
            ItemStack selectedWand = MasterStaffHelper.getSelectedWand(mainMasterStaff);
            cir.setReturnValue((Object)(selectedWand.m_41619_() ? ItemStack.f_41583_ : IWand.getFocus((ItemStack)selectedWand)));
            return;
        }
        if (!(mainMasterStaff.m_41720_() instanceof IWand) && (offMasterStaff = player.m_21206_()).m_41720_() instanceof MasterStaffItem) {
            ItemStack selectedWand = MasterStaffHelper.getSelectedWand(offMasterStaff);
            cir.setReturnValue((Object)(selectedWand.m_41619_() ? ItemStack.f_41583_ : IWand.getFocus((ItemStack)selectedWand)));
            return;
        }
        ItemStack mainHand = player.m_21205_();
        if (mainHand.m_41720_() instanceof IWand && !IWand.getFocus((ItemStack)mainHand).m_41619_()) {
            return;
        }
        ItemStack offHand = player.m_21206_();
        if (offHand.m_41720_() instanceof IWand && !IWand.getFocus((ItemStack)offHand).m_41619_()) {
            return;
        }
        ItemStack ring = SpellRingHelper.findSpellRing(player);
        if (!ring.m_41619_() && !(focus = SpellRingHelper.getActiveFocus(ring)).m_41619_()) {
            cir.setReturnValue((Object)focus);
        }
    }
}

