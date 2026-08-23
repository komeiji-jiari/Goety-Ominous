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

/**
 * 撼地龙仆从的骑乘槽位条 HUD。
 *
 * 原版 AC 在 ClientEvents.onPostRenderGuiOverlay 里按坐骑类型选槽位条纹理偏移:
 * 只有 instanceof TremorsaurusEntity 才走撼地龙样式(纹理 v 偏移 63、条高 31px、y 下移 5),
 * 其余坐骑(含翼龙)落到默认样式(偏移 0、条高 31px)。仆从继承 Goety AnimalSummon,
 * 不是 TremorsaurusEntity,若实现 RidingMeterMount 会被 AC 渲染成默认样式。
 * 为避免自绘,也可直接让仆从实现 RidingMeterMount 并用 Mixin 改 AC 的类型判断,但代价大;
 *
 * 因此仆从不实现 RidingMeterMount(避免 AC 用默认样式渲染),改由本类在骑乘撼地龙仆从时
 * 复刻原版撼地龙的槽位条渲染(贴图、偏移、尺寸、位置全部与原版分支一致)。
 *
 * 注意:不能加 @Mod.EventBusSubscriber —— 本类引用客户端类(Minecraft/GuiGraphics/ForgeGui)
 * 与 AC 类型(AlexsCaves/ACClientConfig),在 GoetyOminous.onClientSetup 里
 * isAlexCavesLoaded() 门内手动注册(客户端侧)。
 */
public class TremorsaurusHudEvents {

    /** AC 恐龙 HUD 贴图(与原版撼地龙共用) */
    private static final ResourceLocation DINOSAUR_HUD_OVERLAYS =
            new ResourceLocation("alexscaves", "textures/misc/dinosaur_hud_overlays.png");

    /**
     * 原版 Tremorsaurus 分支的槽位条参数(逐条对照 AC ClientEvents.onPostRenderGuiOverlay 字节码):
     * 默认(所有坐骑)条高 31、u 偏移 0;Tremorsaurus 分支仅改 u 偏移为 63、y 下移 5。
     * 注意别把"条高"和另设的 istore_3(20,仅用于下方 DarkMatter 护甲条的 y 偏移)搞混——
     * 恐龙槽位条的两处 blit 都用的条高 31,背景 v 偏移 = 63 + 31 = 94。
     */
    private static final int TREMOR_U_OFFSET = 63;
    private static final int TREMOR_BAR_HEIGHT = 31;
    private static final int TREMOR_Y_EXTRA = 5;

    /** 骑乘撼地龙仆从时隐藏原版经验条(与原版 RidingMeterMount 的行为一致) */
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

    /** 骑乘撼地龙仆从时渲染原版撼地龙样式的骑乘槽位条 */
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
        // 背景(整条槽位),v 偏移 = uOffset + 条高,与原版 Tremorsaurus 分支一致
        guiGraphics.blit(DINOSAUR_HUD_OVERLAYS, x, y, 50, 0.0F,
                TREMOR_U_OFFSET + TREMOR_BAR_HEIGHT, 43, TREMOR_BAR_HEIGHT, 128, 512);
        // 填充(空槽部分),高度 = 条高 * (1 - 当前值),与原版 AC 一致
        guiGraphics.blit(DINOSAUR_HUD_OVERLAYS, x, y, 50, 0.0F, TREMOR_U_OFFSET,
                43, (int) Math.floor(TREMOR_BAR_HEIGHT * empty), 128, 512);
        guiGraphics.pose().popPose();
    }

    private static boolean isOverlay(RenderGuiOverlayEvent event, VanillaGuiOverlay overlay) {
        return event.getOverlay().id().equals(overlay.id());
    }
}
