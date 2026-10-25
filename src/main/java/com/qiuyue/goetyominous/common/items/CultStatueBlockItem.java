package com.qiuyue.goetyominous.common.items;

import com.Polarice3.Goety.common.items.block.BlockItemBase;
import com.Polarice3.Goety.utils.EntityFinder;
import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractPiglinServant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.qiuyue.goetyominous.common.init.ModBlocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class CultStatueBlockItem extends BlockItemBase {
    public static final String SERVANT_LIST = "servantList";

    public CultStatueBlockItem(Block block) {
        super(block);
    }

    public CultStatueBlockItem() {
        this(ModBlocks.CULT_STATUE.get());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide) {
            ListTag listTag = getServantList(stack, level);
            if (listTag != null && stack.getTag() != null && listTag.isEmpty()) {
                stack.getTag().remove(SERVANT_LIST);
            }
            List<AbstractPiglinServant> servants = getServants(stack, level);
            if (!servants.isEmpty()) {
                for (AbstractPiglinServant servant : servants) {
                    if (servant.isDeadOrDying()) {
                        removeServant(stack, servant, level);
                    }
                }
            } else if (stack.getTag() != null && stack.getTag().contains(SERVANT_LIST)) {
                stack.getTag().remove(SERVANT_LIST);
            }
        }
        super.inventoryTick(stack, level, entity, slot, selected);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getTag() != null && stack.getTag().contains(SERVANT_LIST);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown() && !player.isCrouching()) {
            return InteractionResultHolder.pass(stack);
        }
        if (stack.getTag() != null) {
            stack.getTag().remove(SERVANT_LIST);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    public static ListTag getServantList(ItemStack stack, Level level) {
        if (!level.isClientSide && stack.getTag() != null && stack.getTag().contains(SERVANT_LIST)) {
            return stack.getTag().getList(SERVANT_LIST, 8);
        }
        return null;
    }

    public static List<AbstractPiglinServant> getServants(ItemStack stack, Level level) {
        List<AbstractPiglinServant> servants = new ArrayList<>();
        if (!level.isClientSide && stack.getTag() != null) {
            ListTag list = stack.getTag().getList(SERVANT_LIST, 8);
            for (int i = 0; i < list.size(); ++i) {
                Entity entity = EntityFinder.getEntityByUuiD(UUID.fromString(list.getString(i)));
                if (entity instanceof AbstractPiglinServant servant) {
                    servants.add(servant);
                }
            }
        }
        return servants;
    }

    public static void removeServant(ItemStack stack, AbstractPiglinServant servant, Level level) {
        if (!level.isClientSide && stack.getTag() != null && stack.getTag().contains(SERVANT_LIST)) {
            ListTag list = stack.getTag().getList(SERVANT_LIST, 8);
            list.remove(StringTag.valueOf(servant.getStringUUID()));
            stack.getTag().put(SERVANT_LIST, list);
        }
    }

    public static void setUUIDs(ItemStack stack, UUID uuid) {
        CompoundTag tag = stack.getOrCreateTag();
        ListTag list = tag.contains(SERVANT_LIST) ? tag.getList(SERVANT_LIST, 8) : new ListTag();
        StringTag entry = StringTag.valueOf(uuid.toString());
        if (!list.contains(entry)) {
            list.add(entry);
        }
        tag.put(SERVANT_LIST, list);
    }

    public static void clearUUIDs(ItemStack stack) {
        if (stack.getTag() != null && stack.getTag().contains(SERVANT_LIST)) {
            stack.getTag().remove(SERVANT_LIST);
        }
    }
}
