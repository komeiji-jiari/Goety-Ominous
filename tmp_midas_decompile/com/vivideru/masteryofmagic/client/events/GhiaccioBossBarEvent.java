/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.config.MainConfig
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.CustomizeGuiOverlayEvent$BossEventProgress
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.vivideru.masteryofmagic.client.events;

import com.Polarice3.Goety.config.MainConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.vivideru.masteryofmagic.entity.GhiaccioEntity;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class GhiaccioBossBarEvent {
    private static final ResourceLocation GHIACCIO_FRAME = new ResourceLocation("goety_mastery_of_magic", "textures/gui/ghiaccio_bossbar.png");
    private static final ResourceLocation BOSS_HURT = new ResourceLocation("goety", "textures/gui/boss_bar_hurt.png");
    private static final ResourceLocation BOSS_BAR_1 = new ResourceLocation("goety", "textures/gui/boss_bar_1.png");

    @SubscribeEvent
    public static void renderBossBar(CustomizeGuiOverlayEvent.BossEventProgress event) {
        Minecraft minecraft = Minecraft.m_91087_();
        if (!((Boolean)MainConfig.SpecialBossBar.get()).booleanValue()) {
            return;
        }
        String bossName = event.getBossEvent().m_18861_().getString();
        if (!bossName.contains("Ghiaccio")) {
            return;
        }
        event.setCanceled(true);
        int screenWidth = minecraft.m_91268_().m_85445_();
        int x = screenWidth / 2 - 100;
        int y = event.getY();
        RenderSystem.setShaderColor((float)0.75f, (float)0.65f, (float)1.0f, (float)1.0f);
        GhiaccioBossBarEvent.drawGhiaccioBar(event.getGuiGraphics(), x, y, event.getPartialTick(), event.getBossEvent().m_142717_(), event);
        int nameWidth = minecraft.f_91062_.m_92852_((FormattedText)event.getBossEvent().m_18861_());
        int nameX = screenWidth / 2 - nameWidth / 2;
        event.getGuiGraphics().m_280430_(minecraft.f_91062_, event.getBossEvent().m_18861_(), nameX, y - 9, 0xFFFFFF);
        if (y < minecraft.m_91268_().m_85446_() / 3) {
            Objects.requireNonNull(minecraft.f_91062_);
            event.setIncrement(12 + 9);
        }
    }

    private static void drawGhiaccioBar(GuiGraphics guiGraphics, int x, int y, float partialTicks, float percent, CustomizeGuiOverlayEvent.BossEventProgress event) {
        Minecraft minecraft = Minecraft.m_91087_();
        percent = Math.max(0.0f, Math.min(1.0f, percent));
        int width = (int)(percent * 182.0f);
        int barX = x + 9;
        int barY = y + 4;
        int offset = (int)(((float)minecraft.f_91065_.m_93079_() + partialTicks) % 364.0f);
        if (percent <= 0.25f) {
            offset = (int)(((float)minecraft.f_91065_.m_93079_() + partialTicks) * 4.0f % 364.0f);
        } else if (percent <= 0.5f) {
            offset = (int)(((float)minecraft.f_91065_.m_93079_() + partialTicks) * 2.0f % 364.0f);
        }
        if (width > 0) {
            guiGraphics.m_280163_(BOSS_BAR_1, barX, barY, (float)offset, 0.0f, width, 8, 364, 64);
            GhiaccioEntity ghiaccio = null;
            if (minecraft.f_91073_ != null) {
                String bossName = event.getBossEvent().m_18861_().getString();
                for (Entity entity : minecraft.f_91073_.m_104735_()) {
                    GhiaccioEntity ghiaccioEntity;
                    if (!(entity instanceof GhiaccioEntity) || !(ghiaccioEntity = (GhiaccioEntity)entity).m_5446_().getString().equals(bossName)) continue;
                    ghiaccio = ghiaccioEntity;
                    break;
                }
            }
            if (ghiaccio != null && ghiaccio.isSmited() && ghiaccio.getAntiRegenTotal() > 0) {
                float smite = 1.0f - (float)ghiaccio.getAntiRegen() / (float)ghiaccio.getAntiRegenTotal();
                guiGraphics.m_280163_(BOSS_BAR_1, barX, barY, (float)offset, 16.0f, width, 8, 364, 64);
                guiGraphics.m_280163_(BOSS_BAR_1, barX, barY, (float)offset, 0.0f, (int)(smite * (float)width), 8, 364, 64);
            }
        }
        guiGraphics.m_280163_(GHIACCIO_FRAME, x, y, 0.0f, 0.0f, 200, 16, 200, 16);
    }
}

