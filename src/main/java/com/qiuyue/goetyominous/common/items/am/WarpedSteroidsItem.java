package com.qiuyue.goetyominous.common.items.am;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.SEHelper;
import com.qiuyue.goetyominous.common.entities.ally.am.CrimsonMosquitoServant;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public class WarpedSteroidsItem extends Item {

    public WarpedSteroidsItem() {
        super(new Item.Properties()
                .rarity(Rarity.UNCOMMON)
                .setNoRepair()
                .stacksTo(1));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (SEHelper.isOnCooldown(player, stack)) {
            return InteractionResult.FAIL;
        }

        if (!(target instanceof CrimsonMosquitoServant mosquito) || mosquito.getTrueOwner() != player) {
            return InteractionResult.PASS;
        }

        if (mosquito.isSick()) {
            return InteractionResult.PASS;
        }
        Level level = player.level();
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        mosquito.startSteroidConversion();

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        player.swing(hand);

        SEHelper.addCooldown(player, this, MathHelper.secondsToTicks(ItemConfig.ReviveSecondsCool.get()));
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
        if (!level.isClientSide) {
            living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 1800, 0));
            living.addEffect(new MobEffectInstance(GoetyEffects.SENSE_LOSS.get(), 1800, 0));
            living.addEffect(new MobEffectInstance(GoetyEffects.SPASMS.get(), 1800, 0));
            living.addEffect(new MobEffectInstance(GoetyEffects.ACID_VENOM.get(), 1800, 0));
            living.addEffect(new MobEffectInstance(GoetyEffects.WANE.get(), 1800, 0));
        }
        if (living instanceof ServerPlayer player) {
            CriteriaTriggers.CONSUME_ITEM.trigger(player, stack);
            if (!player.getAbilities().instabuild) {
                ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
                if (!player.getInventory().add(bottle)) {
                    player.drop(bottle, false);
                }
            }
        }
        stack.shrink(1);
        return stack.isEmpty() ? new ItemStack(Items.GLASS_BOTTLE) : stack;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public SoundEvent getDrinkingSound() {
        return SoundEvents.HONEY_DRINK;
    }

    @Override
    public SoundEvent getEatingSound() {
        return SoundEvents.HONEY_DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 40;
    }
}
