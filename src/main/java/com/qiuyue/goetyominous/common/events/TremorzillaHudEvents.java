package com.qiuyue.goetyominous.common.events;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.config.ACClientConfig;
import com.qiuyue.goetyominous.common.entities.ally.ac.TremorzillaServant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * 特雷莫兹拉仆从骑乘计量条 HUD。忠实移植 AC ClientEvents 的 Tremorzilla 分支:
 * vOffset=193,高度 29,k22+=5;充能满且尖刺收起时闪烁(隔帧切换到 vOffset=251,满刻度)。
 */
public class TremorzillaHudEvents {

    private static final ResourceLocation DINOSAUR_HUD_OVERLAYS =
            new ResourceLocation("alexscaves", "textures/misc/dinosaur_hud_overlays.png");

    private static final int DINO_U_OFFSET = 0;
    private static final int DINO_V_OFFSET = 193;
    private static final int DINO_V_OFFSET_FLASH = 251;
    private static final int DINO_BAR_HEIGHT = 29;
    private static final int DINO_Y_EXTRA = 5;

    @SubscribeEvent
    public static void onPreRenderGuiOverlay(RenderGuiOverlayEvent.Pre event) {
        if (!isOverlay(event, VanillaGuiOverlay.EXPERIENCE_BAR)) {
            return;
        }
        Player player = Minecraft.getInstance().player;
        if (player != null && player.getVehicle() instanceof TremorzillaServant) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPostRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        if (!isOverlay(event, VanillaGuiOverlay.CROSSHAIR)) {
            return;
        }
        Player player = AlexsCaves.PROXY.getClientSidePlayer();
        if (player == null || !(player.getVehicle() instanceof TremorzillaServant servant)) {
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
        int y = guiScaledHeight - yOffset - config.subterranodonIndicatorY.get() + DINO_Y_EXTRA;

        float invProgress = 1.0F - servant.getMeterAmount();
        int vOffset = DINO_V_OFFSET;
        if (servant.isPowered() && !servant.isFiring() && servant.getSpikesDownAmount() > 0.0F) {
            if (servant.tickCount / 2 % 2 == 1) {
                vOffset = DINO_V_OFFSET_FLASH;
            }
            invProgress = 1.0F;
        }

        GuiGraphics guiGraphics = event.getGuiGraphics();
        guiGraphics.pose().pushPose();
        guiGraphics.blit(DINOSAUR_HUD_OVERLAYS, x, y, 50, DINO_U_OFFSET,
                vOffset + DINO_BAR_HEIGHT, 43, DINO_BAR_HEIGHT, 128, 512);
        guiGraphics.blit(DINOSAUR_HUD_OVERLAYS, x, y, 50, DINO_U_OFFSET,
                vOffset, 43, (int) Math.floor(DINO_BAR_HEIGHT * invProgress), 128, 512);
        guiGraphics.pose().popPose();
    }

    private static boolean isOverlay(RenderGuiOverlayEvent event, VanillaGuiOverlay overlay) {
        return event.getOverlay().id().equals(overlay.id());
    }
}
