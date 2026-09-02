package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.projectiles.SpellThrowableProjectile;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.config.SpellConfig;
import com.qiuyue.goetyominous.utils.CroneCuriosUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityPoisonBall extends SpellThrowableProjectile {
    private static final EntityDataAccessor<Boolean> DATA_UPGRADED =
            SynchedEntityData.defineId(EntityPoisonBall.class, EntityDataSerializers.BOOLEAN);

    public EntityPoisonBall(EntityType<? extends EntityPoisonBall> type, Level worldIn) {
        super(type, worldIn);
    }

    public EntityPoisonBall(Level worldIn, LivingEntity shooter) {
        super(ModEntityTypes.POISON_BALL.get(), shooter.getX(), shooter.getEyeY() - 0.1F, shooter.getZ(), worldIn);
        this.setOwner(shooter);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.spawnParticles();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void spawnParticles() {
        for (int i = 0; i < 8; ++i) {
            this.level().addParticle(ParticleTypes.ITEM_SLIME,
                    this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!this.level().isClientSide) {
            Entity target = result.getEntity();
            LivingEntity owner = this.getOwner();
            if (owner != null && target instanceof LivingEntity livingTarget && livingTarget.isAlive()) {
                int enchantment = 0;
                int duration = 1;
                if (owner instanceof Player player && WandUtil.enchantedFocus(player)) {
                    enchantment = WandUtil.getLevels(ModEnchantments.POTENCY.get(), player);
                    duration = WandUtil.getLevels(ModEnchantments.DURATION.get(), player) + 1;
                }
                if (this.isUpgraded()) {
                    livingTarget.hurt(this.damageSources().indirectMagic(this, owner),
                            SpellConfig.PoisonBallDamage.get().floatValue() * WandUtil.damageMultiply() + enchantment);
                }
                if (CroneCuriosUtil.hasCroneSet(owner)) {
                    livingTarget.addEffect(new MobEffectInstance(GoetyEffects.ACID_VENOM.get(), 60 * duration, enchantment));
                    livingTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60 * duration, enchantment));
                } else {
                    livingTarget.addEffect(new MobEffectInstance(MobEffects.POISON, 60 * duration, enchantment));
                }
            }
        }
        this.playSound(SoundEvents.SLIME_ATTACK, 1.0F, 1.0F);
        this.remove(RemovalReason.DISCARDED);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (!this.level().isClientSide) {
            this.playSound(SoundEvents.SLIME_ATTACK, 1.0F, 1.0F);
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_UPGRADED, false);
    }

    public boolean isUpgraded() {
        return this.entityData.get(DATA_UPGRADED);
    }

    public void setUpgraded(boolean upgraded) {
        this.entityData.set(DATA_UPGRADED, upgraded);
    }
}
