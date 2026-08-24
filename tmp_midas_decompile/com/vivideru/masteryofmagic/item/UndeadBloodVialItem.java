/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.utils.SEHelper
 *  com.mojang.authlib.GameProfile
 *  javax.annotation.Nullable
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.ItemUtils
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.Rarity
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.UseAnim
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 */
package com.vivideru.masteryofmagic.item;

import com.Polarice3.Goety.utils.SEHelper;
import com.mojang.authlib.GameProfile;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModMobEffects;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class UndeadBloodVialItem
extends Item {
    public static final String TAG_SOURCE_UUID = "SourceUUID";
    public static final String TAG_SOURCE_NAME = "SourceName";
    public static final String TAG_ANIMAL_SHAPE = "AnimalShape";
    public static final String TAG_THERIANTHROPIC_SELF_BLOOD = "TherianthropicSelfBlood";

    public UndeadBloodVialItem() {
        super(new Item.Properties().m_41487_(64).m_41497_(Rarity.UNCOMMON));
    }

    public static void setSource(ItemStack stack, UUID uuid, String playerName) {
        CompoundTag tag = stack.m_41784_();
        tag.m_128362_(TAG_SOURCE_UUID, uuid);
        tag.m_128359_(TAG_SOURCE_NAME, playerName);
    }

    public static boolean hasSource(ItemStack stack) {
        return stack.m_41782_() && stack.m_41783_().m_128403_(TAG_SOURCE_UUID);
    }

    public static void setAnimalShape(ItemStack stack, String entityId) {
        if (entityId != null && !entityId.isBlank()) {
            stack.m_41784_().m_128359_(TAG_ANIMAL_SHAPE, entityId);
        }
    }

    public static String getAnimalShape(ItemStack stack) {
        return stack.m_41782_() ? stack.m_41783_().m_128461_(TAG_ANIMAL_SHAPE) : "";
    }

    public static boolean hasAnimalShape(ItemStack stack) {
        return !UndeadBloodVialItem.getAnimalShape(stack).isBlank();
    }

    public static void clearAnimalShape(ItemStack stack) {
        if (stack.m_41782_()) {
            stack.m_41783_().m_128473_(TAG_ANIMAL_SHAPE);
        }
    }

    public static void setTherianthropicSelfBlood(ItemStack stack, boolean value) {
        stack.m_41784_().m_128379_(TAG_THERIANTHROPIC_SELF_BLOOD, value);
    }

    public static boolean hasTherianthropicSelfBlood(ItemStack stack) {
        return stack.m_41782_() && stack.m_41783_().m_128471_(TAG_THERIANTHROPIC_SELF_BLOOD);
    }

    public UseAnim m_6164_(ItemStack stack) {
        return UseAnim.DRINK;
    }

    public int m_8105_(ItemStack stack) {
        return 32;
    }

    public InteractionResultHolder<ItemStack> m_7203_(Level level, Player player, InteractionHand hand) {
        return ItemUtils.m_150959_((Level)level, (Player)player, (InteractionHand)hand);
    }

    public ItemStack m_5922_(ItemStack stack, Level level, LivingEntity entity) {
        Player player;
        if (!level.f_46443_ && entity instanceof Player) {
            player = (Player)entity;
            player.m_21195_((MobEffect)GoetyMasteryOfMagicModMobEffects.VULNERABLE.get());
            player.m_7292_(new MobEffectInstance(MobEffects.f_19605_, 200, 3));
            SEHelper.increaseSouls((Player)player, (int)1000);
            player.m_5496_(SoundEvents.f_11911_, 1.0f, 1.0f);
        }
        if (entity instanceof Player) {
            player = (Player)entity;
            if (!player.m_150110_().f_35937_) {
                stack.m_41774_(1);
                if (stack.m_41619_()) {
                    return new ItemStack((ItemLike)Items.f_42590_);
                }
            }
        }
        return stack;
    }

    public void m_7373_(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.m_7373_(stack, level, tooltip, flag);
        if (!stack.m_41782_()) {
            return;
        }
        CompoundTag tag = stack.m_41783_();
        String ownerName = null;
        if (tag.m_128425_(TAG_SOURCE_NAME, 8)) {
            ownerName = tag.m_128461_(TAG_SOURCE_NAME);
        } else if (tag.m_128403_(TAG_SOURCE_UUID) && level != null) {
            try {
                MinecraftServer server = level.m_7654_();
                if (server != null) {
                    UUID uuid = tag.m_128342_(TAG_SOURCE_UUID);
                    Optional profile = server.m_129927_().m_11002_(uuid);
                    if (profile.isPresent()) {
                        ownerName = ((GameProfile)profile.get()).getName();
                    }
                }
            }
            catch (Exception server) {
                // empty catch block
            }
        }
        if (ownerName != null && !ownerName.isEmpty()) {
            tooltip.add((Component)Component.m_237113_((String)("Blood of " + ownerName)));
        } else {
            tooltip.add((Component)Component.m_237113_((String)"Blood of an unknown source"));
        }
        String animalShape = UndeadBloodVialItem.getAnimalShape(stack);
        EntityType.m_20632_((String)animalShape).ifPresent(type -> tooltip.add((Component)Component.m_237110_((String)"tooltip.goety_mastery_of_magic.undead_blood_vial.animal_shape", (Object[])new Object[]{type.m_20676_()})));
    }
}

