package com.qiuyue.goetyominous.common.mixin;

import com.Polarice3.Goety.common.inventory.WitchRobeInventory;
import com.qiuyue.goetyominous.utils.CroneCuriosUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WitchRobeInventory.class)
public class WitchRobeInventoryMixin {

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void goetyominous$croneDisplayName(CallbackInfoReturnable<Component> cir) {
        WitchRobeInventory inventory = (WitchRobeInventory) (Object) this;
        LivingEntity entity = inventory.getLivingEntity();
        if (entity != null && CroneCuriosUtil.hasCroneRobe(entity)) {
            cir.setReturnValue(Component.translatable("item.goetyominous.crone_robe"));
        }
    }
}
