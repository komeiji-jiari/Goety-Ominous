package com.qiuyue.goetyominous.common.items;

import com.Polarice3.Goety.common.items.revive.ReviveServantItem;
import com.Polarice3.Goety.common.ritual.RitualRequirements;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.SEHelper;
import com.qiuyue.goetyominous.common.entities.ally.mobs.BreezeServant;
import com.qiuyue.goetyominous.common.entities.ally.mobs.HurricaneServant;
import com.qiuyue.goetyominous.common.entities.hostile.BreezeEntity;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.common.init.ModParticleTypes;
import com.qiuyue.goetyominous.common.init.ModSounds;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class HurricaneCoreItem extends ReviveServantItem {

    public HurricaneCoreItem() {
        super(new Properties()
                .rarity(Rarity.UNCOMMON)
                .setNoRepair()
                .fireResistant()
                .stacksTo(1));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        boolean ownServant = target instanceof BreezeServant servantBreeze && servantBreeze.getTrueOwner() == player;
        if (!ownServant && !(target instanceof BreezeEntity)) {
            return InteractionResult.PASS;
        }
        LivingEntity breeze = target;
        Level level = player.level();
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        Entity entity = getSummon(stack, level);
        if (entity == null) {
            HurricaneServant fresh = new HurricaneServant(ModEntityTypes.HURRICANE_SERVANT.get(), level);
            fresh.setTrueOwner(player);
            entity = fresh;
        }
        if (entity instanceof HurricaneServant servant && servant.getTrueOwner() == player
                && RitualRequirements.canSummon(level, player, ModEntityTypes.HURRICANE_SERVANT.get())) {
            servant.setHealth(servant.getMaxHealth());
            servant.setPos(breeze.getX(), breeze.getY(), breeze.getZ());
            servant.lookAt(EntityAnchorArgument.Anchor.EYES, player.position());
            if (level.addFreshEntity(servant)) {
                servant.spawnAnim();
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ModParticleTypes.GUST_EMITTER_LARGE.get(), servant.getX(), servant.getY(), servant.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                    serverLevel.sendParticles(ParticleTypes.CLOUD, servant.getX(), servant.getY(1.0D), servant.getZ(), 30, 0.8D, 1.0D, 0.8D, 0.05D);
                }
                level.playSound(null, servant.getX(), servant.getY(), servant.getZ(), ModSounds.BREEZE_WIND_CHARGE_BURST.get(), SoundSource.PLAYERS, 2.0F, 0.6F);
                level.playSound(null, servant.getX(), servant.getY(), servant.getZ(), ModSounds.BREEZE_IDLE_GROUND.get(), SoundSource.PLAYERS, 2.0F, 0.45F);
                breeze.discard();
                player.swing(hand);
                SEHelper.addCooldown(player, this, MathHelper.secondsToTicks(5));
                stack.shrink(1);
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.FAIL;
    }
}
