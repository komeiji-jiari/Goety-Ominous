package com.qiuyue.goetyominous.client.gui.overlay;

import com.Polarice3.Goety.common.items.curios.EternalCauldronItem;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.SEHelper;
import com.qiuyue.goetyominous.common.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class NetherEternalCauldronGui {
    public static final IGuiOverlay OVERLAY = NetherEternalCauldronGui::drawOverlay;
    private static final Minecraft minecraft = Minecraft.getInstance();

    public static void drawOverlay(ForgeGui gui, GuiGraphics graphics, float partialTicks, int screenWidth,
                                   int screenHeight) {
        if (!MainConfig.ECGuiShow.get() || minecraft.player == null) {
            return;
        }
        Player player = minecraft.player;
        ItemStack itemStack = CuriosFinder.findCurio(player, ModItems.NETHER_ETERNAL_CAULDRON.get());
        if (itemStack.isEmpty() || EternalCauldronItem.getBottle(itemStack).isEmpty()) {
            return;
        }
        ItemStack show = itemStack;
        if (SEHelper.isOnCooldown(player, itemStack)) {
            show = new ItemStack(ModItems.NETHER_ETERNAL_CAULDRON.get());
        }
        graphics.pose().pushPose();
        int x = screenWidth / 2 - 91 - 29 - 29 + MainConfig.ECGuiHorizontal.get();
        int y = screenHeight - 16 - 3 + MainConfig.ECGuiVertical.get();
        graphics.renderFakeItem(show, x, y);
        graphics.renderItemDecorations(minecraft.font, show, x, y);
        graphics.pose().popPose();
    }
}
