package com.qiuyue.goetyominous.common.entities.hostile.grimmonarch;

import com.qiuyue.goetyominous.common.network.GrimMonarchNightPacket;
import com.qiuyue.goetyominous.common.network.ModNetwork;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.network.PacketDistributor;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class GrimMonarchNightData extends SavedData {
    private static final String DATA_NAME = "goetyominous_grim_monarch_night";
    private static final String MONARCHS = "Monarchs";
    private static final String STARTED = "Started";
    private static final String LOCKED = "Locked";
    private static final String START = "Start";
    private static final String TARGET = "Target";
    private static final String FINAL = "Final";
    private static final String TICKS = "Ticks";

    private final Set<UUID> monarchs = new LinkedHashSet<>();
    private boolean started;
    private boolean locked;
    private boolean lastSentLocked;
    private long transitionStart;
    private long transitionTarget;
    private long finalTarget;
    private int transitionTicks;

    private GrimMonarchNightData() {
    }

    public static GrimMonarchNightData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                GrimMonarchNightData::load, GrimMonarchNightData::new, DATA_NAME);
    }

    public void register(UUID id) {
        if (this.monarchs.add(id)) {
            this.setDirty();
        }
    }

    public void unregister(UUID id) {
        if (this.monarchs.remove(id)) {
            if (this.monarchs.isEmpty()) {
                this.started = false;
                this.locked = false;
                this.transitionStart = 0L;
                this.transitionTarget = 0L;
                this.finalTarget = 0L;
                this.transitionTicks = 0;
            }
            this.setDirty();
        }
    }

    public void tick(ServerLevel level) {
        if (this.monarchs.isEmpty()) {
            if (this.lastSentLocked) {
                this.lastSentLocked = false;
                sendLocked(level, false);
            }
            return;
        }
        if (!this.started) {
            long now = level.getDayTime();
            long day = Math.floorDiv(now, GrimMonarchConstants.NIGHT_DAY_TICKS);
            long timeOfDay = Math.floorMod(now, GrimMonarchConstants.NIGHT_DAY_TICKS);
            long midnightDay = timeOfDay > GrimMonarchConstants.NIGHT_MIDNIGHT_TIME ? day + 1L : day;
            long fullMoonDay = midnightDay;
            long remainder = Math.floorMod(fullMoonDay, GrimMonarchConstants.NIGHT_FULL_MOON_CYCLE);
            if (remainder != 0L) {
                fullMoonDay += GrimMonarchConstants.NIGHT_FULL_MOON_CYCLE - remainder;
            }
            this.transitionStart = now;
            this.transitionTarget = midnightDay * GrimMonarchConstants.NIGHT_DAY_TICKS
                    + GrimMonarchConstants.NIGHT_MIDNIGHT_TIME;
            this.finalTarget = fullMoonDay * GrimMonarchConstants.NIGHT_DAY_TICKS
                    + GrimMonarchConstants.NIGHT_MIDNIGHT_TIME;
            this.transitionTicks = 0;
            this.started = true;
            this.setDirty();
            if (this.transitionTarget <= this.transitionStart) {
                setLevelTime(level, this.finalTarget);
                this.locked = true;
                this.setDirty();
            }
        }
        if (this.locked) {
            setLevelTime(level, this.finalTarget);
        } else {
            ++this.transitionTicks;
            this.setDirty();
            double progress = Math.min(1.0D, (double) this.transitionTicks
                    / GrimMonarchConstants.NIGHT_TRANSITION_TICKS);
            if (progress >= 1.0D) {
                setLevelTime(level, this.finalTarget);
                this.locked = true;
                this.setDirty();
            } else {
                long time = this.transitionStart + (long) ((this.transitionTarget
                        - this.transitionStart) * progress);
                setLevelTime(level, time);
            }
        }
        if (this.locked != this.lastSentLocked) {
            this.lastSentLocked = this.locked;
            sendLocked(level, this.locked);
        }
    }

    public void sendStateTo(ServerPlayer player, ServerLevel level) {
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new GrimMonarchNightPacket(level.dimension(), this.locked));
    }

    private static void sendLocked(ServerLevel level, boolean locked) {
        ModNetwork.CHANNEL.send(PacketDistributor.DIMENSION.with(level::dimension),
                new GrimMonarchNightPacket(level.dimension(), locked));
    }

    private static void setLevelTime(ServerLevel level, long dayTime) {
        level.setDayTime(dayTime);
        boolean daylight = level.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT);
        for (ServerPlayer player : level.players()) {
            player.connection.send(new ClientboundSetTimePacket(
                    level.getGameTime(), level.getDayTime(), daylight));
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (UUID id : this.monarchs) {
            list.add(StringTag.valueOf(id.toString()));
        }
        tag.put(MONARCHS, list);
        tag.putBoolean(STARTED, this.started);
        tag.putBoolean(LOCKED, this.locked);
        tag.putLong(START, this.transitionStart);
        tag.putLong(TARGET, this.transitionTarget);
        tag.putLong(FINAL, this.finalTarget);
        tag.putInt(TICKS, this.transitionTicks);
        return tag;
    }

    private static GrimMonarchNightData load(CompoundTag tag) {
        GrimMonarchNightData data = new GrimMonarchNightData();
        ListTag list = tag.getList(MONARCHS, Tag.TAG_STRING);
        for (int i = 0; i < list.size(); i++) {
            try {
                data.monarchs.add(UUID.fromString(list.getString(i)));
            } catch (IllegalArgumentException ignored) {
            }
        }
        data.started = tag.getBoolean(STARTED) && !data.monarchs.isEmpty();
        data.locked = data.started && tag.getBoolean(LOCKED);
        data.transitionStart = tag.getLong(START);
        data.transitionTarget = tag.getLong(TARGET);
        data.finalTarget = tag.getLong(FINAL);
        data.transitionTicks = tag.getInt(TICKS);
        if (!data.started || data.finalTarget <= 0L
                || (!data.locked && data.transitionTarget <= 0L)) {
            data.started = false;
            data.locked = false;
            data.transitionStart = 0L;
            data.transitionTarget = 0L;
            data.finalTarget = 0L;
            data.transitionTicks = 0;
        }
        return data;
    }
}
