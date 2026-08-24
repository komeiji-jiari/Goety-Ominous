/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.entities.projectiles.SlashProjectile
 *  com.Polarice3.Goety.init.ModSounds
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package com.vivideru.masteryofmagic.spells.midas;

import com.Polarice3.Goety.common.entities.projectiles.SlashProjectile;
import com.Polarice3.Goety.init.ModSounds;
import com.vivideru.masteryofmagic.entity.PhilosopherKingMidasEntity;
import com.vivideru.masteryofmagic.entity.PhilosopherWindSlashEntity;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModEntities;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModSounds;
import com.vivideru.masteryofmagic.spells.midas.MidasBossSpell;
import com.vivideru.masteryofmagic.spells.midas.MidasNarrator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class MidasPhilosopherWindSlashSpell
implements MidasBossSpell {
    public static final MidasPhilosopherWindSlashSpell INSTANCE = new MidasPhilosopherWindSlashSpell();
    private static final ResourceLocation ID = new ResourceLocation("goety_mastery_of_magic", "midas_philosopher_wind_slash");
    private static final int FIRST_SLASH_TICK = 8;
    private static final int MIN_DELAY_BETWEEN_SLASHES = 36;
    private static final int MAX_DELAY_BETWEEN_SLASHES = 50;
    private static final int END_LAG_TICKS = 10;

    private MidasPhilosopherWindSlashSpell() {
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public int castingFlag() {
        return 8;
    }

    @Override
    public int cooldownTicks() {
        return 180;
    }

    @Override
    public int maximumCastTicks() {
        return 230;
    }

    @Override
    public void start(ServerLevel level, PhilosopherKingMidasEntity midas, @Nullable LivingEntity target) {
        MidasNarrator.announce(level, midas, "narration.goety_mastery_of_magic.midas.slash.1", "narration.goety_mastery_of_magic.midas.slash.2", "narration.goety_mastery_of_magic.midas.slash.3");
        midas.beginPhilosopherSlashVolley(2 + midas.m_217043_().m_188503_(4), 8);
        level.m_5594_(null, midas.m_20183_(), SoundEvents.f_11867_, SoundSource.HOSTILE, 1.8f, 0.72f);
    }

    @Override
    public void tick(ServerLevel level, PhilosopherKingMidasEntity midas, @Nullable LivingEntity target, int castTick) {
        int count;
        if (target == null || !target.m_6084_() || castTick < midas.getPhilosopherSlashNextTick()) {
            return;
        }
        int index = midas.getPhilosopherSlashesSpawned();
        if (index >= (count = midas.getPhilosopherSlashVolleyCount())) {
            return;
        }
        MidasPhilosopherWindSlashSpell.spawnSlash(level, (LivingEntity)midas, target, index, count);
        midas.advancePhilosopherSlashVolley(castTick, 36, 50, 10);
    }

    @Override
    public boolean shouldContinue(ServerLevel level, PhilosopherKingMidasEntity midas, @Nullable LivingEntity target, int castTick) {
        return target != null && target.m_6084_() && castTick < this.maximumCastTicks() && (midas.getPhilosopherSlashesSpawned() < midas.getPhilosopherSlashVolleyCount() || castTick < midas.getPhilosopherSlashNextTick());
    }

    public static PhilosopherWindSlashEntity spawnSlash(ServerLevel level, LivingEntity caster, LivingEntity target, int index, int count) {
        float f;
        if (caster instanceof PhilosopherKingMidasEntity) {
            PhilosopherKingMidasEntity midas = (PhilosopherKingMidasEntity)caster;
            f = midas.nextPhilosopherSlashRoll();
        } else {
            f = level.m_213780_().m_188501_() * 360.0f;
        }
        float roll = f;
        Vec3 origin = caster.m_146892_();
        Vec3 direction = target.m_20191_().m_82399_().m_82546_(origin).m_82541_();
        Vec3 spawn = origin.m_82549_(direction.m_82490_(4.0));
        PhilosopherWindSlashEntity slash = new PhilosopherWindSlashEntity((EntityType<? extends SlashProjectile>)((EntityType)GoetyMasteryOfMagicModEntities.PHILOSOPHER_WIND_SLASH.get()), (Level)level);
        slash.m_5602_((Entity)caster);
        slash.m_6034_(spawn.f_82479_, spawn.f_82480_, spawn.f_82481_);
        slash.setRollDegrees(roll);
        slash.setMaxRadius(3.5f);
        slash.setRadius(3.5f);
        slash.setMaxLifeSpan(240);
        slash.slash(direction, 0.34);
        double horizontal = Math.sqrt(direction.f_82479_ * direction.f_82479_ + direction.f_82481_ * direction.f_82481_);
        float yaw = (float)(Mth.m_14136_((double)direction.f_82481_, (double)direction.f_82479_) * 57.2957763671875) - 90.0f;
        float pitch = (float)(-(Mth.m_14136_((double)direction.f_82480_, (double)horizontal) * 57.2957763671875));
        slash.m_146922_(yaw);
        slash.f_19859_ = yaw;
        slash.m_146926_(pitch);
        slash.f_19860_ = pitch;
        level.m_7967_((Entity)slash);
        level.m_6263_(null, spawn.f_82479_, spawn.f_82480_, spawn.f_82481_, (SoundEvent)ModSounds.WIND.get(), SoundSource.HOSTILE, 2.25f, 0.72f + (float)index * 0.08f);
        if (caster instanceof PhilosopherKingMidasEntity) {
            PhilosopherKingMidasEntity midas = (PhilosopherKingMidasEntity)caster;
            midas.triggerFastSlashAnimation();
            level.m_6263_(null, caster.m_20185_(), caster.m_20186_(), caster.m_20189_(), (SoundEvent)GoetyMasteryOfMagicModSounds.MIDAS_SLASH_VOCAL.get(), SoundSource.HOSTILE, 1.8f, 0.94f + level.m_213780_().m_188501_() * 0.12f);
        }
        return slash;
    }
}

