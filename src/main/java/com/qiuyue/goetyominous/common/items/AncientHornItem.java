package com.qiuyue.goetyominous.common.items;

import com.Polarice3.Goety.common.entities.util.SummonCircleBoss;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.hostile.piglin.PiglinShaman;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AncientHornItem extends Item {
    private static final TagKey<Structure> SUMMON_STRUCTURES = TagKey.create(
            Registries.STRUCTURE,
            new ResourceLocation(GoetyOminous.MOD_ID, "ancient_horn_summon"));

    public AncientHornItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
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
        if (serverLevel.structureManager()
                .getStructureWithPieceAt(entity.blockPosition(), SUMMON_STRUCTURES)
                .isValid()) {
            SoundEvent horn = SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(6).get();
            entity.playSound(horn, 16.0F, 1.0F);
            serverLevel.playSound(null, entity.blockPosition(), horn, SoundSource.NEUTRAL, 16.0F, 1.0F);
            PiglinShaman shaman = ModEntityTypes.PIGLIN_SHAMAN.get().create(serverLevel);
            if (shaman != null) {
                BlockPos pos = entity.blockPosition().relative(entity.getDirection());
                shaman.setPos(pos.getX(), pos.getY(), pos.getZ());
                shaman.finalizeSpawn(serverLevel,
                        serverLevel.getCurrentDifficultyAt(entity.blockPosition()),
                        MobSpawnType.MOB_SUMMONED, null, null);
                SummonCircleBoss summonCircle = new SummonCircleBoss(level, pos, shaman);
                serverLevel.addFreshEntity(summonCircle);
                if (!(entity instanceof Player player) || !player.isCreative()) {
                    stack.setCount(0);
                }
            }
        } else {
            if (entity instanceof Player player) {
                player.displayClientMessage(
                        Component.translatable("info.goetyominous.ancient_horn.failure"), true);
            }
            entity.playSound(SoundEvents.FIRE_EXTINGUISH, 1.0F, 1.0F);
            serverLevel.playSound(null, entity.blockPosition(), SoundEvents.FIRE_EXTINGUISH,
                    SoundSource.NEUTRAL, 1.0F, 1.0F);
        }
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip,
                                TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("info.goetyominous.ancient_horn.desc")
                .withStyle(ChatFormatting.DARK_PURPLE));
    }
}
