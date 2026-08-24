/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.inventory.AbstractContainerMenu
 */
package com.vivideru.masteryofmagic.client.gui.screen.inventory;

import com.vivideru.masteryofmagic.client.inventory.container.MasterStaffContainer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class MasterStaffScreen
extends AbstractContainerScreen<MasterStaffContainer> {
    private final List<Button> slotButtons = new ArrayList<Button>();
    private Button skinButton;

    public MasterStaffScreen(MasterStaffContainer menu, Inventory inventory, Component title) {
        super((AbstractContainerMenu)menu, inventory, title);
        this.f_97726_ = 194;
        this.f_97727_ = 176;
    }

    protected void m_7856_() {
        super.m_7856_();
        this.f_97728_ = (this.f_97726_ - this.f_96547_.m_92852_((FormattedText)this.f_96539_)) / 2;
        this.f_97729_ = 7;
        this.f_97730_ = 16;
        this.f_97731_ = 82;
        this.slotButtons.clear();
        for (int slot = 0; slot < 7; ++slot) {
            int selectedSlot = slot;
            Button button = Button.m_253074_((Component)Component.m_237113_((String)Integer.toString(slot + 1)), ignored -> this.f_96541_.f_91072_.m_105208_(((MasterStaffContainer)this.f_97732_).f_38840_, selectedSlot)).m_252987_(this.f_97735_ + 34 + slot * 18, this.f_97736_ + 24, 16, 16).m_253136_();
            this.slotButtons.add((Button)this.m_142416_((GuiEventListener)button));
        }
        this.skinButton = (Button)this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237119_(), ignored -> this.f_96541_.f_91072_.m_105208_(((MasterStaffContainer)this.f_97732_).f_38840_, 100)).m_252987_(this.f_97735_ + 47, this.f_97736_ + 66, 100, 18).m_253136_());
        this.updateButtonLabels();
    }

    protected void m_181908_() {
        super.m_181908_();
        this.updateButtonLabels();
    }

    private void updateButtonLabels() {
        int activeSlot = ((MasterStaffContainer)this.f_97732_).getActiveSlot();
        for (int slot = 0; slot < this.slotButtons.size(); ++slot) {
            ChatFormatting color = slot == activeSlot ? ChatFormatting.GOLD : ChatFormatting.WHITE;
            this.slotButtons.get(slot).m_93666_((Component)Component.m_237113_((String)Integer.toString(slot + 1)).m_130940_(color));
        }
        if (this.skinButton != null) {
            this.skinButton.m_93666_((Component)Component.m_237110_((String)"gui.goety_mastery_of_magic.master_staff.skin", (Object[])new Object[]{((MasterStaffContainer)this.f_97732_).getSkin() + 1, 8}));
        }
    }

    public void m_88315_(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.m_280273_(graphics);
        super.m_88315_(graphics, mouseX, mouseY, partialTick);
        this.m_280072_(graphics, mouseX, mouseY);
    }

    protected void m_7286_(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.f_97735_;
        int y = this.f_97736_;
        graphics.m_280509_(x, y, x + this.f_97726_, y + this.f_97727_, -15265505);
        graphics.m_280509_(x + 2, y + 2, x + this.f_97726_ - 2, y + this.f_97727_ - 2, -14016457);
        graphics.m_280509_(x + 7, y + 88, x + this.f_97726_ - 7, y + this.f_97727_ - 7, -15199457);
        for (int slot = 0; slot < 7; ++slot) {
            int slotX = x + 33 + slot * 18;
            int slotY = y + 42;
            int border = slot == ((MasterStaffContainer)this.f_97732_).getActiveSlot() ? -14001 : -10136968;
            graphics.m_280509_(slotX, slotY, slotX + 18, slotY + 18, border);
            graphics.m_280509_(slotX + 1, slotY + 1, slotX + 17, slotY + 17, -15791084);
        }
        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 9; ++column) {
                MasterStaffScreen.drawInventorySlot(graphics, x + 15 + column * 18, y + 93 + row * 18);
            }
        }
        for (int column = 0; column < 9; ++column) {
            MasterStaffScreen.drawInventorySlot(graphics, x + 15 + column * 18, y + 151);
        }
    }

    private static void drawInventorySlot(GuiGraphics graphics, int x, int y) {
        graphics.m_280509_(x, y, x + 18, y + 18, -11714470);
        graphics.m_280509_(x + 1, y + 1, x + 17, y + 17, -15791084);
    }
}

