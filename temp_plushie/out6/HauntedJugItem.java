package com.Polarice3.Goety.common.items.block;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Block;

public class HauntedJugItem extends BlockItemBase {
    public HauntedJugItem() {
        super((Block)ModBlocks.HAUNTED_JUG.get());
    }

    public boolean m_142207_(ItemStack stack, Slot slot, ClickAction action, Player player) {
        if (action != ClickAction.SECONDARY) {
            return false;
        } else {
            ItemStack other = slot.m_7993_();
            if (other.m_150930_(Items.f_42446_)) {
                if (other.m_41613_() == 1) {
                    slot.m_5852_(new ItemStack(Items.f_42447_));
                } else {
                    other.m_41774_(1);
                    player.m_150109_().m_150079_(new ItemStack(Items.f_42447_));
                }

                return true;
            } else if (other.m_150930_(Items.f_42590_)) {
                if (other.m_41613_() == 1) {
                    slot.m_5852_(PotionUtils.m_43549_(new ItemStack(Items.f_42589_), Potions.f_43599_));
                } else {
                    other.m_41774_(1);
                    player.m_150109_().m_150079_(PotionUtils.m_43549_(new ItemStack(Items.f_42589_), Potions.f_43599_));
                }

                return true;
            } else {
                return false;
            }
        }
    }

    public boolean m_142305_(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (action != ClickAction.SECONDARY) {
            return false;
        } else if (other.m_150930_(Items.f_42446_)) {
            if (other.m_41613_() == 1) {
                access.m_142104_(new ItemStack(Items.f_42447_));
            } else {
                other.m_41774_(1);
                player.m_150109_().m_150079_(new ItemStack(Items.f_42447_));
            }

            return true;
        } else if (other.m_150930_(Items.f_42590_)) {
            if (other.m_41613_() == 1) {
                access.m_142104_(PotionUtils.m_43549_(new ItemStack(Items.f_42589_), Potions.f_43599_));
            } else {
                other.m_41774_(1);
                player.m_150109_().m_150079_(PotionUtils.m_43549_(new ItemStack(Items.f_42589_), Potions.f_43599_));
            }

            return true;
        } else {
            return false;
        }
    }
}
