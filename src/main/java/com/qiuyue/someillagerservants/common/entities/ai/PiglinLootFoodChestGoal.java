package com.qiuyue.someillagerservants.common.entities.ai;

import com.qiuyue.someillagerservants.common.entities.ally.neutral.AbstractPiglinServant;
import java.util.Iterator;
import java.util.Objects;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class PiglinLootFoodChestGoal<T extends AbstractPiglinServant> extends PiglinChestGoal<T> {
    public PiglinLootFoodChestGoal(T piglin) {
        super(piglin);
        Objects.requireNonNull(piglin);
        this.chestPredicate = piglin::validFood;
    }

    public boolean hasItemInInv() {
        return true;
    }

    public boolean canUse() {
        if (this.piglin.getChestPos() == null) {
            return false;
        } else if (this.piglin.getBoundPos() != null && this.piglin.getChestPos() != null && !this.piglin.isWithinGuard(this.piglin.getChestPos())) {
            return false;
        } else if (this.piglin.getChestLevel() != this.piglin.level().dimension()) {
            return false;
        } else if (!this.piglin.wantsMoreFood()) {
            return false;
        } else {
            return !this.isChestRaidable(this.piglin.level(), this.piglin.getChestPos()) ? false : super.canUse();
        }
    }

    public void chestInteract(Container container) {
        Iterator var2 = this.getItems(container).iterator();
        while(var2.hasNext()) {
            ItemStack itemStack = (ItemStack)var2.next();
            if (this.piglin.getInventory().canAddItem(itemStack) && this.piglin.wantsMoreFood()) {
                this.piglin.getInventory().addItem(itemStack.split(12));
                container.setChanged();
            }
        }
    }
}
