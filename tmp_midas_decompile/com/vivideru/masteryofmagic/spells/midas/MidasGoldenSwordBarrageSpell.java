/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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

import com.vivideru.masteryofmagic.entity.GoldenSwordProjectileEntity;
import com.vivideru.masteryofmagic.entity.PhilosopherKingMidasEntity;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModEntities;
import com.vivideru.masteryofmagic.spells.midas.MidasBossSpell;
import com.vivideru.masteryofmagic.spells.midas.MidasNarrator;
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

public final class MidasGoldenSwordBarrageSpell
implements MidasBossSpell {
    public static final int SWORD_COUNT = 20;
    private static final int WARNING_LEAD_TICKS = 12;
    public static final int PATTERN_DIRECT = 0;
    public static final int PATTERN_CURVED = 1;
    public static final int PATTERN_RAIN = 2;
    public static final MidasGoldenSwordBarrageSpell INSTANCE = new MidasGoldenSwordBarrageSpell();
    private static final ResourceLocation ID = new ResourceLocation("goety_mastery_of_magic", "midas_golden_sword_barrage");

    private MidasGoldenSwordBarrageSpell() {
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public int castingFlag() {
        return 1;
    }

    @Override
    public int cooldownTicks() {
        return 100;
    }

    @Override
    public int maximumCastTicks() {
        return 56;
    }

    @Override
    public void start(ServerLevel level, PhilosopherKingMidasEntity midas, @Nullable LivingEntity target) {
        MidasNarrator.announce(level, midas, "narration.goety_mastery_of_magic.midas.swords.1", "narration.goety_mastery_of_magic.midas.swords.2", "narration.goety_mastery_of_magic.midas.swords.3");
        midas.setSwordBarragePattern(midas.m_217043_().m_188503_(3));
        level.m_5594_(null, midas.m_20183_(), SoundEvents.f_11868_, SoundSource.HOSTILE, 1.8f, 1.35f);
    }

    @Override
    public void tick(ServerLevel level, PhilosopherKingMidasEntity midas, @Nullable LivingEntity target, int castTick) {
        int attackTick = castTick - 12;
        if (target != null && target.m_6084_() && attackTick == 0) {
            for (int index = 0; index < 20; ++index) {
                MidasGoldenSwordBarrageSpell.spawnGoldenSword(level, (LivingEntity)midas, target, index, 20, midas.getSwordBarragePattern());
            }
        }
    }

    @Override
    public boolean shouldContinue(ServerLevel level, PhilosopherKingMidasEntity midas, @Nullable LivingEntity target, int castTick) {
        return target != null && target.m_6084_() && castTick < this.maximumCastTicks();
    }

    public static GoldenSwordProjectileEntity spawnGoldenSword(ServerLevel level, LivingEntity caster, LivingEntity target, int index, int total) {
        return MidasGoldenSwordBarrageSpell.spawnGoldenSword(level, caster, target, index, total, 0);
    }

    public static GoldenSwordProjectileEntity spawnGoldenSword(ServerLevel level, LivingEntity caster, LivingEntity target, int index, int total, int barragePattern) {
        Vec3 toTarget = target.m_20182_().m_82546_(caster.m_20182_()).m_82542_(1.0, 0.0, 1.0);
        Vec3 forward = toTarget.m_82556_() > 1.0E-5 ? toTarget.m_82541_() : new Vec3(0.0, 0.0, 1.0);
        Vec3 right = new Vec3(-forward.f_82481_, 0.0, forward.f_82479_);
        Vec3 spawn = MidasGoldenSwordBarrageSpell.formationPosition(caster, index, total, barragePattern, forward, right);
        GoldenSwordProjectileEntity sword = new GoldenSwordProjectileEntity((EntityType<? extends GoldenSwordProjectileEntity>)((EntityType)GoetyMasteryOfMagicModEntities.GOLDEN_SWORD_PROJECTILE.get()), (Level)level);
        sword.m_5602_((Entity)caster);
        sword.setTarget(target);
        int pathMode = switch (barragePattern) {
            case 1 -> {
                if (index % 2 == 0) {
                    yield 1;
                }
                yield 2;
            }
            case 2 -> 3;
            default -> 0;
        };
        sword.setPathMode(pathMode);
        sword.setFormationSlot(index, total);
        int launchDelay = switch (barragePattern) {
            case 0 -> level.m_213780_().m_188503_(37);
            case 1 -> level.m_213780_().m_188503_(9);
            case 2 -> level.m_213780_().m_188503_(5);
            default -> 0;
        };
        sword.setLaunchDelay(launchDelay);
        sword.m_6034_(spawn.f_82479_, spawn.f_82480_, spawn.f_82481_);
        sword.m_20256_(Vec3.f_82478_);
        level.m_7967_((Entity)sword);
        return sword;
    }

    private static Vec3 formationPosition(LivingEntity caster, int index, int total, int pattern, Vec3 forward, Vec3 right) {
        if (pattern == 1) {
            int half = Math.max(1, total / 2);
            boolean leftArm = index < half;
            int armIndex = leftArm ? index : index - half;
            int armSize = leftArm ? half : Math.max(1, total - half);
            double progress = ((double)armIndex + 1.0) / (double)armSize;
            double armLength = 2.0 + progress * 10.0;
            double side = leftArm ? -1.0 : 1.0;
            return caster.m_20182_().m_82549_(forward.m_82490_(1.5)).m_82549_(right.m_82490_(side * armLength * 0.70710678)).m_82520_(0.0, (double)caster.m_20206_() + 3.0 + armLength * 0.70710678, 0.0);
        }
        if (pattern == 2) {
            double angle = (double)index * 2.399963229728653;
            double radius = 8.5 * Math.sqrt(((double)index + 0.5) / (double)Math.max(1, total));
            return caster.m_20182_().m_82549_(right.m_82490_(Math.cos(angle) * radius)).m_82549_(forward.m_82490_(Math.sin(angle) * radius)).m_82520_(0.0, (double)caster.m_20206_() + 9.0, 0.0);
        }
        double progress = total <= 1 ? 0.5 : (double)index / (double)(total - 1);
        double angle = -1.5707963267948966 + Math.PI * progress;
        double radius = 10.0;
        return caster.m_20182_().m_82549_(forward.m_82490_(Math.cos(angle) * radius)).m_82549_(right.m_82490_(Math.sin(angle) * radius)).m_82520_(0.0, (double)caster.m_20206_() + 5.0 + Math.sin(progress * Math.PI) * 1.5, 0.0);
    }
}

