package com.qiuyue.goetyominous.common.items;

import com.Polarice3.Goety.api.items.ISoulRepair;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.utils.SEHelper;
import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DarkBlackBeastArmorItem extends CursedBlackBeastArmorItem implements ISoulRepair {
    public static final ResourceLocation TEXTURE = new ResourceLocation(
            GoetyOminous.MOD_ID, "textures/entity/black_beast_dark_armor.png");

    public DarkBlackBeastArmorItem(Item.Properties properties) {
        super(properties, TEXTURE, 78);
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return repairCandidate.is(ModItems.DARK_ALLOY_INGOT.get())
                || repairCandidate.is(ModItems.CURSED_METAL_INGOT.get());
    }

    @Override
    public void repairTick(ItemStack stack, Entity entity, boolean isEquipped) {
        if (ItemConfig.SoulRepair.get() && entity instanceof Player player
                && (!player.isCreative() || isEquipped)
                && stack.isDamaged()
                && SEHelper.getSoulsContainer(player)) {
            int cost = ItemConfig.ItemsRepairAmount.get();
            if (SEHelper.getSoulsAmount(player, cost) && player.tickCount % 100 == 0) {
                stack.setDamageValue(stack.getDamageValue() - 1);
                SEHelper.decreaseSouls(player, cost);
            }
        }
    }
}
