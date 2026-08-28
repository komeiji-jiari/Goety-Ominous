package com.qiuyue.goetyominous.common.entities.ally.am;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.IHurtableMultipart;
import com.github.alexthe666.alexsmobs.message.MessageHurtMultipart;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;

import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;

public class ServantCentipedeBody extends Summoned implements IHurtableMultipart {
    private static final EntityDataAccessor<Integer> BODYINDEX;
    private static final EntityDataAccessor<Float> BODY_XROT;
    private static final EntityDataAccessor<Optional<UUID>> PARENT_UUID;
    private static final EntityDataAccessor<Optional<UUID>> CHILD_UUID;
    public EntityDimensions multipartSize;
    protected float radius;
    protected float angleYaw;
    protected float damageMultiplier = 1.0F;
    private double prevHeight = 0.0;

    public ServantCentipedeBody(EntityType type, Level worldIn) {
        super(type, worldIn);
        this.multipartSize = type.getDimensions();
    }

    @Override
    protected void registerGoals() {
    }

    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.getParent() != null;
    }

    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(source);
    }

    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    public boolean isNoGravity() {
        return false;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(com.github.alexthe666.alexsmobs.item.AMItemRegistry.COCKROACH_WING_FRAGMENT.get())
                && this.getTrueOwner() == player) {
            if (!this.level().isClientSide) {
                ServantCentipedeHead head = this.getCentipedeHead();
                if (head != null && head.getHealth() < head.getMaxHealth()) {
                    head.heal(2.0F);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    this.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 1.0F);
                    ((ServerLevel) this.level()).sendParticles(ParticleTypes.HEART,
                            head.getX(), head.getY() + head.getBbHeight() / 2, head.getZ(),
                            5, 0.5, 0.5, 0.5, 0.0);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    private ServantCentipedeHead getCentipedeHead() {
        Entity parent = this.getParent();
        while (parent instanceof ServantCentipedeBody body) {
            parent = body.getParent();
        }
        return parent instanceof ServantCentipedeHead head ? head : null;
    }

    public void tick() {
        super.tick();
        this.isInsidePortal = false;
        this.setDeltaMovement(Vec3.ZERO);
        if (this.tickCount > 1) {
            Entity parent = this.getParent();
            this.refreshDimensions();
            if (parent != null && !this.level().isClientSide) {
                if (parent instanceof LivingEntity) {
                    LivingEntity parentEntity = (LivingEntity)parent;
                    if (parentEntity.hurtTime > 0 || parentEntity.deathTime > 0) {
                        AlexsMobs.sendMSGToAll(new MessageHurtMultipart(this.getId(), parent.getId(), 0.0F));
                        this.hurtTime = parentEntity.hurtTime;
                        this.deathTime = parentEntity.deathTime;
                    }
                }

                if (parent.isRemoved()) {
                    this.remove(RemovalReason.DISCARDED);
                }
            } else if (!this.level().isClientSide && this.tickCount > 20) {
                this.remove(RemovalReason.DISCARDED);
            }
        }

    }

    public ServantCentipedeBody(EntityType t, LivingEntity parent, float radius, float angleYaw, float offsetY) {
        super(t, parent.level());
        this.setParent(parent);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getParentId() != null) {
            compound.putUUID("ParentUUID", this.getParentId());
        }

        if (this.getChildId() != null) {
            compound.putUUID("ChildUUID", this.getChildId());
        }

        compound.putInt("BodyIndex", this.getBodyIndex());
        compound.putFloat("PartAngle", this.angleYaw);
        compound.putFloat("PartRadius", this.radius);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("ParentUUID")) {
            this.setParentId(compound.getUUID("ParentUUID"));
        }

        if (compound.hasUUID("ChildUUID")) {
            this.setChildId(compound.getUUID("ChildUUID"));
        }

        this.setBodyIndex(compound.getInt("BodyIndex"));
        this.angleYaw = compound.getFloat("PartAngle");
        this.radius = compound.getFloat("PartRadius");
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PARENT_UUID, Optional.empty());
        this.entityData.define(CHILD_UUID, Optional.empty());
        this.entityData.define(BODYINDEX, 0);
        this.entityData.define(BODY_XROT, 0.0F);
    }

    public Entity getParent() {
        UUID id = this.getParentId();
        return id != null && !this.level().isClientSide ? ((ServerLevel)this.level()).getEntity(id) : null;
    }

    public void setParent(Entity entity) {
        this.setParentId(entity.getUUID());
    }

    public Entity getChild() {
        UUID id = this.getChildId();
        return id != null && !this.level().isClientSide ? ((ServerLevel)this.level()).getEntity(id) : null;
    }

    @Nullable
    public UUID getChildId() {
        return (UUID)((Optional)this.entityData.get(CHILD_UUID)).orElse((Object)null);
    }

    public void setChildId(@Nullable UUID uniqueId) {
        this.entityData.set(CHILD_UUID, Optional.ofNullable(uniqueId));
    }

    public boolean is(Entity entity) {
        return this == entity || this.getParent() == entity;
    }

    public boolean hurt(DamageSource source, float damage) {
        Entity parent = this.getParent();
        boolean prev = parent != null && parent.hurt(source, damage * this.damageMultiplier);
        if (prev && !this.level().isClientSide) {
            AlexsMobs.sendMSGToAll(new MessageHurtMultipart(this.getId(), parent.getId(), damage * this.damageMultiplier));
        }

        return prev;
    }

    public boolean isPickable() {
        return true;
    }

    public void pushEntities() {
        List<Entity> entities = this.level().getEntities(this, this.getBoundingBox().expandTowards(0.2, 0.0, 0.2));
        Entity parent = this.getParent();
        if (parent != null) {
            entities.stream().filter((entity) -> {
                return entity != parent && !(entity instanceof ServantCentipedeBody) && entity.isPushable();
            }).forEach((entity) -> {
                entity.push(parent);
            });
        }

    }

    public boolean startRiding(Entity entityIn) {
        return !(entityIn instanceof AbstractMinecart) && !(entityIn instanceof Boat) ? super.startRiding(entityIn) : false;
    }

    public int getBodyIndex() {
        return (Integer)this.entityData.get(BODYINDEX);
    }

    public void setBodyIndex(int index) {
        this.entityData.set(BODYINDEX, index);
    }

    @Nullable
    public UUID getParentId() {
        return (UUID)((Optional)this.entityData.get(PARENT_UUID)).orElse((Object)null);
    }

    public void setParentId(@Nullable UUID uniqueId) {
        this.entityData.set(PARENT_UUID, Optional.ofNullable(uniqueId));
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.ServantCentipedeBodyHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.ServantCentipedeFollowRange.get())
                .add(Attributes.ARMOR, AttributesConfig.ServantCentipedeArmor.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.ServantCentipedeAttackDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.ServantCentipedeKnockbackResistance.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.ServantCentipedeBodyMovementSpeed.get());
    }

    public Vec3 tickMultipartPosition(int headId, float parentOffset, Vec3 parentPosition, float parentXRot, float ourYRot, boolean doHeight) {
        float yDif = doHeight ? 1.0F - 0.95F * (float)Math.min(Math.abs(parentPosition.y - this.getY()), 1.0) : 1.0F;
        Vec3 parentFront = parentPosition.add(this.calcOffsetVec(yDif * parentOffset * this.getScale(), parentXRot, ourYRot));
        Vec3 parentButt = parentPosition.add(this.calcOffsetVec(yDif * -parentOffset * this.getScale(), parentXRot, ourYRot));
        Vec3 ourButt = parentButt.add(this.calcOffsetVec((yDif * -this.getBackOffset() - 0.5F * this.getBbWidth()) * this.getScale(), this.getXRot(), ourYRot));
        Vec3 avg = new Vec3((parentButt.x + ourButt.x) / 2.0, (parentButt.y + ourButt.y) / 2.0, (parentButt.z + ourButt.z) / 2.0);
        double d0 = parentButt.x - ourButt.x;
        double d2 = parentButt.z - ourButt.z;
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        double hgt = doHeight ? this.getLowPartHeight(parentButt.x, parentButt.y, parentButt.z) + this.getHighPartHeight(ourButt.x, ourButt.y, ourButt.z) : 0.0;
        if (Math.abs(this.prevHeight - hgt) > 0.2) {
            this.prevHeight = hgt;
        }

        if (!this.isOpaqueBlockAt(parentFront.x, parentFront.y + 0.4000000059604645, parentFront.z) && Math.abs(this.prevHeight) > 1.0) {
            this.prevHeight = 0.0;
        }

        double partYDest = Mth.clamp(this.prevHeight, -0.4000000059604645, 0.4000000059604645);
        float f = (float)(Mth.atan2(d2, d0) * 57.2957763671875) - 90.0F;
        float rawAngle = Mth.wrapDegrees((float)(-(Mth.atan2(partYDest, d3) * 57.2957763671875)));
        float f2 = this.limitAngle(this.getXRot(), rawAngle, 10.0F);
        this.setXRot(f2);
        this.entityData.set(BODY_XROT, f2);
        this.setYRot(f);
        this.yHeadRot = f;
        this.moveTo(avg.x, avg.y, avg.z, f, f2);
        return avg;
    }

    public float getXRot() {
        return (Float)this.entityData.get(BODY_XROT);
    }

    public double getLowPartHeight(double x, double yIn, double z) {
        if (this.isFluidAt(x, yIn, z)) {
            return 0.0;
        } else {
            double checkAt;
            for(checkAt = 0.0; checkAt > -3.0 && !this.isOpaqueBlockAt(x, yIn + checkAt, z); checkAt -= 0.2) {
            }

            return checkAt;
        }
    }

    public double getHighPartHeight(double x, double yIn, double z) {
        if (this.isFluidAt(x, yIn, z)) {
            return 0.0;
        } else {
            double checkAt;
            for(checkAt = 0.0; checkAt <= 3.0 && this.isOpaqueBlockAt(x, yIn + checkAt, z); checkAt += 0.2) {
            }

            return checkAt;
        }
    }

    public boolean isFluidAt(double x, double y, double z) {
        if (this.noPhysics) {
            return false;
        } else {
            return !this.level().getFluidState(AMBlockPos.fromCoords(x, y, z)).isEmpty();
        }
    }

    public boolean isOpaqueBlockAt(double x, double y, double z) {
        if (this.noPhysics) {
            return false;
        } else {
            float f = 1.0F;
            Vec3 vec3 = new Vec3(x, y, z);
            AABB axisalignedbb = AABB.ofSize(vec3, 1.0, 1.0E-6, 1.0);
            return this.level().getBlockStates(axisalignedbb).filter(Predicate.not(BlockBehaviour.BlockStateBase::isAir)).anyMatch((p_185969_) -> {
                BlockPos blockpos = AMBlockPos.fromVec3(vec3);
                return p_185969_.isSuffocating(this.level(), blockpos) && Shapes.joinIsNotEmpty(p_185969_.getCollisionShape(this.level(), blockpos).move(vec3.x, vec3.y, vec3.z), Shapes.create(axisalignedbb), BooleanOp.AND);
            });
        }
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    public float getBackOffset() {
        return 0.5F;
    }

    @Override
    public boolean isWandering() {
        ServantCentipedeHead head = this.getCentipedeHead();
        return head != null ? head.isWandering() : super.isWandering();
    }

    @Override
    public boolean isStaying() {
        ServantCentipedeHead head = this.getCentipedeHead();
        return head != null ? head.isStaying() : super.isStaying();
    }

    @Override
    public boolean isCommanded() {
        ServantCentipedeHead head = this.getCentipedeHead();
        return head != null ? head.isCommanded() : super.isCommanded();
    }

    @Override
    public void setWandering(boolean wandering) {
    }

    @Override
    public void setStaying(boolean staying) {
    }

    @Override
    public void setCommandPos(BlockPos blockPos, boolean removeEntity) {
    }

    @Override
    public void setCommandPosEntity(LivingEntity livingEntity) {
    }

    public void onAttackedFromServer(LivingEntity parent, float damage, DamageSource damageSource) {
        if (parent.deathTime > 0) {
            this.deathTime = parent.deathTime;
        }

        if (parent.hurtTime > 0) {
            this.hurtTime = parent.hurtTime;
        }

    }

    static {
        BODYINDEX = SynchedEntityData.defineId(ServantCentipedeBody.class, EntityDataSerializers.INT);
        BODY_XROT = SynchedEntityData.defineId(ServantCentipedeBody.class, EntityDataSerializers.FLOAT);
        PARENT_UUID = SynchedEntityData.defineId(ServantCentipedeBody.class, EntityDataSerializers.OPTIONAL_UUID);
        CHILD_UUID = SynchedEntityData.defineId(ServantCentipedeBody.class, EntityDataSerializers.OPTIONAL_UUID);
    }
}
