/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer
 *  com.Polarice3.Goety.common.entities.neutral.DrownedNecromancer
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 */
package com.vivideru.masteryofmagic.entity.necromancer;

import com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer;
import com.Polarice3.Goety.common.entities.neutral.DrownedNecromancer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class JarlessDrownedNecromancerServant
extends DrownedNecromancer {
    public JarlessDrownedNecromancerServant(EntityType<? extends AbstractNecromancer> type, Level level) {
        super(type, level);
    }

    public void soulJar() {
    }
}

