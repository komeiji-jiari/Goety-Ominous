/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.color.block.BlockColors
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.util.FastColor$ABGR32
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  net.minecraftforge.client.model.data.ModelData
 */
package com.vivideru.masteryofmagic.goldification.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;

@OnlyIn(value=Dist.CLIENT)
public final class GoldificationMaterialAnalyzer {
    private static final long ANALYSIS_RANDOM_SEED = 42L;
    private static final int MAX_SAMPLES_PER_AXIS = 16;
    private static final double[][] VANILLA_GOLD_PALETTE = new double[][]{{0.8, 0.5569, 0.1529}, {0.8275, 0.5882, 0.1961}, {0.9765, 0.7412, 0.1373}, {0.9608, 0.8, 0.1529}, {1.0, 0.8471, 0.2431}, {0.9961, 0.8784, 0.2824}, {1.0, 0.9255, 0.3098}, {1.0, 0.9922, 0.5647}, {0.9961, 1.0, 0.7412}};
    private static final Map<BakedModel, Map<MaterialKey, Float>> CACHE = new WeakHashMap<BakedModel, Map<MaterialKey, Float>>();

    private GoldificationMaterialAnalyzer() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static float conversionStrength(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos position, ModelData modelData, RenderType renderType) {
        BlockColors blockColors = Minecraft.m_91087_().m_91298_();
        int tint0 = blockColors.m_92577_(state, level, position, 0);
        int tint1 = blockColors.m_92577_(state, level, position, 1);
        int tint2 = blockColors.m_92577_(state, level, position, 2);
        MaterialKey key = new MaterialKey(state, renderType, tint0, tint1, tint2);
        Map<BakedModel, Map<MaterialKey, Float>> map = CACHE;
        synchronized (map) {
            Map modelCache = CACHE.computeIfAbsent(model, ignored -> new HashMap());
            Float cached = (Float)modelCache.get(key);
            if (cached != null) {
                return cached.floatValue();
            }
            float calculated = GoldificationMaterialAnalyzer.analyze(level, model, state, position, modelData, renderType, blockColors, tint0, tint1, tint2);
            modelCache.put(key, Float.valueOf(calculated));
            return calculated;
        }
    }

    private static float analyze(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos position, ModelData modelData, RenderType renderType, BlockColors blockColors, int tint0, int tint1, int tint2) {
        AnalysisAccumulator accumulator = new AnalysisAccumulator();
        RandomSource random = RandomSource.m_216335_((long)42L);
        GoldificationMaterialAnalyzer.analyzeQuads(model.getQuads(state, null, random, modelData, renderType), level, state, position, blockColors, tint0, tint1, tint2, accumulator);
        for (Direction direction : Direction.values()) {
            random.m_188584_(42L);
            GoldificationMaterialAnalyzer.analyzeQuads(model.getQuads(state, direction, random, modelData, renderType), level, state, position, blockColors, tint0, tint1, tint2, accumulator);
        }
        if (accumulator.sampleCount == 0) {
            GoldificationMaterialAnalyzer.analyzeSprite(model.getParticleIcon(modelData), 0xFFFFFF, accumulator);
        }
        if (accumulator.sampleCount == 0) {
            return 1.0f;
        }
        double meanConversion = accumulator.conversionSum / (double)accumulator.sampleCount;
        double nonGoldFraction = (double)accumulator.stronglyNonGoldSamples / (double)accumulator.sampleCount;
        double brightHighlightFraction = (double)accumulator.brightHighlightSamples / (double)accumulator.sampleCount;
        double contextualPush = GoldificationMaterialAnalyzer.smoothstep(0.15, 0.65, nonGoldFraction) * 0.22;
        double result = meanConversion + (1.0 - meanConversion) * contextualPush;
        double matteHighlightDeficit = 1.0 - GoldificationMaterialAnalyzer.smoothstep(0.08, 0.35, brightHighlightFraction);
        double mattePush = matteHighlightDeficit * 0.8;
        result += (1.0 - result) * mattePush;
        return (float)GoldificationMaterialAnalyzer.clamp(result, 0.0, 1.0);
    }

    private static void analyzeQuads(List<BakedQuad> quads, BlockAndTintGetter level, BlockState state, BlockPos position, BlockColors blockColors, int tint0, int tint1, int tint2, AnalysisAccumulator accumulator) {
        for (BakedQuad quad : quads) {
            int tint = 0xFFFFFF;
            if (quad.m_111304_()) {
                int tintIndex = quad.m_111305_();
                switch (tintIndex) {
                    case 0: {
                        int n = tint0;
                        break;
                    }
                    case 1: {
                        int n = tint1;
                        break;
                    }
                    case 2: {
                        int n = tint2;
                        break;
                    }
                    default: {
                        int n = tint = blockColors.m_92577_(state, level, position, tintIndex);
                    }
                }
                if (tint == -1) {
                    tint = 0xFFFFFF;
                }
            }
            GoldificationMaterialAnalyzer.analyzeSprite(quad.m_173410_(), tint, accumulator);
        }
    }

    private static void analyzeSprite(TextureAtlasSprite sprite, int tint, AnalysisAccumulator accumulator) {
        if (sprite == null) {
            return;
        }
        int width = sprite.m_245424_().m_246492_();
        int height = sprite.m_245424_().m_245330_();
        if (width <= 0 || height <= 0) {
            return;
        }
        int stepX = Math.max(1, (int)Math.ceil((double)width / 16.0));
        int stepY = Math.max(1, (int)Math.ceil((double)height / 16.0));
        double tintRed = (double)(tint >> 16 & 0xFF) / 255.0;
        double tintGreen = (double)(tint >> 8 & 0xFF) / 255.0;
        double tintBlue = (double)(tint & 0xFF) / 255.0;
        try {
            for (int y = stepY / 2; y < height; y += stepY) {
                for (int x = stepX / 2; x < width; x += stepX) {
                    int pixel = sprite.getPixelRGBA(0, x, y);
                    if (FastColor.ABGR32.m_266503_((int)pixel) < 32) continue;
                    double red = (double)FastColor.ABGR32.m_266313_((int)pixel) / 255.0 * tintRed;
                    double green = (double)FastColor.ABGR32.m_266446_((int)pixel) / 255.0 * tintGreen;
                    double blue = (double)FastColor.ABGR32.m_266247_((int)pixel) / 255.0 * tintBlue;
                    double conversion = GoldificationMaterialAnalyzer.pixelConversion(red, green, blue);
                    double luminance = red * 0.2126 + green * 0.7152 + blue * 0.0722;
                    accumulator.conversionSum += conversion;
                    ++accumulator.sampleCount;
                    if (luminance >= 0.72) {
                        ++accumulator.brightHighlightSamples;
                    }
                    if (!(conversion >= 0.72)) continue;
                    ++accumulator.stronglyNonGoldSamples;
                }
            }
        }
        catch (RuntimeException runtimeException) {
            // empty catch block
        }
    }

    private static double pixelConversion(double red, double green, double blue) {
        double goldDistance = Double.MAX_VALUE;
        for (int i = 0; i < VANILLA_GOLD_PALETTE.length - 1; ++i) {
            goldDistance = Math.min(goldDistance, GoldificationMaterialAnalyzer.distanceToSegment(red, green, blue, VANILLA_GOLD_PALETTE[i], VANILLA_GOLD_PALETTE[i + 1]));
        }
        double highestChannel = Math.max(red, Math.max(green, blue));
        double lowestChannel = Math.min(red, Math.min(green, blue));
        double saturation = highestChannel - lowestChannel;
        double neutralPenalty = (1.0 - GoldificationMaterialAnalyzer.smoothstep(0.08, 0.24, saturation)) * 0.16;
        return GoldificationMaterialAnalyzer.smoothstep(0.035, 0.4, goldDistance + neutralPenalty);
    }

    private static double distanceToSegment(double red, double green, double blue, double[] start, double[] end) {
        double segmentRed = end[0] - start[0];
        double segmentGreen = end[1] - start[1];
        double segmentBlue = end[2] - start[2];
        double lengthSquared = segmentRed * segmentRed + segmentGreen * segmentGreen + segmentBlue * segmentBlue;
        double position = ((red - start[0]) * segmentRed + (green - start[1]) * segmentGreen + (blue - start[2]) * segmentBlue) / Math.max(lengthSquared, 1.0E-6);
        position = GoldificationMaterialAnalyzer.clamp(position, 0.0, 1.0);
        double differenceRed = red - (start[0] + segmentRed * position);
        double differenceGreen = green - (start[1] + segmentGreen * position);
        double differenceBlue = blue - (start[2] + segmentBlue * position);
        return Math.sqrt(differenceRed * differenceRed + differenceGreen * differenceGreen + differenceBlue * differenceBlue);
    }

    private static double smoothstep(double edge0, double edge1, double value) {
        double position = GoldificationMaterialAnalyzer.clamp((value - edge0) / (edge1 - edge0), 0.0, 1.0);
        return position * position * (3.0 - 2.0 * position);
    }

    private static double clamp(double value, double minimum, double maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    private static final class MaterialKey {
        private final BlockState state;
        private final RenderType renderType;
        private final int tint0;
        private final int tint1;
        private final int tint2;

        private MaterialKey(BlockState state, RenderType renderType, int tint0, int tint1, int tint2) {
            this.state = state;
            this.renderType = renderType;
            this.tint0 = tint0;
            this.tint1 = tint1;
            this.tint2 = tint2;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof MaterialKey)) {
                return false;
            }
            MaterialKey other = (MaterialKey)object;
            return this.state == other.state && this.renderType == other.renderType && this.tint0 == other.tint0 && this.tint1 == other.tint1 && this.tint2 == other.tint2;
        }

        public int hashCode() {
            int result = System.identityHashCode(this.state);
            result = 31 * result + System.identityHashCode(this.renderType);
            result = 31 * result + this.tint0;
            result = 31 * result + this.tint1;
            result = 31 * result + this.tint2;
            return result;
        }
    }

    private static final class AnalysisAccumulator {
        private double conversionSum;
        private int sampleCount;
        private int stronglyNonGoldSamples;
        private int brightHighlightSamples;

        private AnalysisAccumulator() {
        }
    }
}

