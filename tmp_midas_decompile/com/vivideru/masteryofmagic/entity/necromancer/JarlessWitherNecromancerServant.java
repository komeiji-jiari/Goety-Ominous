/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.entities.ally.undead.skeleton.WitherNecromancerServant
 *  com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 */
package com.vivideru.masteryofmagic.entity.necromancer;

import com.Polarice3.Goety.common.entities.ally.undead.skeleton.WitherNecromancerServant;
import com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class JarlessWitherNecromancerServant
extends WitherNecromancerServant {
    public JarlessWitherNecromancerServant(EntityType<? extends AbstractNecromancer> type, Level level) {
        super(type, level);
    }

    public void soulJar() {
    }
}

