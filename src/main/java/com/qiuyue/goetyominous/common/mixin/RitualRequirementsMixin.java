package com.qiuyue.goetyominous.common.mixin;

import com.Polarice3.Goety.common.ritual.RitualRequirements;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RitualRequirements.class)
public class RitualRequirementsMixin {

    @Inject(method = "getConvertEntity", at = @At("RETURN"), cancellable = true, remap = false)
    private static void goetyominous$requireTuskedElephant(
            TagKey<EntityType<?>> tag, BlockPos pos, Level level, CallbackInfoReturnable<Mob> cir) {
        Mob mob = cir.getReturnValue();
        if (tag.location().equals(new ResourceLocation("goety", "elephant_convert"))
                && isNonTuskedElephant(mob)) {
            cir.setReturnValue(null);
        }
    }

    private static boolean isNonTuskedElephant(Mob mob) {
        if (mob == null) {
            return false;
        }
        try {
            Class<?> elephantClass = Class.forName("com.github.alexthe666.alexsmobs.entity.EntityElephant");
            if (!elephantClass.isInstance(mob)) {
                return false;
            }
            java.lang.reflect.Method isTusked = elephantClass.getMethod("isTusked");
            return !(boolean) isTusked.invoke(mob);
        } catch (ReflectiveOperationException e) {
            return false;
        }
    }
}
