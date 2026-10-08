package com.qiuyue.goetyominous.client.rlyeh.ac;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.rlyeh.ac.RlyehStyled;
import com.qiuyue.goetyominous.compat.mod.ModernUiTooltipCompat;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, value = Dist.CLIENT)
public final class RlyehTooltipHandler {

    private static final float Z = 400.0F;

    private static final int FRAME_DARK = 0xFF0A0730;

    private static final int[] FRAME_PALETTE = {
            0x2E6E9E, 0x4F9AD6, 0xB8DFF6, 0x0A1E3C, 0x143A5E, 0x2E6E9E
    };

    private static final long INTRO_MILLIS = 520L;

    private static final long INTRO_GAP_MILLIS = 130L;

    private static long introStartMillis = 0L;
    private static long lastEventMillis = 0L;
    private static Object lastItem = null;
    private static float currentIntro = 1.0F;

    private RlyehTooltipHandler() {
    }

    private static float updateIntro(Object item, long now) {
        if (item != lastItem || now - lastEventMillis > INTRO_GAP_MILLIS) {
            introStartMillis = now;
        }
        lastItem = item;
        lastEventMillis = now;
        float p = (now - introStartMillis) / (float) INTRO_MILLIS;
        currentIntro = p < 0.0F ? 0.0F : (p > 1.0F ? 1.0F : p);
        return currentIntro;
    }

    public static float introProgress() {
        return currentIntro;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onTooltipPreSuppress(RenderTooltipEvent.Pre event) {
        if (!ModernUiTooltipCompat.active()) {
            return;
        }
        if (event.getItemStack().getItem() instanceof RlyehStyled) {
            ModernUiTooltipCompat.suppress();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onTooltipPreRestore(RenderTooltipEvent.Pre event) {
        ModernUiTooltipCompat.restore();
    }

    @SubscribeEvent
    public static void onTooltipColor(RenderTooltipEvent.Color event) {
        if (!(event.getItemStack().getItem() instanceof RlyehStyled)) {
            return;
        }
        ShaderInstance shader = RlyehShaders.tooltip();
        if (shader == null) {
            return;
        }
        GuiGraphics gt = event.getGraphics();
        int x = event.getX();
        int y = event.getY();
        Font font = event.getFont();
        int w = 0;
        int h = 0;
        for (ClientTooltipComponent component : event.getComponents()) {
            w = Math.max(w, component.getWidth(font));
            h += component.getHeight();
        }
        if (event.getComponents().size() == 1) {
            h -= 2;
        }
        int left = x - 3;
        int top = y - 3;
        int right = x + w + 3;
        int bottom = y + h + 3;
        float time = (System.currentTimeMillis() % 100000000L) / 1000.0F;
        float intro = updateIntro(event.getItemStack().getItem(), System.currentTimeMillis());

        event.setBackgroundStart(0);
        event.setBackgroundEnd(0);
        event.setBorderStart(0);
        event.setBorderEnd(0);

        Matrix4f pose = gt.pose().last().pose();

        gt.enableScissor(left, top, right, bottom);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShader(RlyehShaders::tooltip);
        shader.safeGetUniform("uTime").set(time);
        float panelW = Math.max(1.0F, (float) (right - left));
        float panelH = Math.max(1.0F, (float) (bottom - top));
        float panelShort = Math.min(panelW, panelH);
        shader.safeGetUniform("uAspect").set(panelW / panelShort, panelH / panelShort);
        shader.safeGetUniform("uPanelSize").set(panelW, panelH);
        shader.safeGetUniform("uTheme").set(RlyehShaders.RLYEH_THEME);
        shader.safeGetUniform("uIntro").set(intro);
        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        builder.vertex(pose, left, bottom, Z).color(1.0F, 1.0F, 1.0F, 1.0F).uv(0.0F, 1.0F).endVertex();
        builder.vertex(pose, right, bottom, Z).color(1.0F, 1.0F, 1.0F, 1.0F).uv(1.0F, 1.0F).endVertex();
        builder.vertex(pose, right, top, Z).color(1.0F, 1.0F, 1.0F, 1.0F).uv(1.0F, 0.0F).endVertex();
        builder.vertex(pose, left, top, Z).color(1.0F, 1.0F, 1.0F, 1.0F).uv(0.0F, 0.0F).endVertex();
        BufferUploader.drawWithShader(builder.end());
        RenderSystem.enableCull();
        gt.disableScissor();

        drawFrame(pose, left, top, right, bottom, time);
    }

    private static void drawFrame(Matrix4f pose, int left, int top, int right, int bottom, float time) {
        ShaderInstance frame = RlyehShaders.frame();
        if (frame == null) {
            drawFlatFrame(pose, left, top, right, bottom, time);
            return;
        }
        int fl = left - 2;
        int ft = top - 2;
        int fr = right + 2;
        int fb = bottom + 2;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShader(RlyehShaders::frame);
        frame.safeGetUniform("uTime").set(time);
        frame.safeGetUniform("uTheme").set(RlyehShaders.RLYEH_THEME);
        frame.safeGetUniform("uSize").set((float) (fr - fl), (float) (fb - ft));
        frame.safeGetUniform("uBorder").set(2.5F);
        frame.safeGetUniform("uRadius").set(2.0F);
        frame.safeGetUniform("uIntro").set(currentIntro);
        BufferBuilder b = Tesselator.getInstance().getBuilder();
        b.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        b.vertex(pose, fl, fb, Z).color(1.0F, 1.0F, 1.0F, 1.0F).uv(0.0F, 1.0F).endVertex();
        b.vertex(pose, fr, fb, Z).color(1.0F, 1.0F, 1.0F, 1.0F).uv(1.0F, 1.0F).endVertex();
        b.vertex(pose, fr, ft, Z).color(1.0F, 1.0F, 1.0F, 1.0F).uv(1.0F, 0.0F).endVertex();
        b.vertex(pose, fl, ft, Z).color(1.0F, 1.0F, 1.0F, 1.0F).uv(0.0F, 0.0F).endVertex();
        BufferUploader.drawWithShader(b.end());
        RenderSystem.enableCull();
    }

    private static void drawFlatFrame(Matrix4f pose, int left, int top, int right, int bottom, float time) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder b = Tesselator.getInstance().getBuilder();
        b.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        quad(b, pose, left, top, right, top + 1, FRAME_DARK, FRAME_DARK, true);
        quad(b, pose, left, bottom - 1, right, bottom, FRAME_DARK, FRAME_DARK, true);
        quad(b, pose, left, top, left + 1, bottom, FRAME_DARK, FRAME_DARK, false);
        quad(b, pose, right - 1, top, right, bottom, FRAME_DARK, FRAME_DARK, false);

        int iL = left + 1, iT = top + 1, iR = right - 1, iB = bottom - 1;
        float wf = Math.max(1, iR - iL);
        float hf = Math.max(1, iB - iT);
        float per = 2.0F * (wf + hf);
        float ph = time * 0.12F;
        int cTL = sample(FRAME_PALETTE, 0.0F / per + ph);
        int cTR = sample(FRAME_PALETTE, wf / per + ph);
        int cBR = sample(FRAME_PALETTE, (wf + hf) / per + ph);
        int cBL = sample(FRAME_PALETTE, (2.0F * wf + hf) / per + ph);
        quad(b, pose, iL, iT, iR, iT + 2, cTL, cTR, true);        // 上：左→右
        quad(b, pose, iR - 2, iT, iR, iB, cTR, cBR, false);       // 右：上→下
        quad(b, pose, iL, iB - 2, iR, iB, cBL, cBR, true);        // 下：左→右
        quad(b, pose, iL, iT, iL + 2, iB, cTL, cBL, false);       // 左：上→下
        BufferUploader.drawWithShader(b.end());
        RenderSystem.enableCull();
    }

    private static void quad(BufferBuilder b, Matrix4f pose, int x0, int y0, int x1, int y1,
                             int colourA, int colourB, boolean horizontal) {
        int c00 = colourA;                        // (x0,y0)
        int c10 = horizontal ? colourB : colourA; // (x1,y0)
        int c11 = colourB;                        // (x1,y1)
        int c01 = horizontal ? colourA : colourB; // (x0,y1)
        vtx(b, pose, x0, y0, c00);
        vtx(b, pose, x1, y0, c10);
        vtx(b, pose, x1, y1, c11);
        vtx(b, pose, x0, y1, c01);
    }

    private static void vtx(BufferBuilder b, Matrix4f pose, float px, float py, int rgb) {
        float r = ((rgb >> 16) & 0xFF) / 255.0F;
        float g = ((rgb >> 8) & 0xFF) / 255.0F;
        float bl = (rgb & 0xFF) / 255.0F;
        b.vertex(pose, px, py, Z).color(r, g, bl, 1.0F).endVertex();
    }

    @SubscribeEvent
    public static void onItemTooltip(net.minecraftforge.event.entity.player.ItemTooltipEvent event) {
        if (!(event.getItemStack().getItem() instanceof com.qiuyue.goetyominous.common.rlyeh.ac.RlyehStyled)) {
            return;
        }
        List<Component> tooltip = event.getToolTip();
        if (!tooltip.isEmpty() && !tooltip.get(0).getString().isBlank()) {
            tooltip.set(0, RlyehFont.abyss(tooltip.get(0).getString()));
        }
    }

    private static int sample(int[] palette, float t) {
        t -= (float) Math.floor(t);
        float scaled = t * (palette.length - 1);
        int idx = (int) scaled;
        float f = scaled - idx;
        int a = palette[idx];
        int c = palette[Math.min(idx + 1, palette.length - 1)];
        int r = Math.round(((a >> 16) & 0xFF) + (((c >> 16) & 0xFF) - ((a >> 16) & 0xFF)) * f);
        int g = Math.round(((a >> 8) & 0xFF) + (((c >> 8) & 0xFF) - ((a >> 8) & 0xFF)) * f);
        int bl = Math.round((a & 0xFF) + ((c & 0xFF) - (a & 0xFF)) * f);
        return (r << 16) | (g << 8) | bl;
    }
}
