package com.qiuyue.someillagerservants.common.entities.ai;

import java.util.Objects;
import java.util.Optional;

import com.qiuyue.someillagerservants.common.entities.ally.neutral.AbstractPiglinServant;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class PiglinPutFoodChestGoal<T extends AbstractPiglinServant> extends PiglinChestGoal<T> {
    public PiglinPutFoodChestGoal(T piglin) {
        super(piglin);
        Objects.requireNonNull(piglin);
        this.predicate = piglin::validFood;
        this.chestPredicate = (itemStack) -> true;
    }

    public boolean canUse() {
        if (this.piglin.getChestPos() == null) {
            return false;
        } else if (this.piglin.getChestLevel() != this.piglin.level().dimension()) {
            return false;
        } else if (this.piglin.getBoundPos() != null && this.piglin.getChestPos() != null && !this.piglin.isWithinGuard(this.piglin.getChestPos())) {
            return false;
        } else if (!this.canStore()) {
            return false;
        } else if (!this.piglin.hasExcessFood()) {
            return false;
        } else if (this.getChest(this.piglin.level(), this.piglin.getChestPos()) == null) {
            return false;
        } else {
            return this.isFull(this.getItem(), this.piglin.level(), this.piglin.getChestPos()) ? false : super.canUse();
        }
    }

    public boolean canStore() {
        Optional<ItemStack> optional = this.piglin.itemsInInv(this.predicate).stream().findFirst();
        if (optional.isPresent()) {
            ItemStack itemStack = optional.get();
            int h = 0;
            if (itemStack.getCount() > itemStack.getMaxStackSize() / 2) {
                h = itemStack.getCount() / 2;
            }
            if (itemStack.getCount() > 24) {
                h = itemStack.getCount() - 24;
            }
            return h > 0;
        }
        return false;
    }

    public void chestInteract(Container container) {
        Optional<ItemStack> optional = this.piglin.itemsInInv(this.predicate).stream().findFirst();
        ItemStack itemStack0 = ItemStack.EMPTY;
        if (optional.isPresent()) {
            ItemStack itemStack = optional.get();
            int h = 0;
            if (itemStack.getCount() > itemStack.getMaxStackSize() / 2) {
                h = itemStack.getCount() / 2;
            }
            if (itemStack.getCount() > 24) {
                h = itemStack.getCount() - 24;
            }
            if (h > 0) {
                itemStack0 = itemStack.split(h);
            }
            if (!itemStack0.isEmpty()) {
                for(int i = 0; i < container.getContainerSize(); ++i) {
                    ItemStack containerItem = container.getItem(i);
                    if (containerItem.isEmpty()) {
                        container.setItem(i, itemStack0.copyAndClear());
                        container.setChanged();
                        return;
                    }
                    if (containerItem.getItem() == itemStack0.getItem()) {
                        int j = Math.min(container.getMaxStackSize(), containerItem.getMaxStackSize());
                        int k = Math.min(itemStack0.getCount(), j - containerItem.getCount());
                        if (k > 0) {
                            int l = 0;
                            while(l < k && containerItem.getCount() < containerItem.getMaxStackSize()) {
                                ++l;
                                containerItem.grow(1);
                                itemStack0.shrink(1);
                            }
                            if ((l >= k || containerItem.getCount() == containerItem.getMaxStackSize()) && !itemStack0.isEmpty() && this.piglin.getInventory().canAddItem(itemStack0)) {
                                this.piglin.getInventory().addItem(itemStack0);
                            }
                            container.setChanged();
                            return;
                        }
                    }
                }
            }
        }
    }
}
