package com.qiuyue.goetyominous.common.mixin.windshrine;

import com.qiuyue.goetyominous.common.worldgen.windshrine.WindShrineBlend;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Blender.class)
public abstract class BlenderWindShrineMixin implements WindShrineBlend.Blended {
    @Unique
    @Nullable
    private WindShrineBlend.Area goetyominous$shrineBlend;

    @Override
    public void goety$setShrineBlend(WindShrineBlend.Area area) {
        this.goetyominous$shrineBlend = area;
    }

    @Inject(method = "of", at = @At("RETURN"), cancellable = true, require = 1)
    private static void goetyominous$blendShrines(@Nullable WorldGenRegion region, CallbackInfoReturnable<Blender> cir) {
        if (region != null) {
            ChunkPos center = region.getCenter();
            cir.setReturnValue(WindShrineBlend.blender(cir.getReturnValue(), region.getLevel().getChunkSource().randomState(), center.getMinBlockX(), center.getMinBlockZ(), 16));
        }
    }

    @Inject(method = "blendDensity", at = @At("RETURN"), cancellable = true, require = 1)
    private void goetyominous$fillShrineLand(DensityFunction.FunctionContext context, double density, CallbackInfoReturnable<Double> cir) {
        if (this.goetyominous$shrineBlend != null) {
            cir.setReturnValue(this.goetyominous$shrineBlend.fill(context.blockX(), context.blockY(), context.blockZ(), cir.getReturnValue()));
        }
    }
}
