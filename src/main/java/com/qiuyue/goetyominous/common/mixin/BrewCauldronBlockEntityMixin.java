package com.qiuyue.goetyominous.common.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(com.Polarice3.Goety.common.blocks.entities.BrewCauldronBlockEntity.class)
public class BrewCauldronBlockEntityMixin {

    @Redirect(method = "takeBrew", remap = false,
            at = @At(value = "INVOKE", remap = false,
                    target = "Lcom/Polarice3/Goety/utils/CuriosFinder;hasWitchRobe(Lnet/minecraft/world/entity/LivingEntity;)Z"))
    private boolean goetyominous$croneRobeBrewBonus(LivingEntity player) {
        return com.Polarice3.Goety.utils.CuriosFinder.hasWitchRobe(player)
                || com.qiuyue.goetyominous.utils.CroneCuriosUtil.hasCroneRobe(player);
    }
}
