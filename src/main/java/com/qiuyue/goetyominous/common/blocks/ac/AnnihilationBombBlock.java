package com.qiuyue.goetyominous.common.blocks.ac;

import com.Polarice3.Goety.utils.SEHelper;
import com.qiuyue.goetyominous.common.blocks.entities.ac.AnnihilationBombBlockEntity;
import com.qiuyue.goetyominous.common.entities.projectile.AnnihilationBombEntity;
import com.qiuyue.goetyominous.common.init.ac.AcBlockEntityRegistry;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;

public class AnnihilationBombBlock extends Block implements EntityBlock {

    public static final int COOLDOWN_TICKS = 24000;

    public AnnihilationBombBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return AcBlockEntityRegistry.ANNIHILATION_BOMB.get().create(pos, state);
    }

    @Override
    public void onCaughtFire(BlockState state, Level level, BlockPos pos, @Nullable Direction face, @Nullable LivingEntity igniter) {
        if (level.isClientSide) {
            return;
        }
        if (igniter instanceof Player player) {
            if (SEHelper.isOnCooldown(player, new ItemStack(this))) {
                if (!player.isCreative()) {
                    popResource(level, pos, this.createBombItem(level, pos));
                }
                return;
            }
            SEHelper.addCooldown(player, this.asItem(), COOLDOWN_TICKS);
        }
        AnnihilationBombEntity bomb = AcEntityRegistry.ANNIHILATION_BOMB.get().create(level);
        if (bomb == null) {
            return;
        }
        bomb.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        if (igniter instanceof Player player) {
            bomb.setIgniter(player);
        }
        if (level.getBlockEntity(pos) instanceof AnnihilationBombBlockEntity bombBlockEntity) {
            bomb.setIdentity(bombBlockEntity.getIdentity());
        }
        level.addFreshEntity(bomb);
        level.playSound(null, bomb.getX(), bomb.getY(), bomb.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(igniter, GameEvent.PRIME_FUSE, pos);
    }

    private ItemStack createBombItem(Level level, BlockPos pos) {
        ItemStack stack = new ItemStack(this);
        if (level.getBlockEntity(pos) instanceof AnnihilationBombBlockEntity bombBlockEntity) {
            CompoundTag identity = bombBlockEntity.getIdentity();
            if (!identity.isEmpty()) {
                CompoundTag blockEntityTag = new CompoundTag();
                blockEntityTag.put(AnnihilationBombBlockEntity.IDENTITY_TAG, identity.copy());
                stack.addTagElement(BlockItem.BLOCK_ENTITY_TAG, blockEntityTag);
            }
        }
        return stack;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        if (builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof AnnihilationBombBlockEntity bombBlockEntity) {
            CompoundTag identity = bombBlockEntity.getIdentity();
            if (!identity.isEmpty()) {
                CompoundTag blockEntityTag = new CompoundTag();
                blockEntityTag.put(AnnihilationBombBlockEntity.IDENTITY_TAG, identity.copy());
                for (ItemStack stack : drops) {
                    if (stack.is(this.asItem())) {
                        stack.addTagElement(BlockItem.BLOCK_ENTITY_TAG, blockEntityTag.copy());
                    }
                }
            }
        }
        return drops;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!oldState.is(state.getBlock()) && level.hasNeighborSignal(pos)) {
            this.onCaughtFire(state, level, pos, null, null);
            level.removeBlock(pos, false);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean movedByPiston) {
        if (level.hasNeighborSignal(pos)) {
            this.onCaughtFire(state, level, pos, null, null);
            level.removeBlock(pos, false);
        }
    }

    @Override
    public void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        if (level.isClientSide) {
            return;
        }
        BlockPos pos = hit.getBlockPos();
        Entity owner = projectile.getOwner();
        if (projectile.isOnFire() && projectile.mayInteract(level, pos)) {
            this.onCaughtFire(state, level, pos, null, owner instanceof LivingEntity living ? living : null);
            level.removeBlock(pos, false);
        }
    }

    @Override
    public boolean dropFromExplosion(Explosion explosion) {
        return false;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(Items.FLINT_AND_STEEL) && !stack.is(Items.FIRE_CHARGE)) {
            return super.use(state, level, pos, player, hand, hit);
        }
        if (SEHelper.isOnCooldown(player, new ItemStack(this))) {
            return InteractionResult.FAIL;
        }
        this.onCaughtFire(state, level, pos, hit.getDirection(), player);
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
        if (!player.isCreative()) {
            if (stack.is(Items.FLINT_AND_STEEL)) {
                stack.hurtAndBreak(1, player, e -> e.broadcastBreakEvent(hand));
            } else {
                stack.shrink(1);
            }
        }
        player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }
}
