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
public final class GoldifyEntityEvent
extends Event {
    private final Entity target;
    private final long expireGameTime;
    @Nullable
    private final Entity source;

    public GoldifyEntityEvent(Entity target, long expireGameTime, @Nullable Entity source) {
        this.target = target;
        this.expireGameTime = expireGameTime;
        this.source = source;
    }

    public Entity getTarget() {
        return this.target;
    }

    public long getExpireGameTime() {
        return this.expireGameTime;
    }

    @Nullable
    public Entity getSource() {
        return this.source;
    }
}

