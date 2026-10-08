package com.qiuyue.goetyominous.common.mixin;

import com.qiuyue.goetyominous.common.blocks.trial.TrialSpawnerBlock;
import com.qiuyue.goetyominous.common.blocks.trial.TrialSpawnerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(SpawnEggItem.class)
public abstract class SpawnEggItemMixin {
    @Shadow
    public abstract EntityType<?> getType(@Nullable CompoundTag entityTag);

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void goetyominous$useOnTrialSpawner(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof TrialSpawnerBlock)) {
            return;
        }
        if (!(level.getBlockEntity(pos) instanceof TrialSpawnerBlockEntity trialSpawner)) {
            return;
        }
        ItemStack stack = context.getItemInHand();
        trialSpawner.setEntityId(this.getType(stack.getTag()), level.getRandom());
        level.sendBlockUpdated(pos, state, state, 3);
        level.gameEvent(context.getPlayer(), GameEvent.BLOCK_CHANGE, pos);
        stack.shrink(1);
        cir.setReturnValue(InteractionResult.CONSUME);
    }
}
