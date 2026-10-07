package com.qiuyue.goetyominous.common.mixin;

import com.qiuyue.goetyominous.common.blocks.trial.VaultBlockEntity;
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
public abstract class VaultLevelRendererMixin {
    @Shadow
    @Nullable
    private ClientLevel level;

    @Inject(method = "levelEvent", at = @At("HEAD"), require = 1)
    private void goetyominous$vaultAnimation(int type, BlockPos pos, int data, CallbackInfo ci) {
        if (this.level == null) {
            return;
        }
        ClientLevel clientLevel = this.level;
        if (type == 3017) {
            RandomSource random = clientLevel.getRandom();
            for (int i = 0; i < 20; ++i) {
                double x = pos.getX() + 0.4D + random.nextDouble() * 0.2D;
                double y = pos.getY() + 0.4D + random.nextDouble() * 0.2D;
                double z = pos.getZ() + 0.4D + random.nextDouble() * 0.2D;
                double xd = random.nextGaussian() * 0.02D;
                double yd = random.nextGaussian() * 0.02D;
                double zd = random.nextGaussian() * 0.02D;
                clientLevel.addParticle(ParticleTypes.SMALL_FLAME, x, y, z, xd, yd, zd * 0.25D);
                clientLevel.addParticle(ParticleTypes.SMOKE, x, y, z, xd, yd, zd);
            }
            return;
        }
        if (type != 3015 && type != 3016) {
            return;
        }
        if (!(clientLevel.getBlockEntity(pos) instanceof VaultBlockEntity vault)) {
            return;
        }
        if (type == 3015) {
            VaultBlockEntity.Client.emitActivationParticles(clientLevel, vault.getBlockPos(), vault.getBlockState(),
                    vault.getSharedData(), data != 0 ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.SMALL_FLAME);
        } else {
            VaultBlockEntity.Client.emitDeactivationParticles(clientLevel, vault.getBlockPos(),
                    data != 0 ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.SMALL_FLAME);
        }
        RandomSource random = clientLevel.getRandom();
        clientLevel.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                type == 3015 ? ModSounds.VAULT_ACTIVATE.get() : ModSounds.VAULT_DEACTIVATE.get(),
                SoundSource.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, true);
    }
}
