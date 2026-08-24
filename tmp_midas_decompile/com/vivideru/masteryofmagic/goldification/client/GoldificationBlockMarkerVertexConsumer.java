/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexConsumer
 */
package com.vivideru.masteryofmagic.goldification.client;

import com.mojang.blaze3d.vertex.VertexConsumer;

public final class GoldificationBlockMarkerVertexConsumer
implements VertexConsumer {
    private static final int MIN_GOLD_MARKER_ALPHA = 223;
    private static final int MAX_GOLD_MARKER_ALPHA = 254;
    private final VertexConsumer delegate;
    private final int markerAlpha;

    private GoldificationBlockMarkerVertexConsumer(VertexConsumer delegate, float conversionStrength) {
        this.delegate = delegate;
        float clampedStrength = Math.max(0.0f, Math.min(1.0f, conversionStrength));
        this.markerAlpha = 223 + Math.round(clampedStrength * 31.0f);
    }

    public static VertexConsumer wrap(VertexConsumer consumer) {
        return GoldificationBlockMarkerVertexConsumer.wrap(consumer, 1.0f);
    }

    public static VertexConsumer wrap(VertexConsumer consumer, float conversionStrength) {
        return consumer instanceof GoldificationBlockMarkerVertexConsumer ? consumer : new GoldificationBlockMarkerVertexConsumer(consumer, conversionStrength);
    }

    public VertexConsumer m_5483_(double x, double y, double z) {
        this.delegate.m_5483_(x, y, z);
        return this;
    }

    public VertexConsumer m_6122_(int red, int green, int blue, int alpha) {
        this.delegate.m_6122_(red, green, blue, this.markerAlpha);
        return this;
    }

    public VertexConsumer m_7421_(float u, float v) {
        this.delegate.m_7421_(u, v);
        return this;
    }

    public VertexConsumer m_7122_(int u, int v) {
        this.delegate.m_7122_(u, v);
        return this;
    }

    public VertexConsumer m_7120_(int u, int v) {
        this.delegate.m_7120_(u, v);
        return this;
    }

    public VertexConsumer m_5601_(float x, float y, float z) {
        this.delegate.m_5601_(x, y, z);
        return this;
    }

    public void m_5752_() {
        this.delegate.m_5752_();
    }

    public void m_7404_(int red, int green, int blue, int alpha) {
        this.delegate.m_7404_(red, green, blue, this.markerAlpha);
    }

    public void m_141991_() {
        this.delegate.m_141991_();
    }
}

