package com.qiuyue.goetyominous.client.gui.screen.inventory;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.inventory.container.NetherEternalCauldronContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class NetherEternalCauldronScreen extends AbstractContainerScreen<NetherEternalCauldronContainer> {
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID,
            "textures/gui/bottle.png");

    public NetherEternalCauldronScreen(NetherEternalCauldronContainer container, Inventory inventory,
                                       Component title) {
        super(container, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(GUI_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }
}
