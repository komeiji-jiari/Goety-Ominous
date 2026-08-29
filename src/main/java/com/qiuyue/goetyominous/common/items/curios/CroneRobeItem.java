package com.qiuyue.goetyominous.common.items.curios;

import com.Polarice3.Goety.common.inventory.ModSaveInventory;
import com.Polarice3.Goety.common.inventory.WitchRobeInventory;
import com.Polarice3.Goety.common.items.curios.SingleStackItem;
import com.qiuyue.goetyominous.utils.CroneCuriosUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class CroneRobeItem extends SingleStackItem {
    public static String INVENTORY = "CRONE_ROBE_BREW";

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn instanceof LivingEntity livingEntity) {
            if (ModSaveInventory.getInstance() != null) {
                if (!stack.hasTag()) {
                    stack.setTag(new CompoundTag());
                    stack.getTag().putInt(INVENTORY, ModSaveInventory.getInstance().addAndCreateWitchRobe());
                } else {
                    if (!stack.getTag().contains(INVENTORY)) {
                        stack.getTag().putInt(INVENTORY, ModSaveInventory.getInstance().addAndCreateWitchRobe());
                    }
                    WitchRobeInventory inventory = ModSaveInventory.getInstance().getWitchRobeInventory(stack.getTag().getInt(INVENTORY), livingEntity);
                    if (!worldIn.isClientSide) {
                        if (CroneCuriosUtil.hasCroneHat(livingEntity)) {
                            inventory.setIncreaseSpeed(1);
                        } else {
                            inventory.setIncreaseSpeed(0);
                        }
                        inventory.tick();
                    }
                }
            }
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("info.goetyominous.crone_robe").withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("info.goetyominous.crone_robe_brew",
                Component.keybind("key.goety.witch.robe")).withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("info.goetyominous.crone_robe_extract",
                Component.keybind("key.goety.witch.extractPotions")).withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("info.goetyominous.crone_robe_discount").withStyle(ChatFormatting.BLUE));
    }
}
