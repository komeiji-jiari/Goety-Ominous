package com.qiuyue.goetyominous.common.entities.ally.am;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.qiuyue.goetyominous.common.init.am.AmEntityRegistry;
import com.qiuyue.goetyominous.config.AttributesConfig;
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
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class ServantCentipedeHead extends Summoned {
    private static final EntityDataAccessor<Optional<UUID>> CHILD_UUID;
    private static final EntityDataAccessor<Integer> CHILD_ID;
    private static final EntityDataAccessor<Integer> SEGMENT_COUNT;
    public final float[] ringBuffer = new float[64];
    public int ringBufferIndex = -1;
    private ServantCentipedeBody[] parts;

    public ServantCentipedeHead(EntityType type, Level worldIn) {
        super(type, worldIn);
        this.xpReward = 13;
        this.setMaxUpStep(3.0F);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.ServantCentipedeHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.ServantCentipedeFollowRange.get())
                .add(Attributes.ARMOR, AttributesConfig.ServantCentipedeArmor.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.ServantCentipedeAttackDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.ServantCentipedeKnockbackResistance.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.ServantCentipedeMovementSpeed.get());
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.4, false));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0, 13, false));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, EntityCockroach.class, 45, true, true, (Predicate)null));
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return (SoundEvent) AMSoundRegistry.CENTIPEDE_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return (SoundEvent)AMSoundRegistry.CENTIPEDE_HURT.get();
    }

    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound((SoundEvent)AMSoundRegistry.CENTIPEDE_WALK.get(), 1.0F, 1.0F);
    }

    public int getMaxHeadXRot() {
        return 1;
    }

    public int getMaxHeadYRot() {
        return 1;
    }

    public int getHeadRotSpeed() {
        return 1;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CHILD_UUID, Optional.empty());
        this.entityData.define(CHILD_ID, -1);
        this.entityData.define(SEGMENT_COUNT, 5);
    }

    public boolean doHurtTarget(Entity entityIn) {
        if (super.doHurtTarget(entityIn)) {
            if (entityIn instanceof LivingEntity living) {
                if (this.getTrueOwner() != null && CuriosFinder.hasWildSet(this.getTrueOwner())) {
                    living.addEffect(new MobEffectInstance(GoetyEffects.ACID_VENOM.get(), 20 * 20, 1));
                } else {
                    living.addEffect(new MobEffectInstance(MobEffects.POISON, 20 * 20, 1));
                }
            }
            this.playSound(AMSoundRegistry.CENTIPEDE_ATTACK.get(), this.getSoundVolume(), this.getVoicePitch());
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            return true;
        }
        return false;
    }

    public int getSegmentCount() {
        return Math.max((Integer)this.entityData.get(SEGMENT_COUNT), 1);
    }

    public void setSegmentCount(int segments) {
        this.entityData.set(SEGMENT_COUNT, segments);
    }

    @Nullable
    public UUID getChildId() {
        return (UUID)((Optional)this.entityData.get(CHILD_UUID)).orElse((Object)null);
    }

    public void setChildId(@Nullable UUID uniqueId) {
        this.entityData.set(CHILD_UUID, Optional.ofNullable(uniqueId));
    }

    public Entity getChild() {
        UUID id = this.getChildId();
        return id != null && !this.level().isClientSide ? ((ServerLevel)this.level()).getEntity(id) : null;
    }

    public void pushEntities() {
        List<Entity> entities = this.level().getEntities(this, this.getBoundingBox().expandTowards(0.2, 0.0, 0.2));
        entities.stream().filter((entity) -> {
            return !(entity instanceof ServantCentipedeBody) && entity.isPushable();
        }).forEach((entity) -> {
            entity.push(this);
        });
    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, SpawnGroupData spawnDataIn, CompoundTag dataTag) {
        this.setSegmentCount(this.random.nextInt(4) + 5);
        if (this.overSummonLimit()) {
            this.discard();
            return null;
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    private boolean overSummonLimit() {
        if (this.level().isClientSide || this.getTrueOwner() == null) {
            return false;
        }
        int count = 0;
        for (Entity entity : ((ServerLevel) this.level()).getAllEntities()) {
            if (entity instanceof ServantCentipedeHead head
                    && head.getTrueOwner() == this.getTrueOwner() && head.isAlive()) {
                if (++count >= (Integer) com.qiuyue.goetyominous.config.MobsConfig.CentipedeLimit.get()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getChildId() != null) {
            compound.putUUID("ChildUUID", this.getChildId());
        }

        compound.putInt("SegCount", this.getSegmentCount());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("ChildUUID")) {
            this.setChildId(compound.getUUID("ChildUUID"));
        }

        this.setSegmentCount(compound.getInt("SegCount"));
    }

    private boolean shouldReplaceParts() {
        if (this.parts != null && this.parts[0] != null && this.parts.length == this.getSegmentCount()) {
            for(int i = 0; i < this.getSegmentCount(); ++i) {
                if (this.parts[i] == null) {
                    return true;
                }
            }

            return false;
        } else {
            return true;
        }
    }

    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(source);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(com.github.alexthe666.alexsmobs.item.AMItemRegistry.COCKROACH_WING_FRAGMENT.get())
                && this.getTrueOwner() == player) {
            if (!this.level().isClientSide && this.getHealth() < this.getMaxHealth()) {
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

    public void tick() {
        super.tick();
        this.isInsidePortal = false;
        this.yBodyRot = Mth.clamp(this.getYRot(), this.yBodyRot - 2.0F, this.yBodyRot + 2.0F);
        this.yHeadRot = this.yBodyRot;
        if (this.ringBufferIndex < 0) {
            Arrays.fill(this.ringBuffer, this.yBodyRot);
        }

        if (this.updateRingBuffer() || this.ringBufferIndex < 0) {
            ++this.ringBufferIndex;
        }

        if (this.ringBufferIndex == this.ringBuffer.length) {
            this.ringBufferIndex = 0;
        }

        this.ringBuffer[this.ringBufferIndex] = this.getYRot();
        if (!this.level().isClientSide) {
            Entity child = this.getChild();
            float backOffset;
            int i;
            if (child == null) {
                LivingEntity partParent = this;
                this.parts = new ServantCentipedeBody[this.getSegmentCount()];
                Vec3 prevPos = this.position();
                backOffset = 0.45F;

                for(i = 0; i < this.getSegmentCount(); ++i) {
                    ServantCentipedeBody part = this.createBody((LivingEntity)partParent, i == this.getSegmentCount() - 1);
                    part.setParent((Entity)partParent);
                    part.setBodyIndex(i);
                    if (partParent == this) {
                        this.setChildId(part.getUUID());
                        this.entityData.set(CHILD_ID, part.getId());
                    }

                    if (partParent instanceof ServantCentipedeBody) {
                        ServantCentipedeBody body = (ServantCentipedeBody)partParent;
                        body.setChildId(part.getUUID());
                    }

                    part.setPos(part.tickMultipartPosition(this.getId(), backOffset, prevPos, this.getXRot(), this.getYawForPart(i), false));
                    this.level().addFreshEntity(part);
                    this.parts[i] = part;
                    partParent = part;
                    backOffset = part.getBackOffset();
                    prevPos = part.position();
                }
            }

            if (this.tickCount > 1) {
                if (this.shouldReplaceParts() && this.getChild() instanceof ServantCentipedeBody) {
                    this.parts = new ServantCentipedeBody[this.getSegmentCount()];
                    this.parts[0] = (ServantCentipedeBody)this.getChild();
                    this.entityData.set(CHILD_ID, this.parts[0].getId());

                    for (int j = 1; j < this.parts.length && this.parts[j - 1].getChild() instanceof ServantCentipedeBody; ++j) {
                        this.parts[j] = (ServantCentipedeBody) this.parts[j - 1].getChild();
                    }
                }

                Vec3 prev = this.position();
                float xRot = this.getXRot();
                backOffset = 0.45F;

                for(i = 0; i < this.getSegmentCount(); ++i) {
                    if (this.parts[i] != null) {
                        float reqRot = this.getYawForPart(i);
                        prev = this.parts[i].tickMultipartPosition(this.getId(), backOffset, prev, xRot, reqRot, true);
                        xRot = this.parts[i].getXRot();
                        backOffset = this.parts[i].getBackOffset();
                    }
                }
            }
            if (this.tickCount % 5 == 0) {
                this.strongFollow();
            }
        }

    }

    private boolean updateRingBuffer() {
        return this.getDeltaMovement().lengthSqr() >= 0.005;
    }

    private void strongFollow() {
        if (this.isCommanded() || this.isStaying()) {
            return;
        }
        LivingEntity owner = this.getTrueOwner();
        if (owner == null || owner.isSpectator() || !owner.isAlive()) {
            return;
        }
        if (this.getTarget() != null) {
            return;
        }
        this.setWandering(false);
        if (this.distanceToSqr(owner) >= 100.0D) {
            this.getNavigation().moveTo(owner, 1.0D);
        }
    }

    public ServantCentipedeBody createBody(LivingEntity parent, boolean tail) {
        ServantCentipedeBody part = tail
                ? new ServantCentipedeTail((EntityType) AmEntityRegistry.SERVANT_CENTIPEDE_TAIL.get(), parent, 0.84F, 180.0F, 0.0F)
                : new ServantCentipedeBody((EntityType) AmEntityRegistry.SERVANT_CENTIPEDE_BODY.get(), parent, 0.84F, 180.0F, 0.0F);
        part.setTrueOwner(this.getTrueOwner());
        return part;
    }

    public boolean canBeLeashed(Player player) {
        return true;
    }

    private float getYawForPart(int i) {
        return this.getRingBuffer(4 + i * 4, 1.0F);
    }

    public float getRingBuffer(int bufferOffset, float partialTicks) {
        if (this.isDeadOrDying()) {
            partialTicks = 0.0F;
        }

        partialTicks = 1.0F - partialTicks;
        int i = this.ringBufferIndex - bufferOffset & 63;
        int j = this.ringBufferIndex - bufferOffset - 1 & 63;
        float d0 = this.ringBuffer[i];
        float d1 = this.ringBuffer[j] - d0;
        return Mth.wrapDegrees(d0 + d1 * partialTicks);
    }

    static {
        CHILD_UUID = SynchedEntityData.defineId(ServantCentipedeHead.class, EntityDataSerializers.OPTIONAL_UUID);
        CHILD_ID = SynchedEntityData.defineId(ServantCentipedeHead.class, EntityDataSerializers.INT);
        SEGMENT_COUNT = SynchedEntityData.defineId(ServantCentipedeHead.class, EntityDataSerializers.INT);
    }
}
