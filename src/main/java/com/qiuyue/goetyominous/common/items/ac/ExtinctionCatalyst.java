package com.qiuyue.goetyominous.common.items.ac;

import com.Polarice3.Goety.common.items.revive.ReviveServantItem;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.common.ritual.RitualRequirements;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.qiuyue.goetyominous.common.entities.ally.ac.AtlatitanServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.LuxtructosaurusServant;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import com.qiuyue.goetyominous.common.init.ac.AcParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class ExtinctionCatalyst extends ReviveServantItem {

    public ExtinctionCatalyst() {
        super(new Properties().rarity(Rarity.RARE).setNoRepair().fireResistant().stacksTo(1));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        Level level = player.level();
        if (level.isClientSide || !(target instanceof AtlatitanServant atlatitan)) {
            return super.interactLivingEntity(stack, player, target, hand);
        }
        if (atlatitan.getTrueOwner() != player) {
            return InteractionResult.FAIL;
        }
        Entity summon = ReviveServantItem.getSummon(stack, level);
        if (!(summon instanceof LuxtructosaurusServant servant)) {
            return InteractionResult.FAIL;
        }
        if (servant.getTrueOwner() != player
                || !RitualRequirements.canSummon(level, player, AcEntityRegistry.LUXTRUCTOSAURUS_SERVANT.get())) {
            return InteractionResult.FAIL;
        }
        servant.setHealth(servant.getMaxHealth());
        servant.setPos(atlatitan.getX(), atlatitan.getY(), atlatitan.getZ());
        servant.setYRot(atlatitan.getYRot());
        servant.setXRot(atlatitan.getXRot());
        servant.setYHeadRot(atlatitan.yBodyRot);
        servant.yBodyRot = atlatitan.yBodyRot;
        if (!level.addFreshEntity(servant)) {
            return InteractionResult.FAIL;
        }
        servant.playSound(SoundEvents.GENERIC_EXPLODE, 1.0F, 0.5F);
        servant.playSound(ACSoundRegistry.LUXTRUCTOSAURUS_ROAR.get(), 3.0F, 0.5F);
        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 8; i++) {
                ServerParticleUtil.addParticlesAroundSelf(serverLevel,
                        AcParticles.LUXTRUCTOSAURUS_SERVANT_ASH.get(), servant);
            }
            serverLevel.sendParticles(ACParticleRegistry.TEPHRA_FLAME.get(),
                    atlatitan.getX(),
                    atlatitan.getY() + atlatitan.getBbHeight() * 0.5D,
                    atlatitan.getZ(),
                    80,
                    atlatitan.getBbWidth() * 0.5D,
                    atlatitan.getBbHeight() * 0.5D,
                    atlatitan.getBbWidth() * 0.5D,
                    0.35D);
        }
        servant.playSound(SoundEvents.GENERIC_EXPLODE, 1.0F, 0.5F);
        servant.playSound(ACSoundRegistry.LUXTRUCTOSAURUS_ROAR.get(), 3.0F, 0.5F);
        atlatitan.discard();
        player.swing(hand);
        SEHelper.addCooldown(player, this, MathHelper.secondsToTicks(ItemConfig.ReviveSecondsCool.get()));
        stack.shrink(1);
        return InteractionResult.SUCCESS;
    }
}
