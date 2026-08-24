/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraftforge.eventbus.api.Event
 */
package com.vivideru.masteryofmagic.goldification.event;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Event;

public final class GoldificationExpireEvent
extends Event {
    private final TargetType targetType;
    private final ServerLevel level;
    @Nullable
    private final BlockPos position;
    @Nullable
    private final Entity entity;

    private GoldificationExpireEvent(TargetType targetType, ServerLevel level, @Nullable BlockPos position, @Nullable Entity entity) {
        this.targetType = targetType;
        this.level = level;
        this.position = position == null ? null : position.m_7949_();
        this.entity = entity;
    }

    public static GoldificationExpireEvent forBlock(ServerLevel level, BlockPos position) {
        return new GoldificationExpireEvent(TargetType.BLOCK, level, position, null);
    }

    public static GoldificationExpireEvent forEntity(ServerLevel level, Entity entity) {
        return new GoldificationExpireEvent(TargetType.ENTITY, level, null, entity);
    }

    public TargetType getTargetType() {
        return this.targetType;
    }

    public ServerLevel getLevel() {
        return this.level;
    }

    @Nullable
    public BlockPos getPosition() {
        return this.position;
    }

    @Nullable
    public Entity getEntity() {
        return this.entity;
    }

    public static enum TargetType {
        BLOCK,
        ENTITY;

    }
}

