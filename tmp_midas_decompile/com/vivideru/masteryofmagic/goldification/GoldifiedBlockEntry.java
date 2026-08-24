/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.nbt.CompoundTag
 */
package com.vivideru.masteryofmagic.goldification;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;

public record GoldifiedBlockEntry(long expireGameTime, long createdGameTime, long autoShatterGameTime, @Nullable UUID sourceUuid) {
    private static final String EXPIRE = "ExpireGameTime";
    private static final String CREATED = "CreatedGameTime";
    private static final String AUTO_SHATTER = "AutoShatterGameTime";
    private static final String SOURCE = "Source";

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.m_128356_(EXPIRE, this.expireGameTime);
        tag.m_128356_(CREATED, this.createdGameTime);
        tag.m_128356_(AUTO_SHATTER, this.autoShatterGameTime);
        if (this.sourceUuid != null) {
            tag.m_128362_(SOURCE, this.sourceUuid);
        }
        return tag;
    }

    public static GoldifiedBlockEntry load(CompoundTag tag) {
        UUID source = tag.m_128403_(SOURCE) ? tag.m_128342_(SOURCE) : null;
        return new GoldifiedBlockEntry(tag.m_128454_(EXPIRE), tag.m_128454_(CREATED), tag.m_128441_(AUTO_SHATTER) ? tag.m_128454_(AUTO_SHATTER) : -1L, source);
    }
}

