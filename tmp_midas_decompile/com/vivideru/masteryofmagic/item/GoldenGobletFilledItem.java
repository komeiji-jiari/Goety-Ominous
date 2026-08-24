/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.food.FoodProperties$Builder
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Rarity
 *  net.minecraft.world.item.UseAnim
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 */
package com.vivideru.masteryofmagic.item;

import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GoldenGobletFilledItem
extends Item {
    public GoldenGobletFilledItem() {
        super(new Item.Properties().m_41487_(1).m_41497_(Rarity.RARE).m_41489_(new FoodProperties.Builder().m_38760_(0).m_38758_(0.0f).m_38767_()));
    }

    public UseAnim m_6164_(ItemStack itemstack) {
        return UseAnim.NONE;
    }

    public int m_8105_(ItemStack itemstack) {
        return 0;
    }

    public float m_8102_(ItemStack par1ItemStack, BlockState par2Block) {
        return 0.0f;
    }

    @OnlyIn(value=Dist.CLIENT)
    public boolean m_5812_(ItemStack itemstack) {
        return true;
    }

    public ItemStack m_5922_(ItemStack itemstack, Level world, LivingEntity entity) {
        ItemStack retval = new ItemStack((ItemLike)GoetyMasteryOfMagicModItems.GOLDEN_GOBLET.get());
        super.m_5922_(itemstack, world, entity);
        if (itemstack.m_41619_()) {
            return retval;
        }
        if (entity instanceof Player) {
            Player player = (Player)entity;
            if (!player.m_150110_().f_35937_ && !player.m_150109_().m_36054_(retval)) {
                player.m_36176_(retval, false);
            }
        }
        return itemstack;
    }
}

