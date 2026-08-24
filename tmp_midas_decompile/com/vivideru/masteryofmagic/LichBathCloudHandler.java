/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.capabilities.lichdom.LichProvider
 *  com.Polarice3.Goety.common.entities.ModEntityType
 *  com.Polarice3.Goety.common.entities.neutral.VampireBat
 *  com.Polarice3.Goety.utils.BlockFinder
 *  com.Polarice3.Goety.utils.ConstantPaths
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntitySelector
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.ambient.Bat
 *  net.minecraft.world.entity.animal.Bee
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
 */
package com.vivideru.masteryofmagic;

import com.Polarice3.Goety.common.capabilities.lichdom.LichProvider;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.neutral.VampireBat;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ConstantPaths;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class LichBathCloudHandler {
    private static final String MOD_DATA_KEY = "goetymasteryofmagic";
    private static final String GOBLET_BATS_UNLOCKED = "goblet_bats_unlocked";
    private static final String BATS_COOLDOWN_KEY = "goblet_bats_cooldown";
    private static final int COOLDOWN_TICKS = 60;

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
    }

    @Mod.EventBusSubscriber
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            LivingEntity livingEntity = event.getEntity();
            if (!(livingEntity instanceof Player)) {
                return;
            }
            Player player = (Player)livingEntity;
            if (player.m_9236_().f_46443_) {
                return;
            }
            player.getCapability(LichProvider.CAPABILITY).ifPresent(lichdom -> {
                long lastUse;
                if (!lichdom.getLichdom()) {
                    return;
                }
                CompoundTag persistentData = player.getPersistentData();
                CompoundTag modData = persistentData.m_128469_(LichBathCloudHandler.MOD_DATA_KEY);
                if (!modData.m_128471_(LichBathCloudHandler.GOBLET_BATS_UNLOCKED)) {
                    return;
                }
                if (player.m_21023_((MobEffect)GoetyMasteryOfMagicModMobEffects.VULNERABLE.get())) {
                    return;
                }
                long currentTime = player.m_9236_().m_46467_();
                if (currentTime - (lastUse = modData.m_128454_(LichBathCloudHandler.BATS_COOLDOWN_KEY)) < 60L) {
                    return;
                }
                Entity attacker = event.getSource().m_7639_();
                if (!(attacker instanceof LivingEntity)) {
                    return;
                }
                LivingEntity target = (LivingEntity)attacker;
                if (target instanceof Bee || target instanceof Bat) {
                    return;
                }
                if (!EntitySelector.f_20406_.test(target)) {
                    return;
                }
                int amount = player.m_9236_().f_46441_.m_188503_(2) + 3;
                for (int i = 0; i < amount; ++i) {
                    VampireBat bat = new VampireBat((EntityType)ModEntityType.VAMPIRE_BAT.get(), player.m_9236_());
                    bat.m_20035_(BlockFinder.SummonRadius((BlockPos)player.m_20183_(), (Entity)bat, (Level)player.m_9236_()), 0.0f, 0.0f);
                    bat.setTrueOwner((LivingEntity)player);
                    bat.m_6710_(target);
                    bat.m_20049_(ConstantPaths.conjuredBat());
                    player.m_9236_().m_7967_((Entity)bat);
                }
                modData.m_128356_(LichBathCloudHandler.BATS_COOLDOWN_KEY, currentTime);
                persistentData.m_128365_(LichBathCloudHandler.MOD_DATA_KEY, (Tag)modData);
            });
        }
    }
}

