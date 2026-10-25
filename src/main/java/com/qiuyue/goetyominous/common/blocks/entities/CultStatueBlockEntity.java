package com.qiuyue.goetyominous.common.blocks.entities;

import com.Polarice3.Goety.common.blocks.entities.CursedCageBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.OwnedBlockEntity;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.hostile.cultists.Cultist;
import com.Polarice3.Goety.utils.EntityFinder;
import com.qiuyue.goetyominous.common.blocks.CultStatueBlock;
import com.qiuyue.goetyominous.common.blocks.GoldenPiglinStatueBlock;
import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractPiglinServant;
import com.qiuyue.goetyominous.common.init.ModBlockEntities;
import com.qiuyue.goetyominous.common.init.ModBlocks;
import com.qiuyue.goetyominous.config.MobsConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class CultStatueBlockEntity extends OwnedBlockEntity {
    public static final String SERVANT_LIST = "servantList";
    public static final String CLIENT_ID_LIST = "client_id_list";
    private static final int BUFF_INTERVAL = 100;
    private static final int BUFF_DURATION = 100;

    public List<AbstractPiglinServant> servants = new ArrayList<>();
    public List<UUID> uuids = new ArrayList<>();
    public List<Integer> ids = new ArrayList<>();
    public int clientCount;
    private int buffTickCount;
    private CursedCageBlockEntity cursedCage;
    private boolean isEmpty;

    public CultStatueBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CULT_STATUE.get(), pos, state);
    }

    @Override
    public boolean shouldChunkLoad() {
        return this.level instanceof ServerLevel && !this.uuids.isEmpty();
    }

    @Override
    public int selfLoadRadius() {
        return 9;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CultStatueBlockEntity blockEntity) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (state.getValue(CultStatueBlock.ENABLED)) {
            blockEntity.tickEnabled(serverLevel, pos, state);
        } else {
            blockEntity.tickShrine(serverLevel, pos, state);
        }
        blockEntity.clientCount = blockEntity.ids.size();
    }

    private void tickEnabled(ServerLevel level, BlockPos pos, BlockState state) {
        ++this.buffTickCount;
        if (this.buffTickCount % BUFF_INTERVAL == 0) {
            List<Cultist> cultists = level.getEntitiesOfClass(Cultist.class,
                    new AABB(pos.getX(), pos.getY(), pos.getZ(),
                            pos.getX(), pos.getY() - 4.0D, pos.getZ()).inflate(8.0D));
            for (Cultist cultist : cultists) {
                cultist.addEffect(new MobEffectInstance(GoetyEffects.BUFF.get(), BUFF_DURATION));
            }
        }
        if (state.getValue(CultStatueBlock.POWERED)) {
            level.setBlock(pos, state.setValue(CultStatueBlock.POWERED, false), 3);
        }
    }

    private void tickShrine(ServerLevel level, BlockPos pos, BlockState state) {
        if (this.uuids.isEmpty()) {
            this.servants.clear();
        } else {
            this.chunkLoadBlock();
            this.uuids.removeIf(uuid -> {
                Entity entity = EntityFinder.getLivingEntityByUuiD(uuid);
                if (!(entity instanceof AbstractPiglinServant servant)) {
                    this.markUpdated();
                    return true;
                }
                if (!servant.isRemoved() && servant.isAlive()
                        && (servant.getShrine() == null || servant.getShrine() == this)) {
                    return false;
                }
                this.markUpdated();
                return true;
            });
            this.servants.clear();
            for (UUID uuid : this.uuids) {
                Entity entity = EntityFinder.getLivingEntityByUuiD(uuid);
                if (entity instanceof AbstractPiglinServant servant) {
                    this.servants.add(servant);
                    if (!pos.equals(servant.getRevivePos())) {
                        servant.setRevivePos(pos);
                        servant.setReviveDim(level.dimension());
                    }
                }
            }
            for (AbstractPiglinServant servant : this.servants) {
                if (servant.isAlive() && servant.getShrine() == this && !this.ids.contains(servant.getId())) {
                    this.ids.add(servant.getId());
                    this.markUpdated();
                }
            }
        }

        if (!this.ids.isEmpty()) {
            this.isEmpty = false;
            if (this.ids.removeIf(id -> {
                Entity entity = level.getEntity(id);
                return !(entity instanceof AbstractPiglinServant servant) || !servant.isAlive() || servant.isRemoved();
            })) {
                this.markUpdated();
            }
        } else if (!this.isEmpty) {
            this.markUpdated();
            this.isEmpty = true;
        }

        boolean powered = this.checkCage() && !this.servants.isEmpty();
        if (state.getValue(CultStatueBlock.POWERED) != powered) {
            level.setBlock(pos, state.setValue(CultStatueBlock.POWERED, powered), 3);
        }
    }

    public List<AbstractPiglinServant> getServants() {
        return this.servants;
    }

    public void addServant(AbstractPiglinServant servant) {
        if (!this.uuids.contains(servant.getUUID())) {
            this.uuids.add(servant.getUUID());
            this.markUpdated();
        }
    }

    public void removeServant(AbstractPiglinServant servant) {
        this.uuids.remove(servant.getUUID());
        this.servants.remove(servant);
        this.ids.remove((Integer) servant.getId());
        this.markUpdated();
    }

    public int getBindLimit() {
        return MobsConfig.CultStatueBindLimit.get()
                * (this.getBlockState().is(ModBlocks.GOLDEN_PIGLIN_STATUE.get()) ? 2 : 1);
    }

    public boolean hasSpace() {
        return this.uuids.size() < this.getBindLimit();
    }

    public List<UUID> getUuids() {
        return this.uuids;
    }

    public List<Integer> getIds() {
        return this.ids;
    }

    public int getClientCount() {
        return this.clientCount;
    }

    @Nullable
    public static CultStatueBlockEntity resolve(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof CultStatueBlockEntity shrine) {
            return shrine;
        }
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof GoldenPiglinStatueBlock
                && state.getValue(GoldenPiglinStatueBlock.HALF) == DoubleBlockHalf.UPPER) {
            BlockEntity below = level.getBlockEntity(pos.below());
            if (below instanceof CultStatueBlockEntity shrine) {
                return shrine;
            }
        }
        return null;
    }

    private boolean checkCage() {
        if (this.level != null) {
            BlockPos pos = this.getBlockPos().below();
            BlockState state = this.level.getBlockState(pos);
            if (state.is(com.Polarice3.Goety.common.blocks.ModBlocks.CURSED_CAGE_BLOCK.get())) {
                BlockEntity blockEntity = this.level.getBlockEntity(pos);
                if (blockEntity instanceof CursedCageBlockEntity cage) {
                    this.cursedCage = cage;
                    return !cage.getItem().isEmpty();
                }
            }
        }
        return false;
    }

    public int getSoulEnergy() {
        return this.level != null && this.cursedCage != null ? this.cursedCage.getSouls() : 0;
    }

    public void siphonSoulEnergy(int souls) {
        if (this.level != null && this.cursedCage != null) {
            this.cursedCage.decreaseSouls(souls);
        }
    }

    public void markUpdated() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
        }
    }

    @Override
    public void readNetwork(CompoundTag tag) {
        super.readNetwork(tag);
        this.uuids.clear();
        this.ids.clear();
        if (tag.contains(SERVANT_LIST)) {
            ListTag list = tag.getList(SERVANT_LIST, 8);
            for (int i = 0; i < list.size(); ++i) {
                UUID uuid = UUID.fromString(list.getString(i));
                if (!this.uuids.contains(uuid)) {
                    this.uuids.add(uuid);
                }
            }
        }
        if (tag.contains(CLIENT_ID_LIST)) {
            ListTag list = tag.getList(CLIENT_ID_LIST, 3);
            for (int i = 0; i < list.size(); ++i) {
                int id = list.getInt(i);
                if (!this.ids.contains(id)) {
                    this.ids.add(id);
                }
            }
        }
        this.clientCount = this.ids.size();
    }

    @Override
    public CompoundTag writeNetwork(CompoundTag tag) {
        super.writeNetwork(tag);
        ListTag uuidList = new ListTag();
        for (UUID uuid : this.uuids) {
            uuidList.add(StringTag.valueOf(uuid.toString()));
        }
        tag.put(SERVANT_LIST, uuidList);
        ListTag idList = new ListTag();
        for (Integer id : this.ids) {
            idList.add(IntTag.valueOf(id));
        }
        tag.put(CLIENT_ID_LIST, idList);
        return tag;
    }
}
