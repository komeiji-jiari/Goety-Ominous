package com.qiuyue.goetyominous.common.mixin;

import com.Polarice3.Goety.common.items.BlastFungusItem;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.MathHelper;
import com.qiuyue.goetyominous.common.items.FungusPackHelper;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlastFungusItem.class)
public class BlastFungusItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void goetyominous$onUse(Level level, Player player, InteractionHand hand,
                                           CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack stack = player.getItemInHand(hand);
        if (!FungusPackHelper.hasMatchingFungus(player, ModItems.BLAST_FUNGUS.get())) return;

        int count = Math.min(stack.getCount(), 9);
        for (int i = 0; i < count; i++) {
            MobUtil.throwBlastFungus(player, level);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                ModSounds.BLAST_FUNGUS_THROW.get(), SoundSource.PLAYERS, 1.0F,
                0.8F + level.random.nextFloat() * 0.4F);

        if (!level.isClientSide) {
            int consume = (count + 2) / 3;
            stack.shrink(consume);
            player.getCooldowns().addCooldown(stack.getItem(), MathHelper.secondsToTicks(count));
        }

        cir.setReturnValue(InteractionResultHolder.sidedSuccess(stack, level.isClientSide()));
    }
}
