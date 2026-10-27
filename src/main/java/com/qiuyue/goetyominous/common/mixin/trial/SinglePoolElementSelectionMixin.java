package com.qiuyue.goetyominous.common.mixin.trial;

import com.qiuyue.goetyominous.common.worldgen.jigsaw.TrialJigsawContext;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SinglePoolElement.class)
public abstract class SinglePoolElementSelectionMixin {

    private static final Comparator<StructureTemplate.StructureBlockInfo> BY_SELECTION_PRIORITY =
            Comparator.<StructureTemplate.StructureBlockInfo>comparingInt(info -> {
                CompoundTag nbt = info.nbt();
                return nbt == null ? 0 : nbt.getInt("selection_priority");
            }).reversed();

    @Inject(method = "getShuffledJigsawBlocks", at = @At("RETURN"), require = 1)
    private void goetyominous$sortBySelectionPriority(StructureTemplateManager manager, BlockPos pos, Rotation rotation,
                                                      RandomSource random,
                                                      CallbackInfoReturnable<List<StructureTemplate.StructureBlockInfo>> cir) {
        if (TrialJigsawContext.prioritiesActive()) {
            cir.getReturnValue().sort(BY_SELECTION_PRIORITY);
        }
    }
}
