/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 */
package com.vivideru.masteryofmagic.item;

import com.vivideru.masteryofmagic.MasteryData;
import com.vivideru.masteryofmagic.SupremeMasteryAdvancementHelper;
import com.vivideru.masteryofmagic.item.MasteryScrollItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class WizardryMasteryScrollItem
extends MasteryScrollItem {
    private final int level;

    public WizardryMasteryScrollItem(int level) {
        this.level = level;
    }

    public int masteryLevel() {
        return this.level;
    }

    public InteractionResultHolder<ItemStack> m_7203_(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.m_21120_(hand);
        if (!world.f_46443_) {
            int current = MasteryData.getWizardry(player);
            if (current != this.level - 1) {
                player.m_5661_((Component)Component.m_237110_((String)"message.goety_mastery_of_magic.wizardry_wrong_level", (Object[])new Object[]{this.level - 1}), true);
                return InteractionResultHolder.m_19100_((Object)stack);
            }
            MasteryData.setWizardry(player, this.level);
            SupremeMasteryAdvancementHelper.grant(player, this.level);
            if (!player.m_150110_().f_35937_) {
                stack.m_41774_(1);
            }
            player.m_5661_((Component)Component.m_237115_((String)("message.goety_mastery_of_magic.wizardry_unlocked." + this.level)), false);
        }
        return InteractionResultHolder.m_19092_((Object)stack, (boolean)world.f_46443_);
    }
}

