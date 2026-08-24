/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.util.Mth
 */
package com.vivideru.masteryofmagic.goldification.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;

public final class GoldificationVertexConsumer
implements VertexConsumer {
    private final VertexConsumer delegate;

    private GoldificationVertexConsumer(VertexConsumer delegate) {
        this.delegate = delegate;
    }

    public static VertexConsumer wrap(VertexConsumer consumer) {
        return consumer instanceof GoldificationVertexConsumer ? consumer : new GoldificationVertexConsumer(consumer);
    }

    public VertexConsumer m_5483_(double x, double y, double z) {
        this.delegate.m_5483_(x, y, z);
        return this;
    }

    public VertexConsumer m_6122_(int red, int green, int blue, int alpha) {
        int[] gold = GoldificationVertexConsumer.goldPalette(red, green, blue);
        this.delegate.m_6122_(gold[0], gold[1], gold[2], alpha);
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
        int[] gold = GoldificationVertexConsumer.goldPalette(red, green, blue);
        this.delegate.m_7404_(gold[0], gold[1], gold[2], alpha);
    }

    public void m_141991_() {
        this.delegate.m_141991_();
    }

    private static int[] goldPalette(int red, int green, int blue) {
        float luminance = Mth.m_14036_((float)((0.2126f * (float)red + 0.7152f * (float)green + 0.0722f * (float)blue) / 255.0f), (float)0.0f, (float)1.0f);
        float shaped = Mth.m_14116_((float)luminance);
        int goldRed = Mth.m_14045_((int)((int)Mth.m_14179_((float)shaped, (float)82.0f, (float)255.0f)), (int)0, (int)255);
        int goldGreen = Mth.m_14045_((int)((int)Mth.m_14179_((float)shaped, (float)38.0f, (float)218.0f)), (int)0, (int)255);
        int goldBlue = Mth.m_14045_((int)((int)Mth.m_14179_((float)shaped, (float)4.0f, (float)92.0f)), (int)0, (int)255);
        return new int[]{goldRed, goldGreen, goldBlue};
    }
}

