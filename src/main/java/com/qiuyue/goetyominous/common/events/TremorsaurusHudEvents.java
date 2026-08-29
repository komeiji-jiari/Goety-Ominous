package com.qiuyue.goetyominous.common.events;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.config.ACClientConfig;
import com.qiuyue.goetyominous.common.entities.ally.ac.TremorsaurusServant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;


public class TremorsaurusHudEvents {

    
    private static final ResourceLocation DINOSAUR_HUD_OVERLAYS =
            new ResourceLocation("alexscaves", "textures/misc/dinosaur_hud_overlays.png");

    
    private static final int TREMOR_U_OFFSET = 63;
    private static final int TREMOR_BAR_HEIGHT = 31;
    private static final int TREMOR_Y_EXTRA = 5;

    
    @SubscribeEvent
    public static void onPreRenderGuiOverlay(RenderGuiOverlayEvent.Pre event) {
        if (!isOverlay(event, VanillaGuiOverlay.EXPERIENCE_BAR)) {
            return;
        }
        Player player = Minecraft.getInstance().player;
        if (player != null && player.getVehicle() instanceof TremorsaurusServant) {
            event.setCanceled(true);
        }
    }

    
    @SubscribeEvent
    public static void onPostRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        if (!isOverlay(event, VanillaGuiOverlay.CROSSHAIR)) {
            return;
        }
        Player player = AlexsCaves.PROXY.getClientSidePlayer();
        if (player == null || !(player.getVehicle() instanceof TremorsaurusServant servant)) {
            return;
        }
        if (!servant.hasRidingMeter()) {
            return;
        }

        int guiScaledWidth = event.getWindow().getGuiScaledWidth();
        int guiScaledHeight = event.getWindow().getGuiScaledHeight();

        int yOffset = 0;
        Gui gui = Minecraft.getInstance().gui;
        if (gui instanceof ForgeGui forgeGui) {
            yOffset = Math.max(forgeGui.leftHeight, forgeGui.rightHeight);
        }
        yOffset = Math.max(53, yOffset);

        ACClientConfig config = AlexsCaves.CLIENT_CONFIG;
        int x = guiScaledWidth / 2 - config.subterranodonIndicatorX.get();
        int y = guiScaledHeight - yOffset - config.subterranodonIndicatorY.get() + TREMOR_Y_EXTRA;

        float empty = 1.0F - servant.getMeterAmount();

        GuiGraphics guiGraphics = event.getGuiGraphics();
        guiGraphics.pose().pushPose();
        
        guiGraphics.blit(DINOSAUR_HUD_OVERLAYS, x, y, 50, 0.0F,
                TREMOR_U_OFFSET + TREMOR_BAR_HEIGHT, 43, TREMOR_BAR_HEIGHT, 128, 512);
        
        guiGraphics.blit(DINOSAUR_HUD_OVERLAYS, x, y, 50, 0.0F, TREMOR_U_OFFSET,
                43, (int) Math.floor(TREMOR_BAR_HEIGHT * empty), 128, 512);
        guiGraphics.pose().popPose();
    }

    private static boolean isOverlay(RenderGuiOverlayEvent event, VanillaGuiOverlay overlay) {
        return event.getOverlay().id().equals(overlay.id());
    }
}
