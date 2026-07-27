package com.qiuyue.goetyominus.common.items;

import com.qiuyue.goetyominus.common.entities.projectile.BurningPotionEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BurningPotionItem extends Item {
    public BurningPotionItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(),
                SoundEvents.SPLASH_POTION_THROW, SoundSource.NEUTRAL, 0.5F,
                0.4F / (worldIn.getRandom().nextFloat() * 0.4F + 0.8F));

        if (!worldIn.isClientSide) {
            BurningPotionEntity potion = new BurningPotionEntity(worldIn, playerIn);
            potion.setItem(itemstack);
            potion.shootFromRotation(playerIn, playerIn.getXRot(), playerIn.getYRot(), -20.0F, 0.5F, 1.0F);
            worldIn.addFreshEntity(potion);
        }

        playerIn.awardStat(Stats.ITEM_USED.get(this));
        if (!playerIn.getAbilities().instabuild) {
            itemstack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, worldIn.isClientSide());
    }
}
