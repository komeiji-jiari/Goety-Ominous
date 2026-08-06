package com.qiuyue.goetyominous.common.items.spear;

import com.Polarice3.Goety.api.items.IPersist;
import com.Polarice3.Goety.config.ItemConfig;
import com.notunanancyowen.spears.Spears;
import com.notunanancyowen.spears.items.SpearItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class DarkSpearItem extends SpearItem implements IPersist {

    public DarkSpearItem(Tier tier, Item.Properties props) {
        super(tier,
                21,
                1.075F,
                0.5F,
                3.0F,
                7.5F,
                4.0F,
                5.1F,
                10.0F,
                4.6F,
                Spears.SPEAR_HIT, Spears.SPEAR_ATTACK, Spears.SPEAR_USE,
                props);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return this.isDamaged(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return this.isBroken(stack) ? 8388608 : super.getBarColor(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return this.isBroken(stack) ? 13 : super.getBarWidth(stack);
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        if (ItemConfig.DarkToolsPersist.get() && stack.getDamageValue() + amount >= stack.getMaxDamage()) {
            if (stack.getDamageValue() != stack.getMaxDamage() - 1) {
                stack.setDamageValue(stack.getMaxDamage() - 1);
                onBroken.accept(entity);
            }
            return 0;
        } else {
            return amount;
        }
    }

    @Override
    public boolean isBroken(ItemStack stack) {
        return IPersist.super.isBroken(stack) && ItemConfig.DarkToolsPersist.get();
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState blockState) {
        return !this.isNotBroken(stack) && ItemConfig.DarkToolsPersist.get() ? 1.0F : super.getDestroySpeed(stack, blockState);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return !this.isNotBroken(stack) && ItemConfig.DarkToolsPersist.get()
                ? ImmutableMultimap.of()
                : super.getAttributeModifiers(slot, stack);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && slotChanged;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (ItemConfig.DarkToolsPersist.get() && this.isBroken(stack)) {
            tooltip.add(Component.translatable("info.goety.armor.broken").withStyle(ChatFormatting.DARK_RED));
        }
    }
}
