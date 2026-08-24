/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.entities.ally.undead.skeleton.MossyNecromancerServant
 *  com.Polarice3.Goety.common.entities.neutral.AbstractMossyNecromancer
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 */
package com.vivideru.masteryofmagic.entity.necromancer;

import com.Polarice3.Goety.common.entities.ally.undead.skeleton.MossyNecromancerServant;
import com.Polarice3.Goety.common.entities.neutral.AbstractMossyNecromancer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class JarlessMossyNecromancerServant
extends MossyNecromancerServant {
    public JarlessMossyNecromancerServant(EntityType<? extends AbstractMossyNecromancer> type, Level level) {
        super(type, level);
    }

    public void soulJar() {
    }
}

