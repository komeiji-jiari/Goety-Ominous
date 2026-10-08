package com.qiuyue.goetyominous.common.blocks.trial;

import com.mojang.logging.LogUtils;
import com.qiuyue.goetyominous.common.init.ModBlockEntities;
import com.qiuyue.goetyominous.config.PlayerDetector;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public class TrialSpawnerBlockEntity extends BlockEntity implements TrialSpawner.StateAccessor {
    private static final Logger LOGGER = LogUtils.getLogger();
    private TrialSpawner trialSpawner;

    public TrialSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TRIAL_SPAWNER.get(), pos, state);
        this.trialSpawner = new TrialSpawner(this, PlayerDetector.NO_CREATIVE_PLAYERS,
                PlayerDetector.EntitySelector.SELECT_FROM_LEVEL);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("normal_config")) {
            CompoundTag normalConfig = tag.getCompound("normal_config").copy();
            tag.put("ominous_config", normalConfig.merge(tag.getCompound("ominous_config")));
        }
        this.trialSpawner.codec().parse(NbtOps.INSTANCE, tag)
                .resultOrPartial(LOGGER::error)
                .ifPresent(spawner -> this.trialSpawner = spawner);
        if (this.level != null) {
            this.markUpdated();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        this.trialSpawner.codec().encodeStart(NbtOps.INSTANCE, this.trialSpawner)
                .resultOrPartial(error -> LOGGER.warn("Failed to encode TrialSpawner {}", error))
                .ifPresent(encoded -> tag.merge((CompoundTag) encoded));
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.trialSpawner.getData().getUpdateTag(this.getBlockState().getValue(TrialSpawnerBlock.STATE));
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }

    public void setEntityId(EntityType<?> type, RandomSource random) {
        this.trialSpawner.getData().setEntityId(this.trialSpawner, random, type);
        this.setChanged();
    }

    public TrialSpawner getTrialSpawner() {
        return this.trialSpawner;
    }

    @Override
    public TrialSpawnerState getState() {
        return !this.getBlockState().hasProperty(TrialSpawnerBlock.STATE)
                ? TrialSpawnerState.INACTIVE
                : this.getBlockState().getValue(TrialSpawnerBlock.STATE);
    }

    @Override
    public void setState(Level level, TrialSpawnerState state) {
        this.setChanged();
        level.setBlock(this.worldPosition, this.getBlockState().setValue(TrialSpawnerBlock.STATE, state), 3);
    }

    @Override
    public void markUpdated() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }
}
