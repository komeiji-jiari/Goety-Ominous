package com.qiuyue.goetyominous.common.entities.ally.am;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.qiuyue.goetyominous.common.entities.projectile.EntityServentSandShot;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class GusterServant extends Summoned {

    private static final EntityDataAccessor<Integer> LIFT_ENTITY = SynchedEntityData.defineId(GusterServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(GusterServant.class, EntityDataSerializers.INT);
    private int liftingTime = 0;
    private int maxLiftTime = 40;
    private int shootingTicks;

    public GusterServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.setMaxUpStep(1.1F);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.GusterServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.GusterServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.GusterServantDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.GusterServantKnockbackResistance.get())
                .add(Attributes.MOVEMENT_SPEED, 0.2D);
    }

    @Override
    public void setConfigurableAttributes() {
        if (this.getAttribute(Attributes.MAX_HEALTH) != null) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(AttributesConfig.GusterServantHealth.get());
        }
        if (this.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(AttributesConfig.GusterServantDamage.get());
        }
        if (this.getAttribute(Attributes.FOLLOW_RANGE) != null) {
            this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(AttributesConfig.GusterServantFollowRange.get());
        }
        if (this.getAttribute(Attributes.KNOCKBACK_RESISTANCE) != null) {
            this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(AttributesConfig.GusterServantKnockbackResistance.get());
        }
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return MobsConfig.GusterServantLimit.get();
    }

    public int getAmbientSoundInterval() {
        return 80;
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.GUSTER_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.GUSTER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.GUSTER_HURT.get();
    }

    public boolean isSensitiveToWater() {
        return true;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeGoal());
        this.goalSelector.addGoal(2, new AnimalAIWanderRanged(this, 60, 1.0D, 10, 7));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new GroundPathNavigatorWide(this, worldIn);
    }

    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (this.getTrueOwner() != null && CuriosFinder.hasWindCrown(this.getTrueOwner())) {
                this.setHasLifespan(false);
            } else if (this.getLifespan() > 0) {
                this.setHasLifespan(true);
            }
        }
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
    }

    public void doPush(Entity entityIn) {
        if (this.getLiftedEntity() == null && liftingTime >= 0 && !this.isPullExempt(entityIn)) {
            this.setLiftedEntity(entityIn.getId());
            maxLiftTime = 30 + random.nextInt(30);
        }
    }

    private boolean isPullExempt(Entity entity) {
        if (entity instanceof Player) {
            return true;
        }
        LivingEntity owner = null;
        if (entity instanceof OwnableEntity ownable) {
            owner = ownable.getOwner();
        } else if (entity instanceof IOwned owned) {
            owner = owned.getTrueOwner();
        }
        if (owner == null) {
            return false;
        }
        if (MobUtil.areAllies(this, owner)) {
            return true;
        }
        LivingEntity master = this.getTrueOwner();
        return master != null && MobUtil.areAllies(master, owner);
    }

    public boolean hasLiftedEntity() {
        return this.entityData.get(LIFT_ENTITY) != 0;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LIFT_ENTITY, 0);
        this.entityData.define(VARIANT, 0);
    }

    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            if (source.is(DamageTypeTags.IS_PROJECTILE)) {
                amount = (amount + 1.0F) / 3.0F;
            }
            return super.hurt(source, amount);
        }
    }

    private void spit(LivingEntity target) {
        EntityServentSandShot sghot = new EntityServentSandShot(this.level(), this, true);
        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3333333333333333D) - sghot.getY();
        double d2 = target.getZ() - this.getZ();
        float f = Mth.sqrt((float) (d0 * d0 + d2 * d2)) * 0.35F;
        sghot.shoot(d0, d1 + (double) f, d2, 1F, 10.0F);
        sghot.setVariant(this.getVariant());
        if (!this.isSilent()) {
            this.gameEvent(GameEvent.PROJECTILE_SHOOT);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.SAND_BREAK, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
        }
        this.level().addFreshEntity(sghot);
    }

    public double getEyeY() {
        return this.getY() + 1.0F;
    }

    @Nullable
    public Entity getLiftedEntity() {
        if (!this.hasLiftedEntity()) {
            return null;
        } else {
            return this.level().getEntity(this.entityData.get(LIFT_ENTITY));
        }
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        if (reason == MobSpawnType.SPAWN_EGG) {
            this.setHasLifespan(false);
            this.setLifespan(0);
        }
        if (this.isBiomeNether(worldIn, this.blockPosition())) {
            this.setVariant(2);
        } else if (this.isBiomeRed(worldIn, this.blockPosition())) {
            this.setVariant(1);
        } else {
            this.setVariant(0);
        }
        this.setAirSupply(this.getMaxAirSupply());
        this.setXRot(0.0F);
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    private void setLiftedEntity(int id) {
        this.entityData.set(LIFT_ENTITY, id);
    }

    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, variant);
    }

    public void aiStep() {
        super.aiStep();
        Entity lifted = this.getLiftedEntity();
        if (lifted == null && this.hasLiftedEntity()) {
            this.setLiftedEntity(0);
        }
        if (lifted == null && !this.level().isClientSide && tickCount % 15 == 0) {
            List<ItemEntity> list = this.level().getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(0.8F));
            ItemEntity closestItem = null;
            for (int i = 0; i < list.size(); ++i) {
                ItemEntity entity = list.get(i);
                if (entity.onGround() && (closestItem == null || this.distanceTo(closestItem) > this.distanceTo(entity))) {
                    closestItem = entity;
                }
            }
            if (closestItem != null) {
                this.setLiftedEntity(closestItem.getId());
                maxLiftTime = 30 + random.nextInt(30);
            }
        }
        float f = (float) this.getY();
        if (this.isAlive()) {
            ParticleOptions type = this.getVariant() == 2 ? AMParticleRegistry.GUSTER_SAND_SPIN_SOUL.get() : this.getVariant() == 1 ? AMParticleRegistry.GUSTER_SAND_SPIN_RED.get() : AMParticleRegistry.GUSTER_SAND_SPIN.get();
            for (int j = 0; j < 4; ++j) {
                float f1 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getBbWidth() * 0.95F;
                float f2 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getBbWidth() * 0.95F;
                this.level().addParticle(type, this.getX() + (double) f1, f, this.getZ() + (double) f2, this.getX(), this.getY() + random.nextFloat() * this.getBbHeight() + 0.2F, this.getZ());
            }
        }
        if (lifted != null && liftingTime >= 0) {
            liftingTime++;
            float resist = 1F;
            if (lifted instanceof LivingEntity) {
                resist = (float) Mth.clamp((1.0D - ((LivingEntity) lifted).getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)), 0, 1);
            }
            float radius = 1F + (liftingTime * 0.05F);
            if (lifted instanceof ItemEntity) {
                radius = 0.2F + (liftingTime * 0.025F);
            }
            float angle = liftingTime * -0.25F;
            double extraX = this.getX() + radius * Mth.sin(Mth.PI + angle);
            double extraZ = this.getZ() + radius * Mth.cos(angle);
            double d0 = (extraX - lifted.getX()) * resist;
            double d1 = (extraZ - lifted.getZ()) * resist;
            lifted.setDeltaMovement(d0, 0.1 * resist, d1);
            lifted.hasImpulse = true;
            if (liftingTime > maxLiftTime) {
                this.setLiftedEntity(0);
                liftingTime = -20;
                maxLiftTime = 30 + random.nextInt(30);
            }
        } else if (liftingTime < 0) {
            liftingTime++;
        } else if (this.getTarget() != null && this.distanceTo(this.getTarget()) < this.getBbWidth() + 1F && !this.isPullExempt(this.getTarget())) {
            this.setLiftedEntity(this.getTarget().getId());
            maxLiftTime = 30 + random.nextInt(30);
        }
        if (!this.level().isClientSide && shootingTicks >= 0) {
            if (shootingTicks <= 0) {
                if (this.getTarget() != null && (lifted == null || lifted.getId() != this.getTarget().getId()) && this.isAlive()) {
                    this.spit(this.getTarget());
                }
                shootingTicks = 40 + random.nextInt(40);
            } else {
                shootingTicks--;
            }
        }
        Vec3 vector3d = this.getDeltaMovement();
        if (!this.onGround() && vector3d.y < 0.0D) {
            this.setDeltaMovement(vector3d.multiply(1.0D, 0.6D, 1.0D));
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this.getTrueOwner() == player && this.getHealth() < this.getMaxHealth()) {
            if (stack.is(Items.SAND) || stack.is(Items.RED_SAND) || stack.is(Items.SOUL_SAND)) {
                if (!this.level().isClientSide) {
                    this.heal(2.0F);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    this.playSound(SoundEvents.SAND_BREAK, 1.0F, 1.0F);
                    ((ServerLevel) this.level()).sendParticles(ParticleTypes.HEART,
                            this.getX(), this.getY() + this.getBbHeight() / 2, this.getZ(),
                            5, 0.5, 0.5, 0.5, 0.0);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    public boolean isGooglyEyes() {
        String s = ChatFormatting.stripFormatting(this.getName().getString());
        return s != null && s.toLowerCase().contains("tweester");
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Variant", this.getVariant());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setVariant(compound.getInt("Variant"));
    }

    private static boolean isBiomeRed(LevelAccessor worldIn, BlockPos position) {
        return worldIn.getBiome(position).is(AMTagRegistry.SPAWNS_RED_GUSTERS);
    }

    private static boolean isBiomeNether(LevelAccessor worldIn, BlockPos position) {
        return worldIn.getBiome(position).is(AMTagRegistry.SPAWNS_SOUL_GUSTERS);
    }

    private class MeleeGoal extends Goal {

        public MeleeGoal() {
        }

        public boolean canUse() {
            return GusterServant.this.getTarget() != null;
        }

        public void tick() {
            Entity thrownEntity = GusterServant.this.getLiftedEntity();

            if (GusterServant.this.getTarget() != null) {
                if (thrownEntity != null && thrownEntity.getId() == GusterServant.this.getTarget().getId()) {
                    GusterServant.this.getNavigation().stop();
                } else {
                    GusterServant.this.getNavigation().moveTo(GusterServant.this.getTarget(), 1.25F);
                }
            }
        }
    }
}
