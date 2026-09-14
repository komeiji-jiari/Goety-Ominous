package com.qiuyue.goetyominous.common.items.ac;

import com.Polarice3.Goety.common.items.revive.ReviveServantItem;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.common.ritual.RitualRequirements;
import com.Polarice3.Goety.utils.SEHelper;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.qiuyue.goetyominous.common.entities.ally.ac.AtlatitanServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.LuxtructosaurusServant;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.phys.Vec3;

public class ExtinctionCataclyst extends ReviveServantItem {

    public ExtinctionCataclyst() {
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
        LuxtructosaurusServant servant;
        if (summon instanceof LuxtructosaurusServant luxtructosaurus) {
            servant = luxtructosaurus;
        } else {
            servant = AcEntityRegistry.LUXTRUCTOSAURUS_SERVANT.get().create(level);
            if (servant == null) {
                return InteractionResult.FAIL;
            }
            servant.setTrueOwner(player);
            servant.setPersistenceRequired();
            if (servant.isRemoved()) {
                return InteractionResult.FAIL;
            }
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
            Vec3 center = atlatitan.position();
            double y = center.y + atlatitan.getBbHeight() * 0.5D;
            serverLevel.sendParticles(ParticleTypes.ASH, center.x, y, center.z, 24, 1.5D, 1.5D, 1.5D, 0.1D);
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, center.x, y, center.z, 16, 1.0D, 1.0D, 1.0D, 0.05D);
        }
        atlatitan.discard();
        player.swing(hand);
        SEHelper.addCooldown(player, this, MathHelper.secondsToTicks(ItemConfig.ReviveSecondsCool.get()));
        stack.shrink(1);
        return InteractionResult.SUCCESS;
    }
}
