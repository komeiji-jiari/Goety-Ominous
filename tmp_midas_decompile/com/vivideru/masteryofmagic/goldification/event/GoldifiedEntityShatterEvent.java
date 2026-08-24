/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.world.entity.Entity
 *  net.minecraftforge.eventbus.api.Cancelable
 *  net.minecraftforge.eventbus.api.Event
 */
package com.vivideru.masteryofmagic.goldification.event;

import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public final class GoldifiedEntityShatterEvent
extends Event {
    private final Entity target;
    @Nullable
    private final Entity attacker;
    private int nuggetCount;

    public GoldifiedEntityShatterEvent(Entity target, @Nullable Entity attacker, int nuggetCount) {
        this.target = target;
        this.attacker = attacker;
        this.nuggetCount = Math.max(0, nuggetCount);
    }

    public Entity getTarget() {
        return this.target;
    }

    @Nullable
    public Entity getAttacker() {
        return this.attacker;
    }

    public int getNuggetCount() {
        return this.nuggetCount;
    }

    public void setNuggetCount(int nuggetCount) {
        this.nuggetCount = Math.max(0, nuggetCount);
    }
}

