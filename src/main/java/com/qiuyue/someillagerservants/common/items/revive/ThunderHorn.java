package com.qiuyue.someillagerservants.common.items.revive;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.qiuyue.someillagerservants.common.init.ModEntityTypes;
import com.Polarice3.Goety.common.entities.util.SummonCircleBoss;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import java.util.List;
import javax.annotation.Nullable;

import com.qiuyue.someillagerservants.common.entities.hostile.StormNecromancer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class ThunderHorn extends Item {
    public ThunderHorn() {
        super((new Item.Properties()).stacksTo(1));
    }

    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
        super.finishUsingItem(stack, worldIn, entityLiving);
        if (worldIn instanceof ServerLevel serverWorld) {
            boolean flag = serverWorld.getBiome(entityLiving.blockPosition())
                    .is(net.minecraft.tags.BiomeTags.IS_MOUNTAIN)
                    && serverWorld.isThundering();
            if (flag) {
                entityLiving.playSound((SoundEvent)((Holder.Reference)SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(6)).get(), 16.0F, 1.0F);
                serverWorld.playSound((Player)null, entityLiving.blockPosition(), (SoundEvent)((Holder.Reference)SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(6)).get(), SoundSource.NEUTRAL, 16.0F, 1.0F);
                StormNecromancer necromancer = (StormNecromancer)((EntityType)ModEntityTypes.STORM_NECROMANCER.get()).create(worldIn);
                if (necromancer != null) {
                    BlockPos blockPos = entityLiving.blockPosition().relative(entityLiving.getDirection());
                    necromancer.setPos((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
                    necromancer.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(entityLiving.blockPosition()), MobSpawnType.MOB_SUMMONED, (SpawnGroupData)null, (CompoundTag)null);
                    SummonCircleBoss summonCircle = new SummonCircleBoss(worldIn, blockPos, necromancer);
                    serverWorld.addFreshEntity(summonCircle);
                    if (!(entityLiving instanceof Player) || !((Player)entityLiving).isCreative()) {
                        stack.setCount(0);
                    }
                }
            } else {
                if (entityLiving instanceof Player) {
                    Player player = (Player)entityLiving;
                    player.displayClientMessage(Component.translatable("info.someillagerservants.items.thunder_horn.failure"), true);
                }

                entityLiving.playSound(SoundEvents.FIRE_EXTINGUISH, 1.0F, 1.0F);
                serverWorld.playSound((Player)null, entityLiving.blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 1.0F, 1.0F);
            }
        }

        return stack;
    }

    public int getUseDuration(ItemStack stack) {
        return 25;
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        playerIn.startUsingItem(handIn);
        return InteractionResultHolder.consume(itemstack);
    }

    public void onUseTick(Level worldIn, LivingEntity livingEntityIn, ItemStack stack, int count) {
        if (!worldIn.isClientSide) {
            ServerLevel serverWorld = (ServerLevel)worldIn;
            ServerParticleUtil.addParticlesAroundSelf(serverWorld, ModParticleTypes.BIG_ELECTRIC.get(), livingEntityIn);
            ServerParticleUtil.addParticlesAroundSelf(serverWorld, ModParticleTypes.ELECTRIC.get(), livingEntityIn);
        }

    }

    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("info.someillagerservants.items.thunder_horn.desc").withStyle(ChatFormatting.DARK_PURPLE));
    }
}
