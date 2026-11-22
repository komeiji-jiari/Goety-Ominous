package com.qiuyue.goetyominous.common.mixin;

import com.Polarice3.Goety.common.entities.util.BrewGas;
import com.qiuyue.goetyominous.common.entities.hostile.piglin.PiglinShaman;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BrewGas.class)
public abstract class BrewGasAllyFilterMixin {

    @Redirect(method = "m_8119_", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_5801_()Z"), remap = false)
    private boolean goetyominous$filterShamanGas(LivingEntity entity) {
        BrewGas gas = (BrewGas) (Object) this;
        if (gas.getOwner() instanceof PiglinShaman shaman
                && (entity == shaman || shaman.isOwnSummon(entity))) {
            return false;
        }
        return entity.isAffectedByPotions();
    }
}
