/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.utils.SEHelper
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.vivideru.masteryofmagic.mixins;

import com.Polarice3.Goety.utils.SEHelper;
import com.vivideru.masteryofmagic.enchantment.DarkenedEnchantment;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Player.class})
public abstract class DarkenedRepairMixin {
    @Inject(method={"tick"}, at={@At(value="TAIL")})
    private void gmom_darkenedRepair(CallbackInfo ci) {
        Player player = (Player)this;
        if (player.m_9236_().f_46443_) {
            return;
        }
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer serverPlayer = (ServerPlayer)player;
        int interval = 20;
        if (serverPlayer.f_19797_ % interval != 0) {
            return;
        }
        int costPerPoint = 2;
        for (ItemStack stack : player.m_6168_()) {
            if (stack.m_41619_() || !stack.m_41763_() || !stack.m_41768_() || !DarkenedEnchantment.has(stack) || SEHelper.getSoulAmountInt((Player)serverPlayer) < costPerPoint) continue;
            SEHelper.decreaseSouls((Player)serverPlayer, (int)costPerPoint);
            int newDamage = stack.m_41773_() - 1;
            if (newDamage < 0) {
                newDamage = 0;
            }
            stack.m_41721_(newDamage);
        }
    }
}

