/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.capabilities.lichdom.LichProvider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.food.FoodProperties$Builder
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Rarity
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.UseAnim
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 */
package com.vivideru.masteryofmagic.item;

import com.Polarice3.Goety.common.capabilities.lichdom.LichProvider;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModItems;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GobletofSoulEnergyItem
extends Item {
    public static final String MOD_DATA_KEY = "goetymasteryofmagic";
    public static final String GOBLET_SOUL_ENERGY_UNLOCKED = "goblet_soul_energy_unlocked";

    public GobletofSoulEnergyItem() {
        super(new Item.Properties().m_41487_(1).m_41497_(Rarity.RARE).m_41489_(new FoodProperties.Builder().m_38760_(0).m_38758_(0.0f).m_38765_().m_38767_()));
    }

    public UseAnim m_6164_(ItemStack stack) {
        return UseAnim.DRINK;
    }

    public int m_8105_(ItemStack stack) {
        return 20;
    }

    public float m_8102_(ItemStack stack, BlockState state) {
        return 0.0f;
    }

    @OnlyIn(value=Dist.CLIENT)
    public boolean m_5812_(ItemStack stack) {
        return true;
    }

    public ItemStack m_5922_(ItemStack stack, Level level, LivingEntity entity) {
        Player player;
        ItemStack emptyGoblet = new ItemStack((ItemLike)GoetyMasteryOfMagicModItems.GOLDEN_GOBLET.get());
        super.m_5922_(stack, level, entity);
        if (!level.f_46443_ && entity instanceof Player) {
            player = (Player)entity;
            player.getCapability(LichProvider.CAPABILITY).ifPresent(lichdom -> {
                if (!lichdom.getLichdom()) {
                    player.m_7292_(new MobEffectInstance(MobEffects.f_19614_, 200, 1));
                    return;
                }
                CompoundTag persistentData = player.getPersistentData();
                CompoundTag modData = persistentData.m_128469_(MOD_DATA_KEY);
                modData.m_128379_(GOBLET_SOUL_ENERGY_UNLOCKED, true);
                persistentData.m_128365_(MOD_DATA_KEY, (Tag)modData);
            });
        }
        if (stack.m_41619_()) {
            return emptyGoblet;
        }
        if (entity instanceof Player) {
            player = (Player)entity;
            if (!player.m_150110_().f_35937_ && !player.m_150109_().m_36054_(emptyGoblet)) {
                player.m_36176_(emptyGoblet, false);
            }
        }
        return stack;
    }

    public void m_7373_(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add((Component)Component.m_237115_((String)"item.goety_mastery_of_magic.gobletof_soul_energy.info"));
        super.m_7373_(stack, level, tooltip, flag);
    }
}

