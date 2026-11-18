package com.qiuyue.goetyominous.common.inventory.container;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.items.handler.EternalCauldronItemHandler;
import com.mojang.datafixers.util.Pair;
import com.qiuyue.goetyominous.common.init.ModContainerTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class NetherEternalCauldronContainer extends AbstractContainerMenu {
    private final ItemStack stack;

    public NetherEternalCauldronContainer(int id, Inventory playerInventory,
                                          EternalCauldronItemHandler handler, ItemStack stack) {
        super(ModContainerTypes.NETHER_ETERNAL_CAULDRON.get(), id);
        this.stack = stack;
        this.addSlot(new SlotItemHandler(handler, 0, 80, 35) {
            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, Goety.location("item/empty_slot_potion"));
            }
        });
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    public static NetherEternalCauldronContainer createContainerClientSide(int id, Inventory playerInventory,
                                                                          FriendlyByteBuf buffer) {
        return new NetherEternalCauldronContainer(id, playerInventory,
                new EternalCauldronItemHandler(), ItemStack.EMPTY);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index == 0) {
                if (!this.moveItemStackTo(itemstack1, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else {
                if (this.moveItemStackTo(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
                if (index >= 1 && index < 28 ? !this.moveItemStackTo(itemstack1, 28, 37, false)
                        : (index >= 28 && index < 37 ? !this.moveItemStackTo(itemstack1, 1, 28, false)
                        : !this.moveItemStackTo(itemstack1, 1, 37, false))) {
                    return ItemStack.EMPTY;
                }
            }
            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(playerIn, itemstack1);
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return !this.stack.isEmpty();
    }
}
