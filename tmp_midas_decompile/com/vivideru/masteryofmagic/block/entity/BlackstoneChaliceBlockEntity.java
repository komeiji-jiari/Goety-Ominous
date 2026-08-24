/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.NonNullList
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
 *  net.minecraft.world.ContainerHelper
 *  net.minecraft.world.WorldlyContainer
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.ChestMenu
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraftforge.common.capabilities.Capability
 *  net.minecraftforge.common.capabilities.ForgeCapabilities
 *  net.minecraftforge.common.util.LazyOptional
 *  net.minecraftforge.items.IItemHandler
 *  net.minecraftforge.items.wrapper.SidedInvWrapper
 */
package com.vivideru.masteryofmagic.block.entity;

import com.vivideru.masteryofmagic.block.BlackstoneChaliceBlock;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModBlockEntities;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class BlackstoneChaliceBlockEntity
extends RandomizableContainerBlockEntity
implements WorldlyContainer {
    private NonNullList<ItemStack> stacks = NonNullList.m_122780_((int)9, (Object)ItemStack.f_41583_);
    private final LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper.create((WorldlyContainer)this, (Direction[])Direction.values());
    private int blood = 0;
    private String ownerUUID = "";
    private String ownerName = "";

    public BlackstoneChaliceBlockEntity(BlockPos pos, BlockState state) {
        super((BlockEntityType)GoetyMasteryOfMagicModBlockEntities.BLACKSTONE_CHALICE.get(), pos, state);
    }

    public void m_142466_(CompoundTag tag) {
        super.m_142466_(tag);
        if (!this.m_59631_(tag)) {
            this.stacks = NonNullList.m_122780_((int)this.m_6643_(), (Object)ItemStack.f_41583_);
        }
        ContainerHelper.m_18980_((CompoundTag)tag, this.stacks);
        this.blood = tag.m_128451_("Blood");
        this.ownerUUID = tag.m_128461_("OwnerUUID");
        this.ownerName = tag.m_128461_("OwnerName");
    }

    protected void m_183515_(CompoundTag tag) {
        super.m_183515_(tag);
        if (!this.m_59634_(tag)) {
            ContainerHelper.m_18973_((CompoundTag)tag, this.stacks);
        }
        tag.m_128405_("Blood", this.blood);
        tag.m_128359_("OwnerUUID", this.ownerUUID);
        tag.m_128359_("OwnerName", this.ownerName);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.m_195640_((BlockEntity)this);
    }

    public CompoundTag m_5995_() {
        return this.m_187480_();
    }

    private void sync() {
        if (this.f_58857_ == null) {
            return;
        }
        BlockState state = this.m_58900_();
        this.f_58857_.m_7260_(this.f_58858_, state, state, 3);
    }

    public int getBlood() {
        return this.blood;
    }

    public int getBloodLevel() {
        return Math.min(9, this.blood / 1000);
    }

    public boolean isFull() {
        return this.blood >= 9000;
    }

    public void addBlood(int amount) {
        if (amount <= 0 || this.isFull()) {
            return;
        }
        this.blood = Math.min(9000, this.blood + amount);
        this.updateLevel();
        this.m_6596_();
        this.sync();
    }

    public boolean consumeBlood(int amount) {
        if (amount <= 0) {
            return false;
        }
        if (this.blood < amount) {
            return false;
        }
        this.blood -= amount;
        this.updateLevel();
        this.m_6596_();
        this.sync();
        return true;
    }

    private void updateLevel() {
        if (this.f_58857_ == null) {
            return;
        }
        int level = Math.min(3, (this.blood + 2000) / 3000);
        BlockState state = this.m_58900_();
        if ((Integer)state.m_61143_((Property)BlackstoneChaliceBlock.LEVEL) != level) {
            this.f_58857_.m_7731_(this.f_58858_, (BlockState)state.m_61124_((Property)BlackstoneChaliceBlock.LEVEL, (Comparable)Integer.valueOf(level)), 3);
        }
    }

    public boolean hasOwner() {
        return this.ownerUUID != null && !this.ownerUUID.isEmpty();
    }

    public void setOwner(String uuid, String name) {
        this.ownerUUID = uuid;
        this.ownerName = name;
        this.m_6596_();
        this.sync();
    }

    public void setOwnerIfAbsent(String uuid) {
        if (!this.hasOwner()) {
            this.setOwner(uuid, uuid);
        }
    }

    public void setOwnerIfAbsent(String uuid, String name) {
        if (!this.hasOwner()) {
            this.setOwner(uuid, name);
        }
    }

    @Nullable
    public String getOwnerUUID() {
        return this.ownerUUID;
    }

    @Nullable
    public String getOwnerName() {
        return this.ownerName;
    }

    public void onLoad() {
        super.onLoad();
        if (this.f_58857_ == null || this.f_58857_.m_5776_()) {
            return;
        }
        if (this.hasOwner()) {
            return;
        }
        List players = this.f_58857_.m_45976_(Player.class, new AABB(this.f_58858_).m_82400_(5.0));
        if (!players.isEmpty()) {
            Player p = (Player)players.get(0);
            this.setOwnerIfAbsent(p.m_20148_().toString(), p.m_36316_().getName());
        }
    }

    public int m_6643_() {
        return this.stacks.size();
    }

    public boolean m_7983_() {
        for (ItemStack stack : this.stacks) {
            if (stack.m_41619_()) continue;
            return false;
        }
        return true;
    }

    public Component m_6820_() {
        return Component.m_237113_((String)"blackstone_chalice");
    }

    public int m_6893_() {
        return 64;
    }

    public AbstractContainerMenu m_6555_(int id, Inventory inventory) {
        return ChestMenu.m_39255_((int)id, (Inventory)inventory);
    }

    public Component m_5446_() {
        return Component.m_237113_((String)"Blackstone Chalice");
    }

    protected NonNullList<ItemStack> m_7086_() {
        return this.stacks;
    }

    protected void m_6520_(NonNullList<ItemStack> stacks) {
        this.stacks = stacks;
    }

    public boolean m_7013_(int index, ItemStack stack) {
        return true;
    }

    public int[] m_7071_(Direction side) {
        return IntStream.range(0, this.m_6643_()).toArray();
    }

    public boolean m_7155_(int index, ItemStack stack, @Nullable Direction direction) {
        return this.m_7013_(index, stack);
    }

    public boolean m_7157_(int index, ItemStack stack, Direction direction) {
        return true;
    }

    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (!this.f_58859_ && side != null && cap == ForgeCapabilities.ITEM_HANDLER) {
            return this.handlers[side.ordinal()].cast();
        }
        return super.getCapability(cap, side);
    }

    public void m_7651_() {
        super.m_7651_();
        for (LazyOptional<? extends IItemHandler> handler : this.handlers) {
            handler.invalidate();
        }
    }
}

