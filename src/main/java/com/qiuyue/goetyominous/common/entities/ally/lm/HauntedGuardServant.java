package com.qiuyue.goetyominous.common.entities.ally.lm;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IMoveGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IStateGoal;
import com.qiuyue.goetyominous.config.lm.LmConfig;
import com.qiuyue.goetyominous.utils.ServantAllyUtil;
import net.miauczel.legendary_monsters.Particle.custom.KnightSweepParticle;
import net.miauczel.legendary_monsters.sound.ModSounds;
import net.miauczel.legendary_monsters.util.MathUtils;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

import static com.qiuyue.goetyominous.common.entities.ally.lm.IAnimatedMonsterServant.ATTACK_STATE;

public class HauntedGuardServant extends IAnimatedMonsterServant {

    private static final EntityDataAccessor<Integer> TEXTURE_VARIANT =
            SynchedEntityData.defineId(HauntedGuardServant.class, EntityDataSerializers.INT);

    public static final int DEFAULT_VARIANT = 0;
    public static final int GOLDEN_VARIANT = 1;
    public static final int DIAMOND_VARIANT = 2;

    public final AnimationState DeathAnimationState = new AnimationState();
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState uppercutAnimationState = new AnimationState();
    public final AnimationState slamAnimationState = new AnimationState();
    public final AnimationState combo2AnimationState = new AnimationState();
    public final AnimationState slashAnimationState = new AnimationState();
    public final AnimationState sitAnimationState = new AnimationState();
    public final AnimationState guardAnimationState = new AnimationState();
    public final AnimationState awakenAnimationState = new AnimationState();

    public float getR;
    public float getG;
    public float getB;
    public double lastTargetX;
    public double lastTargetY;
    public double lastTargetZ;
    public int deathAnimTicks;

    public HauntedGuardServant(EntityType<? extends HauntedGuardServant> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 5;
        this.setPersistenceRequired();
        this.saveTargetPos(this.getX(), this.getY(), this.getZ());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, LmConfig.HauntedGuardServantHealth.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.7)
                .add(Attributes.FOLLOW_RANGE, 30.0)
                .add(Attributes.ARMOR, LmConfig.HauntedGuardServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, LmConfig.HauntedGuardServantArmorToughness.get())
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_KNOCKBACK, 1.3)
                .add(Attributes.ATTACK_DAMAGE, LmConfig.HauntedGuardServantDamage.get());
    }

    @Override
    public double damageMultiplier() {
        return LmConfig.HauntedGuardServantDamageMultiplier.get();
    }

    @Override
    public double getFollowSpeed() {
        return 1.0D;
    }

    @Override
    public double getCommandSpeed() {
        return 1.0D;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
                                        MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData,
                                        @Nullable CompoundTag pDataTag) {
        if (pReason == MobSpawnType.MOB_SUMMONED && this.getTrueOwner() instanceof Player player) {
            if (countServants(player) >= LmConfig.HauntedGuardServantLimit.get()) {
                return null;
            }
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof HauntedGuardServant servant && servant.getTrueOwner() == player) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(6, new Summoned.WanderGoal<>(this, this.getFollowSpeed()));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 3.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(2, new IMoveGoal(this, false, 1.0D));
        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 2, 0, MathUtils.toTicks(2.67F), 15, 4.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && HauntedGuardServant.this.getRandom().nextFloat() * 35.0F < 16.0F
                        && HauntedGuardServant.this.getTarget() != null;
            }
        });
        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 3, 0, 90, 55, 4.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && HauntedGuardServant.this.getRandom().nextFloat() * 35.0F < 16.0F
                        && HauntedGuardServant.this.getTarget() != null;
            }
        });
        this.goalSelector.addGoal(1, new IStateGoal(this, 4, 4, 0, 0, 0) {
            @Override
            public void tick() {
                this.entity.setDeltaMovement(0.0D, this.entity.getDeltaMovement().y, 0.0D);
            }
        });
        this.goalSelector.addGoal(0, new IAttackGoal(this, 4, 5, 0, 15, 15, 10.0F));
    }

    @Override
    public void tick() {
        if (this.level().isClientSide) {
            this.idleAnimationState.animateWhen(this.getAttackState() == 0, this.tickCount);
        }
        this.updateWithAttack();
        if (!this.level().isClientSide && this.getAttackState() == 0 && this.isStandby()) {
            this.setSleep(true);
        }
        super.tick();
    }

    private boolean isStandby() {
        return this.isStaying() && !this.isCommanded() && this.getTarget() == null;
    }

    public void setSleep(boolean sleep) {
        this.setAttackState(sleep ? 4 : 0);
    }

    public boolean isSleep() {
        return this.getAttackState() == 4 || this.getAttackState() == 5;
    }

    @Override
    public boolean addEffect(MobEffectInstance pEffectInstance, @Nullable Entity pEntity) {
        if (this.isSleep()) {
            return false;
        }
        return super.addEffect(pEffectInstance, pEntity);
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return !this.isSleep() && super.canBeSeenAsEnemy();
    }

    @Override
    public boolean isAlliedTo(Entity pEntity) {
        if (super.isAlliedTo(pEntity)) {
            return true;
        }
        if (pEntity == null) {
            return false;
        }
        LivingEntity owner = this.getOwner();
        if (owner != null) {
            if (pEntity == owner) {
                return true;
            }
            if (pEntity instanceof OwnableEntity other && other.getOwner() == owner) {
                return true;
            }
            return owner.isAlliedTo(pEntity);
        }
        return false;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TEXTURE_VARIANT, DEFAULT_VARIANT);
    }

    public int getTextureVariant() {
        return this.entityData.get(TEXTURE_VARIANT);
    }

    public void setTextureVariant(int variant) {
        this.entityData.set(TEXTURE_VARIANT, variant);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("is_Sleep", this.isSleep());
        compound.putInt("texture", this.getTextureVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setSleep(compound.getBoolean("is_Sleep"));
        this.setTextureVariant(compound.getInt("texture"));
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (this.getTrueOwner() == pPlayer && this.tryRestyle(pPlayer, pHand, itemstack)) {
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(pPlayer, pHand);
    }

    private boolean tryRestyle(Player pPlayer, InteractionHand pHand, ItemStack itemstack) {
        int variant;
        SoundEvent sound;
        ParticleOptions particle;
        if (itemstack.is(Items.GOLD_INGOT)) {
            variant = GOLDEN_VARIANT;
            sound = SoundEvents.ARMOR_EQUIP_GOLD;
            particle = ParticleTypes.WAX_ON;
        } else if (itemstack.is(Items.DIAMOND)) {
            variant = DIAMOND_VARIANT;
            sound = SoundEvents.ARMOR_EQUIP_DIAMOND;
            particle = ParticleTypes.ELECTRIC_SPARK;
        } else if (itemstack.is(Items.IRON_INGOT)) {
            variant = DEFAULT_VARIANT;
            sound = SoundEvents.ARMOR_EQUIP_IRON;
            particle = ParticleTypes.WAX_OFF;
        } else {
            return false;
        }

        if (this.getTextureVariant() == variant) {
            return false;
        }

        if (!this.level().isClientSide) {
            if (!pPlayer.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            this.setTextureVariant(variant);
            this.playSound(sound, 1.0F, 1.0F);
            this.gameEvent(GameEvent.ENTITY_INTERACT, pPlayer);
            if (this.level() instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 12; ++i) {
                    double d0 = this.random.nextGaussian() * 0.02D;
                    double d1 = this.random.nextGaussian() * 0.02D;
                    double d2 = this.random.nextGaussian() * 0.02D;
                    serverLevel.sendParticles(particle,
                            this.getRandomX(1.0D), this.getY() + this.getBbHeight() + 0.3F, this.getRandomZ(1.0D),
                            0, d0, d1, d2, 0.5F);
                }
            }
        }
        pPlayer.swing(pHand);
        return true;
    }

    public void saveTargetPos(double x, double y, double z) {
        this.lastTargetX = x;
        this.lastTargetY = y;
        this.lastTargetZ = z;
    }

    public Vec3 lastTargetPos() {
        return new Vec3(this.lastTargetX, this.lastTargetY, this.lastTargetZ);
    }

    public void calculatedDashToPositon(float multiplier, Vec3 position) {
        if (this.getTarget() != null) {
            this.setDeltaMovement((position.x - this.getX()) * multiplier, 0, (position.z - this.getZ()) * multiplier);
        }
    }

    public void setRGB(float r, float g, float b) {
        this.getR = r / 255.0F;
        this.getG = g / 255.0F;
        this.getB = b / 255.0F;
    }

    public void updateWithAttack() {
        switch (this.getTextureVariant()) {
            case 1 -> this.setRGB(234.0F, 238.0F, 87.0F);
            case 2 -> this.setRGB(161.0F, 251.0F, 232.0F);
            default -> this.setRGB(203.0F, 212.0F, 214.0F);
        }
        float sweepSize = 1.0F;
        float sweepRot = 20.0F;
        int slamAttack = 20;
        if (this.getAttackState() == 2) {
            if (this.attackTicks == slamAttack - 7 && this.targetIsNotNull()) {
                this.saveTargetPos(this.target().getX(), this.target().getY(), this.target().getZ());
            }
            if (this.attackTicks == slamAttack - 3) {
                this.calculatedDashToPositon(0.15F, this.lastTargetPos());
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
                float yaw = (float) Math.toRadians(-this.yBodyRot + 90.0F);
                float pitch = (float) Math.toRadians(-this.getXRot() + 180.0F);
                if (this.level().isClientSide) {
                    this.level().addParticle(new KnightSweepParticle.SweepData(sweepSize, yaw, pitch, this.getR, this.getG, this.getB),
                            this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
                }
            }
            if (this.attackTicks == slamAttack) {
                this.playSound(SoundEvents.DRAGON_FIREBALL_EXPLODE, 1.0F, 1.0F);
                this.straightLineAreaAttack(-0.5F, 3.0F, 3.25F, 100, 14.0F, true, 1.2F);
            }
        }
        int uppercutTick = MathUtils.toTicks(1.21F);
        int slashTick = MathUtils.toTicks(2.63F);
        if (this.getAttackState() == 3) {
            if (this.attackTicks == uppercutTick - 3) {
                this.createSweep(1.0F, 0.0F, 4.0F, false, sweepSize, sweepRot);
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
                this.calculatedDash(0.25F);
            }
            if (this.attackTicks == uppercutTick) {
                this.areaAttack(3.0F, 3.0F, 180.0F, 14.0F, 10);
            }
            if (this.attackTicks == slashTick - 3) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
                this.createSweep(1.0F, 0.0F, 2.0F, true, sweepSize, sweepRot);
                this.calculatedDash(0.25F);
            }
            if (this.attackTicks == slashTick) {
                this.areaAttack(3.0F, 3.0F, 180.0F, 14.0F, 10);
            }
        }
    }

    private void areaAttack(float range, float height, float arc, float damage, int maxExtraMobs) {
        int extraMobs = 0;
        for (LivingEntity entityHit : this.getEntityLivingBaseNearby(range, height, range, range)) {
            double dx = entityHit.getX() - this.getX();
            double dz = entityHit.getZ() - this.getZ();
            float entityHitAngle = (float) ((Math.atan2(dz, dx) * 57.29577951308232D - 90.0D) % 360.0D);
            float entityAttackingAngle = this.yBodyRot % 360.0F;
            if (entityHitAngle < 0.0F) {
                entityHitAngle += 360.0F;
            }
            if (entityAttackingAngle < 0.0F) {
                entityAttackingAngle += 360.0F;
            }
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            float entityHitDistance = (float) Math.sqrt(dx * dx + dz * dz);
            boolean inRange = entityHitDistance <= range;
            boolean inArc = entityRelativeAngle <= arc / 2.0F && entityRelativeAngle >= -arc / 2.0F
                    || entityRelativeAngle >= 360.0F - arc / 2.0F
                    || entityRelativeAngle <= -360.0F + arc / 2.0F;
            if (!inRange || !inArc) {
                continue;
            }
            if (ServantAllyUtil.areAllied(this, entityHit) || entityHit == this) {
                continue;
            }
            if (entityHit == this.getTarget()) {
                this.playSound(ModSounds.POSESSED_PALADIN_ATTACK3.get(), 1.0F, 1.0F);
                boolean hurt = entityHit.hurt(this.damageSources().mobAttack(this),
                        (float) (damage * this.damageMultiplier()));
                if (hurt) {
                    this.launch(entityHit, 0.25F, 0.2F);
                }
                continue;
            }
            if (extraMobs >= maxExtraMobs) {
                continue;
            }
            this.playSound(ModSounds.POSESSED_PALADIN_ATTACK3.get(), 1.0F, 1.0F);
            boolean hurt = entityHit.hurt(this.damageSources().mobAttack(this),
                    (float) (damage * this.damageMultiplier()));
            if (hurt) {
                this.launch(entityHit, 0.25F, 0.2F);
            }
            ++extraMobs;
        }
    }

    private void straightLineAreaAttack(float boxWidth, float yHeight, float range, int brokenShieldTicks,
                                        float damage, boolean launch, float launchPower) {
        double rad = Math.toRadians(this.getYRot() + 90.0F);
        double xRange = (double) range * Math.cos(rad);
        double zRange = (double) range * Math.sin(rad);
        AABB attackRange = this.getBoundingBox().inflate(boxWidth, yHeight, boxWidth).move(xRange, 0.0D, zRange);
        for (LivingEntity entityHit : this.level().getEntitiesOfClass(LivingEntity.class, attackRange)) {
            if (this.isAlliedTo(entityHit) || ServantAllyUtil.areAllied(this, entityHit) || entityHit == this) {
                continue;
            }
            boolean flag = entityHit.hurt(this.damageSources().mobAttack(this),
                    (float) (damage * this.damageMultiplier()));
            if (flag && launch) {
                this.launch(entityHit, launchPower, 0.2F);
            }
            if (entityHit instanceof Player player && player.isBlocking() && brokenShieldTicks > 0) {
                disableShield(player, brokenShieldTicks);
            }
        }
    }

    public void createSweep(float pos, float posOffset, float yHeight, boolean reverse, float scale, float rot) {
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180));
        double theta = (double) this.yBodyRot * (Math.PI / 180) + 1.5707963267948966D;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        double x = this.getX() + (double) pos * vecX + (double) (f * posOffset);
        double z = this.getZ() + (double) pos * vecZ + (double) (f1 * posOffset);
        if (this.level().isClientSide) {
            double d1 = this.getY() + (double) (this.getBbHeight() / 2.0F);
            float yaw = (float) Math.toRadians(-this.yBodyRot + (reverse ? rot : 180.0F));
            double lookX = -Math.cos(yaw);
            double lookZ = -Math.sin(yaw);
            float pitch = (float) (reverse ? -1 : 1) * (float) Math.atan2(yHeight, Math.sqrt(lookX * lookX + lookZ * lookZ));
            this.level().addParticle(new KnightSweepParticle.SweepData(this.getScale() * scale, yaw, pitch, this.getR, this.getG, this.getB),
                    x, d1, z, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (LmConfig.HauntedGuardServantProjectileImmunity.get()
                && source.getDirectEntity() instanceof AbstractArrow) {
            return false;
        }
        if (source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.IN_FIRE)) {
            return false;
        }
        return super.hurt(source, amount);
    }

    public AnimationState getAnimationState(String input) {
        if ("slash".equals(input)) {
            return this.slashAnimationState;
        }
        if ("idle".equals(input)) {
            return this.idleAnimationState;
        }
        if ("sit".equals(input)) {
            return this.sitAnimationState;
        }
        if ("uppercut".equals(input)) {
            return this.uppercutAnimationState;
        }
        if ("death".equals(input)) {
            return this.DeathAnimationState;
        }
        if ("awaken".equals(input)) {
            return this.awakenAnimationState;
        }
        if ("guard".equals(input)) {
            return this.guardAnimationState;
        }
        return new AnimationState();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (ATTACK_STATE.equals(accessor) && this.level().isClientSide) {
            switch (this.getAttackState()) {
                case 0 -> this.stopAllAnimationStates();
                case 1 -> {
                    this.stopAllAnimationStates();
                    this.idleAnimationState.startIfStopped(this.tickCount);
                }
                case 2 -> {
                    this.stopAllAnimationStates();
                    this.slamAnimationState.startIfStopped(this.tickCount);
                }
                case 3 -> {
                    this.stopAllAnimationStates();
                    this.uppercutAnimationState.startIfStopped(this.tickCount);
                }
                case 4 -> {
                    this.stopAllAnimationStates();
                    this.guardAnimationState.startIfStopped(this.tickCount);
                }
                case 5 -> {
                    this.stopAllAnimationStates();
                    this.awakenAnimationState.startIfStopped(this.tickCount);
                }
                case 7 -> {
                    this.stopAllAnimationStates();
                    this.DeathAnimationState.startIfStopped(this.tickCount);
                }
                default -> {
                }
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    public void stopAllAnimationStates() {
        this.idleAnimationState.stop();
        this.sitAnimationState.stop();
        this.DeathAnimationState.stop();
        this.slamAnimationState.stop();
        this.slashAnimationState.stop();
        this.uppercutAnimationState.stop();
        this.combo2AnimationState.stop();
        this.awakenAnimationState.stop();
        this.guardAnimationState.stop();
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        this.setAttackState(7);
        this.deathAnimTicks = 0;
    }

    @Override
    protected void tickDeath() {
        ++this.deathAnimTicks;
        if (this.deathAnimTicks == 60) {
            this.remove(Entity.RemovalReason.KILLED);
            this.gameEvent(GameEvent.ENTITY_DIE);
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pSource) {
        return ModSounds.LIVING_ARMOR_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.OVERGROWN_COLOSSUS_DEATH.get();
    }
}
