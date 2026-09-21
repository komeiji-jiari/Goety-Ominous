package com.qiuyue.goetyominous.common.entities.util;

import com.Polarice3.Goety.common.items.revive.ReviveServantItem;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.qiuyue.goetyominous.common.entities.ally.ac.LuxtructosaurusServant;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;

public class ExtinctionCatalystEntity extends ItemEntity {
    private static final double CORE_RANGE = 20.0D;
    private static final double CORE_HEIGHT = 100.0D;
    private static final double PULL_SPEED = 0.2D;
    private static final double DRAG = 0.8D;
    private static final double SUMMON_DISTANCE = 0.66D;
    private static final int CORE_SEARCH_INTERVAL = 20;
    private static final int VOLCANO_SCAN_RANGE = 64;
    private static final int VOLCANO_SPAWN_OFFSET = 2;

    private int coreSearchCooldown;
    private BlockPos corePos;

    public ExtinctionCatalystEntity(EntityType<? extends ItemEntity> entityType, Level level) {
        super(entityType, level);
    }

    public ExtinctionCatalystEntity(Level level, double x, double y, double z, ItemStack stack) {
        this(AcEntityRegistry.EXTINCTION_CATALYST.get(), level);
        this.setPos(x, y, z);
        this.setItem(stack);
        this.setPickUpDelay(40);
        this.setYRot(this.random.nextFloat() * 360.0F);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            return;
        }
        if (this.coreSearchCooldown-- <= 0) {
            this.coreSearchCooldown = CORE_SEARCH_INTERVAL;
            this.corePos = this.findNearbyCore();
        }
        if (this.corePos == null) {
            return;
        }
        Vec3 center = Vec3.atCenterOf(this.corePos);
        double distance = Math.sqrt(this.distanceToSqr(center));
        if (distance < CORE_HEIGHT) {
            this.setDeltaMovement(center.subtract(this.position()).normalize().scale(PULL_SPEED)
                    .add(this.getDeltaMovement().scale(DRAG)));
        }
        if (distance < SUMMON_DISTANCE) {
            this.summonServant();
        }
    }

    private BlockPos findNearbyCore() {
        BlockPos best = null;
        double bestDistance = Double.MAX_VALUE;
        int chunkX = this.chunkPosition().x;
        int chunkZ = this.chunkPosition().z;
        for (int x = -1; x <= 1; ++x) {
            for (int z = -1; z <= 1; ++z) {
                LevelChunk chunk = this.level().getChunkSource().getChunkNow(chunkX + x, chunkZ + z);
                if (chunk == null) {
                    continue;
                }
                for (BlockPos pos : chunk.getBlockEntities().keySet()) {
                    if (!this.level().getBlockState(pos).is(ACBlockRegistry.VOLCANIC_CORE.get())) {
                        continue;
                    }
                    double dx = pos.getX() + 0.5D - this.getX();
                    double dy = pos.getY() + 0.5D - this.getY();
                    double dz = pos.getZ() + 0.5D - this.getZ();
                    if (Math.abs(dx) > CORE_RANGE || Math.abs(dz) > CORE_RANGE || Math.abs(dy) > CORE_HEIGHT) {
                        continue;
                    }
                    double distance = dx * dx + dy * dy + dz * dz;
                    if (distance < bestDistance) {
                        bestDistance = distance;
                        best = pos.immutable();
                    }
                }
            }
        }
        return best;
    }

    private void summonServant() {
        ItemStack stack = this.getItem();
        if (stack.isEmpty() || !(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        BlockPos spawnPos = this.findVolcanoTop();
        Entity summon = ReviveServantItem.getSummon(stack, serverLevel);
        LuxtructosaurusServant servant;
        if (summon instanceof LuxtructosaurusServant luxtructosaurus) {
            servant = luxtructosaurus;
        } else {
            servant = AcEntityRegistry.LUXTRUCTOSAURUS_SERVANT.get().create(serverLevel);
            if (servant == null) {
                return;
            }
            if (this.getOwner() instanceof Player player) {
                servant.setTrueOwner(player);
                if (servant.isRemoved()) {
                    return;
                }
            }
        }
        servant.setHealth(servant.getMaxHealth());
        servant.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D,
                this.random.nextFloat() * 360.0F, 0.0F);
        servant.setInvisible(true);
        servant.setEnraged(true);
        servant.setAnimation(LuxtructosaurusServant.ANIMATION_SUMMON);
        if (!serverLevel.addFreshEntity(servant)) {
            return;
        }
        stack.shrink(1);
    }

    private BlockPos findVolcanoTop() {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        int maxY = this.level().getMaxBuildHeight() - 1;
        int top = this.corePos.getY();
        for (int x = -VOLCANO_SCAN_RANGE; x <= VOLCANO_SCAN_RANGE; ++x) {
            for (int z = -VOLCANO_SCAN_RANGE; z <= VOLCANO_SCAN_RANGE; ++z) {
                mutable.set(this.corePos.getX() + x, this.corePos.getY(), this.corePos.getZ() + z);
                if (!this.level().hasChunkAt(mutable)
                        || !this.level().getBlockState(mutable).is(ACTagRegistry.VOLCANO_BLOCKS)) {
                    continue;
                }
                while (mutable.getY() < maxY && this.level().getBlockState(mutable).is(ACTagRegistry.VOLCANO_BLOCKS)) {
                    mutable.move(0, 1, 0);
                }
                mutable.move(0, -1, 0);
                if (mutable.getY() > top) {
                    top = mutable.getY();
                }
            }
        }
        return this.corePos.atY(top + VOLCANO_SPAWN_OFFSET);
    }
}
