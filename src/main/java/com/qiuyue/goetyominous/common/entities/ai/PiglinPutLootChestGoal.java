package com.qiuyue.goetyominous.common.entities.ai;

import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractPiglinServant;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class PiglinPutLootChestGoal<T extends AbstractPiglinServant> extends PiglinStoreChestGoal<T> {
    public PiglinPutLootChestGoal(T piglin) {
        super(piglin);
        Objects.requireNonNull(piglin);
        this.predicate = piglin::validLootToStore;
        this.chestPredicate = (itemStack) -> true;
    }

    public void chestInteract(Container container) {
        Optional<ItemStack> optional = this.piglin.itemsInInv(this.predicate).stream().findFirst();
        if (optional.isPresent()) {
            ItemStack itemStack = optional.get();
            for(int i = 0; i < container.getContainerSize(); ++i) {
                ItemStack containerItem = container.getItem(i);
                if (!itemStack.isEmpty()) {
                    if (containerItem.isEmpty()) {
                        container.setItem(i, itemStack.copyAndClear());
                        container.setChanged();
                        return;
                    }
                    if (containerItem.getItem() == itemStack.getItem()) {
                        int j = Math.min(container.getMaxStackSize(), containerItem.getMaxStackSize());
                        int k = Math.min(itemStack.getCount(), j - containerItem.getCount());
                        if (k > 0) {
                            int l = 0;
                            while(l < k && containerItem.getCount() < containerItem.getMaxStackSize()) {
                                ++l;
                                containerItem.grow(1);
                                itemStack.shrink(1);
                            }
                            if ((l >= k || containerItem.getCount() == containerItem.getMaxStackSize()) && !itemStack.isEmpty() && this.piglin.getInventory().canAddItem(itemStack)) {
                                this.piglin.getInventory().addItem(itemStack);
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
