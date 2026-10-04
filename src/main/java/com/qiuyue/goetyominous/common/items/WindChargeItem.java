package com.qiuyue.goetyominous.common.items;

import com.qiuyue.goetyominous.common.entities.projectile.ServantWindCharge;
import com.qiuyue.goetyominous.common.init.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class WindChargeItem extends Item {
    public WindChargeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            ServantWindCharge charge = new ServantWindCharge(level, player,
                    player.getX(), player.getEyeY(), player.getZ(), 1.0F, 1.0F);
            charge.setRadius(1.2F);
            charge.setKnockbackMultiplier(1.22F);
            charge.setGrantsFallDamageImmunity(true);
            charge.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(charge);
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.WIND_CHARGE_THROW.get(),
                SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        ItemStack stack = player.getItemInHand(hand);
        player.getCooldowns().addCooldown(this, 10);
        player.awardStat(Stats.ITEM_USED.get(this));
        stack.shrink(1);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
