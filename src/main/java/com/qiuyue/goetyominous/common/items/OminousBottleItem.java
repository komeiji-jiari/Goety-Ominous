package com.qiuyue.goetyominous.common.items;

import com.qiuyue.goetyominous.common.init.ModSounds;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;

public class OminousBottleItem extends Item {
    private static final int DRINK_DURATION = 32;
    public static final int BAD_OMEN_DURATION = 120000;
    public static final int MIN_AMPLIFIER = 0;
    public static final int MAX_AMPLIFIER = 4;
    private static final String AMPLIFIER_TAG = "Amplifier";

    public OminousBottleItem(Properties properties) {
        super(properties);
    }

    public static int getAmplifier(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag == null ? MIN_AMPLIFIER : Mth.clamp(tag.getInt(AMPLIFIER_TAG), MIN_AMPLIFIER, MAX_AMPLIFIER);
    }

    public static void setAmplifier(ItemStack stack, int amplifier) {
        int clamped = Mth.clamp(amplifier, MIN_AMPLIFIER, MAX_AMPLIFIER);
        if (clamped == MIN_AMPLIFIER) {
            CompoundTag tag = stack.getTag();
            if (tag != null) {
                tag.remove(AMPLIFIER_TAG);
                if (tag.isEmpty()) stack.setTag(null);
            }
        } else {
            stack.getOrCreateTag().putInt(AMPLIFIER_TAG, clamped);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return DRINK_DURATION;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (livingEntity instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        }
        if (!level.isClientSide) {
            level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(),
                    ModSounds.OMINOUS_BOTTLE_DISPOSE.get(), livingEntity.getSoundSource(), 1.0F, 1.0F);
            livingEntity.addEffect(new MobEffectInstance(MobEffects.BAD_OMEN, BAD_OMEN_DURATION, getAmplifier(stack), false, false, true));
        }
        if (!(livingEntity instanceof Player player) || !player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        PotionUtils.addPotionTooltip(List.of(new MobEffectInstance(MobEffects.BAD_OMEN, BAD_OMEN_DURATION, getAmplifier(stack), false, false, true)), tooltip, 1.0F);
    }
}
