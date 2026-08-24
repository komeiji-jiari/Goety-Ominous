/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  net.minecraftforge.client.event.MovementInputUpdateEvent
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$PlayerTickEvent
 *  net.minecraftforge.event.entity.living.LivingEvent$LivingTickEvent
 *  net.minecraftforge.event.entity.player.AttackEntityEvent
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$LeftClickBlock
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$RightClickItem
 *  net.minecraftforge.event.server.ServerStartingEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
 *  net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
 */
package com.vivideru.masteryofmagic;

import com.vivideru.masteryofmagic.TimeFreezeManager;
import com.vivideru.masteryofmagic.TimeFreezeRenderAnimationState;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModMobEffects;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicNetwork;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class TimeFreezeHandler {
    private static boolean TIME_FREEZE_SHADER_ACTIVE = false;

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        event.enqueueWork(GoetyMasteryOfMagicNetwork::register);
        new TimeFreezeHandler();
    }

    @OnlyIn(value=Dist.CLIENT)
    @Mod.EventBusSubscriber(value={Dist.CLIENT})
    private static class TimeFreezeHandlerClientForgeBusEvents {
        private TimeFreezeHandlerClientForgeBusEvents() {
        }

        @SubscribeEvent
        public static void onMovementInputUpdate(MovementInputUpdateEvent event) {
            Player player = event.getEntity();
            if (!player.m_21023_((MobEffect)GoetyMasteryOfMagicModMobEffects.TIME_FREEZE_EFFECT.get())) {
                return;
            }
            event.getInput().f_108566_ = 0.0f;
            event.getInput().f_108567_ = 0.0f;
            event.getInput().f_108572_ = false;
            event.getInput().f_108573_ = false;
            player.f_20902_ = 0.0f;
            player.f_20900_ = 0.0f;
            player.m_6858_(false);
            player.m_20260_(false);
            player.m_20256_(Vec3.f_82478_);
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            boolean playerFrozen;
            if (event.phase != TickEvent.Phase.END) {
                return;
            }
            Minecraft minecraft = Minecraft.m_91087_();
            if (minecraft.f_91074_ == null || minecraft.f_91073_ == null) {
                TIME_FREEZE_SHADER_ACTIVE = false;
                TimeFreezeRenderAnimationState.clear();
                return;
            }
            boolean bl = playerFrozen = TimeFreezeRenderAnimationState.isFrozen(minecraft.f_91074_.m_19879_()) || minecraft.f_91074_.m_21023_((MobEffect)GoetyMasteryOfMagicModMobEffects.TIME_FREEZE_EFFECT.get());
            if (playerFrozen) {
                minecraft.f_91074_.f_20902_ = 0.0f;
                minecraft.f_91074_.f_20900_ = 0.0f;
                minecraft.f_91074_.m_6858_(false);
                minecraft.f_91074_.m_20260_(false);
                minecraft.f_91074_.m_20256_(Vec3.f_82478_);
                TimeFreezeHandlerClientForgeBusEvents.enableShader(minecraft);
            } else {
                TimeFreezeHandlerClientForgeBusEvents.disableShader(minecraft);
            }
        }

        private static void enableShader(Minecraft minecraft) {
            if (minecraft.f_91063_.m_109149_() != null) {
                TIME_FREEZE_SHADER_ACTIVE = true;
                return;
            }
            minecraft.f_91063_.m_109128_(new ResourceLocation("minecraft", "shaders/post/desaturate.json"));
            TIME_FREEZE_SHADER_ACTIVE = true;
        }

        private static void disableShader(Minecraft minecraft) {
            if (!TIME_FREEZE_SHADER_ACTIVE) {
                return;
            }
            if (minecraft.f_91063_.m_109149_() != null) {
                minecraft.f_91063_.m_109086_();
            }
            TIME_FREEZE_SHADER_ACTIVE = false;
        }
    }

    @Mod.EventBusSubscriber
    private static class TimeFreezeHandlerForgeBusEvents {
        private TimeFreezeHandlerForgeBusEvents() {
        }

        @SubscribeEvent
        public static void serverLoad(ServerStartingEvent event) {
        }

        @SubscribeEvent
        public static void onLivingTick(LivingEvent.LivingTickEvent event) {
            UUID immuneUuid;
            LivingEntity entity = event.getEntity();
            if (!entity.m_21023_((MobEffect)GoetyMasteryOfMagicModMobEffects.TIME_FREEZE_EFFECT.get())) {
                return;
            }
            if (!entity.m_9236_().m_5776_() && (immuneUuid = TimeFreezeManager.getCasterUuidForFrozenZone(entity.m_9236_(), entity.m_20182_())) != null) {
                for (ServerPlayer serverPlayer : entity.m_9236_().m_45976_(ServerPlayer.class, entity.m_20191_().m_82400_(128.0))) {
                    GoetyMasteryOfMagicNetwork.sendTimeFreezeSync(serverPlayer, entity.m_20185_(), entity.m_20186_(), entity.m_20189_(), 4.0, immuneUuid, true);
                }
            }
            entity.m_20256_(Vec3.f_82478_);
            entity.f_19864_ = true;
            entity.f_19789_ = 0.0f;
            if (entity instanceof Mob) {
                Mob mob = (Mob)entity;
                mob.m_21573_().m_26573_();
                mob.m_21561_(false);
                mob.m_6710_(null);
            }
            if (entity instanceof Player) {
                Player player = (Player)entity;
                player.f_20902_ = 0.0f;
                player.f_20900_ = 0.0f;
                player.m_6858_(false);
                player.m_20260_(false);
                player.m_20256_(Vec3.f_82478_);
                player.f_19864_ = true;
            }
        }

        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }
            Player player = event.player;
            if (!player.m_21023_((MobEffect)GoetyMasteryOfMagicModMobEffects.TIME_FREEZE_EFFECT.get())) {
                return;
            }
            player.f_20902_ = 0.0f;
            player.f_20900_ = 0.0f;
            player.m_6858_(false);
            player.m_20260_(false);
            player.m_20256_(Vec3.f_82478_);
            player.f_19864_ = true;
            player.f_19789_ = 0.0f;
        }

        @SubscribeEvent
        public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
            if (event.getEntity().m_21023_((MobEffect)GoetyMasteryOfMagicModMobEffects.TIME_FREEZE_EFFECT.get())) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
            if (event.getEntity().m_21023_((MobEffect)GoetyMasteryOfMagicModMobEffects.TIME_FREEZE_EFFECT.get())) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
            if (event.getEntity().m_21023_((MobEffect)GoetyMasteryOfMagicModMobEffects.TIME_FREEZE_EFFECT.get())) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onEntityAttack(AttackEntityEvent event) {
            if (event.getEntity().m_21023_((MobEffect)GoetyMasteryOfMagicModMobEffects.TIME_FREEZE_EFFECT.get())) {
                event.setCanceled(true);
            }
        }

        @OnlyIn(value=Dist.CLIENT)
        @SubscribeEvent
        public static void clientLoad(FMLClientSetupEvent event) {
        }
    }
}

