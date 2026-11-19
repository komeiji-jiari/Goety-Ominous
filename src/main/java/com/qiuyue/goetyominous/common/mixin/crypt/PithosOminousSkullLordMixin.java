package com.qiuyue.goetyominous.common.mixin.crypt;

import com.Polarice3.Goety.common.blocks.PithosBlock;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.qiuyue.goetyominous.common.init.ModEffects;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PithosBlock.class)
public abstract class PithosOminousSkullLordMixin {

    @ModifyExpressionValue(method = "use", require = 1,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraftforge/registries/RegistryObject;get()Ljava/lang/Object;"))
    private Object goetyominous$ominousSkullLord(Object original, @Local(argsOnly = true) Player player) {
        if (original == ModEntityType.SKULL_LORD.get()
                && (player.hasEffect(MobEffects.BAD_OMEN) || player.hasEffect(ModEffects.TRIAL_OMEN.get()))) {
            return ModEntityTypes.OMINOUS_SKULL_LORD.get();
        }
        return original;
    }
}
