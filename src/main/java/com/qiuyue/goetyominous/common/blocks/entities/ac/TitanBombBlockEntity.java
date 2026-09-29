package com.qiuyue.goetyominous.common.blocks.entities.ac;

import com.qiuyue.goetyominous.common.init.ac.AcBlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TitanBombBlockEntity extends BlockEntity {

    public static final String IDENTITY_TAG = "Identity";

    private CompoundTag identity = new CompoundTag();

    public TitanBombBlockEntity(BlockPos pos, BlockState state) {
        super(AcBlockEntityRegistry.TITAN_BOMB.get(), pos, state);
    }

    public CompoundTag getIdentity() {
        return this.identity;
    }

    public void setIdentity(CompoundTag identity) {
        this.identity = identity;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(IDENTITY_TAG)) {
            this.identity = tag.getCompound(IDENTITY_TAG).copy();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!this.identity.isEmpty()) {
            tag.put(IDENTITY_TAG, this.identity.copy());
        }
    }
}
