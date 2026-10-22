package com.qiuyue.goetyominous.client.render.model.mm;

import java.util.WeakHashMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class MutantMobAnimationStates {
    private static final WeakHashMap<LivingEntity, MutantMobAnimationState> STATES = new WeakHashMap<>();

    private MutantMobAnimationStates() {
    }

    public static MutantMobAnimationState get(LivingEntity entity) {
        return STATES.computeIfAbsent(entity, e -> new MutantMobAnimationState());
    }
}
