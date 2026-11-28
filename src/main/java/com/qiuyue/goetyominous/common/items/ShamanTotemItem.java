package com.qiuyue.goetyominous.common.items;

import com.Polarice3.Goety.common.items.revive.ReviveServantItem;
import com.Polarice3.Goety.common.ritual.RitualRequirements;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.qiuyue.goetyominous.common.entities.ally.mobs.AllyPiglinShaman;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ShamanTotemItem extends ReviveServantItem {

    public ShamanTotemItem() {
        super(new Item.Properties().rarity(Rarity.UNCOMMON).setNoRepair().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (SEHelper.isOnCooldown(player, stack)) {
            return InteractionResultHolder.fail(stack);
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 25;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int count) {
        if (level instanceof ServerLevel serverLevel) {
            ServerParticleUtil.addParticlesAroundSelf(serverLevel, ParticleTypes.WITCH, entity);
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        super.finishUsingItem(stack, level, entity);
        if (!(level instanceof ServerLevel serverLevel)) {
            return stack;
        }
        if (!(entity instanceof Player player)) {
            return stack;
        }
        if (SEHelper.isOnCooldown(player, stack)) {
            return stack;
        }
        Entity stored = getSummon(stack, serverLevel);
        if (stored != null && !(stored instanceof AllyPiglinShaman)) {
            return stack;
        }
        AllyPiglinShaman servant = stored == null ? null : (AllyPiglinShaman) stored;
        if (servant != null && servant.getTrueOwner() != player) {
            return stack;
        }
        if (!RitualRequirements.canSummon(serverLevel, player, ModEntityTypes.ALLY_SHAMAN.get())) {
            return stack;
        }
        Vec3 look = player.getLookAngle().multiply(1.0D, 0.0D, 1.0D);
        if (look.lengthSqr() < 1.0E-4D) {
            look = Vec3.atLowerCornerOf(player.getDirection().getNormal());
        }
        BlockPos pos = BlockFinder.SummonPosition(serverLevel, player,
                BlockPos.containing(player.position().add(look.normalize().scale(3.0D))));
        if (servant != null) {
            servant.setPos(Vec3.atBottomCenterOf(pos));
            servant.deathTime = 0;
            servant.hurtTime = 0;
            servant.setDeltaMovement(Vec3.ZERO);
            servant.clearFire();
            servant.setHealth(servant.getMaxHealth());
            servant.setPersistenceRequired();
        } else {
            servant = new AllyPiglinShaman(ModEntityTypes.ALLY_SHAMAN.get(), serverLevel);
            servant.setTrueOwner(player);
            servant.setPos(Vec3.atBottomCenterOf(pos));
            servant.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(pos),
                    MobSpawnType.MOB_SUMMONED, null, null);
            servant.setHealth(servant.getMaxHealth());
            servant.setPersistenceRequired();
        }
        servant.lookAt(EntityAnchorArgument.Anchor.EYES, player.position());
        if (serverLevel.addFreshEntity(servant)) {
            SoundEvent horn = ModSounds.APOSTLE_PREPARE_SUMMON.get();
            player.playSound(horn, 16.0F, 1.0F);
            serverLevel.playSound(null, player.blockPosition(), horn, SoundSource.NEUTRAL, 16.0F, 1.0F);
            stack.shrink(1);
            SEHelper.addCooldown(player, this, MathHelper.secondsToTicks(5));
        }
        return stack;
    }
}
