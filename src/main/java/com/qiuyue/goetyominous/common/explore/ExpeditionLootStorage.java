package com.qiuyue.goetyominous.common.explore;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class ExpeditionLootStorage {
    public static final String KEY = "GoetyOminousExpeditionLoot";

    private ExpeditionLootStorage() {
    }

    public static List<ItemStack> read(CompoundTag tag) {
        List<ItemStack> out = new ArrayList<>();
        ListTag list = tag.getList(KEY, Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            ItemStack stack = ItemStack.of(list.getCompound(i));
            if (!stack.isEmpty()) {
                out.add(stack);
            }
        }
        return out;
    }

    public static void write(CompoundTag tag, List<ItemStack> loot) {
        ListTag list = new ListTag();
        for (ItemStack stack : loot) {
            if (!stack.isEmpty()) {
                list.add(stack.save(new CompoundTag()));
            }
        }
        if (list.isEmpty()) {
            tag.remove(KEY);
        } else {
            tag.put(KEY, list);
        }
    }

    public static void append(CompoundTag tag, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        List<ItemStack> loot = read(tag);
        loot.add(stack.copy());
        write(tag, loot);
    }

    public static void appendAll(CompoundTag tag, List<ItemStack> stacks) {
        List<ItemStack> loot = read(tag);
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                loot.add(stack.copy());
            }
        }
        write(tag, loot);
    }
}
