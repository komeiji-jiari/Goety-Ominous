package com.qiuyue.goetyominus.common.items;

import com.Polarice3.Goety.init.ModSounds;
import com.qiuyue.goetyominus.common.entities.projectile.AcidFungus;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AcidFungusItem extends Item {

    public AcidFungusItem() {
        super(new Properties());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        boolean enhanced = FungusPackHelper.hasMatchingFungus(player, ModItems.ACID_FUNGUS.get());

        if (!level.isClientSide) {
            AcidFungus fungus = new AcidFungus(level, player);
            fungus.shootFromRotation(player, player.getXRot(), player.getYRot(), -20.0F, 0.75F, 8.0F);
            level.addFreshEntity(fungus);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                ModSounds.BLAST_FUNGUS_THROW.get(), SoundSource.PLAYERS,
                0.5F, 0.8F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

        if (!level.isClientSide && !player.getAbilities().instabuild) {
            if (enhanced) {
                if (level.random.nextFloat() >= 0.5F) {
                    stack.shrink(1);
                }
            } else {
                stack.shrink(1);
            }
            player.getCooldowns().addCooldown(this, 10);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
