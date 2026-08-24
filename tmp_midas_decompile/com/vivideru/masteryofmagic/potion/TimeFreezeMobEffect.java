/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffectCategory
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.attributes.AttributeMap
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.phys.Vec3
 */
package com.vivideru.masteryofmagic.potion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.Vec3;

public class TimeFreezeMobEffect
extends MobEffect {
    private static final Map<UUID, FrozenData> FROZEN_DATA = new HashMap<UUID, FrozenData>();

    public TimeFreezeMobEffect() {
        super(MobEffectCategory.HARMFUL, -13382401);
    }

    public List<ItemStack> getCurativeItems() {
        ArrayList<ItemStack> cures = new ArrayList<ItemStack>();
        cures.add(new ItemStack((ItemLike)Items.f_42747_));
        return cures;
    }

    public boolean m_6584_(int duration, int amplifier) {
        return true;
    }

    public void m_6742_(LivingEntity entity, int amplifier) {
        Player player;
        if (entity instanceof Player && ((player = (Player)entity).m_7500_() || player.m_5833_())) {
            return;
        }
        entity.m_20256_(Vec3.f_82478_);
        entity.f_19864_ = true;
        entity.f_19789_ = 0.0f;
        if (entity instanceof Player) {
            player = (Player)entity;
            player.f_20902_ = 0.0f;
            player.f_20900_ = 0.0f;
            player.m_6858_(false);
        }
    }

    public void m_6386_(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.m_6386_(entity, attributeMap, amplifier);
        FrozenData data = FROZEN_DATA.remove(entity.m_20148_());
        if (data != null) {
            entity.m_20256_(data.motion);
            if (entity instanceof Mob) {
                Mob mob = (Mob)entity;
                mob.m_21557_(data.hadNoAi);
            }
        }
    }

    private static class FrozenData {
        Vec3 motion = Vec3.f_82478_;
        boolean hadNoAi = false;

        private FrozenData() {
        }
    }
}

