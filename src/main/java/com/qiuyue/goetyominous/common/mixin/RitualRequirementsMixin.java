package com.qiuyue.goetyominous.common.mixin;

import com.Polarice3.Goety.common.ritual.RitualRequirements;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RitualRequirements.class)
public class RitualRequirementsMixin {

    private static final ResourceLocation ELEPHANT_CONVERT = new ResourceLocation("goety", "elephant_convert");
    private static final ResourceLocation TREMORZILLA_CONVERT = new ResourceLocation("goety", "tremorzilla_convert");

    @Inject(method = "getConvertEntity", at = @At("RETURN"), cancellable = true, remap = false)
    private static void goetyominous$requireTuskedElephant(
            TagKey<EntityType<?>> tag, BlockPos pos, Level level, CallbackInfoReturnable<Mob> cir) {
        Mob mob = cir.getReturnValue();
        if (ELEPHANT_CONVERT.equals(tag.location()) && isNonTuskedElephant(mob)) {
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "getConvertEntity", at = @At("HEAD"), cancellable = true, remap = false)
    private static void goetyominous$findBabyTremorzilla(
            TagKey<EntityType<?>> tag, BlockPos pos, Level level, CallbackInfoReturnable<Mob> cir) {
        if (!TREMORZILLA_CONVERT.equals(tag.location())) {
            return;
        }
        Mob nearest = null;
        double best = Double.MAX_VALUE;
        for (Mob mob : level.getEntitiesOfClass(Mob.class, new AABB(pos).inflate(RitualRequirements.RANGE))) {
            if (!mob.isBaby() || !mob.getType().is(tag)) {
                continue;
            }
            double d = mob.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
            if (d < best) {
                best = d;
                nearest = mob;
            }
        }
        cir.setReturnValue(nearest);
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
