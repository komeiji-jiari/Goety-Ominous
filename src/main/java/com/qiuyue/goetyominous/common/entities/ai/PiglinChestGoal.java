package com.qiuyue.goetyominous.common.entities.ai;

import com.Polarice3.Goety.api.entities.ally.illager.ILooter;
import com.Polarice3.Goety.config.MobsConfig;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractPiglinServant;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class PiglinChestGoal<T extends AbstractPiglinServant & ILooter> extends MoveToBlockGoal {
    public T piglin;
    public boolean hasOpenedChest;
    public int searchRange;
    public Predicate<ItemStack> predicate;
    public Predicate<ItemStack> chestPredicate;

    public PiglinChestGoal(T piglin, int range) {
        super(piglin, 0.75, range);
        this.hasOpenedChest = false;
        this.predicate = (itemStack) -> {
            return true;
        };
        this.chestPredicate = (itemStack) -> {
            return false;
        };
        this.piglin = piglin;
        this.searchRange = range;
    }

    public PiglinChestGoal(T piglin) {
        this(piglin, (Integer)MobsConfig.IllagerServantChestRange.get());
    }

    public ItemStack getItem() {
        Optional<ItemStack> optional = ((ILooter)this.piglin).itemsInInv(this.predicate).stream().findFirst();
        return (ItemStack)optional.orElse(ItemStack.EMPTY);
    }

    public boolean canUse() {
        if (this.piglin.isBaby()) return false;
        if (this.piglin.isStaying()) {
            return false;
        } else if (this.piglin.getTarget() != null) {
            return false;
        } else {
            return !this.hasItemInInv() ? false : this.findNearestBlock();
        }
    }

    public boolean hasItemInInv() {
        return !((ILooter)this.piglin).itemsInInv(this.predicate).isEmpty();
    }

    public boolean canContinueToUse() {
        return super.canContinueToUse() && this.canUse();
    }

    protected boolean baseNearestBlock() {
        return super.findNearestBlock();
    }

    protected boolean findNearestBlock() {
        if (((ILooter)this.piglin).getChestPos() != null) {
            this.blockPos = ((ILooter)this.piglin).getChestPos();
            if (this.blockPos != null) {
                return this.piglin.distanceToSqr((double)((float)this.blockPos.getX() + 0.5F), (double)((float)this.blockPos.getY() + 0.5F), (double)((float)this.blockPos.getZ() + 0.5F)) <= (double)Mth.square(this.searchRange);
            }
        }

        return false;
    }

    public boolean isChestRaidable(LevelReader world, BlockPos pos) {
        Container container = this.getChest(world, pos);
        return container != null ? container.hasAnyMatching(this.chestPredicate) : false;
    }

    public @Nullable Container getChest(LevelReader world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        if (block instanceof ChestBlock chestBlock) {
            if (world instanceof Level level) {
                return ChestBlock.getContainer(chestBlock, blockState, level, pos, true);
            }
        }

        if (world.getBlockState(pos).getBlock() instanceof BaseEntityBlock) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof Container) {
                Container inventory = (Container)entity;
                return inventory;
            }
        }

        return null;
    }

    public List<ItemStack> getChestContent(LevelReader world, BlockPos pos) {
        List<ItemStack> list = new ArrayList();
        Container container = this.getChest(world, pos);
        if (container != null) {
            for(int i = 0; i < container.getContainerSize(); ++i) {
                ItemStack itemStack = container.getItem(i);
                list.add(itemStack);
            }
        }

        return list;
    }

    public boolean isFull(ItemStack addStack, LevelReader world, BlockPos pos) {
        Container chest = this.getChest(world, pos);
        if (chest == null) {
            return false;
        } else {
            List<ItemStack> list = this.getChestContent(world, pos);
            if (list.isEmpty()) {
                return false;
            } else if (addStack != null && !addStack.isEmpty()) {
                int i = 0;
                Iterator var7 = list.iterator();

                while(var7.hasNext()) {
                    ItemStack containerStack = (ItemStack)var7.next();
                    if (containerStack.isEmpty()) {
                        ++i;
                    } else if (ItemStack.isSameItem(containerStack, addStack)) {
                        int j = Math.min(addStack.getMaxStackSize(), containerStack.getMaxStackSize());
                        int k = Math.min(addStack.getCount(), j - containerStack.getCount());
                        if (k > 0) {
                            ++i;
                        }
                    }
                }

                return i == 0;
            } else {
                return false;
            }
        }
    }

    public boolean hasLineOfSightChest() {
        HitResult hitResult = this.piglin.level().clip(new ClipContext(this.piglin.getEyePosition(1.0F), new Vec3((double)this.blockPos.getX() + 0.5, (double)this.blockPos.getY() + 0.5, (double)this.blockPos.getZ() + 0.5), net.minecraft.world.level.ClipContext.Block.COLLIDER, Fluid.NONE, this.piglin));
        if (!(hitResult instanceof BlockHitResult blockHitResult)) {
            return true;
        } else {
            BlockPos pos = blockHitResult.getBlockPos();
            return pos.equals(this.blockPos) || this.piglin.level().isEmptyBlock(pos) || this.piglin.level().getBlockEntity(pos) == this.piglin.level().getBlockEntity(this.blockPos);
        }
    }

    public List<ItemStack> getItems(Container inventory) {
        List<ItemStack> items = new ArrayList();

        for(int i = 0; i < inventory.getContainerSize(); ++i) {
            ItemStack stack = inventory.getItem(i);
            if (this.chestPredicate.test(stack)) {
                items.add(stack);
            }
        }

        return items;
    }

    public void tick() {
        super.tick();
        if (this.blockPos == null) {
            this.stop();
        } else {
            Container chest = this.getChest(this.piglin.level(), this.blockPos);
            if (chest != null) {
                double distance = this.piglin.distanceToSqr((double)((float)this.blockPos.getX() + 0.5F), (double)((float)this.blockPos.getY() + 0.5F), (double)((float)this.blockPos.getZ() + 0.5F));
                if (this.hasLineOfSightChest()) {
                    if (this.isReachedTarget() && distance <= 3.0) {
                        this.toggleChest(chest, false);
                        this.chestInteract(chest);
                        this.stop();
                    } else if (distance < 5.0 && !this.hasOpenedChest) {
                        this.hasOpenedChest = true;
                        this.toggleChest(chest, true);
                    }
                }
            }
        }

    }

    public void chestInteract(Container container) {
    }

    public void stop() {
        super.stop();
        if (this.blockPos != null) {
            BlockEntity blockEntity = this.piglin.level().getBlockEntity(this.blockPos);
            if (blockEntity instanceof Container) {
                Container container = (Container)blockEntity;
                this.toggleChest(container, false);
            }
        }

        this.blockPos = BlockPos.ZERO;
        this.hasOpenedChest = false;
    }

    protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
        return pos != null && this.isChestRaidable(worldIn, pos);
    }

    public void toggleChest(Container container, boolean open) {
        if (container instanceof ChestBlockEntity chest) {
            if (open) {
                this.piglin.level().blockEvent(this.blockPos, chest.getBlockState().getBlock(), 1, 1);
            } else {
                this.piglin.level().blockEvent(this.blockPos, chest.getBlockState().getBlock(), 1, 0);
            }

            this.piglin.level().updateNeighborsAt(this.blockPos, chest.getBlockState().getBlock());
            this.piglin.level().updateNeighborsAt(this.blockPos.below(), chest.getBlockState().getBlock());
        }

    }
}
