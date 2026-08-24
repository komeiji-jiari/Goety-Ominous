/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.entities.ally.BlackBeast
 *  net.minecraft.client.Minecraft
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.RenderPlayerEvent$Pre
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.vivideru.masteryofmagic.client;

import com.Polarice3.Goety.common.entities.ally.BlackBeast;
import com.vivideru.masteryofmagic.PlanetShapeAccess;
import com.vivideru.masteryofmagic.mixins.EntityWaterStateAccessor;
import com.vivideru.masteryofmagic.mixins.WalkAnimationStateAccessor;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.FORGE)
public final class PlanetShapeRenderer {
    private static final Map<UUID, LivingEntity> CACHE = new HashMap<UUID, LivingEntity>();
    private static final Map<UUID, Integer> BLACK_BEAST_ROAR_UNTIL = new HashMap<UUID, Integer>();

    private PlanetShapeRenderer() {
    }

    @SubscribeEvent
    public static void render(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        if (!(player instanceof PlanetShapeAccess)) {
            return;
        }
        PlanetShapeAccess access = (PlanetShapeAccess)player;
        String id = access.gmom$getPlanetShape();
        if (id.isBlank()) {
            CACHE.remove(player.m_20148_());
            BLACK_BEAST_ROAR_UNTIL.remove(player.m_20148_());
            return;
        }
        LivingEntity shape = CACHE.get(player.m_20148_());
        boolean createdNow = false;
        if (shape == null || !EntityType.m_20613_((EntityType)shape.m_6095_()).toString().equals(id)) {
            Entity created = EntityType.m_20632_((String)id).map(type -> type.m_20615_(player.m_9236_())).orElse(null);
            if (!(created instanceof LivingEntity)) {
                return;
            }
            LivingEntity living = (LivingEntity)created;
            shape = living;
            shape.m_20225_(true);
            CACHE.put(player.m_20148_(), shape);
            createdNow = true;
        }
        PlanetShapeRenderer.copyAnimationState(player, shape);
        if (shape instanceof BlackBeast) {
            BlackBeast blackBeast = (BlackBeast)shape;
            PlanetShapeRenderer.copyBlackBeastAnimation(player, blackBeast, createdNow);
        }
        event.setCanceled(true);
        Minecraft.m_91087_().m_91290_().m_114384_((Entity)shape, 0.0, 0.0, 0.0, player.m_146908_(), event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());
    }

    private static void copyAnimationState(Player player, LivingEntity shape) {
        shape.f_19797_ = player.f_19797_;
        shape.m_146884_(player.m_20182_());
        shape.m_20256_(player.m_20184_());
        shape.m_6853_(player.m_20096_());
        shape.m_20124_(player.m_20089_());
        shape.m_20282_(player.m_6069_());
        shape.m_6858_(player.m_20142_());
        shape.m_20260_(player.m_6144_());
        ((EntityWaterStateAccessor)shape).gmom$setWasTouchingWater(player.m_20069_());
        shape.m_146922_(player.m_146908_());
        shape.f_19859_ = player.f_19859_;
        shape.m_146926_(player.m_146909_());
        shape.f_19860_ = player.f_19860_;
        shape.m_5616_(player.m_6080_());
        shape.f_20886_ = player.f_20886_;
        shape.f_20883_ = player.f_20883_;
        shape.f_20884_ = player.f_20884_;
        shape.f_20911_ = player.f_20911_;
        shape.f_20912_ = player.f_20912_;
        shape.f_20913_ = player.f_20913_;
        shape.f_20921_ = player.f_20921_;
        shape.f_20920_ = player.f_20920_;
        shape.f_20916_ = player.f_20916_;
        shape.f_20919_ = player.f_20919_;
        PlanetShapeRenderer.copyWalkAnimation((LivingEntity)player, shape);
    }

    private static void copyBlackBeastAnimation(Player player, BlackBeast beast, boolean createdNow) {
        boolean attacking;
        UUID id = player.m_20148_();
        if (createdNow) {
            beast.setAnimationState("roar");
            BLACK_BEAST_ROAR_UNTIL.put(id, player.f_19797_ + 30);
        }
        boolean bl = attacking = player.f_20911_ || player.f_20913_ > 0 || player.f_20921_ > 0.01f;
        if (attacking) {
            if (beast.getCurrentAnimation() != beast.getAnimationState("attack")) {
                beast.leftSwiped = !beast.leftSwiped;
                beast.setAnimationState("attack");
            }
            return;
        }
        int roarUntil = BLACK_BEAST_ROAR_UNTIL.getOrDefault(id, 0);
        if (player.f_19797_ < roarUntil) {
            return;
        }
        BLACK_BEAST_ROAR_UNTIL.remove(id);
        if (beast.getCurrentAnimation() != beast.getAnimationState("idle")) {
            beast.setAnimationState("idle");
        }
    }

    private static void copyWalkAnimation(LivingEntity source, LivingEntity target) {
        WalkAnimationStateAccessor from = (WalkAnimationStateAccessor)source.f_267362_;
        WalkAnimationStateAccessor to = (WalkAnimationStateAccessor)target.f_267362_;
        to.gmom$setSpeedOld(from.gmom$getSpeedOld());
        to.gmom$setSpeed(from.gmom$getSpeed());
        to.gmom$setPosition(from.gmom$getPosition());
    }
}

