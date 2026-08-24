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
public final class GoldifyBlockEvent
extends Event {
    private final ServerLevel level;
    private final BlockPos position;
    private final BlockState state;
    private final long expireGameTime;
    @Nullable
    private final Entity source;

    public GoldifyBlockEvent(ServerLevel level, BlockPos position, BlockState state, long expireGameTime, @Nullable Entity source) {
        this.level = level;
        this.position = position.m_7949_();
        this.state = state;
        this.expireGameTime = expireGameTime;
        this.source = source;
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

    public long getExpireGameTime() {
        return this.expireGameTime;
    }

    @Nullable
    public Entity getSource() {
        return this.source;
    }
}

