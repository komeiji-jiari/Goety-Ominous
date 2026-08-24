/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.capabilities.lichdom.LichProvider
 *  com.Polarice3.Goety.common.capabilities.soulenergy.ISoulEnergy
 *  com.Polarice3.Goety.common.items.magic.TaglockKit
 *  com.Polarice3.Goety.utils.SEHelper
 *  net.minecraft.network.chat.Component
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.enchantment.Enchantment
 *  net.minecraft.world.item.enchantment.EnchantmentHelper
 *  net.minecraft.world.item.enchantment.Enchantments
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraftforge.event.entity.player.ItemTooltipEvent
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$RightClickItem
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.vivideru.masteryofmagic;

import com.Polarice3.Goety.common.capabilities.lichdom.LichProvider;
import com.Polarice3.Goety.common.capabilities.soulenergy.ISoulEnergy;
import com.Polarice3.Goety.common.items.magic.TaglockKit;
import com.Polarice3.Goety.utils.SEHelper;
import com.vivideru.masteryofmagic.MasteryData;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModItems;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModMobEffects;
import com.vivideru.masteryofmagic.item.UndeadBloodVialItem;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic", bus=Mod.EventBusSubscriber.Bus.FORGE)
public class UndeadBloodVialCreationHandler {
    private static final int SOUL_COST = 1000;

    @SubscribeEvent
    public static void onTaglockSelfBlood(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        if (!(stack.m_41720_() instanceof TaglockKit) || !player.m_6047_()) {
            return;
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.m_19078_((boolean)event.getLevel().m_5776_()));
        if (event.getLevel().m_5776_()) {
            return;
        }
        TaglockKit.setEntity((ItemStack)stack, (LivingEntity)player);
        UndeadBloodVialItem.clearAnimalShape(stack);
        String animalShape = MasteryData.getPlanetSavedShape(player);
        if (animalShape.isBlank()) {
            animalShape = MasteryData.getPlanetShape(player);
        }
        if (!animalShape.isBlank()) {
            UndeadBloodVialItem.setAnimalShape(stack, animalShape);
        }
        UndeadBloodVialItem.setTherianthropicSelfBlood(stack, true);
        event.getLevel().m_5594_(null, player.m_20183_(), SoundEvents.f_11686_, SoundSource.PLAYERS, 1.0f, 0.45f);
        if (animalShape.isBlank()) {
            player.m_5661_((Component)Component.m_237115_((String)"message.goety_mastery_of_magic.planet_shape.taglock_collected_no_shape"), true);
        } else {
            EntityType.m_20632_((String)animalShape).ifPresentOrElse(type -> player.m_5661_((Component)Component.m_237110_((String)"message.goety_mastery_of_magic.planet_shape.taglock_collected", (Object[])new Object[]{type.m_20676_()}), true), () -> player.m_5661_((Component)Component.m_237115_((String)"message.goety_mastery_of_magic.planet_shape.taglock_collected_unknown"), true));
        }
    }

    @SubscribeEvent
    public static void onTaglockTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!(stack.m_41720_() instanceof TaglockKit) || !UndeadBloodVialItem.hasTherianthropicSelfBlood(stack)) {
            return;
        }
        EntityType.m_20632_((String)UndeadBloodVialItem.getAnimalShape(stack)).ifPresent(type -> event.getToolTip().add(Component.m_237110_((String)"tooltip.goety_mastery_of_magic.undead_blood_vial.animal_shape", (Object[])new Object[]{type.m_20676_()})));
    }

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        if (level.m_5776_()) {
            return;
        }
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }
        ItemStack mainHand = player.m_21120_(InteractionHand.MAIN_HAND);
        ItemStack offHand = player.m_21120_(InteractionHand.OFF_HAND);
        if (!mainHand.m_150930_(Items.f_42590_)) {
            return;
        }
        if (EnchantmentHelper.m_44843_((Enchantment)Enchantments.f_44978_, (ItemStack)offHand) <= 0) {
            return;
        }
        if (player.m_21023_((MobEffect)GoetyMasteryOfMagicModMobEffects.VULNERABLE.get())) {
            return;
        }
        player.getCapability(LichProvider.CAPABILITY).ifPresent(lichdom -> {
            if (!lichdom.getLichdom()) {
                return;
            }
            ISoulEnergy se = SEHelper.getCapability((Player)player);
            if (se.getSoulEnergy() < 1000) {
                return;
            }
            SEHelper.decreaseSESouls((Player)player, (int)1000);
            SEHelper.sendSEUpdatePacket((Player)player);
            float currentHealth = player.m_21223_();
            player.m_6469_(level.m_269111_().m_269425_(), currentHealth * 0.5f);
            ItemStack vial = new ItemStack((ItemLike)GoetyMasteryOfMagicModItems.UNDEAD_BLOOD_VIAL.get());
            UndeadBloodVialItem.setSource(vial, player.m_20148_(), player.m_36316_().getName());
            String animalShape = MasteryData.getPlanetSavedShape(player);
            if (!animalShape.isBlank()) {
                UndeadBloodVialItem.setAnimalShape(vial, animalShape);
            }
            player.m_7292_(new MobEffectInstance(MobEffects.f_19613_, 600, 0));
            player.m_7292_(new MobEffectInstance((MobEffect)GoetyMasteryOfMagicModMobEffects.VULNERABLE.get(), 72000, 0, false, true));
            if (!player.m_150110_().f_35937_) {
                mainHand.m_41774_(1);
            }
            if (mainHand.m_41619_()) {
                player.m_21008_(InteractionHand.MAIN_HAND, vial);
            } else if (!player.m_150109_().m_36054_(vial)) {
                player.m_36176_(vial, false);
            }
            level.m_5594_(null, player.m_20183_(), SoundEvents.f_11770_, SoundSource.PLAYERS, 1.0f, 1.0f);
            event.setCanceled(true);
        });
    }
}

