/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleEngine
 *  net.minecraft.client.particle.TerrainParticle
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.vivideru.masteryofmagic.mixins;

import com.vivideru.masteryofmagic.goldification.client.GoldificationClientState;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ParticleEngine.class})
public abstract class GoldificationParticleEngineMixin {
    @Shadow
    protected ClientLevel f_107287_;

    @Shadow
    public abstract void m_107344_(Particle var1);

    @Inject(method={"destroy(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"}, at={@At(value="HEAD")}, cancellable=true, remap=false, require=0)
    private void replaceDestroyParticlesDev(BlockPos position, BlockState state, CallbackInfo callbackInfo) {
        this.replaceDestroyParticles(position, callbackInfo);
    }

    @Inject(method={"m_107355_(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"}, at={@At(value="HEAD")}, cancellable=true, remap=false, require=0)
    private void replaceDestroyParticlesProduction(BlockPos position, BlockState state, CallbackInfo callbackInfo) {
        this.replaceDestroyParticles(position, callbackInfo);
    }

    @Inject(method={"crack(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)V"}, at={@At(value="HEAD")}, cancellable=true, remap=false, require=0)
    private void replaceHitParticlesDev(BlockPos position, Direction side, CallbackInfo callbackInfo) {
        this.replaceHitParticles(position, side, callbackInfo);
    }

    @Inject(method={"m_107367_(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)V"}, at={@At(value="HEAD")}, cancellable=true, remap=false, require=0)
    private void replaceHitParticlesProduction(BlockPos position, Direction side, CallbackInfo callbackInfo) {
        this.replaceHitParticles(position, side, callbackInfo);
    }

    private void replaceDestroyParticles(BlockPos position, CallbackInfo callbackInfo) {
        if (!GoldificationClientState.isBlockGoldified(position)) {
            return;
        }
        BlockState gold = Blocks.f_50074_.m_49966_();
        for (int x = 0; x < 4; ++x) {
            for (int y = 0; y < 4; ++y) {
                for (int z = 0; z < 4; ++z) {
                    double offsetX = ((double)x + 0.5) / 4.0;
                    double offsetY = ((double)y + 0.5) / 4.0;
                    double offsetZ = ((double)z + 0.5) / 4.0;
                    Particle particle = new TerrainParticle(this.f_107287_, (double)position.m_123341_() + offsetX, (double)position.m_123342_() + offsetY, (double)position.m_123343_() + offsetZ, offsetX - 0.5, offsetY - 0.5, offsetZ - 0.5, gold, position).updateSprite(gold, position);
                    this.m_107344_(particle);
                }
            }
        }
        callbackInfo.cancel();
    }

    private void replaceHitParticles(BlockPos position, Direction side, CallbackInfo callbackInfo) {
        if (!GoldificationClientState.isBlockGoldified(position)) {
            return;
        }
        BlockState gold = Blocks.f_50074_.m_49966_();
        double x = (double)position.m_123341_() + 0.5 + (double)side.m_122429_() * 0.51;
        double y = (double)position.m_123342_() + 0.5 + (double)side.m_122430_() * 0.51;
        double z = (double)position.m_123343_() + 0.5 + (double)side.m_122431_() * 0.51;
        Particle particle = new TerrainParticle(this.f_107287_, x, y, z, 0.0, 0.0, 0.0, gold, position).updateSprite(gold, position);
        particle.m_107268_(0.2f);
        particle.m_6569_(0.6f);
        this.m_107344_(particle);
        callbackInfo.cancel();
    }
}

