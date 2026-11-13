package com.qiuyue.goetyominous.client.events;

import com.Polarice3.Goety.client.events.BossBarEvent;
import com.Polarice3.Goety.client.events.ClientEvents;
import com.Polarice3.Goety.utils.MiscCapHelper;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.hostile.grimmonarch.GrimMonarchEntity;
import com.qiuyue.goetyominous.common.init.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, value = Dist.CLIENT)
public final class GrimMonarchClientEvents {
    private static final ResourceLocation FRAME = new ResourceLocation(
            GoetyOminous.MOD_ID, "textures/gui/boss_bar.png");
    private static final ResourceLocation FILL = new ResourceLocation(
            GoetyOminous.MOD_ID, "textures/gui/boss_bar_1.png");
    private static final ResourceLocation HURT = new ResourceLocation(
            GoetyOminous.MOD_ID, "textures/gui/boss_bar_hurt.png");
    private static final Set<ResourceKey<Level>> LOCKED_DIMENSIONS = new HashSet<>();

    private GrimMonarchClientEvents() {
    }

    public static void setNightLocked(ResourceKey<Level> dimension, boolean locked) {
        if (locked) {
            LOCKED_DIMENSIONS.add(dimension);
        } else {
            LOCKED_DIMENSIONS.remove(dimension);
        }
    }

    public static boolean nightLocked() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.level != null && LOCKED_DIMENSIONS.contains(minecraft.level.dimension());
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        LOCKED_DIMENSIONS.clear();
    }

    @SubscribeEvent
    public static void playBossMusic(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof GrimMonarchEntity monarch) || monarch.isEcho()) {
            return;
        }
        if (!(monarch.level() instanceof ClientLevel)) {
            return;
        }
        if (monarch.isNoAi() || !(MiscCapHelper.getMobTarget(monarch) instanceof Player)) {
            return;
        }
        ClientEvents.playBossMusic(ModSounds.MONARCH_BOSS.get(), monarch, 1.0F, 1.0F);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void renderBossBar(CustomizeGuiOverlayEvent.BossEventProgress event) {
        Mob boss = BossBarEvent.BOSS_BARS.get(event.getBossEvent().getId());
        if (!(boss instanceof GrimMonarchEntity monarch)) {
            return;
        }
        event.setCanceled(true);
        Minecraft minecraft = Minecraft.getInstance();
        GuiGraphics graphics = event.getGuiGraphics();
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int x = screenWidth / 2 - 100;
        int y = event.getY();
        float partial = event.getPartialTick();
        boolean echo = monarch.isEcho();
        float fillV = echo ? 8.0F : 0.0F;
        float frameV = echo ? 18.0F : 0.0F;
        float hurtV = echo ? 16.0F : 0.0F;
        float percent = monarch.getHealth() / monarch.getMaxHealth();
        int fillWidth = (int) (percent * 182.0F);
        int offset = (int) (((float) monarch.tickCount + partial) % 364.0F);
        if (percent <= 0.25F) {
            offset = (int) (((float) monarch.tickCount + partial) * 4.0F % 364.0F);
        } else if (percent <= 0.5F) {
            offset = (int) (((float) monarch.tickCount + partial) * 2.0F % 364.0F);
        }
        if (fillWidth > 0) {
            graphics.blit(FILL, x + 9, y + 4, (float) offset, fillV, fillWidth, 8, 364, 16);
            if (monarch.hurtTime >= 5) {
                int jitter = Math.min(monarch.hurtTime, 9);
                int shake = monarch.getRandom().nextInt(jitter);
                int damage = monarch.getRandom().nextInt(jitter);
                graphics.blit(HURT, x + 9, y + 4, (float) shake, hurtV + (float) damage, fillWidth, 8, 256, 32);
            }
        }
        graphics.blit(FRAME, x, y, 0.0F, frameV, 200, 18, 256, 36);
        Component name = monarch.getDisplayName();
        int nameWidth = minecraft.font.width(name);
        graphics.drawString(minecraft.font, name, screenWidth / 2 - nameWidth / 2, y - 9, 0xFFFFFF);
        if (y < minecraft.getWindow().getGuiScaledHeight() / 3) {
            event.setIncrement(21);
        }
    }
}
