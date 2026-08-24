/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.LivingEntity
 *  org.jetbrains.annotations.Nullable
 */
package com.vivideru.masteryofmagic.spells.midas;

import com.vivideru.masteryofmagic.entity.PhilosopherKingMidasEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface MidasBossSpell {
    public ResourceLocation id();

    public int castingFlag();

    public int cooldownTicks();

    public int maximumCastTicks();

    default public void start(ServerLevel level, PhilosopherKingMidasEntity midas, @Nullable LivingEntity target) {
    }

    public void tick(ServerLevel var1, PhilosopherKingMidasEntity var2, @Nullable LivingEntity var3, int var4);

    default public boolean shouldContinue(ServerLevel level, PhilosopherKingMidasEntity midas, @Nullable LivingEntity target, int castTick) {
        return castTick < this.maximumCastTicks();
    }

    default public void stop(ServerLevel level, PhilosopherKingMidasEntity midas, @Nullable LivingEntity target, int castTick) {
    }
}

