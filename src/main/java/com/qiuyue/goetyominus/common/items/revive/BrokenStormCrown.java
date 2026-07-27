package com.qiuyue.goetyominus.common.items.revive;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class BrokenStormCrown extends Item {
    public BrokenStormCrown() {
        super(new Properties()
                .rarity(Rarity.UNCOMMON)
                .stacksTo(1));
    }

    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("info.goetyominus.items.broken_storm_crown.desc").withStyle(ChatFormatting.DARK_PURPLE));
    }
}

