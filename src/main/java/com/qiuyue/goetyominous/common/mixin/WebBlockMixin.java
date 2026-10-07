package com.qiuyue.goetyominous.common.mixin;

import com.qiuyue.goetyominous.common.init.ModEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WebBlock.class)
public class WebBlockMixin {

    @Redirect(method = "entityInside", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;makeStuckInBlock(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/phys/Vec3;)V"))
    private void goetyominous$weavingStuckMultiplier(Entity entity, BlockState state, Vec3 multiplier) {
        if (entity instanceof LivingEntity living && living.hasEffect(ModEffects.WEAVING.get())) {
            entity.makeStuckInBlock(state, new Vec3(0.5D, 0.25D, 0.5D));
        } else {
            entity.makeStuckInBlock(state, multiplier);
        }
    }
}
