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
public abstract class TrialSpawnerLevelRendererMixin {
    @Shadow
    @Nullable
    private ClientLevel level;

    @Inject(method = "levelEvent", at = @At("HEAD"), require = 1)
    private void goetyominous$trialSpawnerEvent(int type, BlockPos pos, int data, CallbackInfo ci) {
        if (this.level == null) {
            return;
        }
        ClientLevel clientLevel = this.level;
        RandomSource random = clientLevel.getRandom();
        switch (type) {
            case 3011 -> TrialSpawner.addSpawnParticles(clientLevel, pos, random,
                    TrialSpawner.FlameParticle.byId(data).particleType);
            case 3012 -> {
                clientLevel.playLocalSound(pos, ModSounds.TRIAL_SPAWNER_SPAWN_MOB.get(), SoundSource.BLOCKS, 1.0F,
                        (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, true);
                TrialSpawner.addSpawnParticles(clientLevel, pos, random,
                        TrialSpawner.FlameParticle.byId(data).particleType);
            }
            case 3021 -> {
                clientLevel.playLocalSound(pos, ModSounds.TRIAL_SPAWNER_SPAWN_ITEM.get(), SoundSource.BLOCKS, 1.0F,
                        (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, true);
                TrialSpawner.addSpawnParticles(clientLevel, pos, random,
                        TrialSpawner.FlameParticle.byId(data).particleType);
            }
            case 3013 -> {
                clientLevel.playLocalSound(pos, ModSounds.TRIAL_SPAWNER_DETECT_PLAYER.get(), SoundSource.BLOCKS, 1.0F,
                        (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, true);
                TrialSpawner.addDetectPlayerParticles(clientLevel, pos, random, data,
                        ModParticleTypes.TRIAL_SPAWNER_DETECTION.get());
            }
            case 3019 -> {
                clientLevel.playLocalSound(pos, ModSounds.TRIAL_SPAWNER_DETECT_PLAYER.get(), SoundSource.BLOCKS, 1.0F,
                        (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, true);
                TrialSpawner.addDetectPlayerParticles(clientLevel, pos, random, data,
                        ModParticleTypes.TRIAL_SPAWNER_DETECTION_OMINOUS.get());
            }
            case 3014 -> {
                clientLevel.playLocalSound(pos, ModSounds.TRIAL_SPAWNER_EJECT_ITEM.get(), SoundSource.BLOCKS, 1.0F,
                        (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, true);
                TrialSpawner.addEjectItemParticles(clientLevel, pos, random);
            }
            default -> {
            }
        }
    }
}
