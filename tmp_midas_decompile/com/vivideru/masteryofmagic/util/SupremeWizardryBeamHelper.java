/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.entities.ModEntityType
 *  com.Polarice3.Goety.common.entities.projectiles.CorruptedBeam
 *  com.Polarice3.Goety.common.magic.SpellStat
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package com.vivideru.masteryofmagic.util;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.projectiles.CorruptedBeam;
import com.Polarice3.Goety.common.magic.SpellStat;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class SupremeWizardryBeamHelper {
    private SupremeWizardryBeamHelper() {
    }

    public static void spawnWeakBeam(ServerLevel level, LivingEntity caster, SpellStat stat) {
        Vec3 view = caster.m_20252_(1.0f);
        CorruptedBeam beam = new CorruptedBeam((EntityType)ModEntityType.CORRUPTED_BEAM.get(), (Level)level, caster);
        beam.m_7678_(caster.m_20185_() + view.f_82479_ / 2.0, caster.m_20188_() - 0.2, caster.m_20189_() + view.f_82481_ / 2.0, caster.m_146908_(), caster.m_146909_());
        beam.setOwner(caster);
        beam.setExtraDamage(Math.max(0.0f, (float)stat.getPotency() * 0.25f));
        beam.setItemBase(true);
        level.m_7967_((Entity)beam);
    }
}

