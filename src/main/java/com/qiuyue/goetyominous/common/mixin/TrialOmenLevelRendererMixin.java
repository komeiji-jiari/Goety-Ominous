package com.qiuyue.goetyominous.common.mixin;

import com.qiuyue.goetyominous.common.init.ModParticleTypes;
import com.qiuyue.goetyominous.common.init.ModSounds;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class TrialOmenLevelRendererMixin {
    @Shadow
    @Nullable
    private ClientLevel level;

    @Inject(method = "levelEvent", at = @At("HEAD"), require = 1)
    private void goetyominous$trialOmenEvent(int type, BlockPos pos, int data, CallbackInfo ci) {
        if (type != 3020 || this.level == null) {
            return;
        }
        ClientLevel clientLevel = this.level;
        RandomSource random = clientLevel.getRandom();
        clientLevel.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                ModSounds.TRIAL_SPAWNER_OMINOUS_ACTIVATE.get(), SoundSource.BLOCKS,
                data == 0 ? 0.3F : 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, true);
        for (int i = 0; i < 30; ++i) {
            double offsetX = (2.0F * random.nextFloat() - 1.0F) * 0.65D;
            double offsetZ = (2.0F * random.nextFloat() - 1.0F) * 0.65D;
            clientLevel.addParticle(ModParticleTypes.TRIAL_SPAWNER_DETECTION_OMINOUS.get(),
                    pos.getX() + 0.5D + offsetX,
                    pos.getY() + 0.1D + random.nextFloat() * 0.8D,
                    pos.getZ() + 0.5D + offsetZ,
                    0.0D, 0.0D, 0.0D);
        }
        for (int i = 0; i < 20; ++i) {
            double x = pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 2.0D;
            double y = pos.getY() + 0.5D + (random.nextDouble() - 0.5D) * 2.0D;
            double z = pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 2.0D;
            double vx = random.nextGaussian() * 0.02D;
            double vy = random.nextGaussian() * 0.02D;
            double vz = random.nextGaussian() * 0.02D;
            clientLevel.addParticle(ModParticleTypes.TRIAL_OMEN.get(), x, y, z, vx, vy, vz);
            clientLevel.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, vx, vy, vz);
        }
    }
}
