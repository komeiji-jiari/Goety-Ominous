package com.qiuyue.goetyominous.common.items.ac;

import com.Polarice3.Goety.utils.CuriosFinder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;

public class RaycatAmuletItem extends Item implements ICurioItem {

    public RaycatAmuletItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        tooltip.add(Component.translatable("info.goetyominous.raycat_amulet").withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("info.goetyominous.raycat_amulet.radiation").withStyle(ChatFormatting.GRAY));
    }

    public static boolean hasAmulet(LivingEntity entity) {
        if (!AcItems.RAYCAT_AMULET.isPresent()) {
            return false;
        }
        return CuriosFinder.hasCurio(entity, AcItems.RAYCAT_AMULET.get());
    }
}
