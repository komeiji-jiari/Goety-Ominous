package com.qiuyue.goetyominous.common.mixin;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieServant;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieServant.class)
public class ZombieServantMiredMixin {

    @Inject(
            method = "getVariant(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/entity/EntityType;",
            at = @At("RETURN"), cancellable = true, remap = false)
    private void goetyominous$miredInSwamp(Level level, BlockPos pos,
                                           CallbackInfoReturnable<EntityType<?>> cir) {
        if (cir.getReturnValue() == ModEntityType.ZOMBIE_SERVANT.get()
                && level.getBiome(pos).is(Tags.Biomes.IS_SWAMP)) {
            cir.setReturnValue(ModEntityTypes.MIRED_SERVANT.get());
        }
    }
}
