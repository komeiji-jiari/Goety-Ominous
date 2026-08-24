/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.items.curios.RingItem
 *  javax.annotation.Nullable
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.level.Level
 *  top.theillusivec4.curios.api.type.capability.ICurioItem
 */
package com.vivideru.masteryofmagic;

import com.Polarice3.Goety.common.items.curios.RingItem;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class EmpoweredForgeRingItem
extends RingItem
implements ICurioItem {
    public void m_7373_(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.m_7373_(stack, level, tooltip, flag);
        tooltip.add((Component)Component.m_237115_((String)"item.goety_mastery_of_magic.empowered_forge_ring.info").m_130940_(ChatFormatting.GRAY));
    }
}

