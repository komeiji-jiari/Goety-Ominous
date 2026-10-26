package com.qiuyue.goetyominous.common.entities.ally.lm;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IMoveGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IStateGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.knight.KnightParryFrameGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.knight.KnightParryGoal;
import com.qiuyue.goetyominous.compat.lm.LmScreenShake;
import com.qiuyue.goetyominous.utils.ServantAllyUtil;
import net.miauczel.legendary_monsters.Particle.custom.KnightSweepParticle;
import net.miauczel.legendary_monsters.effect.ModEffects;
import net.miauczel.legendary_monsters.sound.ModSounds;
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
import net.minecraft.world.effect.MobEffectInstance;
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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;
import com.qiuyue.goetyominous.config.lm.LmConfig;

public class HauntedKnightServant extends IAnimatedMonsterServant {

    private static final EntityDataAccessor<Integer> TEXTURE_VARIANT =
            SynchedEntityData.defineId(HauntedKnightServant.class, EntityDataSerializers.INT);

    public static final int DEFAULT_VARIANT = 0;
    public static final int GOLDEN_VARIANT = 1;
    public static final int DIAMOND_VARIANT = 2;

    public final AnimationState DeathAnimationState = new AnimationState();
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState slashAnimationState = new AnimationState();
    public final AnimationState sweepSpinAnimationState = new AnimationState();
    public final AnimationState parryAnimationState = new AnimationState();
    public final AnimationState parryFrameAnimationState = new AnimationState();
    public final AnimationState parryFailAnimationState = new AnimationState();
    public final AnimationState parrySuccessAnimationState = new AnimationState();

    public float getR;
    public float getG;
    public float getB;
    public boolean gambitParry = false;
    public int deathAnimTicks;

    public HauntedKnightServant(EntityType<? extends HauntedKnightServant> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 5;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, LmConfig.HauntedKnightServantHealth.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.7)
                .add(Attributes.FOLLOW_RANGE, 30.0)
                .add(Attributes.ARMOR, LmConfig.HauntedKnightServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, LmConfig.HauntedKnightServantArmorToughness.get())
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_KNOCKBACK, 0.7)
                .add(Attributes.ATTACK_DAMAGE, LmConfig.HauntedKnightServantDamage.get());
    }

    @Override
    public double damageMultiplier() {
        return LmConfig.HauntedKnightServantDamageMultiplier.get();
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
            if (countServants(player) >= LmConfig.HauntedKnightServantLimit.get()) {
                return null;
            }
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof HauntedKnightServant servant && servant.getTrueOwner() == player) {
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
        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 2, 0, 39, 20, 3.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && HauntedKnightServant.this.getRandom().nextFloat() * 100.0F < 28.0F
                        && HauntedKnightServant.this.getTarget() != null;
            }
        });
        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 3, 0, 65, 40, 3.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && HauntedKnightServant.this.getRandom().nextFloat() * 100.0F < 28.0F
                        && HauntedKnightServant.this.getTarget() != null;
            }
        });
        this.goalSelector.addGoal(1, new KnightParryGoal(this, 0, 4, 0, 8, 8, 3.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && HauntedKnightServant.this.getRandom().nextFloat() * 100.0F < 33.0F
                        && HauntedKnightServant.this.getTarget() != null;
            }
        });
        this.goalSelector.addGoal(1, new KnightParryFrameGoal(this, 5, 5, 0, 25, 25));
        this.goalSelector.addGoal(0, new IStateGoal(this, 7, 7, 0, 11, 11));
        this.goalSelector.addGoal(0, new IStateGoal(this, 8, 8, 0, 27, 27));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.idleAnimationState.animateWhen(this.getAttackState() == 0, this.tickCount);
        }
        this.updateWithAttack();
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
        compound.putInt("texture", this.getTextureVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
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
        float swordSize = 2.5F;
        float sweepSize = 1.0F;
        float sweepRot = 20.0F;
        float bigSweepHeight = 3.0F;
        int extraMobs = 1;
        if (this.getAttackState() == 2) {
            if (this.attackTicks == 10) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 2.0F);
            }
            if (this.attackTicks == 12) {
                float offset = -0.5F;
                float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180));
                float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180));
                double d0 = this.getX() + (double) (f * offset);
                double d1 = this.getY() + 1.5D;
                double d2 = this.getZ() + (double) (f1 * offset);
                float yaw = (float) Math.toRadians(-this.yBodyRot + 90.0F);
                float pitch = (float) Math.toRadians(-this.getXRot() + 180.0F);
                this.level().addParticle(new KnightSweepParticle.SweepData(sweepSize, yaw, pitch, this.getR, this.getG, this.getB),
                        d0, d1, d2, 0.0D, 0.0D, 0.0D);
                this.calculatedDash(0.25F);
            }
            if (this.attackTicks == 15) {
                this.areaAttack(swordSize, 3.0F, 100.0F, 8.0F, extraMobs);
            }
        }
        int attack1 = 14;
        int attack2 = 36;
        if (this.getAttackState() == 3) {
            if (this.attackTicks == attack1 - 3) {
                this.createSweep(1.0F, 0.0F, bigSweepHeight, true, sweepSize, sweepRot);
                this.calculatedDash(0.25F);
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 2.0F);
            }
            if (this.attackTicks == attack1) {
                this.areaAttack(swordSize, 3.0F, 100.0F, 8.0F, extraMobs);
            }
            if (this.attackTicks == attack2 - 3) {
                this.createSweep(1.0F, 0.0F, bigSweepHeight, true, sweepSize, sweepRot);
                this.calculatedDash(0.25F);
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 2.0F);
            }
            if (this.attackTicks == attack2) {
                this.areaAttack(swordSize, 3.0F, 100.0F, 8.0F, extraMobs);
            }
        }
        int parryTick = 8;
        if (this.getAttackState() == 8) {
            if (this.attackTicks == parryTick - 3) {
                this.createSweep(1.0F, 0.0F, bigSweepHeight, false, sweepSize, sweepRot);
                this.calculatedDash(0.25F);
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 2.0F);
            }
            if (this.attackTicks == parryTick) {
                this.areaAttack(swordSize, 3.0F, 180.0F, 13.0F, 4);
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
                this.playAttackSound();
                boolean hurt = entityHit.hurt(this.damageSources().mobAttack(this),
                        (float) (damage * this.damageMultiplier()));
                if (hurt && this.getAttackState() == 8) {
                    entityHit.addEffect(new MobEffectInstance(ModEffects.STUN.get(), 15, 0));
                }
                continue;
            }
            if (extraMobs >= maxExtraMobs) {
                continue;
            }
            this.playAttackSound();
            boolean hurt = entityHit.hurt(this.damageSources().mobAttack(this),
                    (float) (damage * this.damageMultiplier()));
            ++extraMobs;
            if (hurt && this.getAttackState() == 8) {
                entityHit.addEffect(new MobEffectInstance(ModEffects.STUN.get(), 15, 0));
            }
        }
    }

    private void playAttackSound() {
        if (this.getAttackState() == 8) {
            this.playSound(SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 1.0F, 0.75F);
        } else {
            this.playSound(ModSounds.POSESSED_PALADIN_ATTACK3.get(), 1.0F, 1.0F);
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

    public boolean canParry() {
        return !this.gambitParry
                && !this.level().isClientSide
                && (this.getAttackState() == 4 && this.attackTicks > 5 || this.getAttackState() == 5);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.getAttackState() == 2 && this.attackTicks <= 13) {
            this.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 1.0F);
            return false;
        }
        if (this.canParry()) {
            this.setAttackState(8);
            this.gambitParry = true;
            LmScreenShake.shake(this.level(), this.position(), 10.0F, 0.1F, 5, 5);
            this.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 1.0F);
            return false;
        }
        return super.hurt(source, amount);
    }

    public AnimationState getAnimationState(String input) {
        if (input.equals("slash")) {
            return this.slashAnimationState;
        }
        if (input.equals("idle")) {
            return this.idleAnimationState;
        }
        if (input.equals("sweep_spin")) {
            return this.sweepSpinAnimationState;
        }
        if (input.equals("parry")) {
            return this.parryAnimationState;
        }
        if (input.equals("parry_frame")) {
            return this.parryFrameAnimationState;
        }
        if (input.equals("parry_fail")) {
            return this.parryFailAnimationState;
        }
        if (input.equals("parry_success")) {
            return this.parrySuccessAnimationState;
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
                    this.slashAnimationState.startIfStopped(this.tickCount);
                }
                case 3 -> {
                    this.stopAllAnimationStates();
                    this.sweepSpinAnimationState.startIfStopped(this.tickCount);
                }
                case 4 -> {
                    this.stopAllAnimationStates();
                    this.parryAnimationState.startIfStopped(this.tickCount);
                }
                case 5 -> {
                    this.stopAllAnimationStates();
                    this.parryFrameAnimationState.startIfStopped(this.tickCount);
                }
                case 6 -> {
                    this.stopAllAnimationStates();
                    this.DeathAnimationState.startIfStopped(this.tickCount);
                }
                case 7 -> {
                    this.stopAllAnimationStates();
                    this.parryFailAnimationState.startIfStopped(this.tickCount);
                }
                case 8 -> {
                    this.stopAllAnimationStates();
                    this.parrySuccessAnimationState.startIfStopped(this.tickCount);
                }
                default -> {
                }
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    public void stopAllAnimationStates() {
        this.idleAnimationState.stop();
        this.DeathAnimationState.stop();
        this.slashAnimationState.stop();
        this.sweepSpinAnimationState.stop();
        this.parryFailAnimationState.stop();
        this.parryAnimationState.stop();
        this.parrySuccessAnimationState.stop();
        this.parryFrameAnimationState.stop();
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        this.deathAnimTicks = 0;
        this.setAttackState(6);
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
