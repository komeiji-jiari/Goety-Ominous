package com.qiuyue.someillagerservants.common.items;

import com.Polarice3.Goety.common.items.brew.BrewItem;
import com.Polarice3.Goety.common.items.brew.ThrowableBrewItem;
import com.qiuyue.someillagerservants.common.entities.projectile.WitchBombEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class WitchBombItem extends ThrowableBrewItem {

    public WitchBombItem() {
        super();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack bombStack = playerIn.getItemInHand(handIn);

        if (!bombStack.hasTag()) {
            InteractionHand otherHand = handIn == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
            ItemStack otherStack = playerIn.getItemInHand(otherHand);

            if (otherStack.getItem() instanceof BrewItem && otherStack.hasTag()) {
                ItemStack chargedBomb = bombStack.split(1);
                chargedBomb.setTag(otherStack.getTag().copy());
                chargedBomb.addTagElement("BrewItem", otherStack.save(new CompoundTag()));

                if (!playerIn.getAbilities().instabuild) {
                    otherStack.shrink(1);
                }

                if (!playerIn.getInventory().add(chargedBomb)) {
                    playerIn.drop(chargedBomb, false);
                }

                worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(),
                        SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 1.0F, 1.0F);
                return InteractionResultHolder.success(bombStack);
            }
        }

        worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(),
                SoundEvents.EGG_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (worldIn.getRandom().nextFloat() * 0.4F + 0.8F));

        if (!worldIn.isClientSide) {
            WitchBombEntity bomb = new WitchBombEntity(worldIn, playerIn);
            bomb.setItem(bombStack);
            bomb.shootFromRotation(playerIn, playerIn.getXRot(), playerIn.getYRot(), -20.0F, 0.75F, 1.0F);
            worldIn.addFreshEntity(bomb);
        }

        if (!playerIn.getAbilities().instabuild) {
            bombStack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(bombStack, worldIn.isClientSide());
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 16;
    }

    @Override
    public net.minecraft.world.item.UseAnim getUseAnimation(ItemStack stack) {
        return net.minecraft.world.item.UseAnim.NONE;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return InteractionResult.PASS;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 0;
    }
}
