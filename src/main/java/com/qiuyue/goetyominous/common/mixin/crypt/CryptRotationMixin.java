package com.qiuyue.goetyominous.common.mixin.crypt;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.qiuyue.goetyominous.common.worldgen.crypt.CryptPlacementRules;
import com.qiuyue.goetyominous.common.worldgen.windshrine.WindShrinePlacementRules;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = JigsawPlacement.class, priority = 1100)
public abstract class CryptRotationMixin {

    @WrapOperation(method = "addPieces", require = 1,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/Rotation;getRandom(Lnet/minecraft/util/RandomSource;)Lnet/minecraft/world/level/block/Rotation;"))
    private static Rotation goetyominous$forcedStartRotation(RandomSource random, Operation<Rotation> original) {
        return CryptPlacementRules.forcedRotationOr(WindShrinePlacementRules.forcedRotationOr(original.call(random)));
    }
}
