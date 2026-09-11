package com.Polarice3.Goety.common.items.block;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class RedstoneMonstrosityHeadItem extends StandingAndWallBlockItem {
    public RedstoneMonstrosityHeadItem(Item.Properties p_43250_) {
        super((Block)ModBlocks.REDSTONE_MONSTROSITY_HEAD_BLOCK.get(), (Block)ModBlocks.WALL_REDSTONE_MONSTROSITY_HEAD_BLOCK.get(), p_43250_, Direction.DOWN);
    }

    public boolean m_5812_(ItemStack p_41453_) {
        return getOwnerID(p_41453_) != null;
    }

    public static void setOwner(@Nullable LivingEntity entity, ItemStack stack) {
        CompoundTag entityTag = stack.m_41784_();
        if (entity != null) {
            entityTag.m_128362_("owner", entity.m_20148_());
            entityTag.m_128359_("owner_name", entity.m_5446_().getString());
        }

    }

    public static void setCustomName(String string, ItemStack stack) {
        CompoundTag entityTag = stack.m_41784_();
        entityTag.m_128359_("mod_custom_name", string);
    }

    public static String getCustomName(ItemStack stack) {
        CompoundTag entityTag = stack.m_41783_();
        return entityTag != null && entityTag.m_128441_("mod_custom_name") ? entityTag.m_128461_("mod_custom_name") : null;
    }

    @Nullable
    public static UUID getOwnerID(ItemStack stack) {
        CompoundTag entityTag = stack.m_41783_();
        return entityTag != null && entityTag.m_128441_("owner") ? entityTag.m_128342_("owner") : null;
    }

    public void m_7373_(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (stack.m_41783_() != null) {
            if (stack.m_41783_().m_128441_("owner_name")) {
                tooltip.add(Component.m_237115_("tooltip.goety.arcaPlayer").m_6270_(Style.f_131099_.m_131157_(ChatFormatting.GRAY)).m_7220_(Component.m_237113_("" + stack.m_41783_().m_128461_("owner_name")).m_6270_(Style.f_131099_.m_131157_(ChatFormatting.GRAY))));
            }

            if (stack.m_41783_().m_128441_("mod_custom_name")) {
                tooltip.add(Component.m_237115_("tooltip.goety.customName").m_6270_(Style.f_131099_.m_131157_(ChatFormatting.GRAY)).m_7220_(Component.m_237113_("" + stack.m_41783_().m_128461_("mod_custom_name")).m_6270_(Style.f_131099_.m_131157_(ChatFormatting.GRAY))));
            }
        }

        super.m_7373_(stack, worldIn, tooltip, flagIn);
    }
}
