package com.qiuyue.goetyominous.common.mixin;

import com.qiuyue.goetyominous.common.blocks.CultStatueBlock;
import com.qiuyue.goetyominous.common.blocks.entities.CultStatueBlockEntity;
import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractPiglinServant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class CultStatueGlowMixin {

    @Inject(method = "shouldEntityAppearGlowing", at = @At("HEAD"), cancellable = true, require = 1)
    private void goetyominous$shrineGlow(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || !player.isCrouching()) {
            return;
        }
        if (!(entity instanceof AbstractPiglinServant)) {
            return;
        }
        if (!(minecraft.hitResult instanceof BlockHitResult hit) || minecraft.level == null) {
            return;
        }
        CultStatueBlockEntity shrine = CultStatueBlockEntity.resolve(minecraft.level, hit.getBlockPos());
        if (shrine == null) {
            return;
        }
        if (shrine.getBlockState().getValue(CultStatueBlock.ENABLED)) {
            return;
        }
        if (shrine.getIds().contains(entity.getId())) {
            cir.setReturnValue(true);
        }
    }
}
