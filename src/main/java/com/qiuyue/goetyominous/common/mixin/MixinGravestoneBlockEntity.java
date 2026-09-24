package com.qiuyue.goetyominous.common.mixin;

import com.Polarice3.Goety.common.blocks.GravestoneBlock;
import com.Polarice3.Goety.common.blocks.entities.GravestoneBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.TrainingBlockEntity;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GravestoneBlockEntity.class)
public class MixinGravestoneBlockEntity {

    @Inject(method = "setVariant", at = @At("RETURN"), remap = false)
    private void goetyominous$miredVariant(ItemStack stack, Level level, BlockPos pos, CallbackInfo ci) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        BlockState state = level.getBlockState(pos);
        if (state.hasProperty(GravestoneBlock.WATERLOGGED)
                && state.getValue(GravestoneBlock.WATERLOGGED)) {
            return;
        }

        TrainingBlockEntity self = (TrainingBlockEntity) (Object) this;
        if (self.getTrainMob() == ModEntityTypes.MIRED_SERVANT.get()) return;

        boolean swamp = serverLevel.getBiome(pos.below()).is(Tags.Biomes.IS_SWAMP);
        boolean bog = self.getBlocks(s -> s.is(Blocks.MUD), 15)
                && self.getBlocks(s -> s.is(Blocks.CAULDRON), 1);
        if (!swamp && !bog) return;

        self.setEntityType(ModEntityTypes.MIRED_SERVANT.get());
        self.markUpdated();
    }
}
