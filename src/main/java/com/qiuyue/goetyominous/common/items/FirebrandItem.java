package com.qiuyue.goetyominous.common.items;

import com.Polarice3.Goety.common.items.equipment.RampagingAxeItem;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.qiuyue.goetyominous.config.WeaponConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

public class FirebrandItem extends RampagingAxeItem {

    private final Multimap<Attribute, AttributeModifier> firebrandAttributes;

    public FirebrandItem() {
        super();
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                BASE_ATTACK_DAMAGE_UUID, "Weapon modifier",
                WeaponConfig.FirebrandDamage.get() - 1.0,
                AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(
                BASE_ATTACK_SPEED_UUID, "Weapon modifier",
                -3.0,
                AttributeModifier.Operation.ADDITION));
        this.firebrandAttributes = builder.build();
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        if (!target.level().isClientSide) {
            if (target.isOnFire()) {
                target.hurt(target.damageSources().onFire(), WeaponConfig.FirebrandFireBonus.get().floatValue());
            }
            target.setSecondsOnFire(10);
        }
        return result;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.firebrandAttributes
                : super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return slot == EquipmentSlot.MAINHAND ? this.firebrandAttributes
                : super.getAttributeModifiers(slot, stack);
    }
}
