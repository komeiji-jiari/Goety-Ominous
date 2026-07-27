package com.qiuyue.goetyominus.common.entities.ally.sar;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.qiuyue.goetyominus.config.AttributesConfig;
import com.teamabnormals.savage_and_ravage.core.registry.SRSoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.HitResult;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.EnumSet;

public class CreepieServant extends Summoned implements PowerableMob {
    private static final EntityDataAccessor<Integer> DATA_SWELL_DIR = SynchedEntityData.defineId(CreepieServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_IS_POWERED = SynchedEntityData.defineId(CreepieServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_IGNITED = SynchedEntityData.defineId(CreepieServant.class, EntityDataSerializers.BOOLEAN);

    private int oldSwell;
    private int swell;
    private int maxSwell = 30;
    private float explosionRadius = 1.2F;

    public CreepieServant(EntityType<? extends Summoned> type, Level worldIn) {
        super(type, worldIn);
        this.xpReward = 0;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SWELL_DIR, -1);
        this.entityData.define(DATA_IS_POWERED, false);
        this.entityData.define(DATA_IS_IGNITED, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new CreeperSwellGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new AvoidEntityGoal<>(this, Cat.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(5, new AvoidEntityGoal<>(this, Ocelot.class, 6.0F, 1.0D, 1.2D));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));

        super.registerGoals();
    }

    class CreeperSwellGoal extends Goal {
        private final CreepieServant creeper;

        public CreeperSwellGoal(CreepieServant creeper) {
            this.creeper = creeper;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = this.creeper.getTarget();
            return livingentity != null && this.creeper.distanceToSqr(livingentity) < 4.0D;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity livingentity = this.creeper.getTarget();
            return livingentity != null && this.creeper.distanceToSqr(livingentity) < 6.25D;
        }

        @Override
        public void start() {
            this.creeper.setSwellDir(1);
        }

        @Override
        public void stop() {
            this.creeper.setSwellDir(-1);
        }
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.CreepieServantHealth.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.CreepieServantDamage.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.CreepieServantMovementSpeed.get());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.entityData.get(DATA_IS_POWERED)) {
            compound.putBoolean("powered", true);
        }
        compound.putShort("Fuse", (short) this.maxSwell);
        compound.putFloat("ExplosionRadius", this.explosionRadius);
        compound.putBoolean("ignited", this.entityData.get(DATA_IS_IGNITED));
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(DATA_IS_POWERED, compound.getBoolean("powered"));
        if (compound.contains("Fuse", 99)) {
            this.maxSwell = compound.getShort("Fuse");
        }
        if (compound.contains("ExplosionRadius", 99)) {
            this.explosionRadius = compound.getFloat("ExplosionRadius");
        }
        if (compound.contains("ignited", 99)) {
            this.entityData.set(DATA_IS_IGNITED, compound.getBoolean("ignited"));
        }
    }

    @Override
    public int getMaxFallDistance() {
        return this.getTarget() == null ? 3 : 3 + (int) (this.getHealth() - 1.0F);
    }

    @Override
    public void tick() {
        if (this.isAlive()) {
            this.oldSwell = this.swell;

            if (this.hasIgnited()) {
                this.setSwellDir(1);
            }

            int i = this.getSwellDir();
            this.swell += i;
            if (this.swell < 0) {
                this.swell = 0;
            }

            if (this.swell >= this.maxSwell - 10 && this.swell < this.maxSwell - 5) {
                if (!this.level().isClientSide) {
                    this.playSound(SRSoundEvents.CREEPIE_PRIMED.get(), this.getSoundVolume(), this.getVoicePitch());
                }
            }

            if (this.swell >= this.maxSwell) {
                this.swell = this.maxSwell;
                this.explodeCreeper();
            }
        }
        super.tick();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRSoundEvents.CREEPIE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SRSoundEvents.CREEPIE_DEATH.get();
    }

    public float getSwelling(float partialTicks) {
        return Mth.lerp(partialTicks, (float) this.oldSwell, (float) this.swell) / (float) (this.maxSwell - 2);
    }

    public int getSwellDir() {
        return this.entityData.get(DATA_SWELL_DIR);
    }

    public void setSwellDir(int state) {
        this.entityData.set(DATA_SWELL_DIR, state);
    }

    public boolean isPowered() {
        return this.entityData.get(DATA_IS_POWERED);
    }

    public void setCharged(boolean charged) {
        this.entityData.set(DATA_IS_POWERED, charged);
    }

    public boolean hasIgnited() {
        return this.entityData.get(DATA_IS_IGNITED);
    }

    public void ignite() {
        this.entityData.set(DATA_IS_IGNITED, true);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.getItem() == Items.FLINT_AND_STEEL) {
            this.level().playSound(player, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.FLINTANDSTEEL_USE, this.getSoundSource(), 1.0F,
                    this.random.nextFloat() * 0.4F + 0.8F);
            if (!this.level().isClientSide()) {
                this.ignite();
                itemstack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
            }

            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        boolean flag = super.causeFallDamage(distance, damageMultiplier, source);
        this.swell = (int) ((float) this.swell + distance * 1.5F);
        if (this.swell > this.maxSwell - 5) {
            this.swell = this.maxSwell - 5;
        }

        return flag;
    }

    public float getVoicePitch() {
        return (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.5F;
    }

    public ItemStack getPickedResult(HitResult target) {
        return new ItemStack(Items.CREEPER_SPAWN_EGG);
    }

    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return sizeIn.height * 0.8F;
    }

    private boolean shouldExplosionDestroyBlocks() {
        try {
            Class<?> srConfigClass = Class.forName("com.teamabnormals.savage_and_ravage.core.SRConfig");
            Object commonInstance = srConfigClass.getDeclaredField("COMMON").get(null);

            java.lang.reflect.Field configField = commonInstance.getClass()
                    .getDeclaredField("creepieExplosionsDestroyBlocks");
            Object configValue = configField.get(commonInstance);

            Method getMethod = configValue.getClass().getMethod("get");
            return (Boolean) getMethod.invoke(configValue);
        } catch (Exception e) {
            return true;
        }
    }

    private void explodeCreeper() {
        if (!this.level().isClientSide()) {
            float chargedModifier = this.isPowered() ? 2.0F : 1.0F;
            this.dead = true;
            ExplosionInteraction interaction = shouldExplosionDestroyBlocks() ?
                    ExplosionInteraction.MOB : ExplosionInteraction.NONE;
            this.level().explode(this, this.getX(), this.getY(), this.getZ(),
                    this.explosionRadius * chargedModifier, interaction);
            this.discard();
            this.spawnLingeringCloud();
        }
    }

    protected void spawnLingeringCloud() {
        Collection<MobEffectInstance> collection = this.getActiveEffects();
        if (!collection.isEmpty()) {
            AreaEffectCloud areaeffectcloudentity = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
            areaeffectcloudentity.setRadius(1.0F);
            areaeffectcloudentity.setRadiusOnUse(-0.5F);
            areaeffectcloudentity.setWaitTime(10);
            areaeffectcloudentity.setDuration(areaeffectcloudentity.getDuration() / 2);
            areaeffectcloudentity.setRadiusPerTick(-areaeffectcloudentity.getRadius() / (float) areaeffectcloudentity.getDuration());

            for (MobEffectInstance effectinstance : collection) {
                areaeffectcloudentity.addEffect(new MobEffectInstance(effectinstance));
            }

            this.level().addFreshEntity(areaeffectcloudentity);
        }
    }

    @Override
    public boolean doHurtTarget(Entity entityIn) {
        return true;
    }

    public float getCreeperFlashIntensity(float partialTicks) {
        return this.getSwelling(partialTicks);
    }
}
