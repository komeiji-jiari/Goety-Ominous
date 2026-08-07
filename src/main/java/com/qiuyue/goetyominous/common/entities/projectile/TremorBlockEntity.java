package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class TremorBlockEntity extends Entity {
    private static final EntityDataAccessor<BlockState> BLOCK_STATE = SynchedEntityData
            .defineId(TremorBlockEntity.class, EntityDataSerializers.BLOCK_STATE);
    private static final EntityDataAccessor<Optional<UUID>> OWNER_ID = SynchedEntityData
            .defineId(TremorBlockEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(TremorBlockEntity.class,
            EntityDataSerializers.FLOAT);

    public TremorBlockEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(BLOCK_STATE, Blocks.STONE.defaultBlockState());
        this.entityData.define(OWNER_ID, Optional.empty());
        this.entityData.define(DAMAGE, 0.0F);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("BlockState")) {
            this.setBlockState(NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK),
                    tag.getCompound("BlockState")));
        }
        if (tag.hasUUID("Owner")) {
            this.setOwnerId(tag.getUUID("Owner"));
        }
        this.setDamage(tag.getFloat("Damage"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.put("BlockState", NbtUtils.writeBlockState(this.getBlockState()));
        if (this.getOwnerId() != null) {
            tag.putUUID("Owner", this.getOwnerId());
        }
        tag.putFloat("Damage", this.getDamage());
    }

    public BlockState getBlockState() {
        return this.entityData.get(BLOCK_STATE);
    }

    public void setBlockState(BlockState state) {
        this.entityData.set(BLOCK_STATE, state);
    }

    @Nullable
    public UUID getOwnerId() {
        return this.entityData.get(OWNER_ID).orElse(null);
    }

    public void setOwnerId(@Nullable UUID uuid) {
        this.entityData.set(OWNER_ID, Optional.ofNullable(uuid));
    }

    public void setOwner(@Nullable LivingEntity owner) {
        if (owner != null) {
            this.setOwnerId(owner.getUUID());
        }
    }

    @Nullable
    public LivingEntity getOwnerEntity() {
        UUID uuid = this.getOwnerId();
        if (uuid != null && this.level() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(uuid);
            if (entity instanceof LivingEntity living) {
                return living;
            }
        }
        return null;
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    public void setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount > 30) {
            this.discard();
            return;
        }
        Vec3 v = this.getDeltaMovement();
        this.setDeltaMovement(v.x, v.y - 0.04D, v.z);
        this.move(MoverType.SELF, this.getDeltaMovement());
        if (this.onGround()) {
            this.discard();
            return;
        }
        if (this.level().isClientSide) {
            if (this.tickCount % 2 == 0) {
                this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, this.getBlockState()),
                        this.getX(), this.getY() + this.getBbHeight() / 2.0D, this.getZ(), 0.0D, 0.0D, 0.0D);
            }
            return;
        }
        LivingEntity owner = this.getOwnerEntity();
        for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox())) {
            if (target != owner && (owner == null || !MobUtil.areAllies(owner, target)) && target.isAlive()
                    && target.onGround()) {
                DamageSource damageSource = this.damageSources().flyIntoWall();
                if (target.hurt(damageSource, this.getDamage())) {
                    MobUtil.push(target, 0.0D, 0.6D, 0.0D);
                }
                this.discard();
                return;
            }
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double d0 = this.getBoundingBox().getSize() * 4.0D;
        if (Double.isNaN(d0)) {
            d0 = 4.0D;
        }
        d0 *= 64.0D;
        return distance < d0 * d0;
    }

    @Override
    public BlockPos getBlockPosBelowThatAffectsMyMovement() {
        return this.blockPosition();
    }
}
