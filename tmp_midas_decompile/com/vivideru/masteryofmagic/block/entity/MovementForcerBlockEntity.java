/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.blocks.ModBlocks
 *  com.Polarice3.Goety.common.blocks.entities.CursedCageBlockEntity
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.NonNullList
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.ContainerHelper
 *  net.minecraft.world.WorldlyContainer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.ChestMenu
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.common.capabilities.Capability
 *  net.minecraftforge.common.capabilities.ForgeCapabilities
 *  net.minecraftforge.common.util.LazyOptional
 *  net.minecraftforge.items.IItemHandler
 *  net.minecraftforge.items.wrapper.SidedInvWrapper
 */
package com.vivideru.masteryofmagic.block.entity;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.entities.CursedCageBlockEntity;
import com.vivideru.masteryofmagic.block.MovementForcerBlock;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModBlockEntities;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class MovementForcerBlockEntity
extends RandomizableContainerBlockEntity
implements WorldlyContainer {
    private static final int DEFAULT_RANGE = 32;
    private static final int MIN_RANGE = 1;
    private static final int MAX_RANGE = 96;
    private static final double VANILLA_GRAVITY = 0.08;
    private static final double DEFAULT_ACCELERATION = 0.16;
    private static final double MIN_ACCELERATION = 0.024;
    private static final double MAX_ACCELERATION = 0.4;
    private static final int DEFAULT_WIDTH = 5;
    private static final int MAX_WIDTH = 15;
    private static final int WIDTH_CLICK_COOLDOWN = 20;
    private static final int SE_CAPACITY = 1000;
    private static final int SE_PULL_PER_SECOND = 20;
    private static final int SE_DRAIN_PER_SECOND = 1;
    private static final int SE_TICK_INTERVAL = 20;
    private NonNullList<ItemStack> stacks = NonNullList.m_122780_((int)9, (Object)ItemStack.f_41583_);
    private final LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper.create((WorldlyContainer)this, (Direction[])Direction.values());
    private int range = 32;
    private double acceleration = 0.16;
    private boolean inverted = false;
    private int width = 5;
    private int storedSE = 0;
    private int sePullTicker = 0;
    private int seDrainTicker = 0;
    private int particleTicker = 0;
    private long lastWidthChangeTick = -20L;

    public MovementForcerBlockEntity(BlockPos position, BlockState state) {
        super((BlockEntityType)GoetyMasteryOfMagicModBlockEntities.MOVEMENT_FORCER.get(), position, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MovementForcerBlockEntity blockEntity) {
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        blockEntity.pullSoulEnergy(state);
        if (!level.m_276867_(pos)) {
            return;
        }
        if (blockEntity.storedSE <= 0) {
            return;
        }
        Direction fieldDirection = (Direction)state.m_61143_((Property)MovementForcerBlock.FACING);
        Vec3 direction = Vec3.m_82528_((Vec3i)fieldDirection.m_122436_()).m_82541_();
        if (blockEntity.inverted) {
            direction = direction.m_82548_();
        }
        Vec3 origin = Vec3.m_82512_((Vec3i)pos).m_82549_(Vec3.m_82528_((Vec3i)fieldDirection.m_122436_()).m_82490_(0.55));
        AABB searchBox = blockEntity.createDirectionalSearchBox(origin, fieldDirection);
        List entities = serverLevel.m_6443_(Entity.class, searchBox, entity -> {
            if (entity == null) {
                return false;
            }
            if (!entity.m_6084_()) {
                return false;
            }
            if (entity instanceof Player) {
                Player player = (Player)entity;
                return !player.m_7500_() && !player.m_5833_();
            }
            return entity instanceof LivingEntity || entity instanceof Projectile || entity instanceof ItemEntity;
        });
        boolean affectedAny = false;
        Vec3 accelerationVector = direction.m_82490_(blockEntity.acceleration);
        for (Entity entity2 : entities) {
            if (!blockEntity.isInsideDirectionalArea(entity2, origin, fieldDirection)) continue;
            entity2.m_20256_(entity2.m_20184_().m_82549_(accelerationVector));
            entity2.f_19864_ = true;
            entity2.f_19789_ = 0.0f;
            affectedAny = true;
        }
        if (affectedAny) {
            blockEntity.consumeSoulEnergyPerSecond();
        }
        ++blockEntity.particleTicker;
        if (blockEntity.particleTicker >= 4) {
            blockEntity.particleTicker = 0;
            blockEntity.spawnDirectionParticles(serverLevel, origin, direction, fieldDirection);
        }
    }

    private void pullSoulEnergy(BlockState state) {
        if (this.f_58857_ == null) {
            return;
        }
        if (this.storedSE >= 1000) {
            return;
        }
        if (++this.sePullTicker < 20) {
            return;
        }
        this.sePullTicker = 0;
        Direction fieldDirection = (Direction)state.m_61143_((Property)MovementForcerBlock.FACING);
        BlockPos cagePos = this.f_58858_.m_121945_(fieldDirection.m_122424_());
        if (!this.f_58857_.m_8055_(cagePos).m_60713_((Block)ModBlocks.CURSED_CAGE_BLOCK.get())) {
            return;
        }
        BlockEntity blockEntity = this.f_58857_.m_7702_(cagePos);
        if (!(blockEntity instanceof CursedCageBlockEntity)) {
            return;
        }
        CursedCageBlockEntity cage = (CursedCageBlockEntity)blockEntity;
        int need = 1000 - this.storedSE;
        int toPull = Math.min(20, need);
        if (toPull <= 0) {
            return;
        }
        if (cage.getSouls() >= toPull) {
            cage.decreaseSouls(toPull);
            this.storedSE += toPull;
            this.syncData();
        }
    }

    private void consumeSoulEnergyPerSecond() {
        if (++this.seDrainTicker < 20) {
            return;
        }
        this.seDrainTicker = 0;
        this.storedSE = Math.max(0, this.storedSE - 1);
        this.syncData();
    }

    private AABB createDirectionalSearchBox(Vec3 origin, Direction facing) {
        Vec3 end = origin.m_82549_(Vec3.m_82528_((Vec3i)facing.m_122436_()).m_82490_((double)this.range));
        double minX = Math.min(origin.f_82479_, end.f_82479_);
        double minY = Math.min(origin.f_82480_, end.f_82480_);
        double minZ = Math.min(origin.f_82481_, end.f_82481_);
        double maxX = Math.max(origin.f_82479_, end.f_82479_);
        double maxY = Math.max(origin.f_82480_, end.f_82480_);
        double maxZ = Math.max(origin.f_82481_, end.f_82481_);
        double halfWidth = (double)this.width * 0.5;
        if (facing.m_122434_() != Direction.Axis.X) {
            minX -= halfWidth;
            maxX += halfWidth;
        } else {
            minX -= 0.75;
            maxX += 0.75;
        }
        if (facing.m_122434_() != Direction.Axis.Y) {
            minY -= halfWidth;
            maxY += halfWidth;
        } else {
            minY -= 0.75;
            maxY += 0.75;
        }
        if (facing.m_122434_() != Direction.Axis.Z) {
            minZ -= halfWidth;
            maxZ += halfWidth;
        } else {
            minZ -= 0.75;
            maxZ += 0.75;
        }
        return new AABB(minX, minY, minZ, maxX, maxY, maxZ).m_82400_(0.5);
    }

    private boolean isInsideDirectionalArea(Entity entity, Vec3 origin, Direction facing) {
        Vec3 direction = Vec3.m_82528_((Vec3i)facing.m_122436_()).m_82541_();
        Vec3 center = entity.m_20191_().m_82399_();
        Vec3 delta = center.m_82546_(origin);
        double forwardDistance = delta.m_82526_(direction);
        if (forwardDistance < 0.0 || forwardDistance > (double)this.range) {
            return false;
        }
        Vec3 projected = direction.m_82490_(forwardDistance);
        Vec3 perpendicular = delta.m_82546_(projected);
        double halfWidth = (double)this.width * 0.5;
        return Math.abs(perpendicular.f_82479_) <= halfWidth && Math.abs(perpendicular.f_82480_) <= halfWidth && Math.abs(perpendicular.f_82481_) <= halfWidth;
    }

    private void spawnDirectionParticles(ServerLevel serverLevel, Vec3 origin, Vec3 direction, Direction fieldDirection) {
        Vec3 axisB;
        Vec3 axisA;
        double powerRatio = this.acceleration / 0.16;
        double baseSpeed = 0.12 * powerRatio;
        double accelFactor = 0.02 * powerRatio;
        Vec3 normal = Vec3.m_82528_((Vec3i)fieldDirection.m_122436_()).m_82541_();
        if (fieldDirection.m_122434_() == Direction.Axis.Y) {
            axisA = new Vec3(1.0, 0.0, 0.0);
            axisB = new Vec3(0.0, 0.0, 1.0);
        } else if (fieldDirection.m_122434_() == Direction.Axis.X) {
            axisA = new Vec3(0.0, 1.0, 0.0);
            axisB = new Vec3(0.0, 0.0, 1.0);
        } else {
            axisA = new Vec3(1.0, 0.0, 0.0);
            axisB = new Vec3(0.0, 1.0, 0.0);
        }
        for (int i = 0; i < 8; ++i) {
            double forwardOffset = this.f_58857_.f_46441_.m_188500_() * (double)this.range;
            double sideOffsetA = (this.f_58857_.f_46441_.m_188500_() - 0.5) * (double)this.width;
            double sideOffsetB = (this.f_58857_.f_46441_.m_188500_() - 0.5) * (double)this.width;
            Vec3 particlePos = origin.m_82549_(normal.m_82490_(forwardOffset)).m_82549_(axisA.m_82490_(sideOffsetA)).m_82549_(axisB.m_82490_(sideOffsetB));
            Vec3 particleVelocity = direction.m_82490_(baseSpeed + accelFactor * forwardOffset);
            serverLevel.m_8767_((ParticleOptions)ParticleTypes.f_123760_, particlePos.f_82479_, particlePos.f_82480_, particlePos.f_82481_, 0, particleVelocity.f_82479_, particleVelocity.f_82480_, particleVelocity.f_82481_, 1.0);
        }
    }

    public boolean increaseRange() {
        if (this.range >= 96) {
            this.playFailSound();
            return false;
        }
        this.range = Math.min(96, this.range + 1);
        this.syncData();
        this.playUseSound();
        return true;
    }

    public boolean decreaseRange() {
        if (this.range <= 1) {
            this.playFailSound();
            return false;
        }
        this.range = Math.max(1, this.range - 1);
        this.syncData();
        this.playUseSound();
        return true;
    }

    public boolean increaseAcceleration() {
        if (this.acceleration >= 0.4) {
            this.playFailSound();
            return false;
        }
        this.acceleration = Math.min(0.4, this.acceleration + 0.008);
        this.syncData();
        this.playUseSound();
        return true;
    }

    public boolean decreaseAcceleration() {
        if (this.acceleration <= 0.024) {
            this.playFailSound();
            return false;
        }
        this.acceleration = Math.max(0.024, this.acceleration - 0.008);
        this.syncData();
        this.playUseSound();
        return true;
    }

    public boolean increaseWidth() {
        if (this.f_58857_ == null) {
            return false;
        }
        long gameTime = this.f_58857_.m_46467_();
        if (gameTime - this.lastWidthChangeTick < 20L) {
            this.playFailSound();
            return false;
        }
        if (this.width >= 15) {
            this.playFailSound();
            return false;
        }
        this.width = Math.min(15, this.width + 1);
        this.lastWidthChangeTick = gameTime;
        this.syncData();
        this.playUseSound();
        return true;
    }

    public void toggleInverted() {
        this.inverted = !this.inverted;
        this.syncData();
        this.playUseSound();
    }

    public boolean refillSE() {
        if (this.storedSE >= 1000) {
            this.playFailSound();
            return false;
        }
        this.storedSE = 1000;
        this.syncData();
        this.playRefillSound();
        return true;
    }

    public int getStoredSE() {
        return this.storedSE;
    }

    public int getSECapacity() {
        return 1000;
    }

    public int getWidth() {
        return this.width;
    }

    private void playUseSound() {
        if (this.f_58857_ == null || this.f_58857_.m_5776_()) {
            return;
        }
        this.f_58857_.m_5594_(null, this.f_58858_, SoundEvents.f_11852_, SoundSource.BLOCKS, 0.7f, 0.55f);
    }

    private void playRefillSound() {
        if (this.f_58857_ == null || this.f_58857_.m_5776_()) {
            return;
        }
        this.f_58857_.m_5594_(null, this.f_58858_, SoundEvents.f_11851_, SoundSource.BLOCKS, 0.8f, 0.45f);
    }

    private void playFailSound() {
        if (this.f_58857_ == null || this.f_58857_.m_5776_()) {
            return;
        }
        this.f_58857_.m_5594_(null, this.f_58858_, SoundEvents.f_11849_, SoundSource.BLOCKS, 0.55f, 0.5f);
    }

    private void syncData() {
        this.m_6596_();
        if (this.f_58857_ != null) {
            this.f_58857_.m_7260_(this.f_58858_, this.m_58900_(), this.m_58900_(), 3);
        }
    }

    public void m_142466_(CompoundTag compound) {
        super.m_142466_(compound);
        if (!this.m_59631_(compound)) {
            this.stacks = NonNullList.m_122780_((int)this.m_6643_(), (Object)ItemStack.f_41583_);
        }
        ContainerHelper.m_18980_((CompoundTag)compound, this.stacks);
        this.range = compound.m_128441_("Range") ? compound.m_128451_("Range") : 32;
        this.acceleration = compound.m_128441_("Acceleration") ? compound.m_128459_("Acceleration") : 0.16;
        this.inverted = compound.m_128471_("Inverted");
        this.width = compound.m_128441_("Width") ? compound.m_128451_("Width") : 5;
        this.storedSE = compound.m_128441_("StoredSE") ? compound.m_128451_("StoredSE") : 0;
        long l = this.lastWidthChangeTick = compound.m_128441_("LastWidthChangeTick") ? compound.m_128454_("LastWidthChangeTick") : -20L;
        if (this.range < 1) {
            this.range = 1;
        }
        if (this.range > 96) {
            this.range = 96;
        }
        if (this.acceleration < 0.024) {
            this.acceleration = 0.024;
        }
        if (this.acceleration > 0.4) {
            this.acceleration = 0.4;
        }
        if (this.width < 5) {
            this.width = 5;
        }
        if (this.width > 15) {
            this.width = 15;
        }
        if (this.storedSE < 0) {
            this.storedSE = 0;
        }
        if (this.storedSE > 1000) {
            this.storedSE = 1000;
        }
    }

    public void m_183515_(CompoundTag compound) {
        super.m_183515_(compound);
        if (!this.m_59634_(compound)) {
            ContainerHelper.m_18973_((CompoundTag)compound, this.stacks);
        }
        compound.m_128405_("Range", this.range);
        compound.m_128347_("Acceleration", this.acceleration);
        compound.m_128379_("Inverted", this.inverted);
        compound.m_128405_("Width", this.width);
        compound.m_128405_("StoredSE", this.storedSE);
        compound.m_128356_("LastWidthChangeTick", this.lastWidthChangeTick);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.m_195640_((BlockEntity)this);
    }

    public CompoundTag m_5995_() {
        return this.m_187480_();
    }

    public int m_6643_() {
        return this.stacks.size();
    }

    public boolean m_7983_() {
        for (ItemStack itemstack : this.stacks) {
            if (itemstack.m_41619_()) continue;
            return false;
        }
        return true;
    }

    public Component m_6820_() {
        return Component.m_237113_((String)"movement_forcer");
    }

    public int m_6893_() {
        return 64;
    }

    public AbstractContainerMenu m_6555_(int id, Inventory inventory) {
        return ChestMenu.m_39255_((int)id, (Inventory)inventory);
    }

    public Component m_5446_() {
        return Component.m_237113_((String)"Movement Forcer");
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

    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (!this.f_58859_ && facing != null && capability == ForgeCapabilities.ITEM_HANDLER) {
            return this.handlers[facing.ordinal()].cast();
        }
        return super.getCapability(capability, facing);
    }

    public void m_7651_() {
        super.m_7651_();
        for (LazyOptional<? extends IItemHandler> handler : this.handlers) {
            handler.invalidate();
        }
    }
}

