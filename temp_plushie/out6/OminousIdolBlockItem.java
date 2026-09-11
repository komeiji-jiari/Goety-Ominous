package com.Polarice3.Goety.common.items.block;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.Polarice3.Goety.utils.EntityFinder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
import org.jetbrains.annotations.NotNull;

public class OminousIdolBlockItem extends BlockItemBase {
    public static String ILLAGER_LIST = "illagerList";

    public OminousIdolBlockItem() {
        super((Block)ModBlocks.OMINOUS_IDOL.get());
    }

    public void m_6883_(@NotNull ItemStack stack, Level worldIn, @NotNull Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.f_46443_) {
            ListTag listTag = getIllagerList(stack, worldIn);
            if (listTag != null && stack.m_41783_() != null && listTag.isEmpty()) {
                stack.m_41783_().m_128473_(ILLAGER_LIST);
            }

            if (!getIllagers(stack, worldIn).isEmpty()) {
                for(RaiderServant illagerServant : getIllagers(stack, worldIn)) {
                    if (illagerServant == null || illagerServant.m_21224_()) {
                        removeIllager(stack, illagerServant, worldIn);
                    }
                }
            } else if (stack.m_41783_() != null && stack.m_41783_().m_128441_(ILLAGER_LIST)) {
                stack.m_41783_().m_128473_(ILLAGER_LIST);
            }
        }

        super.m_6883_(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    public boolean m_5812_(ItemStack p_41453_) {
        return p_41453_.m_41783_() != null && p_41453_.m_41783_().m_128441_(ILLAGER_LIST);
    }

    public @NotNull InteractionResultHolder<ItemStack> m_7203_(@NotNull Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.m_21120_(hand);
        if (!player.m_6144_() && !player.m_6047_()) {
            return InteractionResultHolder.m_19098_(itemstack);
        } else {
            if (itemstack.m_41720_() instanceof OminousIdolBlockItem && itemstack.m_41783_() != null) {
                itemstack.m_41783_().m_128473_(ILLAGER_LIST);
            }

            return InteractionResultHolder.m_19092_(itemstack, level.m_5776_());
        }
    }

    public static ListTag getIllagerList(ItemStack stack, Level level) {
        if (!level.f_46443_) {
            CompoundTag compound = new CompoundTag();
            if (stack.m_41782_()) {
                compound = stack.m_41783_();
            }

            if (compound != null && compound.m_128441_(ILLAGER_LIST)) {
                return compound.m_128437_(ILLAGER_LIST, 8);
            }
        }

        return null;
    }

    public static void removeIllager(ItemStack stack, RaiderServant illagerServant, Level level) {
        if (!level.f_46443_) {
            CompoundTag compound = new CompoundTag();
            if (stack.m_41782_()) {
                compound = stack.m_41783_();
            }

            List<String> list = new ArrayList();
            if (compound != null) {
                if (compound.m_128441_(ILLAGER_LIST)) {
                    for(int i = 0; i < compound.m_128437_(ILLAGER_LIST, 8).size(); ++i) {
                        list.add(compound.m_128437_(ILLAGER_LIST, 8).m_128778_(i));
                    }
                }

                if (list.contains(illagerServant.m_20149_())) {
                    ListTag nbttaglist = new ListTag();
                    if (compound.m_128441_(ILLAGER_LIST)) {
                        nbttaglist = compound.m_128437_(ILLAGER_LIST, 8);
                    }

                    nbttaglist.remove(StringTag.m_129297_(illagerServant.m_20149_()));
                    compound.m_128365_(ILLAGER_LIST, nbttaglist);
                    stack.m_41751_(compound);
                }
            }
        }

    }

    public static List<RaiderServant> getIllagers(ItemStack stack, Level level) {
        List<RaiderServant> illagerServants = new ArrayList();
        if (!level.f_46443_ && stack.m_41783_() != null) {
            ListTag list = stack.m_41783_().m_128437_(ILLAGER_LIST, 8);

            for(int i = 0; i < list.size(); ++i) {
                Entity entity = EntityFinder.getEntityByUuiD(UUID.fromString(list.m_128778_(i)));
                if (entity instanceof RaiderServant) {
                    RaiderServant servant = (RaiderServant)entity;
                    illagerServants.add(servant);
                }
            }
        }

        return illagerServants;
    }

    public static void setIllager(ItemStack stack, Player player, RaiderServant illagerServant) {
        if (!player.f_19853_.f_46443_) {
            CompoundTag compound = new CompoundTag();
            if (stack.m_41782_()) {
                compound = stack.m_41783_();
            }

            List<String> list = new ArrayList();
            if (compound != null) {
                if (compound.m_128441_(ILLAGER_LIST)) {
                    for(int i = 0; i < compound.m_128437_(ILLAGER_LIST, 8).size(); ++i) {
                        list.add(compound.m_128437_(ILLAGER_LIST, 8).m_128778_(i));
                    }
                }

                if (!list.contains(illagerServant.m_20149_())) {
                    ListTag nbttaglist = new ListTag();
                    if (compound.m_128441_(ILLAGER_LIST)) {
                        nbttaglist = compound.m_128437_(ILLAGER_LIST, 8);
                    }

                    nbttaglist.add(StringTag.m_129297_(illagerServant.m_20149_()));
                    compound.m_128365_(ILLAGER_LIST, nbttaglist);
                    stack.m_41751_(compound);
                }
            }
        }

    }

    public static void setUUIDs(ItemStack stack, UUID uuid) {
        CompoundTag compound = new CompoundTag();
        if (stack.m_41782_()) {
            compound = stack.m_41783_();
        }

        List<String> list = new ArrayList();
        if (compound != null) {
            if (compound.m_128441_(ILLAGER_LIST)) {
                for(int i = 0; i < compound.m_128437_(ILLAGER_LIST, 8).size(); ++i) {
                    list.add(compound.m_128437_(ILLAGER_LIST, 8).m_128778_(i));
                }
            }

            if (!list.contains(uuid.toString())) {
                ListTag nbttaglist = new ListTag();
                if (compound.m_128441_(ILLAGER_LIST)) {
                    nbttaglist = compound.m_128437_(ILLAGER_LIST, 8);
                }

                nbttaglist.add(StringTag.m_129297_(uuid.toString()));
                compound.m_128365_(ILLAGER_LIST, nbttaglist);
                stack.m_41751_(compound);
            }
        }

    }

    public static void clearUUIDs(ItemStack stack) {
        if (stack.m_41783_() != null && stack.m_41783_().m_128441_(ILLAGER_LIST)) {
            stack.m_41783_().m_128473_(ILLAGER_LIST);
        }

    }
}
