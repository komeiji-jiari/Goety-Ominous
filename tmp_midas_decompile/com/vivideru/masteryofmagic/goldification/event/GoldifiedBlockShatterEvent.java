/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraftforge.eventbus.api.Cancelable
 *  net.minecraftforge.eventbus.api.Event
 */
package com.vivideru.masteryofmagic.goldification.event;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public final class GoldifiedBlockShatterEvent
extends Event {
    private final ServerLevel level;
    private final BlockPos position;
    private final BlockState state;
    @Nullable
    private final Entity breaker;
    private int nuggetCount;

    public GoldifiedBlockShatterEvent(ServerLevel level, BlockPos position, BlockState state, @Nullable Entity breaker, int nuggetCount) {
        this.level = level;
        this.position = position.m_7949_();
        this.state = state;
        this.breaker = breaker;
        this.nuggetCount = Math.max(0, nuggetCount);
    }

    public ServerLevel getLevel() {
        return this.level;
    }

    public BlockPos getPosition() {
        return this.position;
    }

    public BlockState getState() {
        return this.state;
    }

    @Nullable
    public Entity getBreaker() {
        return this.breaker;
    }

    public int getNuggetCount() {
        return this.nuggetCount;
    }

    public void setNuggetCount(int nuggetCount) {
        this.nuggetCount = Math.max(0, nuggetCount);
    }
}

