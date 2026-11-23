package com.qiuyue.goetyominous.common.entities.hostile;

import com.Polarice3.Goety.common.entities.hostile.BoneLord;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class OminousBoneLord extends BoneLord {

    public OminousBoneLord(EntityType<? extends OminousBoneLord> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.BoneLordHealth.get() * 2.0D)
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.BoneLordDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.0D);
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.BoneLordHealth.get() * 2.0D);
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.BoneLordDamage.get());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide && this.isAlive()) {
            double yaw = (180.0D - this.yBodyRot) * Mth.DEG_TO_RAD;
            double back = 5.54D / 16.0D;
            double px = this.getX() + back * Math.sin(yaw) + (this.random.nextDouble() - 0.5D) * 0.08D;
            double py = this.getY() + 1.453D + (this.random.nextDouble() - 0.5D) * 0.08D;
            double pz = this.getZ() + back * Math.cos(yaw) + (this.random.nextDouble() - 0.5D) * 0.08D;
            this.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, px, py, pz, 0.0D, 0.01D, 0.0D);
        }
    }
}
