package com.qiuyue.goetyominous.common.items;

import com.Polarice3.Goety.api.magic.SpellType;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mega.revelationfix.common.init.ModAttributes;
import com.mega.revelationfix.common.item.tool.wand.SecondPhaseStaff;
import com.qiuyue.goetyominous.common.items.ac.AcItems;
import com.qiuyue.goetyominous.common.rlyeh.RlyehStyled;
import com.qiuyue.goetyominous.utils.KeyOfRlyehMixinHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class KeyOfRlyehStaffItem extends SecondPhaseStaff implements RlyehStyled {

    private static final UUID BASE_ABYSS_POWER_UUID = UUID.fromString("8e4c2f61-b793-4a05-9d3e-6f1a8b5c7d20");

    private static final double ATTACK_DAMAGE = 11.0D;
    private static final double ATTACK_SPEED_MODIFIER = 1.8D - 4.0D;

    public KeyOfRlyehStaffItem(Item.Properties properties) {
        super(BASE_ABYSS_POWER_UUID, SpellType.ABYSS);
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
                    BASE_ATTACK_DAMAGE_UUID, "Tool modifier", ATTACK_DAMAGE, AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(
                    BASE_ATTACK_SPEED_UUID, "Tool modifier", ATTACK_SPEED_MODIFIER, AttributeModifier.Operation.ADDITION));

            builder.put(ModAttributes.spellAttribute(SpellType.ABYSS), new AttributeModifier(
                    BASE_ABYSS_POWER_UUID, "Tool modifier", 1.5D, AttributeModifier.Operation.ADDITION));
            builder.put(ModAttributes.SPELL_POWER_MULTIPLIER.get(), new AttributeModifier(
                    BASE_SPELL_POWER_MULTIPLIER_UUID, "Tool modifier", 0.2D, AttributeModifier.Operation.MULTIPLY_BASE));
            builder.put(ModAttributes.SPELL_COOLDOWN.get(), new AttributeModifier(
                    BASE_SPELL_COOLDOWN_UUID, "Tool modifier", 0.25D, AttributeModifier.Operation.MULTIPLY_BASE));

            return builder.build();
        }
        return super.getAttributeModifiers(slot, stack);
    }
}
