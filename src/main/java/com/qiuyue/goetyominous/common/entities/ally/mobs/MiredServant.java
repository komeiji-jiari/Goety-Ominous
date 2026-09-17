package com.qiuyue.goetyominous.common.entities.ally.mobs;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieServant;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.utils.CroneCuriosUtil;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class MiredServant extends ZombieServant {

    public MiredServant(EntityType<? extends Summoned> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.MiredHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.MiredFollowRange.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.MiredMovementSpeed.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.MiredDamage.get())
                .add(Attributes.ARMOR, AttributesConfig.MiredArmor.get());
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.MiredHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.MiredArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.MiredDamage.get());
    }

    @Override
    protected boolean convertsInWater() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.JUNGLE_ZOMBIE_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.JUNGLE_ZOMBIE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.JUNGLE_ZOMBIE_DEATH.get();
    }

    @Override
    protected SoundEvent getStepSound() {
        return ModSounds.JUNGLE_ZOMBIE_STEP.get();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            if (this.tickCount % 5 == 0 && this.level().random.nextBoolean()) {
                double[] colors = MathHelper.rgbParticle(0x6B4423);
                this.level().addParticle(ModParticleTypes.BIG_CULT_SPELL.get(),
                        this.getX(), this.getY() + 1.0D, this.getZ(),
                        colors[0], colors[1], colors[2]);
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.WITCH_RESISTANT_TO)) {
            amount *= 0.75F;
        }
        return super.hurt(source, amount);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean flag = super.doHurtTarget(entity);
        if (flag && entity instanceof LivingEntity livingTarget) {
            livingTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
                    MathHelper.secondsToTicks(5)), this);
            MobEffect effect = MobEffects.POISON;
            LivingEntity owner = this.getTrueOwner();
            if (owner != null && CroneCuriosUtil.hasCroneRobe(owner)) {
                effect = GoetyEffects.ACID_VENOM.get();
            }
            livingTarget.addEffect(new MobEffectInstance(effect, MathHelper.secondsToTicks(5)), this);
        }
        return flag;
    }
}
