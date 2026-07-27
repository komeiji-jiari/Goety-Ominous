package com.qiuyue.goetyominus.common.items;

import com.Polarice3.Goety.common.items.equipment.RampagingAxeItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import com.google.common.collect.Multimap;

import java.util.UUID;

public class FirebrandItem extends RampagingAxeItem {

    private static final UUID EXTRA_DAMAGE_UUID = UUID.fromString("cb3f55d3-645c-4f38-a497-9c13a33db5cf");

    public FirebrandItem() {
        super();
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.setSecondsOnFire(10);
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> modifiers = super.getAttributeModifiers(slot, stack);
        if (slot == EquipmentSlot.MAINHAND) {
            Multimap<Attribute, AttributeModifier> mutable = com.google.common.collect.ArrayListMultimap.create(modifiers);
            mutable.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                    EXTRA_DAMAGE_UUID, "Firebrand bonus", 3.0D, AttributeModifier.Operation.ADDITION));
            return mutable;
        }
        return modifiers;
    }
}
