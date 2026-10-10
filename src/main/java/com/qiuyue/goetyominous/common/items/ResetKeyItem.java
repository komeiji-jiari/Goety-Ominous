package com.qiuyue.goetyominous.common.items;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ResetKeyItem extends Item {
    private final String infoKey;

    public ResetKeyItem(Properties properties, String infoKey) {
        super(properties);
        this.infoKey = infoKey;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable(this.infoKey).withStyle(ChatFormatting.DARK_PURPLE));
    }
}
