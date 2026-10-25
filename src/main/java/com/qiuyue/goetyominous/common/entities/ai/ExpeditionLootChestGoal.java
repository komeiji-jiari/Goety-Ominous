package com.qiuyue.goetyominous.common.entities.ai;

import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractPiglinServant;
import com.qiuyue.goetyominous.common.explore.ExpeditionLootStorage;
import com.qiuyue.goetyominous.common.explore.IExpeditionLootHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ExpeditionLootChestGoal<T extends AbstractPiglinServant & IExpeditionLootHolder> extends PiglinChestGoal<T> {
    public ExpeditionLootChestGoal(T piglin) {
        super(piglin);
    }

    @Override
    public boolean canUse() {
        if (this.piglin.isFollowing() || this.piglin.isStaying()) {
            return false;
        }
        if (!this.piglin.hasExpeditionLoot() || !this.piglin.hasUsableChestLink()) {
            return false;
        }
        BlockPos chestPos = this.piglin.getChestPos();
        if (chestPos == null || this.getChest(this.piglin.level(), chestPos) == null) {
            return false;
        }
        if (!ExpeditionLootStorage.hasRoom(this.piglin.level(), chestPos, this.piglin.getExpeditionLoot())) {
            return false;
        }
        return super.canUse();
    }

    @Override
    public boolean hasItemInInv() {
        return this.piglin.hasExpeditionLoot();
    }

    @Override
    public ItemStack getItem() {
        return this.piglin.getExpeditionLoot().stream()
                .filter(stack -> !stack.isEmpty())
                .findFirst()
                .orElse(ItemStack.EMPTY);
    }

    @Override
    public void chestInteract(Container container) {
        List<ItemStack> loot = this.piglin.getExpeditionLoot();
        int index = 0;
        boolean changed = false;
        while (index < loot.size()) {
            ItemStack stack = loot.get(index);
            if (stack.isEmpty()) {
                loot.remove(index);
                continue;
            }
            int inserted = insert(container, stack);
            if (inserted > 0) {
                stack.shrink(inserted);
                changed = true;
            }
            if (stack.isEmpty()) {
                loot.remove(index);
                continue;
            }
            index++;
        }
        if (changed) {
            container.setChanged();
        }
    }

    private static int insert(Container container, ItemStack stack) {
        int remaining = stack.getCount();
        for (int i = 0; i < container.getContainerSize() && remaining > 0; i++) {
            ItemStack existing = container.getItem(i);
            if (existing.isEmpty() || !ItemStack.isSameItemSameTags(existing, stack)) {
                continue;
            }
            int capacity = Math.min(existing.getMaxStackSize(), container.getMaxStackSize()) - existing.getCount();
            if (capacity <= 0) {
                continue;
            }
            int moved = Math.min(capacity, remaining);
            existing.grow(moved);
            remaining -= moved;
        }
        for (int i = 0; i < container.getContainerSize() && remaining > 0; i++) {
            if (!container.getItem(i).isEmpty()) {
                continue;
            }
            ItemStack copy = stack.copy();
            int moved = Math.min(remaining, Math.min(copy.getMaxStackSize(), container.getMaxStackSize()));
            copy.setCount(moved);
            container.setItem(i, copy);
            remaining -= moved;
        }
        return stack.getCount() - remaining;
    }
}
