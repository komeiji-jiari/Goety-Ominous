package com.qiuyue.goetyominous.common.mixin;

import com.qiuyue.goetyominous.client.particle.SmashParticleUtils;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class MaceLevelRendererMixin {
    @Shadow
    @Nullable
    private ClientLevel level;

    @Inject(method = "levelEvent", at = @At("HEAD"), require = 1)
    private void goetyominous$maceSmashParticles(int type, BlockPos pos, int data, CallbackInfo ci) {
        if (type != 2013 || this.level == null) {
            return;
        }
        SmashParticleUtils.spawnSmashAttackParticles(this.level, pos, data);
    }
}
