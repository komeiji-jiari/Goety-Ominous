package com.qiuyue.goetyominous.common.entities.ally.lm;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IMoveGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.StratlingAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.Tornado;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.miauczel.legendary_monsters.config.ModConfig;
import net.miauczel.legendary_monsters.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;
import java.util.List;

public class HoveringHurricaneServant extends IAnimatedMonsterServant {

    public int shootCoolown = 0;
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState ShootAnimationState = new AnimationState();
    public AnimationState SlamAnimationState = new AnimationState();
    public AnimationState deathAnimationState = new AnimationState();

    public HoveringHurricaneServant(EntityType<? extends Summoned> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 5;
        this.setNoAi(false);
        this.setPersistenceRequired();
    }

    @Override
    public void tick() {
        this.updateWithAttack();
        if (this.level().isClientSide) {
            this.idleAnimationState.animateWhen(this.getAttackState() == 0, this.tickCount);
        }
        if (this.shootCoolown > 0) {
            --this.shootCoolown;
        }
        super.tick();
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    public ItemEntity spawnAtLocation(ItemStack stack) {
        ItemEntity itementity = this.spawnAtLocation(stack, 0.0F);
        if (itementity != null) {
            itementity.setGlowingTag(true);
            itementity.setExtendedLifetime();
        }
        return itementity;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        this.setPersistenceRequired();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(6, new Summoned.WanderGoal<>(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(2, new IMoveGoal(this, false, 3.0D));

        this.goalSelector.addGoal(1, new StratlingAttackGoal(this, 0, 2, 0, 40, 40, 3.5F) {
            @Override
            public boolean canUse() {
                return super.canUse() && HoveringHurricaneServant.this.getRandom().nextFloat() * 40.0F < 16.0F
                        && HoveringHurricaneServant.this.getTarget() != null
                        && HoveringHurricaneServant.this.getAttackState() != 3;
            }
        });

        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 3, 0, 45, 45, 15.0F) {
            @Override
            public void stop() {
                super.stop();
                HoveringHurricaneServant.this.shootCoolown = 40;
            }

            @Override
            public boolean canUse() {
                return super.canUse() && HoveringHurricaneServant.this.getRandom().nextFloat() * 40.0F < 16.0F
                        && HoveringHurricaneServant.this.getTarget() != null
                        && HoveringHurricaneServant.this.getAttackState() != 2
                        && HoveringHurricaneServant.this.shootCoolown <= 0;
            }
        });
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.HoveringHurricaneServantHealth.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.HoveringHurricaneServantKnockbackResistance.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.HoveringHurricaneServantFollowRange.get())
                .add(Attributes.ARMOR, AttributesConfig.HoveringHurricaneServantArmor.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.HoveringHurricaneServantMovementSpeed.get())
                .add(Attributes.ATTACK_KNOCKBACK, AttributesConfig.HoveringHurricaneServantAttackKnockback.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.HoveringHurricaneServantDamage.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.HoveringHurricaneServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.HoveringHurricaneServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.HoveringHurricaneServantDamage.get());
    }

    @Override
    public double getFollowSpeed() {
        return 3.0D;
    }

    @Override
    public double getCommandSpeed() {
        return 3.0D;
    }

    public AnimationState getAnimationState(String input) {
        if (input == "slam") {
            return this.SlamAnimationState;
        } else if (input == "idle") {
            return this.idleAnimationState;
        } else if (input == "shoot") {
            return this.ShootAnimationState;
        }
        return new AnimationState();
    }

    public void setSleep(boolean sleep) {
        this.setAttackState(sleep ? 1 : 0);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (ATTACK_STATE.equals(accessor) && this.level().isClientSide) {
            switch (this.getAttackState()) {
                case 0:
                    this.stopAllAnimationStates();
                    break;
                case 1:
                    this.stopAllAnimationStates();
                    this.idleAnimationState.startIfStopped(this.tickCount);
                    break;
                case 2:
                    this.stopAllAnimationStates();
                    this.SlamAnimationState.startIfStopped(this.tickCount);
                    break;
                case 3:
                    this.stopAllAnimationStates();
                    this.ShootAnimationState.startIfStopped(this.tickCount);
                    break;
                default:
                    break;
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    public void stopAllAnimationStates() {
        this.idleAnimationState.stop();
        this.ShootAnimationState.stop();
        this.SlamAnimationState.stop();
        this.deathAnimationState.stop();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.getAttackState() != 14 || this.getAttackState() != 17) {
            this.setNoGravity(false);
        }
        this.level().addParticle(ParticleTypes.CLOUD, this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D),
                0.0D, 0.025D, 0.0D);
    }

    private void AreaAttack(float range, float height, float arc, float damage) {
        List<LivingEntity> entitiesHit = this.getEntityLivingBaseNearby(range, height, range, range);
        for (LivingEntity entityHit : entitiesHit) {
            float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - this.getZ(), entityHit.getX() - this.getX())
                    * 57.29577951308232D - 90.0D) % 360.0D);
            float entityAttackingAngle = this.yBodyRot % 360.0F;
            if (entityHitAngle < 0.0F) {
                entityHitAngle += 360.0F;
            }
            if (entityAttackingAngle < 0.0F) {
                entityAttackingAngle += 360.0F;
            }
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - this.getZ()) * (entityHit.getZ() - this.getZ())
                    + (entityHit.getX() - this.getX()) * (entityHit.getX() - this.getX()));
            if ((entityHitDistance <= range && entityRelativeAngle <= arc / 2.0F && entityRelativeAngle >= -arc / 2.0F)
                    || entityRelativeAngle >= 360.0F - arc / 2.0F
                    || entityRelativeAngle <= -360.0F + arc / 2.0F) {
                if (MobUtil.areAllies(this, entityHit) || entityHit instanceof HoveringHurricaneServant || entityHit == this) {
                    continue;
                }
                entityHit.hurt(this.damageSources().mobAttack(this),
                        (float) ((double) damage * ModConfig.MOB_CONFIG.StratlingDamageMultiplier.get()));
            }
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.CGA.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.CGH.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.CGD.get();
    }

    public void updateWithAttack() {
        if (this.getAttackState() == 3 && this.attackTicks == 13) {
            this.playSound(SoundEvents.WITHER_SHOOT, 1.0F, 1.0F);
            double theta = this.yBodyRot * (Math.PI / 180);
            theta += Math.PI / 2;
            double vecX = Math.cos(theta);
            double vecZ = Math.sin(theta);
            float angle = this.yBodyRot;
            float rad = (float) Math.toRadians(angle);
            double dx = -Math.sin(rad);
            double dz = Math.cos(rad);
            LivingEntity target = this.getTarget();
            float damage = target != null ? 10.0F + (float) (target.getMaxHealth() * 0.05) : 10.0F;
            Tornado tornado = new Tornado(this, dx, 0.0D, dz, this.level(), damage, angle, 120.0F);
            tornado.setPos(this.getX() + vecX * 1.0D, this.getY(0.15D), this.getZ() + vecZ * 1.0D);
            this.level().addFreshEntity(tornado);
        }
        if (this.getAttackState() == 2) {
            if (this.attackTicks == 7) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 2.0F);
            }
            if (this.attackTicks == 15) {
                this.playSound(SoundEvents.DRAGON_FIREBALL_EXPLODE, 2.0F, 1.0F);
                this.AreaAttack(4.5F, 3.0F, 180.0F, 13.0F);
            }
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
                                        MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData,
                                        @Nullable CompoundTag pDataTag) {
        if (pReason == MobSpawnType.MOB_SUMMONED && this.getTrueOwner() instanceof Player player) {
            if (countServants(player) >= MobsConfig.HoveringHurricaneServantLimit.get()) {
                return null;
            }
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof HoveringHurricaneServant servant && servant.getTrueOwner() == player) {
                    ++count;
                }
            }
        }
        return count;
    }
}
