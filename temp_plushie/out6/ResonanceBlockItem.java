package com.Polarice3.Goety.common.items.block;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.entities.ally.golem.SquallGolem;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.utils.EntityFinder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class ResonanceBlockItem extends BlockItemBase {
    public static String GOLEM_LIST = "golemList";

    public ResonanceBlockItem() {
        super((Block)ModBlocks.RESONANCE_CRYSTAL.get());
    }

    public void m_6883_(@NotNull ItemStack stack, Level worldIn, @NotNull Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.f_46443_) {
            ListTag listTag = getGolemList(stack, worldIn);
            if (listTag != null && stack.m_41783_() != null && listTag.size() == 0) {
                stack.m_41783_().m_128473_(GOLEM_LIST);
            }

            if (!getGolems(stack, worldIn).isEmpty()) {
                for(SquallGolem squallGolem : getGolems(stack, worldIn)) {
                    if (squallGolem == null || squallGolem.m_21224_()) {
                        removeGolem(stack, squallGolem, worldIn);
                    }
                }
            }
        }

        super.m_6883_(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    public boolean m_5812_(ItemStack p_41453_) {
        return p_41453_.m_41783_() != null && p_41453_.m_41783_().m_128441_(GOLEM_LIST);
    }

    public @NotNull InteractionResultHolder<ItemStack> m_7203_(@NotNull Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.m_21120_(hand);
        if (!player.m_6144_() && !player.m_6047_()) {
            return InteractionResultHolder.m_19098_(itemstack);
        } else {
            if (itemstack.m_41720_() instanceof ResonanceBlockItem && itemstack.m_41783_() != null) {
                itemstack.m_41783_().m_128473_(GOLEM_LIST);
            }

            return InteractionResultHolder.m_19092_(itemstack, level.m_5776_());
        }
    }

    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!player.f_19853_.f_46443_ && entity instanceof SquallGolem squallGolem) {
            ListTag listTag = getGolemList(stack, player.f_19853_);
            if (listTag == null || listTag.size() < 4) {
                setGolems(stack, player, squallGolem);
                ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.f_11686_, 1.0F, 0.45F));
            }
        }

        return true;
    }

    public static ListTag getGolemList(ItemStack stack, Level level) {
        if (!level.f_46443_) {
            CompoundTag compound = new CompoundTag();
            if (stack.m_41782_()) {
                compound = stack.m_41783_();
            }

            if (compound != null && compound.m_128441_(GOLEM_LIST)) {
                return compound.m_128437_(GOLEM_LIST, 8);
            }
        }

        return null;
    }

    public static void removeGolem(ItemStack stack, SquallGolem squallGolem, Level level) {
        if (!level.f_46443_) {
            CompoundTag compound = new CompoundTag();
            if (stack.m_41782_()) {
                compound = stack.m_41783_();
            }

            List<String> list = new ArrayList();
            if (compound != null) {
                if (compound.m_128441_(GOLEM_LIST)) {
                    for(int i = 0; i < compound.m_128437_(GOLEM_LIST, 8).size(); ++i) {
                        list.add(compound.m_128437_(GOLEM_LIST, 8).m_128778_(i));
                    }
                }

                if (list.contains(squallGolem.m_20149_())) {
                    ListTag nbttaglist = new ListTag();
                    if (compound.m_128441_(GOLEM_LIST)) {
                        nbttaglist = compound.m_128437_(GOLEM_LIST, 8);
                    }

                    nbttaglist.remove(StringTag.m_129297_(squallGolem.m_20149_()));
                    compound.m_128365_(GOLEM_LIST, nbttaglist);
                    stack.m_41751_(compound);
                }
            }
        }

    }

    public static List<SquallGolem> getGolems(ItemStack stack, Level level) {
        List<SquallGolem> squallGolems = new ArrayList();
        if (!level.f_46443_ && stack.m_41783_() != null) {
            ListTag list = stack.m_41783_().m_128437_(GOLEM_LIST, 8);

            for(int i = 0; i < list.size(); ++i) {
                Entity entity = EntityFinder.getEntityByUuiD(UUID.fromString(list.m_128778_(i)));
                if (entity instanceof SquallGolem) {
                    SquallGolem squallGolem = (SquallGolem)entity;
                    squallGolems.add(squallGolem);
                }
            }
        }

        return squallGolems;
    }

    public static void setGolems(ItemStack stack, Player player, SquallGolem squallGolem) {
        if (!player.f_19853_.f_46443_) {
            CompoundTag compound = new CompoundTag();
            if (stack.m_41782_()) {
                compound = stack.m_41783_();
            }

            List<String> list = new ArrayList();
            if (compound != null) {
                if (compound.m_128441_(GOLEM_LIST)) {
                    for(int i = 0; i < compound.m_128437_(GOLEM_LIST, 8).size(); ++i) {
                        list.add(compound.m_128437_(GOLEM_LIST, 8).m_128778_(i));
                    }
                }

                if (!list.contains(squallGolem.m_20149_())) {
                    ListTag nbttaglist = new ListTag();
                    if (compound.m_128441_(GOLEM_LIST)) {
                        nbttaglist = compound.m_128437_(GOLEM_LIST, 8);
                    }

                    nbttaglist.add(StringTag.m_129297_(squallGolem.m_20149_()));
                    compound.m_128365_(GOLEM_LIST, nbttaglist);
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
            if (compound.m_128441_(GOLEM_LIST)) {
                for(int i = 0; i < compound.m_128437_(GOLEM_LIST, 8).size(); ++i) {
                    list.add(compound.m_128437_(GOLEM_LIST, 8).m_128778_(i));
                }
            }

            if (!list.contains(uuid.toString())) {
                ListTag nbttaglist = new ListTag();
                if (compound.m_128441_(GOLEM_LIST)) {
                    nbttaglist = compound.m_128437_(GOLEM_LIST, 8);
                }

                nbttaglist.add(StringTag.m_129297_(uuid.toString()));
                compound.m_128365_(GOLEM_LIST, nbttaglist);
                stack.m_41751_(compound);
            }
        }

    }
}
