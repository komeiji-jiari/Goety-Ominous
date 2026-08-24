/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.MobType
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.enchantment.Enchantment
 *  net.minecraft.world.item.enchantment.EnchantmentHelper
 *  net.minecraft.world.item.enchantment.Enchantments
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package com.vivideru.masteryofmagic.handler;

import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class VulnerableSmiteHandler {
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (target.m_6336_() != MobType.f_21641_) {
            return;
        }
        if (!target.m_21023_((MobEffect)GoetyMasteryOfMagicModMobEffects.VULNERABLE.get())) {
            return;
        }
        Entity entity = event.getSource().m_7639_();
        if (!(entity instanceof LivingEntity)) {
            return;
        }
        LivingEntity attacker = (LivingEntity)entity;
        ItemStack weapon = attacker.m_21205_();
        int smite = EnchantmentHelper.m_44843_((Enchantment)Enchantments.f_44978_, (ItemStack)weapon);
        if (smite <= 0) {
            return;
        }
        float extraDamage = target.m_21233_() * (0.01f * (float)smite);
        event.setAmount(event.getAmount() + extraDamage);
    }
}

