/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.items.magic.IFocus
 *  com.Polarice3.Goety.api.magic.ISpell
 *  com.Polarice3.Goety.api.magic.SpellType
 *  com.Polarice3.Goety.common.items.magic.DarkWand
 *  javax.annotation.Nullable
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraftforge.common.capabilities.ICapabilityProvider
 *  top.theillusivec4.curios.api.type.capability.ICurioItem
 */
package com.vivideru.masteryofmagic;

import com.Polarice3.Goety.api.items.magic.IFocus;
import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.items.magic.DarkWand;
import com.vivideru.masteryofmagic.SpellRingHelper;
import com.vivideru.masteryofmagic.capability.SpellRingCapability;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class SpellRingItem
extends DarkWand
implements ICurioItem {
    public SpellRingItem() {
        super(SpellType.NONE);
    }

    public float getWandVisualHeight(Level level, LivingEntity entity, ItemStack stack) {
        return 0.0f;
    }

    @Nullable
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new SpellRingCapability(stack);
    }

    public ISpell getSpell(ItemStack stack) {
        ItemStack focus = SpellRingHelper.getActiveFocus(stack);
        Item item = focus.m_41720_();
        if (item instanceof IFocus) {
            IFocus magicFocus = (IFocus)item;
            return magicFocus.getSpell();
        }
        return null;
    }
}

