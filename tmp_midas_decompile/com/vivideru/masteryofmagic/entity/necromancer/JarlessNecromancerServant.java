/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.entities.ally.undead.skeleton.NecromancerServant
 *  com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 */
package com.vivideru.masteryofmagic.entity.necromancer;

import com.Polarice3.Goety.common.entities.ally.undead.skeleton.NecromancerServant;
import com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class JarlessNecromancerServant
extends NecromancerServant {
    public JarlessNecromancerServant(EntityType<? extends AbstractNecromancer> type, Level level) {
        super(type, level);
    }

    public void soulJar() {
    }
}

