/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.utils.SEHelper
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.vivideru.masteryofmagic.mixins;

import com.Polarice3.Goety.utils.SEHelper;
import com.vivideru.masteryofmagic.enchantment.DarkenedEnchantment;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ItemStack.class})
public abstract class DarkenedUnbreakableMixin {
    @Inject(method={"hurt(ILnet/minecraft/util/RandomSource;Lnet/minecraft/server/level/ServerPlayer;)Z"}, at={@At(value="HEAD")}, cancellable=true)
    private void gmom_darkenedHurt(int amount, RandomSource random, ServerPlayer player, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = (ItemStack)this;
        if (!stack.m_41763_()) {
            return;
        }
        if (!DarkenedEnchantment.has(stack)) {
            return;
        }
        int max = stack.m_41776_();
        int newDamage = stack.m_41773_() + amount;
        if (newDamage < max) {
            return;
        }
        if (player != null && !player.m_9236_().f_46443_) {
            int cost = 5;
            if (SEHelper.getSoulAmountInt((Player)player) >= cost) {
                SEHelper.decreaseSouls((Player)player, (int)cost);
            }
        }
        stack.m_41721_(max - 1);
        cir.setReturnValue((Object)false);
    }
}

