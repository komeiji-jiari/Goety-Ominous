package com.qiuyue.goetyominus.common.entities.ally.sar;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.teamabnormals.savage_and_ravage.common.block.RunedGloomyTilesBlock;
import com.teamabnormals.savage_and_ravage.core.registry.SRBlocks;
import com.teamabnormals.savage_and_ravage.core.registry.SRMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;

public class RunePrison extends Owned {
    private static final EntityDataAccessor<Integer> TICKS_TILL_REMOVE = SynchedEntityData.defineId(RunePrison.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<BlockPos>> BLOCK_POS = SynchedEntityData.defineId(RunePrison.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private final boolean fromTrap;
    private int currentFrame = 0;
    private boolean isBackwardsFrameCycle = false;

    public RunePrison(EntityType<? extends RunePrison> type, Level world) {
        super(type, world);
        this.fromTrap = false;
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public RunePrison(Level world, BlockPos position, int ticksTillRemove, boolean fromTrap) {
        super(com.qiuyue.goetyominus.common.init.sar.SarEntityRegistry.RUNE_PRISON.get(), world);
        this.setBlockPos(position);
        this.fromTrap = fromTrap;
        this.setTicksTillRemove(ticksTillRemove);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public RunePrison(Level world, BlockPos position, int ticksTillRemove, boolean fromTrap, LivingEntity caster) {
        this(world, position, ticksTillRemove, fromTrap);
        this.setTrueOwner(caster);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BLOCK_POS, Optional.empty());
        this.entityData.define(TICKS_TILL_REMOVE, 0);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setTicksTillRemove(compound.getInt("TicksTillRemove"));
        if (compound.contains("GloomyTilePosition", 10))
            this.setBlockPos(NbtUtils.readBlockPos(compound.getCompound("GloomyTilePosition")));
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("TicksTillRemove", this.getTicksTillRemove());
        if (this.getBlockPos() != null)
            compound.put("GloomyTilePosition", NbtUtils.writeBlockPos(this.getBlockPos()));
    }

    public int getTicksTillRemove() {
        return this.entityData.get(TICKS_TILL_REMOVE);
    }

    public void setTicksTillRemove(int tickCount) {
        this.entityData.set(TICKS_TILL_REMOVE, tickCount);
    }

    @Nullable
    public BlockPos getBlockPos() {
        return this.entityData.get(BLOCK_POS).orElse(null);
    }

    private void setBlockPos(@Nullable BlockPos positionIn) {
        this.entityData.set(BLOCK_POS, Optional.ofNullable(positionIn));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance potioneffectIn) {
        return false;
    }

    @Override
    public void tick() {
        this.setNoGravity(true);
        this.setDeltaMovement(0.0D, 0.0D, 0.0D);

        super.tick();

        if (this.level().isClientSide() && getTicksTillRemove() % 5 == 0) {
            if (!isBackwardsFrameCycle) {
                currentFrame++;
                if (currentFrame == 4) {
                    isBackwardsFrameCycle = true;
                }
            } else {
                currentFrame--;
                if (currentFrame == 0) {
                    isBackwardsFrameCycle = false;
                }
            }
        }

        if (getTicksTillRemove() > 0)
            setTicksTillRemove(getTicksTillRemove() - 1);

        LivingEntity owner = this.getTrueOwner();
        for (LivingEntity livingEntity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox())) {
            if (livingEntity.isAffectedByPotions()) {
                if (owner != null && (livingEntity == owner || owner.isAlliedTo(livingEntity))) {
                    continue;
                }
                livingEntity.addEffect(new MobEffectInstance(SRMobEffects.WEIGHT.get(), 60, 2));
            }
        }

        if (this.getTicksTillRemove() == 0) {
            this.discard();

            BlockPos pos = this.getBlockPos();
            if (pos != null && this.fromTrap) {
                if (this.level().getBlockState(pos).getBlock() instanceof RunedGloomyTilesBlock)
                    this.level().setBlockAndUpdate(pos, SRBlocks.GLOOMY_TILES.get().defaultBlockState());
            }
        }
    }


    public int getCurrentFrame() {
        return this.currentFrame;
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1.0D);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
