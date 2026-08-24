/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant
 *  com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.damagesource.DamageType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.monster.AbstractIllager
 *  net.minecraft.world.entity.npc.AbstractVillager
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.vivideru.masteryofmagic;

import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.vivideru.masteryofmagic.block.entity.BlackstoneChaliceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic", bus=Mod.EventBusSubscriber.Bus.FORGE)
public class BlackstoneChaliceBloodAbsorbHandler {
    private static ResourceKey<DamageType> createCrushDamageType;

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity dead = event.getEntity();
        Level level = dead.m_9236_();
        if (level.m_5776_()) {
            return;
        }
        boolean ok = false;
        if (dead instanceof AbstractVillager) {
            ok = true;
        }
        if (dead instanceof AbstractIllager) {
            ok = true;
        }
        if (dead instanceof AbstractIllagerServant) {
            ok = true;
        }
        if (dead instanceof RaiderServant) {
            ok = true;
        }
        if (!ok) {
            return;
        }
        BlockPos origin = dead.m_20183_();
        int bloodAmount = (int)(dead.m_21233_() * 5.0f);
        if (BlackstoneChaliceBloodAbsorbHandler.isCreateCrushDamage(event)) {
            bloodAmount *= 2;
        }
        BlackstoneChaliceBlockEntity closest = null;
        double bestDist = Double.MAX_VALUE;
        for (BlockPos pos : BlockPos.m_121940_((BlockPos)origin.m_7918_(-8, -8, -8), (BlockPos)origin.m_7918_(8, 8, 8))) {
            double dist;
            BlackstoneChaliceBlockEntity be;
            BlockEntity blockEntity;
            if (!level.m_46805_(pos) || !((blockEntity = level.m_7702_(pos)) instanceof BlackstoneChaliceBlockEntity) || (be = (BlackstoneChaliceBlockEntity)blockEntity).isFull() || !((dist = pos.m_123331_((Vec3i)origin)) < bestDist)) continue;
            bestDist = dist;
            closest = be;
        }
        if (closest != null) {
            closest.addBlood(bloodAmount);
        }
    }

    private static boolean isCreateCrushDamage(LivingDeathEvent event) {
        try {
            Class<?> allDamageTypesClass;
            Object crushFieldValue;
            if (createCrushDamageType == null && (crushFieldValue = (allDamageTypesClass = Class.forName("com.simibubi.create.AllDamageTypes")).getField("CRUSH").get(null)) instanceof ResourceKey) {
                createCrushDamageType = (ResourceKey)crushFieldValue;
            }
            if (createCrushDamageType == null) {
                return false;
            }
            return event.getSource().m_276093_(createCrushDamageType);
        }
        catch (Throwable ignored) {
            return false;
        }
    }
}

