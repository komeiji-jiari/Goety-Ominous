package com.qiuyue.goetyominous.client.render;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.events.GrimMonarchClientEvents;
import net.minecraft.Util;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, value = Dist.CLIENT)
public class DarkSceneRenderer {

    private static final float[] VISIBILITY = {0.100F, 0.200F, 0.120F, 0.080F, 0.040F, 0.020F, 0.000F};
    private static final float MIN_FOG_END = 0.35F;
    private static final float FOG_START_RATIO = 0.30F;
    private static final float MIN_FOG_START = 0.02F;

    private static final float CLOSE_SPEED = 110.0F;
    private static final long MIN_ONSET_MILLIS = 700L;
    private static final long MAX_ONSET_MILLIS = 3200L;

    private static final float FOG_RED = 0.043F;
    private static final float FOG_GREEN = 0.039F;
    private static final float FOG_BLUE = 0.063F;

    private static final Vec3 SKY_COLOR = new Vec3(0.055D, 0.053D, 0.072D);

    private static final float MOON_TINT_RED = 0.38F;
    private static final float MOON_TINT_GREEN = 1.06F;
    private static final float MOON_TINT_BLUE = 1.18F;

    public static final float MOON_SCALE = 2.5F;

    private static boolean active;
    private static int level = 1;
    private static long onsetAt;
    private static long onsetLength;
    private static float onsetStart;
    private static boolean releasing;
    private static long releaseAt;
    private static long releaseLength;
    private static float releaseStart;
    private static long darkenAt;
    private static long darkenLength;
    private static boolean fogSuppressed;
    private static float fogNear = 1.0F;
    private static float fogFar = 8.0F;
    @Nullable private static CloudStatus savedClouds;

    public static boolean active() {
        return active;
    }

    public static int level() {
        return level;
    }

    public static int levelPercent() {
        return Math.round(VISIBILITY[level] * 100.0F);
    }

    public static Vec3 skyColor() {
        return SKY_COLOR;
    }

    public static boolean moonTintActive() {
        return GrimMonarchClientEvents.nightLocked();
    }

    public static void applyMoonTint() {
        if (!moonTintActive()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        float alpha = 1.0F;
        if (minecraft.level != null) {
            alpha = 1.0F - minecraft.level.getRainLevel(minecraft.getFrameTime());
        }
        RenderSystem.setShaderColor(MOON_TINT_RED, MOON_TINT_GREEN, MOON_TINT_BLUE, alpha);
    }

    public static void toggle() {
        active = !active;
        if (active) {
            Minecraft minecraft = Minecraft.getInstance();
            Entity entity = minecraft.getCameraEntity();
            onsetStart = renderDistance(minecraft) + 32.0F;
            startOnset(onsetStart, minecraft);
        } else {
            onsetAt = 0L;
            onsetLength = 0L;
        }
        applyClouds();
    }

    public static void cycleLevel() {
        level = (level + 1) % VISIBILITY.length;
        if (active) {
            startOnset(Math.max(fogFar, MIN_FOG_END), Minecraft.getInstance());
        }
    }

    public static int blackLevel() {
        return VISIBILITY.length - 1;
    }

    public static void darken(int newLevel) {
        Minecraft minecraft = Minecraft.getInstance();
        newLevel = Mth.clamp(newLevel, 0, VISIBILITY.length - 1);
        if (active && !releasing && newLevel == level) {
            return;
        }
        boolean activating = !active;
        level = newLevel;
        releasing = false;
        if (activating) {
            active = true;
            onsetStart = renderDistance(minecraft) + 32.0F;
            applyClouds();
        }
        startOnset(activating ? onsetStart : Math.max(fogFar, MIN_FOG_END), minecraft);
        if (activating) {
            darkenAt = onsetAt;
            darkenLength = onsetLength;
        }
    }

    public static void release() {
        if (!active || releasing) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options == null) {
            finishRelease();
            return;
        }
        releasing = true;
        releaseStart = fogFar;
        float targetFar = Math.max(MIN_FOG_END, renderDistance(minecraft) + 32.0F);
        long millis = (long) (Math.abs(targetFar - releaseStart) / CLOSE_SPEED * 1000.0F);
        releaseLength = Math.max(MIN_ONSET_MILLIS, Math.min(MAX_ONSET_MILLIS, millis));
        releaseAt = Util.getMillis();
    }

    public static float darkness() {
        if (!active) {
            return 0.0F;
        }
        if (releasing) {
            return 1.0F - releaseProgress();
        }
        if (darkenAt == 0L || darkenLength <= 0L) {
            return 1.0F;
        }
        float t = (Util.getMillis() - darkenAt) / (float) darkenLength;
        if (t >= 1.0F) {
            darkenAt = 0L;
            return 1.0F;
        }
        return Math.max(0.0F, t);
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        active = false;
        onsetAt = 0L;
        onsetLength = 0L;
        releasing = false;
        releaseAt = 0L;
        releaseLength = 0L;
        applyClouds();
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        if (!active || event.getMode() != FogRenderer.FogMode.FOG_TERRAIN) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }
        update(minecraft);
        event.setNearPlaneDistance(fogNear);
        event.setFarPlaneDistance(fogFar);
        event.setFogShape(FogShape.SPHERE);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        if (!active) {
            return;
        }
        if (Minecraft.getInstance().level == null) {
            return;
        }
        event.setRed(FOG_RED);
        event.setGreen(FOG_GREEN);
        event.setBlue(FOG_BLUE);
    }

    @SubscribeEvent
    public static void onRenderStage(RenderLevelStageEvent event) {
        RenderLevelStageEvent.Stage stage = event.getStage();
        if (stage == RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS) {
            if (!active) {
                return;
            }
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.level == null) {
                return;
            }
            update(minecraft);
            RenderSystem.setShaderFogStart(Float.MAX_VALUE);
            fogSuppressed = true;
        } else if (stage == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            if (!fogSuppressed) {
                return;
            }
            fogSuppressed = false;
            RenderSystem.setShaderFogStart(fogNear);
            RenderSystem.setShaderFogEnd(fogFar);
            RenderSystem.setShaderFogShape(FogShape.SPHERE);
        }
    }

    private static void update(Minecraft minecraft) {
        float renderDistance = renderDistance(minecraft);
        float targetFar = releasing
                ? Math.max(MIN_FOG_END, renderDistance + 32.0F)
                : Math.max(MIN_FOG_END, renderDistance * VISIBILITY[level]);
        float from = releasing ? releaseStart : onsetStart;
        float progress = releasing ? releaseProgress() : onsetProgress();
        fogFar = Mth.lerp(progress, from, targetFar);
        fogNear = Math.max(MIN_FOG_START, fogFar * FOG_START_RATIO);
    }

    private static float onsetProgress() {
        if (onsetAt == 0L || onsetLength <= 0L) {
            return 1.0F;
        }
        float t = (Util.getMillis() - onsetAt) / (float) onsetLength;
        if (t >= 1.0F) {
            onsetAt = 0L;
            return 1.0F;
        }
        return Math.max(0.0F, t);
    }

    private static float releaseProgress() {
        if (releaseAt == 0L || releaseLength <= 0L) {
            finishRelease();
            return 1.0F;
        }
        float t = (Util.getMillis() - releaseAt) / (float) releaseLength;
        if (t >= 1.0F) {
            finishRelease();
            return 1.0F;
        }
        return Math.max(0.0F, t);
    }

    private static void finishRelease() {
        releasing = false;
        releaseAt = 0L;
        releaseLength = 0L;
        active = false;
        applyClouds();
    }

    private static void startOnset(float fromFar, Minecraft minecraft) {
        onsetStart = fromFar;
        float targetFar = Math.max(MIN_FOG_END, renderDistance(minecraft) * VISIBILITY[level]);
        long millis = (long) (Math.abs(fromFar - targetFar) / CLOSE_SPEED * 1000.0F);
        onsetLength = Math.max(MIN_ONSET_MILLIS, Math.min(MAX_ONSET_MILLIS, millis));
        onsetAt = Util.getMillis();
    }

    private static float renderDistance(Minecraft minecraft) {
        if (minecraft.options == null) {
            return 128.0F;
        }
        return minecraft.options.getEffectiveRenderDistance() * 16.0F;
    }

    private static void applyClouds() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options == null) {
            return;
        }
        if (active) {
            if (savedClouds == null) {
                savedClouds = minecraft.options.getCloudsType();
            }
            minecraft.options.cloudStatus().set(CloudStatus.OFF);
        } else if (savedClouds != null) {
            minecraft.options.cloudStatus().set(savedClouds);
            savedClouds = null;
        }
    }
}
