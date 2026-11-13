package com.qiuyue.goetyominous.client;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.DarkSceneRenderer;
import com.qiuyue.goetyominous.common.items.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, value = Dist.CLIENT)
public class DarkSceneStickHandler {

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
        if (!event.getItemStack().is(ModItems.DARK_SCENE_STICK.get())) {
            return;
        }
        if (!event.getEntity().level().isClientSide) {
            return;
        }
        Player player = event.getEntity();
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
        if (player.isShiftKeyDown()) {
            DarkSceneRenderer.cycleLevel();
            player.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 0.45F,
                    0.85F + 0.15F * DarkSceneRenderer.level());
            player.displayClientMessage(Component.translatable("message.goetyominous.dark_scene.level",
                    DarkSceneRenderer.levelPercent()), true);
            return;
        }
        DarkSceneRenderer.toggle();
        player.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 0.55F, DarkSceneRenderer.active() ? 0.65F : 1.35F);
        player.displayClientMessage(Component.translatable(DarkSceneRenderer.active()
                ? "message.goetyominous.dark_scene.on"
                : "message.goetyominous.dark_scene.off"), true);
    }
}
