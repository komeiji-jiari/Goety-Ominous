package com.qiuyue.goetyominus.client.inventory.container;

import com.qiuyue.goetyominus.common.init.ModContainerTypes;
import com.qiuyue.goetyominus.common.items.handler.FungusPackItemHandler;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class FungusPackContainer extends AbstractContainerMenu {
    private final ItemStack stack;

    public FungusPackContainer(int id, Inventory playerInventory, FungusPackItemHandler handler, ItemStack stack) {
        super(ModContainerTypes.FUNGUS_PACK.get(), id);
        this.stack = stack;

        this.addSlot(new SlotItemHandler(handler, 0, 80, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return handler.isItemValid(0, stack);
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9,
                        8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col,
                    8 + col * 18, 142));
        }
    }

    public static FungusPackContainer createContainerClientSide(int id, Inventory playerInventory, net.minecraft.network.FriendlyByteBuf buffer) {
        return new FungusPackContainer(id, playerInventory, new FungusPackItemHandler(), ItemStack.EMPTY);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            stack = slotStack.copy();
            if (index == 0) {
                if (!this.moveItemStackTo(slotStack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
                    if (index < 28) {
                        if (!this.moveItemStackTo(slotStack, 28, 37, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index < 37) {
                        if (!this.moveItemStackTo(slotStack, 1, 28, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else {
                        if (!this.moveItemStackTo(slotStack, 1, 37, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            }
            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return stack;
    }



    @Override
    public boolean stillValid(Player player) {
        return !stack.isEmpty();
    }
}
