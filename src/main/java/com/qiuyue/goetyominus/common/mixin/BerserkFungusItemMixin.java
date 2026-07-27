package com.qiuyue.goetyominus.common.mixin;

import com.Polarice3.Goety.common.items.BerserkFungusItem;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.entities.projectiles.BerserkFungus;
import com.Polarice3.Goety.init.ModSounds;
import com.qiuyue.goetyominus.common.items.FungusPackHelper;
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

@Mixin(BerserkFungusItem.class)
public class BerserkFungusItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void goetyominus$onUse(Level level, Player player, InteractionHand hand,
                                           CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack stack = player.getItemInHand(hand);
        if (!FungusPackHelper.hasMatchingFungus(player, ModItems.BERSERK_FUNGUS.get())) return;

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                ModSounds.BLAST_FUNGUS_THROW.get(), SoundSource.PLAYERS, 0.5F,
                (0.4F + level.random.nextFloat() * 0.4F) / 0.8F);

        if (!level.isClientSide) {
            BerserkFungus fungus = new BerserkFungus(player, level);
            fungus.shootFromRotation(player, player.getXRot(), player.getYRot(), -20.0F, 0.75F, 8.0F);
            level.addFreshEntity(fungus);

            if (level.random.nextFloat() >= 0.5F) {
                stack.shrink(1);
            }
        }

        cir.setReturnValue(InteractionResultHolder.sidedSuccess(stack, level.isClientSide()));
    }
}