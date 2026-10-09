package com.qiuyue.goetyominous.common.mixin;

import com.qiuyue.goetyominous.common.blocks.trial.TrialPotteryPatterns;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DecoratedPotPatterns.class)
public abstract class DecoratedPotPatternsMixin {
    @Inject(method = "getResourceKey", at = @At("HEAD"), cancellable = true, require = 1)
    private static void goetyominous$sherdPattern(Item item, CallbackInfoReturnable<ResourceKey<String>> cir) {
        ResourceKey<String> pattern = TrialPotteryPatterns.getPattern(item);
        if (pattern != null) {
            cir.setReturnValue(pattern);
        }
    }
}
