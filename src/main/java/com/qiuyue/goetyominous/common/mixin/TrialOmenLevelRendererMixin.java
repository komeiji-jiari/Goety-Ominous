package com.qiuyue.goetyominous.common.mixin;

import com.qiuyue.goetyominous.common.blocks.trial.TrialSpawner;
import com.qiuyue.goetyominous.common.init.ModParticleTypes;
import com.qiuyue.goetyominous.common.init.ModSounds;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
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
        clientLevel.playLocalSound(pos, ModSounds.TRIAL_SPAWNER_OMINOUS_ACTIVATE.get(), SoundSource.BLOCKS,
                data == 0 ? 0.3F : 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, true);
        TrialSpawner.addDetectPlayerParticles(clientLevel, pos, random, 0,
                ModParticleTypes.TRIAL_SPAWNER_DETECTION_OMINOUS.get());
        TrialSpawner.addBecomeOminousParticles(clientLevel, pos, random);
    }
}
