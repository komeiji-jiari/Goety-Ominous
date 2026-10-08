package com.qiuyue.goetyominous.common.magic.spells.ac;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.qiuyue.goetyominous.common.entities.projectile.ac.DeepOneServantWave;
import com.qiuyue.goetyominous.config.SpellConfig;
import com.qiuyue.goetyominous.utils.ac.KeyOfRlyehMixinHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class WaveBashSpell extends Spell {

    private static final double POWER = 2.0D;
    private static final double POWER_RIGHT_STAFF = 4.0D;
    private static final int FULL_CHARGE = 60;
    private static final int CHARGE_PER_WAVE = 5;
    private static final double DASH_BOX_FACTOR = 2.0D;
    private static final double DASH_DAMAGE = 6.0D;
    private static final double VERTICAL_SCALE = 0.4D;

    @Override
    public int defaultSoulCost() {
        return SpellConfig.TideBashCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return 0;
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.ABYSS_PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.TideBashCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.ABYSS;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int potency = spellStat.getPotency();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
        }

        double power = this.rightStaff(staff) ? POWER_RIGHT_STAFF : POWER;
        double push = power + (double) potency / 2.0D;

        Vec3 look = caster.getLookAngle().normalize();

        int waveCount = FULL_CHARGE / CHARGE_PER_WAVE;

        if (!caster.isShiftKeyDown()) {
            caster.hurtMarked = true;
            caster.setOnGround(false);
            caster.setDeltaMovement(look.x * push, look.y * push * VERTICAL_SCALE, look.z * push);
            caster.hasImpulse = true;
            caster.fallDistance = 0.0F;

            double dashDamage = DASH_DAMAGE + (double) potency;
            AABB dashBox = new AABB(caster.position(),
                    caster.position().add(look.scale(push * DASH_BOX_FACTOR))).inflate(1.0D);
            DamageSource dashSource = caster.damageSources().playerAttack(
                    caster instanceof Player player ? player : null);
            for (LivingEntity hit : worldIn.getEntitiesOfClass(LivingEntity.class, dashBox)) {
                if (!caster.isAlliedTo(hit) && !caster.equals(hit) && caster.hasLineOfSight(hit)) {
                    hit.hurt(dashSource, (float) dashDamage);
                    hit.stopRiding();
                }
            }
        }

        worldIn.playSound(null, caster.getX(), caster.getY(), caster.getZ(),
                ACSoundRegistry.ORTHOLANCE_WAVE.get(), caster.getSoundSource(), 4.0F, 1.0F);

        boolean keyStaff = KeyOfRlyehMixinHelper.isKeyOfRlyeh(staff);

        if (keyStaff) {
            DeepOneServantWave tsunami = new DeepOneServantWave(worldIn, caster);
            tsunami.moveTo(caster.getX(), caster.getY(), caster.getZ());
            tsunami.setLifespan(20);
            tsunami.setWaveScale(5.0F);
            tsunami.setWaitingTicks(2);
            tsunami.setScaleBasedDamage(true);
            tsunami.setExtraDamage((float) potency);
            tsunami.setYRot(-(float) (Mth.atan2(look.x, look.z) * (180.0D / Math.PI)));
            worldIn.addFreshEntity(tsunami);
        } else {
            float baseYaw = -(float) (Mth.atan2(look.x, look.z) * (180.0D / Math.PI));
            for (int i = 0; i < waveCount; ++i) {
                float f = (float) i / (float) waveCount;
                int lifespan = 3 + (int) ((1.0F - f) * 3.0F);
                Vec3 center = caster.position().add(look.scale((double) (f * 2.0F)));

                DeepOneServantWave left = new DeepOneServantWave(worldIn, caster);
                left.moveTo(center.x, caster.getY(), center.z);
                left.setLifespan(lifespan);
                left.setExtraDamage((float) potency);
                left.setYRot(baseYaw + 60.0F - (float) (15 * i));
                worldIn.addFreshEntity(left);

                DeepOneServantWave right = new DeepOneServantWave(worldIn, caster);
                right.moveTo(center.x, caster.getY(), center.z);
                right.setLifespan(lifespan);
                right.setExtraDamage((float) potency);
                right.setYRot(baseYaw - 60.0F + (float) (15 * i));
                worldIn.addFreshEntity(right);
            }
        }
    }
}
