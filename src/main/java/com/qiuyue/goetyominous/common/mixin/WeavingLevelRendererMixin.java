package com.qiuyue.goetyominous.common.mixin;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class WeavingLevelRendererMixin {
    @Shadow
    @Nullable
    private ClientLevel level;

    @Inject(method = "levelEvent", at = @At("HEAD"), require = 1)
    private void goetyominous$spawnCobweb(int type, BlockPos pos, int data, CallbackInfo ci) {
        if (type != 3018 || this.level == null) {
            return;
        }
        ClientLevel clientLevel = this.level;
        RandomSource random = clientLevel.getRandom();
        for (int i = 0; i < 10; ++i) {
            double xd = random.nextGaussian() * 0.02D;
            double yd = random.nextGaussian() * 0.02D;
            double zd = random.nextGaussian() * 0.02D;
            clientLevel.addParticle(ParticleTypes.POOF,
                    pos.getX() + random.nextDouble(),
                    pos.getY() + random.nextDouble(),
                    pos.getZ() + random.nextDouble(),
                    xd, yd, zd);
        }
    }
}
