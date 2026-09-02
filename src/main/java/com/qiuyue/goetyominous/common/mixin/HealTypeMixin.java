package com.qiuyue.goetyominous.common.mixin;

import com.Polarice3.Goety.utils.ServantUtil;
import com.qiuyue.goetyominous.common.init.ModTags;
import com.qiuyue.goetyominous.utils.CroneCuriosUtil;
import com.qiuyue.goetyominous.utils.ModMobType;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com/Polarice3/Goety/utils/ServantUtil$HealType")
public class HealTypeMixin {

    @Inject(method = "getConfig", at = @At("HEAD"), cancellable = true, remap = false)
    private static void goetyominous$felHealConfig(LivingEntity servant, LivingEntity owner,
                                                   CallbackInfoReturnable<ServantUtil.HealConfig> cir) {
        boolean fel = servant.getMobType() == ModMobType.FEL
                || servant.getType().is(ModTags.EntityTypes.FEL_HEAL);
        if (fel && CroneCuriosUtil.hasCroneRobe(owner)) {
            cir.setReturnValue(new ServantUtil.HealConfig(true, 1, 1, 1.0F));
        }
    }
}
