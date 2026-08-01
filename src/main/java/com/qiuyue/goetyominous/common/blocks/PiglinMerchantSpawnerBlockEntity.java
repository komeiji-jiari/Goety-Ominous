package com.qiuyue.goetyominous.common.blocks;

import com.qiuyue.goetyominous.common.entities.ally.mobs.PiglinMerchant;
import com.qiuyue.goetyominous.common.init.ModBlockEntities;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class PiglinMerchantSpawnerBlockEntity extends BlockEntity {

    private boolean hasSpawned;

    public PiglinMerchantSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PIGLIN_MERCHANT_SPAWNER.get(), pos, state);
    }

    public void tick(Level level, BlockPos pos) {
        if (this.hasSpawned) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        AABB aabb = new AABB(pos).inflate(7);
        for (ServerPlayer player : serverLevel.getEntitiesOfClass(ServerPlayer.class, aabb)) {
            if (!player.isSpectator() && !player.getAbilities().instabuild) {
                PiglinMerchant merchant = new PiglinMerchant(
                        ModEntityTypes.PIGLIN_MERCHANT.get(), level);
                merchant.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                level.addFreshEntity(merchant);
                this.hasSpawned = true;
                level.removeBlock(pos, false);
                break;
            }
        }
    }
}
