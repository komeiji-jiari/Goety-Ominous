/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.DynamicTexture
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.Resource
 *  net.minecraft.server.packs.resources.ResourceManagerReloadListener
 */
package com.vivideru.masteryofmagic.client;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public final class DarkenedArmorTextures {
    private static final Map<ResourceLocation, ResourceLocation> CACHE = new ConcurrentHashMap<ResourceLocation, ResourceLocation>();
    public static final ResourceManagerReloadListener RELOAD_LISTENER = resourceManager -> CACHE.clear();

    private DarkenedArmorTextures() {
    }

    public static ResourceLocation getOrCreate(ResourceLocation baseArmorTexture) {
        ResourceLocation cached = CACHE.get(baseArmorTexture);
        if (cached != null) {
            return cached;
        }
        ResourceLocation out = DarkenedArmorTextures.generate(baseArmorTexture);
        CACHE.put(baseArmorTexture, out);
        return out;
    }

    private static ResourceLocation generate(ResourceLocation base) {
        NativeImage img;
        int HUE_BINS = 36;
        float BIN_DEG = 10.0f;
        float HIST_MIN_SAT = 0.18f;
        float HIST_MIN_VAL = 0.15f;
        float DOMINANT_MIN_FRAC = 0.08f;
        int MAX_DOMINANT_CENTERS = 4;
        float MERGE_CENTER_DEG = 18.0f;
        float PROTECT_RADIUS_DEG = 22.0f;
        float BONE_HUE_MIN = 18.0f;
        float BONE_HUE_MAX = 55.0f;
        float BONE_SAT_MAX = 0.45f;
        float BONE_VAL_MIN = 0.55f;
        float FIRE_RANGE_MIN = 8.0f;
        float FIRE_RANGE_MAX = 62.0f;
        float FIRE_MIN_SAT = 0.42f;
        float FIRE_MIN_VAL = 0.22f;
        float FIRE_PRESENCE_MIN_FRAC = 0.06f;
        float GOLD_RANGE_MIN = 35.0f;
        float GOLD_RANGE_MAX = 72.0f;
        float GOLD_MIN_SAT = 0.14f;
        float GOLD_MAX_SAT = 0.6f;
        float GOLD_MIN_VAL = 0.28f;
        float GOLD_PRESENCE_MIN_FRAC = 0.06f;
        float GRAY_CHROMA_MAX = 0.22f;
        float BROWN_HUE_MIN = 12.0f;
        float BROWN_HUE_MAX = 55.0f;
        float BROWN_SAT_MAX = 0.55f;
        float BROWN_VAL_MAX = 0.55f;
        float GB_FLOOR = 0.04f;
        float GB_STRENGTH = 0.92f;
        float GB_DESAT = 0.55f;
        float GB_CONTRAST = 1.35f;
        float C_FLOOR = 0.07f;
        float C_STRENGTH = 0.62f;
        float C_CONTRAST = 1.1f;
        float VERY_BRIGHT_LUMA = 0.96f;
        boolean PRESERVE_SPECULAR = false;
        float WHITE_SAT_MAX = 0.1f;
        float WHITE_VAL_MIN = 0.88f;
        Minecraft mc = Minecraft.m_91087_();
        TextureManager tm = mc.m_91097_();
        try {
            Resource res = mc.m_91098_().m_215593_(base);
            try (InputStream in = res.m_215507_();){
                img = NativeImage.m_85058_((InputStream)in);
            }
        }
        catch (Exception e) {
            return base;
        }
        int w = img.m_84982_();
        int h = img.m_85084_();
        float[] hist = new float[36];
        float totalW = 0.0f;
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                int abgr = img.m_84985_(x, y);
                int a = abgr >>> 24 & 0xFF;
                if (a == 0) continue;
                int b = abgr >>> 16 & 0xFF;
                int g = abgr >>> 8 & 0xFF;
                int r = abgr & 0xFF;
                float rf = (float)r / 255.0f;
                float gf = (float)g / 255.0f;
                float bf = (float)b / 255.0f;
                float[] hsv = DarkenedArmorTextures.rgbToHsv(rf, gf, bf);
                float hue = hsv[0];
                float sat = hsv[1];
                float val = hsv[2];
                if (sat < 0.18f || val < 0.15f) continue;
                int bin = (int)(hue / 10.0f);
                if (bin < 0) {
                    bin = 0;
                }
                if (bin >= 36) {
                    bin = 35;
                }
                float wgt = sat * val;
                int n = bin;
                hist[n] = hist[n] + wgt;
                totalW += wgt;
            }
        }
        float[] dominantCenters = DarkenedArmorTextures.pickDominantHueCentersDeg(hist, totalW, 10.0f, 0.08f, 4, 18.0f);
        float firePresence = 0.0f;
        float goldPresence = 0.0f;
        if (totalW > 1.0E-5f) {
            for (int i = 0; i < 36; ++i) {
                float center = ((float)i + 0.5f) * 10.0f;
                float frac = hist[i] / totalW;
                if (DarkenedArmorTextures.hueInRangeDeg(center, 8.0f, 62.0f)) {
                    firePresence += frac;
                }
                if (!DarkenedArmorTextures.hueInRangeDeg(center, 35.0f, 72.0f)) continue;
                goldPresence += frac;
            }
        }
        boolean fireFamilyPresent = firePresence >= 0.06f;
        boolean goldFamilyPresent = goldPresence >= 0.06f;
        float fireCenter = -1.0f;
        float goldCenter = -1.0f;
        if (dominantCenters != null) {
            float d;
            float bestD;
            if (fireFamilyPresent) {
                bestD = 9999.0f;
                for (float c : dominantCenters) {
                    if (!DarkenedArmorTextures.hueInRangeDeg(c, 8.0f, 62.0f) || !((d = DarkenedArmorTextures.hueDistDeg(c, 35.0f)) < bestD)) continue;
                    bestD = d;
                    fireCenter = c;
                }
                if (fireCenter < 0.0f) {
                    fireCenter = 35.0f;
                }
            }
            if (goldFamilyPresent) {
                bestD = 9999.0f;
                for (float c : dominantCenters) {
                    if (!DarkenedArmorTextures.hueInRangeDeg(c, 35.0f, 72.0f) || !((d = DarkenedArmorTextures.hueDistDeg(c, 55.0f)) < bestD)) continue;
                    bestD = d;
                    goldCenter = c;
                }
                if (goldCenter < 0.0f) {
                    goldCenter = 55.0f;
                }
            }
        } else {
            if (fireFamilyPresent) {
                fireCenter = 35.0f;
            }
            if (goldFamilyPresent) {
                goldCenter = 55.0f;
            }
        }
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                int abgr = img.m_84985_(x, y);
                int a = abgr >>> 24 & 0xFF;
                if (a == 0) continue;
                int b = abgr >>> 16 & 0xFF;
                int g = abgr >>> 8 & 0xFF;
                int r = abgr & 0xFF;
                float rf = (float)r / 255.0f;
                float gf = (float)g / 255.0f;
                float bf = (float)b / 255.0f;
                float luma = 0.2126f * rf + 0.7152f * gf + 0.0722f * bf;
                float curve = (float)Math.pow(luma, 1.4f);
                float darkBase = 0.04f;
                float darkMax = 0.15f;
                float target = darkBase + curve * (darkMax - darkBase);
                float desat = 0.85f;
                float gray = luma;
                rf += (gray - rf) * desat;
                gf += (gray - gf) * desat;
                bf += (gray - bf) * desat;
                float tintR = 0.02f;
                float tintG = 0.06f;
                float tintB = 0.09f;
                float l2 = 0.2126f * (rf *= 0.15f) + 0.7152f * gf + 0.0722f * bf;
                if (l2 > 1.0E-5f) {
                    float scale = target / l2;
                    rf *= scale;
                    gf *= scale;
                    bf *= scale;
                }
                rf = DarkenedArmorTextures.clamp01(rf + tintR * target);
                gf = DarkenedArmorTextures.clamp01(gf + tintG * target);
                bf = DarkenedArmorTextures.clamp01(bf + tintB * target);
                int nr = DarkenedArmorTextures.clamp255((int)(rf * 255.0f));
                int ng = DarkenedArmorTextures.clamp255((int)(gf * 255.0f));
                int nb = DarkenedArmorTextures.clamp255((int)(bf * 255.0f));
                int nabgr = a << 24 | nb << 16 | ng << 8 | nr;
                img.m_84988_(x, y, nabgr);
            }
        }
        ResourceLocation id = new ResourceLocation(base.m_135827_(), "generated/darken/" + Integer.toHexString(base.toString().hashCode()));
        DynamicTexture dyn = new DynamicTexture(img);
        tm.m_118495_(id, (AbstractTexture)dyn);
        return id;
    }

    private static float hueDistDeg(float a, float b) {
        float d = Math.abs(a - b) % 360.0f;
        return d > 180.0f ? 360.0f - d : d;
    }

    private static boolean hueInRangeDeg(float h, float min, float max) {
        if (min <= max) {
            return h >= min && h <= max;
        }
        return h >= min && h <= 360.0f || h >= 0.0f && h <= max;
    }

    private static float[] pickDominantHueCentersDeg(float[] hist, float totalW, float binDeg, float minFrac, int maxCenters, float mergeDeg) {
        int i;
        if (hist == null || hist.length == 0) {
            return null;
        }
        if (totalW <= 1.0E-5f) {
            return null;
        }
        float[] candidates = new float[hist.length];
        int cCount = 0;
        for (i = 0; i < hist.length; ++i) {
            float frac = hist[i] / totalW;
            if (!(frac >= minFrac)) continue;
            candidates[cCount++] = ((float)i + 0.5f) * binDeg;
        }
        if (cCount == 0) {
            return null;
        }
        for (i = 0; i < cCount - 1; ++i) {
            int best = i;
            float bestW = -1.0f;
            for (int j = i; j < cCount; ++j) {
                float w;
                int bin = (int)(candidates[j] / binDeg);
                if (bin < 0) {
                    bin = 0;
                }
                if (bin >= hist.length) {
                    bin = hist.length - 1;
                }
                if (!((w = hist[bin]) > bestW)) continue;
                bestW = w;
                best = j;
            }
            float tmp = candidates[i];
            candidates[i] = candidates[best];
            candidates[best] = tmp;
        }
        float[] centers = new float[maxCenters];
        int out = 0;
        for (int i2 = 0; i2 < cCount && out < maxCenters; ++i2) {
            float c = candidates[i2];
            boolean merged = false;
            for (int k = 0; k < out; ++k) {
                if (!(DarkenedArmorTextures.hueDistDeg(centers[k], c) <= mergeDeg)) continue;
                merged = true;
                break;
            }
            if (merged) continue;
            centers[out++] = c;
        }
        if (out == 0) {
            return null;
        }
        float[] trimmed = new float[out];
        for (int i3 = 0; i3 < out; ++i3) {
            trimmed[i3] = centers[i3];
        }
        return trimmed;
    }

    private static float smoothstep(float edge0, float edge1, float x) {
        float t = DarkenedArmorTextures.clamp01((x - edge0) / (edge1 - edge0));
        return t * t * (3.0f - 2.0f * t);
    }

    private static float clamp01(float v) {
        if (v < 0.0f) {
            return 0.0f;
        }
        if (v > 1.0f) {
            return 1.0f;
        }
        return v;
    }

    private static float[] rgbToHsv(float r, float g, float b) {
        float min;
        float max = Math.max(r, Math.max(g, b));
        float d = max - (min = Math.min(r, Math.min(g, b)));
        float h = d == 0.0f ? 0.0f : (max == r ? 60.0f * ((g - b) / d % 6.0f) : (max == g ? 60.0f * ((b - r) / d + 2.0f) : 60.0f * ((r - g) / d + 4.0f)));
        if (h < 0.0f) {
            h += 360.0f;
        }
        float s = max == 0.0f ? 0.0f : d / max;
        float v = max;
        return new float[]{h, s, v};
    }

    private static int clamp255(int v) {
        if (v < 0) {
            return 0;
        }
        if (v > 255) {
            return 255;
        }
        return v;
    }
}

