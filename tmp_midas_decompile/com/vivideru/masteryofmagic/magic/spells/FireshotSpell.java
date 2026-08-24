/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.magic.SpellType
 *  com.Polarice3.Goety.common.enchantments.ModEnchantments
 *  com.Polarice3.Goety.common.entities.projectiles.HellBolt
 *  com.Polarice3.Goety.common.entities.projectiles.ModFireball
 *  com.Polarice3.Goety.common.magic.Spell
 *  com.Polarice3.Goety.common.magic.SpellStat
 *  com.Polarice3.Goety.init.ModSounds
 *  com.Polarice3.Goety.utils.CuriosFinder
 *  com.Polarice3.Goety.utils.WandUtil
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.world.damagesource.DamageType
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.enchantment.Enchantment
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package com.vivideru.masteryofmagic.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.HellBolt;
import com.Polarice3.Goety.common.entities.projectiles.ModFireball;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.WandUtil;
import com.vivideru.masteryofmagic.config.SpellConfig;
import com.vivideru.masteryofmagic.config.SpellConfigCache;
import com.vivideru.masteryofmagic.util.AreaAttackUtil;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FireshotSpell
extends Spell {
    private static final ResourceKey<DamageType> MAGIC_FIRE = ResourceKey.m_135785_((ResourceKey)Registries.f_268580_, (ResourceLocation)new ResourceLocation("goety", "magic_fire"));

    public int defaultSoulCost() {
        return (Integer)SpellConfig.FIRESHOT_SOUL_COST.get();
    }

    public int defaultCastDuration() {
        return (Integer)SpellConfig.FIRESHOT_CAST_TIME.get();
    }

    public int defaultSpellCooldown() {
        return (Integer)SpellConfig.FIRESHOT_COOLDOWN.get();
    }

    public int soulCost(LivingEntity caster, ItemStack staff) {
        return this.defaultSoulCost();
    }

    public int castDuration(LivingEntity caster, ItemStack staff) {
        return this.defaultCastDuration();
    }

    public int spellCooldown(LivingEntity caster, ItemStack staff) {
        return this.defaultSpellCooldown();
    }

    public SoundEvent CastingSound(LivingEntity caster) {
        if (CuriosFinder.hasUnholySet((LivingEntity)caster)) {
            return (SoundEvent)ModSounds.HELL_BOLT_SHOOT.get();
        }
        return SoundEvents.f_11701_;
    }

    public SpellType getSpellType() {
        return SpellType.NETHER;
    }

    public List<Enchantment> acceptedEnchantments() {
        ArrayList<Enchantment> list = new ArrayList<Enchantment>();
        list.add((Enchantment)ModEnchantments.POTENCY.get());
        list.add((Enchantment)ModEnchantments.BURNING.get());
        return list;
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        Vec3 up;
        int potency = spellStat.getPotency();
        int burning = spellStat.getBurning();
        if (WandUtil.enchantedFocus((LivingEntity)caster)) {
            potency += WandUtil.getPotencyLevel((LivingEntity)caster);
            burning += WandUtil.getLevels((Enchantment)((Enchantment)ModEnchantments.BURNING.get()), (LivingEntity)caster);
        }
        int projectileCount = 3;
        float coneWidth = 4.0f;
        double projectileSpread = 0.24;
        double knockback = 1.35;
        if (this.rightStaff(staff)) {
            projectileCount = 9;
            coneWidth = 6.5f;
            projectileSpread = 0.34;
            knockback = 1.75;
        }
        int maxDamage = SpellConfigCache.FIRESHOT_MAX_DAMAGE + 5 * potency;
        int minDamage = SpellConfigCache.FIRESHOT_MIN_DAMAGE + potency;
        Vec3 look = caster.m_20252_(1.0f).m_82541_();
        Vec3 right = look.m_82537_(up = new Vec3(0.0, 1.0, 0.0)).m_82541_();
        if (right.m_82556_() < 0.001) {
            right = new Vec3(1.0, 0.0, 0.0);
        }
        Vec3 vertical = right.m_82537_(look).m_82541_();
        for (int i = 0; i < projectileCount; ++i) {
            double horizontalSpread = (worldIn.f_46441_.m_188500_() - 0.5) * projectileSpread * 2.0;
            double verticalSpread = (worldIn.f_46441_.m_188500_() - 0.5) * projectileSpread * 0.75;
            Vec3 direction = look.m_82549_(right.m_82490_(horizontalSpread)).m_82549_(vertical.m_82490_(verticalSpread)).m_82541_();
            ModFireball projectile = new ModFireball((Level)worldIn, caster.m_20185_() + look.f_82479_ / 2.0, caster.m_20188_() - 0.5, caster.m_20189_() + look.f_82481_ / 2.0, direction.f_82479_, direction.f_82480_, direction.f_82481_);
            if (CuriosFinder.hasUnholySet((LivingEntity)caster)) {
                projectile = new HellBolt(caster.m_20185_() + look.f_82479_ / 2.0, caster.m_20188_() - 0.5, caster.m_20189_() + look.f_82481_ / 2.0, direction.f_82479_, direction.f_82480_, direction.f_82481_, (Level)worldIn);
            }
            projectile.m_5602_((Entity)caster);
            projectile.m_6686_(direction.f_82479_, direction.f_82480_, direction.f_82481_, 1.5f, 0.0f);
            projectile.m_20256_(direction.m_82490_(1.5));
            if (projectile instanceof ModFireball) {
                ModFireball fireball = projectile;
                if (this.isShifting(caster)) {
                    fireball.setDangerous(false);
                }
                fireball.setExtraDamage(0.0f);
                fireball.setFiery(burning);
            } else if (projectile instanceof HellBolt) {
                HellBolt hellBolt = (HellBolt)projectile;
                hellBolt.setDamage(1.0f);
                hellBolt.setFiery(burning);
            }
            worldIn.m_7967_((Entity)projectile);
        }
        AreaAttackUtil.attackInFrontScalingDamageKnockback((Entity)caster, minDamage, maxDamage, MAGIC_FIRE, 0.0f, 16.0f, coneWidth, 4 + burning, knockback);
        this.spawnConeFireParticles(worldIn, caster, look, right, vertical, coneWidth);
        this.playSound(worldIn, (Entity)caster, SoundEvents.f_11913_, 1.65f, 0.58f + worldIn.f_46441_.m_188501_() * 0.1f);
        SoundEvent soundEvent = SoundEvents.f_11705_;
        if (CuriosFinder.hasUnholySet((LivingEntity)caster)) {
            soundEvent = (SoundEvent)ModSounds.HELL_BOLT_SHOOT.get();
        }
        this.playSound(worldIn, (Entity)caster, soundEvent, 2.0f, this.projPitch(worldIn.m_213780_()));
    }

    private void spawnConeFireParticles(ServerLevel worldIn, LivingEntity caster, Vec3 look, Vec3 right, Vec3 vertical, float coneWidth) {
        Vec3 origin = caster.m_146892_().m_82549_(look.m_82490_(0.75));
        for (int i = 0; i < 70; ++i) {
            double distance = 0.75 + worldIn.f_46441_.m_188500_() * 9.25;
            double currentHalfWidth = (double)coneWidth * 0.5 * (distance / 10.0);
            double horizontal = (worldIn.f_46441_.m_188500_() - 0.5) * currentHalfWidth * 2.0;
            double verticalOffset = (worldIn.f_46441_.m_188500_() - 0.5) * currentHalfWidth * 1.1;
            Vec3 pos = origin.m_82549_(look.m_82490_(distance)).m_82549_(right.m_82490_(horizontal)).m_82549_(vertical.m_82490_(verticalOffset));
            Vec3 velocity = look.m_82490_(0.05 + worldIn.f_46441_.m_188500_() * 0.1).m_82549_(right.m_82490_(horizontal * 0.015)).m_82549_(vertical.m_82490_(verticalOffset * 0.015));
            worldIn.m_8767_((ParticleOptions)ParticleTypes.f_123744_, pos.f_82479_, pos.f_82480_, pos.f_82481_, 1, velocity.f_82479_, velocity.f_82480_, velocity.f_82481_, 0.02);
            if (i % 3 == 0) {
                worldIn.m_8767_((ParticleOptions)ParticleTypes.f_123756_, pos.f_82479_, pos.f_82480_, pos.f_82481_, 1, 0.0, 0.0, 0.0, 0.0);
            }
            if (i % 4 != 0) continue;
            worldIn.m_8767_((ParticleOptions)ParticleTypes.f_123755_, pos.f_82479_, pos.f_82480_, pos.f_82481_, 1, velocity.f_82479_ * 0.35, velocity.f_82480_ * 0.35, velocity.f_82481_ * 0.35, 0.01);
        }
    }
}

