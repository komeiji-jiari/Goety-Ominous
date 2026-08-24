/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.monster.AbstractIllager
 *  net.minecraft.world.entity.npc.Villager
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package com.vivideru.masteryofmagic;

import com.vivideru.masteryofmagic.entity.VampiratorServantEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic")
public class VampiratorServantBloodcountEvents {
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        Entity entity = event.getSource().m_7639_();
        if (!(entity instanceof VampiratorServantEntity)) {
            return;
        }
        VampiratorServantEntity vamp = (VampiratorServantEntity)entity;
        LivingEntity victim = event.getEntity();
        if (victim instanceof Villager || victim instanceof AbstractIllager) {
            int gain = (int)(victim.m_21233_() * 5.0f);
            vamp.addBloodCount(gain);
        }
    }
}

