package com.qiuyue.goetyominous.common.mixin;

import com.qiuyue.goetyominous.common.entities.ally.mobs.AllyPiglinShaman;
import com.qiuyue.goetyominous.common.entities.hostile.piglin.PiglinShaman;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.PiglinSpecificSensor;
import net.minecraft.world.entity.monster.piglin.Piglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PiglinSpecificSensor.class)
public abstract class PiglinFearShamanMixin {

    @SuppressWarnings("unchecked")
    @Inject(method = "doTick", at = @At("TAIL"))
    private void goetyominous$fearShaman(ServerLevel level, LivingEntity entity, CallbackInfo ci) {
        if (!(entity instanceof Piglin)) {
            return;
        }
        Brain<LivingEntity> brain = (Brain<LivingEntity>) entity.getBrain();
        if (brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED).isPresent()) {
            return;
        }
        NearestVisibleLivingEntities visible = brain
                .getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
                .orElse(NearestVisibleLivingEntities.empty());
        visible.findClosest(e -> e instanceof PiglinShaman || e instanceof AllyPiglinShaman)
                .ifPresent(shaman -> brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, shaman));
    }
}
