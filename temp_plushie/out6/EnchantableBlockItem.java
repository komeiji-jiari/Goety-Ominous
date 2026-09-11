package com.Polarice3.Goety.common.items.block;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.ThroneBlock;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.config.MainConfig;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class EnchantableBlockItem extends BlockItemBase {
    public EnchantableBlockItem(Block blockIn) {
        super(blockIn);
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (stack.m_41720_() == ((Block)ModBlocks.SCULK_DEVOURER.get()).m_5456_()) {
            return stack.m_41613_() == 1 && (enchantment == ModEnchantments.SOUL_EATER.get() || enchantment == ModEnchantments.RADIUS.get());
        } else if (stack.m_41720_() == ((Block)ModBlocks.SCULK_CONVERTER.get()).m_5456_()) {
            return stack.m_41613_() == 1 && enchantment == ModEnchantments.POTENCY.get();
        } else if (stack.m_41720_() == ((Block)ModBlocks.SCULK_GROWER.get()).m_5456_()) {
            if (MainConfig.SculkGrowerPotency.get()) {
                return stack.m_41613_() == 1 && (enchantment == ModEnchantments.POTENCY.get() || enchantment == ModEnchantments.RADIUS.get());
            } else {
                return stack.m_41613_() == 1 && enchantment == ModEnchantments.RADIUS.get();
            }
        } else {
            Item var4 = stack.m_41720_();
            if (var4 instanceof BlockItem) {
                BlockItem blockItem = (BlockItem)var4;
                if (blockItem.m_40614_() instanceof ThroneBlock) {
                    return stack.m_41613_() == 1 && enchantment == ModEnchantments.ROYALTY.get();
                }
            }

            return stack.m_41613_() == 1;
        }
    }

    public int getMaxStackSize(ItemStack itemStack) {
        return itemStack.m_41793_() ? 1 : super.getMaxStackSize(itemStack);
    }

    public boolean m_8120_(ItemStack stack) {
        return stack.m_41613_() == 1;
    }

    public int getEnchantmentValue(ItemStack stack) {
        return 25;
    }

    public static void setOwner(@Nullable LivingEntity entity, ItemStack stack) {
        CompoundTag entityTag = stack.m_41784_();
        if (entity != null) {
            entityTag.m_128362_("owner", entity.m_20148_());
            entityTag.m_128359_("owner_name", entity.m_5446_().getString());
        }

    }

    @Nullable
    public static UUID getOwnerID(ItemStack stack) {
        CompoundTag entityTag = stack.m_41783_();
        return entityTag != null && entityTag.m_128441_("owner") ? entityTag.m_128342_("owner") : null;
    }

    public void m_7373_(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (stack.m_41783_() != null && stack.m_41783_().m_128441_("owner_name")) {
            tooltip.add(Component.m_237115_("tooltip.goety.arcaPlayer").m_6270_(Style.f_131099_.m_131157_(ChatFormatting.GRAY)).m_7220_(Component.m_237113_("" + stack.m_41783_().m_128461_("owner_name")).m_6270_(Style.f_131099_.m_131157_(ChatFormatting.GRAY))));
        }

        super.m_7373_(stack, worldIn, tooltip, flagIn);
    }
}
