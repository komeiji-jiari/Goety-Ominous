package com.qiuyue.goetyominous.common.items;

import com.qiuyue.goetyominous.common.events.WindBowEvents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;

public class WindBowItem extends WitchBowItem {

    @Override
    public AbstractArrow customArrow(AbstractArrow arrow) {
        WindBowEvents.mark(super.customArrow(arrow));
        return arrow;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        InteractionResultHolder<ItemStack> ret =
                ForgeEventFactory.onArrowNock(stack, level, player, hand, true);
        if (ret != null) {
            return ret;
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return repair.getItem() instanceof WindBowItem
                || repair.is(com.Polarice3.Goety.common.items.ModItems.JADE.get());
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) {
            return;
        }
        int i = this.getUseDuration(stack) - timeLeft;
        i = ForgeEventFactory.onArrowLoose(stack, level, player, i, true);
        if (i < 0) {
            return;
        }
        float f = getPowerForTime(i);
        if (f < 0.1F) {
            return;
        }
        if (!level.isClientSide) {
            ItemStack virtualArrow = new ItemStack(Items.ARROW);
            AbstractArrow abstractarrow =
                    ((ArrowItem) Items.ARROW).createArrow(level, virtualArrow, player);
            abstractarrow = customArrow(abstractarrow);
            abstractarrow.shootFromRotation(player, player.getXRot(), player.getYRot(),
                    0.0F, f * 3.0F, 1.0F);
            if (f == 1.0F) {
                abstractarrow.setCritArrow(true);
            }
            int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
            if (j > 0) {
                abstractarrow.setBaseDamage(abstractarrow.getBaseDamage() + (double) j * 0.5D + 0.5D);
            }
            int k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);
            if (k > 0) {
                abstractarrow.setKnockback(k);
            }
            if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) > 0) {
                abstractarrow.setSecondsOnFire(100);
            }
            stack.hurtAndBreak(1, player,
                    p -> p.broadcastBreakEvent(player.getUsedItemHand()));
            abstractarrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            level.addFreshEntity(abstractarrow);
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS,
                1.0F, 1.0F / (player.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
        player.awardStat(Stats.ITEM_USED.get(this));
    }
}
