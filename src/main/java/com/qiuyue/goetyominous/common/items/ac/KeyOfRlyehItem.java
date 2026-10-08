package com.qiuyue.goetyominous.common.items.ac;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.items.magic.DarkStaff;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.qiuyue.goetyominous.common.items.ac.AcItems;
import com.qiuyue.goetyominous.common.rlyeh.ac.RlyehStyled;
import com.qiuyue.goetyominous.utils.ac.KeyOfRlyehMixinHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class KeyOfRlyehItem extends DarkStaff implements RlyehStyled {

    public static final float DAMAGE = 11.0F;
    public static final float ATTACK_SPEED = 1.8F;

    public KeyOfRlyehItem(Item.Properties properties) {
        super(properties.fireResistant(), DAMAGE, ATTACK_SPEED, SpellType.ABYSS);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return AcItems.RARITY_DEEP_SEA;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean held) {
        super.inventoryTick(stack, level, entity, slot, held);
        KeyOfRlyehMixinHelper.applyDeepOneReputation(level, entity);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot == EquipmentSlot.MAINHAND) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                    BASE_ATTACK_DAMAGE_UUID, "Tool modifier", 11.0D, AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(
                    BASE_ATTACK_SPEED_UUID, "Tool modifier", 1.8D - 4.0D, AttributeModifier.Operation.ADDITION));
            return builder.build();
        }
        return super.getAttributeModifiers(slot, stack);
    }
}
