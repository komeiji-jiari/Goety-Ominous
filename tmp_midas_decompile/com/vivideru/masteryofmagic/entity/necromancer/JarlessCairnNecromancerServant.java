/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.entities.ally.undead.skeleton.CairnNecromancerServant
 *  com.Polarice3.Goety.common.entities.neutral.AbstractCairnNecromancer
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 */
package com.vivideru.masteryofmagic.entity.necromancer;

import com.Polarice3.Goety.common.entities.ally.undead.skeleton.CairnNecromancerServant;
import com.Polarice3.Goety.common.entities.neutral.AbstractCairnNecromancer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class JarlessCairnNecromancerServant
extends CairnNecromancerServant {
    public JarlessCairnNecromancerServant(EntityType<? extends AbstractCairnNecromancer> type, Level level) {
        super(type, level);
    }

    public void soulJar() {
    }
}

