/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffectCategory
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.MobType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 */
package com.vivideru.masteryofmagic.potion;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class VulnerableMobEffect
extends MobEffect {
    public VulnerableMobEffect() {
        super(MobEffectCategory.HARMFUL, -52);
    }

    public List<ItemStack> getCurativeItems() {
        ArrayList<ItemStack> cures = new ArrayList<ItemStack>();
        return cures;
    }

    public boolean m_6584_(int duration, int amplifier) {
        return duration % 5 == 0;
    }

    public void m_6742_(LivingEntity entity, int amplifier) {
        Player player;
        if (entity instanceof Player && ((player = (Player)entity).m_7500_() || player.m_5833_())) {
            return;
        }
        if (entity.m_6336_() != MobType.f_21641_) {
            return;
        }
        Level level = entity.m_9236_();
        if (level.m_5776_()) {
            return;
        }
        if (!level.m_46461_()) {
            return;
        }
        if (!level.m_45527_(entity.m_20183_())) {
            return;
        }
        float maxHealth = entity.m_21233_();
        float damage = maxHealth * 0.1f;
        entity.m_6469_(level.m_269111_().m_269341_(), damage);
    }

    public boolean m_8093_() {
        return false;
    }
}

