/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.entities.neutral.Wildfire
 *  javax.annotation.Nullable
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraftforge.network.PlayMessages$SpawnEntity
 */
package com.vivideru.masteryofmagic;

import com.Polarice3.Goety.common.entities.neutral.Wildfire;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModEntities;
import javax.annotation.Nullable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages;

public class FocusWildfireEntity
extends Wildfire {
    public FocusWildfireEntity(EntityType<? extends Wildfire> type, Level level) {
        super(type, level);
    }

    public FocusWildfireEntity(PlayMessages.SpawnEntity packet, Level level) {
        this((EntityType<? extends Wildfire>)((EntityType)GoetyMasteryOfMagicModEntities.FOCUS_WILDFIRE.get()), level);
    }

    public int xpReward() {
        return 0;
    }

    @Nullable
    public ItemEntity m_19983_(ItemStack stack) {
        return null;
    }

    @Nullable
    public ItemEntity m_5552_(ItemStack stack, float yOffset) {
        return null;
    }

    protected void m_7472_(DamageSource source, int looting, boolean recentlyHit) {
    }

    protected void m_7625_(DamageSource source, boolean recentlyHit) {
    }

    protected void m_5907_() {
    }

    protected void m_21226_() {
    }
}

