package com.qiuyue.goetyominus.common.entities.ally.am;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.github.alexthe666.alexsmobs.entity.ISemiAquatic;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILeaveWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;

import com.qiuyue.goetyominus.config.AttributesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MurmurServant extends Summoned implements ISemiAquatic {
    private static final EntityDataAccessor<Optional<UUID>> HEAD_UUID;
    private static final EntityDataAccessor<Integer> HEAD_ID;
    private boolean renderFakeHead = true;

    public MurmurServant(EntityType<? extends Owned> type, Level level) {
        super(type, level);
        this.xpReward = 10;
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.MurmurServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.MurmurServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.MurmurServantDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.MurmurServantKnockbackResistance.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.MurmurServantMovementSpeed.get());
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AnimalAILeaveWater(this));
        this.goalSelector.addGoal(2, new AnimalAIWanderRanged(this, 55, 1.0, 14, 7));
        this.targetSelector.addGoal(0, new HurtByTargetGoal(this, new Class[0]));
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return (SoundEvent)AMSoundRegistry.MURMUR_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return (SoundEvent)AMSoundRegistry.MURMUR_HURT.get();
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
    }

    public boolean isAlliedTo(Entity entity) {
        return this.getHeadUUID() != null && entity.getUUID().equals(this.getHeadUUID()) || super.isAlliedTo(entity);
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return dimensions.height * 1.2F;
    }

    protected float getWaterSlowDown() {
        return 0.9F;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HEAD_UUID, Optional.empty());
        this.entityData.define(HEAD_ID, -1);
    }

    @Nullable
    public UUID getHeadUUID() {
        return (UUID)((Optional)this.entityData.get(HEAD_UUID)).orElse((Object)null);
    }

    public void setHeadUUID(@Nullable UUID uniqueId) {
        this.entityData.set(HEAD_UUID, Optional.ofNullable(uniqueId));
    }

    public Entity getHead() {
        if (!this.level().isClientSide) {
            UUID id = this.getHeadUUID();
            return id == null ? null : ((ServerLevel)this.level()).getEntity(id);
        } else {
            int id = (Integer)this.entityData.get(HEAD_ID);
            return id == -1 ? null : this.level().getEntity(id);
        }
    }

    public boolean shouldRenderFakeHead() {
        return this.renderFakeHead;
    }

    public void tick() {
        super.tick();
        if (this.renderFakeHead) {
            this.renderFakeHead = false;
        }

        this.yBodyRot = this.getYRot();
        this.yHeadRot = Mth.clamp(this.yHeadRot, this.yBodyRot - 70.0F, this.yBodyRot + 70.0F);
        if (!this.level().isClientSide) {
            Entity head = this.getHead();
            if (head == null) {
                LivingEntity created = this.createHead();
                this.setHeadUUID(created.getUUID());
                this.entityData.set(HEAD_ID, created.getId());
            }
        }

    }

    public Vec3 getNeckBottom(float partialTick) {
        double d0 = Mth.lerp((double)partialTick, this.xo, this.getX());
        double d1 = Mth.lerp((double)partialTick, this.yo, this.getY());
        double d2 = Mth.lerp((double)partialTick, this.zo, this.getZ());
        double height = (double)(this.getBbHeight() - 0.4F) + this.calculateWalkBounce(partialTick);
        Vec3 rotatedOnDeath = new Vec3(0.0, height, 0.0);
        if (this.deathTime > 0) {
            float f = ((float)this.deathTime + partialTick - 1.0F) / 20.0F * 1.6F;
            f = Mth.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }

            rotatedOnDeath = rotatedOnDeath.add((double)(f * 0.1F), (double)(f * 0.4F), 0.0).zRot((float)((double)f * Math.PI / 2.0)).yRot(-this.yBodyRot * 0.017453292F);
        }

        return (new Vec3(d0, d1, d2)).add(rotatedOnDeath);
    }

    public double calculateWalkBounce(float partialTick) {
        float limbSwingAmount = this.walkAnimation.speed(partialTick);
        float limbSwing = this.walkAnimation.position() - this.walkAnimation.speed() * (1.0F - partialTick);
        return Math.abs(Math.sin((double)(limbSwing * 0.9F)) * (double)limbSwingAmount * 0.25);
    }

    public boolean shouldEnterWater() {
        return false;
    }

    public boolean shouldLeaveWater() {
        return true;
    }

    public boolean shouldStopMoving() {
        return false;
    }

    public int getWaterSearchRange() {
        return 5;
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("HeadUUID")) {
            this.setHeadUUID(compound.getUUID("HeadUUID"));
        }

    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getHeadUUID() != null) {
            compound.putUUID("HeadUUID", this.getHeadUUID());
        }

    }

    private LivingEntity createHead() {
        MurmurServantHead head = new MurmurServantHead(this);
        this.level().addFreshEntity(head);
        return head;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(com.Polarice3.Goety.common.items.ModItems.ECTOPLASM.get())
                && this.getTrueOwner() == player && this.getHealth() < this.getMaxHealth()) {
            if (!this.level().isClientSide) {
                this.heal(2.0F);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                this.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 1.0F);
                ((ServerLevel) this.level()).sendParticles(ParticleTypes.HEART,
                        this.getX(), this.getY() + this.getBbHeight() / 2, this.getZ(),
                        5, 0.5, 0.5, 0.5, 0.0);
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    public boolean isAngry() {
        Entity entity = this.getHead();
        return entity instanceof MurmurServantHead ? ((MurmurServantHead)entity).isAngry() : false;
    }

    static {
        HEAD_UUID = SynchedEntityData.defineId(MurmurServant.class, EntityDataSerializers.OPTIONAL_UUID);
        HEAD_ID = SynchedEntityData.defineId(MurmurServant.class, EntityDataSerializers.INT);
    }
}