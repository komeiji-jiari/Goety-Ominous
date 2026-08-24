/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.config.MainConfig
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraftforge.client.event.RenderGuiEvent$Post
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 */
package com.vivideru.masteryofmagic.client.midas;

import com.Polarice3.Goety.config.MainConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class MidasNarrationOverlay {
    private static final int DISPLAY_TICKS = 70;
    private static final List<Notice> notices = new ArrayList<Notice>();

    private MidasNarrationOverlay() {
    }

    public static void show(String translationKey) {
        notices.add(0, new Notice((Component)Component.m_237115_((String)translationKey), 70));
        while (notices.size() > 3) {
            notices.remove(notices.size() - 1);
        }
    }

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !Minecraft.m_91087_().m_91104_()) {
            for (int i = notices.size() - 1; i >= 0; --i) {
                Notice notice = notices.get(i);
                --notice.remainingTicks;
                if (notice.remainingTicks > 0) continue;
                notices.remove(i);
            }
        }
    }

    @SubscribeEvent
    public static void render(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.m_91087_();
        if (notices.isEmpty() || minecraft.f_91066_.f_92062_ || minecraft.f_91074_ == null) {
            return;
        }
        GuiGraphics graphics = event.getGuiGraphics();
        int soulBarX = graphics.m_280182_() / 2 + (Integer)MainConfig.SoulGuiHorizontal.get();
        int rightX = Math.min(graphics.m_280182_() - 8, soulBarX + 128);
        int y = graphics.m_280206_() + (Integer)MainConfig.SoulGuiVertical.get() - 23;
        for (int i = 0; i < notices.size(); ++i) {
            Notice notice = notices.get(i);
            float fade = Math.min(1.0f, (float)notice.remainingTicks / 12.0f);
            int alpha = Math.max(4, (int)(fade * 220.0f));
            int color = alpha << 24 | 0xFFD36A;
            graphics.m_280168_().m_85836_();
            graphics.m_280168_().m_252880_((float)rightX, (float)(y - i * 11), 0.0f);
            graphics.m_280168_().m_85841_(0.78f, 0.78f, 1.0f);
            RenderSystem.enableBlend();
            graphics.m_280614_(minecraft.f_91062_, notice.message, -minecraft.f_91062_.m_92852_((FormattedText)notice.message), 0, color, true);
            RenderSystem.disableBlend();
            graphics.m_280168_().m_85849_();
        }
    }

    private static final class Notice {
        private final Component message;
        private int remainingTicks;

        private Notice(Component message, int remainingTicks) {
            this.message = message;
            this.remainingTicks = remainingTicks;
        }
    }
}

