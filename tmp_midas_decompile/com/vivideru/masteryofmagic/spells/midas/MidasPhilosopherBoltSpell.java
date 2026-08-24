/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package com.vivideru.masteryofmagic.spells.midas;

import com.vivideru.masteryofmagic.entity.PhilosopherBoltEntity;
import com.vivideru.masteryofmagic.entity.PhilosopherKingMidasEntity;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModEntities;
import com.vivideru.masteryofmagic.spells.midas.MidasBossSpell;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class MidasPhilosopherBoltSpell
implements MidasBossSpell {
    public static final MidasPhilosopherBoltSpell INSTANCE = new MidasPhilosopherBoltSpell();
    private static final ResourceLocation ID = new ResourceLocation("goety_mastery_of_magic", "midas_philosopher_bolt");

    private MidasPhilosopherBoltSpell() {
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public int castingFlag() {
        return 16;
    }

    @Override
    public int cooldownTicks() {
        return 120;
    }

    @Override
    public int maximumCastTicks() {
        return 24;
    }

    @Override
    public void start(ServerLevel l, PhilosopherKingMidasEntity m, @Nullable LivingEntity t) {
        l.m_5594_(null, m.m_20183_(), SoundEvents.f_11867_, SoundSource.HOSTILE, 1.6f, 1.25f);
    }

    @Override
    public void tick(ServerLevel l, PhilosopherKingMidasEntity m, @Nullable LivingEntity ignored, int tick) {
        BlockPos target = m.getPhilosopherBoltTarget();
        if (target == null) {
            return;
        }
        Vec3 aim = Vec3.m_82512_((Vec3i)target);
        Vec3 origin = m.m_146892_();
        m.m_21563_().m_24950_(aim.f_82479_, aim.f_82480_, aim.f_82481_, 180.0f, 180.0f);
        if (tick == 10) {
            Vec3 dir = aim.m_82546_(origin).m_82541_();
            PhilosopherBoltEntity bolt = new PhilosopherBoltEntity((EntityType<? extends PhilosopherBoltEntity>)((EntityType)GoetyMasteryOfMagicModEntities.PHILOSOPHER_BOLT.get()), (Level)l);
            bolt.m_5602_((Entity)m);
            bolt.m_6034_(origin.f_82479_ + dir.f_82479_ * 1.5, origin.f_82480_ + dir.f_82480_ * 1.5, origin.f_82481_ + dir.f_82481_ * 1.5);
            bolt.m_6686_(dir.f_82479_, dir.f_82480_, dir.f_82481_, 1.65f, 0.0f);
            l.m_7967_((Entity)bolt);
            l.m_5594_(null, m.m_20183_(), SoundEvents.f_11862_, SoundSource.HOSTILE, 2.0f, 1.35f);
        }
    }

    @Override
    public boolean shouldContinue(ServerLevel l, PhilosopherKingMidasEntity m, @Nullable LivingEntity t, int tick) {
        return m.getPhilosopherBoltTarget() != null && tick < this.maximumCastTicks();
    }

    @Override
    public void stop(ServerLevel l, PhilosopherKingMidasEntity m, @Nullable LivingEntity t, int tick) {
        m.setPhilosopherBoltTarget(null);
    }
}

