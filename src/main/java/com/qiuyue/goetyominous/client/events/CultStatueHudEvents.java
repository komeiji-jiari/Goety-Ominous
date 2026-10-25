package com.qiuyue.goetyominous.client.events;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.blocks.CultStatueBlock;
import com.qiuyue.goetyominous.common.blocks.entities.CultStatueBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CultStatueHudEvents {

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (!event.getOverlay().id().equals(VanillaGuiOverlay.CROSSHAIR.id())) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null || minecraft.level == null) {
            return;
        }
        if (!(minecraft.hitResult instanceof BlockHitResult hit)) {
            return;
        }
        CultStatueBlockEntity shrine = CultStatueBlockEntity.resolve(minecraft.level, hit.getBlockPos());
        if (shrine == null) {
            return;
        }
        if (shrine.getBlockState().getValue(CultStatueBlock.ENABLED)) {
            return;
        }
        GuiGraphics graphics = event.getGuiGraphics();
        Font font = minecraft.font;
        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        Player owner = shrine.getPlayer();
        String ownerText = Component.translatable("tooltip.goety.blockOwner").getString()
                + (owner != null ? owner.getDisplayName().getString() : "");
        String countText = Component.translatable("tooltip.goetyominous.cult_statue.count").getString()
                + shrine.getClientCount() + "/" + shrine.getBindLimit();
        graphics.drawString(font, ownerText, width / 2 - font.width(ownerText) / 2, height - 58, 16777215);
        graphics.drawString(font, countText, width / 2 - font.width(countText) / 2, height - 68, 16777215);
    }
}
