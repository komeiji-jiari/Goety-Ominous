package com.qiuyue.goetyominous.common.mixin.trial;

import com.qiuyue.goetyominous.common.blocks.trial.RaidStartContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.raid.Raids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Raids.class)
public abstract class RaidsRaidOmenMixin {

    @Redirect(method = "createOrExtendRaid", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;blockPosition()Lnet/minecraft/core/BlockPos;"))
    private BlockPos goetyominous$useRaidOmenOrigin(ServerPlayer player) {
        BlockPos origin = RaidStartContext.current();
        return origin != null ? origin : player.blockPosition();
    }
}
