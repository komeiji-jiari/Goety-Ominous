/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.vivideru.masteryofmagic.mixins;

import com.vivideru.masteryofmagic.MasteryData;
import java.util.HashSet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={LivingEntity.class})
public abstract class PlanetShapeArmorMixin {
    @Inject(method={"getArmorValue"}, at={@At(value="RETURN")}, cancellable=true)
    private void gmom$ignoreWornArmor(CallbackInfoReturnable<Integer> callback) {
        Player player;
        PlanetShapeArmorMixin planetShapeArmorMixin = this;
        if (!(planetShapeArmorMixin instanceof Player) || MasteryData.getPlanetShape(player = (Player)planetShapeArmorMixin).isBlank()) {
            return;
        }
        AttributeInstance armor = player.m_21051_(Attributes.f_22284_);
        if (armor == null) {
            return;
        }
        HashSet worn = new HashSet();
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack stack = player.m_6844_(slot);
            stack.m_41638_(slot).get((Object)Attributes.f_22284_).forEach(modifier -> worn.add(modifier.m_22209_()));
        }
        double value = armor.m_22115_();
        for (AttributeModifier modifier2 : armor.m_22104_(AttributeModifier.Operation.ADDITION)) {
            if (worn.contains(modifier2.m_22209_())) continue;
            value += modifier2.m_22218_();
        }
        double multiplied = value;
        for (AttributeModifier modifier3 : armor.m_22104_(AttributeModifier.Operation.MULTIPLY_BASE)) {
            if (worn.contains(modifier3.m_22209_())) continue;
            multiplied += value * modifier3.m_22218_();
        }
        for (AttributeModifier modifier3 : armor.m_22104_(AttributeModifier.Operation.MULTIPLY_TOTAL)) {
            if (worn.contains(modifier3.m_22209_())) continue;
            multiplied *= 1.0 + modifier3.m_22218_();
        }
        callback.setReturnValue((Object)Math.max(0, Mth.m_14107_((double)multiplied)));
    }
}

