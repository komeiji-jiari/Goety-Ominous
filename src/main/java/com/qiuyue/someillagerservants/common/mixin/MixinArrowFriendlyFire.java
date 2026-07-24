package com.qiuyue.someillagerservants.common.mixin;

import com.Polarice3.Goety.api.entities.IOwned;
import com.qiuyue.someillagerservants.common.entities.ally.neutral.AbstractPiglinServant;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractArrow.class)
public class MixinArrowFriendlyFire {

    @Inject(method = "canHitEntity(Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"), cancellable = true)
    private void someillagerservants$canHitEntity(Entity target, CallbackInfoReturnable<Boolean> cir) {
        AbstractArrow arrow = (AbstractArrow) (Object) this;
        if (arrow.getOwner() instanceof AbstractPiglinServant piglin) {
            if (target instanceof IOwned owned
                    && owned.getTrueOwner() == piglin.getTrueOwner()
                    && owned.getTrueOwner() != null) {
                cir.setReturnValue(false);
            }
        }
    }
}
