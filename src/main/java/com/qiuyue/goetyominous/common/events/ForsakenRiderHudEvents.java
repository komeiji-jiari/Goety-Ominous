package com.qiuyue.goetyominous.common.events;

import com.qiuyue.goetyominous.common.entities.ally.ac.ForsakenServant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForsakenRiderHudEvents {

    private static final ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation("textures/gui/icons.png");

    private static ForsakenServant riddenForsaken() {
        Player player = Minecraft.getInstance().player;
        if (player != null && player.getVehicle() instanceof ForsakenServant forsaken
                && forsaken.getControllingPassenger() == player) {
            return forsaken;
        }
        return null;
    }

    @SubscribeEvent
    public static void onPreRenderGuiOverlay(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay().id().equals(VanillaGuiOverlay.EXPERIENCE_BAR.id()) && riddenForsaken() != null) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPostRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        if (!event.getOverlay().id().equals(VanillaGuiOverlay.JUMP_BAR.id())) {
            return;
        }
        ForsakenServant forsaken = riddenForsaken();
        if (forsaken == null || Minecraft.getInstance().options.hideGui) {
            return;
        }
        int width = event.getWindow().getGuiScaledWidth();
        int height = event.getWindow().getGuiScaledHeight();
        int x = width / 2 - 91;
        int y = height - 32 + 3;
        int fill = (int) (forsaken.getRiderChargeMeter() * 183.0F);
        Gui gui = Minecraft.getInstance().gui;
        if (gui instanceof ForgeGui forgeGui) {
            forgeGui.setupOverlayRenderState(true, false);
        }
        GuiGraphics guiGraphics = event.getGuiGraphics();
        guiGraphics.pose().pushPose();
        guiGraphics.blit(GUI_ICONS_LOCATION, x, y, 0, 84, 182, 5);
        if (fill > 0) {
            guiGraphics.blit(GUI_ICONS_LOCATION, x, y, 0, 89, fill, 5);
        }
        guiGraphics.pose().popPose();
    }
}
