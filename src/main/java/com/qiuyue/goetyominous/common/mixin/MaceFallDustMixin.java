package com.qiuyue.goetyominous.common.mixin;

import com.mojang.authlib.GameProfile;
import com.qiuyue.goetyominous.utils.WindChargeImpulse;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class MaceFallDustMixin extends Player {
    protected MaceFallDustMixin(Level level, BlockPos pos, float yRot, GameProfile gameProfile) {
        super(level, pos, yRot, gameProfile);
    }

    @Shadow
    public abstract ServerLevel serverLevel();

    @Inject(method = "doCheckFallDamage", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;checkFallDamage(DZLnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)V"), require = 1)
    private void goetyominous$maceLandingDust(double movementX, double movementY, double movementZ, boolean onGround, CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        if (!(self instanceof WindChargeImpulse impulse) || !impulse.getSpawnExtraParticlesOnFall() || !onGround || self.fallDistance <= 0.0F) {
            return;
        }
        BlockPos pos = this.getOnPos(0.2F);
        BlockState state = this.serverLevel().getBlockState(pos);
        Vec3 center = Vec3.atBottomCenterOf(pos).add(0.0D, 0.5D, 0.0D);
        int count = (int)(50.0F * self.fallDistance);
        this.serverLevel().sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state), center.x, center.y, center.z, count, 0.3D, 0.3D, 0.3D, 0.15D);
        impulse.setSpawnExtraParticlesOnFall(false);
    }
}
