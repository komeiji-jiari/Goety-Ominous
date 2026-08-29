package com.qiuyue.goetyominous.common.blocks.entities.ac;

import com.Polarice3.Goety.api.blocks.entities.IOwnedBlock;
import com.qiuyue.goetyominous.common.init.ac.AcBlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;


public class TremorzillaServantEggBlockEntity extends BlockEntity implements IOwnedBlock {

    private UUID ownerUUID;
    private int ownerClientId = -1;

    public TremorzillaServantEggBlockEntity(BlockPos pos, BlockState state) {
        super(AcBlockEntityRegistry.TREMORZILLA_SERVANT_EGG.get(), pos, state);
    }

    @Override
    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    @Override
    public void setOwnerUUID(UUID uuid) {
        this.ownerUUID = uuid;
    }

    @Override
    public int getOwnerId() {
        return this.ownerClientId;
    }

    @Override
    public void setOwnerId(int id) {
        this.ownerClientId = id;
    }

    @Nullable
    @Override
    public Player getPlayer() {
        return null;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.hasUUID("Owner")) {
            this.ownerUUID = tag.getUUID("Owner");
        }
        if (tag.contains("OwnerClient")) {
            this.ownerClientId = tag.getInt("OwnerClient");
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.ownerUUID != null) {
            tag.putUUID("Owner", this.ownerUUID);
        }
        if (this.ownerClientId > -1) {
            tag.putInt("OwnerClient", this.ownerClientId);
        }
    }
}
