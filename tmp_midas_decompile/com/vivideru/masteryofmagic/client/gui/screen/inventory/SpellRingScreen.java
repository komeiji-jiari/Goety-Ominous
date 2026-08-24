/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.inventory.AbstractContainerMenu
 */
package com.vivideru.masteryofmagic.client.gui.screen.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.vivideru.masteryofmagic.client.inventory.container.SpellRingContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class SpellRingScreen
extends AbstractContainerScreen<SpellRingContainer> {
    private static final ResourceLocation GUI_TEXTURES = new ResourceLocation("goety_mastery_of_magic", "textures/screens/wand_ring.png");

    public SpellRingScreen(SpellRingContainer menu, Inventory inventory, Component title) {
        super((AbstractContainerMenu)menu, inventory, title);
        this.f_97726_ = 176;
        this.f_97727_ = 166;
    }

    protected void m_7856_() {
        super.m_7856_();
        this.f_97728_ = (this.f_97726_ - this.f_96547_.m_92852_((FormattedText)this.f_96539_)) / 2;
        this.f_97729_ = 6;
        this.f_97730_ = 8;
        this.f_97731_ = 72;
    }

    public void m_88315_(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.m_280273_(graphics);
        super.m_88315_(graphics, mouseX, mouseY, partialTicks);
        this.m_280072_(graphics, mouseX, mouseY);
    }

    protected void m_7286_(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        int x = (this.f_96543_ - this.f_97726_) / 2;
        int y = (this.f_96544_ - this.f_97727_) / 2;
        graphics.m_280218_(GUI_TEXTURES, x, y, 0, 0, this.f_97726_, this.f_97727_);
    }
}

